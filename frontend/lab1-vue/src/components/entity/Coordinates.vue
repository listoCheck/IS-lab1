<script setup lang="ts">
import "../../css/entity.css";
import {reactive, ref, computed, onMounted, onUnmounted} from "vue";
import type {CoordinatesDTO} from "../../ts/dto/CoordinatesDTO.ts";
import Movie from "./Movie.vue";

const movieRef = ref<InstanceType<typeof Movie> | null>(null);
//const baseUrl = "http://localhost:8080/IS-lab1JEE-1.0-SNAPSHOT/api";
const baseUrl = "http://localhost:25102/IS-lab1JEE-1.0-SNAPSHOT/api";
const toast = ref("");

const sortDir = ref<"asc" | "desc">("asc");
const sortBy = ref<"id" | "x" | "y">("id");

const showForm = ref(false);
const formMode = ref<"create" | "edit">("create");
const editId = ref<number | null>(null);
const form = reactive({coordinatesX: null as number | null, coordinatesY: null as number | null});

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

const emit = defineEmits(['updated']);

function showToast(message: string) {
    toast.value = message;
    setTimeout(() => toast.value = "", 5000);
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
        const res = await fetch(`${baseUrl}/coordinates/table?page=${page.value}&size=${pageSize}`);
        if (!res.ok) throw new Error(res.statusText);
        coordinatesList.value = await res.json();
    } catch (e) {
        showToast("Ошибка загрузки координат");
    }
}

async function saveCoordinates() {
    const dto = {x: form.coordinatesX, y: form.coordinatesY};
    try {
        let res;
        if (formMode.value === "create") {
            res = await fetch(`${baseUrl}/coordinates`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(dto),
            });
        } else if (formMode.value === "edit" && editId.value !== null) {
            res = await fetch(`${baseUrl}/coordinates/${editId.value}`, {
                method: "PATCH",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(dto),
            });
            emit('updated');
        }
        if (!res || !res.ok) throw new Error(res?.statusText);
        movieRef.value?.refresh();
        await fetchCoordinates();
        showForm.value = false;
        showToast("Сохранено!");
    } catch (e) {
        showToast("Ошибка сохранения: " + e);
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

defineExpose({refresh});

onMounted(() => {
    //window.addEventListener("keydown", handleKeydown);
    fetchCoordinates();
    refreshInterval = window.setInterval(() => {fetchCoordinates();}, 5000);
});



</script>

<template>
    <div class="table-wrapper">Coordinates
        <div v-if="toast" class="toast">{{ toast }}</div>

        <button @click="showForm=true; formMode='create'" class="add-btn">Добавить</button>

        <table>
            <thead>
            <tr>
                <th @click="toggleSort('id')">ID <span v-if="sortBy==='id'">{{ sortDir === 'asc' ? '▲' : '▼' }}</span>
                </th>
                <th @click="toggleSort('x')">X <span v-if="sortBy==='x'">{{ sortDir === 'asc' ? '▲' : '▼' }}</span></th>
                <th @click="toggleSort('y')">Y <span v-if="sortBy==='y'">{{ sortDir === 'asc' ? '▲' : '▼' }}</span></th>
                <th>Действия</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="c in pagedCoordinates" :key="c.id">
                <td>{{ c.id }}</td>
                <td>{{ c.x }}</td>
                <td>{{ c.y }}</td>
                <td>
                    <button @click="openEdit(c)">Редактировать</button>
                    <button @click="confirmDelete(c)" class="delete-btn">Удалить</button>
                </td>
            </tr>
            <tr v-if="pagedCoordinates.length===0">
                <td colspan="4" class="no-data">Нет данных</td>
            </tr>
            </tbody>
        </table>

        <div class="pagination">
            <button @click="prevPage" :disabled="page===0">Назад</button>
            <span>Стр: {{ page + 1 }}</span>
            <button @click="nextPage" :disabled="pagedCoordinates.length<pageSize">Вперёд</button>
        </div>

        <transition name="fade">
            <div v-if="showForm" class="overlay" @click.self="showForm=false">
                <div class="modal">
                    <h3>{{ formMode === 'create' ? 'Создать координаты' : 'Редактировать координаты' }}</h3>
                    <form @submit.prevent="saveCoordinates">
                        <label>X: <input type="number" v-model.number="form.coordinatesX" required/></label>
                        <label>Y: <input type="number" v-model.number="form.coordinatesY" required/></label>
                        <div class="modal-actions">
                            <button type="submit">Сохранить</button>
                            <button type="button" @click="showForm=false" class="delete-btn">Закрыть</button>
                        </div>
                    </form>
                </div>
            </div>
        </transition>
    </div>
</template>

<style scoped>

</style>
