<template>
  <form v-on:submit.prevent>
    <autocomplete :input-attrs="{ placeholder: 'Zoomer sur la commune...' }" v-model="selected"
      :items="results" :get-label="getLabel" :component-item='communeTemplate'
      :auto-select-one-item="true" @update-items="search" @item-selected="communeSelected" />
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
      this.$parent.zoomToGeom(commune.geometrie)
    },
    communeClicked(commune) {
      console.log(commune)
    }
  }
}
</script>

<style>
</style>
