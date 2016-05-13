Ext.require('Sdis.Remocra.widget.BlocDocumentGrid');

Ext.define('Sdis.Remocra.features.hydrants.TabBlocDocument', {
    extend: 'Ext.Panel',
    alias: 'widget.crHydrantsBlocDocument',

    title: 'Documents',
    itemId: 'documents',
    cls: 'tabblocdocument',
    
    height: 500,
    forceFit: true,
    bodyPadding: 20,
    
    items: [{
        html: 'Documents de la catégorie :',
        border: false
    }, {
        xtype: 'crBlocDocumentGrid',
        thematiques: 'POINTDEAU'
    }],

    initComponent: function() {
        this.callParent(arguments);
    }
});
