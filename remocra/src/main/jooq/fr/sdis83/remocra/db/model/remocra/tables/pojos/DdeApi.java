/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;

import org.joda.time.Instant;


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
public class DdeApi implements Serializable {

	private static final long serialVersionUID = 1121242524;

	private Integer id;
	private Long    organisme;
	private String  code;
	private Instant dateDemande;
	private Boolean utilise;

	public DdeApi() {}

	public DdeApi(DdeApi value) {
		this.id = value.id;
		this.organisme = value.organisme;
		this.code = value.code;
		this.dateDemande = value.dateDemande;
		this.utilise = value.utilise;
	}

	public DdeApi(
		Integer id,
		Long    organisme,
		String  code,
		Instant dateDemande,
		Boolean utilise
	) {
		this.id = id;
		this.organisme = organisme;
		this.code = code;
		this.dateDemande = dateDemande;
		this.utilise = utilise;
	}

	public Integer getId() {
		return this.id;
	}

	public DdeApi setId(Integer id) {
		this.id = id;
		return this;
	}

	public Long getOrganisme() {
		return this.organisme;
	}

	public DdeApi setOrganisme(Long organisme) {
		this.organisme = organisme;
		return this;
	}

	public String getCode() {
		return this.code;
	}

	public DdeApi setCode(String code) {
		this.code = code;
		return this;
	}

	public Instant getDateDemande() {
		return this.dateDemande;
	}

	public DdeApi setDateDemande(Instant dateDemande) {
		this.dateDemande = dateDemande;
		return this;
	}

	public Boolean getUtilise() {
		return this.utilise;
	}

	public DdeApi setUtilise(Boolean utilise) {
		this.utilise = utilise;
		return this;
	}
}