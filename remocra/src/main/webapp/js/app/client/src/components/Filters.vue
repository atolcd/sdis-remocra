<template>
  <div>
  <input-tag :tags.sync="filtres" v-on:change="controlFilter"></input-tag>
  <b-list-group flush>
    <b-list-group-item>
       <strong>Type d'évènement</strong>
       <b-list-group v-for="(type, index) in types" :key="index">
            <a @click="addFilter('type',type)" href="#">{{type.nom}}</a>
       </b-list-group>
    </b-list-group-item>
    <b-list-group-item>
       <strong>Statut</strong>
       <b-list-group v-for="(statut, index) in ['Nouveau','Clos']" :key="index">
            <a @click="addFilter('statut',statut)" href="#">{{statut}}</a>
       </b-list-group>
    </b-list-group-item>
    <b-list-group-item >
       <strong>Dernier message</strong>
       <b-list-group v-for="(periode, index) in ['<10mn', '<30mn', '<1h', '<24h']" :key="index">
            <a @click="addFilter('periode',periode)" href="#">{{periode}}</a>
       </b-list-group>
    </b-list-group-item>
    <b-list-group-item>
       <strong>Tags</strong>
            <span v-for="(tag, index) in tags" :key="index"><b-badge @click="addFilter('tag',tag)"  href="#" pill variant="primary">{{tag}}</b-badge></span>
    </b-list-group-item>
    <b-list-group-item>
       <strong>Auteur</strong>
       <b-list-group v-for="(origine, index) in origines" :key="index">
            <a @click="addFilter('origine',origine)" href="#">{{origine}}</a>
       </b-list-group>
    </b-list-group-item>
    <b-list-group-item>
       <strong>Importance</strong>
       <rate :length="5" @after-rate="onAftereRate"/>
    </b-list-group-item>
  </b-list-group>
</div>
</template>

<script>
/* eslint-disable */
import axios from 'axios'
import _ from 'lodash'
export default {
  name: 'Filters',
  data() {
    return {
      types:[],
      tags:[],
      origines:[],
      filtres: [],
      filterTags:[]
    }
  },
  props:{
    criseId:{
      required:true,
      type:String
    }
  },
  mounted(){
    this.load()
  },
  watch:{
    'filterTags' :'filterChanged',
    'filtres': 'controlFilter'
  },
  methods: {
    filterChanged(newFilters, oldFilters) {
      this.$parent.$parent.$refs.evenements.loadEvenements(this.criseId, newFilters)
    },
     load(){
       axios.get('/remocra/typecrisenatureevenement/'+this.criseId)
         .then((response) => {
           if (response.data.data) {
             var types = response.data.data
             this.types = types
           }
         })
         .catch(function(error) {
           console.error('nature évenement', error)
         })
         axios.get('/remocra/evenements/tags/'+this.criseId)
           .then((response) => {
             if (response.data.data) {
               var tags = response.data.data
               var t= [] ;
               _.forEach(tags, function(tag){
                 t.push(_.split(tag, ","))
               })
               this.tags = _.flattenDeep(t)
             }
           })
           .catch(function(error) {
             console.error('tags', error)
           })
           axios.get('/remocra/evenements/origines/'+this.criseId)
             .then((response) => {
               if (response.data.data) {
                 var origines = response.data.data
                 this.origines = origines
               }
             })
             .catch(function(error) {
               console.error('tags', error)
             })
     },
     addFilter(filtre ,value){
       if(filtre === 'type'){
         if(_.indexOf(this.filtres, value.nom) === -1){
           this.filtres.push(value.nom)
           this.filterTags.push({property: filtre, value: value.id, showedFilter: value.nom})
         }
       }else if(_.indexOf(this.filtres, value) === -1){
         this.filtres.push(value)
         this.filterTags.push({property: filtre, value: value})
       }
     },
     onAftereRate(rate){
       _.remove(this.filterTags, function(filter){
         //Pour le filtre importance on supprime l'ancienne valeur
          return filter.property === 'importance'
       })
        this.filterTags.push({property: 'importance', value: rate})
     },
     controlFilter(newFiltres, oldFiltres){
        if(oldFiltres.length > newFiltres.length){
           var difference = _.pullAll(oldFiltres, newFiltres);
           _.remove(this.filterTags, function(filter){
             //Pour les types d'évenement on recupere l'id mais on affiche les noms
             if(filter.property == 'type'){
               return _.indexOf(difference, filter.showedFilter) !== -1;
             }
             return _.indexOf(difference, filter.value) !== -1;
           })
           this.$parent.$parent.$refs.evenements.loadEvenements(this.criseId, this.filterTags)
        }
     }

  }
}
</script>

<style>
</style>
