Ext.require('Sdis.Remocra.network.TypeReferenceStore');
Ext.require('Sdis.Remocra.model.TypePermisInterservice');

Ext.define('Sdis.Remocra.store.TypePermisInterserviceStore', {
    extend: 'Sdis.Remocra.network.TypeReferenceStore',
    
    singleton : true,
    storeId: 'TypePermisInterserviceStore',
    autoLoad: false,
    
    url: 'typepermisinterservices',
    model : 'Sdis.Remocra.model.TypePermisInterservice',
    
    listeners: {
        'load': function(store, records, successful, eOpts) {
            Ext.Array.each(records, function(record, index, theRecords) {
                if (record.get('pprif')===true) {
                    Sdis.Remocra.store.TypePermisInterservicePprifStore.add(record);
                } else {
                    Sdis.Remocra.store.TypePermisInterserviceNoPprifStore.add(record);
                }
            });
        }
    }
});
