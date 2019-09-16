<template>
<div>
  <b-modal id="modalMessage" ref="modal" title="Nouveau message" no-close-on-backdrop ok-title="Valider" cancel-title="Annuler" :ok-disabled="disableOk" @ok="handleOk" @hidden="clearFields">
    <form :id="'formMessage'+criseId" @submit.stop.prevent="handleSubmit" class="needs-validation">
      <b-form-group horizontal label="Objet:" label-for="objet">
        <b-form-input id="objetMessage" required v-model="form.objet" class="form-control">
        </b-form-input>
      </b-form-group>
      <b-form-group horizontal label="Message:" label-for="message">
        <b-form-textarea id="message" v-model="form.message" :rows="3" :max-rows="6" class="form-control">
        </b-form-textarea>
      </b-form-group>
      <b-form-group horizontal label="Constaté:" label-for="constate">
        <b-form-input v-model="form.creation" type="date" class="form-control"></b-form-input>
        <b-form-input v-model="form.time" :value="form.time" type="time" style="margin-top:6px;" class="form-control"></b-form-input>
      </b-form-group>
      <b-form-group horizontal label="Origine:" label-for="origine">
        <search-origine :crise='criseId' ref='searchOrigine'></search-origine>
      </b-form-group>
      <b-form-group horizontal label="Importance:" label-for="importanceMessage">
        <div class="resetrate">
          <img src="/remocra/static/img/resetrate.png" style="cursor:pointer" @click="resetRate">
          <rate id="importanceMessage" :length="5" v-model="form.importance" />
        </div>
      </b-form-group>
      <b-form-group horizontal label="Tags:" label-for="tags">
        <b-form-textarea id="tagsMessage" v-model="form.tags" :rows="3" :max-rows="6">
        </b-form-textarea>
      </b-form-group>
    </form>
  </b-modal>
</div>
</template>

<script>
import axios from 'axios'
import moment from 'moment'
import SearchOrigine from './SearchOrigine.vue'
import * as eventTypes from '../bus/event-types.js'
export default {
  name: 'NewMessage',
  components: {
    SearchOrigine
  },
  props: {
    criseId: {
      required: true,
      type: Number
    }
  },
  data() {
    return {
      evenementId: null,
      disableOk: false,
      form: {
        objet: '',
        message: '',
        origine: '  ',
        tags: '',
        creation: moment().format('YYYY-MM-DD'),
        time: moment().format('HH:mm'),
        importance: 0
      }
    }
  },
  methods: {
    showModal(criseId, evenementId) {
      this.criseId = criseId
      this.evenementId = evenementId
      this.$refs.modal.show()
      this.$root.$emit('bv::hide::popover')
    },
    clearFields() {
      document.getElementById('formMessage' + this.criseId).classList.remove('was-validated')
      this.form.objet = ''
      this.form.message = ''
      this.form.creation = moment().format('YYYY-MM-DD')
      this.form.time = moment().format('HH:mm')
      this.form.origine = null
      this.$refs.searchOrigine.origine = '  '
      this.form.importance = 0
      this.form.tags = ''
      this.evenementId = null
      this.disableOk = false
    },
    handleOk(evt) {
      // Prevent modal from closing
      evt.preventDefault()
      if (document.getElementById('formMessage' + this.criseId).checkValidity()) {
        this.handleSubmit()
      } else {
        this.$notify({
          group: 'remocra',
          title: 'Évènements',
          type: 'error',
          text: 'Veuillez saisir les champs obligatoires.'
        })
        document.getElementById('formMessage' + this.criseId).classList.add('was-validated')
      }
    },
    handleSubmit() {
      var formData = {
        objet: this.form.objet,
        message: this.form.message,
        creation: moment(this.form.creation.toString() + 'T' + this.form.time.toString()).format(),
        origine: this.$refs.searchOrigine.origine && this.$refs.searchOrigine.origine !== null ? this.$refs.searchOrigine.origine : this.$refs.searchOrigine.searchText,
        importance: this.form.importance,
        tags: this.form.tags,
        crise: this.criseId,
        evenement: this.evenementId
      }
      var criseId = this.criseId
      axios.post('/remocra/evenements/message', formData).then(response => {
        if (response.data.success) {
          this.$root.$options.bus.$emit(eventTypes.LOAD_EVENEMENTS, {
            crise: criseId
          })
          this.$refs.modal.hide()
        }
      }).catch(function(error) {
        console.error('postEvent', error)
      })
    },
    resetRate() {
      this.form.importance = 0
    }
  }
}
</script>

<style scoped>
>>>input {
  width: 100%;
}

>>>button.Rate__star {
  color: #fff;
}
</style>
