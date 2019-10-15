<template>
<div class="evenements">
  <b-list-group flush v-for="(evenement, indexevt) in evenements" :key="indexevt">
    <b-list-group-item>
      <div class="evenement">
        <img class="messages-toggle" src="/remocra/static/img/expand.svg" @click="loadMessages(evenement.id)">
        <sup><span class="messages-count">{{evenement.criseSuivis.length}}</span></sup>
        <span class="evenement-nom" @click="modifEvent(evenement)">{{evenement.nom}}</span>
        <div class="mini-tools">
          <img style="cursor:pointer" v-if="evenement.cloture == null" src="/remocra/static/img/message-add.svg" title="Nouveau message" @click="openNewMessage(evenement.id)">
          <img style="cursor:pointer" v-if="evenement.geometrie !== null" src="/remocra/static/img/location.svg" title="Zoomer" @click="locateEvent(evenement.geometrie)">
        </div>
      </div>
      <b-collapse v-if="evenement.criseSuivis.length!==0" class="mt-2 messages" :id="'c'+evenement.id">
        <b-list-group flush v-for="(message, indexmsg) in evenement.criseSuivis" :key="indexmsg">
          <b-list-group-item>
            <div class="message">
              <span class="creation">{{message.creation}}</span> <span class="origine"> {{message.auteur}}</span>
            </div>
            <div class="objet">{{message.objet}}</div>
            <div v-if="message.message !== null">
              <div class="description" v-for="(msgpart, indexmsgpart) in message.message.split('\n')" :key="indexmsgpart">{{ msgpart }}</div>
            </div>
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
      lastFilters: [],
      evenements: [],
      contexte: null,
    }
  },
  mounted() {
    this.evenements = []
    this.loadEvenements(this.crise, [{
      property: 'statut',
      value: 'En cours'
    }, {
      property: 'contexte',
      value: 'OPERATIONNEL'
    }])
    this.$root.$options.bus.$on(eventTypes.LOAD_EVENEMENTS, this.loadEvenemtsPrep)
  },
  destroyed() {
    this.$root.$options.bus.$off(eventTypes.LOAD_EVENEMENTS)
  },
  methods: {
    loadEvenemtsPrep(args) {
      if (typeof(args.contexte) !== 'undefined') {
        this.contexte = args.contexte
      }
      this.loadEvenements(args.crise, args.filters)
    },
    loadEvenements(crise, newFilters) {
      var filters = _.clone(newFilters)
      //On utilise les anciens filtre :  cas de chargement après création
      if (typeof filters === 'undefined') {
        filters = this.lastFilters
      }
      // On ajoute le contexte (filtre particulier) : On supprime l'ancien (pas de cache pour ce filtre)
      _.remove(filters, function(filter) {
        return filter.property == 'contexte'
      })
      filters.push({
        property: 'contexte',
        value: this.contexte
      })
      var jsonFilters = JSON.stringify(filters)
      // On sauvegarde les filtres
      this.lastFilters = filters
      axios.get('/remocra/evenements/' + crise, {
        params: {
          filter: jsonFilters
        }
      }).then(response => {
        if (response.data.data) {
          var evenements = response.data.data
          _.forEach(evenements, function(evenement) {
            // Tri antéchronologique du suivi (création)
            evenement.criseSuivis.sort(function compare(a, b) {
              var dateA = new Date(a.creation);
              var dateB = new Date(b.creation);
              return dateB - dateA;
            });
            _.forEach(evenement.criseSuivis, function(message) {
              var messageIsoDateTo = moment(new Date(message.creation), 'YYYY-MM-DD[T]HH:mm:ss[Z]').format('DD/MM/YYYY' + ' - ' + 'HH:mm')
              message.creation = messageIsoDateTo
            })
          })
          // Tri antéchronologique des évènements (constat)
          evenements.sort(function compare(a, b) {
            var dateA = new Date(a.constat);
            var dateB = new Date(b.constat);
            return dateB - dateA;
          });
          this.evenements = evenements
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
