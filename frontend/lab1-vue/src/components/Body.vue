<script setup lang="ts">
import {ref, reactive, computed, onMounted} from 'vue'

const baseUrl = 'http://localhost:8080/IS-lab1JEE-1.0-SNAPSHOT/api'

const movies = ref([])
const page = ref(1)
const pageSize = ref(10)
const sortBy = ref('id')
const sortDir = ref('asc')
const filterText = ref('')
const totalPages = ref(1)
const coordinatesList = ref([])
const personsList = ref([])
const mpaaRatings = ref(["G", "PG", "PG_13", "R", "NC_17"])
const genres = ref(["WESTERN", "COMEDY", "MUSICAL", "ADVENTURE", "FANTASY"])

const showForm = ref(false)
const formMode = ref('create')
const editId = ref<number | null>(null)

const form = reactive({
    name: '', coordinatesX: 0, coordinatesY: 0, oscarsCount: 0, budget: null, totalBoxOffice: 0,
    mpaaRating: null, directorId: null, screenwriterId: null, operatorId: null, length: null,
    goldenPalmCount: 0, usaBoxOffice: 0.0, tagline: '', genre: ''
})
const errors = reactive({})
const showView = ref(false)
const currentMovie = reactive({})
const showDeleteConfirm = ref(false)
const deleteTarget = reactive({})
const toast = ref('')
const opResult = ref('')
const opGenreThreshold = ref(0)
const opTaglineThreshold = ref(0)
const opGenreToStrip = ref('')

const filtered = computed(() => {
    if (!filterText.value) return movies.value
    const q = filterText.value.toLowerCase()
    return movies.value.filter(m => {
        if (m.name?.toLowerCase().includes(q)) return true
        if (m.tagline?.toLowerCase().includes(q)) return true
        if (m.mpaaRating?.toLowerCase().includes(q)) return true
        if (m.genre?.toLowerCase().includes(q)) return true
        if (m.director && personName(m.director).toLowerCase().includes(q)) return true
        return false
    })
})

const pagedMovies = computed(() => {
    const arr = [...filtered.value]
    arr.sort((a, b) => {
        let A = a[sortBy.value], B = b[sortBy.value]
        if (A === undefined) A = '';
        if (B === undefined) B = ''
        if (sortBy.value === 'creationDate') {
            A = new Date(A);
            B = new Date(B)
        }
        if (typeof A === 'string') A = A.toLowerCase();
        if (typeof B === 'string') B = B.toLowerCase()
        if (A < B) return sortDir.value === 'asc' ? -1 : 1
        if (A > B) return sortDir.value === 'asc' ? 1 : -1
        return 0
    })
    totalPages.value = Math.max(1, Math.ceil(arr.length / pageSize.value))
    const start = (page.value - 1) * pageSize.value
    return arr.slice(start, start + pageSize.value)
})

function formatDate(d) {
    if (!d) return '—';
    return new Date(d).toLocaleDateString()
}

function personName(p) {
    if (!p) return '—';
    return `${p.firstName ?? ''} ${p.lastName ?? ''}`.trim()
}

function personDetailed(p) {
    if (!p) return '—';
    return `${p.firstName ?? ''} ${p.lastName ?? ''} (id ${p.id ?? '?'})`.trim()
}

// === API ===
async function fetchMovies() {
    try {
        const res = await fetch(`${baseUrl}`)
        if (!res.ok) throw new Error(res.statusText)
        movies.value = await res.json()
    } catch (e) {
        toast.value = 'Ошибка загрузки фильмов'
    }
}

async function fetchAuxiliary() {
    try {
        const res1 = await fetch(`${baseUrl}/coordinates`)
        coordinatesList.value = await res1.json()

        const res2 = await fetch(`${baseUrl}/persons`)
        personsList.value = await res2.json()
    } catch (e) {
        toast.value = 'Ошибка загрузки справочников'
    }
}

function initSSE() {
    const es = new EventSource(`${baseUrl}/sse`)
    es.onmessage = ev => {
        toast.value = `Обновление: ${ev.data}`
        fetchMovies()
    }
}

function refresh() {
    fetchMovies()
}

function toggleSort(field: string) {
    if (sortBy.value === field) {
        sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc'
    } else {
        sortBy.value = field
        sortDir.value = 'asc'
    }
}

function viewMovie(id: number) {
    const m = movies.value.find(m => m.id === id)
    if (m) {
        Object.assign(currentMovie, m)
        showView.value = true
    }
}

function openEdit(movie: any) {
    formMode.value = 'edit'
    editId.value = movie.id
    Object.assign(form, movie)
    showForm.value = true
}

function confirmDelete(movie: any) {
    Object.assign(deleteTarget, movie)
    showDeleteConfirm.value = true
}

// === CRUD actions ===
async function saveMovie() {
    const coordinatesDTO = {
        x: form.coordinatesX,
        y: form.coordinatesY,
    }
    try {
        const res = await fetch(`${baseUrl}/coordinates`, {
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(coordinatesDTO)
        })
        if (!res.ok) throw new Error(res.statusText)
        if (!res.ok) throw new Error(res)
        const ans = await res.json();
        const coordID = ans.id;
        fetchMovies()
    } catch (e) {
        toast.value = 'Ошибка сохранения'
    }
}

async function deleteMovie() {
    try {
        const res = await fetch(`${baseUrl}/${deleteTarget.id}`, {method: 'DELETE'})
        if (!res.ok) throw new Error(res.statusText)

        toast.value = 'Фильм удалён'
        showDeleteConfirm.value = false
        fetchMovies()
    } catch (e) {
        toast.value = 'Ошибка удаления'
    }
}

// === Pagination ===
function prevPage() {
    if (page.value > 1) page.value--
}

function nextPage() {
    if (page.value < totalPages.value) page.value++
}

// === Special operations ===
async function opAvgUsaBoxOffice() {
    try {
        const res = await fetch(`${baseUrl}/ops/avg-usaBoxOffice`)
        opResult.value = await res.text()
    } catch {
        opResult.value = 'Ошибка операции'
    }
}

async function opCountGenreGreater() {
    try {
        const res = await fetch(`${baseUrl}/ops/count-genre?threshold=${opGenreThreshold.value}`)
        opResult.value = await res.text()
    } catch {
        opResult.value = 'Ошибка операции'
    }
}

async function opTaglineGreater() {
    try {
        const res = await fetch(`${baseUrl}/ops/tagline?threshold=${opTaglineThreshold.value}`)
        opResult.value = await res.text()
    } catch {
        opResult.value = 'Ошибка операции'
    }
}

async function opNoOscars() {
    try {
        const res = await fetch(`${baseUrl}/ops/no-oscars`)
        opResult.value = await res.text()
    } catch {
        opResult.value = 'Ошибка операции'
    }
}

async function opStripOscars() {
    try {
        const res = await fetch(`${baseUrl}/ops/strip-oscars?genre=${encodeURIComponent(opGenreToStrip.value)}`, {
            method: 'POST'
        })
        opResult.value = await res.text()
        fetchMovies()
    } catch {
        opResult.value = 'Ошибка операции'
    }
}

onMounted(() => {
    fetchMovies()
    fetchAuxiliary()
    initSSE()
})


function openCreate() {
    formMode.value = 'create'
    editId.value = null
    Object.assign(form, {
        name: '', coordinatesId: null, oscarsCount: 0, budget: null, totalBoxOffice: 0,
        mpaaRating: null, directorId: null, screenwriterId: null, operatorId: null, length: null,
        goldenPalmCount: 0, usaBoxOffice: 0.0, tagline: '', genre: ''
    })
    showForm.value = true
}

function closeForm() {
    showForm.value = false
}

async function submitForm() {
    try {
        toast.value = 'Фильм сохранён'
        showForm.value = false
        fetchMovies() // обновляем список
    } catch (e) {
        toast.value = 'Ошибка сохранения фильма'
        console.error(e)
    }
}
</script>
<template>
    <div>

        <h1>Movie Manager</h1>
        <div>
            <button @click="openCreate()" class="btn">Создать фильм</button>
            <button @click="refresh()" title="Обновить список" class="btn">Обновить</button>
        </div>

        <section class="main-section">
            <div class="filter-section">

                <label>
                    Показать на странице:
                    <select v-model.number="pageSize" @change="fetchMovies">
                        <option :value="5">5</option>
                        <option :value="10">10</option>
                        <option :value="25">25</option>
                    </select>
                </label>
            </div>

            <!-- Table -->
            <div class="table-wrapper">
                <table>
                    <thead>
                    <tr>
                        <th @click="toggleSort('id')">ID <small v-if="sortBy==='id'">({{ sortDir }})</small></th>
                        <th @click="toggleSort('name')">Name <small v-if="sortBy==='name'">({{ sortDir }})</small></th>
                        <th>CoordinatesDTO</th>
                        <th @click="toggleSort('creationDate')">Creation date <small
                            v-if="sortBy==='creationDate'">({{ sortDir }})</small></th>
                        <th @click="toggleSort('oscarsCount')">Oscars</th>
                        <th>Budget</th>
                        <th>Total box office</th>
                        <th>MPAA</th>
                        <th>Director</th>
                        <th>Tagline</th>
                        <th>Genre</th>
                        <th>Действия</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="movie in pagedMovies" :key="movie.id">
                        <td>{{ movie.id }}</td>
                        <td>{{ movie.name }}</td>
                        <td>
                            <div v-if="movie.coordinates">{{ movie.coordinates.x }}, {{ movie.coordinates.y }}</div>
                            <div v-else>—</div>
                        </td>
                        <td>{{ formatDate(movie.creationDate) }}</td>
                        <td>{{ movie.oscarsCount }}</td>
                        <td>{{ movie.budget ?? '—' }}</td>
                        <td>{{ movie.totalBoxOffice }}</td>
                        <td>{{ movie.mpaaRating ?? '—' }}</td>
                        <td>{{ personName(movie.director) }}</td>
                        <td>{{ movie.tagline }}</td>
                        <td>{{ movie.genre }}</td>
                        <td>
                            <button @click="viewMovie(movie.id)">Просмотр</button>
                            <button @click="openEdit(movie)">Редактировать</button>
                            <button @click="confirmDelete(movie)" class="delete-btn">Удалить</button>
                        </td>
                    </tr>
                    <tr v-if="pagedMovies.length===0">
                        <td colspan="12" class="no-data">Нет данных</td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <!-- Pagination -->
            <div class="pagination">
                <div>
                    <button @click="prevPage" :disabled="page===1">← Prev</button>
                    <button @click="nextPage" :disabled="page===totalPages">Next →</button>
                </div>
                <div>Страница {{ page }} / {{ totalPages }} — всего {{ filtered.length }} объектов</div>
            </div>
        </section>


        <!-- Модальное окно -->
        <div v-if="showForm" class="modal-overlay">
            <div class="modal-content">
                <h2>{{ formMode === 'create' ? 'Создать фильм' : 'Редактировать фильм' }}</h2>
                <form @submit.prevent="submitForm">
                    <label>
                        Название:
                        <input v-model="form.name" required/>
                    </label>

                    <label>
                        Координаты:
                        <form>
                            X:
                            <input type="number" v-model.number="form.coordinatesX" required/>
                            Y:
                            <input type="number" v-model.number="form.coordinatesY" required/>
                        </form>
                    </label>

                    <label>
                        Кол-во "Оскаров":
                        <input type="number" v-model.number="form.oscarsCount" min="0"/>
                    </label>

                    <label>
                        Бюджет:
                        <input type="number" v-model.number="form.budget" min="0"/>
                    </label>

                    <label>
                        MPAA:
                        <select v-model="form.mpaaRating">
                            <option value="">-- выберите --</option>
                            <option v-for="r in mpaaRatings" :key="r">{{ r }}</option>
                        </select>
                    </label>

                    <label>
                        Режиссёр (ID):
                        <input type="number" v-model.number="form.directorId" required/>
                    </label>

                    <label>
                        Сценарист (ID):
                        <input type="number" v-model.number="form.screenwriterId"/>
                    </label>

                    <label>
                        Оператор (ID):
                        <input type="number" v-model.number="form.operatorId" required/>
                    </label>

                    <label>
                        Жанр:
                        <select v-model="form.genre" required>
                            <option value="">-- выберите --</option>
                            <option v-for="g in genres" :key="g">{{ g }}</option>
                        </select>
                    </label>

                    <label>
                        Tagline:
                        <input type="text" v-model="form.tagline"/>
                    </label>

                    <div class="modal-buttons">
                        <button type="submit" @click="saveMovie">Сохранить</button>
                        <button type="button" @click="closeForm">Отмена</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Special Operations -->
        <section class="operations-section">
            <h2>Специальные операции</h2>
            <div class="operations">
                <button @click="opAvgUsaBoxOffice">Среднее usaBoxOffice</button>

                <label>
                    Количество фильмов с genre >
                    <input v-model.number="opGenreThreshold" type="number"/>
                </label>
                <button @click="opCountGenreGreater">Выполнить</button>

                <label>
                    Tagline длинее
                    <input v-model.number="opTaglineThreshold" type="number"/>
                </label>
                <button @click="opTaglineGreater">Получить массив</button>

                <button @click="opNoOscars">Фильмы без "Оскара"</button>

                <label>
                    Уберите все "Оскары" у режиссёров, снявших фильм в жанре:
                    <select v-model="opGenreToStrip">
                        <option disabled value="">-- выбрать жанр --</option>
                        <option v-for="g in genres" :key="g">{{ g }}</option>
                    </select>
                </label>
                <button @click="opStripOscars">Выполнить</button>
            </div>
            <div v-if="opResult" class="op-result">
                <strong>Результат:</strong>
                <div>{{ opResult }}</div>
            </div>
        </section>

        <!-- Toast -->
        <div v-if="toast" class="toast">{{ toast }}</div>

        <!-- Модалы (просмотр, форма, удаление) будут здесь -->
        <!-- ... можно перенести аналогично как отдельные компоненты, чтобы SFC был чище ... -->

    </div>
</template>

<style scoped>
.header {
    padding: 16px;
    border-bottom: 1px solid #ddd;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.btn {
    margin-right: 8px;
}

.main-section {
    padding: 16px;
}

.filter-section {
    display: flex;
    gap: 12px;
    margin-bottom: 12px;
    align-items: center;
}

.filter-input {
    flex: 1;
    padding: 6px;
}

.table-wrapper {
    overflow: auto;
    border: 1px solid #eee;
    border-radius: 6px;
}

table {
    width: 100%;
    border-collapse: collapse;
}

thead {
    background: #fafafa;
    position: sticky;
    top: 0;
}

th, td {
    padding: 8px;
    border-bottom: 1px solid #eee;
}

.no-data {
    text-align: center;
    color: #666;
    padding: 16px;
}

.pagination {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 12px;
}

.operations-section {
    padding: 16px;
    border-top: 1px solid #eee;
}

.operations {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
    align-items: center;
}

.op-result {
    margin-top: 12px;
    padding: 12px;
    border: 1px dashed #ccc;
    white-space: pre-wrap;
}

.delete-btn {
    color: #b00;
}

.toast {
    position: fixed;
    right: 16px;
    bottom: 16px;
    background: #222;
    color: white;
    padding: 12px 16px;
    border-radius: 6px;
}
</style>
