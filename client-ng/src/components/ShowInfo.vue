<template>
<div>
  <b-modal :id="'modalInfo'+crise" no-close-on-backdrop ref="modal" title="Informations" hide-footer>
    <component v-bind:is="loaderFeatureInfo"></component>
    <b-btn size="sm" class="float-right" variant="primary" @click="hideModal"> Fermer </b-btn>
  </b-modal>
</div>
</template>

<script>
import * as eventTypes from '../bus/event-types.js'
import moment from 'moment'
import Vue from 'vue'
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
      html: null,
      loaderFeatureInfo: null,
      feature: null,
      nomFeature: null,
      natureFeature: null,
      creationFeature: null,
      features: [],
      selected: null,
      currentView: null
    }
  },
  methods: {
    showModal(html) {
      this.html = html
      if ("" !== this.html && this.html.indexOf("<body></body>") === -1) {
        this.loaderFeatureInfo = Vue.component('info-loader', {
          data() {
            return {}
          },
          template: "<div>" + html + "</div>"
        })
        this.$refs.modal.show()
        this.$root.$emit('bv::hide::popover')
      } else {
        this.$notify({
          group: 'remocra',
          title: 'Évènements',
          type: 'warn',
          text: 'Aucun évènement trouvé'
        })
      }
    },
    showModalFromValues(feature) {
      this.nomFeature = feature.feature.nom
      this.natureFeature = feature.feature.natureNom
      this.creationFeature = moment(new Date(feature.feature.creation), 'DD/MM/YYYY[T]HH:mm:ss[Z]').format('DD/MM/YYYY' + ' - ' + 'HH:mm')
      this.$refs.modal.show()
      this.$root.$emit('bv::hide::popover')
    },
    hideModal() {
      this.html = null
      this.$refs.modal.hide()
      this.$root.$options.bus.$emit(eventTypes.REFRESH_MAP, this.crise)
    }
  }
}
</script>

<style scoped>
strong {
  font-weight: bold;
  display: -webkit-inline-box;
}

.infoFeatures {
  max-height: 500px;
  overflow: auto;
  margin-bottom: 10px;
  background-color: rgb(255, 255, 255);
  padding: 5px;
}
</style>
