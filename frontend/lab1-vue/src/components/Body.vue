<template>
    <div>

        <h1>Movie Manager</h1>
        <div>
            <button @click="openCreate()" class="btn">Создать фильм</button>
            <button @click="refresh()" title="Обновить список" class="btn">Обновить</button>
        </div>

        <!-- Main Section -->
        <section class="main-section">
            <!-- Filter and Pagination -->
            <div class="filter-section">
                <input v-model="filterText" @input="applyFilter"
                       placeholder="Фильтрация по строковым колонкам (неполное совпадение)"
                       class="filter-input"/>
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
                        <th>Coordinates</th>
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

<script setup>
import {ref, reactive, computed, onMounted} from 'vue'
import axios from 'axios'

const baseUrl = '/movies'

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
const editId = ref(null)
const form = reactive({
    name: '', coordinatesId: null, oscarsCount: 0, budget: null, totalBoxOffice: 0,
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

// API functions, UI actions, special operations, SSE init, etc.
// Можно перенести полностью из вашего скрипта, без изменений, т.к. setup позволяет держать всё реактивное

onMounted(() => {
    fetchMovies()
    fetchAuxiliary()
    initSSE()
})

</script>

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
