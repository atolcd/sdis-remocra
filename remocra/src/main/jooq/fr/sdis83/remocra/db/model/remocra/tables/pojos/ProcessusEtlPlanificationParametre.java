/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Valeur de paramètre par défaut à utiliser lors d'une demande de processus 
 * planifié
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ProcessusEtlPlanificationParametre implements Serializable {

	private static final long serialVersionUID = -897351673;

	private Long   processusEtlPlanification;
	private Long   parametre;
	private String valeur;

	public ProcessusEtlPlanificationParametre() {}

	public ProcessusEtlPlanificationParametre(ProcessusEtlPlanificationParametre value) {
		this.processusEtlPlanification = value.processusEtlPlanification;
		this.parametre = value.parametre;
		this.valeur = value.valeur;
	}

	public ProcessusEtlPlanificationParametre(
		Long   processusEtlPlanification,
		Long   parametre,
		String valeur
	) {
		this.processusEtlPlanification = processusEtlPlanification;
		this.parametre = parametre;
		this.valeur = valeur;
	}

	public Long getProcessusEtlPlanification() {
		return this.processusEtlPlanification;
	}

	public ProcessusEtlPlanificationParametre setProcessusEtlPlanification(Long processusEtlPlanification) {
		this.processusEtlPlanification = processusEtlPlanification;
		return this;
	}

	public Long getParametre() {
		return this.parametre;
	}

	public ProcessusEtlPlanificationParametre setParametre(Long parametre) {
		this.parametre = parametre;
		return this;
	}

	public String getValeur() {
		return this.valeur;
	}

	public ProcessusEtlPlanificationParametre setValeur(String valeur) {
		this.valeur = valeur;
		return this;
	}
}
