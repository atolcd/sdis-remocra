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
    numero_patch := 56;
    description_patch := 'Suivi des patches avec vérification préalable';

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

-- Ajout des colonnes
alter table remocra.rci add column famille_promethee bigint;
alter table only remocra.rci add constraint fk_rci_famille_promethee foreign key (famille_promethee) references type_rci_prom_famille(id) match simple on update no action on delete no action;

alter table remocra.rci add column partition_promethee bigint;
alter table only remocra.rci add constraint fk_rci_partition_promethee FOREIGN KEY (partition_promethee) references type_rci_prom_partition(id) match simple on update no action on delete no action;

-- Renseignement des familles et partitions par rapport aux saisies déjà effectuées
update remocra.rci r set partition_promethee=(select partition from remocra.type_rci_prom_categorie trpc where trpc.id=r.categorie_promethee) where categorie_promethee is not null;
update remocra.rci r set famille_promethee=(select famille from remocra.type_rci_prom_partition trpp where trpp.id=r.partition_promethee) where partition_promethee is not null;

--
-- Contenu réel du patch fin
--------------------------------------------------

commit;

