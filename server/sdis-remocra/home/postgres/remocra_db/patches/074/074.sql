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
    numero_patch := 74;
    description_patch := 'Authentification LDAP';

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


INSERT INTO remocra.param_conf (cle, description, valeur, version, nomgroupe) VALUES ('PDI_LDAP_URL_HOST', 'LDAP : Hôte du serveur : ldap://<b>hôte</b>:port/dn (ex : ldap.sdisXX.fr)<br/>Obligatoire pour activer l''authentification LDAP', '', 1, 'Authentification depuis un autre service');
INSERT INTO remocra.param_conf (cle, description, valeur, version, nomgroupe) VALUES ('PDI_LDAP_URL_PORT', 'LDAP : Port du serveur : ldap://hôte:<b>port</b>/dn (389 par défaut)', '389', 1, 'Authentification depuis un autre service');
INSERT INTO remocra.param_conf (cle, description, valeur, version, nomgroupe) VALUES ('PDI_LDAP_URL_BASE_DN', 'LDAP : Distinguished Name de base : ldap://hôte:port/<b>dn</b> (ex : cn=Users,dc=sdis,dc=fr)<br/>Facultatif', '', 1, 'Authentification depuis un autre service');
INSERT INTO remocra.param_conf (cle, description, valeur, version, nomgroupe) VALUES ('PDI_LDAP_ADMIN_DN', '<span style="color:grey">LDAP : Distinguished Name du compte admin (ex : cn=Administrator,cn=Users,dc=sdis77-ad,dc=local)<br/>Obligatoire pour activer la méthode "recherche d''utilisateur"</span>', '', 1, 'Authentification depuis un autre service');
INSERT INTO remocra.param_conf (cle, description, valeur, version, nomgroupe) VALUES ('PDI_LDAP_ADMIN_PASSWORD', '<span style="color:grey">LDAP : Mot de passe du compte admin<br/>Obligatoire pour activer la méthode "recherche d''utilisateur"</span>', '', 1, 'Authentification depuis un autre service');
INSERT INTO remocra.param_conf (cle, description, valeur, version, nomgroupe) VALUES ('PDI_LDAP_USER_BASE_NAME', '<span style="color:grey">LDAP : Base name des comptes utilisateurs (ex : ou=Sites SDIS 77,dc=sdis77-ad,dc=local)<br/>Obligatoire pour activer la méthode "recherche d''utilisateur"</span>', '', 1, 'Authentification depuis un autre service');
INSERT INTO remocra.param_conf (cle, description, valeur, version, nomgroupe) VALUES ('PDI_LDAP_USER_FILTER', '<span style="color:grey">LDAP : Filtre des comptes utilisateurs<br/><i>[USERNAME]</i> est automatiquement remplacé par l''identifiant.<br/>Par défaut : (&(objectclass=user)(sAMAccountName=[USERNAME]))</span>', '(&(objectclass=user)(sAMAccountName=[USERNAME]))', 1, 'Authentification depuis un autre service');


-- Contenu réel du patch fin
--------------------------------------------------

commit;

