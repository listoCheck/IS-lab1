<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const history = ref([])
const filterUser = ref('')
const filterStatus = ref('')

const loadHistory = async () => {
    try {
        const response = await axios.get('/api/import/history')
        history.value = response.data
    } catch (err) {
        console.error('Ошибка при загрузке истории:', err)
    }
}

const filteredHistory = computed(() =>
    history.value.filter((r) => {
        return (
            (!filterUser.value || r.user.includes(filterUser.value)) &&
            (!filterStatus.value || r.status === filterStatus.value)
        )
    })
)

const formatDate = (iso) =>
    new Date(iso).toLocaleString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
    })

onMounted(loadHistory)
</script>
<template>
    <div class="p-6 max-w-4xl mx-auto space-y-6">
        <h1 class="text-2xl font-bold">История импортов</h1>

        <div class="flex items-center gap-4">
            <input
                v-model="filterUser"
                placeholder="Фильтр по пользователю"
                class="border rounded-lg px-3 py-2"
            />
            <select v-model="filterStatus" class="border rounded-lg px-3 py-2">
                <option value="">Все статусы</option>
                <option value="SUCCESS">Успешно</option>
                <option value="FAILED">Ошибка</option>
            </select>
            <button
                class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                @click="loadHistory"
            >
                Обновить
            </button>
        </div>

        <table class="w-full text-left border-collapse">
            <thead class="bg-gray-100">
            <tr>
                <th class="p-2 border">ID</th>
                <th class="p-2 border">Пользователь</th>
                <th class="p-2 border">Статус</th>
                <th class="p-2 border">Добавлено</th>
                <th class="p-2 border">Дата</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="record in filteredHistory" :key="record.id">
                <td class="p-2 border">{{ record.id }}</td>
                <td class="p-2 border">{{ record.user }}</td>
                <td
                    class="p-2 border font-semibold"
                    :class="{
              'text-green-600': record.status === 'SUCCESS',
              'text-red-600': record.status === 'FAILED'
            }"
                >
                    {{ record.status }}
                </td>
                <td class="p-2 border">{{ record.status === 'SUCCESS' ? record.addedCount : '-' }}</td>
                <td class="p-2 border">{{ formatDate(record.timestamp) }}</td>
            </tr>
            </tbody>
        </table>

        <div v-if="!history.length" class="text-gray-500 text-center mt-4">
            История импортов пуста
        </div>
    </div>
</template>

