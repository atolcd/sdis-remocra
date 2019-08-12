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
    numero_patch := 123;
    description_patch := 'Fiche indisponibilité temporaire';

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

ALTER TABLE remocra.hydrant_indispo_temporaire 
DROP COLUMN date_prev_debut,
DROP COLUMN date_prev_fin;

ALTER TABLE remocra.hydrant_indispo_temporaire
ADD COLUMN bascule_auto_indispo BOOLEAN DEFAULT FALSE,
ADD COLUMN bascule_auto_dispo BOOLEAN DEFAULT FALSE,
ADD COLUMN mel_avant_indispo BOOLEAN DEFAULT FALSE,
ADD COLUMN mel_avant_dispo BOOLEAN DEFAULT FALSE;

UPDATE remocra.param_conf 
SET description='Notifier par mél N heures avant la fin d''une indisponibilité temporaire' 
WHERE cle = 'PDI_DELTA_NOTIF_INDISPO_FIN';

UPDATE remocra.email_modele
SET objet='SDIS REMOCRA - DEBUT D''INDISPONIBILITE TEMPORAIRE' 
WHERE code = 'INDISPO_TEMPORAIRE_DEBUT';

UPDATE remocra.email_modele
SET objet='SDIS REMOCRA - FIN D''INDISPONIBILITE TEMPORAIRE' 
WHERE code = 'INDISPO_TEMPORAIRE_FIN';

UPDATE remocra.email_modele
SET corps='<title>Indisponibilité temporaire SDIS REMOCRA</title>
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
    <br/>Des indisponibilités temporaires dont la date de début programmée est imminente ont été paramétrées dans REMOCRA. 
    Si vous n''avez pas choisi une mise en indisponibilité automatique, merci de vous connecter à REMOCRA si vous souhaitez confirmer ces indisponibilités et rendre ainsi les hydrants indisponibles.<br/>
    <br/><a href="[URL_SITE]#hydrants/indispos">Gérer les indisponibilités temporaires</a><br/>
    <br/>[INDISPO]<br/>
    <br/>Cordialement.</p>
    <p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS.</p>
    <p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p>
</div>' 
WHERE code = 'INDISPO_TEMPORAIRE_DEBUT';


UPDATE remocra.email_modele
SET corps='<title>Indisponibilité temporaire SDIS REMOCRA</title>
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
    <br/>Des indisponibilités temporaires dont la date de fin programmée est imminente ont été paramétrées dans REMOCRA. 
    Si vous n''avez pas choisi une remise en disponibilité automatique, merci de vous connecter à REMOCRA si vous souhaitez lever ces indisponibilités et rendre ainsi les hydrants de nouveau disponibles.<br/>
    <br/><a href="[URL_SITE]#hydrants/indispos">Gérer les indisponibilités temporaires</a><br/>
    <br/>[INDISPO]<br/>
    <br/>Cordialement.</p>
    <p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS.</p>
    <p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p>
</div>' 
WHERE code = 'INDISPO_TEMPORAIRE_FIN';


-- Contenu réel du patch fin
--------------------------------------------------

commit;
