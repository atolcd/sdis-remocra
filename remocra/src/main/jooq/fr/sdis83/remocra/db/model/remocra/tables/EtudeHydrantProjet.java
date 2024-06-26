/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables;


import com.vividsolutions.jts.geom.Geometry;

import fr.sdis83.remocra.db.converter.GeometryBinding;
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
public class EtudeHydrantProjet extends TableImpl<Record> {

	private static final long serialVersionUID = -733871965;

	/**
	 * The reference instance of <code>remocra.etude_hydrant_projet</code>
	 */
	public static final EtudeHydrantProjet ETUDE_HYDRANT_PROJET = new EtudeHydrantProjet();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.etude_hydrant_projet.id</code>.
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>remocra.etude_hydrant_projet.etude</code>.
	 */
	public final TableField<Record, Long> ETUDE = createField("etude", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>remocra.etude_hydrant_projet.type_deci</code>.
	 */
	public final TableField<Record, Long> TYPE_DECI = createField("type_deci", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>remocra.etude_hydrant_projet.type</code>.
	 */
	public final TableField<Record, String> TYPE = createField("type", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

	/**
	 * The column <code>remocra.etude_hydrant_projet.diametre_nominal</code>.
	 */
	public final TableField<Record, Long> DIAMETRE_NOMINAL = createField("diametre_nominal", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>remocra.etude_hydrant_projet.diametre_canalisation</code>.
	 */
	public final TableField<Record, Integer> DIAMETRE_CANALISATION = createField("diametre_canalisation", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>remocra.etude_hydrant_projet.capacite</code>.
	 */
	public final TableField<Record, Integer> CAPACITE = createField("capacite", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>remocra.etude_hydrant_projet.debit</code>.
	 */
	public final TableField<Record, Integer> DEBIT = createField("debit", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>remocra.etude_hydrant_projet.geometrie</code>.
	 */
	public final TableField<Record, Geometry> GEOMETRIE = createField("geometrie", org.jooq.impl.DefaultDataType.getDefaultDataType("USER-DEFINED"), this, "", new GeometryBinding());

	/**
	 * Create a <code>remocra.etude_hydrant_projet</code> table reference
	 */
	public EtudeHydrantProjet() {
		this("etude_hydrant_projet", null);
	}

	/**
	 * Create an aliased <code>remocra.etude_hydrant_projet</code> table reference
	 */
	public EtudeHydrantProjet(String alias) {
		this(alias, ETUDE_HYDRANT_PROJET);
	}

	private EtudeHydrantProjet(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private EtudeHydrantProjet(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_ETUDE_HYDRANT_PROJET;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.ETUDE_HYDRANT_PROJET_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.ETUDE_HYDRANT_PROJET_PKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<Record, ?>> getReferences() {
		return Arrays.<ForeignKey<Record, ?>>asList(Keys.ETUDE_HYDRANT_PROJET__FK_ETUDE, Keys.ETUDE_HYDRANT_PROJET__FK_TYPE_DECI, Keys.ETUDE_HYDRANT_PROJET__FK_DIAMETRE_NOMINAL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EtudeHydrantProjet as(String alias) {
		return new EtudeHydrantProjet(alias, this);
	}

	/**
	 * Rename this table
	 */
	public EtudeHydrantProjet rename(String name) {
		return new EtudeHydrantProjet(name, null);
	}
}
