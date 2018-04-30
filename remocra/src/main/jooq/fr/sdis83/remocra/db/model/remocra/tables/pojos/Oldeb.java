/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Informations relatives à une parcelle sousmise à une obligation légale 
 * de débroussaillement
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Oldeb implements Serializable {

	private static final long serialVersionUID = -473053982;

	private Long    id;
	private Object  geometrie;
	private String  section;
	private String  parcelle;
	private String  numVoie;
	private String  voie;
	private String  lieuDit;
	private Integer volume;
	private Integer largeurAcces;
	private Boolean portailElectrique;
	private String  codePortail;
	private Boolean actif;
	private Long    commune;
	private Long    zoneUrbanisme;
	private Long    acces;

	public Oldeb() {}

	public Oldeb(Oldeb value) {
		this.id = value.id;
		this.geometrie = value.geometrie;
		this.section = value.section;
		this.parcelle = value.parcelle;
		this.numVoie = value.numVoie;
		this.voie = value.voie;
		this.lieuDit = value.lieuDit;
		this.volume = value.volume;
		this.largeurAcces = value.largeurAcces;
		this.portailElectrique = value.portailElectrique;
		this.codePortail = value.codePortail;
		this.actif = value.actif;
		this.commune = value.commune;
		this.zoneUrbanisme = value.zoneUrbanisme;
		this.acces = value.acces;
	}

	public Oldeb(
		Long    id,
		Object  geometrie,
		String  section,
		String  parcelle,
		String  numVoie,
		String  voie,
		String  lieuDit,
		Integer volume,
		Integer largeurAcces,
		Boolean portailElectrique,
		String  codePortail,
		Boolean actif,
		Long    commune,
		Long    zoneUrbanisme,
		Long    acces
	) {
		this.id = id;
		this.geometrie = geometrie;
		this.section = section;
		this.parcelle = parcelle;
		this.numVoie = numVoie;
		this.voie = voie;
		this.lieuDit = lieuDit;
		this.volume = volume;
		this.largeurAcces = largeurAcces;
		this.portailElectrique = portailElectrique;
		this.codePortail = codePortail;
		this.actif = actif;
		this.commune = commune;
		this.zoneUrbanisme = zoneUrbanisme;
		this.acces = acces;
	}

	public Long getId() {
		return this.id;
	}

	public Oldeb setId(Long id) {
		this.id = id;
		return this;
	}

	public Object getGeometrie() {
		return this.geometrie;
	}

	public Oldeb setGeometrie(Object geometrie) {
		this.geometrie = geometrie;
		return this;
	}

	public String getSection() {
		return this.section;
	}

	public Oldeb setSection(String section) {
		this.section = section;
		return this;
	}

	public String getParcelle() {
		return this.parcelle;
	}

	public Oldeb setParcelle(String parcelle) {
		this.parcelle = parcelle;
		return this;
	}

	public String getNumVoie() {
		return this.numVoie;
	}

	public Oldeb setNumVoie(String numVoie) {
		this.numVoie = numVoie;
		return this;
	}

	public String getVoie() {
		return this.voie;
	}

	public Oldeb setVoie(String voie) {
		this.voie = voie;
		return this;
	}

	public String getLieuDit() {
		return this.lieuDit;
	}

	public Oldeb setLieuDit(String lieuDit) {
		this.lieuDit = lieuDit;
		return this;
	}

	public Integer getVolume() {
		return this.volume;
	}

	public Oldeb setVolume(Integer volume) {
		this.volume = volume;
		return this;
	}

	public Integer getLargeurAcces() {
		return this.largeurAcces;
	}

	public Oldeb setLargeurAcces(Integer largeurAcces) {
		this.largeurAcces = largeurAcces;
		return this;
	}

	public Boolean getPortailElectrique() {
		return this.portailElectrique;
	}

	public Oldeb setPortailElectrique(Boolean portailElectrique) {
		this.portailElectrique = portailElectrique;
		return this;
	}

	public String getCodePortail() {
		return this.codePortail;
	}

	public Oldeb setCodePortail(String codePortail) {
		this.codePortail = codePortail;
		return this;
	}

	public Boolean getActif() {
		return this.actif;
	}

	public Oldeb setActif(Boolean actif) {
		this.actif = actif;
		return this;
	}

	public Long getCommune() {
		return this.commune;
	}

	public Oldeb setCommune(Long commune) {
		this.commune = commune;
		return this;
	}

	public Long getZoneUrbanisme() {
		return this.zoneUrbanisme;
	}

	public Oldeb setZoneUrbanisme(Long zoneUrbanisme) {
		this.zoneUrbanisme = zoneUrbanisme;
		return this;
	}

	public Long getAcces() {
		return this.acces;
	}

	public Oldeb setAcces(Long acces) {
		this.acces = acces;
		return this;
	}
}