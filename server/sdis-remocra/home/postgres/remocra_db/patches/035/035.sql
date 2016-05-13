BEGIN;

alter table remocra.permis
add column date_permis timestamp without time zone;

update remocra.permis
set date_permis = date_modification;

alter table remocra.permis
alter column date_permis SET NOT NULL;

COMMIT;


/*
Patch APIS : voir patch sdis83-sdis/home/oracle/patches/001/001.sql
*/

