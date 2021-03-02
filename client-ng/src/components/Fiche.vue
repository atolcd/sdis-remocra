<template>
<div :class="{ 'Fiche': true, 'mode-visite': newVisite, 'loading': !dataLoaded }">
  <modal name="modalFiche"  :draggable="true" id="modalFiche"  ref="modalFiche" classes="modalFiche" @closed="close()"
    :reset="true"
    width="70%"
    height="auto">
    <header class="modal-header"><h5 class="modal-title">{{title}}</h5>
    <div slot="top-right">
     <button type="button" aria-label="Close" @click="$modal.hide('modalFiche')" class="close">×</button>
    </div></header>
    <form id='formFiche' name='fiche' enctype="multipart/form-data" method="POST" ref="formFiche">
      <!-- ================================== En-tête du formulaire ==================================-->
      <div id="entete" class="entete form-group">
        <div class="row">
          <div class="col-md-3">
            <b-form-group invalid-feedback="Numéro manquant ou déjà attribué" :state="etats.numeroInterne" label="Numéro interne" label-for="numeroInterne" label-cols-md="6">
              <b-form-input type="text" id="numeroInterne" v-model="hydrant.numeroInterne" :disabled="!idHydrant || (utilisateurDroits.indexOf('HYDRANTS_NUMEROTATION_C') == -1)" class="parametre" size="sm" :state="etats.numeroInterne">
              </b-form-input>
            </b-form-group>
          </div>
          <div class="col-md-3">
            <b-form-group label="Nature" label-for="nature" invalid-feedback="La nature doit être renseignée" :state="etats.nature" label-cols-md="4">
              <b-form-select id="nature" v-model="hydrant.nature" class="parametre" :options="comboType" size="sm" :state="etats.nature" v-on:change="onNatureChange" required></b-form-select>
            </b-form-group>
          </div>
          <div class="col-md-6">
            <b-form-group label="Autorité de police DECI" label-for="autoriteDeci" invalid-feedback="L'autorité de police DECI doit être renseignée" :state="etats.autoriteDeci" label-cols-md="5">
              <b-form-select id="autoriteDeci" v-model="hydrant.autoriteDeci" class="parametre" :options="ellipsis(comboAutoriteDeci)" size="sm" :state="etats.autoriteDeci" required></b-form-select>
            </b-form-group>
          </div>
        </div>
        <div class="row">
          <div class="col-md-5">
            <b-form-group label="Type de DECI" label-for="nature_deci" invalid-feedback="La nature DECI doit être renseignée" :state="etats.natureDeci" label-cols-md="5">
              <b-form-select ref="natureDeci" v-model="hydrant.natureDeci" :options="comboDeci" size="sm" id="natureDeci" class="parametre" v-on:change="getPublicOrPrive" :state="etats.natureDeci"></b-form-select>
            </b-form-group>
          </div>
          <div class="col-md-7">
            <b-form-group label="Service Public DECI" label-for="spDeci" invalid-feedback="Le service public DECI doit être renseignée" :state="etats.spDeci" label-cols-md="4">
              <b-form-select ref="spDeci" v-model="hydrant.spDeci" :options="comboSpDeci" size="sm" id="spDeci" class="parametre" :state="etats.spDeci" required></b-form-select>
            </b-form-group>
          </div>
        </div>
        <div class="row">
          <div class="col-md-7">
            <b-form-group label="Maintenance et CTP" label-for="maintenance_deci" :state="etats.maintenanceDeci" label-cols-md="3">
              <b-form-select ref="maintenanceDeci" v-model="hydrant.maintenanceDeci" :options="comboMaintenanceDeci" size="sm" id="maintenanceDeci" class="parametre" :state="etats.maintenanceDeci"></b-form-select>
            </b-form-group>
          </div>
        </div>
        <div class="row">
          <div class="col-md-10" v-if="hydrant.natureDeci != idDeciPublic">
            <b-form-group label="Gestionnaire" label-for="gestionnaire" invalid-feedback="Le gestionnaire doit être renseigné" :state="etats.gestionnaire" label-cols-md="2">
              <b-form-select id="gestionnaire" v-model="hydrant.gestionnaire" class="parametre" :options="sortCombo(ellipsis(comboGestionnaire))" size="sm" v-on:change="onGestionnaireChange" :state="etats.gestionnaire">
              </b-form-select>
              <button class="gestionnaireBtn" @click="modifGestionnaire" v-if="utilisateurDroits.indexOf('HYDRANTS_GESTIONNAIRE_C') != -1">
                <img src="/remocra/static/img/pencil.png">
              </button>
              <button class="gestionnaireBtn addGest" @click="addGestionnaire" v-if="utilisateurDroits.indexOf('HYDRANTS_GESTIONNAIRE_C') != -1">
                <img src="/remocra/static/img/add.png">
              </button>
            </b-form-group>
          </div>
        </div>
        <div class="row">
          <div class="col-md-10" v-if="hydrant.natureDeci != idDeciPublic">
            <b-form-group label="Site" label-for="site" label-cols-md="2">
              <b-form-select id="site" v-model="hydrant.site" class="parametre" :options="ellipsis(comboSite)" size="sm"></b-form-select>
            </b-form-group>
          </div>
        </div>
      </div>
      <div>
        <b-tabs fill content-class="mt-3" active-nav-item-class="text-primary" nav-class="fiche-onglets">
          <b-tab active title="Résumé" v-if="hydrant.id !== null">
            <FicheResume ref="ficheResume" :hydrantRecord="hydrantRecord" v-if="dataLoaded">
            </FicheResume>
          <div class="small" v-if="loaded">
            <bar-chart :chartdata="chartdata" :options="{title:'Débit (m³/h)', background:'green'}" :styles="{height: '250px', width:'90%'}"/>
          </div>
          </b-tab>
          <!-- ================================== Onglet Localisation ==================================-->
          <b-tab>
            <template slot="title"> Localisation <b-badge pill variant="danger" v-if="tabWarning.localisation">!</b-badge>
            </template>
            <FicheLocalisation :hydrant="hydrant" :utilisateurDroits="utilisateurDroits" :geometrie="geometrie" v-if="dataLoaded" @getComboData="getComboData" @resolveForeignKey="resolveForeignKey" @onCoordsChange="onCoordsChange"
              ref="ficheLocalisation">
            </FicheLocalisation>
          </b-tab>
          <!-- ================================== Onglet Caractéristiques techniques ==================================-->
          <b-tab>
            <template slot="title"> Caractéristiques techniques <b-badge pill variant="danger" v-if="tabWarning.caracteristiquesTechniques">!</b-badge>
            </template>
            <FicheCaracteristiquesPibi :hydrant="hydrant" :hydrantRecord="hydrantRecord" :listeNaturesDeci="listeNaturesDeci" :geometrie="geometrie" @getComboData="getComboData" @resolveForeignKey="resolveForeignKey" ref="fichePibi"
              :utilisateurDroits="utilisateurDroits" v-if="hydrant.code=='PIBI' && dataLoaded">
            </FicheCaracteristiquesPibi>
            <FicheCaracteristiquesPena :hydrant="hydrant" :hydrantRecord="hydrantRecord" @getComboData="getComboData" @resolveForeignKey="resolveForeignKey" :utilisateurDroits="utilisateurDroits" ref="fichePena"
              v-if="hydrant.code=='PENA' && dataLoaded">
            </FicheCaracteristiquesPena>
          </b-tab>
          <!-- ================================== Onglet Visites ==================================-->
          <b-tab ref="visitesTab">
            <template slot="title"> Visites <b-badge pill variant="danger" v-if="tabWarning.visites">!</b-badge>
            </template>
            <FicheVisite :hydrant="hydrant" :utilisateurDroits="utilisateurDroits" :newVisite="newVisite" @getComboData="getComboData" @resolveForeignKey="resolveForeignKey" ref="ficheVisite" v-if="dataLoaded">
            </FicheVisite>
          </b-tab>
          <!-- ================================== Onglet Documents ==================================-->
          <b-tab title="Documents">
            <FicheDocument ref="ficheDocument" :hydrant="hydrant" v-if="dataLoaded">
            </FicheDocument>
          </b-tab>
        </b-tabs>
      </div>
    </form>
    <div class="modal-footer">
        <b-button size="sm" type="reset" variant="secondary" @click="$modal.hide('modalFiche')">Annuler</b-button>
        <b-button size="sm" type="submit" variant="primary" @click="handleOk" :disabled="!dataLoaded">Valider</b-button>
      </div>
    <ModalGestionnairePrive v-on:modalGestionnaireValues="onGestionnaireCreated" ref="modalGestionnairePrive"></ModalGestionnairePrive>
    <notifications group="remocra" position="top right" animation-type="velocity" :duration="3000" />
  </modal>
  </div>
</template>

<script>
import axios from 'axios'
import {
  loadProgressBar
} from 'axios-progress-bar'
import _ from 'lodash'
import ModalGestionnairePrive from './ModalGestionnairePrive.vue'
import FicheLocalisation from './FicheLocalisation.vue'
import FicheCaracteristiquesPibi from './FicheCaracteristiquesPibi.vue'
import FicheCaracteristiquesPena from './FicheCaracteristiquesPena.vue'
import FicheVisite from './FicheVisite.vue'
import FicheDocument from './FicheDocument.vue'
import FicheResume from './FicheResume.vue'
import BarChart from './utils/BarChart.js'

export default {
  name: 'Fiche',
  components: {
    BarChart,
    ModalGestionnairePrive,
    FicheLocalisation,
    FicheCaracteristiquesPibi,
    FicheCaracteristiquesPena,
    FicheVisite,
    FicheDocument,
    FicheResume
  },
  data() {
    return {
      idDeciPrive: '',
      idDeciPublic: '',
      hydrantRecord: {}, // Données initiales du PEI
      hydrant: {}, // Données actuelles du PEI
      utilisateurDroits: [],
      dataLoaded: false,
      //ComboBox
      comboType: [],
      comboDeci: [],
      comboSpDeci: [],
      comboGestionnaire: [],
      comboSite: [],
      comboAutoriteDeci: [],
      listeNaturesDeci: [],
      comboMaintenanceDeci: [],
      tabWarning: {
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
        natureDeci: null,
        spDeci: null,
        maintenanceDeci: null
      },
    loaded: false,
    chartdata: {} }
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
    },
    title: {
      type: String,
      required: true
    },
    showHistorique: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    /**
     * Renvoie une liste d'option de select dont le texte a été ellipsé
     * Cela permet d'éviter le comportement par défaut consistant à définir la taille des options selon la taille du plus grand élément. La gestion et l'affichage des éléments de type
     * select étant différents selon les navigateurs, on s'assure de la compatibilité cross-browser en agissant sur les données
     * @param combo Un tableau d'objet contenant les options de la combobox
     */
    ellipsis: function() {
      return function(combo) {
        var c = [];
        _.forEach(combo, item => {
          var i = {
            value: item.value,
            text: item.text
          };
          if (i.text.length > 80) {
            i.text = i.text.substring(0, 80).concat("...");
          }
          c.push(i);
        });
        return c;
      }
    },
    /**
     * Renvoie les données d'une combobox triées alphabétiqueement selon la valeur associée
     * @param combo Un tableau d'objet contenant les options de la combobox
     */
    sortCombo: function() {
      return function(combo) {
        return combo.sort((a, b) => a.text.localeCompare(b.text));
      }
    }
  },
  mounted: function() {
    //this.$refs.modalFiche.show()
    this.$modal.show('modalFiche', {
        title: 'Information' })
    loadProgressBar({
      parent: "#modalFiche",
      showSpinner: false
    })
    //TODO déplacer la charte
    var self = this;
    // Récupération des droits de l'utilisateur courant
    axios.get('/remocra/utilisateurs/current/xml').then(response => {
      var xmlDoc = (new DOMParser()).parseFromString(response.data, "text/xml");
      self.utilisateurDroits = [];
      _.forEach(xmlDoc.getElementsByTagName("right"), function(item) {
        self.utilisateurDroits.push(item.getAttribute("code"));
      });
    }).then(function() {
      if (self.idHydrant) {
        self.modification()
      } else {
        self.creation()
      }
    });

  },
  /**
   * Désactivation des libellés lorsque le champ auquel il est lié est désactivé
   * On est obligé de passer par du js, les sélecteurs CSS qui permettraient de faire ça ne ne sont pas encore supportés
   */
  updated: function() {
    _.forEach(document.querySelectorAll('.form-group .col'), node => {
      if (node.firstElementChild && node.firstElementChild.disabled == true) {
        node.parentElement.firstChild.classList.add("labelDisabled");
      } else {
        node.parentElement.firstChild.classList.remove("labelDisabled");
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
        spDeci: null,
        code: self.codeHydrant,
        maintenanceDeci: null
      }
      self.hydrant = _.clone(self.hydrantRecord);
      self.dataLoaded = true;
      self.createCombo();
    },
    modification() {
      let self = this
      axios.get('/remocra/hydrants' + self.codeHydrant.toLowerCase() + '/' + self.idHydrant).then(response => {
        if (response.data.data && response.data.data.length !== 0) {
          self.hydrantRecord = response.data.data;
          //Résolution des clés étrangères
          self.hydrant = _.clone(self.hydrantRecord, true);
          self.dataLoaded = true;
          self.resolveForeignKey(['nature', 'site', 'autoriteDeci', 'natureDeci', 'gestionnaire', 'maintenanceDeci']);
          self.createCombo();
          self.getHistorique(self.idHydrant)
          if (self.newVisite === true) {
            self.$refs.visitesTab.activate()
            self.$root.$options.bus.$on('pei_visite_ready', () => {
              if (!self.$refs.ficheVisite.createVisiteDisabled) { // Si l'utilisateur peut créer une visite
                self.$refs.ficheVisite.createVisite()
              }
            })
          }
        } else {
          console.error('Traitement de la réponse : ', response)
        }
      }).catch(function(error) {
        console.error('Retrieving data ', error)
      })
    },
    getHistorique(id){
      this.loaded = false
      if(this.showHistorique){
        axios.get('/remocra/hydrantspibi/histoverifhydrauforchart/'+id).then(response => {
          if(response.data){
              this.chartdata.labels =  response.data.data.labels;
              this.chartdata.values =  response.data.data.values;
              this.loaded = true
            }
        }).catch(function(error) {
            console.error(error);
        })
      }
    },


    // Résolution des clés étrangères
    resolveForeignKey(clesEtrangeres) {
      _.forEach(clesEtrangeres, function(item) {
        this.hydrant[item] = (this.hydrantRecord[item]) ? _.clone(this.hydrantRecord[item].id) : null;
      }.bind(this));
    },
    // Fonction récupérant les données nécessaire aux combos du formulaire
    createCombo() {
      this.getComboData(this, 'comboType', '/remocra/typehydrantnatures.json', {
        "filter": JSON.stringify([{
          "property": "typeHydrantCode",
          "value": this.codeHydrant
        }])
      }, 'id', 'nom');
      this.getComboData(this, 'comboDeci', '/remocra/typehydrantnaturedeci.json', null, 'id', 'nom');
      //Combo de l'autorité DECI: on modifie le texte affiché en fonction du type d'organisme
      var self = this;
      axios.get('/remocra/organismes/autoritepolicedeci.json?geometrie=' + this.geometrie).then(response => {
        _.forEach(response.data, function(item) {
          var libelle;
          switch (item['typeOrganisme']) {
            case 'COMMUNE':
              libelle = "Maire (" + item['nom'] + ")";
              break;
            case 'EPCI':
              libelle = "Président (" + item['nom'] + ")";
              break;
            case 'PREFECTURE':
              libelle = "Préfet (" + item['nom'] + ")";
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
        self.getPublicOrPrive(true)
        self.getComboSpDeci();
      })).catch(function(error) {
        console.error('Retrieving combo data from /remocra/organismes/autoritepolicedeci/', error);
      })
      //On récupère les organismes de maintenance et CTP
      this.getComboData(this, 'comboMaintenanceDeci', '/remocra/organismes/maintenancedeci',
      { 'geometrie' : this.geometrie}, 'id', 'nom', ' ');
    },
    /**
     * Récupère les données des combobox
     * @param attribut L'attribut du composant qui contiendra les données (on ne peut pas utiliser async/await par souci de compatibilité, et on évite l'enchaînement de callback)
     * @param url L'url remocra permettant de récupérer les données
     * @param champValeur Indique parmis les données récupérées le champ de valeur
     * @param champTexte Indique parmis les données récupérées le champ texte à afficher dans la combo
     * @param optionVide Si indiqué, ajoute en première position une option vide valant null avec un texte configurable
     */
    getComboData(context, attribut, url, params = {}, champValeur, champTexte, optionVide) {
      context[attribut] = [];
      let allParams = _.defaults(params, {
        "sort": JSON.stringify([{
          "property": champTexte,
          "direction": "ASC"
        }])
      })
      axios.get(url, {
        params: allParams
      }).then(response => {
        _.forEach(response.data.data, function(item) {
          context[attribut].push({
            text: item[champTexte],
            value: item[champValeur]
          });
        });
        if (optionVide) {
          context[attribut].unshift({
            text: optionVide,
            value: null
          });
        }
      }).catch(function(error) {
        console.error('Retrieving combo data from ' + url, error);
      })
    },
    getPublicOrPrive(init) {
      this.idDeciPublic = this.listeNaturesDeci.filter(item => item.code === "PUBLIC")[0].id;
      this.idDeciPrive = this.listeNaturesDeci.filter(item => item.code === "PRIVE")[0].id;
      //si on est sur du privé ou conventionné on charge la combo gestionnaire
      if (this.hydrant.natureDeci != this.idDeciPublic) {
        this.getComboGestionnaire(init);
      }
    },
    getComboGestionnaire(init) {
      if(!init) {
        this.hydrant.site = null;
      }

      //Si DECI privé ou conventionne , le gestionnaire est un privé
      this.getComboData(this, 'comboGestionnaire', '/remocra/gestionnaire.json', null, 'id', 'nom', ' ');
      this.onGestionnaireChange();
    },
    getComboSpDeci() {
      if (this.hydrantRecord.natureDeci && this.hydrantRecord.spDeci && this.hydrantRecord.natureDeci.id === this.hydrant.natureDeci) {
        this.hydrant.spDeci = this.hydrantRecord.spDeci.id;
      } else {
        this.hydrant.spDeci = null;
      }
      // Si DECI publique , le gestionnaire est un organisme de type COMMUNE ou EPCI
      var self = this;
      axios.get('/remocra/organismes/gestionnairepublic.json', {
        params: {
          geometrie: this.geometrie,
          sort: JSON.stringify([{
            "property": "nom",
            "direction": "ASC"
          }])
        }
      }).then(response => {
        self.comboSpDeci = [];
        _.forEach(response.data, function(nature) {
          self.comboSpDeci.push({
            text: nature.nom,
            value: nature.id
          })
        });
      }).then().catch(function(error) {
        console.error('Retrieving combo data from /remocra/organismes/servicepublicDeci/' + this.geometrie, error);
      });
    },
    /**
     * En cas de changement de nature DECI
     * On met à jour le gestionnaire et on transmet l'évènement aux caractéristiques du PIBI dont les champs dépendent de la nature
     */
    onNatureDeciChange() {
      this.getPublicOrPrive(false);
      this.getComboSpDeci();
      if (this.$refs.fichePibi) {
        this.$refs.fichePibi.onNatureDeciChange();
      }
    },
    onNatureChange(value) {
      if (this.$refs.fichePibi) {
        let nature = this.comboType.filter(item => item.value === value)[0].text;
        this.$refs.fichePibi.updateComboDiametres(nature);
        this.$refs.fichePibi.updateComboJumelage(nature);
      }
      this.$refs.ficheVisite.onNatureChange();
      this.hydrant.gestionnaire = null
      this.hydrant.site = null
    },
    /**
     * Appelée lorsque l'on créé un gestionnaire grâce à la modale
     * @param values Les données du gestionnaire créé (transmises par le composant ModalGestionnaire)
     */
    onGestionnaireCreated(values) {
      // Création : ajout du gestionnaire dans la combo
      if ((_.findIndex(this.comboGestionnaire, o => o.value == values.id) == -1)) {
        this.comboGestionnaire.push({
          text: values.nom,
          value: values.id
        });
      } else { // Modification : mise à jour du nom pour le gestionnaire déjà sélectionné
        _.find(this.comboGestionnaire, o => o.value == values.id).text = values.nom;
      }
      this.hydrant.gestionnaire = values.id
    },
    /**
     * En cas de changement de gestionnaire, on met à jour la liste des sites
     */
    onGestionnaireChange() {
      if (this.hydrant.gestionnaire != null) {
        this.getComboData(this, 'comboSite', '/remocra/site.json', {
          "filter": JSON.stringify([{
            "property": "gestionnaire",
            "value": this.hydrant.gestionnaire
          }]),
          "sort": JSON.stringify([{
            "property": "nom",
            "direction": "ASC"
          }])
        }, 'id', 'nom', 'Aucun');
      } else {
        this.comboSite = this.comboSite.filter(i => i.value == null);
      }
      if (this.hydrantRecord.gestionnaire && this.hydrantRecord.gestionnaire.id == this.hydrant.gestionnaire) {
        this.hydrant.site = (this.hydrantRecord.site) ? this.hydrantRecord.site.id : null;
      } else {
        this.hydrant.site = null;
      }
    },
    /**
     * Fonction appelée lorsque les coordonnées du PEI sont modifies
     * On peut notifier les composants enfant, notamment si certains de leurs champs sont dépendant de la géométrie du PEI
     */
    onCoordsChange(longitude, latitude) {
      this.geometrie = "POINT (" + longitude + " " + latitude + ")";
      // Mise à jour de la combo de sélection de jumelage
      if (this.$refs.fichePibi) {
        let nature = this.comboType.filter(item => item.value === this.hydrant.nature)[0].text;
        this.$refs.fichePibi.updateComboJumelage(nature, this.geometrie);
      }
    },
    /**
     * Valide ou non les données du formulaire de ce module ainsi que de ses modules enfants
     * La réponse est envoyée au serveur, qui fera appel à this.handleSubmit() pour procéder à l'envoi des données
     */
    checkFormValidity() {
      this.etats.numeroInterne = !this.idHydrant || (this.idHydrant && this.hydrant.numeroInterne.toString().length > 0) ? 'valid' : 'invalid';
      this.etats.spDeci = (this.hydrant.spDeci !== null) ? 'valid' : 'invalid';
      this.etats.nature = this.hydrant.nature ? 'valid' : 'invalid';
      this.etats.autoriteDeci = (this.hydrant.autoriteDeci !== null) ? 'valid' : 'invalid';
      this.etats.natureDeci = (this.hydrant.natureDeci !== null) ? 'valid' : 'invalid';
      this.etats.maintenanceDeci = 'valid';
      this.tabWarning = {
        localisation: false,
        caracteristiquesTechniques: false,
        visites: false
      };
      var isFormValid = true;
      if (this.hasInvalidState(this.etats)) {
        isFormValid = false;
      }
      if (this.hasInvalidState(this.$refs.ficheLocalisation.checkFormValidity())) {
        this.tabWarning.localisation = true;
        isFormValid = false;
      }
      if ((this.$refs.fichePibi && this.hasInvalidState(this.$refs.fichePibi.checkFormValidity())) || (this.$refs.fichePena && this.hasInvalidState(this.$refs.fichePena.checkFormValidity()))) {
        this.tabWarning.caracteristiquesTechniques = true;
        isFormValid = false;
      }
      if (this.hasInvalidState(this.$refs.ficheVisite.checkFormValidity())) {
        this.tabWarning.visites = true;
        isFormValid = false;
      }
      // On vérifie que le numéro de PEI n'est pas un doublon
      if (this.hydrant.id) {
        return axios.post('/remocra/hydrants/checkdispo.json?id=' + this.hydrant.id + '&nature=' + this.hydrant.nature + '&commune=' + this.hydrant.commune + '&num=' + this.hydrant.numeroInterne + '&geometrie=' + this.geometrie).then(() => {
          return isFormValid;
        }).catch((error) => {
          this.$notify({
            group: 'remocra',
            type: 'error',
            title: 'Saisie invalide',
            text: error.response.data.message
          });
          this.etats.numeroInterne = 'invalid';
          return false;
        });
      } else {
        return isFormValid
      }
    },
    /**
     * Vérifie si le formulaire a des champs invalides
     * En pratique, les modules vueJS enfants ce module renvoient leur état après un appel à leur fonction checkFormValidity()
     * @param etats L'objet "etats" des champs d'un module
     * @return TRUE si le champ présente des champs invalides, FALSE sinon
     */
    hasInvalidState(etats) {
      var hasInvalidState = false;
      for (var key in etats) {
        hasInvalidState = hasInvalidState || etats[key] == "invalid";
      }
      return hasInvalidState;
    },
    /**
     * Envoie les données au serveur afin de procéder à la mise à jour de l'hydrant
     * @param url L'url à contacter
     */
    handleOk(evt) {
      evt.preventDefault()
      var url = null;
      if (this.hydrant != null) {
        if (this.hydrant.code == "PIBI") {
          url = '/remocra/hydrantspibi'
        } else {
          url = '/remocra/hydrantspena'
        }
      }
      url = url + (this.hydrant.id == null ? '' : '/' + this.hydrant.id);
      if (this.hydrant.id) { // PEI déjà en base -> on attend le retour de la requête faite au serveur pour éviter les doublons de numéro de PEI
        this.checkFormValidity().then((response) => {
          if (response) {
            this.handleSubmit(url);
          } else if(this.newVisite){ // Mode modification, visite rapide et form non valide
            this.$notify({
              group: 'remocra',
              type: 'error',
              title: 'Création de visite impossible',
              text: "Les informations de l'hydrant sélectionné ne sont pas valides. Veuillez les corriger via la fiche PEI avant de saisir des visites",
              duration: 5000
            });
          }
        });
      } else if (this.checkFormValidity()) {
        this.handleSubmit(url);
      }
    },
    handleSubmit(url) {
      // Si la nature est passée de PRIVE a PUBLIC/CONVENTIONNE ou inversement, le PEI ne respecte plus la contrainte de nature DECI au sein de ses tournées
      // On le désaffecte donc pour tous les organismes
      this.idDeciPrive = this.listeNaturesDeci.filter(item => item.code === "PRIVE")[0].id;
      if (this.hydrantRecord.natureDeci && (this.hydrant.natureDeci == this.idDeciPrive) != (this.hydrantRecord.natureDeci.id == this.idDeciPrive)) {
        axios.post('hydrants/desaffecter?json=[' + this.hydrant.id + ']&allOrganismes=true').catch(function(error) {
          console.error('postEvent', error)
        });
      }
      // Récupération des données
      var data = {};
      _.forEach(document.getElementsByClassName('parametre'), item => {
        if (Array.from(item.classList).indexOf('custom-checkbox') > -1) { // Checkbox
          var element = item.getElementsByTagName('input')[0];
          data[element.id] = this.hydrant[element.id];
        } else if (item.type === "number") { // Input de type number
          data[item.id] = (item.value === "") ? null : parseInt(item.value);
        } else if (Array.from(item.classList).indexOf('autocomplete') > -1) { //Autocomplete
          data[item.id] = item[0]._value;
        } else {
          data[item.id] = item.value;
        }
      });
      if (this.hydrant.natureDeci == this.idDeciPublic) {
        data["gestionnaire"] = null
        data["site"] = null
      }
      data["geometrie"] = this.geometrie;
      if (!this.idHydrant) {
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
        }).then(function(response) { // Une fois le PEI mis à jour, on peut récupérer son id et mettre à jour les aspirations
          var id = response.data.data.id;
          let numero = response.data.data.numero;
          var requests = []
          if (id !== null) {
            if (self.$refs.fichePena) {
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
          axios.all(requests).then(function() {
            loadProgressBar({
              parent: "head",
              showSpinner: false
            });
            self.$root.$options.bus.$emit('pei_modified', {
              id: id,
              numero: numero
            })
             if (self.$refs.modalFiche) {
                self.$modal.hide('modalFiche')
           }});
        }).catch(function(error) {
          console.error('postEvent', error)
        })
      }).catch(function(error) {
        console.log(error);
      });
    },
    close() {
      loadProgressBar({
        parent: "head",
        showSpinner: false
      });
      this.$root.$options.bus.$emit('closed')
    },
    addGestionnaire(evt) {
      var appartenance = (this.listeNaturesDeci.filter(item => item.code === "PRIVE")[0].id == this.hydrant.natureDeci) ? "GESTIONNAIRE" : "ORGANISME";
      this.$refs.modalGestionnairePrive.editGestionnaire(null, appartenance);
      evt.preventDefault()
    },
    modifGestionnaire(evt) {
      var appartenance = (this.listeNaturesDeci.filter(item => item.code === "PRIVE")[0].id == this.hydrant.natureDeci) ? "GESTIONNAIRE" : "ORGANISME";
      this.$refs.modalGestionnairePrive.editGestionnaire(this.hydrant.gestionnaire, appartenance);
      evt.preventDefault()
    }
  }
};
</script>

<style>
.Fiche {
  width: 600px;
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

.btnInlineForm {
  border-radius: 5px;
  min-height: 28px;
}

.tabOnglet {
  background-color: red;
  color: blue;
}

#formFiche {
  padding-top: 3px;
}

#formFiche .invalid-feedback {
  font-size: 100%;
}

.title {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
  margin-top: 1rem;
  color: #7b7b7b;
  font-family: Helvetica, Arial !important
}

fieldset,
.form-group {
  margin-bottom: 5px;
}

label {
  margin-bottom: 2px;
}

.vm--modal {
  background-color: #e9e9e9 !important;
  padding: 10px;
}

#modalFiche .modal-content {
 width: max-content;
 left: 50%;
 transform: translate(-50%);
 background-color: #e9e9e9 !important;
}

.modal-footer {
  justify-content: center;
}

.gestionnaireBtn {
  padding: .25rem .5rem;
  font-size: .875rem;
  line-height: 1.5;
  border-radius: .2rem;
  position: absolute;
  height: calc(1.5em + .5rem + 2px);
  background-color: #fff;
  border: 1px solid #ced4da;
  border-radius: .25rem;
}

.vm--modal.modalFiche {
  max-height: 90%;
  overflow-y: scroll;
}

#modalFiche .addGest {
  margin-left: 30px;
}

.mode-visite .entete,
.mode-visite .fiche-onglets.nav-tabs,
.mode-visite .visites-lst {
  display: none;
}

.mode-visite .modal-body {
  padding-top: 0;
}

.Fiche.loading .modal-body::after {
  content: 'Initialisation fiche...';
  margin-left: calc(50% - 67px);
  animation: opacity-anim 1s linear infinite;
}

.labelDisabled {
  color: #6c757d !important;
}
 .small {
   display: block;
    height: 250px;
    margin-top: 20px;
    width: 80%;
    margin-left: 10%;
  }
</style>
