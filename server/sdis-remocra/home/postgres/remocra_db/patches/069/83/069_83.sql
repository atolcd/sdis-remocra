begin;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pdi, public, pg_catalog;



UPDATE remocra.param_conf set valeur='CNIL : déclaration simplifiée n°1378196 enregistrée le 02/07/2009' where cle='MENTION_CNIL';



commit;
