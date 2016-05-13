begin;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pdi, public, pg_catalog;


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

	update remocra.hydrant
	set dispo_hbe = dispoHbe,
	dispo_terrestre = dispoTerrestre
	where id = id_hydrant;

  end if;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE;


-- Suivi du patch
insert into remocra.suivi_patches(numero, description) values(
  51,
  'Reprise trigger remocra.calcul_indispo (ambiguité sur id)'
);

commit;