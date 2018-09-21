Ext.require('Sdis.Remocra.widget.AbstractDragAndDropGrid');
Ext.require('Sdis.Remocra.store.RepertoireLieu');

Ext.define('Sdis.Remocra.features.crises.bloc.Repertoires', {

    extend: 'Sdis.Remocra.widget.AbstractDragAndDropGrid',
    title: 'Répertoires des lieux',
    id: 'repertoires',
    alias: 'widget.crise.Repertoires',
        deferredRender: false,


      // create the data store
      getFirstGridStore: function() {
                return  Ext.create('Sdis.Remocra.store.RepertoireLieu', {
                   autoLoad: true
                });
      },


      // Column Model shortcut array
      getColumns: function() {
          return  [{flex: 1, sortable: true, dataIndex: 'libelle'}];
      },

      getSecondGridStore: function() {
                return  Ext.create('Sdis.Remocra.store.RepertoireLieu', {
                       autoLoad: false
                });
      }

});

