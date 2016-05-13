Ext.require('Sdis.Remocra.store.ImageStyle');
Ext.require('Sdis.Remocra.store.VectorStyle');
Ext.require('Ext.picker.Color');
Ext.require('Sdis.Remocra.widget.ColorPickerCombo');
Ext.require('Ext.slider.Single');
Ext.require('Sdis.Remocra.features.cartographie.PreviewMap');

Ext.define('Sdis.Remocra.features.cartographie.StyleWindow', {
    extend : 'Ext.Window',
    alias : 'widget.crCartographieStyleWin',

    title : 'Choix du style',
    info : null,

    layout : {
        type : 'hbox',
        align : 'stretch'
    },

    dockedItems : [ {
        xtype : 'toolbar',
        dock : 'bottom',
        ui : 'footer',
        layout : {
            pack : 'center'
        },
        items : [ {
            text : 'Valider',
            itemId : 'ok'
        }, {
            text : 'Annuler',
            itemId : 'cancel'
        } ]
    } ],

    defaults : {
        border : false
    },

    items : [
            {
                defaults : {
                    xtype : 'fieldset',
                    margin : 10,
                    defaults : {
                        style : 'margin-left:20px',
                        labelAlign : 'left',
                        labelWidth : 180,
                        labelSeparator : '',
                        width : 400
                    }
                },
                items : [
                        {
                            xtype : 'displayfield',
                            fieldStyle : 'font-style:italic;color:grey',
                            itemId : 'info',
                            hidden : true
                        },
                        {
                            title : 'Point',
                            items : [ {
                                xtype : 'slider',
                                fieldLabel : 'Rayon',
                                increment : 1,
                                minValue : 1,
                                maxValue : 200,
                                itemId : 'pointRadius'
                            }, {
                                xtype : 'combo',
                                fieldLabel : 'Symbole',
                                store : 'VectorStyle',
                                displayField : 'lbl',
                                valueField : 'code',
                                forceSelection : true,
                                autoSelect : true,
                                editable : false,
                                itemId : 'graphicName'
                            }, {
                                xtype : 'combo',
                                fieldLabel : 'Image <span style="font-style:italic;color:grey;">choix ou http://...</span>',
                                store : 'ImageStyle',
                                queryMode : 'local',
                                displayField : 'lbl',
                                valueField : 'url',
                                emptyText : 'URL http://',
                                allowBlank : true,
                                itemId : 'externalGraphic'
                            }, {
                                xtype : 'slider',
                                fieldLabel : 'Rotation <span style="font-style:italic;color:grey;">en degrés</span>',
                                increment : 5,
                                minValue : 0,
                                maxValue : 360,
                                itemId : 'rotation'
                            } ]
                        },
                        {
                            title : 'Ligne',
                            items : [
                                    {
                                        xtype : 'colorcombo',
                                        fieldLabel : 'Couleur',
                                        itemId : 'strokeColor'
                                    },
                                    {
                                        xtype : 'slider',
                                        fieldLabel : 'Transparence',
                                        increment : 5,
                                        minValue : 0,
                                        maxValue : 100,
                                        itemId : 'strokeOpacity'
                                    },
                                    {
                                        xtype : 'slider',
                                        fieldLabel : 'Epaisseur',
                                        increment : 1,
                                        minValue : 1,
                                        maxValue : 300,
                                        itemId : 'strokeWidth'
                                    },
                                    {
                                        xtype : 'combo',
                                        fieldLabel : 'Fins de lignes',
                                        store : new Ext.data.SimpleStore({
                                            fields : [ 'value', 'display' ],
                                            data : [ [ 'round', 'Arrondies' ], [ 'square', 'Carrées' ], [ 'butt', 'Droites' ] ]
                                        }),
                                        displayField : 'display',
                                        valueField : 'value',
                                        forceSelection : true,
                                        autoSelect : true,
                                        editable : false,
                                        itemId : 'strokeLinecap'
                                    },
                                    {
                                        xtype : 'combo',
                                        fieldLabel : 'Style',
                                        store : new Ext.data.SimpleStore({
                                            fields : [ 'value', 'display' ],
                                            data : [ [ 'solid', 'Continu' ], [ 'dot', 'Pointillés' ], [ 'dash', 'Tirets' ],
                                                    [ 'dashdot', 'Tirets / Pointillés' ], [ 'longdash', 'Tirets longs' ],
                                                    [ 'longdashdot', 'Tirets longs / Pointillés' ] ]
                                        }),
                                        displayField : 'display',
                                        valueField : 'value',
                                        forceSelection : true,
                                        autoSelect : true,
                                        editable : false,
                                        itemId : 'strokeDashstyle'
                                    } ]
                        }, {
                            title : 'Remplissage',
                            items : [ {
                                xtype : 'colorcombo',
                                fieldLabel : 'Couleur',
                                itemId : 'fillColor'
                            }, {
                                xtype : 'slider',
                                fieldLabel : 'Transparence',
                                increment : 5,
                                minValue : 0,
                                maxValue : 100,
                                itemId : 'fillOpacity'
                            } ]
                        }, {
                            title : 'Texte',
                            items : [ {
                                xtype : 'textfield',
                                fieldLabel : 'Texte <span style="font-style:italic;color:grey;">appliqué à la sélection</span>',
                                allowBlank : true,
                                itemId : 'label'
                            }, {
                                xtype : 'colorcombo',
                                fieldLabel : 'Couleur',
                                itemId : 'fontColor'
                            }, {
                                xtype : 'slider',
                                fieldLabel : 'Taille',
                                increment : 1,
                                minValue : 5,
                                maxValue : 50,
                                itemId : 'fontSize'
                            } ]
                        } ]
            }, {
                margin : 20,
                layout : {
                    type : 'vbox',
                    pack : 'center'
                },
                items : [ {
                    xtype : 'crCartographiepreviewMap',
                    itemId : 'preview'
                } ]
            } ],

    initComponent : function() {
        this.callParent(arguments);

        // On définit les propriétés en mettant à jour la preview après le rendu
        this.on('afterrender', this.initAndInstallListeners);

        // On affiche les info si nécessaire
        if (this.info) {
            var info = this.queryById('info');
            info.setValue(this.info);
            info.setVisible(true);
        }
    },

    initAndInstallListeners : function() {
        // On initialise l'interface graphique
        this.setProps(this.props);

        // Dès qu'un changement a lieu, on met à jour la preview
        var cmps = this.query('textfield,numberfield,combo,colorcombo,slider');
        Ext.Array.each(cmps, function(cmp, index, recs) {
            cmp.on('change', this.updPreview, this);
        }, this);

        this.queryById('cancel').on('click', this.close, this);

        this.queryById('ok').on('click', function(button) {
            this.fireEvent('valid', this.getProps());
            this.close();
        }, this);
    },

    updPreview : function() {
        this.queryById('preview').setProps(this.getProps());
    },

    setProps : function(props) {
        if (!props) {
            return;
        }
        // Point
        var pointRadius = this.queryById('pointRadius');
        var strokeWidth = this.queryById('strokeWidth');
        pointRadius.setValue(this.progressiveSliderToCmpValue(pointRadius.maxValue, props.pointRadius));
        this.queryById('graphicName').setValue(props.graphicName);
        this.queryById('externalGraphic').setValue(props.externalGraphic);
        this.queryById('rotation').setValue(props.rotation);
        // Ligne
        this.queryById('strokeColor').setValue(props.strokeColor);
        this.queryById('strokeOpacity').setValue(100 - (props.strokeOpacity * 100));
        strokeWidth.setValue(this.progressiveSliderToCmpValue(strokeWidth.maxValue, props.strokeWidth));
        this.queryById('strokeLinecap').setValue(props.strokeLinecap);
        this.queryById('strokeDashstyle').setValue(props.strokeDashstyle);
        // Remplissage
        this.queryById('fillColor').setValue(props.fillColor);
        this.queryById('fillOpacity').setValue(100 - (props.fillOpacity * 100));
        // Texte
        this.queryById('label').setValue(props.label);
        this.queryById('fontColor').setValue(props.fontColor);
        this.queryById('fontSize').setValue(props.fontSize);

        this.queryById('preview').setProps(props);
    },
    getProps : function() {
        var pointRadius = this.queryById('pointRadius');
        var strokeWidth = this.queryById('strokeWidth');
        return {
            // Point
            pointRadius : this.progressiveSliderToRealValue(pointRadius.maxValue, pointRadius.getValue()),
            graphicName : this.queryById('graphicName').getValue(),
            externalGraphic : this.queryById('externalGraphic').getValue(),
            rotation : this.queryById('rotation').getValue(),
            // Ligne
            strokeColor : this.queryById('strokeColor').getValue(),
            strokeOpacity : (100 - this.queryById('strokeOpacity').getValue()) / 100,
            strokeWidth : this.progressiveSliderToRealValue(strokeWidth.maxValue, strokeWidth.getValue()),
            strokeLinecap : this.queryById('strokeLinecap').getValue(),
            strokeDashstyle : this.queryById('strokeDashstyle').getValue(),
            // Remplissage
            fillColor : this.queryById('fillColor').getValue(),
            fillOpacity : (100 - this.queryById('fillOpacity').getValue()) / 100,
            // Texte
            label : this.queryById('label').getValue(),
            fontColor : this.queryById('fontColor').getValue(),
            fontSize : this.queryById('fontSize').getValue()
        };
    },

    /**
     * Fonctions utilitaires : pour avoir une progressivité dans la sélection
     * d'une valeur de combo (doucement au début puis rapidement à la fin).
     * 
     * Pour trouver la bonne fonction, j'ai utilisé le viewer :
     * http://www.abhortsoft.hu/functionvisualizer/functionvisualizer.html. J'ai
     * retenu la méthode HalfCircle que j'ai adaptée (initialement sin(acos(x)),
     * vers 1-sin(acos(x)). J'ai un bel arc de cercle qui monte en allant de 0 à
     * 1. D'où la nécessité de rapporter à la maxValue du slider.
     * 
     * Pour finir, comme mes dernières réflexions mathématiques commencent à
     * dater, je me suis servi des pages suivantes pour trouver la fonction
     * inverse : http://www.purplemath.com/modules/invrsfcn3.htm
     * http://en.wikipedia.org/wiki/Inverse_trigonometric_functions.
     */
    progressiveSliderToCmpValue : function(maxValue, realValue) {
        var x = realValue;
        var cmpValue = maxValue * Math.cos(Math.asin(1 + ((1 - x) / maxValue)));
        return cmpValue;
    },
    progressiveSliderToRealValue : function(maxValue, cmpValue) {
        var x = cmpValue;
        var realValue = (1 - Math.sin(Math.acos((x) / maxValue))) * maxValue + 1;
        return realValue;
    }
});