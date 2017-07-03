Ext.define('Sdis.Remocra.model.OldebProprietaire', {
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
        name: 'adresse',
        type: 'string',
        convert: function(newValue, model) {
            return model.get('numVoie') + '  ' + model.get('voie');
        }
    }, {
        name: 'lieuDit',
        type: 'string'
    }, {
        name: 'codePostal',
        type: 'string'
    }, {
        name: 'ville',
        type: 'string'
    }, {
        name: 'pays',
        type: 'string'
    }, {
        name: 'numVoie',
        type: 'string'
    }, {
        name: 'voie',
        type: 'string'
    }

    ],
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../proprietaire')
    }
});