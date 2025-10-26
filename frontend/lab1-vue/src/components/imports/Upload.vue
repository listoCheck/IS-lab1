<script setup lang="ts">
import { ref, computed, defineEmits } from 'vue'
import axios from 'axios'

const emit = defineEmits(['imported'])

const fileInput = ref<HTMLInputElement | null>(null)
const selectedFile = ref<File | null>(null)
const preview = ref<any[]>([])
const message = ref('')
const isLoading = ref(false)
const baseUrl = 'http://localhost:8080/IS-lab1JEE-1.0-SNAPSHOT/api'

const handleFileSelect = (event: Event) => {
    const file = (event.target as HTMLInputElement).files?.[0]
    if (!file) return
    selectedFile.value = file
    readFile(file)
}

const readFile = (file: File) => {
    const reader = new FileReader()
    reader.onload = (e) => {
        try {
            const rawText = (e.target?.result as string).trim()
            const text = cleanMultipart(rawText)
            let objects: any[] = []

            if (text.startsWith('{')) {
                const parsed = JSON.parse(text)
                objects = [parsed]
                message.value = parsed.type ? `Тип объектов: ${parsed.type}` : ''
            } else if (text.startsWith('[')) {
                objects = JSON.parse(text)
                message.value = ''
            } else {
                const lines = text.split(/\r?\n/).filter(line => line.trim().length > 0)
                objects = lines.map(line => JSON.parse(line.replace(/^\uFEFF/, '').trim()))
                message.value = ''
            }

            preview.value = objects.slice(0, 3)
        } catch (err: any) {
            console.error(err)
            message.value = `Ошибка: неверный формат JSON-файла. ${err.message}`
            preview.value = []
        }
    }
    reader.readAsText(file)
}

const cleanMultipart = (rawText: string) => {
    const lines = rawText.split(/\r?\n/)
    let insideJson = false
    const result: string[] = []

    for (let line of lines) {
        line = line.trim()
        if (!insideJson && (line.startsWith('{') || line.startsWith('['))) insideJson = true
        if (insideJson) result.push(line)
    }

    return result.join('\n')
}

const formattedPreview = computed(() => JSON.stringify(preview.value, null, 2))

const uploadFile = async () => {
    if (!selectedFile.value) return
    isLoading.value = true
    message.value = ''

    try {
        const formData = new FormData()
        formData.append('file', selectedFile.value)

        const response = await axios.post(`${baseUrl}/import`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })

        if (response.status >= 400) {
            message.value = response.data?.message || 'Ошибка при импорте объектов.'
        } else {
            message.value = `Импорт успешно выполнен. Добавлено ${response.data.addedCount} объектов.`
        }
        emit('imported')
    } catch (err: any) {
        console.error(err)
        message.value = err.response?.data?.message
            ? `Ошибка при импорте объектов: ${err.response.data.message}`
            : `Ошибка при импорте объектов: ${err.message}`
    } finally {
        isLoading.value = false
    }
}

const reset = () => {
    selectedFile.value = null
    preview.value = []
    message.value = ''
    if (fileInput.value) fileInput.value.value = ''
}

const messageClass = computed(() =>
    message.value.includes('Ошибка') ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'
)
</script>

<template>
    <div class="p-6 max-w-2xl mx-auto space-y-6">
        <h1 class="text-2xl font-bold">Импорт объектов</h1>

        <div class="border-2 border-dashed p-6 rounded-2xl text-center bg-gray-50">
            <input
                ref="fileInput"
                type="file"
                accept=".json,.jsonl"
                class="hidden"
                @change="handleFileSelect"
            />
            <button
                class="px-4 py-2 bg-blue-600 text-white rounded-xl shadow hover:bg-blue-700"
                @click="fileInput.value?.click()"
            >
                {{ selectedFile ? selectedFile.name : 'Выбрать файл' }}
            </button>
            <p class="text-gray-600 mt-2">Поддерживаются форматы: JSON, JSONL, {"type": "...", "objects": [...]}</p>
        </div>

        <div v-if="preview.length" class="bg-white rounded-xl shadow p-4">
            <h2 class="text-lg font-semibold mb-2">Предпросмотр (первые {{ preview.length }} объектов):</h2>
            <pre class="text-sm bg-gray-100 rounded-lg p-2 overflow-auto max-h-64">{{ formattedPreview }}</pre>
        </div>

        <div class="flex gap-4">
            <button
                class="px-4 py-2 bg-green-600 text-white rounded-xl hover:bg-green-700 disabled:opacity-50"
                :disabled="!selectedFile || isLoading"
                @click="uploadFile"
            >
                {{ isLoading ? "Импортируется..." : "Импортировать" }}
            </button>

            <button
                class="px-4 py-2 bg-gray-300 rounded-xl hover:bg-gray-400"
                @click="reset"
            >
                Очистить
            </button>
        </div>

        <div v-if="message" :class="messageClass" class="p-3 rounded-xl mt-4">
            {{ message }}
        </div>
    </div>
</template>
