Ext.require('Ext.container.Container');
Ext.require('Ext.form.Panel');
Ext.require('Ext.form.field.File');
Ext.require('Sdis.Remocra.widget.LinkButton');

Ext.define('Sdis.Remocra.features.documents.fiche.General', {
    extend : 'Ext.container.Container',
    alias : 'widget.crBlocDocumentFiche.general',

    title : 'Général',
    defaults : {
        width : 500
    },

    items : [ {
        xtype : 'textfield',
        fieldLabel : 'Titre',
        name : 'titre',
        msgTarget : 'under',
        minLength : 2,
        enforceMinLength : true
    }, {
        xtype : 'textarea',
        fieldLabel : 'Description',
        name : 'description',
        style : 'margin-bottom: 20px;margin-top: 5px;'
    }, {
        xtype : 'form',
        border : false,
        defaults : {
            anchor : '100%',
            maxWidth : 500
        },
        name : 'formDocument',
        items : [ {
            xtype : 'fieldcontainer',
            layout : 'hbox',
            fieldLabel : 'Document',
            name : 'downloadContainer',
            items : [ {
                xtype : 'linkbutton',
                name : 'download',
                text : 'Télécharger',
                margin : '0 20 0 0'
            }, {
                xtype : 'filefield',
                buttonOnly: true,
                name : 'fichier',
                buttonText : '<span>Choisir...</span>',
                submitValue : false
            }]
        } ]
    } ],

    initComponent : function() {
        this.callParent(arguments);
    }
});