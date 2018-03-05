Ext.require('Sdis.Remocra.store.TypeHydrantAnomalie');
Ext.require('Sdis.Remocra.store.TypeHydrantNature');
Ext.require('Sdis.Remocra.store.TypeHydrantSaisie');
Ext.require('Sdis.Remocra.store.TypeHydrantCritere');

Ext.require('Sdis.Remocra.features.hydrants.AdminAnomalieFiche');
Ext.require('Sdis.Remocra.features.hydrants.AdminAnomalieEdit');

Ext.define('Sdis.Remocra.controller.hydrant.Admin', {
    extend: 'Ext.app.Controller',

    stores: ['TypeHydrantNature','TypeHydrantAnomalie','TypeHydrantSaisie','TypeHydrantCritere'],

    refs: [{
        ref: 'panelAdmin',
        selector: 'adminanomalie'
    },{
        ref: 'ficheAnomalie',
        selector: 'adminanomaliefiche',
        xtype: 'adminanomaliefiche',
        autoCreate: true
    },{
        ref: 'editAnomalie',
        selector: 'adminanomalieedit',
        xtype: 'adminanomalieedit',
        autoCreate: true
    },{
        ref: 'grid',
        selector: 'adminanomalie grid'
    }],

    init: function() {
        this.columns = null;
        if (Ext.getStore('TypeHydrantNature').getCount() == 0) {
            Ext.getStore('TypeHydrantNature').on('load', function() {
                this.initColumns();
            }, this, {
                single: true
            });
        } else {
            this.initColumns();
        }

        this.control({
            'adminanomalie': {
                afterrender: this.initView
            },
            'adminanomalie grid': {
                celldblclick: this.onEditCell
            },
            'adminanomaliefiche': {
                valid: this.onValidFiche
            },
            'adminanomalieedit': {
                valid: this.onValidEdition
            }
        });
    },

    initColumns: function() {
        if (!Sdis.Remocra.Rights.hasRight('REFERENTIELS_C')) {
            if (this.getPanelAdmin()) {
                this.getPanelAdmin().add({html : 'Vous n\'avez pas les autorisations requises pour accéder à la ressource demandée.'});
            }
            return;
        }
        if (this.columns == null) {
            this.columns = [];
            var store = Ext.getStore('TypeHydrantNature'), types = store.collect('parentName', false, true);

            this.columns.push({
                text: 'anomalie',
                dataIndex: 'nom',
                width: 300
            });

            this.columns.push({
                xtype: 'actioncolumn',
                width: 50,
                items: [{
                    icon: 'images/delete.png',
                    tooltip: 'Supprimer',
                    handler: function(grid, rowIndex, colIndex) {
                        var rec = grid.getStore().getAt(rowIndex);
                        Ext.Msg.confirm('Suppression anomalie', 'Confirmez-vous la suppression de l\'anomalie "' + rec.get('nom') + '" ?', function(buttonId) {
                            if (buttonId == 'yes') {
                                rec.destroy({
                                    success: function(record, operation) {
                                        grid.getStore().remove(record);
                                    }
                                });
                            }
                        });
                    }
                },{
                    icon: 'images/update.png',
                    tooltip: 'Mettre à jour',
                    scope: this,
                    handler: function(grid, rowIndex, colIndex) {
                        var rec = grid.getStore().getAt(rowIndex);
                        w = this.getEditAnomalie();
                        w.loadRecord(rec);
                        w.show();
                    }
                }]
            });

            Ext.Array.each(types, function(nomHydrant) {
                var column = {
                    text: nomHydrant,
                    columns: []
                };
                var natures = store.query('parentName', nomHydrant);
                natures.each(function(nature) {
                    column.columns.push({
                        text: nature.get('nom'),
                        align: 'center',
                        nature: nature,
                        renderer: this.renderCell
                    });
                }, this);
                this.columns.push(column);
            }, this);

            this.initView();
        }
    },

    initView: function() {
        if (this.columns == null) {
            return;
        }
        if (this.getPanelAdmin() == null) {
            return;
        }
        this.getPanelAdmin().add({
            xtype: 'grid',
            features: [{
                ftype: 'grouping',
                groupHeaderTpl: '{name}',
                enableNoGroups: false,
                enableGroupingMenu: false,
                startCollapsed: false
            }],
            columnLines: true,
            viewConfig: {
                stripeRows: true
            },
            tbar: [{
                xtype: 'button',
                text: 'Ajouter une anomalie',
                scope: this,
                handler: this.addAnomalie
            }],
            columns: this.columns,
            store: 'TypeHydrantAnomalie',
            selModel: {
                selType: 'cellmodel'
            },
            saisie: {
                'CREA': {
                    text: 'Cr'
                },
                'RECO': {
                    text: 'Re'
                },
                'RECEP': {
                    text: 'Rc'
                },
                'CTRL': {
                    text: 'Ct'
                },
                'VERIF': {
                    text: 'Ve'
                },
                'LECT': {
                    text: 'Le'
                }
            }
        });
    },

    addAnomalie: function() {
        w = this.getEditAnomalie();
        w.loadRecord(Ext.create('Sdis.Remocra.model.TypeHydrantAnomalie'));
        w.show();
    },

    renderCell: function(value, metaData, record, rowIndex, colIndex, store, view) {
        var col = view.getHeaderAtIndex(colIndex);
        var info = record.getInfoByNature(col.nature.getId());

        if (info == null) {
            return '-';
        }
        var s = '';
        s += info.get('valIndispoTerrestre') == null ? '-' : info.get('valIndispoTerrestre');
        s += ' / ';
        s += info.get('valIndispoHbe') == null ? '-' : info.get('valIndispoHbe');
        s += '<br />';

        Ext.Object.each(this.saisie, function(key, item) {
            s += info.getSaisieByCode(key) != null ? item.text + ' ' : '';
        });
        return s;
    },

    onEditCell: function(view, td, cellIndex, record, tr, rowIndex, e, eOpts) {
        var col = view.getHeaderAtIndex(cellIndex), info, w;

        if (col.nature == null) {
            return;
        }

        info = record.getInfoByNature(col.nature.getId());
        w = this.getFicheAnomalie();

        w.currentRow = rowIndex;
        w.currentAnomalie = record;
        w.currentNature = col.nature;

        w.down('displayfield[name=anomalie]').setValue(record.get('nom'));
        w.down('displayfield[name=nature]').setValue(col.nature.get('nom'));

        if (info == null) {
            w.down('radio[name=op]').setValue(-1);
            w.down('radio[name=hbe]').setValue(-1);
        } else {
            w.info = info;
            w.down('radio[name=op]').setValue(info.get('valIndispoTerrestre') == null ? -1 : info.get('valIndispoTerrestre'));
            w.down('radio[name=hbe]').setValue(info.get('valIndispoHbe') == null ? -1 : info.get('valIndispoHbe'));
            info.saisies().each(function(rec) {
                try {
                    w.down('checkbox[name=saisie][inputValue=' + rec.getId() + ']').setValue(true);
                } catch (e) {
                    console.log('oh...');
                }
                
            });
        }
        w.show();
    },

    onValidFiche: function(fiche) {
        var record = fiche.info;
        var callback = {
            scope: this,
            callback: function(record, operation) {
                if (operation.action == "destroy") {
                    record.store.remove(record);
                }
                if (operation.action == "create") {
                    fiche.currentAnomalie.anomalieNatures().add(record);
                }
                this.getGrid().getView().refreshNode(fiche.currentRow);
                fiche.close();
            }
        };
        if (fiche.getValTerrestre() == -1 && fiche.getValHbe() == -1) {
            if (record != null) {
                record.destroy(callback);
            } else {
                fiche.close();
            }
        } else {
            if (record == null) {
                // Créé un record
                record = Ext.create('Sdis.Remocra.model.TypeHydrantAnomalieNature', {
                    nature: fiche.currentNature,
                    anomalie: fiche.currentAnomalie
                });
            }
            // mise à jour du record
            record.set('valIndispoTerrestre', this.nullIfNeg(fiche.getValTerrestre()));
            record.set('valIndispoHbe', this.nullIfNeg(fiche.getValHbe()));

            var saisies = fiche.getSaisie();
            var oldIds = record.saisies().collect('id');
            var newIds = Ext.Array.pluck(saisies, "internalId");
            if (oldIds.length != newIds.length || Ext.Array.difference(newIds, oldIds).length != 0) {
                record.saisies().removeAll();
                record.saisies().add(saisies);
                record.setDirty(true);
            }

            // sauvegarde
            if (record.phantom || record.dirty) {
                record.save(callback);
            } else {
                fiche.close();
            }
        }
    },

    nullIfNeg: function(val) {
        if (val < 0) {
            return null;
        }
        return val;
    },

    onValidEdition: function(fiche) {
        var form = fiche.getForm(), rec = form.getRecord();
        if (form.isValid()) {
            form.updateRecord();
            var isPhantom = rec.phantom;
            if (rec.dirty) {
                rec.save({
                    scope: this,
                    success: function(record, operation) {
                        fiche.close();
                        if (isPhantom) {
                            this.getGrid().getStore().add(record);
                        }
                    }
                });
            } else {
                fiche.close();
            }
        }
    }
});