Ext.ns('Sdis.Remocra.features.traitements');
Ext.require('Sdis.Remocra.network.TraitementsStore');

Ext.define('Sdis.Remocra.features.traitements.Traitements', {
    extend : 'Ext.Panel',
    alias : 'widget.crTraitements',

    title : 'Traitements',
    id : 'traitements',

    initComponent : function() {

        var formPanel = new Ext.form.FormPanel({
                itemId : 'formTraitement',
                defaultType : 'textfield',
                defaults : { labelSeparator : '', allowBlank : true, msgTarget : 'side' },
                items : this.getItems(),
                buttons : this.getButtons(),
                buttonAlign : 'left',
                border : false
        });

        Ext.apply(this, { items : [ formPanel ] });
                
        this.on('render',function(){
            //Gestion de l'url : /#traitements/index/application/num_appli
            if(this.extraParams['application']){
                //Cache la combo Application
                var comboAppli = this.getComponent('formTraitement').getComponent('comboAppli');
                comboAppli.hide();            
                
                //Charge la liste des modèles de traitement
                var modeleCombo = this.getComponent('formTraitement').getComponent('idmodeleTrt');                
                if(modeleCombo){
                    var descTrtCmp = this.getComponent('formTraitement').getComponent('descTrt');
                    descTrtCmp.setVisible(false);
                    var store = modeleCombo.store;
                    store.filters.clear();
                    store.filters.add(new Ext.util.Filter({ property : 'code', value : this.extraParams['application'] }));
                    store.load();
                }
            }
        },this);

        this.callParent(arguments);
    },

    getButtons : function() {
        return [
                 { id : 'idExecButton', style: 'margin-top:20px',
                   xtype : 'button',
                   text : 'Exécuter',
                   disabled : true,
                   //width : 100,
                   //iconCls : 'valider',
                   tooltip : 'Exécute le traitement sélectionné',
                   handler : Ext.bind(this.execTrt, this)
                }];
    },

    getItems : function() {
        var items = [
            {
                itemId : 'comboAppli',
                xtype : 'combo',
                fieldLabel : 'Application',
                labelWidth : 120,
                queryMode : 'local',
                valueField : 'value',
                displayField : 'display',                    
                editable : false,
                anchor : '50%',
                store : new Ext.data.SimpleStore({
                    fields : [ 'value', 'display' ],
                    data : [ 
                             [ '1', 'Points d\'eau' ],
                             [ '2', 'Adresses' ],
                             [ '3', 'Permis' ],
                             [ '4', 'Défense de la Forêt Contre les Incendies' ],
                             [ '5', 'Risques' ],
                             [ '7', 'Recherche des Causes et des Circonstances Incendie' ],
                             [ '0', 'Divers' ]
                   ]
                }),
                emptyText : 'Sélectionner une application...',
                listeners : {
                    select : function(combo, records, eOpts) {
                        var application = records[0];
                        var id = application.get('value');
                        var modeleCombo = this.getComponent('formTraitement').getComponent('idmodeleTrt');
                        if(modeleCombo){
                            var descTrtCmp = this.getComponent('formTraitement').getComponent('descTrt');
                            descTrtCmp.setVisible(false);
                            var store = modeleCombo.store;
                            store.filters.clear();
                            store.filters.add(new Ext.util.Filter({ property : 'code', value : id }));
                            store.load();
                            modeleCombo.setRawValue(null);
                            modeleCombo.reset();

                        }
                        
                }, scope : this } 
            },{
                itemId : 'idmodeleTrt',
                xtype : 'combo',
                fieldLabel : 'Choix du traitement',
                labelWidth : 120,
                queryMode : 'remote',
                valueField : 'idmodele',                    
                displayField : 'nom',
                anchor : '50%', 
                store : Ext.create('Sdis.Remocra.network.TraitementsStore',{
                    listeners: {
                        load : Ext.bind(this.deleteParams, this)
                       }                        
                }),                                             
                emptyText : 'Sélectionner un modèle de traitement...',
                editable : false,              
                listeners : {
                    select : function(combo, records, eOpts) {
                        var current = records[0];
                        var descTrtCmp = this.getComponent('formTraitement').getComponent('descTrt');
                        descTrtCmp.setValue(current.get('description'));
                        descTrtCmp.setVisible(true);
                        Ext.Ajax.request({
                            url : Sdis.Remocra.util.Util.withBaseUrl("../traitements/modeletraitementparam/" + current.get('idmodele')),
                            method : 'GET',
                            scope : this,
                            callback : function(options, success, response) {
                                if (success == true) {
                                    var decodedResp = Ext.decode(response.responseText);                                   
                                    this.deleteParams();
                                    this.buildParams(decodedResp.data);
                                    Ext.getCmp('idExecButton').setDisabled(false);
                                } else {
                                    Ext.MessageBox.show({ title : this.title, msg : "Un problème est survenu lors de la récupération des modèles de traitement.",
                                        buttons : Ext.Msg.OK, icon : Ext.MessageBox.ERROR });
                                }
                            }});
                    }, scope : this }
            },{
                itemId : 'descTrt',
                xtype : 'textarea', cls: 'remocra-field-read-only', fieldStyle: 'background-color:transparent;',
                labelWidth : 120,
                fieldLabel : 'Description',
                //height : 150,
                grow : true, //growMin: 100, 
                anchor : '50%',
                readOnly : true,
                hidden: true
            }
        ];

        return items;
    },

    buildParams : function(data) {
        this.tab_items = [];
        // Récupération des emprises communales et des noms de communes
        var panelFather = this.getComponent('formTraitement');
        panelFather.add({
            xtype:'panel', border: false, style: 'margin-bottom:10px;',
            itemId: 'infoParam', html: (data.length>0?'Veuillez renseigner les paramètres suivants :':'Aucun paramètre pour ce traitement')
        });
        var i;
        for (i = 0; i < data.length; i++) {
            var typeControle = data[i]['formTypeValeur'];

            switch (typeControle) {
                // Static Combo
                case 'staticcombo':
                    var paramLstDStore = new Ext.data.JsonStore({
                        proxy : {
                            format : 'json',
                            type : 'rest',
                            headers : { 'Accept' : 'application/json,application/xml', 'Content-Type' : 'application/json' },
                            url : Sdis.Remocra.util.Util.withBaseUrl("../traitements/modtrtparalst/" + data[i]['formSourceDonnee']),
                            reader : { type : 'json', root : 'data', totalProperty : 'total' }
                        },
                        idProperty : 'id',
                        autoLoad : true,
                        restful : true,
                        fields : [ { name : 'id', type : 'integer' },
                                   { name : 'libelle', type : 'string' }
                        ]
                    });
                    panelFather.add({
                        xtype : data[i]['formTypeValeur'],
                        itemId : 'param-'+data[i]['idparametre'],
                        fieldLabel : data[i]['formEtiquette'],
                        allowBlank : !data[i]['formObligatoire'],
                        value : data[i]['formValeurDefaut'],
                        // Propre à la combo
                        store : paramLstDStore,
                        valueField : 'id',
                        displayField : 'libelle',
                        hiddenName : 'id',
                        // typeAhead: true,
                        mode : 'local',
                        triggerAction : 'all',
                        forceSelection : true,
                        hideTrigger : false,
                        editable : false,
                        width : 470,
                        emptyText : "Sélectionner une valeur",
                        labelWidth : 120
                    });
                    break;

                // Combo avec requête Saisie et requête de type "like" sur le libellé
                case 'combo':
                    var paramLstLikeDStore = new Ext.data.JsonStore({
                        proxy : { 
                            format : 'json',
                            type : 'rest',
                            headers : { 'Accept' : 'application/json,application/xml', 'Content-Type' : 'application/json' },
                            url : Sdis.Remocra.util.Util.withBaseUrl("../traitements/modtrtparalst/" + data[i]['formSourceDonnee']),
                            reader : { type : 'json', root : 'data', totalProperty : 'total' }
                        },
                        idProperty : 'id',
                        autoLoad : true, 
                        restful : true, 
                        fields : [ { name : 'id', type : 'integer' },
                                   { name : 'libelle', type : 'string' }
                        ]
                    });
                    panelFather.add({
                        xtype: 'combo',
                        itemId : 'param-'+data[i]['idparametre'],
                        fieldLabel : data[i]['formEtiquette'],
                        allowBlank : !data[i]['formObligatoire'],
                        value : data[i]['formValeurDefaut'],
                        // Propre à la combo
                        store: paramLstLikeDStore,
                        valueField: 'id',
                        displayField: 'libelle',
                        hiddenName : 'id',
                        typeAhead: true,
                        mode : 'remote',
                        triggerAction : 'all',
                        forceSelection: true,
                        hideTrigger : false,
                        editable : true,
                        minChars: 1,
                        width : 470,
                        emptyText : "Sélectionner une valeur",
                        labelWidth : 120
                    });
                    break;

                // FileUpload
                case 'fileuploadfield':
                    panelFather.add({
                                xtype : data[i]['formTypeValeur'],
                                itemId : 'param-'+data[i]['idparametre'],
                                fieldLabel : data[i]['formEtiquette'],
                                name : 'file_upload_' + data[i]['idparametre'],
                                allowBlank : !data[i]['formObligatoire'],
                                width : 470,
                                // Propre au bouton file chooser
                                buttonText : 'Parcourir', labelWidth : 120 
                            });

                    break;

                // DateField
                case 'datefield':
                    panelFather.add({
                        xtype : data[i]['formTypeValeur'],
                        itemId : 'param-'+data[i]['idparametre'],
                        fieldLabel : data[i]['formEtiquette'],
                        allowBlank : !data[i]['formObligatoire'],
                        value : data[i]['formValeurDefaut'],
                        labelWidth : 120,
                        format : 'd/m/Y'
                    });

                    break;

                // Composant par défaut
                case 'checkbox':
                    panelFather.add({
                        xtype : data[i]['formTypeValeur'],
                        itemId : 'param-'+data[i]['idparametre'],
                        fieldLabel : data[i]['formEtiquette'],
                        allowBlank : !data[i]['formObligatoire'],
                        checked : data[i]['formValeurDefaut'],
                        labelWidth : 120
                    });

                    break;

                default:
                    panelFather.add({
                        xtype : data[i]['formTypeValeur'], 
                        itemId : 'param-'+data[i]['idparametre'],
                        fieldLabel : data[i]['formEtiquette'],
                        allowBlank : !data[i]['formObligatoire'],
                        value : data[i]['formValeurDefaut'],
                        labelWidth : 120
                    });

                    break;

            }
            this.tab_items.push('param-'+data[i]['idparametre']);
            panelFather.doLayout();
        }

    },

    deleteParams : function() {
        var compFather =  this.getComponent('formTraitement');
        var infoParam = compFather.getComponent('infoParam');
        if (infoParam) {
            compFather.remove(infoParam);
        }
        if (this.tab_items && this.tab_items.length > 0) {
            if (this.tab_items != null) {                
                var i; 
                for (i = 0; i < this.tab_items.length; i++) {
                    var comp = compFather.getComponent(this.tab_items[i]);
                    if (comp) {
                        compFather.remove(comp, true);
                    }
                }
            }
        }
    },

    execTrt : function() {
        var formP = this.getComponent('formTraitement');
        if (!formP.getForm().isValid()) {
            Ext.MessageBox.show({ title : this.title, msg : "Les paramètres ne sont pas tous valides.<br/>Veuillez corriger les erreurs avant de valider.", buttons : Ext.Msg.OK,
                icon : Ext.MessageBox.ERROR });
            return;
        }

        var data = [];
        var idmodele = formP.getComponent('idmodeleTrt').getValue();

        var i;
        for (i = 0; i < this.tab_items.length; i++) {
            var comp = formP.getComponent(this.tab_items[i]);
            var value;
            if (comp.xtype == 'datefield') {
                value = Ext.Date.format(comp.getValue(), 'Y-m-d');
            } else if (comp.xtype == 'filefield') {
                // Cas du fileupload : value peut être de la forme suivante (ex : Chrome) : C:\\fakepath\\R112815A.txt
                var rawValue = comp.getValue();
                var rawValueParts = rawValue.split('\\');
                value = rawValueParts[rawValueParts.length-1];
            } else {
                value = comp.getValue();
            }

            data.push({ idparametre : { idparametre : this.tab_items[i].substring(6) }, idtraitement : { idtraitement : -1 }, valeur : value.toString() });
        }

        // TODO api ajouter une vérification utilisateur connecté
        formP.getForm().submit(
                {
                    params : { jsonValeurs : Ext.JSON.encode(data) },
                    url : Sdis.Remocra.util.Util.withBaseUrl("../traitements/" + idmodele),
                    method : 'POST',
                    scope : this,

                    success : function(form, action) {
                        Ext.MessageBox.show({ title : 'Demande de traitement',
                            msg : 'Votre demande est en cours de traitement.\n Un message électronique vous informera de l\'issue du \n' + 'traitement.', buttons : Ext.Msg.OK,
                            icon : Ext.MessageBox.INFO });
                    },

                    failure : function(form, action) {
                        Ext.MessageBox.show({ title : this.title, msg : 'Un problème est survenu lors de l\'enregistrement des paramètres.', buttons : Ext.Msg.OK,
                            icon : Ext.MessageBox.ERROR });
                    } });
    }

});