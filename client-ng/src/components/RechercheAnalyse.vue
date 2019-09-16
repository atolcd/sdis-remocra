<template>
<div>
  <p class="card-text">Recherches et analyses</p>
  <b-form-group>
    <select v-model="selected" :id="'selectAnalyse'+criseId" v-on:change="createFormFromSelect()" required class="form-control">
      <option v-for="(value, key) in selectAnalyseOptions" :key="key" :data-description="value.dataDescription" :data-spatial="value.spatial" :value="value.valeur">
        {{ value.libelle }}
      </option>
    </select>
  </b-form-group>
  <div class="form-parameters">
    <form :id="'formParameters'+criseId" class="needs-validation" @submit="createRequest" @reset="resetRequest">
      <p v-if="parametres.length > 0"> Veuillez renseigner les paramètres suivants : </p>
      <p v-else-if="selected">Aucun paramètre pour cette requête</p>
      <hr />
      <div v-for="(item, index) in parametres" :key="`${index}-${item.id}`">
        <b-form-group v-if="item.formulaireTypeControle == 'combo'" vertical :label="item.formulaireEtiquette" :label-for="item.nom">
          <select :idInput="item.nom" :required="item.obligatoire" class="form-control parametreRequete">
            <option v-for="(value, key) in getOption(item.nom)" :key="key" :value="value.valeur" :selected="value.valeur == value.formulaireValeurDefaut">
              {{ value.libelle }}
            </option>
          </select>
        </b-form-group>
        <b-form-group v-if="item.formulaireTypeControle == 'autocomplete'" :id="'input' + item.id" :required="item.obligatoire" class="form-control parametreRequete" inputType="autocomplete" :idInput="item.nom" vertical
          :label="item.formulaireEtiquette" :label-for="'input' + item.id">
          <search-process-param :ref="'searchinput' + item.id" :paramId="item.id" :searchInput="item.formulaireValeurDefaut" queryURL="remocra/requetemodele/reqmodparalst/"></search-process-param>
        </b-form-group>
        <b-form-group v-if="item.formulaireTypeControle == 'checkbox'" vertical :label="item.formulaireEtiquette" :label-for="item.nom">
          <input type="checkbox" style="width:5%" :idInput="item.nom" class="form-control parametreRequete" :value="checkboxChecked[item.nom]" v-model="checkboxChecked[item.nom]" />
        </b-form-group>
        <b-form-group v-if="item.formulaireTypeControle == 'textfield'" vertical :label="item.formulaireEtiquette" :label-for="item.nom">
          <input type="text" :idInput="item.nom" class="form-control parametreRequete" :value="item.formulaireValeurDefaut" :required="item.obligatoire" />
        </b-form-group>
        <b-form-group v-if="item.formulaireTypeControle == 'numberfield'" vertical :label="item.formulaireEtiquette" :label-for="item.nom">
          <input type="number" :idInput="item.nom" class="form-control parametreRequete" :value="item.formulaireValeurDefaut" :required="item.obligatoire" :step="item.typeValeur == 'integer' ? 1 : 0.001" />
        </b-form-group>
        <b-form-group v-if="item.formulaireTypeControle == 'datefield'" vertical :label="item.formulaireEtiquette" :label-for="item.nom">
          <input type="date" :idInput="item.nom" class="form-control parametreRequete" :value="item.formulaireValeurDefaut" :required="item.obligatoire" />
        </b-form-group>
        <b-form-group v-if="item.formulaireTypeControle == 'timefield'" vertical :label="item.formulaireEtiquette" :label-for="item.nom">
          <input type="time" :idInput="item.nom" class="form-control parametreRequete" :value="item.formulaireValeurDefaut" :required="item.obligatoire" step="1" />
        </b-form-group>
        <b-form-group v-if="item.formulaireTypeControle == 'datetimefield'" :id="item.nom" vertical :label="item.formulaireEtiquette" inputType="datetimefield" class="parametreRequete" :label-for="item.nom">
          <input type="date" :idInput="item.nom.concat('_date')" :value="
                item.formulaireValeurDefaut !== null
                  ? item.formulaireValeurDefaut.split(' ')[0]
                  : ''
              " :required="item.obligatoire" class="form-control" />
          <input type="time" :idInput="item.nom.concat('_time')" :value="
                item.formulaireValeurDefaut !== null
                  ? item.formulaireValeurDefaut.split(' ')[1]
                  : ''
              " :required="item.obligatoire" step="1" class="form-control" />
        </b-form-group>
      </div>
      <div>
        <div v-for="(param, index) in parametresGeometry" :key="index">
          <b-form-group class='recherchegeom' vertical :label="param.formulaireEtiquette" :label-for="'input' + index">
            <input :ref="'input' + index" :id="'input' + index" type="text" :idInput="param.nom" class="parametreRequete" readonly hidden />
            <a title="Dessiner" :id="'geom' + index" :class="['geom-' + param.formulaireTypeControle.toLowerCase()]" href="#" @click="selectGeom($event, param.formulaireTypeControle, index)"></a>
            <a title="Modifier la géométrie" :id="'modif' + index" class="modif" href="#" @click="modifGeom($event, index)"></a>
            <a title="Supprimer la géométrie" class="delete" href="#" @click="deleteGeom($event, index)"></a>
            <b-button-group size="sm" class="validation-geom" v-if="showValidGeom === index">
              <b-btn class="ok-cancel-btns" @click="validGeom(index)">Valider</b-btn>
              <b-btn class="ok-cancel-btns" @click="annulGeom(index)">Annuler</b-btn>
            </b-button-group>
          </b-form-group>
        </div>
      </div>
      <br />
      <div class="modal-footer">
        <b-button size="sm" type="submit" variant="primary">Exécuter</b-button>
        <b-button size="sm" type="reset" variant="secondary">Réinitialiser</b-button>
      </div>
    </form>
  </div>
</div>
</template>

<script>
import axios from 'axios'
import * as eventTypes from '../bus/event-types.js'
import _ from 'lodash'
import SearchProcessParam from './SearchProcessParam.vue'
export default {
  name: 'RechercheAnalyse',
  components: {
    SearchProcessParam
  },
  props: {
    criseId: {
      required: true,
      type: Number
    }
  },
  data() {
    return {
      selected: null,
      parametres: [],
      parametresGeometry: [],
      comboOptions: [],
      selectAnalyseOptions: [],
      nombreParametres: -1,
      checkboxChecked: [],
      toggle: null,
      geom: null,
      showValidGeom: null
    }
  },
  mounted() {
    this.getSelectAnalyseItems()
  },
  methods: {
    // Select de recherches et analyses
    getSelectAnalyseItems() {
      var self = this
      this.selectAnalyseOptions = []
      var jsonFilters = JSON.stringify([{
        "property": "categorie",
        "value": "GESTION_CRISE"
      }])
      axios.get('/remocra/requetemodele', {
        params: {
          filter: jsonFilters
        }
      }).then(response => {
        _.forEach(response.data.data, function(item) {
          var o = {
            valeur: item.id,
            dataDescription: item.description,
            libelle: item.libelle,
            spatial: item.spatial
          }
          self.selectAnalyseOptions.push(o)
        })
      }).catch(function(error) {
        console.error('requeteModele', error)
      })
    },
    // Création du formulaire selon le choix effectué
    createFormFromSelect() {
      var self = this
      axios.get('/remocra/requetemodele/requetemodelparam/' + this.selected).then(response => {
        // On trie les paramètres dans l'ordre
        this.parametres = response.data.data.sort(function(a, b) {
          return a.formulaireNumOrdre > b.formulaireNumOrdre
        })
        self.comboOptions = []
        self.parametresGeometry = []
        _.forEach(this.parametres, function(item) {
          if (item.typeValeur === 'geometry') {
            self.parametresGeometry.push(item)
          }
          // Si c'est une combo, on récupère toutes les valeurs
          if (item.formulaireTypeControle === 'combo') {
            // Récupération des valeurs de la comboBox
            axios.get('/remocra/requetemodele/reqmodparalst/' + item.id).then(response => {
              self.comboOptions[item.nom] = []
              _.forEach(response.data.data, function(option) {
                var o = {
                  nomChamp: item.nom,
                  valeur: option[item.sourceSqlValeur],
                  libelle: option[item.sourceSqlLibelle],
                  obligatoire: item.obligatoire,
                  formulaireValeurDefaut: item.formulaireValeurDefaut
                }
                self.comboOptions.push(o)
              })
            }).catch(function(error) {
              console.error('Combo', error)
            })
          } else if (item.formulaireTypeControle === 'checkbox') {
            self.$set(self.checkboxChecked, item.nom, item.formulaireValeurDefaut === 'true')
          }
        })
      }).catch(function(error) {
        console.error('requeteModele', error)
      })
    },
    createRequest(evt) {
      var self = this
      if (document.getElementById('formParameters' + this.criseId).checkValidity()) {
        var valParams = []
        _.forEach(evt.target.getElementsByClassName('parametreRequete'), function(item) {
          var param = {}
          if (item.getAttribute('inputType') === 'datetimefield') {
            param['nomparametre'] = item.id
            param['valeur'] = document.querySelector("input[idInput='" + item.id + "_date']").value + ' ' + document.querySelector("input[idInput='" + item.id + "_time']").value
          } else if (item.getAttribute('inputType') === 'autocomplete') {
            var autocomplete = self.$refs['search' + item.id][0]
            param['nomparametre'] = item.getAttribute('idinput')
            param['valeur'] = autocomplete.selected !== null ? autocomplete.selected.id : autocomplete.searchInput
          } else {
            param['nomparametre'] = item.getAttribute('idInput')
            param['valeur'] = item.value
          }
          valParams.push(param)
        })
        this.executeRequest(this.selected, valParams)
      }
      if (evt) {
        evt.preventDefault()
        evt.stopPropagation()
      }
    },
    resetRequest() {
      this.selected = null
      this.parametres = []
      this.parametresGeometry = []
      this.comboOptions = []
      this.nombreParametres = -1
      this.checkboxChecked = []
      this.toggle = null
      this.geom = null
      this.showValidGeom = null
      this.$root.$options.bus.$emit(eventTypes.TOGGLE_TABDONNEES)
    },
    // Clic sur le bouton Exécuter
    executeRequest(idRequeteModele, valParams) {
      // Envoi des données
      let formData = new FormData()
      formData.append('jsonValeurs', JSON.stringify(valParams))
      axios.post('/remocra/requetemodele/' + idRequeteModele, formData).then(response => {
        if (response.data.message) {
          this.retrieveData(JSON.parse(response.data.message))
        }
      }).catch(function(error) {
        console.error('requeteModele', error)
      })
    },
    // Récupération des données
    retrieveData(header) {
      var idSelection = header[0].requete
      axios.get('/remocra/requetemodele/reqmodresult/' + idSelection).then(response => {
        if (response.data.data && response.data.data.length !== 0) {
          var spatial = document.getElementById('selectAnalyse' + this.criseId).options[document.getElementById('selectAnalyse' + this.criseId).selectedIndex].getAttribute('data-spatial')
          this.$root.$options.bus.$emit(eventTypes.RESEARCH_TABDONNEES, header, response.data.data, spatial, idSelection)
        } else {
          this.$notify({
            group: 'remocra',
            title: 'Recherche et analyse',
            type: 'error',
            text: "Aucune donnée n'est disponible pour cette séléction."
          })
        }
      }).catch(function(error) {
        console.error('Retrieving data ', error)
      })
    },
    selectGeom(evt, typeGeom, index) {
      document.getElementById('geom' + index).classList.toggle('active')
      document.getElementById('modif' + index).classList.remove('active')
      typeGeom = _.replace(typeGeom, 'geometryfield', '')
      this.$root.$options.bus.$emit(eventTypes.INPUT_GEOM, {
        typeGeom: typeGeom,
        index: index
      })
      if (evt) {
        evt.preventDefault()
        evt.stopPropagation()
      }
    },
    annulGeom(index) {
      document.getElementById('geom' + index).classList.remove('active')
      document.getElementById('modif' + index).classList.remove('active')
      this.$root.$options.bus.$emit(eventTypes.ANNULE_INPUTGEOM, index)
    },
    validGeom(index) {
      document.getElementById('geom' + index).classList.remove('active')
      document.getElementById('modif' + index).classList.remove('active')
      this.$root.$options.bus.$emit(eventTypes.VALIDE_INPUTGEOM, index)
    },
    modifGeom(evt, index) {
      document.getElementById('geom' + index).classList.remove('active')
      document.getElementById('modif' + index).classList.toggle('active')
      this.$root.$options.bus.$emit(eventTypes.MODIFY_INPUTGEOM, index)
      // on arrete la propagation de l'évenement pour que le router ne prends pas en compte le href#
      if (evt) {
        evt.preventDefault()
        evt.stopPropagation()
      }
    },
    deleteGeom(evt, index) {
      document.getElementById('geom' + index).classList.remove('active')
      document.getElementById('modif' + index).classList.remove('active')
      this.$root.$options.bus.$emit(eventTypes.DELETE_INPUTGEOM, index)
      if (evt) {
        evt.preventDefault()
        evt.stopPropagation()
      }
    },
    getOption: function(nom) {
      console.log(nom)
      return this.comboOptions.filter(function(value) {
        return value.nomChamp === nom
      })
    }
  }
}
</script>

<style scoped>
.inputLibelle {
  margin-bottom: 2px;
  margin-top: 5px;
}

.geom-pointgeometryfield {
  content: url('/remocra/static/img/pencil_point.png');
  cursor: pointer;
}

.geom-pointgeometryfield.active {
  background-color: #bbbbbb;
  border-color: #9d9d9d;
  border: 2px solid #9d9d9d;
  border-radius: 3px;
}

.geom-linestringgeometryfield {
  content: url('/remocra/static/img/pencil_ligne.png');
  cursor: pointer;
}

.geom-linestringgeometryfield.active {
  background-color: #bbbbbb;
  border-color: #9d9d9d;
  border: 2px solid #9d9d9d;
  border-radius: 3px;
}

.geom-polygongeometryfield {
  content: url('/remocra/static/img/pencil_polygone.png');
  cursor: pointer;
}

.geom-polygongeometryfield.active {
  background-color: #bbbbbb;
  border-color: #9d9d9d;
  border: 2px solid #9d9d9d;
  border-radius: 3px;
}

.geom-circlegeometryfield {
  content: url('/remocra/static/img/pencil_circle.png');
  cursor: pointer;
}

.geom-circlegeometryfield.active {
  background-color: #bbbbbb;
  border-color: #9d9d9d;
  border: 2px solid #9d9d9d;
  border-radius: 3px;
}

.geom-boxgeometryfield {
  content: url('/remocra/static/img/pencil_rectangle.png');
  cursor: pointer;
}

.geom-boxgeometryfield.active {
  background-color: #bbbbbb;
  border-color: #9d9d9d;
  border: 2px solid #9d9d9d;
  border-radius: 3px;
}

.delete {
  content: url('/remocra/static/img/delete.png');
  margin-left: 20px;
  cursor: pointer;
}

.delete.active {
  background-color: #bbbbbb;
  border-color: #9d9d9d;
  border: 2px solid #9d9d9d;
  border-radius: 3px;
}

.modif {
  content: url('/remocra/static/img/pencil.png');
  margin-left: 20px;
  cursor: pointer;
}

.modif.active {
  background-color: #bbbbbb;
  border-color: #9d9d9d;
  border: 2px solid #9d9d9d;
  border-radius: 3px;
}

.recherchegeom {
  background-color: #e9ecef;
  text-indent: 5px;
}

.validation-geom {
  display: flex;
}
</style>
