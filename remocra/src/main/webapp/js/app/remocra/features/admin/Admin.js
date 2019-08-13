Ext.require('Sdis.Remocra.widget.WidgetFactory');
Ext.require('Sdis.Remocra.features.admin.typereference.ParamConf');
Ext.require('Sdis.Remocra.network.ParamConfStore');
Ext.require('Sdis.Remocra.store.ProfilUtilisateur');
Ext.require('Sdis.Remocra.store.ProfilOrganisme');
Ext.require('Sdis.Remocra.features.admin.typereference.OrganismeGrid');
Ext.require('Sdis.Remocra.features.admin.typereference.UtilisateurGrid');
Ext.require('Sdis.Remocra.features.admin.typereference.ProfilGrid');
Ext.require('Sdis.Remocra.features.admin.typereference.AdminDroit');
Ext.require('Sdis.Remocra.features.admin.typereference.ProfilDroitGrid');
Ext.require('Sdis.Remocra.features.admin.typereference.ProfilOrganismeUtilisateurDroitGrid');
Ext.require('Sdis.Remocra.features.admin.typereference.TypeOrganismeGrid');
Ext.require('Sdis.Remocra.store.Utilisateur');
Ext.require('Sdis.Remocra.store.Organisme');


Ext.define('Sdis.Remocra.features.admin.Admin', {
    extend: 'Ext.Panel',
    alias: 'widget.crAdmin',

    title: 'Administration',
    id: 'admin',
    
    initComponent: function() {
        var data = [], panels = [];
        if (Sdis.Remocra.Rights.hasRight('REFERENTIELS_C')) {
            data.push(['adminParamConf', 'Paramètres de configuration', 'paramconf', '#a63a3a']);
            panels.push({
                xtype: 'crAdminParamConf',
                itemId: 'adminParamConf'
            });
        }
        if (Sdis.Remocra.Rights.hasRight('UTILISATEUR_FILTER_ALL_C')
            || Sdis.Remocra.Rights.hasRight('UTILISATEUR_FILTER_ORGANISME_UTILISATEUR_C')) {
            var prefix = Sdis.Remocra.Rights.hasRight('REFERENTIELS_C') ? '7 : ' : '';
            data.push(['adminUtilisateur', prefix + 'Utilisateurs', 'utilisateurs', '#000000']);
            panels.push({
                xtype: 'crAdminTypeReference',
                itemId: 'adminUtilisateur',
                grid: {
                    xtype: 'crAdminUtilisateurGrid'
                }
            });
        }
        if (Sdis.Remocra.Rights.hasRight('REFERENTIELS_C')) {
            data.push(['adminOrganisme', '6 : Organismes', 'organismes', '#000000']);
            panels.push({
                xtype: 'crAdminTypeReference',
                itemId: 'adminOrganisme',
                grid: {
                    xtype: 'crAdminOrganismeGrid'
                }
            });
            data.push(['adminProfilOrganismeUtilisateurDroit', '5 : Liens entre les profils et les fonctionnalités', 'proorgutidroits', '#6262a0']);
            panels.push({
                itemId: 'adminProfilOrganismeUtilisateurDroit',
                border: false, defaults: {border: false},
                bodyPadding : 10,
                items : [{
                     html : 'Il s\'agit ici de déterminer le groupe de fonctionnalités attribué à un utilisateur en fonction de son profil'
                        + ' et du profil de son organisme.',
                         style: 'font-style:italic;margin-bottom: 20px;'
                 }, {
                    xtype: 'crAdminTypeReference',
                    grid: {
                        xtype: 'crAdminProfilOrganismeUtilisateurDroitGrid'
                    }
                }]
            });
            data.push(['adminProfilUtilisateur', '4\' : Profils utilisateurs', 'profilutilisateurs', '#6262a0']);
            panels.push({
                xtype: 'crAdminTypeReference',
                itemId: 'adminProfilUtilisateur',
                grid: {
                    xtype: 'crAdminProfilGrid',
                    modelType: 'Sdis.Remocra.model.ProfilUtilisateur',
                    store: Ext.create('Sdis.Remocra.store.ProfilUtilisateur', {
                        remoteFilter: true,
                        remoteSort: true,
                        autoLoad: true,
                        autoSync: true,
                        pageSize: 20
                    })
                }
            });
            data.push(['adminProfilOrganisme', '4 : Profils organismes', 'profilorganismes', '#6262a0']);
            panels.push({
                itemId: 'adminProfilOrganisme',
                border: false, defaults: {border: false},
                bodyPadding : 10,
                items : [{
                    html : 'Au plus simple, le profil d\'un organisme correspond à son type.'
                        + ' Le profil d\'organisme permet aussi de gérer des <b>variations dans le temps</b> (commune étape 1, étape 2, etc.)'
                        + ' ou <b>de typologie</b> ("grosse commune", "petite commune", etc.).',
                        style: 'font-style:italic;margin-bottom: 20px;'
                }, {
                    xtype: 'crAdminTypeReference',
                    grid: {
                        xtype: 'crAdminProfilGrid',
                        modelType: 'Sdis.Remocra.model.ProfilOrganisme',
                        store: Ext.create('Sdis.Remocra.store.ProfilOrganisme', {
                            remoteFilter: true,
                            remoteSort: true,
                            autoLoad: true,
                            autoSync: true,
                            pageSize: 20
                        })
                     }
                }]
            });
            data.push(['adminTypeOrganisme', '3 : Types d\'organismes', 'typeorganismes', '#6262a0']);
            panels.push({
                xtype: 'crAdminTypeReference',
                itemId: 'adminTypeOrganisme',
                grid: {
                    xtype: 'crAdminTypeOrganismeGrid'
                }
            });
            data.push(['adminDroit', '2 : Attribution des fonctionnalités', 'fonctionnalites', '#a87e2d']);
            panels.push({
                xtype: 'crAdminDroit',
                itemId: 'adminDroit'
            });
            data.push(['adminProfilDroit', '1 : Groupes de fonctionnalités', 'groupesfnct', '#a87e2d']);
            panels.push({
                itemId: 'adminProfilDroit',
                border: false, defaults: {border: false},
                bodyPadding : 10,
                items : [{
                     html : 'Un groupe de fonctionnalités porte les <b><a href="#admin/index/elt/fonctionnalites">fonctionnalités'
                        + ' qui sont attribuées</a> aux profils</b> et détermine la manière de <b>présenter les informations des'
                        + ' cartes</b> lorsque le bouton "Information" est utilisé (via une feuille de style GeoServer).',
                         style: 'font-style:italic;margin-bottom: 20px;'
                 }, {
                    xtype: 'crAdminTypeReference',
                    grid: {
                        xtype: 'crAdminProfilDroitGrid'
                    }
                }]
            });
        }

        var valueToBeSelected = this.searchIndexToBeSelected(data, this.extraParams.elt||null);
        valueToBeSelected = data[valueToBeSelected][0];
        Ext.apply(this, {
            items: [{
                xtype: 'combo',
                tpl: '<tpl for="."><li role="option" class="x-boundlist-item" style="color: {color};">{display}</li></tpl>',
                itemId: 'adminCombo',
                width: 500,
                labelSeparator: Sdis.Remocra.widget.WidgetFactory.DEFAULT_LABEL_SEP,
                labelWidth: Sdis.Remocra.widget.WidgetFactory.DEFAULT_LABEL_WIDTH,
                fieldLabel: 'Administrer les',
                queryMode: 'local', valueField: 'value', displayField: 'display',
                editable: false,
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'display', 'key', 'color'],
                    data : data
                }),
                value: valueToBeSelected, // On sélectionne le premier élément
                listeners: {
                    select: function(combo, records, eOpts) {
                        // On change l'URL (suite dans urlChanged)
                        Sdis.Remocra.util.Util.changeHash('admin/index/elt/'+records[0].get('key'));
                    }, scope: this
                },
                hidden: data.length <2
            },{
                xtype: 'panel',
                itemId: 'contentPanel',

                layout: { type: 'card', deferredRender: false },
                margins: '2 5 5 0',
                activeItem: valueToBeSelected,
                border: false, defaults: {border: false},
                items: panels
            }]
        });
        this.callParent(arguments);
        var contentPanel = this.getComponent('contentPanel');
        contentPanel.items.items.forEach(function(item){
             if(item.itemId === "adminOrganisme") {
               item.down("toolbar").getComponent("organismeContact").show();
             }

        });
    },
    
    // Recherche de l'index à sélectionner. 0 par défaut
    searchIndexToBeSelected: function(data, value) {
        var i;
        for(i=0 ; i<data.length ; i++) {
            if (data[i][2] == value) {
                return i;
            }
        }
        return 0;
    },
    
    // Changement d'URL : on affiche le composant adéquat
    urlChanged: function(desc) {

        var key = desc.extraParams.elt;
        var adminCombo = this.getComponent('adminCombo');
        var recordToSelect = adminCombo.findRecord('key', key);
        adminCombo.select(recordToSelect);

        var contentPanel = this.getComponent('contentPanel');
        contentPanel.layout.setActiveItem(recordToSelect.get('value'));
        this.doLayout();
        // If panel has a refreshView then call it
        if(contentPanel.layout.activeItem.refreshView) {
            contentPanel.layout.activeItem.refreshView();
        }

        // Si on bascule dans l'administration des organismes ou l'administration des types d'organisme, on refresh le store
        // Ca permet de prendre en compte les changements d'autres onglets (comme les types d'organisme parent) sans avoir besoin de rafraîchir la page
        if(key === "organismes" || key === "typeorganismes"){
            contentPanel.items.items.forEach(function(item){
                if(item.itemId === "adminOrganisme" || item.itemId === "adminTypeOrganisme"){
                    item.items.items[0].store.reload();
                }
            });
        }


    } 
});