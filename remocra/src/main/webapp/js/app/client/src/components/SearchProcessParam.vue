<template>
  <form v-on:submit.prevent>
    <autocomplete :input-attrs="{ placeholder: 'Origine...' }" v-model="selected"
      :items="results" :get-label="getLabel" :component-item='processParamTemplate' :min-len="2"
      :auto-select-one-item="false" @update-items="search" @item-selected="paramSelected"
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
    selected: null,
      results: [],
      processParamTemplate: SearchProcessParamTemplate,
      searchInput: ""
    }
  },

  props:{
    paramId:{
      required:false,
      type: Number
    }
  },

  methods: {
    getLabel(item) {
      // valeur affiché peut être un libelle ou un nom ou autre
      return item ? Object.values(item)[1] : ''
    },
    search(text) {
      console.log(text)
      axios.get('remocra/processusetlmodele/processusetlmodparalst/'+this.paramId+'?&query=' +text + '&page=1&start=0&limit=10')
        .then((response) => {
          this.results = response.data.data
          this.searchInput = text
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
