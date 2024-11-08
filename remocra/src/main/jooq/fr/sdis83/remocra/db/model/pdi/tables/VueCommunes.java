/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.pdi.tables;


import fr.sdis83.remocra.db.model.pdi.Pdi;

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
public class VueCommunes extends TableImpl<Record> {

	private static final long serialVersionUID = 1687133546;

	/**
	 * The reference instance of <code>pdi.vue_communes</code>
	 */
	public static final VueCommunes VUE_COMMUNES = new VueCommunes();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>pdi.vue_communes.id</code>.
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>pdi.vue_communes.libelle</code>.
	 */
	public final TableField<Record, String> LIBELLE = createField("libelle", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * Create a <code>pdi.vue_communes</code> table reference
	 */
	public VueCommunes() {
		this("vue_communes", null);
	}

	/**
	 * Create an aliased <code>pdi.vue_communes</code> table reference
	 */
	public VueCommunes(String alias) {
		this(alias, VUE_COMMUNES);
	}

	private VueCommunes(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private VueCommunes(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Pdi.PDI, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VueCommunes as(String alias) {
		return new VueCommunes(alias, this);
	}

	/**
	 * Rename this table
	 */
	public VueCommunes rename(String name) {
		return new VueCommunes(name, null);
	}
}
