<template>
  <div class='Fiche'>
    <form id='formFiche' name='fiche' enctype="multipart/form-data" method="POST" ref="formFiche">
<!-- ================================== En-tête du formulaire ==================================-->
      <div id="entete" class="form-group">
        <div class="row">
          <div class="col-md-4">
            <b-form-group invalid-feedback="Le numéro du PEI est manquant" 
                          :state="etats.numeroInterne"
                          label="Numéro interne : "
                          label-for="numeroInterne">
              <b-form-input type="text" 
                            id="numeroInterne" 
                            v-model="hydrant.numeroInterne" 
                            :disabled="!idHydrant"
                            class="parametre" 
                            size="sm"
                            :state="etats.numeroInterne">
              </b-form-input>
            </b-form-group>
          </div>

          <div class="col-md-4">
            <b-form-group label="Nature : " label-for="nature" invalid-feedback="La nature doit être renseignée" :state="etats.nature">
              <b-form-select id="nature" v-model="hydrant.nature" class="parametre" :options="comboType" size="sm" :state="etats.nature" required ></b-form-select>
            </b-form-group>
          </div>

          <div class="col-md-4">
            <b-form-group label="Autorité de police DECI : " label-for="autoriteDeci" invalid-feedback="L'autorité de police DECI doit être renseignée'" :state="etats.autoriteDeci">
              <b-form-select id="autoriteDeci" v-model="hydrant.autoriteDeci" class="parametre" :options="comboAutoriteDeci" size="sm" :state="etats.autoriteDeci" required></b-form-select>
            </b-form-group>
          </div>
        </div>

        <div class="row">
          <div class="col-md-4">
            <b-form-group label="Type de DECI : "
                          label-for="nature_deci" 
                          invalid-feedback="La nature DECI doit être renseignée'" 
                          :state="etats.natureDeci">
              <b-form-select v-model="hydrant.natureDeci" :options="comboDeci" size="sm" id="natureDeci" class="parametre" v-on:change="getComboGestionnaire" :state="etats.natureDeci"></b-form-select>
            </b-form-group>
          </div>
 
          <div class="col-md-4">
            <b-form-group label="Gestionnaire : " label-for="gestionnaire" invalid-feedback="Le gestionnaire doit être renseigné" :state="etats.gestionnaire">
              <b-form-select  id="gestionnaire"
                              v-model="hydrant.gestionnaire" 
                              class="parametre" 
                              :options="comboGestionnaire" 
                              size="sm" 
                              v-on:change="onGestionnaireChange"
                              :state="etats.gestionnaire"
                              required>
              </b-form-select>
              <button class="btnInlineForm" @click.prevent v-b-modal.modal-gestionnaire v-if="utilisateurDroits.indexOf('HYDRANTS_GESTIONNAIRE_C') != -1"><img src="../assets/img/pencil.png"></button>
            </b-form-group>
          </div>

          <div class="col-md-4">
            <b-form-group label="Site : " label-for="site">
              <b-form-select id="site" v-model="hydrant.site" class="parametre" :options="comboSite" size="sm"></b-form-select>
            </b-form-group>
          </div>
        </div>

      </div>

      <div>
        <b-tabs fill content-class="mt-3" active-nav-item-class="text-primary">
          <b-tab title="Résumé" active><p>TODO : résumé</p></b-tab>

          <!-- ================================== Onglet Localisation ==================================-->
          <b-tab title="Localisation" active>
            <FicheLocalisation  :hydrant="hydrant"
                                :utilisateurDroits="utilisateurDroits" 
                                :geometrie="geometrie"
                                v-if="dataLoaded"
                                @getComboData="getComboData"
                                @resolveForeignKey="resolveForeignKey"
                                ref="ficheLocalisation">
            </FicheLocalisation>
          </b-tab>

          <b-tab title="Caractéristiques techniques">
            
          </b-tab>
          <b-tab title="Visites"><p>Form visites</p></b-tab>
          <b-tab title="Documents"><p>Form documents</p></b-tab>
        </b-tabs>
      </div>

    </form>

    <ModalGestionnaire v-on:modalGestionnaireValues="onGestionnaireCreated"></ModalGestionnaire>

  </div>
</template>

<script>

import axios from 'axios'
import _ from 'lodash'
import ModalGestionnaire from './ModalGestionnaire.vue'
import FicheLocalisation from './FicheLocalisation.vue'


export default {
  name: 'Fiche',

  components: {
    ModalGestionnaire,
    FicheLocalisation,
  },

  data() {
    return {
      hydrantRecord: {}, // Données initiales du PEI
      hydrant: {}, // Données actuelles du PEI 
      utilisateurDroits: [],
      dataLoaded: false,
      

      //ComboBox
      comboType: [],
      comboDeci: [],
      comboGestionnaire: [],
      comboSite: [],
      comboAutoriteDeci: [],

      listeNaturesDeci: [],

      // Etat des champs du formulaire pour la validation
      etats: {
        numeroInterne: null,
        gestionnaire: null,
        nature: null,
        autoriteDeci: null,
        natureDeci: null
      }
    }
  },
  
  props: {
    idHydrant: {
      type: Object,
      required: false
    },

    codeHydrant: {
      type: Object,
      required: true
    },

    geometrie: {
      type: Object,
      required: true
    }
  },

  mounted: function(){

    var self = this;
    // Récupération des droits de l'utilisateur courant
    axios.get('/remocra/utilisateurs/current/xml').then(response => {
      var xmlDoc = (new DOMParser()).parseFromString(response.data,"text/xml");
      self.utilisateurDroits = [];
      _.forEach(xmlDoc.getElementsByTagName("right"), function(item){
          self.utilisateurDroits.push(item.getAttribute("code"));
      });
    });

    if(!this.idHydrant) { // Création
      this.hydrantRecord = {
        id: null,
        nature: null,
        gestionnaire: null,
        site: null,
        autoriteDeci: null,
        natureDeci: null,
        code: this.codeHydrant
      }

      this.hydrant = _.clone(this.hydrantRecord);
      this.dataLoaded = true;
      this.createCombo();

    } else { //Modification
      axios.get('/remocra/hydrants'+this.codeHydrant.toLowerCase()+'/'+this.idHydrant+'.json?&id='+this.idHydrant).then(response => {
        if (response.data.data && response.data.data.length !== 0) {

          this.hydrantRecord = response.data.data;

          //Résolution des clés étrangères
          this.hydrant = _.clone(this.hydrantRecord, true);
          this.dataLoaded = true;
          this.resolveForeignKey(['nature', 'site', 'autoriteDeci', 'natureDeci']);

          this.createCombo();
          
        }
      }).catch(function(error) {
        console.error('Retrieving data ', error)  
      })
    }
  },

  methods: {

    // Résolution des clés étrangères
    resolveForeignKey(clesEtrangeres){
      _.forEach(clesEtrangeres, function(item){
        this.hydrant[item] = (this.hydrantRecord[item]) ? _.clone(this.hydrantRecord[item].id) : null;
      }.bind(this));
    },

    // Fonction récupérant les données nécessaire aux combos du formulaire
    createCombo() {

      this.getComboData(this, 'comboType', '/remocra/typehydrantnatures.json?filter=[{"property":"typeHydrantCode","value":"'+this.codeHydrant+'"}]', 'id', 'nom');
      this.getComboData(this, 'comboDeci', '/remocra/typehydrantnaturedeci.json', 'id', 'nom');

      //Combo de l'autorité DECI: on modifie le texte affiché en fonction du type d'organisme
      var self = this;
      axios.get('/remocra/organismes/autoritepolicedeci.json?geometrie='+this.geometrie).then(response => {
        _.forEach(response.data, function(item) {
          var libelle;
          switch(item['typeOrganisme']){

            case 'COMMUNE' :
              libelle = "Maire de "+item['nom'];
              break;

            case 'EPCI' : 
              libelle = "Président de "+item['nom'];
              break;

            case 'PREFECTURE' :
              libelle = "Préfet";
              break;

            default:
              libelle = item['nom'];
          }
          self['comboAutoriteDeci'].push({
            text: libelle,
            value: item['id']
          });
        });
      }).then(axios.get('/remocra/typehydrantnaturedeci').then(response => { // Récupération des données de nature DECI
          self.listeNaturesDeci = response.data.data;
          self.getComboGestionnaire();
      })).catch(function(error) {
        console.error('Retrieving combo data from /remocra/organismes/autoritepolicedeci/', error);
      })
    },


    /**
      * Récupère les données des combobox
      * @param attribut L'attribut du composant qui contiendra les données (on ne peut pas utiliser async/await par souci de compatibilité, et on évite l'enchaînement de callback)
      * @param url L'url remocra permettant de récupérer les données
      * @param champValeur Indique parmis les données récupérées le champ de valeur 
      * @param champTexte Indique parmis les données récupérées le champ texte à afficher dans la combo
      * @param optionVide Si indiqué, ajoute en première position une option vide valant null avec un texte configurable
      */
    getComboData(context, attribut, url, champValeur, champTexte, optionVide){
      context[attribut] = [];
      axios.get(url+'&sort=[{"property":"'+champTexte+'","direction":"ASC"}]').then(response => {
        _.forEach(response.data.data, function(item) {
          context[attribut].push({
            text: item[champTexte],
            value: item[champValeur]
          });
        });

        if(optionVide){
          context[attribut].unshift({
            text: optionVide,
            value: null
          });
        }
      }).catch(function(error) {
        console.error('Retrieving combo data from '+url, error);
      })
    },

    getComboGestionnaire(){
      var idDeciPrive = this.listeNaturesDeci.filter(item => item.code === "PRIVE")[0].id;
      this.hydrant.site = null;

      // En cas de changement de nature (passage de PUBLIC/CONVENTIONNE à PRIVE et inversement), on set le gestionnaire à null
      // De cette manière, si on passe de PUBLIC à CONVENTIONNE ou inversement, on laisse tel quel
      if(this.hydrantRecord.natureDeci && (this.hydrant.natureDeci == idDeciPrive) != (this.hydrantRecord.natureDeci.id == idDeciPrive)){
        this.hydrant.gestionnaire = null;
      }
      
      //Si DECI privé, le gestionnaire est un privé
      if(this.hydrant.natureDeci == idDeciPrive){
        this.getComboData(this, 'comboGestionnaire', '/remocra/gestionnaire.json', 'id', 'code', 'Aucun');
        this.onGestionnaireChange();


      } else { // Si DECI publique ou conventionnée, le gestionnaire est un organisme de type COMMUNE ou EPCI
        var self = this;
        axios.get('/remocra/organismes/gestionnairepublic.json?geometrie='+this.geometrie+'&sort=[{"property":"nom","direction":"ASC"}]').then(response => {
          self.comboGestionnaire = [];
          _.forEach(response.data, function(nature){
            self.comboGestionnaire.push({
              text: nature.nom,
              value: nature.id
            })
          });
        }).then(this.onGestionnaireChange()).catch(function(error) {
          console.error('Retrieving combo data from /remocra/organismes/gestionnairepublic/'+this.geometrie, error);
        });
      }
    },

    /**
      * Appelée lorsque l'on créé un gestionnaire grâce à la modale
      * @param values Les données du gestionnaire créé (transmises par le composant ModalGestionnaire)
      */
    onGestionnaireCreated(values){
      this.comboGestionnaire.push({
            text: values.nom,
            value: values.id
      });
    },

    /**
      * En cas de changement de gestionnaire, on met à jour la liste des sites
      */
    onGestionnaireChange(){
      if(this.hydrant.gestionnaire != null){
        this.getComboData(this, 'comboSite', '/remocra/site.json?filter=[{"property":"gestionnaire","value":"'+this.hydrant.gestionnaire+'"}]&sort=[{"property":"nom","direction":"ASC"}]', 'id', 'nom', 'Aucun');
      }
      
      if(this.hydrantRecord.gestionnaire != this.hydrant.gestionnaire){
        this.hydrant.site = null;
      }
      else{
        this.hydrant.site = (this.hydrantRecord.site) ? this.hydrantRecord.site.id : null;
      }
    },

    

    checkFormValidity(){
      this.etats.numeroInterne = !this.idHydrant || (this.idHydrant && this.hydrant.numeroInterne.toString().length > 0) ? 'valid' : 'invalid';
      this.etats.gestionnaire = (this.hydrant.gestionnaire !== null) ? 'valid' : 'invalid';
      this.etats.nature = this.hydrant.nature? 'valid' : 'invalid';
      this.etats.autoriteDeci = (this.hydrant.autoriteDeci !== null) ? 'valid' : 'invalid';
      this.etats.natureDeci = (this.hydrant.natureDeci !== null) ? 'valid' : 'invalid';
      this.$refs.ficheLocalisation.checkFormValidity();
      return this.$refs.formFiche.checkValidity();
    },
 
    /**
      * Envoie les données au serveur afin de procéder à la mise à jour de l'hydrant
      * @param url L'url à contacter
      */
    handleSubmit(url){

      // Si la nature est passée de PRIVE a PUBLIC/CONVENTIONNE ou inversement, le PEI ne respecte plus la contrainte de nature DECI au sein de ses tournées
      // On le désaffecte donc pour tous les organismes
      var idDeciPrive = this.listeNaturesDeci.filter(item => item.code === "PRIVE")[0].id;
      if(this.hydrantRecord.natureDeci &&(this.hydrant.natureDeci == idDeciPrive) != (this.hydrantRecord.natureDeci.id == idDeciPrive)){
        axios.post('hydrants/desaffecter?json=['+this.hydrant.id+']&allOrganismes=true')
        .catch(function(error) {
          console.error('postEvent', error)
        });
      }

      // Récupération des données
      var data = {};
      _.forEach(document.getElementsByClassName('parametre'), item => {
        if(item.type === "number"){
          data[item.id] = (item.value === "") ? null : parseInt(item.value);
        } else {
          data[item.id] = item.value;
        }
      });
      data["geometrie"] = this.geometrie;
      if(!this.idHydrant){
        data["numeroInterne"] = null;
        data["commune"] = 296; // Temporaire, à supprimer après implémentation de l'onglet Localisation (une commune est nécessaire pour pouvoir calculer le numéro interne)
      }

      var formData = new FormData();

      axios.post('/remocra/hydrants/getUpdatedCoordonnees', this.$refs.ficheLocalisation.getLocalisationData()).then(function(response) { // Une fois la maj faite, on met à jour le reste des données
        data["geometrie"] = response.data.message;
        formData.append("hydrant", JSON.stringify(data));

        axios.post(url, formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }).catch(function(error) {
            console.error('postEvent', error)
          })
      }).catch(function (error) {
        console.log(error);
      });
    }

  }
};
</script>

<style>
.Fiche{
  margin-top: 27px;
  min-height: 250px;
  padding: 10px;
}

.btnInlineForm{
  border-radius: 5px;
  min-height: 28px;
}

.tabOnglet{
  background-color: red;
  color: blue;
}

#formFiche .invalid-feedback{
  font-size: 100%;
}

.title{
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
  margin-top: 1rem;
  color: #7b7b7b;
}

fieldset, .form-group {
  margin-bottom: 5px;
}

label {
  margin-bottom: 2px;
}


</style>