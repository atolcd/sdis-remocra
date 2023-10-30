<template>

  <select size="8" class="w-100" v-model="selectRow" @click="windowsClicked" >
    <option v-for="data in datas" :value="data" :key="data">{{ data.libelle }}</option>
  </select>
</template>
<script>

export default {
  name: 'ListeCaracteristiques',
  mounted() {
  },
  props: {
    datas: {
      type: Array,
      required: true
    },
    id:{
      type: String,
      required: true
    },
    libelle:{
      type: String
    }
  },
  data() {
    return {
      selectRow: null,
    }
  },
  methods: {
    windowsClicked:function(){
      this.$emit("rowChange",this.selectRow,this.id);
    }
  }
};
</script>