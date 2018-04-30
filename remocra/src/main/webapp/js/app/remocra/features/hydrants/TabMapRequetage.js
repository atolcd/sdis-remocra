Ext.require('Sdis.Remocra.widget.map.Map');
Ext.define('Sdis.Remocra.features.hydrants.TabMapRequetage', {
    extend: 'Sdis.Remocra.widget.map.Map',
    alias: 'widget.crHydrantsMapRequetage',
    cls: 'hydrants-map-requetage',
    title: 'LocalisationRequetage',
    itemId: 'localisationRequetage',
    legendUrl: BASE_URL + '/../ext-res/js/app/remocra/features/hydrants/data/carte-requete-selection.json',
    initComponent: function() {
        this.callParent(arguments);
    },

    refreshZonesLayer: function(params) {
        if (!this.zonesLayer) {
            this.zonesLayer = this.getLayerByCode('zonesLayer');
        }
        // Sans la méthode clearGrid, le redraw ne rafraîchit pas bien la couche
        this.zonesLayer.clearGrid();
        if(params){
            //on rajoute l'id de la selection en paramètre
            this.zonesLayer.mergeNewParams({'viewparams':'SELECTION_ID:'+params});
        }
        this.zonesLayer.redraw(true);
    },
    setIdSelection: function(idSelection){
           this.idSelection = idSelection;
    },

    getIdSelection: function(){
      if (this.idSelection != null) {
         return this.idSelection;
      }
      return null;
    }

});
