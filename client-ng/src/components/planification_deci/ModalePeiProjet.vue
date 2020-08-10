<template>
<div>
  <b-modal id="modalePeiProjet" title="PEI Projet" size="lg" cancel-title="Annuler" ok-title="Valider" ref="modal" @show="resetModal" @ok="handleOk">
    <form ref="formPeiprojet">
      <div class="row">
        <div class="col-md-6">
          <b-form-group
           id="fieldset-horizontal"
           label-cols-sm="4"
           label-cols-lg="3"
           label="Type"
           label-for="type"
           invalid-feedback="Champ obligatoire"
           >
             <b-form-select id="type" v-model="type" :options="comboTypes" @change="onTypeSelected" :state="etats.type" required></b-form-select>
           </b-form-group>
        </div>

        <div class="col-md-6">
          <b-form-group
           id="fieldset-horizontal"
           label-cols-sm="4"
           label-cols-lg="3"
           label="Type DECI"
           label-for="deci"
           invalid-feedback="Champ obligatoire"
           >
             <b-form-select id="deci" v-model="deci" :options="comboDeci" :state="etats.deci" required></b-form-select>
           </b-form-group>
         </div>
      </div>

      <div class="row" v-if="type == 'PIBI'">
        <div class="col-md-6">
          <b-form-group
           id="fieldset-horizontal"
           label-cols-sm="4"
           label-cols-lg="3"
           label="Diamètre Nominal"
           label-for="diametreNominal"
           invalid-feedback="Champ obligatoire"
           >
           <b-form-select id="diametreNominal" v-model="diametreNominal" :options="comboDiametreNominal" :state="etats.diametreNominal"></b-form-select>
           </b-form-group>
         </div>
         <div class="col-md-6">
           <b-form-group
            id="fieldset-horizontal"
            label-cols-sm="4"
            label-cols-lg="3"
            label="Diamètre canalisation"
            label-for="diametreCanalisation"
            invalid-feedback="Champ obligatoire"
            >
            <b-form-input id="diametreCanalisation" v-model="diametreCanalisation" type="number" min="0" :state="etats.diametreCanalisation"></b-form-input>
            </b-form-group>
          </div>
      </div>

      <div class="row" v-if="type == 'reserve'">
        <div class="col-md-6">
          <b-form-group
           id="fieldset-horizontal"
           label-cols-sm="4"
           label-cols-lg="3"
           label="Capacité en m3"
           label-for="capacite"
           invalid-feedback="Champ obligatoire"
           >
             <b-form-input id="capacite" v-model="capacite" type="number" min="0" :state="etats.capacite"></b-form-input>
           </b-form-group>
         </div>
         <div class="col-md-6">
           <b-form-group
            id="fieldset-horizontal"
            label-cols-sm="4"
            label-cols-lg="3"
            label="Débit en m3"
            label-for="debit"
            invalid-feedback="Champ obligatoire"
            >
              <b-form-input id="debit" v-model="debit" type="number" min="0" :state="etats.debit" required></b-form-input>
            </b-form-group>
         </div>
      </div>

      <div class="row" v-if="type == 'PA'">
        <div class="col-md-6">
          <b-form-group
           id="fieldset-horizontal"
           label-cols-sm="4"
           label-cols-lg="3"
           label="Débit en m3"
           label-for="debit"
           invalid-feedback="Champ obligatoire"
           >
             <b-form-input id="debit" v-model="debit" type="number" min="0" :state="etats.debit" required></b-form-input>
           </b-form-group>
        </div>
      </div>

    </form>
  </b-modal>
</div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'

import * as OlProj from 'ol/proj'
import * as eventTypes from '../../bus/event-types.js'

export default {
  name: 'ModalPeiProjet',

  data() {
    return {

      // Données PEI
      type: null,
      deci: null,

      // Données PI/BI
      diametreNominal: null,
      diametreCanalisation: null,

      // Données Citerne et/ou point aspiration
      capacite: null,
      debit: null,


      // formulaire
      comboTypes: [],
      comboDeci: [],
      comboDiametreNominal: [],
      etats: {
        type: null,
        deci: null,
        diametreNominal: null,
        diametreCanalisation: null,
        capacite: null,
        debit: null
      }
    }
  },

  props: {
    idEtude: {
      type: Number,
      required: true
    },

    coordonnees: {
      required: true
    },

    idHydrantProjet: {
      type: Number,
      required: false
    }
  },

  methods: {
    resetModal() {
      this.type = null;
      this.deci = null;
      this.capacite = null;
      this.diametreNominal = null;
      this.diametreCanalisation = null;
      this.debit = null;
      this.etats = {
        type: null,
        deci: null,
        diametreNominal: null,
        diametreCanalisation: null,
        capacite: null,
        debit: null
      };

      // Combo Types
      this.comboTypes = [{
        value: "PIBI",
        text: "PI ou BI"
      }, {
        value: "reserve",
        text: "Réserve"
      },{
        value: "PA",
        text: "Point d'aspiration"
      }];

      // Combo DECI
      axios.get('/remocra/typehydrantnaturedeci').catch(() => {
        this.$notify({
          group: 'remocra',
          type: 'error',
          title: 'Récupération des données',
          text: "Impossible de récupérer la nature DECI des hydrants"
        });
      }).then(response => {
        this.comboDeci = [];
        if(response.data) {
          _.forEach(response.data.data, type => {
            this.comboDeci.push({
              value: type.id,
              text: type.nom,
            })
          });
        }
      });

      // Combo Diamètre nominal
      axios.get('/remocra/typehydrantdiametres').catch(() => {
        this.$notify({
          group: 'remocra',
          type: 'error',
          title: 'Récupération des données',
          text: "Impossible de récupérer le diamètre des hydrants"
        });
      }).then(response => {
        this.comboDiametreNominal = [];
        if(response.data) {
          _.forEach(response.data.data, type => {
            this.comboDiametreNominal.push({
              value: type.id,
              text: type.nom,
            })
          });
        }
      });

      if(this.idHydrantProjet) {
        axios.get("http://localhost:8080/remocra/etudehydrantprojet", {
          params: {
            filter: JSON.stringify([{
              "property": "id",
              "value": this.idHydrantProjet
            }])
          }
        }).then(response => {
          if(response.data) {
            var hydrant = response.data.data[0];
            this.type = hydrant.type;
            this.deci = (hydrant.type_deci) ? hydrant.type_deci.id : null;
            this.capacite = hydrant.capacite;
            this.diametreNominal = (hydrant.diametre_nominal) ? hydrant.diametre_nominal.id : null;
            this.diametreCanalisation = hydrant.diametre_canalisation;
            this.debit = hydrant.debit;
          }
        }).catch(() => {
          this.$notify({
            group: 'remocra',
            type: 'error',
            title: 'Récupération des données',
            text: "Impossible de récupérer les PEI projet de l'étude"
          });
        })
      }
    },

    onTypeSelected() {
      this.capacite = null;
      this.diametreNominal = null;
      this.diametreCanalisation = null;
      this.debit = null;
      this.etats = {
        type: null,
        deci: null,
        diametreNominal: null,
        diametreCanalisation: null,
        capacite: null,
        debit: null
      };
    },

    checkFormValidity() {
      this.etats = _.mapValues(this.etats, () => 'valid');

      this.etats.type = (this.type == null) ? "invalid" : "valid";
      this.etats.deci = (this.deci == null) ? "invalid" : "valid";

      if (this.type == "PIBI") {
        this.etats.diametreNominal = (this.diametreNominal == null) ? "invalid" : "valid";
        this.etats.diametreCanalisation = (this.diametreCanalisation == null) ? "invalid" : "valid";
      } else if (this.type == "reserve") {
        this.etats.debit = (this.debit == null && this.debit > 0) ? "invalid" : "valid";
        this.etats.capacite = (this.capacite == null && this.capacite > 0) ? "invalid" : "valid";
      } else if (this.type == "PA") {
        this.etats.debit = (this.debit == null && this.debit > 0) ? "invalid" : "valid";
      }
      return !this.hasInvalidState(this.etats) && this.$refs['formPeiprojet'].checkValidity();
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

    handleOk(bvModalEvt) {
      bvModalEvt.preventDefault();
      if(this.checkFormValidity()) {
        if(this.idHydrantProjet) {
          this.handleEditPeiProjet();
        } else {
          this.handleAddPeiProjet();
        }
      }
    },

    handleAddPeiProjet() {
      var formData = new FormData();
      var coords = OlProj.transform(this.coordonnees, 'EPSG:3857', 'EPSG:2154');
      formData.append("peiProjet", JSON.stringify({
        type: this.type,
        deci: this.deci,
        diametreNominal: this.diametreNominal,
        diametreCanalisation: this.diametreCanalisation,
        capacite: this.capacite,
        debit: this.debit,
        idEtude: this.idEtude,
        longitude: coords[0],
        latitude: coords[1]
      }));
      // Envoi au serveur
      axios.post('/remocra/etudehydrantprojet/', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).catch(() => {
        this.$notify({
          group: 'remocra',
          type: 'error',
          title: 'Création de l\'hydrant projet',
          text: "Une erreur est survenue lors de la création de l'hydrant projet"
        });
      }).then(() => {
        this.$bvModal.hide("modalePeiProjet");
        this.$root.$options.bus.$emit(eventTypes.OLMAP_COUCHES_REFRESHLAYER, 'etude_hydrant_projet');
      })
    },

    handleEditPeiProjet() {
      var formData = new FormData();
      formData.append("peiProjet", JSON.stringify({
        id: this.idHydrantProjet,
        type: this.type,
        deci: this.deci,
        diametreNominal: this.diametreNominal,
        diametreCanalisation: this.diametreCanalisation,
        capacite: this.capacite,
        debit: this.debit
      }));
      // Envoi au serveur
      axios.post('/remocra/etudehydrantprojet/update', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).catch(() => {
        this.$notify({
          group: 'remocra',
          type: 'error',
          title: 'Envoi des données',
          text: "La modification du PEI projet a échouée"
        });
      }).then(() => {
        this.$bvModal.hide("modalePeiProjet");
      })
    }
  }
};
</script>

<style>
#modalEtude .invalid-feedback {
  font-size: 12px;
}

#modalEtude .modal-title {
  color: #7B7B7B;
  font-size: 20px;
  font-family: sans-serif, arial, verdana;
}
.tabs {
 border: 1px solid #dee2e6;
}

select {
  margin-top: 5px;
}

.form-row{
  padding-left: 10px;
  padding-right: 10px;
}

.communeInvalid {
  color:  #d9534f;
  font-size: 12px;
  margin-top: 5px;
  margin-bottom: 5px;
}

.buttonRow {
  margin-right: 10px;
  margin-top: 10px;
  margin-bottom: 10px;
}

.itemCommuneContainer {
  max-height: 100px;
  overflow-y: scroll;
  overflow-x: hidden;
  border-radius: 0.2em;
  border: 1px solid #CED4DA;
  background-color: white;
}

.itemCommune {
  padding: 2px 0px 0px 5px;
  color: #495057;
  margin-bottom: 0;
}

.tabDocuments {
  padding-left: 15px;
  padding-right: 15px;
}
.custom-file-input ~ .custom-file-label::after {
  content: 'Parcourir' !important;
}
</style>
