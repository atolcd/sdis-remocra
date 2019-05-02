Ext.require('Ext.form.FieldSet');
Ext.define('Sdis.Remocra.features.crises.bloc.ActivationCrise', {

    extend: 'Ext.form.FieldSet',
    title: 'Activation de crise',
    id: 'activationCrise',
    alias: 'widget.activationCrise',
     height: 120,
    layout: 'vbox',
    defaults: {
        labelAlign: 'right'
    },

    items: [{
        xtype: 'fieldcontainer',
        itemId: 'containDebCrise',
        items: [{
           xtype: 'datefield',
           fieldLabel: 'Le * ',
           labelWidth: 30,
           labelAlign: 'left',
           name: 'dateDebutCrise',
           value: new Date(),
           maxValue: new Date(),
           format: 'd/m/Y',
           allowBlank: false
        },{
           xtype: 'timefield',
           name: 'timeDebutCrise',
           fieldLabel: 'à * ',
           labelWidth: 30,
           value: new Date(),
           format: 'H:i',
           increment: 15,
           allowBlank: false
        }]
    }]
});