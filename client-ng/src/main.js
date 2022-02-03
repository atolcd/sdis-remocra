// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import GestCrise from './App'
import Fiche from './components/Fiche'
import Contacts from './components/Contacts'
import FicheIndisponibiliteTemporaire from './components/FicheIndisponibiliteTemporaire'
import DebitSimultaneSelection from './components/DebitSimultaneSelection'
import DebitSimultaneFiche from './components/DebitSimultaneFiche'
import VisualisationCourriers from './components/VisualisationCourriers.vue'
import PlanificationDeci from './components/planification_deci/index.vue'
import OlMap from './components/OlMap/OlMap.vue'
import OlMapEtude from './components/planification_deci/OlMapEtude.vue'
import Dashboard from './components/Dashboard'
import TransfertsAutomatises from './components/TransfertsAutomatises.vue'

// import router from './router'
import BootstrapVue from 'bootstrap-vue'
import Courrier from './components/Courrier'
import rate from 'vue-rate'
import { BPopover } from 'bootstrap-vue'
import InputTag from 'vue-input-tag'
import Notifications from 'vue-notification'
import velocity from 'velocity-animate'

// Feuilles de style
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-vue/dist/bootstrap-vue.min.css'
import 'ol/ol.css'
import 'v-autocomplete/dist/v-autocomplete.css'
import _ from 'lodash'
import VModal from 'vue-js-modal'

Vue.config.productionTip = false

Vue.use(BootstrapVue)
Vue.use(rate)
Vue.use(VModal)
Vue.use(Notifications, {
  velocity
})
Vue.component('input-tag', InputTag)
Vue.component('b-popover', BPopover)


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
    template: "<Fiche  title='" + data.title + "' :showHistorique=" +data.showHistorique+ " :newVisite=" + data.newVisite + " :idHydrant=" + data.id + " codeHydrant=" + data.code + "  geometrie='" + data.geometrie + "' />"
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
    template: "<Contacts :id=" + data.id + " title='" + data.title + "' nom='" + _.escape(data.nom) + "' />"
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
const buildCourrier = function(el, data){
  var v = new Vue({
  el,
  bus: new Vue(),
  components: {
      Courrier
  },
  template: "<Courrier thematique="+data.thematique+" />"
  })
  return v
}

//module de visualisation de courrier
const visualisationCourriers = function(id){
  var v = new Vue({
    el: '#' + id,
    bus: new Vue(),
    components: {
        VisualisationCourriers
    },
    template: "<VisualisationCourriers />"
  })
  return v
}

const planificationDeci = function(id, cleIgn, bounds){
  var v = new Vue({
    el: '#' + id,
    bus: new Vue(),
    components: {
        PlanificationDeci
    },
    template: "<PlanificationDeci cleIgn="+cleIgn+" bounds="+bounds+" />"
  })
  return v
}

const olMap = function(id){
  var v = new Vue({
    el: '#' + id,
    bus: new Vue(),
    components: {
        OlMap
    },
    template: "<OlMap />"
  })
  return v
}

const olMapEtude = function(id){
  var v = new Vue({
    el: '#' + id,
    bus: new Vue(),
    components: {
        OlMapEtude
    },
    template: "<OlMapEtude idEtude=1 />"
  })
  return v
}

//module de dashboard
const buildDashboard = function(el){
  var v = new Vue({
  el,
  bus: new Vue(),
  components: {
      Dashboard
  },
  template: "<Dashboard />"
  })
  return v
}

const transfertsAutomatises = function(id){
  var v = new Vue({
    el: '#' + id,
    bus: new Vue(),
    components: {
        TransfertsAutomatises
    },
    template: "<TransfertsAutomatises />"
  })
  return v
}

export {
  transfertsAutomatises,
  criseBuildMap,
  peiBuildFiche,
  indispoTempBuildFiche,
  buildContacts,
  debitSimultaneSelection,
  debitSimultaneFiche,
  buildCourrier,
  visualisationCourriers,
  planificationDeci,
  olMap,
  olMapEtude,
  buildDashboard

}
