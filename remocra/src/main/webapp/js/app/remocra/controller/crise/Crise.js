Ext.require('Sdis.Remocra.features.crises.CreationCrise');
Ext.require('Sdis.Remocra.features.crises.ExportCrise');
Ext.require('Sdis.Remocra.features.crises.FusionCrise');
Ext.require('Sdis.Remocra.features.crises.bloc.CommuneCrise');
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
    },{
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
            'gridFusionCrise': {
               select: this.showFusionSelection
            },
            'creationCrise combo[name=choixServices]':{
               change: this.changeServiceSelection
            },
            'creationCrise #validCrise':{
               click: this.saveCrise
            },
            'creationCrise #gridServices':{
               render: this.filterCouches
            },
            'fusionCrise #validFusion':{
               click: this.fusionneCrise
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
                grid.queryById('exportCrise').setDisabled(!Sdis.Remocra.Rights.hasRight('CRISE_R'));
                grid.queryById('ouvrirCrise').setDisabled(dateCloture != null || !Sdis.Remocra.Rights.hasRight('CRISE_R'));
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
                      nomCrise: crise.nom,
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
         var gridRepertoires = fiche.down('#repertoire'), gridProcessus = fiche.down('#synchro'), gridServices = fiche.down('#gridServices');

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

    showFusionSelection: function(sel, records, index, opt){
       Sdis.Remocra.util.Msg.msg('style selectionné  '+ records.data.id);
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

    saveCrise: function(){
        var fiche = this.getCreationCrise(), gridRepertoires = fiche.down('#repertoire'), gridProcessus = fiche.down('#synchro'), gridServices = fiche.down('#gridServices');
        var crise = (fiche.crise && !fiche.crise.phantom) ? fiche.crise : Ext.create('Sdis.Remocra.model.Crise',null);
        crise.set('nom', fiche.down('textfield[name=nomCrise]').getValue());
        crise.set('description', fiche.down('field[name=descriptionCrise]').getValue());
        var dateActivation = fiche.down('datefield[name=dateDebutCrise]').getValue();
        var timeActivation = fiche.down('timefield[name=timeDebutCrise]').getValue();
        dateActivation.setHours(timeActivation.getHours(),timeActivation.getMinutes());
        crise.set('activation', dateActivation);

        var dateCloture = fiche.down('datefield[name=dateFinCrise]').getValue();
        var timeCloture = fiche.down('timefield[name=timeFinCrise]').getValue();
        if(dateCloture != null){
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
      var couches = [];
      // on vérifie si le composant est rendu (sinon il va
      // prendre toutes les caractéristiques par défaut)
      if (gridServices.rendered) {
          tabRight.eachChild(function(child, index, array) {
              var children = child;
              var childNodes = [].concat(children.childNodes);
              Ext.each(childNodes, function(child) {
                  couches.push(child.raw.definition);
              });
          });
      }
        crise.set('carte', couches);

        var form = fiche.down('form[name=ficheCreation]').getForm();
       if(form.isValid()){
                var msgErrorField = fiche.down('displayfield[name=errorMsg]');
                if(crise.get('cloture') && crise.get('cloture')< crise.get('activation')){
                  form.findField('dateFinCrise').markInvalid("");
                  form.findField('timeFinCrise').markInvalid("");
                  msgErrorField.setValue("La date de clôture ne peut pas être antérieure à la date d'activation");
                  msgErrorField.setVisible(true);
                  return;
                }
              if(crise && !crise.phantom){
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
            var couches = [];
            var codes = [];
            if (crise) {
              couches = crise.ogcCouches();
              couches.each(function(record) {
                 codes.push(record.data.code);
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
                if(couches.length !== 0){
                couches.each(function(record) {
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
     }/*,

    removeReadOnly: function(component) {
       if (Ext.isFunction(component.cascade)) {
           component.cascade(function(item) {
               if (Ext.isFunction(item.setReadOnly)) {
                   item.setReadOnly(false);
               }
               if (Ext.isFunction(item.hideTrigger)) {
                   item.triggerEl.show();
               }
               if (item.getToolbar) {
                   toolbar = item.getToolbar();
               }
               if (item.isXType('button')) {
                  item.setDisabled(false);
               }
               if (item.isXType('grid')) {
                   Ext.Array.each(item.plugins, function(plugin) {
                       plugin.beforeEdit = function() {
                           return true;
                       };
                   });
               }
           });
       }
    }*/


});
