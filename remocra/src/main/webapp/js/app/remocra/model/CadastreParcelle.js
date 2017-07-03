Ext.require('Sdis.Remocra.model.CadastreSection');
Ext.define('Sdis.Remocra.model.CadastreParcelle', {
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
    }, {
        name: 'jsonGeometrie',
        type: 'auto',
        persist: false,
        useNull: true
    } ],
    associations: [{
        type: 'belongsTo',
        model: 'Sdis.Remocra.model.CadastreSection',
        associationKey: 'section',
        getterName: 'getSection',
        setterName: 'setSection',
        associatedName: 'CadastreSection',
        persist: true
    } ],

    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../cadastreparcelle')
    }

});