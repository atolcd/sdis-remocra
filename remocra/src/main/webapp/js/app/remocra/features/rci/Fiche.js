Ext.require('Sdis.Remocra.features.rci.bloc.Entete');
Ext.require('Sdis.Remocra.features.rci.bloc.Renseignements');
Ext.require('Sdis.Remocra.features.rci.bloc.Constatations');
Ext.require('Sdis.Remocra.features.rci.bloc.CauseResultats');

Ext.define('Sdis.Remocra.features.rci.Fiche', {
    extend : 'Ext.window.Window',
    alias : 'widget.rciFiche',

    modal : true,
    width : 900,
    layout : 'fit',

    title : 'Départ de feu',
    bodyPadding : 10,

    dockedItems : [ {
        xtype : 'toolbar',
        dock : 'bottom',
        ui : 'footer',
        layout : {
            pack : 'center'
        },
        items : [ {
            text : 'Valider',
            itemId : 'ok'
        }, {
            text : 'Annuler',
            itemId : 'cancel'
        } ]
    } ],

    items : [ {
        xtype : 'form',
        name : 'fiche',
        border : false,
        layout : {
            type : 'vbox',
            align : 'stretch'
        },
        items : [ {
            xtype : 'rciEntete',
            padding : 10
        }, {
            xtype : 'tabpanel',
            plain : true,
            flex : 1,
            defaults : {
                padding : 10
            },
            items : [ {
                xtype : 'rciRenseignements'
            }, {
                xtype : 'rciConstatations'
            }, {
                xtype : 'rciCauseResultats'
            }, {
                title : 'Documents',
                xtype : 'crFileupload',
                itemId : 'documents'
            } ]
        } ]
    } ]
});