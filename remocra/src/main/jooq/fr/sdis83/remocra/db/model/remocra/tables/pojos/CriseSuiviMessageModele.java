/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Gestion de crise : modèle de message à utiliser pour le suivi de crise. 
 * Utilisé notament par REMOCRA dans le cadre de la création automatique de 
 * messages suite à des actions utilisateurs
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CriseSuiviMessageModele implements Serializable {

	private static final long serialVersionUID = -1419484040;

	private Long    id;
	private Boolean actif;
	private String  code;
	private String  corps;
	private String  objet;
	private Long    importance;
	private String  tags;

	public CriseSuiviMessageModele() {}

	public CriseSuiviMessageModele(CriseSuiviMessageModele value) {
		this.id = value.id;
		this.actif = value.actif;
		this.code = value.code;
		this.corps = value.corps;
		this.objet = value.objet;
		this.importance = value.importance;
		this.tags = value.tags;
	}

	public CriseSuiviMessageModele(
		Long    id,
		Boolean actif,
		String  code,
		String  corps,
		String  objet,
		Long    importance,
		String  tags
	) {
		this.id = id;
		this.actif = actif;
		this.code = code;
		this.corps = corps;
		this.objet = objet;
		this.importance = importance;
		this.tags = tags;
	}

	public Long getId() {
		return this.id;
	}

	public CriseSuiviMessageModele setId(Long id) {
		this.id = id;
		return this;
	}

	public Boolean getActif() {
		return this.actif;
	}

	public CriseSuiviMessageModele setActif(Boolean actif) {
		this.actif = actif;
		return this;
	}

	public String getCode() {
		return this.code;
	}

	public CriseSuiviMessageModele setCode(String code) {
		this.code = code;
		return this;
	}

	public String getCorps() {
		return this.corps;
	}

	public CriseSuiviMessageModele setCorps(String corps) {
		this.corps = corps;
		return this;
	}

	public String getObjet() {
		return this.objet;
	}

	public CriseSuiviMessageModele setObjet(String objet) {
		this.objet = objet;
		return this;
	}

	public Long getImportance() {
		return this.importance;
	}

	public CriseSuiviMessageModele setImportance(Long importance) {
		this.importance = importance;
		return this;
	}

	public String getTags() {
		return this.tags;
	}

	public CriseSuiviMessageModele setTags(String tags) {
		this.tags = tags;
		return this;
	}
}
