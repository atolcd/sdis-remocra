/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;

import org.joda.time.Instant;


/**
 * Message de suivi lié à une crise ou à un évènement. Le message peut être 
 * créé directement et manuellement par un utilisateur ou de manière indirecte 
 * par le système suite à une action dans REMOCRA (ex : mise à jour des attributs 
 * d'un évènement, ajout d'un document, création d'une carte horodatée, etc.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CriseSuivi implements Serializable {

	private static final long serialVersionUID = 1720895986;

	private Long    id;
	private String  origine;
	private String  objet;
	private String  message;
	private Instant creation;
	private Integer importance;
	private String  tags;
	private Long    crise;
	private Long    evenement;

	public CriseSuivi() {}

	public CriseSuivi(CriseSuivi value) {
		this.id = value.id;
		this.origine = value.origine;
		this.objet = value.objet;
		this.message = value.message;
		this.creation = value.creation;
		this.importance = value.importance;
		this.tags = value.tags;
		this.crise = value.crise;
		this.evenement = value.evenement;
	}

	public CriseSuivi(
		Long    id,
		String  origine,
		String  objet,
		String  message,
		Instant creation,
		Integer importance,
		String  tags,
		Long    crise,
		Long    evenement
	) {
		this.id = id;
		this.origine = origine;
		this.objet = objet;
		this.message = message;
		this.creation = creation;
		this.importance = importance;
		this.tags = tags;
		this.crise = crise;
		this.evenement = evenement;
	}

	public Long getId() {
		return this.id;
	}

	public CriseSuivi setId(Long id) {
		this.id = id;
		return this;
	}

	public String getOrigine() {
		return this.origine;
	}

	public CriseSuivi setOrigine(String origine) {
		this.origine = origine;
		return this;
	}

	public String getObjet() {
		return this.objet;
	}

	public CriseSuivi setObjet(String objet) {
		this.objet = objet;
		return this;
	}

	public String getMessage() {
		return this.message;
	}

	public CriseSuivi setMessage(String message) {
		this.message = message;
		return this;
	}

	public Instant getCreation() {
		return this.creation;
	}

	public CriseSuivi setCreation(Instant creation) {
		this.creation = creation;
		return this;
	}

	public Integer getImportance() {
		return this.importance;
	}

	public CriseSuivi setImportance(Integer importance) {
		this.importance = importance;
		return this;
	}

	public String getTags() {
		return this.tags;
	}

	public CriseSuivi setTags(String tags) {
		this.tags = tags;
		return this;
	}

	public Long getCrise() {
		return this.crise;
	}

	public CriseSuivi setCrise(Long crise) {
		this.crise = crise;
		return this;
	}

	public Long getEvenement() {
		return this.evenement;
	}

	public CriseSuivi setEvenement(Long evenement) {
		this.evenement = evenement;
		return this;
	}
}