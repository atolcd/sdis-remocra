<template>
<div id="couches" class="couchesContainer">
  <b-card
    style="max-width: 20rem;"
    class="mb-2"
    id="cardCouches"
  >
    <b-card-header class="bg-transparent">
      <p v-b-toggle.accordionCouches>Couches</p>
    </b-card-header>
    <b-card-text>
      <b-collapse id="accordionCouches" visible role="tabpanel" accordion="accordionCouchesLegende">
        <div v-for="(groupe, indexGroup) in layersGroups" :key="indexGroup">
          <p class="layerGroup">{{groupe}}</p>
          <div v-for="(layer, index) in getSortedLayers(groupe)" :key="index" :class="{'selectedLayer':layer.get('code') == coucheActive}">
            <div class="layerItem">
              <div>
                <b-form-checkbox @change="layer.set('visible', $event)" :checked="layer.get('visible')">
                  <span @click="onSelectedCouche($event, layer)">{{layer.get('libelle')}}</span>
                </b-form-checkbox>
              </div>
              <div>
                <b-button :id="'tooltip-couche'+layer.get('code')" size="sm" class="layerItemButton">
                  <img src="../../assets/img/cog.png" width="16" height="16"/>
                </b-button>
              </div>
            </div>

            <b-tooltip :target="'tooltip-couche'+layer.get('code')" triggers="click blur" placement="bottom" class="tooltip-couche">
              Opacité: {{Math.round(layer.get('opacity')*100)}}%
              <div class="slidecontainer">
                <input type="range" min="0" max="1" step="0.01" @input="layer.setOpacity(Number($event.explicitOriginalTarget.value));">
              </div>
              <b-form-checkbox v-if="layer.get('legende')" @change="layer.get('legende').visible = $event" :checked="layer.get('legende').visible">
                Légende
              </b-form-checkbox>
            </b-tooltip>
          </div>
        </div>
      </b-collapse>

    </b-card-text>
  </b-card>

  <b-card
      style="max-width: 20rem;"
      class="mb-2"
      id="cardLegende"
    >
    <b-card-header class="bg-transparent">
      <p v-b-toggle.accordionLegende>Légende</p>
    </b-card-header>
    <b-card-text>
      <b-collapse id="accordionLegende" role="tabpanel" accordion="accordionCouchesLegende">
        <div v-for="(groupe, indexGroup) in legendeLayersGroup" :key="indexGroup">
          <p class="layerGroup">{{groupe}}</p>
          <div v-for="(layer, index) in getSortedLayers(groupe)" :key="index" class="layerItemLegende">
            <div v-if="layer.get('legende') && layer.get('legende').visible">
              <div v-if="layer.get('legende').type == 'getLegendGraphic'" class='legendeItem'>
                <img :src="'/remocra/geoserver/remocra/wms?REQUEST=GetLegendGraphic&VERSION=1.0.0&FORMAT=image/png&WIDTH=20&HEIGHT=20&LAYER='+layer.get('layer')" alt="legende"/>
                <p class="legendeLibelle">{{layer.get("libelle")}}</p>
              </div>

              <div v-if="layer.get('legende') && layer.get('legende').type == 'url'" class='legendeItem'>
                <img :src="'/remocra/ext-res/layers/legendes/'+layer.get('legende').src" alt="legende" />
                <p class="legendeLibelle" v-if="layer.get('legende').libelle != false">{{layer.get("libelle")}}</p>
              </div>
            </div>
          </div>
        </div>
      </b-collapse>
    </b-card-text>
  </b-card>
</div>
</template>

<script>
import _ from 'lodash'
import axios from 'axios'

import * as eventTypes from '../../bus/event-types.js'
import OlLayerVector from 'ol/layer/Vector.js'
import OlSourceVector from 'ol/source/Vector.js'
import ImageLayer from 'ol/layer/Image.js'
import ImageWMS from 'ol/source/ImageWMS.js'
import TileLayer from 'ol/layer/Tile.js'
import WMTSTileGrid from 'ol/tilegrid/WMTS';
import {get as getProjection} from 'ol/proj';
import {getWidth} from 'ol/extent';
import WMTS from 'ol/source/WMTS';
import OSM from 'ol/source/OSM.js'
import GeoJSON from 'ol/format/GeoJSON.js'
import * as OlProj from 'ol/proj'

import {
  Stroke,
  Style
} from 'ol/style.js'
export default {
  name: 'Couches',

  props: {
    map: {
      required: true,
      type: Object
    },

    cleIgn: {
      type: String,
      required: false
    },

    couchesJSONPath: {
      required: true,
      type: String
    },

    couchesViewParams: {
      required: false,
      type: Array
    }
  },
  data() {
    return {
      jsonData: null,
      layers: [],
      layersGroups: [],
      coucheActive: null
    }
  },

  watch: {

    // Modification des viewparams d'une couche après sa création
    couchesViewParams: function(newValue) {
      _.forEach(newValue, viewParam => {
        var layer = this.layers.filter(f => f.get('code') == viewParam.layer)[0];
        if(layer.get('wms_layer')) {
          layer.getSource().updateParams({
            VIEWPARAMS: viewParam.value
          });
        }
      })
    }
  },

  computed: {
    /**
      * Retourne les groupes de couches à afficher dans la Légende
      * Si un groupe ne contient pas au moins une couche à afficher (ex: fonds de plans), il n'est pas retourné par la fonction
      */
    legendeLayersGroup: function() {
      var groupes = [];
      _.forEach(this.layersGroups, groupe => {
        var show = false;
        _.forEach(this.getSortedLayers(groupe), layer => {
          if(layer.get('legende')) {
            show = true;
          }
        });
        if(show) {
          groupes.push(groupe);
        }
      });
      return groupes;
    }
  },

  created() {
    this.$root.$options.bus.$on(eventTypes.OLMAP_COUCHES_ADDLAYER, this.addLayer);
    this.$root.$options.bus.$on(eventTypes.OLMAP_COUCHES_GETFEATURESFROMPOINT, this.getFeaturesFromPoint);
    this.$root.$options.bus.$on(eventTypes.OLMAP_COUCHES_GETFEATURESFROMBBOX, this.getFeaturesFromBBOX);
    this.$root.$options.bus.$on(eventTypes.OLMAP_COUCHES_REFRESHLAYER, this.refreshLayer);
  },

  destroyed() {
    this.$root.$options.bus.$off(eventTypes.OLMAP_COUCHES_ADDLAYER);
    this.$root.$options.bus.$off(eventTypes.OLMAP_COUCHES_GETFEATURESFROMPOINT);
    this.$root.$options.bus.$off(eventTypes.OLMAP_COUCHES_GETFEATURESFROMBBOX);
    this.$root.$options.bus.$off(eventTypes.OLMAP_COUCHES_REFRESHLAYER);
  },

  mounted: function() {
    //this.dragElement(document.getElementById("cardCouche"));
    this.map.addLayer(this.createWorkingLayer('workingLayer'));
    this.map.addLayer(this.createWorkingLayer('selectionLayer'));

    axios.get(this.couchesJSONPath).then(response => {
      if(response) {
        var jsonData = response.data;
        _.forEach(jsonData, layer => {
          this.addLayer(layer);
        })
      }
    }).then(() => {
      this.map.addLayer(this.createWorkingLayer('deplacementLayer'));
    }).then(() => {
      this.$root.$options.bus.$emit(eventTypes.OLMAP_COUCHES_ONLAYERSLOADED, this.layers);
    });
  },

  methods: {

    dragElement(elmnt) {
      var pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
      elmnt.onmousedown = dragMouseDown;

      function dragMouseDown(e) {
        e = e || window.event;
        e.preventDefault();
        pos3 = e.clientX;
        pos4 = e.clientY;
        document.onmouseup = closeDragElement;
        document.onmousemove = elementDrag;
      }

      function closeDragElement() {
        document.onmouseup = null;
        document.onmousemove = null;
      }

      function elementDrag(e) {
        e = e || window.event;
        e.preventDefault();
        // calculate the new cursor position:
        pos1 = pos3 - e.clientX;
        pos2 = pos4 - e.clientY;
        pos3 = e.clientX;
        pos4 = e.clientY;

        var map = document.getElementById("map");

        var positionTop = (elmnt.offsetTop - pos2);
        var positionLeft = (elmnt.offsetLeft - pos1);

        if(map.offsetLeft > positionLeft) {
          positionLeft = map.offsetLeft
        }

        if(map.offsetLeft + map.offsetWidth < elmnt.offsetLeft + elmnt.offsetWidth) {
          positionLeft = map.offsetLeft + map.offsetWidth - elmnt.offsetWidth;
        }

        elmnt.style.top = positionTop + "px";
        elmnt.style.left = positionLeft + "px";
      }
    },

    // Retourne la liste des couches triées par z-index au sein d'un groupe de couches
    getSortedLayers(groupe) {
      var sortedLayers = this.layers.filter(o => o.get('groupe') == groupe);
      sortedLayers.sort(function(a, b){return a.get('zIndex') < b.get('zIndex')});
      return sortedLayers;
    },

    addLayer(layerDef) {
      var layer = null;
      if(layerDef.type == "wms") {
        layer = this.createWMSLayer(layerDef);
      } else if(layerDef.type == "ign") {
        layer = this.createIgnlayer(layerDef);
      } else if(layerDef.type == "osm") {
        layer = this.createOSMLayer(layerDef);
      } else if(layerDef.type == "wfs") {
        layer = this.createWFSLayer(layerDef);
      }

      if(layer) {
        this.map.addLayer(layer);
        this.layers.push(layer);
        if(layer.get('groupe') && _.indexOf(this.layersGroups, layer.get('groupe')) == -1) {
          this.layersGroups.push(layer.get('groupe'));
        }
        if(layer.getSource().updateParams) {
          layer.getSource().updateParams({
            time: Date.now()
          })
        }
      }

    },

    createWMSLayer(layerDef) {
      let crossOriginValue = layerDef.url.indexOf(window.document.location.hostname) !== -1 ? 'use-credentials' : 'anonymous'
      var viewParams = _.find(this.couchesViewParams, f => f.layer == layerDef.id);
      var wmsLayer = new ImageLayer({
        source: new ImageWMS({
          url: layerDef.url,
          crossOrigin: crossOriginValue,
          params: {
            LAYERS: layerDef.layers,
            VIEWPARAMS: (viewParams) ? viewParams.value : null
          }
        }),
        code: layerDef.id,
        extent: this.map.getView().getProjection().getExtent(),
        opacity: layerDef.opacity,
        visible: layerDef.visibility,
        minResolution: layerDef.scale_min,
        maxResolution: layerDef.scale_max,
        wms_layer: true,
        libelle: layerDef.libelle,
        legende: layerDef.legende,
        layer: layerDef.layers,
        groupe: layerDef.groupe,
        zIndex: layerDef.zIndex,
        viewParamCode: layerDef.viewParamCode,
        properties: layerDef.properties
      });
      return wmsLayer
    },

    createWFSLayer(layerDef) {
      var sourceWFS = new OlSourceVector({
        url: layerDef.url+'?service=WFS&' +
        'version=1.1.0&request=GetFeature&typename='+ layerDef.layer +
        '&outputFormat=application/json',
        format: new GeoJSON(),
        serverType: 'geoserver'
      });

      return new OlLayerVector({
        source: sourceWFS,
        code: layerDef.id,
        libelle: layerDef.libelle,
        legende: layerDef.legende,
        layer: layerDef.layers,
        groupe: layerDef.groupe,
        zIndex: layerDef.zIndex,
        properties: layerDef.properties
      });
    },

    // Création du fond de carte OpenStreetMap
    createOSMLayer(layerDef) {
      return new TileLayer({
        source: new OSM({
          url: layerDef.url
        }),
        libelle: layerDef.libelle,
        groupe: layerDef.groupe,
        code: layerDef.id,
        zIndex: layerDef.zIndex,
        properties: layerDef.properties,
        visible: layerDef.visibility,
      });
    },

    // Création du fond de carte IGN
    createIgnlayer(layerDef) {
      var resolutions = [];
      var matrixIds = [];
      var proj3857 = getProjection('EPSG:3857');
      var maxResolution = getWidth(proj3857.getExtent()) / 256;

      for (var i = 0; i < 18; i++) {
        matrixIds[i] = i.toString();
        resolutions[i] = maxResolution / Math.pow(2, i);
      }
      var tileGrid = new WMTSTileGrid({
        origin: [-20037508, 20037508],
        resolutions: resolutions,
        matrixIds: matrixIds
      });

      var IGNLayer = new TileLayer({
        source: new WMTS({
          url: layerDef.urlBase,
          layer: layerDef.ignLayer,
          matrixSet: 'PM',
          format: layerDef.format !== null ? layerDef.format : 'image/jpeg',
          projection: 'EPSG:3857',
          tileGrid: tileGrid,
          style: 'normal'
        }),
        libelle: layerDef.libelle,
        groupe: layerDef.groupe,
        code: layerDef.id,
        zIndex: layerDef.zIndex,
        properties: layerDef.properties
      });
      return IGNLayer;
    },

    createWorkingLayer(code) {
      var source = new OlSourceVector()
      var style = new Style({
        stroke: new Stroke({
          color: 'blue',
          width: 2
        })
      })
      var vectorLayer = new OlLayerVector({
        name: code,
        code: code,
        source: source,
        style: style,
        visibility: true,
        opacity: 1,
        zIndex: 1000,
        id: code
      })
      return vectorLayer
    },

    onSelectedCouche(event, layer) {
      event.preventDefault();
      if(this.coucheActive != layer.get('code')) {
        this.coucheActive = layer.get('code');
      } else {
        this.coucheActive = null;
      }
      this.$root.$options.bus.$emit(eventTypes.OLMAP_COUCHES_UPDATECOUCHEACTIVE, this.coucheActive);
    },

    /**
      * Retourne la feature située à un point donné
      * @param evtRetour L'évènement auquel renvoyer le résultat
      * @param coordonnées Les coordonnées du point
      */
    getFeaturesFromPoint(evtRetour, coordonnees) {
      coordonnees = OlProj.transform(coordonnees, 'EPSG:3857', 'EPSG:2154');
      var radius = Math.abs(20-this.map.getView().getZoom())
      var layer = _.find(this.layers, l => l.get('code') == this.coucheActive);
      var viewParams = _.find(this.couchesViewParams, f => f.layer == layer.get('code'));
      axios.get('/remocra/geoserver/remocra/wfs', {
        params: {
          service: 'wfs',
          version: '2.0.0',
          request: 'GetFeature',
          typeNames: layer.get('layer'),
          outputFormat: 'application/json',
          cql_filter: ("DWithin(geometrie,POINT("+coordonnees[0]+" "+coordonnees[1]+"),"+radius+", meters)"),
          viewparams: (viewParams) ? viewParams.value : null
        }
      }).then(response => {
        if(response.data && response.data.totalFeatures > 0) {
          this.$root.$options.bus.$emit(evtRetour, response.data.features);
        }
      }).catch(() => {
        this.$notify({
          group: 'remocra',
          type: 'error',
          title: 'Récupération des données',
          text: "Requête WFS: impossible de récupérer les features à cet endroit"
        });
      })
    },

    getFeaturesFromBBOX(evtRetour, bbox) {
      bbox = OlProj.transformExtent(bbox, 'EPSG:3857', 'EPSG:2154');
      var layer = _.find(this.layers, l => l.get('code') == this.coucheActive);
      var viewParams = _.find(this.couchesViewParams, f => f.layer == layer.get('code'));
      axios.get('/remocra/geoserver/remocra/wfs', {
        params: {
          service: 'wfs',
          version: '2.0.0',
          request: 'GetFeature',
          typeNames: layer.get('layer'),
          outputFormat: 'application/json',
          bbox: bbox.toString(),
          viewparams: (viewParams) ? viewParams.value : null
        }
      }).then(response => {
        if(response.data.totalFeatures > 0) {
          this.$root.$options.bus.$emit(evtRetour, response.data.features);
        }
      })
    },

    /**
      * Met à jour les couches
      * Lié à @event OLMAP_COUCHES_REFRESHLAYER
      * @param layerCode Le code de la couche à mettre à jour. Si non fourni, toutes les couches seront mises à jour
      */
    refreshLayer(layerCode) {
      if(layerCode) {
        var layer = _.find(this.layers, l => l.get('code') == layerCode);
        if(layer) {
          layer.getSource().updateParams({
            time: Date.now()
          });
        }
      } else {
        _.forEach(this.layers, l => {
          if(l.getSource && l.getSource().updateParams) {
            l.getSource().updateParams({
              time: Date.now()
            });
          }
        })
      }
    }
  }
}
</script>

<style scoped>
.couchesContainer {
  width: 200px;
  padding: 5px;

  position: absolute;
  right: 0%;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1;
  display: inline-flex;
  flex-direction: column;
  max-height: 85%;
  overflow-y: scroll;
}

#cardCouches, #cardLegende {
  overflow-y: scroll;
  overflow-x: clip;
  max-height: 500px !important;
  overscroll-behavior: contain;
}

.card-header {
  padding: 0 0 0 1.25rem;
  font-weight: 500;
  line-height: 1.2;
  font-size: 1.5rem;
  border-bottom: none;
}

.card-body {
  padding-right: 0;
  padding-left: 0;
}

.layerGroup {
  font-weight: bold;
}
.layerGroup, .legendeLibelle {
  margin-bottom: 0px;
  margin-left: 8px;
  margin-top: 10px;
}
.layerItem {
  display: flex;
  margin-left: 38px;
  font-size: 14px;
  font-weight: 400;
}

.selectedLayer {
  background-color: rgb(200,200,200);
}

.layerItem .custom-checkbox {
  padding-top: 5px;
}

.layerItem .custom-checkbox .custom-control-label {
  top: 5px !important;
}
.layerItem div:last-child {
  margin-left: auto;
  padding-left: 5px;
}

.layerItemButton {
  background-color: transparent;
  border-color: transparent;
}

.tooltip-couche {
  color: red !important;
}

#accordionLegende img {
  margin-left: 10px;
  padding-right: 10px;
  overflow-x: scroll;
}

.legendeItem {
  display: flex;
  align-items: center;
}

.legendeLibelle {
  margin-bottom: 0;
  font-size: 10px;
  transform: translate(0, -3px);
}

.slidecontainer {
  width: 100%;
}

.layerItemLegende {
  padding-left: 10px;
}
</style>
