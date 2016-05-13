begin;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pdi, public, pg_catalog;



-- Vérification préparation des données
create or replace function remocra.rcitestifusernotsadlydefined4sd5() returns void as
$BODY$
DECLARE
    atleastone boolean;
    list varchar;
    notfounds RECORD;
BEGIN
    -- arrivee_ddtm_onf
    atleastone := false;
    list := '';
    FOR notfounds IN (select distinct arrivee_ddtm_onf as arrivee from remocra.rci r where r.arrivee_ddtm_onf is not null and not exists (select id from remocra.utilisateur where identifiant=r.arrivee_ddtm_onf)) LOOP
        list := list || ' ' || quote_ident(notfounds.arrivee);
        atleastone := true;
    END LOOP;
    if (atleastone) then
        RAISE EXCEPTION 'Identifiant(s) d''utilisateur(s) non trouvé(s) pour arrivee_ddtm_onf --> %', list USING HINT = 'Les valeurs doivent être présentes dans remocra.utilisateur.identifiant';
    end if;
    -- arrivee_sdis
    atleastone := false;
    list := '';
    FOR notfounds IN (select distinct arrivee_sdis as arrivee from remocra.rci r where r.arrivee_sdis is not null and not exists (select id from remocra.utilisateur where identifiant=r.arrivee_sdis)) LOOP
        list := list || ' ' || quote_ident(notfounds.arrivee);
        atleastone := true;
    END LOOP;
    if (atleastone) then
        RAISE EXCEPTION 'Identifiant(s) d''utilisateur(s) non trouvé(s) pour arrivee_sdis --> %', list USING HINT = 'Les valeurs doivent être présentes dans remocra.utilisateur.identifiant';
    end if;
    -- arrivee_gendarmerie
    atleastone := false;
    list := '';
    FOR notfounds IN (select distinct arrivee_gendarmerie as arrivee from remocra.rci r where r.arrivee_gendarmerie is not null and not exists (select id from remocra.utilisateur where identifiant=r.arrivee_gendarmerie)) LOOP
        list := list || ' ' || quote_ident(notfounds.arrivee);
        atleastone := true;
    END LOOP;
    if (atleastone) then
        RAISE EXCEPTION 'Identifiant(s) d''utilisateur(s) non trouvé(s) pour arrivee_gendarmerie --> %', list USING HINT = 'Les valeurs doivent être présentes dans remocra.utilisateur.identifiant';
    end if;
    -- arrivee_police
    atleastone := false;
    list := '';
    FOR notfounds IN (select distinct arrivee_police as arrivee from remocra.rci r where r.arrivee_police is not null and not exists (select id from remocra.utilisateur where identifiant=r.arrivee_police)) LOOP
        list := list || ' ' || quote_ident(notfounds.arrivee);
        atleastone := true;
    END LOOP;
    if (atleastone) then
        RAISE EXCEPTION 'Identifiant(s) d''utilisateur(s) non trouvé(s) pour arrivee_police --> %', list USING HINT = 'Les valeurs doivent être présentes dans remocra.utilisateur.identifiant';
    end if;
END
$BODY$
LANGUAGE 'plpgsql';

select remocra.rcitestifusernotsadlydefined4sd5();
drop function remocra.rcitestifusernotsadlydefined4sd5();



-- Mise à jour des données (identifiants varchar vers identifiant bigint) puis du schéma
update remocra.rci set arrivee_ddtm_onf=(select id from remocra.utilisateur where identifiant=arrivee_ddtm_onf)::varchar where arrivee_ddtm_onf is not null;
alter table remocra.rci alter arrivee_ddtm_onf set data type bigint using arrivee_ddtm_onf::bigint;
alter table remocra.rci add constraint fk1b858a9234c5c foreign key (arrivee_ddtm_onf)
    references remocra.utilisateur (id) match simple
    on update no action on delete no action;

update remocra.rci set arrivee_sdis=(select id from remocra.utilisateur where identifiant=arrivee_sdis)::varchar where arrivee_sdis is not null;
alter table remocra.rci alter column arrivee_sdis set data type bigint using arrivee_sdis::bigint;
alter table remocra.rci add constraint fk1b858a9c54d11 foreign key (arrivee_sdis)
    references remocra.utilisateur (id) match simple
    on update no action on delete no action;

update remocra.rci set arrivee_gendarmerie=(select id from remocra.utilisateur where identifiant=arrivee_gendarmerie)::varchar where arrivee_gendarmerie is not null;
alter table remocra.rci alter column arrivee_gendarmerie set data type bigint using arrivee_gendarmerie::bigint;
alter table remocra.rci add constraint fk1b858a9cc1bbb foreign key (arrivee_gendarmerie)
    references remocra.utilisateur (id) match simple
    on update no action on delete no action;

update remocra.rci set arrivee_police=(select id from remocra.utilisateur where identifiant=arrivee_police)::varchar where arrivee_police is not null;
alter table remocra.rci alter column arrivee_police set data type bigint using arrivee_police::bigint;
alter table remocra.rci add constraint fk1b858a9948aa1 foreign key (arrivee_police)
    references remocra.utilisateur (id) match simple
    on update no action on delete no action;



-- Suivi du patch
insert into remocra.suivi_patches(numero, description) values(
  54,
  'RCCI : saisie libre des référents remplacée par fk vers utilisateurs'
);

commit;

