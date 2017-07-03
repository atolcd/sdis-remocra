Ext.require('Sdis.Remocra.model.Commune');
Ext.define('Sdis.Remocra.model.CadastreSection', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'geometrie',
        type: 'string'
    }, {
        name: 'numero',
        type: 'string'
    }

    ],
    associations: [{
        type: 'belongsTo',
        model: 'Sdis.Remocra.model.Commune',
        associationKey: 'commune',
        getterName: 'getCommune',
        setterName: 'setCommune',
        associatedName: 'Commune',
        persist: true
    } ],

    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../cadastresection')
    }

});