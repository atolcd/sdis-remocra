/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.remocra.tables;


import fr.sdis83.remocra.db.model.remocra.Indexes;
import fr.sdis83.remocra.db.model.remocra.Keys;
import fr.sdis83.remocra.db.model.remocra.Remocra;

import java.util.Arrays;
import java.util.List;

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
 * Anomalie constatée lors d'une visite de contrôle réalisée sur une parcelle 
 * soumise à une obligation légale de débroussaillement
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OldebVisiteAnomalie extends TableImpl<Record> {

    private static final long serialVersionUID = -144736898;

    /**
     * The reference instance of <code>remocra.oldeb_visite_anomalie</code>
     */
    public static final OldebVisiteAnomalie OLDEB_VISITE_ANOMALIE = new OldebVisiteAnomalie();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>remocra.oldeb_visite_anomalie.visite</code>.
     */
    public final TableField<Record, Long> VISITE = createField("visite", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>remocra.oldeb_visite_anomalie.anomalie</code>.
     */
    public final TableField<Record, Long> ANOMALIE = createField("anomalie", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>remocra.oldeb_visite_anomalie</code> table reference
     */
    public OldebVisiteAnomalie() {
        this(DSL.name("oldeb_visite_anomalie"), null);
    }

    /**
     * Create an aliased <code>remocra.oldeb_visite_anomalie</code> table reference
     */
    public OldebVisiteAnomalie(String alias) {
        this(DSL.name(alias), OLDEB_VISITE_ANOMALIE);
    }

    /**
     * Create an aliased <code>remocra.oldeb_visite_anomalie</code> table reference
     */
    public OldebVisiteAnomalie(Name alias) {
        this(alias, OLDEB_VISITE_ANOMALIE);
    }

    private OldebVisiteAnomalie(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private OldebVisiteAnomalie(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Anomalie constatée lors d'une visite de contrôle réalisée sur une parcelle soumise à une obligation légale de débroussaillement"));
    }

    public <O extends Record> OldebVisiteAnomalie(Table<O> child, ForeignKey<O, Record> key) {
        super(child, key, OLDEB_VISITE_ANOMALIE);
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
        return Arrays.<Index>asList(Indexes.PK_VISITE_ANOMALIE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.PK_VISITE_ANOMALIE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(Keys.PK_VISITE_ANOMALIE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return Arrays.<ForeignKey<Record, ?>>asList(Keys.OLDEB_VISITE_ANOMALIE__FK_OLDEB_VISITE_ANOMALIE_VISITE, Keys.OLDEB_VISITE_ANOMALIE__FK_OLDEB_VISITE_ANOMALIE_ANOMALIE);
    }

    public OldebVisite oldebVisite() {
        return new OldebVisite(this, Keys.OLDEB_VISITE_ANOMALIE__FK_OLDEB_VISITE_ANOMALIE_VISITE);
    }

    public TypeOldebAnomalie typeOldebAnomalie() {
        return new TypeOldebAnomalie(this, Keys.OLDEB_VISITE_ANOMALIE__FK_OLDEB_VISITE_ANOMALIE_ANOMALIE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OldebVisiteAnomalie as(String alias) {
        return new OldebVisiteAnomalie(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OldebVisiteAnomalie as(Name alias) {
        return new OldebVisiteAnomalie(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public OldebVisiteAnomalie rename(String name) {
        return new OldebVisiteAnomalie(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public OldebVisiteAnomalie rename(Name name) {
        return new OldebVisiteAnomalie(name, null);
    }
}