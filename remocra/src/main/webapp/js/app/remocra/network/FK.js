Ext.define('Sdis.Remocra.network.FK', {});

Ext.apply(Ext.data.Types, {
    FK: {
        type: 'int',
        convert: function(v, record) {
            if (Ext.isObject(v)) {
                if (v.data && v.data.id) {
                    return v.data.id;
                }
                return v.id;
            }
            return v;
        },
        sortType: Ext.data.SortTypes.none
    }
});