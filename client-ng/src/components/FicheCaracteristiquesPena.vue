<template>
<div>
  <div class="title">Ressource</div>
  <div class="row">
    <div class="col-md-3 vertical-bottom">
      <b-form-checkbox id="illimitee" v-model="hydrant.illimitee" :disabled="disableIllimitee || modificationCaracteristiquesDisabled" class="parametre" size="sm" v-on:change="onIllimiteeChecked"> Capacité illimitée </b-form-checkbox>
    </div>
    <div class="col-md-3 vertical-bottom">
      <b-form-checkbox id="incertaine" v-model="hydrant.incertaine" class="parametre" size="sm" :disabled="disableIncertaine || modificationCaracteristiquesDisabled" v-on:change="onIncertaineChecked"> Ressource incertaine </b-form-checkbox>
    </div>
    <div class="col-md-3 vertical-bottom">
      <b-form-group label="Capacité en m3" label-for="capacite" invalid-feedback="La capacité n'est pas valide" label-cols-sm="6" :state="etats.capacite">
        <b-form-input id="capacite" v-model="hydrant.capacite" class="parametre" type="text" size="sm" :state="etats.capacite" :disabled="hydrant.illimitee || modificationCaracteristiquesDisabled"></b-form-input>
      </b-form-group>
    </div>
    <div class="col-md-3">
      <b-form-group label="Q appoint en m3/h" label-for="QAppoint" invalid-feedback="Le débit d'appoint n'est pas valide" label-cols-sm="7" :state="etats.QAppoint">
        <b-form-input id="QAppoint" v-model="hydrant.QAppoint" class="parametre" type="number" size="sm" step="any" :state="etats.QAppoint" :disabled="hydrant.illimitee || modificationCaracteristiquesDisabled"></b-form-input>
      </b-form-group>
    </div>
  </div>
  <div class="row">
    <div class="col-md-6">
      <b-form-group label="Matériau de la citerne" label-for="materiau" label-cols-md="4">
        <b-form-select id="materiau" v-model="hydrant.materiau" class="parametre" :options="comboMateriau" size="sm" :disabled="modificationCaracteristiquesDisabled"></b-form-select>
      </b-form-group>
    </div>
  </div>
  <div class="title">Aspiration</div>
  <div class="row">
    <div class="col-md-4">
      <button class="btn btn-outline-primary" @click.prevent @click="createAspiration" :disabled="modificationCaracteristiquesDisabled">Nouvelle aire</button>
      <button class="btn btn-outline-danger btn-suppr" @click.prevent @click="deleteAspiration" :disabled="modificationCaracteristiquesDisabled">Supprimer</button>
    </div>
  </div>
  <div class="row">
    <div class="col-md-12">
      <div id="tableScroll">
        <table class="table table-striped table-sm table-bordered" id="tableAspirations">
          <thead class="thead-light">
            <th scope="col">Numéro</th>
            <th scope="col">Normalisée</th>
            <th scope="col">Hauteur d'aspiration > 3m</th>
            <th scope="col">Dispositif d'aspiration</th>
            <th scope="col">Déporté</th>
            <th scope="col">Lon</th>
            <th scope="col">Lat</th>
          </thead>
          <tbody>
            <tr v-for="(item, index) in listeAspirations" :key="index" @click="onRowSelected(index)">
              <td>
                <b-form-input v-model="item.numero" type="text" size="sm" :disabled="modificationCaracteristiquesDisabled"></b-form-input>
              </td>
              <td>
                <b-form-checkbox v-model="item.normalise" size="sm" :disabled="modificationCaracteristiquesDisabled"></b-form-checkbox>
              </td>
              <td>
                <b-form-checkbox v-model="item.hauteur" size="sm" class="mx-auto" :disabled="modificationCaracteristiquesDisabled"></b-form-checkbox>
              </td>
              <td>
                <b-form-select v-model="item.typeAspiration" :options="comboTypeAspiration" size="sm" :disabled="modificationCaracteristiquesDisabled"></b-form-select>
              </td>
              <td>
                <b-form-checkbox v-model="item.deporte" size="sm" v-on:change="onDeporteChecked($event, index)" :disabled="modificationCaracteristiquesDisabled"></b-form-checkbox>
              </td>
              <td>
                <b-form-input v-model="item.longitude" type="text" size="sm" pattern="[-+]?[0-9]*\.?[0-9]*" class="coordValidation" :id="'longitude-'+index" :disabled="modificationCaracteristiquesDisabled"></b-form-input>
              </td>
              <td>
                <b-form-input v-model="item.latitude" type="text" size="sm" pattern="[-+]?[0-9]*\.?[0-9]*" class="coordValidation" :id="'latitude-'+index" :disabled="modificationCaracteristiquesDisabled"></b-form-input>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="col-md-12">
      <b-form-checkbox id="hbe" v-model="hydrant.hbe" class="parametre" size="sm" :disabled="modificationCaracteristiquesDisabled"> Equipé HBE </b-form-checkbox>
    </div>
  </div>
</div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'
export default {
  name: 'FichePena',
  data() {
    return {
      disableIllimitee: false,
      disableIncertaine: false,
      comboMateriau: [],
      comboTypeAspiration: [],
      listeAspirations: [],
      listeAspirationsRecord: [],
      aspirationsASupprimer: [],
      selectedRow: null,
      etats: {
        capacite: null,
        QAppoint: null,
        coordonnees: null
      }
    }
  },
  props: {
    hydrant: {
      required: true,
      type: Object
    },
    hydrantRecord: {
      required: true,
      type: Object
    },
    utilisateurDroits: {
      required: true,
      type: Array,
    }
  },

  computed: {
    modificationCaracteristiquesDisabled: function() {
      return this.utilisateurDroits.indexOf('HYDRANTS_MCO_C') == -1
    }
  },

  mounted: function() {
    this.$emit('resolveForeignKey', ['materiau']);
    this.$emit('getComboData', this, 'comboMateriau', '/remocra/typehydrantmateriaus.json', null, 'id', 'nom');
    this.$emit('getComboData', this, 'comboTypeAspiration', '/remocra/typehydrantaspiration.json', null, 'id', 'nom');
    this.aspirationsASupprimer = [];
    // Si  l'hydrant a un identifiant, c'est une modification. Dans ce cas, on charge les aspirations existantes si elles existent
    if (this.hydrant.id != null) {
      var self = this;
      axios.get('/remocra/hydrantaspiration', {
        params: {
          filter: JSON.stringify([{
            "property": "pena",
            "value": this.hydrant.id
          }])
        }
      }).then(response => {
        self.listeAspirations = response.data.data;
        _.forEach(self.listeAspirations, function(item) {
          item.pena = self.hydrant.id;
          item.typeAspiration = (item.typeAspiration) ? item.typeAspiration.id : null;
          item.oldLongitude = item.longitude;
          item.oldLatitude = item.latitude;
        });
        self.listeAspirationsRecord = JSON.parse(JSON.stringify(self.listeAspirations));
      }).then(function() {
        _.forEach(self.listeAspirations, function(item) {
          self.onDeporteChecked(item.deporte, self.listeAspirations.indexOf(item));
        })
      });
    }
    this.onIllimiteeChecked(typeof(this.hydrant.illimite) !== 'undefined' ? this.hydrant.illimite : false);
  },
  methods: {
    /**
     * Evènement lors d'un changement d'état de la checkbox de capacité illimitée
     * Si cochée, on désactive et on vide les champs à sa droite
     * Si décochée, on active les champs et on les remplit avec leurs valeurs initiales
     */
    onIllimiteeChecked(value) {
      if (value) {
        this.hydrant.incertaine = false;
        this.hydrant.capacite = "";
        this.hydrant.QAppoint = null;
      } else {
        this.hydrant.incertaine = typeof(this.hydrantRecord.incertaine) !== 'undefined' ? this.hydrant.incertaine : false;
        this.hydrant.capacite = this.hydrantRecord.capacite;
        this.hydrant.QAppoint = this.hydrantRecord.QAppoint;
      }
      this.disableIncertaine = value
    },
    onIncertaineChecked(value) {
      if (value) {
        this.hydrant.illimitee = false;
      } else {
        this.hydrant.illimitee = typeof(this.hydrantRecord.illimitee) !== 'undefined' ? this.hydrant.illimitee : false;
      }
      this.disableIllimitee = value
    },
    /**
     * Evènement lors d'un changement d'état de la checkbox "déporté"
     * Si cochée, on active les saises de longitude et latitude. On remplit les champs avec les données initiales si elle sont présentes
     * Si décochée, on vide les saisies de longitude et latitude, et on les désactive
     */
    onDeporteChecked(value, index) {
      this.listeAspirations[index].longitude = (value) ? this.listeAspirations[index].oldLongitude : '';
      this.listeAspirations[index].latitude = (value) ? this.listeAspirations[index].oldLatitude : '';
      document.getElementById('longitude-' + index).disabled = !value;
      document.getElementById('latitude-' + index).disabled = !value;
    },
    /**
     * Clic sur le bouton "nouvelle aire"
     */
    createAspiration() {
      this.listeAspirations.push({});
    },
    /**
     * Clic sur le bouton "Supprimer"
     * L'id des aspirations est stocké (si il existe) en mémoire. On les supprime tous en une seule requête à la validation
     */
    deleteAspiration() {
      if (this.selectedRow !== null) {
        // Aspiration enregistrée dans la base
        if (this.listeAspirations[this.selectedRow].id !== undefined) {
          this.aspirationsASupprimer.push(this.listeAspirations[this.selectedRow].id);
        }
        this.listeAspirations.splice(this.selectedRow, 1);
        this.selectedRow = null;
        this.onRowSelected(-1);
      }
    },
    /**
     * Lorsqu'une ligne est sélectionnée, elle obtient la classe "rowSelected"
     * Une ligne est sélectionnée si l'on clique dessus, ou bien si l'on modifie l'un des champs présents dans les cellules de la ligne
     */
    onRowSelected(index) {
      var table = document.getElementById('tableAspirations');
      _.forEach(table.getElementsByTagName('tr'), item => {
        item.classList.remove("table-success");
        if (item.rowIndex == index) {
          item.classList.add("table-success");
        }
      });
      this.selectedRow = index;
    },
    checkFormValidity() {
      var regexCapacite = /^\d+$/;
      var regexQAppoint = /^(\d+(?:\.\d*)?)$/;
      this.etats.capacite = (!this.hydrant.capacite || regexCapacite.test(this.hydrant.capacite)) ? 'valid' : 'invalid';
      this.etats.QAppoint = (!this.hydrant.QAppoint || regexQAppoint.test(this.hydrant.QAppoint)) ? 'valid' : 'invalid';
      this.etats.coordonnees = 'valid';
      _.forEach(document.getElementsByClassName('coordValidation'), item => {
        if (!item.validity.valid) {
          this.etats.coordonnees = 'invalid';
        }
      });
      return this.etats;
    },
    prepareAspirationData(idPena) {
      _.forEach(this.listeAspirations, function(aspiration) {
        aspiration.pena = idPena;
        aspiration.version = 1
      });
      return {
        'aspirations': JSON.stringify(this.listeAspirations, function(key, value) {
          return value === "" ? null : value
        }),
        'aspirationsDel': this.aspirationsASupprimer
      }
    }
  }
};
</script>

<style scoped>
.vertical-bottom {
  display: flex;
  align-items: center;
}

#tableScroll {
  margin-top: 5px;
  max-height: 270px;
  overflow: auto;
}

#tableScroll th {
  text-align: left;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
  font-size: 1rem;
  color: #495057;
  font-size: 1rem;
  font-weight: 400;
  line-height: 1.5;
  vertical-align: middle;
}

.btn-suppr {
  margin-left: 10px;
}
</style>