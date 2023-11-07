-- Vue remocra_sgo.remocrArtemis
DROP VIEW IF EXISTS remocra_sgo.remocrArtemis;
CREATE OR REPLACE VIEW remocra_sgo.remocrArtemis AS
	WITH anomalie AS (
		SELECT
			h_1.id,
			array_to_string(array_agg(tha.code), ' | '::text) AS liste_anomalie_code,
			array_to_string(array_agg(tha.nom), ' | '::text) AS liste_anomalie_nom
		FROM remocra.hydrant h_1
			LEFT JOIN remocra.hydrant_anomalies ha ON h_1.id = ha.hydrant
			LEFT JOIN remocra.type_hydrant_anomalie tha ON ha.anomalies = tha.id
		GROUP BY h_1.id
	)
	SELECT
		h.numero AS num,
		thn.code AS nature,
		thnd.code AS type_deci,
		h.complement AS complt,
		h.numero_voie AS num_v,
		h.suffixe_voie AS num_v_su,
		h.voie,
		dom.code AS domaine,
		o.code AS code_organisme_rattachement_commune,
		o.nom AS nom_organisme_rattachement_commune,
		pibi.debit,
		pibi.pression,
		pena.capacite,
		thd.code AS diametre,
		h.dispo_terrestre AS disponibilite,
		anomalie.liste_anomalie_code,
		anomalie.liste_anomalie_nom,
		h.geometrie,
		st_x(h.geometrie) AS coord_x,
		st_y(h.geometrie) AS coord_y,
		o_tournee.code as code_organisme_tournee,
		o_tournee.nom as nom_organisme_tournee
	FROM remocra.hydrant h
		JOIN remocra.type_hydrant_nature thn ON h.nature = thn.id
		JOIN remocra.type_hydrant_nature_deci thnd ON h.nature_deci = thnd.id
		LEFT JOIN remocra.type_hydrant_domaine dom ON h.domaine = dom.id
		LEFT JOIN remocra.hydrant_pibi pibi ON h.id = pibi.id
		LEFT JOIN remocra.hydrant_pena pena ON h.id = pena.id
		LEFT JOIN remocra.type_hydrant_diametre thd ON pibi.diametre = thd.id
		LEFT JOIN anomalie ON anomalie.id = h.id
		JOIN remocra.commune c ON h.commune = c.id
		JOIN remocra.organisme o_transi ON c.nom::text = o_transi.nom::text
		JOIN remocra.organisme o ON o.id = o_transi.organisme_parent
		LEFT join remocra.hydrant_tournees ht ON h.id = ht.hydrant
		LEFT join remocra.tournee t on ht.tournees = t.id
		LEFT join remocra.organisme o_tournee on t.affectation = o_tournee.id;