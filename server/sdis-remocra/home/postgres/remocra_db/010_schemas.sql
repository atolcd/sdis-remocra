--
-- PostgreSQL database dump
--

-- Started on 2015-03-11 16:29:48 CET

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 26 (class 2615 OID 548150)
-- Name: pdi; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA pdi;


ALTER SCHEMA pdi OWNER TO postgres;

--
-- TOC entry 28 (class 2615 OID 548151)
-- Name: remocra; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA remocra;


ALTER SCHEMA remocra OWNER TO postgres;

--
-- TOC entry 39 (class 2615 OID 548154)
-- Name: tracabilite; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA tracabilite;


ALTER SCHEMA tracabilite OWNER TO postgres;

SET search_path = remocra, pg_catalog;

--
-- TOC entry 1268 (class 1255 OID 548964)
-- Dependencies: 28 2360
-- Name: calcul_debit_pression(bigint); Type: FUNCTION; Schema: remocra; Owner: postgres
--

CREATE FUNCTION calcul_debit_pression(id_hydrant bigint) RETURNS void
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION remocra.calcul_debit_pression(id_hydrant bigint) OWNER TO postgres;

--
-- TOC entry 1269 (class 1255 OID 548965)
-- Dependencies: 28 2360
-- Name: calcul_indispo(bigint); Type: FUNCTION; Schema: remocra; Owner: postgres
--

CREATE FUNCTION calcul_indispo(id_hydrant bigint) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
	p_hasNC boolean := false;
	p_code_nature varchar;
	nbTerr integer := 0;
	nbHBE  integer := 0;
	dispoTerrestre varchar;
	dispoHbe varchar;
	p_anomalies record;
	c_anomalies CURSOR (idCurs integer,code_nat varchar) is
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
		and hy.id = idCurs;
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
$$;


ALTER FUNCTION remocra.calcul_indispo(id_hydrant bigint) OWNER TO postgres;

--
-- TOC entry 1270 (class 1255 OID 548966)
-- Dependencies: 2360 28
-- Name: trg_calcul_debit_pression(); Type: FUNCTION; Schema: remocra; Owner: postgres
--

CREATE FUNCTION trg_calcul_debit_pression() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION remocra.trg_calcul_debit_pression() OWNER TO postgres;

--
-- TOC entry 1271 (class 1255 OID 548967)
-- Dependencies: 28 2360
-- Name: trg_calcul_indispo(); Type: FUNCTION; Schema: remocra; Owner: postgres
--

CREATE FUNCTION trg_calcul_indispo() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION remocra.trg_calcul_indispo() OWNER TO postgres;

--
-- TOC entry 1272 (class 1255 OID 548968)
-- Dependencies: 28 2360
-- Name: trg_hydrant_tournee(); Type: FUNCTION; Schema: remocra; Owner: postgres
--

CREATE FUNCTION trg_hydrant_tournee() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	if (TG_OP = 'DELETE' OR TG_OP = 'UPDATE') then
		if (OLD.tournee is not null) then
			perform remocra.updateInfoTournee(OLD.tournee);
		end if;
	end if;

	if (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') then
		if (NEW.tournee is not null) then
			perform remocra.updateInfoTournee(NEW.tournee);
		end if;
	end if;
	if (TG_OP = 'DELETE') then
		RETURN OLD;
	else
		return NEW;
	end if;
END;
$$;


ALTER FUNCTION remocra.trg_hydrant_tournee() OWNER TO postgres;

--
-- TOC entry 1273 (class 1255 OID 548969)
-- Dependencies: 2360 28
-- Name: update_all_indispo(); Type: FUNCTION; Schema: remocra; Owner: postgres
--

CREATE FUNCTION update_all_indispo() RETURNS character varying
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION remocra.update_all_indispo() OWNER TO postgres;

--
-- TOC entry 1274 (class 1255 OID 548970)
-- Dependencies: 28
-- Name: updateinfotournee(bigint); Type: FUNCTION; Schema: remocra; Owner: postgres
--

CREATE FUNCTION updateinfotournee(bigint) RETURNS void
    LANGUAGE sql
    AS $_$
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
$_$;


ALTER FUNCTION remocra.updateinfotournee(bigint) OWNER TO postgres;

SET search_path = tracabilite, pg_catalog;

--
-- TOC entry 1275 (class 1255 OID 548971)
-- Dependencies: 39 2360
-- Name: insert_hydrant(bigint, character varying); Type: FUNCTION; Schema: tracabilite; Owner: postgres
--

CREATE FUNCTION insert_hydrant(p_id_hydrant bigint, p_operation character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION tracabilite.insert_hydrant(p_id_hydrant bigint, p_operation character varying) OWNER TO postgres;

--
-- TOC entry 1276 (class 1255 OID 548972)
-- Dependencies: 39 2360
-- Name: trg_hydrant(); Type: FUNCTION; Schema: tracabilite; Owner: postgres
--

CREATE FUNCTION trg_hydrant() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    p_id bigint;
    p_operation varchar;
    p_rec record;
BEGIN
    if (TG_OP = 'DELETE') then
        p_rec = OLD;
    else
        p_rec = NEW;
    end if;

    if TG_TABLE_NAME = 'hydrant_anomalies' then
        p_id := p_rec.hydrant;
    else
        p_id := p_rec.ID;
    end if;
    select nom_operation into p_operation from tracabilite.hydrant where num_transac = txid_current() and id_hydrant = p_id;
    if FOUND then
        if (TG_WHEN = 'AFTER' AND p_operation != 'DELETE') then
        if TG_TABLE_NAME = 'hydrant' then
            perform tracabilite.update_hydrant(p_id, TG_OP::varchar, txid_current());
        elsif TG_TABLE_NAME = 'hydrant_pibi' then
            perform tracabilite.update_hydrant_pibi(p_id, TG_OP::varchar, txid_current());
        elsif TG_TABLE_NAME = 'hydrant_pena' then
            perform tracabilite.update_hydrant_pena(p_id, TG_OP::varchar, txid_current());
        elsif TG_TABLE_NAME = 'hydrant_anomalies' then
            perform tracabilite.update_hydrant_anomalies(p_id, TG_OP::varchar, txid_current());
        end if;
        end if;
    else
        perform tracabilite.insert_hydrant(p_id, TG_OP::varchar);
    end if;
    RETURN p_rec;
END;
$$;


ALTER FUNCTION tracabilite.trg_hydrant() OWNER TO postgres;

--
-- TOC entry 1277 (class 1255 OID 548973)
-- Dependencies: 39 2360
-- Name: update_hydrant(bigint, character varying, bigint); Type: FUNCTION; Schema: tracabilite; Owner: postgres
--

CREATE FUNCTION update_hydrant(p_id_hydrant bigint, p_operation character varying, p_num_transac bigint) RETURNS void
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION tracabilite.update_hydrant(p_id_hydrant bigint, p_operation character varying, p_num_transac bigint) OWNER TO postgres;

--
-- TOC entry 1278 (class 1255 OID 548974)
-- Dependencies: 39 2360
-- Name: update_hydrant_anomalies(bigint, character varying, bigint); Type: FUNCTION; Schema: tracabilite; Owner: postgres
--

CREATE FUNCTION update_hydrant_anomalies(p_id_hydrant bigint, p_operation character varying, p_num_transac bigint) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
    update tracabilite.hydrant
    SET anomalies = (select array_agg(a.nom) from remocra.hydrant_anomalies ha JOIN remocra.type_hydrant_anomalie a on (ha.anomalies = a.id) where ha.hydrant = p_id_hydrant)
    WHERE num_transac = p_num_transac
    AND id_hydrant = p_id_hydrant;
END;
$$;


ALTER FUNCTION tracabilite.update_hydrant_anomalies(p_id_hydrant bigint, p_operation character varying, p_num_transac bigint) OWNER TO postgres;

--
-- TOC entry 1279 (class 1255 OID 548975)
-- Dependencies: 39 2360
-- Name: update_hydrant_pena(bigint, character varying, bigint); Type: FUNCTION; Schema: tracabilite; Owner: postgres
--

CREATE FUNCTION update_hydrant_pena(p_id_pena bigint, p_operation character varying, p_num_transac bigint) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
    update tracabilite.hydrant
    SET hbe = pena.hbe, materiau = mat.nom, vol_constate = v.nom, positionnement = p.nom
    FROM remocra.hydrant_pena pena
    LEFT JOIN remocra.type_hydrant_positionnement p on (pena.positionnement = p.id)
    LEFT JOIN remocra.type_hydrant_materiau mat on (pena.materiau= mat.id)
    LEFT JOIN remocra.type_hydrant_vol_constate v on (pena.vol_constate = v.id)
    WHERE pena.id = id_hydrant
    AND num_transac = p_num_transac
    AND id_hydrant = p_id_pena;
END;
$$;


ALTER FUNCTION tracabilite.update_hydrant_pena(p_id_pena bigint, p_operation character varying, p_num_transac bigint) OWNER TO postgres;

--
-- TOC entry 1280 (class 1255 OID 548976)
-- Dependencies: 39 2360
-- Name: update_hydrant_pibi(bigint, character varying, bigint); Type: FUNCTION; Schema: tracabilite; Owner: postgres
--

CREATE FUNCTION update_hydrant_pibi(p_id_pibi bigint, p_operation character varying, p_num_transac bigint) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
    update tracabilite.hydrant
    SET  diametre = d.nom, debit = pibi.debit,  debit_max = pibi.debit_max, pression = pibi.pression, pression_dyn = pibi.pression_dyn, marque = m.nom, modele = mod.nom
    FROM remocra.hydrant_pibi pibi
    LEFT JOIN remocra.type_hydrant_diametre d on (pibi.diametre = d.id)
    LEFT JOIN remocra.type_hydrant_marque m on (pibi.marque = m.id)
    LEFT JOIN remocra.type_hydrant_modele mod on (pibi.modele = mod.id)
    WHERE pibi.id = id_hydrant
    AND num_transac = p_num_transac
    AND id_hydrant = p_id_pibi;
END;
$$;


ALTER FUNCTION tracabilite.update_hydrant_pibi(p_id_pibi bigint, p_operation character varying, p_num_transac bigint) OWNER TO postgres;

SET search_path = pdi, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 174 (class 1259 OID 549083)
-- Dependencies: 26
-- Name: modele_message; Type: TABLE; Schema: pdi; Owner: postgres; Tablespace: 
--

CREATE TABLE modele_message (
    idmodele integer NOT NULL,
    corps character varying NOT NULL,
    objet character varying NOT NULL
);


ALTER TABLE pdi.modele_message OWNER TO postgres;

--
-- TOC entry 175 (class 1259 OID 549089)
-- Dependencies: 26
-- Name: modele_traitement; Type: TABLE; Schema: pdi; Owner: postgres; Tablespace: 
--

CREATE TABLE modele_traitement (
    idmodele integer NOT NULL,
    code integer,
    description character varying NOT NULL,
    nom character varying NOT NULL,
    ref_chemin character varying NOT NULL,
    ref_nom character varying NOT NULL,
    type character varying NOT NULL,
    message_echec integer,
    message_succes integer
);


ALTER TABLE pdi.modele_traitement OWNER TO postgres;

--
-- TOC entry 176 (class 1259 OID 549095)
-- Dependencies: 26
-- Name: modele_traitement_parametre; Type: TABLE; Schema: pdi; Owner: postgres; Tablespace: 
--

CREATE TABLE modele_traitement_parametre (
    idparametre integer NOT NULL,
    form_etiquette character varying NOT NULL,
    form_num_ordre integer NOT NULL,
    form_obligatoire boolean NOT NULL,
    form_source_donnee character varying,
    form_type_valeur character varying NOT NULL,
    form_valeur_defaut character varying,
    nom character varying NOT NULL,
    idmodele integer NOT NULL
);


ALTER TABLE pdi.modele_traitement_parametre OWNER TO postgres;

--
-- TOC entry 177 (class 1259 OID 549101)
-- Dependencies: 26
-- Name: statut; Type: TABLE; Schema: pdi; Owner: postgres; Tablespace: 
--

CREATE TABLE statut (
    idstatut integer NOT NULL,
    code character varying NOT NULL,
    libelle character varying NOT NULL
);


ALTER TABLE pdi.statut OWNER TO postgres;

--
-- TOC entry 178 (class 1259 OID 549107)
-- Dependencies: 26
-- Name: traitement_idtraitement_seq; Type: SEQUENCE; Schema: pdi; Owner: postgres
--

CREATE SEQUENCE traitement_idtraitement_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE pdi.traitement_idtraitement_seq OWNER TO postgres;

--
-- TOC entry 179 (class 1259 OID 549109)
-- Dependencies: 3765 26
-- Name: traitement; Type: TABLE; Schema: pdi; Owner: postgres; Tablespace: 
--

CREATE TABLE traitement (
    idtraitement integer DEFAULT nextval('traitement_idtraitement_seq'::regclass) NOT NULL,
    demande timestamp without time zone NOT NULL,
    execution timestamp without time zone,
    idutilisateur integer NOT NULL,
    urlressource character varying,
    idmodele integer,
    idstatut integer NOT NULL
);


ALTER TABLE pdi.traitement OWNER TO postgres;

--
-- TOC entry 180 (class 1259 OID 549116)
-- Dependencies: 26
-- Name: traitement_cc; Type: TABLE; Schema: pdi; Owner: postgres; Tablespace: 
--

CREATE TABLE traitement_cc (
    idtraitement integer NOT NULL,
    idutilisateur integer NOT NULL
);


ALTER TABLE pdi.traitement_cc OWNER TO postgres;

--
-- TOC entry 181 (class 1259 OID 549119)
-- Dependencies: 26
-- Name: traitement_parametre; Type: TABLE; Schema: pdi; Owner: postgres; Tablespace: 
--

CREATE TABLE traitement_parametre (
    idparametre integer NOT NULL,
    idtraitement integer NOT NULL,
    valeur character varying NOT NULL
);


ALTER TABLE pdi.traitement_parametre OWNER TO postgres;

SET search_path = remocra, pg_catalog;

--
-- TOC entry 182 (class 1259 OID 549125)
-- Dependencies: 3766 3768 3769 3770 1574 28
-- Name: commune; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE commune (
    id bigint NOT NULL,
    geometrie public.geometry NOT NULL,
    insee character varying NOT NULL,
    nom character varying NOT NULL,
    pprif boolean DEFAULT false NOT NULL,
    code character varying,
    CONSTRAINT enforce_dims_geometrie CHECK ((public.st_ndims(geometrie) = 2)),
    CONSTRAINT enforce_geotype_geometrie CHECK ((public.geometrytype(geometrie) = 'MULTIPOLYGON'::text)),
    CONSTRAINT enforce_srid_geometrie CHECK ((public.st_srid(geometrie) = 2154))
);


ALTER TABLE remocra.commune OWNER TO postgres;

--
-- TOC entry 4208 (class 0 OID 0)
-- Dependencies: 182
-- Name: TABLE commune; Type: COMMENT; Schema: remocra; Owner: postgres
--

COMMENT ON TABLE commune IS 'Commune du Var. Géométrie en WGS 84 de type surfacique';


--
-- TOC entry 4209 (class 0 OID 0)
-- Dependencies: 182
-- Name: COLUMN commune.id; Type: COMMENT; Schema: remocra; Owner: postgres
--

COMMENT ON COLUMN commune.id IS 'Identifiant interne à la base de données';


--
-- TOC entry 4210 (class 0 OID 0)
-- Dependencies: 182
-- Name: COLUMN commune.geometrie; Type: COMMENT; Schema: remocra; Owner: postgres
--

COMMENT ON COLUMN commune.geometrie IS 'Limite communale de type surfacique. Coordonnées en WGS84';


--
-- TOC entry 4211 (class 0 OID 0)
-- Dependencies: 182
-- Name: COLUMN commune.insee; Type: COMMENT; Schema: remocra; Owner: postgres
--

COMMENT ON COLUMN commune.insee IS 'Code insee';


--
-- TOC entry 4212 (class 0 OID 0)
-- Dependencies: 182
-- Name: COLUMN commune.nom; Type: COMMENT; Schema: remocra; Owner: postgres
--

COMMENT ON COLUMN commune.nom IS 'Nom';


--
-- TOC entry 183 (class 1259 OID 549135)
-- Dependencies: 28
-- Name: param_conf; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE param_conf (
    cle character varying NOT NULL,
    description character varying,
    valeur character varying NOT NULL,
    version integer
);


ALTER TABLE remocra.param_conf OWNER TO postgres;

SET search_path = pdi, pg_catalog;

--
-- TOC entry 184 (class 1259 OID 549141)
-- Dependencies: 3548 26
-- Name: vue_communes; Type: VIEW; Schema: pdi; Owner: postgres
--

CREATE VIEW vue_communes AS
    SELECT allin.id, allin.libelle FROM (SELECT (-1) AS id, 'Toutes' AS libelle, NULL::unknown AS tricol UNION SELECT commune.id, commune.nom AS libelle, commune.nom AS tricol FROM remocra.commune WHERE ((commune.insee)::text ~~ ((SELECT param_conf.valeur FROM remocra.param_conf WHERE ((param_conf.cle)::text = 'COMMUNES_INSEE_LIKE_FILTRE_SQL'::text)))::text)) allin ORDER BY allin.tricol NULLS FIRST;


ALTER TABLE pdi.vue_communes OWNER TO postgres;

SET search_path = remocra, pg_catalog;

--
-- TOC entry 185 (class 1259 OID 549145)
-- Dependencies: 3771 3772 28
-- Name: type_rci_prom_famille; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_rci_prom_famille (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


ALTER TABLE remocra.type_rci_prom_famille OWNER TO postgres;

SET search_path = pdi, pg_catalog;

--
-- TOC entry 186 (class 1259 OID 549153)
-- Dependencies: 3549 26
-- Name: vue_familles_promethee; Type: VIEW; Schema: pdi; Owner: postgres
--

CREATE VIEW vue_familles_promethee AS
    SELECT allin.id, allin.libelle FROM (SELECT (-1) AS id, 'Toutes'::character varying AS libelle, NULL::character varying AS tricol UNION SELECT type_rci_prom_famille.id, type_rci_prom_famille.nom AS libelle, type_rci_prom_famille.nom AS tricol FROM remocra.type_rci_prom_famille) allin ORDER BY allin.tricol NULLS FIRST;


ALTER TABLE pdi.vue_familles_promethee OWNER TO postgres;

SET search_path = remocra, pg_catalog;

--
-- TOC entry 187 (class 1259 OID 549157)
-- Dependencies: 3774 3775 28
-- Name: organisme; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE organisme (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    email_contact character varying,
    nom character varying,
    version integer DEFAULT 1,
    profil_organisme bigint NOT NULL,
    type_organisme bigint NOT NULL,
    zone_competence bigint NOT NULL
);


ALTER TABLE remocra.organisme OWNER TO postgres;

--
-- TOC entry 188 (class 1259 OID 549165)
-- Dependencies: 28
-- Name: type_organisme_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_organisme_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_organisme_id_seq OWNER TO postgres;

--
-- TOC entry 189 (class 1259 OID 549167)
-- Dependencies: 3777 3778 28
-- Name: type_organisme; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_organisme (
    id bigint DEFAULT nextval('type_organisme_id_seq'::regclass) NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    actif boolean DEFAULT true NOT NULL
);


ALTER TABLE remocra.type_organisme OWNER TO postgres;

SET search_path = pdi, pg_catalog;

--
-- TOC entry 190 (class 1259 OID 549175)
-- Dependencies: 3550 26
-- Name: vue_organisme_cis; Type: VIEW; Schema: pdi; Owner: postgres
--

CREATE VIEW vue_organisme_cis AS
    SELECT org.id, org.libelle, org.tricol FROM (SELECT (-1) AS id, 'Toutes' AS libelle, NULL::unknown AS tricol UNION SELECT o.id, o.nom AS libelle, o.nom AS tricol FROM (remocra.organisme o JOIN remocra.type_organisme tyo ON ((o.type_organisme = tyo.id))) WHERE ((o.actif = true) AND ((tyo.code)::text = 'CIS'::text))) org ORDER BY org.tricol NULLS FIRST;


ALTER TABLE pdi.vue_organisme_cis OWNER TO postgres;

SET search_path = remocra, pg_catalog;

--
-- TOC entry 191 (class 1259 OID 549180)
-- Dependencies: 3779 3780 28
-- Name: type_permis_avis; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_permis_avis (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying,
    nom character varying NOT NULL,
    pprif boolean DEFAULT false NOT NULL
);


ALTER TABLE remocra.type_permis_avis OWNER TO postgres;

SET search_path = pdi, pg_catalog;

--
-- TOC entry 192 (class 1259 OID 549188)
-- Dependencies: 3551 26
-- Name: vue_permis_etats; Type: VIEW; Schema: pdi; Owner: postgres
--

CREATE VIEW vue_permis_etats AS
    SELECT allin.id, allin.libelle FROM (SELECT (-1) AS id, 'Tous' AS libelle, NULL::unknown AS tricol UNION SELECT type_permis_avis.id, type_permis_avis.nom AS libelle, type_permis_avis.nom AS tricol FROM remocra.type_permis_avis) allin ORDER BY allin.tricol NULLS FIRST;


ALTER TABLE pdi.vue_permis_etats OWNER TO postgres;

--
-- TOC entry 193 (class 1259 OID 549192)
-- Dependencies: 3552 26
-- Name: vue_telechargements; Type: VIEW; Schema: pdi; Owner: postgres
--

CREATE VIEW vue_telechargements AS
    SELECT traitement.idtraitement, (md5(((((traitement.idtraitement || '_'::text) || traitement.idutilisateur) || '_'::text) || (traitement.urlressource)::text)))::character varying AS code, traitement.urlressource AS ressource FROM traitement WHERE ((traitement.idstatut = 2) AND (traitement.urlressource IS NOT NULL));


ALTER TABLE pdi.vue_telechargements OWNER TO postgres;

SET search_path = remocra, pg_catalog;

--
-- TOC entry 194 (class 1259 OID 549196)
-- Dependencies: 3782 3783 3784 28
-- Name: utilisateur; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE utilisateur (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    email character varying NOT NULL,
    identifiant character varying NOT NULL,
    message_remocra boolean DEFAULT false NOT NULL,
    nom character varying,
    password character varying NOT NULL,
    prenom character varying,
    salt character varying,
    telephone character varying,
    version integer DEFAULT 1,
    organisme bigint,
    profil_utilisateur bigint NOT NULL
);


ALTER TABLE remocra.utilisateur OWNER TO postgres;

SET search_path = pdi, pg_catalog;

--
-- TOC entry 195 (class 1259 OID 549205)
-- Dependencies: 3553 26
-- Name: vue_utilisateurs; Type: VIEW; Schema: pdi; Owner: postgres
--

CREATE VIEW vue_utilisateurs AS
    SELECT utilisateur.id, utilisateur.identifiant AS libelle FROM remocra.utilisateur;


ALTER TABLE pdi.vue_utilisateurs OWNER TO postgres;

SET search_path = remocra, pg_catalog;

--
-- TOC entry 199 (class 1259 OID 549226)
-- Dependencies: 3786 3788 3789 3790 1574 28
-- Name: alerte; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE alerte (
    id bigint NOT NULL,
    commentaire character varying NOT NULL,
    date_constat timestamp without time zone NOT NULL,
    date_modification timestamp without time zone NOT NULL,
    etat boolean,
    geometrie public.geometry NOT NULL,
    version integer DEFAULT 1,
    rapporteur bigint NOT NULL,
    CONSTRAINT enforce_dims_geometrie CHECK ((public.st_ndims(geometrie) = 2)),
    CONSTRAINT enforce_geotype_geometrie CHECK ((public.geometrytype(geometrie) = 'POINT'::text)),
    CONSTRAINT enforce_srid_geometrie CHECK ((public.st_srid(geometrie) = 2154))
);


ALTER TABLE remocra.alerte OWNER TO postgres;

--
-- TOC entry 200 (class 1259 OID 549236)
-- Dependencies: 28
-- Name: alerte_document; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE alerte_document (
    id bigint NOT NULL,
    alerte bigint NOT NULL,
    document bigint NOT NULL
);


ALTER TABLE remocra.alerte_document OWNER TO postgres;

--
-- TOC entry 201 (class 1259 OID 549239)
-- Dependencies: 28 200
-- Name: alerte_document_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE alerte_document_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.alerte_document_id_seq OWNER TO postgres;

--
-- TOC entry 4223 (class 0 OID 0)
-- Dependencies: 201
-- Name: alerte_document_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE alerte_document_id_seq OWNED BY alerte_document.id;


--
-- TOC entry 202 (class 1259 OID 549241)
-- Dependencies: 3792 3794 3795 1574 28
-- Name: alerte_elt; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE alerte_elt (
    id bigint NOT NULL,
    commentaire character varying,
    geometrie public.geometry NOT NULL,
    version integer DEFAULT 1,
    alerte bigint NOT NULL,
    sous_type_alerte_elt bigint NOT NULL,
    CONSTRAINT enforce_dims_geometrie CHECK ((public.st_ndims(geometrie) = 2)),
    CONSTRAINT enforce_srid_geometrie CHECK ((public.st_srid(geometrie) = 2154))
);


ALTER TABLE remocra.alerte_elt OWNER TO postgres;

--
-- TOC entry 203 (class 1259 OID 549250)
-- Dependencies: 28
-- Name: alerte_elt_ano; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE alerte_elt_ano (
    id bigint NOT NULL,
    version integer,
    alerte_elt bigint NOT NULL,
    type_alerte_ano bigint NOT NULL
);


ALTER TABLE remocra.alerte_elt_ano OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 549253)
-- Dependencies: 28 203
-- Name: alerte_elt_ano_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE alerte_elt_ano_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.alerte_elt_ano_id_seq OWNER TO postgres;

--
-- TOC entry 4227 (class 0 OID 0)
-- Dependencies: 204
-- Name: alerte_elt_ano_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE alerte_elt_ano_id_seq OWNED BY alerte_elt_ano.id;


--
-- TOC entry 205 (class 1259 OID 549255)
-- Dependencies: 28 202
-- Name: alerte_elt_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE alerte_elt_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.alerte_elt_id_seq OWNER TO postgres;

--
-- TOC entry 4229 (class 0 OID 0)
-- Dependencies: 205
-- Name: alerte_elt_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE alerte_elt_id_seq OWNED BY alerte_elt.id;


--
-- TOC entry 206 (class 1259 OID 549257)
-- Dependencies: 28 199
-- Name: alerte_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE alerte_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.alerte_id_seq OWNER TO postgres;

--
-- TOC entry 4231 (class 0 OID 0)
-- Dependencies: 206
-- Name: alerte_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE alerte_id_seq OWNED BY alerte.id;


--
-- TOC entry 207 (class 1259 OID 549259)
-- Dependencies: 28
-- Name: bloc_document; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE bloc_document (
    id bigint NOT NULL,
    description character varying,
    titre character varying,
    document bigint NOT NULL
);


ALTER TABLE remocra.bloc_document OWNER TO postgres;

--
-- TOC entry 208 (class 1259 OID 549265)
-- Dependencies: 28 207
-- Name: bloc_document_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE bloc_document_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.bloc_document_id_seq OWNER TO postgres;

--
-- TOC entry 4234 (class 0 OID 0)
-- Dependencies: 208
-- Name: bloc_document_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE bloc_document_id_seq OWNED BY bloc_document.id;


--
-- TOC entry 209 (class 1259 OID 549267)
-- Dependencies: 28
-- Name: bloc_document_profil_droits; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE bloc_document_profil_droits (
    bloc_document bigint NOT NULL,
    profil_droits bigint NOT NULL
);


ALTER TABLE remocra.bloc_document_profil_droits OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 549270)
-- Dependencies: 28
-- Name: bloc_document_thematiques; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE bloc_document_thematiques (
    bloc_document bigint NOT NULL,
    thematiques bigint NOT NULL
);


ALTER TABLE remocra.bloc_document_thematiques OWNER TO postgres;

--
-- TOC entry 211 (class 1259 OID 549273)
-- Dependencies: 28 182
-- Name: commune_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE commune_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.commune_id_seq OWNER TO postgres;

--
-- TOC entry 4238 (class 0 OID 0)
-- Dependencies: 211
-- Name: commune_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE commune_id_seq OWNED BY commune.id;


--
-- TOC entry 212 (class 1259 OID 549275)
-- Dependencies: 3798 28
-- Name: dde_mdp; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE dde_mdp (
    id bigint NOT NULL,
    code character varying NOT NULL,
    date_demande timestamp without time zone DEFAULT now() NOT NULL,
    utilisateur bigint NOT NULL
);


ALTER TABLE remocra.dde_mdp OWNER TO postgres;

--
-- TOC entry 213 (class 1259 OID 549282)
-- Dependencies: 28 212
-- Name: dde_mdp_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE dde_mdp_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.dde_mdp_id_seq OWNER TO postgres;

--
-- TOC entry 4241 (class 0 OID 0)
-- Dependencies: 213
-- Name: dde_mdp_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE dde_mdp_id_seq OWNED BY dde_mdp.id;


--
-- TOC entry 214 (class 1259 OID 549284)
-- Dependencies: 28
-- Name: depot_document; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE depot_document (
    id bigint NOT NULL,
    document bigint NOT NULL,
    utilisateur bigint NOT NULL
);


ALTER TABLE remocra.depot_document OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 549287)
-- Dependencies: 28 214
-- Name: depot_document_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE depot_document_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.depot_document_id_seq OWNER TO postgres;

--
-- TOC entry 4244 (class 0 OID 0)
-- Dependencies: 215
-- Name: depot_document_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE depot_document_id_seq OWNED BY depot_document.id;


--
-- TOC entry 216 (class 1259 OID 549289)
-- Dependencies: 3801 3802 28
-- Name: document; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE document (
    id bigint NOT NULL,
    code character varying NOT NULL,
    date timestamp without time zone DEFAULT now() NOT NULL,
    fichier character varying NOT NULL,
    repertoire character varying NOT NULL,
    type character varying NOT NULL,
    date_doc timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE remocra.document OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 549297)
-- Dependencies: 28 216
-- Name: document_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE document_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.document_id_seq OWNER TO postgres;

--
-- TOC entry 4247 (class 0 OID 0)
-- Dependencies: 217
-- Name: document_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE document_id_seq OWNED BY document.id;


--
-- TOC entry 218 (class 1259 OID 549299)
-- Dependencies: 28
-- Name: droit_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE droit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.droit_id_seq OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 549301)
-- Dependencies: 3804 3805 3806 3807 3808 3809 28
-- Name: droit; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE droit (
    id bigint DEFAULT nextval('droit_id_seq'::regclass) NOT NULL,
    droit_create boolean DEFAULT false NOT NULL,
    droit_delete boolean DEFAULT false NOT NULL,
    droit_read boolean DEFAULT false NOT NULL,
    droit_update boolean DEFAULT false NOT NULL,
    version integer DEFAULT 1,
    profil_droit bigint NOT NULL,
    type_droit bigint NOT NULL
);


ALTER TABLE remocra.droit OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 549310)
-- Dependencies: 28
-- Name: email; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE email (
    id bigint NOT NULL,
    corps character varying,
    destinataire character varying,
    destinataire_email character varying,
    expediteur character varying,
    expediteur_email character varying,
    notification timestamp without time zone,
    objet character varying
);


ALTER TABLE remocra.email OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 549316)
-- Dependencies: 220 28
-- Name: email_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE email_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.email_id_seq OWNER TO postgres;

--
-- TOC entry 4252 (class 0 OID 0)
-- Dependencies: 221
-- Name: email_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE email_id_seq OWNED BY email.id;


--
-- TOC entry 222 (class 1259 OID 549318)
-- Dependencies: 3811 28
-- Name: email_modele; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE email_modele (
    id bigint NOT NULL,
    code character varying,
    corps character varying,
    objet character varying,
    version integer DEFAULT 1
);


ALTER TABLE remocra.email_modele OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 549325)
-- Dependencies: 222 28
-- Name: email_modele_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE email_modele_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.email_modele_id_seq OWNER TO postgres;

--
-- TOC entry 4255 (class 0 OID 0)
-- Dependencies: 223
-- Name: email_modele_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE email_modele_id_seq OWNED BY email_modele.id;


--
-- TOC entry 224 (class 1259 OID 549327)
-- Dependencies: 28
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.hibernate_sequence OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 549329)
-- Dependencies: 3813 3815 3816 3817 28 1574
-- Name: hydrant; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE hydrant (
    id bigint NOT NULL,
    agent1 character varying,
    agent2 character varying,
    annee_fabrication integer,
    code character varying,
    complement character varying,
    date_contr timestamp without time zone,
    date_recep timestamp without time zone,
    date_reco timestamp without time zone,
    date_verif timestamp without time zone,
    dispo_admin character varying,
    dispo_hbe character varying,
    dispo_terrestre character varying,
    geometrie public.geometry NOT NULL,
    lieu_dit character varying,
    numero character varying,
    numero_interne integer,
    observation character varying,
    version integer DEFAULT 1,
    voie character varying,
    voie2 character varying,
    commune bigint,
    domaine bigint,
    nature bigint,
    tournee bigint,
    date_modification timestamp without time zone,
    courrier character varying,
    gest_point_eau character varying,
    organisme bigint,
    date_gps timestamp without time zone,
    zone_speciale bigint,
    CONSTRAINT enforce_dims_geometrie CHECK ((public.st_ndims(geometrie) = 2)),
    CONSTRAINT enforce_geotype_geometrie CHECK ((public.geometrytype(geometrie) = 'POINT'::text)),
    CONSTRAINT enforce_srid_geometrie CHECK ((public.st_srid(geometrie) = 2154))
);


ALTER TABLE remocra.hydrant OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 549339)
-- Dependencies: 28
-- Name: hydrant_anomalies; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE hydrant_anomalies (
    hydrant bigint NOT NULL,
    anomalies bigint NOT NULL
);


ALTER TABLE remocra.hydrant_anomalies OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 549342)
-- Dependencies: 28
-- Name: hydrant_document; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE hydrant_document (
    id bigint NOT NULL,
    document bigint NOT NULL,
    hydrant bigint NOT NULL
);


ALTER TABLE remocra.hydrant_document OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 549345)
-- Dependencies: 28 227
-- Name: hydrant_document_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE hydrant_document_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.hydrant_document_id_seq OWNER TO postgres;

--
-- TOC entry 4261 (class 0 OID 0)
-- Dependencies: 228
-- Name: hydrant_document_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE hydrant_document_id_seq OWNED BY hydrant_document.id;


--
-- TOC entry 229 (class 1259 OID 549347)
-- Dependencies: 28 225
-- Name: hydrant_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE hydrant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.hydrant_id_seq OWNER TO postgres;

--
-- TOC entry 4263 (class 0 OID 0)
-- Dependencies: 229
-- Name: hydrant_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE hydrant_id_seq OWNED BY hydrant.id;


--
-- TOC entry 230 (class 1259 OID 549349)
-- Dependencies: 3819 28
-- Name: hydrant_pena; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE hydrant_pena (
    capacite character varying,
    coorddfci character varying,
    hbe boolean DEFAULT false,
    piste character varying,
    id bigint NOT NULL,
    materiau bigint,
    positionnement bigint,
    vol_constate bigint,
    q_appoint double precision
);


ALTER TABLE remocra.hydrant_pena OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 549356)
-- Dependencies: 28
-- Name: hydrant_pibi; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE hydrant_pibi (
    debit integer,
    debit_max integer,
    pression double precision,
    pression_dyn double precision,
    id bigint NOT NULL,
    diametre bigint,
    gest_reseau character varying,
    numeroscp character varying,
    choc boolean,
    marque bigint,
    modele bigint,
    pena bigint
);


ALTER TABLE remocra.hydrant_pibi OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 549362)
-- Dependencies: 28 1574
-- Name: hydrant_prescrit; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE hydrant_prescrit (
    id bigint NOT NULL,
    date_prescrit timestamp without time zone,
    debit integer,
    geometrie public.geometry NOT NULL,
    nb_poteaux integer,
    organisme bigint
);


ALTER TABLE remocra.hydrant_prescrit OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 549368)
-- Dependencies: 28 232
-- Name: hydrant_prescrit_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE hydrant_prescrit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.hydrant_prescrit_id_seq OWNER TO postgres;

--
-- TOC entry 4268 (class 0 OID 0)
-- Dependencies: 233
-- Name: hydrant_prescrit_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE hydrant_prescrit_id_seq OWNED BY hydrant_prescrit.id;


--
-- TOC entry 234 (class 1259 OID 549370)
-- Dependencies: 28
-- Name: metadonnee; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE metadonnee (
    id bigint NOT NULL,
    resume character varying,
    titre character varying NOT NULL,
    version integer,
    thematique bigint NOT NULL,
    url_fiche character varying,
    url_vignette character varying
);


ALTER TABLE remocra.metadonnee OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 549376)
-- Dependencies: 28 234
-- Name: metadonnee_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE metadonnee_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.metadonnee_id_seq OWNER TO postgres;

--
-- TOC entry 4271 (class 0 OID 0)
-- Dependencies: 235
-- Name: metadonnee_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE metadonnee_id_seq OWNED BY metadonnee.id;


--
-- TOC entry 236 (class 1259 OID 549378)
-- Dependencies: 28 187
-- Name: organisme_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE organisme_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.organisme_id_seq OWNER TO postgres;

--
-- TOC entry 4273 (class 0 OID 0)
-- Dependencies: 236
-- Name: organisme_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE organisme_id_seq OWNED BY organisme.id;


--
-- TOC entry 237 (class 1259 OID 549380)
-- Dependencies: 3822 3824 3825 3826 1574 28
-- Name: permis; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE permis (
    id bigint NOT NULL,
    complement character varying NOT NULL,
    annee integer NOT NULL,
    date_modification timestamp without time zone NOT NULL,
    geometrie public.geometry NOT NULL,
    nom character varying NOT NULL,
    numero character varying NOT NULL,
    observations character varying NOT NULL,
    parcelle_cadastrale character varying NOT NULL,
    section_cadastrale character varying NOT NULL,
    version integer DEFAULT 1,
    avis bigint NOT NULL,
    commune bigint NOT NULL,
    instructeur bigint NOT NULL,
    interservice bigint NOT NULL,
    service_instructeur bigint NOT NULL,
    voie character varying NOT NULL,
    date_permis timestamp without time zone NOT NULL,
    CONSTRAINT enforce_dims_geometrie CHECK ((public.st_ndims(geometrie) = 2)),
    CONSTRAINT enforce_geotype_geometrie CHECK ((public.geometrytype(geometrie) = 'POINT'::text)),
    CONSTRAINT enforce_srid_geometrie CHECK ((public.st_srid(geometrie) = 2154))
);


ALTER TABLE remocra.permis OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 549390)
-- Dependencies: 28
-- Name: permis_document; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE permis_document (
    id bigint NOT NULL,
    document bigint NOT NULL,
    permis bigint NOT NULL
);


ALTER TABLE remocra.permis_document OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 549393)
-- Dependencies: 28 238
-- Name: permis_document_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE permis_document_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.permis_document_id_seq OWNER TO postgres;

--
-- TOC entry 4277 (class 0 OID 0)
-- Dependencies: 239
-- Name: permis_document_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE permis_document_id_seq OWNED BY permis_document.id;


--
-- TOC entry 240 (class 1259 OID 549395)
-- Dependencies: 28 237
-- Name: permis_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE permis_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.permis_id_seq OWNER TO postgres;

--
-- TOC entry 4279 (class 0 OID 0)
-- Dependencies: 240
-- Name: permis_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE permis_id_seq OWNED BY permis.id;


--
-- TOC entry 241 (class 1259 OID 549397)
-- Dependencies: 28
-- Name: profil_droit_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE profil_droit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.profil_droit_id_seq OWNER TO postgres;

--
-- TOC entry 242 (class 1259 OID 549399)
-- Dependencies: 3828 3829 28
-- Name: profil_droit; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE profil_droit (
    id bigint DEFAULT nextval('profil_droit_id_seq'::regclass) NOT NULL,
    nom character varying,
    feuille_de_style_geo_server character varying,
    version integer DEFAULT 1,
    code character varying NOT NULL
);


ALTER TABLE remocra.profil_droit OWNER TO postgres;

--
-- TOC entry 243 (class 1259 OID 549407)
-- Dependencies: 28
-- Name: profil_organisme_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE profil_organisme_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.profil_organisme_id_seq OWNER TO postgres;

--
-- TOC entry 244 (class 1259 OID 549409)
-- Dependencies: 3830 3831 28
-- Name: profil_organisme; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE profil_organisme (
    id bigint DEFAULT nextval('profil_organisme_id_seq'::regclass) NOT NULL,
    nom character varying NOT NULL,
    type_organisme bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL
);


ALTER TABLE remocra.profil_organisme OWNER TO postgres;

--
-- TOC entry 245 (class 1259 OID 549417)
-- Dependencies: 28
-- Name: profil_organisme_utilisateur_droit_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE profil_organisme_utilisateur_droit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.profil_organisme_utilisateur_droit_id_seq OWNER TO postgres;

--
-- TOC entry 246 (class 1259 OID 549419)
-- Dependencies: 3832 3833 28
-- Name: profil_organisme_utilisateur_droit; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE profil_organisme_utilisateur_droit (
    id bigint DEFAULT nextval('profil_organisme_utilisateur_droit_id_seq'::regclass) NOT NULL,
    version integer DEFAULT 1,
    profil_droit bigint NOT NULL,
    profil_organisme bigint NOT NULL,
    profil_utilisateur bigint NOT NULL
);


ALTER TABLE remocra.profil_organisme_utilisateur_droit OWNER TO postgres;

--
-- TOC entry 247 (class 1259 OID 549424)
-- Dependencies: 28
-- Name: profil_utilisateur_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE profil_utilisateur_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.profil_utilisateur_id_seq OWNER TO postgres;

--
-- TOC entry 248 (class 1259 OID 549426)
-- Dependencies: 3834 3835 28
-- Name: profil_utilisateur; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE profil_utilisateur (
    id bigint DEFAULT nextval('profil_utilisateur_id_seq'::regclass) NOT NULL,
    nom character varying,
    type_organisme bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL
);


ALTER TABLE remocra.profil_utilisateur OWNER TO postgres;

--
-- TOC entry 249 (class 1259 OID 549434)
-- Dependencies: 28 1574
-- Name: rci; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE rci (
    id bigint NOT NULL,
    commentaire_conclusions character varying,
    complement character varying,
    coorddfci character varying,
    date_incendie timestamp without time zone NOT NULL,
    date_modification timestamp without time zone NOT NULL,
    direction_vent integer,
    force_vent integer,
    forces_ordre character varying,
    gel_lieux boolean,
    geometrie public.geometry NOT NULL,
    hygrometrie integer,
    indice_rothermel integer,
    point_eclosion character varying,
    premier_cos character varying,
    premier_engin character varying,
    superficie_finale double precision,
    superficie_referent double precision,
    superficie_secours double precision,
    temperature double precision,
    vent_local boolean,
    version integer,
    voie character varying,
    categorie_promethee bigint,
    commune bigint,
    degre_certitude bigint,
    origine_alerte bigint NOT NULL,
    utilisateur bigint NOT NULL,
    gdh timestamp without time zone,
    arrivee_ddtm_onf character varying,
    arrivee_sdis character varying,
    arrivee_gendarmerie character varying,
    arrivee_police character varying
);


ALTER TABLE remocra.rci OWNER TO postgres;

--
-- TOC entry 250 (class 1259 OID 549440)
-- Dependencies: 28
-- Name: rci_document; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE rci_document (
    id bigint NOT NULL,
    document bigint NOT NULL,
    rci bigint NOT NULL
);


ALTER TABLE remocra.rci_document OWNER TO postgres;

--
-- TOC entry 251 (class 1259 OID 549443)
-- Dependencies: 250 28
-- Name: rci_document_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE rci_document_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.rci_document_id_seq OWNER TO postgres;

--
-- TOC entry 4291 (class 0 OID 0)
-- Dependencies: 251
-- Name: rci_document_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE rci_document_id_seq OWNED BY rci_document.id;


--
-- TOC entry 252 (class 1259 OID 549445)
-- Dependencies: 249 28
-- Name: rci_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE rci_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.rci_id_seq OWNER TO postgres;

--
-- TOC entry 4293 (class 0 OID 0)
-- Dependencies: 252
-- Name: rci_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE rci_id_seq OWNED BY rci.id;


--
-- TOC entry 253 (class 1259 OID 549447)
-- Dependencies: 3838 28
-- Name: sous_type_alerte_elt; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE sous_type_alerte_elt (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    type_geom integer NOT NULL,
    type_alerte_elt bigint NOT NULL
);


ALTER TABLE remocra.sous_type_alerte_elt OWNER TO postgres;

--
-- TOC entry 254 (class 1259 OID 549454)
-- Dependencies: 253 28
-- Name: sous_type_alerte_elt_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE sous_type_alerte_elt_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.sous_type_alerte_elt_id_seq OWNER TO postgres;

--
-- TOC entry 4296 (class 0 OID 0)
-- Dependencies: 254
-- Name: sous_type_alerte_elt_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE sous_type_alerte_elt_id_seq OWNED BY sous_type_alerte_elt.id;


--
-- TOC entry 255 (class 1259 OID 549456)
-- Dependencies: 3840 28
-- Name: suivi_patches; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE suivi_patches (
    numero bigint NOT NULL,
    description character varying NOT NULL,
    application timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE remocra.suivi_patches OWNER TO postgres;

--
-- TOC entry 256 (class 1259 OID 549463)
-- Dependencies: 28
-- Name: synchronisation; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE synchronisation (
    id bigint NOT NULL,
    date_synchro timestamp without time zone NOT NULL,
    succes boolean NOT NULL,
    version integer,
    thematique bigint NOT NULL
);


ALTER TABLE remocra.synchronisation OWNER TO postgres;

--
-- TOC entry 257 (class 1259 OID 549466)
-- Dependencies: 28 256
-- Name: synchronisation_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE synchronisation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.synchronisation_id_seq OWNER TO postgres;

--
-- TOC entry 4300 (class 0 OID 0)
-- Dependencies: 257
-- Name: synchronisation_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE synchronisation_id_seq OWNED BY synchronisation.id;


--
-- TOC entry 258 (class 1259 OID 549468)
-- Dependencies: 3842 28
-- Name: thematique; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE thematique (
    id bigint NOT NULL,
    nom character varying,
    version integer,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL
);


ALTER TABLE remocra.thematique OWNER TO postgres;

--
-- TOC entry 259 (class 1259 OID 549475)
-- Dependencies: 258 28
-- Name: thematique_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE thematique_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.thematique_id_seq OWNER TO postgres;

--
-- TOC entry 4303 (class 0 OID 0)
-- Dependencies: 259
-- Name: thematique_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE thematique_id_seq OWNED BY thematique.id;


--
-- TOC entry 260 (class 1259 OID 549477)
-- Dependencies: 3844 28
-- Name: tournee; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE tournee (
    id bigint NOT NULL,
    deb_sync timestamp without time zone,
    last_sync timestamp without time zone,
    version integer DEFAULT 1,
    affectation bigint,
    reservation bigint,
    etat integer,
    hydrant_count integer
);


ALTER TABLE remocra.tournee OWNER TO postgres;

--
-- TOC entry 261 (class 1259 OID 549481)
-- Dependencies: 260 28
-- Name: tournee_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE tournee_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.tournee_id_seq OWNER TO postgres;

--
-- TOC entry 4306 (class 0 OID 0)
-- Dependencies: 261
-- Name: tournee_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE tournee_id_seq OWNED BY tournee.id;


--
-- TOC entry 262 (class 1259 OID 549483)
-- Dependencies: 3846 28
-- Name: type_alerte_ano; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_alerte_ano (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL
);


ALTER TABLE remocra.type_alerte_ano OWNER TO postgres;

--
-- TOC entry 263 (class 1259 OID 549490)
-- Dependencies: 262 28
-- Name: type_alerte_ano_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_alerte_ano_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_alerte_ano_id_seq OWNER TO postgres;

--
-- TOC entry 4309 (class 0 OID 0)
-- Dependencies: 263
-- Name: type_alerte_ano_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_alerte_ano_id_seq OWNED BY type_alerte_ano.id;


--
-- TOC entry 264 (class 1259 OID 549492)
-- Dependencies: 3848 28
-- Name: type_alerte_elt; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_alerte_elt (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL
);


ALTER TABLE remocra.type_alerte_elt OWNER TO postgres;

--
-- TOC entry 265 (class 1259 OID 549499)
-- Dependencies: 28 264
-- Name: type_alerte_elt_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_alerte_elt_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_alerte_elt_id_seq OWNER TO postgres;

--
-- TOC entry 4312 (class 0 OID 0)
-- Dependencies: 265
-- Name: type_alerte_elt_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_alerte_elt_id_seq OWNED BY type_alerte_elt.id;


--
-- TOC entry 266 (class 1259 OID 549501)
-- Dependencies: 28
-- Name: type_droit_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_droit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_droit_id_seq OWNER TO postgres;

--
-- TOC entry 267 (class 1259 OID 549503)
-- Dependencies: 3850 28
-- Name: type_droit; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_droit (
    id bigint DEFAULT nextval('type_droit_id_seq'::regclass) NOT NULL,
    code character varying,
    description character varying,
    nom character varying,
    version integer
);


ALTER TABLE remocra.type_droit OWNER TO postgres;

--
-- TOC entry 268 (class 1259 OID 549510)
-- Dependencies: 3851 3852 28
-- Name: type_hydrant; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_hydrant (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


ALTER TABLE remocra.type_hydrant OWNER TO postgres;

--
-- TOC entry 269 (class 1259 OID 549518)
-- Dependencies: 3854 3855 28
-- Name: type_hydrant_anomalie; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_hydrant_anomalie (
    id bigint NOT NULL,
    actif boolean DEFAULT true,
    code character varying NOT NULL,
    commentaire character varying,
    nom character varying NOT NULL,
    version integer DEFAULT 1,
    critere bigint
);


ALTER TABLE remocra.type_hydrant_anomalie OWNER TO postgres;

--
-- TOC entry 270 (class 1259 OID 549526)
-- Dependencies: 269 28
-- Name: type_hydrant_anomalie_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_hydrant_anomalie_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_hydrant_anomalie_id_seq OWNER TO postgres;

--
-- TOC entry 4318 (class 0 OID 0)
-- Dependencies: 270
-- Name: type_hydrant_anomalie_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_hydrant_anomalie_id_seq OWNED BY type_hydrant_anomalie.id;


--
-- TOC entry 271 (class 1259 OID 549528)
-- Dependencies: 28
-- Name: type_hydrant_anomalie_nature; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_hydrant_anomalie_nature (
    id bigint NOT NULL,
    val_indispo_admin integer,
    val_indispo_hbe integer,
    val_indispo_terrestre integer,
    version integer,
    anomalie bigint NOT NULL,
    nature bigint NOT NULL
);


ALTER TABLE remocra.type_hydrant_anomalie_nature OWNER TO postgres;

--
-- TOC entry 272 (class 1259 OID 549531)
-- Dependencies: 271 28
-- Name: type_hydrant_anomalie_nature_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_hydrant_anomalie_nature_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_hydrant_anomalie_nature_id_seq OWNER TO postgres;

--
-- TOC entry 4321 (class 0 OID 0)
-- Dependencies: 272
-- Name: type_hydrant_anomalie_nature_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_hydrant_anomalie_nature_id_seq OWNED BY type_hydrant_anomalie_nature.id;


--
-- TOC entry 273 (class 1259 OID 549533)
-- Dependencies: 28
-- Name: type_hydrant_anomalie_nature_saisies; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_hydrant_anomalie_nature_saisies (
    type_hydrant_anomalie_nature bigint NOT NULL,
    saisies bigint NOT NULL
);


ALTER TABLE remocra.type_hydrant_anomalie_nature_saisies OWNER TO postgres;

--
-- TOC entry 274 (class 1259 OID 549536)
-- Dependencies: 3858 3859 28
-- Name: type_hydrant_critere; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_hydrant_critere (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


ALTER TABLE remocra.type_hydrant_critere OWNER TO postgres;

--
-- TOC entry 275 (class 1259 OID 549544)
-- Dependencies: 274 28
-- Name: type_hydrant_critere_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_hydrant_critere_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_hydrant_critere_id_seq OWNER TO postgres;

--
-- TOC entry 4325 (class 0 OID 0)
-- Dependencies: 275
-- Name: type_hydrant_critere_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_hydrant_critere_id_seq OWNED BY type_hydrant_critere.id;


--
-- TOC entry 276 (class 1259 OID 549546)
-- Dependencies: 3861 3862 28
-- Name: type_hydrant_diametre; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_hydrant_diametre (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


ALTER TABLE remocra.type_hydrant_diametre OWNER TO postgres;

--
-- TOC entry 277 (class 1259 OID 549554)
-- Dependencies: 28 276
-- Name: type_hydrant_diametre_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_hydrant_diametre_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_hydrant_diametre_id_seq OWNER TO postgres;

--
-- TOC entry 4328 (class 0 OID 0)
-- Dependencies: 277
-- Name: type_hydrant_diametre_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_hydrant_diametre_id_seq OWNED BY type_hydrant_diametre.id;


--
-- TOC entry 278 (class 1259 OID 549556)
-- Dependencies: 28
-- Name: type_hydrant_diametre_natures; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_hydrant_diametre_natures (
    type_hydrant_diametre bigint NOT NULL,
    natures bigint NOT NULL
);


ALTER TABLE remocra.type_hydrant_diametre_natures OWNER TO postgres;

--
-- TOC entry 279 (class 1259 OID 549559)
-- Dependencies: 3864 3865 28
-- Name: type_hydrant_domaine; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_hydrant_domaine (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


ALTER TABLE remocra.type_hydrant_domaine OWNER TO postgres;

--
-- TOC entry 280 (class 1259 OID 549567)
-- Dependencies: 279 28
-- Name: type_hydrant_domaine_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_hydrant_domaine_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_hydrant_domaine_id_seq OWNER TO postgres;

--
-- TOC entry 4332 (class 0 OID 0)
-- Dependencies: 280
-- Name: type_hydrant_domaine_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_hydrant_domaine_id_seq OWNED BY type_hydrant_domaine.id;


--
-- TOC entry 281 (class 1259 OID 549569)
-- Dependencies: 28 268
-- Name: type_hydrant_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_hydrant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_hydrant_id_seq OWNER TO postgres;

--
-- TOC entry 4334 (class 0 OID 0)
-- Dependencies: 281
-- Name: type_hydrant_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_hydrant_id_seq OWNED BY type_hydrant.id;


--
-- TOC entry 282 (class 1259 OID 549571)
-- Dependencies: 3867 3868 28
-- Name: type_hydrant_marque; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_hydrant_marque (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


ALTER TABLE remocra.type_hydrant_marque OWNER TO postgres;

--
-- TOC entry 283 (class 1259 OID 549579)
-- Dependencies: 282 28
-- Name: type_hydrant_marque_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_hydrant_marque_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_hydrant_marque_id_seq OWNER TO postgres;

--
-- TOC entry 4337 (class 0 OID 0)
-- Dependencies: 283
-- Name: type_hydrant_marque_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_hydrant_marque_id_seq OWNED BY type_hydrant_marque.id;


--
-- TOC entry 284 (class 1259 OID 549581)
-- Dependencies: 3870 3871 28
-- Name: type_hydrant_materiau; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_hydrant_materiau (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


ALTER TABLE remocra.type_hydrant_materiau OWNER TO postgres;

--
-- TOC entry 285 (class 1259 OID 549589)
-- Dependencies: 284 28
-- Name: type_hydrant_materiau_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_hydrant_materiau_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_hydrant_materiau_id_seq OWNER TO postgres;

--
-- TOC entry 4340 (class 0 OID 0)
-- Dependencies: 285
-- Name: type_hydrant_materiau_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_hydrant_materiau_id_seq OWNED BY type_hydrant_materiau.id;


--
-- TOC entry 286 (class 1259 OID 549591)
-- Dependencies: 3873 3874 28
-- Name: type_hydrant_modele; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_hydrant_modele (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1,
    marque bigint
);


ALTER TABLE remocra.type_hydrant_modele OWNER TO postgres;

--
-- TOC entry 287 (class 1259 OID 549599)
-- Dependencies: 286 28
-- Name: type_hydrant_modele_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_hydrant_modele_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_hydrant_modele_id_seq OWNER TO postgres;

--
-- TOC entry 4343 (class 0 OID 0)
-- Dependencies: 287
-- Name: type_hydrant_modele_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_hydrant_modele_id_seq OWNED BY type_hydrant_modele.id;


--
-- TOC entry 288 (class 1259 OID 549601)
-- Dependencies: 3876 28
-- Name: type_hydrant_nature; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_hydrant_nature (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer,
    type_hydrant bigint
);


ALTER TABLE remocra.type_hydrant_nature OWNER TO postgres;

--
-- TOC entry 289 (class 1259 OID 549608)
-- Dependencies: 28 288
-- Name: type_hydrant_nature_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_hydrant_nature_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_hydrant_nature_id_seq OWNER TO postgres;

--
-- TOC entry 4346 (class 0 OID 0)
-- Dependencies: 289
-- Name: type_hydrant_nature_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_hydrant_nature_id_seq OWNED BY type_hydrant_nature.id;


--
-- TOC entry 290 (class 1259 OID 549610)
-- Dependencies: 3878 3879 28
-- Name: type_hydrant_positionnement; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_hydrant_positionnement (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


ALTER TABLE remocra.type_hydrant_positionnement OWNER TO postgres;

--
-- TOC entry 291 (class 1259 OID 549618)
-- Dependencies: 28 290
-- Name: type_hydrant_positionnement_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_hydrant_positionnement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_hydrant_positionnement_id_seq OWNER TO postgres;

--
-- TOC entry 4349 (class 0 OID 0)
-- Dependencies: 291
-- Name: type_hydrant_positionnement_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_hydrant_positionnement_id_seq OWNED BY type_hydrant_positionnement.id;


--
-- TOC entry 292 (class 1259 OID 549620)
-- Dependencies: 3881 28
-- Name: type_hydrant_saisie; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_hydrant_saisie (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer
);


ALTER TABLE remocra.type_hydrant_saisie OWNER TO postgres;

--
-- TOC entry 293 (class 1259 OID 549627)
-- Dependencies: 292 28
-- Name: type_hydrant_saisie_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_hydrant_saisie_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_hydrant_saisie_id_seq OWNER TO postgres;

--
-- TOC entry 4352 (class 0 OID 0)
-- Dependencies: 293
-- Name: type_hydrant_saisie_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_hydrant_saisie_id_seq OWNED BY type_hydrant_saisie.id;


--
-- TOC entry 294 (class 1259 OID 549629)
-- Dependencies: 3883 3884 28
-- Name: type_hydrant_vol_constate; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_hydrant_vol_constate (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


ALTER TABLE remocra.type_hydrant_vol_constate OWNER TO postgres;

--
-- TOC entry 295 (class 1259 OID 549637)
-- Dependencies: 28 294
-- Name: type_hydrant_vol_constate_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_hydrant_vol_constate_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_hydrant_vol_constate_id_seq OWNER TO postgres;

--
-- TOC entry 4355 (class 0 OID 0)
-- Dependencies: 295
-- Name: type_hydrant_vol_constate_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_hydrant_vol_constate_id_seq OWNED BY type_hydrant_vol_constate.id;


--
-- TOC entry 296 (class 1259 OID 549639)
-- Dependencies: 28 191
-- Name: type_permis_avis_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_permis_avis_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_permis_avis_id_seq OWNER TO postgres;

--
-- TOC entry 4357 (class 0 OID 0)
-- Dependencies: 296
-- Name: type_permis_avis_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_permis_avis_id_seq OWNED BY type_permis_avis.id;


--
-- TOC entry 297 (class 1259 OID 549641)
-- Dependencies: 3886 3887 28
-- Name: type_permis_interservice; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_permis_interservice (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying,
    nom character varying NOT NULL,
    pprif boolean DEFAULT false NOT NULL
);


ALTER TABLE remocra.type_permis_interservice OWNER TO postgres;

--
-- TOC entry 298 (class 1259 OID 549649)
-- Dependencies: 28 297
-- Name: type_permis_interservice_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_permis_interservice_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_permis_interservice_id_seq OWNER TO postgres;

--
-- TOC entry 4360 (class 0 OID 0)
-- Dependencies: 298
-- Name: type_permis_interservice_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_permis_interservice_id_seq OWNED BY type_permis_interservice.id;


--
-- TOC entry 299 (class 1259 OID 549651)
-- Dependencies: 3889 3890 28
-- Name: type_rci_degre_certitude; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_rci_degre_certitude (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


ALTER TABLE remocra.type_rci_degre_certitude OWNER TO postgres;

--
-- TOC entry 300 (class 1259 OID 549659)
-- Dependencies: 28 299
-- Name: type_rci_degre_certitude_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_rci_degre_certitude_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_rci_degre_certitude_id_seq OWNER TO postgres;

--
-- TOC entry 4363 (class 0 OID 0)
-- Dependencies: 300
-- Name: type_rci_degre_certitude_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_rci_degre_certitude_id_seq OWNED BY type_rci_degre_certitude.id;


--
-- TOC entry 301 (class 1259 OID 549661)
-- Dependencies: 3892 3893 28
-- Name: type_rci_origine_alerte; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_rci_origine_alerte (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


ALTER TABLE remocra.type_rci_origine_alerte OWNER TO postgres;

--
-- TOC entry 302 (class 1259 OID 549669)
-- Dependencies: 28 301
-- Name: type_rci_origine_alerte_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_rci_origine_alerte_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_rci_origine_alerte_id_seq OWNER TO postgres;

--
-- TOC entry 4366 (class 0 OID 0)
-- Dependencies: 302
-- Name: type_rci_origine_alerte_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_rci_origine_alerte_id_seq OWNED BY type_rci_origine_alerte.id;


--
-- TOC entry 303 (class 1259 OID 549671)
-- Dependencies: 3895 3896 28
-- Name: type_rci_prom_categorie; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_rci_prom_categorie (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1,
    partition bigint
);


ALTER TABLE remocra.type_rci_prom_categorie OWNER TO postgres;

--
-- TOC entry 304 (class 1259 OID 549679)
-- Dependencies: 28 303
-- Name: type_rci_prom_categorie_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_rci_prom_categorie_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_rci_prom_categorie_id_seq OWNER TO postgres;

--
-- TOC entry 4369 (class 0 OID 0)
-- Dependencies: 304
-- Name: type_rci_prom_categorie_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_rci_prom_categorie_id_seq OWNED BY type_rci_prom_categorie.id;


--
-- TOC entry 305 (class 1259 OID 549681)
-- Dependencies: 28 185
-- Name: type_rci_prom_famille_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_rci_prom_famille_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_rci_prom_famille_id_seq OWNER TO postgres;

--
-- TOC entry 4371 (class 0 OID 0)
-- Dependencies: 305
-- Name: type_rci_prom_famille_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_rci_prom_famille_id_seq OWNED BY type_rci_prom_famille.id;


--
-- TOC entry 306 (class 1259 OID 549683)
-- Dependencies: 3898 3899 28
-- Name: type_rci_prom_partition; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE type_rci_prom_partition (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1,
    famille bigint
);


ALTER TABLE remocra.type_rci_prom_partition OWNER TO postgres;

--
-- TOC entry 307 (class 1259 OID 549691)
-- Dependencies: 306 28
-- Name: type_rci_prom_partition_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE type_rci_prom_partition_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.type_rci_prom_partition_id_seq OWNER TO postgres;

--
-- TOC entry 4374 (class 0 OID 0)
-- Dependencies: 307
-- Name: type_rci_prom_partition_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE type_rci_prom_partition_id_seq OWNED BY type_rci_prom_partition.id;


--
-- TOC entry 308 (class 1259 OID 549693)
-- Dependencies: 194 28
-- Name: utilisateur_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE utilisateur_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.utilisateur_id_seq OWNER TO postgres;

--
-- TOC entry 4376 (class 0 OID 0)
-- Dependencies: 308
-- Name: utilisateur_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE utilisateur_id_seq OWNED BY utilisateur.id;


--
-- TOC entry 309 (class 1259 OID 549695)
-- Dependencies: 3901 3903 3904 3905 1574 28
-- Name: voie; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE voie (
    id bigint NOT NULL,
    geometrie public.geometry NOT NULL,
    mot_classant character varying NOT NULL,
    nom character varying NOT NULL,
    source character varying NOT NULL,
    commune bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    CONSTRAINT enforce_dims_geometrie CHECK ((public.st_ndims(geometrie) = 2)),
    CONSTRAINT enforce_geotype_geometrie CHECK ((public.geometrytype(geometrie) = 'MULTILINESTRING'::text)),
    CONSTRAINT enforce_srid_geometrie CHECK ((public.st_srid(geometrie) = 2154))
);


ALTER TABLE remocra.voie OWNER TO postgres;

--
-- TOC entry 310 (class 1259 OID 549705)
-- Dependencies: 309 28
-- Name: voie_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE voie_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.voie_id_seq OWNER TO postgres;

--
-- TOC entry 4379 (class 0 OID 0)
-- Dependencies: 310
-- Name: voie_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE voie_id_seq OWNED BY voie.id;


--
-- TOC entry 311 (class 1259 OID 549707)
-- Dependencies: 3907 3908 3909 1574 28
-- Name: zone_competence; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE zone_competence (
    id bigint NOT NULL,
    code character varying,
    geometrie public.geometry NOT NULL,
    nom character varying,
    CONSTRAINT enforce_dims_geometrie CHECK ((public.st_ndims(geometrie) = 2)),
    CONSTRAINT enforce_geotype_geometrie CHECK (((public.geometrytype(geometrie) = 'POLYGON'::text) OR (public.geometrytype(geometrie) = 'MULTIPOLYGON'::text))),
    CONSTRAINT enforce_srid_geometrie CHECK ((public.st_srid(geometrie) = 2154))
);


ALTER TABLE remocra.zone_competence OWNER TO postgres;

--
-- TOC entry 312 (class 1259 OID 549716)
-- Dependencies: 28 311
-- Name: zone_competence_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE zone_competence_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.zone_competence_id_seq OWNER TO postgres;

--
-- TOC entry 4382 (class 0 OID 0)
-- Dependencies: 312
-- Name: zone_competence_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE zone_competence_id_seq OWNED BY zone_competence.id;


--
-- TOC entry 313 (class 1259 OID 549718)
-- Dependencies: 3911 3912 3913 1574 28
-- Name: zone_speciale; Type: TABLE; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE TABLE zone_speciale (
    id bigint NOT NULL,
    code character varying NOT NULL,
    geometrie public.geometry NOT NULL,
    nom character varying NOT NULL,
    CONSTRAINT enforce_dims_geometrie CHECK ((public.st_ndims(geometrie) = 2)),
    CONSTRAINT enforce_geotype_geometrie CHECK (((public.geometrytype(geometrie) = 'POLYGON'::text) OR (public.geometrytype(geometrie) = 'MULTIPOLYGON'::text))),
    CONSTRAINT enforce_srid_geometrie CHECK ((public.st_srid(geometrie) = 2154))
);


ALTER TABLE remocra.zone_speciale OWNER TO postgres;

--
-- TOC entry 314 (class 1259 OID 549727)
-- Dependencies: 28 313
-- Name: zone_speciale_id_seq; Type: SEQUENCE; Schema: remocra; Owner: postgres
--

CREATE SEQUENCE zone_speciale_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE remocra.zone_speciale_id_seq OWNER TO postgres;

--
-- TOC entry 4385 (class 0 OID 0)
-- Dependencies: 314
-- Name: zone_speciale_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: postgres
--

ALTER SEQUENCE zone_speciale_id_seq OWNED BY zone_speciale.id;


SET search_path = tracabilite, pg_catalog;

--
-- TOC entry 419 (class 1259 OID 550744)
-- Dependencies: 39 1574
-- Name: hydrant; Type: TABLE; Schema: tracabilite; Owner: postgres; Tablespace: 
--

CREATE TABLE hydrant (
    id integer NOT NULL,
    num_transac bigint,
    nom_operation character varying,
    date_operation timestamp with time zone,
    id_hydrant bigint,
    numero character varying,
    geometrie public.geometry,
    insee character varying,
    commune character varying,
    lieu_dit character varying,
    voie character varying,
    carrefour character varying,
    complement character varying,
    agent1 character varying,
    agent2 character varying,
    date_recep timestamp without time zone,
    date_reco timestamp without time zone,
    date_contr timestamp without time zone,
    date_verif timestamp without time zone,
    dispo_terrestre character varying,
    dispo_hbe character varying,
    nature character varying,
    type_hydrant character varying,
    anomalies character varying[],
    observation character varying,
    organisme character varying,
    hbe boolean,
    positionnement character varying,
    materiau character varying,
    vol_constate character varying,
    diametre character varying,
    debit integer,
    debit_max integer,
    pression double precision,
    pression_dyn double precision,
    marque character varying,
    modele character varying
);


ALTER TABLE tracabilite.hydrant OWNER TO postgres;

--
-- TOC entry 420 (class 1259 OID 550750)
-- Dependencies: 39 419
-- Name: hydrant_id_seq; Type: SEQUENCE; Schema: tracabilite; Owner: postgres
--

CREATE SEQUENCE hydrant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tracabilite.hydrant_id_seq OWNER TO postgres;

--
-- TOC entry 4388 (class 0 OID 0)
-- Dependencies: 420
-- Name: hydrant_id_seq; Type: SEQUENCE OWNED BY; Schema: tracabilite; Owner: postgres
--

ALTER SEQUENCE hydrant_id_seq OWNED BY hydrant.id;


SET search_path = remocra, pg_catalog;

--
-- TOC entry 3787 (class 2604 OID 550752)
-- Dependencies: 206 199
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY alerte ALTER COLUMN id SET DEFAULT nextval('alerte_id_seq'::regclass);


--
-- TOC entry 3791 (class 2604 OID 550753)
-- Dependencies: 201 200
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY alerte_document ALTER COLUMN id SET DEFAULT nextval('alerte_document_id_seq'::regclass);


--
-- TOC entry 3793 (class 2604 OID 550754)
-- Dependencies: 205 202
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY alerte_elt ALTER COLUMN id SET DEFAULT nextval('alerte_elt_id_seq'::regclass);


--
-- TOC entry 3796 (class 2604 OID 550755)
-- Dependencies: 204 203
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY alerte_elt_ano ALTER COLUMN id SET DEFAULT nextval('alerte_elt_ano_id_seq'::regclass);


--
-- TOC entry 3797 (class 2604 OID 550756)
-- Dependencies: 208 207
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY bloc_document ALTER COLUMN id SET DEFAULT nextval('bloc_document_id_seq'::regclass);


--
-- TOC entry 3767 (class 2604 OID 550757)
-- Dependencies: 211 182
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY commune ALTER COLUMN id SET DEFAULT nextval('commune_id_seq'::regclass);


--
-- TOC entry 3799 (class 2604 OID 550758)
-- Dependencies: 213 212
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY dde_mdp ALTER COLUMN id SET DEFAULT nextval('dde_mdp_id_seq'::regclass);


--
-- TOC entry 3800 (class 2604 OID 550759)
-- Dependencies: 215 214
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY depot_document ALTER COLUMN id SET DEFAULT nextval('depot_document_id_seq'::regclass);


--
-- TOC entry 3803 (class 2604 OID 550760)
-- Dependencies: 217 216
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY document ALTER COLUMN id SET DEFAULT nextval('document_id_seq'::regclass);


--
-- TOC entry 3810 (class 2604 OID 550761)
-- Dependencies: 221 220
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY email ALTER COLUMN id SET DEFAULT nextval('email_id_seq'::regclass);


--
-- TOC entry 3812 (class 2604 OID 550762)
-- Dependencies: 223 222
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY email_modele ALTER COLUMN id SET DEFAULT nextval('email_modele_id_seq'::regclass);


--
-- TOC entry 3814 (class 2604 OID 550763)
-- Dependencies: 229 225
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant ALTER COLUMN id SET DEFAULT nextval('hydrant_id_seq'::regclass);


--
-- TOC entry 3818 (class 2604 OID 550764)
-- Dependencies: 228 227
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_document ALTER COLUMN id SET DEFAULT nextval('hydrant_document_id_seq'::regclass);


--
-- TOC entry 3820 (class 2604 OID 550765)
-- Dependencies: 233 232
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_prescrit ALTER COLUMN id SET DEFAULT nextval('hydrant_prescrit_id_seq'::regclass);


--
-- TOC entry 3821 (class 2604 OID 550766)
-- Dependencies: 235 234
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY metadonnee ALTER COLUMN id SET DEFAULT nextval('metadonnee_id_seq'::regclass);


--
-- TOC entry 3776 (class 2604 OID 550767)
-- Dependencies: 236 187
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY organisme ALTER COLUMN id SET DEFAULT nextval('organisme_id_seq'::regclass);


--
-- TOC entry 3823 (class 2604 OID 550768)
-- Dependencies: 240 237
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY permis ALTER COLUMN id SET DEFAULT nextval('permis_id_seq'::regclass);


--
-- TOC entry 3827 (class 2604 OID 550769)
-- Dependencies: 239 238
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY permis_document ALTER COLUMN id SET DEFAULT nextval('permis_document_id_seq'::regclass);


--
-- TOC entry 3836 (class 2604 OID 550770)
-- Dependencies: 252 249
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY rci ALTER COLUMN id SET DEFAULT nextval('rci_id_seq'::regclass);


--
-- TOC entry 3837 (class 2604 OID 550771)
-- Dependencies: 251 250
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY rci_document ALTER COLUMN id SET DEFAULT nextval('rci_document_id_seq'::regclass);


--
-- TOC entry 3839 (class 2604 OID 550772)
-- Dependencies: 254 253
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY sous_type_alerte_elt ALTER COLUMN id SET DEFAULT nextval('sous_type_alerte_elt_id_seq'::regclass);


--
-- TOC entry 3841 (class 2604 OID 550773)
-- Dependencies: 257 256
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY synchronisation ALTER COLUMN id SET DEFAULT nextval('synchronisation_id_seq'::regclass);


--
-- TOC entry 3843 (class 2604 OID 550774)
-- Dependencies: 259 258
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY thematique ALTER COLUMN id SET DEFAULT nextval('thematique_id_seq'::regclass);


--
-- TOC entry 3845 (class 2604 OID 550775)
-- Dependencies: 261 260
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY tournee ALTER COLUMN id SET DEFAULT nextval('tournee_id_seq'::regclass);


--
-- TOC entry 3847 (class 2604 OID 550776)
-- Dependencies: 263 262
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_alerte_ano ALTER COLUMN id SET DEFAULT nextval('type_alerte_ano_id_seq'::regclass);


--
-- TOC entry 3849 (class 2604 OID 550777)
-- Dependencies: 265 264
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_alerte_elt ALTER COLUMN id SET DEFAULT nextval('type_alerte_elt_id_seq'::regclass);


--
-- TOC entry 3853 (class 2604 OID 550778)
-- Dependencies: 281 268
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant ALTER COLUMN id SET DEFAULT nextval('type_hydrant_id_seq'::regclass);


--
-- TOC entry 3856 (class 2604 OID 550779)
-- Dependencies: 270 269
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_anomalie ALTER COLUMN id SET DEFAULT nextval('type_hydrant_anomalie_id_seq'::regclass);


--
-- TOC entry 3857 (class 2604 OID 550780)
-- Dependencies: 272 271
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_anomalie_nature ALTER COLUMN id SET DEFAULT nextval('type_hydrant_anomalie_nature_id_seq'::regclass);


--
-- TOC entry 3860 (class 2604 OID 550781)
-- Dependencies: 275 274
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_critere ALTER COLUMN id SET DEFAULT nextval('type_hydrant_critere_id_seq'::regclass);


--
-- TOC entry 3863 (class 2604 OID 550782)
-- Dependencies: 277 276
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_diametre ALTER COLUMN id SET DEFAULT nextval('type_hydrant_diametre_id_seq'::regclass);


--
-- TOC entry 3866 (class 2604 OID 550783)
-- Dependencies: 280 279
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_domaine ALTER COLUMN id SET DEFAULT nextval('type_hydrant_domaine_id_seq'::regclass);


--
-- TOC entry 3869 (class 2604 OID 550784)
-- Dependencies: 283 282
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_marque ALTER COLUMN id SET DEFAULT nextval('type_hydrant_marque_id_seq'::regclass);


--
-- TOC entry 3872 (class 2604 OID 550785)
-- Dependencies: 285 284
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_materiau ALTER COLUMN id SET DEFAULT nextval('type_hydrant_materiau_id_seq'::regclass);


--
-- TOC entry 3875 (class 2604 OID 550786)
-- Dependencies: 287 286
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_modele ALTER COLUMN id SET DEFAULT nextval('type_hydrant_modele_id_seq'::regclass);


--
-- TOC entry 3877 (class 2604 OID 550787)
-- Dependencies: 289 288
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_nature ALTER COLUMN id SET DEFAULT nextval('type_hydrant_nature_id_seq'::regclass);


--
-- TOC entry 3880 (class 2604 OID 550788)
-- Dependencies: 291 290
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_positionnement ALTER COLUMN id SET DEFAULT nextval('type_hydrant_positionnement_id_seq'::regclass);


--
-- TOC entry 3882 (class 2604 OID 550789)
-- Dependencies: 293 292
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_saisie ALTER COLUMN id SET DEFAULT nextval('type_hydrant_saisie_id_seq'::regclass);


--
-- TOC entry 3885 (class 2604 OID 550790)
-- Dependencies: 295 294
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_vol_constate ALTER COLUMN id SET DEFAULT nextval('type_hydrant_vol_constate_id_seq'::regclass);


--
-- TOC entry 3781 (class 2604 OID 550791)
-- Dependencies: 296 191
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_permis_avis ALTER COLUMN id SET DEFAULT nextval('type_permis_avis_id_seq'::regclass);


--
-- TOC entry 3888 (class 2604 OID 550792)
-- Dependencies: 298 297
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_permis_interservice ALTER COLUMN id SET DEFAULT nextval('type_permis_interservice_id_seq'::regclass);


--
-- TOC entry 3891 (class 2604 OID 550793)
-- Dependencies: 300 299
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_rci_degre_certitude ALTER COLUMN id SET DEFAULT nextval('type_rci_degre_certitude_id_seq'::regclass);


--
-- TOC entry 3894 (class 2604 OID 550794)
-- Dependencies: 302 301
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_rci_origine_alerte ALTER COLUMN id SET DEFAULT nextval('type_rci_origine_alerte_id_seq'::regclass);


--
-- TOC entry 3897 (class 2604 OID 550795)
-- Dependencies: 304 303
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_rci_prom_categorie ALTER COLUMN id SET DEFAULT nextval('type_rci_prom_categorie_id_seq'::regclass);


--
-- TOC entry 3773 (class 2604 OID 550796)
-- Dependencies: 305 185
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_rci_prom_famille ALTER COLUMN id SET DEFAULT nextval('type_rci_prom_famille_id_seq'::regclass);


--
-- TOC entry 3900 (class 2604 OID 550797)
-- Dependencies: 307 306
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_rci_prom_partition ALTER COLUMN id SET DEFAULT nextval('type_rci_prom_partition_id_seq'::regclass);


--
-- TOC entry 3785 (class 2604 OID 550798)
-- Dependencies: 308 194
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY utilisateur ALTER COLUMN id SET DEFAULT nextval('utilisateur_id_seq'::regclass);


--
-- TOC entry 3902 (class 2604 OID 550799)
-- Dependencies: 310 309
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY voie ALTER COLUMN id SET DEFAULT nextval('voie_id_seq'::regclass);


--
-- TOC entry 3906 (class 2604 OID 550800)
-- Dependencies: 312 311
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY zone_competence ALTER COLUMN id SET DEFAULT nextval('zone_competence_id_seq'::regclass);


--
-- TOC entry 3910 (class 2604 OID 550801)
-- Dependencies: 314 313
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY zone_speciale ALTER COLUMN id SET DEFAULT nextval('zone_speciale_id_seq'::regclass);


SET search_path = tracabilite, pg_catalog;

--
-- TOC entry 3914 (class 2604 OID 550825)
-- Dependencies: 420 419
-- Name: id; Type: DEFAULT; Schema: tracabilite; Owner: postgres
--

ALTER TABLE ONLY hydrant ALTER COLUMN id SET DEFAULT nextval('hydrant_id_seq'::regclass);


SET search_path = pdi, pg_catalog;

--
-- TOC entry 3916 (class 2606 OID 560578)
-- Dependencies: 174 174
-- Name: modele_message_pkey; Type: CONSTRAINT; Schema: pdi; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY modele_message
    ADD CONSTRAINT modele_message_pkey PRIMARY KEY (idmodele);


--
-- TOC entry 3920 (class 2606 OID 560582)
-- Dependencies: 176 176
-- Name: modele_traitement_parametre_pkey; Type: CONSTRAINT; Schema: pdi; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY modele_traitement_parametre
    ADD CONSTRAINT modele_traitement_parametre_pkey PRIMARY KEY (idparametre);


--
-- TOC entry 3918 (class 2606 OID 560584)
-- Dependencies: 175 175
-- Name: modele_traitement_pkey; Type: CONSTRAINT; Schema: pdi; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY modele_traitement
    ADD CONSTRAINT modele_traitement_pkey PRIMARY KEY (idmodele);


--
-- TOC entry 3922 (class 2606 OID 560586)
-- Dependencies: 177 177
-- Name: statut_pkey; Type: CONSTRAINT; Schema: pdi; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY statut
    ADD CONSTRAINT statut_pkey PRIMARY KEY (idstatut);


--
-- TOC entry 3926 (class 2606 OID 560588)
-- Dependencies: 180 180 180
-- Name: traitement_cc_pkey; Type: CONSTRAINT; Schema: pdi; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY traitement_cc
    ADD CONSTRAINT traitement_cc_pkey PRIMARY KEY (idtraitement, idutilisateur);


--
-- TOC entry 3928 (class 2606 OID 560590)
-- Dependencies: 181 181 181
-- Name: traitement_parametre_pkey; Type: CONSTRAINT; Schema: pdi; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY traitement_parametre
    ADD CONSTRAINT traitement_parametre_pkey PRIMARY KEY (idparametre, idtraitement);


--
-- TOC entry 3924 (class 2606 OID 560592)
-- Dependencies: 179 179
-- Name: traitement_pkey; Type: CONSTRAINT; Schema: pdi; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY traitement
    ADD CONSTRAINT traitement_pkey PRIMARY KEY (idtraitement);


SET search_path = remocra, pg_catalog;

--
-- TOC entry 3954 (class 2606 OID 560598)
-- Dependencies: 200 200
-- Name: alerte_document_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY alerte_document
    ADD CONSTRAINT alerte_document_pkey PRIMARY KEY (id);


--
-- TOC entry 3959 (class 2606 OID 560600)
-- Dependencies: 203 203
-- Name: alerte_elt_ano_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY alerte_elt_ano
    ADD CONSTRAINT alerte_elt_ano_pkey PRIMARY KEY (id);


--
-- TOC entry 3957 (class 2606 OID 560602)
-- Dependencies: 202 202
-- Name: alerte_elt_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY alerte_elt
    ADD CONSTRAINT alerte_elt_pkey PRIMARY KEY (id);


--
-- TOC entry 3952 (class 2606 OID 560604)
-- Dependencies: 199 199
-- Name: alerte_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY alerte
    ADD CONSTRAINT alerte_pkey PRIMARY KEY (id);


--
-- TOC entry 3961 (class 2606 OID 560606)
-- Dependencies: 207 207
-- Name: bloc_document_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bloc_document
    ADD CONSTRAINT bloc_document_pkey PRIMARY KEY (id);


--
-- TOC entry 3963 (class 2606 OID 560608)
-- Dependencies: 209 209 209
-- Name: bloc_document_profil_droits_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bloc_document_profil_droits
    ADD CONSTRAINT bloc_document_profil_droits_pkey PRIMARY KEY (bloc_document, profil_droits);


--
-- TOC entry 3965 (class 2606 OID 560610)
-- Dependencies: 210 210 210
-- Name: bloc_document_thematiques_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bloc_document_thematiques
    ADD CONSTRAINT bloc_document_thematiques_pkey PRIMARY KEY (bloc_document, thematiques);


--
-- TOC entry 3931 (class 2606 OID 560612)
-- Dependencies: 182 182
-- Name: commune_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY commune
    ADD CONSTRAINT commune_pkey PRIMARY KEY (id);


--
-- TOC entry 3967 (class 2606 OID 560614)
-- Dependencies: 212 212
-- Name: dde_mdp_code_key; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY dde_mdp
    ADD CONSTRAINT dde_mdp_code_key UNIQUE (code);


--
-- TOC entry 3969 (class 2606 OID 560616)
-- Dependencies: 212 212
-- Name: dde_mdp_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY dde_mdp
    ADD CONSTRAINT dde_mdp_pkey PRIMARY KEY (id);


--
-- TOC entry 3971 (class 2606 OID 560618)
-- Dependencies: 214 214
-- Name: depot_document_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY depot_document
    ADD CONSTRAINT depot_document_pkey PRIMARY KEY (id);


--
-- TOC entry 3973 (class 2606 OID 560620)
-- Dependencies: 216 216
-- Name: document_code_key; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY document
    ADD CONSTRAINT document_code_key UNIQUE (code);


--
-- TOC entry 3975 (class 2606 OID 560622)
-- Dependencies: 216 216 216
-- Name: document_fichier_repertoire_key; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY document
    ADD CONSTRAINT document_fichier_repertoire_key UNIQUE (fichier, repertoire);


--
-- TOC entry 3977 (class 2606 OID 560624)
-- Dependencies: 216 216
-- Name: document_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY document
    ADD CONSTRAINT document_pkey PRIMARY KEY (id);


--
-- TOC entry 3979 (class 2606 OID 560626)
-- Dependencies: 219 219
-- Name: droit_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY droit
    ADD CONSTRAINT droit_pkey PRIMARY KEY (id);


--
-- TOC entry 3981 (class 2606 OID 560628)
-- Dependencies: 219 219 219
-- Name: droit_profil_droit_type_droit; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY droit
    ADD CONSTRAINT droit_profil_droit_type_droit UNIQUE (profil_droit, type_droit);


--
-- TOC entry 3985 (class 2606 OID 560630)
-- Dependencies: 222 222
-- Name: email_modele_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY email_modele
    ADD CONSTRAINT email_modele_pkey PRIMARY KEY (id);


--
-- TOC entry 3983 (class 2606 OID 560632)
-- Dependencies: 220 220
-- Name: email_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY email
    ADD CONSTRAINT email_pkey PRIMARY KEY (id);


--
-- TOC entry 3992 (class 2606 OID 560634)
-- Dependencies: 226 226 226
-- Name: hydrant_anomalies_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hydrant_anomalies
    ADD CONSTRAINT hydrant_anomalies_pkey PRIMARY KEY (hydrant, anomalies);


--
-- TOC entry 3994 (class 2606 OID 560636)
-- Dependencies: 227 227
-- Name: hydrant_document_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hydrant_document
    ADD CONSTRAINT hydrant_document_pkey PRIMARY KEY (id);


--
-- TOC entry 3988 (class 2606 OID 560638)
-- Dependencies: 225 225
-- Name: hydrant_numero_key; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT hydrant_numero_key UNIQUE (numero);


--
-- TOC entry 3996 (class 2606 OID 560640)
-- Dependencies: 230 230
-- Name: hydrant_pena_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hydrant_pena
    ADD CONSTRAINT hydrant_pena_pkey PRIMARY KEY (id);


--
-- TOC entry 3998 (class 2606 OID 560642)
-- Dependencies: 231 231
-- Name: hydrant_pibi_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hydrant_pibi
    ADD CONSTRAINT hydrant_pibi_pkey PRIMARY KEY (id);


--
-- TOC entry 3990 (class 2606 OID 560644)
-- Dependencies: 225 225
-- Name: hydrant_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT hydrant_pkey PRIMARY KEY (id);


--
-- TOC entry 4000 (class 2606 OID 560646)
-- Dependencies: 232 232
-- Name: hydrant_prescrit_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hydrant_prescrit
    ADD CONSTRAINT hydrant_prescrit_pkey PRIMARY KEY (id);


--
-- TOC entry 4002 (class 2606 OID 560648)
-- Dependencies: 234 234
-- Name: metadonnee_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY metadonnee
    ADD CONSTRAINT metadonnee_pkey PRIMARY KEY (id);


--
-- TOC entry 3937 (class 2606 OID 560650)
-- Dependencies: 187 187
-- Name: organisme_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY organisme
    ADD CONSTRAINT organisme_pkey PRIMARY KEY (id);


--
-- TOC entry 3933 (class 2606 OID 560652)
-- Dependencies: 183 183
-- Name: param_conf_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY param_conf
    ADD CONSTRAINT param_conf_pkey PRIMARY KEY (cle);


--
-- TOC entry 4007 (class 2606 OID 560654)
-- Dependencies: 238 238
-- Name: permis_document_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY permis_document
    ADD CONSTRAINT permis_document_pkey PRIMARY KEY (id);


--
-- TOC entry 4005 (class 2606 OID 560656)
-- Dependencies: 237 237
-- Name: permis_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY permis
    ADD CONSTRAINT permis_pkey PRIMARY KEY (id);


--
-- TOC entry 4009 (class 2606 OID 560658)
-- Dependencies: 242 242
-- Name: profil_droit_code_key; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY profil_droit
    ADD CONSTRAINT profil_droit_code_key UNIQUE (code);


--
-- TOC entry 4011 (class 2606 OID 560660)
-- Dependencies: 242 242
-- Name: profil_droit_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY profil_droit
    ADD CONSTRAINT profil_droit_pkey PRIMARY KEY (id);


--
-- TOC entry 4013 (class 2606 OID 560662)
-- Dependencies: 244 244
-- Name: profil_organisme_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY profil_organisme
    ADD CONSTRAINT profil_organisme_pkey PRIMARY KEY (id);


--
-- TOC entry 4015 (class 2606 OID 560664)
-- Dependencies: 246 246
-- Name: profil_organisme_utilisateur_droit_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY profil_organisme_utilisateur_droit
    ADD CONSTRAINT profil_organisme_utilisateur_droit_pkey PRIMARY KEY (id);


--
-- TOC entry 4017 (class 2606 OID 560666)
-- Dependencies: 246 246 246
-- Name: profil_organisme_utilisateur_droit_profil_organisme_profil_util; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY profil_organisme_utilisateur_droit
    ADD CONSTRAINT profil_organisme_utilisateur_droit_profil_organisme_profil_util UNIQUE (profil_organisme, profil_utilisateur);


--
-- TOC entry 4019 (class 2606 OID 560668)
-- Dependencies: 248 248
-- Name: profil_utilisateur_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY profil_utilisateur
    ADD CONSTRAINT profil_utilisateur_pkey PRIMARY KEY (id);


--
-- TOC entry 4023 (class 2606 OID 560670)
-- Dependencies: 250 250
-- Name: rci_document_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY rci_document
    ADD CONSTRAINT rci_document_pkey PRIMARY KEY (id);


--
-- TOC entry 4021 (class 2606 OID 560672)
-- Dependencies: 249 249
-- Name: rci_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY rci
    ADD CONSTRAINT rci_pkey PRIMARY KEY (id);


--
-- TOC entry 4025 (class 2606 OID 560674)
-- Dependencies: 253 253
-- Name: sous_type_alerte_elt_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY sous_type_alerte_elt
    ADD CONSTRAINT sous_type_alerte_elt_pkey PRIMARY KEY (id);


--
-- TOC entry 4027 (class 2606 OID 560676)
-- Dependencies: 255 255
-- Name: suivi_patches_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY suivi_patches
    ADD CONSTRAINT suivi_patches_pkey PRIMARY KEY (numero);


--
-- TOC entry 4029 (class 2606 OID 560678)
-- Dependencies: 256 256
-- Name: synchronisation_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY synchronisation
    ADD CONSTRAINT synchronisation_pkey PRIMARY KEY (id);


--
-- TOC entry 4031 (class 2606 OID 560680)
-- Dependencies: 258 258
-- Name: thematique_code_key; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY thematique
    ADD CONSTRAINT thematique_code_key UNIQUE (code);


--
-- TOC entry 4033 (class 2606 OID 560682)
-- Dependencies: 258 258
-- Name: thematique_nom_key; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY thematique
    ADD CONSTRAINT thematique_nom_key UNIQUE (nom);


--
-- TOC entry 4035 (class 2606 OID 560684)
-- Dependencies: 258 258
-- Name: thematique_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY thematique
    ADD CONSTRAINT thematique_pkey PRIMARY KEY (id);


--
-- TOC entry 4037 (class 2606 OID 560686)
-- Dependencies: 260 260
-- Name: tournee_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY tournee
    ADD CONSTRAINT tournee_pkey PRIMARY KEY (id);


--
-- TOC entry 4039 (class 2606 OID 560688)
-- Dependencies: 262 262
-- Name: type_alerte_ano_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_alerte_ano
    ADD CONSTRAINT type_alerte_ano_pkey PRIMARY KEY (id);


--
-- TOC entry 4041 (class 2606 OID 560690)
-- Dependencies: 264 264
-- Name: type_alerte_elt_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_alerte_elt
    ADD CONSTRAINT type_alerte_elt_pkey PRIMARY KEY (id);


--
-- TOC entry 4043 (class 2606 OID 560692)
-- Dependencies: 267 267
-- Name: type_droit_code_key; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_droit
    ADD CONSTRAINT type_droit_code_key UNIQUE (code);


--
-- TOC entry 4045 (class 2606 OID 560694)
-- Dependencies: 267 267
-- Name: type_droit_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_droit
    ADD CONSTRAINT type_droit_pkey PRIMARY KEY (id);


--
-- TOC entry 4051 (class 2606 OID 560696)
-- Dependencies: 271 271
-- Name: type_hydrant_anomalie_nature_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_hydrant_anomalie_nature
    ADD CONSTRAINT type_hydrant_anomalie_nature_pkey PRIMARY KEY (id);


--
-- TOC entry 4053 (class 2606 OID 560698)
-- Dependencies: 273 273 273
-- Name: type_hydrant_anomalie_nature_saisies_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_hydrant_anomalie_nature_saisies
    ADD CONSTRAINT type_hydrant_anomalie_nature_saisies_pkey PRIMARY KEY (type_hydrant_anomalie_nature, saisies);


--
-- TOC entry 4049 (class 2606 OID 560700)
-- Dependencies: 269 269
-- Name: type_hydrant_anomalie_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_hydrant_anomalie
    ADD CONSTRAINT type_hydrant_anomalie_pkey PRIMARY KEY (id);


--
-- TOC entry 4055 (class 2606 OID 560702)
-- Dependencies: 274 274
-- Name: type_hydrant_critere_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_hydrant_critere
    ADD CONSTRAINT type_hydrant_critere_pkey PRIMARY KEY (id);


--
-- TOC entry 4059 (class 2606 OID 560704)
-- Dependencies: 278 278 278
-- Name: type_hydrant_diametre_natures_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_hydrant_diametre_natures
    ADD CONSTRAINT type_hydrant_diametre_natures_pkey PRIMARY KEY (type_hydrant_diametre, natures);


--
-- TOC entry 4057 (class 2606 OID 560706)
-- Dependencies: 276 276
-- Name: type_hydrant_diametre_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_hydrant_diametre
    ADD CONSTRAINT type_hydrant_diametre_pkey PRIMARY KEY (id);


--
-- TOC entry 4061 (class 2606 OID 560708)
-- Dependencies: 279 279
-- Name: type_hydrant_domaine_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_hydrant_domaine
    ADD CONSTRAINT type_hydrant_domaine_pkey PRIMARY KEY (id);


--
-- TOC entry 4063 (class 2606 OID 560710)
-- Dependencies: 282 282
-- Name: type_hydrant_marque_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_hydrant_marque
    ADD CONSTRAINT type_hydrant_marque_pkey PRIMARY KEY (id);


--
-- TOC entry 4065 (class 2606 OID 560712)
-- Dependencies: 284 284
-- Name: type_hydrant_materiau_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_hydrant_materiau
    ADD CONSTRAINT type_hydrant_materiau_pkey PRIMARY KEY (id);


--
-- TOC entry 4067 (class 2606 OID 560714)
-- Dependencies: 286 286
-- Name: type_hydrant_modele_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_hydrant_modele
    ADD CONSTRAINT type_hydrant_modele_pkey PRIMARY KEY (id);


--
-- TOC entry 4069 (class 2606 OID 560716)
-- Dependencies: 288 288
-- Name: type_hydrant_nature_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_hydrant_nature
    ADD CONSTRAINT type_hydrant_nature_pkey PRIMARY KEY (id);


--
-- TOC entry 4047 (class 2606 OID 560718)
-- Dependencies: 268 268
-- Name: type_hydrant_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_hydrant
    ADD CONSTRAINT type_hydrant_pkey PRIMARY KEY (id);


--
-- TOC entry 4071 (class 2606 OID 560720)
-- Dependencies: 290 290
-- Name: type_hydrant_positionnement_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_hydrant_positionnement
    ADD CONSTRAINT type_hydrant_positionnement_pkey PRIMARY KEY (id);


--
-- TOC entry 4073 (class 2606 OID 560722)
-- Dependencies: 292 292
-- Name: type_hydrant_saisie_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_hydrant_saisie
    ADD CONSTRAINT type_hydrant_saisie_pkey PRIMARY KEY (id);


--
-- TOC entry 4075 (class 2606 OID 560724)
-- Dependencies: 294 294
-- Name: type_hydrant_vol_constate_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_hydrant_vol_constate
    ADD CONSTRAINT type_hydrant_vol_constate_pkey PRIMARY KEY (id);


--
-- TOC entry 3939 (class 2606 OID 560726)
-- Dependencies: 189 189
-- Name: type_organisme_code_key; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_organisme
    ADD CONSTRAINT type_organisme_code_key UNIQUE (code);


--
-- TOC entry 3941 (class 2606 OID 560728)
-- Dependencies: 189 189
-- Name: type_organisme_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_organisme
    ADD CONSTRAINT type_organisme_pkey PRIMARY KEY (id);


--
-- TOC entry 3943 (class 2606 OID 560730)
-- Dependencies: 191 191
-- Name: type_permis_avis_code_key; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_permis_avis
    ADD CONSTRAINT type_permis_avis_code_key UNIQUE (code);


--
-- TOC entry 3945 (class 2606 OID 560732)
-- Dependencies: 191 191
-- Name: type_permis_avis_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_permis_avis
    ADD CONSTRAINT type_permis_avis_pkey PRIMARY KEY (id);


--
-- TOC entry 4077 (class 2606 OID 560734)
-- Dependencies: 297 297
-- Name: type_permis_interservice_code_key; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_permis_interservice
    ADD CONSTRAINT type_permis_interservice_code_key UNIQUE (code);


--
-- TOC entry 4079 (class 2606 OID 560736)
-- Dependencies: 297 297
-- Name: type_permis_interservice_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_permis_interservice
    ADD CONSTRAINT type_permis_interservice_pkey PRIMARY KEY (id);


--
-- TOC entry 4081 (class 2606 OID 560738)
-- Dependencies: 299 299
-- Name: type_rci_degre_certitude_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_rci_degre_certitude
    ADD CONSTRAINT type_rci_degre_certitude_pkey PRIMARY KEY (id);


--
-- TOC entry 4083 (class 2606 OID 560740)
-- Dependencies: 301 301
-- Name: type_rci_origine_alerte_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_rci_origine_alerte
    ADD CONSTRAINT type_rci_origine_alerte_pkey PRIMARY KEY (id);


--
-- TOC entry 4085 (class 2606 OID 560742)
-- Dependencies: 303 303
-- Name: type_rci_prom_categorie_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_rci_prom_categorie
    ADD CONSTRAINT type_rci_prom_categorie_pkey PRIMARY KEY (id);


--
-- TOC entry 3935 (class 2606 OID 560744)
-- Dependencies: 185 185
-- Name: type_rci_prom_famille_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_rci_prom_famille
    ADD CONSTRAINT type_rci_prom_famille_pkey PRIMARY KEY (id);


--
-- TOC entry 4087 (class 2606 OID 560746)
-- Dependencies: 306 306
-- Name: type_rci_prom_partition_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY type_rci_prom_partition
    ADD CONSTRAINT type_rci_prom_partition_pkey PRIMARY KEY (id);


--
-- TOC entry 3947 (class 2606 OID 560748)
-- Dependencies: 194 194
-- Name: utilisateur_identifiant_key; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY utilisateur
    ADD CONSTRAINT utilisateur_identifiant_key UNIQUE (identifiant);


--
-- TOC entry 3949 (class 2606 OID 560750)
-- Dependencies: 194 194
-- Name: utilisateur_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY utilisateur
    ADD CONSTRAINT utilisateur_pkey PRIMARY KEY (id);


--
-- TOC entry 4090 (class 2606 OID 560752)
-- Dependencies: 309 309
-- Name: voie_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY voie
    ADD CONSTRAINT voie_pkey PRIMARY KEY (id);


--
-- TOC entry 4093 (class 2606 OID 560755)
-- Dependencies: 311 311
-- Name: zone_competence_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY zone_competence
    ADD CONSTRAINT zone_competence_pkey PRIMARY KEY (id);


--
-- TOC entry 4096 (class 2606 OID 560757)
-- Dependencies: 313 313
-- Name: zone_speciale_pkey; Type: CONSTRAINT; Schema: remocra; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY zone_speciale
    ADD CONSTRAINT zone_speciale_pkey PRIMARY KEY (id);


SET search_path = tracabilite, pg_catalog;

--
-- TOC entry 4098 (class 2606 OID 561005)
-- Dependencies: 419 419
-- Name: hydrant_pkey; Type: CONSTRAINT; Schema: tracabilite; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT hydrant_pkey PRIMARY KEY (id);


SET search_path = remocra, pg_catalog;

--
-- TOC entry 3955 (class 1259 OID 561006)
-- Dependencies: 202 3219
-- Name: alerte_elt_geometrie_idx; Type: INDEX; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE INDEX alerte_elt_geometrie_idx ON alerte_elt USING gist (geometrie);


--
-- TOC entry 3950 (class 1259 OID 561007)
-- Dependencies: 199 3219
-- Name: alerte_geometrie_idx; Type: INDEX; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE INDEX alerte_geometrie_idx ON alerte USING gist (geometrie);


--
-- TOC entry 3929 (class 1259 OID 561008)
-- Dependencies: 182 3219
-- Name: commune_geometrie_idx; Type: INDEX; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE INDEX commune_geometrie_idx ON commune USING gist (geometrie);


--
-- TOC entry 3986 (class 1259 OID 561009)
-- Dependencies: 225 3219
-- Name: hydrant_geometrie_idx; Type: INDEX; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE INDEX hydrant_geometrie_idx ON hydrant USING gist (geometrie);


--
-- TOC entry 4003 (class 1259 OID 561010)
-- Dependencies: 237 3219
-- Name: permis_geometrie_idx; Type: INDEX; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE INDEX permis_geometrie_idx ON permis USING gist (geometrie);


--
-- TOC entry 4088 (class 1259 OID 561011)
-- Dependencies: 309 3219
-- Name: voie_geometrie_idx; Type: INDEX; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE INDEX voie_geometrie_idx ON voie USING gist (geometrie);


--
-- TOC entry 4091 (class 1259 OID 561012)
-- Dependencies: 311 3219
-- Name: zone_competence_geometrie_idx; Type: INDEX; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE INDEX zone_competence_geometrie_idx ON zone_competence USING gist (geometrie);


--
-- TOC entry 4094 (class 1259 OID 561013)
-- Dependencies: 313 3219
-- Name: zone_speciale_geometrie_idx; Type: INDEX; Schema: remocra; Owner: postgres; Tablespace: 
--

CREATE INDEX zone_speciale_geometrie_idx ON zone_speciale USING gist (geometrie);


--
-- TOC entry 4185 (class 2620 OID 561135)
-- Dependencies: 1276 225
-- Name: trig_aui; Type: TRIGGER; Schema: remocra; Owner: postgres
--

CREATE TRIGGER trig_aui
    AFTER INSERT OR UPDATE ON hydrant
    FOR EACH ROW
    EXECUTE PROCEDURE tracabilite.trg_hydrant();


--
-- TOC entry 4192 (class 2620 OID 561136)
-- Dependencies: 231 1276
-- Name: trig_aui; Type: TRIGGER; Schema: remocra; Owner: postgres
--

CREATE TRIGGER trig_aui
    AFTER INSERT OR UPDATE ON hydrant_pibi
    FOR EACH ROW
    EXECUTE PROCEDURE tracabilite.trg_hydrant();


--
-- TOC entry 4190 (class 2620 OID 561137)
-- Dependencies: 230 1276
-- Name: trig_aui; Type: TRIGGER; Schema: remocra; Owner: postgres
--

CREATE TRIGGER trig_aui
    AFTER INSERT OR UPDATE ON hydrant_pena
    FOR EACH ROW
    EXECUTE PROCEDURE tracabilite.trg_hydrant();


--
-- TOC entry 4187 (class 2620 OID 561138)
-- Dependencies: 1276 226
-- Name: trig_aui; Type: TRIGGER; Schema: remocra; Owner: postgres
--

CREATE TRIGGER trig_aui
    AFTER INSERT OR DELETE OR UPDATE ON hydrant_anomalies
    FOR EACH ROW
    EXECUTE PROCEDURE tracabilite.trg_hydrant();


--
-- TOC entry 4193 (class 2620 OID 561139)
-- Dependencies: 1276 231
-- Name: trig_bd; Type: TRIGGER; Schema: remocra; Owner: postgres
--

CREATE TRIGGER trig_bd
    BEFORE DELETE ON hydrant_pibi
    FOR EACH ROW
    EXECUTE PROCEDURE tracabilite.trg_hydrant();


--
-- TOC entry 4191 (class 2620 OID 561140)
-- Dependencies: 1276 230
-- Name: trig_bd; Type: TRIGGER; Schema: remocra; Owner: postgres
--

CREATE TRIGGER trig_bd
    BEFORE DELETE ON hydrant_pena
    FOR EACH ROW
    EXECUTE PROCEDURE tracabilite.trg_hydrant();


--
-- TOC entry 4188 (class 2620 OID 561141)
-- Dependencies: 1276 226
-- Name: trig_bd; Type: TRIGGER; Schema: remocra; Owner: postgres
--

CREATE TRIGGER trig_bd
    BEFORE DELETE ON hydrant_anomalies
    FOR EACH ROW
    EXECUTE PROCEDURE tracabilite.trg_hydrant();


--
-- TOC entry 4194 (class 2620 OID 561142)
-- Dependencies: 1270 231
-- Name: trig_debit_pression; Type: TRIGGER; Schema: remocra; Owner: postgres
--

CREATE TRIGGER trig_debit_pression
    AFTER INSERT OR UPDATE ON hydrant_pibi
    FOR EACH ROW
    EXECUTE PROCEDURE trg_calcul_debit_pression();


--
-- TOC entry 4186 (class 2620 OID 561143)
-- Dependencies: 1272 225
-- Name: trig_hydrant_tournee; Type: TRIGGER; Schema: remocra; Owner: postgres
--

CREATE TRIGGER trig_hydrant_tournee
    AFTER INSERT OR DELETE OR UPDATE ON hydrant
    FOR EACH ROW
    EXECUTE PROCEDURE trg_hydrant_tournee();


--
-- TOC entry 4189 (class 2620 OID 561144)
-- Dependencies: 226 1271
-- Name: trig_indispo; Type: TRIGGER; Schema: remocra; Owner: postgres
--

CREATE TRIGGER trig_indispo
    AFTER INSERT OR DELETE ON hydrant_anomalies
    FOR EACH ROW
    EXECUTE PROCEDURE trg_calcul_indispo();


SET search_path = pdi, pg_catalog;

--
-- TOC entry 4101 (class 2606 OID 561145)
-- Dependencies: 3917 176 175
-- Name: fk3d5405684675afa4; Type: FK CONSTRAINT; Schema: pdi; Owner: postgres
--

ALTER TABLE ONLY modele_traitement_parametre
    ADD CONSTRAINT fk3d5405684675afa4 FOREIGN KEY (idmodele) REFERENCES modele_traitement(idmodele);


--
-- TOC entry 4102 (class 2606 OID 561150)
-- Dependencies: 179 175 3917
-- Name: fk923581f54675afa4; Type: FK CONSTRAINT; Schema: pdi; Owner: postgres
--

ALTER TABLE ONLY traitement
    ADD CONSTRAINT fk923581f54675afa4 FOREIGN KEY (idmodele) REFERENCES modele_traitement(idmodele);


--
-- TOC entry 4104 (class 2606 OID 561155)
-- Dependencies: 180 179 3923
-- Name: fk923581f54675afa4; Type: FK CONSTRAINT; Schema: pdi; Owner: postgres
--

ALTER TABLE ONLY traitement_cc
    ADD CONSTRAINT fk923581f54675afa4 FOREIGN KEY (idtraitement) REFERENCES traitement(idtraitement);


--
-- TOC entry 4103 (class 2606 OID 561160)
-- Dependencies: 177 3921 179
-- Name: fk923581f57cd7aabd; Type: FK CONSTRAINT; Schema: pdi; Owner: postgres
--

ALTER TABLE ONLY traitement
    ADD CONSTRAINT fk923581f57cd7aabd FOREIGN KEY (idstatut) REFERENCES statut(idstatut);


--
-- TOC entry 4099 (class 2606 OID 561165)
-- Dependencies: 175 174 3915
-- Name: fkd8399bb880a2e237; Type: FK CONSTRAINT; Schema: pdi; Owner: postgres
--

ALTER TABLE ONLY modele_traitement
    ADD CONSTRAINT fkd8399bb880a2e237 FOREIGN KEY (message_succes) REFERENCES modele_message(idmodele);


--
-- TOC entry 4100 (class 2606 OID 561170)
-- Dependencies: 3915 175 174
-- Name: fkd8399bb8fb49f7ff; Type: FK CONSTRAINT; Schema: pdi; Owner: postgres
--

ALTER TABLE ONLY modele_traitement
    ADD CONSTRAINT fkd8399bb8fb49f7ff FOREIGN KEY (message_echec) REFERENCES modele_message(idmodele);


--
-- TOC entry 4105 (class 2606 OID 561175)
-- Dependencies: 181 179 3923
-- Name: fkeca70365a667cb41; Type: FK CONSTRAINT; Schema: pdi; Owner: postgres
--

ALTER TABLE ONLY traitement_parametre
    ADD CONSTRAINT fkeca70365a667cb41 FOREIGN KEY (idtraitement) REFERENCES traitement(idtraitement);


--
-- TOC entry 4106 (class 2606 OID 561180)
-- Dependencies: 181 3919 176
-- Name: fkeca70365f5817db6; Type: FK CONSTRAINT; Schema: pdi; Owner: postgres
--

ALTER TABLE ONLY traitement_parametre
    ADD CONSTRAINT fkeca70365f5817db6 FOREIGN KEY (idparametre) REFERENCES modele_traitement_parametre(idparametre);


SET search_path = remocra, pg_catalog;

--
-- TOC entry 4122 (class 2606 OID 561185)
-- Dependencies: 207 210 3960
-- Name: fk140d14a94995249; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY bloc_document_thematiques
    ADD CONSTRAINT fk140d14a94995249 FOREIGN KEY (bloc_document) REFERENCES bloc_document(id);


--
-- TOC entry 4123 (class 2606 OID 561190)
-- Dependencies: 258 4034 210
-- Name: fk140d14a9fdec3d27; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY bloc_document_thematiques
    ADD CONSTRAINT fk140d14a9fdec3d27 FOREIGN KEY (thematiques) REFERENCES thematique(id);


--
-- TOC entry 4162 (class 2606 OID 561195)
-- Dependencies: 299 4080 249
-- Name: fk1b85821870359; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY rci
    ADD CONSTRAINT fk1b85821870359 FOREIGN KEY (degre_certitude) REFERENCES type_rci_degre_certitude(id);


--
-- TOC entry 4163 (class 2606 OID 561200)
-- Dependencies: 249 4082 301
-- Name: fk1b85838423ee7; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY rci
    ADD CONSTRAINT fk1b85838423ee7 FOREIGN KEY (origine_alerte) REFERENCES type_rci_origine_alerte(id);


--
-- TOC entry 4164 (class 2606 OID 561205)
-- Dependencies: 4084 303 249
-- Name: fk1b8583fb5f1fe; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY rci
    ADD CONSTRAINT fk1b8583fb5f1fe FOREIGN KEY (categorie_promethee) REFERENCES type_rci_prom_categorie(id);


--
-- TOC entry 4165 (class 2606 OID 561210)
-- Dependencies: 3948 194 249
-- Name: fk1b858a98055b2; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY rci
    ADD CONSTRAINT fk1b858a98055b2 FOREIGN KEY (utilisateur) REFERENCES utilisateur(id);


--
-- TOC entry 4166 (class 2606 OID 561215)
-- Dependencies: 3930 249 182
-- Name: fk1b858d2da796c; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY rci
    ADD CONSTRAINT fk1b858d2da796c FOREIGN KEY (commune) REFERENCES commune(id);


--
-- TOC entry 4183 (class 2606 OID 561220)
-- Dependencies: 306 185 3934
-- Name: fk257dcb7aa7ef692; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_rci_prom_partition
    ADD CONSTRAINT fk257dcb7aa7ef692 FOREIGN KEY (famille) REFERENCES type_rci_prom_famille(id);


--
-- TOC entry 4125 (class 2606 OID 561225)
-- Dependencies: 3976 216 214
-- Name: fk2b3f72c636f0130a; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY depot_document
    ADD CONSTRAINT fk2b3f72c636f0130a FOREIGN KEY (document) REFERENCES document(id) ON DELETE CASCADE;


--
-- TOC entry 4126 (class 2606 OID 561230)
-- Dependencies: 214 3948 194
-- Name: fk2b3f72c6a98055b2; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY depot_document
    ADD CONSTRAINT fk2b3f72c6a98055b2 FOREIGN KEY (utilisateur) REFERENCES utilisateur(id);


--
-- TOC entry 4120 (class 2606 OID 561235)
-- Dependencies: 3960 209 207
-- Name: fk36b2bc674995249; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY bloc_document_profil_droits
    ADD CONSTRAINT fk36b2bc674995249 FOREIGN KEY (bloc_document) REFERENCES bloc_document(id);


--
-- TOC entry 4121 (class 2606 OID 561243)
-- Dependencies: 242 209 4010
-- Name: fk36b2bc6798a29b26; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY bloc_document_profil_droits
    ADD CONSTRAINT fk36b2bc6798a29b26 FOREIGN KEY (profil_droits) REFERENCES profil_droit(id);


--
-- TOC entry 4184 (class 2606 OID 561248)
-- Dependencies: 3930 309 182
-- Name: fk375195d2da796c; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY voie
    ADD CONSTRAINT fk375195d2da796c FOREIGN KEY (commune) REFERENCES commune(id);


--
-- TOC entry 4180 (class 2606 OID 561253)
-- Dependencies: 282 286 4062
-- Name: fk41b5bdf8cd9e6420; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_modele
    ADD CONSTRAINT fk41b5bdf8cd9e6420 FOREIGN KEY (marque) REFERENCES type_hydrant_marque(id);


--
-- TOC entry 4181 (class 2606 OID 561258)
-- Dependencies: 4046 288 268
-- Name: fk42acd04386657e5d; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_nature
    ADD CONSTRAINT fk42acd04386657e5d FOREIGN KEY (type_hydrant) REFERENCES type_hydrant(id);


--
-- TOC entry 4170 (class 2606 OID 561263)
-- Dependencies: 258 256 4034
-- Name: fk43a80607d27676e2; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY synchronisation
    ADD CONSTRAINT fk43a80607d27676e2 FOREIGN KEY (thematique) REFERENCES thematique(id);


--
-- TOC entry 4149 (class 2606 OID 561268)
-- Dependencies: 234 4034 258
-- Name: fk507e37b0d27676e2; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY metadonnee
    ADD CONSTRAINT fk507e37b0d27676e2 FOREIGN KEY (thematique) REFERENCES thematique(id);


--
-- TOC entry 4129 (class 2606 OID 561273)
-- Dependencies: 225 187 3936
-- Name: fk51b8f028374add52; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f028374add52 FOREIGN KEY (organisme) REFERENCES organisme(id);


--
-- TOC entry 4130 (class 2606 OID 561278)
-- Dependencies: 225 279 4060
-- Name: fk51b8f0285d29d8a8; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f0285d29d8a8 FOREIGN KEY (domaine) REFERENCES type_hydrant_domaine(id);


--
-- TOC entry 4131 (class 2606 OID 561283)
-- Dependencies: 288 4068 225
-- Name: fk51b8f028d10a0428; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f028d10a0428 FOREIGN KEY (nature) REFERENCES type_hydrant_nature(id);


--
-- TOC entry 4132 (class 2606 OID 561288)
-- Dependencies: 182 3930 225
-- Name: fk51b8f028d2da796c; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f028d2da796c FOREIGN KEY (commune) REFERENCES commune(id);


--
-- TOC entry 4133 (class 2606 OID 561293)
-- Dependencies: 225 260 4036
-- Name: fk51b8f028da542518; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f028da542518 FOREIGN KEY (tournee) REFERENCES tournee(id);


--
-- TOC entry 4124 (class 2606 OID 561298)
-- Dependencies: 212 194 3948
-- Name: fk5a4fde5fa98055b2; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY dde_mdp
    ADD CONSTRAINT fk5a4fde5fa98055b2 FOREIGN KEY (utilisateur) REFERENCES utilisateur(id);


--
-- TOC entry 4127 (class 2606 OID 561303)
-- Dependencies: 4044 219 267
-- Name: fk5b6ae8c12db1a65; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY droit
    ADD CONSTRAINT fk5b6ae8c12db1a65 FOREIGN KEY (type_droit) REFERENCES type_droit(id);


--
-- TOC entry 4128 (class 2606 OID 561308)
-- Dependencies: 242 219 4010
-- Name: fk5b6ae8c3723a725; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY droit
    ADD CONSTRAINT fk5b6ae8c3723a725 FOREIGN KEY (profil_droit) REFERENCES profil_droit(id) ON DELETE CASCADE;


--
-- TOC entry 4137 (class 2606 OID 561313)
-- Dependencies: 3976 227 216
-- Name: fk5b90bf5236f0130a; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_document
    ADD CONSTRAINT fk5b90bf5236f0130a FOREIGN KEY (document) REFERENCES document(id);


--
-- TOC entry 4138 (class 2606 OID 561318)
-- Dependencies: 225 227 3989
-- Name: fk5b90bf5250004fc; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_document
    ADD CONSTRAINT fk5b90bf5250004fc FOREIGN KEY (hydrant) REFERENCES hydrant(id);


--
-- TOC entry 4135 (class 2606 OID 561323)
-- Dependencies: 226 4048 269
-- Name: fk5e56b38a1c51b70d; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_anomalies
    ADD CONSTRAINT fk5e56b38a1c51b70d FOREIGN KEY (anomalies) REFERENCES type_hydrant_anomalie(id);


--
-- TOC entry 4136 (class 2606 OID 561328)
-- Dependencies: 225 3989 226
-- Name: fk5e56b38a50004fc; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_anomalies
    ADD CONSTRAINT fk5e56b38a50004fc FOREIGN KEY (hydrant) REFERENCES hydrant(id);


--
-- TOC entry 4167 (class 2606 OID 561333)
-- Dependencies: 216 250 3976
-- Name: fk6c6e2d2236f0130a; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY rci_document
    ADD CONSTRAINT fk6c6e2d2236f0130a FOREIGN KEY (document) REFERENCES document(id) ON DELETE CASCADE;


--
-- TOC entry 4168 (class 2606 OID 561338)
-- Dependencies: 4020 249 250
-- Name: fk6c6e2d224556cb5c; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY rci_document
    ADD CONSTRAINT fk6c6e2d224556cb5c FOREIGN KEY (rci) REFERENCES rci(id) ON DELETE CASCADE;


--
-- TOC entry 4115 (class 2606 OID 561343)
-- Dependencies: 253 202 4024
-- Name: fk72bdedf72a91e8f3; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY alerte_elt
    ADD CONSTRAINT fk72bdedf72a91e8f3 FOREIGN KEY (sous_type_alerte_elt) REFERENCES sous_type_alerte_elt(id);


--
-- TOC entry 4116 (class 2606 OID 561348)
-- Dependencies: 202 3951 199
-- Name: fk72bdedf7d653dae6; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY alerte_elt
    ADD CONSTRAINT fk72bdedf7d653dae6 FOREIGN KEY (alerte) REFERENCES alerte(id) ON DELETE CASCADE;


--
-- TOC entry 4107 (class 2606 OID 561353)
-- Dependencies: 244 4012 187
-- Name: fk805999d374ebaf33; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY organisme
    ADD CONSTRAINT fk805999d374ebaf33 FOREIGN KEY (profil_organisme) REFERENCES profil_organisme(id);


--
-- TOC entry 4108 (class 2606 OID 561358)
-- Dependencies: 311 4092 187
-- Name: fk805999d39b5c78a5; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY organisme
    ADD CONSTRAINT fk805999d39b5c78a5 FOREIGN KEY (zone_competence) REFERENCES zone_competence(id);


--
-- TOC entry 4109 (class 2606 OID 561363)
-- Dependencies: 189 187 3940
-- Name: fk805999d3f5378273; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY organisme
    ADD CONSTRAINT fk805999d3f5378273 FOREIGN KEY (type_organisme) REFERENCES type_organisme(id);


--
-- TOC entry 4161 (class 2606 OID 561368)
-- Dependencies: 189 3940 248
-- Name: fk82645600f5378273; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY profil_utilisateur
    ADD CONSTRAINT fk82645600f5378273 FOREIGN KEY (type_organisme) REFERENCES type_organisme(id);


--
-- TOC entry 4169 (class 2606 OID 561373)
-- Dependencies: 253 4040 264
-- Name: fk91f5e3d755054712; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY sous_type_alerte_elt
    ADD CONSTRAINT fk91f5e3d755054712 FOREIGN KEY (type_alerte_elt) REFERENCES type_alerte_elt(id) ON DELETE CASCADE;


--
-- TOC entry 4174 (class 2606 OID 561378)
-- Dependencies: 4048 271 269
-- Name: fk95654598771bfbfe; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_anomalie_nature
    ADD CONSTRAINT fk95654598771bfbfe FOREIGN KEY (anomalie) REFERENCES type_hydrant_anomalie(id) ON DELETE CASCADE;


--
-- TOC entry 4175 (class 2606 OID 561383)
-- Dependencies: 271 4068 288
-- Name: fk95654598d10a0428; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_anomalie_nature
    ADD CONSTRAINT fk95654598d10a0428 FOREIGN KEY (nature) REFERENCES type_hydrant_nature(id);


--
-- TOC entry 4134 (class 2606 OID 561388)
-- Dependencies: 313 225 4095
-- Name: fk_zone_speciale; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk_zone_speciale FOREIGN KEY (zone_speciale) REFERENCES zone_speciale(id);


--
-- TOC entry 4173 (class 2606 OID 561393)
-- Dependencies: 274 4054 269
-- Name: fkaafafc6efd3ae2e2; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_anomalie
    ADD CONSTRAINT fkaafafc6efd3ae2e2 FOREIGN KEY (critere) REFERENCES type_hydrant_critere(id);


--
-- TOC entry 4112 (class 2606 OID 561398)
-- Dependencies: 194 199 3948
-- Name: fkaba7a2891e30c88f; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY alerte
    ADD CONSTRAINT fkaba7a2891e30c88f FOREIGN KEY (rapporteur) REFERENCES utilisateur(id);


--
-- TOC entry 4171 (class 2606 OID 561403)
-- Dependencies: 260 3948 194
-- Name: fkbc6300366f3f65fb; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY tournee
    ADD CONSTRAINT fkbc6300366f3f65fb FOREIGN KEY (reservation) REFERENCES utilisateur(id);


--
-- TOC entry 4172 (class 2606 OID 561408)
-- Dependencies: 187 3936 260
-- Name: fkbc630036dbf82b2f; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY tournee
    ADD CONSTRAINT fkbc630036dbf82b2f FOREIGN KEY (affectation) REFERENCES organisme(id);


--
-- TOC entry 4150 (class 2606 OID 561413)
-- Dependencies: 3944 237 191
-- Name: fkc4e3841a60bac826; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY permis
    ADD CONSTRAINT fkc4e3841a60bac826 FOREIGN KEY (avis) REFERENCES type_permis_avis(id);


--
-- TOC entry 4151 (class 2606 OID 561418)
-- Dependencies: 237 189 3940
-- Name: fkc4e3841aad903b23; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY permis
    ADD CONSTRAINT fkc4e3841aad903b23 FOREIGN KEY (service_instructeur) REFERENCES type_organisme(id);


--
-- TOC entry 4152 (class 2606 OID 561423)
-- Dependencies: 237 182 3930
-- Name: fkc4e3841ad2da796c; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY permis
    ADD CONSTRAINT fkc4e3841ad2da796c FOREIGN KEY (commune) REFERENCES commune(id);


--
-- TOC entry 4153 (class 2606 OID 561428)
-- Dependencies: 3948 194 237
-- Name: fkc4e3841ade630077; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY permis
    ADD CONSTRAINT fkc4e3841ade630077 FOREIGN KEY (instructeur) REFERENCES utilisateur(id);


--
-- TOC entry 4154 (class 2606 OID 561433)
-- Dependencies: 4078 297 237
-- Name: fkc4e3841ae451849a; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY permis
    ADD CONSTRAINT fkc4e3841ae451849a FOREIGN KEY (interservice) REFERENCES type_permis_interservice(id);


--
-- TOC entry 4157 (class 2606 OID 561438)
-- Dependencies: 189 3940 244
-- Name: fkcad83790f5378273; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY profil_organisme
    ADD CONSTRAINT fkcad83790f5378273 FOREIGN KEY (type_organisme) REFERENCES type_organisme(id);


--
-- TOC entry 4182 (class 2606 OID 561443)
-- Dependencies: 4086 306 303
-- Name: fkcaf3ab04d230270a; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_rci_prom_categorie
    ADD CONSTRAINT fkcaf3ab04d230270a FOREIGN KEY (partition) REFERENCES type_rci_prom_partition(id);


--
-- TOC entry 4119 (class 2606 OID 561448)
-- Dependencies: 216 3976 207
-- Name: fkcd6c849c36f0130a; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY bloc_document
    ADD CONSTRAINT fkcd6c849c36f0130a FOREIGN KEY (document) REFERENCES document(id) ON DELETE CASCADE;


--
-- TOC entry 4158 (class 2606 OID 561453)
-- Dependencies: 248 4018 246
-- Name: fkcdd9b3e12343353; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY profil_organisme_utilisateur_droit
    ADD CONSTRAINT fkcdd9b3e12343353 FOREIGN KEY (profil_utilisateur) REFERENCES profil_utilisateur(id);


--
-- TOC entry 4159 (class 2606 OID 561458)
-- Dependencies: 246 4010 242
-- Name: fkcdd9b3e13723a725; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY profil_organisme_utilisateur_droit
    ADD CONSTRAINT fkcdd9b3e13723a725 FOREIGN KEY (profil_droit) REFERENCES profil_droit(id);


--
-- TOC entry 4160 (class 2606 OID 561463)
-- Dependencies: 246 4012 244
-- Name: fkcdd9b3e174ebaf33; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY profil_organisme_utilisateur_droit
    ADD CONSTRAINT fkcdd9b3e174ebaf33 FOREIGN KEY (profil_organisme) REFERENCES profil_organisme(id);


--
-- TOC entry 4139 (class 2606 OID 561468)
-- Dependencies: 230 4070 290
-- Name: fkd60e141f7dc71cd6; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_pena
    ADD CONSTRAINT fkd60e141f7dc71cd6 FOREIGN KEY (positionnement) REFERENCES type_hydrant_positionnement(id);


--
-- TOC entry 4140 (class 2606 OID 561473)
-- Dependencies: 230 4074 294
-- Name: fkd60e141f8ac9c5e3; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_pena
    ADD CONSTRAINT fkd60e141f8ac9c5e3 FOREIGN KEY (vol_constate) REFERENCES type_hydrant_vol_constate(id);


--
-- TOC entry 4141 (class 2606 OID 561478)
-- Dependencies: 225 230 3989
-- Name: fkd60e141fb34721ef; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_pena
    ADD CONSTRAINT fkd60e141fb34721ef FOREIGN KEY (id) REFERENCES hydrant(id);


--
-- TOC entry 4142 (class 2606 OID 561483)
-- Dependencies: 284 4064 230
-- Name: fkd60e141fe51486ba; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_pena
    ADD CONSTRAINT fkd60e141fe51486ba FOREIGN KEY (materiau) REFERENCES type_hydrant_materiau(id);


--
-- TOC entry 4143 (class 2606 OID 561488)
-- Dependencies: 276 231 4056
-- Name: fkd60e21b7a5a0e880; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_pibi
    ADD CONSTRAINT fkd60e21b7a5a0e880 FOREIGN KEY (diametre) REFERENCES type_hydrant_diametre(id);


--
-- TOC entry 4144 (class 2606 OID 561493)
-- Dependencies: 231 225 3989
-- Name: fkd60e21b7b34721ef; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_pibi
    ADD CONSTRAINT fkd60e21b7b34721ef FOREIGN KEY (id) REFERENCES hydrant(id);


--
-- TOC entry 4145 (class 2606 OID 561498)
-- Dependencies: 282 4062 231
-- Name: fkd60e21b7cd9e6420; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_pibi
    ADD CONSTRAINT fkd60e21b7cd9e6420 FOREIGN KEY (marque) REFERENCES type_hydrant_marque(id);


--
-- TOC entry 4146 (class 2606 OID 561503)
-- Dependencies: 286 4066 231
-- Name: fkd60e21b7cf1bdf92; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_pibi
    ADD CONSTRAINT fkd60e21b7cf1bdf92 FOREIGN KEY (modele) REFERENCES type_hydrant_modele(id);


--
-- TOC entry 4147 (class 2606 OID 561508)
-- Dependencies: 231 3995 230
-- Name: fkd60e21b7dda2e3c4; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_pibi
    ADD CONSTRAINT fkd60e21b7dda2e3c4 FOREIGN KEY (pena) REFERENCES hydrant_pena(id);


--
-- TOC entry 4176 (class 2606 OID 561513)
-- Dependencies: 271 273 4050
-- Name: fkd67208386bceb98b; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_anomalie_nature_saisies
    ADD CONSTRAINT fkd67208386bceb98b FOREIGN KEY (type_hydrant_anomalie_nature) REFERENCES type_hydrant_anomalie_nature(id);


--
-- TOC entry 4177 (class 2606 OID 561518)
-- Dependencies: 273 292 4072
-- Name: fkd6720838873aedcd; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_anomalie_nature_saisies
    ADD CONSTRAINT fkd6720838873aedcd FOREIGN KEY (saisies) REFERENCES type_hydrant_saisie(id);


--
-- TOC entry 4148 (class 2606 OID 561523)
-- Dependencies: 232 3936 187
-- Name: fkdc1ab241374add52; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY hydrant_prescrit
    ADD CONSTRAINT fkdc1ab241374add52 FOREIGN KEY (organisme) REFERENCES organisme(id);


--
-- TOC entry 4110 (class 2606 OID 561528)
-- Dependencies: 194 248 4018
-- Name: fkdd1633832343353; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY utilisateur
    ADD CONSTRAINT fkdd1633832343353 FOREIGN KEY (profil_utilisateur) REFERENCES profil_utilisateur(id);


--
-- TOC entry 4111 (class 2606 OID 561533)
-- Dependencies: 187 3936 194
-- Name: fkdd163383374add52; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY utilisateur
    ADD CONSTRAINT fkdd163383374add52 FOREIGN KEY (organisme) REFERENCES organisme(id);


--
-- TOC entry 4117 (class 2606 OID 561538)
-- Dependencies: 202 203 3956
-- Name: fke5c4c39a52320d07; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY alerte_elt_ano
    ADD CONSTRAINT fke5c4c39a52320d07 FOREIGN KEY (alerte_elt) REFERENCES alerte_elt(id) ON DELETE CASCADE;


--
-- TOC entry 4118 (class 2606 OID 561543)
-- Dependencies: 4038 203 262
-- Name: fke5c4c39a5505297c; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY alerte_elt_ano
    ADD CONSTRAINT fke5c4c39a5505297c FOREIGN KEY (type_alerte_ano) REFERENCES type_alerte_ano(id);


--
-- TOC entry 4113 (class 2606 OID 561548)
-- Dependencies: 3976 216 200
-- Name: fkedde2a1136f0130a; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY alerte_document
    ADD CONSTRAINT fkedde2a1136f0130a FOREIGN KEY (document) REFERENCES document(id) ON DELETE CASCADE;


--
-- TOC entry 4114 (class 2606 OID 561553)
-- Dependencies: 199 200 3951
-- Name: fkedde2a11d653dae6; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY alerte_document
    ADD CONSTRAINT fkedde2a11d653dae6 FOREIGN KEY (alerte) REFERENCES alerte(id) ON DELETE CASCADE;


--
-- TOC entry 4155 (class 2606 OID 561558)
-- Dependencies: 216 238 3976
-- Name: fkf1087ba036f0130a; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY permis_document
    ADD CONSTRAINT fkf1087ba036f0130a FOREIGN KEY (document) REFERENCES document(id) ON DELETE CASCADE;


--
-- TOC entry 4156 (class 2606 OID 561563)
-- Dependencies: 237 4004 238
-- Name: fkf1087ba08cb9e08; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY permis_document
    ADD CONSTRAINT fkf1087ba08cb9e08 FOREIGN KEY (permis) REFERENCES permis(id) ON DELETE CASCADE;


--
-- TOC entry 4178 (class 2606 OID 561568)
-- Dependencies: 276 278 4056
-- Name: fkfd10131c75c4c51c; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_diametre_natures
    ADD CONSTRAINT fkfd10131c75c4c51c FOREIGN KEY (type_hydrant_diametre) REFERENCES type_hydrant_diametre(id);


--
-- TOC entry 4179 (class 2606 OID 561573)
-- Dependencies: 288 278 4068
-- Name: fkfd10131c76d4a02d; Type: FK CONSTRAINT; Schema: remocra; Owner: postgres
--

ALTER TABLE ONLY type_hydrant_diametre_natures
    ADD CONSTRAINT fkfd10131c76d4a02d FOREIGN KEY (natures) REFERENCES type_hydrant_nature(id);


--
-- TOC entry 4197 (class 0 OID 0)
-- Dependencies: 26
-- Name: pdi; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA pdi FROM PUBLIC;
REVOKE ALL ON SCHEMA pdi FROM postgres;
GRANT ALL ON SCHEMA pdi TO postgres;
GRANT USAGE ON SCHEMA pdi TO remocra;


--
-- TOC entry 4198 (class 0 OID 0)
-- Dependencies: 28
-- Name: remocra; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA remocra FROM PUBLIC;
REVOKE ALL ON SCHEMA remocra FROM postgres;
GRANT ALL ON SCHEMA remocra TO postgres;
GRANT USAGE ON SCHEMA remocra TO remocra;


--
-- TOC entry 4199 (class 0 OID 0)
-- Dependencies: 39
-- Name: tracabilite; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA tracabilite FROM PUBLIC;
REVOKE ALL ON SCHEMA tracabilite FROM postgres;
GRANT ALL ON SCHEMA tracabilite TO postgres;
GRANT USAGE ON SCHEMA tracabilite TO remocra;


SET search_path = pdi, pg_catalog;

--
-- TOC entry 4200 (class 0 OID 0)
-- Dependencies: 174
-- Name: modele_message; Type: ACL; Schema: pdi; Owner: postgres
--

REVOKE ALL ON TABLE modele_message FROM PUBLIC;
REVOKE ALL ON TABLE modele_message FROM postgres;
GRANT ALL ON TABLE modele_message TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE modele_message TO remocra;


--
-- TOC entry 4201 (class 0 OID 0)
-- Dependencies: 175
-- Name: modele_traitement; Type: ACL; Schema: pdi; Owner: postgres
--

REVOKE ALL ON TABLE modele_traitement FROM PUBLIC;
REVOKE ALL ON TABLE modele_traitement FROM postgres;
GRANT ALL ON TABLE modele_traitement TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE modele_traitement TO remocra;


--
-- TOC entry 4202 (class 0 OID 0)
-- Dependencies: 176
-- Name: modele_traitement_parametre; Type: ACL; Schema: pdi; Owner: postgres
--

REVOKE ALL ON TABLE modele_traitement_parametre FROM PUBLIC;
REVOKE ALL ON TABLE modele_traitement_parametre FROM postgres;
GRANT ALL ON TABLE modele_traitement_parametre TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE modele_traitement_parametre TO remocra;


--
-- TOC entry 4203 (class 0 OID 0)
-- Dependencies: 177
-- Name: statut; Type: ACL; Schema: pdi; Owner: postgres
--

REVOKE ALL ON TABLE statut FROM PUBLIC;
REVOKE ALL ON TABLE statut FROM postgres;
GRANT ALL ON TABLE statut TO postgres;
GRANT SELECT ON TABLE statut TO remocra;


--
-- TOC entry 4204 (class 0 OID 0)
-- Dependencies: 178
-- Name: traitement_idtraitement_seq; Type: ACL; Schema: pdi; Owner: postgres
--

REVOKE ALL ON SEQUENCE traitement_idtraitement_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE traitement_idtraitement_seq FROM postgres;
GRANT ALL ON SEQUENCE traitement_idtraitement_seq TO postgres;
GRANT SELECT ON SEQUENCE traitement_idtraitement_seq TO remocra;


--
-- TOC entry 4205 (class 0 OID 0)
-- Dependencies: 179
-- Name: traitement; Type: ACL; Schema: pdi; Owner: postgres
--

REVOKE ALL ON TABLE traitement FROM PUBLIC;
REVOKE ALL ON TABLE traitement FROM postgres;
GRANT ALL ON TABLE traitement TO postgres;
GRANT SELECT ON TABLE traitement TO remocra;


--
-- TOC entry 4206 (class 0 OID 0)
-- Dependencies: 180
-- Name: traitement_cc; Type: ACL; Schema: pdi; Owner: postgres
--

REVOKE ALL ON TABLE traitement_cc FROM PUBLIC;
REVOKE ALL ON TABLE traitement_cc FROM postgres;
GRANT ALL ON TABLE traitement_cc TO postgres;
GRANT SELECT ON TABLE traitement_cc TO remocra;


--
-- TOC entry 4207 (class 0 OID 0)
-- Dependencies: 181
-- Name: traitement_parametre; Type: ACL; Schema: pdi; Owner: postgres
--

REVOKE ALL ON TABLE traitement_parametre FROM PUBLIC;
REVOKE ALL ON TABLE traitement_parametre FROM postgres;
GRANT ALL ON TABLE traitement_parametre TO postgres;
GRANT SELECT ON TABLE traitement_parametre TO remocra;


SET search_path = remocra, pg_catalog;

--
-- TOC entry 4213 (class 0 OID 0)
-- Dependencies: 182
-- Name: commune; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE commune FROM PUBLIC;
REVOKE ALL ON TABLE commune FROM postgres;
GRANT ALL ON TABLE commune TO postgres;
GRANT SELECT,UPDATE ON TABLE commune TO remocra;


--
-- TOC entry 4214 (class 0 OID 0)
-- Dependencies: 183
-- Name: param_conf; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE param_conf FROM PUBLIC;
REVOKE ALL ON TABLE param_conf FROM postgres;
GRANT ALL ON TABLE param_conf TO postgres;
GRANT SELECT ON TABLE param_conf TO remocra;


--
-- TOC entry 4215 (class 0 OID 0)
-- Dependencies: 185
-- Name: type_rci_prom_famille; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_rci_prom_famille FROM PUBLIC;
REVOKE ALL ON TABLE type_rci_prom_famille FROM postgres;
GRANT ALL ON TABLE type_rci_prom_famille TO postgres;
GRANT SELECT ON TABLE type_rci_prom_famille TO remocra;


--
-- TOC entry 4216 (class 0 OID 0)
-- Dependencies: 187
-- Name: organisme; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE organisme FROM PUBLIC;
REVOKE ALL ON TABLE organisme FROM postgres;
GRANT ALL ON TABLE organisme TO postgres;
GRANT SELECT ON TABLE organisme TO remocra;


--
-- TOC entry 4217 (class 0 OID 0)
-- Dependencies: 188
-- Name: type_organisme_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_organisme_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_organisme_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_organisme_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_organisme_id_seq TO remocra;


--
-- TOC entry 4218 (class 0 OID 0)
-- Dependencies: 189
-- Name: type_organisme; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_organisme FROM PUBLIC;
REVOKE ALL ON TABLE type_organisme FROM postgres;
GRANT ALL ON TABLE type_organisme TO postgres;
GRANT SELECT ON TABLE type_organisme TO remocra;


--
-- TOC entry 4219 (class 0 OID 0)
-- Dependencies: 191
-- Name: type_permis_avis; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_permis_avis FROM PUBLIC;
REVOKE ALL ON TABLE type_permis_avis FROM postgres;
GRANT ALL ON TABLE type_permis_avis TO postgres;
GRANT SELECT ON TABLE type_permis_avis TO remocra;


--
-- TOC entry 4220 (class 0 OID 0)
-- Dependencies: 194
-- Name: utilisateur; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE utilisateur FROM PUBLIC;
REVOKE ALL ON TABLE utilisateur FROM postgres;
GRANT ALL ON TABLE utilisateur TO postgres;
GRANT SELECT,UPDATE ON TABLE utilisateur TO remocra;


--
-- TOC entry 4221 (class 0 OID 0)
-- Dependencies: 199
-- Name: alerte; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE alerte FROM PUBLIC;
REVOKE ALL ON TABLE alerte FROM postgres;
GRANT ALL ON TABLE alerte TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE alerte TO remocra;


--
-- TOC entry 4222 (class 0 OID 0)
-- Dependencies: 200
-- Name: alerte_document; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE alerte_document FROM PUBLIC;
REVOKE ALL ON TABLE alerte_document FROM postgres;
GRANT ALL ON TABLE alerte_document TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE alerte_document TO remocra;


--
-- TOC entry 4224 (class 0 OID 0)
-- Dependencies: 201
-- Name: alerte_document_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE alerte_document_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE alerte_document_id_seq FROM postgres;
GRANT ALL ON SEQUENCE alerte_document_id_seq TO postgres;
GRANT ALL ON SEQUENCE alerte_document_id_seq TO remocra;


--
-- TOC entry 4225 (class 0 OID 0)
-- Dependencies: 202
-- Name: alerte_elt; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE alerte_elt FROM PUBLIC;
REVOKE ALL ON TABLE alerte_elt FROM postgres;
GRANT ALL ON TABLE alerte_elt TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE alerte_elt TO remocra;


--
-- TOC entry 4226 (class 0 OID 0)
-- Dependencies: 203
-- Name: alerte_elt_ano; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE alerte_elt_ano FROM PUBLIC;
REVOKE ALL ON TABLE alerte_elt_ano FROM postgres;
GRANT ALL ON TABLE alerte_elt_ano TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE alerte_elt_ano TO remocra;


--
-- TOC entry 4228 (class 0 OID 0)
-- Dependencies: 204
-- Name: alerte_elt_ano_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE alerte_elt_ano_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE alerte_elt_ano_id_seq FROM postgres;
GRANT ALL ON SEQUENCE alerte_elt_ano_id_seq TO postgres;
GRANT ALL ON SEQUENCE alerte_elt_ano_id_seq TO remocra;


--
-- TOC entry 4230 (class 0 OID 0)
-- Dependencies: 205
-- Name: alerte_elt_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE alerte_elt_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE alerte_elt_id_seq FROM postgres;
GRANT ALL ON SEQUENCE alerte_elt_id_seq TO postgres;
GRANT ALL ON SEQUENCE alerte_elt_id_seq TO remocra;


--
-- TOC entry 4232 (class 0 OID 0)
-- Dependencies: 206
-- Name: alerte_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE alerte_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE alerte_id_seq FROM postgres;
GRANT ALL ON SEQUENCE alerte_id_seq TO postgres;
GRANT ALL ON SEQUENCE alerte_id_seq TO remocra;


--
-- TOC entry 4233 (class 0 OID 0)
-- Dependencies: 207
-- Name: bloc_document; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE bloc_document FROM PUBLIC;
REVOKE ALL ON TABLE bloc_document FROM postgres;
GRANT ALL ON TABLE bloc_document TO postgres;
GRANT SELECT ON TABLE bloc_document TO remocra;


--
-- TOC entry 4235 (class 0 OID 0)
-- Dependencies: 208
-- Name: bloc_document_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE bloc_document_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE bloc_document_id_seq FROM postgres;
GRANT ALL ON SEQUENCE bloc_document_id_seq TO postgres;
GRANT SELECT ON SEQUENCE bloc_document_id_seq TO remocra;


--
-- TOC entry 4236 (class 0 OID 0)
-- Dependencies: 209
-- Name: bloc_document_profil_droits; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE bloc_document_profil_droits FROM PUBLIC;
REVOKE ALL ON TABLE bloc_document_profil_droits FROM postgres;
GRANT ALL ON TABLE bloc_document_profil_droits TO postgres;
GRANT SELECT ON TABLE bloc_document_profil_droits TO remocra;


--
-- TOC entry 4237 (class 0 OID 0)
-- Dependencies: 210
-- Name: bloc_document_thematiques; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE bloc_document_thematiques FROM PUBLIC;
REVOKE ALL ON TABLE bloc_document_thematiques FROM postgres;
GRANT ALL ON TABLE bloc_document_thematiques TO postgres;
GRANT SELECT ON TABLE bloc_document_thematiques TO remocra;


--
-- TOC entry 4239 (class 0 OID 0)
-- Dependencies: 211
-- Name: commune_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE commune_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE commune_id_seq FROM postgres;
GRANT ALL ON SEQUENCE commune_id_seq TO postgres;
GRANT SELECT ON SEQUENCE commune_id_seq TO remocra;


--
-- TOC entry 4240 (class 0 OID 0)
-- Dependencies: 212
-- Name: dde_mdp; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE dde_mdp FROM PUBLIC;
REVOKE ALL ON TABLE dde_mdp FROM postgres;
GRANT ALL ON TABLE dde_mdp TO postgres;
GRANT SELECT ON TABLE dde_mdp TO remocra;


--
-- TOC entry 4242 (class 0 OID 0)
-- Dependencies: 213
-- Name: dde_mdp_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE dde_mdp_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE dde_mdp_id_seq FROM postgres;
GRANT ALL ON SEQUENCE dde_mdp_id_seq TO postgres;
GRANT SELECT ON SEQUENCE dde_mdp_id_seq TO remocra;


--
-- TOC entry 4243 (class 0 OID 0)
-- Dependencies: 214
-- Name: depot_document; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE depot_document FROM PUBLIC;
REVOKE ALL ON TABLE depot_document FROM postgres;
GRANT ALL ON TABLE depot_document TO postgres;
GRANT SELECT ON TABLE depot_document TO remocra;


--
-- TOC entry 4245 (class 0 OID 0)
-- Dependencies: 215
-- Name: depot_document_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE depot_document_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE depot_document_id_seq FROM postgres;
GRANT ALL ON SEQUENCE depot_document_id_seq TO postgres;
GRANT SELECT ON SEQUENCE depot_document_id_seq TO remocra;


--
-- TOC entry 4246 (class 0 OID 0)
-- Dependencies: 216
-- Name: document; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE document FROM PUBLIC;
REVOKE ALL ON TABLE document FROM postgres;
GRANT ALL ON TABLE document TO postgres;
GRANT SELECT ON TABLE document TO remocra;


--
-- TOC entry 4248 (class 0 OID 0)
-- Dependencies: 217
-- Name: document_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE document_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE document_id_seq FROM postgres;
GRANT ALL ON SEQUENCE document_id_seq TO postgres;
GRANT SELECT ON SEQUENCE document_id_seq TO remocra;


--
-- TOC entry 4249 (class 0 OID 0)
-- Dependencies: 218
-- Name: droit_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE droit_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE droit_id_seq FROM postgres;
GRANT ALL ON SEQUENCE droit_id_seq TO postgres;
GRANT SELECT ON SEQUENCE droit_id_seq TO remocra;


--
-- TOC entry 4250 (class 0 OID 0)
-- Dependencies: 219
-- Name: droit; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE droit FROM PUBLIC;
REVOKE ALL ON TABLE droit FROM postgres;
GRANT ALL ON TABLE droit TO postgres;
GRANT SELECT ON TABLE droit TO remocra;


--
-- TOC entry 4251 (class 0 OID 0)
-- Dependencies: 220
-- Name: email; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE email FROM PUBLIC;
REVOKE ALL ON TABLE email FROM postgres;
GRANT ALL ON TABLE email TO postgres;
GRANT SELECT ON TABLE email TO remocra;


--
-- TOC entry 4253 (class 0 OID 0)
-- Dependencies: 221
-- Name: email_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE email_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE email_id_seq FROM postgres;
GRANT ALL ON SEQUENCE email_id_seq TO postgres;
GRANT SELECT ON SEQUENCE email_id_seq TO remocra;


--
-- TOC entry 4254 (class 0 OID 0)
-- Dependencies: 222
-- Name: email_modele; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE email_modele FROM PUBLIC;
REVOKE ALL ON TABLE email_modele FROM postgres;
GRANT ALL ON TABLE email_modele TO postgres;
GRANT SELECT,UPDATE ON TABLE email_modele TO remocra;


--
-- TOC entry 4256 (class 0 OID 0)
-- Dependencies: 223
-- Name: email_modele_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE email_modele_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE email_modele_id_seq FROM postgres;
GRANT ALL ON SEQUENCE email_modele_id_seq TO postgres;
GRANT SELECT,UPDATE ON SEQUENCE email_modele_id_seq TO remocra;


--
-- TOC entry 4257 (class 0 OID 0)
-- Dependencies: 224
-- Name: hibernate_sequence; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE hibernate_sequence FROM PUBLIC;
REVOKE ALL ON SEQUENCE hibernate_sequence FROM postgres;
GRANT ALL ON SEQUENCE hibernate_sequence TO postgres;
GRANT SELECT ON SEQUENCE hibernate_sequence TO remocra;


--
-- TOC entry 4258 (class 0 OID 0)
-- Dependencies: 225
-- Name: hydrant; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE hydrant FROM PUBLIC;
REVOKE ALL ON TABLE hydrant FROM postgres;
GRANT ALL ON TABLE hydrant TO postgres;
GRANT SELECT,UPDATE ON TABLE hydrant TO remocra;


--
-- TOC entry 4259 (class 0 OID 0)
-- Dependencies: 226
-- Name: hydrant_anomalies; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE hydrant_anomalies FROM PUBLIC;
REVOKE ALL ON TABLE hydrant_anomalies FROM postgres;
GRANT ALL ON TABLE hydrant_anomalies TO postgres;
GRANT SELECT ON TABLE hydrant_anomalies TO remocra;


--
-- TOC entry 4260 (class 0 OID 0)
-- Dependencies: 227
-- Name: hydrant_document; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE hydrant_document FROM PUBLIC;
REVOKE ALL ON TABLE hydrant_document FROM postgres;
GRANT ALL ON TABLE hydrant_document TO postgres;
GRANT SELECT ON TABLE hydrant_document TO remocra;


--
-- TOC entry 4262 (class 0 OID 0)
-- Dependencies: 228
-- Name: hydrant_document_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE hydrant_document_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE hydrant_document_id_seq FROM postgres;
GRANT ALL ON SEQUENCE hydrant_document_id_seq TO postgres;
GRANT SELECT ON SEQUENCE hydrant_document_id_seq TO remocra;


--
-- TOC entry 4264 (class 0 OID 0)
-- Dependencies: 229
-- Name: hydrant_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE hydrant_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE hydrant_id_seq FROM postgres;
GRANT ALL ON SEQUENCE hydrant_id_seq TO postgres;
GRANT SELECT ON SEQUENCE hydrant_id_seq TO remocra;


--
-- TOC entry 4265 (class 0 OID 0)
-- Dependencies: 230
-- Name: hydrant_pena; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE hydrant_pena FROM PUBLIC;
REVOKE ALL ON TABLE hydrant_pena FROM postgres;
GRANT ALL ON TABLE hydrant_pena TO postgres;
GRANT SELECT ON TABLE hydrant_pena TO remocra;


--
-- TOC entry 4266 (class 0 OID 0)
-- Dependencies: 231
-- Name: hydrant_pibi; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE hydrant_pibi FROM PUBLIC;
REVOKE ALL ON TABLE hydrant_pibi FROM postgres;
GRANT ALL ON TABLE hydrant_pibi TO postgres;
GRANT SELECT ON TABLE hydrant_pibi TO remocra;


--
-- TOC entry 4267 (class 0 OID 0)
-- Dependencies: 232
-- Name: hydrant_prescrit; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE hydrant_prescrit FROM PUBLIC;
REVOKE ALL ON TABLE hydrant_prescrit FROM postgres;
GRANT ALL ON TABLE hydrant_prescrit TO postgres;
GRANT SELECT ON TABLE hydrant_prescrit TO remocra;


--
-- TOC entry 4269 (class 0 OID 0)
-- Dependencies: 233
-- Name: hydrant_prescrit_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE hydrant_prescrit_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE hydrant_prescrit_id_seq FROM postgres;
GRANT ALL ON SEQUENCE hydrant_prescrit_id_seq TO postgres;
GRANT SELECT ON SEQUENCE hydrant_prescrit_id_seq TO remocra;


--
-- TOC entry 4270 (class 0 OID 0)
-- Dependencies: 234
-- Name: metadonnee; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE metadonnee FROM PUBLIC;
REVOKE ALL ON TABLE metadonnee FROM postgres;
GRANT ALL ON TABLE metadonnee TO postgres;
GRANT SELECT ON TABLE metadonnee TO remocra;


--
-- TOC entry 4272 (class 0 OID 0)
-- Dependencies: 235
-- Name: metadonnee_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE metadonnee_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE metadonnee_id_seq FROM postgres;
GRANT ALL ON SEQUENCE metadonnee_id_seq TO postgres;
GRANT SELECT ON SEQUENCE metadonnee_id_seq TO remocra;


--
-- TOC entry 4274 (class 0 OID 0)
-- Dependencies: 236
-- Name: organisme_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE organisme_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE organisme_id_seq FROM postgres;
GRANT ALL ON SEQUENCE organisme_id_seq TO postgres;
GRANT SELECT ON SEQUENCE organisme_id_seq TO remocra;


--
-- TOC entry 4275 (class 0 OID 0)
-- Dependencies: 237
-- Name: permis; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE permis FROM PUBLIC;
REVOKE ALL ON TABLE permis FROM postgres;
GRANT ALL ON TABLE permis TO postgres;
GRANT SELECT ON TABLE permis TO remocra;


--
-- TOC entry 4276 (class 0 OID 0)
-- Dependencies: 238
-- Name: permis_document; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE permis_document FROM PUBLIC;
REVOKE ALL ON TABLE permis_document FROM postgres;
GRANT ALL ON TABLE permis_document TO postgres;
GRANT SELECT ON TABLE permis_document TO remocra;


--
-- TOC entry 4278 (class 0 OID 0)
-- Dependencies: 239
-- Name: permis_document_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE permis_document_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE permis_document_id_seq FROM postgres;
GRANT ALL ON SEQUENCE permis_document_id_seq TO postgres;
GRANT SELECT ON SEQUENCE permis_document_id_seq TO remocra;


--
-- TOC entry 4280 (class 0 OID 0)
-- Dependencies: 240
-- Name: permis_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE permis_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE permis_id_seq FROM postgres;
GRANT ALL ON SEQUENCE permis_id_seq TO postgres;
GRANT SELECT ON SEQUENCE permis_id_seq TO remocra;


--
-- TOC entry 4281 (class 0 OID 0)
-- Dependencies: 241
-- Name: profil_droit_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE profil_droit_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE profil_droit_id_seq FROM postgres;
GRANT ALL ON SEQUENCE profil_droit_id_seq TO postgres;
GRANT SELECT ON SEQUENCE profil_droit_id_seq TO remocra;


--
-- TOC entry 4282 (class 0 OID 0)
-- Dependencies: 242
-- Name: profil_droit; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE profil_droit FROM PUBLIC;
REVOKE ALL ON TABLE profil_droit FROM postgres;
GRANT ALL ON TABLE profil_droit TO postgres;
GRANT SELECT ON TABLE profil_droit TO remocra;


--
-- TOC entry 4283 (class 0 OID 0)
-- Dependencies: 243
-- Name: profil_organisme_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE profil_organisme_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE profil_organisme_id_seq FROM postgres;
GRANT ALL ON SEQUENCE profil_organisme_id_seq TO postgres;
GRANT SELECT ON SEQUENCE profil_organisme_id_seq TO remocra;


--
-- TOC entry 4284 (class 0 OID 0)
-- Dependencies: 244
-- Name: profil_organisme; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE profil_organisme FROM PUBLIC;
REVOKE ALL ON TABLE profil_organisme FROM postgres;
GRANT ALL ON TABLE profil_organisme TO postgres;
GRANT SELECT ON TABLE profil_organisme TO remocra;


--
-- TOC entry 4285 (class 0 OID 0)
-- Dependencies: 245
-- Name: profil_organisme_utilisateur_droit_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE profil_organisme_utilisateur_droit_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE profil_organisme_utilisateur_droit_id_seq FROM postgres;
GRANT ALL ON SEQUENCE profil_organisme_utilisateur_droit_id_seq TO postgres;
GRANT SELECT ON SEQUENCE profil_organisme_utilisateur_droit_id_seq TO remocra;


--
-- TOC entry 4286 (class 0 OID 0)
-- Dependencies: 246
-- Name: profil_organisme_utilisateur_droit; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE profil_organisme_utilisateur_droit FROM PUBLIC;
REVOKE ALL ON TABLE profil_organisme_utilisateur_droit FROM postgres;
GRANT ALL ON TABLE profil_organisme_utilisateur_droit TO postgres;
GRANT SELECT ON TABLE profil_organisme_utilisateur_droit TO remocra;


--
-- TOC entry 4287 (class 0 OID 0)
-- Dependencies: 247
-- Name: profil_utilisateur_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE profil_utilisateur_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE profil_utilisateur_id_seq FROM postgres;
GRANT ALL ON SEQUENCE profil_utilisateur_id_seq TO postgres;
GRANT SELECT ON SEQUENCE profil_utilisateur_id_seq TO remocra;


--
-- TOC entry 4288 (class 0 OID 0)
-- Dependencies: 248
-- Name: profil_utilisateur; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE profil_utilisateur FROM PUBLIC;
REVOKE ALL ON TABLE profil_utilisateur FROM postgres;
GRANT ALL ON TABLE profil_utilisateur TO postgres;
GRANT SELECT ON TABLE profil_utilisateur TO remocra;


--
-- TOC entry 4289 (class 0 OID 0)
-- Dependencies: 249
-- Name: rci; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE rci FROM PUBLIC;
REVOKE ALL ON TABLE rci FROM postgres;
GRANT ALL ON TABLE rci TO postgres;
GRANT SELECT ON TABLE rci TO remocra;


--
-- TOC entry 4290 (class 0 OID 0)
-- Dependencies: 250
-- Name: rci_document; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE rci_document FROM PUBLIC;
REVOKE ALL ON TABLE rci_document FROM postgres;
GRANT ALL ON TABLE rci_document TO postgres;
GRANT SELECT ON TABLE rci_document TO remocra;


--
-- TOC entry 4292 (class 0 OID 0)
-- Dependencies: 251
-- Name: rci_document_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE rci_document_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE rci_document_id_seq FROM postgres;
GRANT ALL ON SEQUENCE rci_document_id_seq TO postgres;
GRANT SELECT ON SEQUENCE rci_document_id_seq TO remocra;


--
-- TOC entry 4294 (class 0 OID 0)
-- Dependencies: 252
-- Name: rci_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE rci_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE rci_id_seq FROM postgres;
GRANT ALL ON SEQUENCE rci_id_seq TO postgres;
GRANT SELECT ON SEQUENCE rci_id_seq TO remocra;


--
-- TOC entry 4295 (class 0 OID 0)
-- Dependencies: 253
-- Name: sous_type_alerte_elt; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE sous_type_alerte_elt FROM PUBLIC;
REVOKE ALL ON TABLE sous_type_alerte_elt FROM postgres;
GRANT ALL ON TABLE sous_type_alerte_elt TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE sous_type_alerte_elt TO remocra;


--
-- TOC entry 4297 (class 0 OID 0)
-- Dependencies: 254
-- Name: sous_type_alerte_elt_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE sous_type_alerte_elt_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE sous_type_alerte_elt_id_seq FROM postgres;
GRANT ALL ON SEQUENCE sous_type_alerte_elt_id_seq TO postgres;
GRANT ALL ON SEQUENCE sous_type_alerte_elt_id_seq TO remocra;


--
-- TOC entry 4298 (class 0 OID 0)
-- Dependencies: 255
-- Name: suivi_patches; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE suivi_patches FROM PUBLIC;
REVOKE ALL ON TABLE suivi_patches FROM postgres;
GRANT ALL ON TABLE suivi_patches TO postgres;
GRANT SELECT ON TABLE suivi_patches TO remocra;


--
-- TOC entry 4299 (class 0 OID 0)
-- Dependencies: 256
-- Name: synchronisation; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE synchronisation FROM PUBLIC;
REVOKE ALL ON TABLE synchronisation FROM postgres;
GRANT ALL ON TABLE synchronisation TO postgres;
GRANT SELECT ON TABLE synchronisation TO remocra;


--
-- TOC entry 4301 (class 0 OID 0)
-- Dependencies: 257
-- Name: synchronisation_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE synchronisation_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE synchronisation_id_seq FROM postgres;
GRANT ALL ON SEQUENCE synchronisation_id_seq TO postgres;
GRANT SELECT ON SEQUENCE synchronisation_id_seq TO remocra;


--
-- TOC entry 4302 (class 0 OID 0)
-- Dependencies: 258
-- Name: thematique; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE thematique FROM PUBLIC;
REVOKE ALL ON TABLE thematique FROM postgres;
GRANT ALL ON TABLE thematique TO postgres;
GRANT SELECT ON TABLE thematique TO remocra;


--
-- TOC entry 4304 (class 0 OID 0)
-- Dependencies: 259
-- Name: thematique_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE thematique_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE thematique_id_seq FROM postgres;
GRANT ALL ON SEQUENCE thematique_id_seq TO postgres;
GRANT SELECT ON SEQUENCE thematique_id_seq TO remocra;


--
-- TOC entry 4305 (class 0 OID 0)
-- Dependencies: 260
-- Name: tournee; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE tournee FROM PUBLIC;
REVOKE ALL ON TABLE tournee FROM postgres;
GRANT ALL ON TABLE tournee TO postgres;
GRANT SELECT,UPDATE ON TABLE tournee TO remocra;


--
-- TOC entry 4307 (class 0 OID 0)
-- Dependencies: 261
-- Name: tournee_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE tournee_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE tournee_id_seq FROM postgres;
GRANT ALL ON SEQUENCE tournee_id_seq TO postgres;
GRANT SELECT ON SEQUENCE tournee_id_seq TO remocra;


--
-- TOC entry 4308 (class 0 OID 0)
-- Dependencies: 262
-- Name: type_alerte_ano; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_alerte_ano FROM PUBLIC;
REVOKE ALL ON TABLE type_alerte_ano FROM postgres;
GRANT ALL ON TABLE type_alerte_ano TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE type_alerte_ano TO remocra;


--
-- TOC entry 4310 (class 0 OID 0)
-- Dependencies: 263
-- Name: type_alerte_ano_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_alerte_ano_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_alerte_ano_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_alerte_ano_id_seq TO postgres;
GRANT ALL ON SEQUENCE type_alerte_ano_id_seq TO remocra;


--
-- TOC entry 4311 (class 0 OID 0)
-- Dependencies: 264
-- Name: type_alerte_elt; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_alerte_elt FROM PUBLIC;
REVOKE ALL ON TABLE type_alerte_elt FROM postgres;
GRANT ALL ON TABLE type_alerte_elt TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE type_alerte_elt TO remocra;


--
-- TOC entry 4313 (class 0 OID 0)
-- Dependencies: 265
-- Name: type_alerte_elt_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_alerte_elt_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_alerte_elt_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_alerte_elt_id_seq TO postgres;
GRANT ALL ON SEQUENCE type_alerte_elt_id_seq TO remocra;


--
-- TOC entry 4314 (class 0 OID 0)
-- Dependencies: 266
-- Name: type_droit_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_droit_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_droit_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_droit_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_droit_id_seq TO remocra;


--
-- TOC entry 4315 (class 0 OID 0)
-- Dependencies: 267
-- Name: type_droit; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_droit FROM PUBLIC;
REVOKE ALL ON TABLE type_droit FROM postgres;
GRANT ALL ON TABLE type_droit TO postgres;
GRANT SELECT ON TABLE type_droit TO remocra;


--
-- TOC entry 4316 (class 0 OID 0)
-- Dependencies: 268
-- Name: type_hydrant; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_hydrant FROM PUBLIC;
REVOKE ALL ON TABLE type_hydrant FROM postgres;
GRANT ALL ON TABLE type_hydrant TO postgres;
GRANT SELECT ON TABLE type_hydrant TO remocra;


--
-- TOC entry 4317 (class 0 OID 0)
-- Dependencies: 269
-- Name: type_hydrant_anomalie; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_hydrant_anomalie FROM PUBLIC;
REVOKE ALL ON TABLE type_hydrant_anomalie FROM postgres;
GRANT ALL ON TABLE type_hydrant_anomalie TO postgres;
GRANT SELECT ON TABLE type_hydrant_anomalie TO remocra;


--
-- TOC entry 4319 (class 0 OID 0)
-- Dependencies: 270
-- Name: type_hydrant_anomalie_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_hydrant_anomalie_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_hydrant_anomalie_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_hydrant_anomalie_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_hydrant_anomalie_id_seq TO remocra;


--
-- TOC entry 4320 (class 0 OID 0)
-- Dependencies: 271
-- Name: type_hydrant_anomalie_nature; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_hydrant_anomalie_nature FROM PUBLIC;
REVOKE ALL ON TABLE type_hydrant_anomalie_nature FROM postgres;
GRANT ALL ON TABLE type_hydrant_anomalie_nature TO postgres;
GRANT SELECT ON TABLE type_hydrant_anomalie_nature TO remocra;


--
-- TOC entry 4322 (class 0 OID 0)
-- Dependencies: 272
-- Name: type_hydrant_anomalie_nature_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_hydrant_anomalie_nature_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_hydrant_anomalie_nature_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_hydrant_anomalie_nature_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_hydrant_anomalie_nature_id_seq TO remocra;


--
-- TOC entry 4323 (class 0 OID 0)
-- Dependencies: 273
-- Name: type_hydrant_anomalie_nature_saisies; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_hydrant_anomalie_nature_saisies FROM PUBLIC;
REVOKE ALL ON TABLE type_hydrant_anomalie_nature_saisies FROM postgres;
GRANT ALL ON TABLE type_hydrant_anomalie_nature_saisies TO postgres;
GRANT SELECT ON TABLE type_hydrant_anomalie_nature_saisies TO remocra;


--
-- TOC entry 4324 (class 0 OID 0)
-- Dependencies: 274
-- Name: type_hydrant_critere; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_hydrant_critere FROM PUBLIC;
REVOKE ALL ON TABLE type_hydrant_critere FROM postgres;
GRANT ALL ON TABLE type_hydrant_critere TO postgres;
GRANT SELECT ON TABLE type_hydrant_critere TO remocra;


--
-- TOC entry 4326 (class 0 OID 0)
-- Dependencies: 275
-- Name: type_hydrant_critere_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_hydrant_critere_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_hydrant_critere_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_hydrant_critere_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_hydrant_critere_id_seq TO remocra;


--
-- TOC entry 4327 (class 0 OID 0)
-- Dependencies: 276
-- Name: type_hydrant_diametre; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_hydrant_diametre FROM PUBLIC;
REVOKE ALL ON TABLE type_hydrant_diametre FROM postgres;
GRANT ALL ON TABLE type_hydrant_diametre TO postgres;
GRANT SELECT ON TABLE type_hydrant_diametre TO remocra;


--
-- TOC entry 4329 (class 0 OID 0)
-- Dependencies: 277
-- Name: type_hydrant_diametre_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_hydrant_diametre_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_hydrant_diametre_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_hydrant_diametre_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_hydrant_diametre_id_seq TO remocra;


--
-- TOC entry 4330 (class 0 OID 0)
-- Dependencies: 278
-- Name: type_hydrant_diametre_natures; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_hydrant_diametre_natures FROM PUBLIC;
REVOKE ALL ON TABLE type_hydrant_diametre_natures FROM postgres;
GRANT ALL ON TABLE type_hydrant_diametre_natures TO postgres;
GRANT SELECT ON TABLE type_hydrant_diametre_natures TO remocra;


--
-- TOC entry 4331 (class 0 OID 0)
-- Dependencies: 279
-- Name: type_hydrant_domaine; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_hydrant_domaine FROM PUBLIC;
REVOKE ALL ON TABLE type_hydrant_domaine FROM postgres;
GRANT ALL ON TABLE type_hydrant_domaine TO postgres;
GRANT SELECT ON TABLE type_hydrant_domaine TO remocra;


--
-- TOC entry 4333 (class 0 OID 0)
-- Dependencies: 280
-- Name: type_hydrant_domaine_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_hydrant_domaine_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_hydrant_domaine_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_hydrant_domaine_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_hydrant_domaine_id_seq TO remocra;


--
-- TOC entry 4335 (class 0 OID 0)
-- Dependencies: 281
-- Name: type_hydrant_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_hydrant_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_hydrant_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_hydrant_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_hydrant_id_seq TO remocra;


--
-- TOC entry 4336 (class 0 OID 0)
-- Dependencies: 282
-- Name: type_hydrant_marque; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_hydrant_marque FROM PUBLIC;
REVOKE ALL ON TABLE type_hydrant_marque FROM postgres;
GRANT ALL ON TABLE type_hydrant_marque TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE type_hydrant_marque TO remocra;


--
-- TOC entry 4338 (class 0 OID 0)
-- Dependencies: 283
-- Name: type_hydrant_marque_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_hydrant_marque_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_hydrant_marque_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_hydrant_marque_id_seq TO postgres;
GRANT ALL ON SEQUENCE type_hydrant_marque_id_seq TO remocra;


--
-- TOC entry 4339 (class 0 OID 0)
-- Dependencies: 284
-- Name: type_hydrant_materiau; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_hydrant_materiau FROM PUBLIC;
REVOKE ALL ON TABLE type_hydrant_materiau FROM postgres;
GRANT ALL ON TABLE type_hydrant_materiau TO postgres;
GRANT SELECT ON TABLE type_hydrant_materiau TO remocra;


--
-- TOC entry 4341 (class 0 OID 0)
-- Dependencies: 285
-- Name: type_hydrant_materiau_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_hydrant_materiau_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_hydrant_materiau_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_hydrant_materiau_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_hydrant_materiau_id_seq TO remocra;


--
-- TOC entry 4342 (class 0 OID 0)
-- Dependencies: 286
-- Name: type_hydrant_modele; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_hydrant_modele FROM PUBLIC;
REVOKE ALL ON TABLE type_hydrant_modele FROM postgres;
GRANT ALL ON TABLE type_hydrant_modele TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE type_hydrant_modele TO remocra;


--
-- TOC entry 4344 (class 0 OID 0)
-- Dependencies: 287
-- Name: type_hydrant_modele_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_hydrant_modele_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_hydrant_modele_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_hydrant_modele_id_seq TO postgres;
GRANT ALL ON SEQUENCE type_hydrant_modele_id_seq TO remocra;


--
-- TOC entry 4345 (class 0 OID 0)
-- Dependencies: 288
-- Name: type_hydrant_nature; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_hydrant_nature FROM PUBLIC;
REVOKE ALL ON TABLE type_hydrant_nature FROM postgres;
GRANT ALL ON TABLE type_hydrant_nature TO postgres;
GRANT SELECT ON TABLE type_hydrant_nature TO remocra;


--
-- TOC entry 4347 (class 0 OID 0)
-- Dependencies: 289
-- Name: type_hydrant_nature_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_hydrant_nature_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_hydrant_nature_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_hydrant_nature_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_hydrant_nature_id_seq TO remocra;


--
-- TOC entry 4348 (class 0 OID 0)
-- Dependencies: 290
-- Name: type_hydrant_positionnement; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_hydrant_positionnement FROM PUBLIC;
REVOKE ALL ON TABLE type_hydrant_positionnement FROM postgres;
GRANT ALL ON TABLE type_hydrant_positionnement TO postgres;
GRANT SELECT ON TABLE type_hydrant_positionnement TO remocra;


--
-- TOC entry 4350 (class 0 OID 0)
-- Dependencies: 291
-- Name: type_hydrant_positionnement_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_hydrant_positionnement_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_hydrant_positionnement_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_hydrant_positionnement_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_hydrant_positionnement_id_seq TO remocra;


--
-- TOC entry 4351 (class 0 OID 0)
-- Dependencies: 292
-- Name: type_hydrant_saisie; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_hydrant_saisie FROM PUBLIC;
REVOKE ALL ON TABLE type_hydrant_saisie FROM postgres;
GRANT ALL ON TABLE type_hydrant_saisie TO postgres;
GRANT SELECT ON TABLE type_hydrant_saisie TO remocra;


--
-- TOC entry 4353 (class 0 OID 0)
-- Dependencies: 293
-- Name: type_hydrant_saisie_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_hydrant_saisie_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_hydrant_saisie_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_hydrant_saisie_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_hydrant_saisie_id_seq TO remocra;


--
-- TOC entry 4354 (class 0 OID 0)
-- Dependencies: 294
-- Name: type_hydrant_vol_constate; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_hydrant_vol_constate FROM PUBLIC;
REVOKE ALL ON TABLE type_hydrant_vol_constate FROM postgres;
GRANT ALL ON TABLE type_hydrant_vol_constate TO postgres;
GRANT SELECT ON TABLE type_hydrant_vol_constate TO remocra;


--
-- TOC entry 4356 (class 0 OID 0)
-- Dependencies: 295
-- Name: type_hydrant_vol_constate_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_hydrant_vol_constate_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_hydrant_vol_constate_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_hydrant_vol_constate_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_hydrant_vol_constate_id_seq TO remocra;


--
-- TOC entry 4358 (class 0 OID 0)
-- Dependencies: 296
-- Name: type_permis_avis_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_permis_avis_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_permis_avis_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_permis_avis_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_permis_avis_id_seq TO remocra;


--
-- TOC entry 4359 (class 0 OID 0)
-- Dependencies: 297
-- Name: type_permis_interservice; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_permis_interservice FROM PUBLIC;
REVOKE ALL ON TABLE type_permis_interservice FROM postgres;
GRANT ALL ON TABLE type_permis_interservice TO postgres;
GRANT SELECT ON TABLE type_permis_interservice TO remocra;


--
-- TOC entry 4361 (class 0 OID 0)
-- Dependencies: 298
-- Name: type_permis_interservice_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_permis_interservice_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_permis_interservice_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_permis_interservice_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_permis_interservice_id_seq TO remocra;


--
-- TOC entry 4362 (class 0 OID 0)
-- Dependencies: 299
-- Name: type_rci_degre_certitude; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_rci_degre_certitude FROM PUBLIC;
REVOKE ALL ON TABLE type_rci_degre_certitude FROM postgres;
GRANT ALL ON TABLE type_rci_degre_certitude TO postgres;
GRANT SELECT ON TABLE type_rci_degre_certitude TO remocra;


--
-- TOC entry 4364 (class 0 OID 0)
-- Dependencies: 300
-- Name: type_rci_degre_certitude_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_rci_degre_certitude_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_rci_degre_certitude_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_rci_degre_certitude_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_rci_degre_certitude_id_seq TO remocra;


--
-- TOC entry 4365 (class 0 OID 0)
-- Dependencies: 301
-- Name: type_rci_origine_alerte; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_rci_origine_alerte FROM PUBLIC;
REVOKE ALL ON TABLE type_rci_origine_alerte FROM postgres;
GRANT ALL ON TABLE type_rci_origine_alerte TO postgres;
GRANT SELECT ON TABLE type_rci_origine_alerte TO remocra;


--
-- TOC entry 4367 (class 0 OID 0)
-- Dependencies: 302
-- Name: type_rci_origine_alerte_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_rci_origine_alerte_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_rci_origine_alerte_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_rci_origine_alerte_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_rci_origine_alerte_id_seq TO remocra;


--
-- TOC entry 4368 (class 0 OID 0)
-- Dependencies: 303
-- Name: type_rci_prom_categorie; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_rci_prom_categorie FROM PUBLIC;
REVOKE ALL ON TABLE type_rci_prom_categorie FROM postgres;
GRANT ALL ON TABLE type_rci_prom_categorie TO postgres;
GRANT SELECT ON TABLE type_rci_prom_categorie TO remocra;


--
-- TOC entry 4370 (class 0 OID 0)
-- Dependencies: 304
-- Name: type_rci_prom_categorie_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_rci_prom_categorie_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_rci_prom_categorie_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_rci_prom_categorie_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_rci_prom_categorie_id_seq TO remocra;


--
-- TOC entry 4372 (class 0 OID 0)
-- Dependencies: 305
-- Name: type_rci_prom_famille_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_rci_prom_famille_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_rci_prom_famille_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_rci_prom_famille_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_rci_prom_famille_id_seq TO remocra;


--
-- TOC entry 4373 (class 0 OID 0)
-- Dependencies: 306
-- Name: type_rci_prom_partition; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE type_rci_prom_partition FROM PUBLIC;
REVOKE ALL ON TABLE type_rci_prom_partition FROM postgres;
GRANT ALL ON TABLE type_rci_prom_partition TO postgres;
GRANT SELECT ON TABLE type_rci_prom_partition TO remocra;


--
-- TOC entry 4375 (class 0 OID 0)
-- Dependencies: 307
-- Name: type_rci_prom_partition_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE type_rci_prom_partition_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE type_rci_prom_partition_id_seq FROM postgres;
GRANT ALL ON SEQUENCE type_rci_prom_partition_id_seq TO postgres;
GRANT SELECT ON SEQUENCE type_rci_prom_partition_id_seq TO remocra;


--
-- TOC entry 4377 (class 0 OID 0)
-- Dependencies: 308
-- Name: utilisateur_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE utilisateur_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE utilisateur_id_seq FROM postgres;
GRANT ALL ON SEQUENCE utilisateur_id_seq TO postgres;
GRANT SELECT ON SEQUENCE utilisateur_id_seq TO remocra;


--
-- TOC entry 4378 (class 0 OID 0)
-- Dependencies: 309
-- Name: voie; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE voie FROM PUBLIC;
REVOKE ALL ON TABLE voie FROM postgres;
GRANT ALL ON TABLE voie TO postgres;
GRANT SELECT ON TABLE voie TO remocra;


--
-- TOC entry 4380 (class 0 OID 0)
-- Dependencies: 310
-- Name: voie_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE voie_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE voie_id_seq FROM postgres;
GRANT ALL ON SEQUENCE voie_id_seq TO postgres;
GRANT SELECT ON SEQUENCE voie_id_seq TO remocra;


--
-- TOC entry 4381 (class 0 OID 0)
-- Dependencies: 311
-- Name: zone_competence; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE zone_competence FROM PUBLIC;
REVOKE ALL ON TABLE zone_competence FROM postgres;
GRANT ALL ON TABLE zone_competence TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE zone_competence TO remocra;


--
-- TOC entry 4383 (class 0 OID 0)
-- Dependencies: 312
-- Name: zone_competence_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE zone_competence_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE zone_competence_id_seq FROM postgres;
GRANT ALL ON SEQUENCE zone_competence_id_seq TO postgres;
GRANT SELECT,UPDATE ON SEQUENCE zone_competence_id_seq TO remocra;


--
-- TOC entry 4384 (class 0 OID 0)
-- Dependencies: 313
-- Name: zone_speciale; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON TABLE zone_speciale FROM PUBLIC;
REVOKE ALL ON TABLE zone_speciale FROM postgres;
GRANT ALL ON TABLE zone_speciale TO postgres;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE zone_speciale TO remocra;


--
-- TOC entry 4386 (class 0 OID 0)
-- Dependencies: 314
-- Name: zone_speciale_id_seq; Type: ACL; Schema: remocra; Owner: postgres
--

REVOKE ALL ON SEQUENCE zone_speciale_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE zone_speciale_id_seq FROM postgres;
GRANT ALL ON SEQUENCE zone_speciale_id_seq TO postgres;
GRANT ALL ON SEQUENCE zone_speciale_id_seq TO remocra;


SET search_path = tracabilite, pg_catalog;

--
-- TOC entry 4387 (class 0 OID 0)
-- Dependencies: 419
-- Name: hydrant; Type: ACL; Schema: tracabilite; Owner: postgres
--

REVOKE ALL ON TABLE hydrant FROM PUBLIC;
REVOKE ALL ON TABLE hydrant FROM postgres;
GRANT ALL ON TABLE hydrant TO postgres;
GRANT SELECT,INSERT ON TABLE hydrant TO remocra;


--
-- TOC entry 4389 (class 0 OID 0)
-- Dependencies: 420
-- Name: hydrant_id_seq; Type: ACL; Schema: tracabilite; Owner: postgres
--

REVOKE ALL ON SEQUENCE hydrant_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE hydrant_id_seq FROM postgres;
GRANT ALL ON SEQUENCE hydrant_id_seq TO postgres;
GRANT ALL ON SEQUENCE hydrant_id_seq TO remocra;


-- Completed on 2015-03-11 16:29:48 CET

--
-- PostgreSQL database dump complete
--



-- Schémas liés à la synchro
CREATE SCHEMA remocra_referentiel;
ALTER SCHEMA remocra_referentiel OWNER TO postgres;

CREATE SCHEMA sdis_referentiel;
ALTER SCHEMA sdis_referentiel OWNER TO postgres;

