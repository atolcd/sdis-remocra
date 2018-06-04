Ext.require('Ext.form.FieldSet');
Ext.require('Sdis.Remocra.features.hydrants.bloc.ChartPibi');
Ext.require('Sdis.Remocra.features.hydrants.GridDebits');
Ext.define('Sdis.Remocra.features.hydrants.bloc.VerifHydrauliquePibi', {

    extend: 'Ext.form.FieldSet',
    title: 'Vérification hydraulique',
    id: 'verifHydrauliquePibi',
    alias: 'widget.hydrant.verifhydrauliquepibi',
    layout: {
        type: 'table',
        columns: 2
    },
    defaults: {
        anchor: '100%',
        labelAlign: 'right',
        labelWidth: 170,
        xtype: 'numberfield',
        hideTrigger: true,
        minValue: 0,
        mouseWheelEnabled: false
    },

    items: [{
        fieldLabel: '&nbsp;',
        xtype: 'displayfield', labelSeparator : ''
    }, {
        width: 400,
        fieldLabel: '&nbsp;',
        xtype: 'displayfield', labelSeparator : '',
        name : 'dateTerrain',
        value : 'Evolution du débit',
        fieldStyle : 'font-weight: bold;'
    }, {
        fieldLabel: 'Débit à 1 bar (m³/h)',
        name: 'debit',
        allowDecimals: false
    },{
      xtype: 'hydrant.chartpibi',
      name: 'chartDebit',
      rowspan:9,
      id:'chartDebit',
      store: {fields: ['dateOp', 'debit']},
          axes: [{
                  type: 'Numeric',
                  position: 'left',
                  fields: ['debit'],
                  minimum: 0,
                  label: {
                      renderer: Ext.util.Format.numberRenderer('0,0')
                  },
                  grid: true
              }, {
                  type: 'Category',
                  position: 'bottom',
                  fields: ['dateOp'],
                  label: {
                  renderer: Ext.util.Format.dateRenderer('d/m/Y'),
                   rotate: {
                              degrees: 315
                          }
              }
          }],
          series: [{
              type: 'column',
              axis: 'bottom',
              xField: 'dateOp',
              yField: ['debit'],
              highlight:true,
              tips: {
                trackMouse: true,
                renderer: function(storeItem, item) {
                   this.setTitle(String(item.value[1]));
                }
              }
          }]
    },{
        width: 250,
        fieldLabel: '&nbsp;',
        rowspan:9,
        xtype: 'displayfield', labelSeparator : '',
        name: 'separator',
        hidden:true
    },{
        xtype: 'displayfield',
        name: 'debit_msg',
        margin: '0 0 0 160',
        colspan: 1
    },{
        fieldLabel: 'Pression dynamique à 60m³ (bar)',
        name: 'pressionDyn'
    },{
        xtype: 'displayfield',
        name: 'pressionDyn_msg',
        margin: '0 0 0 160',
        colspan: 1

    },{
        xtype: 'displayfield',
        name: 'Error_msg',
        value: 'La pression dynamique à 60 m³ ne peut pas être inférieure à 1.',
        cls: 'hydrant-msg-error',
        margin: '0 0 0 160',
        colspan: 1,
        hidden:true
    },{
        fieldLabel: 'Débit max (m³/h)',
        name: 'debitMax',
        allowDecimals: false
    },{
        xtype: 'displayfield',
        name: 'debitMax_msg',
        margin: '0 0 0 160',
        colspan: 1,
        hidden:true

    },{
        fieldLabel: 'Pression dynamique au débit max (bar)',
        name: 'pressionDynDeb'
    },{
        xtype: 'displayfield',
        name: 'pressionDynDeb_msg',
        margin: '0 0 0 160',
        colspan: 1
    },{
        fieldLabel: 'Pression statique (bar)',
        name :'pression'
    },{
        xtype: 'displayfield',
        name: 'pression_msg',
        margin: '0 0 30 160',
        colspan: 1
    },{
       xtype: 'gridDebit',
       id:'gridDebit',
       colspan: 2
    }

  ]
});
