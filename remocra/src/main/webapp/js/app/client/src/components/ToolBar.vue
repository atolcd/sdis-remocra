<template>
<!--eslint-disable-->
    <div>
        <b-dropdown text="Dropdown Button" ref="dropDown">
          <template slot="button-content">
             <img src='/static/img/pencil.png'>
          </template>
            <ul v-for="(type, name) in types" :key="name">
              <li class="dropdown-submenu"><a class="dropdown-item" href="#" >{{name}}</a>
                <ul class="dropdown-menu">
                  <li v-for="(nature, index) in type" :key="index" class="dropdown-item"><a @click="selectGeom(nature.typeGeometrie, nature.value)" href="#">{{nature.text}}</a></li>
                </ul>
              </li>
          </ul>
        </b-dropdown>
        <b-btn class="ctrl" @click="activateInteraction('Modify')" v-b-tooltip.hover title="Modifier la géometrie d'un évènement"><img src="/static/img/pencil.png"></b-btn>
         <b-button-group v-if="showUpdateGeom" >
         <b-btn @click="validModifGeom">Valider</b-btn>
         <b-btn @click="annulModifGeom">Annuler</b-btn>
       </b-button-group>
       <b-btn class="ctrl" @click="activateInteraction('Translate')" v-b-tooltip.hover title="Déplacer un évènement"><img src="/static/img/pencil_move.png"></b-btn>
       <b-button-group v-if="showTranslateGeom" >
       <b-btn @click="validTranslateGeom">Valider</b-btn>
       <b-btn @click="annulTranslateGeom">Annuler</b-btn>
     </b-button-group>
     <b-btn class="ctrl" @click="openAttributes" v-b-tooltip.hover title="Modifier les attributs d’un événement"><img src="/static/img/application_view_columns.png"></b-btn>
   </div>
</template>

<script>
/* eslint-disable */
import axios from 'axios'
export default {
  name: 'ToolBar',
  data() {
    return {
      showUpdateGeom: false,
      showTranslateGeom: false,
      types: [],
      categories: []
    }
  },
  mounted(){
    var types = []
    var categories = []
    axios.get('/remocra/typecrisecategorieevenement')
      .then((response) => {
        if (response.data.data) {
          var typeCategs = response.data.data
          _.forEach(typeCategs, function(typeCateg) {
            if (typeCateg !== null) {
              categories.push(typeCateg)
            }
          })
        }
      })
      .catch(function(error) {
        console.error('categorie évenement', error)
      })
    axios.get('/remocra/typecrisenatureevenement')
      .then((response) => {
        if (response.data.data) {
          var typeEvents = response.data.data
          _.forEach(typeEvents, function(typeEvenement) {
            if (typeEvenement.typeGeometrie !== null) {
              _.forEach(categories, function(categ) {
                if (categ.id === typeEvenement.categorieEvenement) {
                  types.push({
                    value: typeEvenement.id,
                    text: typeEvenement.nom,
                    categorie: categ.nom,
                    typeGeometrie: typeEvenement.typeGeometrie
                  })
                }
              })
            }
          })
          this.types = _.groupBy(types, t=>t.categorie);
        }
      })
      .catch(function(error) {
        console.error('nature évenement', error)
      })
   },
  methods: {
    selectGeom(typeGeom, natureId){
      this.$parent.addDrawInteractions(typeGeom, natureId)
      this.$refs.dropDown.hide()
    },
    activateInteraction(type){
      this.$parent.activateInteraction(type)
    },
    annulModifGeom(){
      this.$parent.annulModifGeom()
    },
    validModifGeom(){
      this.$parent.validModifGeom()
    },
    annulTranslateGeom(){
      this.$parent.annulTranslateGeom()
    },
    validTranslateGeom(){
      this.$parent.validTranslateGeom()
    },
    openAttributes(){
      this.$parent.openAttributes()
    }
  }

}
</script>
<style scoped>

.dropdown-submenu {
    position: relative;
}

.dropdown-submenu>.dropdown-menu {
    top: 0;
    left: 100%;
    margin-top: -6px;
    margin-left: -1px;
    -webkit-border-radius: 0 6px 6px 6px;
    -moz-border-radius: 0 6px 6px;
    border-radius: 0 6px 6px 6px;
}

.dropdown-submenu:hover>.dropdown-menu {
    display: block;
}

.dropdown-submenu>a:after {
    display: block;
    content: " ";
    float: right;
    width: 0;
    height: 0;
    border-color: transparent;
    border-style: solid;
    border-width: 5px 0 5px 5px;
    border-left-color: #ccc;
    margin-top: 5px;
    margin-right: -10px;
}

.dropdown-submenu:hover>a:after {
    border-left-color: #fff;
}

.dropdown-submenu.pull-left {
    float: none;
}

.dropdown-submenu.pull-left>.dropdown-menu {
    left: -100%;
    margin-left: 10px;
    -webkit-border-radius: 6px 0 6px 6px;
    -moz-border-radius: 6px 0 6px 6px;
    border-radius: 6px 0 6px 6px;
}

</style>
