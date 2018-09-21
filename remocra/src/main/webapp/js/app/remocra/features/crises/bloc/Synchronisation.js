Ext.require('Sdis.Remocra.widget.AbstractDragAndDropGrid');
Ext.require('Sdis.Remocra.features.crises.bloc.Parametres');
Ext.require( 'Sdis.Remocra.store.ProcessusEtlModele');
Ext.require( 'Sdis.Remocra.store.ProcessusEtlModelePlanificationParametre');

Ext.define('Sdis.Remocra.features.crises.bloc.Synchronisation', {

    extend: 'Sdis.Remocra.widget.AbstractDragAndDropGrid',
    title: 'Synchronisation automatique de données',
    id: 'synchronisation',
    alias: 'widget.crise.Synchronisation',

      // create the data store
   getFirstGridStore: function() {
        return  Ext.create('Sdis.Remocra.store.ProcessusEtlModele', {
        autoLoad: true,
          filters : {
                property: 'categorie',
                value   : 'GESTION_CRISE'
          }
          });
      },


      // Column Model shortcut array
      getColumns: function() {
          return  [{flex: 1,
           sortable: true,
            dataIndex: 'libelle',
            renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                          Ext.Array.forEach(record.parametres().data.items, function(item){
                            if("EXPRESSION_CRON" === item.data.parametre){
                              var cron = item.data.valeur.substring(4, item.data.valeur.indexOf('*')-1);
                              metaData.tdAttr ='<div  data-qclass="qtip-commune-crise" data-qtip="'+"Expression-Cron = "+ cron + '"></div';
                            }
                          });
                         return value;
                 }
                 }];
      },

      getSecondGridStore: function() {
       return  Ext.create('Sdis.Remocra.store.ProcessusEtlModele', {
               autoLoad: false
              });
      },

       moveTo: function(selection, right) {
                  var grid = this.secondGrid;
                  if (right && selection.length !==0) {
                      var win = Ext.create('Sdis.Remocra.features.crises.bloc.Parametres', {
                            listeners: {
                                       scope: this,
                                       valid: function(record) {
                                              if(this.validateParametre(win, selection)){
                                                this.moveToRightEvent(selection);
                                                win.close();
                                              }
                                       },
                                       beforerender: function(){
                                          Ext.Ajax.request({
                                             url : Sdis.Remocra.util.Util.withBaseUrl("../processusetlmodele/processusetlmodelparam/" + selection[0].data.id),
                                             method : 'GET',
                                             scope : this,
                                             callback : function(options, success, response) {
                                                 if (success == true) {
                                                     var decodedResp = Ext.decode(response.responseText);
                                                     //On construit la liste des parametres
                                                    /* if(decodedResp.data.length === 0 ){
                                                         this.moveToRightEvent(selection);
                                                     }else{*/
                                                       this.deleteParams(win);
                                                       this.buildParams(win, decodedResp.data);
                                                       win.show();
                                                     //}
                                                 } else {
                                                     Ext.MessageBox.show({ title : this.title, msg : "Un problème est survenu lors de la récupération des modèles de processus.",
                                                         buttons : Ext.Msg.OK, icon : Ext.MessageBox.ERROR });
                                                 }
                                             }
                                          });
                                       }
                                   }
                               });
                            win.fireEvent('beforerender', this);

                  }else {
                      grid = this.firstGrid;
                      this.moveToLeftEvent(selection);
                  }
                   // Tri automatique après avoir déplacé un élément
                   // Voir s'il faut rajouter un paramètre pour trier automatiquement
                   grid.getStore().sort('libelle', 'ASC');

       },
       deleteParams : function(win) {
               var compFather =  win;
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
        buildParams : function(win, data) {
               this.tab_items = [];
                var panelFather = win.getComponent('formParametre');
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
                                   url : Sdis.Remocra.util.Util.withBaseUrl("../processusetlmodele/processusetlmodparalst/" + data[i]['id']),
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
                                   url : Sdis.Remocra.util.Util.withBaseUrl("../processusetlmodele/processusetlmodparalst/" + data[i]['id']),
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
                            allowBlank : !data[i]['obligatoire'],
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
                               xtype : data[i]['formulaireTypeControle'],
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
             validateParametre : function(win, selection) {
                     var formP = win.down('form');
                     var valid = formP.getForm().isValid();
                     var data = [];
                      var i;
                      if (valid){
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

                              var idParametre = comp.getItemId() != null ? comp.getItemId().substring(6) : "";
                              data.push({parametre: idParametre , valeur: valeur});
                          }
                          //par défaut on rajoute le cron expression
                          data.push({parametre: 'EXPRESSION_CRON', valeur: formP.getComponent('Expression').getValue()});
                          selection[0].parametres().removeAll();
                          selection[0].parametres().add(data);
                      }
                     return valid;
             }

});