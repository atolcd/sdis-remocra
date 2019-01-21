<template>
  <form v-on:submit.prevent>
    <autocomplete :input-attrs="{placeholder: 'Sélectionner une valeur..' }" v-model="selected"
      :items="results" :get-label="getLabel" :component-item='complementTemplate' :min-len="2"
      :auto-select-one-item="true" @update-items="search" @item-selected="paramSelected"
      @item-clicked="paramClicked"/>
  </form>
</template>

<script>
/* eslint-disable */

// https://github.com/paliari/v-autocomplete
import Autocomplete from 'v-autocomplete'
import axios from 'axios'
import SearchComplementTemplate from './SearchComplementTemplate.vue'

export default {
  name: 'SearchComplement',
  components: {
    Autocomplete
  },

  data() {
    return {
      //v-autocomplete ne peut pas setter la valeur par defaut  (cas du chargement de la valeur pour le complement)
      selected: '  ',
      results: [],
      complementTemplate: SearchComplementTemplate,
    }
  },

  props:{
    paramId:{
      required:true,
      type: Number
    },
    searchInput:{
      required: false,
      type: String
    }
  },
  mounted(){
    this.search(this.searchInput)
  },
  methods: {
    getLabel(item) {
      // valeur affiché peut être un libelle ou un nom ou autre
      return item ? Object.values(item)[1]: ''
    },
    search(text) {
      axios.get('/remocra/evenements/evenementmodparalst/'+this.paramId+'?query=' + text + '&page=1&start=0&limit=10')
        .then((response) => {
          this.results = response.data.data
          //Si on a une valeur par defaut on fait une recherche sur le searchText et on selectionne automatiquement
        })
        .catch(function(error) {
          console.error('complement', error)
        })
    },
    paramSelected(complement) {
      this.selected = complement
    },
    paramClicked(complement) {
      this.selected = complement
    }
  }
}
</script>

<style>
</style>
