<template>
<div></div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'
import WKT from 'ol/format/WKT.js'
export default {
  name: 'MapFeatures',
  data() {
    return {}
  },
  methods: {
    // Lorsqu'une requête spatiale est jouée, on affiche sur la map la localisation de notre sélection
    eventDrawMapFeatures(idSelection, map) {
      axios.get('/remocra/requetemodele/reqmodetendu/' + idSelection).then((response) => {
        var sridBounds = response.data.message.split(';')
        var sridComplet = sridBounds[0]
        var srid = sridComplet.split('=')[1]
        var bounds = sridBounds[1]
        var feature = new WKT().readGeometry(bounds)
        var extent = feature.transform('EPSG:' + srid, map.getView().getProjection()).getExtent()
        _.forEach(map.getLayers(), function(layer) {
          if (Array.isArray(layer)) {
            _.forEach(layer, function(i) {
              if (i.values_.code === 'selection') {
                i.getSource().updateParams({
                  'VIEWPARAMS': 'SELECTION_ID:' + idSelection
                }) // Update paramètres
                map.getView().fit(extent, {
                  nearest: true
                })
              }
            })
          }
        })
      }).catch(function(error) {
        console.error('requetemodele', error)
      })
    }
  }
}
</script>

<style scoped>

</style>
