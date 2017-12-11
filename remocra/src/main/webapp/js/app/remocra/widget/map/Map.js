Ext.ns('Sdis.Remocra.widget.map');

Ext.require('Sdis.Remocra.store.Voie');
Ext.require('Sdis.Remocra.widget.map.controls.Measure');
Ext.require('Sdis.Remocra.widget.map.controls.GetFeaturesInfo');
Ext.require('Sdis.Remocra.widget.WidgetFactory');

Ext.require('Sdis.Remocra.widget.map.LegendTemplate');

// Sdis.Remocra.widget.map.EPSG3857 = new OpenLayers.Projection('EPSG:3857');
Sdis.Remocra.widget.map.EPSG900913 = new OpenLayers.Projection('EPSG:900913'); // Google
// Mercator
Sdis.Remocra.widget.map.EPSG4326 = new OpenLayers.Projection('EPSG:4326'); // Projection
// WGS84

Ext.define('Sdis.Remocra.widget.map.Map', {
    extend: 'Ext.container.Container',
    alias: 'widget.crMap',

    // Items qui doivent figurer juste avant le bouton d'affichage de la barre
    // d'édition
    moreItems: [],
    // Items de la barre d'édition
    editItems: [],
    // URL de la légende (qui conditionne le chargement des couches)
    legendUrl: null,

    // Doit-on cacher la combo de zoom sur tournée (true par défaut)
    hideZoomTournee: true,

    workingLayer: null,

    mapTpl: Ext.create('Ext.XTemplate', '<div class="maptbar1"><!-- --></div>', '<div class="maptbar2"><!-- --></div>', '<div class="map"><!-- --></div>',
            '<div class="maplegend"><!-- --></div>', '<div class="mapinfo"><!-- --></div>'),

    legendTpl: null,

    initComponent: function() {
        this.addEvents('layersadded');

        Ext.apply(this, {
            html: ''
        });

        this.on('afterrender', Ext.bind(this.renderContent, this), this);

        this.callParent(arguments);

        // Affichage / masquage du bandeau
        var banniere = Ext.get('banniere');
        var banDisplay = banniere.getStyle('display');
        banniere.setStyle('display', 'none');
        Ext.get('pageTop').toggleDisplay(undefined, true);
        this.on('destroy', function() {
            Ext.get('pageTop').toggleDisplay(true, {
                callback: Ext.bind(function() {
                    this.setStyle("display", banDisplay);
                }, banniere)
            });
        });
    },

    renderContent: function() {
        // Template global
        this.mapTpl.append(this.getEl(), {}, false);

        this.renderMap();
        // this.renderLegend(); Réalisé plus tard lors de la configuration des
        // couches
        this.renderTbar1();
        this.renderTbar2();

        // On ajoute à la hauteur de la carte, une hauteur suffisante pour
        // contenir les barres d'outils et de mesure
        this.setHeight(this.getInitHeight());

        // On doit gérer la hauteur du conteneur lorsque la barre est affichée /
        // masquée (éviter décalage curseur)
        // TODO trouver une solution systématique (rendu complètement terminé et
        // carte complètement chargé OU BIEN sur les deux ?)
        Ext.Function.defer(this.map.updateSize, 500, this.map);

        // Gestion des activations / désactivations des boutons par rapport aux
        // contrôles
        // TODO vérifier que tout est bien là et si nécessaire déplacer cet
        // appel
        this.manageActivationsToggles();
    },

    getInitHeight : function() {
        return this.map.getSize().h + 100;
    },

    manageActivationsToggles: function() {
        var infoBtn = this.maptbar1.getComponent('info');
        if (infoBtn) {
            this.manageActivationsTogglesBtnCtrl(infoBtn, 'infoCtrl');
        }
        var measureBtn = this.maptbar1.getComponent('measure');
        measureBtn.on('click', Ext.bind(function(btn, e, eOpts) {
            if (!btn.pressed) {
                // On désactive les deux contrôles et on cache le menu
                this.activateSpecificControl('measureCtrlLength', false);
                this.activateSpecificControl('measureCtrlArea', false);
                btn.hideMenu();
            }
        }, this));

        this.manageActivationsTogglesBtnCtrl(measureBtn, 'measureCtrlLength');
        this.manageActivationsTogglesBtnCtrl(measureBtn, 'measureCtrlArea');
    },
    manageActivationsTogglesBtnCtrl: function(btn, ctrlKey) {
        if (!btn) {
            return;
        }
        var ctrl = this.getSpecificControl(ctrlKey);
        if (!ctrl) {
            return;
        }
        ctrl.events.on({
            "activate": Ext.bind(this.activateBtn, btn, [true]),
            "deactivate": Ext.bind(this.activateBtn, btn, [false])
        });
    },
    activateBtn: function(activate) {
        this.toggle(activate);
    },

    renderMap: function() {
        // Carte
        var mapNode = Ext.DomQuery.selectNode("div.map", this.getEl().dom);

        // On prend toute la place qu'il reste
        /*
         * var width = Ext.get('viewport').getSize().width;
         * Ext.get(mapNode).setStyle('width', width-(30+250)+'px');
         */

        this.map = new OpenLayers.Map(mapNode, {
            controls: [],
            allOverlays: false,
            projection: 'EPSG:2154',
            theme : false // Evite style.css quand module serveur PageSpeed
        });

        // Controles OpenLayers
        this.map.addControl(new OpenLayers.Control.Navigation());

        var scaleLine = new OpenLayers.Control.ScaleLine({
            bottomOutUnits: '',
            geodesic: true,
            maxWidth: 200
        });
        this.map.addControl(scaleLine);

        // Position en °'" et décimales
        var mousePosition = new OpenLayers.Control.MousePosition({
            displayProjection: Sdis.Remocra.widget.map.EPSG4326, // En WGS84
            prefix: 'x : ',
            separator: ', y : ',
            formatOutput: function(lonLat) {
                var digits = parseInt(this.numDigits, 10);
                var newHtml =
                    this.prefix +
                    Sdis.Remocra.util.Util.getFormattedCoord('x', lonLat.lon, COORDONNEES_FORMAT_AFFICHAGE, digits) +
                    this.separator + 
                    Sdis.Remocra.util.Util.getFormattedCoord('y', lonLat.lat, COORDONNEES_FORMAT_AFFICHAGE, digits) +
                    this.suffix;
                return newHtml;
            }
        });
        this.map.addControl(mousePosition);

        this.map.addControl(new OpenLayers.Control.NavigationHistory());

        this.map.addControl(new OpenLayers.Control.Attribution());

        // Couche de travail
        this.workingLayer = this.createWorkingLayer();

        // Contrôles spécifiques
        this.specificControls = this.createSpecificControls();
        var key;
        for (key in this.specificControls) {
            this.map.addControl(this.specificControls[key]);
        }
        // Charge la légende et les couches
        this.loadMapConfig();
    },

    renderLegend: function(legendData) {
        // Légende
        var legendNode = Ext.DomQuery.selectNode("div.maplegend", this.getEl().dom);
        if (this.legendTpl == null) {
            this.legendTpl = Ext.create('Sdis.Remocra.widget.map.LegendTemplate');
        }
        this.legendTpl.overwrite(legendNode, {
            id: this.id,
            legendData: legendData
        }, false);
        Ext.get(legendNode).setVisibilityMode(Ext.Element.DISPLAY);
    },

    renderTbar1: function() {
        var tbar = Ext.DomQuery.selectNode("div.maptbar1", this.getEl().dom);
        
        var items = [
            { tooltip: 'Déplacer la carte', text: '<span>Pan</span>', // On retire car outil complètement contrôlé par l'utilisateur toggleGroup : 'excl-ctrl1',
                cls: 'pan', iconCls: 'panIcon', enableToggle: true, pressed: true, handler: Ext.bind(this.pan, this) },
            ' ',
            { tooltip: 'Zoomer en avant', text: '<span>Zoom+</span>',
                cls: 'zoom-plus', iconCls: 'zoom-plusIcon', handler: Ext.bind(this.zoomIn, this) },
            { tooltip: 'Zoomer en arrière', text: '<span>Zoom-</span>',
                cls: 'zoom-minus', iconCls: 'zoom-minusIcon', handler: Ext.bind(this.zoomOut, this) },
            { tooltip: 'Rétablir la vue précédente', text: '<span>Zoom p</span>',
                cls: 'zoom-prev', iconCls: 'zoom-prevIcon', handler: Ext.bind(this.zoomPrev, this) },
            { tooltip: 'Rétablir la vue suivante', text: '<span>Zoom n</span>',
                cls: 'zoom-next', iconCls: 'zoom-nextIcon', handler: Ext.bind(this.zoomNext, this) },
            // Zoom sur commune
            Sdis.Remocra.widget.WidgetFactory.createCommuneCombo({
                itemId : 'communeCombo',
                emptyText: 'Zoomer sur la commune...', cls: 'zoom-commune', width: 170,
                listeners: {
                    'select': this.onZoomToCommune,
                    scope: this
                }
            }),
            // Zoom sur voie
            Ext.widget('combo', {
                store: Ext.create('Sdis.Remocra.store.Voie'),
                queryMode: 'remote',
                displayField: 'nom',
                valueField: 'id',
                triggerAction: "all",
                hideTrigger: true,
                typeAhead: true,
                minChars: 3,
                queryCaching: false, // Changement commune avec saisie identique : requête à rejouer quand-même
                
                allowBlank: true,
                itemId: 'voieCombo',
                emptyText: 'Zoomer sur la voie...', cls: 'zoom-voie', width: 170,
                listeners: {
                    'select': this.onZoomToVoie,
                    'beforequery': this.onBeforeVoieQuery,
                    scope: this
                }
            }),
            // Zoom sur tournée
            {
                xtype: 'combo',
                displayField: 'id',
                valueField: 'id',
                forceSelection: true,
                minChars: 1,
                mode: 'remote',
                hideTrigger: true,
                typeAhead: true,
                queryCaching: false,
                emptyText: 'Zoomer sur la tournée...',
                width: 170,
                store: {
                    model: 'Sdis.Remocra.model.Tournee'
                },
                listeners : {
                    'select': this.onZoomToTournee,
                     scope: this
                },
                hidden : !Sdis.Remocra.Rights.getRight('TOURNEE').Read || this.hideZoomTournee
            },
            ' ',
            {
                xtype: 'button',  tooltip: 'Mesurer une distance ou une surface', text : '<span>Mesurer</span>', cls: 'measure', iconCls: 'measureIcon',
                itemId: 'measure',
                enableToggle: true, pressed: false, toggleGroup: 'excl-ctrl1', allowDepress: true,
                menu: new Ext.menu.Menu({
                    items: [
                        { text: '<span>Distance</span>', toggleGroup: 'excl-ctrl1', checked: false, group: 'measure',
                            checkedCls: 'measure-length-checked', uncheckedCls: '', // Pour éviter d'avoir une case à cocher
                            cls: 'measure-length', iconCls: 'measure-lengthIcon', handler: Ext.bind(this.measure, this, [false]) },
                        { text: '<span>Surface</span>', toggleGroup: 'excl-ctrl1', checked: false, group: 'measure',
                            checkedCls: '', uncheckedCls: '', // Pour éviter d'avoir une case à cocher
                            cls: 'measure-area', iconCls: 'measure-areaIcon', handler: Ext.bind(this.measure, this, [true]) }
                    ]
                })
            },
            ' ',
            { tooltip: 'Obtenir des informations sur un point de la carte', text: '<span>Info</span>',
                toggleGroup: 'excl-ctrl1',
                itemId: 'info',
                cls: 'info', iconCls: 'infoIcon', handler: Ext.bind(this.info, this, [true]) },
            ' '
        ];
        if (this.hasMoreTools()) {
            var i;
            for (i = 0; i < this.moreItems.length; i++) {
                items.push(this.moreItems[i]);
            }
            items.push(' ');
        }
        if (this.hasEditTools()) {
            items.push({
                tooltip: 'Activer les outils d\'édition',
                text: '<span>Editer</span>',
                cls: 'edit',
                iconCls: 'editIcon',
                itemId: 'edit',
                enableToggle: true,
                pressed: false,
                handler: Ext.bind(this.edit, this)
            });
        }
        items.push('->');

        this.maptbar1 = Ext.create('Ext.toolbar.Toolbar', {
            style: 'background: none;border: none;',
            renderTo: tbar,
            cls: 'maptbar maptbar1',
            items: items
        });
        this.add(this.maptbar1);

    },
    renderTbar2: function() {
        if (!this.hasEditTools()) {
            return;
        }
        var tbar = Ext.DomQuery.selectNode("div.maptbar2", this.getEl().dom);
        this.maptbar2 = Ext.create('Ext.toolbar.Toolbar', {
            style: 'background: none;border: none;',
            hidden: true,
            // On doit gérer la hauteur du conteneur lorsque la barre est
            // affichée / masquée (éviter décalage curseur)
            listeners: {
                show: function(tbar, opts) {
                    // Hauteur du conteneur
                    // Sauvegarde de la hauteur de la toolbar (avec ses marges) pour la restituer sur "hide"
                    this.tbarHeight = tbar.getHeight() + 5;
                    var elt = this.ownerCt.getEl();
                    elt.setHeight(elt.getHeight() + this.tbarHeight);

                    // Hauteur de la carte
                    var mapElt = Ext.get(this.map.div);
                    mapElt.setHeight(mapElt.getHeight() - this.tbarHeight);
                    this.map.updateSize();

                },
                hide: function(tbar, opts) {
                    // Hauteur du conteneur
                    var elt = this.ownerCt.getEl();
                    elt.setHeight(elt.getHeight() - this.tbarHeight);

                    // Hauteur de la carte
                    var mapElt = Ext.get(this.map.div);
                    mapElt.setHeight(mapElt.getHeight() + this.tbarHeight);
                    this.map.updateSize();

                },
                scope: this
            },
            renderTo: tbar,
            items: this.editItems,
            cls: 'maptbar maptbar2'

        });
        this.add(this.maptbar2);
    },

    hasMoreTools: function() {
        return Ext.isArray(this.moreItems) && this.moreItems.length > 0;
    },
    hasEditTools: function() {
        return Ext.isArray(this.editItems) && this.editItems.length > 0;
    },

    zoomToBestExtent: function() {
        // this.map.zoomToMaxExtent();

        // A peu près sur le département du Var : EPSG:900913;523593.64368054,5303506.7698006,849521.13224316,5486955.6376594
        var sridBounds = REMOCRA_INIT_BOUNDS.split(";");
        var srid = sridBounds[0];
        var bounds = sridBounds[1];
        var initExtent = new OpenLayers.Bounds(bounds.split(',')).transform(srid, this.map.getProjection());
        this.map.zoomToExtent(initExtent, true);
    },

    centerToPoint: function(x, y, srid) {
        if (this.map.layers.length <= 1) {
            // Les cartes ne sont pas encore chargé, on décale à plus tard
            this.on('layersadded', function() {
                this.centerToPoint(x, y);
            }, this, {
                single: true
            });
        } else {
            if (!Ext.isDefined(srid) || Ext.isEmpty(srid)) {
                srid = "2154";
            }
            srid = "epsg:" + srid;
            var lonlat = new OpenLayers.LonLat(x, y);
            lonlat.transform(srid, this.map.getProjection());
            this.map.setCenter(lonlat, this.map.baseLayer.resolutions.length - 1, false, true);
        }
    },

    zoomToBounds: function(bounds) {
        if (this.map.layers.length <= 1) {
            // Les cartes ne sont pas encore chargé, on décale à plus tard
            this.on('layersadded', function() {
                this.zoomToBounds(bounds);
            }, this, {
                single: true
            });
        } else {
            bounds.transform("epsg:2154", this.map.getProjection());
            this.map.zoomToExtent(bounds, true);
        }
    },

    pan: function() {
        var navCtrl = this.map.getControlsByClass('OpenLayers.Control.Navigation')[0];
        if (navCtrl.active === true) {
            navCtrl.deactivate();
        } else {
            navCtrl.activate();
        }
    },

    zoomIn: function() {
        this.map.zoomIn();
    },
    zoomOut: function() {
        this.map.zoomOut();
    },
    zoomPrev: function() {
        map = this.map;
        this.map.getControlsByClass('OpenLayers.Control.NavigationHistory')[0].previous.trigger();
    },
    zoomNext: function() {
        this.map.getControlsByClass('OpenLayers.Control.NavigationHistory')[0].next.trigger();
    },

    measure: function(isArea) {
        var measureCtrl = this.map.getControl(isArea ? 'measureCtrlArea' : 'measureCtrlLength');
        if (measureCtrl.active === true) {
            measureCtrl.deactivate();
        } else {
            this.activateSpecificControl('aucun');
            measureCtrl.activate();
        }
    },

    info: function() {
        this.activateSpecificControl('aucun');
        var infoCtrl = this.getSpecificControl('infoCtrl');
        if (infoCtrl.active === true) {
            infoCtrl.deactivate();
        } else {
            infoCtrl.activate();
        }
    },

    edit: function() {
        if (this.maptbar2.isVisible()) {
            // désactiver tous les outils de la maptbar2 :
            if (this.maptbar2.items) {
                this.maptbar2.items.each(function(cmp) {
                    if (cmp.pressed) {
                        cmp.toggle(false);
                    }
                });
            }
        }
        this.maptbar2.setVisible(!this.maptbar2.isVisible());
    },

    workingLayerStyleMap: function() {
        var sm = new OpenLayers.Style({
            fillColor: '#64C0FF',
            fillOpacity: 0.8,
            strokeColor: '#64C0FF',
            strokeOpacity: 1,
            strokeWidth: 3,
            pointRadius: 5
        });
        var selectStyle = new OpenLayers.Style({
            fillColor: 'red',
            fillOpacity: 0.8,
            strokeColor: 'red',
            strokeOpacity: 1,
            strokeWidth: 3,
            pointRadius: 5
        });
        return new OpenLayers.StyleMap({
            "default": sm,
            "select": selectStyle,
            "temporary": sm
        });
    },

    loadMapConfig: function() {
        if (this.legendUrl == null) {
            if (this.legendData) {
                this.addLayersFromLayerConfig(this.legendData);
            }
            return;
        }
        Ext.Ajax.request({
            url: this.legendUrl,
            method: 'GET',
            scope: this,
            callback: function(options, success, response) {
                if (success !== true) {
                    Ext.Msg.alert('Carte', 'Un problème est survenu lors du chargement de la configuration.');
                    return;
                }
                // On renseigne les données des couches
                var legendData = Ext.decode(response.responseText);
                this.addLayersFromLayerConfig(legendData);
            }
        });
    },

    addLayersFromLayerConfig: function(legendData) {
        // Toujours une baseLayer => fake au démarrage
        var baseLayer = this.createSpecificLayer('fakebaselayer');
        this.map.addLayer(baseLayer);
        this.map.setBaseLayer(baseLayer);

        var iGrp, iLay, legendShow = Ext.clone(legendData);
        // Chaque groupe (à l'envers)
        for (iGrp = legendData.items.length; iGrp > 0; iGrp--) {
            grp = legendData.items[iGrp - 1];
            // Chaque couche (à l'envers)
            for(iLay=grp.items.length ; iLay>0 ; iLay--) {
                var layerDef = grp.items[iLay-1];
                if (layerDef.name == null) {
                    layerDef.name = layerDef.id;
                }
                layerDef.baseLayer = !!layerDef.base_layer;
                layerDef.isBaseLayer = layerDef.baseLayer;
                layerDef.interrogeable = !!layerDef.interrogeable;
                Ext.applyIf(layerDef, {
                    items: []
                });

                // La couche
                var layer = null;
                try {
                    switch (layerDef.type) {
                    case 'wms':
                        layer = this.createWMSLayer(layerDef);
                        break;
                    case 'ign':
                        layer = this.createIGNLayer(layerDef);
                        break;
                    case 'specific':
                        Ext.applyIf(layerDef, {
                            stategy: "fixed"
                        });
                        layer = this.createSpecificLayer(layerDef);
                        break;
                    default:
                        break;
                    }
                } catch (e) {
                    Ext.Msg.alert('Carte', e);
                }

                if (layer) {
                    this.map.addLayer(layer);
                    if (layer.isBaseLayer) {
                        this.map.setBaseLayer(layer);
                    }
                } else {
                    Ext.Array.erase(legendShow.items[iGrp - 1].items, iLay - 1, 1);
                }
            }
        }

        for (iGrp = legendShow.items.length; iGrp > 0; iGrp--) {
            if (Ext.isEmpty(legendShow.items[iGrp - 1].items)) {
                Ext.Array.erase(legendShow.items, iGrp - 1, 1);
            }
        }
        
        // Ajout de la couche de travail
        if (this.workingLayer) {
            this.map.addLayer(this.workingLayer);
        }

        // On met à jour la légende
        this.renderLegend(legendShow);

        // On s'assure qu'il y a bien une base layer
        this.assumeBaseLayer();

        this.zoomToBestExtent();
        this.fireEvent('layersadded', this);
    },

    // Affectation d'une base layer si nécessaire. Normalement pas nécessaire car toujours
    // une fakebaselayer (permière couche ajoutée avant chargement de la légende)
    assumeBaseLayer: function() {
        if (!this.map.baseLayer) {
            // On ajoute une base layer non visible
            var baseLayer = this.createSpecificLayer('fakebaselayer');
            this.map.addLayer(baseLayer);
            this.map.setBaseLayer(baseLayer);
        }
    },

    createWorkingLayer: function() {
        return new OpenLayers.Layer.Vector("Couche de travail", {
            styleMap: this.workingLayerStyleMap(),
            displayInLayerSwitcher: false,
            projection: 'EPSG:2154'
        });
    },

    createWMSLayer: function(layerDef) {
        // Pour les tests locaux (TODO : retirer ?)
        if (window.location.hostname == 'localhost') {
            var baseUrlRE = new RegExp('^/geoserver', 'gi');
            layerDef.url = layerDef.url.replace(baseUrlRE, 'http://sdis83-remocra.lan.priv.atolcd.com/geoserver');
        }

        return new OpenLayers.Layer.WMS(layerDef.name, layerDef.url, {
            layers: layerDef.layers,
            SLD: layerDef.sld,
            transparent: true
        }, {
            code: layerDef.id,
            singleTile: true,
            transitionEffect: 'resize',
            isBaseLayer: layerDef.baseLayer,
            visibility: layerDef.visibility,
            opacity: layerDef.opacity,
            interrogeable: layerDef.interrogeable,
            projection: layerDef.projection
        });
    },    

    createIGNLayer: function(layerDef) {
        var options = {
            isBaseLayer: layerDef.baseLayer,
            code: layerDef.id,
            name: layerDef.name,
            layer: layerDef.layers,
            visibility: layerDef.visibility,
            opacity: layerDef.opacity,
            projection: 'EPSG:900913',
            url: 'https://gpp3-wxs.ign.fr/' + Sdis.Remocra.util.Util.getIgnKey() + '/wmts',
            matrixSet: 'PM',
            style: 'normal',
            numZoomLevels: 19,
            attribution: '<a href="http://www.geoportail.fr/" target="_blank">' + '<img src="' + BASE_URL + '/../images/remocra/cartes/logo_gp.gif"></a>'
                    + '<a href="http://www.geoportail.gouv.fr/depot/api/cgu/licAPI_CGUF.pdf" alt="TOS" title="TOS" target="_blank">Condifions générales d\'utilisation</a>'
        };
        if (layerDef.format) {
            options.format = layerDef.format;
        }
        return new OpenLayers.Layer.WMTS(options);
    },

    // A implémenter dans des cartes spécifiques si nécessaire (composants qui
    // étendent Sdis.Remocra.widget.map.Map)
    createSpecificLayer: function(layerDef) {
        if (layerDef == 'fakebaselayer') {
            // Fausse couche : photos de l'IGN, non visible
            return this.createIGNLayer({
                baseLayer : true,
                id : layerDef,
                name : layerDef,
                layers : 'ORTHOIMAGERY.ORTHOPHOTOS',
                visibility : false,
                opacity : 0.0,
                projection: 'EPSG:900913',
                url: 'https://gpp3-wxs.ign.fr/' + Sdis.Remocra.util.Util.getIgnKey() + '/wmts',
                matrixSet: 'PM',
                style: 'normal',
                numZoomLevels: 20
            });
        }
        throw 'La couche spécifique \'' + layerDef.id + '\' est inconnue pour cette carte.';
    },

    createStrategies: function(config) {
        var strategies = [];
        if (Ext.isString(config)) {
            switch (config) {
            case 'fixed':
                strategies.push(new OpenLayers.Strategy.Fixed());
                break;
            case 'bbox':
                strategies.push(new OpenLayers.Strategy.BBOX());
                break;
            }
        }
        return strategies;
    },

    createGeoJsonLayer: function(layerDef) {

        layerDef.code = layerDef.id;
        delete layerDef.id;

        layerDef.strategies = this.createStrategies(layerDef.stategy);
        delete layerDef.stategy;

        layerDef.protocol = new OpenLayers.Protocol.HTTP({
            url: layerDef.url,
            srsInBBOX: true,
            format: new OpenLayers.Format.GeoJSON({
                ignoreExtraDims: true
            })
        });
        delete layerDef.url;

        layerDef.rendererOptions = {
            zIndexing: true
        };

        return new OpenLayers.Layer.Vector(layerDef.libelle, layerDef);
    },

    /**
     * Crée tous les contrôles dont on contrôle l'activation / désactivation
     */
    createSpecificControls: function() {
        var mapinfo = Ext.DomQuery.selectNode("div.mapinfo", this.getEl().dom);
        return {
            measureCtrlLength: new Sdis.Remocra.widget.map.controls.Measure({
                infoDiv: mapinfo,
                isArea: false,
                id: 'measureCtrlLength',
                autoDeactivate: false
            }),
            measureCtrlArea: new Sdis.Remocra.widget.map.controls.Measure({
                infoDiv: mapinfo,
                isArea: true,
                id: 'measureCtrlArea',
                autoDeactivate: false
            }),
            infoCtrl: new Sdis.Remocra.widget.map.controls.GetFeaturesInfo({
                renderTpls: this.getSpecificInfoFormaters(),
                autoDeactivate: true
            })
        };
    },
    getSpecificControl: function(controlKey) {
        return this.specificControls[controlKey];
    },
    /**
     * @parameter controlKey la clé du contrôle à activer
     * @return true si un contrôle a été activé (ou désactivé selon la demande)
     */
    activateSpecificControl: function(controlKey, activate) {
        if (activate == undefined) {
            activate = true;
        }
        var key, returned = false;
        for (key in this.specificControls) {
            var control = this.specificControls[key];
            if (controlKey == key) {
                if (activate) {
                    control.activate();
                } else {
                    control.deactivate();
                }
                returned = true;
            } else {
                control.deactivate();
            }
        }
        return returned;
    },

    /**
     * Ajout tardif d'un contrôle
     * 
     * @param key
     * @param ctrl
     */
    addSpecificControlLate: function(key, ctrl) {
        this.specificControls[key] = ctrl;
        this.map.addControl(ctrl);
    },

    getLayerByCode: function(code) {
        var i;
        for (i = 0; i < this.map.layers.length; i++) {
            var layer = this.map.layers[i];
            if (layer.code == code) {
                return layer;
            }
        }
        return null;
    },

    getSpecificInfoFormaters: function() {
        // layer.code -> formater
        return {};
    },

    getLayerVisibility: function(layerCode) {
        return this.getLayerByCode(layerCode).getVisibility();
    },
    setLayerVisibility: function(layerCode, visibility) {
        this.getLayerByCode(layerCode).setVisibility(visibility);
    },
    getLayerOpacity: function(layerCode) {
        return this.getLayerByCode(layerCode).getOpacity();
    },
    setLayerOpacity: function(layerCode, opacity) {
        this.getLayerByCode(layerCode).setOpacity(opacity);
    },
    
    /**
     * Zoom sur la commune ou la voie
     */
    zoomToFirstRecord: function(records) {
        if (records.length>0) {
            var record = records[0];
            var wkt = record.get('geometrie');
            var feature = new OpenLayers.Format.WKT().read(wkt);
            var bounds = feature.geometry.getBounds();
            // On reprojette
            var newBounds = bounds.transform('EPSG:2154', this.map.getProjection());
            this.map.zoomToExtent(newBounds, true);
        }
    },
    onZoomToCommune: function(combo, records, opts) {
        // Zoom sur la commune
        this.zoomToFirstRecord(records);
        
        // Réinitialisation de la voie si la commune est différente
        var voieCombo = this.maptbar1.getComponent('voieCombo');
        var voie = voieCombo.getValueModel();
        if (voie && voie.getCommune().get('id') != combo.getValue()) {
            voieCombo.setValue(null);
        }
    },
    onZoomToVoie: function(combo, records, opts) {
        // Zoom sur la voie
        this.zoomToFirstRecord(records);
    },
    onZoomToTournee: function(combo, records, opts) {
        var tournee = records[0];
        var geom = tournee.get('geometrie');
        if (geom && geom.length>0) {
            this.zoomToBounds(Sdis.Remocra.util.Util.getBounds(geom));
        } else {
            Sdis.Remocra.util.Msg.msg('Carte', 'La tournée ne contient aucun point d\'eau.', 3);
        }
    },
    
    /**
     * Avant la requête sur la voie, on filtre selon la commune (ou pas)
     */
    onBeforeVoieQuery: function(queryEvent, eOpts) {
        var voieStore = queryEvent.combo.store;
        var communeCombo = this.maptbar1.getComponent('communeCombo');
        var communeId = communeCombo.getValue();
        
        voieStore.clearFilter(true); // Pas d'événement
        if (communeId) {
            voieStore.filters.add({ property: "communeId", value: communeId });
        }
    },
    
    getCurrentSrid: function() {
        return this.map.getProjection().split(':')[1];
    }
});