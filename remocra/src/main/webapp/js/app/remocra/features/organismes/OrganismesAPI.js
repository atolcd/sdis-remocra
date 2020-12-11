Ext.require('Sdis.Remocra.widget.WidgetFactory');

Ext.define('Sdis.Remocra.features.organismes.OrganismesAPI', {
    extend: 'Ext.form.Panel',
    alias: 'widget.crOrganismesAPI',

    title: 'Génération d\'accès à l\'API',
    id: 'clefAPI',
    border: false,

    code: null,

    getPromise: function (code) {
        return new Promise(function (resolve, reject) {
            //On vérifie que le code du lien est toujours valide
            Ext.Ajax.request({
                scope: this,
                method: 'GET',
                url: Sdis.Remocra.util.Util.withBaseUrl('../accesAPIOrganisme/'+code),
                callback: function(param, success, response) {
                    if(success){
                        var res = Ext.decode(response.responseText);
                        resolve(res.data);
                    }
                }
            });
        });
    },

    initComponent: function() {
        // Récupération du code
        this.code = this.extraParams.code;
        if (this.code==null) {
            Ext.apply(this, {
                items: 'Un paramètre est manquant...'
            });
            this.callParent(arguments);
            return;
        }

        var me = this,
            promise = me.getPromise(this.code);
        me.title = "Génération d'accès à l'API";
        promise.then(function (value) {
            //Si le lien est toujours valide
            if(value){
                me.add(
                {
                    xtype: 'label',
                    id: 'pwdSuccess',
                    text: 'Le mot de passe a bien été enregistré.',
                    style: 'font-size:14px;font-weight:bold;margin-left:20px;',
                    hidden: true
                },
                {
                    xtype: 'label',
                    id: 'labelPwd',
                    text: 'Le mot de passe doit contenir au moins 9 caractères (lettres et chiffres)',
                    style: 'font-size:10px;'
                },
                {
                    xtype: 'textfield',
                    name: 'pwd1',
                    id: 'pwdApi1',
                    inputType: 'password',
                    fieldLabel: 'Mot de passe*'
                },
                {
                    xtype: 'textfield',
                    name: 'pwd2',
                    id: 'pwdApi2',
                    inputType: 'password',
                    fieldLabel: 'Confirmez le mot de passe*'
                },
                {
                    xtype: 'button',
                    style: 'margin-left:180px;',
                    id: 'validPwd',
                    text: 'Valider',
                    minWidth: 70,
                    listeners: {
                        click: function(){
                            //On vérifie que les champs sont bien remplis
                            var pwd1 = Ext.getCmp('pwdApi1').getValue();
                            var pwd2 = Ext.getCmp('pwdApi2').getValue();

                            if(pwd1 != null && pwd2 != null && pwd1 === pwd2){
                                //On envoi le mot de passe vers le serveur
                                Ext.Ajax.request({
                                    scope: this,
                                    method: 'POST',
                                    url: Sdis.Remocra.util.Util.withBaseUrl('../accesAPIOrganisme/password/'+me.code),
                                    headers: {'pwd': pwd1},
                                    callback: function(param, success, response) {
                                        if(success){
                                            me.hidePwdForm();
                                            Ext.getCmp('pwdSuccess').show();
                                        } else {
                                            //Si le mot de passe n'est pas assez complexe
                                            Ext.MessageBox.show({
                                                title: 'Erreur',
                                                msg: 'Le mot de passe n\'est pas assez complexe',
                                                buttons: Ext.Msg.OK,
                                                icon: Ext.MessageBox.ERROR
                                             });
                                        }
                                    }
                                });
                            } else {
                                Ext.MessageBox.show({
                                    title: 'Erreur',
                                    msg: 'Les mots de passes ne sont pas identiques',
                                    buttons: Ext.Msg.OK,
                                    icon: Ext.MessageBox.ERROR
                                 });
                            }
                        }
                    }
                });
            } else {
                me.add({
                    xtype: 'label',
                    text: 'Le lien est expiré. Veuillez vous rapprocher de l\'administrateur REMOcRA afin de générer un nouveau lien.',
                    style: 'font-size:18px;font-weight:bold;'
                });
            }
        });
        this.callParent(arguments);
    },

    hidePwdForm: function(){
        Ext.getCmp('pwdApi1').hide();
        Ext.getCmp('pwdApi2').hide();
        Ext.getCmp('labelPwd').hide();
        Ext.getCmp('validPwd').hide();
    }


});