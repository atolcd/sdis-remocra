Ext.require('Sdis.Remocra.widget.map.Map');
Ext.require('Sdis.Remocra.features.cartographie.StyleHolder');

Ext.define('Sdis.Remocra.features.cartographie.Map', {
    extend: 'Sdis.Remocra.widget.map.Map',
    alias: 'widget.crCartographieMap',

    legendUrl : BASE_URL+'/../ext-res/js/app/remocra/features/cartographie/data/carte.json',
    
    // On affiche la combo de zoom sur tournées (si l'utilisateur a les droits)
    hideZoomTournee : false,

    moreItems : [],
    editItems : [ 'Sélectionner : ', {
        xtype : 'button',
        tooltip : 'Sélectionner un élément',
        text : '<span>Sélectionner</span>',
        iconCls : 'select-polygoneIcon',
        toggleGroup : 'excl-ctrl1',
        enableToggle : true,
        pressed : false,
        allowDepress : true,
        itemId : 'selectBtn'
    }, 'Dessiner : ', {
        xtype : 'button',
        tooltip : 'Dessiner un point',
        text : '<span>Dessiner</span>',
        iconCls : 'draw-pointIcon',
        toggleGroup : 'excl-ctrl1',
        enableToggle : true,
        pressed : false,
        allowDepress : true,
        itemId : 'addPointBtn'
    }, {
        xtype : 'button',
        tooltip : 'Dessiner une ligne',
        text : '<span>Dessiner</span>',
        iconCls : 'draw-linestringIcon',
        toggleGroup : 'excl-ctrl1',
        enableToggle : true,
        pressed : false,
        allowDepress : true,
        itemId : 'addLineBtn'
    }, {
        xtype : 'button',
        tooltip : 'Dessiner un polygone',
        text : '<span>Dessiner</span>',
        iconCls : 'draw-polygonIcon',
        toggleGroup : 'excl-ctrl1',
        enableToggle : true,
        pressed : false,
        allowDepress : true,
        itemId : 'addPolygonBtn'
    }, 'Modifier : ', {
        tooltip : 'Choix de style',
        text : '<span>Style</span>',
        iconCls : 'edit-infoIcon',
        itemId : 'editInfoBtn'
    }, {
        tooltip : 'Modifier un élément',
        text : '<span>Modifier</span>',
        iconCls : 'moveIcon',
        itemId : 'editGeomBtn',
        disabled : true,
        enableToggle : true,
        pressed : false,
        allowDepress : true,
        toggleGroup : 'excl-ctrl1'
    }, {
        tooltip : 'Modifier un élément',
        text : '<span>Modifier avancé</span>',
        iconCls : 'moveRotateIcon',
        itemId : 'editGeomAdvBtn',
        disabled : true,
        enableToggle : true,
        pressed : false,
        allowDepress : true,
        toggleGroup : 'excl-ctrl1'
    }, {
        tooltip : 'Supprimer un élément',
        text : '<span>Supprimer</span>',
        iconCls : 'deleteIcon',
        itemId : 'deleteBtn',
        disabled : true
    } ],

    initComponent: function() {
        var p;
        this.couches = [];
        for (p in this.extraParams) {
            var key = p, value = this.extraParams[p];
            if (key == 't') {
                this.titre = decodeURIComponent(value);
            } else if (key == 'f') {
                this.format = value;
            } else if (key == 'o') {
                this.orientation = value;
            } else  {
                // Couche
                this.couches[key] = value;
            }
        }
        
        this.mapTpl = Ext.create('Ext.XTemplate',
            '<div class="format-'+this.format+' orientation-'+this.orientation
                +'"><div class="x-panel-header-text x-panel-header-text-default" style="margin-bottom:5px;">' + this.titre + '</div>',
            '<div class="maptbar1"><!-- --></div>', '<div class="maptbar2"><!-- --></div>', '<div class="map"><!-- --></div>',

            // Ici : simplemaplegend maplegend forceover pour forcer l'affichage
            // de toute la légende
            '<div class="simplemaplegend maplegend forceover"><!-- --></div>',

            '<div class="mapmentions">',
                '<div class="mapsrc">',
                    '<span>Edition réalisée le <b>',  Ext.Date.format(new Date(), 'd/m/Y à H\\hi'),
                    '</b> à partir de <u>', URL_SITE, '</u></span>',
                '</div>',
                '<div class="mapcp">',
                    '<span> Copyright ', REMOCRA_INFO_COPYRIGHT, '</span>',
                '</div>',
            '</div>'
        );
        
        this.callParent(arguments);
    },
    
    /**
     * Retire les couches non retenues de la configuration avant de faire le
     * filtre classique du parent
     * 
     * @param legendData
     * @returns
     */
    addLayersFromLayerConfig: function(legendData) {
        var legendDataPrepared = Ext.clone(legendData);
        // Chaque groupe (à l'envers)
        for (iGrp = legendData.items.length; iGrp > 0; iGrp--) {
            grp = legendData.items[iGrp - 1];
            // Chaque couche (à l'envers)
            for(iLay=grp.items.length ; iLay>0 ; iLay--) {
                var layerDef = grp.items[iLay-1];
                if (!this.couches[layerDef.id] || (layerDef.id=='permisLayer' && !Sdis.Remocra.Rights.hasRight('PERMIS_R'))) {
                    // Pas de droit sur cette couche
                    Ext.Array.erase(legendDataPrepared.items[iGrp - 1].items, iLay - 1, 1);
                } else {
                    // On force la visibilité de la couche (choix initial de
                    // l'utilisateur)
                    legendDataPrepared.items[iGrp - 1].items[iLay - 1].visibility = true;
                }
            }
        }
        // On retire les groupes vides
        for (iGrp = legendDataPrepared.items.length; iGrp > 0; iGrp--) {
            if (Ext.isEmpty(legendDataPrepared.items[iGrp - 1].items)) {
                Ext.Array.erase(legendDataPrepared.items, iGrp - 1, 1);
            }
        }
        
        this.callParent([legendDataPrepared]);

        // On enregistre l'emprise de la carte de cartographie à chaque zoom
        this.map.events.on({
            "zoomend": Ext.bind(this.saveBounds, this)
        });
    },

    createSpecificControls: function() {
        // On reprend les contrôles du parent
        var ctrls = this.callParent(arguments);
        Ext.apply(ctrls, {
            selectPolygon: new OpenLayers.Control.SelectFeature(this.workingLayer, {
                clickout : true,
                toggle : true,
                multiple : false,
                hover : false,
                multipleKey : "ctrlKey",
                toggleKey : "shiftKey",
                box : true
            }),
            drawPoint: new OpenLayers.Control.DrawFeature(this.workingLayer, OpenLayers.Handler.Point),
            drawLine: new OpenLayers.Control.DrawFeature(this.workingLayer, OpenLayers.Handler.Path),
            drawPolygon: new OpenLayers.Control.DrawFeature(this.workingLayer, OpenLayers.Handler.Polygon),
            modifyGeom: new OpenLayers.Control.ModifyFeature(this.workingLayer, {
                // Standalone pour empêcher le SelectFeature du contrôle car la
                // sélection est déjà gérée
                standalone : true,
                vertexRenderIntent : 'modifyHandles'
            }),
            modifyAdvGeom: new OpenLayers.Control.ModifyFeature(this.workingLayer, {
                // Même commentaire que modifyGeom
                standalone : true,
                mode : OpenLayers.Control.ModifyFeature.DRAG
                    | OpenLayers.Control.ModifyFeature.RESIZE
                    | OpenLayers.Control.ModifyFeature.ROTATE,
                vertexRenderIntent : 'modifyHandles'
            })
        });
        return ctrls;
    },

    workingLayerStyleMap: function() {
        return Sdis.Remocra.features.cartographie.StyleHolder.workingLayerStyleMap();
    },

    // Zoom sur l'emprise stockée si elle existe, sur le département sinon
    zoomToBestExtent: function() {
        var lastBoundsCarto = Ext.util.Cookies.get("lastBoundsCarto") || '';
        if (lastBoundsCarto.length>0) {
            var parts = lastBoundsCarto.split(',');
            if (parts.length == 4) {
                var bounds = new OpenLayers.Bounds(parts);
                this.zoomToBounds(bounds);
                return;
            }
        }
        this.callParent(arguments);
    },

    // Enregistrement de l'emprise courante de la carte de cartographie dans un
    // Cookie (1 journée)
    saveBounds: function() {
        var bounds2154 = this.map.getExtent().transform(this.map.getProjection(), "epsg:2154");
        var expires = new Date();
        expires.setDate(expires.getDate()+1); // Expiration 1 jour
        Ext.util.Cookies.set("lastBoundsCarto", bounds2154.toBBOX(), expires);
    },

    getInitHeight : function() {
        return 100;
    }
});