Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.network.TypeReferenceStore');

Ext.define('Sdis.Remocra.network.TypeAlerteAnoStore', {
    extend : 'Sdis.Remocra.network.TypeReferenceStore',
    singleton : true,
    storeId: 'TypeAlerteAnoStore',
    
    url: 'typealerteanos'
});
