Ext.require('Sdis.Remocra.widget.WidgetFactory');
Ext.require('Sdis.Remocra.features.admin.typereference.ParamConf');
Ext.require('Sdis.Remocra.network.ParamConfStore');
Ext.require('Sdis.Remocra.store.ProfilUtilisateur');
Ext.require('Sdis.Remocra.store.ProfilOrganisme');
Ext.require('Sdis.Remocra.features.admin.typereference.OrganismeGrid');
Ext.require('Sdis.Remocra.features.admin.typereference.UtilisateurGrid');
Ext.require('Sdis.Remocra.features.admin.typereference.ProfilGrid');
Ext.require('Sdis.Remocra.features.admin.typereference.ProfilDroitGrid');
Ext.require('Sdis.Remocra.features.admin.typereference.DroitGrid');
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
        var data = [], panels;
        if(Sdis.Remocra.Rights.getRight('REFERENTIELS').Create) {
                data.push(['adminParamConf', 'Paramètres de configuration', 'paramconf']);
                data.push(['adminUtilisateur', '7 : Utilisateurs', 'utilisateurs']);
                data.push(['adminOrganisme', '6 : Organismes', 'organismes']);
                data.push(['adminProfilOrganismeUtilisateurDroit', '5 : Profils organismes, utilisateurs, droits', 'proorgutidroits']);
                data.push(['adminProfilUtilisateur', '4\' : Profils utilisateurs', 'profilutilisateurs']);
                data.push(['adminProfilOrganisme', '4 : Profils organismes', 'profilorganismes']);
                data.push(['adminTypeOrganisme', '3 : Types d\'organismes', 'typeorganismes']);
                data.push(['adminDroit', '2 : Droits', 'droits']);
                data.push(['adminProfilDroit', '1 : Profils de droits', 'profildroits']);
                panels = [ {
                              xtype: 'crAdminParamConf',
                             itemId: 'adminParamConf'
                         },
                         // Utilisateurs et organismes (paginés)
                         {
                             xtype: 'crAdminTypeReference',
                             itemId: 'adminUtilisateur',
                             grid: {
                               xtype: 'crAdminUtilisateurGrid'
                             }
                         }, {
                             xtype: 'crAdminTypeReference',
                             itemId: 'adminOrganisme',
                             grid: {
                               xtype: 'crAdminOrganismeGrid'
                             }
                         },
                         // Autres (non paginés)
                         {
                             xtype: 'crAdminTypeReference',
                             itemId: 'adminTypeOrganisme',
                             grid: {
                                 xtype: 'crAdminTypeOrganismeGrid'
                             }
                         }, {
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

                         }, {
                             xtype: 'crAdminTypeReference',
                             itemId: 'adminProfilOrganisme',
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
                         }, {
                             xtype: 'crAdminTypeReference',
                             itemId: 'adminProfilDroit',
                             grid: {
                               xtype: 'crAdminProfilDroitGrid'
                             }
                         }, {
                             xtype: 'crAdminTypeReference',
                             itemId: 'adminDroit',
                             grid: {
                               xtype: 'crAdminDroitGrid'
                             }
                         }, {
                             xtype: 'crAdminTypeReference',
                             itemId: 'adminProfilOrganismeUtilisateurDroit',
                             grid: {
                               xtype: 'crAdminProfilOrganismeUtilisateurDroitGrid'
                             }
                         }
                                         ];
        } else if (!Sdis.Remocra.Rights.getRight('REFERENTIELS').Create) {
               data.push(['adminUtilisateur', 'Utilisateurs', 'utilisateurs']);
               panels = [{
                    xtype: 'crAdminTypeReference',
                    itemId: 'adminUtilisateur',
                    grid: {
                        xtype: 'crAdminUtilisateurGrid'
                    }
               }];
        }

        var valueToBeSelected = this.searchIndexToBeSelected(data, this.extraParams.elt||null);
        valueToBeSelected = data[valueToBeSelected][0];
        Ext.apply(this, {
            items: [{
                xtype: 'combo',
                itemId: 'adminCombo',
                width: Sdis.Remocra.widget.WidgetFactory.DEFAULT_WIDTH,
                labelSeparator: Sdis.Remocra.widget.WidgetFactory.DEFAULT_LABEL_SEP,
                labelWidth: Sdis.Remocra.widget.WidgetFactory.DEFAULT_LABEL_WIDTH,
                fieldLabel: "Administrer les",
                queryMode: 'local', valueField: 'value', displayField: 'display',
                editable: false,
                store: new Ext.data.SimpleStore({
                    fields: ['value', 'display', 'key'],
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
    } 
});