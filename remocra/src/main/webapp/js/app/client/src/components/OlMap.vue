<template>
<b-container id="xx" class="bv-example-row" style="max-width:100%">
  <b-row class="text-start">
    <b-col cols="2">
      <div class="text-start my-3">
        <b-btn class="ctrl" v-b-tooltip.hover title="Déplacer la carte"><img src="/static/img/pan.png"></b-btn>
          <b-btn class="ctrl" v-b-tooltip.hover title="Zoomer en avant"><img src="/static/img/magnifier_zoom_in.png"></b-btn>
            <b-btn class="ctrl" v-b-tooltip.hover title="Zoomer en arrière"><img src="/static/img/magnifier_zoom_out.png"></b-btn>
              <b-btn class="ctrl" v-b-tooltip.hover title="Rétablir la vue précédente"><img src="/static/img/zoom_prec.png"></b-btn>
                <b-btn class="ctrl" v-b-tooltip.hover title="Rétablir la vue suivante"><img src="/static/img/zoom_suiv.png"></b-btn>
      </div>
    </b-col>
    <b-col cols="2">
      <div class="text-start my-3">
        <search-commune ref='searchCommune'></search-commune>
      </div>
    </b-col>
    <b-col cols="2">
      <div class="text-start my-3">
        <search-repertoire ref='searchRepertoire'></search-repertoire>
      </div>
    </b-col>
    <div>
      <b-form-select v-model="selectedRuler" class="text-start my-3" @input="toggleDistance">
        <option>Distance</option>
        <option>Surface</option>
      </b-form-select>
    </div>
    <div class="text-start my-3">
      <b-btn class="ctrl" v-b-tooltip.hover title="Obtenir des information sur un point de la carte"><img src="/static/img/information.png"></b-btn>
    </div>
    <div class="text-start my-3">
      <b-btn class="ctrl" @click="showToolsBar" v-b-toggle.collapse1 title="Activer les outils d\'édition"><img src="/static/img/pencil.png"></b-btn>
    </div>
    <b-form-group class="text-start my-3">
      <b-form-radio-group id="btnradios2" buttons button-variant="outline-primary" v-model="modeAffichage" :options="modeAffichages" name="radioBtnOutline" />
    </b-form-group>
    <div class="text-start my-3">
      <b-btn class="ctrl" @click="GoInFullscreen" v-b-tooltip.hover title="Plein écran"><img src="/static/img/pan.png"></b-btn>
    </div>
  </b-row>
  <b-row class="text-start">
    <b-col>
      <div role="tablist">
      <new-evenement ref="newEvenement"></new-evenement>
        <b-card no-body class="mb-1">
          <b-card-header header-tag="header" class="p-1" role="tab">
            <b-btn block variant="info"><span href="#" v-b-toggle.accordion1>Evènements</span><span class="evenement">
              <b-btn @click="openNewEvenement" class="ctrl"><img src="/static/img/add.png"></b-btn>
              <b-btn  class="ctrl" id="popoverButton-open2"><img src="/static/img/icon_SpecifiedFilter.png"></b-btn>
                <b-popover  placement="right" ref="popover" target="popoverButton-open2" title="Filtrer les évènements">
                   <filters :criseId="criseId" ref="filters"></filters>
              </b-popover>
            </span></b-btn>
          </b-card-header>
          <b-collapse id="accordion1" visible accordion="my-accordion" role="tabpanel">
            <b-card-body>
              <p class="card-text">
              </p>
            <evenements :crise="criseId" ref="evenements"></evenements>
            </b-card-body>
          </b-collapse>
        </b-card>
        <b-card no-body class="mb-1">
          <b-card-header header-tag="header" class="p-1" role="tab">
            <b-btn block variant="info"><span href="#" v-b-toggle.accordion2>Documents</span><span class="document">
              <b-btn @click="addNewDocument" class="ctrl"><img src="/static/img/add.png"></b-btn></span></b-btn>
              <new-document ref="newDocument"></new-document>
          </b-card-header>
          <b-collapse id="accordion2" accordion="my-accordion" role="tabpanel">
            <b-card-body>
              <p class="card-text">
                <documents :crise="criseId" ref="evenements"></documents>
              </p>
            </b-card-body>
          </b-collapse>
        </b-card>
        <b-card no-body class="mb-1">
          <b-card-header header-tag="header" class="p-1" role="tab">
            <b-btn block href="#" v-b-toggle.accordion3 variant="info">Indicateurs</b-btn>
          </b-card-header>
          <b-collapse id="accordion3" accordion="my-accordion" role="tabpanel">
            <b-card-body>
              <p class="card-text">
                Indicateurs
              </p>
            </b-card-body>
          </b-collapse>
        </b-card>

        <b-card no-body class="mb-1">
          <b-card-header header-tag="header" class="p-1" role="tab">
            <b-btn block href="#" v-b-toggle.accordion4 variant="info">Recherche et analyse</b-btn>
          </b-card-header>
          <b-collapse id="accordion4" accordion="my-accordion" role="tabpanel">
            <b-card-body>
              <p class="card-text">
                Recherche et analyse
              </p>
            </b-card-body>
          </b-collapse>
        </b-card>

      </div>
    </b-col>
    <b-col cols="8">
      <b-row id="toolsBar" class="toolsBar">
        <b-col cols="8" class="text-start my-3">
          <b-btn class="ctrl" v-b-tooltip.hover title="Obtenir des information sur un point de la carte"><img src="/static/img/information.png"></b-btn>
        </b-col>
      </b-row>
      <div id="map">
        <div class="sidebar">
          <div id="layertree">
            <div v-for="(group,index) in legend.items" :key="index">
              <div class="groupe">{{group.libelle}}</div>
              <draggable :list="group.items" :options="{handle:'.my-handle'}" @start="drag=true" @end="addSortable()">
                <div v-for="(layer,index) in group.items" :key="index">
                  <div class="layer my-handle">
                    <input  type="checkbox" v-bind:id="'checkbox'+layer.id" :value="layer.visibility" v-model="layer.visibility" @click="changeLayerVisibility(layer.id)">
                    <label for="layer.id">{{layer.libelle}}</label>
                  </div>
                  <div>
                    <label>opacity</label>
                    <input type="range"  v-bind:id="'range'+layer.id"  v-model="layer.opacity" min="0" max="1" step="0.01" @change="changeLayerOpacity(layer.id)"/>
                </div>
                  </div>
              </draggable>
            </div>
          </div>
        </div>
      </div>
    </b-col>
    <b-col>
      <div role="tablist">
        <b-card no-body class="mb-1">
          <b-card-header header-tag="header" class="p-1" role="tab">
            <b-btn block href="#" v-b-toggle.accordion5 variant="info">Couches</b-btn>
          </b-card-header>
          <b-collapse id="accordion5" visible accordion="my-accordion2" role="tabpanel">
            <b-card-body>
              <p class="card-text">
                Couches
              </p>
            </b-card-body>
          </b-collapse>
        </b-card>
        <b-card no-body class="mb-1">
          <b-card-header header-tag="header" class="p-1" role="tab">
            <b-btn block href="#" v-b-toggle.accordion6 variant="info">Légende</b-btn>
          </b-card-header>
          <b-collapse id="accordion6" accordion="my-accordion2" role="tabpanel">
            <b-card-body>
              <p class="card-text">
                Légende
              </p>
            </b-card-body>
          </b-collapse>
        </b-card>
      </div>
    </b-col>
  </b-row>
</b-container>
</template>
<script>
/* eslint-disable */

import Map from 'ol/Map.js';
import View from 'ol/View.js';
import {defaults as defaultControls,FullScreen} from 'ol/control.js';
import {ScaleLine} from 'ol/control.js';
import {getWidth} from 'ol/extent.js';
import TileLayer from 'ol/layer/Tile.js';
import {fromLonLat, get as getProjection} from 'ol/proj.js';
import ImageLayer from 'ol/layer/Image.js';
import WMTS from 'ol/source/WMTS.js';
import OSM from 'ol/source/OSM.js';
import {defaults as defaultInteractions} from 'ol/interaction.js'
import ImageWMS from 'ol/source/ImageWMS.js';
import WMTSTileGrid from 'ol/tilegrid/WMTS.js';
import WKT from 'ol/format/WKT.js';
import axios from 'axios'
import legend from '../assets/carte-crise.json'
import Proj from 'ol/proj.js'
import _ from 'lodash'
import draggable from 'vuedraggable'
import SearchCommune from './SearchCommune.vue'
import SearchRepertoire from './SearchRepertoireLieu.vue'
import OlOverlay from 'ol/Overlay.js'
import {Vector as OlSourceVector } from 'ol/source.js'
import {Vector as OlLayerVector} from 'ol/layer.js'
import OlInteractionDraw from 'ol/interaction/Draw.js'
import {getArea, getLength} from 'ol/sphere.js';
import {Circle as CircleStyle, Fill, Stroke, Style} from 'ol/style.js';
import {LineString, Polygon} from 'ol/geom.js';
import NewEvenement from './NewEvenement.vue';
import NewDocument from './NewDocument.vue';
import Evenements from './Evenements.vue';
import Documents from './Documents.vue';
import Filters from './Filters.vue';

  export default {
    name: 'OlMap',
    components: {
           draggable,
           SearchCommune,
           SearchRepertoire,
           NewEvenement,
           NewDocument,
           Evenements,
           Documents,
           Filters   },
    data () {
      return {
        file: null,
        modalShow:false,
        selectedRuler:null,
        modeAffichage:'radio1',
        modeAffichages: [
        { text: 'Opérationnel', value: 'radio1' },
        { text: 'Anticipation', value: 'radio2' }],
        criseId: window.location.hash.substr(16),
        //todo mettre le var par défaut?
        extent: [256805.64470225616, 6249216.947446961, 265705.78118321137, 6252690.054919669],
        legend,
        //todo  à factoriser
        ignKey: 'fjwf53vbh2ikn9q009g6mi7f',
        map: {
        type: Object,
        default: {}
      },
        resolutions : [156543.03392804103, 78271.5169640205, 39135.75848201024, 19567.879241005125, 9783.939620502562, 4891.969810251281, 2445.9849051256406,
                         1222.9924525628203, 611.4962262814101, 305.74811314070485, 152.87405657035254, 76.43702828517625, 38.218514142588134, 19.109257071294063,
                         9.554628535647034, 4.777314267823517, 2.3886571339117584, 1.1943285669558792, 0.5971642834779396, 0.29858214173896974, 0.14929107086948493,
                         0.07464553543474241 ]

      }
    },
    mounted() {
      this.map = new Map({
            controls: [],
            target: 'map',
            layers: [],
            controls: defaultControls({
            rotate: false,
            zoom: true,

            attribution: false,
            attributionOptions: {
              collapsible: false
            }
          }),
            view: new View({
                projection : 'EPSG:3857',
                center: [0, 0],
                zoom:2
            })
        });
        this.constructMap()
        //this.addSortable()

    },
    updated() {
      //this.addSortable()
    },
    methods : {
      addNewDocument(){
        this.$refs['newDocument'].showModal(this.criseId);
      },
      openNewEvenement(){
        this.$refs['newEvenement'].showModal(this.criseId, null);
      },
      addSortable(){
         this.addLayersFromLayerConfig(this.legend);
      },
      changeLayerVisibility(id){
        var layer = this.getLayerById(id);
        var checkbox = document.getElementById("checkbox"+id);
        var newVisibility = !layer.getVisible();
        if (layer.setVisible) {
            layer.setVisible(newVisibility);
        } else {
            layer.visibility = newVisibility;
        }
      },

      changeLayerOpacity(id) {
        var range = document.getElementById("range"+id);
        var layer = this.getLayerById(id)
        layer.setOpacity(parseFloat(range.value))
      },

      constructMap() {
        axios.get('/remocra/crises/'+this.criseId+'/geometrie')
        .then((response) => {
              if (response.data) {
                  //on récupère l'extent (géometrie des commune de la crise)
                   var sridBounds = response.data.data.split(";");
                   var sridComplet = sridBounds[0];
                   var srid=sridComplet.split('=')[1];
                   var bounds = sridBounds[1];
                   var feature = new WKT().readGeometry(bounds);
                   this.extent= feature.getExtent()
                   this.addLayersFromCrise(this.legend)
                   this.map.getView().fit(this.extent)
                   this.addMeasureInteraction()

              }
        })
        .catch(function(error){
          console.error('carte', error)
        })
      },
      addLayersFromCrise(legendData) {
        axios.get('/remocra/crises/'+this.criseId)
        .then((response)=> {
            if(response.data){
               var extraLayers = JSON.parse(response.data.data.carte)
               if (extraLayers && extraLayers.length !== 0){
                 var mobilisedLayers = {"libelle": "Couches mobilisés pour la crise","items":[]}
                _.forEach(extraLayers, function(layer) {
                  console.log(mobilisedLayers)
                  mobilisedLayers.items.push(layer);
                 })
               }
               if(mobilisedLayers && mobilisedLayers.items.length !== 0){
                  legendData.items.push(mobilisedLayers)
               }
            }
            this.addLayersFromLayerConfig(legendData)
        })
        .catch(function(error){
          console.error('crise', error)
        })

      },
      addLayersFromLayerConfig(legendData) {
         var iGrp = legendData.items.length
         // Chaque groupe (à l'envers)
         for (iGrp; iGrp > 0; iGrp--) {
             var grp = legendData.items[iGrp - 1];
             var iLay=grp.items.length
             // Chaque couche (à l'envers)
             for(iLay ; iLay>0 ; iLay--) {
                 var layerDef = grp.items[iLay-1];
                 if (layerDef.name == null) {
                     layerDef.name = layerDef.id;
                 }

                 // La couche
                 var layer = null;
                 try {
                     switch (layerDef.type) {
                     case 'osm':
                         layer = this.createOSMLayer(layerDef);
                         break;
                     case 'wms':
                         layer = this.createWMSLayer(layerDef);
                         break;
                     case 'ign':
                         layer = this.createIGNLayer(layerDef);
                         break;
                     case 'wmts':
                         layer = this.createWMTSLayer(layerDef);
                         break;
                     case 'specific':
                         Ext.applyIf(layerDef, {
                             stategy: "fixed"
                         });
                         layer = this.createSpecificLayer(layerDef);
                         break;
                     default:
                         break;
                     }
                 } catch (e) {
                     console.error('Carte', e);
                 }
                 if (layer) {
                     this.map.addLayer(layer);
                 }
             }
         }
     },
     createOSMLayer(layerDef) {
     return new OSM(layerDef.name, layerDef.urls, {
         code: layerDef.id,
         visibility: layerDef.visibility,
         opacity: layerDef.opacity,
         projection: layerDef.projection,
         sphericalMercator: layerDef.spherical_mercator,
         wrapDateLine: layerDef.wrap_dateLine,
         tileOptions: layerDef.tile_options,
         attribution: "<a href='http://openstreetmap.org/'>OpenStreetMap</a>"
     });
    },
    createWMSLayer(layerDef) {
                // Pour les tests locaux (TODO : retirer ?)
         if (window.location.hostname == 'localhost') {
           var baseUrlRE = new RegExp('^/geoserver', 'gi');
           layerDef.url = layerDef.url.replace(baseUrlRE, 'http://sdis83-remocra.lan.priv.atolcd.com/geoserver');
         }
        var wmsLayer = new ImageLayer({
        source: new ImageWMS({
          url: layerDef.url,
          params: {
            'LAYERS': layerDef.layers
          }}),
        code: layerDef.id,
        extent: this.map.getView().getProjection().getExtent(),
        opacity: layerDef.opacity,
        visible: layerDef.visibility,
        minResolution: layerDef.scale_min,
        maxResolution: layerDef.scale_max

      });
    return wmsLayer;
    },
    createIGNLayer(layerDef) {
           layerDef.url = 'https://wxs.ign.fr/' + this.ignKey + '/geoportail/wmts';
           layerDef.projection = 'EPSG:3857';
           layerDef.matrixSet = 'PM';
           layerDef.attribution = '<a href="http://www.geoportail.fr/" target="_blank">'
               + '<img src="' + BASE_URL + '/../images/remocra/cartes/logo_gp.gif"></a>'
               + '<a href="http://www.geoportail.gouv.fr/depot/api/cgu/licAPI_CGUF.pdf" '
               + 'alt="TOS" title="TOS" target="_blank">Condifions générales d\'utilisation</a>';
           layerDef.style = layerDef.style || 'normal';
           layerDef.format = layerDef.format || 'image/jpeg';
           return this.createWMTSLayer(layerDef);
   },
   // A implémenter dans des cartes spécifiques si nécessaire (composants qui
   // étendent Sdis.Remocra.widget.map.Map)
   createSpecificLayer(layerDef) {
       if (layerDef == 'fakebaselayer') {
           // Fausse couche : photos de l'IGN, non visible
           return this.createIGNLayer({
              id : layerDef,
              name : layerDef,
              //layers : 'GEOGRAPHICALGRIDSYSTEMS.MAPS',
              visibility : false,
              opacity : 0.0,
              projection: layerDef.projection || 'EPSG:3857',
              url: 'https://wxs.ign.fr/' + this.ignKey + '/geoportail/wmts',
              tileMatrixSet: {
                nom: 'PM',
                resolution_min: 0.5971642834779,
                resolution_max : 2445.9849051256400
              },
              styles : [{
                 id : 'normal',
                 libelle : 'Légende générique'
              }],
              numZoomLevels: 20,
              scale_min : 0,
              scale_max : 1000000
          });
       }
       throw 'La couche spécifique \'' + layerDef.id + '\' est inconnue pour cette carte.';
   },
   createWMTSLayer(layerDef) {
       if (typeof (layerDef.style) === 'undefined') {
           // Premier style disponible
           if (layerDef.styles && layerDef.styles.length>0) {
               layerDef.style = layerDef.styles[0].id;
           }
       }
       var resolutions = this.resolutions;
       if (layerDef.tileMatrixSet) {
           resolutions = this.getTruncatedWmts3857Resolutions(layerDef.tileMatrixSet.resolution_min, layerDef.tileMatrixSet.resolution_max);
       }
       var minResolution = resolutions[resolutions.length - 1];
       var maxResolution = resolutions[0];

       var matrixIds = this.getTruncatedMatrixIds(minResolution, maxResolution);
       var tileGrid = new WMTSTileGrid({
               origin: [-20037508, 20037508],
               resolutions: resolutions,
               matrixIds: matrixIds
             });
       var ignSource = new WMTS({
         // todo layerDEf.url ??? quel clé
               url: layerDef.url,
               layer: layerDef.layers,
               matrixSet: layerDef.matrixSet || (layerDef.tileMatrixSet && layerDef.tileMatrixSet.nom ? layerDef.tileMatrixSet.nom : null),
               format: layerDef.format,
               projection: layerDef.projection || 'EPSG:3857',
               tileGrid: tileGrid,
               style: 'normal',
               attributions: '<a href="http://www.geoportail.fr/" target="_blank">' +
                     '<img src="https://api.ign.fr/geoportail/api/js/latest/' +
                     'theme/geoportal/img/logo_gp.gif"></a>'
       });
       var wmtsLayer = new TileLayer({
               source: ignSource,
               minResolution: minResolution,
               maxResolution: maxResolution,
               opacity: layerDef.opacity,
               code: layerDef.id,
               visible: layerDef.visibility
       });
       return wmtsLayer;
   },

    getTruncatedResolutions(resolutions, min, max) {
        var i, returned = [];
        // Comparaison sur des valeurs arrondies car précision variable des données en entrée
        var roundMin = min ? Math.round(min * 10000) / 10000 : resolutions[resolutions.length - 1];
        var roundMax = max ? Math.round(max * 10000) / 10000 : resolutions[0];
        for (i = 0; i < resolutions.length; i++) {
            var resolution = resolutions[i];
            var roundResolution = Math.round(resolution * 10000) / 10000;
            if (roundResolution <= roundMax && roundResolution >= roundMin) {
                returned.push(resolution);
            }
        }
        return returned;
    },
    getTruncatedWmts3857Resolutions(min, max) {
        return this.getTruncatedResolutions(this.resolutions, min, max);
    },

    getTruncatedMatrixIds(min, max) {
       var matrixIds = [];
       var resolutionsOrigin = this.resolutions;
       var i = resolutionsOrigin.indexOf(max);
       for (i ; i < resolutionsOrigin.indexOf(min); i++) {
         matrixIds.push(i);
       }
       return matrixIds;
    },
    getLayerById(id) {
     var i;
     for (i = 0; i < this.map.getLayers().getLength(); i++) {
         var layer = this.map.getLayers().item(i);
         if (layer.getProperties().code == id) {
             return layer;
         }
     }
     return null;
   },
   showToolsBar() {
    var x = document.getElementById('toolsBar');
    if (x.className.indexOf("active") == -1) {
        x.className += " active";
    } else {
        x.className = x.className.replace(" active", "");
    }
  },

 addMeasureInteraction() {
   var formatLength =function(line) {
     var length = getLength(line);
       var output;
       if (length > 100) {
         output = (Math.round(length / 1000 * 100) / 100) +' ' + 'km';
       } else {
         output = (Math.round(length * 100) / 100) +' ' + 'm';
       }
       return output;
   }
   var formatArea=function(polygon) {
     var area = getArea(polygon);
       var output;
       if (area > 10000) {
         output = (Math.round(area / 1000000 * 100) / 100) +' ' + 'km<sup>2</sup>';
       } else {
         output = (Math.round(area * 100) / 100) +' ' + 'm<sup>2</sup>';
       }
       return output;
   }
      var measureTooltipElement = document.createElement('div')
      measureTooltipElement.className = 'tooltip tooltip-measure'
      let measureTooltip = new OlOverlay({
        element: measureTooltipElement,
        offset: [0, -15],
        positioning: 'bottom-center'
      })
      this.map.addOverlay(measureTooltip)
      let source = new OlSourceVector()
      let vectorLayer = new OlLayerVector({
        source: source
      })
      this.map.addLayer(vectorLayer)
      if(this.selectedRuler && this.selectedRuler !== null){
        console.log(this.selectedRuler)
         var type = (this.selectedRuler == 'Surface' ? 'Polygon' : 'LineString');
      }else {
        return
      }
      console.log(type)
      let measuringTool = new OlInteractionDraw({
        type: type,
        source: vectorLayer.getSource()
      })
      measuringTool.on('drawstart', function(event) {
        // tooltip
        vectorLayer.getSource().clear()
        event.feature.on('change', function(event) {
          var geom = event.target.getGeometry();
          var measurement;
          var output;
             if (geom instanceof Polygon) {
              output =formatArea(geom)
             } else if (geom instanceof LineString) {
              output = formatLength(geom)
             }
          measureTooltipElement.innerHTML = output
          measureTooltip.setPosition(event.target.getGeometry().getLastCoordinate())
        })
      })

      measuringTool.on('change:active', function(evt) {
        if (evt.oldValue) {
          // Nettoyage
          vectorLayer.getSource().clear()
          measureTooltip.setPosition([0, 0])
        }
      })
      this.map.addInteraction(measuringTool)
      this.measuringTool = measuringTool
    },
    toggleDistance() {
      if(this.measuringTool){
           this.measuringTool.setActive(!this.measuringTool.getActive())
      }
      this.map.removeInteraction(this.measuringTool);
      this.addMeasureInteraction();
  },
  GoInFullscreen: function(event){
       var elem = document.documentElement;
       console.log(document )
       if (
         document.fullscreenEnabled ||
         document.webkitFullscreenEnabled ||
         document.mozFullScreenEnabled ||
         document.msFullscreenEnabled
       ) {
           if (elem.requestFullscreen) {
             elem.requestFullscreen();
             return;
           } else if (elem.webkitRequestFullscreen) {
             elem.webkitRequestFullscreen();
             return;
           } else if (elem.mozRequestFullScreen) {
             elem.mozRequestFullScreen();
             return;
           } else if (elem.msRequestFullscreen) {
             elem.msRequestFullscreen();
             return;
         };

       }
     }
   }
}

    </script>

<style>
.sidebar {
  height: 100%;
  background-color: #f1f1f1;
  ;
  width: 400px;
  position: absolute;
  z-index: 100;
  right: 0;
  display: none;
}

ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
  background-color: #f1f1f1;
  border: 1px solid #555;
}

.groupe {
  background-color: #4CAF50;
  color: white;

}

.layer {
  color: #fff !important;
  background-color: #555 !important;
  border: none;
  cursor: move;

}

#accordion1 .card-body {
  height: 400px;
}

#accordion2 .card-body {
  height: 400px;
}

#accordion3 .card-body {
  height: 400px;
}

#accordion4 .card-body {
  height: 400px;
}

#accordion5 .card-body {
  height: 530px;
}

#accordion6 .card-body {
  height: 530px;
}


.ctrl {
  background-color: currentColor;
}

.ctrl:hover {
  background-color: currentColor;
}

.text-start {
  text-align: start;
}

.combo {
  width: 10%;
}

.tooltip {
  position: relative;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 4px;
  color: white;
  padding: 4px 8px;
  opacity: 5;
  white-space: nowrap;
}

.tooltip-measure {
  opacity: 1;
  font-weight: bold;
}

.tooltip-static {
  background-color: #ffcc33;
  color: black;
  border: 1px solid white;
}

.tooltip-measure:before,
.tooltip-static:before {
  border-top: 6px solid rgba(0, 0, 0, 0.5);
  border-right: 6px solid transparent;
  border-left: 6px solid transparent;
  content: "";
  position: absolute;
  bottom: -6px;
  margin-left: -7px;
  left: 50%;
}

.tooltip-static:before {
  border-top-color: #ffcc33;
}

.toolsBar {
  display: none !important;
}

.toolsBar.active {
  display: block !important;
}

.btn {
  text-align: start;
}

.evenement {
  margin-left: 70px;
}
.document {
  margin-left: 125px;
}
#files{
    display: none;
}
</style>
