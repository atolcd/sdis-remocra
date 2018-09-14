Ext.define('Sdis.Remocra.features.hydrants.AdminAnomalieFiche', {
    extend: 'Ext.window.Window',
    title: 'Modification',
    alias: 'widget.adminanomaliefiche',

    width: 700,
    height: 350,
    modal: true,
    layout: 'form',

    defaults: {
        labelAlign: 'right',
        labelWidth: 120
    },

    initComponent: function() {
        this.items = [{
            fieldLabel: 'Anomalie',
            xtype: 'displayfield',
            name: 'anomalie',
            value: '-'
        },{
            fieldLabel: 'Nature',
            xtype: 'displayfield',
            name: 'nature',
            value: '-'
        },{
            fieldLabel: 'Valeur opérationelle',
            xtype: 'radiogroup',
            items: [{
                boxLabel: '-',
                name: 'op',
                inputValue: '-1',
                checked: true
            },{
                boxLabel: '0',
                name: 'op',
                inputValue: '0'
            },{
                boxLabel: '1',
                name: 'op',
                inputValue: '1'
            },{
                boxLabel: '2',
                name: 'op',
                inputValue: '2'
            },{
                boxLabel: '3',
                name: 'op',
                inputValue: '3'
            },{
                boxLabel: '4',
                name: 'op',
                inputValue: '4'
            },{
                boxLabel: '5',
                name: 'op',
                inputValue: '5'
            }]
        },{
            fieldLabel: 'Valeur HBE',
            xtype: 'radiogroup',
            items: [{
                boxLabel: '-',
                name: 'hbe',
                inputValue: '-1',
                checked: true
            },{
                boxLabel: '0',
                name: 'hbe',
                inputValue: '0'
            },{
                boxLabel: '1',
                name: 'hbe',
                inputValue: '1'
            },{
                boxLabel: '2',
                name: 'hbe',
                inputValue: '2'
            },{
                boxLabel: '3',
                name: 'hbe',
                inputValue: '3'
            },{
                boxLabel: '4',
                name: 'hbe',
                inputValue: '4'
            },{
                boxLabel: '5',
                name: 'hbe',
                inputValue: '5'
            }]
        }];

        var chks = [];
        Ext.getStore('TypeHydrantSaisie').each(function(rec) {
            if (rec.get('code') != 'LECT') {
                chks.push({
                    boxLabel: rec.get('nom'),
                    name: 'saisie',
                    inputValue: rec.getId(),
                    rec: rec
                });
            }
        });

        this.items.push({
            fieldLabel: 'Type de saisie',
            xtype: 'checkboxgroup',
            vertical: true,
            columns: 1,
            items: chks
        });

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

    getValTerrestre: function() {
        return this.down('radio[name=op][checked=true]').getModelData().op;
    },

    getValHbe: function() {
        return this.down('radio[name=hbe][checked=true]').getModelData().hbe;
    },

    getSaisie: function() {
        var chks = this.query('checkbox[name=saisie]'), saisies = [];
        Ext.Array.each(chks, function(chk) {
            if (chk.checked) {
                saisies.push(chk.rec);
            }
        });
        return saisies;
    }

});
