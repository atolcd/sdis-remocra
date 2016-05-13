Ext.require('Sdis.Remocra.widget.WidgetFactory');

Ext.define('Sdis.Remocra.widget.DepotWindow', {
    extend: 'Ext.Window',

    title: 'Déposer une fichier',
    aFileLbl: 'un fichier',
    fileLbl: 'Fichier',
    urlPart: 'urldepotapreciser',
    
    width: 500,
    
    initComponent: function() {
        var f = Sdis.Remocra.widget.WidgetFactory;
        
        // -- BOUTONS
        var actionButtons = [];
        actionButtons.push(new Ext.Button({
            itemId : 'okBtn',
            text : 'Envoyer',
            minWidth  : 70,
            listeners: {
                click: Ext.bind(this.ok, this)
            }
        }));
        actionButtons.push(new Ext.Button({
            itemId : 'cancelBtn',
            text : 'Annuler',
            minWidth  : 70,
            listeners: {
                click: Ext.bind(this.cancel, this)
            }
        }));
        
        // -- PRESENTATION
        var presPanel = {
            border: false,
            html: 'Pour déposer ' + this.aFileLbl + ', choisissez votre fichier et cliquez sur le bouton "Envoyer".'
        };
        var uploadField = f.createFileField(this.fileLbl, false, null, {itemId: 'depotUpload', name: 'depotUpload'});
        var depotPanel = Ext.create('Ext.form.FormPanel', {
            itemId: 'formp',
            border: false,
            defaults: {labelSeparator: '', allowBlank: false},
            items: uploadField
        });
        
        Ext.apply(this, {
            defaults: {style:'margin:10px;'},
            modal: true,
            constrain: true,
            autoHeight: true,
            buttonAlign: 'center',
            items: [presPanel, depotPanel],
            buttons: actionButtons,
            keys: [{
                key: Ext.EventObject.ENTER,
                fn: Ext.bind(this.ok, this)
            }]
        });
        
        this.callParent(arguments);
    },
    
    cancel: function() {
        this.close();
    },
    
    ok: function() {
        var form = this.getComponent('formp').getForm();
        if (!form.isValid()) {
            return;
        }
        var url = Sdis.Remocra.util.Util.withBaseUrl(this.urlPart);
        
        // On fait une première requete pour vérifier les droits. (même URL mais en AJAX)
        Ext.Ajax.request({
            url: url+'rights',
            method: 'POST',
            headers: {
                'Accept' : 'application/json,application/xml',
                'Content-Type' : 'application/json'
            },
            scope: this,
            success: function (response) {
                // POST
                form.submit({
                    url: url,
                    waitMsg: 'Envoi du fichier...',
                    success: function(fp, o) {
                        var msg = o.result && o.result.message ? o.result.message : 'Le fichier a bien été déposé.';
                        Ext.Msg.alert(this.title, msg);
                        this.cancel();
                    },
                    failure: function(fp, o) {
                        var msg = o.result && o.result.message ? ' :<br/>'+o.result.message : '';
                        Ext.Msg.alert(this.title, 'Un problème est survenu lors du dépôt ' + msg + '.');
                        this.cancel();
                    },
                    scope: this
                });
            }
        });
    }
});