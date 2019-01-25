--
-- SDIS09 - Mise à disposition des disponibilités des PEI du SGO SYSTEL
--
-- En complément du patch server/sdis-remocra/home/postgres/remocra_db/patches/sdis/09/094_003.sql
-- * intégration du débit à 1 bar

begin;


CREATE OR REPLACE VIEW remocra_sgo.hydrant AS
SELECT
	h.numero::character varying AS "NUMERO_POINT_EAU",
	h.numero::character varying AS "NOM_POINT_EAU",
	nat.code_nature_sgo::character varying AS "NUMERO_TYPE_POINT_EAU",
	null::character varying AS "NUMERO_LIEU",
	null::integer AS "NUMERO_DANS_RUE",
	null::integer AS "PRECISIONS_DANS_RUE",
	null::character varying AS "NUMERO_ESCALIER",
	null::character varying AS "NUMERO_ETAGE",
	hpibi.pression::float AS "PRESSION",
	hpibi.debit_max::integer AS "DEBIT",
	null::integer AS "DIAMETRE",
	date_contr::timestamp AS "DH_VERIFICATION",
	null::character varying AS "ACCES",
	dispo.code_disponibilite_sgo::character varying AS "ETAT_POINT_EAU",
	st_x(h.geometrie)::float AS "COORD_X",
	st_y(h.geometrie)::float AS "COORD_Y",
	null::character varying AS "COORDONNEE_ALPHA",
	null::character varying AS "FEUILLE_IGN",
	null::character varying AS "NUM_PLAN",
	null::character varying AS "ID_RESEAU",
	null::timestamp AS "DH_DEB_INDISPO",
	null::timestamp AS "DH_FIN_INDISPO",
	hpibi.debit::integer AS "DEBIT_1BAR",
	null::integer AS "DIAMETRE_CANALISATION",
	null::character varying AS "NON_CONFORMITE",
	null::integer AS "VOLUME",
	null::integer AS "ID_GEO",
	com.insee::character varying AS "NUMERO_COMMUNE",
	null::character varying AS "ETAT_POINT_EAU_AVANT_INDISPO",
	null::character varying AS "OBJET_PT_EAU",
	null::bigint AS "CLEGCES"
FROM
	remocra.hydrant h
	LEFT JOIN remocra.hydrant_pibi hpibi ON (hpibi.id = h.id)
	LEFT JOIN remocra.hydrant_pena hpena ON (hpena.id = h.id)
	JOIN remocra.type_hydrant_nature thnat ON (thnat.id = h.nature)
	JOIN remocra_sgo.hydrant_nature nat ON (nat.code_nature_remocra = thnat.code)
	JOIN remocra_sgo.hydrant_disponibilite dispo ON (dispo.code_disponibilite_remocra = h.dispo_terrestre)
	JOIN remocra.commune com ON (com.id = h.commune);


commit;

