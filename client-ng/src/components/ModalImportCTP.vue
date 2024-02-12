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
        <b-button size="sm" type="reset" variant="secondary" @click="$bvModal.hide('modalImportCTP')" >Annuler</b-button>
        <b-button size="sm" type="submit" variant="primary" @click="handleOk" :disabled="!fileToImport || isloading">
          <span v-if="!isloading">Valider</span>
          <span v-else><b-spinner small></b-spinner>Chargement...</span>
        </b-button>
      </template>
      <notifications group="remocra" position="top right" animation-type="velocity" :duration="3000"/>
    </b-modal>
    <ImportCTPResultat id="importCTPResultat" ref="importCTPResultat"></ImportCTPResultat>
  </div>
</template>

<script>
import axios from 'axios'
import ImportCTPResultat from './ImportCTPResultat.vue'
import {
  loadProgressBar
} from 'axios-progress-bar'

export default {
  name: 'ModalImportCTP',
  components: {
    ImportCTPResultat
  },
  data() {
    return {
      fileToImport: null,
      isloading: false
    }
  },
  mounted: function () {
    this.$refs.modalImportCTP.show();
  },
  methods: {
    handleOk(bvModalEvt) {
      bvModalEvt.preventDefault();
      this.handleSubmit();
    },
    handleSubmit() {
      if (this.fileToImport == null) return;
      this.isloading = true

      this.$notify({
        group: 'remocra',
        type: 'info',
        title: 'Info',
        text: "En cours d'importation"
      });


      var formData = new FormData();
      formData.append('file', this.fileToImport);
      loadProgressBar({
        parent: "#modalImportCTP",
        showSpinner: false
      });
      axios.post('/remocra/hydrants/importctpverification', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(response => {
        this.isloading = false;
        var data = JSON.parse(response.data.message);
        this.$refs.importCTPResultat.loadData(data.bilanVerifications, this.fileToImport.name);
        this.$refs.modalImportCTP.hide();
        this.$bvModal.show("importCTPResultat");
      }).catch(() => {
        this.isloading = false;
        this.$notify({
          group: 'remocra',
          type: 'error',
          title: 'Erreur',
          text: "Une erreur est survenue lors de l'import CTP"
        });
      });
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
