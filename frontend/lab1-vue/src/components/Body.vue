<script setup lang="ts">
import {ref, computed, onMounted} from "vue"
import axios from "axios"
import {Movie} from "../ts/dto/movie.ts"


const movies = ref<Movie[]>([])
const search = ref("")
const sort = ref<{ key: keyof Movie; asc: boolean }>({key: "id", asc: true})
const currentPage = ref(1)
const pageSize = 5

const dialogOpen = ref(false)
const editMovie = ref<Movie | null>(null)
const form = ref<Partial<Movie>>({})
const specialResult = ref<string | null>(null)

const columns = [
    {key: "id", label: "ID"},
    {key: "name", label: "Название"},
    {key: "genre", label: "Жанр"},
    {key: "tagline", label: "Слоган"},
    {key: "usaBoxOffice", label: "Сборы в США"},
    {key: "oscarsCount", label: "Оскары"},
]

async function fetchMovies() {
    const res = await axios.get<Movie[]>("/api/movies")
    movies.value = res.data
}

const filteredMovies = computed(() =>
    movies.value.filter((m) =>
        Object.values(m).some(
            (val) => typeof val === "string" && val.toLowerCase().includes(search.value.toLowerCase())
        )
    )
)

const sortedMovies = computed(() =>
    [...filteredMovies.value].sort((a, b) => {
        const key = sort.value.key
        const order = sort.value.asc ? 1 : -1
        if (a[key] < b[key]) return -1 * order
        if (a[key] > b[key]) return 1 * order
        return 0
    })
)

const totalPages = computed(() => Math.ceil(sortedMovies.value.length / pageSize))

const paginatedMovies = computed(() =>
    sortedMovies.value.slice((currentPage.value - 1) * pageSize, currentPage.value * pageSize)
)

function sortBy(key: keyof Movie) {
    if (sort.value.key === key) {
        sort.value.asc = !sort.value.asc
    } else {
        sort.value.key = key
        sort.value.asc = true
    }
}

function openCreateDialog() {
    form.value = {}
    editMovie.value = null
    dialogOpen.value = true
}

function openEditDialog(movie: Movie) {
    form.value = {...movie}
    editMovie.value = movie
    dialogOpen.value = true
}

function closeDialog() {
    dialogOpen.value = false
}

async function saveMovie() {
    if (editMovie.value) {
        await axios.put(`/api/movies/${editMovie.value.id}`, form.value)
    } else {
        await axios.post("/api/movies", form.value)
    }
    closeDialog()
    await fetchMovies()
}

async function deleteMovie(id: number) {
    await axios.delete(`/api/movies/${id}`)
    await fetchMovies()
}

async function getAvgBoxOffice() {
    const res = await axios.get("/api/movies/avg-boxoffice")
    specialResult.value = JSON.stringify(res.data, null, 2)
}

async function getNoOscars() {
    const res = await axios.get("/api/movies/no-oscars")
    specialResult.value = JSON.stringify(res.data, null, 2)
}

onMounted(fetchMovies)
</script>

<template>
    <div class="p-6">
        <div class="flex justify-between items-center mb-4">
            <input
                v-model="search"
                type="text"
                placeholder="Фильтр по строковым колонкам..."
                class="border rounded px-3 py-1 w-1/3"
            />
            <button
                @click="openCreateDialog"
                class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
            >
                + Добавить фильм
            </button>
        </div>

        <table class="w-full border-collapse border">
            <thead class="bg-gray-200">
            <tr>
                <th v-for="col in columns" :key="col.key" class="p-2 cursor-pointer" @click="sortBy(col.key)">
                    {{ col.label }}
                    <span v-if="sort.key === col.key">{{ sort.asc ? '▲' : '▼' }}</span>
                </th>
                <th class="p-2">Действия</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="movie in paginatedMovies" :key="movie.id" class="border-t">
                <td class="p-2">{{ movie.id }}</td>
                <td class="p-2">{{ movie.name }}</td>
                <td class="p-2">{{ movie.genre }}</td>
                <td class="p-2">{{ movie.tagline }}</td>
                <td class="p-2">{{ movie.usaBoxOffice }}</td>
                <td class="p-2">{{ movie.oscarsCount }}</td>
                <td class="p-2">
                    <button class="text-blue-600" @click="openEditDialog(movie)">Редактировать</button>
                    <button class="text-red-600 ml-2" @click="deleteMovie(movie.id)">Удалить</button>
                </td>
            </tr>
            </tbody>
        </table>

        <div class="flex justify-center mt-4 space-x-2">
            <button
                v-for="page in totalPages"
                :key="page"
                @click="currentPage = page"
                class="px-3 py-1 rounded border"
                :class="currentPage === page ? 'bg-blue-500 text-white' : 'bg-white'"
            >
                {{ page }}
            </button>
        </div>

        <div v-if="dialogOpen" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
            <div class="bg-white p-6 rounded shadow-lg w-96">
                <h2 class="text-lg font-bold mb-4">{{ editMovie ? 'Редактировать' : 'Создать' }} фильм</h2>
                <form @submit.prevent="saveMovie">
                    <label class="block mb-2">Название:</label>
                    <input v-model="form.name" type="text" class="border w-full mb-3 px-2 py-1" required/>

                    <label class="block mb-2">Жанр:</label>
                    <input v-model="form.genre" type="text" class="border w-full mb-3 px-2 py-1" required/>

                    <label class="block mb-2">Слоган:</label>
                    <input v-model="form.tagline" type="text" class="border w-full mb-3 px-2 py-1"/>

                    <label class="block mb-2">Сборы в США:</label>
                    <input v-model.number="form.usaBoxOffice" type="number" class="border w-full mb-3 px-2 py-1"/>

                    <label class="block mb-2">Оскары:</label>
                    <input v-model.number="form.oscarsCount" type="number" class="border w-full mb-3 px-2 py-1"/>

                    <div class="flex justify-end space-x-2">
                        <button type="button" class="px-3 py-1 border rounded" @click="closeDialog">Отмена</button>
                        <button type="submit" class="px-3 py-1 bg-green-600 text-white rounded">Сохранить</button>
                    </div>
                </form>
            </div>
        </div>

        <div class="mt-8">
            <h2 class="text-lg font-bold mb-3">Специальные операции</h2>
            <div class="flex space-x-4">
                <button class="px-4 py-2 border rounded bg-gray-100" @click="getAvgBoxOffice">
                    Среднее USA BoxOffice
                </button>
                <button class="px-4 py-2 border rounded bg-gray-100" @click="getNoOscars">
                    Фильмы без Оскара
                </button>
            </div>
            <div v-if="specialResult" class="mt-4 p-4 border rounded bg-gray-50">
                <pre>{{ specialResult }}</pre>
            </div>
        </div>
    </div>
</template>

<style scoped>
table th,
table td {
    border: 1px solid #ccc;
}
</style>
