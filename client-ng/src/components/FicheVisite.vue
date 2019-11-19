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
    <div :class="{ 'col-md-7': !newVisite, 'col-md-12': newVisite }" v-if="selectedRow != null && comboTypeVisitesFiltered.length > 0">
      <div :class="listeVisites[selectedRow].id !== undefined ? 'notActive' : ''">
        <div class="row">
          <div class="col-md-6">
            <b-form-group label="Date " label-for="date" label-cols-md="3">
              <b-form-input id="date" v-model="formattedDate[selectedRow]" type="date" size="sm" required></b-form-input>
            </b-form-group>
          </div>
          <div class="col-md-6">
            <b-form-group label="Heure " label-for="heure" label-cols-md="3">
              <b-form-input id="heure" v-model="formattedTime[selectedRow]" type="time" size="sm" required></b-form-input>
            </b-form-group>
          </div>
        </div>
        <div class="row">
          <div class="col-md-6">
            <b-form-group label="Type" label-for="type" label-cols-md="3">
              <b-form-select id="type" v-model="listeVisites[selectedRow].type" :options="comboTypeVisitesFiltered" size="sm" v-on:change="onTypeVisiteChange" invalid-feedback="Un type de visite doit être renseigné" :state="etats.type" required>
              </b-form-select>
            </b-form-group>
          </div>
          <div class="col-md-6 vertical-bottom">
            <b-form-checkbox id="ctrl_debit_pression" v-model="listeVisites[selectedRow].ctrl_debit_pression" v-on:change="onCtrlDebitPressionChecked" :disabled="ctrlDebitPressionDisabled" size="sm"> Contrôle débit et pression (CDP)
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
                <b-form-group label="Débit à 1 bar (m3/h) :" label-for="debit" label-cols-md="6">
                  <b-form-input id="debit" v-model="listeVisites[selectedRow].debit" type="number" size="sm" :disabled="!saisieDebitPression"></b-form-input>
                </b-form-group>
                <b-form-group label="Pression dynamique à 60 m3 (bar) :" label-for="pressionDyn" label-cols-md="6">
                  <b-form-input id="pressionDyn" v-model="listeVisites[selectedRow].pressionDyn" type="number" step="any" size="sm" :disabled="!saisieDebitPression"></b-form-input>
                </b-form-group>
                <b-form-group label="Débit max (m3/h) :" label-for="debitMax" label-cols-md="6">
                  <b-form-input id="debitMax" v-model="listeVisites[selectedRow].debitMax" type="number" size="sm" :disabled="!saisieDebitPression"></b-form-input>
                </b-form-group>
                <b-form-group label="Pression dynamique au débit max (bar) :" label-for="pressionDynDeb" label-cols-md="6">
                  <b-form-input id="pressionDynDeb" v-model="listeVisites[selectedRow].pressionDynDeb" type="number" step="any" size="sm" :disabled="!saisieDebitPression"></b-form-input>
                </b-form-group>
                <b-form-group label="Pression statique (bar) :" label-for="pression" label-cols-md="6">
                  <b-form-input id="pression" v-model.number="listeVisites[selectedRow].pression" type="number" step="any" size="sm" :disabled="!saisieDebitPression"></b-form-input>
                </b-form-group>
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
              <div class="row" :class="listeVisites[selectedRow].id !== undefined ? 'notActive' : ''">
                <div class="col-md-12">
                  <b-form-textarea id="observations" v-model="listeVisites[selectedRow].observations" placeholder="Observations..." rows="3" size="sm" max-rows="6">
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
import axios from 'axios'
import _ from 'lodash'
import moment from 'moment'
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
      dataLoaded: false
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
      return this.hydrant.code != "PIBI" || this.selectedRow == null || (this.typesVisites[this.listeVisites[this.selectedRow].type].code != "CTRL" && this.typesVisites[this.listeVisites[this.selectedRow].type].code != "CREA" && this.typesVisites[
        this.listeVisites[this.selectedRow].type].code != "RECEP");
    },
    /** Le bouton "Nouvelle visite" est désactivé ou non en fonction des droits de l'utilisateur
     * Les droits sont en fonction des types de visite qui dépend du nombre de visites déjà effectuées
     */
    createVisiteDisabled: function() {
      //On ne peut créer qu'une visite à la fois. Si une visite sans id est présente en 1ere position, on en déduit qu'elle vient d'être créée
      if (this.listeVisites.length > 0 && !this.listeVisites[0].id) {
        return true;
      }
      var disabled = false;
      switch (this.listeVisites.length) {
        case 0:
          disabled = (this.utilisateurDroits.indexOf("HYDRANTS_CREATION_C") == -1);
          break;
        case 1:
          disabled = (this.utilisateurDroits.indexOf("HYDRANTS_RECEPTION_C") == -1);
          break;
          // Autorisation de la création si présence d'au moins 1 des 3 droits
        default:
          disabled = (this.utilisateurDroits.indexOf("HYDRANTS_CONTROLE_C") == -1) && (this.utilisateurDroits.indexOf("HYDRANTS_RECONNAISSANCE_C") == -1) && (this.utilisateurDroits.indexOf("HYDRANTS_ANOMALIES_C") == -1);
          break;
      }
      return disabled;
    },
    /**
     * Booléen déterminant si le bouton de suppression des visites est désactivé
     * On peut supprimer une visite si les conditions suivantes sont réunies:
     *		- La visite n'est pas protégée (CREA ou RECEP, soit les deux premières visites d'un hydrant)
     * 	- La visite est la plus récente (index 0): on supprime par "désempilage" (structure LIFO): nécessaire pour le calcul des anomalies
     *		- On a le droit de supprimer une visite de ce type OU il s'agit de la visite créée grâce au bouton "nouvelle visite", donc une visite non présente dans la BDD
     */
    deleteVisiteDisabled: function() {
      var hasRightToDelete = false;
      if (this.selectedRow !== null && this.listeVisites[this.selectedRow].type) {
        let codeVisite = this.typesVisites[this.listeVisites[this.selectedRow].type].code;
        hasRightToDelete = (this.utilisateurDroits.indexOf('HYDRANTS_VISITE_' + codeVisite + '_D') > -1);
      }
      return (this.selectedRow > 0 || (!this.createVisiteDisabled && !hasRightToDelete));
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
      return this.anomalies.filter(item => item.indispo[this.hydrant.nature] != null && item.critereCode == this.anomaliesCriteres[this.indexCritere].code && item.indispo[this.hydrant.nature].saisies.indexOf(this.typesVisites[this.listeVisites[this
        .selectedRow].type].code) > -1);
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

      self.anomaliesCriteres.sort(function(a, b) {
        return a.id - b.id;
      });
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
        ctrl_debit_pression: false
      }
      this.listeVisites.unshift(visite);
      var splitDate = visite.date.split(" ");
      this.formattedDate.unshift(splitDate[0]);
      this.formattedTime.unshift(splitDate[1]);
      this.onRowSelected(0); // Sélection automatique de la visite créée
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
     */
    onTypeVisiteChange() {
      this.listeVisites[this.selectedRow].ctrl_debit_pression = false;
      this.indexCritere = -1;
      this.critereSuivant();
    },
    /**
     * En cas de désactivation de la case "Contrôle débit et pression", on vide les champs correspondant au débit et à la pression
     */
    onCtrlDebitPressionChecked(value) {
      if (!value) {
        var item = this.listeVisites[this.selectedRow];
        item.debit = null;
        item.debitMax = null;
        item.pression = null;
        item.pressionDyn = null;
        item.pressionDynDeb = null;
      }
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
        })
      }
      return this.etats;
    },
    /**
     * Renvoie les données d'anomalies, de débit et pression afin d'être passé en paramètre de la requête de mise à jour du PEI
     * Fonction appelée par le composant parent lors de la récupération des données du PEI
     */
    updateDataFromLastVisite() {
      // On réunit les deux champs date et time de chaque visite en une seule donnée formattée correctement pour le serveur
      _.forEach(this.listeVisites, (visite, index) => {
        visite.date = this.formattedDate[index] + " " + this.formattedTime[index] + ":00";
      }, this);
      var data = {};
      this.listeVisites.sort(function(a, b) {
        return new Date(b.date) - new Date(a.date);
      });

      var dates = {
        CREA : null,
        CTRL : null,
        RECEP : null,
        RECO : null
      }

      /**
       * On parcourt toutes les visites, de la plus récente à la plus ancienne afin de trouver la première qui ne soit pas de type "non programmée"
       * Ses anomalies sont les anomalies du PEI => on fait remonter ces données dans les data envoyées pour la mise à jour du PEI
       */
      if (this.listeVisites.length > 0 && this.anomaliesRequeteResult) {
        data["anomalies"] = (this.listeVisites[0].anomalies) ? this.anomaliesRequeteResult.filter(item => this.listeVisites[0].anomalies.indexOf(item.id) != -1) : null;
      }
      //Gestion de l'indisponibilité temporaire
      if (_.findIndex(_.flattenDeep(this.hydrant.anomalies), ['code', 'INDISPONIBILITE_TEMP']) != -1) {
        data["anomalies"] = _.concat(data["anomalies"], this.hydrant.anomalies.filter(item => item.code == 'INDISPONIBILITE_TEMP'));
      }
      /** On recherche la visite de type contrôle technique périodique débit pression la plus récente
       * Ce sont ses valeurs de débit et pression que prendront les attributs éponymes du PEI
       * Si il n'y a pas ce type de visite, on prend les valeurs de débit pression de la visite de ROI (si elle existe et que les données sont renseignées)
       * Si il n'y a pas de visite de réception, on regarde les valeurs de la visite de réception (suivant les mêmes critères)
       *
       * Si aucune visite n'est trouvée, les valeurs sont nulles
       */
      if (this.hydrant.code == "PIBI" && this.listeVisites.length > 0) {
        var found = false;
        for (var i = 0; i < this.listeVisites.length && !found; i++) {
          if ((this.typesVisites[this.listeVisites[i].type].code === "CTRL" || this.typesVisites[this.listeVisites[i].type].code === "RECEP" || this.typesVisites[this.listeVisites[i].type].code === "CREA") && this.listeVisites[i]
            .ctrl_debit_pression) {
            var item = this.listeVisites[i];
            data["debit"] = typeof(Number(item.debit)) === 'number' ? item.debit : null;
            data["debitMax"] = typeof(Number(item.debitMax)) === 'number' ? item.debitMax : null;
            data["pression"] = typeof(Number(item.pression)) === 'number' ? item.pression : null;
            data["pressionDyn"] = typeof(Number(item.pressionDyn)) === 'number' ? item.pressionDyn : null;
            data["pressionDynDeb"] = typeof(Number(item.pressionDynDeb)) === 'number' ? item.pressionDynDeb : null;
            found = true;
          }
        }
        if (!found) {
          data["debit"] = null;
          data["debitMax"] = null;
          data["pression"] = null;
          data["pressionDyn"] = null;
          data["pressionDynDeb"] = null;
        }
      }
      if (this.listeVisites && this.listeVisites.length > 0) {
        var visite = this.listeVisites[0];
        data["agent1"] = visite.agent1;
        data["agent2"] = visite.agent2;

        // Mise à jour des dates de l'hydrant
        _.forEach(this.listeVisites, visite => {
          var dateCode = this.typesVisites[visite.type].code;
          var dateMoment = moment(new Date(visite.date), 'DD/MM/YYYY[T]HH:mm:ss[Z]');
          if(dates[dateCode] == null || dateMoment.diff(dates[dateCode]) > 0) {
            dates[dateCode] = dateMoment;
          }
        });
      }

      data["dateCrea"] = (dates.CREA) ? dates.CREA.format('YYYY-MM-DDTHH:mm:ss') : null;
      data["dateRecep"] = (dates.RECEP) ? dates.RECEP.format('YYYY-MM-DDTHH:mm:ss') : null;
      data["dateReco"] = (dates.RECO) ? dates.RECO.format('YYYY-MM-DDTHH:mm:ss') : null;
      data["dateContr"] = (dates.CTRL) ? dates.CTRL.format('YYYY-MM-DDTHH:mm:ss') : null;

      return data;
    },
    /**
     * Envoi les informations de mise à jour des visites au serveur
     */
    prepareVisitesData(id) {
      _.forEach(this.listeVisites, function(item) {
        item.anomalies = "'" + JSON.stringify(item.anomalies) + "'"; // Mise en forme des données d'anomalies pour le passage dans la BDD
        item.hydrant = id;
      });
      return {
        'visites': JSON.stringify(this.listeVisites, function(key, value) {
          return value === "" ? null : value
        }),
        'visitesDel': this.visitesASupprimer
      }
    }
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
</style>
