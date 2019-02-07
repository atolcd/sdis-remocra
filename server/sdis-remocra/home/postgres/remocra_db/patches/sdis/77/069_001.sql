
begin;


﻿-- Création du schéma et de la structure d'échange de données avec le SGO
DROP SCHEMA IF EXISTS remocra_sgo CASCADE;
CREATE SCHEMA remocra_sgo;
COMMENT ON SCHEMA remocra_sgo IS 'Tables, vues et fonctions dédiées aux échanges avec le système de gestion opérationnelle du SDISS 77';
CREATE TABLE remocra_sgo.hydrant_tracabilite (
	date_message timestamp without time zone NOT NULL,
	num character varying(6) NOT NULL,
	na_pt_code character varying(3) NOT NULL,
	dep_num character varying(2) NOT NULL,
	cod_insee character varying(3) NOT NULL,
	etat_pt_eau_cod character varying(2) NOT NULL,
	cs_code character varying(50) NOT NULL,
	statut character varying(1) NOT NULL,
	adr_1 character varying(32),
	adr_2 character varying(32),
	cp character varying(5),
	ville character varying(26),
	num_voie character varying(5),
	coord_x double precision,
	coord_y double precision,
	debit double precision,
	capacite double precision,
	diam_canal double precision,
	cod_valid character varying(1),
	date_maj_remocra date NOT NULL,
	type_maj character varying NOT NULL,
	flag_flux character varying
);
COMMENT ON TABLE remocra_sgo.hydrant_tracabilite IS 'Message précisant un changements réalisé sur un point d''eau. Structure de table adapatée pour faciliter une mise à jour du SGO ARTEMIS. La colonne "flag_flux" doit être non nulle dés lors que les informations du messages ont été prises en compte dans le SGO';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.date_message IS 'Date et heure de la création du message de changement';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.num IS 'Numéro du PEI dans REMOCRA';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.na_pt_code IS 'Code de la nature du PEI';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.dep_num IS 'Numéro de département';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.cod_insee IS '3 derniers numéro du code INSEE de la commune';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.etat_pt_eau_cod IS 'Code de disponibilité du PEI';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.cs_code IS 'Code du centre de secours associé : CIS opérationnel';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.statut IS 'Code de la nature juridique du PEI : public ou privé';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.adr_1 IS 'Adresse du PEI';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.adr_2 IS 'Adresse du PEI';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.cp IS 'Code postal de l''adresse';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.ville IS 'Ville de l''adresse';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.num_voie IS 'Numéro dans la voie';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.coord_x IS 'Coordonnée X en Lambert 93 en m';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.coord_y IS 'Coordonnée Y en Lambert 93 en m';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.debit IS 'Débit du PEI en M3/h';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.capacite IS 'Capacité du PEI en M3';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.diam_canal IS 'Diamètre des canalisation d''alimentation';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.cod_valid IS 'A RENSEIGNER';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.date_maj_remocra IS 'Date de l''action de mise à jour, de création ou de supression des informations du PEI dans REMOCRA';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.type_maj IS 'Type d''opération éklementaire réalisée dans REMOCRA : création, mise à jour ou suppression de PEI';
COMMENT ON COLUMN remocra_sgo.hydrant_tracabilite.flag_flux IS 'Témoin de prise en compte de l''information par le le système de gestion opérationnelle';

CREATE TABLE remocra_sgo.hydrant_nature (
	code_nature_sgo character varying NOT NULL,
	code_nature_remocra character varying  UNIQUE NOT NULL
);
COMMENT ON TABLE remocra_sgo.hydrant_nature IS 'Correspondance entre la nature de point d''eau ARTEMIS et la combinaison de nature de PEI, de diametre de PIBI ou de positionnement de réserve';
COMMENT ON COLUMN remocra_sgo.hydrant_nature.code_nature_sgo IS 'Code de la nature du PEI dans ARTEMIS';
COMMENT ON COLUMN remocra_sgo.hydrant_nature.code_nature_remocra IS 'Code de combinbaison REMOCRA : code de "type_hydrant_nature" + "_" + code de "type_hydrant_diametre" + code de "type_hydrant_positionnement"';

INSERT INTO remocra_sgo.hydrant_nature VALUES ('01','PI_DIAM70');
INSERT INTO remocra_sgo.hydrant_nature VALUES ('02','PI_DIAM100');
INSERT INTO remocra_sgo.hydrant_nature VALUES ('03','PI_DIAM150');
INSERT INTO remocra_sgo.hydrant_nature VALUES ('04','BI_DIAM80');
INSERT INTO remocra_sgo.hydrant_nature VALUES ('05','BI_DIAM100');
INSERT INTO remocra_sgo.hydrant_nature VALUES ('06','BI_DIAM150');
INSERT INTO remocra_sgo.hydrant_nature VALUES ('07','CI_FIXE_AIR_LIBRE');
INSERT INTO remocra_sgo.hydrant_nature VALUES ('08','CI_FIXE_ENTERRE');
INSERT INTO remocra_sgo.hydrant_nature VALUES ('09','PA_DIAM70');
INSERT INTO remocra_sgo.hydrant_nature VALUES ('09','PA_DIAM80');
INSERT INTO remocra_sgo.hydrant_nature VALUES ('09','PA_DIAM100');
INSERT INTO remocra_sgo.hydrant_nature VALUES ('09','PA_DIAM150');
INSERT INTO remocra_sgo.hydrant_nature VALUES ('10','PU');
INSERT INTO remocra_sgo.hydrant_nature VALUES ('12','CE_DIAM70');
INSERT INTO remocra_sgo.hydrant_nature VALUES ('13','CE_DIAM100');
INSERT INTO remocra_sgo.hydrant_nature VALUES ('14','CE_DIAM150');


commit;

