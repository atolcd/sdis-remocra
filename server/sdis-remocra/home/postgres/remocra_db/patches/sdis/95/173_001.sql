CREATE OR REPLACE VIEW remocra_sgo.hydrant
AS SELECT h.numero AS "NUMERO_POINT_EAU",
          h.numero AS "NOM_POINT_EAU",
          nat.code_nature_sgo AS "NUMERO_TYPE_POINT_EAU",
          NULL::character varying AS "NUMERO_LIEU",
    NULL::integer AS "NUMERO_DANS_RUE",
    NULL::integer AS "PRECISIONS_DANS_RUE",
    NULL::character varying AS "NUMERO_ESCALIER",
    NULL::character varying AS "NUMERO_ETAGE",
    hpibi.pression AS "PRESSION",
    hpibi.debit_max AS "DEBIT",
    NULL::integer AS "DIAMETRE",
    h.date_contr AS "DH_VERIFICATION",
    h.voie AS "ACCES",
    dispo.code_disponibilite_sgo AS "ETAT_POINT_EAU",
    st_x(st_transform(h.geometrie, 27572)) AS "COORD_X",
    st_y(st_transform(h.geometrie, 27572)) AS "COORD_Y",
    NULL::character varying AS "COORDONNEE_ALPHA",
    NULL::character varying AS "FEUILLE_IGN",
    NULL::character varying AS "NUM_PLAN",
    NULL::character varying AS "ID_RESEAU",
    NULL::timestamp without time zone AS "DH_DEB_INDISPO",
    NULL::timestamp without time zone AS "DH_FIN_INDISPO",
    hpibi.debit AS "DEBIT_1BAR",
    NULL::integer AS "DIAMETRE_CANALISATION",
    NULL::character varying AS "NON_CONFORMITE",
    NULL::integer AS "VOLUME",
    NULL::integer AS "ID_GEO",
    com.insee AS "NUMERO_COMMUNE",
    NULL::character varying AS "ETAT_POINT_EAU_AVANT_INDISPO",
    NULL::character varying AS "OBJET_PT_EAU",
    NULL::bigint AS "CLEGCES"
   FROM hydrant h
              LEFT JOIN hydrant_pibi hpibi ON hpibi.id = h.id
              LEFT JOIN hydrant_pena hpena ON hpena.id = h.id
              JOIN type_hydrant_nature thnat ON thnat.id = h.nature
              JOIN remocra_sgo.hydrant_nature nat ON nat.code_nature_remocra::text = thnat.code::text
              JOIN remocra_sgo.hydrant_disponibilite dispo ON dispo.code_disponibilite_remocra::text = h.dispo_terrestre::text
              JOIN commune com ON com.id = h.commune;
