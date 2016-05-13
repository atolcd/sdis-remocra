Ext.ns('Sdis.Remocra.model');

Ext.require('Ext.data.Model');

Ext.define('Sdis.Remocra.model.Traitements', {
    extend: 'Ext.data.Model',    
    fields: [
             { name: 'idmodele', type: 'integer'},
             { name: 'nom', type: 'string' },
             { name: 'description', type: 'string' }
         ]
});