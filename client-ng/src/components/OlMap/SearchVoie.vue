<template>
<form v-on:submit.prevent>
  <autocomplete :items="itemsFiltered"
                :disabled="disabled"
                v-model="item"
                :get-label="getLabel"
                :component-item='template'
                @item-selected="onValueChanged"
                @update-items="refreshData"
                :auto-select-one-item="false"
                :min-len="0"
                placeholder="Voie"
                class="inputVoie">
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
  data() {
    return {
      item: null,
      items: [],
      itemsFiltered: null,
      template: VoieTemplate,
    }
  },
  props: {
    commune: {
      required: false
    },
    defaultValue: {
      required: false,
      type: String
    },
    disabled: {
      required: false,
      type: Boolean
    }
  },

  watch: {
    commune: function() {
      this.refreshData(null);
    }
  },
  methods: {

    refreshData(text) {
      if(this.commune !== null) {
        this.items = [];
        axios.get('/remocra/voies/mc.json', {
          params: {
            withgeom: true,
            page: 1,
            start: 0,
            limit: 10,
            query: text,
            filter: JSON.stringify([{
              "property": "communeId",
              "value": this.commune.id
            }])
          }
        }).then(response => {
          _.forEach(response.data.data, voie => {
            this.items.push({
              nom: voie.nom,
              geometrie: voie.geometrie
            });
          })
          this.itemsFiltered = _.clone(this.items, true);
        }).then(() => {
          if (this.defaultValue) {
            this.item = this.defaultValue;
          }
        });
      } else {
        this.item = null;
      }
    },

    getLabel(item) {
      return item ? item.nom : ''
    },

    onValueChanged(voie) {
      this.item = voie;
      this.$emit('onVoieSelected', voie);
    },
  }
};
</script>

<style>
.inputVoie {
  min-width: 100px;
}

.v-autocomplete-list {
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
  transition: border-color .15s ease-in-out, box-shadow .15s ease-in-out, -webkit-box-shadow .15s ease-in-out;
}
</style>
