/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.pdi.tables.pojos;


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
public class TraitementCc implements Serializable {

	private static final long serialVersionUID = 137601805;

	private Integer idtraitement;
	private Integer idutilisateur;

	public TraitementCc() {}

	public TraitementCc(TraitementCc value) {
		this.idtraitement = value.idtraitement;
		this.idutilisateur = value.idutilisateur;
	}

	public TraitementCc(
		Integer idtraitement,
		Integer idutilisateur
	) {
		this.idtraitement = idtraitement;
		this.idutilisateur = idutilisateur;
	}

	public Integer getIdtraitement() {
		return this.idtraitement;
	}

	public TraitementCc setIdtraitement(Integer idtraitement) {
		this.idtraitement = idtraitement;
		return this;
	}

	public Integer getIdutilisateur() {
		return this.idutilisateur;
	}

	public TraitementCc setIdutilisateur(Integer idutilisateur) {
		this.idutilisateur = idutilisateur;
		return this;
	}
}