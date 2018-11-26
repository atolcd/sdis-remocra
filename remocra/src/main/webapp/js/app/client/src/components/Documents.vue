<template>
  <div>
    <b-list-group flush v-for="(document, index) in documents" :key="index">
           <b-list-group-item>
             <a onclick="return false" ondblclick="location=this.href"  :href="'/remocra/telechargement/document/'+document.code">
               <strong>{{document.fichier}} - {{document.date}}</strong>
                <img v-if="document.geometrie !== null" src="/static/img/flag_green.png" @click="locateDoc(document.geometrie)">
             </a>
           </b-list-group-item>
    </b-list-group>
  </div>
</template>

<script>
/* eslint-disable */
import axios from 'axios'
import moment from 'moment'
import 'moment-timezone';

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
      this.$parent.$parent.zoomToExtent(geometrie)
    }
  }
}
</script>
