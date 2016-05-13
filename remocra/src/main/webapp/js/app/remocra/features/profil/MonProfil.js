
Ext.ns('Sdis.Remocra.features.profil');

Ext.require('Sdis.Remocra.widget.WidgetFactory');
Ext.require('Sdis.Remocra.network.CurrentUtilisateurStore');

Ext.define('Sdis.Remocra.features.profil.MonProfil', {
    extend: 'Ext.Panel',
    alias: 'widget.crMonProfil',

    title: 'Mon Profil',
    id: 'monprofil',
    
    url: null,

    initComponent: function() {

        var f = Sdis.Remocra.widget.WidgetFactory;
        
        // Mot de passe
        var pwd1 = f.createPasswordField('Nouveau mot de passe', true, '', {id: 'pwd1'});
        var pwd2 = f.createPasswordField('Confirmation', true, '', {id: 'pwd2', otherPwdFieldId:'pwd1'});

        var changeMdpFs = f.createLightFS('changeMdpFs', [pwd1, pwd2], {title: 'Changer mon mot de passe'});
        
        // Notifications
        var msgRemocra = f.createCheckbox('Recevoir une copie des messages envoyés avec Remocra', null, false, {id:'msgRemocra'});
        var notifFs = f.createLightFS('notifFs',
                [msgRemocra],
                {title: 'Notifications par messagerie'}
        );
        
        // -- BOUTONS
        var actionButtons = [];
        actionButtons.push(new Ext.Button({
            id : 'okBtn'+this.id,
            text : 'Valider',
            minWidth  : 70,
            listeners: {
                click: Ext.bind(this.saveData, this),
                scope: this
            }
        }));
        actionButtons.push(new Ext.Button({
            id : 'cancelBtn'+this.id,
            text : 'Annuler',
            minWidth  : 70,
            listeners: {
                click: Ext.bind(this.loadData, this),
                scope: this
            }
        }));
        
        Ext.apply(this, {
            items: new Ext.form.FormPanel({
                border: false,
                id: 'monprofilformp', items: [changeMdpFs, notifFs]}),
            buttons: actionButtons,
            buttonAlign: 'left'
        });
        
        this.callParent(arguments);
        
        this.loadData();
    },
    
    loadData: function() {
        Sdis.Remocra.network.CurrentUtilisateurStore.getCurrentUtilisateur(this, function(user) {
                Ext.getCmp('msgRemocra').setValue(user.get('messageRemocra'));
            }
        );
    },
    
    saveData: function() {
        var formP = Ext.getCmp('monprofilformp');
        if (!formP.getForm().isValid()) {
            Ext.MessageBox.show({
               title: this.title,
               msg: "Les éléments saisis ne sont pas tous valides.<br/>Veuillez corriger les erreurs avant de valider.",
               buttons: Ext.Msg.OK,
               icon: Ext.MessageBox.ERROR
            });
            return;
        }
        
        //On va rechercher le currentUtilisateur (On ne stocke pas le currentUtilisateur car il est remis à jour dans le store en annule/remplace lors d'un update )
        Sdis.Remocra.network.CurrentUtilisateurStore.getCurrentUtilisateur(this, function(user) {
            //remet à jour 
            user.set('messageRemocra', Ext.getCmp('msgRemocra').getValue());
            user.set('password', Ext.getCmp('pwd1').getValue());
            
            Sdis.Remocra.network.CurrentUtilisateurStore.sync();
        });
        
    }

});