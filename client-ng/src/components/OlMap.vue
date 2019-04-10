<template>
<b-container class="map-container" style="max-width:100%; max-height: 100%;">
  <b-row class="text-start" :style="{height: mapRowHeight}">
    <b-col class="top_content">
      <b-row class="text-start toolbar">
        <div class="h-spacer" />
        <div class="text-start my-1">
          <b-btn class="ctrl" title="Déplacer la carte"><img src="/remocra/static/img/pan.png"></b-btn>
          <b-btn class="ctrl" title="Zoomer en avant" @click="zoomIn"><img src="/remocra/static/img/magnifier_zoom_in.png"></b-btn>
          <b-btn class="ctrl" title="Zoomer en arrière" @click="zoomOut"><img src="/remocra/static/img/magnifier_zoom_out.png"></b-btn>
          <b-btn class="ctrl" title="Rétablir la vue précédente" @click="zoomPrev"><img src="/remocra/static/img/zoom_prec.png"></b-btn>
          <b-btn class="ctrl" title="Rétablir la vue suivante" @click="zoomNext"><img src="/remocra/static/img/zoom_suiv.png"></b-btn>
        </div>
        <div class="big-h-spacer" />
        <div class="text-start" style="margin-top:0.5rem">
          <search-commune :crise='criseId' ref='searchCommune'></search-commune>
        </div>
        <div class="h-spacer" />
        <div class="text-start" style="margin-top:0.5rem">
          <search-repertoire :crise="criseId" ref='searchRepertoire'></search-repertoire>
        </div>
        <div class="big-h-spacer" />
        <b-btn class=" text-start my-1 measure-container ctrl" :id="'measureTools'+criseId" @click="removeMeasureInteraction"><img src="/remocra/static/img/ruler.png"></b-btn>
        <b-popover class="dropdown-menu" placement="bottomright" :ref="'popovermesure'+criseId" :target="'measureTools'+criseId">
          <div>
            <b-btn class="dropdown-item" @click="activateMeasure('Distance')"><img src='/remocra/static/img/ruler.png'> Distance</b-btn>
          </div>
          <div>
            <b-btn class="dropdown-item" @click="activateMeasure('Surface')"><img src='/remocra/static/img/ruler_square.png'> Surface</b-btn>
          </div>
        </b-popover>
        <div class="big-h-spacer" />
        <div class="text-start my-1">
          <b-btn :id="'infoBtn'+criseId" class="ctrl" @click="activateShowInfo" title="Obtenir des informations sur un point de la carte"><img src="/remocra/static/img/information.png"></b-btn>
        </div>
        <div class="big-h-spacer" />
        <div class="text-start my-1">
          <b-btn :id="'toolsBarBtn'+criseId" class="ctrl" @click="showToolsBar" v-b-toggle.collapse1 title="Activer les outils d'édition"><img src="/remocra/static/img/pencil.png"></b-btn>
        </div>
        <div class="big-h-spacer" />
        <div class="text-start my-1">
          <b-btn :id="'processBtn'+criseId" class="ctrl" @click="showProcess" title="Processus"><img src="/remocra/static/img/process.png"></b-btn>
        </div>
        <div class="big-h-spacer" />
        <b-form-group class="text-start my-1">
          <b-form-radio-group id="btnradios2" buttons button-variant="outline-secondary" v-model="modeAffichage" :options="modeAffichages" name="radioBtnOutline" />
        </b-form-group>
        <div class="big-h-spacer" />
        <div class="text-start my-1">
          <b-btn class="ctrl" @click="GoInFullscreen" title="Plein écran"><img src="/remocra/static/img/fullscreen.svg"></b-btn>
        </div>
      </b-row>
      <b-row :id="'toolsBar'+criseId" class="toolsBar toolbar">
        <tool-bar ref="toolBar"></tool-bar>
      </b-row>
  </b-col>
    <b-col class="left_content">
      <div role="tablist">
        <b-card no-body class="mb-1">
          <b-card-header header-tag="header" class="p-1" role="tab">
            <span style="cursor:pointer" href="#" v-b-toggle.accordion1>Évènements</span><span>
              <div class="toolbar">
                <b-btn @click="addEvent" class="ctrl" title="Nouvel évènement"><img src="/remocra/static/img/event-add.svg"></b-btn>
                <b-btn class="ctrl" :id="'popoverButton'+criseId"><img src="/remocra/static/img/filter.svg" title="Filtrer"></b-btn>
                <b-popover placement="right" ref="popover" :target="'popoverButton'+criseId" title="Filtrer les évènements">
                  <filters :criseId="criseId" ref="filters"></filters>
                </b-popover>
              </div>
            </span>
          </b-card-header>
          <b-collapse id="accordion1" visible accordion="my-accordion" role="tabpanel">
            <b-card-body>
              <!--p class="card-text"></p-->
              <evenements :crise="criseId" ref="evenements"></evenements>
            </b-card-body>
          </b-collapse>
        </b-card>
        <b-card no-body class="mb-1">
          <b-card-header header-tag="header" class="p-1" role="tab">
            <span style="cursor:pointer" href="#" v-b-toggle.accordion2>Documents</span>
            <div class="toolbar">
              <span class="document">
                <b-btn @click="addNewDocument" class="ctrl"><img src="/remocra/static/img/file-add.svg"></b-btn>
              </span>
              <new-document ref="newCriseDocument"></new-document>
            </div>
          </b-card-header>
          <b-collapse id="accordion2" accordion="my-accordion" role="tabpanel">
            <b-card-body>
              <p class="card-text">
                <documents :crise="criseId" ref="documents"></documents>
              </p>
            </b-card-body>
          </b-collapse>
        </b-card>
        <b-card no-body class="mb-1">
          <b-card-header header-tag="header" class="p-1" role="tab">
            <span style="cursor:pointer" block href="#" v-b-toggle.accordion3 variant="info">Indicateurs</span>
          </b-card-header>
          <b-collapse id="accordion3" accordion="my-accordion" role="tabpanel">
            <b-card-body>
              <p class="card-text"> Indicateurs </p>
            </b-card-body>
          </b-collapse>
        </b-card>
        <b-card no-body class="mb-1">
          <b-card-header header-tag="header" class="p-1" role="tab">
            <span style="cursor:pointer" block href="#" v-b-toggle.accordion4 variant="info">Recherches et analyses</span>
          </b-card-header>
          <b-collapse id="accordion4" accordion="my-accordion" role="tabpanel">
            <b-card-body>
              <recherche-analyse :criseId="criseId" ref="rechercheAnalyse"></recherche-analyse>
            </b-card-body>
          </b-collapse>
        </b-card>
      </div>
    </b-col>
    <b-col class="col-map">
      <b-row :id="'mapDiv'+criseId" class='mapDiv'>
        <b-col>
          <div :id="'map'+criseId" class="map">
            <map-features :criseId="criseId" ref="MapFeatures"></map-features>
            <b-row :id="'tableauDiv'+criseId" class='tableauDiv'>
              <b-col>
                <div role="tablist">
                  <b-card no-body class="mb-1">
                    <b-button-close :id="'boutonToggleTableau'+criseId" @click='toggleTableau'></b-button-close>
                    <b-collapse id="accordion7" visible accordion="my-accordion3" role="tabpanel">
                      <b-card-body class="accord7">
                        <tableau-donnees :criseId="criseId" ref="TableauDonnees" :pageSize='10'></tableau-donnees>
                      </b-card-body>
                    </b-collapse>
                    <b-card-header header-tag="header" class="p-1" role="tab">
                      <span style="cursor:pointer" block href="#" v-b-toggle.accordion7 visible variant="info">Données</span>
                    </b-card-header>
                    </b-card>
                  </div>
              </b-col>
            </b-row>
          </div>
        </b-col>
      </b-row>

    </b-col>
    <b-col class="right_content">
      <div role="tablist">
        <b-card no-body class="mb-1">
          <b-card-header header-tag="header" class="p-1" role="tab">
            <span style="cursor:pointer" block href="#" v-b-toggle.accordion5 variant="info">Couches</span>
          </b-card-header>
          <b-collapse id="accordion5" visible accordion="my-accordion2" role="tabpanel">
            <b-card-body>
              <div class="sidebar">
                <div :id="'layertree'+criseId">
                  <div v-for="(group,index) in legend.items" :key="index">
                    <div class="group">{{group.libelle}}</div>
                    <draggable :list="group.items" :options="{handle:'.my-handle'}" @start="drag=true" @end="addSortable()">
                      <div class="layer" v-for="(layer,index) in group.items" :key="index">
                        <div class="my-handle">
                          <input type="checkbox" :id="'checkbox'+layer.id+'-'+criseId" :checked="layer.visibility" @click="changeLayerVisibility(layer.id)">
                          <label for="layer.id">{{layer.libelle}}</label>
                          <b-btn v-bind:id="'btnSuppr'+layer.id" v-if="group.libelle === 'Fichiers importés'" class="ctrlImportLayer" title="Supprimer la couche" @click="deleteImportLayer(layer.id)"><img src="/remocra/static/img/decline.png"></b-btn>
                        </div>
                      </div>
                    </draggable>
                  </div>
                </div>
              </div>
            </b-card-body>
          </b-collapse>
        </b-card>
        <b-card no-body class="mb-1">
          <b-card-header header-tag="header" class="p-1" role="tab">
            <span style="cursor:pointer" block href="#" v-b-toggle.accordion6 variant="info">Légende</span>
          </b-card-header>
          <b-collapse id="accordion6" accordion="my-accordion2" role="tabpanel">
            <b-card-body>
              <div class="sidebar">
                <div id="layertree">
                  <div v-for="(group,index) in legend.items" :key="index">
                    <div class="group">{{group.libelle}}</div>
                    <div class="layer" v-for="(layer,index) in group.items" :key="index" :id="'legend'+layer.id+'-'+criseId">
                      <div class="my-handle">
                        <label>{{layer.libelle}}</label>
                        <img style="display:block;margin-left:20px;" onerror="this.src='/remocra/static/img/layer404.png'" class="legend-img" :src="getLegendGraphics(layer)" />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </b-card-body>
          </b-collapse>
        </b-card>
      </div>
    </b-col>
  </b-row>
  <modalImportFile ref="modalImportFile"></modalImportFile>
  <process ref="showProcess"></process>
  <choice-feature :crise="criseId" ref="choiceFeature"></choice-feature>
  <stamped-card :crise="criseId" ref="stampedCard"></stamped-card>
  <show-info :crise="criseId" ref="showInfo"></show-info>
  <new-evenement :criseId="criseId" ref="newEvenement"></new-evenement>

</b-container>
</template>
<script>
import Map from 'ol/Map.js'
import View from 'ol/View.js'
import {
  defaults as defaultControls
} from 'ol/control.js'
import TileLayer from 'ol/layer/Tile.js'
import ImageLayer from 'ol/layer/Image.js'
import WMTS from 'ol/source/WMTS.js'
import OSM from 'ol/source/OSM.js'
import ImageWMS from 'ol/source/ImageWMS.js'
import WMTSTileGrid from 'ol/tilegrid/WMTS.js'
import WKT from 'ol/format/WKT.js'
import axios from 'axios'
import * as Proj from 'ol/proj'
import * as Polygon from 'ol/geom/Polygon'
import MultiPolygon from 'ol/geom/MultiPolygon'
import {
  register
} from 'ol/proj/proj4.js'
import proj4 from 'proj4'
import _ from 'lodash'
import draggable from 'vuedraggable'
import SearchCommune from './SearchCommune.vue'
import SearchRepertoire from './SearchRepertoireLieu.vue'
import OlOverlay from 'ol/Overlay.js'
import OlSourceVector from 'ol/source/Vector.js'
import OlLayerVector from 'ol/layer/Vector.js'
import OlInteractionDraw, {
  createBox
} from 'ol/interaction/Draw.js'
import {
  Draw,
  Modify,
  Snap,
  Select,
  Translate
} from 'ol/interaction.js'
import {
  getArea,
  getLength
} from 'ol/sphere.js'
import {
  toStringXY
} from 'ol/coordinate'
import {
  Circle as CircleStyle,
  Fill,
  Stroke,
  Style
} from 'ol/style.js'
import GeoJSON from 'ol/format/GeoJSON'
import NewEvenement from './NewEvenement.vue'
import NewDocument from './NewDocument.vue'
import Evenements from './Evenements.vue'
import Documents from './Documents.vue'
import Filters from './Filters.vue'
import ToolBar from './ToolBar.vue'
import ChoiceFeature from './ChoiceFeature.vue'
import ShowInfo from './ShowInfo.vue'
import StampedCard from './StampedCard.vue'
import ModalImportFile from './ModalImportFile.vue'
import * as eventTypes from '../bus/event-types.js'
import MultiLineString from 'ol/geom/MultiLineString'
import MultiPoint from 'ol/geom/MultiPoint'
import RechercheAnalyse from './RechercheAnalyse.vue'
import Process from './Process.vue'
import MapFeatures from './MapFeatures.vue'
import TableauDonnees from './TableauDonnees.vue'
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
    RechercheAnalyse,
    Filters,
    ToolBar,
    ChoiceFeature,
    ShowInfo,
    StampedCard,
    ModalImportFile,
    Process,
    MapFeatures,
    TableauDonnees
  },
  props: {
    criseId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      mapRowHeight: '100%',
      file: null,
      selectedFeature: null,
      oldSelectedFeatureGeom: null,
      modalShow: false,
      workingLayer: null,
      sridL93: 2154,
      proj: null,
      epsgL93: null,
      selectedRuler: null,
      select: null,
      modeAffichage: 'radio1',
      modeAffichages: [
        {
          text: 'Opérationnel',
          value: 'radio1'
        },
        {
          text: 'Anticipation',
          value: 'radio2'
        }
      ],
      // todo mettre le var par défaut?
      extent: [
        256805.64470225616,
        6249216.947446961,
        265705.78118321137,
        6252690.054919669
      ],
      legend: [],
      // todo  à factoriser
      ignKey: 'pratique',
      map: {
        type: Object,
        default: {}
      },
      resolutions: [
        156543.03392804103,
        78271.5169640205,
        39135.75848201024,
        19567.879241005125,
        9783.939620502562,
        4891.969810251281,
        2445.9849051256406,
        1222.9924525628203,
        611.4962262814101,
        305.74811314070485,
        152.87405657035254,
        76.43702828517625,
        38.218514142588134,
        19.109257071294063,
        9.554628535647034,
        4.777314267823517,
        2.3886571339117584,
        1.1943285669558792,
        0.5971642834779396,
        0.29858214173896974,
        0.14929107086948493,
        0.07464553543474241
      ],
      //  Historique de navigation (pile, index courant, flag provenance de l'évènement)
      navigation: {
        stack: [],
        idx: -1,
        btns: false
      },
      inputGeoms: [],
      displayType: 'MAP_ONLY'
    }
  },
  mounted() {
    this.map = new Map({
      target: 'map' + this.criseId,
      layers: [],
      controls: defaultControls({
        rotate: false,
        zoom: false,
        zoomToExtent: true,
        attribution: false,
        attributionOptions: {
          collapsible: false
        }
      }),
      view: new View({
        projection: 'EPSG:3857',
        center: [0, 0],
        zoom: 2
      })
    })
    this.proj = this.map.getView().getProjection()
    this.epsgL93 = 'EPSG:' + this.sridL93
    proj4.defs(this.epsgL93, '+proj=lcc +lat_1=49 +lat_2=44 +lat_0=46.5 +lon_0=3 +x_0=700000 +y_0=6600000 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs')
    register(proj4)
    this.constructMap()
    _.delay(this.map.updateSize.bind(this.map), 10)
    this.createWorkingLayer('workingLayer')
    this.addModifyInteraction()
    this.addTranslateInteraction()
    //  Historique de navigation
    this.map.on('moveend', () => {
      if (!this.navigation.btns) {
        let view = this.map.getView()
        //  Retrait des éléments "suivants" (cas "Zoom précédent" puis "Zoom manuel") et ajout du nouvel état
        this.navigation.stack.splice(this.navigation.idx + 1, this.navigation.stack.length - (this.navigation.idx + 1), {
          zoom: view.getZoom(),
          center: view.getCenter(),
          rotation: view.getRotation()
        })
        //  On limite à 10 entrées
        if (this.navigation.stack.length > 9) {
          this.navigation.stack.shift()
        } else {
          this.navigation.idx++
        }
      }
      this.navigation.btns = false
    })
    this.setDisplay(this.displayType)
    this.$root.$options.bus.$on(eventTypes.REFRESH_MAP, crise => {
      this.refreshMap(crise)
    })
    this.$root.$options.bus.$on(eventTypes.ZOOM_TOGEOM, args => {
      this.zoomToGeom(args.geom, args.crise)
    })
    this.$root.$options.bus.$on(eventTypes.ZOOM_TOEXTENT, extent => {
      this.zoomToExtent(extent)
    })
    this.$root.$options.bus.$on(eventTypes.ADD_TOWORKINGLAYER, feature => {
      this.addToWorkingLayer(feature)
    })
    this.$root.$options.bus.$on(eventTypes.ADD_DRAWINTERACTIONS, args => {
      this.addDrawInteractions(args.typeGeom, args.natureId)
    })
    this.$root.$options.bus.$on(eventTypes.ADD_STAMPEDCARD, this.addStampedCard)
    this.$root.$options.bus.$on(eventTypes.ACTIVATE_INTERACTION, type => {
      this.activateInteraction(type)
    })
    this.$root.$options.bus.$on(eventTypes.ANNULE_MODIFGEOM, this.annulModifGeom)
    this.$root.$options.bus.$on(eventTypes.ANNULE_TRANSLATEGEOM, this.annulTranslateGeom)
    this.$root.$options.bus.$on(eventTypes.VALIDE_MODIFGEOM, this.validModifGeom)
    this.$root.$options.bus.$on(eventTypes.VALIDE_TRANSLATEGEOM, this.validTranslateGeom)
    this.$root.$options.bus.$on(eventTypes.OPEN_ATTRIBUTES, this.openAttributes)
    this.$root.$options.bus.$on(eventTypes.UPDATE_MAPFILTERS, args => {
      this.updateMapFilters(args.id, args.filters)
    })
    this.$root.$options.bus.$on(eventTypes.INPUT_GEOM, args => {
      this.inputGeom(args.typeGeom, args.index)
    })
    this.$root.$options.bus.$on(eventTypes.ANNULE_INPUTGEOM, index => {
      this.annulGeom(index)
    })
    this.$root.$options.bus.$on(eventTypes.VALIDE_INPUTGEOM, index => {
      this.validGeom(index)
    })
    this.$root.$options.bus.$on(eventTypes.MODIFY_INPUTGEOM, this.modifyGeom)
    this.$root.$options.bus.$on(eventTypes.DELETE_INPUTGEOM, this.deleteGeom)
    this.$root.$options.bus.$on(eventTypes.RESEARCH_TABDONNEES, this.showTabDonnees)
    this.$root.$options.bus.$on(eventTypes.TOGGLE_TABDONNEES, this.toggleTableau)
  },
  destroyed() {
    this.$root.$options.bus.$off(eventTypes.REFRESH_MAP)
    this.$root.$options.bus.$off(eventTypes.ZOOM_TOGEOM)
    this.$root.$options.bus.$off(eventTypes.ZOOM_TOEXTENT)
    this.$root.$options.bus.$off(eventTypes.ADD_TOWORKINGLAYER)
    this.$root.$options.bus.$off(eventTypes.ADD_DRAWINTERACTIONS)
    this.$root.$options.bus.$off(eventTypes.ADD_STAMPEDCARD)
    this.$root.$options.bus.$off(eventTypes.ACTIVATE_INTERACTION)
    this.$root.$options.bus.$off(eventTypes.ANNULE_MODIFGEOM)
    this.$root.$options.bus.$off(eventTypes.ANNULE_TRANSLATEGEOM)
    this.$root.$options.bus.$off(eventTypes.VALIDE_MODIFGEOM)
    this.$root.$options.bus.$off(eventTypes.VALIDE_TRANSLATEGEOM)
    this.$root.$options.bus.$off(eventTypes.OPEN_ATTRIBUTES)
    this.$root.$options.bus.$off(eventTypes.UPDATE_MAPFILTERS)
    this.$root.$options.bus.$off(eventTypes.INPUT_GEOM)
    this.$root.$options.bus.$off(eventTypes.ANNULE_INPUTGEOM)
    this.$root.$options.bus.$off(eventTypes.VALIDE_INPUTGEOM)
    this.$root.$options.bus.$off(eventTypes.MODIFY_INPUTGEOM)
    this.$root.$options.bus.$off(eventTypes.DELETE_INPUTGEOM)
    this.$root.$options.bus.$off(eventTypes.RESEARCH_TABDONNEES)
    this.$root.$options.bus.$off(eventTypes.TOGGLE_TABDONNEES)
  },
  updated() {
    // this.addSortable()
  },
  methods: {
    getLegendGraphics(layer) {
      return layer && layer.styles && layer.styles[0] ? layer.styles[0].legende : null
    },
    createWorkingLayer(code) {
      var source = new OlSourceVector()
      var style = new Style({
        fill: new Fill({
          color: 'rgba(255, 255, 255, 0.2)'
        }),
        stroke: new Stroke({
          color: 'blue',
          width: 2
        }),
        image: new CircleStyle({
          radius: 7,
          fill: new Fill({
            color: '#ffcc33'
          })
        })
      })
      var vectorLayer = new OlLayerVector({
        name: code,
        code: code,
        source: source,
        style: style,
        visibility: true,
        opacity: 1,
        zIndex: 1000
      })
      this.map.addLayer(vectorLayer)
      return vectorLayer
    },
    addNewDocument() {
      this.$refs['newCriseDocument'].showModal(this.criseId)
    },
    addEvent() {
      this.$refs['newEvenement'].createEvent(this.criseId)
    },
    addSortable() {
      this.addLayersFromLayerConfig(this.legend)
    },
    changeLayerVisibility(id) {
      var layer = this.getLayerById(id)
      var newVisibility = !layer.getVisible()
      layer.setVisible(newVisibility)
      // Légende
      document.getElementById('legend' + id + '-' + this.criseId).style.display = newVisibility ? 'block' : 'none'
    },
    changeLayerOpacity(id) {
      var range = document.getElementById('range' + id)
      var layer = this.getLayerById(id)
      layer.setOpacity(parseFloat(range.value))
    },
    constructMap() {
      axios.get('/remocra/ext-res/js/app/remocra/features/crises/data/carte.json').then(response => {
        if (response.data) {
          this.legend = response.data
          axios.get('/remocra/crises/' + this.criseId + '/geometrie').then(response => {
            if (response.data) {
              // on récupère l'extent (géometrie des commune de la crise)
              var sridBounds = response.data.data.split(';')
              var bounds = sridBounds[1]
              var feature = new WKT().readGeometry(bounds)
              this.extent = feature.getExtent()
              this.addLayersFromCrise(this.legend)
              this.map.getView().fit(this.extent)
              // this.addMeasureInteraction()
            }
          }).catch(function(error) {
            console.error('carte', error)
          })
        }
      }).catch(function(error) {
        console.error('carte', error)
      })
    },
    addLayersFromCrise(legendData) {
      axios.get('/remocra/crises/' + this.criseId).then(response => {
        if (response.data) {
          if (response.data.data.carte !== null) {
            var extraLayers = JSON.parse(response.data.data.carte)
            if (extraLayers && extraLayers.length !== 0) {
              //  Recherche du groupe "additional"
              var additionalGroup = null
              _.forEach(legendData.items, function(group) {
                if (group.code === 'additional') {
                  additionalGroup = group
                  return false
                }
              })
              if (!additionalGroup) {
                additionalGroup = {
                  libelle: 'Couches mobilisées pour la crise',
                  items: []
                }
                legendData.items.unshift(additionalGroup)
              }
              _.forEach(extraLayers, function(layer) {
                additionalGroup.items.push(layer)
              })
            }
          }
        }
        this.addLayersFromLayerConfig(legendData)
      }).catch(function(error) {
        console.error('crise', error)
      })
    },
    addLayersFromLayerConfig(legendData) {
      var iGrp = legendData.items.length
      //  Chaque groupe (à l'envers)
      for (iGrp; iGrp > 0; iGrp--) {
        var grp = legendData.items[iGrp - 1]
        var iLay = grp.items.length
        //  Chaque couche (à l'envers)
        for (iLay; iLay > 0; iLay--) {
          var layerDef = grp.items[iLay - 1]
          if (layerDef.name === null) {
            layerDef.name = layerDef.id
          }
          //  La couche
          var layer = null
          try {
            switch (layerDef.type) {
              case 'osm':
                layer = this.createOSMLayer(layerDef)
                break
              case 'wms':
                layer = this.createWMSLayer(layerDef)
                break
              case 'ign':
                layer = this.createIGNLayer(layerDef)
                break
              case 'wmts':
                layer = this.createWMTSLayer(layerDef)
                break
              case 'specific':
                layer = this.createSpecificLayer(layerDef)
                break
              default:
                break
            }
          } catch (e) {
            console.error('Carte', e)
          }
          if (layer) {
            this.map.addLayer(layer)
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
      })
    },
    createWMSLayer(layerDef) {
      let crossOriginValue = layerDef.url.indexOf(window.document.location.hostname) ? 'use-credentials' : 'anonymous'
      var wmsLayer = new ImageLayer({
        source: new ImageWMS({
          url: layerDef.url,
          crossOrigin: crossOriginValue,
          params: {
            LAYERS: layerDef.layers
          }
        }),
        code: layerDef.id,
        extent: this.map.getView().getProjection().getExtent(),
        opacity: layerDef.opacity,
        visible: layerDef.visibility,
        minResolution: layerDef.scale_min,
        maxResolution: layerDef.scale_max
      })
      return wmsLayer
    },
    createIGNLayer(layerDef) {
      layerDef.url = 'https://wxs.ign.fr/' + this.ignKey + '/geoportail/wmts'
      layerDef.projection = 'EPSG:3857'
      layerDef.matrixSet = 'PM'
      layerDef.attribution = '<a href="http://www.geoportail.fr/" target="_blank">' + '<img src="/remocra/images/remocra/cartes/logo_gp.gif"></a>' + '<a href="http://www.geoportail.gouv.fr/depot/api/cgu/licAPI_CGUF.pdf" ' +
        'alt="TOS" title="TOS" target="_blank">Conditions générales d\'utilisation</a>'
      layerDef.style = layerDef.style || 'normal'
      layerDef.format = layerDef.format || 'image/jpeg'
      return this.createWMTSLayer(layerDef)
    },
    // A implémenter dans des cartes spécifiques si nécessaire (composants qui
    // étendent Sdis.Remocra.widget.map.Map)
    createSpecificLayer(layerDef) {
      if (layerDef === 'fakebaselayer') {
        // Fausse couche : photos de l'IGN, non visible
        return this.createIGNLayer({
          id: layerDef,
          name: layerDef,
          // layers : 'GEOGRAPHICALGRIDSYSTEMS.MAPS',
          visibility: false,
          opacity: 0.0,
          projection: layerDef.projection || 'EPSG:3857',
          url: 'https://wxs.ign.fr/' + this.ignKey + '/geoportail/wmts',
          tileMatrixSet: {
            nom: 'PM',
            resolution_min: 0.5971642834779,
            resolution_max: 2445.98490512564
          },
          styles: [
            {
              id: 'normal',
              libelle: 'Légende générique'
            }
          ],
          numZoomLevels: 20,
          scale_min: 0,
          scale_max: 1000000
        })
      } else if (layerDef.id === '893bb7520e7fb036d665661847628994') {
        layerDef = _.defaults(layerDef, {
          type: 'wms',
          libelle: 'Évènements',
          scale_min: '0',
          scale_max: '1000000',
          visibility: true,
          opacity: 1,
          interrogeable: false,
          items: null,
          wms_layer: true,
          layers: 'remocra:v_crise_evenement',
          url: '/remocra/evenements/wms',
          sld: null,
          projection: 'EPSG:2154',
          styles: [
            {
              id: 'remocra_barriere',
              libelle: 'Barrière',
              legende: '/remocra/geoserver/remocra/wms?REQUEST=GetLegendGraphic&LAYER=v_crise_evenement&VERSION=1.0.0&FORMAT=image/png&WIDTH=20&HEIGHT=20&STRICT=false&style=crise_evenement'
            }
          ]
        })
        let layer = this.createWMSLayer(layerDef)
        // Application du filtre initial
        layer.getSource().getParams().filter = JSON.stringify([
          {
            property: 'crise',
            value: this.criseId
          }
        ])
        return layer
      }
    },
    createWMTSLayer(layerDef) {
      if (typeof layerDef.style === 'undefined') {
        // Premier style disponible
        if (layerDef.styles && layerDef.styles.length > 0) {
          layerDef.style = layerDef.styles[0].id
        }
      }
      var resolutions = this.resolutions
      if (layerDef.tileMatrixSet) {
        resolutions = this.getTruncatedWmts3857Resolutions(layerDef.tileMatrixSet.resolution_min, layerDef.tileMatrixSet.resolution_max)
      }
      var minResolution = resolutions[resolutions.length - 1]
      var maxResolution = resolutions[0]
      var matrixIds = this.getTruncatedMatrixIds(minResolution, maxResolution)
      var tileGrid = new WMTSTileGrid({
        origin: [-20037508, 20037508],
        resolutions: resolutions,
        matrixIds: matrixIds
      })
      var ignSource = new WMTS({
        // TODO layerDEf.url ??? quelle clé
        url: layerDef.url,
        crossOrigin: 'anonymous',
        layer: layerDef.layers,
        matrixSet: layerDef.matrixSet || (layerDef.tileMatrixSet && layerDef.tileMatrixSet.nom ? layerDef.tileMatrixSet.nom : null),
        format: layerDef.format,
        projection: layerDef.projection || 'EPSG:3857',
        tileGrid: tileGrid,
        style: 'normal',
        attributions: '<a href="http://www.geoportail.fr/" target="_blank">' + '<img src="https://api.ign.fr/geoportail/api/js/latest/' + 'theme/geoportal/img/logo_gp.gif"></a>'
      })
      var wmtsLayer = new TileLayer({
        source: ignSource,
        minResolution: minResolution,
        maxResolution: maxResolution,
        opacity: layerDef.opacity,
        code: layerDef.id,
        visible: layerDef.visibility
      })
      return wmtsLayer
    },
    getTruncatedResolutions(resolutions, min, max) {
      var i
      var returned = []
      // Comparaison sur des valeurs arrondies car précision variable des données en entrée
      var roundMin = min ? Math.round(min * 10000) / 10000 : resolutions[resolutions.length - 1]
      var roundMax = max ? Math.round(max * 10000) / 10000 : resolutions[0]
      for (i = 0; i < resolutions.length; i++) {
        var resolution = resolutions[i]
        var roundResolution = Math.round(resolution * 10000) / 10000
        if (roundResolution <= roundMax && roundResolution >= roundMin) {
          returned.push(resolution)
        }
      }
      return returned
    },
    getTruncatedWmts3857Resolutions(min, max) {
      return this.getTruncatedResolutions(this.resolutions, min, max)
    },
    getTruncatedMatrixIds(min, max) {
      var matrixIds = []
      var resolutionsOrigin = this.resolutions
      var i = resolutionsOrigin.indexOf(max)
      for (i; i < resolutionsOrigin.indexOf(min); i++) {
        matrixIds.push(i)
      }
      return matrixIds
    },
    getLayerById(id) {
      if (!this.map || !this.map.getLayers) {
        return null
      }
      var i
      for (i = 0; i < this.map.getLayers().getLength(); i++) {
        var layer = this.map.getLayers().item(i)
        if (layer.getProperties().code === id) {
          return layer
        }
      }
      return null
    },
    showToolsBar() {
      // Toolbar
      document.getElementById('toolsBar' + this.criseId).classList.toggle('active')
      _.delay(this.map.updateSize.bind(this.map), 10)
      this.desactivateControls()
    },
    showProcess() {
      this.$refs.showProcess.showModal()
    },
    formatLength(line) {
      var length = getLength(line)
      var output
      if (length > 100) {
        output = Math.round((length / 1000) * 100) / 100 + ' ' + 'km'
      } else {
        output = Math.round(length * 100) / 100 + ' ' + 'm'
      }
      return output
    },
    formatRadius(circle) {
      var radius = circle.getRadius()
      var output
      if (radius > 100) {
        output = 'Rayon: ' + Math.round((radius / 1000) * 100) / 100 + ' km'
      } else {
        output = 'Rayon: ' + Math.round(radius * 100) / 100 + ' m'
      }
      return output
    },
    formatArea(polygon) {
      var area = getArea(polygon)
      var output
      if (area > 10000) {
        output = Math.round((area / 1000000) * 100) / 100 + ' ' + 'km<sup>2</sup>'
      } else {
        output = Math.round(area * 100) / 100 + ' ' + 'm<sup>2</sup>'
      }
      return output
    },
    formatXy(point) {
      var output = toStringXY(point.getCoordinates())
      return output
    },
    addMeasureInteraction() {
      document.getElementsByClassName('measure-container')[0].setAttribute('ctrl-active', 'true')
      this.map.un('click', this.handleMapClick)
      var measureTooltipElement = document.createElement('div')
      measureTooltipElement.className = 'tooltip tooltip-measure'
      let measureTooltip = new OlOverlay({
        element: measureTooltipElement,
        offset: [0, -15],
        positioning: 'bottom-center'
      })
      this.map.addOverlay(measureTooltip)
      var workingLayer = this.getLayerById('workingLayer')
      if (this.selectedRuler && this.selectedRuler !== null) {
        var type = this.selectedRuler === 'Surface' ? 'Polygon' : 'LineString'
      } else {
        return
      }
      let measuringTool = new OlInteractionDraw({
        type: type,
        source: workingLayer.getSource()
      })
      var self = this
      measuringTool.on('drawstart', function(event) {
        // tooltipse
        workingLayer.getSource().clear()
        event.feature.on('change', function(event) {
          var geom = event.target.getGeometry()
          var output
          if (geom.getType() === 'Polygon') {
            output = self.formatArea(geom)
          } else if (geom.getType() === 'LineString') {
            output = self.formatLength(geom)
          }
          measureTooltipElement.innerHTML = output
          measureTooltip.setPosition(event.target.getGeometry().getLastCoordinate())
        })
      })
      measuringTool.on('change:active', function(evt) {
        if (evt.oldValue) {
          // Nettoyage
          workingLayer.getSource().clear()
          measureTooltip.setPosition([0, 0])
        }
      })
      this.map.addInteraction(measuringTool)
      this.measuringTool = measuringTool
      this.measureTooltip = measureTooltip
    },
    activateMeasure(type) {
      this.selectedRuler = type
      this.desactivateControls()
      this.removeMeasureInteraction()
      this.addMeasureInteraction()
    },
    desactivateControls() {
      this.map.un('click', this.handleOpenAttributes)
      this.map.un('click', this.handleMapClick)
      if (this.draw) {
        this.draw.setActive(false)
      } else if (this.snap) {
        this.snap.setActive(false)
      } else if (this.modify) {
        this.modify.setActive(false)
      } else if (this.translate) {
        this.translate.setActive(false)
      }
    },
    removeMeasureInteraction() {
      document.getElementsByClassName('measure-container')[0].removeAttribute('ctrl-active')
      if (this.measuringTool) {
        this.measuringTool.setActive(!this.measuringTool.getActive())
        this.map.removeInteraction(this.measuringTool)
      }
      if (this.measureTooltip) {
        this.measureTooltip.setPosition([0, 0])
      }
    },
    GoInFullscreen: function() {
      var elem = document.getElementById(this.$root.$options.id)
      if (document.fullscreenEnabled || document.webkitFullscreenEnabled || document.mozFullScreenEnabled || document.msFullscreenEnabled) {
        if (elem.requestFullscreen) {
          elem.requestFullscreen()
        } else if (elem.webkitRequestFullscreen) {
          elem.webkitRequestFullscreen()
        } else if (elem.mozRequestFullScreen) {
          elem.mozRequestFullScreen()
        } else if (elem.msRequestFullscreen) {
          elem.msRequestFullscreen()
        }
      }
    },
    addDrawInteractions(typeGeom, natureId) {
      this.map.un('click', this.handleOpenAttributes)
      this.map.un('click', this.handleMapClick)
      this.map.removeInteraction(this.measuringTool)
      this.modify.setActive(false)
      this.translate.setActive(false)
      var self = this
      var workingLayer = this.getLayerById('workingLayer')
      this.map.removeInteraction(this.draw)
      this.map.removeInteraction(this.snap)
      let draw, snap
      typeGeom = typeGeom.toUpperCase()
      switch (typeGeom) {
        case 'LINESTRING':
          typeGeom = 'LineString'
          break
        case 'POLYGON':
          typeGeom = 'Polygon'
          break
        default:
          typeGeom = 'Point'
      }
      draw = new Draw({
        source: workingLayer.getSource(),
        type: typeGeom
      })
      this.map.addInteraction(draw)
      draw.on('drawend', function(evt) {
        var geomL93 = evt.feature.getGeometry().transform(self.proj, Proj.get(self.epsgL93))
        var wktGeomL93 = 'srid=' + self.sridL93 + ';' + new WKT().writeGeometry(geomL93)
        self.$refs['newEvenement'].createCartoEvent(self.criseId, natureId, wktGeomL93)
        self.map.removeInteraction(draw)
      })
      snap = new Snap({
        source: workingLayer.getSource()
      })
      this.map.addInteraction(snap)
      this.draw = draw
      this.snap = snap
    },
    addModifyInteraction() {
      var workingLayer = this.getLayerById('workingLayer')
      var modify = new Modify({
        source: workingLayer.getSource()
      })
      this.modify = modify
      this.modify.setActive(false)
      var self = this
      this.map.addInteraction(this.modify)
      this.modify.on('modifyend', function(evt) {
        self.$refs.toolBar.showUpdateGeom = true
        self.selectedFeature = evt.features.getArray()[0]
      })
    },
    addModifyInteractionFromInput() {
      var workingLayer = this.getLayerById('workingLayer')
      var modify = new Modify({
        source: workingLayer.getSource()
      })
      this.modify = modify
      this.modify.setActive(false)
      this.map.addInteraction(this.modify)
      this.modify.on('modifyend', function() {
        this.$refs.rechercheAnalyse.showValidGeom = true
      })
    },
    addTranslateInteraction() {
      var workingLayer = this.getLayerById('workingLayer')
      var translate = new Translate({
        source: workingLayer.getSource()
      })
      this.translate = translate
      this.translate.setActive(false)
      var self = this
      this.map.addInteraction(this.translate)
      this.translate.on('translateend', function(evt) {
        self.$refs.toolBar.showTranslateGeom = true
        self.selectedFeature = evt.features.getArray()[0]
      })
    },
    activateInteraction(type) {
      var workingLayer = this.getLayerById('workingLayer')
      this.map.un('click', this.handleOpenAttributes)
      this.map.on('click', this.handleMapClick)
      if (this.measuringTool) {
        this.map.removeInteraction(this.measuringTool)
        // Nettoyage
        workingLayer.getSource().clear()
        if (this.measureTooltip !== null) {
          this.measureTooltip.setPosition([0, 0])
        }
      }
      if (type === 'Translate') {
        this.translate.setActive(true)
        this.modify.setActive(false)
      } else if (type === 'Modify') {
        this.modify.setActive(true)
        this.translate.setActive(false)
      }
    },
    handleMapClick(e) {
      axios.get('/remocra/evenements/layer', {
        params: {
          point: e.coordinate[0] + ', ' + e.coordinate[1],
          projection: e.target.getView().getProjection().getCode()
        }
      }).then(response => {
        if (response.data) {
          var features = new GeoJSON().readFeatures(JSON.stringify(response.data))
          if (features.length === 0) {
            this.$notify({
              group: 'remocra',
              title: 'Évènements',
              type: 'warn',
              text: 'Aucun évènement trouvé'
            })
          } else if (features.length === 1) {
            var selectedFeature = features[0]
            this.addToWorkingLayer(selectedFeature)
          } else {
            // On affiche un modal de choix de feature
            this.$refs.choiceFeature.showModal(features)
          }
        }
      }).catch(function(error) {
        console.error('carte', error)
      })
    },
    addToWorkingLayer(selectedFeature) {
      var geom = selectedFeature.getGeometry().transform(Proj.get(this.epsgL93), this.proj)
      selectedFeature.setGeometry(geom)
      this.map.getLayers().forEach(function(layer) {
        if (layer.get('name') !== undefined && layer.get('name') === 'workingLayer') {
          var workingFeature = selectedFeature.clone()
          workingFeature.id_ = selectedFeature.id_
          layer.getSource().addFeature(workingFeature)
        }
      })
    },
    annulModifGeom() {
      this.refreshMap(this.criseId)
      this.$refs.toolBar.showUpdateGeom = false
    },
    annulTranslateGeom() {
      this.refreshMap(this.criseId)
      this.$refs.toolBar.showTranslateGeom = false
    },
    validModifGeom() {
      var geom = this.formatGeomFromMap(this.selectedFeature.getGeometry())
      var formData = {
        geometrie: geom
      }
      axios.post('/remocra/evenements/' + this.selectedFeature.getId() + '/updategeom', formData).then(response => {
        if (response.data.message === 'Clos') {
          this.$notify({
            group: 'remocra',
            title: 'Évènements',
            type: 'error',
            text: "L'évènement est déjà clos."
          })
        }
        this.refreshMap(this.criseId)
        this.$root.$options.bus.$emit(eventTypes.LOAD_EVENEMENTS, {
          crise: this.criseId
        })
        this.$refs.toolBar.showUpdateGeom = false
      }).catch(function(error) {
        console.error('updateGeom', error)
      })
    },
    validTranslateGeom() {
      var geom = this.formatGeomFromMap(this.selectedFeature.getGeometry())
      var formData = {
        geometrie: geom
      }
      axios.post('/remocra/evenements/' + this.selectedFeature.getId() + '/updategeom', formData).then(response => {
        if (response.data.message === 'Clos') {
          this.$notify({
            group: 'remocra',
            title: 'Évènements',
            type: 'error',
            text: "L'évènement est déjà clos."
          })
        }
        this.refreshMap(this.criseId)
        this.$root.$options.bus.$emit(eventTypes.LOAD_EVENEMENTS, {
          crise: this.criseId
        })
        this.$refs.toolBar.showTranslateGeom = false
      }).catch(function(error) {
        console.error('updateGeom', error)
      })
    },
    formatGeomFromMap(geom) {
      var geomL93 = geom.transform(this.proj, Proj.get(this.epsgL93))
      var wktGeomL93 = 'srid=' + this.sridL93 + ';' + new WKT().writeGeometry(geomL93)
      return wktGeomL93
    },
    openAttributes() {
      this.map.on('click', this.handleOpenAttributes)
    },
    addStampedCard() {
      var self = this
      var extent = this.map.getView().calculateExtent()
      extent = Proj.transformExtent(extent, this.map.getView().getProjection().getCode(), Proj.get(this.epsgL93))
      this.map.once('postcompose', function(event) {
        var canvas = event.context.canvas
        self.$refs.stampedCard.makeCard(canvas, extent)
      })
      this.map.renderSync()
    },
    handleOpenAttributes(e) {
      axios.get('/remocra/evenements/layer', {
        params: {
          point: e.coordinate[0] + ', ' + e.coordinate[1],
          projection: e.target.getView().getProjection().getCode()
        }
      }).then(response => {
        if (response.data) {
          var features = new GeoJSON().readFeatures(JSON.stringify(response.data))
          if (features.length === 0) {
            this.$notify({
              group: 'remocra',
              title: 'Évènements',
              type: 'warn',
              text: 'Aucun évènement trouvé'
            })
          } else if (features.length === 1) {
            var selectedFeature = features[0]
            this.addToWorkingLayer(selectedFeature)
            this.$refs['newEvenement'].modifyEvent(this.criseId, selectedFeature.getId(), selectedFeature.getProperties().nature)
          } else {
            // On affiche un modal de choix de feature
            this.$refs.choiceFeature.showModal(features)
          }
        }
      }).catch(function(error) {
        console.error('carte', error)
      })
    },
    /**
     *  Active ou désactive le contrôle Info
     * @param activate forcer l'activation ou la désactivation (sinon fonctionnement "toggle")
     */
    activateShowInfo(activate) {
      let isActive = document.getElementById('infoBtn' + this.criseId).getAttribute('ctrl-active') !== null
      if (activate === true || !isActive) {
        document.getElementById('infoBtn' + this.criseId).setAttribute('ctrl-active', 'true')
        this.map.on('click', this.handleOpenInfo)
      } else if (activate === false || isActive) {
        document.getElementById('infoBtn' + this.criseId).removeAttribute('ctrl-active')
        this.map.un('click', this.handleOpenInfo)
      }
    },
    handleOpenInfo(e) {
      axios.get('/remocra/evenements/layer', {
        params: {
          point: e.coordinate[0] + ', ' + e.coordinate[1],
          projection: e.target.getView().getProjection().getCode()
        }
      }).then(response => {
        if (response.data) {
          var features = new GeoJSON().readFeatures(JSON.stringify(response.data))
          if (features.length === 0) {
            this.$notify({
              group: 'remocra',
              title: 'Évènements',
              type: 'warn',
              text: 'Aucun évènement trouvé'
            })
          } else if (features.length === 1) {
            var selectedFeature = features[0]
            this.addToWorkingLayer(selectedFeature)
            this.$refs.showInfo.showModal(selectedFeature)
          } else {
            // On affiche un modal de choix de feature
            // this.$refs.choiceFeature.showModal(features)
          }
        }
      }).catch(function(error) {
        console.error('carte', error)
      })
    },
    zoomToGeom(geometrie) {
      let geom = new WKT().readGeometry(geometrie, {
        dataProjection: this.epsgL93,
        featureProjection: this.proj
      })
      if (geom.getType() === 'Point') {
        this.map.getView().setCenter(geom.getCoordinates())
        this.map.getView().setResolution(0.5)
      } else {
        this.map.getView().fit(geom.getExtent(), {
          nearest: true
        })
      }
    },
    zoomToExtent(geometrie) {
      this.map.getView().fit(new GeoJSON().readGeometry(geometrie, {
        dataProjection: this.epsgL93,
        featureProjection: this.proj
      }).getExtent(),
      {
        nearest: true
      })
    },
    refreshMap(crise) {
      if (crise === this.criseId) {
        var workingLayer = this.getLayerById('workingLayer')
        var wmsLayer = this.getLayerById('893bb7520e7fb036d665661847628994')
        workingLayer.getSource().clear()
        //  Rafraîchissement de la couche WMS des évènements
        // wmsLayer.getSource().refresh()
        wmsLayer.getSource().updateParams({
          time: Date.now()
        })
      }
    },
    zoomIn() {
      this.map.getView().setZoom(this.map.getView().getZoom() + 1)
    },
    zoomOut() {
      this.map.getView().setZoom(this.map.getView().getZoom() - 1)
    },
    zoomPrev() {
      if (this.navigation.idx > 0) {
        this.navigation.btns = true
        this.navigation.idx--
        let state = this.navigation.stack[this.navigation.idx]
        this.map.getView().animate(state)
      }
    },
    zoomNext() {
      if (this.navigation.stack.length - this.navigation.idx <= 1) {
        return
      }
      this.navigation.btns = true
      if (this.navigation.idx < 10) {
        this.navigation.idx++
      }
      let state = this.navigation.stack[this.navigation.idx]
      this.map.getView().animate(state)
    },
    updateMapFilters(id, jsonFilters) {
      var wmsLayer = this.getLayerById('893bb7520e7fb036d665661847628994')
      if (wmsLayer) {
        wmsLayer.getSource().updateParams({
          filter: jsonFilters
        })
      }
    },
    inputGeom(typeGeom, index) {
      typeGeom = typeGeom.toUpperCase()
      var geometryFunction
      switch (typeGeom) {
        case 'POINT':
          typeGeom = 'Point'
          break
        case 'LINESTRING':
          typeGeom = 'LineString'
          break
        case 'POLYGON':
          typeGeom = 'Polygon'
          break
        case 'CIRCLE':
          typeGeom = 'Circle'
          break
        case 'BOX':
          typeGeom = 'Circle'
          geometryFunction = createBox()
          break
        default:
          typeGeom = 'Point'
      }
      this.map.un('click', this.handleMapClick)
      var measureTooltipElement = document.createElement('div')
      measureTooltipElement.className = 'tooltip tooltip-measure'
      var measureTooltip = new OlOverlay({
        element: measureTooltipElement,
        offset: [0, -15],
        positioning: 'bottom-center'
      })
      this.map.addOverlay(measureTooltip)
      // On crée un layer par input
      var workingLayer = this.getLayerById('input' + index)
      if (workingLayer === null) {
        workingLayer = this.createWorkingLayer('input' + index)
      }
      var measuringTool = new OlInteractionDraw({
        type: typeGeom,
        geometryFunction: geometryFunction,
        source: workingLayer.getSource()
      })
      var self = this
      measuringTool.on('drawstart', function(event) {
        // tooltipse
        event.feature.on('change', function(event) {
          var geometrie = event.target.getGeometry()
          var output
          if (geometrie.getType() === 'Polygon' || geometrie.getType() === 'Box') {
            output = self.formatArea(geometrie)
          } else if (geometrie.getType() === 'LineString') {
            output = self.formatLength(geometrie)
          } else if (geometrie.getType() === 'Circle') {
            output = self.formatRadius(geometrie)
          } else if (geometrie.getType() === 'Point') {
            output = self.formatXy(geometrie)
          }
          measureTooltipElement.innerHTML = output
          measureTooltip.setPosition(event.target.getGeometry().getLastCoordinate())
        })
      })
      measuringTool.on('drawend', function(evt) {
        self.$refs.rechercheAnalyse.showValidGeom = index
        self.selectedFeature = evt.feature
        self.map.removeInteraction(measuringTool)
      })
      measuringTool.id = 'tool' + index
      measureTooltip.id = 'tooltip' + index
      this.map.addInteraction(measuringTool)
      this.inputGeoms.push(measuringTool, measureTooltip)
    },
    annulGeom(index) {
      var layer = this.getLayerById('input' + index)
      var features = layer.getSource().getFeatures()
      // layer.getSource().clear()*/
      _.forEach(features, feature => {
        if (feature.ol_uid === this.selectedFeature.ol_uid) {
          if (this.oldSelectedFeatureGeom !== null) {
            feature.setGeometry(this.oldSelectedFeatureGeom)
            this.validGeom(index)
          } else {
            layer.getSource().removeFeature(feature)
          }
        }
      })
      this.removeMeasureInputGeomInteraction(index)
      this.removeModifInputGeomInteraction(index)
      this.removeSelectInputGeomInteraction(index)
      this.$refs.rechercheAnalyse.showValidGeom = null
    },
    validGeom(index) {
      _.forEach(this.$refs.rechercheAnalyse.$refs, input => {
        if (input[0].id === 'input' + index) {
          var multigeom = null
          var geoms = []
          var geom = null
          var layer = this.getLayerById('input' + index)
          var features = layer.getSource().getFeatures()
          _.forEach(features, function(feature) {
            geom = feature.clone().getGeometry()
            if (geom.getType() === 'Circle') {
              geom = Polygon.fromCircle(geom)
            }
            geoms.push(geom)
          })
          if (geoms.length > 1) {
            if (geoms[0].getType() === 'Point') {
              multigeom = this.formatGeomFromMap(new MultiPoint(geoms))
            } else if (geoms[0].getType() === 'LineString') {
              multigeom = this.formatGeomFromMap(new MultiLineString(geoms))
            } else {
              multigeom = this.formatGeomFromMap(new MultiPolygon(geoms))
            }
          } else {
            console.log(geoms[0])
            multigeom = this.formatGeomFromMap(geoms[0])
          }
          input[0].value = multigeom
        }
      })
      this.$refs.rechercheAnalyse.showValidGeom = null
      this.removeModifInputGeomInteraction(index)
      this.removeMeasureInputGeomInteraction(index)
      this.removeSelectInputGeomInteraction(index)
    },
    modifyGeom(index) {
      var workingLayer = this.getLayerById('input' + index)
      if (workingLayer && workingLayer !== null) {
        var selectInput = new Select({
          layers: [workingLayer]
        })
        this.map.addInteraction(selectInput)
        var modifyInput = new Modify({
          features: selectInput.getFeatures()
        })
        var self = this
        modifyInput.id = 'modif' + index
        selectInput.id = 'select' + index
        this.inputGeoms.push(modifyInput, selectInput)
        this.map.addInteraction(modifyInput)
        modifyInput.on('modifyend', function(evt) {
          self.$refs.rechercheAnalyse.showValidGeom = index
          self.selectedFeature = evt.features.getArray()[0]
        })
        modifyInput.on('modifystart', function(evt) {
          self.oldSelectedFeatureGeom = evt.features.getArray()[0].clone().getGeometry()
        })
      }
    },
    deleteGeom(index) {
      this.removeMeasureInputGeomInteraction(index)
      this.removeModifInputGeomInteraction(index)
      this.getLayerById('input' + index).getSource().clear()
      this.$refs.rechercheAnalyse.showValidGeom = false
      _.forEach(this.$refs.rechercheAnalyse.$refs, input => {
        if (input[0].id === 'input' + index) {
          input[0].value = null
        }
      })
    },
    removeMeasureInputGeomInteraction(index) {
      _.forEach(this.inputGeoms, input => {
        if (input.id === 'tool' + index) {
          input.setActive(!input.getActive())
          this.map.removeInteraction(input)
        }
        if (input.id === 'tooltip' + index) {
          input.setPosition([0, 0])
        }
      })
    },
    removeModifInputGeomInteraction(index) {
      _.forEach(this.inputGeoms, input => {
        if (input.id === 'modif' + index) {
          this.map.removeInteraction(input)
        }
      })
    },
    removeSelectInputGeomInteraction(index) {
      _.forEach(this.inputGeoms, input => {
        if (input.id === 'select' + index) {
          this.map.removeInteraction(input)
        }
      })
    },
    openModalImportFile() {
      this.$refs.modalImportFile.openModal()
    },
    modalImportFileValider(value) {
      // on récupère les données saisies dans la fenêtre modale
      var donneesSaisies = value
      // on récupère les infos concernant le layer
      var infosLayer = {
        id: donneesSaisies['layerCode'],
        libelle: donneesSaisies['layerName'],
        visibility: true
      }
      // on recherche le groupe de layer "Autres couches"
      var obj = this.legend.items.find(obj => obj.libelle === 'Fichiers importés')
      // si le groupe de layer existe
      if (obj && obj !== null) {
        // on vérifie que la légende contient au moins un élément
        var indexAutresCouches
        if (this.legend.items.length !== 0) {
          indexAutresCouches = this.legend.items.length - 1
          this.legend.items[indexAutresCouches].items.push(infosLayer)
          // sinon on place notre élément à l'emplacement 0
        } else {
          indexAutresCouches = 0
          this.legend.items[indexAutresCouches].items.push(infosLayer)
        }
      } else {
        // on créé ce groupe en ajoutant notre nouveau layer
        var importLayers = {
          libelle: 'Fichiers importés',
          items: [infosLayer]
        }
        this.legend.items.push(importLayers)
      }
      // on ajoute la couche créée, grâce au fichier, à la map déjà présente
      this.map.addLayer(donneesSaisies['newLayer'])
      // on recentre la vue sur les features ajoutées
      this.map.getView().fit(donneesSaisies['newLayer'].getSource().getExtent())
    },
    deleteImportLayer(idLayer) {
      // on récupère tous les layers de la map
      var layers = this.map.getLayers().getArray()
      for (let layer of layers) {
        if (layer.get('code') === idLayer) {
          // suppression du layer de la map
          this.map.removeLayer(layer)
          // on récupère l'index des "fichiers importés"
          var indexAutresCouches = this.legend.items.length - 1
          // on récupère le tableau de légende des couches
          var tabLegendFileImport = this.legend.items[indexAutresCouches].items
          // on recherche l'index des informations de la couche que l'on a supprimé
          var indexLayer = tabLegendFileImport.findIndex(obj => obj.id === idLayer)
          // on supprime les informations du layer supprimé
          tabLegendFileImport.splice(indexLayer, 1)
          // si la partie "fichiers importés" des couches est vide
          if (tabLegendFileImport.length === 0) {
            // alors on supprime le groupe "fichiers importés"
            this.legend.items.splice(indexAutresCouches, 1)
          }
        }
      }
    },
    // Affichage du tableau de données (et de la map si requête spatiale)
    showTabDonnees(header, data, spatial, idSelection) {
      if (spatial) {
        this.$refs.MapFeatures.eventDrawMapFeatures(idSelection, this.map)
        this.setDisplay('SPLIT')
      } else {
        this.setDisplay('TABLE_ONLY')
      }
      this.$refs.TableauDonnees.eventDrawTableau(header, data)
      document.getElementById('boutonToggleTableau' + this.criseId).style.visibility = 'visible'
    },
    // Détermine le mode d'affichage de la map et du tableau de données
    setDisplay(type) {
      switch (type) {
        case 'MAP_ONLY': // Seulement la map
          document.getElementById('mapDiv' + this.criseId).style.display = ''
          // document.getElementById('mapDiv' + this.criseId).style.height = '100%'
          document.getElementById('tableauDiv' + this.criseId).style.display = 'none'
          this.map.updateSize()
          break
        case 'TABLE_ONLY': // Seulement le tableau
          document.getElementById('mapDiv' + this.criseId).style.display = 'none'
          document.getElementById('tableauDiv' + this.criseId).style.display = ''
          // document.getElementById('tableauDiv' + this.criseId).style.height = '100%'
          this.$refs.TableauDonnees.setPageSize(37)
          break
        case 'SPLIT': // Map et tableau
          document.getElementById('mapDiv' + this.criseId).style.display = ''
          document.getElementById('tableauDiv' + this.criseId).style.display = ''
          /* document.getElementById('mapDiv' + this.criseId).style.height = '60%'
          document.getElementById('tableauDiv' + this.criseId).style.height = '40%' */
          this.map.updateSize()
          this.$refs.TableauDonnees.setPageSize(10)
          break
      }
      this.displayType = type
    },
    toggleTableau() {
      document.getElementById('mapDiv' + this.criseId).style.display = ''
      // document.getElementById('mapDiv' + this.criseId).style.height = '100%'
      document.getElementById('tableauDiv' + this.criseId).style.display = 'none'
      this.map.updateSize()
    }
  }
}
</script>

<style scoped>
.ctrlImportLayer {
  color: transparent;
  background-color: currentColor;
  border-color: currentColor;
}

.ctrlImportLayer:hover {
  border-color: #9d9d9d;
  background-color: #f3f3f3;
}

.tableauDiv {
  bottom: 1%;
  width: 60%;
  left: 25%;
  position: absolute;
  z-index: 1000;
}

.col {
  max-height: 100%;
}

button.close {
  width: 30px;
  height: 30px;
  color: #030303;
  opacity: inherit;
  background-color: #f7f7f7;
  border-radius: 50%;
  position: absolute;
  top: -15px;
  right: -10px;
  border: solid 1px #d3d3d3;
}

.left_content {
  position: absolute;
  width: 350px;
  z-index: 900;
  left: 10px;
  top: 10px;
  -webkit-margin-start: inherit;
  margin-inline-start: inherit;

}
.right_content {
  position: absolute;
  width: 350px;
  z-index: 800;
  right: 10px;
  top: 10px;
  -webkit-margin-end: inherit;
  margin-inline-end: inherit;
}
.top_content {
  position: absolute;
  z-index: 900;
  width: 45%;
  right: 28%;
  top: 10px;
  background: #f4f4f4;
  border-radius: 3px;

}
.accord7{
  padding: 15px;
}
.mapDiv{
  height: 100%;
}
</style>
