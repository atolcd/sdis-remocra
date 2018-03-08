Ext.require('Ext.PagingToolbar');
Ext.require('Sdis.Remocra.features.admin.typereference.TypeReference');
Ext.require('Sdis.Remocra.store.ProfilDroit');
Ext.require('Sdis.Remocra.features.admin.typereference.ProfilDroitChoixWindow');

Ext.define('Sdis.Remocra.features.admin.typereference.ProfilDroitGrid', {
    extend: 'Sdis.Remocra.features.admin.typereference.TypeReferenceGrid',
    alias: 'widget.crAdminProfilDroitGrid',
    
    modelType: 'Sdis.Remocra.model.ProfilDroit',
    store: null,
    uniqueConstraints: ['nom'],
    
    constructor: function(config) {
        config = config || {};
        
        // Ajout des définitions des colonnes si elles ne sont pas déjà définies
        var typeRefGridCols = Sdis.Remocra.features.admin.typereference.TypeReferenceGrid.columns;
        Ext.applyIf(config, {
            columns: [
                typeRefGridCols.nom,
                typeRefGridCols.code,
                {
                    header : 'Feuille de style GeoServer',
                    dataIndex : 'feuilleDeStyleGeoServer',
                    field : 'textfield',
                    menuDisabled : true,
                    flex: 5,
                    editor: {xtype: 'textfield',  allowBlank: false}
                }
            ]
        });
        
        // Paging Toolbar (store partagé)
        this.store = Ext.create('Sdis.Remocra.store.ProfilDroit', {
            remoteFilter: true,
            remoteSort: true,
            autoLoad: true,
            autoSync: true,
            pageSize: 20
        });
        this.bbar = Ext.create('Ext.PagingToolbar', {
            store: this.store,
            displayInfo: true,
            displayMsg: 'Groupes {0} - {1} de {2}',
            emptyMsg: 'Aucun groupe à afficher'
        });
        
        this.callParent([config]);
        
        // Ajout des boutons spécifiques
        var tbar = this.getDockedItems('toolbar[dock="top"]')[0];
        tbar.add({
            itemId: 'copydroits',
            disabled: true,
            text: 'Réinitialiser à partir du groupe...',
            tooltip: 'Réinitialiser les fonctionnalités à partir du groupe',
            iconCls: 'leftIcon',
            handler: this.copyDroitsFromAnotherProfilDroit,
            scope: this
        }, {
            itemId: 'cleardroits',
            disabled: true,
            text: 'Vider',
            tooltip: 'Retirer toutes les fonctionnalités du groupe',
            iconCls: 'deleteIcon',
            handler: this.clearDroits,
            scope: this
        });
        
        this.getSelectionModel().addListener('selectionchange', function(selectionModel, selected, eOpts) {
            var tbar = this.getDockedItems('toolbar[dock="top"]')[0];
            tbar.getComponent('copydroits').setDisabled(selected.length!=1);
            tbar.getComponent('cleardroits').setDisabled(selected.length!=1);
        }, this);
    },
    
    copyDroitsFromAnotherProfilDroit: function() {
        var target = this.getSelectedProfilDroit();
        if (!target) {
            return;
        }
        // Vers target
        var choiceWin = Ext.create('Sdis.Remocra.features.admin.typereference.ProfilDroitChoixWindow', {
            excludeId: target.get('id'),
            listeners: {
                scope: target,
                'ok': this.copyDroitsFromAnotherProfilDroitReal
            }
        });
        choiceWin.addListener('cancel', function() {this.close();}, choiceWin);
        choiceWin.show();
    },
    copyDroitsFromAnotherProfilDroitReal: function(win, profilDroit) {
        var target = this;
        win.close();
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl('../profildroits/' + target.get('id') + '/droits/copyfrom/' + profilDroit.get('id')),
            method: 'PUT',
            scope: this,
            callback: function(options, success, response) {
                if (success == true) {
                    Sdis.Remocra.util.Msg.msg('Groupe de fonctionnalités',
                        'Le groupe  "' + target.get('nom') + '" dispose maintenant des mêmes droits que le groupe "' + profilDroit.get('nom') + '".', 5);
                } else {
                    var msg = o.result && o.result.message ? ' :<br/>'+o.result.message : '';
                    Ext.Msg.alert('Groupe de fonctionnalités',
                        'Un problème est survenu lors de la réinitialisation du groupe de fonctionnalités.' + msg + '.');
                }
            }
        });
    },
    
    clearDroits: function() {
        var target = this.getSelectedProfilDroit();
        if (!target) {
            return;
        }
        Ext.Msg.confirm('Groupe de fonctionnalités', 'Vider les droits du groupe "' + target.get('nom') + '" ?', function(btn) {
            if (btn == "yes"){
                this.clearDroitsReal(target);
            }
        }, this);
    },
    clearDroitsReal: function(target) {
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl('../profildroits/' + target.get('id') + '/droits'),
            method: 'DELETE',
            scope: this,
            callback: function(options, success, response) {
                if (success == true) {
                    Sdis.Remocra.util.Msg.msg('Groupe de fonctionnalités',
                        'Le groupe "' + target.get('nom') + '" n\'a plus aucune fonctionnalité.', 5);
                } else {
                    var msg = o.result && o.result.message ? ' :<br/>'+o.result.message : '';
                    Ext.Msg.alert('Groupe de fonctionnalités',
                        'Un problème est survenu lors du vidage des fonctionnalités du groupe.' + msg + '.');
                }
            }
        });
    },
    
    getSelectedProfilDroit: function() {
        var target = this.getSelectionModel().getSelection();
        if (target != null && Ext.isArray(target)) {
            target = target[0];
        }
        if (!target) {
            Ext.Msg.alert('Groupe de fonctionnalités',
                'Veuillez sélectionner un groupe au préalable.');
            return;
        }
        return target;
    }
});
