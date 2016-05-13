Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.network.TypeReferenceStore');
Ext.require('Sdis.Remocra.model.SousTypeAlerteElt');

Ext.define('Sdis.Remocra.network.SousTypeAlerteEltStore', {
    extend : 'Sdis.Remocra.network.TypeReferenceStore',
    singleton : true,
    storeId: 'SousTypeAlerteEltStore',
    
    model: 'Sdis.Remocra.model.SousTypeAlerteElt',
    url: 'soustypealerteelts'
});
