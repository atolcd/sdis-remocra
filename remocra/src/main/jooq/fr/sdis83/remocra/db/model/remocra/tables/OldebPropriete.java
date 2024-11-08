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
 * Lien entre un propriétaire et une parcelle soumise à une obligation légale 
 * de débroussaillement
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OldebPropriete extends TableImpl<Record> {

	private static final long serialVersionUID = -2097177800;

	/**
	 * The reference instance of <code>remocra.oldeb_propriete</code>
	 */
	public static final OldebPropriete OLDEB_PROPRIETE = new OldebPropriete();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.oldeb_propriete.id</code>.
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>remocra.oldeb_propriete.oldeb</code>.
	 */
	public final TableField<Record, Long> OLDEB = createField("oldeb", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>remocra.oldeb_propriete.proprietaire</code>.
	 */
	public final TableField<Record, Long> PROPRIETAIRE = createField("proprietaire", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>remocra.oldeb_propriete.residence</code>.
	 */
	public final TableField<Record, Long> RESIDENCE = createField("residence", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * Create a <code>remocra.oldeb_propriete</code> table reference
	 */
	public OldebPropriete() {
		this("oldeb_propriete", null);
	}

	/**
	 * Create an aliased <code>remocra.oldeb_propriete</code> table reference
	 */
	public OldebPropriete(String alias) {
		this(alias, OLDEB_PROPRIETE);
	}

	private OldebPropriete(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private OldebPropriete(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "Lien entre un propriétaire et une parcelle soumise à une obligation légale de débroussaillement");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_OLDEB_PROPRIETE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.OLDEB_PROPRIETE_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.OLDEB_PROPRIETE_PKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<Record, ?>> getReferences() {
		return Arrays.<ForeignKey<Record, ?>>asList(Keys.OLDEB_PROPRIETE__FK_OLDEB_PROPRIETE_OLDEB, Keys.OLDEB_PROPRIETE__FK_OLDEB_PROPRIETE_PROPRIETAIRE, Keys.OLDEB_PROPRIETE__FK_OLDEB_PROPRIETE_RESIDENCE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OldebPropriete as(String alias) {
		return new OldebPropriete(alias, this);
	}

	/**
	 * Rename this table
	 */
	public OldebPropriete rename(String name) {
		return new OldebPropriete(name, null);
	}
}
