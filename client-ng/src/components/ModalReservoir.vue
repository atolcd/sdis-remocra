<template>
  <div>
    <b-modal 
        id="modal-reservoir" 
        centered 
        title="Ajout d'un réservoir" 
        cancel-title="Annuler" 
        ok-title="Valider" 
        ref="modalReservoir"
        @show="resetModal"
        @hidden="resetModal"
        @ok="handleOk" >
      <form ref="formReservoir">
        <b-form-group
            :state="etats.nom"
            label="Nom :"
            label-cols-md="4"
            label-for="inputNom"
            invalid-feedback="Le nom du réservoir est manquant" >
          <b-form-input
              id="inputNom"
              v-model="reservoir.nom"
              :state="etats.nom" >      
          </b-form-input>
        </b-form-group>

        <b-form-group
            :state="etats.capacite"
            label="Capacité en m3"
            label-cols-md="4"
            label-for="inputCapacite"
            invalid-feedback="La capacité entrée n'est pas correcte"
        >
          <b-form-input
              id="inputCapacite"
              v-model="reservoir.capacite"
              :state="etats.capacite"
              type="number"
              min=0
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
  name: 'Modalreservoir',
  data() {
    return {
      reservoir: {},
      etats: {
        nom: null,
        capacite: null
      }
    }
  },

  mounted: function(){
    this.reservoir.nom = ""
    this.reservoir.capacite = ""
  },

  methods: {

    checkFormValidity() {
      const valid = this.$refs.formReservoir.checkValidity()
      this.etats.nom = this.reservoir.nom.length ? 'valid' : 'invalid';
      this.etats.capacite = this.reservoir.capacite >= 0 ? 'valid' : 'invalid';
      return valid
    },

    resetModal() {
        this.$refs.formReservoir.reset()
        this.etats.nom = null;
        this.etats.capacite = null;
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
      axios.post('/remocra/reservoir', {
        nom: this.reservoir.nom,
        capacite: this.reservoir.capacite,
        actif: true
      }).then(function(response) {
        self.$emit('modalReservoirValues', response.data.data); //On envoie les données au parent
        self.$nextTick(() => { //Fermeture manuelle de la modale
          self.$refs.modalReservoir.hide()
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