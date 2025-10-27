<script setup lang="ts">
import { defineEmits } from 'vue'
import logoVT from '../assets/logo.png'

const emit = defineEmits(['toggleImport'])
</script>

<template>
    <header>
        <div class="header-content">
            <img id="pic" :src="logoVT" alt="pic" />
            <div class="header-text">
                <h1>Лабораторная работа по ИС №2</h1>
                <h2>ААА</h2>
                <h2>Группа P3306, Вариант №33</h2>
            </div>

            <button class="import-btn" @click="emit('toggleImport')">
                Импорт / История
            </button>
        </div>
    </header>
</template>

<style scoped>
header {
    width: 100%;
    left: 0;
    z-index: 10;
    top: 0;
    position: relative;
    background-color: #101F27;
    font-family: monospace;
    color: #BBBBBB;
    margin: 0;
    font-size: 80%;
}

.header-content {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.header-text {
    flex: 1;
    text-align: center;
    padding-right: 2%;
}

img {
    width: 13%;
    margin-top: -6%;
    margin-left: 1%;
}

.import-btn {
    margin-right: 2%;
    background-color: #1E3A8A;
    color: white;
    border: none;
    padding: 10px 16px;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: 0.2s;
}

.import-btn:hover {
    background-color: #2563EB;
}

@media (max-width: 813px) {
    .import-btn {
        padding: 6px 10px;
        font-size: 12px;
    }
}
</style>
