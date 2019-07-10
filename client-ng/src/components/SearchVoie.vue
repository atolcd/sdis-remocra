<template>
  <form v-on:submit.prevent>
  <autocomplete :items="itemsFiltered"
                v-model="item"
                :get-label="getLabel"
                :component-item='template'
                @change="onValueChanged"
                @item-selected="onValueChanged"
                @update-items="updateItems"
                :auto-select-one-item="false"
                :min-len="0" >
  </autocomplete>
  </form>
</template>

<script>
import VoieTemplate from './SearchVoieTemplate.vue'
import Autocomplete from 'v-autocomplete'
import axios from 'axios'
import _ from 'lodash'
export default {
  name: 'SearchVoie',
  components: {
    Autocomplete
  },
  data () {
    return {
      item: null,
      items: [],
      itemsFiltered: null,
      template: VoieTemplate,
    }
  },

  props: {

    geometrie: {
      required: true,
      type: String
    },

    commune: {
      required: true
    },

    defaultValue: {
      required: false,
      type: String
    },

    attr: {
      required: true,
      type: String
    }
  },

  mounted: function() {
    axios.get('/remocra/voies/mc.json', {params:{
      withgeom: false,
      page: 1,
      start: 0,
      limit: 10,
      filter: JSON.stringify([{"property":"wkt","value":this.geometrie},{"property":"communeId","value":this.commune}])}}).then(response => {
        _.forEach(response.data.data, voie => {
          this.items.push(voie.nom);
        })

        this.itemsFiltered = _.clone(this.items, true);
    }).then(() => {
      if(this.defaultValue){
        this.item = this.defaultValue;
      }
    }).catch(function(error) {
      console.error('Retrieving coordonnees from /remocra/voies/mc', error);
    });
  },

  methods: {

    getLabel (item) {
      return item
    },

    onValueChanged (text) {
      this.item = text;
      this.$emit('onVoieChange', this.attr, text);
    },

    updateItems(text) {
      if(this.itemsFiltered){
        this.itemsFiltered = this.items.filter(item => (text) ? item.toUpperCase().indexOf(text.toUpperCase()) !== -1 : true);
      }
    }
  }
};
</script>

<style>

.v-autocomplete-list{
  z-index: 99999;
  border: 1px solid #dee2e6;
  border-radius: 5px;
}

.v-autocomplete-list-item {
  padding: 0 5px 0 5px;
  background-color: #f8f9fa;
}

.v-autocomplete-input {
  height: calc(1.5em + .5rem + 2px);
  padding: .25rem .5rem;
  font-size: .875rem;
  line-height: 1.5;
  border-radius: .2rem;
  display: block;
  width: 100%;
  font-weight: 400;
  background-clip: padding-box;
  border: 1px solid #ced4da;
  transition: border-color .15s ease-in-out,box-shadow .15s ease-in-out,-webkit-box-shadow .15s ease-in-out;
}

</style>