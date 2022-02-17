<template>
  <div id="Courrier">
    <!-- ================================================ Paramètres du document ========================================================= -->
    <modal id="modalCourrier" name="modalCourrier" @closed="close()"
   :reset="true"
   :draggable="true"
    width="50%"
    height="auto">
       <header class="modal-header"><h5 class="modal-title">Génération de courrier</h5>
    <div slot="top-right">
     <button type="button" aria-label="Close" @click="$modal.hide('modalCourrier')" class="close">×</button>
    </div></header>
    <form id='formCourrier' name='courrier' enctype="multipart/form-data" method="POST" ref="formCourrier">
      <b-form-group>
        <div class="row">
          <div class="col-md-3 mt-1">
            <label>Modèle</label>
          </div>
          <div class="col-md-9 mb-2">
            <b-form-select id="modele" size="sm" v-model="choixModele" :options="comboModele" @change="getParams"></b-form-select>
            <label class="description">{{this.choixModele.description}}</label>
          </div>
        </div>

        <div class="row">
          <div class="col-md-3 mt-1">
            <label>Objet</label>
          </div>
          <div class="col-md-9 mb-2">
            <b-form-input size="sm" type='text' v-model="objet" required></b-form-input>
          </div>
        </div>

        <div class="row">
          <div class="col-md-3 mt-1">
            <label>Référence</label>
          </div>
          <div class="col-md-9 mb-2">
            <b-form-input size="sm" type='text' v-model="reference" required></b-form-input>
          </div>
        </div>
      </b-form-group>
      <p v-if="params.length > 0">Veuillez renseigner les paramètres suivants </p>
      <p v-else-if="choixModele">Aucun paramètre pour cette requête</p>
      <!--Début boucle for-->
      <div v-for="(param, index) in params" :key="index">
        <b-form-group v-if='param.formulaireTypeControle=="autocomplete"' :id="param.nom" inputType='autocomplete' :required="param.obligatoire" class="parametreModele" horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
          <search-process-param :ref="'searchinput'+param.id" :paramId="param.id"></search-process-param>
        </b-form-group>
        <b-form-group v-if='param.formulaireTypeControle=="combo"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id" :required='param.obligatoire'>
          <AutoCompleteComponent :id="param.id" :ref="'autocomplete'+param.id" :options="getOption(param.id)" class="parametreModele" inputType='combo'></AutoCompleteComponent>
        </b-form-group>
        <b-form-group v-if='param.formulaireTypeControle=="checkbox"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
          <input type='checkbox' style="width:5%" :id="param.nom" :checked="param.formulaireValeurDefaut" inputType='checkbox' class="form-control parametreModele" />
        </b-form-group>
        <b-form-group v-if='param.formulaireTypeControle=="textfield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
          <b-form-input size="sm" type='text' :id="param.nom" :value='param.formulaireValeurDefaut' :required='param.obligatoire' class="form-control parametreModele" />
        </b-form-group>
        <b-form-group v-if='param.formulaireTypeControle=="numberfield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
          <b-form-input size="sm" type="number" :id="param.nom" :value='param.formulaireValeurDefaut' :required='param.obligatoire' :step='(param.typeValeur=="integer")?1:0.001' class="form-control parametreModele" />
        </b-form-group>
        <b-form-group v-if='param.formulaireTypeControle=="datefield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
          <b-form-input size="sm" type='date' :id="param.nom" :value='param.formulaireValeurDefaut' :required='param.obligatoire' class="form-control parametreModele" />
        </b-form-group>
        <b-form-group v-if='param.formulaireTypeControle=="timefield"' horizontal :label='param.formulaireEtiquette' :label-for="'input'+param.id">
          <b-form-input size="sm" type='time' :id="param.nom" :value='param.formulaireValeurDefaut' :required='param.obligatoire' step='1' class="form-control parametreModele" />
        </b-form-group>
        <b-form-group v-if='param.formulaireTypeControle=="datetimefield"' :id="'input'+param.id" horizontal :label='param.formulaireEtiquette' inputType='datetimefield' class='parametreModele' :label-for="'input'+param.id">
          <b-form-input size="sm" type='date' :id="param.nom" :value='param.formulaireValeurDefaut && param.formulaireValeurDefaut.split(" ")[0]' :required='param.obligatoire' class='form-control' />
          <b-form-input size="sm" type='time' :id="param.nom" :value='param.formulaireValeurDefaut && param.formulaireValeurDefaut.split(" ")[1]' :required='param.obligatoire' step='1' class='form-control' />
        </b-form-group>
      </div>
      <div class="text-center mt-3">
          <b-spinner v-if='showApercu == true && pdfLoading == true' ></b-spinner>
      </div>
      <div class="row">
        <div class="col-md-12 ">
          <b-alert id="alertError" v-model="showErrorGeneration" size="sm" variant="danger" dismissible>
            {{this.msgError}}
          </b-alert>
        </div>
      </div>
    </form>
    <div class="modal-footer">
        <b-button size="sm" type="reset" variant="secondary" @click="$modal.hide('modalCourrier')">Annuler</b-button>
        <b-button size="sm" type="submit" variant="primary" @click="handleOk" >Aperçu</b-button>
      </div>
    </modal>

      <!-- ================================================ Aperçu du document ========================================================= -->

    <b-modal id="modalApercu" class="text-center" ref="modalApercu" no-close-on-backdrop title="Aperçu du courrier" hide-footer>
    <div class="pdf" :key="refreshPdf">
      <object type="application/pdf" :data="this.urlCourrier"
          width="100%" height="100%"></object>
    </div>
    <b-form-group>
      <div class="row justify-content-md-center mt-2" id="btnApercu">
        <div class="col-md-3 text-center">
          <b-button id="bouton" @click="setModalVisibility('modalApercu', false);" class="modifier">Modifier</b-button>
        </div>
        <div class="col-md-3 text-center">
          <b-button id="bouton" @click="telechargerCourrier" variant="primary">Télécharger</b-button>
        </div>
        <div class="col-md-3 text-center">
          <b-button id="bouton" @click="setModalVisibility('modalNotifier', true);" class="notifier" variant="primary">Notifier</b-button>
        </div>
        <div class="col-md-3 text-center">
          <b-button id="bouton" @click="setModalVisibility('modalApercu', false); setModalVisibility('modalCourrier', false);" >Fermer</b-button>
        </div>
      </div>
    </b-form-group>
    </b-modal>

      <!-- ================================================ Envoi du document ========================================================= -->

  <b-modal id="modalNotifier" ref="modalNotifier" no-close-on-backdrop title="Notification par mail" hide-footer>
    <div class="row">
      <div class="col-md-3 mt-1">
        <label>Recherche rapide</label>
      </div>
      <div class="col-md-5">
        <b-form-input v-model="filtre" size="sm" @input="initListeDestinataire" id="filtre" placeholder="Recherche ..." >
        </b-form-input>
      </div>
        <div class="col-md-4 mt-1 labelFiltreZC">
        <b-form-checkbox id="chkBoxZoneComp" v-model="filtreZC" @input="initListeDestinataire"  name="chkBoxZoneComp">Restreindre à ma zone de compétence</b-form-checkbox>
      </div>
    </div>
    <div class="row mt-2">
      <div class="col-md-4">
        <label>Afficher les destinataires de type</label>
      </div>
      <div class="col-md-2">
        <b-form-checkbox id="chkBoxOrganisme" v-model="filtreOrga" @input="initListeDestinataire"  name="chkBoxOrganisme">Organisme</b-form-checkbox>
      </div>
      <div class="col-md-2">
        <b-form-checkbox id="chkBoxUtilisateur" v-model="filtreUtil" @input="initListeDestinataire" name="chkBoxUtilisateur">Utilisateur</b-form-checkbox>
      </div>
      <div class="col-md-2">
        <b-form-checkbox id="chkBoxContact" v-model="filtreContact"  @input="initListeDestinataire" name="chkBoxContact">Contact</b-form-checkbox>
      </div>
    </div>
    <div class="row ">
      <div class="col-md-6 mt-2">
        <label>Destinataires potentiels </label>
      </div>
    </div>
    <div class="row">
      <div class="col-md-12 tabNotifNon">
        <b-table  id="tabDestinataireNotifier" small selectable select-mode="multi" @row-selected="onRowSelectedAjoute" :fields="fields"
          :items="tabDestinataireNotifierNon" :per-page="perPage" :current-page="currentPageNon">
        </b-table>
      </div>
    </div>
    <div class="row mt-1">
      <div class="col-md-6 text-right">
        <b-button id="boutonUpDown" @click="addNotifierOui" variant="primary"><img id="arrow" src="/remocra/static/img/navigate-down-arrow.png"></b-button>
      </div>
      <div class="col-md-2">
        <b-button id="boutonUpDown" @click="addNotifierNon" variant="primary"> <img id="arrow" src="/remocra/static/img/navigate-up-arrow.png"> </b-button>
      </div>
      <div class="col-md-4 overflow-auto">
          <b-pagination v-model="currentPageNon" :total-rows="rowsNon" :per-page="perPage" size="sm" align="right" aria-controls="tabDestinataireNotifierNon">
          </b-pagination>
        </div>
    </div>
    <div class="row">
      <div class="col-md-12">
        <label>Destinataires définitifs </label>
      </div>
    </div>
    <div class="row">
      <div class="col-md-12 tabNotifOui">
        <b-table id="tabDestinataireNotifier" responsive small selectable select-mode="multi" @row-selected="onRowSelectedSupprime" :fields="fields"
          :items="tabDestinataireNotifierOui" :per-page="perPage" :current-page="currentPageOui">
        </b-table>
      </div>
    </div>
    <div class="row">
    </div>
    <b-form-group>
      <div class="row mt-2">
        <div class="col-md-4 text-right">
          <b-button id="bouton" @click="setModalVisibility('modalNotifier', false);" class="retourApercu">Retour</b-button>
        </div>
        <div class="col-md-4 text-center">
          <b-button id="bouton" @click="notificationCourrier" class="notifier" variant="primary">Notifier</b-button>
        </div>
        <div class="col-md-4">
          <b-button id="bouton" @click="setModalVisibility('modalNotifier', false); setModalVisibility('modalApercu', false); setModalVisibility('modalModifier', false)" >Fermer</b-button>
        </div>
      </div>
    </b-form-group>
  </b-modal>

        <!-- ==================================== Pop up notifier ============================================ -->

  <b-modal id="modalPopupNotif" ref="modalPopupNotif" no-close-on-backdrop :header-bg-variant="styleHeaderNotif"
    :title="titleNotif" centered ok-only ok-title="Fermer" @ok="setModalVisibility('modalNotifier', false); setModalVisibility('modalApercu', false); setModalVisibility('modalModifier', false)" >
      <b-form-group>
        <div class="row">
          <div class="col-md-12 text-center">
            <label>{{this.retourNotification}}</label>
          </div>
        </div>
      </b-form-group>
    </b-modal>
  </div>
</template>

       <!-- ======================================= Script ================================================= -->

<script>
  import axios from 'axios'
  import _ from 'lodash'
  import AutoCompleteComponent from './Autocomplete/AutoCompleteComponent.vue'

export default {
  components: {
    AutoCompleteComponent
  },
  name: 'Courrier',
  data() {
    return {

      codeCourrier : "",
      /************* Partie paramètres document ****************/
      comboModele: [],
      choixModele: "",
      comboOptions: [],
      choixCommune: "",
      comboAdresseA: [],
      comboSite: [],
      params: [],
      files: [],
      showErrorGeneration: false,
      msgError: "",
      objet: "",
      reference: "",

      /************** Partie aperçu document *******************/

      refreshPdf: 0,
      showApercu: false,
      urlCourrier: "",
      nomCourrier: "",
      pdfLoading: false,

      /************** Partie notification ***********************/
      filtre: null,
      filtreZC: true,
      filtreOrga: true,
      filtreUtil: true,
      filtreContact: true,
      selectAll: false,
      destinatairesLoaded: false,
      ajouteDestinataire: [],
      retireDestinataire: [],
      tabDestinataires:[],
      tabDestinataireNotifierNon: [],
      tabDestinataireNotifierOui: [],
      fields: ['Type', 'Nom', 'Email', 'Fonction' ],
      perPage: 15,
      currentPageNon: 1,
      currentPageOui: 1,
      retourNotification: "",
      titleNotif: "",
      styleHeaderNotif: ""
    }
  },

  props:{
    thematique: {
      type: String,
      required: true
    },
  },

  computed: {
    rowsNon() {
        return this.tabDestinataireNotifierNon.length
    },
  },

  mounted: function(){
    this.$modal.show('modalCourrier');
    this.initComboModele();
  },

  methods: {

    //rafraichissment de la balise pdf
    refreshPdfFunc(){
      this.refreshPdf += 1;
    },

    selectAllMethod(selectAll){
      if(selectAll == true){
        this.tabDestinataires = this.tabDestinataireNotifierNon;
      } else {
        this.tabDestinataires =[];
      }
    },

    setModalVisibility(modalName, visibility){
      if(visibility == true){
        if(modalName == "modalNotifier"){
          if(!this.destinatairesLoaded){
            this.initListeDestinataire();
          }
        }
        this.$refs[modalName].show();
        if(modalName == "modalApercu"){
          this.refreshPdfFunc();
        }
      } else {
        if(modalName == "modalModifier"){
          var element = document.getElementById("Courrier");
          element.parentNode.removeChild(element);
        } else if (modalName == "modalCourrier"){
           this.$modal.hide('modalCourrier');
        }else {
          this.$refs[modalName].hide();
        }
      }
    },

    onRowSelectedAjoute(items){
      this.ajouteDestinataire = items;
    },

    onRowSelectedSupprime(items){
      this.retireDestinataire = items;
    },

    addNotifierOui(){
      _.forEach(this.ajouteDestinataire, dest =>{
        this.tabDestinataireNotifierOui.push(dest);
        this.tabDestinataireNotifierNon.splice(this.tabDestinataireNotifierNon.indexOf(dest),1);
      })
      this.ajouteDestinataire = [];
    },

    addNotifierNon(){
      _.forEach(this.retireDestinataire, dest =>{
        this.tabDestinataireNotifierNon.push(dest);
        this.tabDestinataireNotifierOui.splice(this.tabDestinataireNotifierOui.indexOf(dest),1);
      })
      this.retireDestinataire = [];
    },

    initComboModele(){
      axios.get('/remocra/courrier/with/'+this.thematique).then((response)=> {
          var courriers = response.data.data;
          _.forEach(courriers, courrier => {
            this.comboModele.push({
            'value': {
              'id': courrier.id,
              'description': courrier.description,
              'libelle': courrier.libelle,
              'code': courrier.code
            },
            'text': courrier.libelle
          })
          });

        }).catch(function(error) {
              console.error(error)
          });
    },

    //Récupère les paramètres du modèle de courrier selectionné
    getParams() {
      this.comboOptions = [];
      this.params = [];
      if (this.choixModele !== null) {
        axios.get('/remocra/courrier/courrierParams/' + this.choixModele.id).then((response) => {

          // Si l'utilisateur n'a pas saisi d'objet, on alimente le champ grâce aux données du modèle
          if(!this.objet.length) {
            this.objet = this.choixModele.libelle;
          }

          this.params = _.sortBy(response.data.data, item => item.formulaireNumOrdre)
          _.forEach(this.params, param => {
            if (param.formulaireTypeControle === 'combo') {
              axios.get('/remocra/courrier/courriermodparalst/' + param.id).then((response) => {
                _.forEach(response.data.data, option => {
                  var o = {
                    idChamp: param.id,
                    valeur: option[param.sourceSqlValeur],
                    libelle: option[param.sourceSqlLibelle],
                    obligatoire: param.obligatoire,
                    formulaireValeurDefaut: param.formulaireValeurDefaut
                  }
                  this.comboOptions.push(o)
                })
              }).catch(function(error) {
                console.error('Combo', error)
              })
            }
          })
        })
      }
    },

    getOption: function (id) {
      return this.comboOptions.filter(function (value) {
        return value.idChamp === id
      })
    },

    telechargerCourrier() {
      axios({
        method: 'GET',
        url: this.urlCourrier,
        responseType: 'blob'
        }).then((response) => {
          var fileUrl = window.URL.createObjectURL(new Blob([response.data]));
          var fileLink = document.createElement('a');

          fileLink.href = fileUrl;
          fileLink.setAttribute('download', this.nomCourrier);
          document.body.appendChild(fileLink);

          fileLink.click();
          })
          .catch(function(error) {
            console.error('Erreur lors du téléchargement du courrier : ', error)
      })
    },

    handleOk(evt){
      evt.preventDefault();
      if(!this.choixModele == ""){
        if (document.getElementById('formCourrier').checkValidity() === false) {
          this.$notify({
            group: 'remocra',
            title: 'Génération de courrier',
            type: 'warn',
            text: 'Veuillez saisir les champs obligatoires'
          })
          document.getElementById('formCourrier').classList.add('was-validated')
        } else {
            let formData = new FormData()
            formData.append("modele", this.choixModele.id)
            _.forEach(document.getElementsByClassName('parametreModele'), item => {
            if (item.getAttribute('inputType') === 'datetimefield') {
              var identifiant = item.querySelector('input').id;
              var date = item.querySelector('input[type=date]').value;
              var time = item.querySelector('input[type=time]').value;
              formData.append(identifiant, date + ' ' + time);
            } else if (item.getAttribute('inputType') === 'autocomplete') {
              var autocomplete = this.$refs['search' + item.id][0]
              formData.append(item.getAttribute('id'), autocomplete.selected !== null ? autocomplete.selected.id : autocomplete.searchInput)
            } else if (item.getAttribute('inputType') === 'filefield') {
              var rawValueParts = item.value.split('\\')
              var value = rawValueParts[rawValueParts.length - 1]
              formData.append(item.getAttribute('id'), value)
            } else if (item.getAttribute('inputType') === 'checkbox') {
              formData.append(item.getAttribute('id'), item.checked)
            } else if (item.getAttribute('inputType') === 'combo') {
              var combo = this.$refs['autocomplete' + item.getAttribute('id')][0];
              formData.append(item.getAttribute('id'), combo.selected !== null ? combo.selected.valeur : null);
            } else {
              formData.append(item.getAttribute('id'), item.value)
            }
          })
          for (var i = 0; i < this.files.length; i++) {
            var file = this.files[i]
            formData.append(file.id, file.file)
          }
          //On ajoute Systématiquement la reference
          formData.append("REFERENCE", this.reference)
          this.handleSubmitParams(formData)
        }
      }
    },

    handleSubmitParams(formData){
      this.showApercu = true;
      this.pdfLoading = true;
      var self = this;
      // Envoi des données
      axios.post('/remocra/courrier/generecourrier/' + this.choixModele.id, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then((response) => {
        if (response.data.success) {
          this.urlCourrier = "/remocra/ext-courrier/courrier_temp/"+response.data.message;
          this.nomCourrier = response.data.message.split("/")[1];
          this.codeCourrier = response.data.message.split("/")[0];
          this.setModalVisibility('modalApercu', true);
          this.pdfLoading=false;
        }
      }).catch(function(error) {
        console.error(error)
        self.pdfLoading = false;
        self.showErrorGeneration = true;
        self.msgError = "Erreur lors de la génération du courrier.";
      })
    },

    initListeDestinataire() {
      var types = [];
      if(this.filtreOrga) { types.push("ORGANISME"); }
      if(this.filtreUtil) { types.push("UTILISATEUR"); }
      if(this.filtreContact) { types.push("CONTACT"); }

      axios.get('/remocra/courrier/contacts',{
        params: {
          useZc: this.filtreZC,
          listeTypes: JSON.stringify(types),
          filter: JSON.stringify(
            [{
              "property": "filtreString",
              "value": this.filtre
            }]
          )
        }
      }).then((response)=> {
        this.tabDestinataireNotifierNon = [];
        this.tabDestinataires = [];
        var contacts = response.data.data;
        _.forEach(contacts, contact => {
          this.tabDestinataires.push({
            'id': contact.id,
            'Type': contact.type,
            'Nom' : contact.nom,
            'Email' : contact.email,
            'Fonction' : contact.fonction
          });
        });
      }).then(()=>{
        var i =0;
        for(i = 0; i<this.tabDestinataires.length; i++){
          this.tabDestinataireNotifierNon.push(this.tabDestinataires[i]);
        }
        this.destinatairesLoaded = true;
      }).catch(function(error) {
        console.error(error)
      });
    },

    /**
     * codeCourrier
     * tabNotifierOui
     */
    notificationCourrier(){
      if(this.tabDestinataireNotifierOui.length != 0){
        var datas = {
          "codeCourrier": this.codeCourrier,
          "nomCourrier": this.nomCourrier,
          "destinataires": this.tabDestinataireNotifierOui,
          "reference": this.reference,
          "codeModele": this.choixModele.code,
          "objet": this.objet
        }
        axios.post('/remocra/courrier/notifier', JSON.stringify(datas), {
          headers: {
            'Content-Type': 'application/json;charset=utf-8'
          }
        }).then((response) => {
                    this.titleNotif = "Succès de la notification";
                    this.retourNotification = response.data.message;
                    this.$refs.modalPopupNotif.show();
        }).catch((error) => {
                    this.titleNotif = "Echec de la notification"
                    this.retourNotification = error.response.data.message;
                    this.styleHeaderNotif = 'warning';
                    this.$refs.modalPopupNotif.show();
        })
      }
    },
    close(){
      this.$root.$options.bus.$emit('closed')
    }

  }
};
</script>

    <!-- ============================================ Style =============================================== -->

<style>

#Courrier .modal-header{
  padding: 0.3em 1em 0.4em 0.5em;
}

#Courrier .modal-footer{
  padding: 0.3em 0.5em 0.3em 0;
}

#Courrier .modal-title{
  font-size: 12pt;
}

#modalCourrier .modal-content{
  background-color: #e9e9e9;
  font-size: 11pt;
  width: 650px;
  padding-bottom: 0px;
  padding-top: 0px;
}

#modalApercu .modal-dialog{
  max-width: 50%;
}

#modalApercu .modal-content{
  background-color: #e9e9e9;
  font-size: 1rem;
  width: 100%;
  height: 100%
}

.pdf{
  align-content: center;
  height: 650px;
}

#modalApercu .modal-body{
  padding: 5px;
}

#modalNotifier .modal-dialog{
  max-width: 50%;
}

#modalNotifier .form-group{
  margin-bottom: 0;
}

#modalNotifier .modal-content{
  background-color: #e9e9e9;
  font-size: 1rem;
  width: 100%;
  padding-bottom: 0px;
  padding-top: 0px;
}

#modalPopupNotif .modal-content{
  background-color: #e9e9e9;
  font-size: 1rem;
  min-width: 650px;
  padding-bottom: 0px;
}

#modalPopupNotif .modal-body{
  padding-bottom: 0px;
}

.chargement{
  animation: opacity-anim 1s linear infinite;
}

#bouton{
  width: 115px;
}

#tabDestinataireNotifier{
  margin-bottom: 0;
  background-color: white;
}

#addNotifierNon{
  width: 38px;
}

#addNotifierOui{
  width: 38px;
}

.tabNotifNon{
  height: 100%;
  font-size: 10pt;
}

.tabNotifOui{
  height: 100%;
  font-size: 10pt;
}

.tabNotifOui .table-responsive{
  max-height: 200px;
}

.tabNotifNon .table-sm th, .tabNotifNon .table-sm td{
  padding: 0;
}

.tabNotifOui .table-sm th, .tabNotifOui .table-sm td{
  padding: 0;
}

.description{
  font-size: 15px;
}

.pagination{
    margin-bottom: 0;
}

#alertError {
  margin-bottom: 0;
  padding: .6rem;
}

</style>
