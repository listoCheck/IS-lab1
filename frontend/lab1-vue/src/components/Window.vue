<script setup lang="ts">

async function submitForm() {
    try {
        const form = {
            x: 11,
            y: 12
        }
        const method = 'POST'
        const url = "http://localhost:8080/IS-lab1JEE-1.0-SNAPSHOT/api/coordinates"

        const res = await fetch(url, {
            method,
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(form)
        })
        if (!res.ok) throw new Error(res)
        const data = await res.json();
        console.log(data);
        console.log(data.id);
    } catch (e) {
        console.error(e)
    }
}


</script>

<template>
    <button type="button" @click="submitForm">Отправить</button>


</template>

<style scoped>

</style>