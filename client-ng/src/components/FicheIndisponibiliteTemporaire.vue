<template>
<div id="FicheIndisponibiliteTemporaire">
  <modal :draggable="true" id="modalFicheIndispo" name="modalFicheIndispo" ref="modalFicheIndispo"
   :reset="true"
    width="50%"
    height="auto">
       <header class="modal-header"><h5 class="modal-title">{{title}}</h5>
    <div slot="top-right">
     <button type="button" aria-label="Close" @click="$modal.hide('modalFicheIndispo')" class="close">×</button>
    </div></header>
    <form id='formFicheIndispoTemp' name='fiche' enctype="multipart/form-data" method="POST" ref="formFicheIndispoTemp">
      <!-- ================================== Partie Mise en indisponibilité ==================================-->
      <div class="title">Mise en indisponibilité</div>
      <div id="miseIndispo" class="form-group">
        <div class="row">
          <div class="col-md-2 mb-3 mt-1">
            <label>Motif :</label>
          </div>
          <div class="col-md-10">
            <b-form-input type="text" :state="etats.motif" v-model="value.motif" id="motif" size="sm" required></b-form-input>
          </div>
        </div>
        <div class="row">
          <div class="col-md-2 mb-3 mt-1">
            <label>Observation :</label>
          </div>
          <div class="col-md-10">
            <b-form-textarea type="text" v-model="value.observation" id="observation" size="sm"></b-form-textarea>
          </div>
        </div>
        <div class="row">
          <div class="col-md-4">
            <b-form-radio v-model="value.typeDateIndispo" value="immediat" :state="etats.typeDateIndispo" v-on:change="desactiveDateTimeIndispo" name="radioIndispo" required>Immédiate</b-form-radio>
          </div>
        </div>
        <div class="row mt-2">
          <div class="col-md-4 mt-1">
            <b-form-radio v-model="value.typeDateIndispo" value="aPartirDu" :state="etats.typeDateIndispo" v-on:change="activeDateTimeIndispo" name="radioIndispo" required>A partir du</b-form-radio>
          </div>
          <div class="col-md-4">
            <b-form-input id="selectDateIndispo" v-model="value.dateIndispo" :state="etats.dateIndispo" type="date" size="sm" :disabled="!this.activeIndispo" required />
          </div>
          <div class="mt-1 mr-3 ml-3">
            <label>à</label>
          </div>
          <div class="col-md-3">
            <b-form-input id="selectTimeIndispo" v-model="value.heureIndispo" :state="etats.heureIndispo" type="time" size="sm" :disabled="!this.activeIndispo" required />
          </div>
        </div>
        <div class="row">
          <div class="col-md-10 ml-4 mt-1">
            <b-form-checkbox v-model="basculeAutoIndispo" value="true" :disabled="!this.activeIndispo">Basculer automatiquement en indisponible</b-form-checkbox>
            <b-form-checkbox v-model="melAvantIndispo" value="true" :disabled="!this.activeIndispo">Prévenir par mél avant la date prévue</b-form-checkbox>
          </div>
        </div>
      </div>
      <!-- ================================== Partie Remise en disponibilité ==================================-->
      <div class="title">Remise en disponibilité</div>
      <div id="miseDispo" class="form-group">
        <div class="row">
          <div class="col-md-4 ">
            <b-form-radio v-model="value.typeDateDispo" value="inconnue" :state="etats.typeDateDispo" v-on:change="desactiveDateTimeDispo" name="radioDispo" required>Inconnue</b-form-radio>
          </div>
        </div>
        <div class="row mt-2">
          <div class="col-md-4 mt-1">
            <b-form-radio v-model="value.typeDateDispo" value="aPartirDu" :state="etats.typeDateDispo" v-on:change="activeDateTimeDispo" name="radioDispo" required>A partir du</b-form-radio>
          </div>
          <div class="col-md-4">
            <b-form-input id="selectDateDispo" v-model="value.dateDispo" :state="etats.dateDispo" type="date" size="sm" :disabled="!activeDispo" required />
          </div>
          <div class="mt-1 mr-3 ml-3 ">
            <label>à</label>
          </div>
          <div class="col-md-3">
            <b-form-input id="selectTimeDispo" v-model="value.heureDispo" :state="etats.heureDispo" type="time" size="sm" :disabled="!activeDispo" required />
          </div>
        </div>
        <div class="row">
          <div class="col-md-10 ml-4 mt-1">
            <b-form-checkbox v-model="basculeAutoDispo" value=true :disabled="!activeDispo">Basculer automatiquement en disponible</b-form-checkbox>
            <b-form-checkbox v-model="melAvantDispo" value=true :disabled="!activeDispo">Prévenir par mél avant la date prévue</b-form-checkbox>
          </div>
        </div>
      </div>
      <!-- ================================== Partie Points d'eau concernés ==================================-->
      <div class="title">Points d'eau concernés</div>
      <div id="newPei" class="form-group">
        <div class="row">
          <div class="col-md-8">
            <b-form-input type="text" v-model="ajoutPei" id="newpei" placeholder="Numéro du pei à ajouter" size="sm"></b-form-input>
          </div>
          <div class="ml-1">
            <b-button id="ajouter" v-on:click="ajoutePeiSelect" size="sm" variant="primary">Ajouter</b-button>
          </div>
        </div>
        <div class="row">
          <div class="col-md-8 mt-2">
            <b-form-checkbox-group class= "liste_pei" v-model="peiSelected" v-on:change="disableRetirer" :options="value.tabNumeroPeiConcernes" :state="etats.tabNumeroPeiConcernes" id="selectPei">
            </b-form-checkbox-group>
          </div>
          <div class="mt-2 ml-1">
            <b-button id="retirer" :disabled="boolRetirer" v-on:click="supprimePeiSelect" size="sm" variant="danger"> Retirer </b-button>
          </div>
        </div>
        <div class="row">
          <div class="col-md-12 mt-2">
            <b-alert :show="alertDejaExistant" dismissible @dismissed="alertDejaExistant=0" variant="warning" size="sm">Pei déjà renseigné</b-alert>
            <b-alert :show="alertCommune" dismissible @dismissed="alertCommune=0" variant="warning" size="sm">Ce pei ne fait pas partie de la commune</b-alert>
            <b-alert :show="alertErreurSaisie" dismissible @dismissed="alertErreurSaisie=0" variant="warning" size="sm">Erreur de saisie du pei</b-alert>
            <b-alert :show="alertDateHeure" dismissible @dismissed="alertDateHeure=0" variant="danger" size="sm">La date de fin est antérieure à la date de début</b-alert>
            <b-alert :show="alertDebutAvantCourant" dismissible @dismissed="alertDebutAvantCourant=0" variant="danger" size="sm">La date de début est antérieure à la date d'aujourd'hui</b-alert>
            <b-alert :show="alertFormInvalid" dismissible @dismissed="alertFormInvalid=0" variant="danger" size="sm">Certains champs ne sont pas remplis correctement</b-alert>
            <b-alert :show="alertNbPei" dismissible @dismissed="alertNbPei=0" variant="danger" size="sm">Au moins un hydrant doit être renseigné</b-alert>
          </div>
        </div>
      </div>
    </form>
    <div class="modal-footer">
        <b-button size="sm" type="reset" variant="secondary" @click="$modal.hide('modalFicheIndispo')">Annuler</b-button>
        <b-button size="sm" type="submit" variant="primary" @click="handleOk" >Valider</b-button>
      </div>
  </modal>
</div>
</template>

       <!-- ======================================= Script ================================================= -->

<script>
import axios from 'axios'
import _ from 'lodash'
import moment from 'moment'
export default {
  name: 'FicheIndispoTemp',
  data() {
    return {
      title: '',
      /****** Var pour partie indispo ******/
      basculeAutoIndispo: false,
      melAvantIndispo: false,
      activeIndispo: false,
      dateTimeIndispo: null,
      dateRappelDebut: null,
      /****** Var pour partie dispo ******/
      basculeAutoDispo: false,
      melAvantDispo: false,
      activeDispo: false,
      dateTimeDispo: null,
      dateRappelFin: null,
      /******* Var pour pei concernés ******/
      ajoutPei: "",
      boolRetirer: false,
      peiSelected: [],
      tabCheckPeiExist: [],
      peiExist: null,
      idCommuneVerif: null,
      idCommune: [],
      flagCommune: null,
      //alerts
      secondesAlert: 5,
      alertDejaExistant: 0,
      alertCommune: 0,
      alertErreurSaisie: 0,
      alertFormInvalid: 0,
      alertDateHeure: 0,
      alertDebutAvantCourant: 0,
      alertNbPei: 0,
      erreurDate: false,
      value: {
        //Partie indispo
        motif: null,
        observation: null,
        typeDateIndispo: 'immediat',
        dateIndispo: null,
        heureIndispo: null,
        //Partie dispo
        typeDateDispo: 'inconnue',
        dateDispo: null,
        heureDispo: null,
        //Partie points d'eau
        tabNumeroPeiConcernes: [],
        tabIdPeiConcernes: []

      },
      etats: {
        //Partie indispo
        motif: null,
        typeDateIndispo: null,
        dateIndispo: null,
        heureIndispo: null,
        //Partie dispo
        typeDateDispo: null,
        dateDispo: null,
        heureDispo: null,
        //Partie points d'eau
        tabNumeroPeiConcernes: null
      }
    }
  },
  props: {
    idIndispoTemp: {
      type: String,
      required: true
    },
    tabNumPeiSelected: {
      type: String,
      required: true
    },
    tabIdPeiSelected: {
      type: String,
      required: true
    },
  },
  computed: {},
  mounted: function() {
    if (this.idIndispoTemp == "null") {
      this.title = "Nouvelle indisponibilité temporaire"
      this.idIndispoTemp = null;
      this.value.tabIdPeiConcernes = this.tabIdPeiSelected.split(",");
      this.value.tabNumeroPeiConcernes = this.tabNumPeiSelected.split(",");
    } else {
      this.title = "Modification d'une indisponibilité temporaire"
      this.initFicheIndispoTemp();
    }
    this.$nextTick(() => {
      this.$modal.show('modalFicheIndispo');
    })
  },
  methods: {
    disableRetirer() {
      if (this.value.tabNumeroPeiConcernes.length <= 1) {
        this.boolRetirer = true;
      }
    },
    initFicheIndispoTemp() {
      var indispoTemp = [];
      axios.get('/remocra/indisponibilites/' + this.idIndispoTemp).then((response) => {
        indispoTemp = response.data.data;
        this.value.motif = indispoTemp.motif
        this.value.typeDateIndispo = 'aPartirDu'
        this.activeIndispo = true;
        var dateTimeDebut = indispoTemp.dateDebut.split('T')
        this.value.dateIndispo = dateTimeDebut[0]
        this.value.heureIndispo = dateTimeDebut[1].substr(0, 5)
        this.melAvantIndispo = indispoTemp.melAvantIndispo
        this.basculeAutoIndispo = indispoTemp.basculeAutoIndispo
        this.value.observation = indispoTemp.observation;
        if (indispoTemp.dateFin != null) {
          this.value.typeDateDispo = 'aPartirDu'
          var dateTimeFin = indispoTemp.dateFin.split('T')
          this.value.dateDispo = dateTimeFin[0]
          this.value.heureDispo = dateTimeFin[1].substr(0, 5)
          this.melAvantDispo = indispoTemp.melAvantDispo
          this.basculeAutoDispo = indispoTemp.basculeAutoDispo
          this.activeDispo = true;
        } else {
          this.value.typeDateDispo = 'inconnue'
        }
        _.forEach(indispoTemp.hydrants, hydrant => {
          this.value.tabNumeroPeiConcernes.push(hydrant.numero)
          this.value.tabIdPeiConcernes.push(hydrant.id.toString())
        });
      }).catch(function(error) {
        console.error(error)
      });
    },
    activeDateTimeIndispo() {
      this.activeIndispo = true;
      this.value.dateIndispo = null;
      this.value.heureIndispo = null;
      this.basculeAutoIndispo = true;

    },
    desactiveDateTimeIndispo() {
      this.value.dateIndispo = null;
      this.value.heureIndispo = null;
      this.basculeAutoIndispo = false;
      this.melAvantIndispo = false;
      var self = this;
      this.$nextTick().then(function() {
        self.activeIndispo = false;
      });
    },
    activeDateTimeDispo() {
      this.activeDispo = true;
      this.basculeAutoDispo = true;
    },
    desactiveDateTimeDispo() {
      this.value.dateDispo = null;
      this.value.heureDispo = null;
      this.basculeAutoDispo = false;
      this.melAvantDispo = false;
      var self = this;
      this.$nextTick().then(function() {
        self.activeDispo = false;
      });
    },
    ajoutePeiSelect() {
      //récupération de la commune de l'indispo
      var tabCommune = [];
      axios.get('/remocra/hydrants', { // Récupération des mesures effectuées
        params: {
          filter: JSON.stringify([{
            "property": "numeroEqual",
            "value": this.value.tabNumeroPeiConcernes[0]
          }]),
        }
      }).then((response) => {
        tabCommune = response.data.data;
        this.idCommuneVerif = tabCommune[0].commune.id
        this.$nextTick(() => {
          this.checkPeiExist()
        })
      }).catch(function(error) {
        console.error(error)
      });
      this.boolRetirer = false;
    },
    supprimePeiSelect() {
      if (this.value.tabNumeroPeiConcernes.length > 1) {
        if(this.value.tabNumeroPeiConcernes.length === this.peiSelected.length) {
          this.alertNbPei = this.secondesAlert
        } else {
            // On récupère les index des PEI dans le tableau à supprimer
            let removeIndex = this.value.tabNumeroPeiConcernes.map((element, i) => [element, i])
              .filter(([element]) => this.peiSelected.includes(element))
              .map(([, i]) => i);

            this.value.tabIdPeiConcernes = this.value.tabIdPeiConcernes.filter((elt, index) =>
              !removeIndex.includes(index)
            );

            this.value.tabNumeroPeiConcernes = this.value.tabNumeroPeiConcernes.filter((elt, index) =>
              !removeIndex.includes(index)
            );
        }
      }
    },
    //vérifie si pei qu'on ajoute existe
    checkPeiExist() {
      if (this.value.tabNumeroPeiConcernes.includes(this.ajoutPei)) {
        //déja présent
        this.alertDejaExistant = this.secondesAlert;
      } else if (this.ajoutPei.length < 1) {
        //trop court
        this.alertErreurSaisie = this.secondesAlert;
      } else {
        axios.get('/remocra/hydrants', {
          params: {
            filter: JSON.stringify([{
                "property": "numeroEqual",
                "value": this.ajoutPei
              }
            ])
          }
        }).then((response) => {
          this.tabCheckPeiExist = response.data.data;
          if (this.tabCheckPeiExist.length != 1) {
            //requête retourne plusieurs pei
            this.alertErreurSaisie = this.secondesAlert;
          } else if (this.tabCheckPeiExist[0].commune.id != this.idCommuneVerif) {
            //alert pas dans commune
            this.alertCommune = this.secondesAlert;
          } else {
            this.value.tabNumeroPeiConcernes.push(this.ajoutPei);
            this.value.tabIdPeiConcernes.push(this.tabCheckPeiExist[0].id.toString());
            this.ajoutPei = '';
          }
        }).catch(function(error) {
          console.error(error)
        });
      }
    },
    checkCommune(pei) {
      axios.get('/remocra/hydrants', { // Récupération des mesures effectuées
        params: {
          filter: JSON.stringify([{
            "property": "numeroEqual",
            "value": pei
          }]),
        }
      }).then((response) => {
        this.idCommune = response.data.data;
      }).catch(function(error) {
        console.error(error)
      });
    },
    getCurrentDateTime: function() {
      return moment().format('YYYY-MM-DD HH:mm:00');
    },
    checkFormValidity() {
      this.etats.motif = (!this.value.motif == null || !this.value.motif == "") ? 'valid' : 'invalid';

      this.etats.typeDateIndispo = (this.value.typeDateIndispo != null) ? 'valid' : 'invalid';
      this.etats.dateIndispo = null;
      this.etats.heureIndispo = null;
      if (this.value.typeDateIndispo == "aPartirDu") {
        this.etats.dateIndispo = (this.value.dateIndispo != null) ? 'valid' : 'invalid';
        this.etats.heureIndispo = (this.value.heureIndispo != null) ? 'valid' : 'invalid';
      }

      this.etats.typeDateDispo = (this.value.typeDateDispo != null) ? 'valid' : 'invalid';
      this.etats.dateDispo = null;
      this.etats.heureDispo = null;
      if (this.value.typeDateDispo == "aPartirDu") {
        this.etats.dateDispo = (this.value.dateDispo != null) ? 'valid' : 'invalid';
        this.etats.heureDispo = (this.value.heureDispo != null) ? 'valid' : 'invalid';
      }

      this.etats.tabNumeroPeiConcernes = (this.value.tabNumeroPeiConcernes.length > 0) ? 'valid' : 'invalid';
      //Si l'utilisateur choisit date de début immédiat, on met la date et heure courante
      if (this.value.typeDateIndispo == "immediat") {
        this.value.dateIndispo = null;
        this.value.heureIndispo = null;
      }
      //Si on connait la date de début d'indispo, on concatène la date et l'heure
      if (this.value.dateIndispo != null) {
        this.dateTimeIndispo = this.value.dateIndispo + " " + this.value.heureIndispo + ":00";
      } else this.dateTimeIndispo = null;
      //Si on connait la date de remise en dispo, on concatène la date et l'heure
      if (this.value.heureDispo != null) {
        this.dateTimeDispo = this.value.dateDispo + " " + this.value.heureDispo + ":00";
      } else this.dateTimeDispo = null;

      //Vérification date début après date courante
      if (this.dateTimeIndispo != null) {
        if (this.getCurrentDateTime() > this.dateTimeIndispo) {
          this.alertDebutAvantCourant = this.secondesAlert;
          this.etats.dateIndispo = 'invalid';
          this.etats.heureIndispo = 'invalid';
          this.erreurDate = true;
        }
      }

      // Vérification date début avant fin
      if (this.dateTimeDispo != null) {
        if (this.dateTimeIndispo == null && this.getCurrentDateTime() > this.dateTimeDispo) {
          this.etats.dateDispo = 'invalid';
          this.etats.heureDispo = 'invalid';
          this.erreurDate = true;
        } else if (this.dateTimeIndispo != null && this.dateTimeIndispo > this.dateTimeDispo) {
          this.alertDateHeure = this.secondesAlert;
          this.etats.dateIndispo = 'invalid';
          this.etats.heureIndispo = 'invalid';
          this.etats.dateDispo = 'invalid';
          this.etats.heureDispo = 'invalid';
          this.erreurDate = true;
        }
      }

      var isFormValid = true;
      if (this.hasInvalidState(this.etats)) {
        isFormValid = false;
      }
      return isFormValid;
    },
    handleOk(evt) {
      evt.preventDefault();
      if (this.checkFormValidity()) {
        this.handleSubmit();
      } else {
        if (!this.erreurDate) {
          this.alertFormInvalid = this.secondesAlert;
        }
        if (this.value.heureIndispo != null) this.value.heureIndispo = this.value.heureIndispo.substr(0, 5);
      }
    },
    hasInvalidState(etats) {
      var hasInvalidState = false;
      for (var key in etats) {
        hasInvalidState = hasInvalidState || etats[key] == "invalid";
      }
      return hasInvalidState;
    },
    handleSubmit() {
      var nouvelleIndispo = {
        "idIndispoTemp": this.idIndispoTemp,
        "motif": this.value.motif,
        "dateDebut": this.dateTimeIndispo,
        "basculeAutoIndispo": this.basculeAutoIndispo,
        "dateRappelDebut": this.dateRappelDebut,
        "melAvantIndispo": this.melAvantIndispo,
        "dateFin": this.dateTimeDispo,
        "basculeAutoDispo": this.basculeAutoDispo,
        "dateRappelFin": this.dateRappelFin,
        "melAvantDispo": this.melAvantDispo,
        "tabIdPeiConcernes": this.value.tabIdPeiConcernes,
        "totalHydrants": this.value.tabNumeroPeiConcernes.length,
        "statut": null,
        "observation" : this.value.observation
      };
      //On envoie les données du formulaires au serveur
      var self = this
      axios.post('/remocra/indisponibilites/indispoTemp', nouvelleIndispo, {
        headers: {
          'Accept': 'application/json'
        }
      }).then(function() {
        self.$modal.hide('modalFicheIndispo');
        self.$root.$options.bus.$emit('indispo_tmp_created');
      }).catch(function(error) {
        console.error(error)
      });
    }
  }
};
</script>

    <!-- ============================================ Style =============================================== -->

<style>
.modal-title {
  font-size: 1.3rem;
}
#modalFicheIndispo .modal-content {
  background-color: #e9e9e9 !important;
  font-size: 1rem;
  padding-bottom: 0px;
  padding-top: 0px;
}

#modalFicheIndispo .modal-body {
  background-color: #e9e9e9 !important;
  font-size: 1rem;
  padding-top: 0px;
}

#modalFicheIndispo .modal-footer {
  background-color: #e9e9e9 !important;
  font-size: 1rem;
  padding: 10px;
  justify-content: center;
}

.title {
  margin-top: 10px;
}

#newPei {
  margin-bottom: 0px;
}

#ajouter {
  width: 85px;
}

#retirer {
  width: 85px;
}

.liste_pei {
  background-color: white !important;
  padding: 10px;
}
</style>
