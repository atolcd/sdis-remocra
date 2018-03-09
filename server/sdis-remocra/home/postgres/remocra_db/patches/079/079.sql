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
    numero_patch := 79;
    description_patch := 'Fonction de calculInduispo';

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

-- Function: remocra.calcul_indispo(bigint)

-- DROP FUNCTION remocra.calcul_indispo(bigint);

CREATE OR REPLACE FUNCTION remocra.calcul_indispo(id_hydrant bigint)
  RETURNS void AS
$BODY$
DECLARE
	p_hasNC boolean := false;
	p_code_nature varchar;
	nbTerr integer := 0;
	nbHBE  integer := 0;
	dispoTerrestre varchar;
	dispoHbe varchar;
	p_anomalies record;
	c_anomalies CURSOR (idCurs integer,code_nat varchar) is
		select distinct
			hy.id as hydrant,
			thya.code as code_anomalie,
			thyan.val_indispo_hbe as val_indispo_hbe,
			thyan.val_indispo_terrestre as val_indispo_terrestre,
			thyn.code as code_nature
		from remocra.hydrant hy
		join remocra.hydrant_anomalies hya on (hy.id = hya.hydrant)
		join remocra.type_hydrant_anomalie thya on(thya.id =hya.anomalies)
		join remocra.type_hydrant_anomalie_nature thyan on (thya.id = thyan.anomalie)
		join remocra.type_hydrant_nature thyn on (thyan.nature = thyn.id)
		where thyn.code = code_nat
		and hy.id = idCurs;
BEGIN
   select thyn.code into p_code_nature from remocra.hydrant hy join remocra.type_hydrant_nature thyn on (hy.nature = thyn.id) where hy.id = id_hydrant;
   if FOUND then
    FOR p_anomalies in c_anomalies(id_hydrant,p_code_nature) LOOP
		if(p_anomalies.val_indispo_terrestre is not null) then
			if(p_anomalies.code_anomalie  like '%_NC') then
				p_hasNC := true;
			end if;
			nbTerr := nbTerr + p_anomalies.val_indispo_terrestre;
		end if;
		if(p_anomalies.val_indispo_hbe is not null) then
			nbHBE := nbHBE + p_anomalies.val_indispo_hbe;
		end if;
	END LOOP;

	if(nbTerr >= 5) then
		dispoTerrestre := 'INDISPO';
	else
      if(p_hasNC) then
		dispoTerrestre := 'NON_CONFORME';
      else
		dispoTerrestre := 'DISPO';
      end if;
	end if;

	if(nbHBE  >= 5) then
		dispoHbe := 'INDISPO';
	else
		dispoHbe := 'DISPO';
	end if;

      if((select h.date_recep from remocra.hydrant h where h.id = id_hydrant) is null ) then
         dispoHbe := 'INDISPO';
         dispoTerrestre := 'INDISPO';
      end if;

	update remocra.hydrant
	set dispo_hbe = dispoHbe,
	dispo_terrestre = dispoTerrestre
	where id = id_hydrant;

  end if;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION remocra.calcul_indispo(bigint)
  OWNER TO postgres;



-- Contenu réel du patch fin
--------------------------------------------------

commit;


