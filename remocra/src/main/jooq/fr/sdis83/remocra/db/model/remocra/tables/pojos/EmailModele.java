/**
 * This class is generated by jOOQ
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
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EmailModele implements Serializable {

	private static final long serialVersionUID = -784511700;

	private Long    id;
	private String  code;
	private String  corps;
	private String  objet;
	private Integer version;

	public EmailModele() {}

	public EmailModele(EmailModele value) {
		this.id = value.id;
		this.code = value.code;
		this.corps = value.corps;
		this.objet = value.objet;
		this.version = value.version;
	}

	public EmailModele(
		Long    id,
		String  code,
		String  corps,
		String  objet,
		Integer version
	) {
		this.id = id;
		this.code = code;
		this.corps = corps;
		this.objet = objet;
		this.version = version;
	}

	public Long getId() {
		return this.id;
	}

	public EmailModele setId(Long id) {
		this.id = id;
		return this;
	}

	public String getCode() {
		return this.code;
	}

	public EmailModele setCode(String code) {
		this.code = code;
		return this;
	}

	public String getCorps() {
		return this.corps;
	}

	public EmailModele setCorps(String corps) {
		this.corps = corps;
		return this;
	}

	public String getObjet() {
		return this.objet;
	}

	public EmailModele setObjet(String objet) {
		this.objet = objet;
		return this;
	}

	public Integer getVersion() {
		return this.version;
	}

	public EmailModele setVersion(Integer version) {
		this.version = version;
		return this;
	}
}
