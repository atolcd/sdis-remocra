/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Couche de données mobilisable sur un serveur de données OGC pour un protocole 
 * (service) donné
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OgcCouche implements Serializable {

	private static final long serialVersionUID = -621128527;

	private Long   id;
	private String code;
	private String nom;
	private String titre;
	private String definition;
	private Long   ogcService;

	public OgcCouche() {}

	public OgcCouche(OgcCouche value) {
		this.id = value.id;
		this.code = value.code;
		this.nom = value.nom;
		this.titre = value.titre;
		this.definition = value.definition;
		this.ogcService = value.ogcService;
	}

	public OgcCouche(
		Long   id,
		String code,
		String nom,
		String titre,
		String definition,
		Long   ogcService
	) {
		this.id = id;
		this.code = code;
		this.nom = nom;
		this.titre = titre;
		this.definition = definition;
		this.ogcService = ogcService;
	}

	public Long getId() {
		return this.id;
	}

	public OgcCouche setId(Long id) {
		this.id = id;
		return this;
	}

	public String getCode() {
		return this.code;
	}

	public OgcCouche setCode(String code) {
		this.code = code;
		return this;
	}

	public String getNom() {
		return this.nom;
	}

	public OgcCouche setNom(String nom) {
		this.nom = nom;
		return this;
	}

	public String getTitre() {
		return this.titre;
	}

	public OgcCouche setTitre(String titre) {
		this.titre = titre;
		return this;
	}

	public String getDefinition() {
		return this.definition;
	}

	public OgcCouche setDefinition(String definition) {
		this.definition = definition;
		return this;
	}

	public Long getOgcService() {
		return this.ogcService;
	}

	public OgcCouche setOgcService(Long ogcService) {
		this.ogcService = ogcService;
		return this;
	}
}
