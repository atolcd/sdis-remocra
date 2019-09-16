<template>
<div>
  <b-modal :id="'modalChoice'+crise" ref="modal" no-close-on-backdrop title="Évènements cartographiques" ok-title="Valider" cancel-title="Annuler" @ok="handleOk" @hidden="clearMode()">
    <b-form-select v-model="selected" :options="features" class="mb-3" @input="addSelected" />
  </b-modal>
</div>
</template>

<script>
import _ from 'lodash'
import * as eventTypes from '../bus/event-types.js'
export default {
  name: 'ChoiceFeature',
  props: {
    crise: {
      required: true,
      type: Number
    }
  },
  components: {},
  data() {
    return {
      criseId: null,
      features: [],
      originFeatures: [],
      selected: null,
      mode: 'modif'
    }
  },
  methods: {
    showModal(features) {
      this.originFeatures = features
      var options = []
      _.forEach(features, function(feature) {
        options.push({
          value: feature.getId(),
          text: feature.getProperties().nom
        })
      })
      this.features = options
      // on sélectionne le premier par defaut
      this.selected = this.features[0].value
      this.$refs.modal.show()
      this.$root.$emit('bv::hide::popover')
    },
    showInfo(features) {
      this.mode = 'info'
      this.showModal(features)
    },
    clearMode() {
      this.mode = 'modif'
    },
    addSelected() {
      this.$root.$options.bus.$emit(eventTypes.REFRESH_MAP, this.crise)
      var selected = this.selected
      var originFeatures = this.originFeatures
      _.forEach(originFeatures, function(feature) {
        if (feature.getId() === selected) {
          selected = feature
        }
      })
      this.$root.$options.bus.$emit(eventTypes.ADD_TOWORKINGLAYER, selected)
    },
    handleOk() {
      var selected = this.selected
      var originFeatures = this.originFeatures
      _.forEach(originFeatures, function(feature) {
        if (feature.getId() === selected) {
          selected = feature
        }
      })
      if (this.mode == 'info') {
        this.$root.$options.bus.$emit(eventTypes.SHOW_INFO, {
          'feature': selected.getProperties(),
        })
      } else if (this.mode == 'modif') {
        this.$root.$options.bus.$emit(eventTypes.MODIFY_EVENT, {
          'criseId': this.crise,
          'evenementId': selected.getId(),
          'natureId': selected.getProperties().nature
        })
      }
      this.$refs.modal.hide()
    }
  }
}
</script>

<style>
</style>
