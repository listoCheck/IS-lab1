<script setup lang="ts">
import "../../css/entity.css"
import { reactive, ref, computed, onMounted } from "vue";
import type {MovieDTO} from "@/ts/dto/MovieDTO.ts";

const baseUrl = "http://localhost:8080/IS-lab1JEE-1.0-SNAPSHOT/api";
const toast = ref("");

const sortDir = ref<"asc" | "desc">("asc");
const sortBy = ref<
    | "id"
    | "name"
    | "oscarsCount"
    | "budget"
    | "totalBoxOffice"
    | "mpaaRating"
    | "length"
    | "goldenPalmCount"
    | "usaBoxOffice"
    | "tagline"
    | "genre"
>("id");

const showForm = ref(false);
const formMode = ref<"create" | "edit">("create");
const editId = ref<number | null>(null);

const mpaaRatings = ["G", "PG", "PG_13", "R", "NC_17"];
const genres = ["WESTERN", "COMEDY", "MUSICAL", "ADVENTURE", "FANTASY"];

const coordinates = ref<{ id: number; x: number; y: number }[]>([]);
const persons = ref<{ id: number; name: string }[]>([]);

const form = reactive({
    name: "",
    coordinatesId: null as number | null,
    oscarsCount: 0,
    budget: null as number | null,
    totalBoxOffice: 0,
    mpaaRating: "",
    directorId: null as number | null,
    screenwriterId: null as number | null,
    operatorId: null as number | null,
    length: null as number | null,
    goldenPalmCount: 0,
    usaBoxOffice: 0,
    tagline: "",
    genre: "",
});

const page = ref(0);
const pageSize = 5;

const moviesList = ref<MovieDTO[]>([]);

const pagedMovies = computed(() => {
    return [...moviesList.value].sort((a, b) => {
        const field = sortBy.value as keyof typeof a;
        if (a[field] < b[field]) return sortDir.value === "asc" ? -1 : 1;
        if (a[field] > b[field]) return sortDir.value === "asc" ? 1 : -1;
        return 0;
    });
});

function toggleSort(field: typeof sortBy.value) {
    if (sortBy.value === field) {
        sortDir.value = sortDir.value === "asc" ? "desc" : "asc";
    } else {
        sortBy.value = field;
        sortDir.value = "asc";
    }
}

function openEdit(movie: MovieDTO) {
    formMode.value = "edit";
    editId.value = movie.id;
    Object.assign(form, {
        ...movie,
        coordinatesId: movie.coordinates.id,
        directorId: movie.director?.id ?? null,
        screenwriterId: movie.screenwriter?.id ?? null,
        operatorId: movie.operator?.id ?? null,
    });
    showForm.value = true;
}

async function confirmDelete(movie: MovieDTO) {
    try {
        await fetch(`${baseUrl}/movie/${movie.id}`, {
            method: "DELETE",
            headers: { "Content-Type": "application/json" },
        });
        await fetchMovies();
    } catch {
        toast.value = "Ошибка удаления Movie";
    }
}

async function fetchMovies() {
    try {
        const res = await fetch(`${baseUrl}/movie/table`);
        if (!res.ok) throw new Error(res.statusText);
        moviesList.value = await res.json();
    } catch {
        toast.value = "Ошибка загрузки Movie";
    }
}

async function fetchCoordinates() {
    try {
        const res = await fetch(`${baseUrl}/coordinates/table`);
        if (!res.ok) throw new Error(res.statusText);
        coordinates.value = await res.json();
    } catch {
        toast.value = "Ошибка загрузки Coordinates";
    }
}

async function fetchPersons() {
    try {
        const res = await fetch(`${baseUrl}/person/table`);
        if (!res.ok) throw new Error(res.statusText);
        persons.value = await res.json();
    } catch {
        toast.value = "Ошибка загрузки Persons";
    }
}

async function saveMovie() {
    const movieDTO = {
        name: form.name,
        coordinatesId: form.coordinatesId,
        oscarsCount: form.oscarsCount,
        budget: form.budget,
        totalBoxOffice: form.totalBoxOffice,
        mpaaRating: form.mpaaRating || null,
        directorId: form.directorId,
        screenwriterId: form.screenwriterId,
        operatorId: form.operatorId,
        length: form.length,
        goldenPalmCount: form.goldenPalmCount,
        usaBoxOffice: form.usaBoxOffice,
        tagline: form.tagline,
        genre: form.genre,
    };

    try {
        let res;
        if (formMode.value === "create") {
            res = await fetch(`${baseUrl}/movie`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(movieDTO),
            });
        } else if (formMode.value === "edit" && editId.value !== null) {
            res = await fetch(`${baseUrl}/movie/${editId.value}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(movieDTO),
            });
        }
        if (!res || !res.ok) throw new Error(res?.statusText);
        await fetchMovies();
        showForm.value = false;
        toast.value = "Movie сохранён!";
    } catch {
        toast.value = "Ошибка сохранения Movie";
    }
}
function nextPage() {
    page.value++;
    fetchMovies();
}

function prevPage() {
    if (page.value > 0) {
        page.value--;
        fetchMovies();
    }
}


onMounted(() => {
    fetchCoordinates();
    fetchPersons();
    fetchMovies();
});
</script>

<template>
    <div class="movie-wrapper">
        <div v-if="toast" class="toast">{{ toast }}</div>

        <div v-if="showForm">
            <form @submit.prevent="saveMovie">
                <label>
                    Name:
                    <input type="text" v-model="form.name" required />
                </label>

                <label>
                    Coordinates:
                    <select v-model.number="form.coordinatesId" required>
                        <option disabled value="">-- select coordinates --</option>
                        <option v-for="c in coordinates" :key="c.id" :value="c.id">
                            {{ c.id }} ({{ c.x }}, {{ c.y }})
                        </option>
                    </select>
                </label>

                <label>
                    Oscars Count:
                    <input type="number" v-model.number="form.oscarsCount" min="1" required />
                </label>

                <label>
                    Budget:
                    <input type="number" v-model.number="form.budget" min="1" />
                </label>

                <label>
                    Total Box Office:
                    <input type="number" v-model.number="form.totalBoxOffice" min="1" required />
                </label>

                <label>
                    MPAA Rating:
                    <select v-model="form.mpaaRating">
                        <option value="">-- none --</option>
                        <option v-for="r in mpaaRatings" :key="r" :value="r">{{ r }}</option>
                    </select>
                </label>

                <label>
                    Director:
                    <select v-model.number="form.directorId" required>
                        <option disabled value="">-- select director --</option>
                        <option v-for="p in persons" :key="p.id" :value="p.id">{{ p.id }} - {{ p.name }}</option>
                    </select>
                </label>

                <label>
                    Screenwriter:
                    <select v-model.number="form.screenwriterId">
                        <option value="">-- none --</option>
                        <option v-for="p in persons" :key="p.id" :value="p.id">{{ p.id }} - {{ p.name }}</option>
                    </select>
                </label>

                <label>
                    Operator:
                    <select v-model.number="form.operatorId" required>
                        <option disabled value="">-- select operator --</option>
                        <option v-for="p in persons" :key="p.id" :value="p.id">{{ p.id }} - {{ p.name }}</option>
                    </select>
                </label>

                <label>
                    Length:
                    <input type="number" v-model.number="form.length" min="1" />
                </label>

                <label>
                    Golden Palm Count:
                    <input type="number" v-model.number="form.goldenPalmCount" min="1" required />
                </label>

                <label>
                    USA Box Office:
                    <input type="number" v-model.number="form.usaBoxOffice" min="1" step="0.01" required />
                </label>

                <label>
                    Tagline:
                    <input type="text" v-model="form.tagline" required />
                </label>

                <label>
                    Genre:
                    <select v-model="form.genre" required>
                        <option disabled value="">-- select genre --</option>
                        <option v-for="g in genres" :key="g" :value="g">{{ g }}</option>
                    </select>
                </label>

                <button type="submit">Сохранить</button>
                <button type="button" @click="showForm = false" class="delete-btn">Закрыть</button>
            </form>
        </div>

        <button v-else @click="showForm = true; formMode = 'create'">Добавить Movie</button>

        <table>
            <thead>
            <tr>
                <th @click="toggleSort('id')">ID</th>
                <th @click="toggleSort('name')">Название</th>
                <th>Координаты</th>
                <th @click="toggleSort('oscarsCount')">Oscars</th>
                <th @click="toggleSort('budget')">Бюджет</th>
                <th @click="toggleSort('totalBoxOffice')">Box Office</th>
                <th @click="toggleSort('mpaaRating')">MPAA</th>
                <th>Режиссёр</th>
                <th>Сценарист</th>
                <th>Оператор</th>
                <th @click="toggleSort('length')">Длина</th>
                <th @click="toggleSort('goldenPalmCount')">Golden Palm</th>
                <th @click="toggleSort('usaBoxOffice')">USA Box Office</th>
                <th @click="toggleSort('tagline')">Слоган</th>
                <th @click="toggleSort('genre')">Жанр</th>
                <th>Действия</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="m in pagedMovies" :key="m.id">
                <td>{{ m.id }}</td>
                <td>{{ m.name }}</td>
                <td>{{ m.coordinates.x + " " + m.coordinates.y }}</td>
                <td>{{ m.oscarsCount }}</td>
                <td>{{ m.budget }}</td>
                <td>{{ m.totalBoxOffice }}</td>
                <td>{{ m.mpaaRating }}</td>
                <td>{{ m.director?.name }}</td>
                <td>{{ m.screenwriter?.name }}</td>
                <td>{{ m.operator?.name }}</td>
                <td>{{ m.length }}</td>
                <td>{{ m.goldenPalmCount }}</td>
                <td>{{ m.usaBoxOffice }}</td>
                <td>{{ m.tagline }}</td>
                <td>{{ m.genre }}</td>
                <td>
                    <button @click="openEdit(m)">Редактировать</button>
                    <button @click="confirmDelete(m)" class="delete-btn">Удалить</button>
                </td>
            </tr>
            <tr v-if="pagedMovies.length === 0">
                <td colspan="16">Нет данных</td>
            </tr>
            </tbody>
        </table>
        <div class="pagination">
            <button @click="prevPage" :disabled="page===0">Назад</button>
            <span>Стр: {{ page + 1 }}</span>
            <button @click="nextPage" :disabled="pagedMovies.length < pageSize">Вперёд</button>
        </div>
    </div>
</template>

<style scoped>


table {
    border-collapse: collapse;
    width: 100%;
    color: #f1f1f1;
    background: #101F27;
}
.table-wrapper {
    padding: 1.5rem;
    max-width: 800px;
    color: #f1f1f1;
    background: #101F27;
    border-radius: 12px;
    margin-left: 2px;
}
</style>

