<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";

const baseUrl = "http://localhost:8080/IS-lab1JEE-1.0-SNAPSHOT/api";

const toast = ref("");
const showForm = ref(false);
const formMode = ref<"create" | "edit">("create");
const editId = ref<number | null>(null);

const mpaaRatings = ["G", "PG", "PG_13", "R", "NC_17"];
const genres = ["WESTERN", "COMEDY", "MUSICAL", "ADVENTURE", "FANTASY"];

// списки для селектов
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

async function fetchCoordinates() {
    try {
        const res = await fetch(`${baseUrl}/coordinates`);
        if (!res.ok) throw new Error(res.statusText);
        coordinates.value = await res.json();
    } catch {
        toast.value = "Ошибка загрузки Coordinates";
    }
}

async function fetchPersons() {
    try {
        const res = await fetch(`${baseUrl}/person`);
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
        toast.value = "Movie сохранён!";
        showForm.value = false;
    } catch {
        toast.value = "Ошибка сохранения Movie";
    }
}

// загружаем данные при монтировании
onMounted(() => {
    fetchCoordinates();
    fetchPersons();
});
</script>

<template>
    <div class="movie-form-wrapper">
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
                        <option v-for="p in persons" :key="p.id" :value="p.id">
                            {{ p.id }} - {{ p.name }}
                        </option>
                    </select>
                </label>

                <label>
                    Screenwriter:
                    <select v-model.number="form.screenwriterId">
                        <option value="">-- none --</option>
                        <option v-for="p in persons" :key="p.id" :value="p.id">
                            {{ p.id }} - {{ p.name }}
                        </option>
                    </select>
                </label>

                <label>
                    Operator:
                    <select v-model.number="form.operatorId" required>
                        <option disabled value="">-- select operator --</option>
                        <option v-for="p in persons" :key="p.id" :value="p.id">
                            {{ p.id }} - {{ p.name }}
                        </option>
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
                <button type="button" @click="showForm = false">Отмена</button>
            </form>
        </div>

        <button v-else @click="showForm = true; formMode = 'create'; fetchCoordinates(); fetchPersons();">
            Добавить Movie
        </button>
    </div>
</template>

<style scoped>
.movie-form-wrapper {
    padding: 1rem;
}
label {
    display: block;
    margin: 0.5rem 0;
}
.toast {
    margin-bottom: 1rem;
    color: red;
}
</style>
