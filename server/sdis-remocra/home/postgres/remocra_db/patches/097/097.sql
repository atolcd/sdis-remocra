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
    numero_patch := 97;
    description_patch := 'Ajout de la table zone_competence_commune';

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


DROP TABLE IF EXISTS remocra.zone_competence_commune;
CREATE TABLE remocra.zone_competence_commune AS
SELECT
    zc.id AS zone_competence_id,
    c.id AS commune_id
FROM
    remocra.commune c,
    remocra.zone_competence zc
WHERE
    (zc.geometrie && c.geometrie)
    and (
      st_Overlaps(st_buffer(zc.geometrie,0), st_buffer(c.geometrie,0))
      OR st_contains(st_buffer(zc.geometrie,0), st_buffer(c.geometrie,0))
    )
ORDER BY
    zone_competence_id,
    commune_id;
CREATE INDEX zone_competence_commune_zone_competence_idx ON remocra.zone_competence_commune USING btree (zone_competence_id);
CREATE INDEX zone_competence_commune_commune_idx ON remocra.zone_competence_commune USING btree (commune_id);


-- Contenu réel du patch fin
--------------------------------------------------

commit;
