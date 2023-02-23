DROP SCHEMA IF EXISTS remocra_sgo CASCADE;
CREATE SCHEMA remocra_sgo;
CREATE OR REPLACE VIEW remocra_sgo.hydrant AS
SELECT
	h.id as id_hydrant,
	h.geometrie,
	h.numero as numero_hydrant,
	h.numero_interne,
	h.code as nature,
	pena.materiau as id_materiau, thm.code as materiau,
	pena.positionnement as id_positionnement_pena, thp.code as positionnement,
	h.nature_deci as id_nature_deci, thnd.code as nature_deci,
	h.autorite_deci as id_autorite, o.code as code_autorite, o.nom as nom_autorite,
	h.sp_deci as id_service_public, o2.code as code_service_public, o2.nom as nom_service_public,
	h.voie, h.voie2,
	h.commune as id_commune, c.insee as code_insee_commune, c.code as code_postal, c.nom as nom_commune,
	h.domaine as id_domaine, thdomaine.nom as domaine,
	h.numero_voie, h.suffixe_voie,
	pibi.diametre as id_diametre, thd.code as code_diametre,
	pibi.modele as id_modele,
	pibi.marque as id_marque_pibi, thmarque.code as code_marque_pibi, thmarque.nom as nom_marque_pibi,
	pibi.type_reseau_canalisation as id_type_canalisation, trc.code as code_type_canalisation, trc.nom as nom_type_canalisation,
	pibi.debit as debit_pibi,
	pena.capacite as volume_pena,
	h.date_reco as date_reconnaissance_ope,
	h.date_contr as date_controle_technique,
	h.dispo_hbe, h.dispo_terrestre,
	anomalie.anomalies_id, anomalie.anomalies_code, anomalie.anomalies_nom,
	pena.coorddfci as coordonnee_dfci
from remocra.hydrant h
	left join remocra.hydrant_pibi pibi on pibi.id=h.id
	left join remocra.hydrant_pena pena on pena.id=h.id
	left join remocra.type_hydrant_materiau thm on thm.id = pena.materiau
	left join remocra.type_hydrant_positionnement thp on thp.id = pena.positionnement
	join remocra.type_hydrant_nature_deci thnd on thnd.id = h.nature_deci
	join remocra.organisme o on o.id = h.autorite_deci
	join remocra.organisme o2 on o2.id = h.sp_deci
	join remocra.commune c on c.id = h.commune
	left join remocra.type_hydrant_domaine thdomaine on thdomaine.id = h.domaine
	left join remocra.type_hydrant_diametre thd on thd.id = pibi.diametre
	left join remocra.type_hydrant_modele thmodele on thmodele.id = pibi.modele -- table vide
	left join remocra.type_hydrant_marque thmarque on thmodele.id = pibi.marque
	left join remocra.type_reseau_canalisation trc on trc.id = pibi.type_reseau_canalisation
	left join (
		select
			ha.hydrant,
			array_to_string(array_agg(tha.id),', ') as anomalies_id,
			array_to_string(array_agg(tha.code),', ') as anomalies_code,
			array_to_string(array_agg(tha.nom),', ') as anomalies_nom
		from
			remocra.hydrant_anomalies ha
			join remocra.type_hydrant_anomalie tha on (tha.id = ha.anomalies)
		group by
			ha.hydrant) as anomalie on (anomalie.hydrant = h.id)
