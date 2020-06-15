<template>
<div id="toolbar">
  <div v-for="(item, index) in toolbarItem" :key="index">
    <b-btn v-if="item.type == 'button'"
            :class="{'activeBtn' : (activeButton == item.name && item.name != null) }"
            @click="item.onClick"
            :title="item.title"
            :disabled="isButtonDisabled(item.name)"
            :pressed="activeButton == item.name">
      <img :src="item.iconPath" width="16" height="16"/>
    </b-btn>

    <div v-if="item.type == 'libelle'">{{item.text}}</div>

    <div v-if="item.type == 'separator'" class="separator"></div>

    <SearchCommune v-if="item.type == 'searchCommune'"
       @communeSelected="item.onCommuneSelected" class="customComponent"></SearchCommune>

    <SearchVoie v-if="item.type == 'searchVoie'"
                :commune="commune"
                :disabled="commune == null"
                @onVoieSelected="item.onVoieSelected" class="customComponent"></SearchVoie>

    <Measures v-if="item.type == 'measures'" :map="map"></Measures>

    <b-btn v-if="item.type == 'fullscreen'" @click="GoInFullscreen" title="Plein écran"><img src="/remocra/static/img/fullscreen.svg"></b-btn>

  </div>
</div>
</template>

<script>
import * as eventTypes from '../../bus/event-types.js'
import SearchCommune from './SearchCommune.vue'
import Measures from './Measures.vue'
import SearchVoie from './SearchVoie.vue'

import _ from 'lodash'
export default {
  name: 'ToolBar',
  components: {
    SearchCommune,
    SearchVoie,
    Measures
  },
  props: {
    map: {
      required: true,
      type: Object
    }
  },
  data() {
    return {
      toolbarItem: [],

      navigation: {
        stack: [],
        idx: -1,
        btns: false
      },

      //itemDisabled: {},
      activeButton: null,
      commune: null,
    }
  },

  created() {
    this.$root.$options.bus.$on(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, this.addToolBarItem);
    this.$root.$options.bus.$on(eventTypes.OLMAP_TOOLBAR_TOGGLEBUTTON, this.toggleButton);
    // Définition de la vue courante comme première vue de la navigation
    let view = this.map.getView();
    this.navigation.stack.push({
      zoom: view.getZoom(),
      center: view.getCenter(),
      rotation: view.getRotation()
    });
    this.navigation.idx++

    // Zoom +
    this.addToolBarItem({
      iconPath: "/remocra/static/img/magnifier_zoom_in.png",
      type: "button",
      title: "Zoom avant",
      onClick: () => {
        this.map.getView().setZoom(this.map.getView().getZoom() + 1)
      }
    });

    // Zoom -
    this.addToolBarItem({
      iconPath: "/remocra/static/img/magnifier_zoom_out.png",
      type: "button",
      title: "Zoom arrière",
      onClick: () => {
        this.map.getView().setZoom(this.map.getView().getZoom() - 1)
      }
    });

    // Zoom précédent
    this.addToolBarItem({
      type: "button",
      name: "ZoomPrev",
      iconPath: "/remocra/static/img/zoom_prec.png",
      title: "Rétablir la vue précédente",
      disabled: () => {
        return this.navigation.idx <= 0;
      },
      onClick: () => {
        this.navigation.btns = true
        this.navigation.idx--
        let state = this.navigation.stack[this.navigation.idx]
        this.map.getView().animate(state)
      }
    });

    // Zoom suivant
    this.addToolBarItem({
      type: "button",
      name: "ZoomNext",
      iconPath: "/remocra/static/img/zoom_suiv.png",
      title: "Rétablir la vue suivante",
      disabled: () => {
          return this.navigation.stack.length - this.navigation.idx <= 1;
      },
      onClick: () => {
        this.navigation.btns = true
        if (this.navigation.idx < 10) {
          this.navigation.idx++
        }
        let state = this.navigation.stack[this.navigation.idx]
        this.map.getView().animate(state)
      }
    })


    // Zoom sur commune
    this.addToolBarItem({
      type: "searchCommune",
      onCommuneSelected: (commune) => {
        this.$emit('zoomToGeom', commune.geometrie);
        this.commune = commune;
        if (!this.navigation.btns) {
          let view = this.map.getView()
          //  Retrait des éléments "suivants" (cas "Zoom précédent" puis "Zoom manuel") et ajout du nouvel état
          this.navigation.stack.splice(this.navigation.idx + 1, this.navigation.stack.length - (this.navigation.idx + 1), {
            zoom: view.getZoom(),
            center: view.getCenter(),
            rotation: view.getRotation()
          })
          //  On limite à 10 entrées
          if (this.navigation.stack.length > 9) {
            this.navigation.stack.shift()
          } else {
            this.navigation.idx++
          }
        }
        this.navigation.btns = false
      }
    });

    // Zoom sur une voie
    this.addToolBarItem({
      type: "searchVoie",
      onVoieSelected: (voie) => {
        console.log(voie);
      }
    })

    // Mesures
    this.addToolBarItem({
      type: "measures"
    });

    // Bouton info
    this.addToolBarItem({
      type: "button",
      name: "Infos",
      iconPath: "/remocra/static/img/information.png",
      title: "Obtenir des informations sur un point de la carte",
      onClick: () => {
        this.toggleButton("Infos");
      }
    });

    // Fullscreen
    this.addToolBarItem({
      type: "fullscreen"
    });
  },

  mounted: function() {

  },

  methods: {
    /**
      * Ajoute un item dans la toolbar. Le type doit être précisé afin de savoir quel composant charger (voir template)
      * @item Object javascript contenant les attributs et fonctions de l'item (se référer aux objets existants et au template pour
      *   la liste des attributs nécessaires)
      * Si un item "fullscreen" est présent, celui-ci sera toujours à la droite de la toolbar
      */
    addToolBarItem(item) {
      var fullScreenItem = _.find(this.toolbarItem, function(i) {
        return i.type == 'fullscreen'
      });
      if(fullScreenItem) {
        this.toolbarItem.splice(this.toolbarItem.length - 1, 0, item);
      } else {
        this.toolbarItem.push(item);
      }

      //console.log(item);
    },

    activateMeasureInteraction() {
      //this.toggleButton('measureTools')
      this.removeMeasureInteraction()
    },

    /**
      * Indique si un bouton doit être désactivé
      * Cette fonction renvoie à la fonction disabled() définie lors de la création de l'objet. Pour respecter
      *   le fonctionnement de Vue JS, on n'y fait pas appel directement, l'appel à cette fonction permet de déclencher
      *   la mise à jour des composants.
      * Ceci ne fonctionne que sur les boutons ayant un attribut "name" déclaré. A défaut, le bouton sera considéré comme
      *   générique et ne bénificiera pas d'une spécification sur ses conditions de désactivation
      */
    isButtonDisabled(name) {
      if(name) {
        var items = _.filter(this.toolbarItem, function(i) { return i.name === name});
        if(items.length > 0 && items[0].disabled != undefined) {
          return items[0].disabled();
        }
      }
      return false;
    },

    toggleButton(name) {
      this.activeButton = (this.activeButton == name) ? null : name;
      if(name) {
        // On désactive tous les boutons actifs
        var items = _.filter(this.toolbarItem, (i) => { return i.name != null && i.name != this.activeButton});
        _.forEach(items, item => {
          if(item.onToggle != undefined) {
            return item.onToggle(false);
          }
        });

        // On active le bouton souhaité
        var item = _.filter(this.toolbarItem, (i) => { return i.name == this.activeButton});
        if(item.length > 0 && item[0].onToggle != undefined) {
          return item[0].onToggle(true);
        }
      }
    },

    getLayerById(id) {
      if (!this.map || !this.map.getLayers) {
        return null
      }
      var i
      for (i = 0; i < this.map.getLayers().getLength(); i++) {
        var layer = this.map.getLayers().item(i)
        if (layer.getProperties().code === id) {
          return layer
        }
      }
      return null
    },

    GoInFullscreen: function() {
      var elem = document.getElementById("olMap")
      if (document.fullscreenEnabled || document.webkitFullscreenEnabled || document.mozFullScreenEnabled || document.msFullscreenEnabled) {
        if (elem.requestFullscreen) {
          elem.requestFullscreen()
        } else if (elem.webkitRequestFullscreen) {
          elem.webkitRequestFullscreen()
        } else if (elem.mozRequestFullScreen) {
          elem.mozRequestFullScreen()
        } else if (elem.msRequestFullscreen) {
          elem.msRequestFullscreen()
        }
      }
    },

  }
}
</script>

<style scoped>

#toolbar {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  display: inline-flex;
  max-width: 75%;
  border: 1px solid rgb(210,210,210);
  top: 45px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1;
  background-color: rgba(255,255,255,0.9);
}

#toolbar button {
  background: none;
  border: none;
}

.customComponent {
  margin-right: 10px;
}

.activeBtn {
  background-color: rgb(200,200,200) !important;
}

.separator {
  border-right: 2px solid rgb(170,170,170);
  margin-right: 10px;
  width: 5px;
  height: 25px;
}
</style>
