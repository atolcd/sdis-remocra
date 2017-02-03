Ext.require('Sdis.Remocra.network.RemocraRest');
Ext.require('Sdis.Remocra.model.Tournee');
Ext.require('Sdis.Remocra.model.TypeHydrantNature');
Ext.require('Sdis.Remocra.model.TypeHydrantDomaine');
Ext.require('Sdis.Remocra.model.TypeHydrantAnomalie');

Ext.define('Sdis.Remocra.model.HydrantDocument', {
    extend: 'Ext.data.Model',

    fields: [ {
        name: 'id',
        type: 'int',
        useNull: true
    }, {
        name: 'titre',
        type: 'string'
    }, {
        name: 'code',
        type: 'string'
    } ]

});