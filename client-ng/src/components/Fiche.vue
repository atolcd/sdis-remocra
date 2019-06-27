<template>
  <div class='Fiche'>
    <form id='formFiche' name='fiche' enctype="multipart/form-data" method="POST" ref="formFiche">
<!-- ================================== En-tête du formulaire ==================================-->
      <div id="entete" class="form-group">
        <div class="row">
          <div class="col-md-3">
            <b-form-group invalid-feedback="Le numéro du PEI est manquant" 
                          :state="etats.numeroInterne"
                          label="Numéro interne"
                          label-for="numeroInterne"
                          label-cols-md="6">
              <b-form-input type="text" 
                            id="numeroInterne" 
                            v-model="hydrant.numeroInterne" 
                            :disabled="!idHydrant"
                            class="parametre" 
                            size="sm"
                            :state="etats.numeroInterne">
              </b-form-input>
            </b-form-group>
          </div>

          <div class="col-md-3">
            <b-form-group label="Nature" label-for="nature" invalid-feedback="La nature doit être renseignée" :state="etats.nature" label-cols-md="4">
              <b-form-select id="nature" v-model="hydrant.nature" class="parametre" :options="comboType" size="sm" :state="etats.nature" v-on:change="onNatureChange" required ></b-form-select>
            </b-form-group>
          </div>

          <div class="col-md-6">
            <b-form-group label="Autorité de police DECI" label-for="autoriteDeci" invalid-feedback="L'autorité de police DECI doit être renseignée" :state="etats.autoriteDeci" label-cols-md="5">
              <b-form-select id="autoriteDeci" v-model="hydrant.autoriteDeci" class="parametre" :options="comboAutoriteDeci" size="sm" :state="etats.autoriteDeci" required></b-form-select>
            </b-form-group>
          </div>
        </div>

        <div class="row">
          <div class="col-md-4">
            <b-form-group label="Type de DECI"
                          label-for="nature_deci" 
                          invalid-feedback="La nature DECI doit être renseignée" 
                          :state="etats.natureDeci"
                          label-cols-md="4">
              <b-form-select v-model="hydrant.natureDeci" :options="comboDeci" size="sm" id="natureDeci" class="parametre" v-on:change="getComboGestionnaire" :state="etats.natureDeci"></b-form-select>
            </b-form-group>
          </div>
 
          <div class="col-md-5">
            <b-form-group label="Gestionnaire" label-for="gestionnaire" invalid-feedback="Le gestionnaire doit être renseigné" :state="etats.gestionnaire" label-cols-md="3">
              <b-form-select  id="gestionnaire"
                              v-model="hydrant.gestionnaire" 
                              class="parametre" 
                              :options="comboGestionnaire" 
                              size="sm" 
                              v-on:change="onGestionnaireChange"
                              :state="etats.gestionnaire"
                              required>
              </b-form-select>
              <button class="btnInlineForm btn btn-sm btn-outline-success" @click.prevent v-b-modal.modal-gestionnaire v-if="utilisateurDroits.indexOf('HYDRANTS_GESTIONNAIRE_C') != -1">
                <img src="../assets/img/pencil.png">
              </button>
            </b-form-group>
          </div>

          <div class="col-md-3">
            <b-form-group label="Site" label-for="site" label-cols-md="4">
              <b-form-select id="site" v-model="hydrant.site" class="parametre" :options="comboSite" size="sm"></b-form-select>
            </b-form-group>
          </div>
        </div>

      </div>

      <div>
        <b-tabs fill content-class="mt-3" active-nav-item-class="text-primary">
          <b-tab title="Résumé">
            <div>Fonctionnalité à venir.</div>
          </b-tab>

          <!-- ================================== Onglet Localisation ==================================-->
          <b-tab active>
            <template slot="title">
                    Localisation <b-badge pill variant="danger" v-if="tabWarning.localisation">!</b-badge>
            </template>
            <FicheLocalisation  :hydrant="hydrant"
                                :utilisateurDroits="utilisateurDroits" 
                                :geometrie="geometrie"
                                v-if="dataLoaded"
                                @getComboData="getComboData"
                                @resolveForeignKey="resolveForeignKey"
                                ref="ficheLocalisation">
            </FicheLocalisation>
          </b-tab>

          <!-- ================================== Onglet Caractéristiques techniques ==================================-->
          <b-tab>
            <template slot="title">
                    Caractéristiques techniques <b-badge pill variant="danger" v-if="tabWarning.caracteristiquesTechniques">!</b-badge>
            </template>
            <FicheCaracteristiquesPibi  :hydrant="hydrant"
                                        :hydrantRecord="hydrantRecord"
                                        :listeNaturesDeci="listeNaturesDeci"
                                        :geometrie="geometrie"
                                        @getComboData="getComboData"
                                        @resolveForeignKey="resolveForeignKey"
                                        ref="fichePibi"
                                        v-if="hydrant.code=='PIBI' && dataLoaded">
            </FicheCaracteristiquesPibi>
            <FicheCaracteristiquesPena  :hydrant="hydrant"
                                        :hydrantRecord="hydrantRecord"
                                        @getComboData="getComboData"
                                        @resolveForeignKey="resolveForeignKey"
                                        ref="fichePena"
                                        v-if="hydrant.code=='PENA' && dataLoaded">
            </FicheCaracteristiquesPena>
          </b-tab>

          <!-- ================================== Onglet Visites ==================================-->
          <b-tab ref="visitesTab">
            <template slot="title">
                    Visites <b-badge pill variant="danger" v-if="tabWarning.visites">!</b-badge>
            </template>
            <FicheVisite  :hydrant="hydrant"
                          :utilisateurDroits="utilisateurDroits"
                          @getComboData="getComboData"
                          @resolveForeignKey="resolveForeignKey"
                          ref="ficheVisite"
                          v-if="dataLoaded">
            </FicheVisite>
          </b-tab>

          <!-- ================================== Onglet Documents ==================================-->
          <b-tab title="Documents">
            <FicheDocument  ref="ficheDocument"
                            :hydrant="hydrant"
                            v-if="dataLoaded">
            </FicheDocument>
          </b-tab>
        </b-tabs>
      </div>

    </form>

    <ModalGestionnaire v-on:modalGestionnaireValues="onGestionnaireCreated"></ModalGestionnaire>

  </div>
</template>

<script>

import axios from 'axios'
import { loadProgressBar } from 'axios-progress-bar'
import _ from 'lodash'
import ModalGestionnaire from './ModalGestionnaire.vue'
import FicheLocalisation from './FicheLocalisation.vue'
import FicheCaracteristiquesPibi from './FicheCaracteristiquesPibi.vue'
import FicheCaracteristiquesPena from './FicheCaracteristiquesPena.vue'
import FicheVisite from './FicheVisite.vue'
import FicheDocument from './FicheDocument.vue'


export default {
  name: 'Fiche',

  components: {
    ModalGestionnaire,
    FicheLocalisation,
    FicheCaracteristiquesPibi,
    FicheCaracteristiquesPena,
    FicheVisite,
    FicheDocument
  },

  data() {
    return {
      hydrantRecord: {}, // Données initiales du PEI
      hydrant: {}, // Données actuelles du PEI 
      utilisateurDroits: [],
      dataLoaded: false,
      

      //ComboBox
      comboType: [],
      comboDeci: [],
      comboGestionnaire: [],
      comboSite: [],
      comboAutoriteDeci: [],

      listeNaturesDeci: [],
      tabWarning : {
        localisation: false,
        caracteristiquesTechniques: false,
        visites: false,
      },

      // Etat des champs du formulaire pour la validation
      etats: {
        numeroInterne: null,
        gestionnaire: null,
        nature: null,
        autoriteDeci: null,
        natureDeci: null
      }
    }
  },
  
  props: {
    newVisite: {
        type: Boolean,
        default: false
    },

    idHydrant: {
      type: Number,
      required: false
    },

    codeHydrant: {
      type: String,
      required: true
    },

    geometrie: {
      type: String,
      required: true
    }
  },

  mounted: function(){
    loadProgressBar({parent: "#formFiche", showSpinner: false})
    var self = this;
    // Récupération des droits de l'utilisateur courant
    axios.get('/remocra/utilisateurs/current/xml').then(response => {
      var xmlDoc = (new DOMParser()).parseFromString(response.data,"text/xml");
      self.utilisateurDroits = [];
      _.forEach(xmlDoc.getElementsByTagName("right"), function(item){
          self.utilisateurDroits.push(item.getAttribute("code"));
      });
    }).then(function() {
      if(self.idHydrant) {
        self.modification()
      } else {
        self.creation()
      }
    });
  },

  methods: {

    creation() {
      let self = this
      self.hydrantRecord = {
         id: null,
         nature: null,
         gestionnaire: null,
         site: null,
         autoriteDeci: null,
         natureDeci: null,
         code: self.codeHydrant
      }

      self.hydrant = _.clone(self.hydrantRecord);
      self.dataLoaded = true;
      self.createCombo();
    },
    modification() {
      let self = this
      axios.get('/remocra/hydrants'+self.codeHydrant.toLowerCase()+'/'+self.idHydrant+'.json?&id='+self.idHydrant).then(response => {
        if (response.data.data && response.data.data.length !== 0) {

          self.hydrantRecord = response.data.data;

          //Résolution des clés étrangères
          self.hydrant = _.clone(self.hydrantRecord, true);
          self.dataLoaded = true;
          self.resolveForeignKey(['nature', 'site', 'autoriteDeci', 'natureDeci']);

          self.createCombo();

          if (self.newVisite===true) {
            self.$root.$options.bus.$on('pei_visite_ready', () => {
              self.$refs.visitesTab.activate()
              self.$refs.ficheVisite.createVisite()
            })
          }
        }
      }).catch(function(error) {
        console.error('Retrieving data ', error)
      })
    },

    // Résolution des clés étrangères
    resolveForeignKey(clesEtrangeres){
      _.forEach(clesEtrangeres, function(item){
        this.hydrant[item] = (this.hydrantRecord[item]) ? _.clone(this.hydrantRecord[item].id) : null;
      }.bind(this));
    },

    // Fonction récupérant les données nécessaire aux combos du formulaire
    createCombo() {

      this.getComboData(this, 'comboType', '/remocra/typehydrantnatures.json', {
        "filter": JSON.stringify([{"property":"typeHydrantCode","value":this.codeHydrant}])
        }, 'id', 'nom');
      this.getComboData(this, 'comboDeci', '/remocra/typehydrantnaturedeci.json', null, 'id', 'nom');

      //Combo de l'autorité DECI: on modifie le texte affiché en fonction du type d'organisme
      var self = this;
      axios.get('/remocra/organismes/autoritepolicedeci.json?geometrie='+this.geometrie).then(response => {
        _.forEach(response.data, function(item) {
          var libelle;
          switch(item['typeOrganisme']){

            case 'COMMUNE' :
              libelle = "Maire de "+item['nom'];
              break;

            case 'EPCI' : 
              libelle = "Président de "+item['nom'];
              break;

            case 'PREFECTURE' :
              libelle = "Préfet";
              break;

            default:
              libelle = item['nom'];
          }
          self['comboAutoriteDeci'].push({
            text: libelle,
            value: item['id']
          });
        });
      }).then(axios.get('/remocra/typehydrantnaturedeci').then(response => { // Récupération des données de nature DECI
          self.listeNaturesDeci = response.data.data;
          self.getComboGestionnaire();
      })).catch(function(error) {
        console.error('Retrieving combo data from /remocra/organismes/autoritepolicedeci/', error);
      })
    },


    /**
      * Récupère les données des combobox
      * @param attribut L'attribut du composant qui contiendra les données (on ne peut pas utiliser async/await par souci de compatibilité, et on évite l'enchaînement de callback)
      * @param url L'url remocra permettant de récupérer les données
      * @param champValeur Indique parmis les données récupérées le champ de valeur 
      * @param champTexte Indique parmis les données récupérées le champ texte à afficher dans la combo
      * @param optionVide Si indiqué, ajoute en première position une option vide valant null avec un texte configurable
      */
    getComboData(context, attribut, url, params={}, champValeur, champTexte, optionVide){
      context[attribut] = [];
      let allParams = _.defaults(params, {"sort":JSON.stringify([{"property":champTexte,"direction":"ASC"}])})
      axios.get(url, {params: allParams}).then(response => {
        _.forEach(response.data.data, function(item) {
          context[attribut].push({
            text: item[champTexte],
            value: item[champValeur]
          });
        });

        if(optionVide){
          context[attribut].unshift({
            text: optionVide,
            value: null
          });
        }
      }).catch(function(error) {
        console.error('Retrieving combo data from '+url, error);
      })
    },

    getComboGestionnaire(){

      var idDeciPrive = this.listeNaturesDeci.filter(item => item.code === "PRIVE")[0].id;
      this.hydrant.site = null;

      // En cas de changement de nature (passage de PUBLIC/CONVENTIONNE à PRIVE et inversement), on set le gestionnaire à null
      // De cette manière, si on passe de PUBLIC à CONVENTIONNE ou inversement, on laisse tel quel
      if(this.hydrantRecord.natureDeci && (this.hydrant.natureDeci == idDeciPrive) != (this.hydrantRecord.natureDeci.id == idDeciPrive)){
        this.hydrant.gestionnaire = null;
      }
      
      //Si DECI privé, le gestionnaire est un privé
      if(this.hydrant.natureDeci == idDeciPrive){
        this.getComboData(this, 'comboGestionnaire', '/remocra/gestionnaire.json', null, 'id', 'nom', ' ');
        this.onGestionnaireChange();


      } else { // Si DECI publique ou conventionnée, le gestionnaire est un organisme de type COMMUNE ou EPCI
        var self = this;
        axios.get('/remocra/organismes/gestionnairepublic.json', {
          params: {
            geometrie:this.geometrie,
            sort:JSON.stringify([{"property":"nom","direction":"ASC"}])
          }
        }).then(response => {
          self.comboGestionnaire = [];
          _.forEach(response.data, function(nature){
            self.comboGestionnaire.push({
              text: nature.nom,
              value: nature.id
            })
          });
        }).then(self.onGestionnaireChange()).catch(function(error) {
          console.error('Retrieving combo data from /remocra/organismes/gestionnairepublic/'+this.geometrie, error);
        });
      }
    },

    /**
      * En cas de changement de nature DECI
      * On met à jour le gestionnaire et on transmet l'évènement aux caractéristiques du PIBI dont les champs dépendent de la nature
      */
    onNatureDeciChange() {
      this.getComboGestionnaire();

      if(this.$refs.fichePibi){
        this.$refs.fichePibi.onNatureDeciChange();
      }
      
    },

    onNatureChange(value) {
      if(this.$refs.fichePibi) {
        this.$refs.fichePibi.updateComboDiametres(this.comboType.filter(item => item.value === value)[0].text);
      }
    },

    /**
      * Appelée lorsque l'on créé un gestionnaire grâce à la modale
      * @param values Les données du gestionnaire créé (transmises par le composant ModalGestionnaire)
      */
    onGestionnaireCreated(values){
      this.comboGestionnaire.push({
            text: values.nom,
            value: values.id
      });
    },

    /**
      * En cas de changement de gestionnaire, on met à jour la liste des sites
      */
    onGestionnaireChange(){
      if(this.hydrant.gestionnaire != null){
        this.getComboData(this, 'comboSite', '/remocra/site.json', {
            "filter": JSON.stringify([{"property":"gestionnaire","value":this.hydrant.gestionnaire}]),
            "sort": JSON.stringify([{"property":"nom","direction":"ASC"}])
        }, 'id', 'nom', 'Aucun');
      }
      
      if(this.hydrantRecord.gestionnaire != this.hydrant.gestionnaire){
        this.hydrant.site = null;
      }
      else{
        this.hydrant.site = (this.hydrantRecord.site) ? this.hydrantRecord.site.id : null;
      }
    },

    
    /**
      * Valide ou non les données du formulaire de ce module ainsi que de ses modules enfants
      * La réponse est envoyée au serveur, qui fera appel à this.handleSubmit() pour procéder à l'envoi des données
      */ 
    checkFormValidity(){
        this.etats.numeroInterne = !this.idHydrant || (this.idHydrant && this.hydrant.numeroInterne.toString().length > 0) ? 'valid' : 'invalid';
        this.etats.gestionnaire = (this.hydrant.gestionnaire !== null) ? 'valid' : 'invalid';
        this.etats.nature = this.hydrant.nature? 'valid' : 'invalid';
        this.etats.autoriteDeci = (this.hydrant.autoriteDeci !== null) ? 'valid' : 'invalid';
        this.etats.natureDeci = (this.hydrant.natureDeci !== null) ? 'valid' : 'invalid';

        this.tabWarning = {
          localisation: false,
          caracteristiquesTechniques: false,
          visites: false
        };

        var isFormValid = true;

        if(this.hasInvalidState(this.etats)){
          isFormValid = false;
        }

        if(this.hasInvalidState(this.$refs.ficheLocalisation.checkFormValidity())){
          this.tabWarning.localisation = true;
          isFormValid = false;
        }

        if((this.$refs.fichePibi && this.hasInvalidState(this.$refs.fichePibi.checkFormValidity())) || (this.$refs.fichePena && this.hasInvalidState(this.$refs.fichePena.checkFormValidity()))){
          this.tabWarning.caracteristiquesTechniques = true;
          isFormValid = false;
        }

        if(this.hasInvalidState(this.$refs.ficheVisite.checkFormValidity())){
          this.tabWarning.visites = true;
          isFormValid = false;
        }

        return isFormValid;
    },

    /**
      * Vérifie si le formulaire a des champs invalides
      * En pratique, les modules vueJS enfants ce module renvoient leur état après un appel à leur fonction checkFormValidity()
      * @param etats L'objet "etats" des champs d'un module
      * @return TRUE si le champ présente des champs invalides, FALSE sinon
      */
    hasInvalidState(etats){
      var hasInvalidState = false;
      for(var key in etats){
        hasInvalidState = hasInvalidState || etats[key] == "invalid";
      }
      return hasInvalidState;
    },
 
    /**
      * Envoie les données au serveur afin de procéder à la mise à jour de l'hydrant
      * @param url L'url à contacter
      */
    handleSubmit(url){
      // Si la nature est passée de PRIVE a PUBLIC/CONVENTIONNE ou inversement, le PEI ne respecte plus la contrainte de nature DECI au sein de ses tournées
      // On le désaffecte donc pour tous les organismes
      var idDeciPrive = this.listeNaturesDeci.filter(item => item.code === "PRIVE")[0].id;
      if(this.hydrantRecord.natureDeci &&(this.hydrant.natureDeci == idDeciPrive) != (this.hydrantRecord.natureDeci.id == idDeciPrive)){
        axios.post('hydrants/desaffecter?json=['+this.hydrant.id+']&allOrganismes=true')
        .catch(function(error) {
          console.error('postEvent', error)
        });
      }

      // Récupération des données
      var data = {};
      _.forEach(document.getElementsByClassName('parametre'), item => {
        if(Array.from(item.classList).indexOf('custom-checkbox') > -1){ // Checkbox
          var element = item.getElementsByTagName('input')[0];
          data[element.id] = this.hydrant[element.id];
          
        } else if(item.type === "number"){ // Input de type number
          data[item.id] = (item.value === "") ? null : parseInt(item.value);

        } else {
          data[item.id] = item.value;
        }
      });

      data["geometrie"] = this.geometrie;

      if(!this.idHydrant){
        data["numeroInterne"] = null;
      }

      var formData = new FormData();

      //Ajout des fichiers
      var fichiers = this.$refs.ficheDocument.getFiles();
      for (var i = 0; i < fichiers.length; i++) {
        let file = fichiers[i].data;
        formData.append('files[' + i + ']', file)
      }
	var self = this
      axios.post('/remocra/hydrants/getUpdatedCoordonnees', this.$refs.ficheLocalisation.getLocalisationData()).then(function(response) { // Une fois la maj faite, on met à jour le reste des données
        data["geometrie"] = response.data.message;
        //On ajoute en plus les données de débit/pression issues des visites (si éligibles)
        data = _.merge(data, self.$refs.ficheVisite.updateDataFromLastVisite());

        formData.append("hydrant", JSON.stringify(data));

      }).then(function() { // On met à jour le PEI
        axios.post(url, formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }).then(function(response){ // Une fois le PEI mis à jour, on peut récupérer son id et mettre à jour les aspirations
            var id = response.data.data.id;
            let numero = response.data.data.numero;
            var requests = []
            if(id !== null) {
              if(self.$refs.fichePena){
                var aspirationData = self.$refs.fichePena.prepareAspirationData(id);
                requests.push(axios.post('/remocra/hydrantaspiration/updatemany', JSON.parse(aspirationData.aspirations)).catch(function(error) {
                  console.error('postEvent', error)
                }))
                requests.push(axios.delete('/remocra/hydrantaspiration/', {
                  data: aspirationData.aspirationsDel
                }).catch(function(error) {
                  console.error('postEvent', error)
                }))
              }

              // Simultanément, MAj des visites
              var visiteData = self.$refs.ficheVisite.prepareVisitesData(id);
              requests.push(axios.post('/remocra/hydrantvisite/updatemany', JSON.parse(visiteData.visites)).catch(function(error) {
                console.error('postEvent', error)
              }))
              requests.push(axios.delete('/remocra/hydrantvisite', {
                data: visiteData.visitesDel
              }).catch(function(error) {
                console.error('postEvent', error)
              }))
            }

             axios.all(requests)
             .then(function () {
               self.$root.$options.bus.$emit('pei_modified', {
                 id: id, numero: numero
               })
             });
          }).catch(function(error) {
            console.error('postEvent', error)
          })
      }).catch(function (error) {
        console.log(error);
      });
    }

  }
};
</script>

<style>

.Fiche{
  margin-top: 27px;
  min-height: 250px;
  padding: 10px;
}

.nav-link {
  border-bottom-color: #bababa !important;
  font-weight: bold;
}

.nav-link.active {
  border-color: #bababa #bababa transparent #bababa !important;
  background-color: #ccccccbf !important;
}

b-tab {
  background-color: #e9e9e9 !important;
}

.btnInlineForm{
  border-radius: 5px;
  min-height: 28px;
}

.tabOnglet{
  background-color: red;
  color: blue;
}

#formFiche {
  padding-top: 3px;
}
#formFiche .invalid-feedback{
  font-size: 100%;
}

.title{
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
  margin-top: 1rem;
  color: #7b7b7b;
  font-family: Helvetica, Arial !important
}

fieldset, .form-group {
  margin-bottom: 5px;
}

label {
  margin-bottom: 2px;
}


</style>
