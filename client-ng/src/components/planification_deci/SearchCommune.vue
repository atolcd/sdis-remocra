<template>
<form v-on:submit.prevent>
  <autocomplete ref="autocomplete"
                :input-attrs="{ placeholder: 'Sélectionnez une commune...' }"
                v-model="selected"
                :items="results"
                :get-label="getLabel"
                :component-item='communeTemplate'
                :auto-select-one-item="false"
                @update-items="search"
                @item-selected="communeSelected" />
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
      this.$emit('communeInputChange');
      axios.get('/remocra/communes/nom', {
        params: {
          "query": text,
          "page": 1,
          "start": 0,
          "limit": 10
        }
      }).then((response) => {
        this.results = (response.data) ? response.data.data : null;
      }).catch(function(error) {
        console.error('communes', error)
      })
    },

    communeSelected(commune) {
      this.selected = commune;
      this.$emit('communeSelected');
    },

    reset() {
      this.selected = null;
      this.results = [];
    },

    getSelected() {
      return this.selected;
    }
  }
}
</script>

<style>
</style>
