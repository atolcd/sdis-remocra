<template>
<div>
  <b-modal id="modalDocument" ref="modal" title="Nouveau document" ok-title="valider"  cancel-title="annuler" @ok="handleOk" @hide="clearFields">
    <!-- Styled -->
     <b-file v-model="file" placeholder="Choose a file..." v-on:input="onInputFile"></b-file>
     <div v-for="(file, index) in files" :key="index" class="mt-3">{{file && file.name}}</div>
  </b-modal>
</div>
</template>

<script>
/* eslint-disable */
import axios from 'axios'
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

    },
    handleOk(evt) {
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
          }
        ).then(function(){
          console.log('SUCCESS!!');
        })
        .catch(function(){
            console.error('documents', error)
        });
    },
    onInputFile() {
     this.files.push(this.file)
  }
}
}
</script>
<style>

</style>
