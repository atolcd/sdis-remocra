
Ext.ns('Sdis.Remocra.network');
Ext.define('Sdis.Remocra.network.CurrentUtilisateurStore', {
    extend : 'Ext.data.Store',
    singleton : true,
    requires : [ 'Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
    model : 'Sdis.Remocra.model.Utilisateur',
    pageSize : 1,
    proxy : {
        format : 'json',
        type : 'rest',
        headers : {
            'Accept' : 'application/json,application/xml',
            'Content-Type' : 'application/json'
        },
        url : Sdis.Remocra.util.Util.withBaseUrl('../utilisateurs/current'),
        reader : {
            type : 'json',
            root : 'data',
            totalProperty: 'total'
        },
        afterRequest:function(request,success){
            if(request.action == 'update'){
                // -- Réinitialise le current utilisateur pour qu'il soit rechargé au prochain appel.
                // On doit faire ça car dans la mise à jour du profil le store.sync() ne permet pas 
                // de rajouter un callback (possible en extjs 4.1 seulement) pour rafraichir le currentUser...
                Sdis.Remocra.network.CurrentUtilisateurStore.currentUser = null;
                
                if(success){
                    Sdis.Remocra.util.Msg.msg(this.title,
                    "Le profil a bien été mis à jour.");
                }else{
                    Ext.MessageBox.show({
                        title: this.title,
                        msg: "Un problème est survenu lors de la mise à jour du profil.",
                        buttons: Ext.Msg.OK,
                        icon: Ext.MessageBox.ERROR
                    });
                }
            }
        }
    },
    
    autoLoad : false,
    
    getCurrentUtilisateur : function(scope, callback) {
        if(!Sdis.Remocra.network.CurrentUtilisateurStore.currentUser){
            this.load({
                scope : scope,
                callback : function(records, operation, success) {
                    if(success){
                        Sdis.Remocra.network.CurrentUtilisateurStore.currentUser = records[0];
                        callback.call(scope, records[0]);
                    }
                }
            });
        }else{
            return callback.call(scope, Sdis.Remocra.network.CurrentUtilisateurStore.currentUser);
        }
    }
});
