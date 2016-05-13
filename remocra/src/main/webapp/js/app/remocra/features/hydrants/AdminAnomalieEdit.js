Ext.define('Sdis.Remocra.features.hydrants.AdminAnomalieEdit', {
    extend: 'Ext.window.Window',
    title: 'Modification',
    alias: 'widget.adminanomalieedit',

    width: 700,
    modal: true,
    padding: 10,

    items: {
        padding: 10,
        xtype: 'form',
        defaults: {
            labelAlign: 'right',
            labelWidth: 80,
            anchor: '100%',
            margin: '0 0 10 0'
                
        },
        border: false,
        layout: 'anchor',
        items: [{
            fieldLabel: 'Anomalie',
            xtype: 'textfield',
            name: 'nom'
        },{
            fieldLabel: 'Critère',
            xtype: 'combo',
            name: 'critere',
            value: null,
            displayField: 'nom',
            valueField: 'id',
            store: 'TypeHydrantCritere',
            queryMode: 'local'
        }]
    },

    initComponent: function() {

        this.buttons = [{
            text: 'Valider',
            scope: this,
            handler: function() {
                this.fireEvent('valid', this);
            }
        },{
            text: 'Annuler',
            scope: this,
            handler: function() {
                this.close();
            }
        }];
        this.callParent(arguments);
    },

    loadRecord: function(record) {
        this.down('form').loadRecord(record);
    },
    
    getForm : function() {
        return this.down('form').getForm();
    }

});