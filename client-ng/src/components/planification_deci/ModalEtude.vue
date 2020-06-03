<template>
<div>
  <b-modal id="modalEtude" title="Etude DECI" size="lg" cancel-title="Annuler" ok-title="Valider" ref="modal" @show="resetModal" @hidden="resetModal" @ok="handleOk">
    <form ref="formEtude">
      <b-tabs content-class="mt-3">
          <b-tab title="Généralités" ref="tabDefault" active>
            <div class="row">
              <div class="col-md-6">
                <b-form-group
                 id="fieldset-horizontal"
                 label-cols-sm="4"
                 label-cols-lg="3"
                 label="Type"
                 label-for="type"
                 >
                  <b-form-select id="type" v-model="type" :options="typeEtudes" size="sm" :disabled="readOnly"></b-form-select>
                </b-form-group>
              </div>
              <div class="col-md-6">
                <b-form-group
                 id="fieldset-horizontal"
                 label-cols-sm="4"
                 label-cols-lg="3"
                 label="Numéro"
                 label-for="numero"
                 invalid-feedback="Numéro manquant ou déjà attribué"
                 >
                  <b-form-input id="numero" v-model="numero" :state="etats.numero" required :readOnly="etude !== null"></b-form-input>
                </b-form-group>
              </div>
            </div>

            <div class="row">
              <div class="col-md-12">
                <b-form-group
                 id="fieldset-horizontal"
                 label-cols-sm="4"
                 label-cols-lg="3"
                 label="Nom"
                 label-for="nom"
                 invalid-feedback="Champ obligatoire"
                 >
                  <b-form-input id="nom" v-model="nom" :state="etats.nom" required :readOnly="readOnly"></b-form-input>
                </b-form-group>
              </div>
            </div>

            <div class="row">
              <div class="col-md-12">
                <b-form-group
                 id="fieldset-horizontal"
                 label-cols-sm="4"
                 label-cols-lg="3"
                 label="Description"
                 label-for="description"
                 invalid-feedback="Champ obligatoire"
                 >
                  <b-form-textarea
                    id="description"
                    v-model="description"
                    rows="3"
                    max-rows="6"
                    :state="etats.description"
                    required
                    :disabled="readOnly"
                  ></b-form-textarea>
                </b-form-group>
              </div>
            </div>

            <div class="row">
              <div class="col-md-12">
                  <b-form-group
                   id="fieldset-horizontal"
                   label-cols-sm="4"
                   label-cols-lg="3"
                   label="Communes"
                   label-for="communes"
                   >
                   <div id="communes">
                     <div v-if="!readOnly"> <!-- Affichage de la searchbox et des boutons seulement hors mode lecture seule -->
                       <SearchCommune ref="searchCommune"
                          @communeSelected="disableAjoutCommuneBouton = false"
                          @communeInputChange="disableAjoutCommuneBouton = true; showInvalidCommune=false"></SearchCommune>
                          <p class="communeInvalid" v-if="showInvalidCommune">La commune est déjà présente</p>
                       <b-button variant="primary"
                          :disabled="disableAjoutCommuneBouton"
                          @click="addCommune"
                          size="sm"
                          class="buttonRow">Ajouter</b-button>
                       <b-button variant="danger"
                            size="sm"
                            class="buttonRow"
                            :disabled="selectedCommune == null"
                            @click="removeCommune(selectedCommune)">Retirer</b-button>
                        </div>
                     <div class="itemCommuneContainer" v-if="communes.length > 0">
                       <p v-for="(commune, index) in communes"
                          :key="index"
                          size="sm"
                          :class="{'itemCommune': true, 'bg-secondary':commune.id==selectedCommune, 'text-light':commune.id==selectedCommune}"
                          @click="selectedCommune = (selectedCommune == commune.id) || readOnly ? null : commune.id">
                         {{commune.nom}}
                       </p>
                     </div>
                   </div>
                  </b-form-group>
              </div>
            </div>
          </b-tab>

          <b-tab title="Documents" class="tabDocuments">
            <div class="row" v-if="!readOnly">
              <div class="col-md-12">
                <b-button variant="primary"
                   size="sm"
                   class="buttonRow" v-b-modal.modalDocument>Ajouter</b-button>
                <b-button variant="danger"
                     size="sm"
                     class="buttonRow btn"
                     :disabled="selectedFiles.length == 0"
                     @click="removeFiles(selectedFiles)">Retirer</b-button>
              </div>
            </div>

            <div class="row">
              <div class="col-md-12">
                <b-form-checkbox-group v-model="selectedFiles" name="flavour-2">
                  <table class="table table-sm table-bordered table-fixed" id="tableDocuments" v-if="documents.length > 0">
                    <thead class="thead-light">
                      <th scope="col">
                        <p>Titre</p>
                      </th>
                      <th scope="col">
                        <p>Fichier</p>
                      </th>
                    </thead>
                    <tbody>
                      <tr v-for="(document, index) in documents" :key="index">
                        <td>
                          <b-form-checkbox :value="index" v-if="!readOnly">{{document.nom}}</b-form-checkbox>
                          <div v-else >{{document.nom}}</div>
                        </td>
                        <td v-if="document.code">
                          <a :href="'telechargement/document/'+document.code" :download="document.name">{{document.name}}
                          </a>
                        </td>
                        <td v-else>{{document.name}}</td>
                      </tr>
                    </tbody>
                  </table>
                </b-form-checkbox-group>
              </div>
            </div>
          </b-tab>
        </b-tabs>
    </form>
  </b-modal>

  <ModalEtudeDocument @documentImport="onDocumentImport"></ModalEtudeDocument>
</div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'

import SearchCommune from './SearchCommune.vue'
import ModalEtudeDocument from './ModalEtudeDocument.vue'

export default {
  name: 'ModalEtude',

  components: {
    SearchCommune,
    ModalEtudeDocument
  },

  data() {
    return {

      // Données de l'étude
      type: null,
      numero: null,
      nom: null,
      description: null,
      communes: [],
      documents: [],

      // Modale ajout de document
      fileToImport: null,
      fileToImportName: null,

      disableAjoutCommuneBouton: true,
      showInvalidCommune: false,
      selectedCommune: null,
      selectedFiles: [],
      readOnly: false,

      etats : {
        numero: null,
        nom: null,
        description: null
      }
    }
  },

  props: {
    typeEtudes: {
      required: true,
      type: Array
    },

    etude: {
      required: false,
      type: Object
    }
  },

  mounted: function() {
    this.type = this.typeEtudes[0].value;
  },
  methods: {
    resetModal() {
      this.$refs.formEtude.reset();
      if(this.$refs.searchCommune) {
        this.$refs.searchCommune.reset()
      }
      this.$refs.tabDefault.activate();

      this.type = this.typeEtudes[0].value;
      this.numero = null;
      this.nom = null;
      this.description = null;
      this.communes = [];
      this.documents = [];

      this.fileToImport = null;
      this.fileToImportName = null;

      this.etats = {
        numero: null,
        nom: null,
        description: null
      };

      this.disableAjoutCommuneBouton = true;
      this.showInvalidCommune = false;
      this.selectedCommune = null;
      this.selectedFiles = [];
      this.readOnly = false;

      // Mode modification d'une étude existante
      if(this.etude) {
        this.nom = this.etude.nom;
        this.type = this.etude.type.code;
        this.numero = this.etude.numero;
        this.description = this.etude.description;
        this.communes = _.cloneDeep(this.etude.communes);
        this.documents = _.clone(this.etude.documents);
        _.forEach(this.documents, d => {
          d.name = d.fichier;
          d.nom = this.etude.documentsNoms[d.code];
        })

        this.readOnly = this.etude.readOnly || this.etude.statut.code == "TERMINEE";
      }
    },

    // =================================== Gestion des communes
    addCommune() {
      var commune = this.$refs.searchCommune.getSelected();
      var index = _.findIndex(this.communes, c => c.id == commune.id);

      // On n'ajoute la commune que si elle n'est pas déjà présente
      if(index == -1) {
        this.communes.push(this.$refs.searchCommune.getSelected());
        this.$refs.searchCommune.reset();
      } else {
        this.showInvalidCommune = true;
      }
    },

    removeCommune(communeId) {
      this.communes = this.communes.filter(c => c.id != communeId);
      this.selectedCommune = null;
    },

    // ========================================================

    onDocumentImport(file, name) {
      file.nom = name;
      this.documents.push(file);
      this.fileToImport = null;
      this.fileToImportName = null;
    },

    removeFiles(indexFiles) {
      // Retrait client
      this.documents = this.documents.filter(d => _.indexOf(indexFiles, _.indexOf(this.documents, d)) == -1)

      // TODO: Mise en mémoire retrait serveur

      this.selectedFiles = [];
    },

    /**
     * Vérifie si le formulaire a des champs invalides
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

    checkFormValidity() {

      this.etats.numero = 'valid';
      this.etats.nom = this.nom && this.nom.length > 0 ? 'valid' : 'invalid';
      this.etats.description = this.description && this.description.length > 0 ? 'valid' : 'invalid';

      // On vérifie que le numéro de l'étude n'est pas un doublon (création seulement)
      if (!this.etude && this.numero && this.numero.length > 0) {
        return axios.get('/remocra/etudes/checknumero.json?numero='+this.numero).catch(() => {
          this.$notify({
            group: 'remocra',
            type: 'error',
            title: 'Erreur numméro de l\'étude',
            text: "Une erreur est survenue lors de la vérification du numéro de l'étude"
          });
        }).then((response) => {
          if(!response.data || !response.data.data) {
            this.etats.numero = 'invalid';
            return !this.hasInvalidState(this.etats);
          }
          return !this.hasInvalidState(this.etats);
        });
      } else if(!this.etude && (this.numero && this.numero.length == 0)){
        this.etats.numero = 'invalid';
      }
      return !this.hasInvalidState(this.etats);
    },

    handleOk(bvModalEvt) {
      bvModalEvt.preventDefault();
      // Numéro renseigné et ajout d'une étude => vérification de doublon
      if(this.numero && this.etude == null) {
        this.checkFormValidity().then(response => {
          if(response) {
            this.handleAddEtude(); // Si checkFormValidity renvoie une promise, c'est forcément un ajout
          }
        });
      } else if(this.checkFormValidity()) {
        if(this.etude) {
          this.handleEditEtude();
        } else {
          this.handleAddEtude();
        }

      }
    },

    handleAddEtude() {
      var formData = new FormData();
      formData.append("etude", JSON.stringify({
        nom: this.nom,
        numero: this.numero,
        type: this.type,
        description: this.description,
        communes: this.communes.map(commune => commune.id)
      }));

      _.forEach(this.documents, document => {
        formData.append(document.nom, document)
      })

      axios.post('/remocra/etudes/', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).catch(() => {
        this.$notify({
          group: 'remocra',
          type: 'error',
          title: 'Erreur création étude',
          text: "Une erreur est survenue lors de la création de l'étude"
        });
      }).then(response => {
        if(response.data && response.data.success) {
          this.$bvModal.hide("modalEtude");
          this.$emit('refreshEtudes');
          this.$notify({
            group: 'remocra',
            type: 'success',
            title: 'Ajout réussi',
            text: response.data.message
          });
        }
      })
    },

    handleEditEtude() {
      /** On compare les données actuelles et anciennes afin de déterminer si il y a eu une modification
        * des données (la date de dernière modification est gardée en base).
        */

      var newEtude = {
        nom: this.nom,
        numero: this.numero,
        type: this.type,
        description: this.description,
        communes: this.communes.map(commune => commune.id).sort(),
        documents: this.documents
      };

      var oldEtude = {
        nom: this.etude.nom,
        numero: this.etude.numero,
        type: this.etude.type.code,
        description: this.etude.description,
        communes: this.etude.communes.map(commune => commune.id).sort(),
        documents: this.etude.documents
      }

      if(!_.isEqual(oldEtude, newEtude)) {
        var formData = new FormData();
        formData.append("etude", JSON.stringify({
          id: this.etude.id,
          nom: this.nom,
          numero: this.numero,
          type: this.type,
          description: this.description,
          communes: this.communes.map(commune => commune.id),
        }));

        // Ajout de fichiers
        var addedFiles = _.difference(this.documents, this.etude.documents);
        if(addedFiles.length) {
          _.forEach(addedFiles, document => {
            formData.append(document.nom, document)
          })
        }

        // Suppression de fichiers (envoi du code)
        var removedFiles = _.difference(this.etude.documents, this.documents).map(document => document.code);
        formData.append("removedDocuments", JSON.stringify(removedFiles));

        // Envoi au serveur
        axios.post('/remocra/etudes/editEtude', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).catch(() => {
          this.$notify({
            group: 'remocra',
            type: 'error',
            title: 'Erreur modification étude',
            text: "Une erreur est survenue lors de la modification de l'étude"
          });
        }).then(response => {
          if(response.data && response.data.success) {
            this.$bvModal.hide("modalEtude");
            this.$emit('refreshEtudes');
            this.$notify({
              group: 'remocra',
              type: 'success',
              title: 'Modification réussie',
              text: response.data.message
            });
          }
        })

      } else {

        this.$nextTick(() => {
          this.$bvModal.hide("modalEtude");
          this.$emit('refreshEtudes');
        })
      }
    }
  }
};
</script>

<style>
#modalEtude .invalid-feedback {
  font-size: 12px;
}

#modalEtude .modal-title {
  color: #7B7B7B;
  font-size: 20px;
  font-family: sans-serif, arial, verdana;
}
.tabs {
 border: 1px solid #dee2e6;
}

select {
  margin-top: 5px;
}

.form-row{
  padding-left: 10px;
  padding-right: 10px;
}

.communeInvalid {
  color:  #d9534f;
  font-size: 12px;
  margin-top: 5px;
  margin-bottom: 5px;
}

.buttonRow {
  margin-right: 10px;
  margin-top: 10px;
  margin-bottom: 10px;
}

.itemCommuneContainer {
  max-height: 100px;
  overflow-y: scroll;
  overflow-x: hidden;
  border-radius: 0.2em;
  border: 1px solid #CED4DA;
  background-color: white;
}

.itemCommune {
  padding: 2px 0px 0px 5px;
  color: #495057;
  margin-bottom: 0;
}

.tabDocuments {
  padding-left: 15px;
  padding-right: 15px;
}
.custom-file-input ~ .custom-file-label::after {
  content: 'Parcourir' !important;
}
</style>
