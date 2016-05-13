Ext.require('Ext.form.FieldSet');

Ext.define('Sdis.Remocra.features.hydrants.bloc.GestionnairePena', {

    extend: 'Ext.form.FieldSet',
    title: 'Gestionnaire',
    alias: 'widget.hydrant.gestionnairepena',

    defaults: {
        anchor: '100%',
        labelAlign: 'right',
        labelWidth: 150
    },

    items: [{
        xtype: 'combo',
        fieldLabel: 'Domaine',
        name: 'domaine',
        store: 'TypeHydrantDomaine',
        displayField: 'nom',
        valueField: 'id',
        queryMode: 'local',
        forceSelection: true
    },{
        xtype: 'textfield',
        fieldLabel: 'Gestionnaire point d\'eau',
        name: 'gestPointEau'
    }]
});