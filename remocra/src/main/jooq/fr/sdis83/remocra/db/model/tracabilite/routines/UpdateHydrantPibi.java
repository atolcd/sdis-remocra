/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.tracabilite.routines;


import fr.sdis83.remocra.db.model.tracabilite.Tracabilite;

import javax.annotation.Generated;

import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UpdateHydrantPibi extends AbstractRoutine<java.lang.Void> {

	private static final long serialVersionUID = 1457490013;

	/**
	 * The parameter <code>tracabilite.update_hydrant_pibi.p_id_pibi</code>.
	 */
	public static final Parameter<Long> P_ID_PIBI = createParameter("p_id_pibi", org.jooq.impl.SQLDataType.BIGINT, false);

	/**
	 * The parameter <code>tracabilite.update_hydrant_pibi.p_operation</code>.
	 */
	public static final Parameter<String> P_OPERATION = createParameter("p_operation", org.jooq.impl.SQLDataType.VARCHAR, false);

	/**
	 * The parameter <code>tracabilite.update_hydrant_pibi.p_num_transac</code>.
	 */
	public static final Parameter<Long> P_NUM_TRANSAC = createParameter("p_num_transac", org.jooq.impl.SQLDataType.BIGINT, false);

	/**
	 * Create a new routine call instance
	 */
	public UpdateHydrantPibi() {
		super("update_hydrant_pibi", Tracabilite.TRACABILITE);

		addInParameter(P_ID_PIBI);
		addInParameter(P_OPERATION);
		addInParameter(P_NUM_TRANSAC);
	}

	/**
	 * Set the <code>p_id_pibi</code> parameter IN value to the routine
	 */
	public void setPIdPibi(Long value) {
		setValue(P_ID_PIBI, value);
	}

	/**
	 * Set the <code>p_operation</code> parameter IN value to the routine
	 */
	public void setPOperation(String value) {
		setValue(P_OPERATION, value);
	}

	/**
	 * Set the <code>p_num_transac</code> parameter IN value to the routine
	 */
	public void setPNumTransac(Long value) {
		setValue(P_NUM_TRANSAC, value);
	}
}