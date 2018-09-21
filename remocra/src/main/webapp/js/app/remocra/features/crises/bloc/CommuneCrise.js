Ext.require('Ext.form.FieldSet');
Ext.require('Sdis.Remocra.features.crises.bloc.GridCommuneCrise');
Ext.define('Sdis.Remocra.features.crises.bloc.CommuneCrise', {

    extend: 'Ext.form.FieldSet',
    title: 'Communes concernées',
    id: 'communeConcernee',
    alias: 'widget.communeCrise',

    layout: 'vbox',
    defaults: {
        labelAlign: 'right'
    },

    items: [{
        xtype: 'fieldcontainer',
        layout: 'hbox',
         items: [{
           xtype: 'combo',
           displayField: 'nom',
           valueField: 'id',
           forceSelection: true,
           hideTrigger: true,
           minChars: 2,
           mode: 'remote',
           typeAhead: true,
           //store: {model: 'Sdis.Remocra.model.Hydrant', autoLoad: true},
           name: 'communeCrise',
           labelAlign: 'left',
           margin: '0 10 0 0',
           width: 350,
           labelWidth: 130,
           store: {
                type: 'crCommune',
                filters: [{
                   property: 'zoneCompetence',
                   value: ' '
                }]
           }
           }, {
           xtype: 'button',
           text: 'Ajouter',
           itemId:'addCommuneCrise'
         }]
    }, {
            xtype: 'fieldcontainer',
            layout: 'hbox',
            items: [{
        xtype: 'gridCommuneCrise',
        itemId: 'gridCommuneCrise',
        title: 'Commune concernées',
        height: 180,
        width: 350,
        hideHeaders: true,
        margin: '0 10 0 0'
        }, {
           xtype: 'button',
           text: 'Retirer',
           itemId:'deleteCommuneCrise'
         }]
    }]
});


