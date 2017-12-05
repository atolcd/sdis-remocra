Ext.ns('Sdis.Remocra.widget.map.controls.GetFeaturesInfo');

Ext.require('Ext.XTemplate');

//Pour la gestion des dépendances
Ext.define('Sdis.Remocra.widget.map.controls.GetFeaturesInfo', {});

OpenLayers.Renderer.symbol.thincross = [0, 0, -2, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, -2, 0, 0];

Sdis.Remocra.widget.map.controls.GetFeaturesInfo = OpenLayers.Class(OpenLayers.Control, {

    // Paramètre en entrée
    renderTpls: null,
    defaultMarginInPixels: 5,
    
    // On laisse 30 secondes aux requêtes pour arriver. Passé ce délai, toute requête est ignorée
    requestsDelay: 15*1000,
    requestTimerId: null,
    requestsCounter: 0,
    contentToShow: [],
    working: false,
    
    autoDeactivate: false,
    
    genTpl: new Ext.XTemplate(
        '<div><ul>',
            '<tpl for="k">',
                '<tpl if="values && parent.v[xindex-1]">',
                    '<li style="margin-bottom:10px;">',
                    '<u>{[values.charAt(0).toUpperCase()+values.substr(1, values.length)]}',
                    ' :</u> ',
                    '{[parent.v[xindex-1]]}',
                    '</li>',
                '</tpl>',
            '</tpl>',
        '</ul></div>'
    ),
    
    initialize: function(options) {
        options = options || {};
        options.handlerOptions = options.handlerOptions || {};

        OpenLayers.Control.prototype.initialize.apply(this, [options]);
        if (this.renderTpls==null) {
            this.renderTpls = {};
        }
        
        this.handler = new OpenLayers.Handler.Click(
            this, {"click": this.getInfoForClick}, this.handlerOptions.click || {});
        
        this.wMSGetFeatureInfo = new OpenLayers.Control.WMSGetFeatureInfo({
            drillDown: true, queryVisible: true
        });
        
        // A la volée, on définit la carte à la volée si besoin (sans ça, pb de viewport)
        // Et on crée / détruit la couche de mise en évidence
        this.events.register("activate", this, function() {
            if (!this.wMSGetFeatureInfo.map) {
                this.wMSGetFeatureInfo.setMap(this.map);
            }
            this.createHighlightLayer();
        });
        this.events.register("deactivate", this, function() {
            this.destroyHighlightLayer();
        });
    },

    // ----------
    // Gestion de la couche de mise en évidence
    // ----------
    destroyHighlightLayer: function() {
        if (this.highlightLayer.map != null) {
            this.highlightLayer.destroyFeatures();
            this.highlightPoint = null;
        }
        this.highlightLayer = null;
    },
    createHighlightLayer: function() {
        if (this.highlightLayer == null) {
            this.highlightLayer = new OpenLayers.Layer.Vector("Info du point", {
                displayInLayerSwitcher: false, 
                isBaseLayer: false,
                calculateInRange: OpenLayers.Function.True,
                interrogeable: false
                }
            );
        }
        this.map.addLayer(this.highlightLayer);
        this.highlightStyle = OpenLayers.Util.extend(OpenLayers.Feature.Vector.style['default'], {
            graphicName: "thincross",
            strokeColor: "red",
            strokeWidth: 1,
            pointRadius: 5,
            fillOpacity: 0
        });
    },
    createHighlightFeature: function(pixel) {
        var lonlat = this.highlightLayer.getLonLatFromViewPortPx(pixel); 
        var geometry = new OpenLayers.Geometry.Point(
            lonlat.lon, lonlat.lat
        );
        this.highlightPoint = new OpenLayers.Feature.Vector(geometry);
        this.highlightPoint.geometry.clearBounds();
        this.highlightLayer.addFeatures([this.highlightPoint], {silent: true});
    },
    highlightPixel: function(pixel) {
        if(!this.highlightPoint) {
            this.createHighlightFeature(pixel);
        }
        var lonlat = this.highlightLayer.getLonLatFromViewPortPx(pixel); 
        this.highlightPoint.geometry.x = lonlat.lon;
        this.highlightPoint.geometry.y = lonlat.lat;
        this.highlightPoint.geometry.clearBounds();
        this.highlightLayer.drawFeature(this.highlightPoint, this.highlightStyle);
    },
    
    // ----------
    // Démarrage / Arrêt de la recherche
    // ----------
    
    startStopWorking: function(start) {
        if (start===true) {
            // Démarrage : on lance le timer et on génère un id de clic
            this.requestTimerId = setInterval(Ext.bind(this.showResult, this, []), this.requestsDelay);
            this.idClic = Ext.id();
            OpenLayers.Element.addClass(this.map.viewPortDiv, "olCursorWait");
        } else {
            // Arrêt : on arrête le timer
            if(this.requestTimerId) {
                clearInterval(this.requestTimerId);
            }
            this.idClic = null;
            OpenLayers.Element.removeClass(this.map.viewPortDiv, "olCursorWait");
        }
        this.requestsCounter=0;
        this.contentToShow = [];
        this.working=start;
    },
    
    // ----------
    // Information au clic
    // ----------
    
    getInfoForClick: function(evt) {
        if (this.working) {
            Sdis.Remocra.util.Msg.msg('Informations', 'Une requête est toujours en cours d\'exécution. Merci, de patienter.', 3);
            return;
        }
        this.events.triggerEvent("beforegetfeatureinfo", {xy: evt.xy});
        OpenLayers.Element.addClass(this.map.viewPortDiv, "olCursorWait");

        this.highlightPixel(evt.xy);
        
        // Start working
        this.startStopWorking(true);
        
        var urls = [], i, layers = this.wMSGetFeatureInfo.findLayers();
        for (i=0 ; i<layers.length ; i++) {
            var layer = layers[i];
            if (layer.interrogeable===true) {
                var layerUrl = OpenLayers.Util.isArray(layer.url) ? layer.url[0] : layer.url;
                // Si l'URL est décrite partiellement, on la traite en absolue par rapport à l'origine du serveur actuel
                var internal = layerUrl.indexOf('http')<0; 
                if (internal) {
                    var dl=document.location;
                    layerUrl = dl.protocol+'//'+dl.host+(layerUrl.charAt(0)=='/'?'':'/')+layerUrl;
                }
                var requOpts = this.wMSGetFeatureInfo.buildWMSOptions(
                    layerUrl,
                    [layer],
                    evt.xy,
                    layer.params.FORMAT);
                var url = OpenLayers.Util.urlAppend(requOpts.url, OpenLayers.Util.getParameterString(requOpts.params || {}));
                new Ext.data.Connection({ignorealert:true}).request({
                    url: (internal?url:OpenLayers.ProxyHost+encodeURIComponent(url)),
                    callback: Ext.bind(this.manageResponse, this, [layer.name, this.idClic], true), scope: this
                });
                this.requestsCounter++;
            }
        }
        
        // On ajoute les info sur les vecteurs :
        // Features
        layers = this.findVectorLayers();
        for(i=0; i<layers.length; i++) {
            var features = this.findFeaturesFromEvent(layers[i], evt);
            var j;
            for (j=0 ; j<features.length ; j++) {
                var feature = features[j];
                if (feature) {
                    this.prepareFeatureInfo(feature);
                }
            }
        }
        
        if (this.requestsCounter<1) {
            // Aucune requête ne sera exécutée. On appelle directement la méthode de rendu
            this.showResult();
        }
    },
    
    // ----------
    // Gestion des couches vectorielles
    // ----------
    
    prepareFeatureInfo: function(feature) {
        // Préparation des données (en attendant le foreach d'Ext 4.1.2)
        var data = feature.data; var keys=[], values=[], key;
        for(key in data) {
            keys.push(key);
            values.push(data[key]);
        }
        var preparedData = {k:keys, v:values, raw:data, layer:feature.layer.name};
        
        var tpl = this.renderTpls[feature.layer.code];
        if (!tpl) {
            // Pas de template spécifique, on utilise un template générique : clé : valeur
            tpl = this.genTpl;
        }
        this.contentToShow.push({layer:preparedData.layer, html:tpl.apply(preparedData)});
    },
    
    findFeaturesFromEvent: function(layer, evt) {
//        // Fonctionnement 1 : une feature par couche (géré par le renderer)
//        var returned = [];
//        var feature = layers[i].getFeatureFromEvent(evt);
//        if (feature) {
//            returned.push(feature);
//            return returned;
//        }
        
        // Fonctionnement 2 : toutes les features que le point intersecte
        var returned = [];
        if (!layer.features) {
            return returned;
        }
        var lonlat = this.map.getLonLatFromPixel(evt.xy);
        var point = new OpenLayers.Geometry.Point(lonlat.lon, lonlat.lat);
        var i;
        for (i=0 ; i<layer.features.length ; i++) {
            var feature = layer.features[i];
            var marginInMapUnit = (layer.infoMarginInPixels||this.defaultMarginInPixels)*this.map.getResolution();
            if (point.intersects(feature.geometry)
                    || point.distanceTo(feature.geometry)<marginInMapUnit
                ) {
                returned.push(feature);
            }
        }
        return returned;
    },
    
    findVectorLayers: function() {
        var candidates = this.layers || this.map.layers;
        var layers = [];
        var layer, url, i;
        for(i = candidates.length - 1; i >= 0; --i) {
            layer = candidates[i];
            if(layer instanceof OpenLayers.Layer.Vector
                && layer.interrogeable===true
                && (!this.queryVisible || layer.getVisibility())) {
                layers.push(layer);
            }
        }
        return layers;
    },
    
    // ----------
    // Gestion des couches WMS
    // ----------
    manageResponse: function(opts, success, response, layerName, idClic) {
        if (idClic != this.idClic) {
            // Certainement une requête qui a mis du temps à arriver car
            // elle ne fait pas partie du même lot. On ne la traite pas.
            return;
        }
        if (success && response.responseText && response.responseText.length>0) {
            this.contentToShow.push({layer: layerName, html: response.responseText});
        }
        if (--this.requestsCounter<=0) {
            // Dernière requête arrivée
            this.showResult();
        }
    },
    
    // ----------
    // Affichage des résultats
    // ----------
    showResult: function() {
        var layerName, html, items = [], i;
        for (i=0 ; i<this.contentToShow.length ; i++) {
            html = this.contentToShow[i].html;
            if (html.indexOf('<body></body>')<0) {
                // On ne retient pas les corps vides
                layerName = this.contentToShow[i].layer;
                items.push({
                    title: layerName,
                    html: html
                });
            }
        }
        if (items.length>0) {
            Ext.create('Ext.window.Window', {
                title: 'Informations',
                modal: true,
                constrain: true,
                width: 500,
                items: Ext.create('Ext.panel.Panel', {
                    defaults: {
                        bodyStyle: 'padding:15px;background-color:#fff;'
                    },
                    layout: {
                        type: 'accordion',
                        titleCollapse: true,
                        animate: true
                    },
                    items: items
                })
            }).show();
        } else {
            Sdis.Remocra.util.Msg.msg('Informations', 'Aucune information à afficher', 5);
        }

        // Stop working
        this.startStopWorking(false);
        
        if (this.autoDeactivate) {
            this.deactivate();
        }
    },
    
    CLASS_NAME: "Sdis.Remocra.widget.map.controls.GetFeaturesInfo"
});
