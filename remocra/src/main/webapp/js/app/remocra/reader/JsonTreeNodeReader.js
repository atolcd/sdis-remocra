Ext.require('Ext.data.reader.Json');

Ext.define('Sdis.Remocra.reader.JsonTreeNodeReader', {
    extend: 'Ext.data.reader.Json',
    type: 'json',

    // Valeur du noeud utilisé pour créer la hiérarchie
    // - Chaque noeud du Json à afficher dans un tree nécessite d'avoir le même
    // nom
    root: 'data',

    /** ************************************* */
    /** ******** A définir ********** */
    /** ************************************* */

    // Field du json lu à utiliser comme texte à afficher dans l'arbre
    text: null,

    // Par défaut non étendu
    expanded: false,

    /** ************************************* */

    /**
     * Surcharge Lecture des noeuds principaux (noeuds parents)
     */
    readRecords: function(json) {
        // Conversion du Json pour qu'il colle au json attendu par un TreePanel
        var datas = this.convertJson(json);
        // Retour des données convertis par le reader au format NodeInterface
        var nodes = this.callParent([datas ]);
        // Mise à jour des données des NodeInterface adaptées
        this.updateRootNodes(nodes);
        return nodes;
    },

    /**
     * Surcharge Enrichissement des noeuds autres que root
     */
    extractData: function(data) {
        var self = this;

        var datas = this.callParent([data ]);
        Ext.each(datas, function(data) {
            // Text à afficher
            data.data.text = data.data[self.text];
            // C'est un noeud parent
            data.data.leaf = true;
            // Extention automatique du noeud parent
            data.data.expanded = self.expanded;
            // Chargement automatique
            // (pour ne pas que lancer de requête avec un TreeStore)
            data.data.loaded = true;
        });

        return datas;
    },

    /**
     * Conversion du Json pour qu'il colle au format attendu. La méthode est
     * spécifique au json retourné et doit être implémentée
     */
    convertJson: function(json) {
        throw 'unimplemented method: JsonNodeReader.convertJson non implémentée';
    },

    /**
     * Méthode utilitaire a utiliser si potentiellement les ids des enfants et
     * des parents peuvent être les mêmes.
     */
    changeIds: function(parent) {
        Ext.each(parent[this.root], function(child) {
            child.initialId = child.id;
            child.id = Math.random().toString(36).substring(7);
        });
    },

    /**
     * Mise à jour des noeuds pour coller avec l'interface. A définir
     * (OBLIGATOIRE): - text - leaf (true|false, faire le parcours des fils au
     * cas où
     */
    updateRootNodes: function(nodes) {
        var self = this;

        Ext.each(nodes.records, function(record) {
            // Text à afficher
            record.data.text = record.data[self.text];
            // C'est un noeud parent
            record.data.leaf = false;
        });
    }
});
