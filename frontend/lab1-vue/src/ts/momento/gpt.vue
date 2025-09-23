<template>
    <div class="p-6 max-w-6xl mx-auto">
        <!-- Header row: search + create -->
        <div class="flex items-center justify-between mb-4 gap-4">
            <input
                v-model="search"
                type="text"
                placeholder="Фильтр по строковым колонкам..."
                class="border rounded px-3 py-2 flex-1"
            />
            <div class="flex gap-2">
                <button @click="openMovieDialog()" class="btn-primary">+ Добавить фильм</button>
                <button @click="openAuxPanel" class="btn-outline">Управление вспомогательными</button>
            </div>
        </div>

        <!-- Table -->
        <div class="overflow-x-auto border rounded">
            <table class="min-w-full">
                <thead class="bg-gray-100">
                <tr>
                    <th v-for="col in columns" :key="col.key" class="p-2 text-left cursor-pointer" @click="sortBy(col.key)">
                        {{ col.label }}
                        <span v-if="sort.key === col.key" class="ml-1 text-xs">{{ sort.asc ? '▲' : '▼' }}</span>
                    </th>
                    <th class="p-2">Действия</th>
                </tr>
                </thead>
                <tbody>
                <tr v-if="loading" class="p-4">
                    <td :colspan="columns.length + 1" class="p-4 text-center">Загрузка...</td>
                </tr>
                <tr v-for="movie in paginatedMovies" :key="movie.id" class="border-t hover:bg-gray-50">
                    <td class="p-2">{{ movie.id }}</td>
                    <td class="p-2">{{ movie.name }}</td>
                    <td class="p-2">{{ movie.genre }}</td>
                    <td class="p-2">{{ movie.tagline }}</td>
                    <td class="p-2">{{ movie.usaBoxOffice }}</td>
                    <td class="p-2">{{ movie.oscarsCount }}</td>
                    <td class="p-2">
                        <button class="text-blue-600 mr-2" @click="viewMovie(movie.id)">Просмотр</button>
                        <button class="text-blue-600 mr-2" @click="openMovieDialog(movie)">Редактировать</button>
                        <button class="text-red-600" @click="confirmDelete(movie.id)">Удалить</button>
                    </td>
                </tr>
                <tr v-if="!loading && sortedMovies.length === 0">
                    <td :colspan="columns.length + 1" class="p-4 text-center text-gray-600">Нет фильмов</td>
                </tr>
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <div class="flex items-center justify-between mt-4">
            <div>
                Показано {{ paginatedMovies.length }} из {{ filteredMovies.length }}
            </div>
            <div class="flex items-center gap-2">
                <button class="px-3 py-1 border rounded" :disabled="currentPage===1" @click="currentPage--">Prev</button>
                <span>Стр. {{ currentPage }} / {{ totalPages }}</span>
                <button class="px-3 py-1 border rounded" :disabled="currentPage===totalPages" @click="currentPage++">Next</button>
            </div>
        </div>

        <!-- Specials panel -->
        <div class="mt-6 p-4 border rounded bg-gray-50">
            <h3 class="font-semibold mb-2">Специальные операции</h3>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
                <div>
                    <button class="btn-ghost w-full" @click="getAvgBoxOffice">Среднее USA BoxOffice</button>
                </div>
                <div>
                    <input v-model="special.genre" placeholder="Жанр (для count/remove)" class="border px-2 py-1 w-full mb-2" />
                    <div class="flex gap-2">
                        <button class="btn-ghost flex-1" @click="countByGenre">Количество по жанру</button>
                        <button class="btn-danger flex-1" @click="removeOscarsByGenre">Отобрать все Оскары (по жанру)</button>
                    </div>
                </div>
                <div>
                    <input v-model="special.taglineValue" placeholder="Значение для tagline (query)" class="border px-2 py-1 w-full mb-2" />
                    <button class="btn-ghost w-full" @click="getByTagline">Найти по tagline</button>
                </div>
            </div>

            <div v-if="specialResult" class="mt-3 p-2 bg-white border rounded text-sm">
                <pre class="whitespace-pre-wrap">{{ specialResult }}</pre>
            </div>
        </div>

        <!-- Movie modal (create/edit) -->
        <Modal v-if="movieDialogOpen" @close="closeMovieDialog">
            <template #title>{{ editingMovie ? 'Редактировать фильм' : 'Создать фильм' }}</template>
            <template #body>
                <form @submit.prevent="saveMovie">
                    <div class="grid grid-cols-1 gap-2">
                        <label>Название *</label>
                        <input v-model="movieForm.name" type="text" class="border px-2 py-1 w-full" />
                        <div v-if="errors.name" class="text-red-600 text-sm">{{ errors.name }}</div>

                        <label>Жанр *</label>
                        <input v-model="movieForm.genre" type="text" class="border px-2 py-1 w-full" />
                        <div v-if="errors.genre" class="text-red-600 text-sm">{{ errors.genre }}</div>

                        <label>Слоган</label>
                        <input v-model="movieForm.tagline" type="text" class="border px-2 py-1 w-full" />

                        <label>Сборы в США</label>
                        <input v-model.number="movieForm.usaBoxOffice" type="number" class="border px-2 py-1 w-full" />

                        <label>Оскары</label>
                        <input v-model.number="movieForm.oscarsCount" type="number" class="border px-2 py-1 w-full" />

                        <label>Длина</label>
                        <input v-model.number="movieForm.length" type="number" class="border px-2 py-1 w-full" />

                        <label>Budget</label>
                        <input v-model.number="movieForm.budget" type="number" class="border px-2 py-1 w-full" />

                        <!-- Select existing coordinates -->
                        <label>CoordinatesDTO *</label>
                        <select v-model.number="movieForm.coordinatesId" class="border px-2 py-1 w-full">
                            <option :value="null" disabled>-- выбрать --</option>
                            <option v-for="c in coordinatesList" :key="c.id" :value="c.id">
                                {{ c.id }} — x:{{ c.x }} y:{{ c.y }}
                            </option>
                        </select>

                        <!-- Persons -->
                        <label>Director (обязательный)</label>
                        <select v-model.number="movieForm.directorId" class="border px-2 py-1 w-full">
                            <option :value="null" disabled>-- выбрать --</option>
                            <option v-for="p in personsList" :key="p.id" :value="p.id">{{ p.id }} — {{ p.name }}</option>
                        </select>

                        <label>Operator (обязательный)</label>
                        <select v-model.number="movieForm.operatorId" class="border px-2 py-1 w-full">
                            <option :value="null" disabled>-- выбрать --</option>
                            <option v-for="p in personsList" :key="p.id" :value="p.id">{{ p.id }} — {{ p.name }}</option>
                        </select>

                        <label>Screenwriter (опционально)</label>
                        <select v-model.number="movieForm.screenwriterId" class="border px-2 py-1 w-full">
                            <option :value="null">-- нет --</option>
                            <option v-for="p in personsList" :key="p.id" :value="p.id">{{ p.id }} — {{ p.name }}</option>
                        </select>

                        <div v-if="serverError" class="text-red-600 text-sm">{{ serverError }}</div>
                    </div>
                </form>
            </template>
            <template #footer>
                <div class="flex justify-end gap-2">
                    <button class="px-3 py-1 border rounded" @click="closeMovieDialog">Отмена</button>
                    <button class="px-3 py-1 bg-green-600 text-white rounded" @click="saveMovie">
                        Сохранить
                    </button>
                </div>
            </template>
        </Modal>

        <!-- Movie view modal -->
        <Modal v-if="viewingMovie" @close="viewingMovie = null">
            <template #title>Фильм #{{ viewingMovie?.id }} — {{ viewingMovie?.name }}</template>
            <template #body>
                <div class="grid gap-2">
                    <div><strong>Жанр:</strong> {{ viewingMovie?.genre }}</div>
                    <div><strong>Слоган:</strong> {{ viewingMovie?.tagline }}</div>
                    <div><strong>USA BoxOffice:</strong> {{ viewingMovie?.usaBoxOffice }}</div>
                    <div><strong>Оскары:</strong> {{ viewingMovie?.oscarsCount }}</div>
                    <hr/>
                    <div v-if="viewingMovie?.coordinates">
                        <h4 class="font-semibold">CoordinatesDTO</h4>
                        <div>ID: {{ viewingMovie.coordinates.id }}, x: {{ viewingMovie.coordinates.x }}, y: {{ viewingMovie.coordinates.y }}</div>
                    </div>
                    <div v-if="viewingMovie?.director">
                        <h4 class="font-semibold">Director</h4>
                        <div>ID: {{ viewingMovie.director.id }}, name: {{ viewingMovie.director.name }}</div>
                        <div v-if="viewingMovie.director.location">Location: {{ viewingMovie.director.location.name }}</div>
                    </div>
                    <div v-if="viewingMovie?.screenwriter">
                        <h4 class="font-semibold">Screenwriter</h4>
                        <div>ID: {{ viewingMovie.screenwriter.id }}, name: {{ viewingMovie.screenwriter.name }}</div>
                    </div>
                    <div v-if="viewingMovie?.operator">
                        <h4 class="font-semibold">Operator</h4>
                        <div>ID: {{ viewingMovie.operator.id }}, name: {{ viewingMovie.operator.name }}</div>
                    </div>
                </div>
            </template>
            <template #footer>
                <div class="flex justify-end">
                    <button class="px-3 py-1 border rounded" @click="viewingMovie = null">Закрыть</button>
                </div>
            </template>
        </Modal>

        <!-- Aux management panel: coordinates / locations / persons (compact) -->
        <Modal v-if="auxPanelOpen" @close="auxPanelOpen = false">
            <template #title>Вспомогательные сущности</template>
            <template #body>
                <div class="grid md:grid-cols-3 gap-4">
                    <!-- CoordinatesDTO manager -->
                    <div>
                        <h4 class="font-semibold mb-1">CoordinatesDTO</h4>
                        <ul class="max-h-40 overflow-auto text-sm border p-2">
                            <li v-for="c in coordinatesList" :key="c.id" class="flex justify-between items-center py-1">
                                <span>#{{c.id}} x:{{c.x}} y:{{c.y}}</span>
                                <span>
                  <button class="text-blue-600 mr-2" @click="openCoordDialog(c)">E</button>
                  <button class="text-red-600" @click="deleteCoordinate(c.id)">D</button>
                </span>
                            </li>
                        </ul>
                        <button class="btn-primary mt-2" @click="openCoordDialog()">+ Добавить</button>
                    </div>

                    <!-- Locations manager -->
                    <div>
                        <h4 class="font-semibold mb-1">Locations</h4>
                        <ul class="max-h-40 overflow-auto text-sm border p-2">
                            <li v-for="l in locationsList" :key="l.id" class="flex justify-between items-center py-1">
                                <span>#{{l.id}} {{l.name}}</span>
                                <span>
                  <button class="text-blue-600 mr-2" @click="openLocationDialog(l)">E</button>
                  <button class="text-red-600" @click="deleteLocation(l.id)">D</button>
                </span>
                            </li>
                        </ul>
                        <button class="btn-primary mt-2" @click="openLocationDialog()">+ Добавить</button>
                    </div>

                    <!-- Persons manager -->
                    <div>
                        <h4 class="font-semibold mb-1">Persons</h4>
                        <ul class="max-h-40 overflow-auto text-sm border p-2">
                            <li v-for="p in personsList" :key="p.id" class="flex justify-between items-center py-1">
                                <span>#{{p.id}} {{p.name}}</span>
                                <span>
                  <button class="text-blue-600 mr-2" @click="openPersonDialog(p)">E</button>
                  <button class="text-red-600" @click="deletePerson(p.id)">D</button>
                </span>
                            </li>
                        </ul>
                        <button class="btn-primary mt-2" @click="openPersonDialog()">+ Добавить</button>
                    </div>
                </div>
            </template>
            <template #footer>
                <div class="flex justify-end">
                    <button class="px-3 py-1 border rounded" @click="auxPanelOpen=false">Закрыть</button>
                </div>
            </template>
        </Modal>

        <!-- Small reusable entity modals -->
        <EntityModal
            v-if="coordDialogOpen"
            title="Coordinate"
            :entity="coordForm"
            :fields="[{k:'x', label:'X', type:'number'},{k:'y', label:'Y', type:'number'}]"
            @save="saveCoordinate"
            @close="coordDialogOpen=false"
            :errors="entityErrors"
        />

        <EntityModal
            v-if="locationDialogOpen"
            title="Location"
            :entity="locationForm"
            :fields="[{k:'name', label:'Name', type:'text'},{k:'x', label:'X', type:'number'},{k:'y', label:'Y', type:'number'},{k:'z', label:'Z', type:'number'}]"
            @save="saveLocation"
            @close="locationDialogOpen=false"
            :errors="entityErrors"
        />

        <EntityModal
            v-if="personDialogOpen"
            title="Person"
            :entity="personForm"
            :fields="[{k:'name', label:'Name', type:'text'},{k:'hairColor', label:'Hair color', type:'text'},{k:'weight', label:'Weight', type:'number'},{k:'passportID', label:'Passport ID', type:'text'},{k:'locationId', label:'Location', type:'select', options: locationsList}]"
            @save="savePerson"
            @close="personDialogOpen=false"
            :errors="entityErrors"
        />

        <!-- Confirm delete modal -->
        <ConfirmModal v-if="confirmDeleteOpen" @close="confirmDeleteOpen=false" @confirm="doDelete" />
    </div>
</template>

<script setup lang="ts">
/**
 * Body.vue
 * Унифицированная одно-страничная панель для управления сущностями (movies, coordinates, locations, persons).
 * - Базовый URL API: /api
 * - Если реальные endpoints другие — заменяй строки api.get/post/...
 */

import { ref, computed, onMounted, reactive } from 'vue'
import axios from 'axios'

/* ---------- config ---------- */
const apiBase = '/api' // если у тебя другое, поменяй

/* ---------- types (упрощённые DTO) ---------- */
type CoordinatesDto = { id?: number; x: number; y: number }
type LocationDto = { id?: number; name: string; x: number; y?: number; z?: number }
type PersonDto = { id?: number; name: string; hairColor?: string; weight?: number; passportID?: string; location?: LocationDto; locationId?: number }
type MovieDto = {
    id?: number
    name: string
    genre?: string
    tagline?: string
    usaBoxOffice?: number
    oscarsCount?: number
    length?: number
    budget?: number
    coordinates?: CoordinatesDto
    coordinatesId?: number
    director?: PersonDto
    directorId?: number
    screenwriter?: PersonDto | null
    screenwriterId?: number | null
    operator?: PersonDto
    operatorId?: number
    creationDate?: string
}

/* ---------- reactive state ---------- */
const loading = ref(false)
const movies = ref<MovieDto[]>([])
const coordinatesList = ref<CoordinatesDto[]>([])
const locationsList = ref<LocationDto[]>([])
const personsList = ref<PersonDto[]>([])

const search = ref('')
const sort = ref<{ key: keyof MovieDto; asc: boolean }>({ key: 'id', asc: true })
const currentPage = ref(1)
const pageSize = ref(8)

const movieDialogOpen = ref(false)
const editingMovie = ref<MovieDto | null>(null)
const movieForm = reactive<Partial<MovieDto>>({})

const viewingMovie = ref<MovieDto | null>(null)

const auxPanelOpen = ref(false)

/* aux entity modals */
const coordDialogOpen = ref(false)
const coordForm = reactive<Partial<CoordinatesDto>>({})

const locationDialogOpen = ref(false)
const locationForm = reactive<Partial<LocationDto>>({})

const personDialogOpen = ref(false)
const personForm = reactive<Partial<PersonDto>>({})

const confirmDeleteOpen = ref(false)
let idToDelete: number | null = null

/* errors */
const errors = reactive<Record<string, string>>({})
const entityErrors = reactive<Record<string,string>>({})
const serverError = ref('')

/* special operations */
const special = reactive({ genre: '', taglineValue: '' })
const specialResult = ref<string | null>(null)

/* columns */
const columns = [
    { key: 'id', label: 'ID' },
    { key: 'name', label: 'Название' },
    { key: 'genre', label: 'Жанр' },
    { key: 'tagline', label: 'Слоган' },
    { key: 'usaBoxOffice', label: 'Сборы в США' },
    { key: 'oscarsCount', label: 'Оскары' },
]

/* ---------- helpers: API calls ---------- */
const api = axios.create({ baseURL: apiBase, headers: { 'Content-Type': 'application/json' } })

async function fetchAll() {
    loading.value = true
    try {
        const [mRes, cRes, lRes, pRes] = await Promise.all([
            api.get<MovieDto[]>('/movies'),
            api.get<CoordinatesDto[]>('/coordinates'),
            api.get<LocationDto[]>('/locations'),
            api.get<PersonDto[]>('/persons'),
        ])
        movies.value = mRes.data || []
        coordinatesList.value = cRes.data || []
        locationsList.value = lRes.data || []
        personsList.value = pRes.data || []
    } catch (e: any) {
        console.error('fetch error', e)
        serverError.value = e?.response?.data?.message || e.message || 'Ошибка загрузки'
    } finally {
        loading.value = false
    }
}

/* Movie CRUD */
function openMovieDialog(movie?: MovieDto) {
    clearErrors()
    if (movie) {
        editingMovie.value = movie
        Object.assign(movieForm, {
            id: movie.id,
            name: movie.name,
            genre: movie.genre,
            tagline: movie.tagline,
            usaBoxOffice: movie.usaBoxOffice,
            oscarsCount: movie.oscarsCount,
            length: movie.length,
            budget: movie.budget,
            coordinatesId: movie.coordinates?.id ?? movie.coordinatesId,
            directorId: movie.director?.id ?? movie.directorId,
            screenwriterId: movie.screenwriter?.id ?? movie.screenwriterId ?? null,
            operatorId: movie.operator?.id ?? movie.operatorId,
        })
    } else {
        editingMovie.value = null
        Object.assign(movieForm, {
            name: '',
            genre: '',
            tagline: '',
            usaBoxOffice: undefined,
            oscarsCount: undefined,
            length: undefined,
            budget: undefined,
            coordinatesId: null,
            directorId: null,
            screenwriterId: null,
            operatorId: null,
        })
    }
    movieDialogOpen.value = true
}

function closeMovieDialog() {
    movieDialogOpen.value = false
    serverError.value = ''
    clearErrors()
}

/* save movie (POST/PUT) */
async function saveMovie() {
    clearErrors()
    serverError.value = ''
    try {
        const payload: any = {
            name: movieForm.name,
            genre: movieForm.genre,
            tagline: movieForm.tagline,
            usaBoxOffice: movieForm.usaBoxOffice,
            oscarsCount: movieForm.oscarsCount,
            length: movieForm.length,
            budget: movieForm.budget,
            coordinatesId: movieForm.coordinatesId,
            directorId: movieForm.directorId,
            screenwriterId: movieForm.screenwriterId,
            operatorId: movieForm.operatorId,
        }

        if (editingMovie.value && editingMovie.value.id) {
            await api.put(`/movies/${editingMovie.value.id}`, payload)
        } else {
            await api.post('/movies', payload)
        }
        await fetchAll()
        closeMovieDialog()
    } catch (e: any) {
        handleApiError(e, errors, serverError)
    }
}

/* view movie (fetch by id to include related objects) */
async function viewMovie(id?: number) {
    if (!id) return
    try {
        const res = await api.get<MovieDto>(`/movies/${id}`)
        viewingMovie.value = res.data
    } catch (e: any) {
        console.error(e)
        serverError.value = e?.response?.data?.message || 'Ошибка получения фильма'
    }
}

/* delete flow */
function confirmDelete(id: number) {
    idToDelete = id
    confirmDeleteOpen.value = true
}
async function doDelete() {
    if (!idToDelete) return
    try {
        await api.delete(`/movies/${idToDelete}`)
        await fetchAll()
    } catch (e: any) {
        serverError.value = e?.response?.data?.message || 'Ошибка удаления'
    } finally {
        confirmDeleteOpen.value = false
        idToDelete = null
    }
}

/* ---------- Auxiliary entities CRUD ---------- */

/* CoordinatesDTO */
function openCoordDialog(coord?: CoordinatesDto) {
    clearEntityErrors()
    if (coord) Object.assign(coordForm, coord)
    else Object.assign(coordForm, { x: null, y: null, id: null })
    coordDialogOpen.value = true
}
async function saveCoordinate(payload: CoordinatesDto) {
    try {
        if (payload.id) {
            await api.put(`/coordinates/${payload.id}`, payload)
        } else {
            await api.post('/coordinates', payload)
        }
        await fetchAll()
        coordDialogOpen.value = false
    } catch (e: any) {
        handleApiError(e, entityErrors)
    }
}
async function deleteCoordinate(id: number) {
    try {
        await api.delete(`/coordinates/${id}`)
        await fetchAll()
    } catch (e: any) {
        serverError.value = e?.response?.data?.message || 'Ошибка удаления'
    }
}

/* Location */
function openLocationDialog(l?: LocationDto) {
    clearEntityErrors()
    if (l) Object.assign(locationForm, l)
    else Object.assign(locationForm, { name: '', x: null, y: null, z: null, id: null })
    locationDialogOpen.value = true
}
async function saveLocation(payload: LocationDto) {
    try {
        if (payload.id) await api.put(`/locations/${payload.id}`, payload)
        else await api.post('/locations', payload)
        await fetchAll()
        locationDialogOpen.value = false
    } catch (e: any) {
        handleApiError(e, entityErrors)
    }
}
async function deleteLocation(id: number) {
    try {
        await api.delete(`/locations/${id}`)
        await fetchAll()
    } catch (e: any) {
        serverError.value = e?.response?.data?.message || 'Ошибка удаления'
    }
}

/* Person */
function openPersonDialog(p?: PersonDto) {
    clearEntityErrors()
    if (p) Object.assign(personForm, { id: p.id, name: p.name, hairColor: p.hairColor, weight: p.weight, passportID: p.passportID, locationId: p.location?.id ?? null })
    else Object.assign(personForm, { name: '', hairColor: '', weight: null, passportID: '', locationId: null })
    personDialogOpen.value = true
}
async function savePerson(payload: PersonDto) {
    try {
        if (payload.id) await api.put(`/persons/${payload.id}`, payload)
        else await api.post('/persons', payload)
        await fetchAll()
        personDialogOpen.value = false
    } catch (e: any) {
        handleApiError(e, entityErrors)
    }
}
async function deletePerson(id: number) {
    try {
        await api.delete(`/persons/${id}`)
        await fetchAll()
    } catch (e: any) {
        serverError.value = e?.response?.data?.message || 'Ошибка удаления'
    }
}

/* ---------- special ops ---------- */
async function getAvgBoxOffice() {
    try {
        const res = await api.get('/movies/avg-boxoffice')
        specialResult.value = JSON.stringify(res.data, null, 2)
    } catch (e: any) {
        specialResult.value = 'Ошибка: ' + (e?.response?.data?.message || e.message)
    }
}

async function countByGenre() {
    try {
        const genre = encodeURIComponent(special.genre || '')
        const res = await api.get(`/movies/count-by-genre?genre=${genre}`)
        specialResult.value = JSON.stringify(res.data, null, 2)
    } catch (e: any) {
        specialResult.value = 'Ошибка: ' + (e?.response?.data?.message || e.message)
    }
}

async function getByTagline() {
    try {
        const v = encodeURIComponent(special.taglineValue || '')
        const res = await api.get(`/movies/by-tagline?value=${v}`)
        specialResult.value = JSON.stringify(res.data, null, 2)
    } catch (e: any) {
        specialResult.value = 'Ошибка: ' + (e?.response?.data?.message || e.message)
    }
}

async function getNoOscars() {
    try {
        const res = await api.get('/movies/no-oscars')
        specialResult.value = JSON.stringify(res.data, null, 2)
    } catch (e: any) {
        specialResult.value = 'Ошибка: ' + (e?.response?.data?.message || e.message)
    }
}

async function removeOscarsByGenre() {
    try {
        const g = encodeURIComponent(special.genre || '')
        const res = await api.post(`/movies/remove-oscars-by-genre?genre=${g}`)
        specialResult.value = 'OK: ' + JSON.stringify(res.data, null, 2)
        await fetchAll()
    } catch (e: any) {
        specialResult.value = 'Ошибка: ' + (e?.response?.data?.message || e.message)
    }
}

/* ---------- sorting / filtering / pagination ---------- */
const filteredMovies = computed(() => {
    const q = search.value.trim().toLowerCase()
    if (!q) return movies.value
    return movies.value.filter(m =>
        Object.keys(m).some(k => {
            const v = (m as any)[k]
            return typeof v === 'string' && v.toLowerCase().includes(q)
        })
    )
})

const sortedMovies = computed(() => {
    const arr = [...filteredMovies.value]
    const key = sort.value.key as keyof MovieDto
    arr.sort((a: any, b: any) => {
        const av = (a as any)[key]; const bv = (b as any)[key]
        if (av == null && bv == null) return 0
        if (av == null) return sort.value.asc ? -1 : 1
        if (bv == null) return sort.value.asc ? 1 : -1
        if (typeof av === 'number' && typeof bv === 'number') return (av - bv) * (sort.value.asc ? 1 : -1)
        return String(av).localeCompare(String(bv)) * (sort.value.asc ? 1 : -1)
    })
    return arr
})

const totalPages = computed(() => Math.max(1, Math.ceil(sortedMovies.value.length / pageSize.value)))
const paginatedMovies = computed(() => sortedMovies.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value))

function sortBy(key: keyof MovieDto) {
    if (sort.value.key === key) sort.value.asc = !sort.value.asc
    else { sort.value.key = key; sort.value.asc = true }
}

/* ---------- SSE / polling for realtime updates ---------- */
let es: EventSource | null = null
function startSSE() {
    try {
        es = new EventSource(apiBase + '/events') // если сервер поддерживает SSE
        es.onmessage = () => { fetchAll() }
        es.onerror = () => {
            // пробуем через 10 секунд polling
            stopSSE()
            setTimeout(() => startPolling(), 10000)
        }
    } catch {
        startPolling()
    }
}
let pollHandle: number | null = null
function startPolling() {
    if (pollHandle != null) return
    pollHandle = window.setInterval(() => fetchAll(), 8000)
}
function stopSSE() {
    if (es) { es.close(); es = null }
    if (pollHandle) { clearInterval(pollHandle); pollHandle = null }
}

/* ---------- error helpers ---------- */
function handleApiError(e: any, target: Record<string,string>, global?: any) {
    if (!e || !e.response) {
        target['__'] = e?.message || 'Неизвестная ошибка'
        if (global) global.value = target['__']
        return
    }
    const r = e.response
    if (r.status === 400 || r.status === 422) {
        // ожидаем { field: message } или message
        const data = r.data
        if (typeof data === 'object') {
            for (const k of Object.keys(data)) {
                target[k] = data[k]
            }
        } else {
            target['__'] = String(data)
            if (global) global.value = target['__']
        }
    } else {
        target['__'] = r.data?.message || r.statusText || 'Ошибка сервера'
        if (global) global.value = target['__']
    }
}

function clearErrors() { Object.keys(errors).forEach(k => delete errors[k]) }
function clearEntityErrors() { Object.keys(entityErrors).forEach(k=>delete entityErrors[k]) }

/* ---------- lifecycle ---------- */
onMounted(() => {
    fetchAll()
    startSSE()
})

/* cleanup on unmount (optional) */
// onUnmounted(() => stopSSE())

/* ---------- expose for template ---------- */
const movieDialogOpenRef = movieDialogOpen
const coordDialogOpenRef = coordDialogOpen
const locationDialogOpenRef = locationDialogOpen
const personDialogOpenRef = personDialogOpen

const openAuxPanel = () => { auxPanelOpen.value = true }

/* For ConfirmModal's doDelete */
function doDeleteConfirm() { if (idToDelete) doDelete() }

/* expose used in template */
const viewingMovieRef = viewingMovie

const paginatedMoviesRef = paginatedMovies
const filteredMoviesRef = filteredMovies
const sortedMoviesRef = sortedMovies
const totalPagesRef = totalPages

</script>

<style scoped>
/* минимальные стили для кнопок и модалок — или используй свой UI */
.btn-primary { background: #2563eb; color: white; padding: 8px 12px; border-radius: 6px }
.btn-outline { border: 1px solid #cbd5e1; padding: 8px 10px; border-radius: 6px; background: white }
.btn-ghost { background: #f8fafc; padding: 8px; border-radius: 6px }
.btn-danger { background: #ef4444; color: #fff; padding: 8px; border-radius: 6px }
table { border-collapse: collapse; width: 100% }
table th, table td { border-bottom: 1px solid #e5e7eb; padding: 8px }
</style>
