// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import GestCrise from './App'
import router from './router'
import BootstrapVue from 'bootstrap-vue'
import rate from 'vue-rate'
import { Popover } from 'bootstrap-vue/es/components'
import InputTag from 'vue-input-tag'
import Notifications from 'vue-notification'
import velocity from 'velocity-animate'

Vue.config.productionTip = false
/* eslint-disable no-new */
Vue.use(BootstrapVue)
Vue.use(rate)
Vue.use(Popover)
Vue.use(Notifications, {
  velocity
})
Vue.component('input-tag', InputTag)

new Vue({
  el: '#app',
  router,
  components: { GestCrise },
  template: '<GestCrise/>'
})
