Ext.require('Ext.form.FieldSet');

Ext.define('Sdis.Remocra.widget.Anomalie', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.anomalie',
    title: 'Anomalie',

    mixins: {
        bindable: 'Ext.util.Bindable'
    },

    messageRight: 'Vous ne possédez pas les droits nécessaires pour ajouter/supprimer cette anomalie',

    typeSaisie: -1,
    nature: -1,

    initComponent: function() {
        var me = this;
        me.entete = Ext.widget('component', {
            autoEl: 'h2',
            html: '&nbsp;'
        });

        me.btnPrec = Ext.widget('button', {
            text: 'Précédent',
            width: 100,
            handler: function() {
                me.setPosition(me.currentPosition - 1);
            }
        });

        me.btnNext = Ext.widget('button', {
            text: 'Suivant',
            width: 100,
            handler: function() {
                me.setPosition(me.currentPosition + 1);
            }
        });

        me.grid = Ext.widget('grid', {
            height: 250,
            forceFit: true,
            selModel: Ext.create('Ext.selection.CheckboxModel', {
                checkOnly: true,
                showHeaderCheckbox: false,
                listeners: {
                    // scope: me,
                    select: function(selectModel, record) {
                        me.selectRecord(record);
                    },
                    deselect: function(selectModel, record) {
                        me.deselectRecord(record);
                    }
                },
                renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                    var baseCSSPrefix = Ext.baseCSSPrefix;
                    metaData.tdCls = baseCSSPrefix + 'grid-cell-special ' + baseCSSPrefix + 'grid-cell-row-checker';
                    if (!me.isAnomalieSelectionnable(record)) {
                        return '<div data-qtip="' + me.messageRight + '" class="x-grid-row-checker-no-modif">&#160;</div>';
                    } else {
                        return '<div class="' + baseCSSPrefix + 'grid-row-checker">&#160;</div>';
                    }
                }
            }),
            columns: [{
                text: 'Point d\'attention ',
                flex: 400,
                dataIndex: 'nom',
                renderer: function(value, metadata, record, rowIndex, colIndex) {
                    // tooltip
                    metadata.tdAttr = 'data-qtip="' + record.get('nom');
                    if (!me.isAnomalieSelectionnable(record)) {
                        metadata.tdAttr += ' - ' + me.messageRight;
                    }
                    metadata.tdAttr += '"';

                    // TODO : gras ou pas ?
                    // if (record.getMaxValue() > 2) {
                    // metadata.tdCls = 'attention-maj';
                    // }

                    return value;
                }
            } ],
            viewConfig: {
                getRowClass: function(record, index, rowParams, store) {
                    if (!me.isAnomalieSelectionnable(record)) {
                        return 'anomalie-unmodifiable';
                    }
                    return '';
                }
            }
        });

        me.position = Ext.widget('component', {
            columnWidth: 1,
            style: {
                textAlign: 'center'
            },
            html: '&nbsp;'
        });

        me.items = [me.entete, me.grid, {
            xtype: 'container',
            layout: 'column',
            items: [me.btnPrec, me.position, me.btnNext ]
        } ];
        me.callParent(arguments);
        me.currentPosition = 0;

        me.on('destroy', function() {
            if (me.getStore() != null) {
                me.unbindStoreListeners(me.getStore());
            }

            Ext.getStore('TypeHydrantAnomalie').rejectChanges();

        });
    },

    selectRecord: function(record, refresh) {
        if (record == null || !record.isModel) {
            return;
        }

        var lengthBefore = this.selectedRecord.length;
        Ext.Array.include(this.selectedRecord, record);
        if (this.selectedRecord.length > lengthBefore) {
            this.fireEvent('selectionChange', this, this.selectedRecord);
            if (refresh == true) {
                this.checkSelected();
            }

        }
    },

    deselectRecord: function(record, refresh) {
        if (record == null || !record.isModel) {
            return;
        }
        var lengthBefore = this.selectedRecord.length;
        Ext.Array.remove(this.selectedRecord, record);
        if (this.selectedRecord.length < lengthBefore) {
            this.fireEvent('selectionChange', this, this.selectedRecord);
            if (refresh == true) {
                this.checkSelected();
            }
        }
    },

    setInfo: function(typeSaisie, nature) {
        this.typeSaisie = typeSaisie;
        this.nature = nature;
    },

    isAnomalieSelectionnable: function(record) {
        var info = record.getInfoByNature(this.nature);

        if (info == null) {
            return false;
        }
        return info.getSaisieByCode(this.typeSaisie) != null;
    },

    getStoreListeners: function() {
        return {
            scope: this,
            'datachanged': this.initPosition
        };
    },

    bindStore: function(store, initial) {
        var me = this;
        if (!store.isGrouped()) {

            store.group('critere_nom');

        }
        me.mixins.bindable.bindStore.apply(me, arguments);
        me.initPosition(store);
    },

    initPosition: function() {
        if (this.getStore() == null) {
            return;
        }
        this.currentPosition = 0;
        this.maxPosition = this.getStore().getGroups().length;
        this.setPosition(1);
    },

    updateUi: function() {
        this.position.update(this.currentPosition + ' / ' + this.maxPosition);
        this.btnPrec.setDisabled(this.currentPosition <= 1);
        this.btnNext.setDisabled(this.currentPosition >= this.maxPosition);
    },

    setPosition: function(newPosition) {
        var group = null;
        if (this.maxPosition == 0) {
            this.grid.getStore().removeAll();
            this.updateUi();
            this.fireEvent('positionChange', this, this.currentPosition, this.maxPosition, this.currentPosition == this.maxPosition);
        } else if (newPosition >= 0 && newPosition <= this.maxPosition && newPosition != this.currentPosition) {
            this.currentPosition = newPosition;
            group = this.getStore().getGroups()[(this.currentPosition - 1)];
            this.entete.update(group.name);
            this.grid.getStore().removeAll();
            this.grid.getStore().add(group.children);
            this.checkSelected();
            this.updateUi();
            this.fireEvent('positionChange', this, this.currentPosition, this.maxPosition, this.currentPosition == this.maxPosition);
        }
    },

    checkSelected: function() {
        if (!this.grid.rendered) {
            this.grid.on('viewready', this.checkSelected, this, {
                single: true
            });
            return;
        }
        this.grid.getSelectionModel().select(this.selectedRecord);
    },

    getSelected: function() {
        return this.selectedRecord;
    },

    setSelected: function(records) {
        this.selectedRecord = [];
        Ext.Array.each(records, function(item) {
            var record = this.getStore().getById(item.getId());
            if (record != null) {
                Ext.Array.include(this.selectedRecord, record);
            } else {
                Ext.Array.include(this.selectedRecord, item);
            }
        }, this, true);
    }
});