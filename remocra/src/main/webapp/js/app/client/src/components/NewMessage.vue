<template>
<div>
  <b-modal id="modalMessage" ref="modal" title="Nouveau message" ok-title="Valider" cancel-title="Annuler" :ok-disabled="disableOk" @ok="handleOk" @hide="clearFields">
    <form @submit.stop.prevent="handleSubmit">
      <b-form-group horizontal label="Objet:" label-for="objet">
        <b-form-input id="objetMessage" required  v-model="form.objet">
        </b-form-input>
      </b-form-group>
      <b-form-group  horizontal label="Message:" label-for="message">
        <b-form-textarea id="message" v-model="form.message" :rows="3" :max-rows="6">
        </b-form-textarea>
      </b-form-group>
      <b-form-group  horizontal label="Constaté:" label-for="constate">
        <b-form-input v-model="form.creation"
                  type="date"></b-form-input>
        <b-form-input v-model="form.time" :value="form.time"
                  type="time" style="margin-top:6px;"></b-form-input>
      </b-form-group>
      <b-form-group horizontal label="Origine:" label-for="origine">
      <search-origine :crise='this.criseId'  ref='searchOrigine'></search-origine>
      </b-form-group>
      <b-form-group  horizontal label="Importance:" label-for="importance">
        <rate id="importanceMessage" :length="5" v-model="form.importance" />
      </b-form-group>
      <b-form-group  horizontal label="Tags:" label-for="tags">
        <b-form-textarea id="tagsMessage" v-model="form.tags" :rows="3" :max-rows="6">
        </b-form-textarea>
      </b-form-group>
    </form>
  </b-modal>
</div>
</template>

<script>
/* eslint-disable */
import axios from 'axios'
import moment from 'moment'
import SearchOrigine from './SearchOrigine.vue'

import _ from 'lodash'
export default {
  name: 'NewMessage',
  components: {
    SearchOrigine
  },
  data() {
    return {
      criseId: null,
      evenementId: null,
      disableOk: false,
      form: {
        objet: '',
        message: '',
        origine: null,
        tags: '',
        creation: moment(),
        time: moment(),
        importance: 0,
      },
    }
  },
  methods: {
    showModal(criseId, evenementId, messageId) {
      this.criseId = criseId
      this.evenementId = evenementId
      if (messageId !== null) {
        //c'est un updated
        axios.get('/remocra/evenements/message/'+messageId)
          .then((response) => {
            var message = response.data.data[0]
            this.form.objet = message.objet
            this.form.message = message.message
            this.form.creation = moment(message.creation.toString()).format("YYYY-MM-DD")
            this.form.time = moment(message.creation.toString()).format("HH:mm")
            this.form.origine = message.origine
            this.$refs.searchOrigine.selected = message.origine
            this.form.importance = message.importance
            this.form.tags = message.tags
          })
          .catch(function(error) {
            console.error('message', error)
          })
          console.log(this.$refs.modal)
          this.disableOk = true
      }
      this.$refs.modal.show()
      this.$root.$emit('bv::hide::popover')
    },
    clearFields() {
      this.form.objet = ''
      this.form.message = ''
      this.form.creation = moment().format("YYYY-MM-DD")
      this.form.time = moment().format("HH:mm")
      this.$refs.searchOrigine.selected = null
      this.form.origine = null
      this.form.importance = 0
      this.form.tags = ''
      this.criseId =  null,
      this.evenementId =  null,
      this.disableOk = false

    },
    handleOk(evt) {
      // Prevent modal from closing
      evt.preventDefault()
      if (!this.form.objet) {
        alert('Please enter your name')
      } else {
        this.handleSubmit()
      }
    },
    handleSubmit() {
      var formData = {'objet': this.form.objet,
      'message': this.form.message,
      'creation': moment(this.form.creation.toString()+'T'+this.form.time.toString()).format(),
      'origine': this.$refs.searchOrigine.selected !== null ? this.$refs.searchOrigine.selected : this.$refs.searchOrigine.searchInput,
      'importance': this.form.importance,
      'tags': this.form.tags,
      'crise': this.criseId,
      'evenement': this.evenementId}
      var criseId = this.criseId
      axios.post('/remocra/evenements/message', formData)
        .then((response) => {
           if(response.data.success){
             this.$parent.loadEvenements(criseId)
             this.$refs.modal.hide()
           }
        })
        .catch(function(error) {
          console.error('postEvent', error)
        })

    }
  }
}
</script>

<style scoped>

>>> input {
    width: 100%;
}

</style>
