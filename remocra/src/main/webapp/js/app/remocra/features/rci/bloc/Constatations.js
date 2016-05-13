Ext.require('Ext.form.Panel');

Ext.define('Sdis.Remocra.features.rci.bloc.Constatations', {
    extend : 'Ext.form.Panel',
    alias : 'widget.rciConstatations',

    title : 'Constatations',

    defaults : {
        labelAlign : 'right',
        labelWidth : 180,
        labelSeparator : '',
        width : 400
    },

    layout : {
        type : 'table',
        columns : 2
    },

    items : [ {
        colspan : 2,
        xtype : 'displayfield',
        labelAlign : 'left',
        style : 'margin-left:20px;',
        fieldLabel : '<b>Coordonnées</b>'
    }, {
        colspan : 2,
        xtype : 'combo',
        fieldLabel : 'Format de saisie',
        store : new Ext.data.SimpleStore({
            fields: ['value', 'display'],
            data : [['dd', 'dd,dddd'], ['dm', 'dd°mm,mm'], ['dms', 'dd°mm\'ss,ss']]
        }),
        displayField : 'display',
        valueField : 'value',
        forceSelection : true,
        editable : false,
        queryMode : 'local',
        itemId : 'formatXY',
        value : 'dd'
    },

    // dd
    {
        itemId : 'ddX',
        hideMode : 'offsets',
        fieldLabel : 'Coord X ',
        xtype : 'fieldcontainer',
        layout : 'column',
        defaults : {
            minValue : 0,
            allowBlank : false
        },
        items : [ {
            columnWidth : 0.3,
            xtype : 'combo',
            store : new Ext.data.SimpleStore({
                fields : ['value', 'display'],
                data : [['E', 'Est'], ['O', 'Ouest']]
            }),
            displayField : 'display',
            valueField : 'value',
            forceSelection : true,
            editable : false,
            queryMode : 'local',
            itemId : 'ddOrientX',
            value : REMOCRA_DEFAULT_ORIENTATION.x
        }, {
            columnWidth : 0.65,
            xtype : 'numberfield',
            maxValue : 179.9999999999,
            allowDecimals : true,
            decimalPrecision : 20,
            hideTrigger : true,
            itemId : 'ddCoordXd'
        }, {
            columnWidth : 0.05,
            border : false,
            html : '&nbsp;°'
        } ]
    }, {
        itemId : 'ddY',
        hideMode : 'offsets',
        fieldLabel : 'Y ',
        xtype : 'fieldcontainer',
        layout : 'column',
        defaults : {
            minValue : 0,
            allowBlank : false
        },
        items : [ {
            columnWidth : 0.3,
            xtype : 'combo',
            store : new Ext.data.SimpleStore({
                fields : ['value', 'display'],
                data : [['N', 'Nord'], ['S', 'Sud']]
            }),
            displayField : 'display',
            valueField : 'value',
            forceSelection : true,
            editable : false,
            queryMode : 'local',
            itemId : 'ddOrientY',
            value : REMOCRA_DEFAULT_ORIENTATION.y
        }, {
            columnWidth : 0.65,
            xtype : 'numberfield',
            maxValue : 179.9999999999,
            allowDecimals : true,
            decimalPrecision : 20,
            hideTrigger : true,
            itemId : 'ddCoordYd'
        }, {
            columnWidth : 0.05,
            border : false,
            html : '&nbsp;°'
        } ]
    },

    // dm
    {
        itemId : 'dmX',
        hideMode : 'offsets',
        fieldLabel : 'Coord X ',
        xtype : 'fieldcontainer',
        layout : 'column',
        defaults : {
            allowDecimals : false,
            minValue : 0,
            allowBlank : false
        },
        items : [ {
            columnWidth : 0.3,
            xtype : 'combo',
            store : new Ext.data.SimpleStore({
                fields : ['value', 'display'],
                data : [['E', 'Est'], ['O', 'Ouest']]
            }),
            displayField : 'display',
            valueField : 'value',
            forceSelection : true,
            editable : false,
            queryMode : 'local',
            itemId : 'dmOrientX',
            value : REMOCRA_DEFAULT_ORIENTATION.x
        }, {
            columnWidth : 0.25,
            xtype : 'numberfield',
            maxValue : 179,
            hideTrigger : true,
            itemId : 'dmCoordXd'
        }, {
            columnWidth : 0.05,
            border : false,
            html : '&nbsp;°'
        }, {
            columnWidth : 0.35,
            xtype : 'numberfield',
            maxValue : 59.9999999999,
            allowDecimals : true,
            decimalPrecision : 20,
            hideTrigger : true,
            itemId : 'dmCoordXm'
        }, {
            columnWidth : 0.05,
            border : false,
            html : '&nbsp;\''
        }  ]
    }, {
        itemId : 'dmY',
        hideMode : 'offsets',
        fieldLabel : 'Y ',
        xtype : 'fieldcontainer',
        layout : 'column',
        defaults : {
            allowDecimals : false,
            minValue : 0,
            allowBlank : false
        },
        items : [ {
            columnWidth : 0.3,
            xtype : 'combo',
            store : new Ext.data.SimpleStore({
                fields : ['value', 'display'],
                data : [['N', 'Nord'], ['S', 'Sud']]
            }),
            displayField : 'display',
            valueField : 'value',
            forceSelection : true,
            editable : false,
            queryMode : 'local',
            itemId : 'dmOrientY',
            value : REMOCRA_DEFAULT_ORIENTATION.y
        }, {
            columnWidth : 0.25,
            xtype : 'numberfield',
            maxValue : 179,
            hideTrigger : true,
            itemId : 'dmCoordYd'
        }, {
            columnWidth : 0.05,
            border : false,
            html : '&nbsp;°'
        }, {
            columnWidth : 0.35,
            xtype : 'numberfield',
            maxValue : 59.9999999999,
            allowDecimals : true,
            decimalPrecision : 20,
            hideTrigger : true,
            itemId : 'dmCoordYm'
        }, {
            columnWidth : 0.05,
            border : false,
            html : '&nbsp;\''
        } ]
    },

    // dms
    {
        itemId : 'dmsX',
        hideMode : 'offsets',
        fieldLabel : 'Coord X ',
        xtype : 'fieldcontainer',
        layout : 'column',
        defaults : {
            allowDecimals : false,
            minValue : 0,
            allowBlank : false
        },
        items : [ {
            columnWidth : 0.3,
            xtype : 'combo',
            store : new Ext.data.SimpleStore({
                fields : ['value', 'display'],
                data : [['E', 'Est'], ['O', 'Ouest']]
            }),
            displayField : 'display',
            valueField : 'value',
            forceSelection : true,
            editable : false,
            queryMode : 'local',
            itemId : 'dmsOrientX',
            value : REMOCRA_DEFAULT_ORIENTATION.x
        }, {
            columnWidth : 0.15,
            xtype : 'numberfield',
            maxValue : 179,
            hideTrigger : true,
            itemId : 'dmsCoordXd'
        }, {
            columnWidth : 0.05,
            border : false,
            html : '&nbsp;°'
        }, {
            columnWidth : 0.15,
            xtype : 'numberfield',
            maxValue : 59,
            hideTrigger : true,
            itemId : 'dmsCoordXm'
        }, {
            columnWidth : 0.05,
            border : false,
            html : '&nbsp;\''
        }, {
            columnWidth : 0.25,
            xtype : 'numberfield',
            maxValue : 59.9999999999,
            allowDecimals : true,
            decimalPrecision : 20,
            hideTrigger : true,
            itemId : 'dmsCoordXs'
        }, {
            columnWidth : 0.05,
            border : false,
            html : '&nbsp;"'
        } ]
    }, {
        itemId : 'dmsY',
        hideMode : 'offsets',
        fieldLabel : 'Y ',
        xtype : 'fieldcontainer',
        layout : 'column',
        defaults : {
            allowDecimals : false,
            minValue : 0,
            allowBlank : false
        },
        items : [ {
            columnWidth : 0.3,
            xtype : 'combo',
            store : new Ext.data.SimpleStore({
                fields : ['value', 'display'],
                data : [['N', 'Nord'], ['S', 'Sud']]
            }),
            displayField : 'display',
            valueField : 'value',
            forceSelection : true,
            editable : false,
            queryMode : 'local',
            itemId : 'dmsOrientY',
            value : REMOCRA_DEFAULT_ORIENTATION.y
        }, {
            columnWidth : 0.15,
            xtype : 'numberfield',
            maxValue : 179,
            hideTrigger : true,
            itemId : 'dmsCoordYd'
        }, {
            columnWidth : 0.05,
            border : false,
            html : '&nbsp;°'
        }, {
            columnWidth : 0.15,
            xtype : 'numberfield',
            maxValue : 59,
            hideTrigger : true,
            itemId : 'dmsCoordYm'
        }, {
            columnWidth : 0.05,
            border : false,
            html : '&nbsp;\''
        }, {
            columnWidth : 0.25,
            xtype : 'numberfield',
            maxValue : 59.9999999999,
            allowDecimals : true,
            decimalPrecision : 20,
            hideTrigger : true,
            itemId : 'dmsCoordYs'
        }, {
            columnWidth : 0.05,
            border : false,
            html : '&nbsp;"'
        } ]
    },

    {
        fieldLabel : 'Coord DFCI',
        xtype : 'displayfield',
        itemId : 'coordDFCI',
        name : 'coordDFCI'
    }, {
        xtype : 'textfield',
        regex : /^([a-z]|[A-Z])[0-9]{1,2}$/,
        regexText : 'Doit être composé d\'une lettre et un ou deux chiffres. Exemple : A5 ou A66',
        fieldLabel : 'Point d\'éclosion',
        itemId : 'pointEclosion',
        name : 'pointEclosion'
    }, {
        colspan : 2,
        xtype : 'displayfield',
        labelAlign : 'left',
        style : 'margin-left:20px;',
        fieldLabel : '<b>Météo</b>'
    }, {
        fieldLabel : 'GDH ',
        xtype : 'fieldcontainer',
        layout : 'column',
        defaults : {
            columnWidth : 0.5
        },
        items : [ {
            xtype : 'datefield',
            itemId : 'dateGdh',
            name : 'dateGdh'
        }, {
            xtype : 'timefield',
            format : 'H:i',
            increment: 30,
            itemId : 'heureGdh',
            name : 'heureGdh'
        } ]
    }, {
        xtype : 'combo',
        fieldLabel : 'Vent local',
        store : new Ext.data.SimpleStore({
            fields: ['value', 'display'],
            data : [[true, 'Oui'], [false, 'Non']]
        }),
        displayField : 'display',
        valueField : 'value',
        forceSelection : true,
        editable : false,
        queryMode : 'local',
        itemId : 'ventLocal',
        name : 'ventLocal'
    }, {
        xtype : 'numberfield',
        fieldLabel : 'Hygrometrie %',
        allowDecimals : false,
        step : 10,
        minValue : 0,
        maxValue : 100,
        hideTrigger : false,
        itemId : 'hygrometrie',
        name : 'hygrometrie'
    }, {
        xtype : 'combo',
        fieldLabel : 'Direction',
        store : new Ext.data.SimpleStore({
            fields: ['value', 'display'],
            data : [['N', 'Nord'], ['S', 'Sud'], ['E', 'Est'], ['O', 'Ouest'],
                    ['NE', 'Nord-Est'], ['NO', 'Nord-Ouest'], ['SE', 'Sud-Est'],
                    ['SO', 'Sud-Ouest']]
        }),
        displayField : 'display',
        valueField : 'value',
        forceSelection : true,
        editable : false,
        queryMode : 'local',
        itemId : 'directionVent',
        name : 'directionVent'
    }, {
        xtype : 'numberfield',
        fieldLabel : 'Température °C',
        allowDecimals : true,
        step : 1,
        hideTrigger : false,
        itemId : 'temperature',
        name : 'temperature'
    }, {
        xtype : 'numberfield',
        fieldLabel : 'Force km/h',
        allowDecimals : false,
        step : 10,
        minValue : 0,
        hideTrigger : false,
        itemId : 'forceVent',
        name : 'forceVent'
    }, {
        colspan : 2,
        xtype : 'combo',
        fieldLabel : 'Indice ROTHERMEL',
        store : new Ext.data.SimpleStore({
            fields: ['value', 'display'],
            data : [[-1, 'Non renseigné'], [0, '0'], [10, '10'], [20, '20'], [30, '30'], [40, '40'],
                    [50, '50'], [60, '60'], [70, '70'], [80, '80'], [90, '90'],
                    [100, '100']]
        }),
        displayField : 'display',
        valueField : 'value',
        forceSelection : true,
        editable : false,
        queryMode : 'local',
        itemId : 'indiceRothermel',
        name : 'indiceRothermel'
    }, {
        colspan : 2,
        xtype : 'displayfield',
        labelAlign : 'left',
        style : 'margin-left:20px;',
        fieldLabel : '<b>Feu</b>'
    }, {
        xtype : 'numberfield',
        fieldLabel : 'Superficie arrivée secours (m2)',
        allowDecimals : true,
        hideTrigger : true,
        itemId : 'superficieSecours',
        name : 'superficieSecours'
    }, {
        xtype : 'textfield',
        fieldLabel : 'Premier engin sur les lieux',
        itemId : 'premierEngin',
        name : 'premierEngin'
    }, {
        xtype : 'numberfield',
        fieldLabel : 'Superficie arrivée référent (m2)',
        allowDecimals : true,
        hideTrigger : true,
        itemId : 'superficieReferent',
        name : 'superficieReferent' 
    }, {
        xtype : 'textfield',
        fieldLabel : 'Premier COS',
        itemId : 'premierCos',
        name : 'premierCos'
    }, {
        xtype : 'numberfield',
        fieldLabel : 'Superficie finale (m2)',
        allowDecimals : true,
        hideTrigger : true,
        itemId : 'superficieFinale',
        name : 'superficieFinale'
    }, {
        xtype : 'textfield',
        fieldLabel : 'Forces de l\'ordre présentes',
        itemId : 'forcesOrdre',
        name : 'forcesOrdre'
    }, {
        xtype : 'displayfield',
        hidden : true
    }, {
        xtype : 'combo',
        fieldLabel : 'Gel des lieux',
        store : new Ext.data.SimpleStore({
            fields: ['value', 'display'],
            data : [[true, 'Oui'], [false, 'Non']]
        }),
        displayField : 'display',
        valueField : 'value',
        forceSelection : true,
        editable : false,
        queryMode : 'local',
        itemId : 'gelLieux',
        name : 'gelLieux'
    }]
});