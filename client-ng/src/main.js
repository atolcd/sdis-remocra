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
import _ from 'lodash'

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

let vm = []
const buildVue = function buildVue(id, idCrise) {
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
  vm.push(v)
  return v
}

const buildFiche = function buildFiche(fiche) {
  var v = new Vue({
    el: '#'+fiche.el.id + '-body',
    bus: new Vue(),
    id: fiche.hydrant.data.id,
    hydrant: fiche.hydrant.data,
    components: {
      Fiche
    },
    template: "<Fiche :idHydrant="+fiche.hydrant.data.id+" codeHydrant="+fiche.hydrant.data.code+"  geometrie='"+fiche.hydrant.data.geometrie+"' />"
  })
  vm.push(v)
  return v
}

const destroyVue = function destroyVue(idCrise) {
  _.forEach(vm, v => {
    if (v.$options.crise === idCrise) {
      v.$destroy()
    }
  })
}

export {
  buildVue,
  buildFiche,
  destroyVue
}