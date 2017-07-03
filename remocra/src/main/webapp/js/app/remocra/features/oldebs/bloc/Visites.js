Ext.require('Ext.form.FieldSet');
Ext.require('Sdis.Remocra.features.oldebs.bloc.GridVisites');
Ext.require('Sdis.Remocra.features.oldebs.bloc.DetailsVisites');

Ext.define('Sdis.Remocra.features.oldebs.bloc.Visites', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.oldeb.visites',

    deferredRender: false,
    defaults: {
        anchor: '100%',
        labelAlign: 'right',
        labelWidth: 120
    },

    items: [{
        xtype: 'oldeb.GridVisites'
    } ],

    initComponent: function() {
        this.callParent(arguments);
    }

});