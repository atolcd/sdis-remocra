<template>
  <form v-on:submit.prevent>
    <autocomplete :input-attrs="{ placeholder: 'Zoomer sur le lieu...' }" v-model="selected"
      :items="results" :get-label="getLabel" :component-item='repertoireTemplate'
      :auto-select-one-item="true" @update-items="search" @item-selected="repertoireSelected" />
  </form>
</template>

<script>
/* eslint-disable */

// https://github.com/paliari/v-autocomplete
import Autocomplete from 'v-autocomplete'
import axios from 'axios'
import RepertoireTemplate from './SearchRepertoireLieuTemplate.vue'

export default {
  name: 'SearchRepertoireLieu',
  props: {
   crise: {
     required: true,
     type: String
   }
 },
  components: {
    Autocomplete
  },

  data() {
    return {
      selected: null,
      results: [],
      repertoireTemplate: RepertoireTemplate
    }
  },

  methods: {
    getLabel(item) {
      return item ? item.libelle: ''
    },
    search(text) {
      axios.get('/remocra/repertoirelieu/'+this.crise+'?query=' +
          text + '&page=1&start=0&limit=10')
        .then((response) => {
          this.results = response.data.data
        })
        .catch(function(error) {
          console.error('repertoires', error)
        })
    },
    repertoireSelected(repertoire) {
      // On agit directement
      this.$parent.zoomToGeom(repertoire.geometrie)
    }
  }
}
</script>

<style>
</style>
