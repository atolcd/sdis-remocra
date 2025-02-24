/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.couverture_hydraulique.tables.pojos;


import com.vividsolutions.jts.geom.Geometry;

import java.io.Serializable;

import javax.annotation.Generated;


/**
 * Table permettant de stocker les informations nécessaire au parcours de 
 * graph; basé sur l'algorithme de Dijkstra
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TempDistances implements Serializable {

	private static final long serialVersionUID = -2122429147;

	private Integer  sommet;
	private Integer  voie;
	private Double   distance;
	private Geometry geometrie;
	private String   side;
	private Boolean  traversable;
	private Integer  voieprecedente;
	private Integer  start;

	public TempDistances() {}

	public TempDistances(TempDistances value) {
		this.sommet = value.sommet;
		this.voie = value.voie;
		this.distance = value.distance;
		this.geometrie = value.geometrie;
		this.side = value.side;
		this.traversable = value.traversable;
		this.voieprecedente = value.voieprecedente;
		this.start = value.start;
	}

	public TempDistances(
		Integer  sommet,
		Integer  voie,
		Double   distance,
		Geometry geometrie,
		String   side,
		Boolean  traversable,
		Integer  voieprecedente,
		Integer  start
	) {
		this.sommet = sommet;
		this.voie = voie;
		this.distance = distance;
		this.geometrie = geometrie;
		this.side = side;
		this.traversable = traversable;
		this.voieprecedente = voieprecedente;
		this.start = start;
	}

	public Integer getSommet() {
		return this.sommet;
	}

	public TempDistances setSommet(Integer sommet) {
		this.sommet = sommet;
		return this;
	}

	public Integer getVoie() {
		return this.voie;
	}

	public TempDistances setVoie(Integer voie) {
		this.voie = voie;
		return this;
	}

	public Double getDistance() {
		return this.distance;
	}

	public TempDistances setDistance(Double distance) {
		this.distance = distance;
		return this;
	}

	public Geometry getGeometrie() {
		return this.geometrie;
	}

	public TempDistances setGeometrie(Geometry geometrie) {
		this.geometrie = geometrie;
		return this;
	}

	public String getSide() {
		return this.side;
	}

	public TempDistances setSide(String side) {
		this.side = side;
		return this;
	}

	public Boolean getTraversable() {
		return this.traversable;
	}

	public TempDistances setTraversable(Boolean traversable) {
		this.traversable = traversable;
		return this;
	}

	public Integer getVoieprecedente() {
		return this.voieprecedente;
	}

	public TempDistances setVoieprecedente(Integer voieprecedente) {
		this.voieprecedente = voieprecedente;
		return this;
	}

	public Integer getStart() {
		return this.start;
	}

	public TempDistances setStart(Integer start) {
		this.start = start;
		return this;
	}
}
