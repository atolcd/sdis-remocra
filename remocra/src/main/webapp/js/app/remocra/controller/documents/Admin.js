Ext.require('Sdis.Remocra.store.BlocDocument');
Ext.require('Sdis.Remocra.store.Thematique');

Ext.require('Sdis.Remocra.features.documents.AdminBlocDocument');

Ext.define('Sdis.Remocra.controller.documents.Admin', {
    extend : 'Ext.app.Controller',

    stores : [ 'BlocDocument', 'Thematique' ],

    refs : [ {
        ref : 'grid',
        selector : 'crAdminBlocDocument'
    }, {
        ref : 'fiche',
        selector : 'crBlocDocumentFiche',
        autoCreate : true
    } ],

    init : function() {
        this.control({
            'crAdminBlocDocument' : {
                render : this.gridRender,
                celldblclick : this.modifyDocument,
                selectionchange : this.onSelectionChange
            },
            'crAdminBlocDocument #newDocument' : {
                click : this.newDocument
            },
            'crAdminBlocDocument #deleteDocument' : {
                click : this.deleteDocument
            }
        /*
         * , 'crBlocDocumentFiche': { valid: this.onValidFiche }
         */
        });
    },

    gridRender : function() {
        // On lie le store de la grille à la PagingToolbar
        var grid = this.getGrid();
        var tbar = grid.query('pagingtoolbar')[0];
        tbar.bindStore(grid.getStore());
    },

    onSelectionChange : function(selModel, selected, eOpts) {
        this.getGrid().queryById('deleteDocument').setDisabled(
                selected.length < 1);
    },

    newDocument : function(btn, e, eOpts) {
        this.getController('documents.Fiche').showFiche();
    },

    modifyDocument : function(view, td, cellIndex, record, tr, rowIndex, e,
            eOpts) {
        this.getController('documents.Fiche').showFiche(record);
    },

    deleteDocument : function(btn, e, eOpts) {
        var document = this.getSelectedDocument();
        Ext.Msg.confirm('Documents',
                'Confirmez-vous la suppression du document "'
                        + document.get('titre') + '" ?', function(btn) {
                    if (btn == "yes") {
                        document.destroy({
                            success : function(record, operation) {
                                record.store.remove(record);
                                Sdis.Remocra.util.Msg.msg('Documents',
                                        'Le document ' + record.get('titre')
                                                + ' a bien été supprimé.');
                            },
                            failure : function() {
                                Ext.Msg.alert('Documents',
                                        'Un problème est survenu lors de la suppression du document '
                                                + record.get('titre') + '.');
                            }
                        });
                    }
                }, this);
    },

    update : function() {
        this.getGrid().getStore().load();
    },

    getSelectedDocument : function() {
        var record = this.getGrid().getSelectionModel().getSelection();
        if (record != null && Ext.isArray(record)) {
            record = record[0];
        }
        return record;
    }
});