Ext.require('Ext.container.Container');
Ext.require('Sdis.Remocra.widget.SdisItemSelector');

Ext.require('Sdis.Remocra.store.Thematique');

Ext.define('Sdis.Remocra.features.documents.fiche.Thematiques', {
    extend : 'Ext.container.Container',
    alias : 'widget.crBlocDocumentFiche.thematiques',

    title : 'Thématiques',
    layout : 'fit',
        
    items : [ {
        html : 'Thématiques concernées par le document :',
        border : false
    }, {
        xtype : 'sdisitemselector',
        toTitle : 'Sélectionnées',

        name : 'thematiques',
        store : 'Thematique',
        valueField : 'id',
        displayField : 'nom',
        style : 'margin-bottom: 15px;margin-top: 5px;',
        maxHeight: 200
    } ],

    initComponent : function() {
        this.callParent(arguments);
    }
});