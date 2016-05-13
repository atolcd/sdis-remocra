Ext.require('Ext.form.FieldSet');
Ext.require('Sdis.Remocra.widget.map.LegendTemplate');

Ext.define('Sdis.Remocra.features.cartographie.Config', {
    extend : 'Ext.Panel',
    alias : 'widget.crCartographieConfig',

    title : 'Configuration',

    defaults : {
        style : 'margin-left:20px',
        labelAlign : 'left',
        labelWidth : 180,
        labelSeparator : '',
        width : 400
    },

    legendUrl : BASE_URL+'/../ext-res/js/app/remocra/features/cartographie/data/carte.json',
    //legendHideSliders : true,
    
    items : [ {
        xtype : 'textfield',
        fieldLabel : 'Titre',
        itemId : 'titre',
        value : 'Nouvelle carte'
    }, {
        xtype : 'combo',
        fieldLabel : 'Format',
        itemId : 'format',
        store : new Ext.data.SimpleStore({
            fields : [ 'value', 'display' ],
            data : [ [ 'a4', 'A4' ], [ 'a3', 'A3' ] ]
        }),
        displayField : 'display',
        valueField : 'value',
        forceSelection : true,
        autoSelect : true,
        editable : false,
        value : 'a4'
    }, {
        xtype : 'combo',
        fieldLabel : 'Orientation',
        itemId : 'orientation',
        store : new Ext.data.SimpleStore({
            fields : [ 'value', 'display' ],
            data : [ [ 'landscape', 'Paysage' ], [ 'portrait', 'Portrait' ] ]
        }),
        displayField : 'display',
        valueField : 'value',
        forceSelection : true,
        autoSelect : true,
        editable : false,
        value : 'landscape'
    }, {
        xtype : 'displayfield',
        fieldLabel : 'Couches'
    },{
        xtype : 'displayfield',
        itemId : 'couches',
        cls : 'simplemaplegend maplegend forceover',
        style : 'margin-left:190px;float:none'
    }, {
        xtype : 'button',
        itemId : 'open',
        text : 'Ouvrir la carte',
        width : 180,
        style : 'margin-top:20px;'
    } ],

    initComponent : function() {
        this.callParent(arguments);
        this.on('afterrender', this.loadMapConfig, this);
    },
    
    loadMapConfig : function() {
        if (this.legendUrl == null) {
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
                this.legendData = Ext.decode(response.responseText);
                this.prepareLegendData();
                this.renderLegend();
            }
        });
    },
    
    /**
     * Préparation du choix des couches : droits sur les couches, etc.
     * Couche permis : nécessite le droit PERMIS.Read 
     * @returns
     */
    prepareLegendData : function() {
        var legendDataPrepared = Ext.clone(this.legendData);
        var legendData = this.legendData;
        // Chaque groupe (à l'envers)
        for (iGrp = legendData.items.length; iGrp > 0; iGrp--) {
            grp = legendData.items[iGrp - 1];
            // Chaque couche (à l'envers)
            for(iLay=grp.items.length ; iLay>0 ; iLay--) {
                var layerDef = grp.items[iLay-1];
                if (layerDef.id=='permisLayer' && !Sdis.Remocra.Rights.getRight('PERMIS').Read) {
                    // Pas de droit sur cette couche
                    Ext.Array.erase(legendDataPrepared.items[iGrp - 1].items, iLay - 1, 1);
                }
            }
        }
        // On retire les groupes vides
        for (iGrp = legendDataPrepared.items.length; iGrp > 0; iGrp--) {
            if (Ext.isEmpty(legendDataPrepared.items[iGrp - 1].items)) {
                Ext.Array.erase(legendDataPrepared.items, iGrp - 1, 1);
            }
        }
        // Les couches concernées sont celles qui ont été préparées
        this.legendData = legendDataPrepared;
    },
    
    getTargetLegendData : function() {
        var legendDataPrepared = Ext.clone(this.legendData);
        var legendData = this.legendData;
        // Chaque groupe (à l'envers)
        for (iGrp = legendData.items.length; iGrp > 0; iGrp--) {
            grp = legendData.items[iGrp - 1];
            // Chaque couche (à l'envers)
            for(iLay=grp.items.length ; iLay>0 ; iLay--) {
                var layerDef = grp.items[iLay-1];
                if (!layerDef.visibility) {
                    // Pas de droit sur cette couche
                    Ext.Array.erase(legendDataPrepared.items[iGrp - 1].items, iLay - 1, 1);
                }
            }
        }
        // On retire les groupes vides
        for (iGrp = legendDataPrepared.items.length; iGrp > 0; iGrp--) {
            if (Ext.isEmpty(legendDataPrepared.items[iGrp - 1].items)) {
                Ext.Array.erase(legendDataPrepared.items, iGrp - 1, 1);
            }
        }
        // Les couches concernées sont celles qui ont été préparées
        return legendDataPrepared;
    },
    
    renderLegend : function() {
        // Légende
        if (this.legendTpl == null) {
            this.legendTpl = Ext.create('Sdis.Remocra.widget.map.LegendTemplate');
        }
        var legendNode = this.queryById('couches').getEl();
        this.legendTpl.overwrite(legendNode, {
            id: this.id,
            legendData: this.legendData
        }, false);
        
        // Mise à jour de la hauteur du conteneur
        this.doLayout();
    },
    
    getLayerByCode : function(layerCode) {
        var legendData = this.legendData, i, j;
        if (!legendData) {
            return null;
        }
        for (i=0 ; i<this.legendData.items.length ; i++) {
            var grp = this.legendData.items[i];
            for (j=0 ; j<grp.items.length ; j++) {
                var layerDef = grp.items[j];
                if (layerDef.id == layerCode) {
                    return layerDef;
                }
            }
        }
        return null;
    },
    setLayerVisibility : function(layerCode, visibility) {
        this.getLayerByCode(layerCode).visibility = visibility;
    },
    setLayerOpacity : function(layerCode, opacity) {
        this.getLayerByCode(layerCode).opacity = opacity;
    },
    
    getUrl : function() {
        var i, j;
        var titre = this.queryById('titre').getValue();
        var format = this.queryById('format').getValue();
        var orientation = this.queryById('orientation').getValue();
        var url = 'cartographie/carte/t/' + (encodeURIComponent(titre)||'Carte sans nom') + '/f/' + format + '/o/' + orientation;
        // Couches de la forme /{couche}/{opacité}
        for (i=0 ; i<this.legendData.items.length ; i++) {
            var grp = this.legendData.items[i];
            for (j=0 ; j<grp.items.length ; j++) {
                var layerDef = grp.items[j];
                if (layerDef.visibility && layerDef.opacity>0) {
                    url += '/'+layerDef.id+'/'+(layerDef.opacity||1);
                }
            }
        }
        return url;
    }
});