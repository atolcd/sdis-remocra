/*
 * This file is generated by jOOQ.
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


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
public class DebitSimultaneHydrant implements Serializable {

    private static final long serialVersionUID = -240252569;

    private Long id;
    private Long debit;
    private Long hydrant;

    public DebitSimultaneHydrant() {}

    public DebitSimultaneHydrant(DebitSimultaneHydrant value) {
        this.id = value.id;
        this.debit = value.debit;
        this.hydrant = value.hydrant;
    }

    public DebitSimultaneHydrant(
        Long id,
        Long debit,
        Long hydrant
    ) {
        this.id = id;
        this.debit = debit;
        this.hydrant = hydrant;
    }

    public Long getId() {
        return this.id;
    }

    public DebitSimultaneHydrant setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getDebit() {
        return this.debit;
    }

    public DebitSimultaneHydrant setDebit(Long debit) {
        this.debit = debit;
        return this;
    }

    public Long getHydrant() {
        return this.hydrant;
    }

    public DebitSimultaneHydrant setHydrant(Long hydrant) {
        this.hydrant = hydrant;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DebitSimultaneHydrant (");

        sb.append(id);
        sb.append(", ").append(debit);
        sb.append(", ").append(hydrant);

        sb.append(")");
        return sb.toString();
    }
}