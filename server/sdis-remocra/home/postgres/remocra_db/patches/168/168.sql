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
    numero_patch := 168;
    description_patch := 'OPTIMISATION "dernier_changement_detat" dans hydrant';

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

-- Ajout du champs dans la table hydrant début
  ALTER TABLE remocra.hydrant
  ADD COLUMN IF NOT EXISTS date_changement_dispo_terrestre timestamp without time zone NULL;
  COMMENT ON COLUMN remocra.hydrant.date_changement_dispo_terrestre IS 'Date du dernier changement d''état';
-- Ajout du champs dans la table hydrant fin

-- Création du trigger et de la fonction qui remplira les champs date_changement_dispo_terrestre début
  CREATE OR REPLACE FUNCTION remocra.trg_date_changement_dispo_terrestre()
    RETURNS trigger
    LANGUAGE plpgsql
  AS $function$
    DECLARE
    p_rec_old record;
    p_rec_new record;
    BEGIN
      p_rec_old = OLD;
      p_rec_new = NEW;
      --SI la dispo a changé on met la date du jour ou si l'hydrant n'existait pas (insertion)
      if (p_rec_old.dispo_terrestre != p_rec_new.dispo_terrestre OR p_rec_old IS NULL) then
        UPDATE remocra.hydrant
        SET date_changement_dispo_terrestre=NOW()
        WHERE id=p_rec_new.id;

      end if;
      RETURN p_rec_new;
    END;
  $function$
  ;

  DROP TRIGGER IF EXISTS trig_date_changement_dispo_terrestre ON remocra.hydrant;
  CREATE TRIGGER trig_date_changement_dispo_terrestre AFTER
    INSERT
    OR
    UPDATE
    ON
    remocra.hydrant FOR EACH ROW EXECUTE PROCEDURE remocra.trg_date_changement_dispo_terrestre();
-- Création du trigger et de la fonction qui remplira les champs date_changement_dispo_terrestre FIN


--MISE A JOUR DE TOUT LES HYDRANT DEJA PRESENT EN BASE début

--Fonction de récupération de la dernière date de changement.
--Si pas de changement récupération de la date d'insert en BDD
--Si toujours pas alors date du jour
create or replace function init_date_changement_etat(idHydrant bigint)
  RETURNS timestamp without time zone
  LANGUAGE plpgsql
  AS $function$
  declare
  dateChangement timestamp without time zone;
  begin
    select min(t.date_operation) into dateChangement
    from tracabilite.hydrant t
    where t.id_hydrant = idHydrant and date_operation > (
      select max(t2.date_operation)
      from tracabilite.hydrant t2
      where t2.id_hydrant = idHydrant and t.dispo_terrestre != t2.dispo_terrestre
    );

    if dateChangement is null then
      dateChangement = coalesce(
        (select min(date_operation)
        from tracabilite.hydrant
        where id_hydrant = idHydrant),
        NOW()
      );
    end if;

    return dateChangement;
  end;
$function$
;

--Lancement de l'update (ça peut etre TRES LONG !!!)
UPDATE remocra.hydrant
SET date_changement_dispo_terrestre = init_date_changement_etat(id)
;
--Suppression de la fonction car nous n'en aurons plus besoin
DROP FUNCTION IF EXISTS remocra.init_date_changement_etat();
--MISE A JOUR DE TOUT LES HYDRANT DEJA PRESENT EN BASE fin

-- Contenu réel du patch fin
--------------------------------------------------
COMMIT;
