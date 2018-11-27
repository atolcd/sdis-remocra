<template>
  <div>
    <b-list-group flush v-for="(evenement, index) in evenements" :key="index">
      <b-list-group-item>
       <div>
         <img src="/static/img/folder.png" @click="loadMessages(evenement.id)"><span style="cursor:pointer" @dblclick="modifEvent(evenement)">{{evenement.nom}}</span>
          <img v-if="evenement.geometrie !== null" src="/static/img/flag_green.png" @click="locateEvent(evenement.geometrie)">
          <img src="/static/img/add.png" @click="openNewMessage(evenement.id)">
      </div>
      <b-collapse v-if="evenement.criseSuivis.length!==0" class="mt-2" :id="'c'+evenement.id">
         <b-list-group flush v-for="(message, index) in evenement.criseSuivis" :key="index">
           <b-list-group-item>
             <div>
               {{message.creation}}  {{message.origine}}
             </div>
             <div  @dblclick="showMessage(evenement.id, message.id)">{{message.message}}</div>
           </b-list-group-item>
         </b-list-group>
      </b-collapse>
      </b-list-group-item>
  </b-list-group>
  <new-message ref="newMessage"></new-message>
  </div>
</template>

<script>
/* eslint-disable */
import axios from 'axios'
import moment from 'moment'
import 'moment-timezone';
import NewMessage from './NewMessage.vue';
import WKT from 'ol/format/WKT.js';

export default {
  name: 'Evenements',
  components: {
    NewMessage
  },
  props: {
   crise: {
     required: true,
     type: String
   }
  },
  data () {
    return {
      evenements:[]   }
  },
  mounted(){
    this.evenements =[] ,
     this.loadEvenements(this.crise)
  },
  methods : {
    loadEvenements(crise, filters){
      var jsonFilters = JSON.stringify(filters)
      axios.get('/remocra/evenements/'+crise,  {params: {filter: jsonFilters}})
        .then((response) => {
          if (response.data.data) {
             this.evenements = response.data.data
             _.forEach(this.evenements, function(evenement){
               var IsoDateTo = moment(new Date(evenement.constat), 'DD/MM/YYYY[T]HH:mm:ss[Z]').format('DD/MM/YYYY'+' - '+'HH:mm')
                evenement.constat = IsoDateTo
                _.forEach(evenement.criseSuivis, function(message){
                  var IsoDateTo = moment(new Date(message.creation), 'DD/MM/YYYY[T]HH:mm:ss[Z]').format('DD/MM/YYYY'+' - '+'HH:mm')
                   message.creation = IsoDateTo
                })
                // Tri antéchronologique du suivi (création)
                evenement.criseSuivis = _.orderBy(evenement.criseSuivis, ['creation'], ['desc']);
             })
             // Tri antéchronologique des évènements (constat)
             this.evenements = _.orderBy(this.evenements, ['constat'], ['desc']);
          }
        })
        .catch(function(error) {
          console.error('évenements', error)
        })

        // Filtre des événements de crise sur la carte
        if (!filters) {
          filters = [];
        }
        var idProp = _.find(filters, function(obj) {
          return obj.property === 'crise'
        })
        if (!idProp) {
          filters.push({ property: 'crise', value: crise })
        } else {
          idProp.value = crise
        }

        // TODO cva voir s'il est nécessaire d'éviter le cache en maintenant un paramètre type "time": Date.now()
        var jsonFilters = JSON.stringify(filters)

        var mapCmp = this.$parent.$parent
        var wmsLayer = mapCmp.getLayerById('893bb7520e7fb036d665661847628994')
        if (wmsLayer) {
          wmsLayer.getSource().updateParams({filter: jsonFilters});
        }
    },
    loadMessages(id){
      //on recharge la liste des messsages
      //Si la liste is not empty on collapse
      this.$root.$emit('bv::toggle::collapse','c'+id)
    },
    modifEvent(evenement){
      var natureId = evenement.geometrie !== null ? evenement.natureId : null
      this.$parent.$parent.$refs['newEvenement'].modifyEvent(this.crise, evenement.id, natureId)
    },
    openNewMessage(id){
      this.$refs.newMessage.showModal(this.crise, id, null)
    },
    showMessage(evenementId, messageId){
      this.$refs.newMessage.showModal(this.crise, evenementId, messageId)
    },
    locateEvent(geometrie){
      this.$parent.$parent.zoomToGeom(geometrie)
    }

  }
}
</script>
