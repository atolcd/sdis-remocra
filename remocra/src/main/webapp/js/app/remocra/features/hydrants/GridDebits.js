Ext.require('Ext.grid.Panel');
Ext.define('Sdis.Remocra.features.hydrants.GridDebits', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.gridDebit',
    height: 120,
    store: {fields: [
    {name: 'dateOp'},
    {name: 'debit'},
    {name: 'debitMax'},
    {name: 'pressionStat'},
    {name: 'pressionDyn'},
    {name: 'pressionDynDeb'}],
    queryMode:"local"
    },
     features: [{
                ftype: 'summary',
                id: 'summary'
            }],
    columns: [{
                  text: "Date de contrôle", flex: 1, dataIndex: 'dateOp', sortable: true, renderer: Ext.util.Format.dateRenderer('d/m/Y'),
                   summaryRenderer: function(value, summaryData, dataIndex) {
                      return ('Moyenne');
                   }
              },
              {
                  text: "Debit", dataIndex: 'debit', flex: 1, sortable: true,
                  summaryType: 'average',
                  summaryRenderer: function(value, summaryData, dataIndex) {
                      return (Ext.util.Format.round(value,2));
                  }
              },
              {
                  text: "Pression dynamique <br/> à 60m³ (bar)", flex: 1, dataIndex: 'pressionDyn', sortable: true,
                  summaryType: 'average',
                  summaryRenderer: function(value, summaryData, dataIndex) {
                      return (Ext.util.Format.round(value,2));
                  }

              },
              {
                  text: "Débit max (m³/h)", dataIndex: 'debitMax', flex: 1, sortable: true,
                  summaryType: 'average',
                  summaryRenderer: function(value, summaryData, dataIndex) {
                      return (Ext.util.Format.round(value,2));
                  }
              },
              {
                  text: "Pression dynamique <br/> au débit max (bar)", flex: 1, dataIndex: 'pressionDynDeb', sortable: true,
                  summaryType: 'average',
                  summaryRenderer: function(value, summaryData, dataIndex) {
                      return (Ext.util.Format.round(value,2));
                  }
              },
              {
                  text: "Pression statique (bar)", dataIndex: 'pressionStat', flex: 1, sortable: true,
                  summaryType: 'average',
                  summaryRenderer: function(value, summaryData, dataIndex) {
                      return (Ext.util.Format.round(value,2));
                  }
              }]});