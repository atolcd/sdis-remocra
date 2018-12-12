<template>
  <div class="documents">
    <b-list-group flush v-for="(document, index) in documents" :key="index">
           <b-list-group-item>
              <div class="document">
                <a onclick="location=this.href" :href="'/remocra/telechargement/document/'+document.code">
                  <strong>{{document.fichier}} - {{document.date}}</strong>
                </a>
                <div class="mini-tools">
                  <img style="cursor:pointer" v-if="document.geometrie !== null" src="/static/img/location.svg"
                    title="Zoomer"
                    @click="locateDoc(document.geometrie)">
                </div>
              </div>
           </b-list-group-item>
    </b-list-group>
  </div>
</template>

<script>
/* eslint-disable */
import axios from 'axios'
import moment from 'moment'
import 'moment-timezone';
import EventBus from '../bus'
import * as eventTypes from '../bus/event-types.js'

export default {
  name: 'Documents',
  props: {
   crise: {
     required: true,
     type: String
   }
  },
  data () {
    return {
      documents:[]   }
  },
  mounted(){
    this.documents =[] ,
     this.loadDocuments(this.crise)
     EventBus.$on(eventTypes.LOAD_DOCUMENTS, crise => {
       this.loadDocuments(crise)
     })
  },
  methods : {
    loadDocuments(crise){
      axios.get('/remocra/crises/'+crise+'/documents')
        .then((response) => {
          if (response.data.data) {
             this.documents = response.data.data
             _.forEach(this.documents, function(document){
               var IsoDateTo = moment(new Date(document.date), 'DD/MM/YYYY[T]HH:mm:ss[Z]').format('DD/MM/YYYY'+' - '+'HH:mm')
                document.date = IsoDateTo
             })
             this.documents = _.orderBy(this.documents, ['date'], ['desc']);
          }
        })
        .catch(function(error) {
          console.error('évenements', error)
        })
    },
    showDocument(code){
      axios.get('/remocra/telechargement/document/'+code)
        .then((response) => {
          if (response.data.data) {
          }
        })
        .catch(function(error) {
          console.error('évenements', error)
        })
    },
    locateDoc(geometrie){
      EventBus.$emit(eventTypes.ZOOM_TOEXTENT, geometrie)
    }
  }
}
</script>
