Ext.require('Ext.data.Model');

Ext.define('Sdis.Remocra.model.ParamConf', {
    extend: 'Ext.data.Model',
    
    fields: [
         {name: 'cle', type: 'string'},
         {name: 'valeur', type: 'string'},
         {name: 'clDisplay', type: 'string'},
         {name: 'description', type: 'string'}
    ],
    
    validations: [{
        type: 'length', field: 'cle',  min: 1
    }]
});