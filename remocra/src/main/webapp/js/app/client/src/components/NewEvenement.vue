<template>
<div>
  <b-modal id="modalEvent" ref="modal" title="Nouvel évènement" ok-title="valider" cancel-title="annuler" @ok="handleOk" @hide="clearFields">
    <form @submit.stop.prevent="handleSubmit">
      <b-form-group horizontal label="Type:" label-for="typeEvent">
        <b-form-select id="typeEvent" required  v-model="form.type">
          <optgroup  v-for="(type, name) in types" :key="name" :label="name">
            <option v-for="(nature, index) in type" :key="index" :value="nature.value">
               {{ nature.text }}
            </option>
         </optgroup>
        </b-form-select>
      </b-form-group>
      <b-form-group horizontal label="Titre:" label-for="titleEvent">
        <b-form-input id="titleEvent" type="text" v-model="form.titre"  required>
        </b-form-input>
      </b-form-group>
      <b-form-group  horizontal label="Description:" label-for="descriptEvent">
        <b-form-textarea id="descriptEvent" v-model="form.description" :rows="3" :max-rows="6">
        </b-form-textarea>
      </b-form-group>
      <b-form-group  horizontal label="Intervention associée:" label-for="interventionAssoc">
        <b-form-select id="interventionAssoc" :options="interventionAssocs" required v-model="form.interventionAssoc">
        </b-form-select>
      </b-form-group>
      <b-form-group  horizontal label="Origine:" label-for="origine">
      <search-origine id="origineEvent" :crise='this.criseId'  ref='searchOrigine'></search-origine>
      </b-form-group>
      <b-form-group  horizontal label="Constaté:" label-for="constate">
        <b-form-input v-model="form.constat" :value="form.constat"
                  type="date"></b-form-input>
        <b-form-input v-model="form.time" :value="form.time"
                  type="time"></b-form-input>
      </b-form-group>
      <b-form-group  horizontal label="Importance:" label-for="importance">
        <rate id="importanceEvent" :length="5" v-model="form.importance" />
      </b-form-group>
      <b-form-group  horizontal label="Document:" label-for="document">
        <b-form-file id="documentEvent" v-model="file" :state="Boolean(file)" placeholder="Choose a file..."></b-form-file>
      </b-form-group>
      <b-form-group  horizontal label="Tags:" label-for="tags">
        <input-tag :tags.sync="form.tags"></input-tag>
      </b-form-group>

    </form>
  </b-modal>
</div>
</template>

<script>
/* eslint-disable */
import axios from 'axios'
import moment from 'moment-timezone'
import SearchOrigine from './SearchOrigine.vue'
import _ from 'lodash'
export default {
  name: 'NewEvenement',
  components: {
    SearchOrigine},
  data() {
    return {
      file: null,
      criseId: null,
      evenementId:null,
      form: {
        titre: '',
        type: null,
        description: '',
        interventionAssoc: null,
        origine: null,
        tags: [],
        constat: moment(),
        time: moment(),
        importance: 0,
      },
      types: [],
      categories: [],
      interventionAssocs: [{}],
      origines: [{}]

    }
  },
  methods: {
    showModal(criseId, evenementId) {
      this.criseId = criseId
      this.evenementId = evenementId
      var types = []
      var categories = []
      axios.get('/remocra/typecrisecategorieevenement')
        .then((response) => {
          if (response.data.data) {
            var typeCategs = response.data.data
            _.forEach(typeCategs, function(typeCateg) {
              if (typeCateg !== null) {
                categories.push(typeCateg)
              }
            })
          }
        })
        .catch(function(error) {
          console.error('categorie évenement', error)
        })
      axios.get('/remocra/typecrisenatureevenement')
        .then((response) => {
          if (response.data.data) {
            var typeEvents = response.data.data
            _.forEach(typeEvents, function(typeEvenement) {
              if (typeEvenement.typeGeometrie === null) {
                _.forEach(categories, function(categ) {
                  if (categ.id === typeEvenement.categorieEvenement) {
                    types.push({
                      value: typeEvenement.id,
                      text: typeEvenement.nom,
                      categorie: categ.nom
                    })
                  }
                })
              }
            })
            this.types = _.groupBy(types, t=>t.categorie);
          }
        })
        .catch(function(error) {
          console.error('nature évenement', error)
        })
        axios.get('/remocra/evenements/origines/'+criseId)
          .then((response) => {
          })
          .catch(function(error) {
            console.error('categorie évenement', error)
          })
          //si l'évenementId est renseigné c'est un update
          if(evenementId !== null){
            axios.get('/remocra/evenements/'+criseId+'/'+evenementId)
              .then((response) => {
                var evenement = response.data.data[0]
                this.form.titre = evenement.nom
                this.form.description = evenement.description
                this.form.constat = moment(evenement.constat.toString()).format("YYYY-MM-DD")
                this.form.time = moment(evenement.constat.toString()).format("HH:mm")
                this.form.origine = evenement.origine
                this.$refs.searchOrigine.selected = evenement.origine
                this.form.importance = evenement.importance
                this.form.tags = evenement.tags.split(",")
                this.form.type = evenement.typeCriseNatureEvenement.id
                console.log(this.form.constat)
              })
              .catch(function(error) {
                console.error('categorie évenement', error)
              })
          }
      this.$root.$emit('bv::hide::popover')
      this.$refs.modal.show()
    },
    clearFields() {
      this.form.titre = ''
      this.form.description = ''
      this.form.constat = moment().format("YYYY-MM-DD")
      this.form.time = moment().format("HH:mm")
      this.form.origine = null
      this.$refs.searchOrigine.selected = null
      this.form.importance = 0
      this.form.tags = []
      this.form.type = null
    },
    handleOk(evt) {
      // Prevent modal from closing
      evt.preventDefault()
      if (!this.form.titre) {
        alert('Please enter your name')
      } else {
        this.handleSubmit()
      }
    },
    handleSubmit() {
      var formData = {'nom': this.form.titre,
      'description': this.form.description,
      'constat': moment(this.form.constat.toString()+'T'+this.form.time.toString()).format(),
      'origine': this.$refs.searchOrigine.selected !== null ? this.$refs.searchOrigine.selected : this.$refs.searchOrigine.searchInput,
      'importance': this.form.importance,
      'tags': this.form.tags.join(),
      'crise': this.criseId,
      'natureEvent': this.form.type}
      if(this.evenementId != null){
        axios.put('/remocra/evenements/'+this.evenementId, formData)
          .then((response) => {
             if(response.data.success){
               this.$parent.$refs.evenements.loadEvenements(this.criseId)
             }
          })
          .catch(function(error) {
            console.error('putEvent', error)
          })
      }else{
        axios.post('/remocra/evenements', formData)
          .then((response) => {
             if(response.data.success){
               this.$parent.$refs.evenements.loadEvenements(this.criseId)
             }
          })
          .catch(function(error) {
            console.error('postEvent', error)
          })
      }
      this.$refs.modal.hide()
    }
  }
}
</script>
<style>
.input-tag{
  color: #6c757d;
  background-color: #fff;
}
</style>
