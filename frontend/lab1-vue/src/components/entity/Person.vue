<script setup lang="ts">
import {reactive, ref, computed, onMounted} from "vue";
import type {LocationDTO} from "@/ts/dto/LocationDTO.ts";
import type {PersonDTO} from "@/ts/dto/PersonDTO.ts";

const baseUrl = "http://localhost:8080/IS-lab1JEE-1.0-SNAPSHOT/api";
const toast = ref("");

const colors = ["GREEN", "BLACK", "YELLOW", "BROWN"];
const countries = ["RUSSIA", "UNITED_KINGDOM", "VATICAN", "ITALY"];

const sortDir = ref<"asc" | "desc">("asc");
const sortBy = ref<"id" | "name" | "eyeColor" | "hairColor" | "weight" | "passportID" | "nationality" | "locationId">("id");

const showForm = ref(false);
const formMode = ref<"create" | "edit">("create");
const editId = ref<number | null>(null);

const form = reactive({
    name: "",
    eyeColor: "",
    hairColor: "",
    location: "",
    weight: 0,
    passportID: "",
    nationality: "",
});

const personsList = ref<PersonDTO[]>([]);
const locationsList = ref<LocationDTO[]>([]);

const pagedPersons = computed(() => {
    return [...personsList.value].sort((a, b) => {
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

function openEdit(person: PersonDTO) {
    formMode.value = "edit";
    editId.value = person.id;
    Object.assign(form, person);
    showForm.value = true;
}

async function confirmDelete(person: PersonDTO) {
    personsList.value = personsList.value.filter((c) => c.id !== person.id);
    try {
        const res = await fetch(`${baseUrl}/person/` + person.id, {
            method: "DELETE",
            headers: {"Content-Type": "application/json"},
        });
        if (!res.ok) throw new Error(res.statusText);
        personsList.value = await res.json();
    } catch (e) {
        toast.value = "Ошибка удаления";
    }
    await fetchPersons();
}

async function fetchPersons() {
    try {
        const res = await fetch(`${baseUrl}/person/table`, {
            method: "GET",
            headers: {"Content-Type": "application/json"},
        });
        if (!res.ok) throw new Error(res.statusText);
        personsList.value = await res.json();
        console.log(personsList);
    } catch (e) {
        toast.value = "Ошибка загрузки персоналий";
    }
}

async function fetchLocations() {
    try {
        const res = await fetch(`${baseUrl}/location/table`, {
            method: "GET",
            headers: {"Content-Type": "application/json"},
        });
        if (!res.ok) throw new Error(res.statusText);
        locationsList.value = await res.json();
    } catch (e) {
        toast.value = "Ошибка загрузки локаций";
    }
}

async function savePerson() {
    const personDTO = {
        name: form.name,
        eyeColor: form.eyeColor || null,
        hairColor: form.hairColor,
        location: form.locationId,
        weight: form.weight,
        passportID: form.passportID,
        nationality: form.nationality || null,
    };
    try {
        let res;
        if (formMode.value === "create") {
            res = await fetch(`${baseUrl}/person`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(personDTO),
            });
        } else if (formMode.value === "edit" && editId.value !== null) {
            res = await fetch(`${baseUrl}/person/${editId.value}`, {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(personDTO),
            });
        }
        if (!res || !res.ok) throw new Error(res?.statusText);
        await fetchPersons();
        showForm.value = false;
        toast.value = "Сохранено!";
    } catch (e) {
        toast.value = "Ошибка сохранения";
    }
}

onMounted(async () => {
    await fetchLocations();
    await fetchPersons();
});
</script>

<template>
    <div class="table-wrapper">
        <div v-if="toast" class="toast">{{ toast }}</div>

        <div v-if="showForm">
            <form @submit.prevent="savePerson">
                Name:
                <input type="text" v-model="form.name" required/>

                Eye Color:
                <select v-model="form.eyeColor">
                    <option value="">-- none --</option>
                    <option v-for="c in colors" :key="c" :value="c">{{ c }}</option>
                </select>

                Hair Color:
                <select v-model="form.hairColor" required>
                    <option disabled value="">-- select --</option>
                    <option v-for="c in colors" :key="c" :value="c">{{ c }}</option>
                </select>

                Location:
                <select v-model="form.locationId" required>
                    <option disabled value="">-- select location --</option>
                    <option v-for="loc in locationsList" :key="loc" :value="loc">
                        {{ loc.id }} ({{ loc.x }}, {{ loc.y }}, {{ loc.z }})
                        {{ loc.name ? " - " + loc.name : "" }}
                    </option>
                </select>

                Weight:
                <input type="number" step="0.01" v-model.number="form.weight" required/>

                Passport ID:
                <input type="text" v-model="form.passportID" required/>

                Nationality:
                <select v-model="form.nationality">
                    <option value="">-- none --</option>
                    <option v-for="c in countries" :key="c" :value="c">{{ c }}</option>
                </select>



                <button type="submit">Сохранить</button>
            </form>
        </div>
        <button v-else @click="showForm = true; formMode = 'create'">Добавить</button>

        <table>
            <thead>
            <tr>
                <th @click="toggleSort('id')">ID <small v-if="sortBy==='id'">({{ sortDir }})</small></th>
                <th @click="toggleSort('name')">Имя <small v-if="sortBy==='name'">({{ sortDir }})</small></th>
                <th @click="toggleSort('eyeColor')">Глаза</th>
                <th @click="toggleSort('hairColor')">Волосы</th>
                <th @click="toggleSort('locationId')">Локация</th>
                <th @click="toggleSort('weight')">Вес</th>
                <th @click="toggleSort('passportID')">Паспорт</th>
                <th @click="toggleSort('nationality')">Национальность</th>
                <th>Действия</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="p in pagedPersons" :key="p.id">
                <td>{{ p.id }}</td>
                <td>{{ p.name }}</td>
                <td>{{ p.eyeColor }}</td>
                <td>{{ p.hairColor }}</td>
                <td>{{ p.location.id + " " + p.location.name}}</td>
                <td>{{ p.weight }}</td>
                <td>{{ p.passportID }}</td>
                <td>{{ p.nationality }}</td>

                <td>
                    <button @click="openEdit(p)">Редактировать</button>
                    <button @click="confirmDelete(p)" class="delete-btn">Удалить</button>
                </td>
            </tr>
            <tr v-if="pagedPersons.length === 0">
                <td colspan="9" class="no-data">Нет данных</td>
            </tr>
            </tbody>
        </table>
    </div>
</template>

<style scoped>
.table-wrapper {
    padding: 1rem;
}

.toast {
    margin-bottom: 1rem;
    color: red;
}
</style>
