<template>
  <form v-on:submit.prevent>
    <autocomplete :input-attrs="{ placeholder: 'Sélectionner une valeur..'}" v-model="selected"
      :items="results" :get-label="getLabel" :component-item='processParamTemplate' :min-len="2"
      :auto-select-one-item="true" @update-items="search" @item-selected="paramSelected"
      @item-clicked="paramClicked"/>
  </form>
</template>

<script>
/* eslint-disable */

// https://github.com/paliari/v-autocomplete
import Autocomplete from 'v-autocomplete'
import axios from 'axios'
import SearchProcessParamTemplate from './SearchProcessParamTemplate.vue'

export default {
  name: 'SearchProcessParam',
  components: {
    Autocomplete
  },

  data() {
    return {
      selected: '  ',
      results: [],
      processParamTemplate: SearchProcessParamTemplate,
    }
  },

  props:{
    paramId:{
      required:true,
      type: Number
    },
    queryURL:{
      required:false,
      type:String
    },
    searchInput:{
      required: false,
      type: String
    },
    nomParam:{
      required:false,
      type:String
    }
  },
  mounted(){
    this.search(this.searchInput);
  },
  methods: {
    getLabel(item) {
      // valeur affiché peut être un libelle ou un nom ou autre
      return item ? Object.values(item)[1] : ''
    },

    search(text) {
      var url = (this.queryURL) ? this.queryURL : 'remocra/processusetlmodele/processusetlmodparalst/';
      axios.get(url+this.paramId+'?&query=' +text + '&page=1&start=0&limit=10')
        .then((response) => {
          this.results = response.data.data
        })
        .catch(function(error) {
          console.error('origines', error)
        })
    },
    paramSelected(selected) {
      this.selected = selected
    },
    paramClicked(selected) {
      this.selected = selected
    }
  }
}
</script>

<style>
</style>
