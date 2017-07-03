Ext.require('Sdis.Remocra.widget.FileUploadMultiPanel');

Ext.define('Sdis.Remocra.features.oldebs.bloc.Documents', {
    extend: 'Ext.Panel',
    alias: 'widget.oldeb.documents',

    itemId: 'documents',
    cls: 'tabblocdocument',
    forceFit: true,
    bodyPadding: 20,

    items: [{
        xtype: 'crFileuploadMulti',
        itemId: 'oldebVisiteDocument',
        border: false
    } ]

});
