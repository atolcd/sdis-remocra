<template>
<div>
  <b-dropdown text="Dropdown Button" ref="dropDown" title="Dessiner un évènement">
    <template slot="button-content">
      <img src='/remocra/static/img/pencil_point.png'>
    </template>
    <ul v-for="(type, name) in types" :key="name">
      <li class="dropdown-submenu"><a class="dropdown-item" href="#">{{name}}</a>
        <ul class="dropdown-menu">
          <li v-for="(nature, index) in type" :key="index" class="dropdown-item">
            <a :class="['geom-'+nature.typeGeometrie.toLowerCase()]" @click="selectGeom($event, nature.typeGeometrie, nature.value)" href="#">{{nature.text}}</a></li>
        </ul>
      </li>
    </ul>
  </b-dropdown>
  <b-btn class="ctrl" @click="activateInteraction('Modify')" title="Modifier la géometrie d'un évènement"><img src="/remocra/static/img/pencil.png"></b-btn>
  <b-button-group v-if="showUpdateGeom">
    <b-btn class="ok-cancel-btns" @click="validModifGeom">Valider</b-btn>
    <b-btn class="ok-cancel-btns" @click="annulModifGeom">Annuler</b-btn>
  </b-button-group>
  <b-btn class="ctrl" @click="activateInteraction('Translate')" title="Déplacer un évènement"><img src="/remocra/static/img/pencil_move.png"></b-btn>
  <b-button-group v-if="showTranslateGeom">
    <b-btn class="ok-cancel-btns" @click="validTranslateGeom">Valider</b-btn>
    <b-btn class="ok-cancel-btns" @click="annulTranslateGeom">Annuler</b-btn>
  </b-button-group>
  <b-btn class="ctrl" @click="openAttributes" title="Modifier les attributs d’un événement"><img src="/remocra/static/img/application_view_columns.png"></b-btn>
  <b-btn class="ctrl" @click="addStampedCard" title="Carte horodatée"><img src="/remocra/static/img/photo-add.svg"></b-btn>
  <b-btn class="ctrl" @click="openModalImportFile" title="Ajouter un fichier vectoriel"><img id="iconBtnImportFile" src="/remocra/static/img/cartographie.png"></b-btn>
</div>
</template>

<script>
import axios from 'axios'
import * as eventTypes from '../bus/event-types.js'
import _ from 'lodash'
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
  mounted() {
    var types = []
    var categories = []
    axios.get('/remocra/typecrisecategorieevenement').then((response) => {
      if (response.data.data) {
        var typeCategs = response.data.data
        _.forEach(typeCategs, function(typeCateg) {
          if (typeCateg !== null) {
            categories.push(typeCateg)
          }
        })
      }
    }).catch(function(error) {
      console.error('categorie évenement', error)
    })
    axios.get('/remocra/typecrisenatureevenement').then((response) => {
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
        this.types = _.groupBy(types, t => t.categorie)
      }
    }).catch(function(error) {
      console.error('nature évenement', error)
    })
  },
  methods: {
    selectGeom(evt, typeGeom, natureId) {
      this.$root.$options.bus.$emit(eventTypes.ADD_DRAWINTERACTIONS, {
        'typeGeom': typeGeom,
        'natureId': natureId
      })
      this.$refs.dropDown.hide()
      if (evt) {
        evt.preventDefault()
        evt.stopPropagation()
      }
    },
    activateInteraction(type) {
      this.$root.$options.bus.$emit(eventTypes.ACTIVATE_INTERACTION, type)
    },
    annulModifGeom() {
      this.$root.$options.bus.$emit(eventTypes.ANNULE_MODIFGEOM)
    },
    validModifGeom() {
      this.$root.$options.bus.$emit(eventTypes.VALIDE_MODIFGEOM)
    },
    annulTranslateGeom() {
      this.$root.$options.bus.$emit(eventTypes.ANNULE_TRANSLATEGEOM)
    },
    validTranslateGeom() {
      this.$root.$options.bus.$emit(eventTypes.VALIDE_TRANSLATEGEOM)
    },
    openAttributes() {
      this.$root.$options.bus.$emit(eventTypes.OPEN_ATTRIBUTES)
    },
    addStampedCard() {
      this.$root.$options.bus.$emit(eventTypes.ADD_STAMPEDCARD)
    },
    openModalImportFile() {
      this.$parent.openModalImportFile()
    }
  }
}
</script>

<style scoped>
.dropdown-item {
  border: 1px solid transparent;
  border-radius: 3px;
}

.dropdown-item:hover {
  background-color: #e6e6e6;
  border-color: #9d9d9d;
  cursor: pointer;
}

.dropdown ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
}

.dropdown-submenu {
  position: relative;
  line-height: 1;
}

.dropdown-submenu>.dropdown-menu {
  top: 0;
  left: 100%;
  margin-top: -6px;
  margin-left: -1px;
  border-radius: 0 6px 6px 6px;
  background-color: #f0f0f0;
}

.dropdown-submenu:hover>.dropdown-menu {
  display: block;
}

.dropdown-item.active,
.dropdown-item:active,
.dropdown-submenu>.dropdown-menu>.dropdown-item>a {
  color: #212529;
}

.dropdown-submenu>.dropdown-menu>.dropdown-item>a:hover {
  text-decoration: none;
}

#iconBtnImportFile {
  height: 25px;
  width: 25px;
}

.geom-point:before {
  content: url('/remocra/static/img/pencil_point.png');
  margin-right: 7px;
}

.geom-linestring:before {
  content: url('/remocra/static/img/pencil_ligne.png');
  margin-right: 7px;
}

.geom-polygon:before {
  content: url('/remocra/static/img/pencil_polygone.png');
  margin-right: 7px;
}
</style>
