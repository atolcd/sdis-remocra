/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Service de données mobilisable sur un serveur OGC
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OgcService implements Serializable {

	private static final long serialVersionUID = 1077543706;

	private Long   id;
	private String typeService;
	private String nom;
	private String description;
	private Long   ogcSource;

	public OgcService() {}

	public OgcService(OgcService value) {
		this.id = value.id;
		this.typeService = value.typeService;
		this.nom = value.nom;
		this.description = value.description;
		this.ogcSource = value.ogcSource;
	}

	public OgcService(
		Long   id,
		String typeService,
		String nom,
		String description,
		Long   ogcSource
	) {
		this.id = id;
		this.typeService = typeService;
		this.nom = nom;
		this.description = description;
		this.ogcSource = ogcSource;
	}

	public Long getId() {
		return this.id;
	}

	public OgcService setId(Long id) {
		this.id = id;
		return this;
	}

	public String getTypeService() {
		return this.typeService;
	}

	public OgcService setTypeService(String typeService) {
		this.typeService = typeService;
		return this;
	}

	public String getNom() {
		return this.nom;
	}

	public OgcService setNom(String nom) {
		this.nom = nom;
		return this;
	}

	public String getDescription() {
		return this.description;
	}

	public OgcService setDescription(String description) {
		this.description = description;
		return this;
	}

	public Long getOgcSource() {
		return this.ogcSource;
	}

	public OgcService setOgcSource(Long ogcSource) {
		this.ogcSource = ogcSource;
		return this;
	}
}
