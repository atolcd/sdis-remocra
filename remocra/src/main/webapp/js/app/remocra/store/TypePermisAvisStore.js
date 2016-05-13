Ext.require('Sdis.Remocra.network.TypeReferenceStore');
Ext.require('Sdis.Remocra.model.TypePermisAvis');

Ext.define('Sdis.Remocra.store.TypePermisAvisStore', {
    extend: 'Sdis.Remocra.network.TypeReferenceStore',
    
    singleton: true,
    storeId: 'TypePermisAvisStore',
    autoLoad: false,
    
    url: 'typepermisavis',
    model: 'Sdis.Remocra.model.TypePermisAvis',
    
    listeners: {
        'load': function(store, records, successful, eOpts) {
            Sdis.Remocra.store.TypePermisAvisTousStore.add(Ext.create('Sdis.Remocra.model.TypePermisAvis', {id: null, nom: ' Tous'}));
            Ext.Array.each(records, function(record, index, theRecords) {
                Sdis.Remocra.store.TypePermisAvisTousStore.add(record);
                if (record.get('code')==='ATTENTE') {
                    Sdis.Remocra.store.TypePermisAvisPprifStore.add(record);
                    Sdis.Remocra.store.TypePermisAvisNoPprifStore.add(record);
                } else if (record.get('pprif')===true) {
                    Sdis.Remocra.store.TypePermisAvisPprifStore.add(record);
                } else {
                    Sdis.Remocra.store.TypePermisAvisNoPprifStore.add(record);
                }
            });
        }
    }
});
