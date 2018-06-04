
Ext.require('Ext.chart.Chart');
Ext.define('Sdis.Remocra.features.hydrants.bloc.ChartPibi', {

    extend: 'Ext.chart.Chart',
    alias: 'widget.hydrant.chartpibi',
    width: 400,
    height: 200,
    store: {fields:[]},
    animate: true,
    shadow: true,
    legend: {
       visible: false
    }
 });
