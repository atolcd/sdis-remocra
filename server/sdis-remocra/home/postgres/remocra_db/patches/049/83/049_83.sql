begin;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pdi, public, pg_catalog;



UPDATE remocra.hydrant_pena hp set coorddfci = (
    select coordonnee_complete from remocra_referentiel.carro_dfci cd, remocra.hydrant h where h.id = hp.id and cd.sous_type = 'CARRES INTRA 2x2 KM' and st_dwithin (cd.geometrie, st_transform(h.geometrie, '2154'), 0) = true limit 1
) where coorddfci is null or coorddfci = '';



commit;
