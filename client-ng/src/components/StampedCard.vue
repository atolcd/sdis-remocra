<template>
<div>
  <!-- Modal Component -->
  <modal :id="'modalCard'+crise" :name="'modalCard'+crise" @before-close="clearName" :draggable="true"
   :reset="true"
    width="50%"
    height="auto">
       <header class="modal-header"><h5 class="modal-title">Carte horodatée</h5>
    <div slot="top-right">
     <button type="button" aria-label="Close" @click="$modal.hide('modalCard'+crise)" class="close">×</button>
    </div></header>
    <form @submit.stop.prevent="handleSubmit">
      <b-form-input type="text" placeholder="Saisir le titre de la carte" v-model="name"></b-form-input>
    </form>
    <div class="modal-footer">
        <b-button size="sm" type="reset" variant="secondary" @click="$modal.hide('modalCard'+crise)">Annuler</b-button>
        <b-button size="sm" type="submit" variant="primary" @click="handleOk" >Valider</b-button>
      </div>
  </modal>
</div>
</template>

<script>
import axios from 'axios'
import * as eventTypes from '../bus/event-types.js'
export default {
  name: 'StampedCard',
  props: {
    crise: {
      required: true,
      type: Number
    }
  },
  components: {},
  data() {
    return {
      image: null,
      name: ''
    }
  },
  methods: {
    makeCard(canvas, extent) {
      this.canvas = canvas
      this.extent = extent
      this.$modal.show('modalCard'+this.crise)
    },
    handleOk(evt) {
      // Prevent modal from closing
      evt.preventDefault()
      if (!this.name) {
        this.$notify({
          group: 'remocra',
          title: 'Carte horodatée',
          type: 'warn',
          text: 'Veuillez saisir le titre de la carte'
        })
      } else {
        this.handleSubmit()
      }
    },
    clearName() {
      this.name = ''
    },
    handleSubmit() {
      var cardName = this.name
      var criseId = this.crise
      this.clearName()
      this.saveCard(cardName, criseId)
    },
    saveCard(cardName, criseId) {
      let formData = new FormData()
      var self = this
      self.canvas.toBlob(function(blob) {
        var fileOfBlob = new File([blob], cardName + '.png', {
          type: 'image/png'
        })
        formData.append('files[0]', fileOfBlob)
        formData.append('geometrie', self.extent)
        axios.post('/remocra/crises/' + criseId + '/documents', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).then(response => {
          if (response.data.success) {
            self.$root.$options.bus.$emit(eventTypes.LOAD_DOCUMENTS, criseId)
          }
        }).catch(function(error) {
          console.error('postEvent', error)
        })
        self.$modal.hide('modalCard'+criseId)
      }, 'image/png')
    }
  }
}
</script>

<style>
</style>
