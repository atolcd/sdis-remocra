/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.incoming.tables;


import fr.sdis83.remocra.db.model.incoming.Incoming;
import fr.sdis83.remocra.db.model.incoming.Keys;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
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
public class Contact extends TableImpl<Record> {

	private static final long serialVersionUID = -2064464990;

	/**
	 * The reference instance of <code>incoming.contact</code>
	 */
	public static final Contact CONTACT = new Contact();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>incoming.contact.id_contact</code>.
	 */
	public final TableField<Record, UUID> ID_CONTACT = createField("id_contact", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>incoming.contact.id_contact_remocra</code>.
	 */
	public final TableField<Record, Long> ID_CONTACT_REMOCRA = createField("id_contact_remocra", org.jooq.impl.SQLDataType.BIGINT, this, "");

	/**
	 * The column <code>incoming.contact.id_gestionnaire</code>.
	 */
	public final TableField<Record, UUID> ID_GESTIONNAIRE = createField("id_gestionnaire", org.jooq.impl.SQLDataType.UUID, this, "");

	/**
	 * The column <code>incoming.contact.fonction_contact</code>.
	 */
	public final TableField<Record, String> FONCTION_CONTACT = createField("fonction_contact", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>incoming.contact.civilite_contact</code>.
	 */
	public final TableField<Record, String> CIVILITE_CONTACT = createField("civilite_contact", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>incoming.contact.nom_contact</code>.
	 */
	public final TableField<Record, String> NOM_CONTACT = createField("nom_contact", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>incoming.contact.prenom_contact</code>.
	 */
	public final TableField<Record, String> PRENOM_CONTACT = createField("prenom_contact", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>incoming.contact.numero_voie_contact</code>.
	 */
	public final TableField<Record, String> NUMERO_VOIE_CONTACT = createField("numero_voie_contact", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>incoming.contact.suffixe_voie_contact</code>.
	 */
	public final TableField<Record, String> SUFFIXE_VOIE_CONTACT = createField("suffixe_voie_contact", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>incoming.contact.voie_contact</code>.
	 */
	public final TableField<Record, String> VOIE_CONTACT = createField("voie_contact", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>incoming.contact.lieu_dit_contact</code>.
	 */
	public final TableField<Record, String> LIEU_DIT_CONTACT = createField("lieu_dit_contact", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>incoming.contact.code_postal_contact</code>.
	 */
	public final TableField<Record, String> CODE_POSTAL_CONTACT = createField("code_postal_contact", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>incoming.contact.ville_contact</code>.
	 */
	public final TableField<Record, String> VILLE_CONTACT = createField("ville_contact", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>incoming.contact.pays_contact</code>.
	 */
	public final TableField<Record, String> PAYS_CONTACT = createField("pays_contact", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>incoming.contact.telephone_contact</code>.
	 */
	public final TableField<Record, String> TELEPHONE_CONTACT = createField("telephone_contact", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * The column <code>incoming.contact.email_contact</code>.
	 */
	public final TableField<Record, String> EMAIL_CONTACT = createField("email_contact", org.jooq.impl.SQLDataType.VARCHAR, this, "");

	/**
	 * Create a <code>incoming.contact</code> table reference
	 */
	public Contact() {
		this("contact", null);
	}

	/**
	 * Create an aliased <code>incoming.contact</code> table reference
	 */
	public Contact(String alias) {
		this(alias, CONTACT);
	}

	private Contact(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private Contact(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, Incoming.INCOMING, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<Record> getPrimaryKey() {
		return Keys.CONTACT_PKEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.CONTACT_PKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<Record, ?>> getReferences() {
		return Arrays.<ForeignKey<Record, ?>>asList(Keys.CONTACT__CONTACT_ID_GESTIONNAIRE_FKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Contact as(String alias) {
		return new Contact(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Contact rename(String name) {
		return new Contact(name, null);
	}
}
