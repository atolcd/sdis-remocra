<template>
  <div id="formGestionnaire"> <!-- Définition page de la pop up de formulaire de gestionnaire -->
    <b-modal name="modalFormGestionnaire" id="formGestionnaire" v-bind:title="buildedTitle" no-close-on-backdrop cancel-title="Annuler" 
      ok-title="Valider" refs="modalFormGestionnaire" @ok="handleOk" :clickToClose="false">
        <form id='formGestionnaireId' ref='formGestionnaire'>
          <div class="col-md-6" id="divInputNom">
            <b-form-group id="formInputNom" label-for="inputNom" invalid-feedback="Le nom du gestionnaire est manquant">
              Nom : *
              <b-form-input id="inputNom" v-model="nomGestionnaire" required></b-form-input>
            </b-form-group>
          </div>
          <div class="col-md-6" id="divInputCode">
            <b-form-group id="formInputCode" label-for="inputCode" invalid-feedback="Le code est invalide (champ vide OU 9 chiffres)">
              N°SIREN : *
              <b-form-input id="inputCode" v-model="codeGestionnaire" pattern="[0-9]{9}"></b-form-input>
            </b-form-group>
          </div>
          <div class="row msgErrorDiv">
            <span id="msgError" style="display:none">Ce N°SIREN est déja assigné à un gestionnaire</span>
          </div>
        </form>
    </b-modal>
  </div>
</template>

<script>
  import axios from 'axios'
  
  export default {
    data(){
      return{
        contacts: [],
        codesGestionnaire: [],
        appartenance: '',
        nomGestionnaire:'',
        codeGestionnaire:'',
        versionGestionnaire:0,
        idGestionnaire:'',
        buildedTitle:'',
      }
    },

    methods: {
      showModalFormGestionnaire(idGestionnaire_) {
        this.getGestionnaireCode()
        if(idGestionnaire_!==null){ // Modification du gestionnaire idGestionnaire_
          this.idGestionnaire=idGestionnaire_;
          axios.get('/remocra/gestionnaire/' + idGestionnaire_).then(response => {
            response.data.data ? (this.nomGestionnaire = response.data.data.nom, 
                                  this.codeGestionnaire = response.data.data.code,
                                  this.versionGestionnaire = response.data.data.version) : null
            this.buildedTitle='Modification de '+this.nomGestionnaire;
          }).catch(function(error){
            console.error('erreur recup info gestionnaire', error);
          })
          this.$bvModal.show('formGestionnaire')
        }
        else{ // Création d'un gestionnaire
          this.resetFormGestionnaire();
          this.buildedTitle="Création d'un gestionnaire";
          this.$bvModal.show('formGestionnaire')
        }
        
      },
      getGestionnaireCode(){
        axios.get('/remocra/gestionnaire/listeGestionnaireCodes').then(response => {
          if(response.data) {
            this.codesGestionnaire = response.data.data;
          }
        }).catch(function(error){
          console.error('erreur recup codes gestionnaire', error);
        })
      },
      handleOk(bvModalEvt) {
        bvModalEvt.preventDefault()
        if (document.getElementById('formGestionnaireId').checkValidity() === false) {
          document.getElementById('formGestionnaireId').classList.add('was-validated');
        }
        else {
          this.handleSubmit()
        }
      },
      handleSubmit() {
        if (!this.checkFormValidity()){
          return;
        }
        var formData = new FormData();
        formData.append('gestionnaire', JSON.stringify({
          nom: this.nomGestionnaire,
          code: this.codeGestionnaire == '' ? null: this.codeGestionnaire,
          actif: true,
          version: this.versionGestionnaire == 0 ? 1 : this.versionGestionnaire+1,
        }))
        var url = this.idGestionnaire !== null ? '/remocra/gestionnaire/updateGestionnaire/' + this.idGestionnaire : '/remocra/gestionnaire/createGestionnaire';
        axios.post(url, formData).then(() => {
          this.$emit('gestionnaireUpdate'),  // confirme au parent qu'il y a eu un changement en base
          this.$nextTick(() =>{
            this.resetFormGestionnaire();
            this.$bvModal.hide('formGestionnaire')
          })
        }).catch(function(error) {
          console.error('erreur upsert gestionnaire', error);
        });
      },
      resetFormGestionnaire(){
        this.$refs.formGestionnaire.reset();
        this.idGestionnaire= null;
        this.nomGestionnaire='';
        this.codeGestionnaire='';
        this.versionGestionnaire=0;
        this.contacts=[];
      },
      checkFormValidity() { 
        if (this.$refs.formGestionnaire.checkValidity() == false ||         // Si formulaire invalide OU
            ( this.codeGestionnaire != '' && this.codesGestionnaire.includes(this.codeGestionnaire))) // SIREN pas vide ET deja renseigné
            {
              var msgErrorElement = document.getElementById("msgError");
              msgErrorElement.style="display:bloc";
              setTimeout(function(){
                msgErrorElement.style="display:none";
              },5000) // Affichage du message d'erreur pendant 5 sec
              return false; // Le formulaire n'est pas valide
        }
        else {return true;}
      },
    },
  }
</script>

<style>
  div#formGestionnaire___BV_modal_content_{width:unset;min-width:unset !important;}
  #formGestionnaire .modal-dialog.modal-md {min-width: 400px;max-width:60%;}
  #divInputCode, #divInputNom{width:100%;}
  #formGestionnaire .invalid-feedback {width:150%;}
  .msgErrorDiv{justify-content: center;}
</style>