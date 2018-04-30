/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Type de résidence pour le proprietaire d'une parcelle batie sousmise à 
 * une obligation légale de débroussaillement
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeOldebResidence implements Serializable {

	private static final long serialVersionUID = 2101026244;

	private Long    id;
	private Boolean actif;
	private String  code;
	private String  nom;

	public TypeOldebResidence() {}

	public TypeOldebResidence(TypeOldebResidence value) {
		this.id = value.id;
		this.actif = value.actif;
		this.code = value.code;
		this.nom = value.nom;
	}

	public TypeOldebResidence(
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

	public TypeOldebResidence setId(Long id) {
		this.id = id;
		return this;
	}

	public Boolean getActif() {
		return this.actif;
	}

	public TypeOldebResidence setActif(Boolean actif) {
		this.actif = actif;
		return this;
	}

	public String getCode() {
		return this.code;
	}

	public TypeOldebResidence setCode(String code) {
		this.code = code;
		return this;
	}

	public String getNom() {
		return this.nom;
	}

	public TypeOldebResidence setNom(String nom) {
		this.nom = nom;
		return this;
	}
}