Ext.require('Sdis.Remocra.features.crises.CreationCrise');
Ext.require('Sdis.Remocra.features.crises.ExportCrise');
Ext.require('Sdis.Remocra.features.crises.FusionCrise');
Ext.require('Sdis.Remocra.features.crises.bloc.CommuneCrise');
Ext.require('Sdis.Remocra.features.crises.bloc.ActivationCrise');
Ext.require('Sdis.Remocra.features.crises.bloc.ClotureCrise');
Ext.require('Sdis.Remocra.features.crises.bloc.GridFusionCrise');
Ext.require('Sdis.Remocra.features.crises.bloc.ServicesCrise');
Ext.require('Sdis.Remocra.features.crises.bloc.Repertoires');
Ext.require('Sdis.Remocra.model.OgcCouche');
Ext.require('Sdis.Remocra.model.Crise');
Ext.require('Sdis.Remocra.store.Commune');
Ext.require('Sdis.Remocra.store.TypeCriseStatut');
Ext.require('Sdis.Remocra.store.TypeCrise');

Ext.require('Sdis.Remocra.store.Crise');
Ext.define('Sdis.Remocra.controller.crise.Crise', {
    extend: 'Ext.app.Controller',

    stores: ['Crise', 'Commune', 'TypeCrise', 'TypeCriseStatut'],

    refs: [{
        ref: 'tabPanel',
        selector: 'crCrise'
    }, {
        ref: 'tabGeneral',
        selector: 'crCrisesGeneral'
    }, {
       ref: 'creationCrise',
       selector: 'creationCrise'
    }, {
       ref: 'activationCrise',
       selector: 'activationCrise'
    }, {
       ref: 'clotureCrise',
       selector: 'clotureCrise'
    }, {
       ref: 'exportCrise',
       selector: 'exportCrise'
    },{
       ref: 'fusionCrise',
       selector: 'fusionCrise'
    },{
       ref:'communeCrise',
       selector:'communeCrise'
    },{
       ref:'gridFusionCrise',
       selector:'gridFusionCrise'
    },{
        ref: 'tabMap',
        selector: 'crCrisesMapCrise'
    }
    ],

    init: function() {


        this.control({
            'crCrise': {
                tabchange: this.onTabChange,
                urlChanged: this.onUrlChanged,
                afterrender: this.initFromUrl
            },
            'crCrisesGeneral': {
               afterrender: this.onRenderGeneral,
               selectionchange: this.onSelectCrise
            },
            'crCrisesGeneral #creerCrise':{
               click: this.showCreationCrise
            },
            'crCrisesGeneral #configureCrise':{
               click: this.showConfigureCrise
            },
            'crCrisesGeneral #cloreCrise':{
               click: this.showCloreCrise
            },
            'crCrisesGeneral #exportCrise':{
               click: this.showExportCrise
            },
            'crCrisesGeneral #ouvrirCrise':{
               click: this.openCardCrise
            },
            'crCrisesGeneral #fusionneCrise':{
               click: this.showFusionneCrise
            },
            'creationCrise':{
               afterrender: this.loadCriseDetail
            },
            'communeCrise #addCommuneCrise': {
               click: this.addCommuneToCrise
            },
            'communeCrise #deleteCommuneCrise': {
               click: this.deleteCommuneFromCrise
            },
            'creationCrise combo[name=choixServices]':{
               change: this.changeServiceSelection
            },
            'activationCrise timefield[name=timeDebutCrise]':{
               change: this.checkTimeActivation
            },
            'activationCrise datefield[name=dateDebutCrise]':{
               change: this.checkTimeActivation
            },
            'clotureCrise timefield[name=timeFinCrise]':{
               change: this.checkTimeCloture
            },
            'clotureCrise datefield[name=dateFinCrise]':{
               change: this.checkTimeCloture
            },
            'creationCrise #validCrise':{
               click: this.saveCrise
            },
            'creationCrise #gridServices':{
               render: this.filterCouches
            },
            'fusionCrise #validFusion':{
               click: this.fusionneCrise
            },
            'exportCrise':{
               render: this.loadExportData
            },
            'exportCrise #validExportCrise':{
               click: this.execExport
            }


        });
    },

    /***********************************************************
     * Gestion URL / méthode globale
     **********************************************************/

    onTabChange: function(tabPanel, newCard, oldCard) {

        var currentHash = Sdis.Remocra.util.Util.getHashTokenNoSharp();
        if (!Ext.isEmpty(currentHash)) {
            currentHash = currentHash.split('/');
            if (currentHash.length >= 2 && currentHash[1] == newCard.itemId) {
                return;
            }
        }
        // Changement "manuel" d'onglet -> on change l'url
        Sdis.Remocra.util.Util.changeHash('crises/' + newCard.itemId);
    },
    initFromUrl: function() {

        var p2 = this.getTabPanel().p2, extra = this.getTabPanel().extraParams;
        this.onUrlChanged(p2,extra);
    },

    onUrlChanged: function(itemId, extraParams) {

    var currentTab = this.getTabPanel().getActiveTab();
    extraParams = extraParams || {};
    if (Ext.isObject(itemId)) {
       extraParams = itemId.extraParams;
       if(itemId.p2 === 'localisation'){
         itemId = 'localisation/a/'+extraParams['a'];
       } else {
         itemId = itemId.p2;
       }
    }
    if (currentTab.itemId != itemId) {
     // on switch sur le bon onglet
     this.getTabPanel().setActiveTab(this.getTabPanel().queryById(itemId));
    }

        switch (itemId) {
        case 'general':
            this.updateCrise(extraParams);
        break;
        case 'index':
            this.updateCrise(extraParams);
        break;
        case 'localisation':
            this.updateCrise(extraParams);
        break;
        default:
        break;
        }
    },

    updateCrise: function(extraParams) {
       this.getTabGeneral().getStore().load();
    },

    onRenderGeneral: function(){
       var grid = this.getTabGeneral();
       grid.queryById('creerCrise').setDisabled(!Sdis.Remocra.Rights.hasRight('CRISE_C'));
       var tbar = grid.query('pagingtoolbar')[0];
       tbar.bindStore(grid.getStore());

    },

    onSelectCrise: function(sel, records, index, opt) {
       var grid = this.getTabGeneral();
           var dateCloture = records.length != 0 ? records[0].data.cloture : null ;
           var nbCommune = records.length != 0 ? records[0].communesStore.data.length : 0 ;
                grid.queryById('exportCrise').setDisabled(!Sdis.Remocra.Rights.hasRight('CRISE_R'));
                grid.queryById('ouvrirCrise').setDisabled(dateCloture != null || nbCommune == 0 || !Sdis.Remocra.Rights.hasRight('CRISE_R'));
                grid.queryById('fusionneCrise').setDisabled(dateCloture != null || !Sdis.Remocra.Rights.hasRight('CRISE_U'));
                grid.queryById('configureCrise').setDisabled(dateCloture != null ||!Sdis.Remocra.Rights.hasRight('CRISE_U'));
                grid.queryById('cloreCrise').setDisabled(dateCloture != null ||!Sdis.Remocra.Rights.hasRight('CRISE_U'));

    },

    showCreationCrise: function(){
       var ficheCreation = Ext.widget('creationCrise');
       ficheCreation.show();
    },

    showConfigureCrise: function(){
       var grid = this.getTabGeneral();
       var criseId = grid.getSelectionModel().getSelection()[0].data.id;
       var tabPanel = this.getTabPanel();
       tabPanel.items.each(function(item, index, len) {
          if (item.xtype == 'crCrisesMapCrise' && item.getItemId() == 'localisation/a/'+criseId) {
              tabPanel.remove(item, true);
          }
       });
       var model = Sdis.Remocra.model.Crise;
        if (model != null) {
                  model.load(criseId, {
                      scope: this,
                      success: function(record) {
                        var ficheConfiguration = Ext.widget('creationCrise', {'crise': record});
                        ficheConfiguration.show();
                      }
                  });
              }

    },
    openCardCrise: function(){
       var grid = this.getTabGeneral();
       var crise = grid.getSelectionModel().getSelection()[0].data;
       Ext.Ajax.request({
           scope: this,
           method: 'GET',
           url: Sdis.Remocra.util.Util.withBaseUrl('../crises/'+crise.id+'/geometrie'),
           callback: function(param, success, response) {
               if(success){
                var decodedResp = Ext.decode(response.responseText);
                  if (decodedResp.data!= "null") {
                    var sridBounds = decodedResp.data.split(";");
                    var sridComplet = sridBounds[0];
                    var srid=sridComplet.split('=')[1];
                    var bounds = sridBounds[1];
                    var tabPanel = this.getTabPanel();
                      var cardTab = null;
                      tabPanel.items.each(function(item, index, len) {
                            if (item.xtype == 'crCrisesMapCrise' && item.getItemId() == 'localisation/a/'+crise.id) {
                                cardTab = item;
                            }
                      }, tabPanel);
                    var currentTab = tabPanel.getActiveTab();
                    if (cardTab) {
                        if (currentTab != cardTab) {
                            // on switch sur le bon onglet s'il existe
                            tabPanel.setActiveTab(cardTab);
                        }
                    //sinon on le crée
                    }else {
                      cardTab = tabPanel.add({
                      closable : true,
                      xtype : 'crCrisesMapCrise',
                      nomCrise: crise.nom +' '+ Ext.util.Format.date(crise.activation, 'd/m/Y à H:i'),
                      idCrise: crise.id,
                      carteCrise: crise.carte,
                      bbox: bounds,
                      itemId: 'localisation/a/'+crise.id});
                      tabPanel.setActiveTab(cardTab);
                    }
                  }
               }
           }
       });

    },
    loadCriseDetail: function(fiche){
       if(fiche.crise){
         var crise = fiche.crise;
         fiche.down('textfield[name=nomCrise]').setValue(crise.data.nom);
         fiche.down('combo[name=typeCrise]').setValue(crise.getTypeCrise());
         fiche.down('field[name=descriptionCrise]').setValue(crise.data.description);
         fiche.down('datefield[name=dateDebutCrise]').setValue(crise.data.activation);
         fiche.down('timefield[name=timeDebutCrise]').setValue(crise.data.activation);
         fiche.down('datefield[name=dateFinCrise]').setValue(crise.data.cloture);
         fiche.down('timefield[name=timeFinCrise]').setValue(crise.data.cloture);
         var gridCommune = fiche.down('#gridCommuneCrise');
         gridCommune.bindStore(crise.communes());
         var gridRepertoires = fiche.down('#repertoire'), gridProcessus = fiche.down('#synchro'), sourceTab = fiche.down('#sources');

         Ext.defer(function() {
           Ext.Array.forEach(crise.repertoireLieusStore.data.items, function(item){
             gridRepertoires.moveToRightEvent(gridRepertoires.firstGrid.getStore().findRecord("id",item.data.id));
           });
         },500, this);

         Ext.defer(function() {
           Ext.Array.forEach(crise.processusEtlPlanificationsStore.data.items, function(item){
             //On passe les plannifications existantes à gauche et on leurs associes leurs valeurs de parametres
             var recordToMove = gridProcessus.firstGrid.getStore().findRecord("id",item.data.modele);
             recordToMove.parametres().add(item.raw.processusEtlPlanificationParametres);
             var cron = item.raw.expression.substring(4, item.raw.expression.indexOf('*')-1);
             recordToMove.parametres().add({parametre:"EXPRESSION_CRON", valeur:cron});
             gridProcessus.moveToRightEvent(recordToMove);
             });
         },500, this);

          Ext.defer(function() {
             sourceTab.doAutoRender();
          },500,this);
       }
    },

    addCommuneToCrise: function(){
       var communeConcerne =  this.getCommuneCrise();
       var grid = this.getCommuneCrise().down('#gridCommuneCrise');
       var comboCommune = this.getCommuneCrise().down('combo[name=communeCrise]');
       var commune = comboCommune.findRecordByValue(comboCommune.getValue());
       if(commune){
          if(grid.store.findRecord("id", commune.getId(),0, false, true, true) == null) {
             grid.store.add(commune);
             comboCommune.clearInvalid();
             grid.getView().refresh();
          }
       }
    },

    deleteCommuneFromCrise : function() {
          var grid = this.getCommuneCrise().down('#gridCommuneCrise');
          var commune = grid.getSelectionModel().getSelection();
          grid.getStore().remove(commune);
    },

    showCloreCrise: function(){
       var grid = this.getTabGeneral();
       var criseId = grid.getSelectionModel().getSelection()[0].data.id;
       var tabPanel = this.getTabPanel();
       tabPanel.items.each(function(item, index, len) {
         if (item.xtype == 'crCrisesMapCrise' && item.getItemId() == 'localisation/a/'+criseId) {
             tabPanel.remove(item, true);
         }
       });
       var model = Sdis.Remocra.model.Crise;
        if (model != null) {
                  model.load(criseId, {
                      scope: this,
                      success: function(record) {
                        var ficheClore = Ext.widget('creationCrise', {'crise': record});
                        this.setReadOnly(ficheClore);
                        ficheClore.down('datefield[name=dateFinCrise]').setDisabled(false);
                        ficheClore.down('timefield[name=timeFinCrise]').setDisabled(false);
                        ficheClore.down('datefield[name=dateFinCrise]').setReadOnly(false);
                        ficheClore.down('timefield[name=timeFinCrise]').setReadOnly(false);
                        ficheClore.show();
                      }
                  });
              }
    },

    showExportCrise: function(){
      Ext.widget('exportCrise').show();
    },

    loadExportData: function(){
        Ext.Ajax.request({
                      url : Sdis.Remocra.util.Util.withBaseUrl("../processusetlmodele/processusetlmodelparam/" + 1),
                      method : 'GET',
                      scope : this,
                      callback : function(options, success, response) {
                          if (success == true) {
                              var decodedResp = Ext.decode(response.responseText);
                               this.deleteParamsExport();
                              this.buildParamsForExport(decodedResp.data);
                          } else {
                              Ext.MessageBox.show({ title : this.title, msg : "Un problème est survenu lors de la récupération des modèles de requête.",
                                  buttons : Ext.Msg.OK, icon : Ext.MessageBox.ERROR });
                          }
                      }});
    },
    deleteParamsExport : function() {
             var compFather =  this.getExportCrise().getComponent('formExport');
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


    buildParamsForExport : function(data) {
        this.tab_items = [];
        var panelFather = this.getExportCrise().getComponent('formExport');
        var crise = this.getTabGeneral().getSelectionModel().getSelection();
        panelFather.add({
            xtype:'panel', border: false, style: 'margin-bottom:10px;',
            itemId: 'infoParam', html: (data.length>0?'Veuillez renseigner les paramètres suivants :':'Aucun paramètre pour cet export')
        });
        var i;
        for (i = 0; i < data.length; i++) {
            var typeControle = data[i]['formulaireTypeControle'];

            switch (typeControle) {
                // Static Combo
                case 'staticcombo':
                    var paramLstDStore = new Ext.data.JsonStore({
                        autoLoad: true,
                        proxy : {
                            format : 'json',
                            type : 'rest',
                            headers : { 'Accept' : 'application/json,application/xml', 'Content-Type' : 'application/json' },
                            url : Sdis.Remocra.util.Util.withBaseUrl("../processusetlmodele/processusetlmodparalst/" + data[i]['id']),
                            //On filtre pour récupérer la valeur par défaut à l'index 0
                            extraParams : {query: data[i]['formulaireValeurDefaut']},
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
                        itemId : 'input'+data[i]['id'],
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
                     var comboLstD = Ext.ComponentQuery.query('#input'+data[i]['id'])[0];
                     this.setDefaultValue(comboLstD);
                    break;

                // Combo avec requête Saisie et requête de type "like" sur le libellé
                case 'combo':
                    var paramLstLikeDStore = new Ext.data.JsonStore({
                        autoLoad: true,
                        proxy : {
                            format : 'json',
                            type : 'rest',
                            headers : { 'Accept' : 'application/json,application/xml', 'Content-Type' : 'application/json' },
                            url : Sdis.Remocra.util.Util.withBaseUrl("../processusetlmodele/processusetlmodparalst/" + data[i]['id']),
                             //On filtre pour récupérer la valeur par défaut à l'index 0
                             //dans ce cas le like est sur le nom et non pas l'id attention au appostrophe et doublon nom crise
                            extraParams : {query: data[i]['nom'] == "CRISE_ID" ? crise[0].data.nom.replace('\'', '\'\'') : data[i]['formulaireValeurDefaut']},
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
                        itemId : 'input'+data[i]['id'],
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
                    var comboLstLike = Ext.ComponentQuery.query('#input'+data[i]['id'])[0];
                    this.setDefaultValue(comboLstLike);
                    break;
                // DateField
                case 'datefield':
                    panelFather.add({
                        xtype : data[i]['formulaireTypeControle'],
                        itemId : 'input'+data[i]['id'],
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
                        itemId : 'input'+data[i]['id'],
                        fieldLabel : data[i]['formulaireEtiquette'],
                        allowBlank : !data[i]['obligatoire'],
                        checked : data[i]['formulaireValeurDefaut'] == 'true' ? true : false,
                        name : data[i]['nom']
                    });

                    break;
                case 'datetimefield':
                //le champ est la cncaténation d'un input-date-[id] et input-time-[id] et le résultat est enregistré dans param[id]
                    panelFather.add({
                        xtype: 'fieldcontainer',
                        layout: 'hbox',
                        fieldLabel:  data[i]['formulaireEtiquette'],
                        items: [{
                         xtype: 'datefield',
                         itemId: 'input-date-'+data[i]['id'],
                         labelAlign: 'left',
                         width: 120,
                         name: 'date',
                         value: Ext.Date.format(new Date(data[i]['formulaireValeurDefaut']), 'd/m/Y'),
                         allowBlank : !data[i]['obligatoire'],
                         format: 'd/m/Y',
                         listeners: {
                             change: this.onChangeDateField
                             }
                         }, {
                         xtype: 'timefield',
                         itemId: 'input-time-'+data[i]['id'],
                         name: 'time',
                         fieldLabel: 'à',
                         labelAlign: 'left',
                         margin: '0 0 0 10',
                         labelWidth: 25,
                         width: 120,
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
                     itemId: 'input'+data[i]['id'],
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
                         itemId: 'input'+data[i]['id'],
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
                       itemId : 'input'+data[i]['id'],
                       fieldLabel : data[i]['formulaireEtiquette'],
                       allowBlank : !data[i]['obligatoire'],
                       value : data[i]['formulaireValeurDefaut'],
                       name : data[i]['nom']

                     });
                   break;
            }
            this.tab_items.push('input'+data[i]['id']);
            panelFather.doLayout();
        }

    },

    showFusionneCrise: function(){
         var grid = this.getTabGeneral();
                  var criseId = grid.getSelectionModel().getSelection()[0].data.id;
                  var model = Sdis.Remocra.model.Crise;
                   if (model != null) {
                             model.load(criseId, {
                                 scope: this,
                                 success: function(record) {
                                   var ficheFusion = Ext.widget('fusionCrise', {'crise': record});
                                   ficheFusion.down('#gridFusionCrise').getStore().clearFilter(true);
                                   ficheFusion.down('#gridFusionCrise').getStore().filter([{
                                      property: "typeCrise",
                                      value: ficheFusion.crise.getTypeCrise().data.id
                                   },{
                                   property: "idCrise",
                                   value:ficheFusion.crise.data.id
                                   }]);
                                   ficheFusion.down('#msgFusion').setValue(
                                     "Veuillez séléctionner les crises à fusionner avec la crise: <b>"+ficheFusion.crise.data.nom+"</b>"
                                   );
                                   ficheFusion.down('#gridFusionCrise').getStore().load({
                                      callback: function(records,operation, success){
                                        if(records.length !== 0){
                                            ficheFusion.show();
                                        }else {
                                          Ext.Msg.show({
                                            title: "Fusion impossible",
                                            msg: 'Aucune autre crise de type '+ ficheFusion.crise.getTypeCrise().data.nom+' est en cours actuellement',
                                            buttons: Ext.Msg.OK,
                                            icon: Ext.Msg.WARNING
                                          });

                                        }
                                      }

                                   });

                                 }
                             });
                         }
    },
    fusionneCrise: function(){
      var ficheFusion = this.getFusionCrise();
      var selectedCrises = ficheFusion.down('#gridFusionCrise').getSelectionModel().getSelection();
      var dateFusion = ficheFusion.down('datefield[name=dateFusionCrise]').getValue();
      var timeActivation = ficheFusion.down('timefield[name=timeFusionCrise]').getValue();
      dateFusion.setHours(timeActivation.getHours(),timeActivation.getMinutes());
      var fusionnedCrises = [];
       // on envoie que les ids des crises
       Ext.Array.forEach(selectedCrises, function(crise){
          fusionnedCrises.push(crise.data.id);
       });
       Ext.Ajax.request({
                scope: this,
                method: 'POST',
                url: Sdis.Remocra.util.Util.withBaseUrl('../crises/'+ficheFusion.crise.data.id+'/fusion'),
                jsonData: {
                    fusionnedCrises: fusionnedCrises,
                    dateFusion: dateFusion
                },
                callback: function(param, success, response) {
                    if(success){
                      var res = Ext.decode(response.responseText);
                      Sdis.Remocra.util.Msg.msg("Crise", res.message);
                      ficheFusion.close();
                      this.getTabGeneral().getStore().load();
                    }
                }
      });

    },

    changeServiceSelection: function(combo, records){
       var panel = combo.up('creationCrise'), gridServicesCrise = panel.down('#gridServices');
       var storeLeft = Ext.create('Ext.data.Store', {
            model: 'Sdis.Remocra.model.OgcCouche',
            autoLoad: true,
           proxy: {
                   format: 'json',
                   type: 'rest',
                   headers: {
                       'Accept': 'application/json,application/xml',
                       'Content-Type': 'application/json'
                   },
                   url: Sdis.Remocra.util.Util.withBaseUrl('../ogccouche/'+records),
                   reader: {
                       type: 'json',
                       root: 'data'
                   }
               }

       });
       gridServicesCrise.firstGrid.bindStore(storeLeft);
    },

    checkTimeActivation: function(){
          var fiche = this.getCreationCrise();
          var msgErrorField = fiche.down('displayfield[name=errorMsg]');
          msgErrorField.setVisible(false);
          fiche.down('datefield[name=dateDebutCrise]').clearInvalid();
          fiche.down('timefield[name=timeDebutCrise]').clearInvalid();
          var dateActivation = fiche.down('datefield[name=dateDebutCrise]').getValue();
          var timeActivation = fiche.down('timefield[name=timeDebutCrise]').getValue();
          if(timeActivation != null){
              dateActivation.setHours(timeActivation.getHours(),timeActivation.getMinutes());
          }

          if (dateActivation > new Date()) {
             fiche.down('datefield[name=dateDebutCrise]').markInvalid("");
             fiche.down('timefield[name=timeDebutCrise]').markInvalid("");
             msgErrorField.setValue("La date d'activation ne peut pas être postérieure à la date actuelle");
             msgErrorField.setVisible(true);
             return;
          }
    },

    checkTimeCloture: function(){
          var fiche = this.getCreationCrise();
          var crise = fiche.crise;
          var msgErrorField = fiche.down('displayfield[name=errorMsg]');
          msgErrorField.setVisible(false);
          fiche.down('datefield[name=dateFinCrise]').clearInvalid();
          fiche.down('timefield[name=timeFinCrise]').clearInvalid();
          var dateCloture = fiche.down('datefield[name=dateFinCrise]').getValue();
          var timeCloture = fiche.down('timefield[name=timeFinCrise]').getValue();
          if(dateCloture != null){
             if(timeCloture != null){
                    dateCloture.setHours(timeCloture.getHours(),timeCloture.getMinutes());
             }
             if (dateCloture > new Date()) {
               fiche.down('datefield[name=dateFinCrise]').markInvalid("");
               fiche.down('timefield[name=timeFinCrise]').markInvalid("");
               msgErrorField.setValue("La date de clôture ne peut pas être postérieure à la date actuelle");
               msgErrorField.setVisible(true);
               return;
             } else if (dateCloture < crise.get('activation')) {
               fiche.down('datefield[name=dateFinCrise]').markInvalid("");
               fiche.down('timefield[name=timeFinCrise]').markInvalid("");
               msgErrorField.setValue("La date de clôture ne peut pas être antérieure à la date d'activation");
               msgErrorField.setVisible(true);
               return;
             }
          }

    },

    saveCrise: function(){
        var fiche = this.getCreationCrise(), gridRepertoires = fiche.down('#repertoire'), gridProcessus = fiche.down('#synchro'), gridServices = fiche.down('#gridServices');
        var crise = (fiche.crise && !fiche.crise.phantom) ? fiche.crise : Ext.create('Sdis.Remocra.model.Crise',null);
        crise.set('nom', fiche.down('textfield[name=nomCrise]').getValue());
        crise.set('description', fiche.down('field[name=descriptionCrise]').getValue());
        var dateActivation = fiche.down('datefield[name=dateDebutCrise]').getValue();
        var timeActivation = fiche.down('timefield[name=timeDebutCrise]').getValue();
        if(timeActivation != null) {
          dateActivation.setHours(timeActivation.getHours(),timeActivation.getMinutes());
        }
        crise.set('activation', dateActivation);

        var dateCloture = fiche.down('datefield[name=dateFinCrise]').getValue();
        var timeCloture = fiche.down('timefield[name=timeFinCrise]').getValue();
        if(dateCloture != null && timeCloture != null){
          dateCloture.setHours(timeCloture.getHours(),timeCloture.getMinutes());
          crise.set('cloture', dateCloture);
        }

        crise.setTypeCrise(fiche.down('combo[name=typeCrise]').getValue());
        var selectedCommunes = this.getCommuneCrise().down('#gridCommuneCrise').getStore().data.items;
        crise.communes().removeAll();
        crise.communes().add(selectedCommunes);
        var idCommunes = [];
           Ext.Array.forEach(crise.communes().data.items,function(commune){
           idCommunes.push(commune.data.id);
        });
        var repertoires = [];
        Ext.Array.forEach(gridRepertoires.secondGridStore.data.items, function(repertoire){
           repertoires.push(repertoire.data.id);
        });
        var processusPlanif = [];
        Ext.Array.forEach(gridProcessus.secondGridStore.data.items, function(processus){
            var processusPlanifParametres = [];
               Ext.Array.forEach(processus.parametres().data.items, function(item){
                    if("EXPRESSION_CRON" === item.data.parametre){
                      processus.set('expressionCron',item.data.valeur);
                    } else {
                      processusPlanifParametres.push(item.data);
                    }
               });
            processusPlanif.push({'idModele': processus.data.id, 'expressionCron':processus.data.expressionCron, 'parametres':processusPlanifParametres});
        });

       // mettre à jour l'environnement
      var tabRight = gridServices.getRightPanel().getRootNode();
      var couchesAnt = [];
      var couchesOp = [];
      // on vérifie si le composant est rendu (sinon il va
      // prendre toutes les caractéristiques par défaut)
      if (gridServices.rendered) {
          tabRight.eachChild(function(child, index, array) {
              var children = child;
              var childNodes = [].concat(children.childNodes);
              Ext.each(childNodes, function(child) {

                  if(child.data.isOp){
                     couchesOp.push(child.raw.definition);
                  }
                  if(child.data.isAnt){
                     couchesAnt.push(child.raw.definition);
                  }
              });
          });
      }
       crise.set('carteOp', couchesOp);
       crise.set('carteAnt', couchesAnt);
        var form = fiche.down('form[name=ficheCreation]').getForm();
       if(form.isValid()){
              if (crise.communes().data.length === 0){
                 this.getCommuneCrise().down('combo[name=communeCrise]').markInvalid("");
                 return;
              }
              if(crise && !crise.phantom){
                    this.checkTimeCloture();
                    //On fait un update
                    Ext.Ajax.request({
                              scope: this,
                              method: 'PUT',
                              url: Sdis.Remocra.util.Util.withBaseUrl('../crises/'+ crise.getId()),
                              jsonData: {
                                  crise: crise.data,
                                  typeCrise: crise.getTypeCrise(),
                                  communes: idCommunes,
                                  repertoires: repertoires,
                                  processusPlanif: processusPlanif
                                  //processusPlanifParametres: processusPlanifParametres
                              },
                              callback: function(param, success, response) {
                                  if(success){
                                    var res = Ext.decode(response.responseText);
                                    Sdis.Remocra.util.Msg.msg("Crise", res.message);
                                    fiche.close();
                                    this.getTabGeneral().getStore().load();
                                  }

                              }
                    });
              }else {
                    this.checkTimeActivation();
                    Ext.Ajax.request({
                              scope: this,
                              method: 'POST',
                              url: Sdis.Remocra.util.Util.withBaseUrl('../crises'),
                              jsonData: {
                                  crise: crise.data,
                                  typeCrise: crise.getTypeCrise(),
                                  communes: idCommunes,
                                  repertoires: repertoires,
                                  processusPlanif: processusPlanif
                                  //processusPlanifParametres: processusPlanifParametres
                              },
                              callback: function(param, success, response) {
                                  if(success){
                                    var res = Ext.decode(response.responseText);
                                    Sdis.Remocra.util.Msg.msg("Crise", res.message);
                                    fiche.close();
                                    this.getTabGeneral().getStore().load();
                                  }

                              }
                    });
             }
        }

    },

     filterCouches: function() {
            var fiche = this.getCreationCrise(), gridServices = fiche.down('#gridServices'), crise = fiche.crise;
            var couchesOp = [], couchesAnt = [];
            var codesOp = [], codesAnt = [];
           if (crise) {
              couchesOp = crise.ogcCouchesOp();
              couchesOp.each(function(record) {
                 codesOp.push(record.data.code);
              });

              couchesAnt = crise.ogcCouchesAnt();
                 couchesAnt.each(function(record) {
                 codesAnt.push(record.data.code);
              });
           }
            var tabRight = gridServices.getRightPanel().getRootNode();
            var tabLeft = gridServices.getLeftPanel().getRootNode();
            tabRight.eachChild(function(child, index, array) {
            // eachChild() doesn't make a copy of the children
            // before processing them, so adding or removing
            // children in the callback will break eachChild.
            var children = child;
            var childNodes = [].concat(children.childNodes);
            Ext.each(childNodes, function(child) {
                var codes = [];
                var i = 0;
                if(couchesOp.length !== 0){
                couchesOp.each(function(record) {
                    //On ajoute la proprieté isOp pour déterminer si la case est coché ou pas
                    if(record.raw.code === child.raw.code){
                      child.data.isOp = true;
                    }
                    codes.push(record.data.code);
                });
                }
                if(couchesAnt.length !== 0){
                couchesAnt.each(function(record) {
                    if(record.raw.code === child.raw.code){
                       child.data.isAnt = true;
                    }
                    codes.push(record.data.code);
                });
                }
                if (Ext.Array.contains(codes, child.raw.code)) {
                    // on supprime l'équivalent à gauche
                    leftChild = tabLeft.findChild('nom', child.data.nom, true);
                    leftChild.remove();
                } else {
                    children.removeChild(child);
                }
            });
        });
        },

     setReadOnly: function(component) {
           if (Ext.isFunction(component.cascade)) {
               component.cascade(function(item) {
                   if (Ext.isFunction(item.setReadOnly)) {
                       item.setReadOnly(true);
                   }
                   if (Ext.isFunction(item.hideTrigger)) {
                       item.hideTrigger();
                   }
                   if (item.getToolbar) {
                       toolbar = item.getToolbar();
                   }
                   if (item.isXType('button')) {
                      item.setDisabled(true);
                   }
                   if (item.isXType('grid')) {
                       Ext.Array.each(item.plugins, function(plugin) {
                           plugin.beforeEdit = function() {
                               return false;
                           };
                       });
                       Ext.Array.each(item.columns, function(column) {
                           if (column.isXType('actioncolumn')) {
                               column.destroy();
                           }
                       }, null, true);
                   }
               });
           }
     },

    setDefaultValue : function(combo){
       combo.store.on('load', function(store, records, successful, eOpts) {
           if (records.length>0) {
               this.setValue(records[0]);
           }
       }, combo, {single: true});
     },

    onChangeDateField: function(datefield, newValue, oldValue) {
          var id = datefield.getItemId().substring(11);
          var date  =  Ext.Date.format(newValue,'Y-m-d')+" "+Ext.Date.format(Ext.ComponentQuery.query('#input-time-'+id)[0].getValue(),'h:m:s');
          Ext.ComponentQuery.query('#input'+id)[0].setValue(date);

    },

    onChangeTimeField: function(timefield, newValue, oldValue) {
         var id = timefield.getItemId().substring(11);
         var date  = Ext.Date.format(Ext.ComponentQuery.query('#input-date-'+id)[0].getValue(),'Y-m-d')+" "+ Ext.Date.format(newValue,'h:m:s');
         Ext.ComponentQuery.query('#input'+id)[0].setValue(date);
    },

     execExport : function() {
         var formP = this.getExportCrise().getComponent('formExport');
         if (!formP.getForm().isValid()) {
             Ext.MessageBox.show({ title : this.title, msg : "Les paramètres ne sont pas tous valides.<br/>Veuillez corriger les erreurs avant de valider.", buttons : Ext.Msg.OK,
                 icon : Ext.MessageBox.ERROR });
             return;
         }

         var i;
         var data = [];
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
             data.push(Ext.JSON.encodeValue(comp.getItemId()) +":"+ Ext.JSON.encodeValue(valeur));
         }

         data.push(Ext.JSON.encodeValue("processus") +":"+ Ext.JSON.encodeValue(1));
         data.push(Ext.JSON.encodeValue("priorite") +":"+ Ext.JSON.encodeValue(3));
         Ext.Ajax.request(
                 {
                     jsonData:  "{" + data + "}" ,
                     url : Sdis.Remocra.util.Util.withBaseUrl("../processusetlmodele/withoutfile/" + 1),
                     method : 'POST',
                     scope : this,

                     success : function(form, action) {
                         Sdis.Remocra.util.Msg.msg('Processus Etl','Votre demande est en cours de traitement.\n'+
                          'Un message électronique vous informera de l\'issue du \n' + 'traitement.',5);
                         this.getExportCrise().close();
                     },

                     failure : function(form, action) {
                         Ext.MessageBox.show({ title : this.title, msg : 'Un problème est survenu lors de l\'enregistrement des paramètres.', buttons : Ext.Msg.OK,
                             icon : Ext.MessageBox.ERROR });
                     }

         });
     }

});
