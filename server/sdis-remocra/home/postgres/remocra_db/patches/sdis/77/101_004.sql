begin;

-- View: pdi.vue_commune_ou_interco_one

-- DROP VIEW pdi.vue_commune_ou_interco_one;

CREATE OR REPLACE VIEW pdi.vue_commune_ou_interco_one AS 
 SELECT org.id, ((org.nom::text || ' ('::text) || torg.type::text) || ')'::text AS libelle
   FROM remocra.organisme org
   JOIN ( SELECT type_organisme.id, type_organisme.code AS type
           FROM remocra.type_organisme
          WHERE type_organisme.code::text = ANY (ARRAY['COMMUNE'::character varying::text, 'EPCI'::character varying::text])) torg ON torg.id = org.type_organisme
  WHERE org.actif AND COALESCE(org.email_contact, ''::character varying)::text <> ''::text
  ORDER BY ((org.nom::text || ' ('::text) || torg.type::text) || ')'::text;

ALTER TABLE pdi.vue_commune_ou_interco_one
  OWNER TO postgres;

commit;

