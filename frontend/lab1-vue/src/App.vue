<script setup lang="ts">
import { ref } from "vue";

import Header from "./components/Header.vue";
import Footer from "./components/Footer.vue";
import "./css/page.css";
import Location from "./components/entity/Location.vue";
import Movie from "./components/entity/Movie.vue";
import Person from "./components/entity/Person.vue";
import Coordinates from "./components/entity/Coordinates.vue";
import "./css/app.css";
import ImportUpload from "@/components/imports/Upload.vue";
import ImportHistory from "@/components/imports/History.vue";

const showImportPanel = ref(false);

const coordsRef = ref<InstanceType<typeof Coordinates> | null>(null);
const locationRef = ref<InstanceType<typeof Location> | null>(null);
const personRef = ref<InstanceType<typeof Person> | null>(null);
const movieRef = ref<InstanceType<typeof Movie> | null>(null);
const historyRef = ref<InstanceType<typeof ImportHistory> | null>(null);

function updateAll() {
    coordsRef.value?.refresh();
    locationRef.value?.refresh();
    personRef.value?.refresh();
    movieRef.value?.refresh();
    historyRef.value?.loadHistory();
}

function toggleImportPanel() {
    showImportPanel.value = !showImportPanel.value;
}
</script>

<template>
    <Header @toggleImport="toggleImportPanel" />

    <div class="content-wrapper" :class="{ blurred: showImportPanel }">
        <div class="top-row">
            <Coordinates ref="coordsRef" @updated="updateAll" />
            <Location ref="locationRef" @updated="updateAll" />
            <Person ref="personRef" @updated="updateAll" />
        </div>

        <div class="bottom-row">
            <Movie ref="movieRef" @updated="updateAll" />
        </div>
    </div>

    <transition name="fade">
        <div v-if="showImportPanel" class="overlay" @click.self="toggleImportPanel">
            <div class="import-modal">
                <div class="import-header">
                    <h2>Импорт и история операций</h2>
                    <button class="close-btn" @click="toggleImportPanel">×</button>
                </div>

                <div class="import-body">
                    <div class="import-left">
                        <ImportUpload @imported="updateAll" />
                    </div>
                    <div class="import-right">
                        <ImportHistory ref="historyRef" />
                    </div>
                </div>

                <div class="import-footer">
                    <button class="exit-btn" @click="toggleImportPanel">
                        Закрыть и вернуться
                    </button>
                </div>
            </div>
        </div>
    </transition>

    <Footer />
</template>

<style scoped>

</style>
