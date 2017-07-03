Ext.define('Sdis.Remocra.model.TypeOldebNature', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'actif',
        type: 'boolean'
    }, {
        name: 'code',
        type: 'string'
    }, {
        name: 'nom',
        type: 'string'
    } ]
});