<template>
<div>
  <b-modal id="importCTPResultat" size="lg"  v-bind:title="fullTitle" centered ref="importCTPResultat"  @hidden="close">
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
          <tr v-for="(item, index) in items" :key="index" :class="item.bilanStyle">
            <td>
              {{item.numeroLigne}}
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
        <p align="left">Nombre de CT valides : {{nbCTValides}}</p>
        <p align="left">Dont : {{nbCTValidesWarn}} CT valides avec avertissements</p>
        <p align="left">Nombre de CT rejetés : {{nbCTRejetes}}</p>
        <p align="left">Dont : {{nbCTRejetesNR}}  CT rejetés car non renseignés</p>
    </div>
    <template #modal-footer="">
      <b-button size="sm" type="submit" variant="primary" @click="exportResultat">Exporter le résultat du controle des données</b-button>
      <b-button size="sm" type="submit" variant="primary" @click="importControle" :disabled="nbCTValides === 0 || importDejaFait">Importer les contrôles techniques valides</b-button>
      <b-button size="sm" type="reset"  variant="secondary" @click="$bvModal.hide('importCTPResultat')">Annuler</b-button>
    </template>
    <notifications group="remocra" position="top right" animation-type="velocity" :duration="3000" />
  </b-modal>
</div>
</template>

<script>
import _ from 'lodash'
import axios from 'axios'
import {
  loadProgressBar
} from 'axios-progress-bar'

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
       dataVisites: [],
       importDejaFait: false,
    }
  },

  methods: {
    loadData(bilanVerifications, fileName) {
      this.items = bilanVerifications;
      this.fullTitle = "Résultat du contrôle du fichier : " + fileName;
      this.nbCTValides = this.items.filter(bilanVerif => bilanVerif.bilanStyle == "OK" ||  bilanVerif.bilanStyle == "WARNING").length;
      this.nbCTValidesWarn =  this.items.filter(bilanVerif => bilanVerif.bilanStyle == "WARNING").length;
      this.nbCTRejetes =  this.items.filter(bilanVerif => bilanVerif.bilanStyle == "ERREUR" || bilanVerif.bilanStyle == "INFO").length;
      this.nbCTRejetesNR = this.items.filter(bilanVerif => bilanVerif.bilanStyle == "INFO").length;
      this.dataVisites = _.filter(_.map(bilanVerifications, 'dataVisite'), o => o != null);
    },
    exportResultat() {
      var csvContent = "N°Ligne;Code Insee;N° du PEI;Date du CT;Bilan du contrôle\n";
      var rows = [];
      _.forEach(this.items, item => {
        var row = [item.numeroLigne, item.insee, item.numeroInterne, item.dateCtp ? item.dateCtp : "", this.getBilan(item)];
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
      // UTF-8 avec BOM
      link.setAttribute("href", "data:text/csv;charset=utf-8,%EF%BB%BF" + data);
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
      // On précise que l'import des CTP a déjà été fait pour éviter que l'utilisateur réimporte les CTP valides
      this.importDejaFait = true
      //on notifie que l'import est en cours
      this.$notify({
        group: 'remocra',
        type: 'success',
        title: 'Succès',
        text: 'L\'import est en cours... veuillez patienter quelques instants'
      });

      // on cache la modale

      setTimeout(() =>{
        this.$bvModal.hide('importCTPResultat');
      }, 2000)


      var formData = new FormData();
      formData.append('visites', JSON.stringify(this.dataVisites));
      loadProgressBar({
        parent: "#importCTPResultat",
        showSpinner: false
      });
      axios.post('/remocra/hydrants/importctp', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
    },
    close() {
      this.$root.$options.bus.$emit('closed')
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
