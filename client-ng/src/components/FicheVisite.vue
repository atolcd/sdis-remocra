<template>
  <div :class="{ 'visites': true, 'loading': !dataLoaded }">
    <div class="row">
      <div class="col-md-5 visites-lst">
        <div class="row">
          <div class="col-md-12">
            <button class="btn btn-outline-primary" @click.prevent @click="createVisite" :disabled="createVisiteDisabled">Nouvelle visite</button>
            <button class="btn btn-outline-danger right" @click.prevent @click="deleteVisite" :disabled="deleteVisiteDisabled">Supprimer</button>
          </div>
        </div>
        <div class="row">
          <div class="col-md-12">
            <div id="tableScroll">
              <table class="table table-striped table-sm table-bordered" id="tableVisites">
                <thead class="thead-light">
                <th scope="col">Date</th>
                <th scope="col">Type</th>
                <th scope="col">Agent</th>
                </thead>
                <tbody>
                <tr v-for="(item, index) in listeVisites" :key="index" @click="onRowSelected(index)">
                  <td>
                    {{dateFormatee(index)}}
                  </td>
                  <td>{{labelTypeSaisie(item)}}
                  </td>
                  <td>
                    {{item.agent1}}
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
      <div :class="{ 'col-md-7': !newVisite, 'col-md-12': newVisite }" v-if="selectedRow != null">
        <div :class="listeVisites[selectedRow].id !== undefined ? 'notActive' : ''">
          <div class="row">
            <div class="col-md-6">
              <b-form-group label="Date " label-for="date" label-cols-md="3">
                <b-form-input id="date" v-model="formattedDate[selectedRow]" type="date" :max="dateMax" size="sm" :state="etats.time" required></b-form-input>
              </b-form-group>
            </div>
            <div class="col-md-6">
              <b-form-group label="Heure " label-for="heure" label-cols-md="3">
                <b-form-input id="heure" v-model="formattedTime[selectedRow]" type="time" size="sm" :state="etats.time" required></b-form-input>
              </b-form-group>
            </div>
          </div>
          <div class="row">
            <div class="col-md-6">
              <b-form-group label="Type" label-for="type" label-cols-md="3">
                <b-form-select id="type" v-model="listeVisites[selectedRow].type" :options="getComboVisites(selectedRow)" size="sm" v-on:change="onTypeVisiteChange" invalid-feedback="Un type de visite doit être renseigné" :state="etats.type" required>
                </b-form-select>
              </b-form-group>
            </div>
            <div class="col-md-6 vertical-bottom">
              <b-form-checkbox id="ctrl_debit_pression" v-model="listeVisites[selectedRow].ctrl_debit_pression" :disabled="ctrlDebitPressionDisabled" size="sm"> Contrôle débit et pression (CDP)
              </b-form-checkbox>
            </div>
          </div>
          <div class="row">
            <div class="col-md-6">
              <b-form-group label="Agent 1" label-for="agent1" label-cols-md="4">
                <b-form-input id="agent1" v-model="listeVisites[selectedRow].agent1" type="text" size="sm"></b-form-input>
              </b-form-group>
            </div>
            <div class="col-md-6">
              <b-form-group label="Agent 2" label-for="agent2" label-cols-md="4">
                <b-form-input id="agent2" v-model="listeVisites[selectedRow].agent2" type="text" size="sm"></b-form-input>
              </b-form-group>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="col-md-12">
            <b-tabs fill content-class="mt-3" active-nav-item-class="text-primary">
              <b-tab title="Mesures" active :disabled="!saisieDebitPression">
                <div :class="listeVisites[selectedRow].id !== undefined ? 'notActive' : ''">
                  <div class="row">
                    <div class="col-md-9">
                      <b-form-group label="Débit à 1 bar (m3/h) :" label-for="debit" label-cols-md="5"  >
                        <b-form-input id="debit" v-model.number="listeVisites[selectedRow].debit" type="number" size="sm" :disabled="!saisieDebitPression"></b-form-input>
                      </b-form-group>
                    </div>
                    <p class="col-sm-3">{{this.getDerniereValeurDebitPression("debit", false)}}</p>
                  </div>
                  <!-- Si le debit nominal est renseigné on l'affiche sinon on affiche juste 'pression dynamique' -->
                  <div class="row">
                    <div class="col-md-9">
                      <b-form-group
                          :label="this.hydrant.debitNominal!==null ?
                    'Pression dynamique au débit nominal de ' + this.hydrant.debitNominal +' m3 (en bar)':
                    'Pression dynamique au débit nominal (en bar)'"
                          label-for="pressionDyn"
                          label-cols-md="5"
                      >
                        <b-form-input id="pressionDyn" v-model.number="listeVisites[selectedRow].pressionDyn" type="number" step="any" size="sm" :disabled="!saisieDebitPression"></b-form-input>
                      </b-form-group>
                    </div>
                    <p class="col-sm-3">{{this.getDerniereValeurDebitPression("pressionDyn", false)}}</p>
                  </div>
                  <div class="row">
                    <div class="col-md-9">
                      <b-form-group label="Pression statique (bar) :" label-for="pression" label-cols-md="5">
                        <b-form-input id="pression" v-model.number="listeVisites[selectedRow].pression" type="number" step="any" size="sm" :disabled="!saisieDebitPression"></b-form-input>
                      </b-form-group>
                    </div>
                    <p class="col-sm-3">{{this.getDerniereValeurDebitPression("pression", false)}}</p>
                  </div>
                </div>
              </b-tab>
              <b-tab class="anomalies-tab" title="Points d'attention">
                <div v-if="anomaliesCriteres[indexCritere] !== undefined">
                  <div :class="listeVisites[selectedRow].id !== undefined ? 'notActive' : ''" v-if="hydrant.nature">
                    <div class="row" id="anomalieCritere">
                      <div class="col-md-12" v-if="this.anomaliesFiltered.length > 0">
                        <p class="bold">{{anomaliesCriteres[indexCritere].nom}}</p>
                      </div>
                    </div>
                    <div class="row">
                      <div class="col-md-12">
                        <b-form-group>
                          <b-form-checkbox-group id="checkbox-group-2" v-model="listeVisites[selectedRow].anomalies" name="flavour-2">
                            <table class="table table-striped table-sm table-bordered" id="tableAnomalies">
                              <tbody>
                              <tr v-for="(item,index) in anomaliesFiltered" :key="index" class="rowAnomalie">
                                <b-form-checkbox :value="item.id" :class="getAnomalieClass(index)">{{item.nom}}</b-form-checkbox>
                              </tr>
                              </tbody>
                            </table>
                          </b-form-checkbox-group>
                        </b-form-group>
                      </div>
                    </div>
                  </div>
                  <div class="row">
                    <div class="col-md-12">
                      <b-button @click.prevent @click="criterePrecedent" class="btn btn-primary" size="sm" :disabled="anomaliePrecedentDisabled">Précédent</b-button>
                      <b-button @click.prevent @click="critereSuivant" class="btn btn-secondary right" size="sm" :disabled="anomalieSuivantDisabled">Suivant</b-button>
                    </div>
                  </div>
                </div>
                <div v-else>
                  <p>Chargement des données des anomalies...</p>
                </div>
              </b-tab>
              <b-tab title="Observations">
                <div class="row">
                  <div class="col-md-12">
                    <b-form-textarea :readonly="listeVisites[selectedRow].id !== undefined" id="observations" v-model="listeVisites[selectedRow].observations" placeholder="Observations..." rows="3" size="sm" max-rows="6" >
                    </b-form-textarea>
                  </div>
                </div>
              </b-tab>
            </b-tabs>
          </div>
        </div>
      </div>

      <!-- Message d'erreur si aucune création de visite n'est possible lors du clic sur le bouton "Saisir une visite" (par exemple, l'utilisateur n'a pas les droits) -->
      <div v-else-if="dataLoaded && newVisite" class="col-md-12">
        <b-alert variant="danger" show>
          <p>La création de visite est impossible <br /> Votre profil ne dispose pas des droits suffisants pour créer des visites dont le type est actuellement attendu par ce point d'eau </p>
        </b-alert>
      </div>
    </div>
  </div>
</template>

<script>
import moment from 'moment'
import axios from 'axios'
import _ from 'lodash'
export default {
  name: 'FicheVisite',
  data() {
    return {
      listeVisites: [],
      typesVisites: {},
      selectedRow: null,
      comboTypeVisites: [],
      comboTypeVisitesFiltered: [],
      nbVisitesInitiales: 0,
      visitesASupprimer: [],
      typeNouvellesVisitesEtat1: null, // L'id du type de visite à la création (forcé)
      typeNouvellesVisitesEtat2: null, // L'id du type de visite à la seconde visite (forcé)
      formattedDate: [], // tableau contenant la version formattée des dates des visites
      formattedTime: [], // tableau contenant la version formattée des heures des visites
      anomaliesRequeteResult: null, // Résultat de la requête de récupération des visites,
      anomalies: [], // Liste de toutes les anomalies disponibles
      anomaliesCriteres: [], // Liste des critères dans lesquels sont regroupés les anomalies
      indexCritere: 0,
      etats: {
        date: null,
        time: null,
        noSameDateTime: null,
        type: null
      },
      dataLoaded: false,

      // Valeur maximales des données temporelles
      dateMax: moment().format('YYYY-MM-DD')
    }
  },
  props: {
    hydrant: {
      required: true,
      type: Object
    },
    utilisateurDroits: {
      required: true,
      type: Array
    },
    newVisite: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    /**
     * Label du type de saisie
     * Lors d'un contrôle technique périodique, indique si celui-ci est un contrôle débit pression (CDP) ou bien un contrôle fonctionnel
     * Le label est mis à jour automatiquement dans la datagrid des visites
     */
    labelTypeSaisie: function() {
      var self = this;
      return function(item) {
        if (!item.type || !self.typesVisites[item.type]) {
          return '';
        }
        if (self.typesVisites[item.type].code === "CTRL") {
          if (item.ctrl_debit_pression) {
            return self.typesVisites[item.type].nom + ' (CDP)';
          }
          return self.typesVisites[item.type].nom + ' (CF)';
        }
        return self.typesVisites[item.type].nom;
      }
    },
    /**
     * Date au format dd/MM/yyyy pour l'affichage dans la datagrid des visites
     */
    dateFormatee: function() {
      var self = this;
      return function(index) {
        return (new Date(self.formattedDate[index])).toLocaleDateString("fr-FR")
      }
    },
    /**
     * Etat (activé ou désactivé) du bouton contrôle débit pression
     */
    ctrlDebitPressionDisabled: function() {
      if (!this.listeVisites[this.selectedRow].type) {
        return true;
      }
      return this.hydrant.code != "PIBI" || this.selectedRow == null || (this.typesVisites[this.listeVisites[this.selectedRow].type].code != "CTRL" && this.typesVisites[this.listeVisites[this.selectedRow].type].code != "CREA");
    },

    isNewVisite: function (){
      //On ne peut créer qu'une visite à la fois. Si une visite sans id est présente en 1ere position, on en déduit qu'elle vient d'être créée
      return (this.listeVisites.length > 0 && !this.listeVisites[0].id);
    },
    /** Le bouton "Nouvelle visite" est désactivé ou non en fonction des droits de l'utilisateur
     * Les droits sont en fonction des types de visite qui dépend du nombre de visites déjà effectuées
     */
    createVisiteDisabled: function() {

      var disabled = false;
      switch (this.listeVisites.length) {
        case 0:
          disabled = (this.utilisateurDroits.indexOf("HYDRANTS_CREATION_C") === -1);
          break;
        case 1:
          disabled = (this.utilisateurDroits.indexOf("HYDRANTS_RECEPTION_C") === -1);
          break;
          // Autorisation de la création si présence d'au moins 1 des 3 droits
        default:
          disabled = (this.utilisateurDroits.indexOf("HYDRANTS_CONTROLE_C") === -1)
              && (this.utilisateurDroits.indexOf("HYDRANTS_RECONNAISSANCE_C") === -1)
              && (this.utilisateurDroits.indexOf("HYDRANTS_ANOMALIES_C") === -1);
          break;
      }
      //Si disabled OU new visite on retourne true
      return (disabled || this.isNewVisite);
    },
    /**
     * Booléen déterminant si le bouton de suppression des visites est désactivé
     * On peut supprimer une visite si les conditions suivantes sont réunies:
     *		- La visite n'est pas protégée (CREA ou RECEP, soit les deux premières visites d'un hydrant)
     * 	- La visite est la plus récente (index 0): on supprime par "désempilage" (structure LIFO): nécessaire pour le calcul des anomalies
     *		- On a le droit de supprimer une visite de ce type OU il s'agit de la visite créée grâce au bouton "nouvelle visite", donc une visite non présente dans la BDD
     */
    deleteVisiteDisabled: function() {
      let hasRightToDelete = false;
      if (this.selectedRow !== null && this.listeVisites[this.selectedRow].type) {
        let codeVisite = this.typesVisites[this.listeVisites[this.selectedRow].type].code;
        hasRightToDelete = (this.utilisateurDroits.indexOf('HYDRANTS_VISITE_' + codeVisite + '_D') > -1);
      }
      let newVisite = this.isNewVisite

      // Si c'est la première ligne ET que tu a les droits de delete on "disable:false"
      // OU si newVisite on disable false
      //si ni l'un ni l'autre un disable le bouton supprimer
      let disable  = !((this.selectedRow === 0 && hasRightToDelete) || newVisite)

      return (disable);
    },
    /**
     * TRUE si l'utilisateur souhaite saisir une visite de type Contrôle avec débit/pression
     * FALSE sinon
     */
    saisieDebitPression: function() {
      return !this.ctrlDebitPressionDisabled && this.listeVisites[this.selectedRow].ctrl_debit_pression;
    },
    /**
     * Désactivation des boutons "Suivant" et "Précédent" en fin de parcours des anomalies
     * Si tous les critères suivant ou tous les précédents n'ont aucune anomalie disponibles, on arrête également
     */
    anomalieSuivantDisabled: function() {
      let isLastCritere = true;
      for (var i = this.indexCritere + 1; i < this.anomaliesCriteres.length && isLastCritere; i++) {
        if (this.nbAnomaliesParCritere(i) != 0) {
          isLastCritere = false;
        }
      }
      return this.indexCritere == this.anomaliesCriteres.length - 1 || isLastCritere;
    },
    anomaliePrecedentDisabled: function() {
      let isFirstCritere = true;
      for (var i = this.indexCritere - 1; i >= 0 && isFirstCritere; i--) {
        if (this.nbAnomaliesParCritere(i) != 0) {
          isFirstCritere = false;
        }
      }
      return this.indexCritere == 0 || isFirstCritere;
    },
    /**
     * Retourne, pour une anomalie donnée, le style CSS à appliquer
     * Si provoque une indisponibilité: gras
     * Si provoque une indisponibilité HBE: souligné
     * Les styles sont cumulables
     */
    getAnomalieClass: function() {
      var self = this;
      return function(index) {
        var classes = "";
        if (self.anomaliesFiltered[index].indispo[this.hydrant.nature].valIndispoTerrestre) {
          classes += "bold ";
        }
        if (self.anomaliesFiltered[index].indispo[this.hydrant.nature].valIndispoHbe) {
          classes += "underline ";
        }
        return classes;
      }
    },
    /**
     * Renvoie une liste d'anomalies filtrées depuis 'anomalies'
     * Les anomalies filtrées sont celles correspondant à la nature du type de PEI, du critère sélectionné ainsi qu'au type de saisie actuellement effectué
     */
    anomaliesFiltered: function() {
      if (!this.listeVisites[this.selectedRow] || this.listeVisites[this.selectedRow].type == null || this.anomaliesCriteres[this.indexCritere] == null) {
        return [];
      }
      var anosFiltered = this.anomalies.filter(item => item.indispo[this.hydrant.nature] != null && item.critereCode == this.anomaliesCriteres[this.indexCritere].code && item.indispo[this.hydrant.nature].saisies.indexOf(this.typesVisites[this.listeVisites[this
          .selectedRow].type].code) > -1);
      // On trie sur le nom de l'anomalie
      return _.sortBy(anosFiltered, ['nom']);
    }
  },
  mounted: function() {
    var self = this;
    let requests = []
    /* =============================================== Récupération des visites =============================================== */
    if (this.hydrant.id != null) {
      requests.push(axios.get('/remocra/hydrantvisite.json', {
        params: {
          filter: JSON.stringify([{
            "property": "hydrant",
            "value": this.hydrant.id
          }]),
          sort: JSON.stringify([{
            "property": "date",
            "direction": "DESC"
          }])
        }
      }).then(response => {
        if (response.data.data) {
          self.listeVisites = response.data.data;
          // Pour le datetime des visites, on sépare la donnée en deux champs distincts date et time
          _.forEach(self.listeVisites, item => {
            var splitDate = item.date.split("T");
            self.formattedDate.push(splitDate[0]);
            self.formattedTime.push(splitDate[1].substr(0, splitDate[1].length - 3));
            item.type = (item.type) ? item.type.id : null;
            item.hydrant = self.hydrant.id;
            item.anomalies = (item.anomalies) ? JSON.parse(item.anomalies) : [];
          });
          self.nbVisitesInitiales = self.listeVisites.length;
        }
      }).catch(function(error) {
        console.error('Retrieving data from remocra/hydrantvisite', error);
      }))
    } else {
      this.nbVisitesInitiales = 0;
    }
    /* =============================================== Liaison entre l'id des types de visites et leur libelle =============================================== */
    // On récupère dans le même temps les identifiants des types de visite correspondant aux états 1 et 2 des PEI; ansi que le type par défaut pour l'état 3
    requests.push(axios.get('/remocra/typehydrantsaisies.json').then(response => {
      _.forEach(response.data.data, function(item) {
        if (item.code != 'LECT') {
          self.comboTypeVisites.push({
            text: item['nom'],
            value: item['id']
          });
          self.comboTypeVisitesFiltered = self.comboTypeVisites;
          if (item.code == 'CREA') {
            self.typeNouvellesVisitesEtat1 = item.id;
          } else if (item.code == 'RECEP') {
            self.typeNouvellesVisitesEtat2 = item.id;
          }
        }
        self.typesVisites[item.id] = {
          nom: item.nom,
          code: item.code
        };
      })
    }).catch(function(error) {
      console.error('Retrieving combo data from /remocra/typehydrantsaisie', error);
    }))
    /* =============================================== Récupération des anomalies =============================================== */
    requests.push(axios.get('/remocra/typehydrantanomalies.json').then(response => {
      this.anomaliesRequeteResult = response.data.data;

      // On met en forme les données depuis le résultat de la requête (on utilise ici un controller déjà existant)
      _.forEach(response.data.data, function(item) {

        if(item.actif){
          var a = {};
          a.code = item.code;
          a.nom = item.nom
          a.id = item.id;
          a.critereCode = (item.critere) ? item.critere.code : null;
          a.critereNom = (item.critere) ? item.critere.nom : '-';
          a.indispo = {};
          _.forEach(item.anomalieNatures, function(nature) {
            a.indispo[nature.nature.id] = {};
            a.indispo[nature.nature.id].valIndispoAdmin = nature.valIndispoAdmin;
            a.indispo[nature.nature.id].valIndispoHbe = nature.valIndispoHbe;
            a.indispo[nature.nature.id].valIndispoTerrestre = nature.valIndispoTerrestre;
            a.indispo[nature.nature.id].natureCode = nature.nature.code;
            a.indispo[nature.nature.id].saisies = [];
            _.forEach(nature.saisies, function(saisie) {
              a.indispo[nature.nature.id].saisies.push(saisie.code);
            })
          });
          // On récupère la liste des critères
          var critereId = (item.critere) ? item.critere.id : null;
          a.critere = critereId;
          if (critereId != null && _.findIndex(self.anomaliesCriteres, function(o) {
            return o.id != null && o.id == critereId;
          }) == -1) {
            self.anomaliesCriteres.push(item.critere);
          }
          self.anomalies.push(a);
        }
      });

      this.anomaliesCriteres.sort((a, b) => (a.code > b.code) ? 1 : -1)

      if (this.selectedRow >= 0) {
        this.critereSuivant();
      }
    }).catch(function(error) {
      if (this) {
        console.error('Retrieving data from /remocra/typehydrantanomalies', error);
      }
    }))
    axios.all(requests).then(function() {
      self.dataLoaded = true
      self.$root.$options.bus.$emit('pei_visite_ready')
    })
  },
  methods: {
    /**
     * Création d'une nouvelle visite
     * La visite est créée en BDD, initialisée à la date d'aujourd'hui et ayant un type déterminé en fonction du nombre de visites déjà existantes
     */
    createVisite() {
      /*Suivant le nombre de visites déjà effectuées, on détermine le type par défaut de la nouvelle visite
      	1ere visite: Visite de création
      	2e visite: Reconnaissance opérationnelle initiale
      	3e visite: Lecture
      */
      var typeVisite;
      switch (this.listeVisites.length) {
        case 0:
          typeVisite = this.typeNouvellesVisitesEtat1;
          break;
        case 1:
          typeVisite = this.typeNouvellesVisitesEtat2;
          break;
        default:
          typeVisite = null;
          break;
      }
      var anomalies = [];
      if (this.listeVisites.length != 0) {
        anomalies = (this.listeVisites[0].anomalies) ? this.listeVisites[0].anomalies : [];
      }
      var visite = {
        type: typeVisite,
        date: moment().format("YYYY-MM-DD HH:mm"),
        anomalies: anomalies,
        ctrl_debit_pression: false,
      }
      this.listeVisites.unshift(visite);
      var splitDate = visite.date.split(" ");
      this.formattedDate.unshift(splitDate[0]);
      this.formattedTime.unshift(splitDate[1]);
      this.onRowSelected(0); // Sélection automatique de la visite créée
      // on va chercher les anciennes valeurs pour les mettre par défaut
      //important d'aller les chercher aprés le this.onRowSelected(0).
      visite.debit = this.getDerniereValeurDebitPression("debit", true);
      visite.pression = this.getDerniereValeurDebitPression("pression", true);
      visite.pressionDyn = this.getDerniereValeurDebitPression("pressionDyn", true);
      this.indexCritere = -1;
      this.critereSuivant();
    },
    /**
     * Suppression d'une visite
     * Les deux premières visites d'un PEI (CREA et RECEP) ne peuvent êtres effacées
     */
    deleteVisite() {
      if (this.selectedRow === null) {
        return null;
      }
      var idVisite = this.listeVisites[this.selectedRow].id;
      if (idVisite != undefined) {
        this.visitesASupprimer.push(idVisite);
      }
      this.listeVisites.splice(self.selectedRow, 1);
      this.formattedDate.splice(self.selectedRow, 1);
      this.formattedTime.splice(self.selectedRow, 1);
      this.selectedRow = null;
      this.onRowSelected(null);
    },
    /**
     * Mise à jour de la combo "type" de la visite
     * Les types proposés sont dépendants du nombre de visites déjà effectués (les deux premières visites ont notamment un type prédéfini)
     */
    updateComboTypeVisitesFiltered() {
      if (this.selectedRow === null) {
        return null;
      }
      var self = this;
      if (this.listeVisites.length == 1) {
        this.comboTypeVisitesFiltered = this.comboTypeVisites.filter(item => self.typesVisites[item.value].code == "CREA" && self.utilisateurDroits.indexOf('HYDRANTS_CREATION_C') != -1);
      } else if (this.listeVisites.length == 2) {
        this.comboTypeVisitesFiltered = this.comboTypeVisites.filter(item => self.typesVisites[item.value].code == "RECEP" && self.utilisateurDroits.indexOf('HYDRANTS_RECEPTION_C') != -1);
      } else {
        let notIncluded = ["CREA", "RECEP"];
        this.comboTypeVisitesFiltered = this.comboTypeVisites.filter(item => (notIncluded.indexOf(self.typesVisites[item.value].code) == -1) && (self.typesVisites[item.value].code == "CTRL" && self.utilisateurDroits.indexOf('HYDRANTS_CONTROLE_C') !=
            -1) || (self.typesVisites[item.value].code == "RECO" && self.utilisateurDroits.indexOf('HYDRANTS_RECONNAISSANCE_C') != -1) || (self.typesVisites[item.value].code == "NP" && self.utilisateurDroits.indexOf('HYDRANTS_ANOMALIES_C') != -1));
      }
      if (this.comboTypeVisitesFiltered.length && this.selectedRow !== null && !this.listeVisites[this.selectedRow].type) {
        this.listeVisites[this.selectedRow].type = this.comboTypeVisitesFiltered[0].value;
      }
    },
    /**
     * Lorsqu'une ligne est sélectionnée, elle obtient la classe "table-success"
     * Une ligne est sélectionnée si l'on clique dessus
     */
    onRowSelected(index) {
      var table = document.getElementById('tableVisites');
      _.forEach(table.getElementsByTagName('tr'), item => {
        item.classList.remove("table-success");
        if (item.rowIndex == index) {
          item.classList.add("table-success");
        }
      });
      this.selectedRow = index;
      this.updateComboTypeVisitesFiltered();
      this.indexCritere = -1;
      this.critereSuivant();
    },

    /**
     * En cas de changement de type de visite, on décoche toujouts la checkbox "Contrôle débit et pression"
     * de plus, les anomalies reviennent à leur état initial
     */
    onTypeVisiteChange() {
      this.listeVisites[this.selectedRow].ctrl_debit_pression = false;
      if (this.listeVisites.length > 1) {
        this.listeVisites[this.selectedRow].anomalies = (this.listeVisites[1].anomalies) ? this.listeVisites[1].anomalies : [];
      }
      this.indexCritere = -1;
      this.critereSuivant();
    },
    /**
     * Lors d'un clic sur le bouton "Suivant" de la sélection des anomalies
     * on se déplace au prochain critère non vide (fonction récursive)
     */
    critereSuivant() {
      if (this.indexCritere < this.anomaliesCriteres.length - 1) {
        this.indexCritere++;
        if (!this.anomaliesFiltered.length) {
          this.critereSuivant();
        }
      }
    },
    /**
     * Lors d'un clic sur le bouton "Précédent" de la sélection des anomalies
     * on se déplace au précédent critère non vide (fonction récursive)
     */
    criterePrecedent() {
      if (this.indexCritere > 0) {
        this.indexCritere--;
        if (!this.anomaliesFiltered.length) {
          this.criterePrecedent();
        }
      }
    },
    /**
     * Renvoie le nombre d'anomalies pour un critère donné
     * Les anomalies disponibles sont fonction du type de visite et du pei
     * @param index L'index du critère situé dans this.anomaliesCriteres
     */
    nbAnomaliesParCritere(index) {
      if (!this.listeVisites[this.selectedRow].type) {
        return 0;
      }
      return this.anomalies.filter(item => item.indispo[this.hydrant.nature] != null && item.critereCode == this.anomaliesCriteres[index].code && item.indispo[this.hydrant.nature].saisies.indexOf(this.typesVisites[this.listeVisites[this.selectedRow]
          .type].code) > -1).length;
    },
    /**
     * En cas de changement de nature du PEI, on met à jour les anomalies disponibles
     * Les anomalies sont dépendantes de cette valeur
     * Si aucune visite n'est renseignée, ce changement n'a aucun effet
     */
    onNatureChange() {
      this.indexCritere = -1;
      this.critereSuivant();
    },

    /**
     * Retourne la combo à utiliser pour les types de visites
     * @param selectedRow L'index de la visite
     */
    getComboVisites(selectedRow) {
      var visite = this.listeVisites[selectedRow];

      // Nouvelle visite : on retourne la combo des types filtrés selon les droits dont l'utilisateur dispose
      if(visite != null && visite.id == undefined) {
        return this.comboTypeVisitesFiltered;
      }

      // Ancienne visite: quelque soient les droits de l'utilisateur, on utilise la combo standart
      return this.comboTypeVisites;
    },

    checkFormValidity() {
      var regexDate = /([12]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01]))/;
      var regexTime = /^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/;
      this.etats.date = 'valid';
      _.forEach(this.formattedDate, date => {
        if (!regexDate.test(date)) {
          this.etats.date = 'invalid';
        }
      });
      this.etats.time = 'valid';
      _.forEach(this.formattedTime, time => {
        if (!regexTime.test(time)) {
          this.etats.time = 'invalid';
        }
      });
      this.etats.type = 'valid';
      _.forEach(this.listeVisites, visite => {
        if (visite.type === null) {
          this.etats.type = 'invalid';
        }
        //si contrôle débit n'est pas coché on reset les valeurs débit pression
        //ça permet de supprimer les valeurs qui auraient pu être affectées avant de décocher
        //la case.
        if(!visite.ctrl_debit_pression){
          visite.debit = null;
          visite.pression = null;
          visite.pressionDyn = null;
        }
      });
      // Si deux visites sont à des dates et heures identiques, on affiche une alerte et on bloque la validation
      if (this.listeVisites.length > 0) {
        this.etats.noSameDateTime = 'valid';
        var tabDates = [];
        _.forEach(this.listeVisites, (visite, index) => {
          var date = this.formattedDate[index] + " " + this.formattedTime[index];
          if (tabDates.indexOf(date) != -1) { // Une visite avec cette date existe déjà
            this.etats.noSameDateTime = 'invalid';
            this.$notify({
              group: 'remocra',
              title: 'Saisie invalide',
              type: 'error',
              text: 'Au moins deux visites sont renseignées pour la même date et la même heure sur ce point d\'eau'
            })
          } else {
            tabDates.push(date);
          }

          // Si une visite est à une date future, on bloque la validation
          this.etats.noFutureDate = 'valid';
          var timeMax = moment().format('HH:mm');
          this.dateMax = moment().format('YYYY-MM-DD');
          if(moment(this.dateMax+" "+timeMax).diff(moment(date)) < 0) {
            this.etats.date = 'invalid';
            this.etats.time = 'invalid';
            this.$notify({
              group: 'remocra',
              title: 'Saisie invalide',
              type: 'error',
              text: 'Une visite est renseignée à une date future'
            })
          }
        })
      }
      return this.etats;
    },

    /**
     * Envoi les informations de mise à jour des visites au serveur
     */
    prepareVisitesData() {
      var newVisite = null;
      // Si visite sans id présente en 1ere position => nouvelle visite
      if(this.listeVisites.length > 0 && !this.listeVisites[0].id) {
        newVisite = this.listeVisites[0];
        newVisite.date = this.formattedDate[0] + " " + this.formattedTime[0] + ":00";
      }

      return {
        'addVisite': JSON.stringify(newVisite),
        'deleteVisite' : this.visitesASupprimer
      }
    },

    /**
     * Permet de récupérer les anciennes valeurs saisies (débit et pression)
     * @param nomValeur : string de l'ancienne valeur à récupérer
     * @param isJustValue : boolean true si on veut juste la valeur false si on veut le message avec
     */
    getDerniereValeurDebitPression(nomValeur, isJustValue) {
      // On vérifie s'il s'agit d'une visite en cours de création
      // Si ce n'est pas le cas, on affiche pas l'ancienne valeur
      if( this.listeVisites[this.selectedRow].id === undefined ){
        for(var i = 1; i  < this.listeVisites.length -1; i++) {
          if(this.listeVisites[i].ctrl_debit_pression) {
            if(isJustValue){
              return this.listeVisites[i][nomValeur]
            }else{
              return "(Ancienne valeur saisie : "+this.listeVisites[i][nomValeur]+")"
            }
          }
        }
      }
      return null
    },
  }
};
</script>

<style scoped>
.vertical-bottom {
  display: flex;
  align-items: center;
}

.right {
  float: right;
}

.bold {
  font-weight: bold;
}

.underline {
  text-decoration: underline;
}

#anomalieCritere {
  margin-bottom: 10px;
  font-size: 16px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
}

.rowAnomalie {
  font-size: 12px;
}

#tableScroll {
  margin-top: 5px;
  max-height: 450px;
  overflow: auto;
}

#tableScroll th {
  text-align: left;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
  font-size: 1rem;
  color: #495057;
  font-size: 1rem;
  font-weight: 400;
  line-height: 1.5;
  vertical-align: middle;
}

.anomalies-tab {
  max-height: 300px;
  overflow-x: hidden;
  overflow-y: auto;
}

.notActive {
  pointer-events: none;
  opacity: 0.4;
}

.visites.loading::after {
  content: 'Initialisation visites...';
  margin-left: calc(50% - 72px);
  animation: opacity-anim 1s linear infinite;
}

.debitDynAutre {
  border-style: solid;
  border-width: 1px;
  padding-top: 5px;
}
</style>
