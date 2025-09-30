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

const coordsRef = ref<InstanceType<typeof Coordinates> | null>(null);
const locationRef = ref<InstanceType<typeof Location> | null>(null);
const personRef = ref<InstanceType<typeof Person> | null>(null);
const movieRef = ref<InstanceType<typeof Movie> | null>(null);

function updateAll() {
    coordsRef.value?.refresh();
    locationRef.value?.refresh();
    personRef.value?.refresh();
    movieRef.value?.refresh();
}
</script>

<template>
    <Header />

    <div class="content-wrapper">
        <div class="top-row">
            <Coordinates ref="coordsRef" @updated="updateAll" />
            <Location ref="locationRef" @updated="updateAll" />
            <Person ref="personRef" @updated="updateAll" />
        </div>

        <div class="bottom-row">
            <Movie ref="movieRef" @updated="updateAll" />
        </div>
    </div>

    <Footer />
</template>
