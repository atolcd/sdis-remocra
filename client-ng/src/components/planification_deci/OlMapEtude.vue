<template>
  <div>
  <OlMap ref="olMap" :couchesJSONPath="'/remocra/ext-res/layers/etude.json'" :couchesViewParams="couchesViewParams" >
  </OlMap>
  <ModalePeiProjet id="modalePeiProjet" :idEtude="parseInt(idEtude)" :coordonnees="peiProjetCoordonnees" :idHydrantProjet="selectedHydrantProjet"></ModalePeiProjet>
  <b-modal id="modaleDeletePeiProjet" title="Suppression d'un PEI projet" ok-title="Supprimer" cancel-title="Annuler" @ok="deleteHydrantProjet" ok-variant="danger">
    <p class="my-4">Etes-vous certain de vouloir supprimer ce PEI ?</p>
  </b-modal>
</div>


</template>

<script>
import OlMap from '../OlMap/OlMap.vue'
import ModalePeiProjet from './ModalePeiProjet.vue'
import _ from 'lodash'
import axios from 'axios'

import * as eventTypes from '../../bus/event-types.js'

import OlInteractionDraw from 'ol/interaction/Draw.js'
import * as OlProj from 'ol/proj'
import Point from 'ol/geom/Point'
import Feature from 'ol/Feature'
import Circle from 'ol/geom/Circle'
import {Style, Circle as CircleStyle, Stroke} from 'ol/style'
import Translate from 'ol/interaction/Translate'
import Collection from 'ol/Collection'

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
      interactionMovePei: null,
      peiProjetCoordonnees: null,
      featureMovePei: null,

      couchesViewParams: [{
        layer: "etude_hydrant_projet",
        value: "idEtude:"+this.idEtude
      }],

      // features sélectionnées (synchronisé avec la toolBar)
      selectedFeatures: [],
      selectedHydrantProjet: null
    }
  },

  created() {
      this.$root.$options.bus.$on(eventTypes.OLMAP_TOOLBAR_UPDATESELECTEDFEATURES, (features) => {
        this.selectedFeatures = features;
      });
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

      // Bouton d'ajout des hydrants projet
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

      // Bouton de modification des hydrants projet
      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        iconPath: "/remocra/static/img/application_view_columns.png",
        type: "button",
        title: "Modifier un pei",
        name: "modificationPei",
        onClick: () => {
          this.selectedHydrantProjet = this.selectedFeatures[0].properties.id;
          this.$nextTick(() => {
            this.$bvModal.show("modalePeiProjet");
          });
        },
        disabled: () => {
          return !(this.selectedFeatures.length == 1 && this.selectedFeatures[0].id.startsWith("etude_hydrant_projet"))
        }
      });

      // Bouton de déplacement des hydrants projet
      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        iconPath: "/remocra/static/img/move.png",
        type: "button",
        title: "Déplacer un pei",
        name: "deplacementPei",
        onClick: () => {
          this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_TOGGLEBUTTON, "deplacementPei");
        },
        onToggle: (state) => {
          if(state) {
            var layer = this.olMap.getLayerById('deplacementLayer');
            this.featureMovePei = new Feature(new Circle(
                OlProj.transform(this.selectedFeatures[0].geometry.coordinates, 'EPSG:2154', 'EPSG:3857'),
            ));

            this.featureMovePei.setStyle(new Style({
              geometry: () => { return new Point(OlProj.transform(this.selectedFeatures[0].geometry.coordinates, 'EPSG:2154', 'EPSG:3857'))},
              zIndex: 1000000,
              image: new CircleStyle({
                radius: 6,
                stroke: new Stroke({
                  color: 'white',
                  width: 0
                })
              }),
            }));
            layer.getSource().addFeatures([this.featureMovePei]);

            this.interactionMovePei = new Translate({
              features: new Collection([this.featureMovePei]),
              hitTolerance: 5
            });

            this.interactionMovePei.on("translateend", e => {
              var coords = OlProj.transform(e.features.getArray()[0].getGeometry().flatCoordinates, 'EPSG:3857', 'EPSG:2154');
              var formData = new FormData();
              formData.append("peiProjet", JSON.stringify({
                id: this.selectedFeatures[0].properties.id,
                longitude: coords[0],
                latitude: coords[1]
              }));
              axios.post('/remocra/etudehydrantprojet/updategeometrie', formData, {
                headers: {
                  'Content-Type': 'multipart/form-data'
                }
              }).catch(() => {
                this.$notify({
                  group: 'remocra',
                  type: 'error',
                  title: 'Envoi des données',
                  text: "L'enregistrement de la nouvelle position du PEI projet a échoué"
                });
              }).then(() => {
                this.$root.$options.bus.$emit(eventTypes.OLMAP_ONSELECTFEATURES, null, true); // On vide la sélection
                this.$root.$options.bus.$emit(eventTypes.OLMAP_COUCHES_REFRESHLAYER, 'etude_hydrant_projet'); // refresh layer
                this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_TOGGLEBUTTON, "deplacementPei"); // Désactivation des contrôles
              })
            });

            this.olMap.map.addInteraction(this.interactionMovePei);
          } else {
            this.olMap.map.removeInteraction(this.interactionMovePei);
            this.interactionMovePei = null;
            this.olMap.getLayerById('deplacementLayer').getSource().clear();
            this.featureMovePei = null;
          }
        },
        disabled: () => {
          return !(this.selectedFeatures.length == 1 && this.selectedFeatures[0].id.startsWith("etude_hydrant_projet"))
        }
      });

      // Bouton de suppression des hydrants projet
      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        iconPath: "/remocra/static/img/decline.png",
        type: "button",
        title: "Supprimer un pei",
        name: "supressionPei",
        onClick: () => {
          this.$bvModal.show("modaleDeletePeiProjet");
        },
        disabled: () => {
          return !(this.selectedFeatures.length == 1 && this.selectedFeatures[0].id.startsWith("etude_hydrant_projet")) || this.interactionMovePei !== null
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
      this.selectedHydrantProjet = null;
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
    },

    /**
    * Clic sur la carte avec l'outil de déplacement de PEI projet activé
    */
    handleMapClickMovePei() {
      var workingLayer = this.olMap.getLayerById('workingLayer');
      _.forEach(workingLayer.getSource().getFeatures(), feature => {
        if(feature.getGeometryName() === 'newPeiPosition') {
          var coords = OlProj.transform(feature.getGeometry().getCoordinates(), 'EPSG:3857', 'EPSG:2154');
          var formData = new FormData();
          formData.append("peiProjet", JSON.stringify({
            id: this.selectedFeatures[0].properties.id,
            longitude: coords[0],
            latitude: coords[1]
          }));
          axios.post('/remocra/etudehydrantprojet/updategeometrie', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }).catch(() => {
            this.$notify({
              group: 'remocra',
              type: 'error',
              title: 'Envoi des données',
              text: "L'enregistrement de la nouvelle position du PEI projet a échoué"
            });
          }).then(() => {
            this.$root.$options.bus.$emit(eventTypes.OLMAP_ONSELECTFEATURES, null, true); // On vide la sélection
            this.$root.$options.bus.$emit(eventTypes.OLMAP_COUCHES_REFRESHLAYER, 'etude_hydrant_projet'); // refresh layer
            this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_TOGGLEBUTTON, "deplacementPei"); // Désactivation des contrôles
          })
        }
      })
      workingLayer.getSource().clear()
    },

    deleteHydrantProjet() {
      axios.delete('/remocra/etudehydrantprojet/', {
        data: {
          id: this.selectedFeatures[0].properties.id
        }
      }).catch(() => {
        this.$notify({
          group: 'remocra',
          type: 'error',
          title: 'Erreur suppression',
          text: "La suppression du PEI projet a échouée"
        });
      }).then(() => {
        this.$nextTick(() => {
          this.$bvModal.hide("modaleDeletePeiProjet");
          this.$root.$options.bus.$emit(eventTypes.OLMAP_ONSELECTFEATURES, null, true);
          this.$root.$options.bus.$emit(eventTypes.OLMAP_COUCHES_REFRESHLAYER, 'etude_hydrant_projet');
        })
      })
    }
  }
};
</script>

<style scoped>
.test {
  color: green !important;
}
</style>
