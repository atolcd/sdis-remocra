Ext.require('Ext.grid.Panel');
Ext.require('Sdis.Remocra.model.Crise');
Ext.define('Sdis.Remocra.features.crises.bloc.GridFusionCrise', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.gridFusionCrise',
    store: {
        type : 'crCrise'
    },
    title: 'Crises à fusionner',
    titleAlign: 'center',
    margin: '20 10 10 20',
        forceFit: true,


    columns: [{
        text: 'Nom',
        dataIndex: 'nom'
        },{
         text: 'Activation de la crise',
         align : 'center',
         dataIndex: 'activation',
         xtype: 'datecolumn',
         format:'d/m/Y à H:i:s'
        },{
         text: 'Commune',
         align : 'center',
         dataIndex: 'commune',
         sortable: false,
          renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                              var commune = '';
                              var communestri = record.communesStore.data.items.sort();
                              Ext.Array.forEach(communestri, function(item){
                                 commune = commune +'- '+item.data.nom + '</br>';
                               });
                               metaData.tdAttr ='<div  data-qclass="qtip-commune-crise" data-qtip="' + commune + '"></div';
                               return record.communesStore.data.items.length;
          }
         }],
     selModel: {
            selType: 'checkboxmodel',
            mode: 'MULTI',
            checkOnly: true
        }
});
