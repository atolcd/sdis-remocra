<template>
  <b-modal id="modalApercu" @hidden="onHideApercu" no-close-on-backdrop size="lg" title="Aperçu du document">

    <div class="pdf" v-if="pdfData">
      <iframe id="apercuDocument" :src="pdfData" type="application/pdf" width="100%" height="100%" style="overflow: auto;"></iframe>
    </div>

    <div v-if="pdfData === null && !errorLoadPdf" class="d-flex justify-content-center mb-3">
      <b-spinner variant="primary"></b-spinner>
    </div>

    <div v-if="errorLoadPdf">
      <b-alert variant="danger" show>
        <h4 class="alert-heading">Apercu indisponible</h4>
        <p>Une erreur est survenue lors du chargement du document</p>
      </b-alert>
    </div>

    <template v-slot:modal-footer>
      <div class="row">
        <a :href="pdfData" :download="docCourant" v-if="pdfData && !errorLoadPdf">
          <b-button variant="primary">
            Télécharger
          </b-button>
        </a>

        <b-button @click="$bvModal.hide('modalApercu')">
          Fermer
        </b-button>
      </div>
    </template>
  </b-modal>
</template>

<script>
import moment from 'moment'
import axios from 'axios'
export default {
  name: 'ModalApercuDocument',

  props: {
    listeCourriers: {
      type: Array,
      required: true
    }
  },

  data() {
    return {
      docCourant: '',
      pdfData: null,
      errorLoadPdf: false // Booléen, déclenche l'affichage d'une erreur si le document n'a pu être chargé
    }
  },

  mounted: function() {
  },
  methods: {
    visualisationDocument(code, docCourant) {
      this.docCourant = docCourant;
      this.pdfData = null;
      this.errorLoadPdf = false;
      this.$bvModal.show('modalApercu');
      axios.get('/remocra/courrier/'+code, {
        headers: {
          'Content-Type': 'application/json'
        },
        responseType: 'blob',
      }).then(response => {

        var file = window.URL.createObjectURL(new Blob([response.data]), {type: 'application/pdf'});
        this.pdfData = file;
        // Ajout front-end de l'accusé
        var courrier = this.listeCourriers.filter(item => item.code === code)[0];
        if (courrier.utilisateurDestinataire){

          courrier.accuse = moment().format("YYYY-MM-DD HH:mm:ss");
        }
      }).catch(() => {
        this.errorLoadPdf = true;
      })
    },

    /**
      * Evènement lors de la fermeture de la fenêtre modale d'aperçu du document
      */
    onHideApercu: function() {
      this.pdfData = null;
      this.docCourant = '';
    }
  }
};
</script>

<style>
#modalApercu button {
  margin: .25rem;
}

.alert-heading {
  font-size: 1.5rem;
  margin-bottom: .5rem;
  font-weight: 500;
  line-height: 1.2;
}
</style>
