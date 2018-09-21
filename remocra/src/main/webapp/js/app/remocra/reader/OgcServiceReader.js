Ext.define('Sdis.Remocra.reader.OgcServiceReader', {
    extend: 'Sdis.Remocra.reader.JsonTreeNodeReader',

    // withCaracteristiques = false, ne va pas peupler les caractéristiques des
    // catégories
    withCouches: true,

    /**
     * Surcharge de la méthode pour rendre le json compatible avec un TreePanel
     */
    convertJson: function(json) {
        // Si l'option avec enfant est activé il faut convertir le json
        if (this.withCouches) {
            var self = this;

            // Elément root à utiliser pour tous les noeuds fils
            Ext.each(json[self.root], function(childData) {
                // conversion du noeud fils "couches"
                // en noeud compatible avec le tree
                childData[self.root] = childData.ogcCouches;
                delete childData.ogcCouches;

                // Mise à jour des identifiants pour qu'ils soient unique dans
                // l'interface
                self.changeIds(childData);
            });
        }
        return json;
    }

});
