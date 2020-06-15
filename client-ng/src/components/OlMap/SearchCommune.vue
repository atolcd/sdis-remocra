<template>
<form v-on:submit.prevent>
  <autocomplete ref="autocomplete" :input-attrs="{ placeholder: 'Sélectionnez une commune...' }" v-model="selected" :items="results" :get-label="getLabel" :component-item='communeTemplate' :auto-select-one-item="false" @update-items="search"
    @item-selected="communeSelected" v-on:change="onChange"/>
</form>
</template>

<script>
// https://github.com/paliari/v-autocomplete
import Autocomplete from 'v-autocomplete'
import axios from 'axios'
import CommuneTemplate from '../SearchCommuneTemplate.vue'
//import * as eventTypes from '../bus/event-types.js'
export default {
  name: 'SearchCommune',
  components: {
    Autocomplete
  },
  props: {

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
      return item ? item.nom : ''
    },
    search(text) {
      axios.get('/remocra/communes/nom.json?_dc=1537947181318&query=' + text + '&page=1&start=0&limit=10').then((response) => {
        this.results = response.data.data
      }).catch(function(error) {
        console.error('communes', error)
      })
    },
    communeSelected(commune) {
      this.selected = commune;
      this.$emit('communeSelected', this.selected);
    },
    reset() {
      this.selected = null;
      this.results = [];
    },

    onChange() {
      this.$emit('communeInputChange');
    },

    getSelected() {
      return this.selected;
    }
  }
}
</script>

<style>
</style>
