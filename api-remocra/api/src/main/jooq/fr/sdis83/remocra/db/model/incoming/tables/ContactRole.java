/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.incoming.tables;


import fr.sdis83.remocra.db.model.incoming.Incoming;
import fr.sdis83.remocra.db.model.incoming.Indexes;
import fr.sdis83.remocra.db.model.incoming.Keys;
import fr.sdis83.remocra.db.model.remocra.tables.Role;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
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
public class ContactRole extends TableImpl<Record> {

    private static final long serialVersionUID = 775344699;

    /**
     * The reference instance of <code>incoming.contact_role</code>
     */
    public static final ContactRole CONTACT_ROLE = new ContactRole();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>incoming.contact_role.id_role</code>.
     */
    public final TableField<Record, Long> ID_ROLE = createField("id_role", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>incoming.contact_role.id_contact</code>.
     */
    public final TableField<Record, UUID> ID_CONTACT = createField("id_contact", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

    /**
     * Create a <code>incoming.contact_role</code> table reference
     */
    public ContactRole() {
        this(DSL.name("contact_role"), null);
    }

    /**
     * Create an aliased <code>incoming.contact_role</code> table reference
     */
    public ContactRole(String alias) {
        this(DSL.name(alias), CONTACT_ROLE);
    }

    /**
     * Create an aliased <code>incoming.contact_role</code> table reference
     */
    public ContactRole(Name alias) {
        this(alias, CONTACT_ROLE);
    }

    private ContactRole(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private ContactRole(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> ContactRole(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, CONTACT_ROLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Incoming.INCOMING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.CONTACT_ROLE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.CONTACT_ROLE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.CONTACT_ROLE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.<ForeignKey<Record, ?>>asList(Keys.CONTACT_ROLE__CONTACT_ROLE_ID_ROLE_FKEY, Keys.CONTACT_ROLE__CONTACT_ROLE_ID_CONTACT_FKEY);
    }

    public Role role() {
        return new Role(this, Keys.CONTACT_ROLE__CONTACT_ROLE_ID_ROLE_FKEY);
    }

    public Contact contact() {
        return new Contact(this, Keys.CONTACT_ROLE__CONTACT_ROLE_ID_CONTACT_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactRole as(String alias) {
        return new ContactRole(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContactRole as(Name alias) {
        return new ContactRole(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ContactRole rename(String name) {
        return new ContactRole(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ContactRole rename(Name name) {
        return new ContactRole(name, null);
    }
}