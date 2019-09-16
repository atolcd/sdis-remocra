<template>
<div>
  <div class="title">PEI</div>
  <div class="row">
    <div class="col-md-4">
      <b-form-group label="Diamètre nominal" label-for="diametre" label-cols-md="6">
        <b-form-select id="diametre" v-model="hydrant.diametre" class="parametre" :options="comboDiametre" size="sm"></b-form-select>
      </b-form-group>
    </div>
    <div class="col-md-4 vertical-bottom">
      <b-form-checkbox id="dispositif_inviolabilite" v-model="hydrant.dispositif_inviolabilite" class="parametre" size="sm"> Dispositif d'inviolabilité </b-form-checkbox>
    </div>
    <div class="col-md-4 vertical-bottom">
      <b-form-checkbox id="renversable" v-model="hydrant.renversable" class="parametre" size="sm"> Renversable </b-form-checkbox>
    </div>
  </div>
  <div class="row">
    <div class="col-md-4">
      <b-form-group label="Jumelé avec" label-for="jumele" label-cols-md="6">
        <b-form-select id="jumele" v-model="hydrant.jumele" class="parametre" :options="comboJumele" size="sm"></b-form-select>
      </b-form-group>
    </div>
  </div>
  <div class="row">
    <div class="col-md-4">
      <b-form-group label="Marque" label-for="marque" label-cols-md="3">
        <b-form-select id="marque" v-model="hydrant.marque" class="parametre" :options="comboMarque" size="sm" v-on:change="onMarqueChange"></b-form-select>
      </b-form-group>
    </div>
    <div class="col-md-4">
      <b-form-group label="Modèle" label-for="modele" label-cols-md="3">
        <b-form-select id="modele" v-model="hydrant.modele" class="parametre" :options="comboModele" size="sm"></b-form-select>
      </b-form-group>
    </div>
    <div class="col-md-4">
      <b-form-group label="Année de fabrication" label-for="anneeFabrication" invalid-feedback="L'année n'est pas valide" :state="etats.anneeFabrication" label-cols-md="6">
        <b-form-input id="anneeFabrication" v-model="hydrant.anneeFabrication" class="parametre" type="number" size="sm" :state="etats.anneeFabrication"></b-form-input>
      </b-form-group>
    </div>
  </div>
  <div class="title">Réseau</div>
  <div class="row">
    <div class="col-md-4">
      <b-form-group label="Service des eaux" label-for="serviceEaux" label-cols-md="5">
        <b-form-select id="serviceEaux" v-model="hydrant.serviceEaux" class="parametre" :options="comboServiceEaux" size="sm"></b-form-select>
      </b-form-group>
    </div>
    <div class="col-md-4">
      <b-form-group label="Type de réseau" label-for="typeReseauAlimentation" label-cols-md="5">
        <b-form-select id="typeReseauAlimentation" v-model="hydrant.typeReseauAlimentation" class="parametre" :options="comboTypeReseauAlimentation" size="sm"></b-form-select>
      </b-form-group>
    </div>
    <div class="col-md-4 vertical-bottom">
      <b-form-checkbox id="debitRenforce" v-model="hydrant.debitRenforce" class="parametre" size="sm"> Débit renforcé </b-form-checkbox>
    </div>
  </div>
  <div class="row">
    <div class="col-md-4 vertical-bottom">
      <b-form-group label="Type de canalisation" label-for="typeReseauCanalisation" label-cols-md="6">
        <b-form-select id="typeReseauCanalisation" v-model="hydrant.typeReseauCanalisation" class="parametre" :options="comboTypeReseauCanalisation" size="sm"></b-form-select>
      </b-form-group>
    </div>
    <div class="col-md-4 vertical-bottom">
      <b-form-group label="Diamètre de canalisation" label-for="diametreCanalisation" invalid-feedback="Le diamètre n'est pas valide" :state="etats.diametreCanalisation" label-cols-md="7">
        <b-form-input id="diametreCanalisation" v-model="hydrant.diametreCanalisation" class="parametre" type="number" size="sm" :state="etats.diametreCanalisation"></b-form-input>
      </b-form-group>
    </div>
  </div>
  <div class="row">
    <div class="col-md-6">
      <b-form-group label="Réservoir" label-for="reservoir" label-cols-md="4">
        <b-form-select id="reservoir" v-model="hydrant.reservoir" :options="comboReservoir" class="parametre" size="sm"></b-form-select>
        <button class="btn addBtn" @click.prevent v-b-modal.modalReservoir>
          <img src="../assets/img/add.png">
        </button>
      </b-form-group>
    </div>
  </div>
  <div class="row">
    <div class="col-md-6">
      <b-form-checkbox id="surpresse" v-model="hydrant.surpresse" class="parametre" size="sm" :disabled="!isDeciPrivee"> Réseau surpressé </b-form-checkbox>
    </div>
    <div class="col-md-6">
      <b-form-checkbox id="additive" class="parametre" v-model="hydrant.additive" size="sm" :disabled="!isDeciPrivee"> Réseau additivé </b-form-checkbox>
    </div>
  </div>
  <ModalReservoir v-on:modalReservoirValues="onReservoirCreated"></ModalReservoir>
</div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'
import ModalReservoir from './ModalReservoir.vue'
export default {
  name: 'FicheCaracteristiquesPibi',
  components: {
    ModalReservoir
  },
  data() {
    return {
      comboDiametre: [],
      comboMarque: [],
      comboModele: [],
      comboServiceEaux: [],
      comboTypeReseauAlimentation: [],
      comboTypeReseauCanalisation: [],
      comboReservoir: [],
      comboJumele: [],
      listeDiametres: [],
      etats: {
        anneeFabrication: null,
        diametreCanalisation: null
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
    listeNaturesDeci: {
      required: true,
      type: Array
    },
    geometrie: {
      required: true,
      type: String
    }
  },
  computed: {
    /**
     * Indique si le PEI possède ou non une nature DECI privée
     */
    isDeciPrivee: function() {
      return (this.listeNaturesDeci.length > 0) ? this.hydrant.natureDeci === this.listeNaturesDeci.filter(item => item.code === "PRIVE")[0].id : false;
    }
  },
  watch: {
    'isDeciPrivee': 'onNatureDeciChange'
  },
  mounted: function() {
    this.$emit('resolveForeignKey', ['diametre', 'marque', 'modele', 'typeReseauAlimentation', 'typeReseauCanalisation', 'reservoir', 'serviceEaux', 'jumele']);
    this.$emit('getComboData', this, 'comboMarque', '/remocra/typehydrantmarques.json', null, 'id', 'nom');
    this.$emit('getComboData', this, 'comboServiceEaux', '/remocra/organismes.json', {
      "filter": JSON.stringify([{
        "property": "typeOrganismeCode",
        "value": "SERVICEEAUX"
      }])
    }, 'id', 'nom');
    this.$emit('getComboData', this, 'comboTypeReseauAlimentation', '/remocra/typereseaualimentation.json', null, 'id', 'nom');
    this.$emit('getComboData', this, 'comboTypeReseauCanalisation', '/remocra/typereseaucanalisation.json', null, 'id', 'nom');
    this.$emit('getComboData', this, 'comboReservoir', '/remocra/reservoir.json', null, 'id', 'nom', 'Aucun');
    axios.get('/remocra/typehydrantdiametres.json').then(response => {
      this.listeDiametres = [];
      _.forEach(response.data.data, item => {
        var tabNatures = [];
        _.forEach(item.natures, nature => {
          tabNatures.push(nature.code);
        });
        this.listeDiametres.push({
          text: item.nom,
          value: item.id,
          natures: tabNatures
        })
      });
    }).then(() => {
      if (this.hydrant.nature !== null) {
        this.updateComboDiametres(this.hydrantRecord.nature.nom);
        this.hydrant.diametre = (this.hydrantRecord.diametre) ? this.hydrantRecord.diametre.id : null;
      }
    }).catch(function(error) {
      console.error('Retrieving combo data from /remocra/typehydrantdiametres', error);
    })
    let nature = this.hydrantRecord.nature ? this.hydrantRecord.nature.code : null;
    this.updateComboJumelage(nature);
    this.onMarqueChange();
  },
  methods: {
    /**
     *	En cas de changement de marque, on met à jour la liste des modèles en conséquence
     */
    onMarqueChange() {
      var self = this;
      this.comboModele = [];
      self.comboModele.push({
        text: 'Aucun',
        value: null
      });
      if (this.hydrant.marque) {
        axios.get('/remocra/typehydrantmarques.json', {
          params: {
            filter: JSON.stringify([{
              "property": "id",
              "value": this.hydrant.marque
            }])
          }
        }).then(response => {
          if (response.data.data) {
            _.forEach(response.data.data[0].modeles, function(item) {
              self.comboModele.push({
                text: item.nom,
                value: item.id
              });
            });
          }
        }).catch(function(error) {
          console.error('Retrieving combo data from /remocra/typehydrantmarques ', error);
        });
      }
      if (this.hydrantRecord.marque && this.hydrantRecord.marque.id != this.hydrant.marque) {
        this.hydrant.modele = null;
      } else {
        this.hydrant.modele = (this.hydrantRecord.modele) ? this.hydrantRecord.modele.id : null;
      }
    },
    checkFormValidity() {
      this.etats.anneeFabrication = (this.hydrant.anneeFabrication > 0 || !this.hydrant.anneeFabrication) ? 'valid' : 'invalid';
      this.etats.diametreCanalisation = (this.hydrant.diametreCanalisation > 0 || !this.hydrant.diametreCanalisation) ? 'valid' : 'invalid';
      return this.etats;
    },
    onReservoirCreated(values) {
      this.comboReservoir.push({
        text: values.nom,
        value: values.id
      });
    },
    onNatureDeciChange() {
      if (!this.isDeciPrivee) {
        this.hydrant.surpresse = false;
        this.hydrant.additive = false;
      }
    },
    /**
     * Mise à jour de la combo de jumelage
     * @value La nature du PEI (BI, PI, etc), format texte
     * @geom Une géométrie. Paramètre optionnel. Si renseigné, la requête prendra en compte cette géométrie plutôt que celle initiale du PEI
     */
    updateComboJumelage(value, geom) {
      if (value === 'BI') {
        this.comboJumele = [];
        axios.get('/remocra/hydrantspibi/findjumelage', {
          params: {
            geometrie: (geom) ? geom : this.geometrie
          }
        }).then(response => {
          _.forEach(response.data.data, item => {
            if (this.hydrant.id == null || this.hydrant.id != item.id) {
              this.comboJumele.push({
                text: item.numero,
                value: item.id
              });
            }
          });
          if (this.hydrantRecord.jumele) {
            this.comboJumele.push({
              text: this.hydrantRecord.jumele.numero,
              value: this.hydrantRecord.jumele.id
            });
          }
          this.comboJumele.unshift({
            text: 'Aucun',
            value: null
          });
          this.hydrant.jumele = (this.hydrantRecord.jumele) ? this.hydrantRecord.jumele.id : null;
        }).catch(function(error) {
          console.error('Retrieving combo data from /remocra/hydrantspibi/findjumelage', error);
        })
      } else {
        this.comboJumele = [{
          text: 'Aucun',
          value: null
        }]
        this.hydrant.jumele = null;
      }
    },
    /**
     * Si la nature du PEI change, on va alimenter la combo Diametre avec les valeurs possibles pour cette nouvelle nature
     */
    updateComboDiametres(code) {
      this.hydrant.diametre = null;
      this.comboDiametre = this.listeDiametres.filter(item => item.natures.indexOf(code) !== -1);
    }
  }
};
</script>

<style scoped>
.vertical-bottom {
  display: flex;
}

.addBtn {
  padding: .25rem .5rem;
  font-size: .875rem;
  line-height: 1.5;
  border-radius: .2rem;
  position: absolute;
}
</style>
