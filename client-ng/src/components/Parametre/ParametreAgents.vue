<template>
  <select v-model="agentsSelected" @change="rowChange">
    <option v-for="agent in agentsSelectable" :value="agent.code" :key="agent.code" >{{ agent.libelle }}</option>
  </select>
</template>
<script>

import axios from "axios";

export default {
  name: 'ParametreAgent',
  mounted() {
    this.getAgents();
  },
  props: {
    selected:{
      type:String,
      require: true
    },
    options:{
        type: Array,
        require: true
    }

  },
  data() {
    return {
      agentsSelectable: [],
      agentsSelected:null
    }
  },
  methods: {

    rowChange:function(){
      this.$emit("rowChange",this.agentsSelected);
    },
    getAgents() {
      axios.get('/remocra/parametre/agents/selected/')
          .then((response) => {
            if (response.data !== null) {
              this.agentsSelected = response.data

            } else {
              this.$notify({
                group: 'remocra',
                type: 'error',
                title: 'Erreur',
                text: 'La valeur semble non définie en base'
              });
            }
            axios.get('/remocra/parametre/agents/selectable/')
                .then((response) => {
                  let datas = response.data;
                  if (datas !== null) {
                    datas.forEach((data) => {
                      switch (data) {
                        case "UTILISATEUR_CONNECTE_OBLIGATOIRE" :
                          this.agentsSelectable.push({code: data, libelle:"Utilisateur connecté obligatoire"});
                          break;
                        case "UTILISATEUR_CONNECTE" :
                          this.agentsSelectable.push({code: data, libelle:"Utilisateur connecté"});
                          break;
                        case "COMPOSANT_AGENT_ONLY" :
                          this.agentsSelectable.push({code: data, libelle:"Liste des agents"});
                          break;
                        case "VALEUR_PRECEDENTE" :
                          this.agentsSelectable.push({code: data, libelle:"Valeur précédente"});
                          break;
                        default :
                          this.agentsSelectable.push({code: "INCONNU", libelle:"INCONNU"});
                          break;
                      }

                    })

                  } else {
                    this.$notify({
                      group: 'remocra',
                      type: 'error',
                      title: 'Erreur',
                      text: 'La valeur semble non définie en base'
                    });
                  }


                })
          })
    }

  }
};
</script>