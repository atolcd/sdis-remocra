-- ---------------
-- Script liant
-- ---------------

SET CLIENT_ENCODING TO 'UTF8';


-- ----------
-- AJOUT DU ROLE remocra S'IL N'EXISTE PAS
-- ----------
CREATE FUNCTION createremocraroleonce12489() RETURNS void AS $$
BEGIN
   IF NOT EXISTS (
      SELECT *
      FROM   pg_catalog.pg_user
      WHERE  usename = 'remocra') THEN

      CREATE ROLE remocra LOGIN
        PASSWORD 'remocra1453'
        NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE;
      COMMENT ON ROLE remocra IS 'Rôle utilisé par l''application REMOcRA';

   END IF;

END;
$$ LANGUAGE plpgsql;

select createremocraroleonce12489();
DROP FUNCTION createremocraroleonce12489();


\i 010_schemas.sql
\i 020_refdata.sql
\i 021_refdata-param.sql
--\i 030_acces.sql
\i patches/all_patches.sql

