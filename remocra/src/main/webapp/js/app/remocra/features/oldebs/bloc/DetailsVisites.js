Ext.require('Ext.form.FieldSet');
Ext.require('Sdis.Remocra.features.oldebs.bloc.Observations');
Ext.require('Sdis.Remocra.features.oldebs.bloc.Suites');
Ext.require('Sdis.Remocra.features.oldebs.bloc.EnteteVisite');
Ext.require('Sdis.Remocra.features.oldebs.bloc.Documents');
Ext.require('Sdis.Remocra.features.oldebs.bloc.Anomalies');
Ext.require('Sdis.Remocra.features.oldebs.bloc.GridSuites');

Ext.define('Sdis.Remocra.features.oldebs.bloc.DetailsVisites', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.oldeb.DetailsVisites',
    title: 'Details de visite',

    id: 'detailVisite',
    deferredRender: false,

    items: [{
        xtype: 'oldebs.entetevisite'
    }, {
        xtype: 'tabpanel',
        plain: true,
        items: [{
            title: 'Anomalies',
            xtype: 'oldeb.anomalies'

        }, {
            title: 'Suites',
            xtype: 'oldeb.GridSuites'

        }, {
            title: 'Documents',
            xtype: 'oldeb.documents'

        }, {
            title: 'Observations',
            xtype: 'oldeb.observations'

        } ]

    } ],

    initComponent: function() {
        this.callParent(arguments);
    }
});