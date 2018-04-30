/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables;


import fr.sdis83.remocra.db.model.remocra.Keys;
import fr.sdis83.remocra.db.model.remocra.Remocra;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
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
public class Metadonnee extends TableImpl<Record> {

	private static final long serialVersionUID = 1300446916;

	/**
	 * The reference instance of <code>remocra.metadonnee</code>
	 */
	public static final Metadonnee METADONNEE = new Metadonnee();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.metadonnee.id</code>.
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>remocra.metadonnee.resume</code>.
	 */
	public final TableField<Record, String> RESUME = createField("resume", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.metadonnee.titre</code>.
	 */
	public final TableField<Record, String> TITRE = createField("titre", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * The column <code>remocra.metadonnee.version</code>.
	 */
	public final TableField<Record, Integer> VERSION = createField("version", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>remocra.metadonnee.thematique</code>.
	 */
	public final TableField<Record, Long> THEMATIQUE = createField("thematique", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>remocra.metadonnee.url_fiche</code>.
	 */
	public final TableField<Record, String> URL_FICHE = createField("url_fiche", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.metadonnee.url_vignette</code>.
	 */
	public final TableField<Record, String> URL_VIGNETTE = createField("url_vignette", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>remocra.metadonnee.code_export</code>.
	 */
	public final TableField<Record, String> CODE_EXPORT = createField("code_export", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * Create a <code>remocra.metadonnee</code> table reference
	 */
	public Metadonnee() {
		this("metadonnee", null);
	}

	/**
	 * Create an aliased <code>remocra.metadonnee</code> table reference
	 */
	public Metadonnee(String alias) {
		this(alias, METADONNEE);
	}

	private Metadonnee(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private Metadonnee(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_METADONNEE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.METADONNEE_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.METADONNEE_PKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<Record, ?>> getReferences() {
		return Arrays.<ForeignKey<Record, ?>>asList(Keys.METADONNEE__FK507E37B0D27676E2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Metadonnee as(String alias) {
		return new Metadonnee(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Metadonnee rename(String name) {
		return new Metadonnee(name, null);
	}
}