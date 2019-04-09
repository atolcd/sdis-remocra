<template>
<div>
  <!-- Modal Component -->
  <b-modal id="modalCard" ref="modal" no-close-on-backdrop title="Carte horodatée" @ok="handleOk" @shown="clearName">
    <form @submit.stop.prevent="handleSubmit">
      <b-form-input type="text" placeholder="Saisir le titre de la carte" v-model="name"></b-form-input>
    </form>
  </b-modal>
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
      this.$refs.modal.show()
    },
    handleOk(evt) {
      // Prevent modal from closing
      evt.preventDefault()
      if (!this.name) {
        alert('Please enter your name')
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
      this.$refs.modal.hide()
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
            this.$root.$options.bus.$emit(eventTypes.LOAD_DOCUMENTS, criseId)
          }
        }).catch(function(error) {
          console.error('postEvent', error)
        })
      }, 'image/png')
    }
  }
}
</script>

<style>
</style>
