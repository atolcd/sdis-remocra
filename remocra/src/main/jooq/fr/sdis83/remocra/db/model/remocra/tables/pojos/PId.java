/**
 * This class is generated by jOOQ
 */
package fr.sdis83.remocra.db.model.remocra.tables.pojos;


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
public class PId implements Serializable {

	private static final long serialVersionUID = 622239724;

	private Long id;

	public PId() {}

	public PId(PId value) {
		this.id = value.id;
	}

	public PId(
		Long id
	) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}

	public PId setId(Long id) {
		this.id = id;
		return this;
	}
}