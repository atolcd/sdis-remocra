<template>
  <div id="app">
        <router-view/>
        <notifications group="remocra" position="top right" animation-type="velocity" :duration="3000"  />
  </div>
</template>

<script>
export default {
  name: 'app',
  data () {
    return {
    }
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
.notification {
  padding: 30px;
  margin: 0 5px 5px;
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  background: #44A4FC;
  border-left: 5px solid #187FE7;
}

.notification.warn {
  background: #ffb648;
  border-left-color: #f48a06;
}

.notification.error {
  background: #E54D42;
  border-left-color: #B82E24;
}

.notification.success {
  background: #68CD86;
  border-left-color: #42A85F;
}
</style>
