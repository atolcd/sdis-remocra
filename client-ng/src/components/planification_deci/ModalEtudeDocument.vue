<template>
<b-modal id="modalDocument"
    title="Ajouter un document"
    cancel-title="Annuler"
    ok-title="Valider"
    :ok-disabled="fileToImport == null || fileToImportName == null"
    @ok="$emit('documentImport', fileToImport, fileToImportName)"
    @hidden="fileToImportName = null; fileToImport = null"
    @show="resetModal">
  <b-form-group
   id="fieldset-horizontal"
   label-cols-sm="4"
   label-cols-lg="3"
   label="Nom"
   label-for="nom"
   >
    <b-form-input id="nom" v-model="fileToImportName"></b-form-input>
  </b-form-group>
  <b-form-group
   id="fieldset-horizontal"
   label-cols-sm="4"
   label-cols-lg="3"
   label="Document"
   label-for="document"
   invalid-feedback="Champ obligatoire"
   >
    <b-form-file
      v-model="fileToImport"
      id="document"
      placeholder="Choisissez un document..."
      drop-placeholder="Glisser le fichier à importer ici"
    ></b-form-file>
  </b-form-group>
</b-modal>
</template>

<script>

export default {
  name: 'ModalEtudeDocument',

  data() {
    return {
      fileToImport: null,
      fileToImportName: null
    }
  },

  methods: {
    resetModal: function() {
      this.fileToImport = null;
      this.fileToImportName = null;
    }
  }
};
</script>

<style>
.custom-file-input ~ .custom-file-label::after {
  content: 'Parcourir' !important;
}
</style>
