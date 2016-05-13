Ext.require('Sdis.Remocra.model.TypeReference');
Ext.require('Sdis.Remocra.network.RemocraRest');

Ext.define('Sdis.Remocra.model.TypeHydrantDiametre', {
    extend: 'Sdis.Remocra.model.TypeReference',

    associations: [{
        type: 'hasMany',
        model: 'Sdis.Remocra.model.TypeHydrantNature',
        associationKey: 'natures',
        name: 'natures',
        associatedName: 'natures',
        persist: true
    }],

    proxy: {
        type: 'remocra.rest',
        url: Sdis.Remocra.util.Util.withBaseUrl('../typehydrantdiametres')
    },

    hasNature: function(natureId) {
        return this.natures().getById(natureId) != null;
    }
});