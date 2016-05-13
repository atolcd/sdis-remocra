
Ext.require('Sdis.Remocra.widget.WidgetFactory');
Ext.require('Sdis.Remocra.network.ParamConfStore');

Ext.ns('Sdis.Remocra.features.admin.typereference');
Ext.define('Sdis.Remocra.features.admin.typereference.ParamConf', {
    extend: 'Ext.Panel',
    alias: 'widget.crAdminParamConf',

    border: false, defaults: {border: false},
    
    store: null,
    
    initComponent: function() {
        this.store = Ext.create('Sdis.Remocra.network.ParamConfStore', {
            listeners: {
                'load': function(store, records, success, operation, eOpts) {
                    if (!records) {
                        return;
                    }
                    this.removeAll();
                    
                    var f = Sdis.Remocra.widget.WidgetFactory;
                    
                    var fields = [], i, r, cle, valeur, desc, clDisplay, field, label;
                    for (i=0 ; i<records.length ; i++) {
                        r = records[i];
                        cle = r.get('cle');
                        valeur = r.get('valeur');
                        desc = r.get('description');
                        clDisplay = r.get('clDisplay');
                        field = null;
                        //label = '<span '+(desc?'title="'+desc+'"':null)+'>'+cle+'</span>';
                        label = '<span title="'+cle+'">'+(desc&&desc.length>0?desc:cle)+'</span>';
                        if (clDisplay == 'Long' || clDisplay == 'Integer') {
                            field = f.createIntField(label,
                                {id: cle, labelWidth: 400, width: 700, allowBlank: true, value: valeur });
                        } else if (clDisplay == 'Double' || clDisplay == 'Float') {
                            field = f.createDoubleField(label,
                                {id: cle, labelWidth: 400, width: 700, allowBlank: true, value: valeur});
                        } else if (clDisplay == 'Boolean') {
                            field = f.createCheckboxGroup(label, true, {
                                labelWidth: 400, width: 500,
                                items: f.createCheckbox('', valeur, valeur=='true', {id: cle, labelWidth: 400, width: 700})
                            });
                        } else {
                            // Dans tous les autres cas : STRING
                            field = f.createTextField(label, true, valeur, {id: cle, labelWidth: 400, width: 700});
                        }
                        fields.push(field);
                    }
                    var fs = f.createLightFS('allFs', fields, {title: 'Tous les paramètres'});
                    
                    var formPanel = Ext.form.FormPanel({
                        border: false,
                        id: 'paramconfformp', items: fs
                    });
                    
                    this.add(formPanel);
                    this.doLayout();
            }, scope: this}
        });
        
        // -- BOUTONS
        var actionButtons = [];
        actionButtons.push(new Ext.Button({
            id : 'okBtn'+this.id,
            text : 'Valider',
            minWidth  : 70,
            listeners: {
                click: Ext.bind(this.saveData, this),
                scope: this
            }
        }));
        actionButtons.push(new Ext.Button({
            id : 'cancelBtn'+this.id,
            text : 'Annuler',
            minWidth  : 70,
            listeners: {
                click: Ext.bind(this.loadData, this),
                scope: this
            }
        }));
        
        Ext.apply(this, {
            items: {hidden: true},
            buttons: actionButtons,
            buttonAlign: 'left'
        });
        
        this.callParent(arguments);
        
        this.loadData();
    },
    
    loadData: function() {
        this.store.load();
    },
    
    saveData: function() {
        var atLeastARecordDirty = false;
        this.store.each(function(r){
            var cle = r.get('cle');
            var cmp = Ext.getCmp(cle);
            var newValeur = cmp.getValue();
            r.set('valeur', newValeur);
            if (r.dirty) {
                atLeastARecordDirty = true;
            }
        }, this);

        if (atLeastARecordDirty) {
            this.setLoading(true);
            // On synchronise sans recharger les données en provenance du serveur (serait mieux de le faire)
            this.store.sync({
                scope: this,
                success: function(batch, options) {
                    this.setLoading(false);
                        Sdis.Remocra.util.Msg.msg('Paramètres de configuration',
                            'Les paramètres de configuration on été mis à jour.', 3);
                },
                failure: function(batch, options) {
                    this.setLoading(false);
                    Ext.Msg.alert('Paramètres de configurations',
                        'Un problème est survenu lors de la mis à jour.');
                }
            });
        } else {
            Ext.Msg.alert('Paramètres de configurations',
                'Les paramètres de configuration n\'ont pas été modifiés.');
        }
    }
});
