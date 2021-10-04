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
    numero_patch := 145;
    description_patch :='Génération d''accès API Organisme';

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

ALTER TABLE remocra.organisme
ADD COLUMN password character varying,
ADD COLUMN salt character varying;

CREATE TABLE IF NOT EXISTS remocra.dde_api(
    id serial NOT NULL PRIMARY KEY,
    organisme bigint,
    code character varying,
    date_demande timestamp without time zone,
    utilise boolean DEFAULT FALSE,
    CONSTRAINT fk_organisme_dde_api FOREIGN KEY (organisme)
        REFERENCES remocra.organisme (id)
        MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE remocra.dde_api OWNER TO remocra;

--Modèle de mail
INSERT INTO remocra.email_modele (code, corps, objet, version) VALUES (
'ACCES_API',
'<title>Génération de clef d''API REMOCRA</title>
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
    <p>Bonjour,<br/>
    <br/>Une demande de clef d''API REMOCRA a été effectuée pour votre organisme. Pour accéder à cette clef cliquez <a href="[URL_SITE]">ici</a>.<br/>
    <br/>Cordialement.</p>
    <p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS.</p>
    <p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p>
</div>'
,'REMOCRA - Génération de clef d''API',
1);


CREATE TABLE IF NOT EXISTS remocra.transferts_automatises(
    id serial NOT NULL PRIMARY KEY,
    type_organisme bigint,
    recuperer boolean DEFAULT FALSE,
    transmettre boolean DEFAULT FALSE,
    CONSTRAINT fk_organisme_transferts_automatises FOREIGN KEY (type_organisme)
        REFERENCES remocra.type_organisme (id)
        MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE remocra.transferts_automatises OWNER TO remocra;


INSERT INTO remocra.transferts_automatises(type_organisme)
SELECT id FROM remocra.type_organisme;

INSERT INTO remocra.type_organisme(code, nom) VALUES ('PRESTATAIRE_TECHNIQUE', 'Prestataire technique');
UPDATE remocra.type_organisme SET nom = 'Service des eaux' WHERE code = 'SERVICEEAUX';

ALTER TABLE remocra.hydrant ADD maintenance_deci bigint,
ADD CONSTRAINT fk_maintenance_deci_organisme FOREIGN KEY (maintenance_deci) REFERENCES remocra.organisme(id);

-- Contenu réel du patch fin
--------------------------------------------------

commit;
