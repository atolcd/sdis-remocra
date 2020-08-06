<template>
<div>
  <iframe :src="src" id="redash" height="900px" width="100%" frameborder="0" ></iframe>
</div>
</template>

<script>
export default {
  name: 'Dashboard',
  components: {},
  data() {
    return {
      src:"/remocra/proxy/redash"
    }
  },
  methods: {
  }
};
</script>

<style>

</style>
