/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.enums;


import fr.sdis83.remocra.db.model.remocra.Remocra;

import javax.annotation.Generated;

import org.jooq.EnumType;
import org.jooq.Schema;


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
public enum TypeGestionAgent implements EnumType {

	UTILISATEUR_CONNECTE_OBLIGATOIRE("UTILISATEUR_CONNECTE_OBLIGATOIRE"),

	UTILISATEUR_CONNECTE("UTILISATEUR_CONNECTE"),

	COMPOSANT_AGENT_ONLY("COMPOSANT_AGENT_ONLY"),

	VALEUR_PRECEDENTE("VALEUR_PRECEDENTE");

	private final String literal;

	private TypeGestionAgent(String literal) {
		this.literal = literal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Schema getSchema() {
		return Remocra.REMOCRA;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "type_gestion_agent";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLiteral() {
		return literal;
	}
}