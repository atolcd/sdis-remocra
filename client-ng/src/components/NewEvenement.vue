<template>
<div>
  <modal :name="'modalEvent'+criseId" :id="'modalEvent'+criseId" :draggable="true"  @before-close="clearFields"
  :reset="true"
    width="50%"
    height="auto">
       <header class="modal-header"><h5 class="modal-title">{{title}}</h5>
    <div slot="top-right">
     <button type="button" aria-label="Close" @click="$modal.hide('modalEvent'+criseId)" class="close">×</button>
    </div></header>    <b-tabs id="tabsNewEvenement" ref="tabs" v-model="tabIndex">
      <b-tab title="Général" active>
        <form :id="'formEvent'+criseId" class="needs-validation" @submit.stop.prevent="handleSubmit">
          <b-form-group horizontal label="Type:" label-for="typeEvent">
            <b-form-select :disabled="disableNatures" id="typeEvent" required class="form-control" v-model="form.type" @input="loadComplement">
              <optgroup v-for="(type, name) in types" :key="name" :label="name">
                <option v-for="(nature, index) in type" :key="index" :value="nature.value">
                  {{ nature.text }}
                </option>
              </optgroup>
            </b-form-select>
          </b-form-group>
          <b-form-group horizontal label="Titre:" label-for="titleEvent">
            <b-form-input id="titleEvent" type="text" tabIndex="0" class="form-control" v-model="form.titre" required>
            </b-form-input>
          </b-form-group>
          <b-form-group horizontal label="Description:" label-for="descriptEvent">
            <b-form-textarea id="descriptEvent" v-model="form.description" class="form-control" :rows="3" :max-rows="6">
            </b-form-textarea>
          </b-form-group>
          <b-form-group v-if="showInterventions" horizontal label="Intervention associée:" label-for="interventionAssoc">
            <b-form-checkbox-group v-model="form.interventionAssoc" :options="interventionAssocs" name="buttons-1"></b-form-checkbox-group>
          </b-form-group>
          <b-form-group horizontal label="Origine:" label-for="origine">
            <search-origine id="origineEvent" :crise='criseId' ref='searchOrigine'></search-origine>
          </b-form-group>
          <b-form-group horizontal label="Constaté:" label-for="constate">
            <b-form-input v-model="form.constat" :min="dateMin" :max="dateMax" :value="form.constat" type="date" class="form-control"></b-form-input>
            <b-form-input v-model="form.time" id="timeConstat" @change="checkTimeConstat" :value="form.time" type="time" style="margin-top:6px;" class="form-control"></b-form-input>
          </b-form-group>
          <b-form-group horizontal label="Importance:" label-for="importanceEvent">
            <div class="resetrate">
              <img src="/remocra/static/img/resetrate.png" style="cursor:pointer" @click="resetRate">
              <rate id="importanceEvent" :length="5" v-model="form.importance" />
            </div>
          </b-form-group>
          <b-form-group horizontal label="Tags:" label-for="tags">
            <input-tag v-model="form.tags"></input-tag>
          </b-form-group>
          <b-form-group horizontal label="Clore l'évènement:">
            <b-form-checkbox size="sm" id="cloture" v-model="cloture"></b-form-checkbox>
          </b-form-group>
        </form>
      </b-tab>
      <b-tab id="tabComplement" title="Complément">
        <form :id="'formComplement'+criseId" class="needs-validation" v-if="params.length > 0">
          <div v-for="(param, index) in params" :key="index">
            <b-form-group v-if='param.formulaireTypeControle=="autocomplete"' :id="'input'+param.id" inputType='autocomplete' :required="param.obligatoire" class="parametreComplement" horizontal :label='param.formulaireEtiquette'
              :label-for="'input'+param.id">
              <search-complement :searchInput="param.formulaireValeurDefaut" :ref="'searchinput'+param.id" :paramId="param.id"></search-complement>
            </b-form-group>
            <b-form-group v-if='param.formulaireTypeControle=="combo"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
              <select :id="'input'+param.id" :required='param.obligatoire' class="form-control parametreComplement" inputType="combo">
                <option v-for="(value, key) in getOption(param.id)" :key="key" :value="value.valeur" :selected="value.valeur === value.formulaireValeurDefaut">
                  {{value.libelle}}
                </option>
              </select>
            </b-form-group>
            <b-form-group v-if='param.formulaireTypeControle=="checkbox"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
              <input type='checkbox' style="width:5%" :id="'input'+param.id" :value='param.formulaireValeurDefaut' v-model='param.formulaireValeurDefaut' inputType='checkbox' class="form-control parametreComplement" />
            </b-form-group>
            <b-form-group v-if='param.formulaireTypeControle=="textfield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
              <b-form-input type='text' :id="'input'+param.id" :value='param.formulaireValeurDefaut' :required='param.obligatoire' class="form-control parametreComplement" />
            </b-form-group>
            <b-form-group v-if='param.formulaireTypeControle=="numberfield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
              <b-form-input type="number" :id="'input'+param.id" :value='param.formulaireValeurDefaut' :required='param.obligatoire' :step='(param.typeValeur=="integer")?1:0.001' class="form-control parametreComplement" />
            </b-form-group>
            <b-form-group v-if='param.formulaireTypeControle=="datefield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
              <b-form-input type='date' :id="'input'+param.id" :value='param.formulaireValeurDefaut' :required='param.obligatoire' class="form-control parametreComplement" />
            </b-form-group>
            <b-form-group v-if='param.formulaireTypeControle=="timefield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
              <b-form-input type='time' :id="'input'+param.id" :value='param.formulaireValeurDefaut' :required='param.obligatoire' step='1' class="form-control parametreComplement" />
            </b-form-group>
            <b-form-group v-if='param.formulaireTypeControle=="datetimefield"' :id="'input'+param.id" horizontal :label='param.formulaireEtiquette' inputType='datetimefield' class='parametreComplement' :label-for="'input'+param.id">
              <b-form-input type='date' :id="'input'+param.id+'date'" :value='param.formulaireValeurDefaut !== null ? param.formulaireValeurDefaut.split(" ")[0] : null' :required='param.obligatoire' class='form-control' />
              <b-form-input type='time' :id="'input'+param.id+'time'" :value='param.formulaireValeurDefaut !== null ? param.formulaireValeurDefaut.split(" ")[1]: null' :required='param.obligatoire' step='1' class='form-control' />
            </b-form-group>
          </div>
        </form>
      </b-tab>
      <b-tab title="Document">
        <b-form-group horizontal label="Document:" label-for="document">
          <div class="custom-file b-form-file ">
            <input id="eventDocs" type="file" class="custom-file-input" @change="handleChangeFile($event)">
            <label class="custom-file-label">{{file && file.name}}</label></div>
          <div v-for="(file, index) in files" :key="index" class="mt-3">
            <img @click="deleteFile(file.name || file.fichier)" src="/remocra/static/img/delete.png"><strong> {{file && file.name || file.fichier}}</strong>
          </div>
        </b-form-group>
      </b-tab>
    </b-tabs>
     <div class="modal-footer">
        <b-button size="sm" type="reset" variant="secondary" @click="$modal.hide('modalEvent'+criseId)">Annuler</b-button>
        <b-button size="sm" type="submit" variant="primary" @click="handleOk" >Valider</b-button>
      </div>
  </modal>
</div>
</template>

<script>
import axios from 'axios'
import moment from 'moment'
import SearchOrigine from './SearchOrigine.vue'
import SearchComplement from './SearchComplement.vue'
import _ from 'lodash'
import * as eventTypes from '../bus/event-types.js'
export default {
  name: 'NewEvenement',
  components: {
    SearchOrigine,
    SearchComplement
  },
  props: {
    criseId: {
      required: true,
      type: Number
    },
    modeAffichage: {
      required: true,
      type: String
    }
  },
  data() {
    return {
      file: null,
      showInterventions: true,
      files: [],
      dbFiles: [],
      title: 'Nouvel évènement',
      cloture: false,
      evenementId: null,
      natureId: null,
      disableNatures: false,
      dateMin: null,
      dateMax: null,
      timeMin: null,
      timeMax: null,
      form: {
        titre: '',
        type: null,
        description: '',
        interventionAssoc: [],
        origine: null,
        geometrie: null,
        tags: [],
        constat: moment().format('YYYY-MM-DD'),
        time: moment().format('HH:mm'),
        importance: 0
      },
      types: [],
      categories: [],
      interventionAssocs: [],
      origines: [],
      tabIndex: 0,
      params: [],
      comboOptions: [],
      complements: []
    }
  },
  mounted() {
    axios.get('/remocra/crises/' + this.criseId + '/activation').then((response) => {
      if (response.data.data) {
        var activation = response.data.data.activation
        this.dateMin = moment(activation).format('YYYY-MM-DD')
        this.dateMax = moment().format('YYYY-MM-DD')
        this.timeMin = moment(activation).format('HH:mm')
        this.timeMax = moment().format('HH:mm')
      }
    }).catch(function(error) {
      console.error('max date', error)
    })
    this.$root.$options.bus.$on(eventTypes.MODIFY_EVENT, args => {
      this.modifyEvent(args.criseId, args.evenementId, args.natureId)
    })
  },
  destroyed() {
    this.$root.$options.bus.$off(eventTypes.MODIFY_EVENT)
  },
  methods: {
    createEvent() {
      this.loadEvenementNatures(null)
      this.showInterventions = false
      this.$root.$emit('bv::hide::popover')
      this.title = 'Nouvel évènement'
      this.loadComplement()
      this.$modal.show('modalEvent'+this.criseId)
    },
    createCartoEvent(criseId, natureId, wktfeaturegeom) {
      this.showInterventions = true
      this.natureId = natureId
      this.form.geometrie = wktfeaturegeom
      this.loadEvenementNatures(natureId)
      this.loadEvenementInterventions(criseId)
      this.$root.$emit('bv::hide::popover')
      axios.get('/remocra/typecrisenatureevenement/nature/' + natureId).then((response) => {
        if (response.data.data) {
          this.form.type = response.data.data[0].id
          this.disableNatures = true
          this.title = 'Nouvel évènement'
          this.loadComplement()
          this.$modal.show('modalEvent'+ criseId)
        }
      }).catch(function(error) {
        console.error('nature évenement', error)
      })
    },
    modifyEvent(criseId, evenementId, natureId) {
      this.evenementId = evenementId
      this.loadEvenementNatures(natureId)
      this.loadEvenementInterventions(criseId)
      // si l'évenementId est renseigné c'est un update
      if (evenementId !== null) {
        axios.get('/remocra/evenements/' + criseId + '/' + evenementId).then((response) => {
          var evenement = response.data.data[0]
          if (evenement.geometrie !== null) {
            this.showInterventions = true
          }
          this.form.titre = evenement.nom
          this.form.description = evenement.description
          this.form.constat = moment(evenement.constat.toString()).format('YYYY-MM-DD')
          this.form.time = moment(evenement.constat.toString()).format('HH:mm')
          if (evenement.cloture !== null) {
            this.cloture = true
            this.form.cloture = evenement.cloture
          }
          this.form.origine = evenement.origine !== null ? evenement.origine : '  '
          if (evenement.origine !== null) {
            this.$refs.searchOrigine.search(this.form.origine)
          }
          this.form.importance = evenement.importance
          this.form.tags = evenement.tags && evenement.tags.length !== 0 ? evenement.tags.split(',') : []
          this.form.type = evenement.typeCriseNatureEvenement.id
          if (evenement.interventions.length != 0) {
            _.forEach(evenement.interventions, intervention => {
              this.form.interventionAssoc.push(intervention.id)
            })
          }
          this.disableNatures = true
          if (evenement.criseComplement.length !== 0) {
            _.forEach(evenement.criseComplement, complement => {
              this.complements.push({
                id: complement.proprieteEvenement,
                valeurFormatee: complement.valeurFormatee,
                valeurSource: complement.valeurSource
              })
            })
          }
        }).catch(function(error) {
          console.error('categorie évenement', error)
        })
        axios.get('/remocra/evenements/' + evenementId + '/docevents').then((response) => {
          if (response.data.data) {
            this.files = response.data.data
            this.dbFiles = _.clone(this.files)
          }
        }).catch(function(error) {
          console.error('documents', error)
        })
      }
      this.$root.$emit('bv::hide::popover')
      this.title = 'Modification d\'événement'
      this.$modal.show('modalEvent'+criseId)
    },
    clearFields() {
      // todo instancier les data en null et faire un reset
      document.getElementById('formEvent' + this.criseId).classList.remove('was-validated')
      if (document.getElementById('formComplement' + this.criseId)) {
        document.getElementById('formComplement' + this.criseId).classList.remove('was-validated')
      }
      this.params = []
      this.comboOptions = []
      this.complements = []
      this.file = null
      this.files = []
      this.dbFiles = []
      this.form.titre = ''
      this.form.description = ''
      this.form.constat = moment().format('YYYY-MM-DD')
      this.cloture = false
      this.form.cloture = null
      this.form.time = moment().format('HH:mm')
      this.form.origine = null
      this.$refs.searchOrigine.origine = '  '
      this.form.importance = 0
      this.form.tags = []
      this.form.type = null
      this.form.interventionAssoc = []
      this.types = []
      this.categories = []
      this.interventionAssocs = []
      this.origines = []
      this.disableNatures = false
      this.evenementId = null
      this.natureId = null
      this.$root.$options.bus.$emit(eventTypes.REFRESH_MAP, this.criseId)
      this.tabIndex = 0
      this.dateMin = null
      this.dateMax = null
      this.timeMin = null
      this.timeMax = null
    },
    handleOk(evt) {
      // Prevent modal from closing
      evt.preventDefault()
      this.checkTimeConstat()
      var formValid = document.getElementById('formEvent' + this.criseId).checkValidity()
      var complementValid = document.getElementById('formComplement' + this.criseId) ? document.getElementById('formComplement' + this.criseId).checkValidity() : true
      if ((complementValid && formValid) === false) {
        this.$notify({
          group: 'remocra',
          title: 'Évènements',
          type: 'error',
          text: 'Veuillez saisir les champs obligatoires.'
        })
        document.getElementById('formEvent' + this.criseId).classList.add('was-validated')
        if (document.getElementById('formComplement' + this.criseId)) {
          document.getElementById('formComplement' + this.criseId).classList.add('was-validated')
        }
      } else if (this.form.cloture != null) {
        this.$notify({
          group: 'remocra',
          title: 'Évènements',
          type: 'error',
          text: 'L\'évenement est dejà clos.'
        })
      } else {
        // formulaire d'évenement
        let formData = new FormData()
        formData.append('contexte', this.modeAffichage)
        formData.append('nom', this.form.titre)
        formData.append('description', this.form.description)
        formData.append('constat', moment(this.form.constat.toString() + 'T' + this.form.time.toString()).format())
        if (this.cloture) {
          formData.append('cloture', moment())
        }
        if (this.form.geometrie !== null) {
          formData.append('geometrie', this.form.geometrie)
        }
        formData.append('origine', this.$refs.searchOrigine.origine && this.$refs.searchOrigine.origine !== null ? this.$refs.searchOrigine.origine : this.$refs.searchOrigine.searchText)
        formData.append('importance', this.form.importance)
        formData.append('interventions', JSON.stringify(_.map(this.form.interventionAssoc, intervention => {
          return intervention
        })))
        formData.append('tags', this.form.tags.join())
        formData.append('crise', this.criseId)
        formData.append('natureEvent', this.form.type)
        for (var i = 0; i < this.files.length; i++) {
          var file = this.files[i]
          formData.append('files[' + i + ']', file)
        }
        var filesToSave = _.intersectionBy(this.files, this.dbFiles, 'code')
        if (filesToSave.length !== 0) {
          formData.append('filesToSave', JSON.stringify(_.map(filesToSave, file => {
            return file.id
          })))
        }
        // formulaire complementaire
        _.forEach(document.getElementsByClassName('parametreComplement'), item => {
          if (item.getAttribute('inputType') === 'datetimefield') {
            var date = document.querySelector('input[id=' + item.id + 'date ]').value
            var time = document.querySelector('input[id=' + item.id + 'time]').value
            formData.append(item.id, JSON.stringify({
              'valeursource': null,
              'valeurformatee': date + ' ' + time
            }))
          } else if (item.getAttribute('inputType') === 'autocomplete') {
            var autocomplete = this.$refs['search' + item.id][0]
            formData.append(item.getAttribute('id'), JSON.stringify({
              'valeursource': Object.values(autocomplete.selected)[0],
              'valeurformatee': Object.values(autocomplete.selected)[1]
            }))
          } else if (item.getAttribute('inputType') === 'combo') {
            var value = JSON.parse(item.value)
            formData.append(item.getAttribute('id'), JSON.stringify({
              'valeursource': value.property,
              'valeurformatee': value.value
            }))
          } else if (item.getAttribute('inputType') === 'checkbox') {
            formData.append(item.getAttribute('id'), JSON.stringify({
              'valeursource': null,
              'valeurformatee': item.checked
            }))
          } else {
            formData.append(item.getAttribute('id'), JSON.stringify({
              'valeursource': null,
              'valeurformatee': item.value
            }))
          }
        })
        this.handleSubmit(formData)
        this.$root.$options.bus.$emit(eventTypes.REFRESH_MAP, this.criseId)
      }
    },
    handleSubmit(formData) {
      var criseId = this.criseId
      if (this.evenementId != null) {
        axios.post('/remocra/evenements/' + this.evenementId, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).then((response) => {
          if (response.data.success) {
            this.$root.$options.bus.$emit(eventTypes.LOAD_EVENEMENTS, {
              'crise': criseId
            })
            this.$root.$options.bus.$emit(eventTypes.LOAD_DOCUMENTS, criseId)
            this.$root.$options.bus.$emit(eventTypes.LOAD_FILTERS, criseId)
            this.$root.$options.bus.$emit(eventTypes.REFRESH_MAP, criseId)
            this.$modal.hide('modalEvent'+criseId)
          }
        }).catch(function(error) {
          console.error('putEvent', error)
        })
      } else {
        axios.post('/remocra/evenements', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).then((response) => {
          if (response.data.success) {
            this.$root.$options.bus.$emit(eventTypes.LOAD_EVENEMENTS, {
              'crise': criseId
            })
            this.$root.$options.bus.$emit(eventTypes.LOAD_DOCUMENTS, criseId)
            this.$root.$options.bus.$emit(eventTypes.LOAD_FILTERS, criseId)
            this.$root.$options.bus.$emit(eventTypes.REFRESH_MAP, criseId)
            this.$modal.hide('modalEvent'+criseId)
          }
        }).catch(function(error) {
          console.error('postEvent', error)
        })
      }
    },
    loadEvenementNatures(natureId) {
      var types = []
      var categories = []
      axios.get('/remocra/crises/' + this.criseId + '/activation').then((response) => {
        if (response.data.data) {
          var activation = response.data.data
          this.dateMin = moment(activation).format('YYYY-MM-DD')
          this.dateMax = moment().format('YYYY-MM-DD')
          this.timeMin = moment(activation).format('HH:mm')
          this.timeMax = moment().format('HH:mm')
        }
      }).catch(function(error) {
        console.error('max date', error)
      })
      axios.get('/remocra/typecrisecategorieevenement/' + this.criseId).then((response) => {
        if (response.data.data) {
          var typeCategs = response.data.data
          _.forEach(typeCategs, function(typeCateg) {
            if (typeCateg !== null) {
              categories.push(typeCateg)
            }
          })
          axios.get('/remocra/typecrisenatureevenement').then((response) => {
            if (response.data.data) {
              var typeEvents = response.data.data
              _.forEach(typeEvents, function(typeEvenement) {
                if (natureId !== null) {
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
                } else {
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
              this.types = _.groupBy(types, t => t.categorie)
            }
          }).catch(function(error) {
            console.error('nature évenement', error)
          })
        }
      }).catch(function(error) {
        console.error('categorie évenement', error)
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
      if (event) {
        event.preventDefault()
        event.stopPropagation()
      }
    },
    deleteFile(fileName) {
      this.files = _.reject(this.files, file => {
        return fileName === (file.name || file.fichier)
      })
      this.file = null
      document.getElementById('eventDocs').value = null
    },
    showTabComplement() {
      var listeElements = document.querySelectorAll('a')
      _.forEach(listeElements, element => {
        // si un élément contient dans son id tabComplement
        if (element.id.includes('tabComplement')) {
          // si le formulaire est inexistant
          if (this.params.length === 0) {
            // alors on cache le bouton de la tab
            element.style.display = 'none'
            // sinon le formulaire est présent, l'évènement comporte des champs compléments
          } else {
            // on affiche le bouton de la tab complément
            element.style.display = 'block'
          }
        }
      })
    },
    loadComplement() {
      if (this.form.type !== null) {
        axios.get('/remocra/evenements/proprietes/' + this.form.type).then((response) => {
          if (response.data.data) {
            this.params = _.sortBy(response.data.data, item => item.formulaireNumOrdre)
            this.showTabComplement()
            _.forEach(this.params, param => {
              var valeurFormatee = null
              var valeurSource = null
              if (this.complements.length !== 0) {
                // Valeur par defaut prends la valeur inséré en base dans le cas de modif de complement
                _.forEach(this.complements, complement => {
                  if (param.id === complement.id) {
                    valeurFormatee = complement.valeurFormatee
                    valeurSource = complement.valeurSource
                  }
                })
              }
              if (param.formulaireTypeControle === 'autocomplete') {
                param.formulaireValeurDefaut = valeurFormatee !== null ? valeurFormatee : param.formulaireValeurDefaut
              } else if (param.formulaireTypeControle === 'combo') {
                axios.get('/remocra/evenements/evenementmodparalst/' + param.id).then((response) => {
                  _.forEach(response.data.data, option => {
                    var o = {
                      nomChamp: param.id,
                      valeur: JSON.stringify({
                        'property': option[param.sourceSqlValeur],
                        'value': option[param.sourceSqlLibelle]
                      }),
                      libelle: option[param.sourceSqlLibelle],
                      obligatoire: param.obligatoire,
                      formulaireValeurDefaut: JSON.stringify({
                        'property': valeurSource !== null ? valeurSource : (option[param.sourceSqlLibelle] === param.formulaireValeurDefaut ? option[param.sourceSqlValeur] : null),
                        'value': valeurFormatee !== null ? valeurFormatee : param.formulaireValeurDefaut
                      })
                    }
                    this.comboOptions.push(o)
                  })
                }).catch(function(error) {
                  console.error('Combo', error)
                })
              } else {
                param.formulaireValeurDefaut = valeurFormatee !== null ? valeurFormatee : param.formulaireValeurDefaut
              }
            })
          }
        }).catch(function(error) {
          console.error('complement', error)
        })
      }
    },
    getOption: function(id) {
      return this.comboOptions.filter(function(value) {
        return value.nomChamp === id
      })
    },
    resetRate() {
      this.form.importance = 0
    },
    checkTimeConstat() {
      var input = document.getElementById("timeConstat")
      if (this.form.constat == this.dateMin && this.form.time < this.timeMin) {
        input.setCustomValidity(" La date de constatation doit être égale ou postérieure à la date de création de la crise")
        input.reportValidity()
      } else if (this.form.constat == this.dateMax && this.form.time > this.timeMax) {
        input.setCustomValidity("La date de constatation doit être égale ou antérieure à la date actuelle")
        input.reportValidity()
      } else {
        input.setCustomValidity("")
      }
    },
    loadEvenementInterventions(criseId) {
      axios.get('/remocra/intervention/' + criseId).then((response) => {
        if (response.data) {
          var criseInterventions = []
          _.forEach(response.data.data, intervention => {
            var dateIntervention = moment(new Date(intervention.dateCreation), 'YYYY-MM-DD[T]HH:mm:ss[Z]').format('DD/MM/YYYY' + ' - ' + 'HH:mm')
            criseInterventions.push({
              text: intervention.libelleType + ' (' + dateIntervention + ')',
              value: intervention.id
            })
          })
          this.interventionAssocs = criseInterventions
        }
      }).catch(function(error) {
        console.error('interventions', error)
      })
    }
  }
}
</script>

<style scoped>
>>>input {
  width: 100%;
}

.mt-3 {
  display: -webkit-box;
}

>>>button.Rate__star {
  color: #fff;
}

.mt-3 img {
  margin-right: 10px;
}
.modal-footer {
  justify-content: center;
}
</style>
