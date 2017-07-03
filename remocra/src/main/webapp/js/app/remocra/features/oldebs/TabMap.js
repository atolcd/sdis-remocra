Ext.require('Sdis.Remocra.widget.map.Map');
Ext.require('Sdis.Remocra.features.oldebs.map.controls.GetOldeb');

Ext.define('Sdis.Remocra.features.oldebs.TabMap', {
    extend: 'Sdis.Remocra.widget.map.Map',
    alias: 'widget.crOldebMap',

    title: 'Localisation',
    itemId: 'localisation',

    moreItems: [],

    legendUrl: BASE_URL + '/../ext-res/js/app/remocra/features/oldebs/data/carte.json',

    initComponent: function() {
        this.editItems = ['Sélectionner : ', {
            xtype: 'button',
            tooltip: 'Sélectionner une obligation',
            text: '<span>Sélectionner</span>',
            iconCls: 'select-pointIcon',
            toggleGroup: 'excl-ctrl1',
            enableToggle: true,
            pressed: false,
            allowDepress: true,
            itemId: 'selectBtn'
        } ];

        this.editItems.push('Créer : ');
        if (Sdis.Remocra.Rights.getRight('OLDEB').Create) {
            this.editItems.push({
                xtype: 'button',
                tooltip: 'Ajouter une obligation',
                text: '<span>Dessiner</span>',
                cls: 'dessiner',
                iconCls: 'dessinerIcon',
                toggleGroup: 'excl-ctrl1',
                enableToggle: false,
                pressed: false,
                allowDepress: true,
                itemId: 'dessinerBtn'
            });
        }

        this.editItems.push('Modifier : ');

        // Bloc des boutons pour l'édition d'une old
        this.editItems.push({
            xtype: 'button',
            tooltip: 'Modifier une obligation',
            text: '<span>Modifier</span>',
            cls: 'dessiner',
            iconCls: 'editIcon',
            toggleGroup: 'excl-ctrl1',
            enableToggle: false,
            pressed: false,
            allowDepress: true,
            disabled: true,
            itemId: 'modifBtn'
        });
        this.editItems.push({
            tooltip: 'Valider la modification',
            text: 'Valider',
            width: 50,
            itemId: 'validModifBtn',
            disabled: true,
            hidden: true
        });
        this.editItems.push({
            tooltip: 'Annuler la modification',
            text: 'Annuler',
            width: 50,
            itemId: 'cancelModifBtn',
            disabled: true,
            hidden: true
        });

        // Ouverture de la fiche
        this.editItems.push({
            tooltip: "Ouvrir la fiche de l'obligation",
            text: '<span>Informations</span>',
            cls: 'edit-info',
            iconCls: 'edit-infoIcon',
            itemId: 'editInfoBtn',
            disabled: true
        });

        if (Sdis.Remocra.Rights.getRight('OLDEB').Delete) {
            // Suppression de l'old
            this.editItems.push({
                tooltip: 'Supprimer un élément',
                text: '<span>Supprimer</span>',
                cls: 'delete',
                iconCls: 'deleteIcon',
                itemId: 'deleteBtn',
                disabled: true
            });
        }

        this.callParent(arguments);
    },

    createSpecificLayer: function(layerDef) {
        return this.callParent(arguments);
    },

    createSpecificControls: function() {
        // On reprend les contrôles du parent
        var ctrls = this.callParent(arguments);
        Ext.apply(ctrls, {
            createOldeb: new OpenLayers.Control.DrawFeature(this.workingLayer, OpenLayers.Handler.Polygon),
            modifyOldeb: new OpenLayers.Control.ModifyFeature(this.workingLayer, {
                standalone: true
            })
        });

        // un fois que les couches sont chargées
        this.on('layersadded', function() {
            this.selectionOldebControl = new Sdis.Remocra.features.oldebs.map.controls.GetOldeb(this.workingLayer, {});
            // Ajout d'un contrôle sur un clic d'une OLDEB
            Ext.apply(ctrls, {
                selectOldeb: this.selectionOldebControl
            });
        }, this, {
            single: true
        });

        return ctrls;
    },

    getSelectionOldebControl: function() {
        return this.selectionOldebControl;
    },

    refreshZonesLayer: function() {
        if (!this.zonesLayer) {
            this.zonesLayer = this.getLayerByCode('zonesLayer');
        }
        // Sans la méthode clearGrid, le redraw ne rafraîchit pas bien la couche
        this.zonesLayer.clearGrid();
        this.zonesLayer.redraw(true);
    },

    /**
     * Style de la couche de travail
     */
    workingLayerStyleMap: function() {
        var sm = new OpenLayers.Style({
            fillColor: '#0f0fff',
            fillOpacity: 0.6,
            strokeColor: 'blue',
            strokeOpacity: 1,
            strokeWidth: 3,
            pointRadius: 5
        });
        return new OpenLayers.StyleMap({
            'default': sm,
            'select': sm,
            'temporary': sm
        });
    }

});
