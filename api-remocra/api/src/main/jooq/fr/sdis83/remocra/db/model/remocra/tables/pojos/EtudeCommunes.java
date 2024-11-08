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
public class EtudeCommunes implements Serializable {

    private static final long serialVersionUID = 738796482;

    private Long id;
    private Long etude;
    private Long commune;

    public EtudeCommunes() {}

    public EtudeCommunes(EtudeCommunes value) {
        this.id = value.id;
        this.etude = value.etude;
        this.commune = value.commune;
    }

    public EtudeCommunes(
        Long id,
        Long etude,
        Long commune
    ) {
        this.id = id;
        this.etude = etude;
        this.commune = commune;
    }

    public Long getId() {
        return this.id;
    }

    public EtudeCommunes setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getEtude() {
        return this.etude;
    }

    public EtudeCommunes setEtude(Long etude) {
        this.etude = etude;
        return this;
    }

    public Long getCommune() {
        return this.commune;
    }

    public EtudeCommunes setCommune(Long commune) {
        this.commune = commune;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("EtudeCommunes (");

        sb.append(id);
        sb.append(", ").append(etude);
        sb.append(", ").append(commune);

        sb.append(")");
        return sb.toString();
    }
}
