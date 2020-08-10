<template>
<div id="toolbar">
  <div v-for="(item, index) in toolbarItem" :key="index">
    <b-btn v-if="item.type == 'button'"
            :class="{'activeBtn' : (activeButton == item.name && item.name != null) && !isButtonDisabled(item.name) }"
            @click="item.onClick"
            :title="item.title"
            :disabled="isButtonDisabled(item.name)"
            :pressed="activeButton == item.name">
      <img :src="item.iconPath" width="16" height="16"/>
    </b-btn>

    <div v-if="item.type == 'libelle'" class="libelle">{{item.text}}</div>

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

import Circle from 'ol/geom/Circle'
import Feature from 'ol/Feature'
import * as OlProj from 'ol/proj'
import DragPan from 'ol/interaction/DragPan'
import DragBox from 'ol/interaction/DragBox'
import Style from 'ol/style/Style'
import Stroke from 'ol/style/Stroke'
import Point from 'ol/geom/Point'
import CircleStyle from 'ol/style/Circle'
import MouseWheelZoom from 'ol/interaction/MouseWheelZoom';

export default {
  name: 'ToolBar',
  components: {
    SearchCommune,
    SearchVoie,
    Measures,
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
      coucheActive: null,
      eventHandlers: [],
      selectedFeatures: [] // Liste des features actuellement sélectionnées
    }
  },

  watch: {
    // En cas de changement dans la sélection, on envoie un évènement pour informer les autres composants
    selectedFeatures: function() {
      this.$root.$options.bus.$emit(eventTypes.OLMAP_TOOLBAR_UPDATESELECTEDFEATURES, this.selectedFeatures);
    }
  },

  created() {
    var self = this;
    this.$root.$options.bus.$on(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM, this.addToolBarItem);
    this.$root.$options.bus.$on(eventTypes.OLMAP_TOOLBAR_TOGGLEBUTTON, this.toggleButton);
    this.$root.$options.bus.$on(eventTypes.OLMAP_COUCHES_UPDATECOUCHEACTIVE, (code) => {
      this.coucheActive = code;
      this.onSelectFeatures(null, true);
    });
    this.$root.$options.bus.$on(eventTypes.OLMAP_ONSELECTFEATURES, this.onSelectFeatures);
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
      type: "measures",
      name: "mesures",
      onToggle: (state) => {
        this.$root.$options.bus.$emit(eventTypes.OLMAP_MEASURES_TOGGLE, state);
      }
    });

    // Bouton info
    this.addToolBarItem({
      type: "button",
      name: "Infos",
      iconPath: "/remocra/static/img/information.png",
      title: "Obtenir des informations sur un point de la carte",
      onClick: () => {
        this.toggleButton("Infos");
      },
      onToggle: (state) => {
        if(!self.eventHandlers['clickInfo']) {
          self.eventHandlers['clickInfo'] = (e) => {
            self.$root.$options.bus.$emit(eventTypes.OLMAP_COUCHES_GETFEATURESFROMPOINT, eventTypes.OLMAP_SHOW_MODALEINFO , e.coordinate);
          };
        }

        if(state) {
          this.map.on("click", self.eventHandlers['clickInfo']);
        } else {
          this.map.un("click", self.eventHandlers['clickInfo']);
        }
      },
      disabled: () => {
        return this.coucheActive == null;
      }
    });

    // Bouton de sélection
    this.addToolBarItem({
      type: "button",
      name: "selectionPoint",
      iconPath: "/remocra/static/img/selection_point.png",
      onClick: () => {
        this.toggleButton("selectionPoint");
      },
      onToggle: (state) => {
        if(!self.eventHandlers['clickSelection']) {
          self.eventHandlers['clickSelection'] = (e) => {
            self.$root.$options.bus.$emit(eventTypes.OLMAP_COUCHES_GETFEATURESFROMPOINT, eventTypes.OLMAP_ONSELECTFEATURES, e.coordinate);
          };
        }

        if(!self.eventHandlers['dragSelection']) {
          self.eventHandlers['dragSelection'] = (e) => {
            self.$root.$options.bus.$emit(eventTypes.OLMAP_COUCHES_GETFEATURESFROMBBOX, eventTypes.OLMAP_ONSELECTFEATURES, e.getGeometry().getExtent());
          };
        }

        /** On supprime l'interaction de drag pour le remplacer par un autre
          * La nouvelle interaction pourra avoir une condition pour se déclencher (clic + ctrl) si l'outil est activé
          * Si il y a également une interaction DragBox, elle est aussi marquée pour suppression
          */
        this.removeMapInteraction(DragPan);
        this.removeMapInteraction(DragBox);

        if(state) {
          // DragPan avec appui sur touche CTRL
          this.map.addInteraction(new DragPan({
            condition: function(event) {
              return event.originalEvent.ctrlKey
            }
          }));

          // DragBox pour la sélection des features
          var dragBox = new DragBox({
            condition: function(event) {
              return !event.originalEvent.ctrlKey
            },
            style: new Style({
              stroke: new Stroke({
                color: [0, 0, 255, 1]
              })
            }),
            minArea: 25
          });
          dragBox.on('boxend', () => {
            self.eventHandlers['dragSelection'](dragBox);
          });

          this.map.addInteraction(dragBox);
          this.map.addInteraction(new MouseWheelZoom());

          this.map.on("click", self.eventHandlers['clickSelection']);
          this.onSelectFeatures(null, true);
        } else {
          // DragPan sans condition
          this.map.addInteraction(new DragPan());
          this.map.addInteraction(new MouseWheelZoom());
          this.map.un("click", self.eventHandlers['clickSelection']);
        }
      },
      disabled: () => {
        return this.coucheActive == null;
      },
    }),

    // Fullscreen
    this.addToolBarItem({
      type: "fullscreen"
    });
  },

  mounted: function() {

  },

  destroyed() {
    this.eventHandlers = [];
    this.removeMapInteraction(DragBox);
    this.$root.$options.bus.$off(eventTypes.OLMAP_TOOLBAR_ADDTOOLBARITEM);
    this.$root.$options.bus.$off(eventTypes.OLMAP_TOOLBAR_TOGGLEBUTTON);
    this.$root.$options.bus.$off(eventTypes.OLMAP_COUCHES_UPDATECOUCHEACTIVE);
    this.$root.$options.bus.$off(eventTypes.OLMAP_ONSELECTFEATURES);
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

    /**
      * Fonction appelée lors de la sélection/déselection de features
      * Si des features sont nouvellement sélectionnées, elles sont ajoutées à la liste de la sélection
      * Si des features étaient déjà sélectionnées, elles seront retirées de la liste de la sélection
      * @param features Tableau des features venant d'être sélectionnées
      * @param reset Boolean, vide le tableau de sélection actuel si TRUE
      */
    onSelectFeatures(features, reset) {
      if(reset) {
        this.selectedFeatures = [];
      }

      var featuresToRemove = [];
      _.forEach(features, f => {
        if(!_.find(this.selectedFeatures, function(o) { return o.id == f.id})) {
          this.selectedFeatures.push(f);
        } else {
          featuresToRemove.push(f);
        }
      });

      // Retrait des features sélectionnées étant déjà sélectionnées à l'état précédent
      _.forEach(featuresToRemove, ftr => {
        _.remove(this.selectedFeatures, function(o) { return o.id == ftr.id});
      });
      this.selectedFeatures = _.clone(this.selectedFeatures, true); // Nécessaire pour le déclenchement du watch de Vuejs

      // Mise à jour sur la carte de la sélection
      var selectionLayer = this.getLayerById('selectionLayer');
      selectionLayer.getSource().clear();
      _.forEach(this.selectedFeatures, feature => {
        var circle = new Feature(new Circle(
            OlProj.transform(feature.geometry.coordinates, 'EPSG:2154', 'EPSG:3857'),
        ));

        circle.setStyle(new Style({
          geometry: new Point(OlProj.transform(feature.geometry.coordinates, 'EPSG:2154', 'EPSG:3857')),
          image: new CircleStyle({
            radius: 12,
            stroke: new Stroke({
              color: 'blue',
              width: 2
            }),
          }),
        }));
        selectionLayer.getSource().addFeature(circle);
      })
    },

    /**
      * Retire les interactions de la map
      * @param interactionClass la classe de l'interaction
      * Comme le code est compilé, on passe en paramètre directement la classe et non pas un string contenant le nom de la classe:
      *   il est impossible de déterminer le nom de la classe, celui-ci étant modifié lors de la compilation
      */
    removeMapInteraction(interactionClass) {
      var dropInteraction = null;
      _.forEach(this.map.getInteractions().getArray(), interaction => {
        if(interaction.constructor.name == interactionClass.name) {
          dropInteraction = interaction;
        }
      });
      if(dropInteraction){
        this.map.removeInteraction(dropInteraction);
      }
    }

  }
}
</script>

<style scoped>

#toolbar {
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  display: inline-flex;
  max-width: 75%;
  border: 1px solid rgb(210,210,210);
  left: 50%;
  transform: translateX(-50%);
  z-index: 1;
  background-color: rgba(255,255,255,0.9);
  padding-bottom: 2px;
  padding-top: 1px;
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

.libelle {
  font-size: 14px;
  white-space: nowrap;
}
</style>
