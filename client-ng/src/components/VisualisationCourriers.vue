<template>
  <div id="visualisationCourriers" class="container-fluid">
    <div class="row">
      <div class="col-md-12">
        <h1>Visualisation des courriers</h1>
      </div>
    </div>

    <div class="row">
      <div class="col-md-12">
        <table class="table table-striped table-sm table-bordered" id="tableCourriers">
          <thead class="thead-light">
            <th scope="col">Objet</th>
            <th scope="col">Référence</th>
            <th scope="col">Date</th>
            <th scope="col">Expéditeur</th>
            <th scope="col">Destinataires</th>
            <th scope="col">Accusé</th>
          </thead>
          <tbody>
            <tr v-for="(item, index) in listeCourriers" :key="index">
              <td v-if="getRowSpan(index)" :rowSpan="getRowSpan(index)">{{item.nomDocument}}</td>
              <td v-if="getRowSpan(index)" :rowSpan="getRowSpan(index)"></td>
              <td v-if="getRowSpan(index)" :rowSpan="getRowSpan(index)">{{item.dateDoc | formatDate}}</td>
              <td></td>
              <td>{{item.mail}}</td>
              <td class="colAccuse"><img v-if="item.accuse" src="../assets/img/check.png" width="32" class="badge badge-pill badge-success" /></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="row">
      <div class="col-md-3 offset-md-9">
        <b-pagination
          v-model="pageActuelle"
          align="right"
          limit=10
          :total-rows="nbCourriers"
          :per-page="nbCourriersParPage"
          @input="refreshCourriers"
        ></b-pagination>
      </div>
    </div>

  </div>
</template>

<script>

import axios from 'axios'
import moment from 'moment'
//import _ from 'lodash'

export default {
  name: 'visualisationCourriers',
  data() {
    return {
      nbCourriers: 0,
      nbCourriersParPage: 10,
      pageActuelle: 1, // Pagination
      listeCourriers: [], // Liste des courriers de la page actuelle
      listeDocuments: [] // Liste des documents présents dans les courriers
    }
  },

  filters : {

    /**
      * Formatte une date provenant de la base de donnée en date usuelle
      **/
    formatDate: function(date) {
      if(date === null) {
        return '';
      }
      return moment(date, 'YYYY-MM-DDTHH:mm:ss').format('DD/MM/YYYY HH:mm')
    }
  },

  mounted: function() {
    this.refreshCourriers();
  },

  methods: {

    /**
      * Met à jour la liste des courriers affiché
      * Utilisée lors de la création du composant ou lors d'un changement de pageActuelle
      */
    refreshCourriers() {
      // Récupération du nombre de courriers
      axios.get('/remocra/courrier/courrierdocumentcount').then(response => {
        this.nbCourriers = response.data.message;
        let start = (this.pageActuelle - 1) * this.nbCourriersParPage;

        axios.get('/remocra/courrier/courrierdocument?start='+start+'&limit='+this.nbCourriersParPage).then(response => {
          this.listeCourriers = response.data.data;
        });
      });
    },

    /**
      * Retourne pour chaque ligne du tableau la valeur de sa propriété 'rowspan' pour la mise en page
      * @param index L'index du courrier dans le tableau listeCourriers (préalablement trié)
      */
    getRowSpan : function(index) {
      if(this.listeCourriers[index-1] == null){
        return 1;
      }
      else if(this.listeCourriers[index-1].document != this.listeCourriers[index].document) {
        return this.listeCourriers.filter(item => item.document === this.listeCourriers[index].document).length;
      }
      return null;
    }
  }
};
</script>

<style scoped>

h1 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
  margin-top: 1rem;
  color: #7b7b7b;
  font-family: Helvetica, Arial !important
}

#tableCourriers td {
  vertical-align : middle;
}

#tableCourriers .colAccuse {
  text-align: center;
}

#tableCourriers .colAccuse img {
  display: inline;
  vertical-align : middle;
}
</style>
