<template>
  <div id="visualisationCourriers" class="container-fluid">
    <div class="row">
      <div class="col-md-12">
        <h1>Visualisation des courriers</h1>
      </div>
    </div>

    <div class="row">
      <div class="col-md-12">
        <table class="table table-sm table-bordered table-fixed" id="tableCourriers">
          <thead class="thead-light">
            <th scope="col" v-on:click="onSortChange('objet')" :class="getSortStyle('objet')">
              <p>Objet</p>
              <b-form-input v-model="filters.objet" size="sm" placeholder="Objet" class="filter" v-on:input="onFilterChange"></b-form-input>
            </th>
            <th scope="col">
              <p>Référence</p>
              <b-form-input size="sm" placeholder="Référence" class="filter" ></b-form-input>
            </th>
            <th scope="col" v-on:click="onSortChange('date')" :class="getSortStyle('date')">
              <p>Date</p>
              <b-form-select v-model="filters.date" size="sm" placeholder='Tous' class="filter" :options="comboFilterDate" v-on:change="onFilterChange"></b-form-select>
            </th>
            <th scope="col">
              <p>Expéditeur</p>
              <b-form-input size="sm" placeholder="Expéditeur" class="filter" ></b-form-input>
            </th>
            <th scope="col" v-on:click="onSortChange('destinataire')" :class="getSortStyle('destinataire')">
              <p>Destinataires</p>
              <b-form-input v-model="filters.destinataire" size="sm" placeholder="Destinataire" class="filter" v-on:input="onFilterChange"></b-form-input>
            </th>
            <th scope="col">
              <p>Accusé</p>
              <b-form-select v-model="filters.accuse" size="sm" placeholder='Tous' class="filter" :options="comboFilterAccuse" v-on:change="onFilterChange"></b-form-select>
            </th>
            <th scope="col">
              <p style="margin-bottom: 24px;">Action</p>
            </th>
          </thead>
          <tbody :key="tableKey">
            <tr v-for="(item, index) in listeCourriers" :key="index" :class="getStripeStyle(item.document)">
              <td v-if="getRowSpan(index)" :rowSpan="getRowSpan(index)">{{item.nomDocument}}</td>
              <td v-if="getRowSpan(index)" :rowSpan="getRowSpan(index)"></td>
              <td v-if="getRowSpan(index)" :rowSpan="getRowSpan(index)">{{item.dateDoc | formatDate}}</td>
              <td v-if="getRowSpan(index)" :rowSpan="getRowSpan(index)"></td>
              <td>{{item.mail}}</td>
              <td class="colAccuse"><img v-if="item.accuse" src="../assets/img/check.png" width="32" class="badge badge-pill badge-success" /></td>
              <td class="colAction">
                <div  @click="$refs.apercuDocument.visualisationDocument(item.code, item.nomDocument)">
                  <img src="../assets/img/file-pdf-regular.svg" width="20" />
                </div>
              </td>
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

    <ModalApercuDocument ref="apercuDocument" :listeCourriers="listeCourriers"></ModalApercuDocument>
  </div>
</template>

<script>

import axios from 'axios'
import moment from 'moment'
import _ from 'lodash'

import ModalApercuDocument from './ModalApercuDocument.vue'

export default {
  name: 'visualisationCourriers',
  components: {
    ModalApercuDocument
  },
  data() {
    return {
      nbCourriers: 0,
      nbCourriersParPage: 15,
      pageActuelle: 1, // Pagination
      listeCourriers: [], // Liste des courriers de la page actuelle
      listeDocuments: [], // Liste des documents présents dans les courriers
      tableKey: 0,
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
      }],

      sorter: {
        property: 'date',
        direction: 'DESC'
      }
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

      // Critère de tri (un seul par requête)
      var requeteSorts = [];
      requeteSorts.push(this.sorter);

      // Récupération du nombre de courriers
      axios.get('/remocra/courrier/courrierdocumentcount', {
        params: {
          "filter": JSON.stringify(requeteFiltres),
          "sort": JSON.stringify(requeteSorts)
        }
      }).then(response => {
        this.nbCourriers = response.data.message;
        let start = (this.pageActuelle - 1) * this.nbCourriersParPage;

        // Récupération des données des courriers
        axios.get('/remocra/courrier/courrierdocument?start='+start+'&limit='+this.nbCourriersParPage, {
          params: {
            "filter": JSON.stringify(requeteFiltres),
            "sort": JSON.stringify(requeteSorts)
          }
        }).then(response => {
          this.listeCourriers = response.data.data;
          // Force le refresh de la table. La fréquence de tick de la réactivité est parfois trop lente et mène à des problème de style dans la table le temps que les
          // changements soient détectés
          this.tableKey++;
        });
      });
    },

    /**
      * Retourne pour chaque ligne du tableau la valeur de sa propriété 'rowspan' pour la mise en page
      * @param index L'index du courrier dans le tableau listeCourriers (préalablement trié)
      */
    getRowSpan(index) {
      if(this.listeCourriers[index-1] == null || this.listeCourriers[index-1].document != this.listeCourriers[index].document) {
        return this.listeCourriers.filter(item => item.document === this.listeCourriers[index].document).length;
      }
      return null;
    },

    /*
     * Gestion des couleurs des lignes du tableau
     * A cause des rowspan, la coloration automatique par Bootstrap ne fonctionne pas (lignes juxtaposées de même couleur)
     * On doit passer par du javascript, les sélecteurs CSS nécessaires pour ce fix précis n'existent pas
     */
    getStripeStyle(document) {
      if(_.indexOf(_.uniq(_.values(_.mapValues(this.listeCourriers, 'document'))), document) % 2){
        return "stripeDark";
      }
      return "stripeLight";
    },

    onFilterChange() {
      this.pageActuelle = 1;
      this.refreshCourriers();
    },

    onSortChange(value) {
      if(this.sorter.property == value) {
        this.sorter.direction = (this.sorter.direction === 'ASC') ? 'DESC' : 'ASC';
      } else {
        this.sorter.direction = 'ASC';
        this.sorter.property = value;
      }

      this.pageActuelle = 1;
      this.refreshCourriers();
    },

    /**
      * Détermine la classe à utiliser selon le type de tri effectué
      *
      */
    getSortStyle(value) {
      if(this.sorter.property === value) {
        return (this.sorter.direction === 'ASC') ? "sort sortAsc" : "sort sortDesc";
      }
      return "";
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

#tableCourriers th:hover {
  background-color: #c0c1c2;
  color: white;
}

.stripeLight {
  background-color: #f8f9fa;
}

.stripeDark {
  background-color: #e1e2e3;
}

#tableCourriers td {
  vertical-align : middle;
  border: 1px solid #cbd5df;
}

#tableCourriers .colAccuse, #tableCourriers .colAction {
  text-align: center;
}

#tableCourriers .colAccuse img {
  display: inline;
  vertical-align : middle;
}

.sort {
  background-repeat: no-repeat;
  background-position: right 5% bottom 85%;
}

.sortAsc {
  background-image: url(../assets/img/collapse.svg);
}

.sortDesc {
  background-image: url(../assets/img/expand.svg);
}



</style>
