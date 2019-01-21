Ext.ns('Sdis.Remocra.widget.map4');

Ext.require('Sdis.Remocra.store.Voie');
Ext.require('Sdis.Remocra.widget.WidgetFactory');

Sdis.Remocra.widget.map4.NBCARTES = 0;
Ext.define('Sdis.Remocra.widget.map4.Map', {
    extend: 'Ext.container.Container',
    alias: 'widget.crMap4',

    // Items qui doivent figurer juste avant le bouton d'affichage de la barre
    // d'édition
    moreItems: [],
    // Items de la barre d'édition
    editItems: [],
    // URL de la légende (qui conditionne le chargement des couches)
    legendUrl: null,

    // Doit-on cacher la combo de zoom sur tournée (true par défaut)
    hideZoomTournee: true,
    mapTpl: Ext.create('Ext.XTemplate', '<div class="maptbar1"><!-- --></div>', '<div class="maptbar2"><!-- --></div>', '<div class="map"><!-- --></div>',
            '<div class="maplegend"><!-- --></div>', '<div class="mapinfo"><!-- --></div>'),
    workingLayer: null,

    legendTpl: null,

    initComponent: function() {
        this.addEvents('layersadded');

        Ext.apply(this, {
            html: ''
        });


        this.on('afterrender', Ext.bind(this.renderContent, this), this);

        this.callParent(arguments);

        // Affichage / masquage du bandeau
        var banniere = Ext.get('banniere');
        banniere.setStyle('display', 'none');
        Sdis.Remocra.widget.map4.NBCARTES++;
        if(Sdis.Remocra.widget.map4.NBCARTES === 1){
          Ext.get('pageTop').toggleDisplay(undefined, true);
        }
        this.on('destroy', function() {
            Sdis.Remocra.widget.map4.NBCARTES--;
             if(Sdis.Remocra.widget.map4.NBCARTES === 0){
                Ext.get('pageTop').toggleDisplay(true, {
                    callback: Ext.bind(function() {
                      this.setStyle("display", 'block');
                    }, banniere)
                });
             }
        });
    },

    renderContent: function() {
        // Template global
        this.mapTpl.append(this.getEl(), {}, false);
    }

});