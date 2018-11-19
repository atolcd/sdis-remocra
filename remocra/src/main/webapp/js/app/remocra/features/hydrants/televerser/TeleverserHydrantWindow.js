Ext.require('Sdis.Remocra.widget.WidgetFactory');
Ext.require('Sdis.Remocra.model.Empty');
Ext.require('Ext.data.reader.Xml');

Ext.define('Sdis.Remocra.features.hydrants.televerser.TeleverserHydrantWindow', {
    extend: 'Ext.Window',

    title: 'Téléverser un fichier de visites',
    successMsg: 'Les visites ont été enregistrées.',
    errorMsg: 'Un problème est survenu lors de l\'enregistrement des visites.',
    width: 500,

    initComponent: function () {
        Ext.apply(this, {
            defaults: {
                style: 'margin:10px;'
            },
            modal: true,
            constrain: true,
            autoHeight: true,
            buttonAlign: 'center',
            items: [{
                border: false,
                html: 'Pour déposer un fichier de visites, choisissez votre fichier et cliquez sur le bouton "Envoyer".'
            }, Ext.create('Ext.form.FormPanel', {
                itemId: 'formp',
                border: false,
                defaults: {
                    labelSeparator: '',
                    allowBlank: false
                }, items: Sdis.Remocra.widget.WidgetFactory.createFileField('Fichier de visites', false, null, {
                    itemId: 'depotUpload',
                    name: 'file'
                }),
                reader: new Ext.data.reader.Xml({
                    model: 'Sdis.Remocra.model.Empty',
                    record: 'success'
                }),
                errorReader: new Ext.data.reader.Xml({
                    model: 'Sdis.Remocra.model.Empty',
                    record: 'error'
                })
            })],
            buttons: [{
                xtype: 'button',
                itemId: 'okBtn',
                text: 'Envoyer',
                minWidth: 70,
                listeners: {
                    click: Ext.bind(this.ok, this)
                }
            }, {
                xtype: 'button',
                itemId: 'cancelBtn',
                text: 'Annuler',
                minWidth: 70,
                listeners: {
                    click: Ext.bind(this.cancel, this)
                }
            }], keys: [{
                key: Ext.EventObject.ENTER,
                fn: Ext.bind(this.ok, this)
            }]
        });

        this.callParent(arguments);
    },

    cancel: function () {
        this.close();
    },

    ok: function () {
        var form = this.getComponent('formp').getForm();
        if (!form.isValid()) {
            return;
        }
        var url = Sdis.Remocra.util.Util.withBaseUrl('../xml/hydrants/file?v=2');
        form.submit({
            url: url,
            waitMsg: 'Envoi du fichier...',
            success: function(form, action) {
                var success = action.response.responseXML.getElementsByTagName('success');
                Ext.Msg.alert(this.title, success && success.length>0 ? this.successMsg : this.errorMsg);
                this.cancel();
            }, failure: function(form, action) {
                Ext.Msg.alert(this.title, this.errorMsg);
                this.cancel();
            }, scope: this
        });
    }
});