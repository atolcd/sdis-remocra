Ext.require('Sdis.Remocra.features.dfci.Map');
Ext.require('Sdis.Remocra.widget.BlocDocumentGrid');

Ext.define('Sdis.Remocra.features.dfci.Dfci', {
    extend : 'Ext.Panel',
    alias : 'widget.crDfci',

    title : 'Carte DFCI',
    id : 'dfci',

    initComponent : function() {
        this.items = [ {
            xtype : 'crDfciMap'
        } ];
        if (Sdis.Remocra.Rights.getRight('DOCUMENTS').Read) {
            this.items.push({
                xtype : 'crBlocDocumentGrid',
                thematiques : 'DFCI'
            });
        }
        this.callParent(arguments);
    }

});