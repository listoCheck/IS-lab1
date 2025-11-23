<script setup lang="ts">
import { reactive, ref, computed, onMounted, onUnmounted } from "vue";
import type { LocationDTO } from "../../ts/dto/LocationDTO.ts";
const baseUrl = "http://localhost:8080/IS-lab1JEE-1.0-SNAPSHOT/api";
//const baseUrl = "http://localhost:25102/IS-lab1JEE-1.0-SNAPSHOT/api";
const toast = ref("");

const sortDir = ref<"asc" | "desc">("asc");
const sortBy = ref<"id" | "x" | "y" | "z" | "name">("id");

const showForm = ref(false);
const formMode = ref<"create" | "edit">("create");
const editId = ref<number | null>(null);

const form = reactive({
    locationX: null as number | null,
    locationY: null as number | null,
    locationZ: null as number | null,
    locationName: null as string | null,
});
const emit = defineEmits(['updated']);
const locationsList = ref<LocationDTO[]>([]);

const page = ref(0);
const pageSize = 5;

function showToast(message: string) {
    toast.value = message;
    setTimeout(() => toast.value = "", 5000);
}

const pagedLocations = computed(() => {
    return [...locationsList.value].sort((a, b) => {
        const field = sortBy.value as keyof typeof a;
        if (a[field] < b[field]) return sortDir.value === "asc" ? -1 : 1;
        if (a[field] > b[field]) return sortDir.value === "asc" ? 1 : -1;
        return 0;
    });
});

function toggleSort(field: "id" | "x" | "y" | "z" | "name") {
    if (sortBy.value === field) {
        sortDir.value = sortDir.value === "asc" ? "desc" : "asc";
    } else {
        sortBy.value = field;
        sortDir.value = "asc";
    }
}

function openEdit(location: LocationDTO) {
    formMode.value = "edit";
    editId.value = location.id;
    form.locationX = location.x;
    form.locationY = location.y;
    form.locationZ = location.z;
    form.locationName = location.name;
    showForm.value = true;
}

async function confirmDelete(location: LocationDTO) {
    try {
        const res = await fetch(`${baseUrl}/location/${location.id}`, {
            method: "DELETE",
            headers: {"Content-Type": "application/json"}
        });
        if (!res.ok) throw new Error(res.statusText);
        await fetchLocations();
        emit('updated');
    } catch {
        showToast("Ошибка удаления");
    }
}

async function fetchLocations() {
    try {
        const res = await fetch(`${baseUrl}/location/table?page=${page.value}&size=${pageSize}`, {
            method: "GET",
            headers: {"Content-Type": "application/json"}
        });
        if (!res.ok) throw new Error(res.statusText);
        locationsList.value = await res.json();
    } catch {
        showToast("Ошибка загрузки локаций");
    }
}

async function saveLocations() {
    const locationDTO = {
        x: form.locationX,
        y: form.locationY,
        z: form.locationZ,
        name: form.locationName,
    };
    try {
        let res;
        if (formMode.value === "create") {
            res = await fetch(`${baseUrl}/location`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(locationDTO),
            });
        } else if (formMode.value === "edit" && editId.value !== null) {
            res = await fetch(`${baseUrl}/location/${editId.value}`, {
                method: "PATCH",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(locationDTO),
            });
            emit('updated');
        }
        if (!res || !res.ok) throw new Error(res?.statusText);
        await fetchLocations();
        showForm.value = false;
        showToast("Сохранено!");
    } catch {
        showToast("Ошибка сохранения");
    }
}

function nextPage() { page.value++; fetchLocations(); }
function prevPage() { if(page.value>0){page.value--; fetchLocations();} }
function refresh() { fetchLocations(); }

defineExpose({ refresh });

let refreshInterval: number | undefined;



onMounted(() => {
    //window.addEventListener("keydown", handleKeydown);
    fetchLocations();
    //refreshInterval = window.setInterval(() => {fetchLocations();}, 5000);
});

onUnmounted(() => {
    //window.removeEventListener("keydown", handleKeydown);
    clearInterval(refreshInterval);
});
</script>

<template>
    <div class="table-wrapper">Location
        <div v-if="toast" class="toast">{{ toast }}</div>

        <button class="add-btn" @click="showForm=true; formMode='create'">Добавить</button>

        <table>
            <thead>
            <tr>
                <th @click="toggleSort('id')">ID <small v-if="sortBy==='id'">{{ sortDir==='asc'?'▲':'▼' }}</small></th>
                <th @click="toggleSort('x')">X <small v-if="sortBy==='x'">{{ sortDir==='asc'?'▲':'▼' }}</small></th>
                <th @click="toggleSort('y')">Y <small v-if="sortBy==='y'">{{ sortDir==='asc'?'▲':'▼' }}</small></th>
                <th @click="toggleSort('z')">Z <small v-if="sortBy==='z'">{{ sortDir==='asc'?'▲':'▼' }}</small></th>
                <th @click="toggleSort('name')">Name <small v-if="sortBy==='name'">{{ sortDir==='asc'?'▲':'▼' }}</small></th>
                <th>Действия</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="location in pagedLocations" :key="location.id">
                <td>{{ location.id }}</td>
                <td>{{ location.x }}</td>
                <td>{{ location.y }}</td>
                <td>{{ location.z }}</td>
                <td>{{ location.name }}</td>
                <td>
                    <button @click="openEdit(location)">Редактировать</button>
                    <button @click="confirmDelete(location)" class="delete-btn">Удалить</button>
                </td>
            </tr>
            <tr v-if="pagedLocations.length===0">
                <td colspan="6" class="no-data">Нет данных</td>
            </tr>
            </tbody>
        </table>

        <div class="pagination">
            <button @click="prevPage" :disabled="page===0">Назад</button>
            <span>Стр: {{ page + 1 }}</span>
            <button @click="nextPage" :disabled="pagedLocations.length < pageSize">Вперёд</button>
        </div>

        <transition name="fade">
            <div v-if="showForm" class="overlay" @click.self="showForm=false">
                <div class="modal">
                    <h3>{{ formMode==='create'?'Создать локацию':'Редактировать локацию' }}</h3>
                    <form @submit.prevent="saveLocations">
                        <label>X: <input type="number" v-model.number="form.locationX" required/></label>
                        <label>Y: <input type="number" v-model.number="form.locationY" required/></label>
                        <label>Z: <input type="number" v-model.number="form.locationZ" required/></label>
                        <label>Name: <input type="text" v-model="form.locationName" required/></label>
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
