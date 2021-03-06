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
 * Parcelle cadastrale d'une commune
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CadastreParcelle extends TableImpl<Record> {

	private static final long serialVersionUID = 418029375;

	/**
	 * The reference instance of <code>remocra.cadastre_parcelle</code>
	 */
	public static final CadastreParcelle CADASTRE_PARCELLE = new CadastreParcelle();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.cadastre_parcelle.id</code>. Identifiant autogénéré
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "Identifiant autogénéré");

	/**
	 * The column <code>remocra.cadastre_parcelle.geometrie</code>. Géometrie de la parcelle cadastrale
	 */
	public final TableField<Record, Geometry> GEOMETRIE = createField("geometrie", org.jooq.impl.DefaultDataType.getDefaultDataType("USER-DEFINED"), this, "Géometrie de la parcelle cadastrale", new GeometryBinding());

	/**
	 * The column <code>remocra.cadastre_parcelle.numero</code>. Numéro d'identification de la parcelle
	 */
	public final TableField<Record, String> NUMERO = createField("numero", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Numéro d'identification de la parcelle");

	/**
	 * The column <code>remocra.cadastre_parcelle.section</code>. Identifiant de correspondance avec la section rattachée à une parcelle
	 */
	public final TableField<Record, Long> SECTION = createField("section", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "Identifiant de correspondance avec la section rattachée à une parcelle");

	/**
	 * Create a <code>remocra.cadastre_parcelle</code> table reference
	 */
	public CadastreParcelle() {
		this("cadastre_parcelle", null);
	}

	/**
	 * Create an aliased <code>remocra.cadastre_parcelle</code> table reference
	 */
	public CadastreParcelle(String alias) {
		this(alias, CADASTRE_PARCELLE);
	}

	private CadastreParcelle(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private CadastreParcelle(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "Parcelle cadastrale d'une commune");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_CADASTRE_PARCELLE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.CADASTRE_PARCELLE_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.CADASTRE_PARCELLE_PKEY, Keys.CADASTRE_PARCELLE_NUMERO);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<Record, ?>> getReferences() {
		return Arrays.<ForeignKey<Record, ?>>asList(Keys.CADASTRE_PARCELLE__FK_CADASTRE_PARCELLE_SECTION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CadastreParcelle as(String alias) {
		return new CadastreParcelle(alias, this);
	}

	/**
	 * Rename this table
	 */
	public CadastreParcelle rename(String name) {
		return new CadastreParcelle(name, null);
	}
}
