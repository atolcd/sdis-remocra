<template>
  <form v-on:submit.prevent>
    <autocomplete ref="autocomplete" :input-attrs="{ placeholder: 'Zoomer sur la commune...' }" v-model="selected"
      :items="results" :get-label="getLabel" :component-item='communeTemplate'
      :auto-select-one-item="false" @update-items="search" @item-selected="communeSelected" />
  </form>
</template>

<script>
/* eslint-disable */

// https://github.com/paliari/v-autocomplete
import Autocomplete from 'v-autocomplete'
import axios from 'axios'
import CommuneTemplate from './SearchCommuneTemplate.vue'
import * as eventTypes from '../bus/event-types.js'

export default {
  name: 'SearchCommune',
  components: {
    Autocomplete
  },
  props:{
    crise:{
      required:true,
      type: Number
    }
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
      this.$root.$options.bus.$emit(eventTypes.ZOOM_TOGEOM, {'geom': commune.geometrie, 'crise': this.crise})
      this.$nextTick(() => {
       this.selected = null
      })
    },
    communeClicked(commune) {
      console.log(commune)
    }
  }
}
</script>

<style>
</style>
