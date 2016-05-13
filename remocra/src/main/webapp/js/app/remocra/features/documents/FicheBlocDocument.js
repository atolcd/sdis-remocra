Ext.require('Sdis.Remocra.features.documents.fiche.General');
Ext.require('Sdis.Remocra.features.documents.fiche.Thematiques');
Ext.require('Sdis.Remocra.features.documents.fiche.ProfilDroits');

Ext.define('Sdis.Remocra.features.documents.FicheBlocDocument', {
    extend : 'Ext.window.Window',
    alias : 'widget.crBlocDocumentFiche',
    
    modal : true,
    width : 900,
    layout : 'fit',
    buttonAlign: 'center',
    
    title : 'Document',
    bodyPadding : 10,

    // A la création de la fenêtre
    blocDocument : null,
    createMode : true,

    items : [ {
        xtype : 'form',
        name : 'fiche',
        border : false,
        items : [ {
            xtype : 'tabpanel',
            plain : true,
            flex : 1,
            defaults : {
                padding : 10
            },
            items : [ {
                xtype : 'crBlocDocumentFiche.general'
            }, {
                xtype : 'crBlocDocumentFiche.thematiques'
            }, {
                xtype : 'crBlocDocumentFiche.profilDroits'
            } ]
        } ]
    } ],

    initComponent : function() {
        this.buttons = [ {
            text : 'Valider',
            itemId : 'ok'
        }, {
            text : 'Annuler',
            itemId : 'close',
            scope : this,
            handler : function() {
                this.close();
            }
        } ];
        this.callParent(arguments);
    }

});