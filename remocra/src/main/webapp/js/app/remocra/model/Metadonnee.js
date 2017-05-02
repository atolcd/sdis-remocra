Ext.define('Sdis.Remocra.model.Metadonnee', {
    extend: 'Ext.data.Model',

    fields: [ {
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'titre',
        type: 'string'
    }, {
        name: 'resume',
        type: 'string'
    }, {
        name: 'urlVignette',
        type: 'string'
    }, {
        name: 'urlFiche',
        type: 'string'
    }, {
        name: 'thematique',
        type: 'int',
        mapping: function(data, reader) {
            if (data['thematique']) {
                return data['thematique']['id'];
            }
        },
        persist: false
    }, {
        name: 'codeExport',
        type: 'string'
    } ]
});