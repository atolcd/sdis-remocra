<template>
<div :class="{ 'Fiche': true, 'mode-visite': newVisite, 'loading': !dataLoaded }">
  <b-modal id="modalFiche" ref="modalFiche" :title="title" no-close-on-backdrop ok-title="Valider" cancel-title="Annuler" @ok="handleOk" @hidden="close()" :ok-disabled="!dataLoaded">
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
          <div class="col-md-4">
            <b-form-group label="Type de DECI" label-for="nature_deci" invalid-feedback="La nature DECI doit être renseignée" :state="etats.natureDeci" label-cols-md="5">
              <b-form-select ref="natureDeci" v-model="hydrant.natureDeci" :options="comboDeci" size="sm" id="natureDeci" class="parametre" v-on:change="getComboGestionnaire" :state="etats.natureDeci"></b-form-select>
            </b-form-group>
          </div>
          <div class="col-md-5">
            <b-form-group label="Gestionnaire" label-for="gestionnaire" invalid-feedback="Le gestionnaire doit être renseigné" :state="etats.gestionnaire" label-cols-md="4">
              <b-form-select id="gestionnaire" v-model="hydrant.gestionnaire" class="parametre" :options="ellipsis(comboGestionnaire)" size="sm" v-on:change="onGestionnaireChange" :state="etats.gestionnaire" required>
              </b-form-select>
              <button class="gestionnaireBtn" @click="modifGestionnaire" v-if="hydrant.natureDeci == idDeciPrive && utilisateurDroits.indexOf('HYDRANTS_GESTIONNAIRE_C') != -1">
                <img src="/remocra/static/img/pencil.png">
              </button>
              <button class="gestionnaireBtn addGest" @click="addGestionnaire" v-if="hydrant.natureDeci == idDeciPrive && utilisateurDroits.indexOf('HYDRANTS_GESTIONNAIRE_C') != -1">
                <img src="/remocra/static/img/add.png">
              </button>
            </b-form-group>
          </div>
          <div class="col-md-4">
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
            <FicheCaracteristiquesPibi :hydrant="hydrant" :hydrantRecord="hydrantRecord" :listeNaturesDeci="listeNaturesDeci" :geometrie="geometrie" @getComboData="getComboData" @resolveForeignKey="resolveForeignKey" ref="fichePibi" :utilisateurDroits="utilisateurDroits"
              v-if="hydrant.code=='PIBI' && dataLoaded">
            </FicheCaracteristiquesPibi>
            <FicheCaracteristiquesPena :hydrant="hydrant" :hydrantRecord="hydrantRecord" @getComboData="getComboData" @resolveForeignKey="resolveForeignKey" :utilisateurDroits="utilisateurDroits" ref="fichePena" v-if="hydrant.code=='PENA' && dataLoaded">
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
    <ModalGestionnairePrive v-on:modalGestionnaireValues="onGestionnaireCreated" ref="modalGestionnairePrive"></ModalGestionnairePrive>
    <notifications group="remocra" position="top right" animation-type="velocity" :duration="3000" />
  </b-modal>
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
export default {
  name: 'Fiche',
  components: {
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
    },
    title: {
      type: String,
      required: true
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
          if (i.text.length > 35) {
            i.text = i.text.substring(0, 35).concat("...");
          }
          c.push(i);
        });
        return c;
      }
    },
  },

  mounted: function() {
    this.$refs.modalFiche.show()
    loadProgressBar({
      parent: "#formFiche",
      showSpinner: false
    })
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
      if(node.firstElementChild && node.firstElementChild.disabled == true) {
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
        code: self.codeHydrant
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
          self.resolveForeignKey(['nature', 'site', 'autoriteDeci', 'natureDeci']);
          self.createCombo();
          if (self.newVisite === true) {
            self.$refs.visitesTab.activate()
            self.$root.$options.bus.$on('pei_visite_ready', () => {
              self.$refs.ficheVisite.createVisite()
            })
          }
        }
      }).catch(function(error) {
        console.error('Retrieving data ', error)
      })
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
              libelle = "Maire de " + item['nom'];
              break;
            case 'EPCI':
              libelle = "Président de " + item['nom'];
              break;
            case 'PREFECTURE':
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
    getComboGestionnaire() {
      this.idDeciPrive = this.listeNaturesDeci.filter(item => item.code === "PRIVE")[0].id;
      this.hydrant.site = null;
      // En cas de changement de nature (passage de PUBLIC/CONVENTIONNE à PRIVE et inversement), on set le gestionnaire à null
      // De cette manière, si on passe de PUBLIC à CONVENTIONNE ou inversement, on laisse tel quel
      if (this.hydrantRecord.natureDeci && (this.hydrant.natureDeci == this.idDeciPrive) != (this.hydrantRecord.natureDeci.id == this.idDeciPrive)) {
        this.hydrant.gestionnaire = null;
      }
      //Si DECI privé, le gestionnaire est un privé
      if (this.hydrant.natureDeci == this.idDeciPrive) {
        this.getComboData(this, 'comboGestionnaire', '/remocra/gestionnaire.json', null, 'id', 'nom', ' ');
        this.onGestionnaireChange();
      } else { // Si DECI publique ou conventionnée, le gestionnaire est un organisme de type COMMUNE ou EPCI
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
          self.comboGestionnaire = [];
          _.forEach(response.data, function(nature) {
            self.comboGestionnaire.push({
              text: nature.nom,
              value: nature.id
            })
          });
        }).then(self.onGestionnaireChange()).catch(function(error) {
          console.error('Retrieving combo data from /remocra/organismes/gestionnairepublic/' + this.geometrie, error);
        });
      }
    },
    /**
     * En cas de changement de nature DECI
     * On met à jour le gestionnaire et on transmet l'évènement aux caractéristiques du PIBI dont les champs dépendent de la nature
     */
    onNatureDeciChange() {
      this.getComboGestionnaire();
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
    },
    /**
     * Appelée lorsque l'on créé un gestionnaire grâce à la modale
     * @param values Les données du gestionnaire créé (transmises par le composant ModalGestionnaire)
     */
    onGestionnaireCreated(values) {
      if ((_.findIndex(this.comboGestionnaire, o => o.value == values.id) == -1)) {
        this.comboGestionnaire.push({
          text: values.nom,
          value: values.id
        });
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
      }
      if (this.hydrantRecord.gestionnaire != this.hydrant.gestionnaire) {
        this.hydrant.site = null;
      } else {
        this.hydrant.site = (this.hydrantRecord.site) ? this.hydrantRecord.site.id : null;
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
      this.etats.gestionnaire = (this.hydrant.gestionnaire !== null) ? 'valid' : 'invalid';
      this.etats.nature = this.hydrant.nature ? 'valid' : 'invalid';
      this.etats.autoriteDeci = (this.hydrant.autoriteDeci !== null) ? 'valid' : 'invalid';
      this.etats.natureDeci = (this.hydrant.natureDeci !== null) ? 'valid' : 'invalid';
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
              self.$refs.modalFiche.hide()
            }
          });
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
      this.$refs.modalGestionnairePrive.editGestionnaire(null);
      evt.preventDefault()
    },
    modifGestionnaire(evt) {
      this.$refs.modalGestionnairePrive.editGestionnaire(this.hydrant.gestionnaire);
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

#modalFiche .modal-content {
  width: max-content;
  left: 50%;
  transform: translate(-50%);
  background-color: #e9e9e9 !important;
}

#modalFiche .modal-backdrop {
  opacity: 0;
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
</style>
