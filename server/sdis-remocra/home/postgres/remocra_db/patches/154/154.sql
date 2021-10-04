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
    numero_patch := 154;
    description_patch := 'Fix mail demande accès API';

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


UPDATE remocra.email_modele
SET objet = 'REMOCRA - Génération d''accès à l''API',
corps = '
<title>Génération d''accès à l''API REMOCRA</title>
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
    <br/>Une demande d''accès à l''API REMOCRA a été effectuée pour votre organisme. Pour configurer cet accès, cliquez <a href="[URL_API]">ici</a>.<br/>
    <br/>Cordialement.</p>
    <p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS.</p>
    <p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p>
</div>'
WHERE code = 'ACCES_API';

-- Contenu réel du patch fin
--------------------------------------------------

commit;
