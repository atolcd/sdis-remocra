Ext.define('Sdis.Remocra.features.documents.ImportDocuments', {
    extend: 'Ext.Component',
    alias: 'widget.crImportDocuments',

    vueImportDocuments  : null,
    id : "show-importDocuments-"+(++Ext.AbstractComponent.AUTO_ID),

    listeners: {
        'afterrender': function(){
            if(Ext.isDefined(window.remocraVue)) {
                this.vueImportDocuments = remocraVue.importDocuments(this.id);
            } else {
                console.log('importDocuments : remocraVue undefined');
            }
        },
        'beforedestroy': function() {
            this.vueImportDocuments.$el.remove();
            this.vueImportDocuments.$destroy();
        }
    },

    initComponent: function() {
        this.callParent(arguments);
    }
});