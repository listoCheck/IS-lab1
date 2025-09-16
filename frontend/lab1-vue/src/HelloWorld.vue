<script setup lang="ts">
import {ref} from "vue";
import Header from "@/components/Header.vue";
import "@/css/page.css"
import Footer from "@/components/Footer.vue";
const message = ref("Hello world");
const clicks = ref(0);
function changeMessage() {
    clicks.value++;
}
</script>

<template>
    <Header/>
    <div class="container">
        <h1 class="title">{{ message }}</h1>
        <p class="subtitle">Ты кликнул {{ clicks }} раз(а)</p>
        <button class="btn" @click="changeMessage">Сменить текст</button>
    </div>
    <Footer/>
</template>

<style scoped>

</style>