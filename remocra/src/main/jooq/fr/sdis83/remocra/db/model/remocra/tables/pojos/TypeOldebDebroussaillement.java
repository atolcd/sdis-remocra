/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Etat du débroussaillement lié à une parcelle (ou un accès à une parcelle) 
 * sousmise à une obligation légale de débroussaillement
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeOldebDebroussaillement implements Serializable {

	private static final long serialVersionUID = 1618266289;

	private Long    id;
	private Boolean actif;
	private String  code;
	private String  nom;

	public TypeOldebDebroussaillement() {}

	public TypeOldebDebroussaillement(TypeOldebDebroussaillement value) {
		this.id = value.id;
		this.actif = value.actif;
		this.code = value.code;
		this.nom = value.nom;
	}

	public TypeOldebDebroussaillement(
		Long    id,
		Boolean actif,
		String  code,
		String  nom
	) {
		this.id = id;
		this.actif = actif;
		this.code = code;
		this.nom = nom;
	}

	public Long getId() {
		return this.id;
	}

	public TypeOldebDebroussaillement setId(Long id) {
		this.id = id;
		return this;
	}

	public Boolean getActif() {
		return this.actif;
	}

	public TypeOldebDebroussaillement setActif(Boolean actif) {
		this.actif = actif;
		return this;
	}

	public String getCode() {
		return this.code;
	}

	public TypeOldebDebroussaillement setCode(String code) {
		this.code = code;
		return this;
	}

	public String getNom() {
		return this.nom;
	}

	public TypeOldebDebroussaillement setNom(String nom) {
		this.nom = nom;
		return this;
	}
}
