<template>
  <div>
    <b-modal 
        id="modal-gestionnaire" 
        centered 
        title="Ajout d'un gestionnaire" 
        cancel-title="Annuler" 
        ok-title="Valider" 
        ref="modalGestionnaire"
        @show="resetModal"
        @hidden="resetModal"
        @ok="handleOk" >
      <form ref="formGestionnaire">
        <b-form-group
            :state="etats.nom"
            label="Nom du gestionnaire"
            label-cols-md="4"
            label-for="inputNom"
            invalid-feedback="Le nom du gestionnaire est manquant" >
          <b-form-input
              id="inputNom"
              v-model="gestionnaire.nom"
              :state="etats.nom" >      
          </b-form-input>
        </b-form-group>

        <b-form-group
            :state="etats.code"
            label="Code du gestionnaire"
            label-cols-md="4"
            label-for="inputCode"
            invalid-feedback="Le code du gestionnaire est manquant"
        >
          <b-form-input
              id="inputCode"
              v-model="gestionnaire.code"
              :state="etats.code"
              required
          ></b-form-input>
        </b-form-group>
      </form>
      
    </b-modal>
  </div>
</template>

<script>

import axios from 'axios'

export default {
  name: 'ModalGestionnaire',
  data() {
    return {
      gestionnaire: {},
      etats: {
        nom: null,
        code: null
      }
    }
  },

  mounted: function(){
    this.gestionnaire.nom = "";
    this.gestionnaire.code = "";
  },

  methods: {

    checkFormValidity() {
      const valid = this.$refs.formGestionnaire.checkValidity()
      this.etats.nom = this.gestionnaire.nom.length ? 'valid' : 'invalid';
      this.etats.code = this.gestionnaire.code.length ? 'valid' : 'invalid';
      return valid
    },

    resetModal() {
        this.$refs.formGestionnaire.reset()
        this.etats.nom = null;
        this.etats.code = null;
    },

    handleOk(bvModalEvt) {
      bvModalEvt.preventDefault()
      this.handleSubmit()
    },

    handleSubmit() {
      if (!this.checkFormValidity()) {
        return;
      }

      var self = this;
      axios.post('/remocra/gestionnaire', {
        nom: this.gestionnaire.nom,
        code: this.gestionnaire.nom,
        actif: true,
        version: 1
      }).then(function(response) {
        self.$emit('modalGestionnaireValues', response.data.data); //On envoie les données au parent
        self.$nextTick(() => { //Fermeture manuelle de la modale
          self.$refs.modalGestionnaire.hide()
        })

      }).catch(function (error) {
        console.log(error);
      });
        
    }

  }
  
};
</script>

<style>

#modal-gestionnaire .invalid-feedback{
  font-size: 12px;
}

#modal-gestionnaire {
  color: black;
}

#modal-gestionnaire .modal-title{
  color: #7B7B7B;
  font-size: 20px;
  font-family: sans-serif,arial,verdana;
}

</style>