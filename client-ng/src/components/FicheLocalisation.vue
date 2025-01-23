<template>
<div>
  <div class="title">Coordonnées géographiques</div>
  <div class="row">
    <div class="col-md-6">
      <b-form-group label="Système" label-for="systeme" label-cols-md="2">
        <b-form-select v-model="systeme" :options="comboSysteme" size="sm" v-on:change="onSystemeChange" required></b-form-select>
      </b-form-group>
    </div>
  </div>
  <div class="row">
    <div class="col-md-6">
      <b-form-group :label="systeme != 4326 && systeme != -1 ? 'X' : 'Longitude: '" label-for="domaine" label-cols-md="2">
        <b-form-input id="longitude" v-model="longitude" type="text" size="sm" :disabled="utilisateurDroits.indexOf('HYDRANTS_DEPLACEMENT_C') == -1" v-on:change="$emit('onCoordsChange', longitude, latitude)" required></b-form-input>
      </b-form-group>
    </div>
    <div class="col-md-6">
      <b-form-group :label="systeme != 4326 && systeme != -1 ? 'Y' : 'Latitude: '" label-for="domaine" label-cols-md="2">
        <b-form-input id="latitude" v-model="latitude" type="text" size="sm" :disabled="utilisateurDroits.indexOf('HYDRANTS_DEPLACEMENT_C') == -1" v-on:change="$emit('onCoordsChange', longitude, latitude)" required></b-form-input>
      </b-form-group>
    </div>
  </div>
  <div class="title">Adresse</div>
  <div class="row">
    <div class="col-md-6">
      <b-form-group label="Commune" label-for="commune" invalid-feedback="La commune doit être renseignée" :state="etats.commune" label-cols-md="3" class="requiredInput">
        <b-form-select id="commune" v-model="hydrant.commune" class="parametre" :options="comboCommune" size="sm" :state="etats.commune" required :disabled="modificationAdresseDisabled"></b-form-select>
      </b-form-group>
    </div>
    <div class="col-md-6">
      <b-form-group label="Domaine" label-for="domaine" invalid-feedback="Le domaine doit être renseigné" :state="etats.domaine" label-cols-md="2" class="requiredInput">
        <b-form-select id="domaine" v-model="hydrant.domaine" class="parametre" :options="comboDomaine" size="sm" :state="etats.domaine" required :disabled="modificationAdresseDisabled"></b-form-select>
      </b-form-group>
    </div>
  </div>
  <div class="row">
    <div class="col-md-3">
      <b-form-group label="Numéro de voie" label-for="numeroVoie" invalid-feedback="Le numéro de voie doit être supérieur ou égal à 0" :state="etats.numeroVoie" label-cols-md="7">
        <b-form-input id="numeroVoie" v-model="hydrant.numeroVoie" class="parametre" type="number" min="0" size="sm" :state="etats.numeroVoie" :disabled="modificationAdresseDisabled"></b-form-input>
      </b-form-group>
    </div>
    <div class="col-md-2">
      <b-form-group label="Suffixe" label-for="suffixeVoie" label-cols-md="5">
        <b-form-input id="suffixeVoie" v-model="hydrant.suffixeVoie" class="parametre" type="text" size="sm" :disabled="modificationAdresseDisabled"></b-form-input>
      </b-form-group>
    </div>
    <div class="col-md-2">
      <b-form-checkbox id="enFace" v-model="hydrant.enFace" class="parametre" size="sm" :disabled="modificationAdresseDisabled"> Situé en face </b-form-checkbox>
    </div>
    <div class="col-md-5">
      <b-form-group label="Niveau" label-for="niveau" label-cols-md="2">
        <b-form-select id="niveau" v-model="hydrant.niveau" class="parametre" :options="comboNiveau" size="sm" :disabled="modificationAdresseDisabled"></b-form-select>
      </b-form-group>
    </div>
  </div>
  <div class="row">
    <div class="col-md-6">
      <b-form-group label="Voie" label-for="voie" invalid-feedback="La voie doit être renseignée" :state="etats.voie" label-cols-md="2" v-if="typeof hydrant.commune == 'number'" class="requiredInput">
        <SearchVoie id="voie" :commune="hydrant.commune" :geometrie="geometrie" :defaultValue="hydrant.voie" @onVoieChange="onVoieChange" :disabled="modificationAdresseDisabled" attr="voie" class="parametre autocomplete"></SearchVoie>
      </b-form-group>
    </div>
    <div class="col-md-6">
      <b-form-group label="Carrefour" label-for="voie2" label-cols-md="2" v-if="typeof hydrant.commune == 'number'">
        <SearchVoie id="voie2" :commune="hydrant.commune" :geometrie="geometrie" :defaultValue="hydrant.voie2" @onVoieChange="onVoieChange" :disabled="modificationAdresseDisabled" attr="voie2" class="parametre autocomplete"></SearchVoie>
      </b-form-group>
    </div>
  </div>
  <div class="row">
    <div class="col-md-12">
      <b-form-group label="Lieu-dit" label-for="lieuDit" label-cols-md="1">
        <b-form-input id="lieuDit" v-model="hydrant.lieuDit" class="parametre" type="text" size="sm" :disabled="modificationAdresseDisabled"></b-form-input>
      </b-form-group>
    </div>
  </div>
  <div class="row">
    <div class="col-md-12">
      <b-form-group label="Complément" label-for="complement">
        <b-form-textarea id="complement" v-model="hydrant.complement" class="parametre" size="sm" :disabled="modificationAdresseDisabled"></b-form-textarea>
      </b-form-group>
    </div>
  </div>
</div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'
import SearchVoie from './SearchVoie.vue'
import { getSrid } from './utils/FunctionsUtils'

export default {
  name: 'FicheLocalisation',
  components: {
    SearchVoie,
  },
  data() {
    return {
      comboSysteme: [],
      comboCommune: [],
      comboDomaine: [],
      comboNiveau: [],
      comboVoie: [],
      comboCarrefour: [],
      systeme: null,
      srid: null,
      latitude: '',
      longitude: '',
      etats: {
        numeroVoie: null,
        voie: null,
        domaine: null,
        commune: null,
        natureDeci: null,
      },
    }
  },
  props: {
    hydrant: {
      required: true,
      type: Object,
    },
    utilisateurDroits: {
      required: true,
      type: Array,
    },
    geometrie: {
      required: true,
      type: String,
    },
  },
  computed: {
    modificationAdresseDisabled: function() {
      return this.utilisateurDroits.indexOf('HYDRANTS_ADRESSE_C') == -1
    },
  },
  mounted: async function() {
    this.srid = await getSrid();
    
    this.$emit('resolveForeignKey', ['commune', 'domaine', 'niveau'])
    // Récupération des communes selon la position du PEI
    // les communes sont triées de la plus proche à la plus éloignée
    axios.get('/remocra/communes/xy', {
      params: {
        wkt: this.geometrie,
        srid: this.srid,
      },
    }).then(response => {
      _.forEach(response.data.data, item => {
        this.comboCommune.push({
          text: item.nom,
          value: item.id,
        })
      })

      // La première commune est automatiquement sélectionnée si l'hydrant n'en a aucune ou si celle déjà renseignée n'est pas présente dans la liste
      if(this.comboCommune.length > 0) {
        var indexCommuneRecord = _.findIndex(this.comboCommune, c => {return c.value == this.hydrant.commune});
        if(this.hydrant.commune == null || indexCommuneRecord == -1) {
          this.hydrant.commune = this.comboCommune[0].value
        }
      }
    })
    this.$emit('getComboData', this, 'comboDomaine', '/remocra/typehydrantdomaines.json', null, 'id', 'nom')
    this.$emit('getComboData', this, 'comboNiveau', '/remocra/typehydrantniveau.json', null, 'id', 'nom', 'Aucun')
    //Combo des systèmes de coordonnées
    this.comboSysteme.push({
      text: SRID == 2154 ?'Lambert 93' : SRID == 2972 ? 'RGFG95' : SRID == 32620 ?'UTM 20 Nord - WGS84' : '',
      value: SRID,
    }, {
      text: 'WGS84 degrés décimaux',
      value: 4326,
    }, {
      text: 'WGS84 degrés sexagésimaux',
      value: -1,
    })
    this.systeme = this.srid;
    this.onSystemeChange()
  },
  methods: {
    /**
     * En cas de changement de système de coordonnées on interroge le serveur qui effectue la conversion
     */
    onSystemeChange() {
      var srid = this.systeme == -1 ? 4326 : this.systeme
      var degres = this.systeme == -1 ? false : true //Flag pour les degrés (0 = sexagésiamaux, 1 = décimaux)
      var coords = this.geometrie.replace(/\(|\)/g, ' ').split(' ').filter(item => item !== '')
      this.longitude = Math.round(coords[1])
      this.latitude = Math.round(coords[2])
      var self = this
      axios.get('/remocra/hydrants/transformCoordonnees?srid=' + srid + '&degres=' + degres + '&longitude=' + this.longitude + '&latitude=' + this.latitude).then(response => {
        var coords = response.data.message.split(',')
        self.longitude = coords[0]
        self.latitude = coords[1]
      }).catch(function(error) {
        console.error('Retrieving coordonnees from /remocra/hydrants/transformCoordonnees?srid=' + srid + '&degres=' + degres + '&longitude=' + this.longitude + '&latitude=' + this.latitude, error)
      })
    },
    onVoieChange(attr, value) {
      this.hydrant[attr] = value
    },
    checkFormValidity() {
      this.etats.numeroVoie = !this.hydrant.numeroVoie || this.hydrant.numeroVoie >= 0 ? 'valid' : 'invalid'
      this.etats.voie = this.hydrant.voie ? 'valid' : 'invalid'
      this.etats.domaine = this.hydrant.domaine !== null ? 'valid' : 'invalid'
      this.etats.commune = this.hydrant.commune !== null ? 'valid' : 'invalid'
      return this.etats
    },
    // Informations pour la mise à jour des coordonnées
    getLocalisationData() {
      return {
        latitude: this.latitude,
        longitude: this.longitude,
        systeme: this.systeme == -1 ? 4326 : this.systeme,
        degres: this.systeme == -1 ? false : true,
      }
    },
  },
}
</script>

<style></style>
