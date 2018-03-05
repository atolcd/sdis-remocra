Ext.require('Sdis.Remocra.features.adresses.Map');
Ext.require('Sdis.Remocra.widget.BlocDocumentGrid');

Ext.define('Sdis.Remocra.features.adresses.Adresses', {
    extend : 'Ext.Panel',
    alias : 'widget.crAdresses',

    title : 'Carte des adresses',
    id : 'adresses',

    initComponent : function() {
        this.items = [ {
            xtype : 'crAdressesMap'
        } ];
        if (Sdis.Remocra.Rights.hasRight('DOCUMENTS_R')) {
            this.items.push({
                xtype : 'crBlocDocumentGrid',
                thematiques : 'ADRESSES'
            });
        }
        this.callParent(arguments);
    }

});