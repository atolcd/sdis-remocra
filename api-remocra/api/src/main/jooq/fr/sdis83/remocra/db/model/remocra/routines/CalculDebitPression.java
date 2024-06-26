/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.remocra.routines;


import fr.sdis83.remocra.db.model.remocra.Remocra;

import javax.annotation.Generated;

import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.Internal;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CalculDebitPression extends AbstractRoutine<java.lang.Void> {

    private static final long serialVersionUID = -280680154;

    /**
     * The parameter <code>remocra.calcul_debit_pression.id_hydrant</code>.
     */
    public static final Parameter<Long> ID_HYDRANT = Internal.createParameter("id_hydrant", org.jooq.impl.SQLDataType.BIGINT, false, false);

    /**
     * Create a new routine call instance
     */
    public CalculDebitPression() {
        super("calcul_debit_pression", Remocra.REMOCRA);

        addInParameter(ID_HYDRANT);
    }

    /**
     * Set the <code>id_hydrant</code> parameter IN value to the routine
     */
    public void setIdHydrant(Long value) {
        setValue(ID_HYDRANT, value);
    }
}
