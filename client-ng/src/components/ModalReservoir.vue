<template>
<div>
  <b-modal id="modalReservoir" title="Ajout d'un réservoir" cancel-title="Annuler" ok-title="Valider" ref="modalReservoir" @show="resetModal" @hidden="resetModal" @ok="handleOk">
    <form ref="formReservoir">
      <b-form-group :state="etats.nom" label="Nom :" label-cols-md="4" label-for="inputNom" invalid-feedback="Le nom du réservoir est manquant">
        <b-form-input id="inputNom" v-model="reservoir.nom" :state="etats.nom">
        </b-form-input>
      </b-form-group>
      <b-form-group :state="etats.capacite" label="Capacité en m3" label-cols-md="4" label-for="inputCapacite" invalid-feedback="La capacité entrée n'est pas correcte">
        <b-form-input id="inputCapacite" v-model="reservoir.capacite" :state="etats.capacite" type="number" min=0 required></b-form-input>
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
      reservoir: {
        capacite: 0
      },
      etats: {
        nom: null,
        capacite: null
      }
    }
  },
  mounted: function() {
    this.reservoir.nom = ""
    this.reservoir.capacite = 0
  },
  methods: {
    checkFormValidity() {
      this.$refs.formReservoir.checkValidity();
      this.etats.nom = this.reservoir.nom.length > 0 ? 'valid' : 'invalid';
      this.etats.capacite = this.reservoir.capacite >= 0 && /^[0-9]+$/.test(this.reservoir.capacite)? 'valid' : 'invalid';
      return !this.hasInvalidState(this.etats);
    },

    /**
     * Vérifie si le formulaire a des champs invalides
     * En pratique, les modules vueJS enfants ce module renvoient leur état après un appel à leur fonction checkFormValidity()
     * @param etats L'objet "etats" des champs d'un module
     * @return TRUE si le champ présente des champs invalides, FALSE sinon
     */
    hasInvalidState(etats) {
      var hasInvalidState = false;
      for (var key in etats) {
        hasInvalidState = hasInvalidState || etats[key] == "invalid";
      }
      return hasInvalidState;
    },

    resetModal() {
      this.$refs.formReservoir.reset()
      this.etats.nom = "";
      this.etats.capacite = 0;
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
      }).catch(function(error) {
        console.log(error);
      });
    }
  }
};
</script>

<style>
#modalReservoir .invalid-feedback {
  font-size: 12px;
}

#modalReservoir .modal-title {
  color: #7B7B7B;
  font-size: 20px;
  font-family: sans-serif, arial, verdana;
}
</style>
