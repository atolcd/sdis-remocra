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
public class VueCommuneOldeb extends TableImpl<Record> {

	private static final long serialVersionUID = -433711591;

	/**
	 * The reference instance of <code>pdi.vue_commune_oldeb</code>
	 */
	public static final VueCommuneOldeb VUE_COMMUNE_OLDEB = new VueCommuneOldeb();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>pdi.vue_commune_oldeb.id</code>.
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>pdi.vue_commune_oldeb.libelle</code>.
	 */
	public final TableField<Record, String> LIBELLE = createField("libelle", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * Create a <code>pdi.vue_commune_oldeb</code> table reference
	 */
	public VueCommuneOldeb() {
		this("vue_commune_oldeb", null);
	}

	/**
	 * Create an aliased <code>pdi.vue_commune_oldeb</code> table reference
	 */
	public VueCommuneOldeb(String alias) {
		this(alias, VUE_COMMUNE_OLDEB);
	}

	private VueCommuneOldeb(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private VueCommuneOldeb(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Pdi.PDI, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VueCommuneOldeb as(String alias) {
		return new VueCommuneOldeb(alias, this);
	}

	/**
	 * Rename this table
	 */
	public VueCommuneOldeb rename(String name) {
		return new VueCommuneOldeb(name, null);
	}
}
