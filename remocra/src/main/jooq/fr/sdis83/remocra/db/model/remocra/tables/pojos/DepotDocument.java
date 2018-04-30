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
public class DepotDocument implements Serializable {

	private static final long serialVersionUID = -483216826;

	private Long id;
	private Long document;
	private Long utilisateur;

	public DepotDocument() {}

	public DepotDocument(DepotDocument value) {
		this.id = value.id;
		this.document = value.document;
		this.utilisateur = value.utilisateur;
	}

	public DepotDocument(
		Long id,
		Long document,
		Long utilisateur
	) {
		this.id = id;
		this.document = document;
		this.utilisateur = utilisateur;
	}

	public Long getId() {
		return this.id;
	}

	public DepotDocument setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getDocument() {
		return this.document;
	}

	public DepotDocument setDocument(Long document) {
		this.document = document;
		return this;
	}

	public Long getUtilisateur() {
		return this.utilisateur;
	}

	public DepotDocument setUtilisateur(Long utilisateur) {
		this.utilisateur = utilisateur;
		return this;
	}
}