Ext.require('Ext.form.FieldSet');

Ext.define('Sdis.Remocra.features.hydrants.bloc.Tracabilite', {

    extend: 'Ext.form.FieldSet',
    title: 'Traçabilité',
    alias: 'widget.hydrant.tracabilite',

    defaults: {
        columnWidth: 0.5,
        layout: 'anchor',
        border: false,
        defaults: {
            anchor: '90%',
            labelAlign: 'right',
            margin: '0 0 10 0'
        }
    },

    layout: 'column',

    items: [{
        items: [{
            xtype: 'displayfield',
            fieldLabel: 'CIS / Commune',
            name: 'CISCommune'
        },{
            xtype: 'textfield',
            fieldLabel: 'Agent 1',
            name: 'agent1'
        },{
            xtype: 'checkbox',
            name: 'verification',
            fieldLabel: 'Vérification'
        }]
    },{
        items: [{
            xtype: 'datefield',
            fieldLabel: 'Date de visite',
            format: 'd/m/Y',
            name: 'dateSaisie',
            value: new Date()
        },{
            xtype: 'textfield',
            fieldLabel: 'Agent 2',
            name: 'agent2'
        }]
    }]
});