Ext.require('Sdis.Remocra.model.OldebProprietaire');
Ext.require('Sdis.Remocra.model.TypeOldebResidence');

Ext.define('Sdis.Remocra.model.OldebPropriete', {
    extend: 'Ext.data.Model',
    fields: [{
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'proprietaire',
        type: 'fk',
        useNull: true
    }, {
        name: 'oldeb',
        type: 'fk',
        useNull: true
    }, {
        name: 'residence',
        type: 'fk',
        useNull: true
    } ],
    associations: [{
        type: 'belongsTo',
        model: 'Sdis.Remocra.model.OldebProprietaire',
        associationKey: 'proprietaire',
        name: 'proprietaire',
        getterName: 'getProprietaire',
        setterName: 'setProprietaire',
        persist: true
    } ],
    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../propriete')
    }
});