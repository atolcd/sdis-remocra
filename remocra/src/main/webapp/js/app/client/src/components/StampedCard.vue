<template>
    <div>
      <!-- Modal Component -->
      <b-modal id="modalCard"
               ref="modal"
               title="Carte horodatée"
               @ok="handleOk"
               @shown="clearName">
        <form @submit.stop.prevent="handleSubmit">
          <b-form-input type="text"
                        placeholder="Saisir le titre de la carte"
                        v-model="name"></b-form-input>
          </form>
      </b-modal>
    </div>
</template>

<script>
/* eslint-disable */
import _ from 'lodash'
import axios from 'axios'
export default {
  name: 'StampedCard',
  props: {
   crise: {
     required: true,
     type: String
   }
 },
  components: {
  },
  data() {
    return {
      image: null,
      name: ''
    }
  },
  methods:{
      makeCard(canvas, extent){
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
      clearName () {
        this.name = ''
      },
      handleSubmit () {
        var cardName = this.name
        var criseId = this.crise
        this.clearName()
        this.$refs.modal.hide()
        this.saveCard(cardName, criseId)
      },
      saveCard(cardName, criseId) {
        let date = new Date()
        let formData = new FormData();
        var self = this
            self.canvas.toBlob(function(blob) {
              var fileOfBlob = new File([blob], cardName+'.jpeg', {type: 'image/jpeg'});
           formData.append('files[0]', fileOfBlob);
           formData.append('geometrie', self.extent)
           console.log(formData)
           axios.post( '/remocra/crises/'+criseId+'/documents',formData,
               {
                 headers: {
                     'Content-Type': 'multipart/form-data'
                 }
               })
               .then((response) => {
                  if(response.data.success){
                    self.$parent.$refs.documents.loadDocuments(criseId)
                  }
               })
               .catch(function(error) {
                 console.error('postEvent', error)
               })
          },'image/jpeg')
      }
    }
}
</script>
<style>

</style>
