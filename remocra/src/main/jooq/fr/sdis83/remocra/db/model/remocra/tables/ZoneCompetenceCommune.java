/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables;


import fr.sdis83.remocra.db.model.remocra.Remocra;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;


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
public class ZoneCompetenceCommune extends TableImpl<Record> {

	private static final long serialVersionUID = -1004658905;

	/**
	 * The reference instance of <code>remocra.zone_competence_commune</code>
	 */
	public static final ZoneCompetenceCommune ZONE_COMPETENCE_COMMUNE = new ZoneCompetenceCommune();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.zone_competence_commune.zone_competence_id</code>.
	 */
	public final TableField<Record, Long> ZONE_COMPETENCE_ID = createField("zone_competence_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>remocra.zone_competence_commune.commune_id</code>.
	 */
	public final TableField<Record, Long> COMMUNE_ID = createField("commune_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * Create a <code>remocra.zone_competence_commune</code> table reference
	 */
	public ZoneCompetenceCommune() {
		this("zone_competence_commune", null);
	}

	/**
	 * Create an aliased <code>remocra.zone_competence_commune</code> table reference
	 */
	public ZoneCompetenceCommune(String alias) {
		this(alias, ZONE_COMPETENCE_COMMUNE);
	}

	private ZoneCompetenceCommune(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private ZoneCompetenceCommune(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ZoneCompetenceCommune as(String alias) {
		return new ZoneCompetenceCommune(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ZoneCompetenceCommune rename(String name) {
		return new ZoneCompetenceCommune(name, null);
	}
}
