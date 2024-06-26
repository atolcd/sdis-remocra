<template>
  <div :class="{'ModalSaisieVisite' : true, 'loading': !(dataLoaded && anomaliesLoaded) }">
    <b-modal id="modalSaisieVisite"
             ref="modalSaisieVisite"
             no-close-on-backdrop
             :title="this.modalTitle"
             ok-title="Valider"
             :ok-disabled="!saisieMesuresAnomaliesCompleted"
             :ok-only="this.showResults"
             cancel-title="Annuler"
             size="xl"
             @ok="handleOk">
      <div v-if="this.dataTournee !== null && dataLoaded && anomaliesLoaded && !this.showResults">
        <form>
          <div class="row">
            <div class="col-md-4">
              <b-form-group label="Date " label-for="date" label-cols-md="4">
                <b-form-input id="date" v-model="formDate" type="date" :max="dateMax" size="sm" :state="etats.date" required></b-form-input>
              </b-form-group>
            </div>

            <div class="col-md-4">
              <b-form-group label="Heure " label-for="heure" label-cols-md="4">
                <b-form-input id="heure" v-model="formTime" type="time" size="sm" :state="etats.time" required></b-form-input>
              </b-form-group>
            </div>

            <div class="col-md-4">
              <b-form-group label="Type" label-for="type" label-cols-md="4">
                <b-form-select id="type" v-model="formTypeVisite" :options="comboTypesVisite" size="sm" v-on:change="onTypeVisiteChange" :state="etats.typeVisite" required>
                </b-form-select>
              </b-form-group>
            </div>
          </div>

          <div class="row">
            <div class="col-md-4 checkbox-align">
              <b-form-checkbox :id="ctrl_debit_pression+'-'+_uid" v-model="formCtrlDebitPression" @input="onChangeCtrlDebitPression" :disabled="ctrlDebitPressionDisabled" size="sm"> Contrôle débit pression (CDP)
              </b-form-checkbox>
            </div>

            <div class="col-md-4">
              <b-form-group label="Agent 1" label-for="agent1" label-cols-md="4">
                <b-form-input id="agent1" v-model="formAgent1" type="text" size="sm" :state="etats.agent1" required></b-form-input>
              </b-form-group>
            </div>

            <div class="col-md-4">
              <b-form-group label="Agent 2" label-for="agent1" label-cols-md="4">
                <b-form-input id="agent2" v-model="formAgent2" type="text" size="sm"></b-form-input>
              </b-form-group>
            </div>
          </div>

          <div class="row">
            <div class="col-md-12">
              <div id="tableScroll">
                <table class="table table-striped table-sm table-bordered" id="tableHydrants">
                  <thead class="thead-light">
                    <th scope="col">PEI</th>
                    <th scope="col">Voie</th>
                    <th scope="col">Type</th>
                    <th scope="col">Domaine</th>
                    <th scope="col">Propriétaire</th>
                    <th scope="col">Etat</th>
                    <th scope="col">Date de la dernière ROP</th>
                    <th scope="col">Date de la dernière CTP</th>
                    <th scope="col">Anomalies</th>
                    <th scope="col" class="colRAS"></th>
                  </thead>
                  <tbody>
                    <tr v-for="(item, index) in this.sortedHydrants" :key="index">
                      <td>
                        {{item.numero}}
                      </td>
                      <td>
                        {{item.adresse}}
                      </td>
                      <td>
                        {{item.natureNom}}
                      </td>
                      <td>
                        {{item.codeNatureDeci}}
                      </td>
                      <td>
                        {{item.gestionnaireNom }}
                      </td>
                      <td>
                        <p v-if="item.dispoTerrestre === 'DISPO'" class="bg-success rounded-lg text-light font-weight-bolder dispo">Disponible</p>
                        <p v-else-if="item.dispoTerrestre === 'NON_CONFORME'" class="bg-warning rounded-lg dispo dispoLg text-light">Non conforme</p>
                        <p v-else class="bg-danger rounded-lg text-light font-weight-bolder dispo">Indisponible</p>
                        <span v-if="item.dateChangementDispoTerrestre != null">({{ item.dateChangementDispoTerrestre | printDate }})</span>
                      </td>
                      <td>
                        {{ ((item.dateReco) ? item.dateReco : item.dateRecep) | printDate }}
                      </td>
                      <td>
                        {{ ((item.dateContr) ? item.dateContr : item.dateCrea) | printDate }}
                      </td>
                      <td>
                        {{ getVisiteRecenteAnomalies(item.visites) }}
                      </td>
                      <td>
                        <b-form inline>
                          <!-- On cache le bouton RAS si la case CDP est cochée -->
                          <div v-if="getConditionAffichageRas(item)" class="onoffswitch">
                            <input type="checkbox" class="onoffswitch-checkbox" :id="'switchRAS-'+_uid+'-'+item.id" v-model="item.ras">
                            <label class="onoffswitch-label" :for="'switchRAS-'+_uid+'-'+item.id">
                              <span class="onoffswitch-inner"></span>
                              <span class="onoffswitch-switch"></span>
                            </label>
                          </div>
                          <b-button :variant="getConditionColorMesuresAnomalies(item) ? 'primary' : item.variant"
                                    @click="onClickPointsSpecifiques(item)"
                                    size="sm"
                                    class="boutonMesures"
                                    :disabled="getConditionDisableMesuresAnomalies(item)">Mesures / anomalies
                          </b-button>
                        </b-form>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </form>

      </div>
      <div v-else-if="this.showResults">
        <div v-if="this.showErreurs">
          <b-alert variant="warning" show>
            <p>La totalité des visites n'a pas pu être enregistré par Remocra <br />
              Veuillez trouver ci-dessous la liste des PEI concernés et la raison pour laquelle la visite n'a pas pu être renseignée<br />
              Merci de renseigner la visite manuellement pour chacun de ces PEI
            </p>
          </b-alert>
          <div id="tableScroll">
            <table class="table table-striped table-sm table-bordered" id="tableErreurs">
              <thead class="thead-light">
                <th scope="col">PEI</th>
                <th scope="col">Motif de refus</th>
              </thead>
              <tbody>
                <tr v-for="(erreur, index) in this.erreurs" :key="index">
                  <td>
                    {{hydrants.filter(h => h.id == erreur.id)[0].numero}}
                  </td>
                  <td>
                    {{erreur.message}}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <p>Nombre de PEI à RAS : {{this.getNbPeiRAS}}</p>
        <p>Nombre de PEI avec des anomalies : {{this.getNbPeiAnomalies}}</p>
        <p v-if="!ctrlDebitPressionDisabled">Nombre de PEI sans mesure débit/pression : {{this.getNbPeiSansCDP}}</p>
      </div>
    </b-modal>

    <ModalPointsSpecifiques v-on:saisiePointsSpecifiques="saisiePointsSpecifiques"
                            ref="modalPointsSpecifiques"
                            :saisieDebitPression="formCtrlDebitPression"
                            :anomaliesCriteres="anomaliesCriteres"
                            :typeVisite="comboTypesVisite.filter(a => a.value == formTypeVisite)[0]"
                            :anomalies="anomalies">
    </ModalPointsSpecifiques>

    <notifications group="remocra" position="top right" animation-type="velocity" :duration="3000" />

  </div>
</template>

<script>
import moment from 'moment'
import axios from 'axios'
import _ from 'lodash'

import ModalPointsSpecifiques from './ModalPointsSpecifiques.vue'
import { PENA, PIBI } from '../../GlobalConstants.js'


export default {
  name: 'ModalSaisieVisite',

  components: {
    ModalPointsSpecifiques
  },

  props: {
    tournee: {
      type: String,
      required: true
    }
  },

  data() {
    return {
      dataTournee: null,
      modalTitle: '',
      dataLoaded: false,
      anomaliesLoaded: false,
      dateMax: moment().format('YYYY-MM-DD'),
      utilisateurDroits: null,
      hydrants: null,
      comboTypesVisite: [],
      anomalies: [],
      anomaliesCriteres: [],

      showErreurs: false,
      showResults: false,
      erreurs: null,

      formDate: null,
      formTime: null,
      formTypeVisite: null,
      formCtrlDebitPression: false,
      formAgent1: '',
      formAgent2: '',

      etats: {
        date: null,
        time: null,
        typeVisite: null,
        agent1: null
      }
    }
  },

  computed: {
    sortedHydrants: function() {

      return _.sortBy(this.hydrants,[function(o) {

        return o.commune.insee, o.numeroInterne;

        }])
    },

    ctrlDebitPressionDisabled: function() {
      if(this.formTypeVisite == null) {
        return true;
      }

      var typeVisite = this.comboTypesVisite.filter(a => a.value === this.formTypeVisite)[0].code;
      return (typeVisite === "CREA" || typeVisite === "CTRL") ? false : true;
    },

    // Nombre de PEI sans rien à signaler
    getNbPeiRAS: function() {
      return this.hydrants.filter(h => h.ras == true).length;
    },

    // Nombre de PEI avec des anomalies renseignées
    getNbPeiAnomalies: function() {
      return this.hydrants.filter(h => h.newVisite != null
        && h.newVisite.anomalies != null
        && h.newVisite.anomalies.length > 0).length;
    },

    // Nombre de PEI sans saisie de mesure de débit/pression
    getNbPeiSansCDP: function() {
      return this.hydrants.filter(h => h.newVisite == null || (h.newVisite.debit == null && h.newVisite.debitMax == null &&
        h.newVisite.pression == null && h.newVisite.pressionDyn == null && h.newVisite.pressionDynDeb == null)).length;
    },

    saisieMesuresAnomaliesCompleted: function() {
      if (this.hydrants != null) {
        return this.hydrants.filter(h => {
          let lastVisite = h.visites != null && h.visites.length > 0 ? h.visites[0] : null;
          let anomalieLastVisite = lastVisite !=null && lastVisite.anomalies ? JSON.parse(lastVisite.anomalies): null;
          return h.ras
            || (
              h.newVisite != null &&
              (h.newVisite.anomalies != null &&
                    (
                        (lastVisite == null && h.newVisite.anomalies.length > 0) ||
                        (anomalieLastVisite === null && h.newVisite.anomalies!==null)  ||
                        (lastVisite !== null && (anomalieLastVisite.sort().toString() !== h.newVisite.anomalies.sort().toString())) ||
                        (!h.ras && (anomalieLastVisite.sort().toString() === h.newVisite.anomalies.sort().toString()))
                    )
                    || h.newVisite.debit != null || h.newVisite.debitMax != null
                    || h.newVisite.pression != null || h.newVisite.pressionDyn != null || h.newVisite.pressionDynDeb != null
            ))}
        ).length === this.hydrants.length;
      }
      return false;
    }
  },

  filters: {
    printDate: function (value) {
      return (value != null ) ? moment(value).format('DD/MM/YYYY HH[h]mm') : '';
    }
  },

  mounted: function() {
    this.dataTournee = JSON.parse(this.tournee);
    this.modalTitle = _.unescape(this.dataTournee.nom)+" ("+this.dataTournee.hydrantCount+")";
    this.anomaliesCriteres = [];
    this.anomalies = [];
    this.showErreurs = false;
    this.showResults = false;
    this.erreurs = null;

    this.recuperationHydrants();
    this.$refs.modalSaisieVisite.show();
  },

  methods: {

    recuperationHydrants: function() {
      this.hydrants = null;
      // Récupération des données des hydrants
      axios.get('/remocra/hydrants', {
        params: {
          filter: JSON.stringify([{
            "property": "tournee",
            "value": this.dataTournee.id
          }])
        }
      }).then(response => {
        this.hydrants = response.data.data;
        _.forEach(this.hydrants, h => {
          var index = _.findIndex(this.hydrants, function(o) { return o.id == h.id; })
          this.$set(this.hydrants[index], 'ras', false);
          this.$set(this.hydrants[index], 'variant', 'warning');
          _.forEach(h.visites, v => {
            v.date = moment(v.date);
          });
        });

        this.dataLoaded = true;
      });

      axios.get('/remocra/typehydrantanomalies.json').then(response => {
        // On met en forme les données depuis le résultat de la requête (on utilise ici un controller déjà existant)
        var listAnomalies = [];
        _.forEach(response.data.data, item => {

          if(item.actif){
            var a = {};
            a.code = item.code;
            a.nom = item.nom
            a.id = item.id;
            a.critereCode = (item.critere) ? item.critere.code : null;
            a.critereNom = (item.critere) ? item.critere.nom : '-';
            a.indispo = {};
            _.forEach(item.anomalieNatures, nature => {
              a.indispo[nature.nature.id] = {};
              a.indispo[nature.nature.id].valIndispoAdmin = nature.valIndispoAdmin;
              a.indispo[nature.nature.id].valIndispoHbe = nature.valIndispoHbe;
              a.indispo[nature.nature.id].valIndispoTerrestre = nature.valIndispoTerrestre;
              a.indispo[nature.nature.id].natureCode = nature.nature.code;
              a.indispo[nature.nature.id].saisies = [];
              _.forEach(nature.saisies, saisie => {
                a.indispo[nature.nature.id].saisies.push(saisie.code);
              })
            });
            // On récupère la liste des critères
            var critereId = (item.critere) ? item.critere.id : null;
            a.critere = critereId;
            if (critereId != null && _.findIndex(this.anomaliesCriteres, function(o) {
                return o.id != null && o.id == critereId;
              }) == -1) {
              this.anomaliesCriteres.push(item.critere);
            }
            listAnomalies.push(a);
          }
        });
        this.anomalies = _.sortBy(listAnomalies, ['nom']);

        this.anomaliesCriteres.sort((a, b) => (a.code > b.code) ? 1 : -1)

        this.anomaliesLoaded = true;
      })

      // Récupération des droits de l'utilisateur courant et des types de visite
      axios.get('/remocra/utilisateurs/current/xml').then(response => {
        var xmlDoc = (new DOMParser()).parseFromString(response.data, "text/xml");
        this.utilisateurDroits = [];
        _.forEach(xmlDoc.getElementsByTagName("right"), item => {
          this.utilisateurDroits.push(item.getAttribute("code"));
        });

        axios.get('/remocra/typehydrantsaisies.json').then(response => {
          _.forEach(response.data.data, type => {
            if((type.code === "CTRL" && this.utilisateurDroits.indexOf('HYDRANTS_CONTROLE_C') != -1)
            || (type.code === "RECO" && this.utilisateurDroits.indexOf('HYDRANTS_RECONNAISSANCE_C') != -1)
            || (type.code === "NP" && this.utilisateurDroits.indexOf('HYDRANTS_ANOMALIES_C') != -1)
            || (type.code === "RECEP" && this.utilisateurDroits.indexOf('HYDRANTS_RECEPTION_C') != -1)
            || (type.code === "CREA" && this.utilisateurDroits.indexOf('HYDRANTS_CREATION_C') != -1)) {
              this.comboTypesVisite.push({
                text: type.nom,
                value: type.id,
                code: type.code
              });
            }
          })
        });
      })
    },

    /**
      * Retourne le nom des anomalies de la visite la plus récente
      * @param visites La liste des visites d'un PEI
      * @return Une chaîne de caractère formatée contenant le nom des anomalies
      */
    getVisiteRecenteAnomalies(visites) {
      if(visites == null || visites.length == 0) {
        return ''
      }

      // Le tri a été effectué côté serveur, la première visite est la plus récente
      var anomalies = visites[0].anomalies;
      if(anomalies == null || anomalies === "[]") {
        return '';
      }

      var str = "";
      _.forEach(JSON.parse(anomalies), idAno => {
        // On croise avec les données des anomalies pour retrouver le nom depuis l'id
        var anomaliesFound = this.anomalies.filter(a => a.id === idAno);
        if(anomaliesFound.length === 0) { // Gestion des anomalies si celles-ci sont attribuées mais désactivées
         this.$nextTick(() => {
           this.$notify({
            group: 'remocra',
            title: 'Aucune anomalie trouvée',
            type: 'error',
            text: 'Un PEI dispose d\'une anomalie ayant l\'identifiant '+idAno+' ne disposant pas de correspondance. L\'anomalie a peut-être été supprimée de la base ou désactivée',
            duration: 6000
          });
        });
        this.dataTournee = null;
        } else {
          var nomAnomalie = anomaliesFound[0].nom;
          str += (str !== "") ? ", "+nomAnomalie : ""+nomAnomalie;
        }

      });
      return str;
    },

    onTypeVisiteChange: function() {
      if(this.ctrlDebitPressionDisabled) {
        this.formCtrlDebitPression = false;
      }

      /**
        * Clear les données spécifiques rentrées pour chaque PEI
        * les anomalies et la saisie de mesure de débit/pression étant dépendantes du type de visite
        */
      _.forEach(this.hydrants, h => {
        delete h.newVisite;
      })
    },

    onClickPointsSpecifiques: function(hydrant) {
      this.$refs.modalPointsSpecifiques.showModale(hydrant);
    },

    saisiePointsSpecifiques: function(hydrantId, data) {
      var hydrant = this.hydrants.filter(h => h.id === hydrantId)[0];
      var newVisite = {
        anomalies: data.anomalies,
        observations: data.observations,
        debit: data.debit == "" ? null : data.debit,
        debitMax: data.debitMax == "" ? null : data.debitMax,
        pression: data.pression == "" ? null : data.pression,
        pressionDyn: data.pressionDyn == "" ? null : data.pressionDyn,
        pressionDynDeb: data.pressionDynDeb == "" ? null : data.pressionDynDeb
      };

      this.$set(hydrant, 'newVisite', newVisite);

      // Gestion de la couleur du bouton "Mesures /anomalies"
      if (newVisite.anomalies.length > 0 ||
        newVisite.debit != null || newVisite.debitMax != null || newVisite.pression != null || newVisite.pressionDyn != null || newVisite.pressionDynDeb != null) {
        hydrant.variant = "primary";
      } else {
        hydrant.variant = "warning";
      }
    },

    handleOk: function(evt) {
      evt.preventDefault();
      if (this.showResults) { // Si on est déjà sur l'interface de récap des erreurs ou des résultats, on ferme simplement la modale
        this.$nextTick(() => {
          this.$refs.modalSaisieVisite.hide();
        })
      } else if(this.checkFormValidity()) { // Si le formulaire est valide, on envoie les données au serveur
        var data = [];
        _.forEach(this.hydrants, h => {
          var dataHydrant = {
            idHydrant: h.id,
            agent1: this.formAgent1,
            agent2: this.formAgent2,
            date: moment(this.formDate + " " + this.formTime).format("YYYY-MM-DD HH:mm:ss"),
            type: this.formTypeVisite,
            ctrl_debit_pression: this.formCtrlDebitPression,
            ras: true,
            debit: null,
            debitMax: null,
            pression: null,
            pressionDyn: null,
            pressionDynDeb: null,
            anomalies: [],
            observations: null
          };

          // Si des données spécifiques ont pu être renseignées
          if(!h.ras && h.newVisite != null) {
            dataHydrant.ras = false
            dataHydrant.observations = h.newVisite.observations;
            dataHydrant.anomalies = h.newVisite.anomalies;

            // Si des données de débit/pression ont pu être renseignées
            if(this.formCtrlDebitPression) {
              dataHydrant.debit = h.newVisite.debit;
              dataHydrant.debitMax = h.newVisite.debitMax;
              dataHydrant.pression = h.newVisite.pression;
              dataHydrant.pressionDyn = h.newVisite.pressionDyn;
              dataHydrant.pressionDynDeb = h.newVisite.pressionDynDeb;
            }
          }
          data.push(dataHydrant);
        });

        axios.post('/remocra/tournees/saisievisite', JSON.stringify(data), {
          headers: {
            'Content-Type': 'application/json;charset=utf-8'
          }
        }).then(response => {
          if(response.data.message != null && response.data.message.length > 0 && response.data.message != "[]") {
            this.erreurs = JSON.parse(response.data.message);
            this.showErreurs = true;
            this.showResults = true;
            this.$notify({
              group: 'remocra',
              title: 'Saisie invalide',
              type: 'warn',
              text: 'Remocra n\'a pas pu enregistrer la totalité des visites'
            })
          } else {
            this.$nextTick(() => { //Fermeture manuelle de la modale
              this.showResults = true;
              this.$notify({
                group: 'remocra',
                title: 'Visites sauvegardées',
                type: 'success',
                text: 'Les visites ont bien été enregistrées'
              })
            })
          }
        });
      } else {
           this.$notify({
            group: 'remocra',
            title: 'Saisie invalide',
            type: 'warn',
            text: 'Veuillez saisir les champs obligatoires'
          })
      }
    },

    /**
      * Vérifie la validité du formulaire
      * @return TRUE si tous les champs sont valides, FALSE si au moins 1 champ est invalide
      */
    checkFormValidity() {
      this.etats.date = 'valid';
      this.etats.time = 'valid';

      var timeMax = moment().format('HH:mm');
      this.dateMax = moment().format('YYYY-MM-DD');
      var date = this.formDate + " " + this.formTime;

      if(this.formDate == null || this.formTime == null) {
        this.etats.date = 'invalid';
        this.etats.time = 'invalid';
      } else if(moment(this.dateMax+" "+timeMax).diff(moment(date)) < 0) {
        this.etats.date = 'invalid';
        this.etats.time = 'invalid';
        this.$notify({
          group: 'remocra',
          title: 'Saisie invalide',
          type: 'error',
          text: 'Les visites sont renseignées à une date future'
        })
      }

      this.etats.typeVisite = (this.formTypeVisite == null) ? 'invalid' : 'valid';
      this.etats.agent1 = (this.formAgent1 == null || this.formAgent1 == "" ) ? 'invalid' : 'valid';

      var hasInvalidState = false;
      for (var key in this.etats) {
        hasInvalidState = hasInvalidState || this.etats[key] == "invalid";
      }
      return !hasInvalidState;
    },

    onChangeCtrlDebitPression: function() {
      if(this.formCtrlDebitPression) {
        this.hydrants.forEach((h) => {
          if(h.code == PIBI) {
            h.ras = false;
          }
        });
      }
    },

    getConditionAffichageRas(item) {
        return !this.formCtrlDebitPression || item.code == PENA;
    },

    getConditionColorMesuresAnomalies(item) {
      return (item.ras && item.code == PENA) || (item.ras && !this.formCtrlDebitPression);
    },

    getConditionDisableMesuresAnomalies(item) {
      return this.formTypeVisite === null || this.getConditionColorMesuresAnomalies(item);
    }
  }
};
</script>

<style scoped>
.ModalSaisieVisite.loading .modal-body::after {
  content: 'Récupération des données...';
  margin-left: calc(50% - 67px);
  animation: opacity-anim 1s linear infinite;
}

.dispo {
  padding: 3px 10px 3px 10px;
  margin-top: 5px;
  text-align: center
}

.checkbox-align {
  padding-left: 25px;
}

.colRAS {
  width: 260px;
}

.onoffswitch {
  position: relative; width: 62px;
  -webkit-user-select:none; -moz-user-select:none; -ms-user-select: none;
}
.onoffswitch-checkbox {
  position: absolute;
  opacity: 0;
  pointer-events: none;
}
.onoffswitch-label {
  display: block; overflow: hidden; cursor: pointer;
  border: 2px solid #999999; border-radius: 21px;
}
.onoffswitch-inner {
  display: block; width: 200%; margin-left: -100%;
  transition: margin 0.3s ease-in 0s;
}
.onoffswitch-inner:before, .onoffswitch-inner:after {
  display: block; float: left; width: 50%; height: 25px; padding: 0; line-height: 25px;
  font-size: 11px; color: white; font-family: Trebuchet, Arial, sans-serif; font-weight: bold;
  box-sizing: border-box;
}
.onoffswitch-inner:before {
  content: "RAS";
  padding-left: 10px;
  background-color: #0275D8; color: #FFFFFF;
}
.onoffswitch-inner:after {
  content: "RAS";
  padding-right: 10px;
  background-color: #EEEEEE; color: #999999;
  text-align: right;
}
.onoffswitch-switch {
  display: block; width: 11px; margin: 7px;
  background: #FFFFFF;
  position: absolute; top: 0; bottom: 0;
  right: 33px;
  border: 2px solid #999999; border-radius: 21px;
  transition: all 0.3s ease-in 0s;
}
.onoffswitch-checkbox:checked + .onoffswitch-label .onoffswitch-inner {
  margin-left: 0;
}
.onoffswitch-checkbox:checked + .onoffswitch-label .onoffswitch-switch {
  right: 0px;
}

.boutonMesures {
  margin-left: 5px;
}

#tableScroll {
  max-height: 550px;
  overflow-y: auto;
  overflow-x: scroll;
}
</style>
