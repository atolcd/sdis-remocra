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
 * Document associé à une crise de manière générale ou à un évènemement survenu 
 * lors d'une crise
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CriseDocument extends TableImpl<Record> {

	private static final long serialVersionUID = -1497563218;

	/**
	 * The reference instance of <code>remocra.crise_document</code>
	 */
	public static final CriseDocument CRISE_DOCUMENT = new CriseDocument();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>remocra.crise_document.id</code>. Identifiant interne autogénéré
	 */
	public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaulted(true), this, "Identifiant interne autogénéré");

	/**
	 * The column <code>remocra.crise_document.sous_type</code>. Type de document pour la thématique crise. Ex : carte horodatée, photo, vidéo, etc.
	 */
	public final TableField<Record, String> SOUS_TYPE = createField("sous_type", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Type de document pour la thématique crise. Ex : carte horodatée, photo, vidéo, etc.");

	/**
	 * The column <code>remocra.crise_document.document</code>. Référence au document
	 */
	public final TableField<Record, Long> DOCUMENT = createField("document", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "Référence au document");

	/**
	 * The column <code>remocra.crise_document.crise</code>. Crise associée
	 */
	public final TableField<Record, Long> CRISE = createField("crise", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "Crise associée");

	/**
	 * The column <code>remocra.crise_document.evenement</code>. Evènement éventuellement associé
	 */
	public final TableField<Record, Long> EVENEMENT = createField("evenement", org.jooq.impl.SQLDataType.BIGINT, this, "Evènement éventuellement associé");

	/**
	 * Create a <code>remocra.crise_document</code> table reference
	 */
	public CriseDocument() {
		this("crise_document", null);
	}

	/**
	 * Create an aliased <code>remocra.crise_document</code> table reference
	 */
	public CriseDocument(String alias) {
		this(alias, CRISE_DOCUMENT);
	}

	private CriseDocument(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private CriseDocument(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Remocra.REMOCRA, aliased, parameters, "Document associé à une crise de manière générale ou à un évènemement survenu lors d'une crise");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<Record, Long> getIdentity() {
		return Keys.IDENTITY_CRISE_DOCUMENT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.CRISE_DOCUMENT_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.CRISE_DOCUMENT_PKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<Record, ?>> getReferences() {
		return Arrays.<ForeignKey<Record, ?>>asList(Keys.CRISE_DOCUMENT__CRISE_DOCUMENT_DOCUMENT_FK, Keys.CRISE_DOCUMENT__CRISE_DOCUMENT_CRISE_FK, Keys.CRISE_DOCUMENT__CRISE_DOCUMENT_EVENEMENT_FK);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CriseDocument as(String alias) {
		return new CriseDocument(alias, this);
	}

	/**
	 * Rename this table
	 */
	public CriseDocument rename(String name) {
		return new CriseDocument(name, null);
	}
}