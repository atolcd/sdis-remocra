DROP SCHEMA IF EXISTS remocra_sgo CASCADE;
CREATE SCHEMA remocra_sgo;
-- Vue REMOCRA.hydrant_visite
CREATE OR REPLACE VIEW remocra_sgo.visite as
	with anomalies as (
		select visite_anomalie.id , array_to_string(array_agg(tha.nom),', ')as liste_anomalie from
		(select hv.id, unnest(string_to_array(replace(replace(anomalies,'[',''),']',''),',')) as anomalie
		from remocra.hydrant_visite hv) as visite_anomalie
		join remocra.type_hydrant_anomalie tha on anomalie = tha.code
		group by visite_anomalie.id)
	select
		hv.id as id_visite,
		h.numero as numero_hydrant,
		hv."date" as date_visite,
		ths.code as code_type_visite,
		hv.ctrl_debit_pression, hv.agent1, hv.agent2,
		o.nom as nom_organisme,
		anomalies.liste_anomalie, hv.observations,
		u.nom as utilisateur_modification,
		hv.auteur_modification_flag, hv.debit, hv.debit_max,hv.debit_autre,
		hv.pression, hv.pression_dyn, hv.pression_dyn_deb, hv.pression_dyn_autre
	from remocra.hydrant_visite hv
		join remocra.hydrant h on hv.hydrant = h.id
		join remocra.type_hydrant_saisie ths on hv."type" = ths.id
		left join remocra.organisme o on hv.organisme = o.id
		left join remocra.utilisateur u on hv.utilisateur_modification = u.id
		left join anomalies on anomalies.id = hv.id;
-- Vue REMOCRA.hydrant_anomalie
CREATE OR REPLACE VIEW remocra_sgo.hydrant_anomalie as
	select
		ha.hydrant as id_hydrant,
		h.numero as numero_hydrant,
		tha.code as code_anomalie,
		tha.nom as nom_anomalie
		from remocra.hydrant_anomalies ha
			join remocra.hydrant h on ha.hydrant = h.id
			join remocra.type_hydrant_anomalie tha on ha.anomalies = tha.id
		order by h.numero;
-- Vue REMOCRA.hydrant
CREATE OR REPLACE VIEW remocra_sgo.hydrant as
	select
		h.id, h.numero, h.numero_interne, h.code,
		h.agent1, h.agent2, h.annee_fabrication, h.complement,
		h.dispo_admin, h.dispo_hbe, h.dispo_terrestre, h.geometrie,
		h.lieu_dit, h.observation, h."version", h.voie, h.voie2,
		c.insee as insee_commune,
		c.nom as commune,
		thd.code as code_domaine,
		thd.nom as nom_domaine,
		thn.code as code_nature,
		thn.nom as nom_nature,
		h.date_modification, h.courrier, h.gest_point_eau,
		o1.code as code_organisme,
		o1.nom as nom_organisme,
		zs.code as code_zone_speciale,
		zs.nom as nom_zone_speciale,
		h.date_attestation,
		u.identifiant as identifiant_utilisateur_modif,
		u.nom as nom_utilisateur_modif,
		u.prenom as prenom_utilisateur_modif,
		thnd.code as code_nature_deci,
		thnd.nom as nom_nature_deci,
		h.numero_voie, h.suffixe_voie,
		thniv.code as code_niveau,
		thniv.nom as nom_niveau,
		g.code as code_gestionnaire,
		g.nom as nom_gestionnaire,
		s.code as code_site,
		s.nom as nom_site,
		o2.code as code_authorite_deci,
		o2.nom as nom_authorite_deci,
		h.en_face,
		o3.code as code_sp_deci,
		o3.nom as nom_sp_deci,
		o4.code as code_maintenance_deci,
		o4.nom as nom_maintenance_deci,
		h.auteur_modification_flag, h.date_crea, h.date_contr, h.date_recep,
		h.date_reco, h.date_verif, h.date_gps, h.date_changement_dispo_terrestre,
		--PIBI
		pibi.debit as pibi_debit, pibi.debit_max as pibi_debit_max,
		pibi.pression as pibi_pression, pibi.pression_dyn as pibi_pression_dyn,
		diam.code as pibi_code_diametre, pibi.gest_reseau as pibi_gest_reseau,
		pibi.numeroscp as pibi_numero_scp, pibi.renversable as pibi_renversable,
		marque.nom as pibi_marque, modele.nom as pibi_modele,
		h2.numero as pibi_pena, pibi.pression_dyn_deb as pibi_pression_dyn_deb,
		h3.numero as pibi_jumele, pibi.dispositif_inviolabilite as pibi_dispositif_inviolabilite,
		res.nom as pibi_nom_reservoir, res.capacite as pibi_capacite_reservoir,
		o5.code as pibi_code_service_eaux, o5.nom as pibi_nom_service_eaux,
		pibi.debit_renforce as pibi_debit_renforce,
		trc.code as pibi_code_type_reseau_canal, trc.nom as pibi_nom_type_reseau_canal,
		tra.code as pibi_code_type_reseau_alim, tra.nom as pibi_nom_type_reseau_alim,
		pibi.diametre_canalisation as pibi_diam_canalisation,
		pibi.surpresse as pibi_surpresse, pibi.additive as pibi_additive,
		pibi.debit_autre as pibi_debit_autre, pibi.pression_dyn_autre as pibi_pression_dyn_autre,
		pibi.debit_nominal as pibi_debit_nominal,
		--PENA
		pena.capacite as pena_capacite,
		pena.coorddfci as pena_coord_dfci,
		pena.hbe as pena_hbe,
		pena.piste as pena_piste,
		thmater.code as pena_code_materiau,
		thmater.nom as pena_nom_materiau,
		thposi.code as pena_code_positionnement,
		thposi.nom as pena_nom_positionnement,
		vol.code as pena_code_volume_constate,
		vol.nom as pena_nom_volume_constate,
		pena.q_appoint as pena_q_appoint,
		pena.illimitee as pena_illimitee,
		pena.incertaine as pena_incertaine
	from remocra.hydrant h
		join remocra.commune c on h.commune = c.id
		left join remocra.type_hydrant_domaine thd on h.domaine = thd.id
		join remocra.type_hydrant_nature thn on h.nature = thn.id
		left join remocra.organisme o1 on h.organisme = o1.id
		left join remocra.zone_speciale zs on h.zone_speciale = zs.id
		left join remocra.utilisateur u on h.utilisateur_modification = u.id
		join remocra.type_hydrant_nature_deci thnd on h.nature_deci = thnd.id
		left join remocra.type_hydrant_niveau thniv on h.niveau = thniv.id
		left join remocra.gestionnaire g on h.gestionnaire = g.id
		left join remocra.site s on h.site = s.id
		left join remocra.organisme o2 on h.autorite_deci = o2.id
		left join remocra.organisme o3 on h.sp_deci = o3.id
		left join remocra.organisme o4 on h.maintenance_deci = o4.id
		--PIBI
		left join remocra.hydrant_pibi pibi on h.id = pibi.id
		left join remocra.type_hydrant_diametre diam on pibi.diametre = diam.id
		left join remocra.type_hydrant_marque marque on pibi.marque = marque.id
		left join remocra.type_hydrant_modele modele on pibi.modele = modele.id
		left join remocra.hydrant h2 on pibi.pena = h2.id
		left join remocra.hydrant h3 on pibi.jumele = h3.id
		left join remocra.hydrant_reservoir res on pibi.reservoir = res.id
		left join remocra.organisme o5 on pibi.service_eaux = o5.id
		left join remocra.type_reseau_canalisation trc on pibi.type_reseau_canalisation = trc.id
		left join remocra.type_reseau_alimentation tra on pibi.type_reseau_alimentation = tra.id
		--PENA
		left join remocra.hydrant_pena pena on h.id = pena.id
		left join remocra.type_hydrant_materiau thmater on pena.materiau = thmater.id
		left join remocra.type_hydrant_positionnement thposi on pena.positionnement = thposi.id
		left join remocra.type_hydrant_vol_constate vol on pena.vol_constate = vol.id;
-- Vue REMOCRA.remocrArtemis
CREATE OR REPLACE VIEW remocra_sgo.remocrArtemis as
	WITH anomalie AS (
             SELECT h_1.id,
                array_to_string(array_agg(tha.code), ' | '::text) AS liste_anomalie_code,
                array_to_string(array_agg(tha.nom), ' | '::text) AS liste_anomalie_nom
               FROM hydrant h_1
                 LEFT JOIN hydrant_anomalies ha ON h_1.id = ha.hydrant
                 LEFT JOIN type_hydrant_anomalie tha ON ha.anomalies = tha.id
              GROUP BY h_1.id
            )
     SELECT h.numero AS num,
        thn.code AS nature,
        h.complement AS complt,
        h.numero_voie AS num_v,
        h.suffixe_voie AS num_v_su,
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
       FROM hydrant h
         JOIN type_hydrant_nature thn ON h.nature = thn.id
         LEFT JOIN type_hydrant_domaine dom ON h.domaine = dom.id
         LEFT JOIN hydrant_pibi pibi ON h.id = pibi.id
         LEFT JOIN hydrant_pena pena ON h.id = pena.id
         LEFT JOIN type_hydrant_diametre thd ON pibi.diametre = thd.id
         LEFT JOIN anomalie ON anomalie.id = h.id
         JOIN commune c ON h.commune = c.id
         JOIN organisme o_transi ON c.nom::text = o_transi.nom::text
         JOIN organisme o ON o.id = o_transi.organisme_parent
         join hydrant_tournees ht ON h.id = ht.hydrant
         join tournee t on ht.tournees = t.id
         join organisme o_tournee on t.affectation = o_tournee.id;