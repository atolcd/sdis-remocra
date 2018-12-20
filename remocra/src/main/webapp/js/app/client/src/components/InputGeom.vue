<template>
  <div>
    <div v-for="(param, index) in params" :key="index">
     <input :ref = "'input'+index" :id="'input'+index" type="text" />
     <a :class="['geom-'+param.formulaire_type_controle.toLowerCase()]" href="#" @click="selectGeom(param.formulaire_type_controle,index)"></a>
     <a style="cursor:pointer" class="modif" href="#" @click="modifGeom(index)"></a>
     <a style="cursor:pointer" class="delete" href="#" @click="deleteGeom(index)"></a>
     <b-button-group  v-if="showValidGeom === index">
     <b-btn class="ok-cancel-btns" @click="validGeom(index)">Valider</b-btn>
     <b-btn class="ok-cancel-btns" @click="annulGeom(index)">Annuler</b-btn>
    </b-button-group>
 </div>
   </div>
</template>

<script>
/* eslint-disable */

// https://github.com/paliari/v-autocomplete
import EventBus from '../bus'
import * as eventTypes from '../bus/event-types.js'
import _ from 'lodash'

export default {
  name: 'InputGeom',
  data() {
    return {
      geom: null,
      showValidGeom: null,
       params: {param1 : {
        id: 24,
        nom: 'TEST_POINT',
        type_valeur : 'geometry',
        obligatoir : true,
        source_sql: '',
        source_sql_valeur : '',
        source_sql_libelle : '',
        formulaire_etiquette : 'Test_point',
        formulaire_type_controle :'polygongeometryfield',
        formulaire_nul_ordre : 9 ,
        formulaire_valeur_defaut: '',
        requete_modele: 1}, param2 : {
         id: 25,
         nom: 'TEST_POINT',
         type_valeur : 'geometry',
         obligatoir : true,
         source_sql: '',
         source_sql_valeur : '',
         source_sql_libelle : '',
         formulaire_etiquette : 'Test_point',
         formulaire_type_controle :'circlegeometryfield',
         formulaire_nul_ordre : 9 ,
         formulaire_valeur_defaut: '',
         requete_modele: 1
       },
       param3 : {
        id: 26,
        nom: 'TEST_POINT',
        type_valeur : 'geometry',
        obligatoir : true,
        source_sql: '',
        source_sql_valeur : '',
        source_sql_libelle : '',
        formulaire_etiquette : 'Test_point',
        formulaire_type_controle :'boxgeometryfield',
        formulaire_nul_ordre : 9 ,
        formulaire_valeur_defaut: '',
        requete_modele: 1
      },
      param4 : {
       id: 27,
       nom: 'TEST_POINT',
       type_valeur : 'geometry',
       obligatoir : true,
       source_sql: '',
       source_sql_valeur : '',
       source_sql_libelle : '',
       formulaire_etiquette : 'Test_point',
       formulaire_type_controle :'pointgeometryfield',
       formulaire_nul_ordre : 9 ,
       formulaire_valeur_defaut: '',
       requete_modele: 1}
       ,
       param5 : {
        id: 28,
        nom: 'TEST_POINT',
        type_valeur : 'geometry',
        obligatoir : true,
        source_sql: '',
        source_sql_valeur : '',
        source_sql_libelle : '',
        formulaire_etiquette : 'Test_point',
        formulaire_type_controle :'linestringgeometryfield',
        formulaire_nul_ordre : 9 ,
        formulaire_valeur_defaut: '',
        requete_modele: 1}
      }
     }
    },

  methods: {
    selectGeom(typeGeom,index){
      typeGeom = _.replace(typeGeom, 'geometryfield', ''),
      EventBus.$emit(eventTypes.INPUT_GEOM, {'typeGeom':typeGeom, 'index': index})
    },
    annulGeom(index){
      EventBus.$emit(eventTypes.ANNULE_INPUTGEOM, index)
    },
    validGeom(index){
      EventBus.$emit(eventTypes.VALIDE_INPUTGEOM, index)
    },
    modifGeom(index){
      EventBus.$emit(eventTypes.MODIFY_INPUTGEOM, index)
    },
    deleteGeom(index){
      EventBus.$emit(eventTypes.DELETE_INPUTGEOM, index)
    }
  }
}
</script>

<style scoped>
.geom-pointgeometryfield:before {
    content: url("/static/img/pencil_point.png");
    margin-right: 7px;
    cursor:pointer;
}
.geom-linestringgeometryfield:before {
    content:url('/static/img/pencil_ligne.png');
    margin-right: 7px;
    cursor:pointer;
}
.geom-polygongeometryfield:before {
    content:url('/static/img/pencil_polygone.png');
    margin-right: 7px;
    cursor:pointer;
}
.geom-circlegeometryfield:before {
    content:url('/static/img/pencil_circle.png');
    margin-right: 7px;
    cursor:pointer;
}
.geom-boxgeometryfield:before {
    content:url('/static/img/pencil_rectangle.png');
    margin-right: 7px;
    cursor:pointer;
}
.delete:before {
    content:url("/static/img/delete.png");
    margin-right: 7px;
    cursor:pointer;
}
.modif:before {
    content:url("/static/img/pencil.png");
    margin-right: 7px;
    cursor:pointer;
}

</style>
