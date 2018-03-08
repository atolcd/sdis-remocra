Ext.ns('Sdis.Remocra.features.admin.typereference');
Ext.define('Sdis.Remocra.features.admin.typereference.AdminDroit', {
    extend: 'Ext.Panel',
    alias: 'widget.crAdminDroit',

    border: false, defaults: {border: false},
    bodyPadding : 10,

    columns: null,
    storeTypeDroit: null,
    storeProfilDroit: null,
    tdpd: null,

    items: {
        html : 'L\'<b>attribution des fonctionnalités aux groupes</b> se fait en cliquant sur les cellules. Une'
            + ' fonctionnalité est activée pour un groupe lorsque le symbole ✓ est affiché dans la colonne du groupe.'
            + '<br/>L\'interface <a href="#admin/index/elt/groupesfnct">d\'administration des fonctionnalités</a>'
            + ' permet d\'ajouter un groupe, de réinitialiser l\'ensemble des fonctionnalités d\'un groupe et de'
            + ' recopier les fonctionnalités d\'un autre groupe.',
        style: 'font-style:italic;margin-bottom: 20px;'
    },

    initComponent: function() {

        // Types de droits pour créer les lignes
        this.storeTypeDroit = Ext.create('Sdis.Remocra.store.TypeDroit', {
            autoLoad: true,
            remoteSort: true,
            remoteGroup: false,
            sorters: {property: 'code', direction: 'ASC'},
            groupField: 'categorie',
            pageSize: 500
        });
        this.columns = [{
            text: '',
            width: 400,
            sortable: false,
            dataIndex: 'categorie',
            hideable: false,
            locked: true
        }, {
            hideable: false,
            header: 'Fonctionnalité / Groupe',
            width: 300,
            sortable: false,
            dataIndex: 'description',
            locked: true
        }];

        // Chargement des liens types de droits -> profils de droits
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl("../droitsadmin"),
            method: 'GET',
            scope: this,
            callback: function(options, success, response) {
                if (success !== true) {
                    Ext.MessageBox.show({
                        title: "Impossible de charger les droits",
                        msg: res.message,
                        buttons: Ext.Msg.OK,
                        icon: Ext.MessageBox.ERROR
                    });
                    return;
                }
                var jsResp = Ext.decode(response.responseText);
                this.tdpd = jsResp.data;

                // Chargement des profil_droit pour créer les colonnes
                this.storeProfilDroit = Ext.create('Sdis.Remocra.store.ProfilDroit', {
                    autoLoad: true,
                    remoteSort: true,
                    sorters: {property: 'code', direction: 'ASC'},
                    pageSize: 200,
                    listeners: {
                        'load': this.addColumns,
                        scope: this,
                        single: true
                    }
                });
            }
        });

        this.callParent(arguments);
    },

    addColumns: function() {
        this.storeProfilDroit.each(function(profilDroit) {
            this.columns.push({
                header: profilDroit.get('nom'),
                align: 'center',
                width: 70,
                height: 70,
                hideable: false,
                sortable: false,
                dataIndex: 'nom',
                idProfil: profilDroit.get('id'),
                renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                    var idProfil = this.columns[colIndex].idProfil;
                    var idTypeDroit = record.get('id');
                    var admindroitpanel = view.findParentByType('crAdminDroit');
                    var tdConf = admindroitpanel.tdpd[idTypeDroit];
                    var active = tdConf && Ext.Array.contains(tdConf, idProfil);
                    metaData.tdCls = 'grid-cell-tdpd grid-cell-tdpd-' + (active?'':'un')+'checked';
                    return active ? '✓':'-';
                }
            });
        }, this);

        var grid = Ext.create('Ext.grid.Panel', {
            height: 600,
            id: 'admindroits',
            disableSelection: true,
            selModel: Ext.create('Ext.selection.CellModel', {
            }),
            store: this.storeTypeDroit,
            viewConfig: {
                stripeRows: true
            },
            features: [{
                id: 'group',
                ftype: 'grouping',
                groupHeaderTpl: '{name}',
                hideGroupedHeader: true,
                enableGroupingMenu: false
            }],
            columns: this.columns
        });
        grid.getView().on('cellclick', function(gridview, domcell, colIndex, record, domline, rowIndex, evt) {
            var idProfil = gridview.ownerCt.columns[colIndex].idProfil;
            var idTypeDroit = record.get('id');
            if (!idTypeDroit) {
                //Pas une cellule à modifier
                return;
            }
            var tdConf = this.tdpd[idTypeDroit];
            if (!tdConf) {
                tdConf = [];
                this.tdpd[idTypeDroit] = tdConf;
            }
            var active = Ext.Array.contains(tdConf, idProfil);
            if (active) {
                Ext.Array.remove(tdConf, idProfil);
            } else {
                tdConf.push(idProfil);
            }
            this.updDroit(tdConf, idTypeDroit, idProfil, !active, domcell);

        }, this);

        this.add(grid);
    },

    /**
     * Met à jour le droit d'un profil (activation ou désactivation)
     *
     * @param tdConf configuration du type de droits (tableau des id de profils)
     * @param idtypedroit type de droit concerné
     * @param idprofil profil de droits concerné
     * @param active activation en cours (sinon désactivation)
     * @param domcell Noeud dom de la cellule pour mise à jour graphique
    */
    updDroit: function(tdConf, idtypedroit, idprofil, active, domcell) {
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl('../droitsadmin/'+idtypedroit+'/'+idprofil+'/'+active),
            method: 'PUT',
            scope: this,
            callback: function(options, success, response) {
                if (success !== true) {
                    Ext.MessageBox.show({
                        title: "Impossible de mettre à jour les droits",
                        msg: res.message,
                        buttons: Ext.Msg.OK,
                        icon: Ext.MessageBox.ERROR
                    });
                    return;
                }
                // Mise à jour de données au cas où la grille se rafraîchisse.
                if (active) {
                    tdConf.push(idprofil);
                } else {
                    Ext.Array.remove(tdConf, idprofil);
                }
                // Mise à jour graphique immédiate.
                // Pour optimiser l'affichage, on met à jour le dom directement plutôt que de recharger la grille
                domcell.classList.remove('grid-cell-tdpd-'+(active?'un':'')+'checked');
                domcell.classList.add('grid-cell-tdpd-'+(active?'':'un')+'checked');
                domcell.firstChild.textContent = active?'✓':'-';
                //gridview.refresh();
            }
        });
    }
});
