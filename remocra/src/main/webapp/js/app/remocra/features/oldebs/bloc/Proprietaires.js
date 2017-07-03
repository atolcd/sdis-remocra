Ext.require('Ext.window.Window');
Ext.require('Ext.grid.Panel');
Ext.require('Sdis.Remocra.features.oldebs.bloc.GridProprietaires');
Ext.define('Sdis.Remocra.features.oldebs.bloc.Proprietaires', {
    extend: 'Ext.window.Window',
    alias: 'widget.oldebs.proprietaire',

    title: 'Propriétaires',
    width: 500,
    height: 300,
    modal: true,
    layout: 'form',
    minButtonWidth: 100,
    buttonAlign: 'center',

    initComponent: function() {
        var nom = Ext.getCmp('nomProprietaire');
        this.items = [];
        this.items.push({
            xtype: 'fieldcontainer',
            layout: 'hbox',
            items: [{

                xtype: 'displayfield',
                value: '<i>Si le propriétaire de la parcelle est listé dans le tableau ci-dessous,<br/>'
                        + 'merci de le sélectionner et de cliquer sur "Sélectionner". Dans le cas contraire,<br/>' + 'cliquer sur "Annuler</i>"',
                padding: 10
            } ]
        }, {
            xtype: 'oldeb.gridProprietaires',
            id: 'gridProprio',
            store: {

                xtype: 'crOldebProprietaire',
                autoLoad: false,
                model: 'Sdis.Remocra.model.OldebProprietaire',
                remoteFilter: true,
                filters: [{
                    property: "nomProprietaire",
                    value: nom.getValue()
                } ]
            }
        });

        this.buttons = [{
            text: this.okLbl || 'Selectionner',
            itemId: 'ok',
            scope: this,
            handler: this.onOkButton
        }, {
            text: this.cancelLbl || 'Annuler',
            scope: this,
            handler: function() {
                this.fireEvent('cancel');
                this.close();
            }
        } ];
        this.callParent(arguments);
        this.grid = this.down('#gridProprio');
    },

    onOkButton: function() {
        this.fireEvent('valid', this.grid.getSelectionModel().getSelection(0));
        this.close();
    }
});
