<template>
  <form v-on:submit.prevent>
    <autocomplete :input-attrs="{ placeholder: 'Répertoire des lieux' }" v-model="selected" :items="results" :get-label="getLabel" :component-item='repertoireTemplate' :auto-select-one-item="true" @update-items="search" @item-selected="repertoireSelected"
      @item-clicked="repertoireClicked" />
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
      axios.get('/remocra/repertoirelieu')
        .then((response) => {
          this.results = response.data.data
        })
        .catch(function(error) {
          console.error('repertoires', error)
        })
    },
    repertoireSelected(repertoire) {
      // On agit directement
      this.selected = repertoire
    },
    repertoireClicked(repertoire) {
      console.debug('repertoireClicked', repertoire, this.selected)
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
.v-autocomplete-list {
  width: 100%;
  z-index: 100;
}

.v-autocomplete-list-item {
  cursor: pointer;
  background-color: #fff;
  color: #000;
  padding: 10px;
  border-bottom: 1px solid #a8a8a8;
}

</style>
