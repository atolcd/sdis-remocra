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
    numero_patch := 70;
    description_patch := 'Calcul des numéros d''hydrant rendu paramétrable';

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


-- Paramétrage de la méthode de calcul du numéro
DELETE FROM remocra.param_conf where cle in ('HYDRANT_NUMEROTATION_INTERNE_METHODE', 'HYDRANT_NUMEROTATION_METHODE');
INSERT INTO remocra.param_conf(cle, description, valeur, version, nomgroupe) VALUES ('HYDRANT_NUMEROTATION_METHODE', 'Règle de calcul des <i>numéros globaux</i> des PEI (ex : 77, 83). <a href="https://github.com/atolcd/sdis-remocra/blob/master/docs/Numérotation_PEI.adoc" target="_blank">Méthodes disponibles</a>','83', NULL, 'Points d''eau');
INSERT INTO remocra.param_conf(cle, description, valeur, version, nomgroupe) VALUES ('HYDRANT_NUMEROTATION_INTERNE_METHODE', 'Règle de calcul des <i>numéros internes</i> des PEI (ex : 77, 83). <a href="https://github.com/atolcd/sdis-remocra/blob/master/docs/Numérotation_PEI.adoc" target="_blank">Méthodes disponibles</a>','83', NULL, 'Points d''eau');



-- Retourne un numéro interne disponible avec la contrainte d'unicité selon les éléments fournis (type hydrant, commune, zone spéciale) sans prendre en compte l'hydrant éventuel (cas d'une réattribution)
create or replace function remocra.nextNumeroInterne(
  -- Ne pas prendre en compte l'hydrant (NULL)
  idhydrant bigint,
  -- Contrainte d'unicité : type hydrant, commune, zone spéciale (NULLABLE*)
  idtypehydrant bigint,
  idcommune bigint,
  idzonespeciale bigint,
  -- Partir du début ou de la fin ?
  frombeginning boolean
  --
) returns bigint AS $$
declare
  returned bigint;
begin

  if coalesce(frombeginning, false) then
      -- Premier numéro disponible
      select n into returned
      from generate_series(1,99999) gs(n) except select numero_interne from remocra.hydrant h join remocra.type_hydrant_nature thn on (h.nature = thn.id) where
          (case when idhydrant is not null then h.id != idhydrant else true end)
          and (case when idtypehydrant is not null then thn.type_hydrant = idtypehydrant else true end)
          and (case when idcommune is not null then h.commune = idcommune else true end)
          and (case when idzonespeciale is not null then h.zone_speciale = idzonespeciale else true end)
      order by 1 limit 1;
    else
      -- "Dernier" numéro disponible
      select into returned
      case 
          when min(h.numero_interne) is null then 99999
          else min(h.numero_interne) -1
      end as numero_min
      from remocra.hydrant h join remocra.type_hydrant_nature thn on (h.nature = thn.id) where
          (case when idhydrant is not null then h.id != idhydrant else true end)
          and (case when idtypehydrant is not null then thn.type_hydrant = idtypehydrant else true end)
          and (case when idcommune is not null then h.commune = idcommune else true end)
          and (case when idzonespeciale is not null then h.zone_speciale = idzonespeciale else true end)
          and h.numero_interne > 90000;  
  end if;

  return returned;
end;
$$ language plpgsql;

comment on function remocra.nextNumeroInterne(idhydrant bigint, idtypehydrant bigint, idcommune bigint, idzonespeciale bigint, frombeginning boolean) is 'Retourne un numéro interne disponible avec la contrainte d''unicité selon les éléments fournis (type hydrant, commune, zone spéciale) sans prendre en compte l''hydrant éventuel (cas d''une réattribution)';


--
-- Contenu réel du patch fin
--------------------------------------------------

commit;

