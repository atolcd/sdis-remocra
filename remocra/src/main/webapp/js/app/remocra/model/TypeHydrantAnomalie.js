Ext.require('Sdis.Remocra.model.TypeReference');
Ext.require('Sdis.Remocra.network.RemocraRest');
Ext.require('Sdis.Remocra.model.TypeHydrantAnomalieNature');

Ext.define('Sdis.Remocra.model.TypeHydrantAnomalie', {
    extend: 'Sdis.Remocra.model.TypeReference',

    fields: [{
        name: 'actif',
        type: 'bool',
        defaultValue: true
    },{
        name: 'commentaire',
        type: 'string'
    },{
        name: 'critere',
        type: 'fk'
    },{
        name: 'critere_nom',
        type: 'string',
        mapping:function(data, reader) {
            if (data['critere'] ) {
                return data['critere']['nom'];
            }
            return '-';
        },
        persist: false
    }],
    
    associations: [{
        type: 'hasMany',
        model: 'Sdis.Remocra.model.TypeHydrantAnomalieNature',
        associationKey: 'anomalieNatures',
        name: 'anomalieNatures',
        associatedName: 'anomalieNatures',
        foreignKey: 'anomalie',
        persist: true
    }],

    proxy: {
        type: 'remocra.rest',
        limitParam: undefined,
        url: Sdis.Remocra.util.Util.withBaseUrl('../typehydrantanomalies')
    },

    getInfoByNature: function(nature) {
        return this.anomalieNatures().findRecord('nature', nature);
    }
});