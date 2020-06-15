<template>
  <div>
    <b-btn class=" text-start my-1 measure-container ctrl" id="measureTools" @click="activateMeasureInteraction"><img title="Outils de mesure" src="/remocra/static/img/ruler.png"></b-btn>
    <b-popover class="dropdown-menu" placement="bottomright" ref='popovermesure' container="map" target="measureTools">
      <div>
        <b-btn class="dropdown-item" @click="activateMeasure('Distance')"><img src='/remocra/static/img/ruler.png'> Distance</b-btn>
      </div>
      <div>
        <b-btn class="dropdown-item" @click="activateMeasure('Surface')"><img src='/remocra/static/img/ruler_square.png'> Surface</b-btn>
      </div>
    </b-popover>
  </div>
</template>

<script>

import OlOverlay from 'ol/Overlay.js'
import OlInteractionDraw, {
  createBox
} from 'ol/interaction/Draw.js'
import {
  getArea,
  getLength
} from 'ol/sphere.js'
import _ from 'lodash'

export default {
  name: 'Measures',
  data() {
    return {
      selectedRuler: null
    }
  },
  props: {
    map: {
      required: true,
      type: Object
    }
  },

  mounted: function() {

  },

  methods: {
    activateMeasureInteraction() {
      console.log("activate");
    },

    activateMeasure(type) {
      console.log("Mesure "+type);
      this.selectedRuler = type;
      this.addMeasureInteraction();
    },

    addMeasureInteraction() {
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
    formatMultiLength(multiLine) {
      var length = 0
      _.forEach(multiLine.getLineStrings(), line => {
        length = length + getLength(line)
      })
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
        if (layer.getProperties().code === id) {
          return layer
        }
      }
      return null
    }
  }
};
</script>

<style scoped>
#measureTools {
  background: none;
  border: none;
}
.dropdown-item {
  border: 1px solid transparent;
  border-radius: 3px;
}

.dropdown-item:hover {
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
</style>
