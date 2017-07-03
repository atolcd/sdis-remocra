Ext.define('Sdis.Remocra.model.OldebLocataire', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'organisme',
        type: 'boolean'
    }, {
        name: 'raisonSociale',
        type: 'string'
    }, {
        name: 'civilite',
        type: 'string'
    }, {
        name: 'nom',
        type: 'string'
    }, {
        name: 'prenom',
        type: 'string'
    }, {
        name: 'telephone',
        type: 'string'
    }, {
        name: 'email',
        type: 'string'
    }, {
        name: 'oldeb',
        type: 'fk',
        useNull: true
    }

    ],
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../locataire')
    }
});