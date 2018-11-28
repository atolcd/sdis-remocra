<template>
  <div id="modalFile">
    <b-modal ref="modalImportFile" title="Importer un fichier" ok-title="Valider" cancel-title="Annuler" @ok="handleOk" @hidden="clearFields">
      <form @submit.stop.prevent="handleSubmit">
        <b-form-group  horizontal label="Fichier:" label-for="inputFichier">
          <div class="custom-file b-form-file">
            <input id ="inputFichier" type="file" class="custom-file-input" v-on:input="fileSelect" accept=".gpx, .kml, .json, .geojson" required/>
            <label class="custom-file-label">{{file && file.name}}</label></div>
            <b-form-invalid-feedback id="fileInputFeedback" v-show="fileState">
              Seuls les fichiers gpx, kml ou geojson sont acceptés
            </b-form-invalid-feedback>
        </b-form-group>
        <b-form-group horizontal label="Nom de la couche:" label-for="layerName">
          <b-form-input id="layerName" type="text" v-model="layerName">
          </b-form-input>
        </b-form-group>
        <fieldset>
          <legend>Style</legend>
            <fieldset id="fsPoint">
              <legend>Point</legend>
              <b-form-group horizontal label="Rayon" label-for="pointRadius">
                <b-form-input id="pointRadius" v-on:change="updatePreview" type="range" min="0" max="15" v-model="pointRadius">
                </b-form-input>
              </b-form-group>
              <b-form-group horizontal label="Symbole" label-for="symbole">
                <b-form-select id="symbole" v-on:input="updatePreview" v-model="symbole" :options="optionsSymbole"></b-form-select>
              </b-form-group>
              <b-form-group horizontal label="Image" label-for="image">
                <b-form-select id="image" v-on:input="updatePreview" v-model="image" :options="optionsImage"></b-form-select>
              </b-form-group>
              <b-form-group horizontal label="Rotation en degrés" label-for="rotation">
                <b-form-input id="rotation" v-on:change="updatePreview" type="range" min="0" max="360" step="5" v-model="rotation">
                </b-form-input>
              </b-form-group>
            </fieldset>
            <fieldset id="fsStroke">
              <legend>Ligne</legend>
              <b-form-group horizontal label="Couleur" label-for="strokeColor">
                <b-form-input id="strokeColor" v-on:change="updatePreview" type="color" v-model="strokeColor">
                </b-form-input>
              </b-form-group>
              <b-form-group id="labelStrokeOpacity" horizontal label="Transparence" label-for="strokeOpacity">
                <b-form-input id="strokeOpacity" v-on:change="updatePreview" type="range" min="0" max="100" step="5" v-model="strokeOpacity">
                </b-form-input>
              </b-form-group>
              <b-form-group horizontal label="Epaisseur" label-for="thickness">
                <b-form-input id="thickness" v-on:change="updatePreview" type="range" min="0" max="10" step="1" v-model="thickness">
                </b-form-input>
              </b-form-group>
              <b-form-group horizontal label="Fins de lignes" label-for="endLine">
                <b-form-select id="endLine" v-on:input="updatePreview" v-model="endLine" :options="optionsEndLine"></b-form-select>
              </b-form-group>
              <b-form-group horizontal label="Style" label-for="strokeStyle">
                <b-form-select id="strokeStyle" v-on:input="updatePreview" v-model="strokeStyle" :options="optionsStrokeStyle"></b-form-select>
              </b-form-group>
            </fieldset>
            <fieldset id="fsFill">
              <legend>Remplissage</legend>
              <b-form-group horizontal label="Couleur" label-for="fillColor">
                <b-form-input id="fillColor" v-on:change="updatePreview" type="color" v-model="fillColor">
                </b-form-input>
              </b-form-group>
              <b-form-group id="labelFillOpacity" horizontal label="Transparence" label-for="fillOpacity">
                <b-form-input id="fillOpacity" v-on:change="updatePreview" type="range" min="0" max="100" step="5" v-model="fillOpacity">
                </b-form-input>
              </b-form-group>
            </fieldset>
            <div id="mapPreview">
            </div>
        </fieldset>
      </form>
    </b-modal>
  </div>
</template>

<script>
/* eslint-disable */
import Map from 'ol/Map.js';
import View from 'ol/View.js';
import {LineString, Point, Polygon} from 'ol/geom.js';
import {toContext} from 'ol/render.js';
import {Circle as CircleStyle, Fill, Stroke, Style} from 'ol/style.js';
import GeoJSON from 'ol/format/GeoJSON';
import GPX from 'ol/format/GPX';
import KML from 'ol/format/KML';
import * as Color from 'ol/color';
import Feature from 'ol/Feature';
import RegularShape from 'ol/style/RegularShape';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import Icon from 'ol/style/Icon';
import {defaults as defaultControls,FullScreen} from 'ol/control.js'

export default {
  name: 'modalImportFile',
  data: function() {
    return {
      fileState: false,
      file: null,
      layerName: '',
      pointRadius: 5,
      symbole: null,
      optionsSymbole: [
      { value: null, text: 'Aucun' },
      { value: 'square', text: 'Carré' },
      { value: 'circle', text: 'Cercle' },
      { value: 'cross', text: 'Croix' },
      { value: 'star', text: 'Etoile' },
      { value: 'triangle', text: 'Triangle' },
      { value: 'x', text: 'X' }
      ],
      image: null,
      optionsImage: [
      { value: null, text: 'Aucun' },
      { value: 'cis', text: 'CIS' },
      { value: 'camping', text: 'Camping' },
      { value: 'dfci', text: 'DFCI tour de guet' },
      { value: 'gare', text: 'Gare' },
      { value: 'departFeu', text: 'Départ feu' },
      { value: 'departFeuBleu', text: 'Départ feu bleu' }
      ],
      rotation: 0,
      strokeColor: "#000000",
      strokeOpacity: 100,
      thickness: 2,
      endLine: 'round',
      optionsEndLine:[
      { value: 'round', text: 'Arrondies' },
      { value: 'square', text: 'Carrées' },
      { value: 'butt', text: 'Droites' }
      ],
      strokeStyle: 'solid',
      optionsStrokeStyle:[
      { value: 'solid', text: 'Continu' },
      { value: 'dot', text: 'Pointillés' },
      { value: 'dash', text: 'Tirets' },
      { value: 'dashdot', text: 'Tirets / Pointillés' },
      { value: 'longdash', text: 'Tirets longs' },
      { value: 'longdashdot', text: 'Tirets longs / Pointillés' }
      ],
      fillColor: "#000000",
      fillOpacity: 25,
      styles: null,
      styleLigne: [],
      stylesKeys: [],
      imageUrl: '',
      listeUrl: ['/static/img/bat_cis.png', '/static/img/bat_camping.png', '/static/img/dfci_tour_guet.png', '/static/img/res_fer_gare.png', '/static/img/rci.png', '/static/img/rci-tmp.png'],
      nbGpx: 1,
      nbKml: 1,
      nbGeojson: 1,
      map: {
        type: Object,
        default: {}
      }
    }
  },
  methods: {
    // ouverture de la fenêtre modale
    openModal: function() {
      this.$refs.modalImportFile.show();
      this.createMapPreview();
    },
    // sélection du fichier saisi dans l'input
    fileSelect(){
      var fileList = document.getElementById('inputFichier');
      this.file = fileList.files[0];
    },
    // définition des styles pour tous les symmboles
    defineStyles: function(){

      // Personnalisation du style de la ligne suivant le choix de l'uti
      switch(this.strokeStyle){
        case "dot":
          this.styleLigne = [0, 10];
          break;
        case "dash":
          this.styleLigne = [10];
          break;
        case "dashdot":
          this.styleLigne = [10, 0, 10];
          break;
        case "longdash":
          this.styleLigne = [30];
          break;
        case "longdashdot":
          this.styleLigne = [30, 0, 30];
          break;
        default:
          this.styleLigne = [1];
      }

      // Personnalisation de l'image suivant le choix de l'uti
      switch(this.image){
        case "cis":
          this.imageUrl = this.listeUrl[0];
          break;
        case "camping":
          this.imageUrl = this.listeUrl[1];
          break;
        case "dfci":
          this.imageUrl = this.listeUrl[2];
          break;
        case "gare":
          this.imageUrl = this.listeUrl[3];
          break;
        case "departFeu":
          this.imageUrl = this.listeUrl[4];
          break;
        case "departFeuBleu":
          this.imageUrl = this.listeUrl[5];
          break;
      }

      // on récupère la couleur en hexa pour la mettre en rgba
      var strokeRgbaColor = Color.asArray(this.strokeColor);
      strokeRgbaColor = strokeRgbaColor.slice();
      // on applique l'opacité à la couleur
      strokeRgbaColor[3] = this.strokeOpacity/100;

      // on récupère la couleur en hexa pour la mettre en rgba
      var fillRgbaColor = Color.asArray(this.fillColor);
      fillRgbaColor = fillRgbaColor.slice();
      // on applique l'opacité à la couleur
      fillRgbaColor[3] = this.fillOpacity/100;

      // Déclaration des différents styles pour les différents symboles
      this.styles = {
        'square': new Style({
          image: new RegularShape({
            points: 4,
            radius: this.pointRadius,
            rotation: this.rotation * Math.PI/180,
            angle: Math.PI / 4,
            fill: new Fill({
              color: fillRgbaColor
            }),
            stroke: new Stroke({
              color: strokeRgbaColor,
              width: this.thickness,
              lineCap: this.endLine,
              lineDash: this.styleLigne
            })
          }),
          stroke: new Stroke({
            color: strokeRgbaColor,
            width: this.thickness,
            lineCap: this.endLine,
            lineDash: this.styleLigne
          }),
          // style pour le remplissage
          fill: new Fill({
            color: fillRgbaColor
          })
        }),
        'circle': new Style({
          image: new RegularShape({
            points: 10,
            radius: this.pointRadius,
            rotation: this.rotation * Math.PI/180,
            fill: new Fill({
              color: fillRgbaColor
            }),
            stroke: new Stroke({
              color: strokeRgbaColor,
              width: this.thickness,
              lineCap: this.endLine,
              lineDash: this.styleLigne
            })
          }),
          stroke: new Stroke({
            color: strokeRgbaColor,
            width: this.thickness,
            lineCap: this.endLine,
            lineDash: this.styleLigne
          }),
          // style pour le remplissage
          fill: new Fill({
            color: fillRgbaColor
          })
        }),
        'cross': new Style({
          image: new RegularShape({
            points: 4,
            radius: this.pointRadius,
            rotation: this.rotation * Math.PI/180,
            radius2: 0,
            angle: 0,
            fill: new Fill({
              color: fillRgbaColor
            }),
            stroke: new Stroke({
              color: strokeRgbaColor,
              width: this.thickness,
              lineCap: this.endLine,
              lineDash: this.styleLigne
            })
          }),
          stroke: new Stroke({
            color: strokeRgbaColor,
            width: this.thickness,
            lineCap: this.endLine,
            lineDash: this.styleLigne
          }),
          // style pour le remplissage
          fill: new Fill({
            color: fillRgbaColor
          })
        }),
        'star': new Style({
          image: new RegularShape({
            points: 5,
            radius: this.pointRadius,
            rotation: this.rotation * Math.PI/180,
            radius2: this.pointRadius * 0.4,
            angle: 0,
            fill: new Fill({
              color: fillRgbaColor
            }),
            stroke: new Stroke({
              color: strokeRgbaColor,
              width: this.thickness,
              lineCap: this.endLine,
              lineDash: this.styleLigne
            })
          }),
          stroke: new Stroke({
            color: strokeRgbaColor,
            width: this.thickness,
            lineCap: this.endLine,
            lineDash: this.styleLigne
          }),
          // style pour le remplissage
          fill: new Fill({
            color: fillRgbaColor
          })
        }),
        'triangle': new Style({
          image: new RegularShape({
            points: 3,
            radius: this.pointRadius,
            rotation: this.rotation * Math.PI/180,
            angle: 0,
            fill: new Fill({
              color: fillRgbaColor
            }),
            stroke: new Stroke({
              color: strokeRgbaColor,
              width: this.thickness,
              lineCap: this.endLine,
              lineDash: this.styleLigne
            })
          }),
          stroke: new Stroke({
            color: strokeRgbaColor,
            width: this.thickness,
            lineCap: this.endLine,
            lineDash: this.styleLigne
          }),
          // style pour le remplissage
          fill: new Fill({
            color: fillRgbaColor
          })
        }),
        'x': new Style({
          image: new RegularShape({
            points: 4,
            radius: this.pointRadius,
            rotation: this.rotation * Math.PI/180,
            radius2: 0,
            angle: Math.PI / 4,
            fill: new Fill({
              color: fillRgbaColor
            }),
            stroke: new Stroke({
              color: strokeRgbaColor,
              width: this.thickness,
              lineCap: this.endLine,
              lineDash: this.styleLigne
            })
          }),
          stroke: new Stroke({
            color: strokeRgbaColor,
            width: this.thickness,
            lineCap: this.endLine,
            lineDash: this.styleLigne
          }),
          // style pour le remplissage
          fill: new Fill({
            color: fillRgbaColor
          })
        })
      };
      // liste des différents styles des symboles
      this.stylesKeys = ['square', 'circle', 'cross', 'star', 'triangle', 'x'];
    },
    // lecture du fichier pour la création de la nouvelle couche
    lectureFichier: function(){
      // on créé une variable pour lire le document
      var reader = new FileReader();
      // on lit le fichier comme si c'était un texte
      reader.readAsText(this.file, "UTF-8");
      // on sauvegarde le this car on le perd dans reader.onload
      var self = this;
      // lorsque le fichier a bien été lu
      reader.onload = function (evt) {
        var event = evt;
        // variable contenant l'extension du fichier
        var extension = self.file.type;
        if(extension.includes("gpx")){
          self.creationCoucheGpx(event);
        } else if (extension.includes("json")) {
          self.creationCoucheJson(event);
        } else if (extension.includes("kml")) {
          self.creationCoucheKml(event);
        } else{
          console.log("error");
        }
      }
    },
    // création d'une couche lors de la présence d'un fichier GPX
    creationCoucheGpx: function(evt){
      // on récupère le code du layers
      var layerCode = this.nbGpx + 'gpx';
      // création de variable lorsqu'il s'agit d'un format gpx
      var gpxLayer = new VectorLayer({
        source: new VectorSource({
        }),
        title: this.layerName,
        code: layerCode
      });
      var gpxFormat = new GPX();
      var gpxFeatures;
      // on ajoutes les features à la variable
      gpxFeatures = gpxFormat.readFeatures(evt.target.result,{
        // projection réalisée pour positionner les données
        dataProjection:'EPSG:4326',
        featureProjection:'EPSG:3857'
      });
      // si aucune image n'est choisi
      if(this.image == null){
        // on parcours les features que l'on a dans la couche
        for(var i = 0; i < gpxFeatures.length; i++){
          // ajout du style correspondant suivant le symbole choisi
          switch(this.symbole){
            case 'square':
              gpxFeatures[i].setStyle(this.styles[this.stylesKeys[0]]);
              break;
            case 'circle':
              gpxFeatures[i].setStyle(this.styles[this.stylesKeys[1]]);
              break;
            case 'cross':
              gpxFeatures[i].setStyle(this.styles[this.stylesKeys[2]]);
              break;
            case 'star':
              gpxFeatures[i].setStyle(this.styles[this.stylesKeys[3]]);
              break;
            case 'triangle':
              gpxFeatures[i].setStyle(this.styles[this.stylesKeys[4]]);
              break;
            case 'x':
              gpxFeatures[i].setStyle(this.styles[this.stylesKeys[5]]);
              break;
            default:
              gpxFeatures[i].setStyle(this.styles[this.stylesKeys[1]]);
          }
        }
      // sinon on n'applique pas le style des symboles mais celui des images
      } else {
        // on récupère la couleur en hexa pour la mettre en rgba
        var strokeRgbaColor = Color.asArray(this.strokeColor);
        strokeRgbaColor = strokeRgbaColor.slice();
        // on applique l'opacité à la couleur
        strokeRgbaColor[3] = this.strokeOpacity/100;
        // on récupère la couleur en hexa pour la mettre en rgba
        var fillRgbaColor = Color.asArray(this.fillColor);
        fillRgbaColor = fillRgbaColor.slice();
        // on applique l'opacité à la couleur
        fillRgbaColor[3] = this.fillOpacity/100;
        // création d'un style lorsqu'il y a une image
        var styleImage = new Style({
          image : new Icon({
            scale: this.pointRadius/3,
            rotation: this.rotation * Math.PI/180,
            src: this.imageUrl
          }),
          stroke: new Stroke({
            color: strokeRgbaColor,
            width: this.thickness,
            lineCap: this.endLine,
            lineDash: this.styleLigne
          }),
          // style pour le remplissage
          fill: new Fill({
            color: fillRgbaColor
          })
        });
        // on parcours les features que l'on a dans la couche
        for(var i = 0; i < gpxFeatures.length; i++){
          gpxFeatures[i].setStyle(styleImage);
        }
      }
      // on ajoute les features à la source de notre nouvelle couche
      gpxLayer.getSource().addFeatures(gpxFeatures);
      // on envoie à la carte le layer créé et les infos de celui-ci
      var donnees = {"newLayer": gpxLayer, "layerName": this.layerName, "layerCode": layerCode};
      this.nbGpx++;
      this.$parent.modalImportFileValider(donnees);
      this.$refs.modalImportFile.hide();
    },
    // création d'une couche lors de la présence d'un fichier GeoJson
    creationCoucheJson: function(evt){
      // on récupère le code du layers
      var layerCode = this.nbGeojson + 'geojson';
      // création de variable lorsqu'il s'agit d'un format geojson
      var geojsonLayer = new VectorLayer({
        source: new VectorSource({
        }),
        title: this.layerName,
        code: layerCode
      });
      var geojsonFormat = new GeoJSON();
      var geojsonFeatures;
      // on ajoutes les features à la variable
      geojsonFeatures = geojsonFormat.readFeatures(evt.target.result,{
        // projection réalisée pour positionner les données
        dataProjection:'EPSG:4326',
        featureProjection:'EPSG:3857'
      });
      // si aucune image n'est choisi
      if(this.image == null){
        // on parcours les features que l'on a dans la couche
        for(var i = 0; i < geojsonFeatures.length; i++){
          // ajout du style correspondant suivant le symbole choisi
          switch(this.symbole){
            case 'square':
              geojsonFeatures[i].setStyle(this.styles[this.stylesKeys[0]]);
              break;
            case 'circle':
              geojsonFeatures[i].setStyle(this.styles[this.stylesKeys[1]]);
              break;
            case 'cross':
              geojsonFeatures[i].setStyle(this.styles[this.stylesKeys[2]]);
              break;
            case 'star':
              geojsonFeatures[i].setStyle(this.styles[this.stylesKeys[3]]);
              break;
            case 'triangle':
              geojsonFeatures[i].setStyle(this.styles[this.stylesKeys[4]]);
              break;
            case 'x':
              geojsonFeatures[i].setStyle(this.styles[this.stylesKeys[5]]);
              break;
            default:
              geojsonFeatures[i].setStyle(this.styles[this.stylesKeys[1]]);
          }
        }
      // sinon on n'applique pas le style des symboles mais celui des images
      } else {
        // on récupère la couleur en hexa pour la mettre en rgba
        var strokeRgbaColor = Color.asArray(this.strokeColor);
        strokeRgbaColor = strokeRgbaColor.slice();
        // on applique l'opacité à la couleur
        strokeRgbaColor[3] = this.strokeOpacity/100;
        // on récupère la couleur en hexa pour la mettre en rgba
        var fillRgbaColor = Color.asArray(this.fillColor);
        fillRgbaColor = fillRgbaColor.slice();
        // on applique l'opacité à la couleur
        fillRgbaColor[3] = this.fillOpacity/100;
        // création d'un style lorsqu'il y a une image
        var styleImage = new Style({
          image : new Icon({
            scale: this.pointRadius/3,
            rotation: this.rotation * Math.PI/180,
            src: this.imageUrl
          }),
          stroke: new Stroke({
            color: strokeRgbaColor,
            width: this.thickness,
            lineCap: this.endLine,
            lineDash: this.styleLigne
          }),
          // style pour le remplissage
          fill: new Fill({
            color: fillRgbaColor
          })
        });
        // on parcours les features que l'on a dans la couche
        for(var i = 0; i < geojsonFeatures.length; i++){
          geojsonFeatures[i].setStyle(styleImage);
        }
      }
      // on ajoute les features à la source de notre nouvelle couche
      geojsonLayer.getSource().addFeatures(geojsonFeatures);
      // on envoie à la carte le layer créé et les infos de celui-ci
      var donnees = {"newLayer": geojsonLayer, "layerName": this.layerName, "layerCode": layerCode};
      this.nbGeojson++;
      this.$parent.modalImportFileValider(donnees);
      this.$refs.modalImportFile.hide();
    },
    // création d'une couche lors de la présence d'un fichier Kml
    creationCoucheKml: function(evt){
      // on récupère le code du layers
      var layerCode = this.nbKml + 'kml';
      // création de variable lorsqu'il s'agit d'un format kml
      var kmlLayer = new VectorLayer({
        source: new VectorSource({
        }),
        title: this.layerName,
        code: layerCode
      });
      var kmlFormat = new KML();
      var kmlFeatures;
      // on ajoutes les features à la variable
      kmlFeatures = kmlFormat.readFeatures(evt.target.result,{
        // projection réalisée pour positionner les données
        dataProjection:'EPSG:4326',
        featureProjection:'EPSG:3857'
      });
      // si aucune image n'est choisi
      if(this.image == null){
        // on parcours les features que l'on a dans la couche
        for(var i = 0; i < kmlFeatures.length; i++){
          // ajout du style correspondant suivant le symbole choisi
          switch(this.symbole){
            case 'square':
              kmlFeatures[i].setStyle(this.styles[this.stylesKeys[0]]);
              break;
            case 'circle':
              kmlFeatures[i].setStyle(this.styles[this.stylesKeys[1]]);
              break;
            case 'cross':
              kmlFeatures[i].setStyle(this.styles[this.stylesKeys[2]]);
              break;
            case 'star':
              kmlFeatures[i].setStyle(this.styles[this.stylesKeys[3]]);
              break;
            case 'triangle':
              kmlFeatures[i].setStyle(this.styles[this.stylesKeys[4]]);
              break;
            case 'x':
              kmlFeatures[i].setStyle(this.styles[this.stylesKeys[5]]);
              break;
            default:
              kmlFeatures[i].setStyle(this.styles[this.stylesKeys[1]]);
          }
        }
      // sinon on n'applique pas le style des symboles mais celui des images
      } else {
        // on récupère la couleur en hexa pour la mettre en rgba
        var strokeRgbaColor = Color.asArray(this.strokeColor);
        strokeRgbaColor = strokeRgbaColor.slice();
        // on applique l'opacité à la couleur
        strokeRgbaColor[3] = this.strokeOpacity/100;
        // on récupère la couleur en hexa pour la mettre en rgba
        var fillRgbaColor = Color.asArray(this.fillColor);
        fillRgbaColor = fillRgbaColor.slice();
        // on applique l'opacité à la couleur
        fillRgbaColor[3] = this.fillOpacity/100;
        // création d'un style lorsqu'il y a une image
        var styleImage = new Style({
          image : new Icon({
            scale: this.pointRadius/3,
            rotation: this.rotation * Math.PI/180,
            src: this.imageUrl
          }),
          stroke: new Stroke({
            color: strokeRgbaColor,
            width: this.thickness,
            lineCap: this.endLine,
            lineDash: this.styleLigne
          }),
          // style pour le remplissage
          fill: new Fill({
            color: fillRgbaColor
          })
        });
        // on parcours les features que l'on a dans la couche
        for(var i = 0; i < kmlFeatures.length; i++){
          kmlFeatures[i].setStyle(styleImage);
        }
      }
      // on ajoute les features à la source de notre nouvelle couche
      kmlLayer.getSource().addFeatures(kmlFeatures);
      // on envoie à la carte le layer créé et les infos de celui-ci
      var donnees = {"newLayer": kmlLayer, "layerName": this.layerName, "layerCode": layerCode};
      this.nbKml++;
      this.$parent.modalImportFileValider(donnees);
      this.$refs.modalImportFile.hide();
    },
    // création de la map de prévisualisation
    createMapPreview: function() {

      // définition des styles
      this.defineStyles();

      // on récupère la couleur en hexa pour la mettre en rgba
      var strokeRgbaColor = Color.asArray(this.strokeColor);
      strokeRgbaColor = strokeRgbaColor.slice();
      // on applique l'opacité à la couleur
      strokeRgbaColor[3] = this.strokeOpacity/100;

      // on récupère la couleur en hexa pour la mettre en rgba
      var fillRgbaColor = Color.asArray(this.fillColor);
      fillRgbaColor = fillRgbaColor.slice();
      // on applique l'opacité à la couleur
      fillRgbaColor[3] = this.fillOpacity/100;
      // création de la carte de prévisualisation
      this.map = new Map({
        controls: [],
        view: new View({
          projection : 'EPSG:3857',
          center: [0, 0],
          zoom: 2
        }),
        layers:[],
        target: 'mapPreview'
      });

      // création des différentes géométries
      var pointGeometry = new Point([-7, 10]);
      var pointFeature = new Feature({});
      pointFeature.setGeometry(pointGeometry);

      var strokeGeometry = new LineString([[0, 10], [5, 7], [7, 13]])
      var strokeFeature = new Feature({});
      strokeFeature.setGeometry(strokeGeometry);

      var polygonGeometry = new Polygon([[[13, 8], [23, 8], [23, 13], [13, 13]]])
      var polygonFeature = new Feature({});
      polygonFeature.setGeometry(polygonGeometry);

      // création du layer ainsi que de sa source
      var vectorLayer = new VectorLayer({
        source: new VectorSource({
        })
      });
      // ajout des features au layer
      vectorLayer.getSource().addFeature(pointFeature);
      vectorLayer.getSource().addFeature(strokeFeature);
      vectorLayer.getSource().addFeature(polygonFeature);

      // si aucune image n'est choisi
      if(this.image == null){
        // ajout du style correspondant suivant le symbole choisi
        switch(this.symbole){
          case 'square':
            vectorLayer.setStyle(this.styles[this.stylesKeys[0]]);
            break;
          case 'circle':
            vectorLayer.setStyle(this.styles[this.stylesKeys[1]]);
            break;
          case 'cross':
            vectorLayer.setStyle(this.styles[this.stylesKeys[2]]);
            break;
          case 'star':
            vectorLayer.setStyle(this.styles[this.stylesKeys[3]]);
            break;
          case 'triangle':
            vectorLayer.setStyle(this.styles[this.stylesKeys[4]]);
            break;
          case 'x':
            vectorLayer.setStyle(this.styles[this.stylesKeys[5]]);
            break;
          default:
            vectorLayer.setStyle(this.styles[this.stylesKeys[1]]);
        }
      // sinon on n'applique pas le style des symboles mais celui des images
      } else {
        // création d'un style lorsqu'il y a une image
        var styleImage = new Style({
          image : new Icon({
            scale: this.pointRadius/3,
            rotation: this.rotation * Math.PI/180,
            src: this.imageUrl
          }),
          stroke: new Stroke({
            color: strokeRgbaColor,
            width: this.thickness,
            lineCap: this.endLine,
            lineDash: this.styleLigne
          }),
          // style pour le remplissage
          fill: new Fill({
            color: fillRgbaColor
          })
        });
        // ajout du style
        vectorLayer.setStyle(styleImage);
      }

      // ajout du layer à la map
      this.map.addLayer(vectorLayer);
      var self = this;
      // on met un setTimeOut pour rêgler le problème lors du rendu de la prévisu
      setTimeout(function(){
        // on recentre la vue sur les features ajoutées
        self.map.getView().fit(vectorLayer.getSource().getExtent());
        // on met à jour la taille de la carte
        self.map.updateSize();
      }, 100);
    },
    // mise à jour à chaque modification de la map de prévisualisation
    updatePreview: function(){
      if(this.map != null){
        // définition des styles
        this.defineStyles();

        // on récupère la couleur en hexa pour la mettre en rgba
        var strokeRgbaColor = Color.asArray(this.strokeColor);
        strokeRgbaColor = strokeRgbaColor.slice();
        // on applique l'opacité à la couleur
        strokeRgbaColor[3] = this.strokeOpacity/100;

        // on récupère la couleur en hexa pour la mettre en rgba
        var fillRgbaColor = Color.asArray(this.fillColor);
        fillRgbaColor = fillRgbaColor.slice();
        // on applique l'opacité à la couleur
        fillRgbaColor[3] = this.fillOpacity/100;
        // on récupère le layer de la prévisualisation
        var layer = this.map.getLayers().array_[0];

        // si aucune image n'est choisi
        if(this.image == null){
          // ajout du style correspondant suivant le symbole choisi
          switch(this.symbole){
            case 'square':
              layer.setStyle(this.styles[this.stylesKeys[0]]);
              break;
            case 'circle':
              layer.setStyle(this.styles[this.stylesKeys[1]]);
              break;
            case 'cross':
              layer.setStyle(this.styles[this.stylesKeys[2]]);
              break;
            case 'star':
              layer.setStyle(this.styles[this.stylesKeys[3]]);
              break;
            case 'triangle':
              layer.setStyle(this.styles[this.stylesKeys[4]]);
              break;
            case 'x':
              layer.setStyle(this.styles[this.stylesKeys[5]]);
              break;
            default:
              layer.setStyle(this.styles[this.stylesKeys[1]]);
          }
        // sinon on n'applique pas le style des symboles mais celui des images
        } else {
          // création d'un style lorsqu'il y a une image
          var styleImage = new Style({
            image : new Icon({
              scale: this.pointRadius/3,
              rotation: this.rotation * Math.PI/180,
              src: this.imageUrl
            }),
            stroke: new Stroke({
              color: strokeRgbaColor,
              width: this.thickness,
              lineCap: this.endLine,
              lineDash: this.styleLigne
            }),
            // style pour le remplissage
            fill: new Fill({
              color: fillRgbaColor
            })
          });
          layer.setStyle(styleImage);
        }
      }
    },
    handleOk(evt) {
      // Prevent modal from closing
      evt.preventDefault()
      // gère un bug car la fonction est déclenché lorsqu'on réinitialise l'input à la fermeture de la modale
      if((this.file != null) && (this.layerName != "")){
        // on vérifie si le fichier choisi est bien d'un type autorisé
        if ((this.file.type.includes("gpx")) || (this.file.type.includes("kml")) || (this.file.type.includes("json")) || (this.file.type.includes("geojson"))){
          this.handleSubmit();
        // sinon on déclanche l'action d'afficher un message
        } else {
          // en passant fileState à vrai on affiche la recommandation des fichiers
          return this.fileState = true;
        }
      } else {
        alert('Veuillez saisir les champs obligatoires')
      }
    },
    handleSubmit() {
      this.lectureFichier();
    },
    clearFields() {
      this.fileState = false;
      this.file = null;
      document.getElementById('inputFichier').value = "";
      this.layerName = '';
      this.pointRadius = 5;
      this.symbole = null;
      this.image = null;
      this.rotation = 0;
      this.strokeColor = '#000000';
      this.strokeOpacity = 100;
      this.thickness = 2;
      this.endLine = 'round';
      this.strokeStyle = 'solid';
      this.fillColor = '#000000';
      this.fillOpacity = 25;
      this.map = null;
      document.getElementById('mapPreview').innerHTML = "";
    }
  }
}
</script>

<style scoped>
  #strokeOpacity{
    direction: rtl;
  }
  #fillOpacity{
    direction: rtl;
  }
  fieldset{
      text-align: left;
      border: 1px groove #000000 !important;
      padding: 0 1.1em 1.1em 1.1em !important;
      margin: 0 0 1.1em 0 !important;
      -webkit-box-shadow:  0px 0px 0px 0px #000;
              box-shadow:  0px 0px 0px 0px #000;
  }
  #fsPoint{
    float: left;
    width: 280px;
    height: 200px;
  }
  #fsStroke{
    float: right;
    width: 280px;
    height: 200px;
  }
  #fsFill{
    float: left;
    width: 280px;
  }
  legend {
      font-size: 1.2em !important;
      font-weight: bold !important;
      text-align: left !important;
      width:auto;
      padding:0 10px;
      border-bottom:none;
  }
  #fileInputFeedback{
    text-align: left;
    display: block;
  }
  #mapPreview{
    background-color: white;
    border: 1px solid black;
    float: right;
    width: 280px;
    height: 87px;
    margin-top: 13px;
  }
  #modalFile >>> .modal-content{
    width: 630px;
  }
  #labelStrokeOpacity{
    font-size: 10px;
  }
  #labelFillOpacity{
    font-size: 10px;
  }
</style>
