/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.tables;


import fr.sdis83.remocra.db.model.Indexes;
import fr.sdis83.remocra.db.model.Keys;
import fr.sdis83.remocra.db.model.Remocra;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Contact extends TableImpl<Record> {

    private static final long serialVersionUID = -1865812242;

    /**
     * The reference instance of <code>remocra.contact</code>
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
     * The column <code>remocra.contact.id</code>.
     */
    public final TableField<Record, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('remocra.contact_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>remocra.contact.appartenance</code>.
     */
    public final TableField<Record, String> APPARTENANCE = createField("appartenance", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>remocra.contact.id_appartenance</code>.
     */
    public final TableField<Record, String> ID_APPARTENANCE = createField("id_appartenance", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>remocra.contact.fonction</code>.
     */
    public final TableField<Record, String> FONCTION = createField("fonction", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>remocra.contact.civilite</code>.
     */
    public final TableField<Record, String> CIVILITE = createField("civilite", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>remocra.contact.nom</code>.
     */
    public final TableField<Record, String> NOM = createField("nom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>remocra.contact.prenom</code>.
     */
    public final TableField<Record, String> PRENOM = createField("prenom", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>remocra.contact.numero_voie</code>.
     */
    public final TableField<Record, String> NUMERO_VOIE = createField("numero_voie", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>remocra.contact.suffixe_voie</code>.
     */
    public final TableField<Record, String> SUFFIXE_VOIE = createField("suffixe_voie", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>remocra.contact.lieu_dit</code>.
     */
    public final TableField<Record, String> LIEU_DIT = createField("lieu_dit", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>remocra.contact.voie</code>.
     */
    public final TableField<Record, String> VOIE = createField("voie", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>remocra.contact.code_postal</code>.
     */
    public final TableField<Record, String> CODE_POSTAL = createField("code_postal", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>remocra.contact.ville</code>.
     */
    public final TableField<Record, String> VILLE = createField("ville", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>remocra.contact.pays</code>.
     */
    public final TableField<Record, String> PAYS = createField("pays", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>remocra.contact.telephone</code>.
     */
    public final TableField<Record, String> TELEPHONE = createField("telephone", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>remocra.contact.email</code>.
     */
    public final TableField<Record, String> EMAIL = createField("email", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * Create a <code>remocra.contact</code> table reference
     */
    public Contact() {
        this(DSL.name("contact"), null);
    }

    /**
     * Create an aliased <code>remocra.contact</code> table reference
     */
    public Contact(String alias) {
        this(DSL.name(alias), CONTACT);
    }

    /**
     * Create an aliased <code>remocra.contact</code> table reference
     */
    public Contact(Name alias) {
        this(alias, CONTACT);
    }

    private Contact(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private Contact(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Contact(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, CONTACT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Remocra.REMOCRA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.CONTACT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<Record, Long> getIdentity() {
        return Keys.IDENTITY_CONTACT;
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
    public Contact as(String alias) {
        return new Contact(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Contact as(Name alias) {
        return new Contact(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Contact rename(String name) {
        return new Contact(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Contact rename(Name name) {
        return new Contact(name, null);
    }
}