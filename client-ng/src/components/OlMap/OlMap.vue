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
          <Couches :map="map" :cleIgn="cleIgn" :couchesJSONPath="couchesJSONPath" :couchesViewParams="couchesViewParams" ref="couches"></Couches>
        </slot>
      </div>

      <slot name="specifique"></slot>

      <ModaleInfo id="modaleInfo" :feature="modaleInfoFeature"></ModaleInfo>
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
  defaults as defaultInteractions
} from 'ol/interaction.js'
import {
  register
} from 'ol/proj/proj4.js'
import proj4 from 'proj4'
import ToolBar from './ToolBar.vue'
import Couches from './Couches.vue'
import ModaleInfo from './ModaleInfo.vue'
import WKT from 'ol/format/WKT.js'
import * as eventTypes from '../../bus/event-types.js'
import { getSrid } from '../utils/FunctionsUtils.js'

export default {
  name: 'OlMap',
  components: {
    ToolBar,
    Couches,
    ModaleInfo
  },

  props: {
    cleIgn: {
      type: String,
      required: false
    },
    couchesJSONPath: {
      type: String,
      required: false,
      default: '/remocra/ext-res/layers/carte.json'
    },
    couchesViewParams: {
      type: Array,
      required: false
    }
  },

  data() {
    return {
      map: {
        type: Object,
        default: {}
      },
      mapCreated: false,
      srid: null,
      proj: null,
      epsg: null,
      extent: [
        256805.64470225616,
        6249216.947446961,
        265705.78118321137,
        6252690.054919669
      ],
      workingLayer: null,
      modaleInfoFeature: null
    }
  },

  created: function() {
    this.$root.$options.bus.$on(eventTypes.OLMAP_SHOW_MODALEINFO, (features) => {
      this.modaleInfoFeature = features[0];
      this.$nextTick(() => {
        this.$bvModal.show("modaleInfo");
      });
    });
  },

  destroyed() {
    this.$root.$options.bus.$off(eventTypes.OLMAP_SHOW_MODALEINFO);
  },

  mounted: async function() {
    this.srid = await getSrid();

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
      interactions: defaultInteractions({
        dragPan: true
      }),
      view: new View({
        projection: 'EPSG:3857',
        center: [0, 0],
        zoom: 2
      })
    });

    this.proj = this.map.getView().getProjection()
    this.epsg = 'EPSG:' + this.srid;
    switch(this.srid) {
        case 2154:
            proj4.defs(this.epsg, '+proj=lcc +lat_1=49 +lat_2=44 +lat_0=46.5 +lon_0=3 +x_0=700000 +y_0=6600000 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs')
            break;
        case 2972: // RGFG95 UTM zone 22N - Guyane
            proj4.defs(this.epsg, '+proj=utm +zone=22 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs +type=crs')
            break;
        case 32620: // WGS 84 UTM zone 20N - Guadeloupe
            proj4.defs(this.epsg,'+proj=utm +zone=20 +datum=WGS84 +units=m +no_defs +type=crs');
            break;
    }
    register(proj4);
    this.map.getView().fit(this.extent);
    this.mapCreated = true;

    _.delay(this.map.updateSize.bind(this.map), 10)
  },

  methods: {


    // Zoom sur une géométrie donnée
    zoomToGeom(geometrie) {
      let geom = new WKT().readGeometry(geometrie, {
        dataProjection: this.epsg,
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

    getLayers() {
      if (!this.map || !this.map.getLayers) {
        return null
      }
      var layers = [];
      for (var i = 0; i < this.map.getLayers().getLength(); i++) {
        layers.push(this.map.getLayers().item(i))
      }
      return layers
    }
  }
};
</script>

<style scoped>
#olMap, #map {
  height: 700px;
  width: 100%;
  position: relative;
}

#map.fullscreen{
  height: 100%;
}
</style>
