<template>
  <div>
    <b-dropdown size="sm" :class="{'bg-transparent' : true, 'activrated' : toggleState}">
      <template v-slot:button-content>
        <b-btn id="measureTools" :class="{'activated': toggleState}"><img title="Outils de mesure" :src="getIcon()" width="16" height="16"></b-btn>
      </template>
    <b-dropdown-item href="#" @click="activateMeasure('Distance')" :class="{'activated' : selectedRuler == 'Distance'}"><img src='/remocra/static/img/ruler.png'> Distance</b-dropdown-item>
    <b-dropdown-item href="#" @click="activateMeasure('Surface')" :class="{'activated' : selectedRuler == 'Surface'}"><img src='/remocra/static/img/ruler_square.png'> Surface</b-dropdown-item>
  </b-dropdown>
  </div>
</template>

<script>

import * as eventTypes from '../../bus/event-types.js'
import OlOverlay from 'ol/Overlay.js'
import OlInteractionDraw from 'ol/interaction/Draw.js'
import {
  getArea,
  getLength
} from 'ol/sphere.js'
import _ from 'lodash'

export default {
  name: 'Measures',
  data() {
    return {
      selectedRuler: null,
      measuringTool: null,
      measuringToolOverlays: [],
      toggleState: false
    }
  },
  props: {
    map: {
      required: true,
      type: Object
    }
  },

  created: function() {
    this.$root.$options.bus.$on(eventTypes.OLMAP_MEASURES_TOGGLE, this.onToggle);
  },

  mounted: function() {

  },

  methods: {

    activateMeasure(type) {
      if(this.selectedRuler !== null && this.selectedRuler != type) { // Outils de mesure activés, changement de type
        this.removeMeasureInteraction();
        this.selectedRuler = type;
        this.addMeasureInteraction();
      } else { // Activation des outils de mesure
        this.selectedRuler = type;
        this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_TOGGLEBUTTON, "mesures");
      }
    },

    onToggle(state) {
      if(state) {
        this.addMeasureInteraction();
      } else {
        this.removeMeasureInteraction();
      }
      this.toggleState = state;
    },

    addMeasureInteraction() {
      var workingLayer = this.getLayerById('workingLayer');
      if (this.selectedRuler && this.selectedRuler !== null) {
        var type = this.selectedRuler === 'Surface' ? 'Polygon' : 'LineString'
      } else {
        return
      }
      this.measuringTool = new OlInteractionDraw({
        type: type,
        source: workingLayer.getSource()
      });

      this.measuringTool.on('drawstart', event => {
        // Création tooltip
        var measureTooltipElement = document.createElement('div');
        measureTooltipElement.className = 'measureTooltip';
        var measureTooltip = new OlOverlay({
          element: measureTooltipElement,
          offset: [0, -15],
          positioning: 'bottom-center',
        });
        this.measuringToolOverlays.push(measureTooltip);
        this.map.addOverlay(measureTooltip);

        event.feature.on('change', event => {
          var geom = event.target.getGeometry()
          var output
          if (geom.getType() === 'Polygon') {
            output = this.formatArea(geom)
          } else if (geom.getType() === 'LineString') {
            output = this.formatLength(geom)
          }
          measureTooltipElement.innerHTML = output;
          measureTooltip.setPosition(event.target.getGeometry().getLastCoordinate());
        })
      })
      this.measuringTool.on('change:active', function(evt) {

        if (evt.oldValue) {
          workingLayer.getSource().clear()
        }
      })
      this.map.addInteraction(this.measuringTool);

    },

    removeMeasureInteraction() {
      this.map.removeInteraction(this.measuringTool);
      _.forEach(this.measuringToolOverlays, ol => {
        this.map.removeOverlay(ol);
      })

      this.selectedRuler = null;
      var workingLayer = this.getLayerById('workingLayer');
      workingLayer.getSource().clear();
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

    formatArea(polygon) {
      var area = getArea(polygon)
      var output
      if (area > 10000) {
        output = Math.round((area / 10000) * 100) / 100 + ' ' + 'ha'
      } else {
        output = Math.round(area * 100) / 100 + ' ' + 'm<sup>2</sup>'
      }
      return output
    },

    getLayerById(id) {
      if (!this.map || !this.map.getLayers) {
        return null
      }
      var i
      for (i = 0; i < this.map.getLayers().getLength(); i++) {
        var layer = this.map.getLayers().item(i)
        if (layer.getProperties().code == id) {
          return layer
        }
      }
      return null
    },

    getIcon() {
      if(this.selectedRuler === 'Surface') {
        return "/remocra/static/img/ruler_square.png";
      }
      return "/remocra/static/img/ruler.png"
    }
  }
};
</script>

<style>
#measureTools {
  background: none;
  border: none;
  margin-top: 0!important;
  padding-bottom: 6px;
  padding-top: 6px;
}

.btn-secondary:focus {
  box-shadow: none !important;
}

.activated {
  background-color: rgb(200,200,200) !important;
}

.dropdown {
  max-height: 60px;
  margin-right: 5px;
}

.dropdown-toggle {
  background-color: transparent!important;
  border: none!important;
  padding: 0 0 0 0!important;
}

.dropdown-toggle::after{
  border-top-color: black!important;
}
.dropdown-item {
  border: 1px solid transparent;
  border-radius: 3px;
}

.dropdown-item:not(.selectedMeasureTool):hover {
  background-color: #e6e6e6;
  border-color: #9d9d9d;
  cursor: pointer;
}

.dropdown ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
}

.dropdown-submenu {
  position: relative;
  line-height: 1;
}

.dropdown-submenu>.dropdown-menu {
  top: 0;
  left: 100%;
  margin-top: -6px;
  margin-left: -1px;
  border-radius: 0 6px 6px 6px;
  background-color: #f0f0f0;
}

.dropdown-submenu:hover>.dropdown-menu {
  display: block;
}

.dropdown-item.active,
.dropdown-item:active,
.dropdown-submenu>.dropdown-menu>.dropdown-item>a {
  color: #212529;
}

.dropdown-submenu>.dropdown-menu>.dropdown-item>a:hover {
  text-decoration: none;
}

#iconBtnImportFile {
  height: 25px;
  width: 25px;
}

.geom-point:before {
  content: url('/remocra/static/img/pencil_point.png');
  margin-right: 7px;
}

.geom-linestring:before {
  content: url('/remocra/static/img/pencil_ligne.png');
  margin-right: 7px;
}

.geom-polygon:before {
  content: url('/remocra/static/img/pencil_polygone.png');
  margin-right: 7px;
}

.measureTooltip {
  font-weight: bold;
  position: relative;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 4px;
  color: white;
  padding: 10px 8px;
  opacity: 5;
  white-space: nowrap;
  z-index: 1070;
  display: block;
  margin: 0;
  font-style: normal;
  font-weight: 400;
  line-height: 1.5;
  font-family: "Segoe UI",Roboto,"Helvetica Neue",Arial
}
</style>
