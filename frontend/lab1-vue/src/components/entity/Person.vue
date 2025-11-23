<script setup lang="ts">
import {reactive, ref, computed, onMounted, onUnmounted} from "vue";
import "../../css/entity.css";
import type {LocationDTO} from "../../ts/dto/LocationDTO.ts";
import type {PersonDTO} from "../../ts/dto/PersonDTO.ts";

const baseUrl = "http://localhost:8080/IS-lab1JEE-1.0-SNAPSHOT/api";
//const baseUrl = "http://localhost:25102/IS-lab1JEE-1.0-SNAPSHOT/api";
const toast = ref("");

const colors = ["GREEN", "BLACK", "YELLOW", "BROWN"];
const countries = ["RUSSIA", "UNITED_KINGDOM", "VATICAN", "ITALY"];

const sortDir = ref<"asc" | "desc">("asc");
const sortBy = ref<keyof PersonDTO | "locationId">("id");

const showModal = ref(false);
const formMode = ref<"create" | "edit">("create");
const editId = ref<number | null>(null);

const form = reactive({
    name: null as string | null,
    eyeColor: null as string | null,
    hairColor: null as string | null,
    locationId: null as number | null,
    weight: null as number | null,
    passportID: null as number | null,
    nationality: null as string | null,
});

const page = ref(0);
const pageSize = 5;

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

function showToast(message: string) {
    toast.value = message;
    setTimeout(() => {
        toast.value = "";
    }, 5000);
}

function toggleSort(field: keyof PersonDTO | "locationId") {
    if (sortBy.value === field) sortDir.value = sortDir.value === "asc" ? "desc" : "asc";
    else {
        sortBy.value = field;
        sortDir.value = "asc";
    }
}

function openForm(mode: "create" | "edit", person?: PersonDTO) {
    formMode.value = mode;
    if (mode === "edit" && person) {
        editId.value = person.id;
        Object.assign(form, person);
    } else {
        editId.value = null;
        Object.assign(form, {
            name: null,
            eyeColor: null,
            hairColor: null,
            locationId: null,
            weight: null,
            passportID: null,
            nationality: null,
        });
    }
    showModal.value = true;
}

async function confirmDelete(person: PersonDTO) {
    try {
        const res = await fetch(`${baseUrl}/person/${person.id}`, {
            method: "DELETE",
            headers: {"Content-Type": "application/json"}
        });
        if (!res.ok) throw new Error(res.statusText);
        await fetchPersons();
    } catch {
        showToast("Ошибка удаления");
    }
}

async function fetchPersons() {
    try {
        const res = await fetch(`${baseUrl}/person/table?page=${page.value}&size=${pageSize}`);
        if (!res.ok) throw new Error(res.statusText);
        personsList.value = await res.json();
    } catch {
        showToast("Ошибка загрузки персоналий");
    }
}

async function fetchLocations() {
    try {
        const res = await fetch(`${baseUrl}/location/table`);
        if (!res.ok) throw new Error(res.statusText);
        locationsList.value = await res.json();
    } catch {
        showToast("Ошибка загрузки локаций");
    }
}

async function savePerson() {
    const payload = {...form};
    try {
        let res;
        if (formMode.value === "create") {
            res = await fetch(`${baseUrl}/person`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(payload)
            });
        } else if (formMode.value === "edit" && editId.value !== null) {
            res = await fetch(`${baseUrl}/person/${editId.value}`, {
                method: "PATCH",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(payload)
            });
        }
        if (!res || !res.ok) throw new Error(res?.statusText);
        await fetchPersons();
        showModal.value = false;
        showToast("Сохранено!");
    } catch {
        showToast("Ошибка сохранения");
    }
}

function nextPage() {
    page.value++;
    fetchPersons();
}

function prevPage() {
    if (page.value > 0) {
        page.value--;
        fetchPersons();
    }
}

function handleKeydown(event: KeyboardEvent) {
    if (event.key === "Escape") {
        showModal.value = false;
    }
}

let refreshInterval: number | undefined;

onUnmounted(() => {
    clearInterval(refreshInterval);
});

onMounted(() => {
    window.addEventListener("keydown", handleKeydown);
    fetchLocations();
    fetchPersons();
    //refreshInterval = window.setInterval(() => {fetchLocations();fetchPersons();}, 5000);
});


function refresh() {
    fetchLocations();
    fetchPersons();
}

defineExpose({refresh});
</script>

<template>
    <div class="table-wrapper">
        Person
        <div v-if="toast">{{ toast }}</div>

        <button class="add-btn" @click="openForm('create')">Добавить</button>

        <table>
            <thead>
            <tr>
                <th @click="toggleSort('id')">ID</th>
                <th @click="toggleSort('name')">Имя</th>
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
                <td>{{ p.location?.name }}</td>
                <td>{{ p.weight }}</td>
                <td>{{ p.passportID }}</td>
                <td>{{ p.nationality }}</td>
                <td>
                    <button @click="openForm('edit', p)">Редактировать</button>
                    <button @click="confirmDelete(p)">Удалить</button>
                </td>
            </tr>
            <tr v-if="pagedPersons.length === 0">
                <td colspan="9">Нет данных</td>
            </tr>
            </tbody>
        </table>

        <div class="pagination">
            <button @click="prevPage" :disabled="page===0">Назад</button>
            <span>Стр: {{ page + 1 }}</span>
            <button @click="nextPage" :disabled="pagedPersons.length < pageSize">Вперёд</button>
        </div>
    </div>

    <div v-if="showModal" class="overlay" @click.self="showModal = false">
        <div class="modal">
            <h3>{{ formMode === 'create' ? 'Создать' : 'Редактировать' }} Person</h3>
            <form @submit.prevent="savePerson">
                <input type="text" v-model="form.name" placeholder="Name" required/>
                <select v-model="form.eyeColor">
                    <option value="">-- select eye color --</option>
                    <option v-for="c in colors" :key="c" :value="c">{{ c }}</option>
                </select>
                <select v-model="form.hairColor">
                    <option value="">-- select hair color --</option>
                    <option v-for="c in colors" :key="c" :value="c">{{ c }}</option>
                </select>
                <select v-model="form.locationId" required>
                    <option value="">-- select location --</option>
                    <option v-for="loc in locationsList" :key="loc.id" :value="loc.id">{{ loc.name }}</option>
                </select>
                <input type="number" step="0.01" v-model.number="form.weight" placeholder="Weight" required/>
                <input type="text" v-model="form.passportID" placeholder="Passport ID" required/>
                <select v-model="form.nationality">
                    <option value="">-- select nationality --</option>
                    <option v-for="c in countries" :key="c" :value="c">{{ c }}</option>
                </select>
                <div class="modal-actions">
                    <button type="submit">Сохранить</button>
                    <button type="button" @click="showModal=false" class="delete-btn">Закрыть</button>
                </div>
            </form>
        </div>
    </div>
</template>


<style scoped>
.overlay {
    color: #cccccc;
}
</style>
