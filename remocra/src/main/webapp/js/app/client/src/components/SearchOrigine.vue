<template>
<form v-on:submit.prevent>
  <autocomplete :input-attrs="{ placeholder: 'Origine...' }" v-model="origine" :items="results" :get-label="getLabel" :component-item='origineTemplate' :min-len="2" :auto-select-one-item="true" @update-items="search" @item-selected="origineSelected"
  @item-clicked="origineClicked" />
</form>
</template>

<script>
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
      origine: '  ',
      results: [],
      origineTemplate: OrigineTemplate
    }
  },
  props: {
    crise: {
      required: true,
      type: Number
    }
  },
  methods: {
    getLabel(item) {
      return item !== null ? item : ''
    },
    search(text) {
      axios.get('/remocra/evenements/origines/' + this.crise + '?&query=' + text + '&page=1&start=0&limit=10').then((response) => {
        this.results = response.data.data
      }).catch(function(error) {
        console.error('origines', error)
      })
    },
    origineSelected(origine) {
      // On agit directement
      this.origine = origine
    },
    origineClicked(origine) {
      this.origine = origine
    }
  }
}
</script>

<style>
</style>
