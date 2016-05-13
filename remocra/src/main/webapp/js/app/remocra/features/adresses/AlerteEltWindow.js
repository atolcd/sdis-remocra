Ext.ns('Sdis.Remocra.features.adresses');

Ext.require('Sdis.Remocra.widget.WidgetFactory');
Ext.require('Sdis.Remocra.network.TypeAlerteAnoStore');

Ext.define('Sdis.Remocra.features.adresses.AlerteEltWindow', {
    extend: 'Ext.Window',

    title: 'Description de l\'élément',
    width: 500,

    // L'AlerteElt dans le cas d'un modification
    alerteElt: null,
    
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
                click: Ext.bind(this.close, this)
            }
        }));
        
        // -- PRESENTATION
        var presPanel = {
            border: false,
            html: '<p style="font-style:italic">Indiquez les anomalies constatées et une description avant de valider.</p>'
        };
        
        var anomaliesLbl = { xtype: 'label', text: 'Anomalies rencontrées' };
        var anomalies = {
            itemId: 'anomalies',
            xtype: 'checkboxgroup',
            fieldLabel: ' ', labelWidth: 50,
            columns: 1, vertical: true,
            allowBlank: false,
            items: []
        };
        var anos = Sdis.Remocra.network.TypeAlerteAnoStore;
        anos.each(function(item) {
            anomalies.items.push({ boxLabel: item.get('nom'), itemId: item.get('code'), inputValue: item.get('code'), typeAlerteAno: item});
        }, this);
        
        var descriptionLbl = {xtype: 'label', text: 'Commentaire'};
        var description = f.createTextArea(null, true, 10, '', {itemId: 'commentaire', width: 465});
        
        var formP = Ext.create('Ext.form.FormPanel', {
            itemId: 'formp',
            border: false,
            defaults: {labelSeparator: '', allowBlank: false},
            items: [anomaliesLbl, anomalies, descriptionLbl, description]
        });
        
        Ext.apply(this, {
            defaults: {style: 'margin:10px;'},
            modal: true,
            constrain: true,
            autoHeight: true,
            buttonAlign: 'center',
            items: [presPanel, formP],
            buttons: actionButtons,
            keys: [{
                key: Ext.EventObject.ENTER,
                fn: Ext.bind(this.ok, this)
            }]
        });
        
        this.callParent(arguments);
        
        this.fillData();
    },
    
    ok: function() {
        var form = this.getComponent('formp').getForm();
        if (!form.isValid()) {
            return;
        }
        this.alerteElt.set('commentaire', this.getComponent('formp').getComponent('commentaire').getValue());
        
        var alerteAnos = this.alerteElt.alerteEltAnos();
        alerteAnos.removeAll();
        var anomalies = this.getComponent('formp').getComponent('anomalies').items;
        anomalies.each(function(item) {
            if (item.checked) {
                var alerteEltAno = Ext.create('Sdis.Remocra.model.AlerteEltAno');
                alerteEltAno.setTypeAlerteAno(item.typeAlerteAno);
                alerteAnos.add(alerteEltAno);
            }
        }, this);
        
        // Suite : définir la géométrie et le sousTypeAlerteElt
        this.fireEvent('ok', this.alerteElt);
    },
    
    fillData: function() {
        if (!this.alerteElt) {
            // Mode création
            this.alerteElt = Ext.create('Sdis.Remocra.model.AlerteElt');
        } else {
            
            // Mode édition
            this.getComponent('formp').getComponent('commentaire').setValue(this.alerteElt.get('commentaire'));
            
            var anomalies = this.getComponent('formp').getComponent('anomalies');
            
            var alerteEltAnos = this.alerteElt.alerteEltAnos();
            alerteEltAnos.each(function(item) {
                var typeAlerteAno = item.getTypeAlerteAno();
                var itemCmp = anomalies.getComponent(typeAlerteAno.get('code'));
                itemCmp.setValue(true);
            }, this);
        }
    }
});