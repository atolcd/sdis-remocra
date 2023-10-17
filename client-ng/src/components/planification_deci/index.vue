<template>
  <div id="planificationDeci" class="container-fluid">
    <div v-if="showMap">
      <div class="enteteEtude">
          <b-button variant="outline-primary"
              @click="showMap = false"
              class="buttonRetour">
            <img src="../../assets/img/resultset_previous.png" width="16"/>Quitter l'étude
          </b-button>
        <div class="title"> {{ getSelectedEtude().nom }}</div>
      </div>

      <OlMapEtude :cleIgn="cleIgn"
                  :idEtude="selectedEtude"
                  :reseauImporte="selectedEtudeReseauImporte"
                  :isClosed="selectedEtudeStatut == 'TERMINEE'"
                  :bounds="bounds"></OlMapEtude>
    </div>

    <div v-else>
      <div class="row">
        <h1 class="title">Planification DECI</h1>
      </div>

      <div class="row" id="boutonsAction">
        <div class="col-md-12">
          <b-button variant="outline-primary"
              @click="etudeAConfigurer = null"
              v-b-modal.modalEtude>
            <img src="../../assets/img/addFile.png" width="16"/>Créer
          </b-button>

          <b-button variant="outline-primary"
            :disabled="selectedEtude == null"
            @click="showMap = true">
            <img src="../../assets/img/folder-open.gif" width="16"/>Ouvrir
          </b-button>

          <b-button variant="outline-primary"
              :disabled="selectedEtude == null"
              @click="etudeAConfigurer = getSelectedEtude()"
              v-b-modal.modalEtude >
            <img src="../../assets/img/cog.png" width="16"/>Configurer
          </b-button>

          <b-button variant="outline-primary"
              :disabled="selectedEtude == null"
              @click="cloreEtude">
            <img src="../../assets/img/decline.png" width="16"/>Clore
          </b-button>
        </div>
      </div>

      <div class="row">
        <div class="col-md-12">
          <table class="table table-sm table-bordered table-fixed" id="tableEtudes">
            <thead class="thead-light">
              <th scope="col" v-on:click.self="onSortChange('type', $event)" :class="getSortStyle('type')">
                <p v-on:click.self="onSortChange('type', $event)">Type</p>
                <b-form-select v-model="filters.type" size="sm" placeholder='Tous' class="filter" :options="comboFilterType" v-on:change="onFilterChange"></b-form-select>
              </th>
              <th scope="col">
                <p v-on:click="onSortChange('numero', $event)" :class="getSortStyle('numero')">Numéro</p>
              </th>
              <th scope="col" v-on:click="onSortChange('nom', $event)" :class="getSortStyle('nom')">
                <p>Nom</p>
              </th>
              <th scope="col">
                <p>Description</p>
              </th>
              <th scope="col">
                <p>Communes</p>
              </th>
              <th scope="col" v-on:click.self="onSortChange('statut', $event)" :class="getSortStyle('statut')">
                <p v-on:click.self="onSortChange('statut', $event)">Statut</p>
                <b-form-select v-model="filters.statut" size="sm" placeholder='Tous' class="filter" :options="comboFilterStatut" v-on:change="onFilterChange"></b-form-select>
              </th>
              <th scope="col" v-on:click="onSortChange('date_maj', $event)" :class="getSortStyle('date_maj')">
                <p>Dernière mise à jour</p>
              </th>
            </thead>
            <tbody :key="tableKey">
              <tr v-for="(item, index) in listeEtudes"
                  :key="index"
                  @click="selectedEtude = (selectedEtude == item.id) ? null : item.id; selectedEtudeReseauImporte = item.reseauImporte; selectedEtudeStatut = item.statut.code;"
                  :class="{'bg-secondary':item.id==selectedEtude, 'text-light':item.id==selectedEtude}">
                <td>{{item.type.nom}}</td>
                <td>{{item.numero}}</td>
                <td>{{item.nom}}</td>
                <td class="colDescription" >{{item.description}}</td>
                <td>{{item.communes | printCommunes}}</td>
                <td>{{item.statut.nom}}</td>
                <td>{{item.date_maj | printDate}}</td>
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
            :total-rows="nbEtudes"
            :per-page="nbEtudesParPage"
            @input="refreshEtudes"
          ></b-pagination>
        </div>
      </div>

      <ModalEtude ref="modalEtude"
        :typeEtudes="typesEtude"
        v-if="typesEtude.length > 0"
        @refreshEtudes="refreshEtudes"
        :etude="etudeAConfigurer"></ModalEtude>

    </div>
  </div>
</template>

<script>

import axios from 'axios'
import moment from 'moment'
import _ from 'lodash'

import ModalEtude from './ModalEtude.vue'
import OlMapEtude from './OlMapEtude'

export default {
  name: 'planificationDeci',
  components: {
    ModalEtude,
    OlMapEtude
  },

  data() {
    return {
      listeEtudes: [],
      nbEtudes: 0,
      nbEtudesParPage: 10,
      pageActuelle: 1, // Pagination
      tableKey: 0,
      selectedEtude: null,
      selectedEtudeReseauImporte: null,
      selectedEtudeStatut: null,

      etudeAConfigurer: null,
      showMap: false,

      // Filtres en-tête datagrid
      filters: {
        type: '',
        statut: ''
      },

      sorter: {
        property: 'date_maj',
        direction: 'DESC'
      },

      comboFilterType: [],
      comboFilterStatut: [],
    }
  },

  props: {
    cleIgn: {
      type: String,
      required: false
    },

    bounds : {
      type: String,
      required: false
    }
  },

  filters : {
    printDate: function (value) {
      return moment(value).format('DD/MM/YYYY HH[h]mm');
    },

    printCommunes: function(communes) {
      return communes.map(commune => commune.nom).toString().replace("[", "").replace("]", "").replace("\"", "").replace(",", ", ").trim();
    }
  },

  computed: {
    typesEtude : function() {
      return this.comboFilterType.filter(r => r.value);
    }
  },

  mounted: function() {
    axios.get('/remocra/typeetude').then(response => {
      this.comboFilterType = [{
        text: 'Tous',
        value: ''
      }];
      if(response.data) {
        _.forEach(response.data.data, type => {
          this.comboFilterType.push({
            text: type.nom,
            value: type.code
          })
        })
      }
    }).catch(() => {
      this.$notify({
        group: 'remocra',
        type: 'error',
        title: 'Récupération des données',
        text: "Impossible de récupérer les types d'études"
      });
    }).then(axios.get('/remocra/typeetudestatut').then(response => {
      this.comboFilterStatut = [{
        text: 'Tous',
        value: ''
      }];
      if(response.data) {
        _.forEach(response.data.data, type => {
          this.comboFilterStatut.push({
            text: type.nom,
            value: type.code
          })
        })
      }
    })).catch(() => {
      this.$notify({
        group: 'remocra',
        type: 'error',
        title: 'Récupération des données',
        text: "Impossible de récupérer le statut des types d'études"
      });
    }).then(this.refreshEtudes());
  },

  methods: {
    onFilterChange() {
      this.pageActuelle = 1;
      this.refreshEtudes();
    },

    refreshEtudes() {
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

      let start = (this.pageActuelle - 1) * this.nbEtudesParPage;
      axios.get('/remocra/etudes', {
        params: {
          "filter": JSON.stringify(requeteFiltres),
          "sort": JSON.stringify(requeteSorts),
          "start": start,
          "limit": this.nbEtudesParPage
        }
      }).then(response => {
        this.listeEtudes = response.data.data;
        this.nbEtudes = response.data.total;
        // Force le refresh de la table. La fréquence de tick de la réactivité est parfois trop lente et mène à des problème de style dans la table le temps que les
        // changements soient détectés
        this.tableKey++;
      });
    },

    onSortChange(value) {
      if(this.sorter.property == value) {
        this.sorter.direction = (this.sorter.direction === 'ASC') ? 'DESC' : 'ASC';
      } else {
        this.sorter.direction = 'ASC';
        this.sorter.property = value;
      }

      this.pageActuelle = 1;
      this.refreshEtudes();
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
    },

    getSelectedEtude() {
      var etudes = this.listeEtudes.filter(e => e.id == this.selectedEtude);
      if(etudes.length == 1){
        return etudes[0];
      }
      return null;
    },

    cloreEtude() {
      if(this.selectedEtude) {
        axios.post('/remocra/etudes/clore/'+this.selectedEtude).then(() => {
          this.selectedEtude = null;
          this.refreshEtudes();
        }).catch(() => {
          this.$notify({
            group: 'remocra',
            type: 'error',
            title: 'Erreur cloturation',
            text: "Une erreur est survenue lors de la cloture de l'étude"
          });
        })
      }
    }
  }
};
</script>

<style>

#planificationDeci {
  margin-left: 15px;
  padding-right: 40px;
}

.title {
  font-size: 1.5rem !important;
  margin-bottom: 1rem;
  margin-top: 1rem;
  color: #7b7b7b;
  font-family: Helvetica, Arial !important
}

#boutonsAction {
  margin-bottom: 20px;
}

#boutonsAction button {
  margin-right: 10px;
}

#boutonsAction button img{
  margin-right: 0.375rem;
}

#tableEtudes th{
  vertical-align: top;
}

#tableEtudes th select{
  height: 25px;
  line-height: 18px;
}

#tableEtudes th p {
  padding-left: 2px;
  font-size: 16px;
  margin-bottom: 3px;
  margin-right: 32px;
}

#tableEtudes th:hover {
  background-color: #c0c1c2;
  color: white;
}

.stripeLight {
  background-color: #f8f9fa;
}

.stripeDark {
  background-color: #e1e2e3;
}

#tableEtudes tr {
  background-color: white;
}

#tableEtudes td {
  border: 1px solid #cbd5df;
}

.colDescription {
  max-width: 400px !important;
}
.filter {
  width: auto;
  height: calc(1.7em + 2px);
  padding-left: 2px;
  line-height: 1em;
}

.sort {
  background-repeat: no-repeat;
  background-position: right 5% bottom 85%;
}

.sortAsc {
  background-image: url(../../assets/img/collapse.svg);
}

.sortDesc {
  background-image: url(../../assets/img/expand.svg);
}

.buttonRetour {
  margin-left: 15px;
  margin-right: 30px;
}
.enteteEtude {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}
</style>
