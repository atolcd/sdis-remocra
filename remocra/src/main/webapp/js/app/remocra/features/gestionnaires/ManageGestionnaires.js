Ext.define('Sdis.Remocra.features.gestionnaires.ManageGestionnaires', {
    extend: 'Ext.Component',
    alias: 'widget.crManageGestionnaires',

    vueManageGestionnaires: null,
    id: "manageGestionnaires",

    listeners: {
        'afterrender': function () {
            if (Sdis.Remocra.Rights.hasRight('GESTIONNAIRE_E') || Sdis.Remocra.Rights.hasRight('GESTIONNAIRE_L')) {

                if (Ext.isDefined(window.remocraVue)) {
                    this.vueManageGestionnaires = remocraVue.manageGestionnaires(this.id);
                }
            }
        },
        'beforedestroy': function () {
            this.vueManageGestionnaires.$el.remove();
        }
    },

    initComponent: function () {
        this.callParent(arguments);
    }
});