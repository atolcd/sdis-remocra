begin;

-- Correction codes INSEE communes

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pg_catalog;

-- Codes INSEE des communes "RAYOL CANADEL SUR MER", "REGUSSE" et "RIANS"
update remocra.commune set insee = '83102' where code = 'RCL';
update remocra.commune set insee = '83104' where code = 'RGE';
update remocra.commune set insee = '83152' where code = 'RAS';


commit;
