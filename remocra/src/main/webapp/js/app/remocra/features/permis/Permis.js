Ext.require('Sdis.Remocra.features.permis.Map');
Ext.require('Sdis.Remocra.widget.BlocDocumentGrid');

Ext.require('Sdis.Remocra.store.TypePermisAvisPprifStore');
Ext.require('Sdis.Remocra.store.TypePermisAvisNoPprifStore');
Ext.require('Sdis.Remocra.store.TypePermisAvisStore');

Ext.require('Sdis.Remocra.store.TypePermisInterservicePprifStore');
Ext.require('Sdis.Remocra.store.TypePermisInterserviceNoPprifStore');
Ext.require('Sdis.Remocra.store.TypePermisInterserviceStore');

Ext.define('Sdis.Remocra.features.permis.Permis', {
    extend : 'Ext.Panel',
    alias : 'widget.crPermis',

    title : 'Carte des permis',
    id : 'permis',

    initComponent : function() {

        // Ils ne sont pas en autoload
        Sdis.Remocra.store.TypePermisAvisStore.load();
        Sdis.Remocra.store.TypePermisInterserviceStore.load();

        this.items = [ {
            xtype : 'crPermisMap'
        } ];
        if (Sdis.Remocra.Rights.getRight('DOCUMENTS').Read) {
            this.items.push({
                xtype : 'crBlocDocumentGrid',
                thematiques : 'PERMIS'
            });
        }
        this.callParent(arguments);
    }

});