/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Gestion de crise : répertoire de lieux mobilisable dans le cadre d'une 
 * action "zoomer sur..". Associé à un épisode de crise
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CriseRepertoireLieu implements Serializable {

	private static final long serialVersionUID = -27372832;

	private Long crise;
	private Long repertoireLieu;

	public CriseRepertoireLieu() {}

	public CriseRepertoireLieu(CriseRepertoireLieu value) {
		this.crise = value.crise;
		this.repertoireLieu = value.repertoireLieu;
	}

	public CriseRepertoireLieu(
		Long crise,
		Long repertoireLieu
	) {
		this.crise = crise;
		this.repertoireLieu = repertoireLieu;
	}

	public Long getCrise() {
		return this.crise;
	}

	public CriseRepertoireLieu setCrise(Long crise) {
		this.crise = crise;
		return this;
	}

	public Long getRepertoireLieu() {
		return this.repertoireLieu;
	}

	public CriseRepertoireLieu setRepertoireLieu(Long repertoireLieu) {
		this.repertoireLieu = repertoireLieu;
		return this;
	}
}