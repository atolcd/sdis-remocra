<template>
  <div>
    <p class="card-text">Recherches et analyses</p>

    <b-form-group>
      <select v-model="selected" id="selectAnalyse" v-on:change="createFormFromSelect()" required>
          <option v-for="(value, key) in selectAnalyseOptions"  :data-description='value.dataDescription' :value='value.valeur'>
            {{value.libelle}}
          </option>
      </select>
    </b-form-group>
    <hr />
    <div>
      <form id="formParameters" class='needs-validation'>

        <p v-if="parametres.length > 0">Veuillez renseigner les paramètres suivants :</p>
        <p v-else-if="selected">Aucun paramètre pour cette requête</p>

        <div v-for='(item, index) in parametres' :key="`${index}-${item.id}`">

            <b-form-group v-if='item.formulaireTypeControle=="combo"' vertical :label='item.formulaireEtiquette' :label-for="item.nom">
              <select :idInput="item.nom" :required='item.obligatoire' class="parametreRequete">
                  <option v-for="(value, key) in comboOptions" v-if="value.nomChamp == item.nom" :value='value.valeur' :selected='value.valeur==value.formulaireValeurDefaut'>
                    {{value.libelle}}
                  </option>
              </select>
            </b-form-group>

            <b-form-group v-if='item.formulaireTypeControle=="autocomplete"' :id="'input'+item.id" :required = "item.obligatoire" class="parametreRequete" inputType='autocomplete' :idInput='item.nom' vertical :label='item.formulaireEtiquette' :label-for="'input'+item.id">
              <search-process-param :ref="'searchinput'+item.id" :paramId="item.id" :searchInput='item.formulaireValeurDefaut' queryURL='remocra/requetemodele/reqmodparalst/'></search-process-param>
            </b-form-group>

            <b-form-group v-if='item.formulaireTypeControle=="checkbox"' vertical :label='item.formulaireEtiquette' :label-for="item.nom">
              <input type='checkbox' :idInput='item.nom' class='parametreRequete' :value='checkboxChecked[item.nom]' v-model='checkboxChecked[item.nom]' />
            </b-form-group>

            <b-form-group v-if='item.formulaireTypeControle=="textfield"' vertical :label='item.formulaireEtiquette' :label-for="item.nom">
              <input type='text' :idInput='item.nom' class='parametreRequete' :value='item.formulaireValeurDefaut' :required='item.obligatoire' />
            </b-form-group>

            <b-form-group v-if='item.formulaireTypeControle=="numberfield"' vertical :label='item.formulaireEtiquette' :label-for="item.nom">
              <input type="number" :idInput='item.nom' class='parametreRequete' :value='item.formulaireValeurDefaut' :required='item.obligatoire' :step='(item.typeValeur=="integer")?1:0.001' />
            </b-form-group>

            <b-form-group v-if='item.formulaireTypeControle=="datefield"' vertical :label='item.formulaireEtiquette' :label-for="item.nom">
              <input type='date' :idInput='item.nom' class='parametreRequete' :value='item.formulaireValeurDefaut' :required='item.obligatoire' />
            </b-form-group>

            <b-form-group v-if='item.formulaireTypeControle=="timefield"' vertical :label='item.formulaireEtiquette' :label-for="item.nom">
              <input type='time' :idInput='item.nom' class='parametreRequete' :value='item.formulaireValeurDefaut' :required='item.obligatoire' step='1' />
            </b-form-group>

            <b-form-group v-if='item.formulaireTypeControle=="datetimefield"' :id='item.nom' vertical :label='item.formulaireEtiquette' inputType='datetimefield' class='parametreRequete' :label-for="item.nom">
              <input type='date' :idInput='item.nom.concat("_date")' :value='(item.formulaireValeurDefaut!==null) ? item.formulaireValeurDefaut.split(" ")[0] : "" ' :required='item.obligatoire' />
              <input type='time' :idInput='item.nom.concat("_time")' :value='(item.formulaireValeurDefaut!==null) ? item.formulaireValeurDefaut.split(" ")[1] : "" ' :required='item.obligatoire' step='1' />
            </b-form-group>
        </div>
        <div v-for="(param, index) in parametresGeometry" :key="index">
             <b-form-group vertical :label='param.formulaireEtiquette' :label-for="'input'+index">
               <input :ref = "'input'+index" :id="'input'+index" type="text" :idInput='param.nom' class='parametreRequete' readonly hidden />
               <a :class="['geom-'+param.formulaireTypeControle.toLowerCase()]" href="#" @click="selectGeom(param.formulaireTypeControle,index)"></a>
               <a style="cursor:pointer" class="modif" href="#" @click="modifGeom(index)"></a>
               <a style="cursor:pointer" class="delete" href="#" @click="deleteGeom(index)"></a>
             <b-button-group  v-if="showValidGeom === index">
               <b-btn class="ok-cancel-btns" @click="validGeom(index)">Valider</b-btn>
               <b-btn class="ok-cancel-btns" @click="annulGeom(index)">Annuler</b-btn>
             </b-button-group>
            </b-form-group>
        </div>
        <br />
        <input type="submit" id='formExecuteButton' v-on:click.self="createRequest" value="Exécuter"/>
      </form>
    </div>
  </div>
</template>

<script>

import Autocomplete from 'v-autocomplete'
import axios from 'axios'
import EventBus from '../bus'
import * as eventTypes from '../bus/event-types.js'
import _ from 'lodash'
import SearchProcessParam from './SearchProcessParam.vue'

export default {
  name: 'RechercheAnalyse',
  components: {
    Autocomplete,
    SearchProcessParam
  },

  data() {
    return{
      selected: null,
      parametres: [],
      parametresGeometry: [],
      comboOptions: [],
      selectAnalyseOptions: [],
      nombreParametres: -1,
      checkboxChecked: [],
      toggle: null,
      geom: null,
      showValidGeom: null,
    }

  },

  mounted() {
    this.getSelectAnalyseItems();
  },

  methods: {

    //Select de recherches et analyses
    getSelectAnalyseItems() {
      var self = this;
      this.selectAnalyseOptions = [];
      axios.get('/remocra/requetemodele.json?filter=[{"property":"categorie","value":"CRISE"}]').then((response) => {
        _.forEach(response.data.data, function(item){
          var o = {
            valeur: item.id,
            dataDescription: item.description,
            libelle: item.libelle
          }
          self.selectAnalyseOptions.push(o);
        })
      })
      .catch(function(error) {
        console.error('requeteModele', error);
      })
    },

    //Création du formulaire selon le choix effectué
    createFormFromSelect(){
      var self = this;
      axios.get('/remocra/requetemodele/requetemodelparam/'+this.selected).then((response) => {
        //On trie les paramètres dans l'ordre
        this.parametres = response.data.data.sort(function(a, b){
          return a.formulaireNumOrdre > b.formulaireNumOrdre;
        });

        self.comboOptions = [];
        self.parametresGeometry = [];
        _.forEach(this.parametres, function(item){

          if(item.typeValeur == 'geometry'){
            self.parametresGeometry.push(item);
          }

          //Si c'est une combo, on récupère toutes les valeurs
          if(item.formulaireTypeControle == 'combo')
          {
            //Récupération des valeurs de la comboBox
            axios.get('remocra/requetemodele/reqmodparalst/'+item.id).then((response) => {
              self.comboOptions[item.nom] = [];
              _.forEach(response.data.data, function(option){
                var o = {
                  nomChamp: item.nom,
                  valeur: option[item.sourceSqlValeur],
                  libelle: option[item.sourceSqlLibelle],
                  obligatoire: item.obligatoire,
                  formulaireValeurDefaut: item.formulaireValeurDefaut
                };
                self.comboOptions.push(o);
              });

            })
            .catch(function(error) {
              console.error('Combo', error)
            })
          }

          else if(item.formulaireTypeControle == 'checkbox')
          {
            self.$set(self.checkboxChecked, item.nom, (item.formulaireValeurDefaut == 'true'));
          }
        });
      })
      .catch(function(error) {
        console.error('requeteModele', error);
      });

    },

    createRequest(){
      var self = this;

      if(document.getElementById('formParameters').checkValidity()){
        var valParams = [];

        _.forEach(document.getElementsByClassName("parametreRequete"), function(item){
          var param = {};
          if(item.getAttribute("inputType")=='datetimefield'){
            param["nomparametre"] = item.id;
            param["valeur"] = document.querySelector("input[idInput='"+item.id+"_date']").value+" "+document.querySelector("input[idInput='"+item.id+"_time']").value;
          }
          else if(item.getAttribute("inputType")=='autocomplete'){
            var autocomplete = self.$refs['search' + item.id][0]
            param["nomparametre"] = item.getAttribute("idinput");
            param["valeur"] = autocomplete.selected !== null ? autocomplete.selected.id : autocomplete.searchInput;
          }
          else{
            param["nomparametre"] = item.getAttribute("idInput");
            param["valeur"] = item.value;
          }

          valParams.push(param);
        });
        console.log(valParams);
        this.executeRequest(this.selected, valParams);
      }
    },

    //Clic sur le bouton Exécuter
    executeRequest(idRequeteModele, valParams){
      //Envoi des données
      axios.post('/remocra/requetemodele/'+idRequeteModele+'?jsonValeurs='+JSON.stringify(valParams)+'').then((response) => {
        var idSelection = JSON.parse(response.data.message)[0].requete;
        this.retrieveData(idSelection);
      })
      .catch(function(error) {
        console.error('requeteModele', error);
      })
    },

    //Récupération des données
    retrieveData(idSelection)
    {
      //TODO: Gérer la récupération des données de la requête (tableau et carte)
      console.log('http://localhost:8080/remocra/requetemodele/reqmodresult/'+idSelection);
    },

    selectGeom(typeGeom,index){
      typeGeom = _.replace(typeGeom, 'geometryfield', ''),
      EventBus.$emit(eventTypes.INPUT_GEOM, {'typeGeom':typeGeom, 'index': index})
    },
    annulGeom(index){
      EventBus.$emit(eventTypes.ANNULE_INPUTGEOM, index)
    },
    validGeom(index){
      EventBus.$emit(eventTypes.VALIDE_INPUTGEOM, index)
    },
    modifGeom(index){
      EventBus.$emit(eventTypes.MODIFY_INPUTGEOM, index)
    },
    deleteGeom(index){
      EventBus.$emit(eventTypes.DELETE_INPUTGEOM, index)
    },

  }
}

</script>

<style scoped>
.inputLibelle{
  margin-bottom: 2px;
  margin-top: 5px
}

.geom-pointgeometryfield:before {
    content: url("/static/img/pencil_point.png");
    margin-right: 7px;
    cursor:pointer;
}
.geom-linestringgeometryfield:before {
    content:url('/static/img/pencil_ligne.png');
    margin-right: 7px;
    cursor:pointer;
}
.geom-polygongeometryfield:before {
    content:url('/static/img/pencil_polygone.png');
    margin-right: 7px;
    cursor:pointer;
}
.geom-circlegeometryfield:before {
    content:url('/static/img/pencil_circle.png');
    margin-right: 7px;
    cursor:pointer;
}
.geom-boxgeometryfield:before {
    content:url('/static/img/pencil_rectangle.png');
    margin-right: 7px;
    cursor:pointer;
}
.delete:before {
    content:url("/static/img/delete.png");
    margin-right: 7px;
    cursor:pointer;
}
.modif:before {
    content:url("/static/img/pencil.png");
    margin-right: 7px;
    cursor:pointer;
}
</style>
