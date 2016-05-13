Ext.require('Sdis.Remocra.features.risques.Map');
Ext.require('Sdis.Remocra.widget.BlocDocumentGrid');

Ext.define('Sdis.Remocra.features.risques.Risques', {
    extend : 'Ext.Panel',
    alias : 'widget.crRisques',

    title : 'Carte des risques',
    id : 'dfci',

    initComponent : function() {
        this.items = [ {
            xtype : 'crRisquesMap'
        } ];
        if (Sdis.Remocra.Rights.getRight('DOCUMENTS').Read) {
            this.items.push({
                xtype : 'crBlocDocumentGrid',
                thematiques : 'RISQUES'
            });
        }
        this.callParent(arguments);
    }
});