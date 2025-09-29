<script setup lang="ts">
import "../../css/entity.css"
import {reactive, ref, computed} from "vue";
import type {CoordinatesDTO} from "@/ts/dto/CoordinatesDTO.ts";
import Movie from "@/components/entity/Movie.vue";

const movieRef = ref<InstanceType<typeof Movie> | null>(null);

const baseUrl = 'http://localhost:8080/IS-lab1JEE-1.0-SNAPSHOT/api';
const toast = ref("");

const sortDir = ref<"asc" | "desc">("asc");
const sortBy = ref<"id" | "x" | "y">("id");

const showForm = ref(false);
const formMode = ref<"create" | "edit">("create");
const editId = ref<number | null>(null);

const form = reactive({
    coordinatesX: null,
    coordinatesY: null,
});
const coordinatesList = ref<{ id: number; x: number; y: number }[]>([]);

const page = ref(0);
const pageSize = 5;

const pagedCoordinates = computed(() => {
    return [...coordinatesList.value].sort((a, b) => {
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


function toggleSort(field: "id" | "x" | "y") {
    if (sortBy.value === field) {
        sortDir.value = sortDir.value === "asc" ? "desc" : "asc";
    } else {
        sortBy.value = field;
        sortDir.value = "asc";
    }
}

function openEdit(coordinates: CoordinatesDTO) {
    formMode.value = "edit";
    editId.value = coordinates.id;
    form.coordinatesX = coordinates.x;
    form.coordinatesY = coordinates.y;
    showForm.value = true;
}

const emit = defineEmits(['updated']);


async function confirmDelete(coordinates: CoordinatesDTO) {
    try {
        const res = await fetch(`${baseUrl}/coordinates/${coordinates.id}`, {
            method: "DELETE",
            headers: {"Content-Type": "application/json"}
        });
        if (!res.ok) throw new Error(res.statusText);
        await fetchCoordinates();
        emit('updated');

    } catch (e) {
        showToast("Ошибка удаления");
    }
}

async function fetchCoordinates() {
    try {
        const res = await fetch(
            `${baseUrl}/coordinates/table?page=${page.value}&size=${pageSize}`,
            {method: "GET", headers: {"Content-Type": "application/json"}}
        );
        if (!res.ok) throw new Error(res.statusText);
        coordinatesList.value = await res.json();
    } catch (e) {
        showToast("Ошибка загрузки координат");
    }
}

async function saveCoordinates() {
    const coordinatesDTO = {
        x: form.coordinatesX,
        y: form.coordinatesY,
    };
    try {
        let res;
        if (formMode.value === "create") {
            res = await fetch(`${baseUrl}/coordinates`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(coordinatesDTO),
            });
        } else if (formMode.value === "edit" && editId.value !== null) {
            res = await fetch(`${baseUrl}/coordinates/${editId.value}`, {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(coordinatesDTO),
            });
            emit('updated');
        }
        if (!res || !res.ok) throw new Error(res?.statusText);
        movieRef.value?.refresh();
        await fetchCoordinates();
        showForm.value = false;
        showToast("Сохранено!");
    } catch (e) {
        showToast("Ошибка сохранения" + e);
    }
}


function nextPage() {
    page.value++;
    fetchCoordinates();
}

function prevPage() {
    if (page.value > 0) {
        page.value--;
        fetchCoordinates();
    }
}
function refresh() {
    fetchCoordinates();
}

defineExpose({ refresh });

fetchCoordinates();

</script>

<template>
    <div class="table-wrapper">
        <div v-if="toast" class="toast">{{ toast }}</div>

        <div v-if="showForm">
            <form @submit.prevent="saveCoordinates">
                X:
                <input type="number" v-model.number="form.coordinatesX" required/>
                Y:
                <input type="number" v-model.number="form.coordinatesY" required/>
                <button type="submit">Сохранить</button>
                <button type="button" @click="showForm = false" class="delete-btn">Закрыть</button>
            </form>
        </div>
        <button v-else @click="showForm = true; formMode = 'create'">Добавить</button>

        <table>
            <thead>
            <tr>
                <th @click="toggleSort('id')">
                    ID
                    <span v-if="sortBy==='id'">
          {{ sortDir === 'asc' ? '▲' : '▼' }}
        </span>
                </th>
                <th @click="toggleSort('x')">
                    X
                    <span v-if="sortBy==='x'">
          {{ sortDir === 'asc' ? '▲' : '▼' }}
        </span>
                </th>
                <th @click="toggleSort('y')">
                    Y
                    <span v-if="sortBy==='y'">
          {{ sortDir === 'asc' ? '▲' : '▼' }}
        </span>
                </th>
                <th>Действия</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="coordinates in pagedCoordinates" :key="coordinates.id">
                <td>{{ coordinates.id }}</td>
                <td>{{ coordinates.x }}</td>
                <td>{{ coordinates.y }}</td>
                <td>
                    <button @click="openEdit(coordinates)">Редактировать</button>
                    <button @click="confirmDelete(coordinates)" class="delete-btn">
                        Удалить
                    </button>
                </td>
            </tr>
            <tr v-if="pagedCoordinates.length === 0">
                <td colspan="4" class="no-data">Нет данных</td>
            </tr>
            </tbody>
        </table>

        <div class="pagination">
            <button @click="prevPage" :disabled="page===0">Назад</button>
            <span>Стр: {{ page + 1 }}</span>
            <button @click="nextPage" :disabled="pagedCoordinates.length < pageSize">Вперёд</button>
        </div>
    </div>

</template>

<style scoped>

</style>
