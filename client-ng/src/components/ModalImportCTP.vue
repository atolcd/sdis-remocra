<template>
<div>
  <b-modal id="modalImportCTP" title="Import fichier CTP" centered ref="modalImportCTP">
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
  <ImportCTPResultat id="importCTPResultat" ref="importCTPResultat"> </ImportCTPResultat>
</div>
</template>

<script>
import axios from 'axios'
import ImportCTPResultat from './ImportCTPResultat.vue'

export default {
  name: 'ModalImportCTP',
  components : {
    ImportCTPResultat
  },
  data() {
    return {
       fileToImport: null,
    }
  },
  mounted: function() {
    this.$refs.modalImportCTP.show();
  },
  methods: {
    handleOk(bvModalEvt) {
      bvModalEvt.preventDefault();
      this.handleSubmit();
    },
    handleSubmit() {
      this.$refs.modalImportCTP.hide();
      if(this.fileToImport == null) return;
      var formData = new FormData();
      formData.append('file', this.fileToImport);
      axios.post('/remocra/hydrants/importctpverification', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(response => {
        var data = JSON.parse(response.data.message);
        this.$refs.importCTPResultat.loadData(data.bilanVerifications, this.fileToImport.name);
        this.$bvModal.show("importCTPResultat");
      })
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
