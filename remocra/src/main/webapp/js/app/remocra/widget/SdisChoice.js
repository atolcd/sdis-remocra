Ext.require('Ext.window.Window');

Ext.define('Sdis.Remocra.widget.SdisChoice', {
    extend: 'Ext.window.Window',
    alias: 'widget.sdischoice',
    width: 400,
    height: 150,
    modal: true,
    layout: 'form',
    bodyPadding: 20,
    minButtonWidth: 100,
    buttonAlign: 'center',

    initComponent: function() {

        this.items = [];
        // Libellé éventuel
        if (this.explanationsConfig) {
            this.items.push(this.explanationsConfig);
        }
        this.items.push(Ext.apply({}, this.cboConfig, {
            xtype: 'combo',
            queryMode: 'local',
            displayField: 'nom',
            valueField: 'id',
            allowBlank: false
        }));

        this.buttons = [{
            text: this.okLbl||'Valider',
            itemId: 'ok',
            scope: this,
            handler: this.onOkButton
        },{
            text: this.cancelLbl||'Annuler',
            scope: this,
            handler: function() {
                this.close();
            }

        }];
        this.callParent(arguments);
        this.combo = this.down('combo');
    },

    onOkButton: function() {
        if (this.combo.isValid()) {
            this.fireEvent('valid', this.combo.getValueModel() );
            this.close();
        }
    }
});