Ext.require('Sdis.Remocra.network.RemocraRest');
Ext.require('Sdis.Remocra.model.TypeHydrantIndispoStatut');
Ext.define('Sdis.Remocra.model.HydrantIndispoTemporaire', {
    extend : 'Ext.data.Model',

    fields : [ {
        name : 'id',
        type : 'int',
        useNull : true
    }, {
        name : 'dateDebut',
        type : 'date',
        dateFormat: 'c'
    }, {
        name : 'dateFin',
        type : 'date',
        dateFormat: 'c'
    }, {
        name : 'motif',
        type : 'string'
    }, {
        name : 'dateRappelDebut',
        type : 'date'
    }, {
        name : 'dateRappelFin',
        type : 'date'
    }, {
        name : 'datePrevDebut',
        type : 'date',
        dateFormat: 'c'
    }, {
        name : 'datePrevFin',
        type : 'date',
        dateFormat: 'c'
    }, {
        name : 'basculeAutoIndispo',
        type : 'boolean'
    },{
        name : 'basculeAutoDispo',
        type : 'boolean'
    },{
        name : 'melAvantIndispo',
        type : 'boolean'
    },{
        name : 'melAvantDispo',
        type : 'boolean'
    },{
        name : 'timeDebut',
        type : 'string'
    }, {
        name : 'timeFin',
        type : 'string'
    }, {
       name : 'totalHydrants',
       type : 'integer'
    }, {
       name : 'countHydrant',
       type : 'integer'
    }, {
       name : 'statut',
        type: 'fk',
        useNull: true
    }, {
       name : 'commune',
       xtype: 'string'
    }, {
       name : 'nomStatut',
        type: 'string'
    },{
       name : 'geometrie',
       type : 'string'
    }, {
       name : 'description',
       type : 'string',
       convert: function(v, record){
                                if (!record) {
                                    return null;
                                }
                                if((record.get('dateDebut') < new Date()) && record.get('dateFin') == null){
                                    return "En cours depuis le "+Ext.Date.format(record.get("dateDebut"),'d/m/y'+' à '+ 'H:i:s');                            
                                }else if ((record.get('dateDebut') < new Date())&&(record.get('dateFin') > new Date())){
                                    return "En cours depuis le "+Ext.Date.format(record.get("dateDebut"),'d/m/y'+' à '+ 'H:i:s')
                                    + ', termine le '+Ext.Date.format(record.get("dateFin"),'d/m/y'+' à '+ 'H:i:s' );
                                } else if(record.get('dateDebut') > new Date() && record.get('dateFin') == null){
                                    return "Planifié le "+Ext.Date.format(record.get("dateDebut"),'d/m/y'+' à '+ 'H:i:s');    
                                }else if(record.get('dateDebut') > new Date()&&(record.get('dateFin') > new Date())){
                                    return "Planifié entre le "+Ext.Date.format(record.get("dateDebut"),'d/m/y'+' à '+ 'H:i:s')
                                    + ' et le '+Ext.Date.format(record.get("dateFin"),'d/m/y'+' à '+ 'H:i:s' );
                                } else if(record.get('dateFin') < new Date()){
                                    return "Terminé le "+Ext.Date.format(record.get("dateFin"),'d/m/y'+" à "+'H:i:s');
                                }

                                // Rien
                                return null;
                            }
    }
   ],
    associations : [ {
        type : 'hasMany',
        model : 'Sdis.Remocra.model.Hydrant',
        associationKey : 'hydrants',
        name : 'hydrants',
        persist : true
    }, {
               type: 'belongsTo',
               model: 'Sdis.Remocra.model.TypeHydrantIndispoStatut',
               associationKey: 'statut',
               name: 'statut',
               getterName: 'getStatut',
               setterName: 'setStatut',
               persist: true
    }],

    proxy : {
        type : 'remocra.rest',
        url : Sdis.Remocra.util.Util.withBaseUrl('../indisponibilites')
    }

});