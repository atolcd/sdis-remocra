Ext.define('Sdis.Remocra.controller.hydrant.Requetage', {
    extend: 'Ext.app.Controller',

    stores: ['RequeteModele'],

    refs: [{
       ref: 'tabRequetage',
       selector: 'crHydrantsRequetage'
    },{
       ref: 'tabAnalyses',
       selector: 'crHydrantsAnalyses'
    },{
       ref: 'tabDonnees',
       selector: 'crHydrantsDonnees'
    },{
       ref: 'tabMapRequetage',
       selector: 'crHydrantsMapRequetage'
    },{
       ref: 'tabRecherche',
       selector: 'crRecherche'
    }],


    init: function() {

        this.control({
            // Page globale
            'crHydrantsRequetage': {

            },
            'crHydrantsAnalyses #requeteModele': {
               select: this.onSelectRequeteModele
            },
            'crHydrantsAnalyses #idExecButton': {
               click: this.execRqt
            },
            'crRecherche #exportRequete': {
               click: this.onExportRequete
            },
            'crRecherche #deleteRequete': {
               click: this.onDeleteRequete
            },
            'crRecherche': {
               tabchange: this.onTabChange
            }
        });
    },

    onSelectRequeteModele: function(combo, records, initial) {
        var current = records[0];
       this.getTabAnalyses().queryById('description').setText(current.get('description'));
       //On récupèrere les parametres de la requete
        Ext.Ajax.request({
               url : Sdis.Remocra.util.Util.withBaseUrl("../requetemodele/requetemodelparam/" + current.get('id')),
               method : 'GET',
               scope : this,
               callback : function(options, success, response) {
                   if (success == true) {
                       var decodedResp = Ext.decode(response.responseText);
                       this.deleteParams();
                       //On construit la liste des parametres
                       this.buildParams(decodedResp.data);
                       Ext.getCmp('idExecButton').setDisabled(false);
                   } else {
                       Ext.MessageBox.show({ title : this.title, msg : "Un problème est survenu lors de la récupération des modèles de requête.",
                           buttons : Ext.Msg.OK, icon : Ext.MessageBox.ERROR });
                   }
               }});
    },

 buildParams : function(data) {
        this.tab_items = [];
        var panelFather = this.getTabAnalyses().getComponent('formRequetage');
        panelFather.add({
            xtype:'panel', border: false, style: 'margin-bottom:10px;',
            itemId: 'infoParam', html: (data.length>0?'Veuillez renseigner les paramètres suivants :':'Aucun paramètre pour cette requête')
        });
        var i;
        for (i = 0; i < data.length; i++) {
            var typeControle = data[i]['formulaireTypeControle'];

            switch (typeControle) {
                // Static Combo
                case 'staticcombo':
                    var paramLstDStore = new Ext.data.JsonStore({
                        proxy : {
                            format : 'json',
                            type : 'rest',
                            headers : { 'Accept' : 'application/json,application/xml', 'Content-Type' : 'application/json' },
                            url : Sdis.Remocra.util.Util.withBaseUrl("../requetemodele/reqmodparalst/" + data[i]['id']),
                            reader : { type : 'json', root : 'data', totalProperty : 'total' }
                        },
                        idProperty : 'id',
                        //autoLoad : true,
                        restful : true,
                        fields : [ { name : data[i]['sourceSqlValeur'], type : 'string' },
                                   { name : data[i]['sourceSqlLibelle'], type : 'string' }
                        ]
                    });
                    panelFather.add({
                        xtype : data[i]['formulaireTypeControle'],
                        itemId : 'param-'+data[i]['id'],
                        fieldLabel : data[i]['formulaireEtiquette'],
                        allowBlank : !data[i]['obligatoire'],
                        value : data[i]['formulaireValeurDefaut'],
                        // Propre à la combo
                        store : paramLstDStore,
                        valueField: data[i]['sourceSqlValeur'],
                        displayField: data[i]['sourceSqlLibelle'],
                        hiddenName : 'id',
                        // typeAhead: true,
                        mode : 'local',
                        triggerAction : 'all',
                        forceSelection : true,
                        hideTrigger : false,
                        editable : false,
                        emptyText : "Sélectionner une valeur",
                        name : data[i]['nom']
                    });
                    break;

                // Combo avec requête Saisie et requête de type "like" sur le libellé
                case 'combo':
                    var paramLstLikeDStore = new Ext.data.JsonStore({
                        proxy : {
                            format : 'json',
                            type : 'rest',
                            headers : { 'Accept' : 'application/json,application/xml', 'Content-Type' : 'application/json' },
                            url : Sdis.Remocra.util.Util.withBaseUrl("../requetemodele/reqmodparalst/" + data[i]['id']),
                            reader : { type : 'json', root : 'data', totalProperty : 'total' }
                        },
                        idProperty : 'id',
                        //autoLoad : true,
                        restful : true,
                        fields : [ { name : data[i]['sourceSqlValeur'], type : 'string' },
                                   { name : data[i]['sourceSqlLibelle'], type : 'string' }
                        ]
                    });
                    panelFather.add({
                        xtype: 'combo',
                        itemId : 'param-'+data[i]['id'],
                        fieldLabel : data[i]['formulaireEtiquette'],
                        allowBlank : !data[i]['obligatoire'],
                        value : data[i]['formulaireValeurDefaut'],
                        // Propre à la combo
                        store: paramLstLikeDStore,
                        valueField: data[i]['sourceSqlValeur'],
                        displayField: data[i]['sourceSqlLibelle'],
                        hiddenName : 'id',
                        typeAhead: true,
                        mode : 'remote',
                        triggerAction : 'all',
                        forceSelection: true,
                        hideTrigger : false,
                        editable : true,
                        minChars: 1,
                        emptyText : "Sélectionner une valeur",
                        name : data[i]['nom']

                    });
                    break;
                // DateField
                case 'datefield':
                    panelFather.add({
                        xtype : data[i]['formulaireTypeControle'],
                        itemId : 'param-'+data[i]['id'],
                        fieldLabel : data[i]['formulaireEtiquette'],
                        allowBlank : !data[i]['obligatoire'],
                        value : data[i]['formulaireValeurDefaut'],
                        format : 'd/m/Y',
                        name : data[i]['nom']

                    });

                    break;

                // Composant par défaut
                case 'checkbox':
                    panelFather.add({
                        xtype : data[i]['formulaireTypeControle'],
                        itemId : 'param-'+data[i]['id'],
                        fieldLabel : data[i]['formulaireEtiquette'],
                        allowBlank : !data[i]['obligatoire'],
                        checked : data[i]['formulaireValeurDefaut'] == 'true' ? true : false,
                        name : data[i]['nom']
                    });

                    break;
                case 'datetimefield':
                //le champ est la cncaténation d'un param-date-[id] et param-time-[id] et le résultat est enregistré dans param[id]
                    panelFather.add({
                        xtype: 'fieldcontainer',
                        layout: 'hbox',
                        itemId: 'temp',
                        fieldLabel:  data[i]['formulaireEtiquette'],
                        items: [{
                         xtype: 'datefield',
                         itemId: 'param-date-'+data[i]['id'],
                         labelAlign: 'left',
                         width: 200,
                         name: 'date',
                         value: Ext.Date.format(new Date(data[i]['formulaireValeurDefaut']), 'd/m/Y'),
                         allowBlank : !data[i]['obligatoire'],
                         format: 'd/m/Y',
                         listeners: {
                             change: this.onChangeDateField
                             }
                         }, {
                         xtype: 'timefield',
                         itemId: 'param-time-'+data[i]['id'],
                         name: 'time',
                         fieldLabel: 'à',
                         labelAlign: 'left',
                         margin: '0 0 0 10',
                         labelWidth: 25,
                         width: 220,
                         minValue: "00:00",
                         value: Ext.Date.format(new Date(data[i]['formulaireValeurDefaut']), 'H:i:s'),
                         format: 'H:i:s',
                         allowBlank : !data[i]['obligatoire'],
                         increment: 15,
                          listeners: {
                          change: this.onChangeTimeField
                          }
                    }]},{
                     xtype: 'textfield',
                     itemId: 'param-'+data[i]['id'],
                     width: 120,
                     labelWidth: 30,
                     labelAlign: 'left',
                     hidden: true,
                     name: data[i]['nom'],
                     value: Ext.Date.format(new Date(), 'Y-m-d h:m:s')
                    });

                    break;
                    case 'timefield':
                     panelFather.add({
                         xtype: data[i]['formulaireTypeControle'],
                         itemId: 'param-'+data[i]['id'],
                         name: data[i]['nom'],
                         fieldLabel:  data[i]['formulaireEtiquette'],
                         margin: '0 0 0 10',
                         labelWidth: 20,
                         minValue: "00:00",
                         value: data[i]['formulaireValeurDefaut'],
                         format: 'H:i:s',
                         allowBlank : !data[i]['obligatoire'],
                         increment: 15
                     });
                     break;
                default:
                     panelFather.add({
                       xtype : data[i]['formulaireTypeControle'].includes('geom') ? 'textfield' : data[i]['formulaireTypeControle'],
                       itemId : 'param-'+data[i]['id'],
                       fieldLabel : data[i]['formulaireEtiquette'],
                       allowBlank : !data[i]['obligatoire'],
                       value : data[i]['formulaireValeurDefaut'],
                       name : data[i]['nom']

                     });
                   break;
            }
            this.tab_items.push('param-'+data[i]['id']);
            panelFather.doLayout();
        }

    },

     deleteParams : function() {
             var compFather =  this.getTabAnalyses().getComponent('formRequetage');
             var infoParam = compFather.getComponent('infoParam');
             var temp = compFather.getComponent('temp');
             if (infoParam) {
                 compFather.remove(infoParam);
             }
             if (temp) {
              compFather.remove(temp);
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

     onChangeDateField: function(datefield, newValue, oldValue) {
         var id = datefield.getItemId().substring(11);
         var date  =  Ext.Date.format(newValue,'Y-m-d')+" "+Ext.Date.format(Ext.ComponentQuery.query('#param-time-'+id)[0].getValue(),'h:m:s');
         Ext.ComponentQuery.query('#param-'+id)[0].setValue(date);

     },

     onChangeTimeField: function(timefield, newValue, oldValue) {
        var id = timefield.getItemId().substring(11);
        var date  = Ext.Date.format(Ext.ComponentQuery.query('#param-date-'+id)[0].getValue(),'Y-m-d')+" "+ Ext.Date.format(newValue,'h:m:s');
        Ext.ComponentQuery.query('#param-'+id)[0].setValue(date);
     },

     execRqt : function() {
         var formP = this.getTabAnalyses().getComponent('formRequetage');
         if (!formP.getForm().isValid()) {
             Ext.MessageBox.show({ title : this.title, msg : "Les paramètres ne sont pas tous valides.<br/>Veuillez corriger les erreurs avant de valider.", buttons : Ext.Msg.OK,
                 icon : Ext.MessageBox.ERROR });
             return;
         }

         var data = [];
         var idmodele = formP.getComponent('requeteModele').getValue();
         var spatial = formP.getComponent('requeteModele').findRecord('id',idmodele).get('spatial');
       if(spatial) {
          this.getTabRequetage().query('crHydrantsMapRequetage')[0].tab.show();
       }else {
          this.getTabRequetage().query('crHydrantsMapRequetage')[0].tab.hide();
          //On bascule sur getTabDonnees ou cas où on a lancé la requete dans la carte
          this.getTabDonnees().show();
       }
         var i;
         for (i = 0; i < this.tab_items.length; i++) {
             var comp = formP.getComponent(this.tab_items[i]);
             var value;
             if (comp.xtype == 'datefield') {
                 value = Ext.Date.format(comp.getValue(), 'Y-m-d h:m:s');
             }else if (comp.xtype == 'timefield') {
                 value = Ext.Date.format(comp.getValue(), 'H:m:s');
             } else {
                 value = comp.getValue();
             }
             var valeur = "";
             if(value != null) {
                 valeur = value.toString();
             }

             var nom = comp.getName() != null ? comp.getName() : "";
             data.push({ nomparametre: nom , valeur : valeur});
         }

         formP.getForm().submit(
                 {
                     params : { jsonValeurs : Ext.JSON.encode(data) },
                     url : Sdis.Remocra.util.Util.withBaseUrl("../requetemodele/" + idmodele),
                     method : 'POST',
                     scope : this,
                     success : function(form, action) {
                     //La requete retourne le modèle du tableau
                     this.createDonnees(action.result.message,spatial);
                     },

                     failure : function(form, action) {
                         Ext.MessageBox.show({ title : this.title, msg : "Un problème est survenu lors de l\'exécution de la requête.", buttons : Ext.Msg.OK,
                             icon : Ext.MessageBox.ERROR });
                 }
         });
     },

  createDonnees: function(data, spatial) {
        var json = JSON.parse(data);
        var grid = this.getTabDonnees();
        var idSelection = json[0].requete;
        var map = this.getTabRequetage().query('crHydrantsMapRequetage')[0];
        var exportRequete = this.getTabRecherche().queryById('exportRequete');
        var deleteRequete = this.getTabRecherche().queryById('deleteRequete');
        colModel = [];
        grid.reconfigure(store, colModel);

      //Construction du tableau
       var fields = [];
       var columns = [];
       var i=0;
       for (i; i<json.length;i++){
          // On détermine les colonnes du tableau
          var column = {header: json[i].header ,
           dataIndex: json[i].header,
           width: json[i].header.length * 10 < 100 ? 100 : json[i].header.length *10,
           menuDisabled: true};

          //On détermine les fields pour le store du tableau
          var field = {name: json[i].header};
          fields.push(field);
          columns.push(column);
       }
       var store = Ext.create('Ext.data.Store',
         {fields: fields,
          pageSize: 25,
          remoteSort: true,
          remoteFilter: true,
          autoLoad: false,
          proxy : {
                 type   : 'ajax',
                 url    : Sdis.Remocra.util.Util.withBaseUrl("../requetemodele/reqmodresult/" + idSelection),
                 reader : {
                     type : 'json',
                     root : 'data'
                 }
          }
       });

       var tbar = grid.query('pagingtoolbar')[0];
       tbar.bindStore(store, true);
       store.load({

                    callback : function(records, operation, success) {
                     grid.reconfigure(store,columns);
                     exportRequete.setDisabled(false);
                     deleteRequete.setDisabled(false);
                     //On enregiste l'id de larequete dans les param du bouton export
                     exportRequete.setParams({spatial : spatial , requete : idSelection});
                     deleteRequete.setParams({requete : idSelection});
                     map.setIdSelection(idSelection);
                     if(map.isVisible()){
                       //si la map est visible on joue la sélection tout de suite et on zoom sue le bbox du resultat
                       map.workingLayer.removeAllFeatures();
                       map.refreshZonesLayer();
                       this.zoomBBOx(idSelection);
                     }
                     if(records.length ===0){
                         Sdis.Remocra.util.Msg.msg('Résultat de la sélection',
                                                'Aucune donnée disponible pour cette sélection');
                     }

                 },
                 scope: this
       });

     },

    onDeleteRequete: function(button){
     var idSelection = button.params.requete;
     var grid = this.getTabDonnees();
     var exportRequete = this.getTabRecherche().queryById('exportRequete');
     var deleteRequete = this.getTabRecherche().queryById('deleteRequete');
         Ext.Ajax.request({
                 url: Sdis.Remocra.util.Util.withBaseUrl("../requetemodele/"+idSelection),
                 method: 'DELETE',
                 scope: this,
                 callback: function(options, success, response) {
                     if (success == true) {
                      Sdis.Remocra.util.Msg.msg('ANALYSE', 'La sélection a bien été supprimée.');
                      //On reinitialise tous les composants
                      this.getTabRequetage().query('crHydrantsMapRequetage')[0].tab.hide();
                      //On reaffiche le tableau au cas ou on était dans la carte
                      this.getTabDonnees().show();
                      grid.getStore().removeAll();
                      grid.query('pagingtoolbar')[0].onLoad();
                      grid.reconfigure(grid.getStore(), []);
                      exportRequete.setDisabled(true);
                      deleteRequete.setDisabled(true);
                      this.deleteParams();
                      Ext.getCmp('idExecButton').setDisabled(true);
                      this.getTabAnalyses().queryById('requeteModele').clearValue();
                      this.getTabAnalyses().queryById('description').setText('');
                     } else {
                         Ext.MessageBox.show({ title : this.title, msg : 'Un problème est survenu lors de la suppression de la sélection.',
                          buttons : Ext.Msg.OK,
                          icon : Ext.MessageBox.ERROR });
                     }
                 }
             });
    },

    onExportRequete: function(button){
    var data = [];
    //On récupère les params eregistré dans le bouton d'export
    data.push(button.params);
    Ext.Ajax.request({
            url: Sdis.Remocra.util.Util.withBaseUrl("../traitements/requetage"),
            method: 'GET',
            params: {
                spatial: button.params.spatial,
                requete: button.params.requete
            },
            scope: this,
            callback: function(options, success, response) {
                if (success == true) {
                    Ext.MessageBox.show({ title : 'Demande de traitement',
                                               msg : 'Votre demande est en cours de traitement.\n Un message électronique vous informera de l\'issue du \n'
                                                + 'traitement.', buttons : Ext.Msg.OK,
                                               icon : Ext.MessageBox.INFO });
                } else {
                    Ext.MessageBox.show({ title : this.title, msg : 'Un problème est survenu lors de l\'enregistrement des paramètres.',
                     buttons : Ext.Msg.OK,
                     icon : Ext.MessageBox.ERROR });
                }
            }
        });
    },

    zoomBBOx: function(id) {
    var map = this.getTabRequetage().query('crHydrantsMapRequetage')[0];
     Ext.Ajax.request({
               url : Sdis.Remocra.util.Util.withBaseUrl("../requetemodele/reqmodetendu/" +id),
               method : 'GET',
               scope : this,
               callback : function(options, success, response) {
                   if (success == true) {
                       var decodedResp = Ext.decode(response.responseText);
                         if (decodedResp.message!= "null") {
                         //On récupère l'étendu de la sélection
                         var sridBounds = decodedResp.message.split(";");
                         var sridComplet = sridBounds[0];
                         var srid=sridComplet.split('=')[1];
                         var bounds = sridBounds[1];
                           var feature = new OpenLayers.Format.WKT().read(bounds);
                           bounds = Sdis.Remocra.util.Util.getBounds(feature).transform('EPSG:'+srid, map.map.getProjection());
                           //On zoom sur l'étendu
                           map.map.zoomToExtent(bounds, true);
                           map.refreshZonesLayer(id);
                         }

                   } else {
                       Ext.MessageBox.show({ title : this.title, msg : "Un problème est survenu lors du calcul de l'emprise cartographique.",
                           buttons : Ext.Msg.OK, icon : Ext.MessageBox.ERROR });
                   }
               }});

   },

   onTabChange : function(tabPanel, newCard, oldCard) {
      //On joue la requête au moment du passage à la carte
      if (newCard.getXType() == 'crHydrantsMapRequetage') {
          idSelection = newCard.getIdSelection();
          this.zoomBBOx(idSelection);
      }
   }
});
