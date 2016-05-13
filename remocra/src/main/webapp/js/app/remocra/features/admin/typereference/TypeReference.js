
Ext.ns('Sdis.Remocra.features.admin.typereference');

Ext.require('Sdis.Remocra.features.admin.typereference.TypeReferenceGrid');

Ext.define('Sdis.Remocra.features.admin.typereference.TypeReference', {
    extend: 'Ext.Panel',
    alias: 'widget.crAdminTypeReference',

    border: false, defaults: {border: false},
    
    // Attendus
    grid: null,
    //
    
    initComponent: function() {
        Ext.apply(this, {
            items: this.grid
        });
        
        this.callParent(arguments);
    }
});
