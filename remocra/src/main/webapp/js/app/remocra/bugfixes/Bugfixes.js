Ext.ns('Sdis.Remocra.bugfixes');
Ext.define('Sdis.Remocra.bugfixes.Bugfixes', {});

Ext.require('Ext.grid.RowEditor');
Ext.require('Ext.data.validations');
Ext.require('Ext.Element');

/**
 * Traductions
 */
Ext.override(Ext.grid.RowEditor, {
    saveBtnText: 'Valider',
    cancelBtnText: 'Annuler',
    errorsText: 'Erreurs',
    dirtyText: 'Vous devez valider ou annuler les changements de la ligne en cours d\'édition avant de passer à la suite.'
});

Ext.data.validations.presenceMessage = 'doit être présent';
Ext.data.validations.lengthMessage = 'n\'a pas la taille requise';
Ext.data.validations.formatMessage = 'n\'a pas le bon format';
Ext.data.validations.inclusionMessage = 'n\'est pas dans les valeurs acceptées';
Ext.data.validations.exclusionMessage = 'est dans les valeurs refusées';
Ext.data.validations.emailMessage = 'n\'est pas une adresse email valide';

if (Ext.MessageBox) {
    Ext.MessageBox.msgButtons['ok'].text = "Valider";
    Ext.MessageBox.msgButtons['cancel'].text = "Annuler";
    Ext.MessageBox.msgButtons['yes'].text = "Oui";
    Ext.MessageBox.msgButtons['no'].text = "Non";
}

//Correction apportée : sérialisation des dates null en null (avant : '')
Ext.override(Ext.Date, {
    format: function(date, format) {
        var utilDate = Ext.Date;
        var formatFunctions = utilDate.formatFunctions;
    
        if (!Ext.isDate(date)) {
            // ----------- Start FIX ----------------
            return null;
            // ----------- End FIX ------------------
        }
    
        if (formatFunctions[format] == null) {
            utilDate.createFormat(format);
        }
    
        return formatFunctions[format].call(date) + Ext.emptyString;
    }
});

/**
 * Ajoute une instance nulle quand le JSON à un attribut null pour une
 * association belongsTo.
 * 
 * Le problème est qu'on ne peux pas recevoir des association belongsTo avec des
 * null. L'autre alternative aurait été de faire en sorte que les belongsTo
 * puissent ne pas être renseignées du tout et de ne pas les serialiser coté
 * Serveur. (avec NullObjectTransformer)
 */
Ext.override(Ext.data.reader.Reader, {
    readAssociated: function(record, data) {
        var associations = record.associations.items, i = 0, length = associations.length, association, associationData, proxy, reader;

        for (i = 0; i < length; i++) {
            association = associations[i];
            associationData = this.getAssociatedDataRoot(data, association.associationKey || association.name);

            if (associationData) {
                reader = association.getReader();
                if (!reader) {
                    proxy = association.associatedModel.proxy;
                    // if the associated model has a Reader already, use that,
                    // otherwise attempt to create a sensible one
                    if (proxy) {
                        reader = proxy.getReader();
                    } else {
                        reader = new this.constructor({
                            model: association.associatedName
                        });
                    }
                }
                association.read(record, reader, associationData);
            }
            // ----------- Start FIX ----------------
            else {
                // TODO Devrait utiliser une methode que l'on rajouterai dans
                // l'objet association.
                record[association.instanceName] = null;
            }
            // ----------- End FIX ------------------
        }
    }
});

/**
 * Complement du fix ci dessus pour l'utilisation dans les tableaux, template,
 */
Ext.override(Ext.data.Model, {
    /**
     * @private This complex-looking method takes a given Model instance and
     *          returns an object containing all data from all of that Model's
     *          *loaded* associations. See {@link #getAssociatedData}
     * @param {Object}
     *            seenKeys A hash of all the associations we've already seen
     * @param {Number}
     *            depth The current depth
     * @return {Object} The nested data set for the Model's loaded associations
     */
    prepareAssociatedData: function(seenKeys, depth) {

        var me = this, associations = me.associations.items, associationCount = associations.length, associationData = {};
        var toRead = [], toReadKey = [], toReadIndex = [], associatedStore;
        var associatedRecords, associatedRecord, o, index, result, seenDepth, associationId, associatedRecordCount, association, i, j, type, name;

        for (i = 0; i < associationCount; i++) {
            association = associations[i];
            associationId = association.associationId;

            seenDepth = seenKeys[associationId];
            if (!seenDepth || seenDepth === depth) {

                seenKeys[associationId] = depth;

                type = association.type;
                name = association.name;
                if (type == 'hasMany') {
                    // this is the hasMany store filled with the
                    // associated data
                    associatedStore = me[association.storeName];

                    // we will use this to contain each associated
                    // record's data
                    associationData[name] = [];

                    // if it's loaded, put it into the association
                    // data
                    if (associatedStore && associatedStore.getCount() > 0) {
                        associatedRecords = associatedStore.data.items;
                        associatedRecordCount = associatedRecords.length;

                        // now we're finally iterating over the
                        // records in the association. Get
                        // all the records so we can process them
                        for (j = 0; j < associatedRecordCount; j++) {
                            associatedRecord = associatedRecords[j];
                            associationData[name][j] = associatedRecord.getData();
                            toRead.push(associatedRecord);
                            toReadKey.push(name);
                            toReadIndex.push(j);
                        }
                    }
                } else if (type == 'belongsTo' || type == 'hasOne') {
                    associatedRecord = me[association.instanceName];
                    // If we have a record, put it onto our list
                    // ----------- Start FIX ----------------
                    if (associatedRecord !== undefined && associatedRecord != null) {
                        // ----------- end FIX ----------------
                        associationData[name] = associatedRecord.getData();
                        toRead.push(associatedRecord);
                        toReadKey.push(name);
                        toReadIndex.push(-1);
                    }
                }
            }

        }

        for (i = 0, associatedRecordCount = toRead.length; i < associatedRecordCount; ++i) {
            associatedRecord = toRead[i];
            o = associationData[toReadKey[i]];
            index = toReadIndex[i];
            result = associatedRecord.prepareAssociatedData(seenKeys, depth + 1);
            if (index === -1) {
                Ext.apply(o, result);
            } else {
                Ext.apply(o[index], result);
            }
        }

        return associationData;
    }
});

/**
 * Permet d'envoyer les associations dans le JSON quand on sauve un model.
 * Adapté pour les asso belongsTo à partir de :
 * http://www.sencha.com/forum/showthread.php?141957-Saving-objects-that-are-linked-hasMany-relation-with-a-single-Store
 */
Ext.define('Ext.data.writer.DeepJson', {
    extend: 'Ext.data.writer.Json',
    alias: 'writer.deepjson',
    /*
     * This function overrides the default implementation of json writer. Any
     * hasMany relationships will be submitted as nested objects
     */
    getRecordData: function(record) {
        var me = this, i, association, childStore, data = {};
        data = this.callParent([record]);

        var fnEachChild = function(childRecord) {

            // Lié au Fix sur les champs null
            if (!childRecord) {
                data[association.associationKey] = null;
                record.setDirty();
            } else {
                // Recursively get the record data for children (depth first)
                var childData = this.getRecordData.call(this, childRecord);
                if (childRecord.dirty | childRecord.phantom | (childData != null)) {
                    data[association.name].push(childData);
                    record.setDirty();
                }
            }
        };

        // ---------------- FIX START -------------------
        /* Iterate over all the hasMany associations */
        for (i = 0; i < record.associations.length; i++) {
            association = record.associations.get(i);
            if (association.type == 'hasMany' && association.persist) {
                data[association.name] = [];
                childStore = eval('record.' + association.name + '()');

                // Iterate over all the children in the current association
                childStore.each(fnEachChild, me);
            } else if (association.type == 'belongsTo' && association.persist) {
                var childRecord = eval('record.' + association.getterName + '()');

                // Lié au Fix sur les champs null
                if (!childRecord) {
                    data[association.associationKey] = null;
                    record.setDirty();
                } else {
                    // Get the record data for child (depth first)
                    var childData = this.getRecordData.call(this, childRecord);

                    if (childRecord.dirty | childRecord.phantom | (childData != null)) {
                        data[association.associationKey] = childData;
                        record.setDirty();
                    }
                }

            }
        }
        // ---------------- FIX END -------------------
        return data;
    }
});

/**
 * La version originale du setter ne permet pas de stocker la valeur dans
 * l'instance... Elle ne fait que stocker une foreign key... Ce qui ne convient
 * pour le writer DeepJson qui transmet les instances et pas les foreignKey.
 */
Ext.override(Ext.data.BelongsToAssociation, {
    /**
     * @private Returns a setter function to be placed on the owner model's
     *          prototype
     * @return {Function} The setter function
     */
    createSetter: function() {
        var me = this, ownerModel = me.ownerModel, associatedModel = me.associatedModel, foreignKey = me.foreignKey, primaryKey = me.primaryKey,
        // FIX
        instanceName = me.instanceName;

        // 'this' refers to the Model instance inside this function
        return function(value, options, scope) {
            // -------- FIX -----------
            this[instanceName] = value;
            // -------- END FIX ---------
        };
    }
});

Ext.override(Ext.form.field.ComboBox, {

    /*
     * Ajout d'une methode permettant de récuperer le modèle même si le store ne
     * la contient pas encore : Par exemple : Pour la combo Commune : On peut
     * faire un setValue(model) le store ne sera pas chargé. La methode
     * findRecordById renvera null car le store ne contient pas la valeur
     * actuelle puisqu'il n'a jamais été chargé.
     */
    getValueModel: function() {
        var me = this, result = null;

        Ext.each(me.valueModels, function(model) {
            if (model.get(me.valueField) == me.getValue()) {
                result = model;
            }
        });
        return result;
    }
});

/*
 * PagingToolbar : Si le store est vide,la pagingToolBar affiche les boutons
 * Next et Last alors qu'il ne devrait pas
 */
Ext.override(Ext.toolbar.Paging, {
    getPageData: function() {

        var store = this.store, totalCount = store.getTotalCount();

        /* START FIX */
        if (totalCount === 0) {
            totalCount = 1;
        }
        /* END FIX */

        return {
            total: totalCount,
            currentPage: store.currentPage,
            pageCount: Math.ceil(totalCount / store.pageSize),
            fromRecord: ((store.currentPage - 1) * store.pageSize) + 1,
            toRecord: Math.min(store.currentPage * store.pageSize, totalCount)
        };
    }
});

/* Pour pouvoir ajouter un menu après le rendu */
// http://www.sencha.com/forum/showthread.php?46294-How-to-add-menus-to-a-button-manually
Ext.override(Ext.Button, {
    initComponent: function() {
        Ext.Button.superclass.initComponent.call(this);
        this.addEvents("click", "toggle", 'mouseover', 'mouseout', 'menushow', 'menuhide', 'menutriggerover', 'menutriggerout');
        if (this.menu) {
            var m = this.menu;
            delete this.menu;
            this.setMenu(m);
        }

        if (typeof this.toggleGroup === 'string') {
            this.enableToggle = true;
        }
    },

    setMenu: function(menu) {
        var hasMenu = (this.menu != null);
        this.menu = Ext.menu.MenuMgr.get(menu);
        if (this.rendered && !hasMenu) {
            // CVA : plus besoin en 4.1.1 ?
            // this.el.child(this.menuClassTarget).addClass('x-btn-with-menu');
            this.menu.on("show", this.onMenuShow, this);
            this.menu.on("hide", this.onMenuHide, this);
        }
    },

    clearMenu: function(destroy) {
        if (this.rendered) {
            // CVA : plus besoin en 4.1.1 ?
            // this.el.child(this.menuClassTarget).removeClass('x-btn-with-menu');
            this.menu.un('show', this.onMenuShow, this);
            this.menu.un('hide', this.onMenuHide, this);
        }
        if (destroy) {
            Ext.destroy(this.menu);
        }

        this.menu = null;
    }
});

// Ajout d'une fonction qui affiche ou non un élément.
Ext.Element.prototype.toggleDisplay = function(expand, animate) {
    // this.setDisplayed(this.dom.style.display == 'block' ? 'none' : 'block');
    var height = this.getHeight();
    if (height === 0) {
        this.setHeight(this.savedHeight, animate);
        // Hauteur de ligne
        this.setStyle("line-height", this.savedlineHeight);
        this.removeCls('remocra-collapse');
    } else if (expand !== true) {
        this.savedHeight = height;
        this.setHeight(0, animate);
        // Hauteur de ligne
        this.savedlineHeight = this.getStyle("line-height");
        this.setStyle("line-height", 0);
        this.addCls('remocra-collapse');
    }
};
