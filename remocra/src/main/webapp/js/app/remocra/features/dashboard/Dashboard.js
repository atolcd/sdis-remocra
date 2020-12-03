
Ext.ns('Sdis.Remocra.features.dashboard');

Ext.require('Sdis.Remocra.widget.WidgetFactory');

Ext.define('Sdis.Remocra.features.dashboard.Dashboard', {
    extend: 'Ext.Container',
    alias: 'widget.crDashboard',
    maxHeight: 1000,
    autoScroll: true,
    id: 'dashboard',
    listeners: {
     afterrender: function(){
     this.createDashboard();
      }
    },

    initComponent: function() {
        Ext.apply(this, {});

         // Affichage / masquage du bandeau
                var banniere = Ext.get('banniere');
                banniere.setStyle('display', 'none');
                Ext.get('pageTop').toggleDisplay(undefined, true);
                this.on('destroy', function() {
                    Ext.get('pageTop').toggleDisplay(true, {
                        callback: Ext.bind(function() {
                            this.setStyle("display", 'block');
                        }, banniere)
                    });
                });
        this.callParent(arguments);

    },
    createDashboard: function(){
        var d = document.createElement('div');
        var id = "dashboard-"+(++Ext.AbstractComponent.AUTO_ID);
        d.id=id;
        document.getElementById("dashboard").appendChild(d);
        var vueDashboard = window.remocraVue.buildDashboard(d);
    }
});