<template>
<div>
  <b-modal id="modalInfo" no-close-on-backdrop ref="modal" title="Informations" hide-footer>
    <div><strong>Nom :</strong> {{nomFeature}}</div>
    <div><strong>Nature :</strong> {{natureFeature}}</div>
    <div><strong>Constaté le :</strong> {{creationFeature}}</div>
    <b-btn size="sm" class="float-right" variant="primary" @click="hideModal"> Fermer </b-btn>
  </b-modal>
</div>
</template>

<script>
import * as eventTypes from '../bus/event-types.js'
import moment from 'moment'
export default {
  name: 'ShowInfo',
  components: {},
  props: {
    crise: {
      required: true,
      type: Number
    }
  },
  data() {
    return {
      feature: null,
      nomFeature: null,
      natureFeature: null,
      creationFeature: null,
      features: [],
      selected: null
    }
  },
  methods: {
    showModal(feature) {
      this.nomFeature = feature.getProperties().nom
      this.natureFeature = feature.getProperties().natureNom
      this.creationFeature = moment(new Date(feature.getProperties().creation), 'DD/MM/YYYY[T]HH:mm:ss[Z]').format('DD/MM/YYYY' + ' - ' + 'HH:mm')
      this.$refs.modal.show()
      this.$root.$emit('bv::hide::popover')
    },
    hideModal() {
      this.$refs.modal.hide()
      this.$root.$options.bus.$emit(eventTypes.REFRESH_MAP,this.crise)
    }
  }
}
</script>

<style scoped>
strong {
  font-weight: bold;
  display: -webkit-inline-box;
}
</style>
