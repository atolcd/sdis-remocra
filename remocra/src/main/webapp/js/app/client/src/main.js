// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import GestCrise from './App'
// import router from './router'
import BootstrapVue from 'bootstrap-vue'
import rate from 'vue-rate'
import { Popover } from 'bootstrap-vue/es/components'
import InputTag from 'vue-input-tag'
import Notifications from 'vue-notification'
import velocity from 'velocity-animate'
import _ from 'lodash'

Vue.config.productionTip = false

/* eslint-disable  */
Vue.use(BootstrapVue)
Vue.use(rate)
Vue.use(Popover)
Vue.use(Notifications, {
  velocity
})
Vue.component('input-tag', InputTag)

const crise = null
let vm = []
const buildVue = function buildVue (id, idCrise){
  var v = new Vue ({
    el: '#crise-'+id,
    id: id,
    crise: idCrise,
    bus: new Vue(),
    components: { GestCrise },
    template: '<GestCrise :crise='+idCrise+' />'
  })
  vm.push(v)
  return v
}

const destroyVue = function destroyVue (idCrise){
_.forEach(vm , v => {
   if (v.$options.crise === idCrise) {
     v.$destroy()
  }
})
}

export {buildVue, destroyVue}
