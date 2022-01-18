<template>
<div>
  <b-modal id="importCTPResultat" size="lg"  v-bind:title="fullTitle" centered ref="importCTPResultat" @show="resetModal" @hidden="resetModal">
    <div id="tableScrollResCTP">
      <table class="table table-sm table-bordered">
        <thead class="thead-light">
          <th scope="col">N°Ligne</th>
          <th scope="col">Code Inssee</th>
          <th scope="col">N° du PEI</th>
          <th scope="col">Date du CT</th>
          <th scope="col">Bilan du contrôle</th>
        </thead>
        <tbody>
          <tr v-for="(item, index) in items" :key="index" :class="item.bilan_style">
            <td>
              {{item.numero_ligne}}
            </td>
            <td>
              {{item.insee}}
            </td>
            <td>
              {{item.numeroInterne}}
            </td>
            <td>
              {{item.dateCtp}}
            </td>
            <td v-if="item.warnings">
              <li v-for="war in item.warnings" :key="war">{{ war }}</li>
            </td>
            <td v-else>{{item.bilan}}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="col-md-5">
        <p align="right">Nombre de CT valides : {{nbCTValides}}</p>
        <p align="right">Nombre de CT valides avec avertissements : {{nbCTValidesWarn}}</p>
        <p align="right">Nombre de CT rejetés : {{nbCTRejetes}}</p>
        <p align="right">Nombre de CT rejetés car non renseignés : {{nbCTRejetesNR}}</p>
    </div>
    <template #modal-footer="">
      <b-button size="sm" type="submit" variant="primary" @click="exportResultat">Exporter le résultat du controle des données</b-button>
      <b-button size="sm" type="submit" variant="primary" @click="importControle">Importer les contrôles techniques valides</b-button>
      <b-button size="sm" type="reset"  variant="secondary" @click="$bvModal.hide('importCTPResultat')">Annuler</b-button>
    </template>
  </b-modal>
</div>
</template>

<script>
import _ from 'lodash'
import axios from 'axios'

export default {
  name: 'ImportCTPResultat',
  data() {
    return {
       items: [],
       nbCTValides: null,
       nbCTValidesWarn: null,
       nbCTRejetes: null,
       nbCTRejetesNR: null,
       fullTitle: null,
       dataVisites: []
    }
  },

  methods: {
    loadData(bilanVerifications, fileName) {
      this.items = bilanVerifications;
      this.fullTitle = "Résultat du contrôle du fichier : " + fileName;
      this.nbCTValides = this.items.filter(bilanVerif => bilanVerif.bilan_style == "OK" ||  bilanVerif.bilan_style == "WARNING").length;
      this.nbCTValidesWarn =  this.items.filter(bilanVerif => bilanVerif.bilan_style == "WARNING").length;
      this.nbCTRejetes =  this.items.filter(bilanVerif => bilanVerif.bilan_style == "ERREUR" || bilanVerif.bilan_style == "INFO").length;
      this.nbCTRejetesNR = this.items.filter(bilanVerif => bilanVerif.bilan_style == "INFO").length;
      this.dataVisites = _.filter(_.map(bilanVerifications, 'dataVisite'), o => o != null);
    },
    exportResultat() {
      var csvContent = "data:text/csv;charset=utf-8,"
      csvContent+= "N°Ligne;Code Insee;N° du PEI;Date du CT;Bilan du contrôle\n";
      var rows = [];
      _.forEach(this.items, item => {
        var row = [item.numero_ligne, item.insee, item.numeroInterne, item.dateCtp ? item.dateCtp : "", this.getBilan(item)];
        rows.push(row);
      });
      // transforme les données en CSV
      rows.forEach(function (row) {
        row.forEach(function (v) {
          csvContent += '"' + (new String(v).replace(/"/g, '""')) + '";';
          });
        csvContent += "\n";
      });
      const data = encodeURI(csvContent);
      const link = document.createElement("a");
      link.setAttribute("href", data);
      link.setAttribute("download", "exportResultatControle.csv");
      link.click();
    },
    getBilan(item) {
      if(item.warnings) {
        return item.warnings.join('\n');
      }
      return item.bilan;
    },
    importControle() {
      var formData = new FormData();
      formData.append('visites', JSON.stringify(this.dataVisites));
      axios.post('/remocra/hydrants/importctp', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(() => {
        this.$bvModal.hide('importCTPResultat');
      })
    }
  }
};
</script>

<style>
#importCTPResultat .invalid-feedback {
  font-size: 12px;
}

#importCTPResultat .modal-title {
  color: #7B7B7B;
  font-size: 20px;
  font-family: sans-serif, arial, verdana;
}

#tableScrollResCTP {
  margin-top: 5px;
  max-height: 500px;
  overflow: auto;
}

.ERREUR {
  background-color: #d9534f;
}

.WARNING {
  background-color: #f0ad4e;
}

.OK {
  background-color: #5cb85c;
}
</style>
