<!--
  Composant générique pour la création de champs d'autocomplétion
-->
<template>
<form v-on:submit.prevent>
  <autocomplete ref="autocomplete"
                :input-attrs="{ placeholder: 'Sélectionnez une valeur...' }"
                v-model="selected"
                :items="items"
                :get-label="getLabel"
                :component-item='template'
                :auto-select-one-item="false"
                @update-items="search"
                @item-selected="onItemSelected" />
</form>
</template>

<script>
// https://github.com/paliari/v-autocomplete
import Autocomplete from 'v-autocomplete'
import _ from 'lodash'
import AutoCompleteTemplate from './AutoCompleteTemplate.vue'
export default {
  name: 'AutoCompleteComponent',
  components: {
    Autocomplete
  },
  props: {
    options: {
      required: true
    },

    maxDisplayedItem : {
      required: false,
      type: Number,
      default: 10
    }
  },
  data() {
    return {
      items: null,
      selected: null,
      results: [],
      template: AutoCompleteTemplate
    }
  },

  watch: {
    options: function() {
      this.items = _.slice(this.options, 0, this.maxDisplayedItem);
    }
  },

  methods: {
    getLabel(item) {
      return item ? item.libelle : ''
    },

    search(text) {
      this.items = _.slice(this.options.filter(val => {
        return val.libelle.toLowerCase().includes(text.toLowerCase());
      }), 0, this.maxDisplayedItem);
    },

    onItemSelected(selected) {
      this.selected = selected;
    },

    getSelected() {
      return this.selected;
    },

    checkValidity() {
    }
  }
}
</script>

<style>
.v-autocomplete .v-autocomplete-list {
  text-align: left;
  border: 1px solid #eee;
  overflow-y: visible;
  z-index: 1500;
  display: block;
  position:fixed;
}

.v-autocomplete .v-autocomplete-list .v-autocomplete-list-item {
  cursor: pointer;
  background-color: #fff;
  padding: 5px;
  border-bottom: 1px solid #eee;
}

.v-autocomplete .v-autocomplete-list .v-autocomplete-list-item:last-child {
  border: none;
}

.v-autocomplete .v-autocomplete-list .v-autocomplete-list-item:hover {
  background-color: #eee;
}
</style>
