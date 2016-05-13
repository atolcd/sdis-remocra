Ext.require('Sdis.Remocra.widget.map.Map');

Ext.define('Sdis.Remocra.features.prescrits.Map', {
    extend: 'Sdis.Remocra.widget.map.Map',
    alias: 'widget.prescritsMap',

    title: 'Carte des points d\'eau prescrits',

    moreItems: [],

    editItems: ['Sélectionner : ',{
        xtype: 'button',
        tooltip: 'Sélectionner des hydrants prescrits',
        text: '<span>Sélectionner</span>',
        iconCls: 'select-polygoneIcon',
        toggleGroup: 'excl-ctrl1',
        enableToggle: true,
        pressed: false,
        allowDepress: true,
        itemId: 'selectBtn'
    },'Prévention',{
        xtype: 'button',
        tooltip: 'Ajouter un hydrant prescrit',
        text: '<span>Ajout un poteau prescrit</span>',
        cls: 'dessiner',
        iconCls: 'dessinerIconBlue',
        toggleGroup: 'excl-ctrl1',
        enableToggle: true,
        pressed: false,
        allowDepress: true,
        itemId: 'dessinerBtnPrescrit'
    },{
        tooltip: 'Ouvrir la fiche de l\'hydrant prescrit',
        text: '<span>Informations</span>',
        cls: 'edit-info',
        iconCls: 'edit-infoIcon',
        itemId: 'editInfoBtn',
        disabled: true
    },{
        tooltip: 'Supprimer l\'hydrant sélectionné',
        text: '<span>Supprimer</span>',
        cls: 'delete',
        iconCls: 'deleteIcon',
        itemId: 'deleteBtn',
        disabled: true
    }],

    legendUrl: BASE_URL + '/../ext-res/js/app/remocra/features/prescrits/data/carte.json',

    initComponent: function() {
        this.callParent(arguments);
    },

    createSpecificLayer: function(layerDef) {

        if (layerDef.id == 'prescritLayer') {
            layerDef.url = BASE_URL + '/../hydrantprescrits/layer';
            layerDef.styleMap = this.getStyleMap();
            layerDef.infoMarginInPixels = 10;
            this.hydrantLayer = this.createGeoJsonLayer(layerDef);

            this.addSpecificControlLate('selectPolygone', new OpenLayers.Control.SelectFeature([this.workingLayer,this.hydrantLayer], {
                clickout: true,
                toggle: true,
                multiple: false,
                hover: false,
                multipleKey: "ctrlKey",
                toggleKey: "shiftKey",
                box: true
            }));

            return this.hydrantLayer;
        }
        return this.callParent(arguments);

    },

    createSpecificControls: function() {
        // On reprend les contrôles du parent
        var ctrls = this.callParent(arguments);
        Ext.apply(ctrls, {
            drawPoint: new OpenLayers.Control.DrawFeature(this.workingLayer, OpenLayers.Handler.Point)
        });
        return ctrls;
    },

    getStyleMap: function() {
        return new OpenLayers.StyleMap({
            "default": new OpenLayers.Style({
                fillColor: '#ff3fff',
                fillOpacity: 0.5,
                strokeColor: '#ff3fff',
                strokeOpacity: 1,
                strokeWidth: 3,
                pointRadius: 5
            }),
            "select": new OpenLayers.Style({
                fillOpacity: 1
            })
        });
    }
});