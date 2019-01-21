<template>
<div class="evenements">
  <b-list-group flush v-for="(evenement, indexevt) in evenements" :key="indexevt">
    <b-list-group-item>
      <div class="evenement">
        <img class="messages-toggle" src="/static/img/expand.svg" @click="loadMessages(evenement.id)">
        <sup><span class="messages-count">{{evenement.criseSuivis.length}}</span></sup>
        <span class="evenement-nom" @click="modifEvent(evenement)">{{evenement.nom}}</span>
        <div class="mini-tools">
          <img style="cursor:pointer" v-if="evenement.cloture == null" src="/static/img/message-add.svg" title="Nouveau message" @click="openNewMessage(evenement.id)">
          <img style="cursor:pointer" v-if="evenement.geometrie !== null" src="/static/img/location.svg" title="Zoomer" @click="locateEvent(evenement.geometrie)">
        </div>
      </div>
      <b-collapse v-if="evenement.criseSuivis.length!==0" class="mt-2 messages" :id="'c'+evenement.id">
        <b-list-group flush v-for="(message, indexmsg) in evenement.criseSuivis" :key="indexmsg">
          <b-list-group-item>
            <div class="message">
              <span class="creation">{{message.creation}}</span> <span class="origine">{{message.origine}}</span>
            </div>
            <div class="objet">{{message.objet}}</div>
            <div class="description" v-for="(msgpart, indexmsgpart) in message.message.split('\n')" :key="indexmsgpart">{{ msgpart }}</div>
          </b-list-group-item>
        </b-list-group>
      </b-collapse>
    </b-list-group-item>
  </b-list-group>
  <new-message :criseId="crise" ref="newMessage"></new-message>
</div>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'
import moment from 'moment'
import 'moment-timezone'
import NewMessage from './NewMessage.vue'
import * as eventTypes from '../bus/event-types.js'
export default {
  name: 'Evenements',
  components: {
    NewMessage
  },
  props: {
    crise: {
      required: true,
      type: Number
    }
  },
  data() {
    return {
      evenements: []
    }
  },
  mounted() {
    this.evenements = []
    this.loadEvenements(this.crise)
    this.$root.$options.bus.$on(eventTypes.LOAD_EVENEMENTS, this.loadEvenemtsPrep)
  },
  destroyed() {
    this.$root.$options.bus.$off(eventTypes.LOAD_EVENEMENTS)
  },
  methods: {
    loadEvenemtsPrep(args) {
      this.loadEvenements(args.crise, args.filters)
    },
    loadEvenements(crise, filters) {
      var jsonFilters = JSON.stringify(filters)
      axios.get('/remocra/evenements/' + crise, {
        params: {
          filter: jsonFilters
        }
      }).then(response => {
        if (response.data.data) {
          this.evenements = response.data.data
          _.forEach(this.evenements, function(evenement) {
            var IsoDateTo = moment(new Date(evenement.constat), 'DD/MM/YYYY[T]HH:mm:ss[Z]').format('DD/MM/YYYY' + ' - ' + 'HH:mm')
            evenement.constat = IsoDateTo
            _.forEach(evenement.criseSuivis, function(message) {
              var IsoDateTo = moment(new Date(message.creation), 'DD/MM/YYYY[T]HH:mm:ss[Z]').format('DD/MM/YYYY' + ' - ' + 'HH:mm')
              message.creation = IsoDateTo
            })
            // Tri antéchronologique du suivi (création)
            evenement.criseSuivis = _.orderBy(evenement.criseSuivis, ['creation'], ['desc'])
          })
          // Tri antéchronologique des évènements (constat)
          this.evenements = _.orderBy(this.evenements, ['constat'], ['desc'])
        }
      }).catch(function(error) {
        console.error('évenements', error)
      })
      // Filtre des événements de crise sur la carte
      if (!filters) {
        filters = []
      }
      var idProp = _.find(filters, function(obj) {
        return obj.property === 'crise'
      })
      if (!idProp) {
        filters.push({
          property: 'crise',
          value: crise
        })
      } else {
        idProp.value = crise
      }
      // TODO cva voir s'il est nécessaire d'éviter le cache en maintenant un paramètre type "time": Date.now()
      jsonFilters = JSON.stringify(filters)
      this.$root.$options.bus.$emit(eventTypes.UPDATE_MAPFILTERS, {
        id: '893bb7520e7fb036d665661847628994',
        filters: jsonFilters
      })
    },
    loadMessages(id) {
      // on recharge la liste des messsages
      // Si la liste is not empty on collapse
      this.$root.$emit('bv::toggle::collapse', 'c' + id)
    },
    modifEvent(evenement) {
      var natureId = evenement.geometrie !== null ? evenement.natureId : null
      this.$root.$options.bus.$emit(eventTypes.MODIFY_EVENT, {
        criseId: this.crise,
        evenementId: evenement.id,
        natureId: natureId
      })
    },
    openNewMessage(id) {
      this.$refs.newMessage.showModal(this.crise, id, null)
    },
    showMessage(evenementId, messageId) {
      this.$refs.newMessage.showModal(this.crise, evenementId, messageId)
    },
    locateEvent(geometrie) {
      this.$root.$options.bus.$emit(eventTypes.ZOOM_TOGEOM, {
        geom: geometrie,
        crise: this.crise
      })
    }
  }
}
</script>
