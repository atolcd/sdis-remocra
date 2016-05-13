Ext.require('Ext.form.FieldSet');

Ext.define('Sdis.Remocra.features.hydrants.bloc.VerifHydrauliquePena', {

    extend: 'Ext.form.FieldSet',
    title: 'Vérification hydraulique',
    alias: 'widget.hydrant.verifhydrauliquepena',

    defaults: {
        anchor: '100%',
        labelAlign: 'right',
        labelWidth: 150
    },

    items: [{
        xtype: 'numberfield',
        fieldLabel: 'Capacité théorique (m³)',
        hideTrigger: true,
        name: 'capacite'
    },{
        xtype: 'combo',
        fieldLabel: 'Vol. constaté',
        name: 'volConstate',
        store: 'TypeHydrantVolConstate',
        displayField: 'nom',
        valueField: 'id',
        queryMode: 'local',
        forceSelection: true
    }]
});