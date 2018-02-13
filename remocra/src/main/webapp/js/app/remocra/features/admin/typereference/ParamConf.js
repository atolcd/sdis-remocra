
Ext.require('Sdis.Remocra.widget.WidgetFactory');
Ext.require('Sdis.Remocra.network.ParamConfStore');

Ext.ns('Sdis.Remocra.features.admin.typereference');
Ext.define('Sdis.Remocra.features.admin.typereference.ParamConf', {
    extend: 'Ext.Panel',
    alias: 'widget.crAdminParamConf',

    border: false, defaults: {border: false},
    bodyPadding : 10,

    store: null,
    
    initComponent: function() {
        this.store = Ext.create('Sdis.Remocra.network.ParamConfStore', {
            listeners: {
                'load': function(store, records, success, operation, eOpts) {
                    if (!records) {
                        return;
                    }
                    this.removeAll();

                    // Couches cartographiques (layers.json)
                    this.add({
                        xtype:'panel',
                        style: 'margin-bottom:15px;',
                        buttonAlign: 'left',
                        buttons: ['<a href="'
                                + 'https://github.com/atolcd/sdis-remocra/blob/master/docs/administration/Couches.adoc'
                                + '" target="_blank">🔗</a> Couches cartographiques : ', {
                            xtype:'button', text:'<span>Recharger</span>', listeners: {
                            click: Ext.bind(this.reloadLayers, this),
                            scope: this
                        }}, {
                            xtype : 'linkbutton',
                            text : '<span>Visualiser</span>',
                            src : BASE_URL+'/../geoserver/layers',
                            hrefTarget: '_blank'
                        }
                   ]});
                    this.add({
                        xtype: 'panel',
                        html: 'Veuillez vous référer à la <a'
                            +' href="https://github.com/atolcd/sdis-remocra/blob/master/docs/administration/Parametres%20de%20configuration.adoc"'
                            +' target="_blank">documentation dédiée</a> pour plus de renseignements. Les liens 🔗 réfèrent directement la documentation du paramètre.',
                        bodyPadding : 10,
                        border: false
                    });

                    var f = Sdis.Remocra.widget.WidgetFactory;
                    
                    var fields = [], i, r, cle, valeur, desc, clDisplay, nomgroupe, field, label;
                    var fieldsets = {};
                    for (i=0 ; i<records.length ; i++) {
                        r = records[i];
                        cle = r.get('cle');
                        valeur = r.get('valeur');
                        desc = r.get('description');
                        clDisplay = r.get('clDisplay');
                        nomgroupe = r.get('nomgroupe');
                        field = null;
                        //label = '<span '+(desc?'title="'+desc+'"':null)+'>'+cle+'</span>';
                        label = '<span title="'+cle+'"><a href="'
                            + 'https://github.com/atolcd/sdis-remocra/blob/master/docs/administration/Parametres%20de%20configuration.adoc#'
                            + cle.toLowerCase()+'" target="_blank">🔗</a>&nbsp;&nbsp;'+(desc&&desc.length>0?desc:cle)+'</span>';
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
                        } else if (clDisplay == 'Password') {
                            field = f.createPasswordField(label, true, valeur, {id: cle, labelWidth: 400, width: 700});
                        } else{
                            // Dans tous les autres cas : STRING
                            field = f.createTextField(label, true, valeur, {id: cle, labelWidth: 400, width: 700});
                        }
                        if (!Ext.isDefined(fieldsets[nomgroupe])) {
                            fieldsets[nomgroupe] = {
                                xtype: 'fieldset',
                                title: nomgroupe,
                                items: [],
                                autoHeight: true,
                                labelWidth: 150
                            };
                        }
                        fieldsets[nomgroupe].items.push(field);
                    }
                    // Sans ça, le dernier panneau est coupé en bas
                    fieldsets['invisiblehack'] = {html:'&nbsp;', border: false};
                    this.add({
                        xtype: 'form',
                        items: Ext.Object.getValues(fieldsets),
                        border: false
                    });
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
                            'Les paramètres de configuration ont été mis à jour.', 3);
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
    },

    reloadLayers: function() {
        Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl('../geoserver/layers/reload'),
            method: 'GET',
            scope: this,
            callback: function(options, success, response) {
                if (success == true) {
                    Sdis.Remocra.util.Msg.msg('Rechargement des couches',
                        'Les couches ont bien été rechargées.', 5);
                } else {
                    Ext.Msg.alert('Rechargement des couches',
                        'Un problème est survenu lors du rechargement des couches.<br/>Veuillez vérifier le fichier <i>layers.json</i>.');
                }
            }
        });
    }
});
