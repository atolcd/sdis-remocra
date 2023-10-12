Ext.require('Ext.data.Model');
Ext.define('Sdis.Remocra.model.RepertoireLieuData', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'libelle',
        type: 'string'
    }, {
        name: 'geometrie',
        type: 'string'
    }, {
        name: 'origine',
        type: 'string'
    }, {
        name: 'convertedLibelle',
        type: 'string',
        convert: function(newValue, model) {
            return model.get('libelle') + '  (' + model.get('origine')+')';
        }
    }],
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../repertoirelieu')
    }
});