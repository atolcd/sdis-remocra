<template>
<div>
  <b-modal id="modalImportCTP" title="Import fichier CTP" centered ref="modalImportCTP" @show="resetModal" @hidden="resetModal">
    <form ref="formImportCTP">
       <b-form-group
       id="fieldset-horizontal"
       label-cols-sm="4"
       label-cols-lg="3"
       label="Fichier"
       label-for="document"
       invalid-feedback="Champ obligatoire"
       >
        <b-form-file
          v-model="fileToImport"
          id="document"
          placeholder="Choisissez un document..."
          drop-placeholder="Glisser le fichier à importer ici"
          accept=".xls, .xlsx"
        ></b-form-file>
      </b-form-group>
    </form>
    <template #modal-footer="">
      <b-button size="sm" type="reset" variant="secondary" @click="$bvModal.hide('modalImportCTP')">Annuler</b-button>
      <b-button size="sm" type="submit" variant="primary" @click="handleOk" :disabled="!fileToImport">Valider</b-button>
    </template>
  </b-modal>
</div>
</template>

<script>
export default {
  name: 'ModalImportCTP',
  data() {
    return {
       fileToImport: null,
    }
  },
  mounted: function() {
    this.$refs.modalImportCTP.show();
  },
  methods: {
    resetModal() {
      this.$refs.formImportCTP.reset();
    },
    handleOk(bvModalEvt) {
      bvModalEvt.preventDefault()
      this.handleSubmit()
    },
    handleSubmit() {

    }
  }
};
</script>

<style>
#modalImportCTP .invalid-feedback {
  font-size: 12px;
}

#modalImportCTP .modal-title {
  color: #7B7B7B;
  font-size: 20px;
  font-family: sans-serif, arial, verdana;
}
</style>
