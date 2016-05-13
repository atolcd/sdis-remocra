Ext.ns('Sdis.Remocra.model.util.Util');

//Pour la gestion des dépendances
Ext.define('Sdis.Remocra.model.util.Util', {});

/**
 * Reprise de Ext.data.Model.validate pour valider les nouvelles valeurs avant modification d'un enregistrement.
 * 
 * @param validations validations remplace this.validations
 * @param newValues this.get(field) remplacé par newValues[field]
 */
Sdis.Remocra.model.util.Util.validateNewValues = function(validations, newValues) {
    var errors      = Ext.create('Ext.data.Errors'),
    validators  = Ext.data.validations,
    length, validation, field, valid, type, i;

    if (validations) {
        length = validations.length;
    
        for (i = 0; i < length; i++) {
            validation = validations[i];
            field = validation.field || validation.name;
            type  = validation.type;
            valid = validators[type](validation, newValues[field]);
    
            if (!valid) {
                errors.add({
                    field  : field,
                    message: validation.message || validators[type + 'Message']
                });
            }
        }
    }
    
    return errors;
};
