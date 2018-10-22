<template>
  <form v-on:submit.prevent>
    <autocomplete :input-attrs="{ placeholder: 'Commune' }" v-model="selected" :items="results" :get-label="getLabel" :component-item='communeTemplate' :auto-select-one-item="true" @update-items="search" @item-selected="communeSelected"
      @item-clicked="communeClicked" />
  </form>
</template>

<script>
/* eslint-disable */

// https://github.com/paliari/v-autocomplete
import Autocomplete from 'v-autocomplete'
import axios from 'axios'
import CommuneTemplate from './SearchCommuneTemplate.vue'

export default {
  name: 'SearchCommune',
  components: {
    Autocomplete
  },

  data() {
    return {
      selected: null,
      results: [],
      communeTemplate: CommuneTemplate
    }
  },

  methods: {
    getLabel(item) {
      return item ? item.nom: ''
    },
    search(text) {
      axios.get('/remocra/communes/nom.json?_dc=1537947181318&query=' +
          text + '&page=1&start=0&limit=10')
        .then((response) => {
          this.results = response.data.data
        })
        .catch(function(error) {
          console.error('communes', error)
        })
    },
    communeSelected(commune) {
      // On agit directement
      this.selected = commune
    },
    communeClicked(commune) {
      console.debug('communeClicked', commune, this.selected)
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
.v-autocomplete-list {
  width: 100%;
  z-index: 10;
}

.v-autocomplete-list-item {
  cursor: pointer;
  background-color: #fff;
  color: #000;
  padding: 10px;
  border-bottom: 1px solid #a8a8a8;
}

</style>
