<template>
<div>
  <iframe v-if="src && src.length!=0" :src="src" id="redash" height="900px" width="100%" frameborder="0" ></iframe>
</div>
</template>

<script>
import axios from 'axios'
export default {
  name: 'Dashboard',
  components: {},
  data() {
    return {
      src:""
    }
  },
  methods: {
  },
  mounted(){
       axios.get('/remocra/utilisateurs/dashboard').then(response => {
           if(response.data){
             this.src  = "/remocra/redash/public/dashboards/" + response.data + "?org_slug=default"
           }
       }).catch(error => {
           console.error(error)
       })
  }
};
</script>

<style>

</style>
