<template>
  <div>
    <b-modal id="modalPointsSpecifiques"
             ref="modalPointsSpecifiques"
             no-close-on-backdrop
             :title="modalTitle"
             ok-title="Valider"
             cancel-title="Annuler"
             size="lg"
             @ok="handleOk">
       <template #modal-header></template>

       <div class="row">
         <div class="col-md-12">
           <b-tabs fill content-class="mt-3" active-nav-item-class="text-primary">
             <b-tab title="Mesures" active v-if="saisieDebitPression">
               <div>
                 <b-form-group label="Débit à 1 bar (㎥/h) :" label-for="debit" label-cols-md="6">
                   <b-form-input id="debit" v-model="debit" type="number" size="sm"></b-form-input>
                 </b-form-group>
                 <b-form-group label="Pression dynamique à 60 ㎥ (bar) :" label-for="pressionDyn" label-cols-md="6">
                   <b-form-input id="pressionDyn" v-model="pressionDyn" type="number" step="any" size="sm"></b-form-input>
                 </b-form-group>
                 <b-form-group label="Débit max (㎥/h) :" label-for="debitMax" label-cols-md="6">
                   <b-form-input id="debitMax" v-model="debitMax" type="number" size="sm"></b-form-input>
                 </b-form-group>
                 <b-form-group label="Pression dynamique au débit max (bar) :" label-for="pressionDynDeb" label-cols-md="6">
                   <b-form-input id="pressionDynDeb" v-model="pressionDynDeb" type="number" step="any" size="sm"></b-form-input>
                 </b-form-group>
                 <b-form-group label="Pression statique (bar) :" label-for="pression" label-cols-md="6">
                   <b-form-input id="pression" v-model.number="pression" type="number" step="any" size="sm"></b-form-input>
                 </b-form-group>
               </div>
             </b-tab>

             <b-tab class="anomalies-tab" title="Points d'attention">
               <div v-if="anomaliesCriteres[indexCritere] !== undefined">
                 <div class="row" id="anomalieCritere">
                   <div class="col-md-12" v-if="this.anomaliesFiltered.length > 0">
                     <p class="bold">{{anomaliesCriteres[indexCritere].nom}}</p>
                   </div>
                 </div>
                 <div class="row">
                   <div class="col-md-12">
                     <b-form-group>
                       <b-form-checkbox-group id="checkbox-group-2" v-model="formAnomalies" name="flavour-2">
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
                   <b-form-textarea id="observations" v-model="observations" placeholder="Observations..." rows="3" size="sm" max-rows="6">
                   </b-form-textarea>
                 </div>
               </div>
             </b-tab>
           </b-tabs>
         </div>
       </div>
    </b-modal>
  </div>
</template>

<script>
export default {
  name: 'ModalPointsSpecifiques',

  props: {
    saisieDebitPression: {
      type: Boolean,
      required: true
    },

    anomaliesCriteres: {
      type: Array,
      required: true
    },

    anomalies: {
      type: Array,
      required: true
    },

    typeVisite: {
      type: Object,
      required: true
    },
  },

  data() {
    return {
      hydrant: null,
      indexCritere: 0,
      modalTitle: null,

      debit: null,
      pressionDyn: null,
      debitMax: null,
      pressionDynDeb: null,
      pression: null,
      observations: null,
      formAnomalies: null
    }
  },

  computed: {
    /**
     * Désactivation des boutons "Suivant" et "Précédent" en fin de parcours des anomalies
     * Si tous les critères suivants ou tous les précédents n'ont aucune anomalie disponible, on arrête également
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
     * Renvoie une liste d'anomalies filtrées depuis 'anomalies'
     * Les anomalies filtrées sont celles correspondant à la nature du type de PEI, du critère sélectionné ainsi qu'au type de saisie actuellement effectué
     */
    anomaliesFiltered: function() {
      if (this.hydrant == null || this.anomaliesCriteres[this.indexCritere] == null) {
        return [];
      }
      return this.anomalies.filter(item => item.indispo[this.hydrant.nature.id] != null && item.critereCode == this.anomaliesCriteres[this.indexCritere].code && item.indispo[this.hydrant.nature.id].saisies.indexOf(this.typeVisite.code) > -1);
    }
  },

  mounted: function() {

  },

  methods: {

    showModale(hydrant) {
      this.hydrant = hydrant;
      this.modalTitle = hydrant.numero;

      this.debit = null;
      this.pressionDyn = null;
      this.debitMax = null;
      this.pressionDynDeb = null;
      this.pression = null;
      this.observations = null;
      this.formAnomalies = [];

      this.indexCritere = -1;
      this.critereSuivant();

      if (this.hydrant.newVisite != null) { // Si on a déjà ouvert cette interface pour ce PEI, on reprend les informations saisies
        this.debit = this.hydrant.newVisite.debit;
        this.debitMax = this.hydrant.newVisite.debitMax;
        this.pression = this.hydrant.newVisite.pression;
        this.pressionDyn = this.hydrant.newVisite.pressionDyn;
        this.pressionDynDeb = this.hydrant.newVisite.pressionDynDeb;
        this.observations = this.hydrant.newVisite.observations;
        this.formAnomalies = this.hydrant.newVisite.anomalies;

      } else if (this.hydrant.visites && this.hydrant.visites.length > 0) { // Si première ouverture de l'interface pour ce PEI, on reprend les anomalies de la visite la plus récente
        this.formAnomalies = JSON.parse(this.hydrant.visites[0].anomalies);
        if(this.formAnomalies === null) {
          this.formAnomalies = [];
        }
      }

      this.$refs.modalPointsSpecifiques.show();
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
      if(this.hydrant === null) {
        return null;
      }

      return this.anomalies.filter(item => item.indispo[this.hydrant.nature.id] != null && item.critereCode == this.anomaliesCriteres[index].code && item.indispo[this.hydrant.nature.id].saisies.indexOf(this.typeVisite.code) > -1).length;
    },

    /**
     * Retourne, pour une anomalie donnée, le style CSS à appliquer
     * Si provoque une indisponibilité: gras
     * Si provoque une indisponibilité HBE: souligné
     * Les styles sont cumulables
     */
    getAnomalieClass: function(index) {
      var classes = "";
      if (this.anomaliesFiltered[index].indispo[this.hydrant.nature.id].valIndispoTerrestre) {
        classes += "bold ";
      }
      if (this.anomaliesFiltered[index].indispo[this.hydrant.nature.id].valIndispoHbe) {
        classes += "underline ";
      }
      return classes;
    },

    /**
      * Récupération des données et envoie à la modale parente
      */
    handleOk: function(evt) {
      evt.preventDefault();
      var data = {
        anomalies: this.formAnomalies,
        debit: this.debit,
        debitMax: this.debitMax,
        pression: this.pression,
        pressionDyn: this.pressionDyn,
        pressionDynDeb: this.pressionDynDeb,
        observations: this.observations
      }
      this.$emit('saisiePointsSpecifiques', this.hydrant.id, data);
      this.$nextTick(() => { //Fermeture manuelle de la modale
        this.$refs.modalPointsSpecifiques.hide();
      })
    }
  }
};
</script>

<style scoped>
.bold {
  font-weight: bold;
}

.underline {
  text-decoration: underline;
}
</style>
