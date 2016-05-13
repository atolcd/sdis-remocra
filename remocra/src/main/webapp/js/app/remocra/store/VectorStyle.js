Ext.require('Sdis.Remocra.model.VectorStyle');

Ext.define('Sdis.Remocra.store.VectorStyle', {
    extend : 'Ext.data.Store',
    storeId : 'VectorStyle',
    singleton : true,

    model : 'Sdis.Remocra.model.VectorStyle',
    autoLoad : true,

    listeners : {
        load : function(store, records, successful, opt) {
            // Pour chaque élément, on vient définir le style Openlayers si une
            // définition est fournie
            Ext.Array.each(records, function(rec, index, recs) {
                var def = rec.get('def');
                if (def) {
                    OpenLayers.Renderer.symbol[rec.get('code')] = def;
                }
            });
        }
    }
});
