<template>
<div class="documents">
  <b-list-group flush v-for="(document, index) in documents" :key="index">
    <b-list-group-item>
      <div class="document">
        <a onclick="location=this.href" :href="'/remocra/telechargement/document/'+document.code">
          <strong>{{document.fichier}} - {{document.date}}</strong>
        </a>
        <div class="mini-tools">
          <img style="cursor:pointer" v-if="document.geometrie !== null" src="/static/img/location.svg" title="Zoomer" @click="locateDoc(document.geometrie)">
        </div>
      </div>
    </b-list-group-item>
  </b-list-group>
</div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'
import moment from 'moment'
import 'moment-timezone'
import * as eventTypes from '../bus/event-types.js'
export default {
  name: 'Documents',
  props: {
    crise: {
      required: true,
      type: Number
    }
  },
  data() {
    return {
      documents: []
    }
  },
  mounted() {
    this.documents = []
    this.loadDocuments(this.crise)
    this.$root.$options.bus.$on(eventTypes.LOAD_DOCUMENTS, crise => {
      this.loadDocuments(crise)
    })
  },
  destroyed() {
    this.$root.$options.bus.$off(eventTypes.LOAD_DOCUMENTS)
  },
  methods: {
    loadDocuments(crise) {
      axios.get('/remocra/crises/' + crise + '/documents').then(response => {
        if (response.data.data) {
          this.documents = response.data.data
          _.forEach(this.documents, function(document) {
            var IsoDateTo = moment(new Date(document.date), 'DD/MM/YYYY[T]HH:mm:ss[Z]').format('DD/MM/YYYY' + ' - ' + 'HH:mm')
            document.date = IsoDateTo
          })
          this.documents = _.orderBy(this.documents, ['date'], ['desc'])
        }
      }).catch(function(error) {
        console.error('évenements', error)
      })
    },
    locateDoc(geometrie) {
      this.$root.$options.bus.$emit(eventTypes.ZOOM_TOEXTENT, geometrie)
    }
  }
}
</script>
