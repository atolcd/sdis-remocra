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
public class VueFamillesPromethee implements Serializable {

	private static final long serialVersionUID = 2062529660;

	private Long   id;
	private String libelle;

	public VueFamillesPromethee() {}

	public VueFamillesPromethee(VueFamillesPromethee value) {
		this.id = value.id;
		this.libelle = value.libelle;
	}

	public VueFamillesPromethee(
		Long   id,
		String libelle
	) {
		this.id = id;
		this.libelle = libelle;
	}

	public Long getId() {
		return this.id;
	}

	public VueFamillesPromethee setId(Long id) {
		this.id = id;
		return this;
	}

	public String getLibelle() {
		return this.libelle;
	}

	public VueFamillesPromethee setLibelle(String libelle) {
		this.libelle = libelle;
		return this;
	}
}