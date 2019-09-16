<template>
<div>
  <b-modal :id="'modalDocument'+criseId" ref="modal" title="Nouveau document" no-close-on-backdrop ok-title="Valider" cancel-title="Annuler" @ok="handleOk" @hidden="clearFields">
    <form @submit.stop.prevent="handleSubmit">
      <b-form-group horizontal label="Document:" label-for="docs">
        <div class="custom-file b-form-file ">
          <input id="docs" type="file" class="custom-file-input" @change="handleChangeFile($event)">
          <label class="custom-file-label">{{file && file.name}}</label></div>
        <div v-for="(file, index) in files" :key="index" class="mt-3">
          <img @click="deleteFile(file.name)" src="/remocra/static/img/delete.png"><strong> {{file && file.name}}</strong>
        </div>
      </b-form-group>
    </form>
  </b-modal>
</div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'
import * as eventTypes from '../bus/event-types.js'
export default {
  name: 'NewDocument',
  props: {
    criseId: {
      required: true,
      type: Number
    }
  },
  data() {
    return {
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
      this.file = null,
        this.files = []
    },
    handleOk(evt) {
      // Prevent modal from closing
      evt.preventDefault()
      if (this.files.length === 0) {
        this.$notify({
          group: 'remocra',
          title: 'Nouveau document',
          type: 'warn',
          text: 'Veuillez ajouter des documents'
        })
      } else {
        this.handleSubmit()
        this.$root.$options.bus.$emit(eventTypes.REFRESH_MAP, this.criseId)
      }
    },
    handleSubmit() {
      let formData = new FormData()
      for (var i = 0; i < this.files.length; i++) {
        let file = this.files[i]
        formData.append('files[' + i + ']', file)
      }
      axios.post('/remocra/crises/' + this.criseId + '/documents', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(response => {
        if (response.data.success) {
          this.$root.$options.bus.$emit(eventTypes.LOAD_DOCUMENTS, this.criseId)
          this.$refs.modal.hide()
        }
      }).catch(function(error) {
        console.error('postEvent', error)
      })
    },
    handleChangeFile(event) {
      var file = event.target.files[0]
      if (file && file.name != null) {
        var index = _.findIndex(this.files, function(o) {
          return o.name === file.name
        })
        if (index === -1) {
          this.file = file
          this.files.push(event.target.files[0])
        }
      }
    },
    deleteFile(fileName) {
      this.files = _.reject(this.files, function(file) {
        return fileName === file.name
      })
      this.file = null
      document.getElementById('docs').value = null
    }
  }
}
</script>

<style scoped>
.mt-3 {
  display: -webkit-box;
}

.mt-3 img {
  margin-right: 10px;
}
</style>
