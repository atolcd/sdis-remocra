Ext.require('Ext.form.FieldSet');
Ext.define('Sdis.Remocra.features.crises.bloc.ClotureCrise', {

    extend: 'Ext.form.FieldSet',
    title: 'Cloture de crise',
    id: 'clotureCrise',
    alias: 'widget.clotureCrise',
     height: 120,
    layout: 'vbox',
    defaults: {
        labelAlign: 'right'
    },

    items: [{
        xtype: 'fieldcontainer',
        itemId: 'containFinCrise',
        items: [{
           xtype: 'datefield',
           fieldLabel: 'Le * ',
           labelWidth: 30,
           labelAlign: 'left',
           name: 'dateFinCrise',
           disabled: true,
           value: null,
           maxValue: new Date(),
           format: 'd/m/Y',
           allowBlank: false
            }, {
             xtype: 'timefield',
             name: 'timeFinCrise',
             fieldLabel: 'à * ',
             labelWidth: 30,
             value: null,
             format: 'H:i',
             increment: 15,
             disabled: true,
             allowBlank: false
            }]
    }]
});