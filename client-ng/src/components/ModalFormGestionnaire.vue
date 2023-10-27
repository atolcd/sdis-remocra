<template>
  <div id="formGestionnaire"> <!-- Définition page de la pop up de formulaire de gestionnaire -->
    <b-modal name="modalFormGestionnaire" id="formGestionnaire" v-bind:title="buildedTitle" no-close-on-backdrop cancel-title="Annuler"
      ok-title="Valider" refs="modalFormGestionnaire" @cancel="resetFormGestionnaire" @ok="handleOk" :clickToClose="false">
        <form id='formGestionnaireId' ref='formGestionnaire'>
          <div class="col-md-6" id="divInputNom">
            <b-form-group id="formInputNom" label-for="inputNom" invalid-feedback="Le nom du gestionnaire est manquant">
              Nom : *
              <b-form-input id="inputNom" v-model="nomGestionnaire" required></b-form-input>
            </b-form-group>
          </div>
          <div class="col-md-6" id="divInputCode">
            <b-form-group id="formInputCode" label-for="inputCode" invalid-feedback="Le code est invalide (champ vide OU uniquement des chiffres)">
              N°SIREN/SIRET :
              <b-form-input id="inputCode" v-model="codeGestionnaire" pattern="[0-9]*"></b-form-input>
            </b-form-group>
          </div>
          <div class="row msgErrorDiv">
            <span id="msgError" style="display:none">Ce N°SIREN est déja assigné à un gestionnaire</span>
          </div>
          <div class="col-md-6" id="divCBoxActif">
            <b-form-group id="formCBoxActif" label-for="inputActif">
              Statut :
              <b-form-checkbox id="actif" v-model="actifGestionnaire">
                {{this.actifGestionnaire ? "Gestionnaire Actif" : "Gestionnaire Inactif"}}
              </b-form-checkbox>
            </b-form-group>
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
        actifGestionnaire: '',
        idGestionnaire:'',
        buildedTitle:'',
      }
    },
    methods: {
      showModalFormGestionnaire(idGestionnaire_) {
        this.getGestionnaireCode(idGestionnaire_)
        this.getGestionnaireById(idGestionnaire_)
      },
      getGestionnaireCode(idGestionnaire_){
        let url = idGestionnaire_ !== null ? '/remocra/gestionnaire/listeGestionnaireCodes/'+ idGestionnaire_ : '/remocra/gestionnaire/listeGestionnaireCodes';
        axios.get(url).then(response => {
          if(response.data) {
            this.codesGestionnaire = response.data.data;
          }
        })
      },
      getGestionnaireById(idGestionnaire_){
        if(idGestionnaire_!==null){ // Modification du gestionnaire idGestionnaire_
          this.idGestionnaire=idGestionnaire_;
          axios.get('/remocra/gestionnaire/' + idGestionnaire_).then(response => {
            response.data.data ? (this.nomGestionnaire = response.data.data.nom, 
                                  this.codeGestionnaire = response.data.data.code,
                                  this.actifGestionnaire = response.data.data.actif) : null
            this.buildedTitle='Modification de '+this.nomGestionnaire
            this.$bvModal.show('formGestionnaire');
          })
        }
        else{ // Création d'un gestionnaire
          this.resetFormGestionnaire();
          this.buildedTitle="Création d'un gestionnaire";
          this.actifGestionnaire = true;
          this.$bvModal.show('formGestionnaire');
        }
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
        let formData = new FormData();
        formData.append('gestionnaire', JSON.stringify({
          nom: this.nomGestionnaire,
          code: this.codeGestionnaire == '' ? null: this.codeGestionnaire,
          actif: this.actifGestionnaire,
        }))
        let url = this.idGestionnaire !== null ? '/remocra/gestionnaire/updateGestionnaire/' + this.idGestionnaire : '/remocra/gestionnaire/createGestionnaire';
        axios.post(url, formData).then(() => {
          this.$emit('gestionnaireUpdate'),  // confirme au parent qu'il y a eu un changement en base
          this.$nextTick(() =>{
            this.resetFormGestionnaire();
            this.$bvModal.hide('formGestionnaire')
          })
        })
      },
      resetFormGestionnaire(){
        this.$refs.formGestionnaire.reset();
        this.idGestionnaire= null;
        this.nomGestionnaire='';
        this.codeGestionnaire='';
        this.actifGestionnaire='';
        this.contacts=[];
      },
      checkFormValidity() { 
        if (this.$refs.formGestionnaire.checkValidity() == false ||         // Si formulaire invalide OU
            ( this.codeGestionnaire != '' && this.codesGestionnaire.includes(this.codeGestionnaire))) // SIREN pas vide ET deja renseigné
            {
              let msgErrorElement = document.getElementById("msgError");
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
  #formGestionnaire .invalid-feedback {width:150%;}
  .msgErrorDiv{justify-content: center;}
</style>