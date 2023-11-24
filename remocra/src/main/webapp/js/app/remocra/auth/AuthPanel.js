Ext.ns('Sdis.Remocra.auth');

Ext.require('Sdis.Remocra.model.Organisme');
Ext.require('Sdis.Remocra.model.Utilisateur');

Ext.define('Sdis.Remocra.auth.AuthPanel', {
    extend: 'Ext.Panel',
    requires: [],
    alias: 'widget.remocra.authpanel',

    id: 'authPanel',
    cls: 'remocra-authpanel',

    border: false, defaults: {border: false, style: 'margin:5px;'},
    inAWindow: true,

    initComponent: function () {
        // -- EVENEMENTS
        this.addEvents(
            /**
             * @event response
             * Fires after a validation of responses.
             * @param authPanel
             */
            'ok',
            /**
             * @event cancel
             * Fires after the user clicked to cancel button.
             * @param authPanel
             */
            'cancel'
        );
        var msg = {hidden: true};
        if (this.msg !== false) {
            msg = this.msg || 'Veuillez saisir votre identifiant et votre mot de passe pour vous identifier.';
        }

        // -- PRESENTATION
        var presPanel = {cls: 'auth-pres', html: msg};
        var errorPanel = {itemId: 'errorPanel', cls: 'auth-error', hidden: true, html: ''};

        var userLogin = Ext.util.Cookies.get("loginUser") || '';
        // -- SAISIE
        var formPanel = new Ext.form.FormPanel({
            itemId: 'form',
            defaultType: 'textfield',
            border: false,
            defaults: {labelSeparator: '', allowBlank: false},
            items: [{
                xtype: 'label', text: 'Nom d\'utilisateur', hidden: this.inAWindow !== false
            }, {
                itemId: 'loginF', width: 300,
                fieldLabel: this.inAWindow !== false ? 'Nom d\'utilisateur' : null,
                blankText: 'Veuillez saisir votre identifiant',
                value: userLogin
            }, {
                xtype: 'label', text: 'Mot de passe', hidden: this.inAWindow !== false
            }, {
                itemId: 'passwordF', width: 300,
                fieldLabel: this.inAWindow !== false ? 'Mot de passe' : null,
                inputType: 'password',
                blankText: 'Veuillez saisir votre mot de passe',
                enableKeyEvents: true,
                enterIsSpecial: true,
                scope: this,
                listeners: {
                    'specialkey': function (me, e) {
                        if (e.getKey() === e.ENTER) {
                            this.scope.beforeOk.call(this.scope);
                        }
                    }
                }
            }],

            listeners: {
                render: function () {
                    var fieldFocusId = userLogin !== '' ? 'passwordF' : 'loginF';
                    Ext.Function.defer(function () {
                        this.getComponent('form').getComponent(fieldFocusId).focus();
                    }, 100, this);
                }, scope: this
            },
            keys: [{
                key: Ext.EventObject.ENTER,
                fn: Ext.bind(this.beforeOk, this)
            }]
        });

        // -- BOUTONS
        var actionButtons = [];
        actionButtons.push(new Ext.Button({
            id: 'okBtn' + this.id,
            text: 'S\'identifier',
            scale: this.inAWindow !== false ? 'small' : 'large',
            minWidth: this.inAWindow !== false ? 70 : 120,
            listeners: {
                click: Ext.bind(this.beforeOk, this)
            }
        }));
        if (this.inAWindow !== false) {
            actionButtons.push(new Ext.Button({
                id: 'cancelBtn' + this.id,
                text: 'Fermer',
                minWidth: 70,
                listeners: {
                    click: function () {
                        this.fireEvent('cancel', this);
                    },
                    scope: this
                }
            }));
        }
        actionButtons.push({
            xtype: 'box',
            autoEl: {
                tag: 'a',
                style: 'margin-left:20px;',
                href: '',
                onclick: 'Ext.getCmp(\'' + this.id + '\').passwordForgotten();return false;',
                html: 'Mot de passe oublié'
            }
        });

        Ext.apply(this, {
            items: [presPanel, formPanel, errorPanel],
            buttons: actionButtons,
            buttonAlign: this.inAWindow ? 'center' : 'left'
        });

        this.callParent(arguments);
    },

    // ---
    // ACTIONS
    // ---

    beforeOk: function () {
        var expires = new Date();
        expires.setDate(expires.getDate() + 7); // Expiration à 7 jours
        Ext.util.Cookies.set("loginUser", this.getComponent('form').getComponent('loginF').getValue(), expires);
        this.fireEvent('ok', this);
    },

    // Déplacées dans la fenêtre
    showHideErrorMsg: function (msg) {
        var errorPanel = this.getComponent("errorPanel");
        if (msg) {
            errorPanel.update(msg);
            errorPanel.setVisible(true);
        } else {
            errorPanel.setVisible(false);
        }
    },
    escapeHtml: function (text) {
        var map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };

        return text.replace(/[&<>"']/g, function (m) {
            return map[m];
        });
    },
    popUpDemandeResetMdp: function (login) {
        Ext.Msg.confirm('Authentification', 'Confirmez vous la demande de réinitialisation de mot de passe pour "' + this.escapeHtml(login) + '" ?'
            + '<br/><p style="margin-top:10px;margin-left:20px;font-style:italic;color:#a9a9a9;">'
            + 'En choississant "Oui", vous recevrez un courriel qui vous permettra de modifier votre mot de passe.</p>', function (btn) {
            if (btn === "yes") {
                Ext.Ajax.request({
                    url: Sdis.Remocra.util.Util.withBaseUrl('../utilisateurs/lostpassword'),
                    method: 'POST',
                    params: {
                        identifiant: login
                    },
                    scope: this,
                    callback: function (options, success, response) {
                        Ext.MessageBox.show({
                            title: 'Authentification',
                            msg: 'La demande de réinitialisation de mot de passe a été transmise.'
                                + ' Vous devriez recevoir un courriel qui vous permettra de modifier votre mot de passe.',
                            buttons: Ext.Msg.OK,
                            icon: Ext.MessageBox.INFO
                        });
                    }
                });
            }
        }, this);
    },
    passwordForgotten: function () {
        var loginF = this.getComponent('form').getComponent('loginF');
        if (loginF.isValid() !== true) {
            this.showHideErrorMsg("Merci de renseigner votre nom d'utilisateur");
            return;
        }

        var login = loginF.getValue();

        // 2 possibilités :
        // --> cas 1 : Utilisateur Remocra, on lui permet de changer son mot de passe
        // --> cas 2 : Utilisateur LDAP, on interdit la modification du mot de passe
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl('../utilisateurs/ldapPassword'),
            method: 'GET',
            params: {
                identifiant: login
            },
            scope: this,
            callback: function (options, success, response) {
                var jsResp = Ext.decode(response.responseText);

                // Si le mot de passe n'est pas LDAP
                if (!jsResp) {
                    this.popUpDemandeResetMdp(login);
                }
                // SI LDAP
                else if (jsResp === true) {
                    Ext.MessageBox.show({
                        title: 'Utilisateur non autorisé',
                        msg: 'Veuillez contacter votre SDIS pour changer votre mot de passe.',
                        buttons: Ext.Msg.OK,
                        icon: Ext.MessageBox.INFO
                    });
                }
            }
        });
    }
});
