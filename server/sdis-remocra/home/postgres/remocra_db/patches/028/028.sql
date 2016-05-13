BEGIN;



/*----------------------------------------------------------------------------*/
/*			Correction trigger tracabilité                                    */
/*----------------------------------------------------------------------------*/
CREATE OR REPLACE FUNCTION tracabilite.insert_hydrant(p_id_hydrant bigint, p_operation character varying)
  RETURNS void AS
$BODY$
BEGIN
    insert into tracabilite.hydrant (num_transac, nom_operation, date_operation,
    id_hydrant, numero, geometrie, insee, commune, lieu_dit, voie, carrefour, complement, agent1, agent2, date_recep, date_reco, date_contr, date_verif, dispo_terrestre, dispo_hbe, nature, type_hydrant, anomalies, observation, organisme,
    hbe, positionnement, materiau, vol_constate,
    diametre, debit, debit_max, pression, pression_dyn, marque, modele)
    SELECT
      -- traca
      txid_current() ,p_operation,  now() as date_operation,
      -- hydrant
      h.id, h.numero, h.geometrie, c.insee, c.nom, h.lieu_dit, h.voie, h.voie2, h.complement, h.agent1, h.agent2, h.date_recep, h.date_reco, h.date_contr, h.date_verif, h.dispo_terrestre, h.dispo_hbe, n.nom, th.nom, array_agg(anomalie.nom), h.observation, o.nom,
      -- pena
      pena.hbe, p.nom, mat.nom, v.nom,
      -- pibi
      d.nom, pibi.debit, pibi.debit_max, pibi.pression, pibi.pression_dyn, m.nom, mod.nom
      FROM remocra.hydrant h
      -- hydrant
      JOIN remocra.type_hydrant_nature n on (h.nature = n.id)
      JOIN remocra.type_hydrant th on (n.type_hydrant = th.id)
      LEFT JOIN remocra.commune c on (h.commune = c.id)
      LEFT JOIN remocra.organisme o on (h.organisme = o.id)
      -- pibi
      LEFT JOIN remocra.hydrant_pibi pibi on (pibi.id = h.id)
      LEFT JOIN remocra.type_hydrant_diametre d on (pibi.diametre = d.id)
      LEFT JOIN remocra.type_hydrant_marque m on (pibi.marque = m.id)
      LEFT JOIN remocra.type_hydrant_modele mod on (pibi.modele = mod.id)
      -- pena
      LEFT JOIN remocra.hydrant_pena pena on (pena.id = h.id)
      LEFT JOIN remocra.type_hydrant_positionnement p on (pena.positionnement = p.id)
      LEFT JOIN remocra.type_hydrant_materiau mat on (pena.materiau= mat.id)
      LEFT JOIN remocra.type_hydrant_vol_constate v on (pena.vol_constate = v.id)
      -- anomalies
      LEFT JOIN (remocra.hydrant_anomalies ha JOIN remocra.type_hydrant_anomalie a on (ha.anomalies = a.id)) anomalie on (anomalie.hydrant = h.id)
      WHERE h.id = p_id_hydrant
      GROUP BY h.id, h.numero, h.geometrie, c.insee, c.nom, h.lieu_dit, h.voie, h.voie2, h.complement, h.agent1, h.agent2, h.date_recep, h.date_reco, h.date_contr, h.date_verif, h.dispo_terrestre, h.dispo_hbe, n.nom, th.nom, h.observation, o.nom,
    pena.hbe, p.nom, mat.nom, v.nom,
    d.nom, pibi.debit, pibi.debit_max, pibi.pression, pibi.pression_dyn, m.nom, mod.nom;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

CREATE OR REPLACE FUNCTION tracabilite.update_hydrant(p_id_hydrant bigint, p_operation character varying, p_num_transac bigint)
  RETURNS void AS
$BODY$
BEGIN
    update tracabilite.hydrant
    SET numero =h.numero, geometrie = h.geometrie, insee = c.insee, commune = c.nom, lieu_dit = h.lieu_dit, voie = h.voie, carrefour = h.voie2, complement = h.complement, nature = n.nom, type_hydrant = th.nom,
    agent1 = h.agent1, agent2 = h.agent2, date_recep = h.date_recep, date_reco = h.date_reco, date_contr = h.date_contr, date_verif = h.date_verif, dispo_terrestre = h.dispo_terrestre, dispo_hbe = h.dispo_hbe,
    observation = h.observation, organisme = o.nom
    FROM remocra.hydrant h
    JOIN remocra.type_hydrant_nature n on (h.nature = n.id)
    JOIN remocra.type_hydrant th on (n.type_hydrant = th.id)
    LEFT JOIN remocra.commune c on (h.commune = c.id)
    LEFT JOIN remocra.organisme o on (h.organisme = o.id)
    WHERE h.id = p_id_hydrant
    AND num_transac = p_num_transac
    AND id_hydrant = p_id_hydrant;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;


/*----------------------------------------------------------------------------------------*/
/*--   PROCEDURE : calcul des anomalies pour les pressions, pression dyn et débit         */
/*----------------------------------------------------------------------------------------*/
CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression(id_hydrant bigint)
  RETURNS void AS
$BODY$
DECLARE
    p_code_diametre varchar;
	p_anomalie_id integer;
	p_rec remocra.hydrant_pibi%ROWTYPE;
BEGIN

	select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;

	-- Suppression des anciennes anomalies
	delete from remocra.hydrant_anomalies where hydrant = p_rec.id and anomalies in (select id from remocra.type_hydrant_anomalie where critere is null);

	-- Récupération du diamètre id
	select code into p_code_diametre from remocra.type_hydrant_diametre where id = p_rec.diametre;

	if FOUND then
		if (p_code_diametre = 'DIAM80' or p_code_diametre = 'DIAM100' or p_code_diametre = 'DIAM150') then
			-- pression
			if p_rec.pression >= 0 AND  p_rec.pression < 1 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_INSUFF';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			elsif p_rec.pression > 16 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_TROP_ELEVEE';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			end if;
			-- pression dyn
			if p_rec.pression_dyn >= 0.1 AND  p_rec.pression_dyn < 1 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_DYN_INSUFF';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			elsif p_rec.pression_dyn > 16 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'PRESSION_DYN_TROP_ELEVEE';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			end if;
		end if;

		if(p_code_diametre = 'DIAM80' or p_code_diametre = 'DIAM100') then
			-- débit
			if (p_rec.debit >= 0 AND  p_rec.debit < 30) then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			end if;
		end if;

		if p_code_diametre = 'DIAM80' then
			-- debit
			if p_rec.debit > 90 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_TROP_ELEVE';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			end if;

		elsif p_code_diametre = 'DIAM100' then
			-- debit
			if  p_rec.debit >= 30 AND  p_rec.debit <60  then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			elsif  p_rec.debit > 130 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_TROP_ELEVE';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			end if;

		elsif p_code_diametre = 'DIAM150' then
			-- debit
			if  p_rec.debit >= 60 AND p_rec.debit < 120 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF_NC';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			elsif  p_rec.debit > 150 then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_TROP_ELEVE';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			end if;
		end if;
	end if;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION remocra.trg_calcul_debit_pression()
  RETURNS trigger AS
$BODY$
DECLARE
	p_rec record;
BEGIN
	if (TG_OP = 'DELETE') then
		p_rec = OLD;
	else
		p_rec = NEW;
	end if;
	perform remocra.calcul_debit_pression(p_rec.id);

    RETURN p_rec;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

/*----------------------------------------------------------------------------------------*/
/*			TRIGGER : calcul des anomalies									  			  */
/*----------------------------------------------------------------------------------------*/
DROP TRIGGER if exists trig_debit_pression on remocra.hydrant_pibi;

CREATE TRIGGER trig_debit_pression
  AFTER INSERT OR UPDATE
  ON remocra.hydrant_pibi
  FOR EACH ROW
  EXECUTE PROCEDURE remocra.trg_calcul_debit_pression();


/*----------------------------------------------------------------------------------------*/
/*--   PROCEDURE : calcul des indisponibilités HBE et Terrestre        					  */
/*----------------------------------------------------------------------------------------*/

CREATE OR REPLACE FUNCTION remocra.calcul_indispo(id_hydrant bigint)
  RETURNS void AS
$BODY$
DECLARE
	p_hasNC boolean := false;
	p_code_nature varchar;
	nbTerr integer := 0;
	nbHBE  integer := 0;
	dispoTerrestre varchar;
	dispoHbe varchar;
	p_anomalies record;
	c_anomalies CURSOR (id integer,code_nat varchar) is
		select distinct
			hy.id as hydrant,
			thya.code as code_anomalie,
			thyan.val_indispo_hbe as val_indispo_hbe,
			thyan.val_indispo_terrestre as val_indispo_terrestre,
			thyn.code as code_nature
		from remocra.hydrant hy
		join remocra.hydrant_anomalies hya on (hy.id = hya.hydrant)
		join remocra.type_hydrant_anomalie thya on(thya.id =hya.anomalies)
		join remocra.type_hydrant_anomalie_nature thyan on (thya.id = thyan.anomalie)
		join remocra.type_hydrant_nature thyn on (thyan.nature = thyn.id)
		where thyn.code = code_nat
		and hy.id = id;
BEGIN
   select thyn.code into p_code_nature from remocra.hydrant hy join remocra.type_hydrant_nature thyn on (hy.nature = thyn.id) where hy.id = id_hydrant;
   if FOUND then
    FOR p_anomalies in c_anomalies(id_hydrant,p_code_nature) LOOP
		if(p_anomalies.val_indispo_terrestre is not null) then
			if(p_anomalies.code_anomalie  like '%_NC') then
				p_hasNC := true;
			end if;
			nbTerr := nbTerr + p_anomalies.val_indispo_terrestre;
		end if;
		if(p_anomalies.val_indispo_hbe is not null) then
			nbHBE := nbHBE + p_anomalies.val_indispo_hbe;
		end if;
	END LOOP;

	if(nbTerr >= 5) then
		dispoTerrestre := 'INDISPO';
	else
      if(p_hasNC) then
		dispoTerrestre := 'NON_CONFORME';
      else
		dispoTerrestre := 'DISPO';
      end if;
	end if;

	if(nbHBE  >= 5) then
		dispoHbe := 'INDISPO';
	else
		dispoHbe := 'DISPO';
	end if;

	update remocra.hydrant
	set dispo_hbe = dispoHbe,
	dispo_terrestre = dispoTerrestre
	where id = id_hydrant;

  end if;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION remocra.trg_calcul_indispo()
  RETURNS trigger AS
$BODY$
DECLARE
	p_rec record;
BEGIN
	if (TG_OP = 'DELETE') then
		p_rec = OLD;
	else
		p_rec = NEW;
	end if;
	perform remocra.calcul_indispo(p_rec.hydrant);
  RETURN p_rec;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

/*----------------------------------------------------------------------------------------*/
/*			TRIGGER : calcul des indisponibilités HBE et Terrestre					      */
/*----------------------------------------------------------------------------------------*/
DROP TRIGGER if exists trig_indispo on remocra.hydrant_anomalies;

CREATE TRIGGER trig_indispo
  AFTER INSERT OR DELETE
  ON remocra.hydrant_anomalies
  FOR EACH ROW
  EXECUTE PROCEDURE remocra.trg_calcul_indispo();

/*-----------------------------------------------------------------------------------------------------*/
/*			Procédure pour mettre à jour les indisponibilités HBE et Terrestre de tous les hydrants	   */
/*-----------------------------------------------------------------------------------------------------*/
CREATE OR REPLACE FUNCTION remocra.update_all_indispo()
  RETURNS varchar AS
$BODY$
DECLARE
    p_hydrant record;
	c_hydrants CURSOR is select * from remocra.hydrant;
	cpt integer := 0;
BEGIN
   FOR p_hydrant in c_hydrants LOOP
		perform remocra.calcul_indispo(p_hydrant.id);
	   cpt := cpt + 1;
   END LOOP;
   return cpt||' hydrants mis à jour';
END;
$BODY$
  LANGUAGE plpgsql VOLATILE;


/*----------------------------------------------------------------------------*/
/*			Recalcule des dispo des hydrant (assez long)                      */
/*----------------------------------------------------------------------------*/
select remocra.update_all_indispo();

/*-----------------------------------------------------------------------------------------------------*/
/*			Modification de la table TOURNEE  (ajout reservation, nb hydrant et etat)                  */
/*-----------------------------------------------------------------------------------------------------*/

ALTER TABLE remocra.tournee ADD COLUMN reservation bigint;
ALTER TABLE remocra.tournee ADD COLUMN etat integer;
ALTER TABLE remocra.tournee ADD COLUMN hydrant_count integer;

/*-----------------------------------------------------------------------------------------------------*/
/*			Procédure pour mettre à jour les informations d'une tournée (nb hydrant, avancement, )     */
/*-----------------------------------------------------------------------------------------------------*/
CREATE OR REPLACE function updateInfoTournee(bigint) RETURNS void as
$BODY$
update remocra.tournee t
set etat = (case
    when deb_sync is null then 0
else
    case
        when (select count(*) from remocra.hydrant h where h.tournee = t.id) = 0 then 100
    else
        (select count(*) from remocra.hydrant h where h.tournee = t.id and date_trunc('day', deb_sync) <= greatest(date_trunc('day', h.date_contr), date_trunc('day', h.date_reco), date_trunc('day', h.date_recep), date_trunc('day', h.date_verif))) * 100 / (select count(*) from remocra.hydrant h where h.tournee = t.id)
    end
end),
hydrant_count = (select count(*) from remocra.hydrant h where h.tournee = t.id)
where t.id = $1;
$BODY$
  LANGUAGE SQL;


/*-----------------------------------------------------------------------------------------------------*/
/*			Procédure trigger appelé lors de la modification/création/suppression d'un hydrant         */
/*-----------------------------------------------------------------------------------------------------*/
CREATE OR REPLACE FUNCTION remocra.trg_hydrant_tournee()
  RETURNS trigger AS
$BODY$
BEGIN
	if (TG_OP = 'DELETE' OR TG_OP = 'UPDATE') then
		if (OLD.tournee is not null) then
			perform updateInfoTournee(OLD.tournee);
		end if;
	end if;

	if (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') then
		if (NEW.tournee is not null) then
			perform updateInfoTournee(NEW.tournee);
		end if;
	end if;
	if (TG_OP = 'DELETE') then
		RETURN OLD;
	else
		return NEW;
	end if;
END;
$BODY$
  LANGUAGE plpgsql;

/*--------------------------------------------------*/
/*			Trigger sur hydrant                     */
/*--------------------------------------------------*/

DROP TRIGGER if exists trig_hydrant_tournee ON  remocra.hydrant;

CREATE  TRIGGER trig_hydrant_tournee
  AFTER INSERT OR UPDATE OR DELETE
  ON remocra.hydrant
  FOR EACH ROW
  EXECUTE PROCEDURE remocra.trg_hydrant_tournee();


/*----------------------------------------------------------------------------*/
/*			Recalcule des informations des tournées existantes                */
/*----------------------------------------------------------------------------*/
update remocra.hydrant set tournee = tournee where id in (select max(id) from remocra.hydrant where tournee is not null group by tournee);



COMMIT;
