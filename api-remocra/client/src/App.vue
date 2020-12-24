<template>
  <div id="app">
  <OpenApi/>
  </div>
</template>

<script>
import OpenApi from '@/components/OpenApi';
export default {
  name: 'App',
  components: {
    OpenApi
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
