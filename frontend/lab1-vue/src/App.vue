<script setup lang="ts">
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import "@/css/page.css"
import Location from "@/components/entity/Location.vue";
import Movie from "@/components/entity/Movie.vue";
import Person from "@/components/entity/Person.vue";
import Coordinates from "@/components/entity/Coordinates.vue";

</script>

<template>
    <Header/>

    <div class="content-wrapper">
        <div class="top-row">
            <Coordinates/>
            <Location/>
            <Person/>
        </div>

        <div class="bottom-row">
            <Movie/>
        </div>
    </div>

    <Footer/>
</template>

<style scoped>
.content-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 2rem auto;
}

.top-row {
    display: flex;
    gap: 2rem; /* расстояние между блоками */
    justify-content: center;
    margin-bottom: 2rem;
}

.bottom-row {
    display: flex;
    justify-content: center;
}
</style>
