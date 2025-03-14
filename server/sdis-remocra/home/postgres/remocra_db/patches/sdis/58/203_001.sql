CREATE OR REPLACE FUNCTION remocra.nextnumerointerne(idhydrant bigint, codehydrant character varying, naturehydrant bigint, idcommune bigint, startserie bigint, stopserie bigint)
 RETURNS bigint
 LANGUAGE plpgsql
AS $function$
DECLARE returned bigint;
BEGIN
	SELECT n INTO returned
	FROM  generate_series(COALESCE(startserie, 1), COALESCE(stopserie, 99999)) gs(n)
		except
	SELECT numero_interne
	FROM remocra.hydrant h
	WHERE
		(case when idhydrant is not null then h.id != idhydrant else true end)
			and (case
  					when codehydrant = 'PIBI' then h.code = codehydrant
  					when codehydrant = 'PENA' then
  						CASE WHEN naturehydrant = (select id from remocra.type_hydrant_nature where code = 'PU') THEN h.nature = naturehydrant
  						ELSE true END
  					ELSE TRUE END)
			and (case when idcommune is not null then h.commune = idcommune else true end)
	order BY 1
	limit 1;

	return returned;
end;

$function$
;