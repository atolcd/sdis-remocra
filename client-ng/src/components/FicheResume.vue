<template>
<div id="ficheResume">
  <div v-if="data && useCustomResume !== null">
    <!-- ============================================ Résumé par défaut ============================================ -->
    <div v-if="useCustomResume == false">
      <div class="row">
        <div class="col-md-4">
          <p class="title">Localisation</p>
          <p>{{data.hydrant.adresse}}</p>
          <p>{{data.hydrant.commune}}</p>
          <br />
          <div v-if="data.hydrant.complement">
            <p class="observation">Commentaire de localisation :</p>
            <p>{{data.hydrant.complement}}</p>
          </div>
        </div>
        <div class="col-md-4">
          <p class="title">Disponibilité</p>
          <p v-if="data.hydrant.dispo_terrestre === 'DISPO'" class="bg-success rounded-lg dispo text-light font-weight-bolder">OUI</p>
          <p v-else-if="data.hydrant.dispo_terrestre === 'NON_CONFORME'" class="bg-warning rounded-lg dispo dispoLg text-light font-weight-bolder">NON CONFORME</p>
          <p v-else class="bg-danger rounded-lg dispo text-light font-weight-bolder">NON</p>
        </div>
      </div>
    </div>
    <!-- ============================================ Résumé chargé depuis le client ============================================ -->
    <div v-else>
      <component v-bind:is="customResumeComponent"></component>
    </div>
  </div>
  <div v-else>
    <div v-if="errorOnDataLoad">
      <b-alert variant="danger" show>
        <p>Une erreur est survenue lors du chargement des données de ce point d'eau <br /> Le résumé ne peut être affiché </p>
      </b-alert>
    </div>
    <p v-else>Chargement des données du résumé ...</p>
  </div>
</div>
</template>

<script>
import axios from 'axios'
import {
  parseString
} from 'xml2js'
import Vue from 'vue'
export default {
  name: 'FicheResume',
  data() {
    return {
      useCustomResume: null,
      customResumeHTMLBuffer: null,
      customResumeComponent: null,
      errorOnDataLoad: false,
      data: null
    }
  },
  props: {
    hydrantRecord: {
      required: true,
      type: Object
    },
  },
  mounted: function() {
    // On tente de charger le résumé spécifique à cette configuration. Sinon, on utilise la configuration par défaut
    axios.get("/remocra/ext-res/html/hydrants/resume/resume.html").then(response => {
      if (response) {
        this.useCustomResume = true;
        this.customResumeHTMLBuffer = response.data; // Stockage du contenu HTML en attendant la création du composant
      } else {
        this.useCustomResume = false;
      }
    }).then(() => {
      this.loadData()
    }).catch(() => {
      this.useCustomResume = false;
      this.loadData();
    });
  },
  methods: {
    /**
     * Récupération des données depuis le serveur
     * La réponse est un String contenant le document XML de réponse
     * Les données sont ensuite parsées en JSON
     */
    loadData: function() {
      axios.get("/remocra/hydrantResume/" + this.hydrantRecord.id + "?useDefault=" + !this.useCustomResume).then(response => {
        parseString(response.data.toString(), {
          explicitArray: false
        }, (err, result) => {
          this.data = result.data;
          // Si un seul noeud enfant, on créé un tableau de 1 élément (dans ces cas-là, le parser créé un objet à la place)
          if (this.data.anomalie && !Array.isArray(this.data.anomalie)) {
            this.data.anomalie = [this.data.anomalie];
          }
          // Création d'un composant à la volée. Le HTML sera compilé comme template Vue.js
          this.customResumeComponent = Vue.component('resume', {
            template: this.customResumeHTMLBuffer,
            data: () => {
              return {
                data: this.data
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
};
</script>

<style scoped>
.observation {
  text-decoration: underline;
}

.anomalieBloquante {
  font-weight: bold;
}

.liste li {
  list-style: disc outside none !important;
}

.dispo {
  width: 50px;
  padding: 3px 10px 3px 10px;
  text-align: center
}

.dispoLg {
  width: 120px !important;
}
</style>
