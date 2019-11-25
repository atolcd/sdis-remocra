// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import GestCrise from './App'
import Fiche from './components/Fiche'
import Contacts from './components/Contacts'
import FicheIndisponibiliteTemporaire from './components/FicheIndisponibiliteTemporaire'
import DebitSimultaneSelection from './components/DebitSimultaneSelection'
import DebitSimultaneFiche from './components/DebitSimultaneFiche'
// import router from './router'
import BootstrapVue from 'bootstrap-vue'
import Courrier from './components/Courrier'
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
    template: "<Fiche  title='" + data.title + "' :newVisite=" + data.newVisite + " :idHydrant=" + data.id + " codeHydrant=" + data.code + "  geometrie='" + data.geometrie + "' />"
  })
  return v
}

//contacts
const buildContacts = function(el, data) {
  var v = new Vue({
    el,
    bus: new Vue(),
    components: {
      Contacts
    },
    template: "<Contacts :id=" + data.id + " title='" + data.title + "' nom='" + data.nom + "' />"
  })
  return v
}


//fiche indispo temp
const indispoTempBuildFiche = function(el, data) {
  var v = new Vue({
    el,
    bus: new Vue(),
    components: {
      FicheIndisponibiliteTemporaire
    },
    template: "<FicheIndisponibiliteTemporaire idIndispoTemp=" + data.idIndispoTemp + " tabIdPeiSelected=" + data.tabIdPeiSelected + " tabNumPeiSelected='" + data.tabNumPeiSelected + "'/>"
  })
  return v
}

const debitSimultaneSelection = function(el, data) {
  var v = new Vue({
    el,
    bus: new Vue(),
    components: {
      DebitSimultaneSelection
    },
    template: "<DebitSimultaneSelection  typeSelection='" + data.typeSelection + "' debitsData='" + encodeURI(JSON.stringify(data.comboDebits)) + "' />"
  })
  return v
}

// PEI
const debitSimultaneFiche = function(el, data) {
  var v = new Vue({
    el,
    bus: new Vue(),
    components: {
      DebitSimultaneFiche
    },
    template: "<DebitSimultaneFiche idDebitSimultane=" + data.idDebitSimultane + " dataOnCreate='" + JSON.stringify(data.dataOnCreate) + "' vitesseEau='" + data.vitesseEau + "'/>"
  })
  return v
}

//module de courrier
const buildCourrier = function(el){
  var v = new Vue({
  el,
  bus: new Vue(),
  components: {
      Courrier
  },
  template: "<Courrier />"
  })
  return v
}

export {
  criseBuildMap,
  peiBuildFiche,
  indispoTempBuildFiche,
  buildContacts,
  debitSimultaneSelection,
  debitSimultaneFiche,
  buildCourrier
}
