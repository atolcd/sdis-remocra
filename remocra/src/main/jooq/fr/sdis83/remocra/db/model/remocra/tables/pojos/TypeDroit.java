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
public class TypeDroit implements Serializable {

	private static final long serialVersionUID = -1594687018;

	private Long    id;
	private String  code;
	private String  description;
	private String  nom;
	private Integer version;
	private String  categorie;

	public TypeDroit() {}

	public TypeDroit(TypeDroit value) {
		this.id = value.id;
		this.code = value.code;
		this.description = value.description;
		this.nom = value.nom;
		this.version = value.version;
		this.categorie = value.categorie;
	}

	public TypeDroit(
		Long    id,
		String  code,
		String  description,
		String  nom,
		Integer version,
		String  categorie
	) {
		this.id = id;
		this.code = code;
		this.description = description;
		this.nom = nom;
		this.version = version;
		this.categorie = categorie;
	}

	public Long getId() {
		return this.id;
	}

	public TypeDroit setId(Long id) {
		this.id = id;
		return this;
	}

	public String getCode() {
		return this.code;
	}

	public TypeDroit setCode(String code) {
		this.code = code;
		return this;
	}

	public String getDescription() {
		return this.description;
	}

	public TypeDroit setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getNom() {
		return this.nom;
	}

	public TypeDroit setNom(String nom) {
		this.nom = nom;
		return this;
	}

	public Integer getVersion() {
		return this.version;
	}

	public TypeDroit setVersion(Integer version) {
		this.version = version;
		return this;
	}

	public String getCategorie() {
		return this.categorie;
	}

	public TypeDroit setCategorie(String categorie) {
		this.categorie = categorie;
		return this;
	}
}
