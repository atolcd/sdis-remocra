Ext.define('Sdis.Remocra.features.rci.Map', {
    extend : 'Sdis.Remocra.widget.map.Map',
    alias : 'widget.crRciMap',

    legendUrl : BASE_URL
            + '/../ext-res/js/app/remocra/features/rci/data/carte.json',
    pointRadius : 10,

    // Par défaut, on affiche les anciens départs
    hideOldRci : false,

    moreItems : [{
        tooltip: 'Masquer les départs antérieurs à l\'année civile', text: '<span>Masquer les départs antérieurs</span>',
        cls: 'hide-old-rci', iconCls: 'hide-old-rciIcon',
        itemId: 'hideOldRciBtn',
        toggleGroup : 'excl-hide-old-rci',
        enableToggle : true,
        pressed : false,
        allowDepress : true
    }],

    editItems : [ 'Sélectionner : ', {
        xtype : 'button',
        tooltip : 'Sélectionner un départ',
        text : '<span>Sélectionner</span>',
        iconCls : 'select-polygoneIcon',
        toggleGroup : 'excl-ctrl1',
        enableToggle : true,
        pressed : false,
        allowDepress : true,
        itemId : 'selectBtn'
    }, 'Créer : ', {
        xtype : 'button',
        tooltip : 'Ajouter un départ',
        text : '<span>Ajouter</span>',
        iconCls : 'dessinerIcon',
        toggleGroup : 'excl-ctrl1',
        enableToggle : true,
        pressed : false,
        allowDepress : true,
        itemId : 'addBtn'
    }, {
        xtype : 'button',
        tooltip : 'Ajouter un départ X, Y',
        text : '<span>Ajouter X,Y</span>',
        iconCls : 'addXYIcon',
        toggleGroup : 'excl-ctrl1',
        enableToggle : true,
        pressed : false,
        allowDepress : true,
        itemId : 'addXYBtn'
    }, 'Modifier : ', {
        tooltip : 'Ouvrir la fiche du départ',
        text : '<span>Fiche</span>',
        iconCls : 'edit-infoIcon',
        itemId : 'editInfoBtn',
        disabled : true
    }, {
        tooltip : 'Supprimer un départ',
        text : '<span>Supprimer</span>',
        iconCls : 'deleteIcon',
        itemId : 'deleteBtn',
        disabled : true
    }, 'Déplacer : ', {
        tooltip : 'Activer le déplacement',
        text : '<span>Activer</span>',
        iconCls : 'moveIcon',
        itemId : 'activeMoveBtn',
        disabled : true,
        enableToggle : true,
        pressed : false,
        allowDepress : true,
        toggleGroup : 'excl-ctrl1'
    }, {
        tooltip : 'Valider le déplacement',
        text : 'Valider',
        width : 50,
        itemId : 'validMoveBtn',
        disabled : true,
        hidden : true
    }, {
        tooltip : 'Annuler le déplacement',
        text : 'Annuler',
        width : 50,
        itemId : 'cancelMoveBtn',
        disabled : true,
        hidden : true
    } ],

    createSpecificLayer : function(layerDef) {
        if (layerDef.id == 'rciLayer') {
            layerDef.url = BASE_URL + '/../rci/layer';
            layerDef.styleMap = this.getStyleMap();
            layerDef.infoMarginInPixels = this.pointRadius;
            this.rciLayer = this.createGeoJsonLayer(layerDef);
            this.rciLayer.hideOldRci = this.hideOldRci;

            this.addSpecificControlLate('selectPolygone',
                    new OpenLayers.Control.SelectFeature([ this.workingLayer,
                            this.rciLayer ], {
                        clickout : true,
                        toggle : true,
                        multiple : false,
                        hover : false,
                        multipleKey : "ctrlKey",
                        toggleKey : "shiftKey",
                        box : true
                    }));

            this.addSpecificControlLate('movePoint',
                    new OpenLayers.Control.ModifyFeature(this.workingLayer, {
                        standalone : true,
                        dragComplete : function(feature) {
                            this.events.triggerEvent('dragcomplete', {
                                feature : feature
                            });
                        },
                        dragStart : function(feature) {
                            this.events.triggerEvent('dragstart', {
                                feature : feature
                            });
                        }
                    }));

            return this.rciLayer;
        }
        return this.callParent(arguments);
    },

    getStyleMap : function() {
        var s = new OpenLayers.Style({
            fillOpacity : 1,
            externalGraphic: '${externalGraphic}',
            graphicWidth: 35,
            graphicHeight: 35,
            graphicYOffset: -(35/2),
            // Etiquettes
            label: '${label}',
            labelYOffset: -20,
            labelOutlineColor: "white",
            labelOutlineWidth: 3,
            fontSize: 10,
            fontColor: '${fontColor}',
            display: '${display}'
        }, {
            context: {
                fontColor: function(feature) {
                    var dateIncendie = Ext.Date.parse(feature.data['dateIncendie'], 'd/m/Y H\\hi');
                    var inCivilYear = dateIncendie ? new Date().getYear()-dateIncendie.getYear()==0 : true;
                    return inCivilYear ?
                            // Dans l'année civile
                            (feature.renderIntent == 'default' ? '#dc0405' : '#950303') :
                            // Avant l'année civile
                            (feature.renderIntent == 'default' ? '#fe8002' : '#a7580a');
                },
                externalGraphic: function(feature) {
                    var dateIncendie = Ext.Date.parse(feature.data['dateIncendie'], 'd/m/Y H\\hi');
                    var inCivilYear = dateIncendie ? new Date().getYear() - dateIncendie.getYear() == 0 : true;
                    return 'images/remocra/cartes/legende/rci/rci' + (inCivilYear ? '' : '-before')
                            + (feature.renderIntent == 'default' ? '' : '-sel') + '.png';
                },
                label: function(feature) {
                    var dateIncendie = feature.data['dateIncendie'];
                    var formated = dateIncendie && dateIncendie.length>0 ?
                        dateIncendie.split(' ')[0] : '';
                    return formated;
                },
                display: function(feature) {
                    var dateIncendie = Ext.Date.parse(feature.data['dateIncendie'], 'd/m/Y H\\hi');
                    var inCivilYear = dateIncendie ? new Date().getYear() - dateIncendie.getYear() == 0 : true;
                    var hideOldRci = feature.layer.hideOldRci;
                    return hideOldRci===true && !inCivilYear ? 'none' : '';
                }
            }
        });
        return new OpenLayers.StyleMap({
            "default" : s,
            "select": s
        });
    },
    workingLayerStyleMap: function() {
        var s = new OpenLayers.Style({
            fillOpacity : 1,
            externalGraphic: 'images/remocra/cartes/legende/rci/rci-tmp.png',
            graphicWidth: 35,
            graphicHeight: 35,
            graphicYOffset: -(35/2),
            // Etiquettes
            label: '${label}',
            labelYOffset: -20,
            labelOutlineColor: "white",
            labelOutlineWidth: 3,
            fontSize: 10,
            fontColor: '#0c0389'
        }, {
            context: {
                label: function(feature) {
                    var dateIncendie = feature.data['dateIncendie'];
                    var formated = dateIncendie && dateIncendie.length>0 ?
                            '*' + dateIncendie.split(' ')[0] + '*' : '';
                    return formated;
                }
            }
        });
        return new OpenLayers.StyleMap({
            'default' : s,
            'select' : s,
            'temporary' : s
        });
    },
    
    createSpecificControls: function() {
        // On reprend les contrôles du parent
        var ctrls = this.callParent(arguments);
        Ext.apply(ctrls, {
            drawPoint: new OpenLayers.Control.DrawFeature(this.workingLayer, OpenLayers.Handler.Point)
        });
        return ctrls;
    }
});