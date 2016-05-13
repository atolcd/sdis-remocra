begin;

-- Retours RCI : pt éclosion facultatif, supp certitude "Déterminée", mail création RCI

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pg_catalog;

-- Point d'éclosion devient facultatif
alter table remocra.rci alter column point_eclosion drop not null;

-- Suppression certitude "Déterminée"
update remocra.rci set degre_certitude = (select id from remocra.type_rci_degre_certitude where code = 'CERTAINE');
delete from remocra.type_rci_degre_certitude where code = 'DTERMINE';

-- Nouveau départ : Adresse de diffusion
insert into remocra.param_conf (cle, valeur, description) values (
    'EMAIL_DEST_CREATION_RCI',
    (select valeur from param_conf where cle = 'PDI_SMTP_EME_MAIL'),
    'Adresse mél utilisée pour la diffusion lorsqu''un départ de feux est créé'
);

-- Nouveau départ : Modèle de courriel
insert into remocra.email_modele(code, corps, objet) values(
    'CREATION_RCI',
    '<title>Création d''un départ de feu SDIS83 REMOCRA</title><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><style type="text/css">div{width:800px;text-align:justify;}p{text-decoration:none;color:#000000;font-family: arial;font-size: 14px;}p.footer{text-decoration:none;font-style: italic;color:#AAAAAA;font-family: arial;font-size: 12px;}p.caution{text-decoration:none;font-style: italic;color:#000000;font-family: arial;font-size: 12px;}table{border-collapse:collapse;}td{text-decoration:none;border-width:1px;border-style:solid;color:#000000;font-family: arial;font-size: 14px;}</style><div><p>Bonjour,<br/><br/>Le nouveau départ de feu portant le code "[CODE]" vient d''être renseigné dans <a href="[URL_SITE]">REMOcRA</a> par l''utilisateur [IDENTIFIANT] de l''organisme [NOM_ORGANISME].<br/><br/>Cordialement.</p><p class="caution">En cas d''incompréhension de ce message, merci de prendre contact avec le SDIS83.</p><p class="footer">Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.</p></div>',
    'SDIS83 REMOCRA - RCCI'
);

-- Retour sur description traitement RCCI
update pdi.modele_traitement set description = 'Départs de feu sur une période et pour une famille Prométhée' where code = 7;

commit;
