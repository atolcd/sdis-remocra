<template>
  <form v-on:submit.prevent>
    <autocomplete :input-attrs="{ placeholder: 'Origine' }" v-model="selected" :items="results" :get-label="getLabel" :component-item='origineTemplate' :auto-select-one-item="false" @update-items="search" @item-selected="origineSelected"
      @item-clicked="origineClicked" search-input.sync="searchInput" />
  </form>
</template>

<script>
/* eslint-disable */

// https://github.com/paliari/v-autocomplete
import Autocomplete from 'v-autocomplete'
import axios from 'axios'
import OrigineTemplate from './SearchOrigineTemplate.vue'

export default {
  name: 'SearchOrigine',
  components: {
    Autocomplete
  },

  data() {
    return {
      selected: null,
      results: [],
      origineTemplate: OrigineTemplate,
      searchInput: ""
    }
  },

  props:{
    crise:{
      required:false,
      type: String
    }
  },

  methods: {
    getLabel(item) {
      return item ? item: ''
    },
    search(text) {
      axios.get('/remocra/evenements/origines/'+this.crise+'?&query=' +text + '&page=1&start=0&limit=10')
        .then((response) => {
          this.results = response.data.data
          this.searchInput = text
        })
        .catch(function(error) {
          console.error('origines', error)
        })
    },
    origineSelected(origine) {
      // On agit directement
      this.selected = origine
    },
    origineClicked(origine) {
      console.debug('origineClicked', origine, this.selected)
    }
  }
}
</script>

<style>
</style>
