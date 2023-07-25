<template>
  <div id="transfertsAutomatises" class="container-fluid">
    <div class="row">
      <div class="col-md-12">
        <h1>Transferts automatisés</h1>
      </div>
    </div>
      <div class="row">
        <div class="col-md-6">
          <table class="table table-sm table-bordered table-fixed table-striped" id="tableCourriers">
            <thead class="thead-light">
              <th>
                <p>Type d'organisme</p>
              </th>
              <th>
                <p>Récupérer depuis REMOcRA</p>
              </th>
              <th>
                <p>Transmettre à REMOcRA</p>
              </th>
              <th>
                <p>Administrer REMOcRA</p>
              </th>
            </thead>
            <tbody :key="tableKey">
            <tr v-for="(item, index) in listeTypesOrganismes" :key="index" >
              <td>{{item.nom}}</td>
              <td>
                <b-form-checkbox :checked="item.recuperer" @change="changeAccesRecuperer(item.organisme_id, item.recuperer)">
                </b-form-checkbox>
              </td>
              <td>
                 <b-form-checkbox :checked="item.transmettre" @change="changeAccesTransmettre(item.organisme_id, item.transmettre)">
                </b-form-checkbox>
              </td>
              <td>
                 <b-form-checkbox :checked="item.administrer" @change="changeAccesAdministrer(item.organisme_id, item.administrer)">
                 </b-form-checkbox>
              </td>
            </tr>
          </tbody>
          </table>
        </div>
      </div>
  </div>
</template>

<script>

import axios from 'axios'

export default {
  name: 'transfertsAutomatises',

  data() {
    return {
      listeTypesOrganismes: []
    }
  },

  mounted: function() {
    this.getAccesOrganisme();
  },

  methods: {
    getAccesOrganisme() {
      // Récupération des types d'organisme
      axios.get('/remocra/transfertsautomatises', {})
      .then(response => {
        if(response.data){
          this.listeTypesOrganismes = response.data.data;
        }
      });
    },

    changeAccesAdministrer(id, value){
      axios.post('/remocra/transfertsautomatises/updateadmin/'+id, 
      JSON.stringify({"value": !value}),
      {
        headers: {
          'Content-Type': 'application/json;charset=utf-8'
        }
      }
      ).then(response => {
        if(response.data.error){
          this.$notify({
            group: 'remocra',
            type: 'error',
            title: 'Erreur',
            text: "Erreur lors de la modification des accès",
            duration: 5000
          });
        }
        this.getAccesOrganisme()
      }).catch(error => {
        console.error(error);
      });
    },

    changeAccesTransmettre(id, value){
      axios.post('/remocra/transfertsautomatises/updatepost/'+id, 
      JSON.stringify({"value": !value}),
      {
        headers: {
          'Content-Type': 'application/json;charset=utf-8'
        }
      }
      ).then(response => {
        if(response.data.error){
          this.$notify({
            group: 'remocra',
            type: 'error',
            title: 'Erreur',
            text: "Erreur lors de la modification des accès",
            duration: 5000
          });
        }
        this.getAccesOrganisme()
      }).catch(error => {
        console.error(error);
      });
    },

    changeAccesRecuperer(id, value){
      axios.post('/remocra/transfertsautomatises/updateget/'+id, 
      JSON.stringify({"value": !value}),
      {
        headers: {
          'Content-Type': 'application/json;charset=utf-8'
        }
      }
      ).then(response => {
        if(response.data.error){
          this.$notify({
            group: 'remocra',
            type: 'error',
            title: 'Erreur',
            text: "Erreur lors de la modification des accès",
            duration: 5000
          });
        }
        this.getAccesOrganisme()
      }).catch(error => {
        console.error(error);
      });
    }

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

th, td {
    text-align: center;
}
</style>
