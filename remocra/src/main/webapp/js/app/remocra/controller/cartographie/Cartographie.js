Ext.require('Sdis.Remocra.features.cartographie.Config');
Ext.require('Sdis.Remocra.features.cartographie.Map');
Ext.require('Sdis.Remocra.widget.PrintWindow');
Ext.require('Sdis.Remocra.features.cartographie.StyleWindow');
Ext.require('Sdis.Remocra.features.cartographie.StyleHolder');

Ext.define('Sdis.Remocra.controller.cartographie.Cartographie', {
    extend : 'Ext.app.Controller',

    stores : [],

    refs : [ {
        ref : 'tabPanel',
        selector : 'crCartographie'
    }, {
        ref : 'configTab',
        selector : 'crCartographieConfig'
    }, {
        ref : 'map',
        selector : 'crCartographieMap'
    } ],

    init : function() {
        this.control({
            // Cartographie
            '.crCartographieConfig #open' : {
                click : this.openMap
            },

            // Map
            'crCartographieMap' : {
                layersadded: this.initControlMap
            },
            'crCartographieMap #selectBtn': {
                toggle: function(button, pressed) {
                    this.getMap().activateSpecificControl('selectPolygon', pressed);
                }
            },
            'crCartographieMap #addPointBtn' : {
                toggle : function(button, pressed) {
                    this.getMap().activateSpecificControl('drawPoint', pressed);
                }
            },
            'crCartographieMap #addLineBtn' : {
                toggle : function(button, pressed) {
                    this.getMap().activateSpecificControl('drawLine', pressed);
                }
            },
            'crCartographieMap #addPolygonBtn' : {
                toggle : function(button, pressed) {
                    this.getMap().activateSpecificControl('drawPolygon', pressed);
                }
            },
            'crCartographieMap #editInfoBtn' : {
                click : function(button, e, eOpts) {
                    this.editInfo(this.getSelectedFeatures());
                }
            },
            'crCartographieMap #editGeomBtn' : {
                toggle : function(button, pressed) {
                    this.getMap().activateSpecificControl('modifyGeom', pressed);
                }
            },
            'crCartographieMap #editGeomAdvBtn' : {
                toggle : function(button, pressed) {
                    this.getMap().activateSpecificControl('modifyAdvGeom', pressed);
                }
            },
            'crCartographieMap #deleteBtn' : {
                click: this.deleteEltFromMap
            }
        });
    },

    /***************************************************************************
     * Config
     **************************************************************************/
    openMap : function(btn, evt, eOpts) {
        var config = this.getConfigTab();
        var titre = config.queryById('titre').getValue();
        var format = config.queryById('format').getValue();
        var orientation = config.queryById('orientation').getValue();
        var title = 'Carte : ' + titre + ' (' + (format == 'a4' ? 'A4' : 'A3')
                + ' / ' + (orientation == 'landscape' ? 'Paysage' : 'Portrait')
                + ')';
        window.scrollTo(0, 0);
        // URL de base : URL courante avant le # à laquelle on retire le / s'il
        // est présent (pour FF)
        var baseUrl = window.location.href.split('#')[0];
        if (baseUrl.charAt(baseUrl.length-1) == "/") {
            baseUrl = baseUrl.substring(0, baseUrl.length - 1);
        }
        Sdis.Remocra.widget.PrintWindow.showFromUrl(baseUrl + '#'
                + config.getUrl(), title, null, {
            titleText : 'Aperçu avant impression. ' + title
        });
    },

    /***************************************************************************
     * Map
     **************************************************************************/
    addEventToControl: function(controlName, cfg) {
        var control = this.getMap().getSpecificControl(controlName);
        if (control) {
            Ext.applyIf(cfg, {
                scope: this
            });
            control.events.on(cfg);
        }
    },

    initControlMap: function() {
        // Sélection
        this.getMap().workingLayer.events.on({
            'featureadded': this.onFeatureAdded,
            'featureselected': this.enableDisableButtons,
            'featureunselected': this.enableDisableButtons,
            scope: this
        });

        this.addEventToControl('modifyGeom', {
            'activate': this.onModifyGeomActivate
        });
        this.addEventToControl('modifyAdvGeom', {
            'activate': this.onModifyGeomAdvActivate,
            'deactivate': function(evt) {
                // Bug ModifyFeature : les poignées ne sont pas retirées dans le
                // cas d'une suppression de feature par un outil externe
                var ctrl = evt.object;
                var features = [];
                if (ctrl.dragHandle) {
                    features.push(ctrl.dragHandle);
                }
                if (ctrl.radiusHandle) {
                    features.push(ctrl.radiusHandle);
                }
                ctrl.layer.removeFeatures(features);
            }
        });
    },

    enableDisableButtons: function() {
        var layer = this.getMap().workingLayer, nbSelect = layer.selectedFeatures.length;
        var btnEditGeom = this.getMap().queryById('editGeomBtn');
        var btnEditGeomAdv = this.getMap().queryById('editGeomAdvBtn');
        var btnDelete = this.getMap().queryById('deleteBtn');
        btnEditGeom.setDisabled(nbSelect != 1);
        btnEditGeomAdv.setDisabled(nbSelect != 1);
        btnDelete.setDisabled(nbSelect < 1);
    },

    onFeatureAdded: function(event) {
        var feature = event.feature;
        this.setFeatureStyle(feature, Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes);
    },

    getSelectedFeatures: function() {
        var features = this.getMap().workingLayer.selectedFeatures;
        return Ext.Array.from(features);
    },
    selectFeatures: function(feature) {
        var selectFeature = this.getMap().map.getControlsByClass('OpenLayers.Control.SelectFeature')[0];
        selectFeature.activate();
        selectFeature.unselectAll();
        if (Ext.isArray(feature)) {
            var features = feature, i;
            for (i=0 ; i<features.length ; i++) {
                selectFeature.select(features[i]);
            }
        } else if (Ext.isDefined(feature)) {
            selectFeature.select(feature);
        }
    },

    onModifyGeomActivate: function() {
        var features = this.getSelectedFeatures();
        if (features.length < 0) {
            return;
        }
        var ctrl = this.getMap().getSpecificControl('modifyGeom');
        ctrl.selectFeature(features[0]);
    },
    onModifyGeomAdvActivate: function() {
        var features = this.getSelectedFeatures();
        if (features.length < 0) {
            return;
        }
        var ctrl = this.getMap().getSpecificControl('modifyAdvGeom');
        ctrl.selectFeature(features[0]);
    },

    deleteEltFromMap: function() {
        var features = this.getSelectedFeatures();
        if (features.length > 0) {
            Ext.Msg.confirm('Cartographies', 'Supprimer les éléments sélectionnés ?', function(btn) {
                if (btn == "yes"){
                    this.getMap().workingLayer.removeFeatures(features);
                    this.enableDisableButtons();
                    this.activateDefaultTool();
                }
            }, this);
        }
    },

    activateDefaultTool: function() {
        this.getMap().down('#selectBtn').toggle(true);
    },

    editInfo: function(features) {
        if (!Ext.isArray(features)) {
            features = features?[features]:[];
        }
        // Aucune feature => le style global. Au moins une feature : le style de
        // la première
        var props = features.length<1?Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes:features[0].attributes;
        Ext.create('Sdis.Remocra.features.cartographie.StyleWindow', {
            props : props,
            info : 'Les modifications s\'appliqueront aux éléments qui seront dessinés par la suite'
                + (features.length>0 ? ' ainsi qu\'à la sélection ('+features.length+' élément'+(features.length>1?'s':'')+')' : '')
                + (features.length>1 ? '.<br/>Les choix présentés ci-dessous ont été initialisés à partir du premier élément de la sélection' : '')
                + '.',
            listeners : {
                'valid' : function(props) {
                    // Dès qu'une modification de style a lieu, on la répercute
                    // dans le style courant
                    Ext.apply(Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes, props);
                    // On applique également aux features concernées
                    Ext.Array.each(features, function(feat, index, recs) {
                        this.setFeatureStyle(feat, props);
                    }, this);
                    // Désélection de toutes les features et activation de
                    // l'outil par défaut
                    this.selectFeatures();
                    this.activateDefaultTool();
                    // Pour la suite, pas de libellé (cas particulier)
                    Sdis.Remocra.features.cartographie.StyleHolder.currentStyleAttributes.label = '';
                }, scope : this
            }
        }).show();
    },

    setFeatureStyle: function(feature, props) {
        if (props) {
            Ext.apply(feature.attributes, props);
        }
        feature.layer.drawFeature(feature);
    }
});
