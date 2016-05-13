Ext.require('Sdis.Remocra.store.Commune');

/**
 * Ajout de Vtypes
 */
Ext.apply(Ext.form.VTypes, {
    'telephone': function(v){
        var objRegExp = /^\(\+[0-9][0-9]([0-9]|)\)[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]$/;
        return objRegExp.test(v);
    },
    'telephoneText': 'Exemple : (+33)145187272 ou (+590)596546443',
    'password': function(val, field) {
        if (field.otherPwdFieldId) {
            var pwd = Ext.getCmp(field.otherPwdFieldId);
            return (val == pwd.getValue());
        }
        return true;
    },
    'passwordText' : 'Les mots de passe ne sont pas identiques',
    'imagefile': function(v){
        v = v.replace(/^\s|\s$/g, ""); //trims string
        return v.match(/([^\/\\]+)\.(bmp|gif|png|jpg|jpeg)$/i);
    },
    'imagefileText' : 'Votre fichier doit être une image (GIF, JPG, BMP ou PNG)',
    'codepostal': function(strValue){
        var objRegExp = /^((0[1-9])|([1-8][0-9])|(9[0-8])|(2A)|(2B))[0-9]{3}$/;
        return objRegExp.test(strValue);
    },
    'codepostalText':'Code postal non valide',
    'emailOrBlank':  function(v) {
        var objRegExp = /^((\w+)([\-+.][\w]+)*@(\w[\-\w]*\.){1,5}([A-Za-z]){2,6})*$/;
        return objRegExp.test(v);
    },
    'emailOrBlankText': 'Exemple : aaa@bbb.cc',
    'emailOrBlankMask': /[a-z0-9_\.\-@]/i
});

Ext.apply(Ext.data.validations, {
    'emailOrBlank': function(config, value) {
        var objRegExp = /^((\w+)([\-+.][\w]+)*@(\w[\-\w]*\.){1,5}([A-Za-z]){2,6})*$/;
        return objRegExp.test(value);
    },
    'emailOrBlankMessage': 'n\'est pas une adresse email valide'
});

Ext.ns('Sdis.Remocra.widget');

Ext.define('Sdis.Remocra.widget.WidgetFactory', {
    singleton: true,
    //----------
    //-- Eléments génériques
    //----------
    
    DEFAULT_WIDTH: 400,
    DEFAULT_COMBO_IMG_WIDTH: 17,
    DEFAULT_LABEL_WIDTH: 150,
    DEFAULT_BLANK_TEXT: 'Vous devez renseigner cette information',
    DEFAULT_MSG_TARGET: 'side',
    DEFAULT_LABEL_SEP: '',
    DEFAULT_TIME_FORMAT: 'Y-m-d H:i:s',
    DEFAULT_DATE_FORMAT: 'd/m/Y',
    DEFAULT_DECIMAL_SEP : ',',
    DEFAULT_FILE_BUTTON_TEXT: 'Parcourir...',

    /**
     * Création d'un conteneur pour afficher / masquer certaines parties d'un formulaire
     * @param {} id
     * @param {} items
     * @params {} moreConfig
     * @return {}
     */
    createLightFS: function(id, items, moreConfig) {
        var config = {
            id: id, items: items, xtype: 'fieldset', cls: 'remocra-light-fs',
            autoHeight: true, style: 'padding:0px;border:none;',
            labelWidth: this.DEFAULT_LABEL_WIDTH
        };
        if (moreConfig) {
            Ext.apply(config, moreConfig);
        }
        return config;
    },
    
    createTextField: function(fieldLabel, allowBlank, value, moreConfig) {
        var config = {
            width: this.DEFAULT_WIDTH,
            msgTarget: this.DEFAULT_MSG_TARGET,
            blankText : this.DEFAULT_BLANK_TEXT, allowBlank: allowBlank,
            fieldLabel: fieldLabel, hideLabel:fieldLabel==null, labelSeparator:this.DEFAULT_LABEL_SEP,
            labelWidth: this.DEFAULT_LABEL_WIDTH,
            value: value,
            itemCls: allowBlank?'':'required'
        };
        if (moreConfig) {
            Ext.apply(config, moreConfig);
        }
        var field = new Ext.form.TextField(config);
        return field;
    },
    
    createPasswordField: function(fieldLabel, allowBlank, value, moreConfig) {
        moreConfig.inputType = "password";
        moreConfig.vtype = "password";
        return this.createTextField(fieldLabel, allowBlank, value, moreConfig);
    },
    
    createMailField: function(fieldLabel, allowBlank, value, moreConfig) {
        moreConfig.vtype = "email";
        return this.createTextField(fieldLabel, allowBlank, value, moreConfig);
    },
    
    createTelephoneField: function(fieldLabel, allowBlank, value, moreConfig) {
        moreConfig.vtype = "telephone";
        return this.createTextField(fieldLabel, allowBlank, value, moreConfig);
    },
    
    createCodePostalField:function(fieldLabel, allowBlank, value, moreConfig) {
        if(allowBlank==false){
            moreConfig.vtype = "codepostal";
        }
        else{
            moreConfig.regex = /^((0[1-9])|([1-8][0-9])|(9[0-8])|(2A)|(2B))[0-9]{3}$/;
        }
        return this.createTextField(fieldLabel, allowBlank, value, moreConfig);
    },
    
    createUrlField: function(fieldLabel, allowBlank, value, moreConfig) {
        moreConfig.vtype = "url";
        return this.createTextField(fieldLabel, allowBlank, value, moreConfig);
    },
    
    createHtmlEditor: function(fieldLabel, allowBlank, value, moreConfig) {
        var config = {
            height: 500,
            width: this.DEFAULT_WIDTH,
            msgTarget: this.DEFAULT_MSG_TARGET,
            blankText : this.DEFAULT_BLANK_TEXT, allowBlank: allowBlank,
            fieldLabel: fieldLabel, hideLabel:fieldLabel==null, labelSeparator:this.DEFAULT_LABEL_SEP,
            labelWidth: this.DEFAULT_LABEL_WIDTH,
            value: value,
            itemCls: allowBlank?'':'required'
        };
        if (moreConfig) {
            Ext.apply(config, moreConfig);
        }
        var field = Ext.create('Ext.form.HtmlEditor', config);
        return field;
    },
    
    createTextArea: function(fieldLabel, allowBlank, nbLines, value, moreConfig) {
        var config = {
            height: nbLines==null?'auto':nbLines*20, //20px par ligne ?
            width: this.DEFAULT_WIDTH,
            msgTarget: this.DEFAULT_MSG_TARGET,
            blankText : this.DEFAULT_BLANK_TEXT, allowBlank: allowBlank,
            fieldLabel: fieldLabel, hideLabel:fieldLabel==null, labelSeparator:this.DEFAULT_LABEL_SEP,
            labelWidth: this.DEFAULT_LABEL_WIDTH,
            value: value,
            itemCls: allowBlank?'':'required'
        };
        if (moreConfig) {
            Ext.apply(config, moreConfig);
        }
        var field = new Ext.form.TextArea(config);
        return field;
    },
    createComboBox: function(fieldLabel, allowBlank, value, moreConfig) {
        var config = {
            mode: 'local',
            typeAhead: true, forceSelection: true, triggerAction: 'all',
            width: this.DEFAULT_WIDTH, listWidth: this.DEFAULT_WIDTH,
            msgTarget: this.DEFAULT_MSG_TARGET,
            blankText : this.DEFAULT_BLANK_TEXT, allowBlank: allowBlank,
            fieldLabel: fieldLabel, hideLabel:fieldLabel==null, labelSeparator:this.DEFAULT_LABEL_SEP,
            labelWidth: this.DEFAULT_LABEL_WIDTH,
            value: value,
            itemCls: allowBlank?'':'required'
        };
        if (moreConfig) {
            Ext.apply(config, moreConfig);
        }
        var field = new Ext.form.ComboBox(config);
        return field;
    },
    
    createRCBGroup: function(fieldLabel, allowBlank, xtype, moreConfig) {
        var config = {
            width: this.DEFAULT_WIDTH,
            msgTarget: this.DEFAULT_MSG_TARGET,
            blankText : this.DEFAULT_BLANK_TEXT, allowBlank: allowBlank,
            fieldLabel: fieldLabel, hideLabel:fieldLabel==null, labelSeparator:this.DEFAULT_LABEL_SEP,
            labelWidth: this.DEFAULT_LABEL_WIDTH,
            itemCls: allowBlank?'':'required',
            xtype: xtype
        };
        if (moreConfig) {
            Ext.apply(config, moreConfig);
        }
        return config;
    },
    
    createRCB: function(boxLabel, inputValue, checked, xtype, moreConfig) {
        var config = {
            width: this.DEFAULT_WIDTH,
            msgTarget: this.DEFAULT_MSG_TARGET,
            boxLabel: boxLabel, hideLabel:boxLabel==null,
            inputValue: inputValue,
            checked: checked,
            xtype: xtype
        };
        if (moreConfig) {
            Ext.apply(config, moreConfig);
        }
        if (xtype=='radio') {
            return new Ext.form.Radio(config);
        }
        if (xtype=='checkbox') {
            return new Ext.form.Checkbox(config);
        }
        return config;
    },
    
    createRadioGroup: function(fieldLabel, allowBlank, moreConfig) {
        return this.createRCBGroup(fieldLabel, allowBlank, 'radiogroup', moreConfig);
    },
    
    createRadio: function(boxLabel, inputValue, checked, moreConfig) {
        return this.createRCB(boxLabel, inputValue, checked, 'radio', moreConfig);
    },
    
    createCheckboxGroup: function(fieldLabel, allowBlank, moreConfig) {
        return this.createRCBGroup(fieldLabel, allowBlank, 'checkboxgroup', moreConfig);
    },
    
    createCheckbox: function(boxLabel, inputValue, checked, moreConfig) {
        return this.createRCB(boxLabel, inputValue, checked, 'checkbox', moreConfig);
    },
    
    createIdLabelComboBox: function(id, url, moreConfig) {
        // Création du store
        var store = new Ext.data.JsonStore({
            url: url,
            autoLoad: false,
            restful: true,
            root: 'data',
            fields: [
            { name: 'id', type: 'integer' },
            { name: 'label', type: 'string' }
            ]
        });
        // Création du ComboBox
        var config = {
            id: id,
            store: store,
            width: 200,
            valueField: 'id',
            hiddenName: 'id',
            displayField: 'label',
            typeAhead: true,
            mode: 'local',
            triggerAction: 'all',
            allowBlank: false,
            editable: false,
            forceSelection: true, autoSelect: true
        };
        if (moreConfig) {
            Ext.apply(config, moreConfig);
        }
        return new Ext.form.ComboBox(config);
    },
    
    createLabelField: function(fieldLabel, moreConfig) {
        var config = {
            width: this.DEFAULT_LABEL_WIDTH,
            fieldLabel: fieldLabel,
            text: fieldLabel,
            hideLabel:fieldLabel==null, 
            labelSeparator:this.DEFAULT_LABEL_SEP
        };
        if (moreConfig) {
            Ext.apply(config, moreConfig);
        }
        var field = new Ext.form.Label(config);
        return field;
    },
    
    createIntField:function(fieldLabel, moreConfig){

        var config = {
                allowDecimals: false,
                allowNegative: true,
                width: this.DEFAULT_LABEL_WIDTH,
                fieldLabel: fieldLabel,
                text: fieldLabel,
                hideLabel:fieldLabel==null, 
                labelSeparator:this.DEFAULT_LABEL_SEP,
                hideTrigger : true
            };
            if (moreConfig) {
                Ext.apply(config, moreConfig);
            }
            var field = new Ext.form.NumberField(config);
            return field;
    },
    createDoubleField:function(fieldLabel, moreConfig){

        var config = {
                width: this.DEFAULT_LABEL_WIDTH,
                decimalSeparator : this.DEFAULT_DECIMAL_SEP,
                fieldLabel: fieldLabel,
                text: fieldLabel,
                hideLabel:fieldLabel==null, 
                labelSeparator:this.DEFAULT_LABEL_SEP,
                hideTrigger : true
            };
            if (moreConfig) {
                Ext.apply(config, moreConfig);
            }
            var field = new Ext.form.NumberField(config);
            return field;
    },
    createDoubleLabelField:function(moreConfig){

        var config = {
                width: this.DEFAULT_LABEL_WIDTH,
                setValue:function(value){
                    this.setText(String(Ext.Number.toFixed(parseFloat(value), this.decimalPrecision || 0)).replace('.', this.decimalSeparator));
                },
                decimalSeparator : this.DEFAULT_DECIMAL_SEP
            };
            if (moreConfig) {
                Ext.apply(config, moreConfig);
            }
            var field = Ext.create('Ext.form.Label',config);
            return field;
    },
    
    createFileField: function(fieldLabel, allowBlank, value, moreConfig) {
        var buttonCfg = {};
        if (moreConfig.disabled) {
            buttonCfg.disabled = moreConfig.disabled;
        }
        var config = {
            buttonText: this.DEFAULT_FILE_BUTTON_TEXT,
            width: this.DEFAULT_WIDTH,
            msgTarget: this.DEFAULT_MSG_TARGET,
            blankText : this.DEFAULT_BLANK_TEXT, allowBlank: allowBlank,
            fieldLabel: fieldLabel, hideLabel:fieldLabel==null, labelSeparator:this.DEFAULT_LABEL_SEP,
            value: value,
            labelWidth: this.DEFAULT_LABEL_WIDTH,
            itemCls: allowBlank?'':'required',
            buttonCfg: buttonCfg
        };
        if (moreConfig) {
            Ext.apply(config, moreConfig);
        }
        return new Ext.form.FileUploadField(config);
    },
    
    //----------
    //-- Eléments générospécifiques !
    //----------
    comboRenderer: function(combo) {
       return function(value, metaData, record, rowIndex, colIndex, store) {
            var store1 = combo.getStore();
            var record1 = store1.getById(value);
            return record1==null?null:record1.get(combo.displayField);
        };
    },
    
    // Les trois fonctions storeKeepCachedOnLoad et storeAddCached
    // servent à gérer le maintien des données déjà chargées.
    // CAS : store remote et utilisation de comboRenderer
    // TODO : surcharger les fonctions qui vont bien pour éviter de multiplier les parcours
    storeKeepCachedOnLoad: function(store) {
        store.on('load', Sdis.Remocra.widget.WidgetFactory.storeAddCached.createDelegate(store));
    },
    storeAddCached: function(store, records, options) {
        var i, o, add;
        // On met en cache les données fraichement chargées si elles ne le sont pas déjà
        if (store.cache == null) {
            store.cache = [];
        }
        for(i=0 ; i<records.length ; i++) {
            o = records[i];
            add = true;
            for(i=0 ; i<store.cache.length ; i++) {
                var cached = store.cache[i];
                if (cached[store.idProperty] == o.data[store.idProperty]) {
                    add = false;
                    break;
                }
            }
            if (add) {
                store.cache.push(o.data);   
            }
        }
        // On ajoute les enregistrements qui sont en cache et non déjà présents dans la liste
        var recordsss = store.reader.extractData(store.cache, true);
        for(i=0 ; i<recordsss.length ; i++) {
            o = recordsss[i];
            if (store.getById(o.data[store.idProperty])==null) {
                store.addSorted(o);
            }
        }
    },
    
    
    
    //----------
    //-- Eléments spécifiques
    //----------
    
    /**
     * @param allowPhantom Lors de l'annulation, on retire un élément (toujours fantome) lorsqu'il n'a jamais été validé uniquement (false par défaut)
     */
    createRoweditingPluginCfg: function(allowPhantom) {
        return {
            ptype: 'rowediting', clicksToEdit: 2,
            autoCancel: true, // On annule les modifications en cours si l'utilisateur double-clique sur une nouvelle ligne
            // (fonctionne pour le cas d'une ligne initiale déjà validée)
            listeners: {
                'beforeedit': function(roweditor, eOpts) {
                    // La propriété "autoCancel" du rowediting ne fonctionne dans le cas où la ligne en cours d'édition concerne
                    // un enregistrement non existant ("Ajouter" puis double-clic sur une nouvelle ligne)
                    // On harmonise l'ensemble en empêchant une modification si une autre édition est en cours
                    return roweditor.grid.plugins[0].editing!==true;
                },
                'canceledit': function(roweditor, eOpts) {
                    // L'utilisateur a choisi d'annuler les modifications
                    var grid = roweditor.grid;
                    var record = roweditor.context.record;
                    if (record) {
                        if(record.phantom===true) {
                            if (allowPhantom===true) {
                                // Comportement spécifique : grille pour laquelle on permet l'édition de phantom en permanence (enregistrement à la fin)
                                if (record.hasEverBeenValidated!==true) {
                                    // hasEverBeenValidated a été défini lors de la première validation
                                    grid.getStore().remove(record);
                                }
                            } else {
                                // Nouveau : on supprime
                                grid.getStore().remove(record);
                            }
                            
                        } else {
                            // Ancien (en base) : on restitue les valeurs initiales
                            record.reject(false);
                        }
                    }
                },
                'validateedit': function(roweditor, e, eOpts) {
                    var i, key, newCode, explodedKey, refName, refKey, fnTestUnique;
                    var grid = roweditor.grid;
                    var record = e.record;
                    
                    // On récupère les erreurs de niveau modèle (élément), sur les nouvelles valeurs
                    // record.validate : trop tôt car le record n'a pas encore été modifié => on utilise "validateNewValues"
                    var errors = Sdis.Remocra.model.util.Util.validateNewValues(record.validations, e.newValues);
                    
                    fnTestUnique = function(rec) {
                        var oldCode = null;
                        var isRefMode = refName !=null;
                        if (isRefMode) {
                            var referenced = rec.getAssociatedData()[refName];
                            if (referenced) {
                                oldCode = referenced[refKey];
                            } else {
                                errors.add({
                                    field: refName,
                                    message: 'est obligatoire'
                                });
                                return false;
                            }
                        } else {
                            oldCode = rec.get(key);
                        }
                        if (rec.id!==record.id && oldCode == newCode) {
                            errors.add({
                                field: refName!=null ? refName : key,
                                message: 'doit être unique'
                            });
                            return false;
                        }
                    };
                    
                    // On ajoute les erreurs de niveau store (collection d'éléments)
                    // Traitement des contraintes d'unicité
                    if (grid.uniqueConstraints) {
                        for (i=0 ; i<grid.uniqueConstraints.length ; i++) {
                            key = grid.uniqueConstraints[i];
                            newCode = e.newValues[key]; // Pas encore validé => newValues
                            
                            explodedKey = key.split('.'); // Par exemple : 'typeService.id'
                            refName = null;
                            refKey = null;
                            if (explodedKey.length>1) {
                                refName = explodedKey[0];
                                refKey = explodedKey[1];
                                newCode = e.newValues[refName];
                            }
                            grid.getStore().each(fnTestUnique);
                        }
                    }
                    
                    // On marque les champs invalides avec leurs erreurs respectives
                    var form = grid.plugins[0].getEditor().getForm();
                    errors.each(function(error) {
                        var field = form.findField(error.field);
                        field.markInvalid(error.message);
                    });
                    // On ne valide pas si au moins une erreur est trouvée
                    if (errors.getCount()<1) {
                        record.hasEverBeenValidated = true;
                    }
                    return errors.getCount()<1;
                }
            }
        };
    },
    
    getCommuneComboConfig : function(){
        var config = {
            store: 'Commune',
            queryMode: 'remote',
            displayField: 'nom',
            valueField: 'id',
            triggerAction: "all",
            hideTrigger: true,
            typeAhead: true,
            minChars: 3
        };
        return config;
    },
    
    createCommuneCombo : function(opts){
        return Ext.widget('combo',Ext.apply(this.getCommuneComboConfig(), opts || {}));
    }
});

