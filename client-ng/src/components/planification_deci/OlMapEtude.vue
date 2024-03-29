<template>
  <div>
  <OlMap ref="olMap" :cleIgn="cleIgn" :couchesJSONPath="'/remocra/ext-res/layers/etude.json'" :couchesViewParams="couchesViewParams">
    <template #specifique>
      <div class="text-center">
        <b-spinner v-if="spinnerMap" id="spinner" variant="primary"></b-spinner>
      </div>

      <ModalePeiProjet ref="modalePeiProjet" id="modalePeiProjet" :idEtude="parseInt(idEtude)" :coordonnees="peiProjetCoordonnees" :idHydrantProjet="idHydrantProjet" :srid="srid">
      </ModalePeiProjet>

      <Process ref="process" :categorieProcess=categorieProcess></Process>

      <b-modal id="modaleDeletePeiProjet" title="Suppression d'un PEI projet" ok-title="Supprimer" cancel-title="Annuler" @ok="deleteHydrantProjet" ok-variant="danger">
        <p class="my-4">Etes-vous certain de vouloir supprimer ce PEI ?</p>
      </b-modal>

      <b-modal id="modaleChoixReseau"
              title="Choix du reseau"
              hide-footer="true"
              centered
              no-close-on-backdrop>
        <p class="my-4 modalContent">Souhaitez-vous calculer la couverture hydraulique de cet hydrant sur le réseau routier commun ou sur le réseau routier précédemment importé ?</p>
        <div class="buttonChoixReseau row">
            <b-button class="btn btn-outline-primary" variant="primary" @click="calculCouvertureHydraulique(false)">Commun</b-button>
            <b-button class="btn btn-outline-primary" variant="primary" @click="calculCouvertureHydraulique(true)">Importé</b-button>
            <b-button class="btn btn-outline-primary" variant="primary" @click="calculCouvertureHydraulique(true, true)">Commun et importé</b-button>
        </div>
      </b-modal>
    </template>
  </OlMap>
</div>


</template>

<script>
import OlMap from '../OlMap/OlMap.vue'
import ModalePeiProjet from './ModalePeiProjet.vue'
import Process from '../Process.vue'
import * as GlobalConstants from '../../GlobalConstants.js'
import _ from 'lodash'
import axios from 'axios'

import * as eventTypes from '../../bus/event-types.js'

import OlInteractionDraw from 'ol/interaction/Draw.js'
import * as OlProj from 'ol/proj'
import Point from 'ol/geom/Point'
import Feature from 'ol/Feature'
import MultiLineString from 'ol/geom/MultiLineString';
import Circle from 'ol/geom/Circle'
import {Style, Circle as CircleStyle, Stroke, Text, Fill} from 'ol/style'
import Translate from 'ol/interaction/Translate'
import Collection from 'ol/Collection'
import WKT from 'ol/format/WKT'
import { getSrid } from '../utils/FunctionsUtils.js'

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

    bounds : {
      type: String,
      required: false
    },

    cleIgn: {
      type: String,
      required: false
    },

    // Booléen indiquant si il existe un réseau spécifique à cette étude
    reseauImporte: {
      type: Boolean
    },

    // Indique si l'étude est close. Dans ce cas, on désactive les contrôles pouvant modifier l'étude
    isClosed: {
      required: true,
      type: Boolean
    }
  },

  data() {
    return {
      olMap : null,
      toolBar: null,

      srid: null,
      
      categorieProcess: GlobalConstants.COUVERTURE_HYDRAULIQUE,
      idHydrantProjet: null,
      processHiddenValues: [],

      interactionAddPei: null,
      interactionMovePei: null,
      interactionClosestPei: null,
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
      disableToolbar: false,

      // Couches dont les features sont compatibles avec le calcul de la couverture hydraulique
      couchesCouvertureHydrauliqueCompatibles: []
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

  mounted: async function() {
    this.srid = await getSrid();

    this.olMap = this.$refs['olMap'];
    axios.get('/remocra/etudes/etendu/' + this.idEtude).then(response => {
      // Si on a une ou plusieurs communes, on zoom dessus
      if(response.data != null && response.data != ''){
        var sridBounds = response.data.split(';')
        var bounds = sridBounds[1]
        var feature = new WKT().readGeometry(bounds)
        this.extent = feature.getExtent()
        this.olMap.map.getView().fit(this.extent)
      } else if(this.bounds != null) {
        // Sinon, on prend en compte le bounds si on a une valeur
          var srid = this.bounds.split(";")[0];
          var extent = [];
          _.forEach((this.bounds.split(";")[1]).split(","), n => {
            extent.push(Number(n));
          });
          this.olMap.map.getView().fit(OlProj.transformExtent(extent, srid, 'EPSG:3857'));
      }
    });

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
        title: "Ajouter un pei",
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
          return this.disableToolbar || this.isClosed;
        }
      });

      // Bouton de modification des hydrants projet
      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, {
        iconPath: "/remocra/static/img/application_view_columns.png",
        type: "button",
        title: "Modifier un pei",
        name: "modificationPei",
        onClick: () => {
          // L'identifiant est de la forme etude_hydrant_projet.id, donc on split
          this.idHydrantProjet = this.selectedFeatures[0].id.split('.')[1];
          this.$nextTick(() => {
            this.$refs.modalePeiProjet.initModal(this.idHydrantProjet);
          });
        },
        disabled: () => {
          return !(this.selectedFeatures.length == 1 && this.selectedFeatures[0].id.startsWith("etude_hydrant_projet")) || this.disableToolbar || this.isClosed
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
                OlProj.transform(this.selectedFeatures[0].geometry.coordinates, 'EPSG:'+this.srid, 'EPSG:3857'),
            ));

            this.featureMovePei.setStyle(new Style({
              geometry: () => { return new Point(OlProj.transform(this.selectedFeatures[0].geometry.coordinates, 'EPSG:'+this.srid, 'EPSG:3857'))},
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
              var coords = OlProj.transform(e.features.getArray()[0].getGeometry().flatCoordinates, 'EPSG:3857', 'EPSG:'+this.srid);
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
          return !(this.selectedFeatures.length == 1 && this.selectedFeatures[0].id.startsWith("etude_hydrant_projet")) || this.disableToolbar || this.isClosed
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
          return !(this.selectedFeatures.length == 1 && this.selectedFeatures[0].id.startsWith("etude_hydrant_projet")) || this.interactionMovePei !== null || this.disableToolbar || this.isClosed
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
        iconPath: "/remocra/static/img/peiPlusProche.png",
        type: "button",
        title: "Trouver le PEI le plus proche",
        name: "peiPlusProche",
        onClick: () => {
          this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_TOGGLEBUTTON, "peiPlusProche");
        },
        onToggle: (state) => {
          var workingLayer = this.olMap.getLayerById('workingLayer');
          workingLayer.getSource().clear();
          if(state) {
            this.olMap.map.on("click", this.handleMapClickFindClosestPei);

            this.interactionClosestPei = new OlInteractionDraw({
              type: 'Point',
              source: workingLayer.getSource(),
              geometryName: "closestPei"
            });
            this.olMap.map.addInteraction(this.interactionClosestPei);
          } else {
            this.olMap.map.removeInteraction(this.interactionClosestPei);
            this.olMap.map.un("click", this.handleMapClickFindClosestPei);
          }

        },
        disabled: () => {
          return this.disableToolbar;
        }
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
          if(this.disableToolbar || this.isClosed) {
            return true;
          }

          var disabled = this.selectedFeatures.length == 0 || this.interactionMovePei !== null;
          _.forEach(this.selectedFeatures, f => {
            if(_.indexOf(this.couchesCouvertureHydrauliqueCompatibles, f.layer) == -1) {
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
          return this.disableToolbar || this.isClosed;
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
        },
        disabled: () => {
          return this.disableToolbar || this.isClosed;
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
            this.idHydrantProjet = null;
            this.$refs.modalePeiProjet.initModal(null);
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
          var coords = OlProj.transform(feature.getGeometry().getCoordinates(), 'EPSG:3857', 'EPSG:'+this.srid);
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

    /**
      * Clic sur la carte pour trouver le PEI le plus proche
      */
    handleMapClickFindClosestPei() {
      let workingLayer = this.olMap.getLayerById('workingLayer');
      _.forEach(workingLayer.getSource().getFeatures(), feature => {
        if(feature.getGeometryName() === 'closestPei') {
          let coordsClic = OlProj.transform(feature.getGeometry().getCoordinates(), 'EPSG:3857', 'EPSG:'+this.srid);
          let formData = new FormData();
          formData.append("data", JSON.stringify({
            idEtude: this.idEtude,
            longitude: coordsClic[0],
            latitude: coordsClic[1]
          }));
          axios.post('/remocra/couverturehydraulique/closestPei', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }).then(response => {
            if(response.data && response.data.data) {

              workingLayer.getSource().clear();
              let wktChemin = response.data.data.wktGeometrieChemin;
              let wktPei = response.data.data.wktGeometriePei;
              let distance = response.data.data.distance;

              // Affichage des features
              this.cheminPlusCourtFeaturePei(workingLayer, wktPei, distance);
              this.cheminPlusCourtFeatureChemin(workingLayer, wktChemin);
              this.cheminPlusCourtFeatureClic(workingLayer, coordsClic);
            }
          }).catch(() => {
            this.$notify({
              group: 'remocra',
              type: 'error',
              title: 'Calcul des données',
              text: "Impossible de trouver le PEI le plus proche"
            });
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

        /** On détermine sur quelles couchent les features peuvent êtres sélectionnées pour le calcul
        de la couverture hydraulique. La couche doit, dans ses propriétés,
        avoir la valeur "couverture_hydraulique_compatible" à TRUE **/
        if(l.get('properties') && l.get('properties').couverture_hydraulique_compatible) {
          this.couchesCouvertureHydrauliqueCompatibles.push(l.get('code'));
        }
      })
    },

    calculCouvertureHydraulique(reseauImporte, reseauImporteWithCourant = false) {
      this.spinnerMap = true;
      this.disableToolbar = true;

      var hydrants = [];
      var projets = [];

      // On détermine, selon la couche depuis laquelle elles sont sélectionnées, si les features représentent des
      // hydrants existants ou des hydrants projets. L'information est récupérable depuis les properties de la couche
      var layers = this.olMap.getLayers().filter(l => l.get('properties') && l.get('properties').couverture_hydraulique_compatible);
      _.forEach(this.selectedFeatures, f => {
        var layer = layers.filter(l => l.get('code') == f.layer)[0];
        if(layer.get('properties').couverture_hydraulique_type_pei == 'HYDRANT') {
          hydrants.push(f.properties.id);
        } else if(layer.get('properties').couverture_hydraulique_type_pei == 'PROJET') {
          projets.push(f.properties.id);
        }
      });

      var formData = new FormData();

      formData.append("hydrantsExistants", JSON.stringify({hydrants}));
      formData.append("hydrantsProjet", JSON.stringify({projets}));
      formData.append("etude", this.idEtude);
      formData.append("reseauImporte", reseauImporte);
      formData.append("reseauImporteWithCourant", reseauImporteWithCourant);

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

        this.$bvModal.hide("modaleChoixReseau");
      }).catch(() => {
        this.disableToolbar = false;
        this.spinnerMap = false;
      });
    },

    cheminPlusCourtFeaturePei(workingLayer, wktPei, distance) {
      var featurePei = (new WKT()).readFeature(wktPei, {
        dataProjection: 'EPSG:'+this.srid,
        featureProjection: 'EPSG:3857',
      });

      var circle = new Feature(new Circle(
          featurePei.get('geometry').flatCoordinates
      ));

      circle.setStyle(new Style({
        geometry: new Point(featurePei.get('geometry').flatCoordinates),
        image: new CircleStyle({
          radius: 12,
          stroke: new Stroke({
            color: '#f01f18',
            width: 3
          })
        }),
        text: new Text({
          font: '16px Calibri,sans-serif',
          overflow: true,
          fill: new Fill({
            color: 'white',
          }),
          text: Math.round(distance)+" m",
          offsetY: -20,
          stroke: new Stroke({color: 'black', width: 2})
        })
      }));

      workingLayer.getSource().addFeature(circle);
    },

    cheminPlusCourtFeatureChemin(workingLayer, wktChemin) {
      var featureChemin = (new WKT()).readFeature(wktChemin);
      var path = new Feature({
        geometry: new MultiLineString(featureChemin.getGeometry().getCoordinates()).transform('EPSG:'+this.srid,'EPSG:3857'),
        name: 'chemin'
      });

      path.setStyle(new Style({
        stroke: new Stroke({
          color: '#f01f18',
          width: 3
        })
      }));

      workingLayer.getSource().addFeature(path);
    },

    cheminPlusCourtFeatureClic(workingLayer, coordsClic) {
      let pointClic = new Feature(new Point(OlProj.transform(coordsClic, 'EPSG:'+this.srid, 'EPSG:3857')));
      pointClic.setStyle(new Style({
        geometry: new Point(OlProj.transform(coordsClic, 'EPSG:'+this.srid, 'EPSG:3857')),
        image: new CircleStyle({
          radius: 6,
          stroke: new Stroke({
            color: 'black',
            width: 2
          }),
          fill: new Fill({
            color: 'white'
          })
        })
      }));

      workingLayer.getSource().addFeature(pointClic);
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

.buttonChoixReseau {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}

.buttonChoixReseau .btn {
  background-color: transparent;
  margin-left: 10px;
}

.buttonChoixReseau .btn:hover {
  background-color: #007bff;
  color: white;
  margin-left: 10px;
}
</style>
