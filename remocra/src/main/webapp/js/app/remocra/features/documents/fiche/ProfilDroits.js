Ext.require('Ext.container.Container');
Ext.require('Sdis.Remocra.widget.SdisItemSelector');

Ext.require('Sdis.Remocra.store.ProfilDroit');

Ext.define('Sdis.Remocra.features.documents.fiche.ProfilDroits', {
    extend : 'Ext.container.Container',
    alias : 'widget.crBlocDocumentFiche.profilDroits',

    title : 'Profils',
    layout : 'fit',

    items : [ {
        html : 'Profils concernés par le document :',
        border : false
    }, {
        xtype : 'sdisitemselector',
        
        name : 'profilDroits',
        store : {
            type : 'crProfilDroit',
            autoLoad : true,
            pageSize : 100
        },
        valueField : 'id',
        displayField : 'nom',
        style : 'margin-bottom: 15px;margin-top: 5px;',
        maxHeight: 200
    } ],

    initComponent : function() {
        this.callParent(arguments);
    }
});