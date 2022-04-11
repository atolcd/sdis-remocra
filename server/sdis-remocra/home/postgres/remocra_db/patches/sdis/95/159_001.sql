BEGIN;

-- MAJ param conf
UPDATE remocra.param_conf
SET valeur = '95'
WHERE cle = 'HYDRANT_NUMEROTATION_INTERNE_METHODE';

UPDATE remocra.param_conf
SET valeur = '95'
WHERE cle = 'HYDRANT_NUMEROTATION_METHODE';

CREATE OR REPLACE FUNCTION remocra.nextnumerointerne(idhydrant bigint, idtypehydrant bigint, idcommune bigint, idzonespeciale bigint, startserie bigint, stopserie bigint, frombeginning boolean)
 RETURNS bigint
 LANGUAGE plpgsql
AS $function$
declare
  returned bigint;
begin
  if coalesce(frombeginning, false) then
      -- Premier numéro disponible
      select n into returned
      from generate_series(COALESCE(startserie, 1), COALESCE(stopserie,99999)) gs(n) except select numero_interne from remocra.hydrant h join remocra.type_hydrant_nature thn on (h.nature = thn.id) where
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
$function$
;

COMMIT;
