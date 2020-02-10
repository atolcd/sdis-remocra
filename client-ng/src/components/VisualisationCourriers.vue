<template>
  <div id="visualisationCourriers" class="container-fluid">
    <div class="row">
      <div class="col-md-12">
        <h1>Visualisation des courriers</h1>

      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'visualisationCourriers',
  data() {
    return {
      nbCourriers: 0,
      nbCourriersParPage: 10,
      pageActuelle: 1, // Pagination
      listeCourriers: [] // Liste des courriers de la page actuelle
    }
  },
  props: {

  },
  mounted: function() {
    // Récupération du nombre de courriers
    axios.get('/remocra/courrier/courrierdocumentcount').then(response => {
      this.nbCourriers = response.data.message;

      let start = (this.pageActuelle - 1) * this.nbCourriersParPage;
      axios.get('/remocra/courrier/courrierdocument?start='+start+'&limit='+this.nbCourriersParPage).then(response => {
        this.listeCourriers = response.data.data;
      });
    });
  },

  methods: {

  }
};
</script>

<style scoped>
h1 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
  margin-top: 1rem;
  color: #7b7b7b;
  font-family: Helvetica, Arial !important
}

</style>
