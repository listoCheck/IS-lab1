<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";

const baseUrl = "http://localhost:8080/IS-lab1JEE-1.0-SNAPSHOT/api";

const toast = ref("");
const showForm = ref(false);
const formMode = ref<"create" | "edit">("create");
const editId = ref<number | null>(null);

const colors = ["GREEN", "BLACK", "YELLOW", "BROWN"];
const countries = ["RUSSIA", "UNITED_KINGDOM", "VATICAN", "ITALY"];

// список локаций
const locations = ref<{ id: number; x: number; y: number; z: number; name?: string }[]>([]);

const form = reactive({
    name: "",
    eyeColor: "",
    hairColor: "",
    locationId: null as number | null,
    weight: 0,
    passportID: "",
    nationality: "",
});

async function fetchLocations() {
    try {
        const res = await fetch(`${baseUrl}/location`);
        if (!res.ok) throw new Error(res.statusText);
        locations.value = await res.json();
    } catch (e) {
        toast.value = "Ошибка загрузки Locations";
    }
}

async function savePerson() {
    const personDTO = {
        name: form.name,
        eyeColor: form.eyeColor || null,
        hairColor: form.hairColor,
        locationId: form.locationId,
        weight: form.weight,
        passportID: form.passportID,
        nationality: form.nationality || null,
    };

    try {
        let res;
        if (formMode.value === "create") {
            res = await fetch(`${baseUrl}/person`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(personDTO),
            });
        } else if (formMode.value === "edit" && editId.value !== null) {
            res = await fetch(`${baseUrl}/person/${editId.value}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(personDTO),
            });
        }
        if (!res || !res.ok) throw new Error(res?.statusText);
        toast.value = "Сохранено!";
        showForm.value = false;
    } catch (e) {
        toast.value = "Ошибка сохранения";
    }
}

// загружаем список локаций при открытии формы
onMounted(() => {
    fetchLocations();
});
</script>

<template>
    <div class="person-form-wrapper">
        <div v-if="toast" class="toast">{{ toast }}</div>

        <div v-if="showForm">
            <form @submit.prevent="savePerson">
                <label>
                    Name:
                    <input type="text" v-model="form.name" required />
                </label>

                <label>
                    Eye Color:
                    <select v-model="form.eyeColor">
                        <option value="">-- none --</option>
                        <option v-for="c in colors" :key="c" :value="c">{{ c }}</option>
                    </select>
                </label>

                <label>
                    Hair Color:
                    <select v-model="form.hairColor" required>
                        <option disabled value="">-- select --</option>
                        <option v-for="c in colors" :key="c" :value="c">{{ c }}</option>
                    </select>
                </label>

                <label>
                    Location:
                    <select v-model.number="form.locationId" required>
                        <option disabled value="">-- select location --</option>
                        <option
                            v-for="loc in locations"
                            :key="loc.id"
                            :value="loc.id"
                        >
                            {{ loc.id }} ({{ loc.x }}, {{ loc.y }}, {{ loc.z }})
                            {{ loc.name ? ' - ' + loc.name : '' }}
                        </option>
                    </select>
                </label>

                <label>
                    Weight:
                    <input type="number" step="0.01" v-model.number="form.weight" required />
                </label>

                <label>
                    Passport ID:
                    <input type="text" v-model="form.passportID" required />
                </label>

                <label>
                    Nationality:
                    <select v-model="form.nationality">
                        <option value="">-- none --</option>
                        <option v-for="c in countries" :key="c" :value="c">{{ c }}</option>
                    </select>
                </label>

                <button type="submit">Сохранить</button>
                <button type="button" @click="showForm=false">Отмена</button>
            </form>
        </div>

        <button v-else @click="showForm = true; formMode = 'create'; fetchLocations()">
            Добавить Person
        </button>
    </div>
</template>

<style scoped>
.person-form-wrapper {
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
