<script setup lang="ts">
import Header from "@/components/Header.vue";
import Body from "@/components/Body.vue";
import Footer from "@/components/Footer.vue";
import "@/css/page.css"
import Window from "@/components/Window.vue";
import Coordinates from "@/components/entity/Coordinates.vue";
import Location from "@/components/entity/Location.vue";
import Movie from "@/components/entity/Movie.vue";
import Person from "@/components/entity/Person.vue";

</script>

<template>
    <Header/>
    <Coordinates/>
    <Location/>
    <Person/>
    <Movie/>
    <Footer/>
</template>

<style scoped>

</style>
