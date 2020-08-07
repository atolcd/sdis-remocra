<template>
  <div>
  <OlMap ref="olMap" :couchesJSONPath="'/remocra/ext-res/layers/etude.json'" :couchesViewParams="couchesViewParams" >
  </OlMap>
  <ModalePeiProjet id="modalePeiProjet" :idEtude="parseInt(idEtude)" :coordonnees="peiProjetCoordonnees"></ModalePeiProjet>
</div>


</template>

<script>
import OlMap from '../OlMap/OlMap.vue'
import ModalePeiProjet from './ModalePeiProjet.vue'
import _ from 'lodash'

import * as eventTypes from '../../bus/event-types.js'

import OlInteractionDraw from 'ol/interaction/Draw.js'

export default {
  name: 'OlMapEtude',
  components: {
    OlMap,
    ModalePeiProjet
  },

  props: {
    idEtude: {
      required: true
    }
  },

  data() {
    return {
      olMap : null,
      toolBar: null,

      interactionAddPei: null,
      peiProjetCoordonnees: null,

      couchesViewParams: [{
        layer: "etude_hydrant_projet",
        value: "idEtude:"+this.idEtude
      }]
    }
  },

  mounted: function() {
    this.olMap = this.$refs['olMap'];
    this.toolBar = this.$refs['olMap'].$refs['toolBar'];
    this.$nextTick(() => {
      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        type: "separator",
      });

      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        type: "libelle",
        text: "PEI Projets"
      });

      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        iconPath: "/remocra/static/img/pencil.png",
        type: "button",
        name: "addPei",
        onClick: () => {
          this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_TOGGLEBUTTON, "addPei");
        },
        onToggle: (state) => {
          if(state) {
            this.olMap.map.on("click", this.handleMapClickAddPei);
            var workingLayer = this.olMap.getLayerById('workingLayer');
            this.interactionAddPei = new OlInteractionDraw({
              type: 'Point',
              source: workingLayer.getSource(),
              geometryName: "newPeiProjet"
            });
            this.olMap.map.addInteraction(this.interactionAddPei);
          } else {
            this.olMap.map.removeInteraction(this.interactionAddPei);
            this.olMap.map.un("click", this.handleMapClickAddPei);
          }

        }
      });

      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        type: "separator",
      });
    });
  },

  methods: {

      /**
      * Clic sur la carte avec l'outil d'ajout de PEI projet activé
      */
    handleMapClickAddPei() {
      var workingLayer = this.olMap.getLayerById('workingLayer');
      _.forEach(workingLayer.getSource().getFeatures(), feature => {
        if(feature.getGeometryName() === 'newPeiProjet') {
          this.peiProjetCoordonnees = feature.getGeometry().getCoordinates();
          this.$nextTick(() => {
            this.$bvModal.show("modalePeiProjet");
          });
        }
      })
      workingLayer.getSource().clear()
    }
  }
};
</script>

<style scoped>
.test {
  color: green !important;
}
</style>
