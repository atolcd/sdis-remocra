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
            <th scope="col">
              <p>Objet</p>
              <b-form-input v-model="filters.objet" size="sm" placeholder="Objet" class="filter" v-on:input="onFilterChange"></b-form-input>
            </th>
            <th scope="col">
              <p>Référence</p>
              <b-form-input size="sm" placeholder="Référence" class="filter" ></b-form-input>
            </th>
            <th scope="col">
              <p>Date</p>
              <b-form-select v-model="filters.date" size="sm" placeholder='Tous' class="filter" :options="comboFilterDate" v-on:change="onFilterChange"></b-form-select>
            </th>
            <th scope="col">
              <p>Expéditeur</p>
              <b-form-input size="sm" placeholder="Expéditeur" class="filter" ></b-form-input>
            </th>
            <th scope="col">
              <p>Destinataires</p>
              <b-form-input v-model="filters.destinataire" size="sm" placeholder="Destinataire" class="filter" v-on:input="onFilterChange"></b-form-input>
            </th>
            <th scope="col">
              <p>Accusé</p>
              <b-form-select v-model="filters.accuse" size="sm" placeholder='Tous' class="filter" :options="comboFilterAccuse" v-on:change="onFilterChange"></b-form-select>
            </th>
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
          @change="refreshCourriers"
        ></b-pagination>
      </div>
    </div>

  </div>
</template>

<script>

import axios from 'axios'
import moment from 'moment'
import _ from 'lodash'

export default {
  name: 'visualisationCourriers',
  data() {
    return {
      nbCourriers: 0,
      nbCourriersParPage: 15,
      pageActuelle: 1, // Pagination
      listeCourriers: [], // Liste des courriers de la page actuelle
      listeDocuments: [], // Liste des documents présents dans les courriers
      filters: { // Filtres des données
        objet: '',
        accuse: '',
        destinataire: '',
        date: ''
      },

      comboFilterAccuse: [{
        text: "Tous",
        value: ''
      },{
        text: "Reçu",
        value: 'true'
      },{
        text: "Non reçu",
        value: 'false'
      }],

      comboFilterDate: [{
        text: "Tous",
        value: ''
      },{
        text: "< 1 jour",
        value: moment().subtract(1, 'd').format("YYYY-MM-DD HH:mm:ss")
      },{
        text: "< 1 semaine",
        value: moment().subtract(1, 'w').format("YYYY-MM-DD HH:mm:ss")
      },{
        text: "< 1 mois",
        value: moment().subtract(1, 'M').format("YYYY-MM-DD HH:mm:ss")
      },{
        text: "< 3 mois",
        value: moment().subtract(3, 'M').format("YYYY-MM-DD HH:mm:ss")
      },{
        text: "< 6 mois",
        value: moment().subtract(6, 'M').format("YYYY-MM-DD HH:mm:ss")
      }]
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

      // Détermination des filtres à utiliser lors de la requête
      var requeteFiltres = [];
      _.forEach(this.filters, (value, key) => {
        if(value !== ''){
          requeteFiltres.push({
            "property": key,
            "value": value
          });
        }
      });

      // Récupération du nombre de courriers
      axios.get('/remocra/courrier/courrierdocumentcount', {
        params: {
          "filter": JSON.stringify(requeteFiltres)
        }
      }).then(response => {
        this.nbCourriers = response.data.message;
        let start = (this.pageActuelle - 1) * this.nbCourriersParPage;

        // Récupération des données des courriers
        axios.get('/remocra/courrier/courrierdocument?start='+start+'&limit='+this.nbCourriersParPage, {
          params: {
            "filter": JSON.stringify(requeteFiltres)
          }
        }).then(response => {
          this.listeCourriers = response.data.data;
        });
      });
    },

    /**
      * Retourne pour chaque ligne du tableau la valeur de sa propriété 'rowspan' pour la mise en page
      * @param index L'index du courrier dans le tableau listeCourriers (préalablement trié)
      */
    getRowSpan : function(index) {
      if(this.listeCourriers[index-1] == null || this.listeCourriers[index-1].document != this.listeCourriers[index].document) {
        return this.listeCourriers.filter(item => item.document === this.listeCourriers[index].document).length;
      }
      return null;
    },

    onFilterChange() {
      this.pageActuelle = 1;
      this.refreshCourriers();
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

.filter {
  width: auto;
  height: calc(1.7em + 2px);
  padding-left: 2px;
  line-height: 1em;
}

#tableCourriers th p {
  padding-left: 2px;
  font-size: 16px;
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
