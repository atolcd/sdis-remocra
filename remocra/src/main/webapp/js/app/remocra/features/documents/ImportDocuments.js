Ext.define('Sdis.Remocra.features.documents.ImportDocuments', {
    extend: 'Ext.Component',
    alias: 'widget.crImportDocuments',

    vueImportDocuments: null,
    id: "importDocuments",

    listeners: {
        'afterrender': function () {
            if (Sdis.Remocra.Rights.hasRight('REFERENTIELS_C')) {

                if (Ext.isDefined(window.remocraVue)) {
                    this.vueImportDocuments = remocraVue.importDocuments(this.id);
                }
            }
        },
        'beforedestroy': function () {
            this.vueImportDocuments.$el.remove();
        }
    },

    initComponent: function () {
        this.callParent(arguments);
    }
});