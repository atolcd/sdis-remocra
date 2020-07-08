<template>
  <b-container id="olMap" class="map-container" style="max-width:100%; max-height: 100%;">



    <div id="map" class="map">
      <div>
        <slot name="toolbar" v-if="mapCreated">
          <ToolBar :map="map" :olMap="this" @zoomToGeom="zoomToGeom" ref="toolBar"></ToolBar>
        </slot>
      </div>

      <div>
        <slot name="couches" v-if="mapCreated">
          <Couches :map="map" :couchesJSONPath="couchesJSONPath" ref="couches"></Couches>
        </slot>
      </div>

      <slot name="specifique"></slot>
    </div>


  </b-container>
</template>

<script>

import _ from 'lodash'
import Map from 'ol/Map.js'
import View from 'ol/View.js'
import {
  defaults as defaultControls
} from 'ol/control.js'
import {
  register
} from 'ol/proj/proj4.js'
import proj4 from 'proj4'
import ToolBar from './ToolBar.vue'
import Couches from './Couches.vue'
import WKT from 'ol/format/WKT.js'

export default {
  name: 'OlMap',
  components: {
    ToolBar,
    Couches
  },

  props: {
    couchesJSONPath: {
      type: String,
      required: false,
      default: '/remocra/ext-res/layers/carte.json'
    }
  },

  data() {
    return {
      map: {
        type: Object,
        default: {}
      },
      mapCreated: false,
      sridL93: 2154,
      proj: null,
      epsgL93: null,
      extent: [
        256805.64470225616,
        6249216.947446961,
        265705.78118321137,
        6252690.054919669
      ],
      workingLayer: null,

    }
  },


  mounted: function() {
    this.map = new Map({
      target: 'map',
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
    });

    this.proj = this.map.getView().getProjection()
    this.epsgL93 = 'EPSG:' + this.sridL93;
    proj4.defs(this.epsgL93, '+proj=lcc +lat_1=49 +lat_2=44 +lat_0=46.5 +lon_0=3 +x_0=700000 +y_0=6600000 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs')
    register(proj4);
    this.map.getView().fit(this.extent);
    this.mapCreated = true;

    _.delay(this.map.updateSize.bind(this.map), 10)
  },

  methods: {


    // Zoom sur une géométrie donnée
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


  }
};
</script>

<style scoped>
#map {
  width: 100%;
  position: relative;
}
</style>
