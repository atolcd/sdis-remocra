/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


import com.vividsolutions.jts.geom.Geometry;

import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Détail des éléments des requêtes personnalisées REMOCRA
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RequeteModeleSelectionDetail implements Serializable {

	private static final long serialVersionUID = 1055061370;

	private Long     id;
	private Long     selection;
	private Geometry geometrie;

	public RequeteModeleSelectionDetail() {}

	public RequeteModeleSelectionDetail(RequeteModeleSelectionDetail value) {
		this.id = value.id;
		this.selection = value.selection;
		this.geometrie = value.geometrie;
	}

	public RequeteModeleSelectionDetail(
		Long     id,
		Long     selection,
		Geometry geometrie
	) {
		this.id = id;
		this.selection = selection;
		this.geometrie = geometrie;
	}

	public Long getId() {
		return this.id;
	}

	public RequeteModeleSelectionDetail setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getSelection() {
		return this.selection;
	}

	public RequeteModeleSelectionDetail setSelection(Long selection) {
		this.selection = selection;
		return this;
	}

	public Geometry getGeometrie() {
		return this.geometrie;
	}

	public RequeteModeleSelectionDetail setGeometrie(Geometry geometrie) {
		this.geometrie = geometrie;
		return this;
	}
}
