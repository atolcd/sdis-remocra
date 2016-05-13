Ext.require('Sdis.Remocra.widget.WidgetFactory');

Ext.define('Sdis.Remocra.features.profil.ResetPassword', {
    extend: 'Ext.form.Panel',
    alias: 'widget.crResetPassword',

    title: 'Réinitialisation du mot de passe',
    id: 'resetpassword',
    border: false,
    
    /**
     * @cfg {String} code
     * Code de la demande (obligatoire)
     */
    code: null,
    
    initComponent: function() {
        // Récupération du code
        this.code = this.extraParams.reset;
        if (this.code==null) {
            Ext.apply(this, {
                items: 'Un paramètre est manquant...'
            });
            this.callParent(arguments);
            return;
        }
        
        var f = Sdis.Remocra.widget.WidgetFactory;
        
        var presHtml = 'Vous êtes sur le point de réinitialiser le mot de passe du compte.<br/>Après la validation, vous recevrez un courriel de confirmation.';
        var presPanel = { cls: 'auth-pres', html: presHtml, border: false, margin: 10 };
        var errorPanel = { itemId: 'errorPanel', cls: 'error', hidden: true, html: '', border: false, margin: 10 };
        
        // Mot de passe
        var pwd1AbsId = 'pwd1-'+Ext.id();
        var pwd1 = f.createPasswordField('Nouveau mot de passe', false, '', {id: pwd1AbsId, itemId: 'pwd1'});
        var pwd2 = f.createPasswordField('Confirmation', false, '', {itemId: 'pwd2', otherPwdFieldId: pwd1AbsId});
        var changeMdpFs = f.createLightFS('changeMdpFs', [pwd1, pwd2], {title: ''});
        
        Ext.apply(this, {
            items: [presPanel, changeMdpFs, errorPanel],
            fbar: [{
                style: 'margin-left:20px;',
                itemId: 'okBtn',
                text: 'Valider',
                minWidth: 70,
                listeners: {
                    click: this.saveData,
                    scope: this
                }
            }],
            buttonAlign: 'left'
        });
        
        this.callParent(arguments);
    },
    
    showHideErrorMsg: function(msg) {
        var errorPanel = this.getComponent('errorPanel');
        if (msg) {
            errorPanel.update('<div class="error" style="font-style:italic;">'+msg+'</div>');
            errorPanel.setVisible(true);
        } else {
            errorPanel.setVisible(false);
        }
    },
    
    saveData: function() {
        if (!this.getForm().isValid()) {
            this.showHideErrorMsg('Les mots de passe saisis ne sont pas valides.<br/>Veuillez corriger les erreurs avant de valider.');
            return;
        }
        this.showHideErrorMsg();
        
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl('../utilisateurs/resetpwd'),
            method: 'POST',
            params: {
                pwd: this.queryById('pwd1').getValue(),
                code: this.code
            },
            scope: this,
            callback: function(options, success, response) {
                var jsResp = Ext.decode(response.responseText);
                if (!success || !jsResp.success) {
                    Ext.MessageBox.show({
                        title: this.title,
                        msg: 'Impossible de modifier le mot de passe avec les informations fournies.',
                        buttons: Ext.Msg.OK,
                        icon: Ext.MessageBox.ERROR
                     });
                } else {
                    Ext.MessageBox.show({
                        title: this.title,
                        msg: 'Le mot de passe a été modifié. Vous devriez recevoir une confirmation par courriel.',
                        buttons: Ext.Msg.OK,
                        icon: Ext.MessageBox.INFO,
                        fn: function() {
                            Sdis.Remocra.util.Util.changeHash("");
                            Sdis.Remocra.util.Util.forceReload();
                        }
                    });
                }
            }
        });
    }
});