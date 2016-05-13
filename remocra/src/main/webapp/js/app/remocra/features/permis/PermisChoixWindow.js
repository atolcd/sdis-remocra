Ext.require('Sdis.Remocra.widget.WidgetFactory');

Ext.define('Sdis.Remocra.features.permis.PermisChoixWindow', {
    extend: 'Ext.Window',

    title: 'Choix d\'un permis',
    width: 500,

    data: null,
    
    initComponent: function() {
        var f = Sdis.Remocra.widget.WidgetFactory;
        
        // -- BOUTONS
        var actionButtons = [];
        actionButtons.push(new Ext.Button({
            itemId : 'okBtn',
            text : 'Valider',
            minWidth  : 70,
            listeners: {
                click: Ext.bind(this.ok, this)
            }
        }));
        actionButtons.push(new Ext.Button({
            itemId : 'cancelBtn',
            text : 'Annuler',
            minWidth : 70,
            listeners: {
                click: Ext.bind(this.fireEvent, this, ['cancel', this])
            }
        }));
        
        // -- PRESENTATION
        var presPanel = {
            border: false,
            html: '<p>Plusieurs permis ont étés trouvés à la position indiquée.<br/>Veuillez déterminer le permis à sélectionner :</p>'
        };
        
        var combo = {
            itemId : 'permis',
            xtype: 'combo',
            style: 'margin:10px;',
            width: 470,
            queryMode: 'local', valueField: 'id', displayField: 'display',
            editable: false,
            store: Ext.create('Ext.data.Store', {
                model: 'Sdis.Remocra.model.Permis',
                data: this.data
            }),
            value: this.data[0].id
        };
        
        Ext.apply(this, {
            defaults: {style: 'margin:10px;'},
            modal: true,
            constrain: true,
            autoHeight: true,
            buttonAlign: 'center',
            items: [presPanel, combo],
            buttons: actionButtons,
            keys: [{
                key: Ext.EventObject.ENTER,
                fn: Ext.bind(this.ok, this)
            }]
        });
        
        this.callParent(arguments);
    },
    
    ok: function() {
        var permis = this.getComponent('permis').getValueModel();
        if (permis) {
            this.fireEvent('ok', this, permis);
        }
    }
});