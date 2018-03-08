Ext.require('Sdis.Remocra.widget.WidgetFactory');
Ext.require('Sdis.Remocra.store.ProfilDroit');

Ext.define('Sdis.Remocra.features.admin.typereference.ProfilDroitChoixWindow', {
    extend: 'Ext.Window',

    title: 'Choix d\'un groupe de fonctionnalités',
    width: 500,

    data: null,
    excludeId: null,
    
    initComponent: function() {

        // Identifiant à exclure si nécessaire (si null : tout sera présenté)
        var excludeId = this.excludeId;
        var combo = {
            itemId : 'profilDroit',
            xtype: 'combo',
            style: 'margin:10px;',
            width: 470,
            queryMode: 'local', valueField: 'id', displayField: 'nom',
            editable: false,
            store: {
                type: 'crProfilDroit',
                pageSize: 200,
                autoLoad: true,
                // Exclusion d'un élément
                filters: [
                    function(item) {
                        return item.get('id') != excludeId;
                    }
                ],
                // Sélection du premier élément
                listeners: {
                    load: function(store, records, successFull, eOpts) {
                        if (successFull && records.length>0) {
                            this.getComponent('profilDroit').select(records[0]);
                        }
                    },
                    scope: this
                }
            }
        };
        
        Ext.apply(this, {
            defaults: {style: 'margin:10px;'},
            modal: true,
            constrain: true,
            autoHeight: true,
            buttonAlign: 'center',
            items: [{
                border: false,
                html: '<p>Veuillez sélectionner le groupe de fonctionnalités :</p>'
            }, combo],
            buttons: [{
                itemId : 'okBtn',
                text : 'Valider',
                minWidth  : 70,
                handler: Ext.bind(this.ok, this)
            }, {
                itemId : 'cancelBtn',
                text : 'Annuler',
                minWidth : 70,
                handler: Ext.bind(this.fireEvent, this, ['cancel', this])
            }]
        });
        
        this.callParent(arguments);
    },
    
    ok: function() {
        var profilDroit = this.getComponent('profilDroit').getValueModel();
        if (profilDroit) {
            this.fireEvent('ok', this, profilDroit);
        }
    }
});