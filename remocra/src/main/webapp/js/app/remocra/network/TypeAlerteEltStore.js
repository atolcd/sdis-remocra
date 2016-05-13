Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.network.TypeReferenceStore');
Ext.require('Sdis.Remocra.model.TypeAlerteElt');

Ext.define('Sdis.Remocra.network.TypeAlerteEltStore', {
    extend : 'Sdis.Remocra.network.TypeReferenceStore',
    singleton : true,
    storeId: 'TypeAlerteEltStore',
    
    model: 'Sdis.Remocra.model.TypeAlerteElt',
    url: 'typealerteelts'
});
