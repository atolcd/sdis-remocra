import Vue from 'vue'
import Router from 'vue-router'
import OlMap from '@/components/OlMap'

Vue.use(Router)

export default new Router({
  routes: [{
    path: '/olmap',
    name: 'olmap',
    component: OlMap
  }]
})