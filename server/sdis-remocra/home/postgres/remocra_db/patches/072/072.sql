begin;

set statement_timeout = 0;
set client_encoding = 'UTF8';
set standard_conforming_strings = off;
set check_function_bodies = false;
set client_min_messages = warning;
set escape_string_warning = off;

set search_path = remocra, pdi, public, pg_catalog;

--------------------------------------------------
-- Versionnement du patch et vérification
--
create or replace function versionnement_dffd4df4df() returns void language plpgsql AS $body$
declare
    numero_patch int;
    description_patch varchar;
begin
    -- Métadonnées du patch
    numero_patch := 72;
    description_patch := 'Indisponibilité  temporaire';

    -- Vérification
    if (select numero_patch-1 != (select max(numero) from remocra.suivi_patches)) then
        raise exception 'Le numéro de patch requis n''est pas le bon. Dernier appliqué : %, en cours : %', (select max(numero) from remocra.suivi_patches), numero_patch; end if;
    -- Suivi
    insert into remocra.suivi_patches(numero, description) values(numero_patch, description_patch);
end $body$;
select versionnement_dffd4df4df();
drop function versionnement_dffd4df4df();

--------------------------------------------------
-- Contenu réel du patch début
--

-- Table: remocra.type_hydrant_indispo_statut

DROP TABLE IF EXISTS remocra.type_hydrant_indispo_statut CASCADE;
CREATE TABLE remocra.type_hydrant_indispo_statut (
	id bigserial NOT NULL PRIMARY KEY,
	actif boolean DEFAULT true,
	code character varying NOT NULL,
	nom character varying NOT NULL
);
COMMENT ON TABLE remocra.type_hydrant_indispo_statut IS 'Statut de l''indisponibilité';

-- Table:  remocra.type_hydrant_indispo_statut
TRUNCATE remocra.type_hydrant_indispo_statut CASCADE;
INSERT INTO  remocra.type_hydrant_indispo_statut (code,nom) VALUES('EN_COURS','En cours');
INSERT INTO  remocra.type_hydrant_indispo_statut (code,nom) VALUES('PLANIFIE','Planifié');
INSERT INTO  remocra.type_hydrant_indispo_statut (code,nom) VALUES('TERMINE','Terminé');


--Table: remocra.hydrant_indispo_temporaire
DROP TABLE IF EXISTS remocra.hydrant_indispo_temporaire CASCADE;
CREATE TABLE remocra.hydrant_indispo_temporaire (
	id bigserial NOT NULL PRIMARY KEY,
	date_prev_debut timestamp without time zone,
	date_prev_fin timestamp without time zone,
	date_debut timestamp without time zone,
	date_fin timestamp without time zone,
	motif character varying,
        date_rappel_debut timestamp without time zone,
        date_rappel_fin timestamp without time zone,
        statut bigint NOT NULL,
        total_hydrants integer,
	CONSTRAINT fk_hydrant_indispo_temporaire_statut FOREIGN KEY (statut)
		REFERENCES remocra.type_hydrant_indispo_statut (id) MATCH SIMPLE
		ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE remocra.hydrant_indispo_temporaire IS 'Indisponibilié temporaire d''hydrant';
COMMENT ON COLUMN remocra.hydrant_indispo_temporaire.date_rappel_debut IS 'Date de l''envoi du rappel de debut ';
COMMENT ON COLUMN remocra.hydrant_indispo_temporaire.date_rappel_fin IS 'Date de l''envoi du rappel de fin ';

-- Table: remocra.hydrant_indispo_temporaire_hydrant

DROP TABLE IF EXISTS remocra.hydrant_indispo_temporaire_hydrant CASCADE;

CREATE TABLE remocra.hydrant_indispo_temporaire_hydrant(
  
  indisponibilite bigint NOT NULL,
  hydrant bigint NOT NULL,
  CONSTRAINT pk_indisponibilite_hydrant PRIMARY KEY (indisponibilite, hydrant),
  CONSTRAINT fk_hydrant_indispo_temporaire_hydrant_indisponibilite FOREIGN KEY (indisponibilite)
      REFERENCES remocra.hydrant_indispo_temporaire (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_hydrant_indispo_temporaire_hydrant_hydrant FOREIGN KEY (hydrant)
      REFERENCES remocra.hydrant (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON TABLE remocra.hydrant_indispo_temporaire_hydrant
  IS 'Indisponibilité temporaire lié à un hydrant';

-- Table: remocra.type_hydrant_anomalie
select setval('remocra.type_hydrant_anomalie_id_seq',id,false) from (select max(id)+1 as id from remocra.type_hydrant_anomalie) as compteur;
INSERT INTO remocra.type_hydrant_anomalie (actif, code, commentaire, nom, version)
VALUES ('TRUE', 'INDISPONIBILITE_TEMP', '','Indisponibilité temporaire', '1');


--Insertion des modèles de mail pour les notificatins des indisponibilités des hydrants
select setval('remocra.email_modele_id_seq',id,false) from (select max(id)+1 as id from remocra.email_modele) as compteur;
INSERT INTO remocra.email_modele (code, corps, objet, version) VALUES ('INDISPO_TEMPORAIRE_DEBUT', '<title>Indisponibilité temporaire SDIS REMOCRA</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
    div{width:800px;text-align:justify;}
    p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}
    p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}
    p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}
    table{border-collapse:collapse;}
    td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}
</style>
<div>
    <p>Madame, Monsieur,<br/>
    <br/>Des indisponibilités temporaires dont la date de début programmée est imminente ont été paramétrées dans REMOCRA. Merci de vous connecter à REMOCRA si vous souhaitez confirmer ces indisponibilités et rendre ainsi les hydrants indisponibles.<br/>
    <br/><a href="[URL_SITE]#hydrants/indispos">Gérer les indisponibilités temporaires</a><br/>
    <br/>Cordialement.</p>
    <p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS.</p>
    <p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p>
</div>
', 'SDIS REMOCRA - INDISPONIBILITES TEMPORAIRES (date imminente)', 1);

INSERT INTO remocra.email_modele (code, corps, objet, version) VALUES ('INDISPO_TEMPORAIRE_FIN', '<title>Indisponibilité temporaire SDIS REMOCRA</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
    div{width:800px;text-align:justify;}
    p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}
    p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}
    p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}
    table{border-collapse:collapse;}
    td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}
</style>
<div>
    <p>Madame, Monsieur,<br/>
    <br/>Des indisponibilités temporaires dont la date de fin programmée a été dépassée ont été paramétrées dans REMOCRA. Merci de vous connecter à REMOCRA si vous souhaitez lever ces indisponibilités et rendre ainsi les hydrants de nouveau disponibles<br/>
    <br/><a href="[URL_SITE]#hydrants/indispos">Gérer les indisponibilités temporaires</a><br/>
    <br/>Cordialement.</p>
    <p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS.</p>
    <p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p>
</div>', 'SDIS REMOCRA - INDISPONIBILITES TEMPORAIRES (date dépassée)', 1);

--Insertion des paramètres pour le traitement PDI
INSERT INTO remocra.param_conf (cle, description, valeur, version, nomgroupe) VALUES ('PDI_DELTA_NOTIF_INDISPO_DEBUT', 'Notifier par mél N heures avant le début d''une indisponibilité temporaire', 72, 1, 'Points d''eau');
INSERT INTO remocra.param_conf (cle, description, valeur, version, nomgroupe) VALUES ('PDI_DELTA_NOTIF_INDISPO_FIN', 'Notifier par mél N heures après la fin d''une indisponibilité temporaire', 72, 1, 'Points d''eau');
INSERT INTO remocra.param_conf (cle, description, valeur, version, nomgroupe) VALUES ('PDI_UTILISATEUR_NOTIF_INDISPO', 'Code du profil ou adresse du destinataire pour la notification de début ou de fin prévisionnelles des indisponibilités temporaires', '', 1, 'Courriels et courriers');

-- Droits sur la disponibilite temporaire : INDISPOS CRUD -> Profil Administrateur applicatif
select setval('remocra.type_droit_id_seq',id,false) from (select max(id)+1 as id from remocra.type_droit) as compteur;
insert into remocra.type_droit(code, description, nom, version) values ('INDISPOS', 'Droit sur l''indisponibilité temporaire', 'indisponibilite.temporaire', 1);
select setval('remocra.droit_id_seq',id,false) from (select max(id)+1 as id from remocra.droit) as compteur;
insert into remocra.droit(droit_create, droit_delete, droit_read, droit_update, "version", profil_droit, type_droit)
  select 'TRUE','TRUE','TRUE','TRUE',1, pd.id, td.id
  from remocra.profil_droit pd, remocra.type_droit td
  where td.code = 'INDISPOS'
  and pd.code in ('SDIS-ADM-APP');


-- Function: remocra.calcul_debit_pression(bigint)

-- DROP FUNCTION remocra.calcul_debit_pression(bigint);

CREATE OR REPLACE FUNCTION remocra.calcul_debit_pression(id_hydrant bigint)
  RETURNS void AS
$BODY$
DECLARE
	p_code_type_hydrant_nature varchar;
	p_code_diametre varchar;
	p_anomalie_id integer;
	p_rec remocra.hydrant_pibi%ROWTYPE;
BEGIN

	select * into p_rec from remocra.hydrant_pibi where id = id_hydrant;

	-- Suppression des anciennes anomalies
	delete from remocra.hydrant_anomalies where hydrant = p_rec.id and anomalies in (select id from remocra.type_hydrant_anomalie where code IN('PRESSION_INSUFF', 'PRESSION_TROP_ELEVEE', 'PRESSION_DYN_INSUFF', 'PRESSION_DYN_TROP_ELEVEE', 'DEBIT_INSUFF', 'DEBIT_TROP_ELEVE', 'DEBIT_INSUFF_NC'));

	-- Récupération du type_hydrant_nature id
	select thn.code into p_code_type_hydrant_nature from remocra.hydrant h left join remocra.type_hydrant_nature thn on (thn.id=h.nature) where h.id = id_hydrant;

	-- Récupération du diamètre id
	select code into p_code_diametre from remocra.type_hydrant_diametre where id = p_rec.diametre;

	-- Les anomalies sont applicables aux PI et BI uniquement (pas aux PA)
	if FOUND and p_code_type_hydrant_nature in ('PI', 'BI') then
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

		--choix de debit
		if (p_rec.debit_max IS NOT NULL) then
			p_rec.debit=p_rec.debit_max;  
		end if;

		if(p_code_diametre = 'DIAM80' or p_code_diametre = 'DIAM70' or p_code_diametre = 'DIAM100') then
			-- débit
			if (p_rec.debit >= 0 AND  p_rec.debit < 30) then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			end if;
		end if;

		if (p_code_diametre = 'DIAM80' or p_code_diametre = 'DIAM70') then
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

                        if     (p_rec.debit >= 0 AND  p_rec.debit <60) then
				select id into p_anomalie_id from remocra.type_hydrant_anomalie where code = 'DEBIT_INSUFF';
				insert into remocra.hydrant_anomalies (hydrant,anomalies) values (p_rec.id,p_anomalie_id);
			elsif  p_rec.debit >= 60 AND p_rec.debit < 120 then
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
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION remocra.calcul_debit_pression(bigint)
  OWNER TO postgres;
GRANT EXECUTE ON FUNCTION remocra.calcul_debit_pression(bigint) TO remocra;
GRANT EXECUTE ON FUNCTION remocra.calcul_debit_pression(bigint) TO postgres;
GRANT EXECUTE ON FUNCTION remocra.calcul_debit_pression(bigint) TO public;


-- Contenu réel du patch fin
--------------------------------------------------

commit;

