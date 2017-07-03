Ext.require('Sdis.Remocra.features.oldebs.BaseFiche');
Ext.require('Sdis.Remocra.features.oldebs.OldebDragAndDropFiche');
Ext.define('Sdis.Remocra.features.oldebs.FicheOldeb', {
    extend: 'Sdis.Remocra.features.oldebs.BaseFiche',
    alias: 'widget.oldebs.ficheOldeb',

    title: 'Fiche OLD',
    tabItems: [{
        title: 'Parcelle',
        items: [{
            xtype: 'oldeb.localisation'
        }, {
            xtype: 'oldeb.propriete'
        }, {
            xtype: 'oldeb.locataire'
        } ]
    }, {
        title: 'Environnement',
        items: [{
            xtype: 'oldebdragdroptree',
            border: false
        }, {
            layout: 'hbox',
            border: false,
            items: [{
                fieldLabel: 'Volume de la ressource en eau mobilisable',
                xtype: 'numberfield',
                hideTrigger: true,
                allowDecimals: false,
                name: 'volume',
                labelWidth: 250,
                id: 'volume'

            }, {
                xtype: 'label',
                text: 'm³',
                margin: '0 0 0 10'

            } ]
        } ]
    }, {
        title: 'Visites',
        items: [{
            xtype: 'oldeb.visites'
        }, {
            xtype: 'oldeb.DetailsVisites'
        } ]
    } ],

    initComponent: function() {
        this.callParent(arguments);
    }

});