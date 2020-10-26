<template>
  <div>
  <OlMap ref="olMap" :couchesJSONPath="'/remocra/ext-res/layers/etude.json'" :couchesViewParams="couchesViewParams">
    <template #specifique>
      <div class="text-center">
        <b-spinner v-if="spinnerMap" id="spinner" variant="primary"></b-spinner>
      </div>

      <ModalePeiProjet id="modalePeiProjet" :idEtude="parseInt(idEtude)" :coordonnees="peiProjetCoordonnees" :idHydrantProjet="selectedHydrantProjet"></ModalePeiProjet>

  <Process ref="process" :categorieProcess="'COUVERTURE_HYDRAULIQUE'"></Process>

      <b-modal id="modaleDeletePeiProjet" title="Suppression d'un PEI projet" ok-title="Supprimer" cancel-title="Annuler" @ok="deleteHydrantProjet" ok-variant="danger">
        <p class="my-4">Etes-vous certain de vouloir supprimer ce PEI ?</p>
      </b-modal>

      <b-modal id="modaleChoixReseau"
              title="Choix du reseau"
              ok-title="Commun"
              cancel-title="Importé"
              ok-variant="primary"
              cancel-variant="primary"
              @ok="calculCouvertureHydraulique(false)"
              @cancel="calculCouvertureHydraulique(true)"
              centered
              no-close-on-backdrop>
        <p class="my-4 modalContent">Souhaitez-vous calculer la couverture hydraulique de cet hydrant sur le réseau routier commun ou sur le réseau routier précédemment importé ?</p>
      </b-modal>
    </template>
  </OlMap>
</div>


</template>

<script>
import OlMap from '../OlMap/OlMap.vue'
import ModalePeiProjet from './ModalePeiProjet.vue'
import Process from '../Process.vue'
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
    ModalePeiProjet,
    Process
  },

  props: {
    idEtude: {
      required: true
    },

    reseauImporte: {
      type: Boolean
    },
  },

  data() {
    return {
      olMap : null,
      toolBar: null,

      processHiddenValues: [],

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
      selectedHydrantProjet: null,
      spinnerMap: false,
      disableToolbar: false
    }
  },

  created() {
      this.$root.$options.bus.$on(eventTypes.OLMAP_TOOLBAR_UPDATESELECTEDFEATURES, (features) => {
        this.selectedFeatures = features;
      });
      this.$root.$options.bus.$on(eventTypes.OLMAP_COUCHES_ONLAYERSLOADED, this.onLayersLoaded);
  },

  destroyed() {
      this.$root.$options.bus.$off(eventTypes.OLMAP_COUCHES_ONLAYERSLOADED);
  },

  mounted: function() {
    this.olMap = this.$refs['olMap'];
    this.toolBar = this.$refs['olMap'].$refs['toolBar'];
    this.processHiddenValues["ID_OBJET"] = this.idEtude;
    this.$nextTick(() => {
      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        type: "separator",
      });

      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        type: "libelle",
        text: "PEI en projet"
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

        },
        disabled: () => {
          return this.disableToolbar;
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
          return !(this.selectedFeatures.length == 1 && this.selectedFeatures[0].id.startsWith("etude_hydrant_projet")) || this.disableToolbar
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
          return !(this.selectedFeatures.length == 1 && this.selectedFeatures[0].id.startsWith("etude_hydrant_projet")) || this.disableToolbar
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
          return !(this.selectedFeatures.length == 1 && this.selectedFeatures[0].id.startsWith("etude_hydrant_projet")) || this.interactionMovePei !== null || this.disableToolbar
        }
      });

      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        type: "separator",
      });

      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        type: "libelle",
        text: "Simulation"
      });

      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        iconPath: "/remocra/static/img/create.png",
        type: "button",
        title: "lancer une simulation",
        name: "startSimulation",
        onClick: () => {
          if(this.reseauImporte) {
            this.$bvModal.show("modaleChoixReseau");
          } else {
            this.calculCouvertureHydraulique(false)
          }
        },
        disabled: () => {
          if(this.disableToolbar) {
            return true;
          }

          var disabled = this.selectedFeatures.length == 0 || this.interactionMovePei !== null;
          _.forEach(this.selectedFeatures, f => {
            if(!f.id.startsWith("etude_hydrant_projet") && !f.id.startsWith("v_hydrant_pibi") && !f.id.startsWith("v_hydrant_pena")) {
              disabled = true;
            }
          })
          return disabled;
        }
      });

      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        iconPath: "/remocra/static/img/delete.png",
        type: "button",
        title: "Effacer la simulation",
        name: "deleteSimulation",
        onClick: () => {
          axios.delete('/remocra/couverturehydraulique/', {
            data: this.idEtude
          }, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }).then(() => {
            this.$root.$options.bus.$emit(eventTypes.OLMAP_COUCHES_REFRESHLAYER);
          });
        },
        disabled: () => {
          return this.disableToolbar;
        }
      });

      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        type: "separator",
      });

      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        iconPath: "/remocra/static/img/cog.png",
        type: "button",
        title: "Lancer un traitement",
        name: "lancerTraitement",
        onClick: () => {
          this.$refs.process.showModal(this.processHiddenValues);
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
    },

    onLayersLoaded(layers) {
      _.forEach(layers, l => {
        // Une fois les couches chargées, on ajoute un viewParam sur les couches dépendantes de l'id de l'étude active
        if(l.get('viewParamCode') === "etude") {
          this.couchesViewParams.push({
            layer: l.get('code'),
            value: "idEtude:"+this.idEtude
          });
        }
      })
    },

    calculCouvertureHydraulique(reseauImporte) {
      this.spinnerMap = true;
      this.disableToolbar = true;
      var hydrants = _.map(this.selectedFeatures.filter(f => f.id.startsWith("v_hydrant_pibi") || f.id.startsWith("v_hydrant_penai")),'properties.id');
      var projets = _.map(this.selectedFeatures.filter(f => f.id.startsWith("etude_hydrant_projet")), 'properties.id');

      var formData = new FormData();

      formData.append("hydrantsExistants", JSON.stringify({hydrants}));
      formData.append("hydrantsProjet", JSON.stringify({projets}));
      formData.append("etude", this.idEtude);
      formData.append("reseauImporte", reseauImporte);

      axios.post('/remocra/couverturehydraulique/calcul', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(() => {
        this.disableToolbar = false;
        this.spinnerMap = false;
        this.$nextTick(() => {
          this.$root.$options.bus.$emit(eventTypes.OLMAP_COUCHES_REFRESHLAYER);
        });
      }).catch(() => {
        this.disableToolbar = false;
        this.spinnerMap = false;
      });
    }
  }
};
</script>

<style scoped>
#spinner {
  position: absolute;
  z-index: 100000;
  top: 50%;
}

.modalContent {
  font-size: 14px;
  text-indent: 5%;
  font-family: Segoe UI,Roboto,Helvetica,Arial;
}
</style>
