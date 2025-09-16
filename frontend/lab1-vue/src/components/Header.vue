<script setup lang="ts">
import logoVT from '@/assets/logo.png'
</script>

<template>
    <header>
        <div class="header-content">
            <img id="pic" :src="logoVT" alt="pic" />
            <div class="header-text">
                <h1>Лабораторная работа по ИС №1</h1>
                <h2>ААА</h2>
                <h2>Группа P3306, Вариант №145</h2>
            </div>
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
    padding-right: 13%;
}

img {
    width: 13%;
    margin-top: -6%;
    margin-left: 1%;
}

@media (min-width: 1200px) {
    body {
        font-size: 14px;
    }

    h2 {
        font-size: 20px;
    }

    h1 {
        font-size: 26px;
    }
}

@media (max-width: 1063px) and (min-width: 813px) {
    body {
        font-size: 18px;
    }

    h2 {
        font-size: 14px;
    }

    h1 {
        font-size: 18px;
    }
}

@media (max-width: 813px) {
    body {
        font-size: 9px;
    }

    h2 {
        font-size: 9px;
    }

    h1 {
        font-size: 9px;
    }
}
</style>
