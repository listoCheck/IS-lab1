<script setup lang="ts">
import { ref, computed, onMounted, defineExpose } from "vue"

const baseUrl = "http://localhost:8080/IS-lab1JEE-1.0-SNAPSHOT/api"

const history = ref<any[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

const filterStatus = ref("")
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const loadHistory = async () => {
    loading.value = true
    error.value = null

    try {
        const resp = await fetch(`${baseUrl}/import/history?page=${page.value}&size=${pageSize.value}`)
        if (!resp.ok) throw new Error("Ошибка " + resp.status)

        const data = await resp.json()

        total.value = data.total ?? 0
        const array = Array.isArray(data.content) ? data.content : []

        history.value = array.map((r: any) => {
            if (Array.isArray(r.timestamp)) {
                const [y, m, d, h, mi, s, ns] = r.timestamp
                r.timestamp = new Date(y, m - 1, d, h, mi, s, Math.floor(ns / 1_000_000))
            } else if (typeof r.timestamp === "string") {
                r.timestamp = new Date(r.timestamp)
            }
            return r
        })
    } catch (e: any) {
        error.value = e.message
    } finally {
        loading.value = false
    }
}

const filteredHistory = computed(() =>
    history.value.filter(r =>
        (!filterStatus.value || r.status === filterStatus.value)
    )
)

const formatDate = (ts: Date | null) => {
    if (!ts) return "-"
    return ts.toLocaleString("ru-RU", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit"
    })
}

const nextPage = () => {
    if (page.value < totalPages.value) {
        page.value++
        loadHistory()
    }
}

const prevPage = () => {
    if (page.value > 1) {
        page.value--
        loadHistory()
    }
}

onMounted(loadHistory)
defineExpose({ loadHistory })
</script>

<template>
    <div class="p-6 max-w-6xl mx-auto space-y-6">
        <h1 class="text-2xl font-bold text-gray-800">История импортов</h1>

        <div class="flex flex-wrap items-center gap-3">
            <select v-model="filterStatus" class="border rounded-lg px-3 py-2">
                <option value="">Все статусы</option>
                <option value="SUCCESS">Успешно</option>
                <option value="FAILED">Ошибка</option>
            </select>

            <button
                @click="loadHistory"
                :disabled="loading"
                class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-60"
            >
                {{ loading ? "Загрузка..." : "Обновить" }}
            </button>
        </div>

        <div v-if="error" class="text-red-600 font-semibold">
            {{ error }}
        </div>

        <div v-if="!loading && filteredHistory.length" class="overflow-x-auto">
            <table class="w-full text-left border-collapse">
                <thead class="bg-gray-100 text-gray-900">
                <tr>
                    <th class="p-2 border">ID</th>
                    <th class="p-2 border">Статус</th>
                    <th class="p-2 border">Добавлено</th>
                    <th class="p-2 border">Дата</th>
                    <th class="p-2 border">Файл</th>
                </tr>
                </thead>

                <tbody>
                <tr
                    v-for="record in filteredHistory"
                    :key="record.id"
                    class="hover:bg-gray-50 transition-colors"
                >
                    <td class="p-2 border">{{ record.id }}</td>

                    <td class="p-2 border font-semibold"
                        :class="{
                                'text-green-600': record.status === 'SUCCESS',
                                'text-red-600': record.status === 'FAILED'
                            }"
                    >
                        {{ record.status }}
                    </td>

                    <td class="p-2 border">
                        {{ record.status === "SUCCESS" ? record.addedCount : "-" }}
                    </td>

                    <td class="p-2 border">
                        {{ formatDate(record.timestamp) }}
                    </td>

                    <td class="p-2 border">
                        <a
                            v-if="record.s3Url"
                            :href="record.s3Url"
                            class="text-blue-400 underline"
                            target="_blank"
                        >
                            Скачать
                        </a>
                        <span v-else>-</span>
                    </td>
                </tr>
                </tbody>
            </table>

            <div class="flex justify-between items-center mt-4">
                <button
                    @click="prevPage"
                    :disabled="page === 1"
                    class="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300 disabled:opacity-60"
                >
                    Назад
                </button>

                <span>Страница {{ page }} из {{ totalPages }}</span>

                <button
                    @click="nextPage"
                    :disabled="page === totalPages"
                    class="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300 disabled:opacity-60"
                >
                    Вперед
                </button>
            </div>
        </div>

        <div v-else-if="loading" class="text-gray-500 text-center mt-6">
            Загрузка истории...
        </div>

        <div v-else class="text-gray-500 text-center mt-6">
            История импортов пуста
        </div>
    </div>
</template>

<style scoped>
table {
    border-collapse: collapse;
    width: 100%;
    background: #101f27;
    color: #f1f1f1;
}
</style>
