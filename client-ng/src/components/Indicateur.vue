<template>
<div v-if="indicateurs && useCustomIndicateur!== null">
  <!-- ============================================ Onglet par défaut ============================================ -->
  <div v-if="useCustomIndicateur== false">
    <b-tabs>
      <b-tab v-for="(indicateur,index) in indicateurs" :key="index" :title="indicateur.nom" @click="currentPage = 1">
        <div>
          <b-table :id="'tab' + index" striped hover :items="indicateur.indicateur" small :current-page="currentPage" :per-page="perPage" />
          <b-pagination aria-controls="tab" v-model="currentPage" :total-rows="(indicateur.indicateur).length" :per-page="perPage" align="fill" size="sm" class="my-0">
          </b-pagination>
        </div>
      </b-tab>
    </b-tabs>
  </div>
  <!-- ============================================ Onglet surchargé ============================================ -->
  <div v-else>
    <component v-bind:is="customIndicateurComponent"></component>
  </div>
</div>
<div v-else>
  <div v-if="errorOnDataLoad">
    <b-alert variant="danger" show>
      <p>Une erreur est survenue lors du chargement des indicateurs de cette crise <br /> Les indicateurs ne peuvent être affiché </p>
    </b-alert>
  </div>
  <p v-else>Chargement des indicateurs ...</p>
</div>
</template>

<script>
import {
  parseString
} from 'xml2js'
import _ from 'lodash'
import axios from 'axios'
import Vue from 'vue'
export default {
  name: 'Indicateur',
  data() {
    return {
      useCustomIndicateur: null,
      customIndicateurHTMLBuffer: null,
      customIndicateurComponent: null,
      errorOnDataLoad: false,
      currentPage: 1,
      perPage: 5,
      indicateurs: null,
      groupe: [],
      indicateur: []
    }
  },
  mounted() {
    axios.get("ext-res/html/crises/indicateur/indicateur.html").then(response => {
      if (response) {
        this.useCustomIndicateur = true;
        this.customIndicateurHTMLBuffer = response.data; // Stockage du contenu HTML en attendant la création du composant
      } else {
        this.useCustomIndicateur = false;
      }
    }).then(() => {
      this.loadData()
    }).catch(() => {
      this.useCustomIndicateur = false;
      this.loadData();
    })
  },
  methods: {
    /**
     * Récupération des données depuis le serveur
     * La réponse est un String contenant le document XML de réponse
     * Les données sont ensuite parsées en JSON 
     */
    loadData: function() {
      var code = "CRISE_INDICATEUR"
      axios.get("/remocra/intervention/indicateur/?code=" + code).then(response => {
        parseString(response.data.toString(), {
          explicitArray: false
        }, (err, result) => {
          this.indicateurs = result.indicateurs.groupe
          _.forEach(this.indicateurs, indicateur => {
            if (indicateur.indicateur && !Array.isArray(indicateur.indicateur)) {
              indicateur.indicateur = [indicateur.indicateur];
            }
          })
          // Création d'un composant à la volée. Le HTML sera compilé comme template Vue.js 
          this.customIndicateurComponent = Vue.component('Indicateur', {
            template: this.customIndicateurHTMLBuffer,
            data: () => {
              return {
                currentPage: 1,
                perPage: 5,
                indicateurs: this.indicateurs
              }
            }
          });
        });
      }).catch((e) => {
        console.error(e);
        this.errorOnDataLoad = true;
      });
    },
  }
}
</script>

<style>
</style>