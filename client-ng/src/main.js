// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import GestCrise from './App'
import Fiche from './components/Fiche'
// import router from './router'
import BootstrapVue from 'bootstrap-vue'
import rate from 'vue-rate'
import {
  Popover
} from 'bootstrap-vue/es/components'
import InputTag from 'vue-input-tag'
import Notifications from 'vue-notification'
import velocity from 'velocity-animate'

// Feuilles de style
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-vue/dist/bootstrap-vue.min.css'
import 'ol/ol.css'
import 'v-autocomplete/dist/v-autocomplete.css'

Vue.config.productionTip = false 

Vue.use(BootstrapVue)
Vue.use(rate)
Vue.use(Popover)
Vue.use(Notifications, {
  velocity
})
Vue.component('input-tag', InputTag)

// Crise
const criseBuildMap = function(id, idCrise) {
  var v = new Vue({
    el: '#crise-' + id,
    id: id,
    crise: idCrise,
    bus: new Vue(),
    components: {
      GestCrise
    },
    template: '<GestCrise :crise=' + idCrise + ' />'
  })
  return v
}

// PEI
const peiBuildFiche = function(el, data) {
  var v = new Vue({
    el,
    idPei: data.id,
    bus: new Vue(),
    components: {
      Fiche
    },
    template: "<Fiche :newVisite="+data.newVisite+" :idHydrant=" + data.id + " codeHydrant=" + data.code + "  geometrie='" + data.geometrie + "' />"
  })
  return v
}

export {
  criseBuildMap,
  peiBuildFiche
}
