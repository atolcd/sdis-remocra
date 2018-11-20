<template>
<div>
  <b-modal id="modalChoice" ref="modal" title="Evènements cartographiques" ok-title="valider" cancel-title="annuler" @ok="handleOk">
    <b-form-select v-model="selected" :options="features" class="mb-3" @change="addSelected"/>
  </b-modal>
</div>
</template>

<script>
/* eslint-disable */
import _ from 'lodash'
export default {
  name: 'ChoiceFeature',
  components: {
  },
  data() {
    return {
      criseId: null,
      features:[],
      originFeatures:[],
      selected: null
    }
  },
  methods: {
    showModal(features) {
        this.originFeatures = features
        var options = []
      _.forEach(features, function(feature){
        options.push({value: feature.getId(), text:feature.getProperties().nom})
      })
      this.features = options
      //on sélectionne le premier par defaut
      this.selected = this.features[0].value
      this.$refs.modal.show()
      this.$root.$emit('bv::hide::popover')
    },
    addSelected() {
    this.$parent.refreshMap()
    var selected = this.selected
    var originFeatures = this.originFeatures
    _.forEach(originFeatures, function(feature){
       if(feature.getId() === selected){
         selected = feature
       }
    })
    this.$parent.addToWorkingLayer(selected)
  },
  handleOk() {
    var selected = this.selected
    var originFeatures = this.originFeatures
    _.forEach(originFeatures, function(feature){
       if(feature.getId() === selected){
         selected = feature
       }
    })
     this.$refs.modal.hide()
     this.$parent.$refs.newEvenement.modifyEvent(this.criseId, selected.getId(), selected.getProperties().nature)
  }
}
}
</script>
<style>

</style>
