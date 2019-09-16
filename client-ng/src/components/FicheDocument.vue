<template>
<div>
  <div class="row" id="rowDocuments">
    <div class="col-md-12">
      <b-list-group>
        <b-list-group-item class="input-group" v-for="(file, index) in files" :key="index" variant="secondary" size="sm">
          <span v-if="file.code">
            <a :href="'telechargement/document/'+file.code" download>{{file.nom}}</a>
          </span>
          <span v-else>
            {{file.nom}}
          </span>
          <span class="input-group-btn">
            <button @click.prevent @click="deleteFile(index)" size="sm" id="boutonDelete"><img src="../assets/img/decline.png" /></button>
          </span>
        </b-list-group-item>
      </b-list-group>
    </div>
  </div>
  <p>Pour <span class="bold">ajouter un document</span>, cliquez sur le bouton "Importer" et sélectionnez le fichier à transmettre</p>
  <p> Pour <span class="bold">annuler l'envoi d'un fichier</span>, cliquez sur le bouton "Supprimer" en face du document concerné</p>
  <br />
  <div>
    <b-form-file v-model="selectedFile" class="mb-2" placeholder="Aucun fichier sélectionné" browse-text="Importer"></b-form-file>
    <b-button @click="addFile" class="mr-2 btn btn-info" :disabled="isAddButtonDisabled" size="sm">Ajouter</b-button>
  </div>
</div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'
export default {
  name: 'FicheDocument',
  data() {
    return {
      files: [],
      selectedFile: null
    }
  },
  computed: {
    isAddButtonDisabled: function() {
      return this.selectedFile === null;
    },
  },
  props: {
    hydrant: {
      required: true,
      type: Object
    }
  },
  mounted: function() {
    _.forEach(this.hydrant.hydrantDocuments, item => {
      this.files.push({
        id: item.id,
        nom: item.titre,
        code: item.code,
        data: null
      });
    }, this);
  },
  methods: {
    addFile() {
      if (this.selectedFile !== null) {
        this.files.push({
          id: null,
          nom: this.selectedFile.name,
          code: null,
          data: this.selectedFile
        });
        this.selectedFile = null;
      }
    },
    deleteFile(index) {
      // Suppression d'un document nouvellement ajouté, on le retire de la liste
      if (this.files[index].id === null) {
        this.files.splice(index, 1);
      } else { //Suppression d'un document existant, on demande au serveur de le supprimer de la BDD
        var self = this;
        axios.delete('/remocra/hydrants/document/' + this.files[index].id).then(function() {
          self.files.splice(index, 1);
        }).catch(function(error) {
          console.error('Deleting data from remocra/hydrants/document', error);
        })
      }
    },
    getFiles() {
      // De tous les fichiers, on n'envoie que les fichiers nouvellement ajoutés dans la requête de mise à jour
      return this.files.filter(item => item.id == null);
    }
  }
};
</script>

<style scoped>
#rowDocuments {
  margin-bottom: 10px;
  max-height: 320px;
  overflow: auto;
}

#boutonDelete {
  margin-left: 5px;
  border: none;
  background-color: transparent;
}

.bold {
  font-weight: bold;
}

.list-group-item {
  height: 30px;
  vertical-align: middle;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
  font-size: 12px;
  padding-top: 6px;
}

p {
  font-style: italic;
  color: rgb(128, 128, 128);
}
</style>