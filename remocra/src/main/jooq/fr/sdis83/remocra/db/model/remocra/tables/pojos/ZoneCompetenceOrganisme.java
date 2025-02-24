/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Vue des zones de compétence des organismes
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ZoneCompetenceOrganisme implements Serializable {

	private static final long serialVersionUID = -1551748708;

	private Long organismeId;
	private Long organismeContenuId;

	public ZoneCompetenceOrganisme() {}

	public ZoneCompetenceOrganisme(ZoneCompetenceOrganisme value) {
		this.organismeId = value.organismeId;
		this.organismeContenuId = value.organismeContenuId;
	}

	public ZoneCompetenceOrganisme(
		Long organismeId,
		Long organismeContenuId
	) {
		this.organismeId = organismeId;
		this.organismeContenuId = organismeContenuId;
	}

	public Long getOrganismeId() {
		return this.organismeId;
	}

	public ZoneCompetenceOrganisme setOrganismeId(Long organismeId) {
		this.organismeId = organismeId;
		return this;
	}

	public Long getOrganismeContenuId() {
		return this.organismeContenuId;
	}

	public ZoneCompetenceOrganisme setOrganismeContenuId(Long organismeContenuId) {
		this.organismeContenuId = organismeContenuId;
		return this;
	}
}
