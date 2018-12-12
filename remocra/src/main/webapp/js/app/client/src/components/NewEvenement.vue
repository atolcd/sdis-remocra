<template>
<div>
  <b-modal id="modalEvent" ref="modal" :title="title" ok-title="Valider" cancel-title="Annuler" @ok="handleOk" @hidden="clearFields">
    <form @submit.stop.prevent="handleSubmit">
      <b-form-group horizontal label="Type:" label-for="typeEvent">
        <b-form-select :disabled="disableNatures" id="typeEvent" required  v-model="form.type">
          <optgroup v-for="(type, name) in types" :key="name" :label="name">
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
                  type="time" style="margin-top:6px;"></b-form-input>
      </b-form-group>
      <b-form-group  horizontal label="Importance:" label-for="importance">
        <rate id="importanceEvent" :length="5" v-model="form.importance" />
      </b-form-group>
      <b-form-group  horizontal label="Document:" label-for="document">
        <div class="custom-file b-form-file ">
          <input id ="eventDocs" type="file" class="custom-file-input"  @change="handleChangeFile($event)">
          <label class="custom-file-label">{{file && file.name}}</label></div>
          <div v-for="(file, index) in files" :key="index" class="mt-3">
            <img @click="deleteFile(file.name)" src="/static/img/delete.png"><strong >   {{file && file.name || file.fichier}}</strong>
          </div>
      </b-form-group>
      <b-form-group  horizontal label="Tags:" label-for="tags">
        <input-tag :tags.sync="form.tags"></input-tag>
      </b-form-group>
      <b-form-group  horizontal label="Clore l'évènement:" label-for="cloture">
        <input style="width:5%" id="cloture" type="checkbox" :value="cloture" v-model="cloture">
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
import EventBus from '../bus'
import * as eventTypes from '../bus/event-types.js'

export default {
  name: 'NewEvenement',
  components: {
    SearchOrigine},
  data() {
    return {
      file: null,
      files: [],
      title:"Nouvel évènement",
      cloture:false,
      criseId: null,
      evenementId:null,
      natureId:null,
      disableNatures: false,
      form: {
        titre: '',
        type: null,
        description: '',
        interventionAssoc: null,
        origine: null,
        geometrie: null,
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
  mounted(){
    EventBus.$on(eventTypes.MODIFY_EVENT, args => {this.modifyEvent(args.criseId, args.evenementId, args.natureId )})
  },
  methods: {
    createEvent(criseId){
      this.criseId = criseId
      this.loadEvenementNatures(null)
      this.$root.$emit('bv::hide::popover')
      this.title="Nouvel évènement"
      this.$refs.modal.show()
    },
    createCartoEvent(criseId, natureId, wktfeaturegeom){
      this.criseId = criseId
      this.natureId = natureId
      this.form.geometrie = wktfeaturegeom
      this.loadEvenementNatures(natureId)
      axios.get('/remocra/typecrisenatureevenement/nature/'+ natureId)
        .then((response) => {
          if (response.data.data) {
            this.form.type = response.data.data[0].id
            this.disableNatures = true
          }
        })
        .catch(function(error) {
          console.error('nature évenement', error)
        })
      this.$root.$emit('bv::hide::popover')
      this.title="Nouvel évènement"
      this.$refs.modal.show()
    },
    modifyEvent(criseId, evenementId, natureId){
      this.criseId = criseId
      this.evenementId = evenementId
        this.loadEvenementNatures(natureId)
          //si l'évenementId est renseigné c'est un update
          if(evenementId !== null){
            this.disableNatures = true
            axios.get('/remocra/evenements/'+criseId+'/'+evenementId)
              .then((response) => {
                var evenement = response.data.data[0]
                this.form.titre = evenement.nom
                this.form.description = evenement.description
                this.form.constat = moment(evenement.constat.toString()).format("YYYY-MM-DD")
                this.form.time = moment(evenement.constat.toString()).format("HH:mm")
                if(evenement.cloture !== null){
                  this.cloture = true
                  this.form.cloture = evenement.cloture
                }
                this.form.origine = evenement.origine
                this.$refs.searchOrigine.selected = evenement.origine
                this.form.importance = evenement.importance
                this.form.tags = evenement.tags && evenement.tags.length !== 0 ? evenement.tags.split(","): []
                this.form.type = evenement.typeCriseNatureEvenement.id
              })
              .catch(function(error) {
                console.error('categorie évenement', error)
              })

              axios.get('/remocra/evenements/'+evenementId+'/docevents')
                .then((response) => {
                  if (response.data.data) {
                     this.files = response.data.data
                  }
                })
                .catch(function(error) {
                  console.error('documents', error)
                })
          }
      this.$root.$emit('bv::hide::popover')
      this.title = "Modification d'événement"
      this.$refs.modal.show()
    },
    clearFields() {

      //todo instancier les data en null et faire un reset
      this.file = null
      this.files = []
      this.form.titre = ''
      this.form.description = ''
      this.form.constat = moment().format("YYYY-MM-DD")
      this.cloture = false
      this.form.cloture = null
      this.form.time = moment().format("HH:mm")
      this.form.origine = null
      this.$refs.searchOrigine.selected = null
      this.$refs.searchOrigine.searchInput = ""
      this.form.importance = 0
      this.form.tags = []
      this.form.type = null
      this.types= []
      this.categories= []
      this.interventionAssocs= [{}]
      this.origines= [{}]
      this.disableNatures= false
      this.criseId =  null
      this.evenementId = null
      this.natureId = null
      EventBus.$emit(eventTypes.REFRESH_MAP)
    },
    handleOk(evt) {
      // Prevent modal from closing
      evt.preventDefault()
      if (!this.form.titre || !this.form.type || !(this.$refs.searchOrigine.searchInput !== "" || this.$refs.searchOrigine.selected !== null)
       || !this.form.constat || !this.form.time) {
        alert('Veuillez saisir les champs obligatoires')
      } else if (this.form.cloture != null) {
        alert('L\'évènement est déjà clos')
      }else {
        this.handleSubmit()
        EventBus.$emit(eventTypes.REFRESH_MAP)
      }
    },
    handleSubmit() {
      let formData = new FormData()
      formData.append('nom', this.form.titre)
      formData.append('description', this.form.description)
      formData.append('constat', moment(this.form.constat.toString()+'T'+this.form.time.toString()).format())
      if(this.cloture){
        formData.append('cloture', new moment())
      }
      if(this.form.geometrie !== null){
        formData.append('geometrie', this.form.geometrie)
      }
      formData.append('origine', this.$refs.searchOrigine.selected !== null ? this.$refs.searchOrigine.selected : this.$refs.searchOrigine.searchInput)
      formData.append('importance', this.form.importance)
      formData.append('tags', this.form.tags.join())
      formData.append('crise', this.criseId)
      formData.append('natureEvent', this.form.type)
      for( var i = 0; i < this.files.length; i++ ){
        let file = this.files[i];
        formData.append('files[' + i + ']', file);
      }
      var criseId = this.criseId
      if(this.evenementId != null){
        axios.post('/remocra/evenements/'+this.evenementId, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
          })
          .then((response) => {
             if(response.data.success){
               EventBus.$emit(eventTypes.LOAD_EVENEMENTS, {'crise': criseId})
               EventBus.$emit(eventTypes.LOAD_DOCUMENTS,criseId)
               EventBus.$emit(eventTypes.LOAD_FILTERS,criseId)
               EventBus.$emit(eventTypes.REFRESH_MAP)
               this.$refs.modal.hide()
             }
          })
          .catch(function(error) {
            console.error('putEvent', error)
          })
      }else{
        axios.post('/remocra/evenements', formData,
          {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
          })
          .then((response) => {
             if(response.data.success){
               EventBus.$emit(eventTypes.LOAD_EVENEMENTS, {'crise': criseId})
               EventBus.$emit(eventTypes.LOAD_DOCUMENTS, criseId)
               EventBus.$emit(eventTypes.LOAD_FILTERS,criseId)
               EventBus.$emit(eventTypes.REFRESH_MAP)
               this.$refs.modal.hide()
             }
          })
          .catch(function(error) {
            console.error('postEvent', error)
          })
      }
    },
    loadEvenementNatures(natureId){
      var types = []
      var categories = []
      if(natureId != null){
        this.form.type = natureId
      }
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
              if(natureId !== null){
                if (typeEvenement.typeGeometrie !== null) {
                  _.forEach(categories, function(categ) {
                    if (categ.id === typeEvenement.categorieEvenement) {
                      types.push({
                        value: typeEvenement.id,
                        text: typeEvenement.nom,
                        categorie: categ.nom,
                        typeGeometrie: typeEvenement.typeGeometrie
                      })
                    }
                  })
                }
              }else {
                if (typeEvenement.typeGeometrie === null) {
                  _.forEach(categories, function(categ) {
                    if (categ.id === typeEvenement.categorieEvenement) {
                      types.push({
                        value: typeEvenement.id,
                        text: typeEvenement.nom,
                        categorie: categ.nom,
                        typeGeometrie: typeEvenement.typeGeometrie
                      })
                    }
                  })
                }
              }
            })
            this.types = _.groupBy(types, t=>t.categorie);
          }
        })
        .catch(function(error) {
          console.error('nature évenement', error)
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

<style scoped>

>>> input {
    width: 100%;
}

</style>
