/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import com.vividsolutions.jts.geom.Geometry;

import java.io.Serializable;

import javax.annotation.Generated;

import org.joda.time.Instant;


/**
 * Table de synchronisation des interventions pour utilisation dans REMOcRA
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Intervention implements Serializable {

	private static final long serialVersionUID = -510185697;

	private Long     id;
	private String   code;
	private String   codeType;
	private String   libelleType;
	private Integer  priorite;
	private Instant  dateCreation;
	private Instant  dateModification;
	private Instant  dateCloture;
	private String   cloture;
	private String   numVoie;
	private String   voie;
	private Long     commune;
	private Geometry geometrie;

	public Intervention() {}

	public Intervention(Intervention value) {
		this.id = value.id;
		this.code = value.code;
		this.codeType = value.codeType;
		this.libelleType = value.libelleType;
		this.priorite = value.priorite;
		this.dateCreation = value.dateCreation;
		this.dateModification = value.dateModification;
		this.dateCloture = value.dateCloture;
		this.cloture = value.cloture;
		this.numVoie = value.numVoie;
		this.voie = value.voie;
		this.commune = value.commune;
		this.geometrie = value.geometrie;
	}

	public Intervention(
		Long     id,
		String   code,
		String   codeType,
		String   libelleType,
		Integer  priorite,
		Instant  dateCreation,
		Instant  dateModification,
		Instant  dateCloture,
		String   cloture,
		String   numVoie,
		String   voie,
		Long     commune,
		Geometry geometrie
	) {
		this.id = id;
		this.code = code;
		this.codeType = codeType;
		this.libelleType = libelleType;
		this.priorite = priorite;
		this.dateCreation = dateCreation;
		this.dateModification = dateModification;
		this.dateCloture = dateCloture;
		this.cloture = cloture;
		this.numVoie = numVoie;
		this.voie = voie;
		this.commune = commune;
		this.geometrie = geometrie;
	}

	public Long getId() {
		return this.id;
	}

	public Intervention setId(Long id) {
		this.id = id;
		return this;
	}

	public String getCode() {
		return this.code;
	}

	public Intervention setCode(String code) {
		this.code = code;
		return this;
	}

	public String getCodeType() {
		return this.codeType;
	}

	public Intervention setCodeType(String codeType) {
		this.codeType = codeType;
		return this;
	}

	public String getLibelleType() {
		return this.libelleType;
	}

	public Intervention setLibelleType(String libelleType) {
		this.libelleType = libelleType;
		return this;
	}

	public Integer getPriorite() {
		return this.priorite;
	}

	public Intervention setPriorite(Integer priorite) {
		this.priorite = priorite;
		return this;
	}

	public Instant getDateCreation() {
		return this.dateCreation;
	}

	public Intervention setDateCreation(Instant dateCreation) {
		this.dateCreation = dateCreation;
		return this;
	}

	public Instant getDateModification() {
		return this.dateModification;
	}

	public Intervention setDateModification(Instant dateModification) {
		this.dateModification = dateModification;
		return this;
	}

	public Instant getDateCloture() {
		return this.dateCloture;
	}

	public Intervention setDateCloture(Instant dateCloture) {
		this.dateCloture = dateCloture;
		return this;
	}

	public String getCloture() {
		return this.cloture;
	}

	public Intervention setCloture(String cloture) {
		this.cloture = cloture;
		return this;
	}

	public String getNumVoie() {
		return this.numVoie;
	}

	public Intervention setNumVoie(String numVoie) {
		this.numVoie = numVoie;
		return this;
	}

	public String getVoie() {
		return this.voie;
	}

	public Intervention setVoie(String voie) {
		this.voie = voie;
		return this;
	}

	public Long getCommune() {
		return this.commune;
	}

	public Intervention setCommune(Long commune) {
		this.commune = commune;
		return this;
	}

	public Geometry getGeometrie() {
		return this.geometrie;
	}

	public Intervention setGeometrie(Geometry geometrie) {
		this.geometrie = geometrie;
		return this;
	}
}
