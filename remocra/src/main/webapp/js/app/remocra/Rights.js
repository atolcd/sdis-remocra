
Ext.ns('Sdis.Remocra');
Ext.define('Sdis.Remocra.Rights', {
    singleton : true,
    load : function(rights){
        this.userRights = rights;
    },
    
    /**
     * Retourne un objet dont les attribut create, read, update, delete sont des booleens
     * 
     * Utilisation : if( Sdis.Remocra.Rights.getRight('HYDRANTS').Read ){...}
     * TODO : typer les clés et les récupérer dynamiquement de l'enum AccessRight.Right (AccessRight.java) => Evite les typos sur les clés...
     */
    getRight : function(key){
        
        if(this.disabledRights){
            return {Read : true, Create : true, Update : true, Delete : true};
        }
        
        var right = {};
        
        //Il ne doit y avoir qu'une seule valeur pour la clé demandée.
        var originalRight = Ext.Array.filter(this.userRights, function(item){
            return item.key == key;
        })[0];
    
        var perms = originalRight ? originalRight.permissions : [];
        Ext.Array.forEach(perms, function(perm){
            if (perm == 'CREATE'){
                right.Create = true;
                right.Read = true;
            } else if (perm == 'READ'){
                right.Read = true;
            } else if (perm == 'UPDATE'){
                right.Update = true;
                right.Read = true;
            } else if (perm == 'DELETE'){
                right.Delete = true;
                right.Read = true;
            }
        });
        
        return right;
    },
    toggleRights : function(){
        this.disabledRights = !this.disabledRights;
    }
});
