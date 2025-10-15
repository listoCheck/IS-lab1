<script setup lang="ts">
import { ref, computed } from 'vue'
import axios from 'axios'

const fileInput = ref(null)
const selectedFile = ref(null)
const preview = ref([])
const message = ref('')
const isLoading = ref(false)

const handleFileSelect = (event) => {
    const file = event.target.files[0]
    if (!file) return
    selectedFile.value = file
    readFile(file)
}

const readFile = (file) => {
    const reader = new FileReader()
    reader.onload = (e) => {
        try {
            const text = e.target.result.trim()
            if (text.startsWith('[')) {
                preview.value = JSON.parse(text).slice(0, 3)
            } else {
                // JSONL формат
                preview.value = text
                    .split('\n')
                    .filter(line => line.trim().length > 0)
                    .slice(0, 3)
                    .map(line => JSON.parse(line))
            }
            message.value = ''
        } catch (err) {
            message.value = 'Ошибка: неверный формат JSON-файла.'
        }
    }
    reader.readAsText(file)
}

const formattedPreview = computed(() => JSON.stringify(preview.value, null, 2))

const uploadFile = async () => {
    if (!selectedFile.value) return
    isLoading.value = true
    message.value = ''
    try {
        const formData = new FormData()
        formData.append('file', selectedFile.value)
        const response = await axios.post('/api/import', formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        })
        message.value = `Импорт успешно выполнен. Добавлено ${response.data.addedCount} объектов.`
    } catch (err) {
        message.value = err.response?.data?.message || 'Ошибка при импорте объектов.'
    } finally {
        isLoading.value = false
    }
}

const reset = () => {
    selectedFile.value = null
    preview.value = []
    message.value = ''
    fileInput.value.value = ''
}

const messageClass = computed(() =>
    message.value.includes('Ошибка')
        ? 'bg-red-100 text-red-700'
        : 'bg-green-100 text-green-700'
)
</script>
<template>
    <div class="p-6 max-w-2xl mx-auto space-y-6">
        <h1 class="text-2xl font-bold">Импорт объектов</h1>

        <div class="border-2 border-dashed p-6 rounded-2xl text-center bg-gray-50">
            <button
                class="px-4 py-2 bg-blue-600 text-white rounded-xl shadow hover:bg-blue-700"
                @click="fileInput.click()"
            >
                Выбрать файл
            </button>
            <p class="text-gray-600 mt-2">Поддерживаются форматы: JSON, JSONL</p>
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


