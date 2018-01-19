Ext.require('Sdis.Remocra.features.hydrants.BaseFiche');

Ext.define('Sdis.Remocra.features.hydrants.FichePena', {
    extend: 'Sdis.Remocra.features.hydrants.BaseFiche',
    alias: 'widget.hydrant.fichepena',

    title: 'PENA',

    tabItems: [ {
        title: 'Identification et localisation',
        items: [ {
            xtype: 'hydrant.tracabilite',
            itemId: 'tracabilite'
        }, {
            xtype: 'hydrant.localisationpena'
        }, {
            xtype: 'hydrant.identificationpena',
            itemId: 'identification'
        } ]
    }, {
        title: 'Citerne',
        items: [ {
            xtype: 'hydrant.citerne'
        }, {
            xtype: 'hydrant.verifhydrauliquepena'
        } ]
    }, {
        itemId: 'tabMCO',
        title: 'Gestionnaire, MCO, divers',
        items: [ {
            xtype: 'hydrant.mcopena'
        }, {
            xtype: 'hydrant.gestionnairepena'
        }, {
            xtype: 'hydrant.divers'
        } ]
    }, {
        title: 'Documents',
        xtype: 'crFileupload',
        itemId: 'documents'
    }, {
        title: 'Points d\'attention',
        pointAttention: true,
        items: [ {
            xtype: 'hydrant.anomalie'
        } ]
    } ],

    initComponent: function() {
        this.callParent(arguments);
    }

});