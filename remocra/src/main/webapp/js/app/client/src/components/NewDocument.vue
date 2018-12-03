<template>
<div>
  <b-modal id="modalDocument" ref="modal" title="Nouveau document" ok-title="Valider"  cancel-title="Annuler" @ok="handleOk" @hidden="clearFields">
    <form @submit.stop.prevent="handleSubmit">
     <b-form-group horizontal label="Document:" label-for="docs">
     <div class="custom-file b-form-file ">
       <input id ="docs" type="file" class="custom-file-input"  @change="handleChangeFile($event)">
       <label class="custom-file-label">{{file && file.name}}</label></div>
       <div v-for="(file, index) in files" :key="index" class="mt-3">
         <img @click="deleteFile(file.name)" src="/static/img/delete.png"><strong >   {{file && file.name}}</strong>
       </div>
       </b-form-group>
     </form>
  </b-modal>
</div>
</template>

<script>
/* eslint-disable */
import axios from 'axios'
import _ from 'lodash'
export default {
  name: 'NewDocument',
  data() {
    return {
        criseId:null,
        file: null,
        files: []
    }
  },
  methods: {
    showModal(criseId) {
      this.criseId = criseId
       this.$refs.modal.show()
    },
    clearFields() {

    }, handleOk(evt) {
        // Prevent modal from closing
        evt.preventDefault()
        if (this.files.length == 0) {
          alert('Veuillez ajouter des documents')
        } else {
          this.handleSubmit()
          this.$parent.refreshMap()
        }
    },
    handleSubmit() {
       let formData = new FormData();
       for( var i = 0; i < this.files.length; i++ ){
         let file = this.files[i];
         formData.append('files[' + i + ']', file);
       }
      axios.post( '/remocra/crises/'+this.criseId+'/documents',formData,
          {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
          })
          .then((response) => {
             if(response.data.success){
               this.$parent.$refs.documents.loadDocuments(this.criseId)
               this.$refs.modal.hide()
             }
          })
          .catch(function(error) {
            console.error('postEvent', error)
          })
    },
    handleChangeFile(event) {
     var file = event.target.files[0]
     if(file && file.name != null){
       var index = _.findIndex(this.files, function(o) { return o.name == file.name })
        if(index == -1){
          this.file = file
          this.files.push(event.target.files[0])
        }
      }
  },
  deleteFile(fileName){
     this.files = _.reject(this.files, function(file) {
     return fileName == file.name
    })
    this.file = null
    document.getElementById("docs").value = null
  }
}
}
</script>

<style>
</style>
