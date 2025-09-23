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
    <Coordinates/>
    <Location/>
    <Person/>

    <Footer/>
</template>

<style scoped>

</style>
