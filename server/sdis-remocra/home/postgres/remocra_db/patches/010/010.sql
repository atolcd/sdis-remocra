SET search_path = remocra, pdi, pg_catalog;


BEGIN;

DROP TABLE IF EXISTS  remocra.tournee CASCADE;
DROP TABLE IF EXISTS  remocra.hydrant_photos CASCADE;
DROP TABLE IF EXISTS  remocra.hydrant_anomalies CASCADE;
DROP TABLE IF EXISTS  remocra.hydrant_document CASCADE;
DROP TABLE IF EXISTS  remocra.hydrant CASCADE;
DROP TABLE IF EXISTS  remocra.hydrant_pena CASCADE;
DROP TABLE IF EXISTS  remocra.hydrant_pibi CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_sous_type CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_critere CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_anomalie CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_diametre CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_domaine CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_marque CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_modele CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_materiau CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_positionnement CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_usage CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_vol_constate CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_anomalie CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_anomalie_nature CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_anomalie_nature_saisies CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_diametre_natures CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_nature CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant CASCADE;
DROP TABLE IF EXISTS  remocra.type_hydrant_saisie CASCADE;


DELETE FROM remocra.param_conf where cle in ('HYDRANT_DELAI_CTRL_URGENT','HYDRANT_DELAI_CTRL_WARN', 'HYDRANT_DELAI_RECO_URGENT', 'HYDRANT_DELAI_RECO_WARN', 'HYDRANT_RENOUVELLEMENT_CTRL','HYDRANT_RENOUVELLEMENT_RECO');
INSERT INTO remocra.param_conf (cle, description, valeur, version) VALUES ('HYDRANT_DELAI_CTRL_URGENT', 'Nombre de jours avant échéance où un contrôle est considéré comme "urgent"', '15', 1);
INSERT INTO remocra.param_conf (cle, description, valeur, version) VALUES ('HYDRANT_DELAI_CTRL_WARN', 'Nombre de jours avant échéance où un contrôle est considéré comme "à faire bientôt"', '45', 1);
INSERT INTO remocra.param_conf (cle, description, valeur, version) VALUES ('HYDRANT_DELAI_RECO_URGENT', 'Nombre de jours avant échéance où une reconnaisance est considérée comme "urgente"', '15', 1);
INSERT INTO remocra.param_conf (cle, description, valeur, version) VALUES ('HYDRANT_DELAI_RECO_WARN', 'Nombre de jours avant échéance où une reconnaisance est considérée comme "à faire bientôt"', '45', 1);
INSERT INTO remocra.param_conf (cle, description, valeur, version) VALUES ('HYDRANT_RENOUVELLEMENT_CTRL', 'Délai légal entre 2 contrôles (en jours)', '1095', 1);
INSERT INTO remocra.param_conf (cle, description, valeur, version) VALUES ('HYDRANT_RENOUVELLEMENT_RECO', 'Délai légal entre 2 reconnaissances (en jours)', '365', 1);


CREATE TABLE hydrant (
    id bigint NOT NULL,
    agent1 character varying,
    agent2 character varying,
    annee_fabrication integer,
    code character varying,
    complement character varying,
    date_contr timestamp without time zone,
    date_gps timestamp without time zone,
    date_recep timestamp without time zone,
    date_reco timestamp without time zone,
    date_verif timestamp without time zone,
    dispo_admin character varying,
    dispo_hbe character varying,
    dispo_terrestre character varying,
    geometrie public.geometry NOT NULL,
    lieu_dit character varying,
    numero character varying,
    numero_interne integer,
    observation character varying,
    version integer DEFAULT 1,
    voie character varying,
    voie2 character varying,
    commune bigint,
    domaine bigint,
    nature bigint,
    tournee bigint,
    date_modification timestamp without time zone,
    courrier character varying,
    gest_point_eau character varying,
    organisme bigint
);

ALTER TABLE remocra.hydrant OWNER TO postgres;

CREATE TABLE hydrant_anomalies (
    hydrant bigint NOT NULL,
    anomalies bigint NOT NULL
);

ALTER TABLE remocra.hydrant_anomalies OWNER TO postgres;

CREATE TABLE hydrant_document (
    id bigint NOT NULL,
    document bigint NOT NULL,
    hydrant bigint NOT NULL
);

ALTER TABLE remocra.hydrant_document OWNER TO postgres;

CREATE SEQUENCE hydrant_document_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.hydrant_document_id_seq OWNER TO postgres;

ALTER SEQUENCE hydrant_document_id_seq OWNED BY hydrant_document.id;

SELECT pg_catalog.setval('hydrant_document_id_seq', 1, true);

CREATE SEQUENCE hydrant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.hydrant_id_seq OWNER TO postgres;

ALTER SEQUENCE hydrant_id_seq OWNED BY hydrant.id;

SELECT pg_catalog.setval('hydrant_id_seq', 6, true);

CREATE TABLE hydrant_pena (
    capacite character varying,
    coorddfci character varying,
    hbe boolean DEFAULT false,
    piste character varying,
    id bigint NOT NULL,
    materiau bigint,
    positionnement bigint,
    vol_constate bigint,
    q_appoint double precision
);

ALTER TABLE remocra.hydrant_pena OWNER TO postgres;

CREATE TABLE hydrant_pibi (
    debit integer,
    debit_max integer,
    pression double precision,
    pression_dyn double precision,
    id bigint NOT NULL,
    diametre bigint,
    gest_reseau character varying,
    numeroscp character varying,
    choc boolean,
    marque bigint,
    modele bigint,
    pena bigint
);

ALTER TABLE remocra.hydrant_pibi OWNER TO postgres;

CREATE TABLE tournee (
    id bigint NOT NULL,
    deb_sync timestamp without time zone,
    last_sync timestamp without time zone,
    version integer DEFAULT 1,
    affectation bigint
);

ALTER TABLE remocra.tournee OWNER TO postgres;

CREATE SEQUENCE tournee_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.tournee_id_seq OWNER TO postgres;

ALTER SEQUENCE tournee_id_seq OWNED BY tournee.id;

SELECT pg_catalog.setval('tournee_id_seq', 1, false);

CREATE TABLE type_hydrant (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);

ALTER TABLE remocra.type_hydrant OWNER TO postgres;

CREATE TABLE type_hydrant_anomalie (
    id bigint NOT NULL,
    actif boolean DEFAULT true,
    code character varying NOT NULL,
    commentaire character varying,
    nom character varying NOT NULL,
    version integer DEFAULT 1,
    critere bigint
);

ALTER TABLE remocra.type_hydrant_anomalie OWNER TO postgres;

CREATE SEQUENCE type_hydrant_anomalie_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.type_hydrant_anomalie_id_seq OWNER TO postgres;

ALTER SEQUENCE type_hydrant_anomalie_id_seq OWNED BY type_hydrant_anomalie.id;

SELECT pg_catalog.setval('type_hydrant_anomalie_id_seq', 107, true);

CREATE TABLE type_hydrant_anomalie_nature (
    id bigint NOT NULL,
    val_indispo_admin integer,
    val_indispo_hbe integer,
    val_indispo_terrestre integer,
    version integer,
    anomalie bigint NOT NULL,
    nature bigint NOT NULL
);

ALTER TABLE remocra.type_hydrant_anomalie_nature OWNER TO postgres;

CREATE SEQUENCE type_hydrant_anomalie_nature_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.type_hydrant_anomalie_nature_id_seq OWNER TO postgres;

ALTER SEQUENCE type_hydrant_anomalie_nature_id_seq OWNED BY type_hydrant_anomalie_nature.id;

SELECT pg_catalog.setval('type_hydrant_anomalie_nature_id_seq', 137, true);

CREATE TABLE type_hydrant_anomalie_nature_saisies (
    type_hydrant_anomalie_nature bigint NOT NULL,
    saisies bigint NOT NULL
);

ALTER TABLE remocra.type_hydrant_anomalie_nature_saisies OWNER TO postgres;

CREATE TABLE type_hydrant_critere (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);

ALTER TABLE remocra.type_hydrant_critere OWNER TO postgres;

CREATE SEQUENCE type_hydrant_critere_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.type_hydrant_critere_id_seq OWNER TO postgres;

ALTER SEQUENCE type_hydrant_critere_id_seq OWNED BY type_hydrant_critere.id;

SELECT pg_catalog.setval('type_hydrant_critere_id_seq', 9, true);

CREATE TABLE type_hydrant_diametre (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);

ALTER TABLE remocra.type_hydrant_diametre OWNER TO postgres;

CREATE SEQUENCE type_hydrant_diametre_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.type_hydrant_diametre_id_seq OWNER TO postgres;

ALTER SEQUENCE type_hydrant_diametre_id_seq OWNED BY type_hydrant_diametre.id;

SELECT pg_catalog.setval('type_hydrant_diametre_id_seq', 3, true);

CREATE TABLE type_hydrant_diametre_natures (
    type_hydrant_diametre bigint NOT NULL,
    natures bigint NOT NULL
);

ALTER TABLE remocra.type_hydrant_diametre_natures OWNER TO postgres;

CREATE TABLE type_hydrant_domaine (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);

ALTER TABLE remocra.type_hydrant_domaine OWNER TO postgres;

CREATE SEQUENCE type_hydrant_domaine_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.type_hydrant_domaine_id_seq OWNER TO postgres;

ALTER SEQUENCE type_hydrant_domaine_id_seq OWNED BY type_hydrant_domaine.id;

SELECT pg_catalog.setval('type_hydrant_domaine_id_seq', 5, true);

CREATE SEQUENCE type_hydrant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.type_hydrant_id_seq OWNER TO postgres;

ALTER SEQUENCE type_hydrant_id_seq OWNED BY type_hydrant.id;

SELECT pg_catalog.setval('type_hydrant_id_seq', 2, true);

CREATE TABLE type_hydrant_marque (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);

ALTER TABLE remocra.type_hydrant_marque OWNER TO postgres;

CREATE SEQUENCE type_hydrant_marque_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.type_hydrant_marque_id_seq OWNER TO postgres;

ALTER SEQUENCE type_hydrant_marque_id_seq OWNED BY type_hydrant_marque.id;

SELECT pg_catalog.setval('type_hydrant_marque_id_seq', 5, true);

CREATE TABLE type_hydrant_materiau (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);

ALTER TABLE remocra.type_hydrant_materiau OWNER TO postgres;

CREATE SEQUENCE type_hydrant_materiau_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.type_hydrant_materiau_id_seq OWNER TO postgres;

ALTER SEQUENCE type_hydrant_materiau_id_seq OWNED BY type_hydrant_materiau.id;

SELECT pg_catalog.setval('type_hydrant_materiau_id_seq', 5, true);

CREATE TABLE type_hydrant_modele (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1,
    marque bigint
);

ALTER TABLE remocra.type_hydrant_modele OWNER TO postgres;

CREATE SEQUENCE type_hydrant_modele_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.type_hydrant_modele_id_seq OWNER TO postgres;

ALTER SEQUENCE type_hydrant_modele_id_seq OWNED BY type_hydrant_modele.id;

SELECT pg_catalog.setval('type_hydrant_modele_id_seq', 27, true);

CREATE TABLE type_hydrant_nature (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer,
    type_hydrant bigint
);

ALTER TABLE remocra.type_hydrant_nature OWNER TO postgres;

CREATE SEQUENCE type_hydrant_nature_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.type_hydrant_nature_id_seq OWNER TO postgres;

ALTER SEQUENCE type_hydrant_nature_id_seq OWNED BY type_hydrant_nature.id;

SELECT pg_catalog.setval('type_hydrant_nature_id_seq', 8, true);

CREATE TABLE type_hydrant_positionnement (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);

ALTER TABLE remocra.type_hydrant_positionnement OWNER TO postgres;

CREATE SEQUENCE type_hydrant_positionnement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.type_hydrant_positionnement_id_seq OWNER TO postgres;

ALTER SEQUENCE type_hydrant_positionnement_id_seq OWNED BY type_hydrant_positionnement.id;

SELECT pg_catalog.setval('type_hydrant_positionnement_id_seq', 3, true);

CREATE TABLE type_hydrant_saisie (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer
);

ALTER TABLE remocra.type_hydrant_saisie OWNER TO postgres;

CREATE SEQUENCE type_hydrant_saisie_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.type_hydrant_saisie_id_seq OWNER TO postgres;

ALTER SEQUENCE type_hydrant_saisie_id_seq OWNED BY type_hydrant_saisie.id;

SELECT pg_catalog.setval('type_hydrant_saisie_id_seq', 6, true);

CREATE TABLE type_hydrant_vol_constate (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);

ALTER TABLE remocra.type_hydrant_vol_constate OWNER TO postgres;

CREATE SEQUENCE type_hydrant_vol_constate_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE remocra.type_hydrant_vol_constate_id_seq OWNER TO postgres;

ALTER SEQUENCE type_hydrant_vol_constate_id_seq OWNED BY type_hydrant_vol_constate.id;

SELECT pg_catalog.setval('type_hydrant_vol_constate_id_seq', 5, true);

ALTER TABLE ONLY hydrant ALTER COLUMN id SET DEFAULT nextval('hydrant_id_seq'::regclass);

ALTER TABLE ONLY hydrant_document ALTER COLUMN id SET DEFAULT nextval('hydrant_document_id_seq'::regclass);

ALTER TABLE ONLY tournee ALTER COLUMN id SET DEFAULT nextval('tournee_id_seq'::regclass);

ALTER TABLE ONLY type_hydrant ALTER COLUMN id SET DEFAULT nextval('type_hydrant_id_seq'::regclass);

ALTER TABLE ONLY type_hydrant_anomalie ALTER COLUMN id SET DEFAULT nextval('type_hydrant_anomalie_id_seq'::regclass);

ALTER TABLE ONLY type_hydrant_anomalie_nature ALTER COLUMN id SET DEFAULT nextval('type_hydrant_anomalie_nature_id_seq'::regclass);

ALTER TABLE ONLY type_hydrant_critere ALTER COLUMN id SET DEFAULT nextval('type_hydrant_critere_id_seq'::regclass);

ALTER TABLE ONLY type_hydrant_diametre ALTER COLUMN id SET DEFAULT nextval('type_hydrant_diametre_id_seq'::regclass);

ALTER TABLE ONLY type_hydrant_domaine ALTER COLUMN id SET DEFAULT nextval('type_hydrant_domaine_id_seq'::regclass);

ALTER TABLE ONLY type_hydrant_marque ALTER COLUMN id SET DEFAULT nextval('type_hydrant_marque_id_seq'::regclass);

ALTER TABLE ONLY type_hydrant_materiau ALTER COLUMN id SET DEFAULT nextval('type_hydrant_materiau_id_seq'::regclass);

ALTER TABLE ONLY type_hydrant_modele ALTER COLUMN id SET DEFAULT nextval('type_hydrant_modele_id_seq'::regclass);

ALTER TABLE ONLY type_hydrant_nature ALTER COLUMN id SET DEFAULT nextval('type_hydrant_nature_id_seq'::regclass);

ALTER TABLE ONLY type_hydrant_positionnement ALTER COLUMN id SET DEFAULT nextval('type_hydrant_positionnement_id_seq'::regclass);

ALTER TABLE ONLY type_hydrant_saisie ALTER COLUMN id SET DEFAULT nextval('type_hydrant_saisie_id_seq'::regclass);

ALTER TABLE ONLY type_hydrant_vol_constate ALTER COLUMN id SET DEFAULT nextval('type_hydrant_vol_constate_id_seq'::regclass);

INSERT INTO type_hydrant (id, actif, code, nom, version) VALUES (1, true, 'PIBI', 'PIBI', 1);
INSERT INTO type_hydrant (id, actif, code, nom, version) VALUES (2, true, 'PENA', 'PENA', 1);

INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (4, true, '', '', 'A numéroter', 3, 9);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (94, false, '', '', 'Aire de manœuvre non utilisable', 0, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (6, true, '', '', 'A protéger', 2, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (95, false, '', '', 'Anomalie hauteur ou longueur aspiration', 0, 4);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (102, false, '', '', 'Echelle extérieure hors d’usage', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (103, false, '', '', 'Passerelle hors service (rendant l’usage impossible)', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (104, false, '', '', 'Trappe HBE Ouverture hors service (interdisant le pompage) ', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (105, false, '', '', 'Volume théorique identifié (flanc ou panneau)', 0, 9);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (2, true, '', NULL, 'A débroussailler', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (3, true, '', NULL, 'A déplacer', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (5, true, '', NULL, 'A peindre', 1, 3);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (7, true, '', NULL, 'A tourner', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (8, true, '', NULL, 'Abords à dégager', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (11, true, '', NULL, 'Aire de pompage Ø 30 m non conforme', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (13, true, '', NULL, 'Aire de sécurité Ø 30 m non conforme (inclut l’aire de poser)', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (14, true, '', NULL, 'Ancrage', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (15, true, '', NULL, 'Approche(s) > 50 m non conforme(s)', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (16, true, '', NULL, 'Bandeau jaune absent au 1/3 supérieur', 1, 9);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (20, true, '', NULL, 'Capot casse - manquant', 1, 3);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (21, true, '', NULL, 'Carre de manœuvre hors service', 1, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (23, true, '', NULL, 'Corrosion', 1, 3);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (24, true, '', NULL, 'Couleur non normalisée (A repeindre)', 1, 3);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (27, true, '', NULL, 'Débroussaillement et/ou éclaircie à faire (couronne de 25 m autour de l’aire de manœuvre)', 1, 8);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (28, true, '', NULL, 'Distance > 5m', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (31, true, '', NULL, 'En travaux', 1, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (32, true, '', NULL, 'Fermeture hors service (Fuite)', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (33, true, '', NULL, 'Fuite', 1, 4);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (34, true, '', NULL, 'Fuite ½ raccord', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (35, true, '', NULL, 'Fuite à la base', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (36, true, '', NULL, 'Fuite au purgeur', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (37, true, '', NULL, 'Fuite au volant', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (38, true, '', NULL, 'Grippé', 1, 4);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (39, true, '', NULL, 'Hauteur géométrique', 1, 4);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (41, true, '', NULL, 'Hydrant détruit – Cassé', 1, 4);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (42, true, '', NULL, 'Hydrant enterré', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (43, true, '', NULL, 'Immatriculation au-dessus absente ou erronée', 1, 9);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (44, true, '', NULL, 'Immatriculation sur (flanc ou panneau) absente ou erronée (pour tout type)', 1, 9);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (48, true, '', NULL, 'Inaccessible aux engins', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (50, true, '', NULL, 'Introuvable', 1, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (51, true, '', NULL, 'Isolation (Bouche à Clef) Hors service', 1, 5);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (52, true, '', NULL, 'Isolation (Bouche à Clef) Introuvable', 1, 5);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (55, true, '', NULL, 'Manque bouchon 100', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (56, true, '', NULL, 'Manque bouchon 70', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (57, true, '', NULL, 'Manque deux bouchons', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (58, true, '', NULL, 'Manque plaque signalétique (BI uniquement)', 1, 9);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (59, true, '', NULL, 'Obstacle à l’ouverture', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (62, true, '', NULL, 'Pi sous coffre', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (63, true, '', NULL, 'Pi sous scellé', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (65, true, '', NULL, 'Proche d’une installation électrique', 1, 8);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (68, true, '', NULL, 'Sans eau', 1, 4);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (69, true, '', NULL, 'Socle de propreté', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (70, true, '', NULL, 'Stabilité / verticalité', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (76, true, '', NULL, 'Vidange colonne', 1, 6);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (77, true, '', NULL, 'Volant de manœuvre absent, cassé Carré non normalisé', 1, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (79, true, '', NULL, 'Volume de dégagement insuffisant (50 cm autour hydrant)', 1, 1);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (80, true, 'DEBIT_INSUFF', NULL, 'Débit insuffisant', 1, NULL);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (81, true, 'DEBIT_TROP_ELEVE', NULL, 'Débit trop élevé', 1, NULL);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (82, true, 'DEBIT_INSUFF_NC', '', 'Débit insuffisant (non conforme à la NFS 62 200)', 3, NULL);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (47, true, '', '', 'Inaccessible – Cheminement impraticable', 2, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (12, true, '', '', 'Aire de poser HBE lisse Ø 10 m non conforme', 2, 2);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (22, true, '', '', 'Peinture verte (refaire)', 2, 3);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (17, true, '', '', 'Bouchon obturateur Ø100 mm à l’orifice d’aspiration absent', 2, 5);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (96, false, '', '', 'Raccord cassé ou non conforme', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (97, false, '', '', 'Trou d’homme ou puisard d’aspiration hors service', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (98, false, '', '', 'Vanne absente, cassée ou inopérante', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (99, false, '', '', 'Tenons du ½ raccord d’aspiration mal orientés ou cassés', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (100, false, '', '', 'Vanne anti siphon inopérante ou absente', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (101, false, '', '', 'Echec à l’aspiration', 0, 7);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (18, true, '', '', 'Bouchon obturateur Ø65 ou 40 mm (remplissage) absent', 2, 5);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (106, true, 'PRESSION_DYN_TROP_ELEVEE', NULL, 'Pression dynamique trop élevée', 1, NULL);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (107, true, 'PRESSION_DYN_INSUFF', '', 'Pression dynamique insuffisante', 3, NULL);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (83, true, 'PRESSION_INSUFF', '', 'Pression statique insuffisante', 4, NULL);
INSERT INTO type_hydrant_anomalie (id, actif, code, commentaire, nom, version, critere) VALUES (84, true, 'PRESSION_TROP_ELEVEE', '', 'Pression statique trop élevée', 2, NULL);

INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (49, NULL, 5, 5, 2, 11, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (51, NULL, 5, 5, 3, 12, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (50, NULL, 5, 5, 3, 13, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (48, NULL, 5, 5, 2, 15, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (30, 1, NULL, 0, 2, 22, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (31, 1, NULL, 0, 2, 23, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (11, 1, NULL, 4, 2, 33, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (27, 1, NULL, 0, 2, 17, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (28, 1, NULL, 0, 2, 18, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (8, 1, NULL, 1, 1, 27, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (9, 1, NULL, 1, 1, 27, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (10, 1, NULL, 1, 1, 27, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (29, 1, NULL, 1, 1, 44, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (44, NULL, 1, NULL, 1, 16, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (45, NULL, 1, NULL, 1, 43, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (111, NULL, NULL, 1, 0, 55, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (112, NULL, NULL, 1, 0, 56, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (52, NULL, NULL, 2, 2, 8, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (55, NULL, NULL, 2, 0, 8, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (56, NULL, NULL, 1, 0, 14, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (57, NULL, NULL, 1, 0, 14, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (58, NULL, NULL, 0, 0, 59, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (59, NULL, NULL, 0, 0, 59, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (60, NULL, NULL, 0, 0, 62, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (61, NULL, NULL, 0, 0, 63, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (64, NULL, NULL, 0, 0, 69, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (65, NULL, NULL, 3, 0, 70, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (66, NULL, NULL, 0, 0, 79, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (67, NULL, NULL, 0, 0, 79, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (68, NULL, NULL, 2, 0, 2, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (69, NULL, NULL, 2, 0, 2, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (70, NULL, NULL, 0, 0, 3, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (71, NULL, NULL, 0, 0, 3, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (72, NULL, NULL, 0, 0, 7, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (4, 1, NULL, 5, 3, 47, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (73, NULL, NULL, 0, 0, 28, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (74, NULL, NULL, 0, 0, 28, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (75, NULL, NULL, 3, 0, 42, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (79, NULL, NULL, 5, 1, 48, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (80, NULL, NULL, 5, 0, 48, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (81, NULL, NULL, 5, 0, 50, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (82, NULL, NULL, 5, 0, 50, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (83, NULL, NULL, 0, 0, 5, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (84, NULL, NULL, 0, 0, 6, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (85, NULL, NULL, 0, 0, 6, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (86, NULL, NULL, 0, 0, 20, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (87, NULL, NULL, 0, 0, 24, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (88, NULL, NULL, 5, 0, 41, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (89, NULL, NULL, 2, 0, 38, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (90, NULL, NULL, 5, 0, 68, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (91, NULL, NULL, 5, 0, 68, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (94, NULL, NULL, 5, 0, 31, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (95, NULL, NULL, 5, 0, 31, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (96, NULL, NULL, 5, 0, 21, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (97, NULL, NULL, 5, 0, 21, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (98, NULL, NULL, 5, 0, 77, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (99, NULL, NULL, 0, 0, 51, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (100, NULL, NULL, 0, 0, 51, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (101, NULL, NULL, 0, 0, 51, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (102, NULL, NULL, 0, 0, 52, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (103, NULL, NULL, 0, 0, 52, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (104, NULL, NULL, 1, 0, 37, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (105, NULL, NULL, 1, 0, 36, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (106, NULL, NULL, 5, 0, 35, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (107, NULL, NULL, 5, 0, 32, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (108, NULL, NULL, 0, 0, 34, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (109, NULL, NULL, 1, 0, 76, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (110, NULL, NULL, 1, 0, 76, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (113, NULL, NULL, 2, 0, 57, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (114, NULL, NULL, 5, 0, 65, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (115, NULL, NULL, 5, 0, 65, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (116, NULL, NULL, 4, 0, 4, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (117, NULL, NULL, 5, 0, 58, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (118, NULL, NULL, 5, 0, 80, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (119, NULL, NULL, 5, 0, 80, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (120, NULL, NULL, 5, 0, 81, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (121, NULL, NULL, 4, 1, 82, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (122, NULL, NULL, 5, 0, 81, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (123, NULL, NULL, 5, 0, 83, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (124, NULL, NULL, 5, 0, 83, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (125, NULL, NULL, 5, 0, 84, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (126, NULL, NULL, 5, 0, 84, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (127, NULL, NULL, 4, 1, 82, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (76, NULL, NULL, 5, 1, 47, 6);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (77, NULL, NULL, 5, 1, 47, 7);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (78, NULL, NULL, 5, 1, 47, 8);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (3, 1, 5, 5, 8, 47, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (2, 1, NULL, 5, 23, 47, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (134, NULL, NULL, 5, 0, 107, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (135, NULL, NULL, 5, 0, 107, 1);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (136, NULL, NULL, 5, 0, 106, 2);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (137, NULL, NULL, 5, 0, 106, 1);

INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (49, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (49, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (49, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (51, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (51, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (51, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (50, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (50, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (50, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (48, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (48, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (48, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (4, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (4, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (4, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (76, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (76, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (76, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (77, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (77, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (77, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (78, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (78, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (78, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (30, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (30, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (30, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (31, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (31, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (31, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (11, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (11, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (11, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (27, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (27, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (27, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (28, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (28, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (28, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (8, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (9, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (10, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (29, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (44, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (45, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (8, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (9, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (10, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (29, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (44, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (45, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (8, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (9, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (10, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (29, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (44, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (45, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (52, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (52, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (52, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (55, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (55, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (55, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (56, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (56, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (56, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (56, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (57, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (57, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (57, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (57, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (58, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (58, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (58, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (58, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (59, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (59, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (59, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (59, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (60, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (60, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (60, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (60, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (61, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (61, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (61, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (61, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (64, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (64, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (64, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (64, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (65, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (65, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (65, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (65, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (66, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (66, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (66, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (66, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (67, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (67, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (67, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (67, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (68, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (68, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (68, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (68, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (68, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (69, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (69, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (69, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (69, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (69, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (70, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (70, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (70, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (70, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (71, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (71, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (71, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (71, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (72, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (72, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (72, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (72, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (73, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (73, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (73, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (73, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (74, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (74, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (74, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (74, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (75, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (75, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (75, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (75, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (75, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (2, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (2, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (2, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (79, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (79, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (79, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (79, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (79, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (80, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (80, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (80, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (80, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (80, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (81, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (81, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (81, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (81, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (82, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (82, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (82, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (82, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (83, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (83, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (83, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (83, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (83, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (84, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (84, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (84, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (84, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (85, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (85, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (85, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (85, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (86, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (86, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (86, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (86, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (86, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (87, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (87, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (87, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (87, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (87, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (88, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (88, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (88, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (88, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (88, 3);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (89, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (89, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (90, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (90, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (91, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (91, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (94, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (94, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (94, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (95, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (95, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (95, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (96, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (96, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (96, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (97, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (97, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (97, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (98, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (98, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (99, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (99, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (100, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (100, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (101, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (101, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (102, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (102, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (103, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (103, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (104, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (104, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (105, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (105, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (106, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (106, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (107, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (107, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (108, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (108, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (109, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (109, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (110, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (110, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (111, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (111, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (111, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (112, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (112, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (112, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (113, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (113, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (113, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (114, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (114, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (114, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (114, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (115, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (115, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (115, 2);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (115, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (116, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (116, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (116, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (117, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (117, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (117, 6);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (3, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (3, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (3, 6);

INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (1, true, 'ABORD', 'Abords', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (2, true, 'ACCESS', 'Accessibilité', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (3, true, 'ASPECT', 'Aspect', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (4, true, 'HYDRAU', 'Capacité hydraulique', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (5, true, 'DIVER', 'Divers', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (6, true, 'ETANCH', 'Etanchéité', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (7, true, 'OEUVRE', 'Mise en œuvre', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (8, true, 'SECU', 'Sécurité', 1);
INSERT INTO type_hydrant_critere (id, actif, code, nom, version) VALUES (9, true, 'SIGN', 'Signalisation', 1);

INSERT INTO type_hydrant_diametre (id, actif, code, nom, version) VALUES (1, true, 'DIAM80', '80', 1);
INSERT INTO type_hydrant_diametre (id, actif, code, nom, version) VALUES (2, true, 'DIAM100', '100', 1);
INSERT INTO type_hydrant_diametre (id, actif, code, nom, version) VALUES (3, true, 'DIAM150', '150', 1);

INSERT INTO type_hydrant_diametre_natures (type_hydrant_diametre, natures) VALUES (1, 1);
INSERT INTO type_hydrant_diametre_natures (type_hydrant_diametre, natures) VALUES (2, 1);
INSERT INTO type_hydrant_diametre_natures (type_hydrant_diametre, natures) VALUES (3, 1);
INSERT INTO type_hydrant_diametre_natures (type_hydrant_diametre, natures) VALUES (2, 2);

INSERT INTO type_hydrant_domaine (id, actif, code, nom, version) VALUES (1, true, 'DEPARTEMENT', 'Départemental', 1);
INSERT INTO type_hydrant_domaine (id, actif, code, nom, version) VALUES (2, true, 'DOMAINE', 'Domanial', 1);
INSERT INTO type_hydrant_domaine (id, actif, code, nom, version) VALUES (3, true, 'COMMUNAL', 'Communal', 1);
INSERT INTO type_hydrant_domaine (id, actif, code, nom, version) VALUES (4, true, 'MILITAIRE', 'Militaire', 1);
INSERT INTO type_hydrant_domaine (id, actif, code, nom, version) VALUES (5, true, 'PRIVE', 'Privé', 1);

INSERT INTO type_hydrant_marque (id, actif, code, nom, version) VALUES (2, true, 'BAYARD', 'BAYARD', 1);
INSERT INTO type_hydrant_marque (id, actif, code, nom, version) VALUES (3, true, 'PAM', 'PAM', 1);
INSERT INTO type_hydrant_marque (id, actif, code, nom, version) VALUES (4, true, 'AVK', 'AVK', 1);
INSERT INTO type_hydrant_marque (id, actif, code, nom, version) VALUES (5, true, 'Inconnu', 'Inconnu', 1);
INSERT INTO type_hydrant_marque (id, actif, code, nom, version) VALUES (1, true, 'PEYTAVIN', 'PEYTAVIN', 1);

INSERT INTO type_hydrant_materiau (id, actif, code, nom, version) VALUES (1, true, 'METAL', 'Métal', 1);
INSERT INTO type_hydrant_materiau (id, actif, code, nom, version) VALUES (2, true, 'METAL CALO', 'Métal calorifugé', 1);
INSERT INTO type_hydrant_materiau (id, actif, code, nom, version) VALUES (3, true, 'BETON', 'Béton', 1);
INSERT INTO type_hydrant_materiau (id, actif, code, nom, version) VALUES (4, true, 'METAL MEMBRANE', 'Métal avec membrane interne', 1);
INSERT INTO type_hydrant_materiau (id, actif, code, nom, version) VALUES (5, true, 'AUTRE', 'Autre', 1);

INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (2, true, 'mod', 'RACCORD KEYSER NON CONFORME', 1, 1);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (3, true, 'mod', 'NON INCONGELABLE', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (4, true, 'mod', 'INCONGELABLE', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (5, true, 'mod', 'EMERAUDE', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (6, true, 'mod', 'EMERAUDE PARSEC', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (7, true, 'mod', 'SAPHIR', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (8, true, 'mod', 'SAPHIR PARSEC', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (9, true, 'mod', 'RETRO', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (10, true, 'mod', 'DAUPHIN', 1, 2);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (11, true, 'mod', 'PAM', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (12, true, 'mod', 'ELANCIO', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (13, true, 'mod', 'ATLAS', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (16, true, 'mod', 'ATLAS PLUS', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (17, true, 'mod', 'HERMES', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (18, true, 'mod', 'HERMES PLUS', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (19, true, 'mod', 'RATIONNEL', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (20, true, 'mod', 'AJAX', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (21, true, 'mod', 'C9', 1, 3);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (22, true, 'mod', 'PEGASE', 1, 4);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (23, true, 'mod', 'VEGA', 1, 4);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (24, true, 'mod', 'PHENIX', 1, 4);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (25, true, 'mod', 'ORION', 1, 4);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (26, true, 'mod', 'ORION 2', 1, 4);
INSERT INTO type_hydrant_modele (id, actif, code, nom, version, marque) VALUES (27, true, 'mod', 'DAUPHINE', 1, 4);

INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (1, true, 'PI', 'PI', 1, 1);
INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (2, true, 'BI', 'BI', 1, 1);
INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (3, true, 'CI_FIXE', 'Citerne fixe', 1, 2);
INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (4, true, 'CI_ENTERRE', 'Citerne enterrée', 1, 2);
INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (5, true, 'CE', 'Cours d''eau', 1, 2);
INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (6, true, 'PE', 'Plan d''eau', 1, 2);
INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (7, true, 'PU', 'Puisard', 1, 2);
INSERT INTO type_hydrant_nature (id, actif, code, nom, version, type_hydrant) VALUES (8, true, 'RE', 'Retenue', 1, 2);

INSERT INTO type_hydrant_positionnement (id, actif, code, nom, version) VALUES (1, true, 'POSEE', 'Posée', 1);
INSERT INTO type_hydrant_positionnement (id, actif, code, nom, version) VALUES (2, true, 'SEMI', 'Semi-enterrée', 1);

INSERT INTO type_hydrant_saisie (id, actif, code, nom, version) VALUES (1, true, 'LECT', 'Lecture', 1);
INSERT INTO type_hydrant_saisie (id, actif, code, nom, version) VALUES (2, true, 'RECEP', 'Réception', 1);
INSERT INTO type_hydrant_saisie (id, actif, code, nom, version) VALUES (3, true, 'CREA', 'Création', 1);
INSERT INTO type_hydrant_saisie (id, actif, code, nom, version) VALUES (4, true, 'RECO', 'Reconnaisance', 1);
INSERT INTO type_hydrant_saisie (id, actif, code, nom, version) VALUES (5, true, 'CTRL', 'Contrôle', 1);
INSERT INTO type_hydrant_saisie (id, actif, code, nom, version) VALUES (6, true, 'VERIF', 'Vérification', 1);

INSERT INTO type_hydrant_vol_constate (id, actif, code, nom, version) VALUES (1, true, 'VOL4', '4/4', 1);
INSERT INTO type_hydrant_vol_constate (id, actif, code, nom, version) VALUES (2, true, 'VOL3', '3/4', 1);
INSERT INTO type_hydrant_vol_constate (id, actif, code, nom, version) VALUES (3, true, 'VOL2', '2/4', 1);
INSERT INTO type_hydrant_vol_constate (id, actif, code, nom, version) VALUES (4, true, 'VOL1', '1/4', 1);
INSERT INTO type_hydrant_vol_constate (id, actif, code, nom, version) VALUES (5, true, 'VOL0', '0', 1);

ALTER TABLE ONLY hydrant_anomalies
    ADD CONSTRAINT hydrant_anomalies_pkey PRIMARY KEY (hydrant, anomalies);

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT hydrant_code_numero_commune_key UNIQUE (code, numero, commune);

ALTER TABLE ONLY hydrant_document
    ADD CONSTRAINT hydrant_document_pkey PRIMARY KEY (id);

ALTER TABLE ONLY hydrant_pena
    ADD CONSTRAINT hydrant_pena_pkey PRIMARY KEY (id);

ALTER TABLE ONLY hydrant_pibi
    ADD CONSTRAINT hydrant_pibi_pkey PRIMARY KEY (id);

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT hydrant_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tournee
    ADD CONSTRAINT tournee_pkey PRIMARY KEY (id);

ALTER TABLE ONLY type_hydrant_anomalie_nature
    ADD CONSTRAINT type_hydrant_anomalie_nature_pkey PRIMARY KEY (id);

ALTER TABLE ONLY type_hydrant_anomalie_nature_saisies
    ADD CONSTRAINT type_hydrant_anomalie_nature_saisies_pkey PRIMARY KEY (type_hydrant_anomalie_nature, saisies);

ALTER TABLE ONLY type_hydrant_anomalie
    ADD CONSTRAINT type_hydrant_anomalie_pkey PRIMARY KEY (id);

ALTER TABLE ONLY type_hydrant_critere
    ADD CONSTRAINT type_hydrant_critere_pkey PRIMARY KEY (id);

ALTER TABLE ONLY type_hydrant_diametre_natures
    ADD CONSTRAINT type_hydrant_diametre_natures_pkey PRIMARY KEY (type_hydrant_diametre, natures);

ALTER TABLE ONLY type_hydrant_diametre
    ADD CONSTRAINT type_hydrant_diametre_pkey PRIMARY KEY (id);

ALTER TABLE ONLY type_hydrant_domaine
    ADD CONSTRAINT type_hydrant_domaine_pkey PRIMARY KEY (id);

ALTER TABLE ONLY type_hydrant_marque
    ADD CONSTRAINT type_hydrant_marque_pkey PRIMARY KEY (id);

ALTER TABLE ONLY type_hydrant_materiau
    ADD CONSTRAINT type_hydrant_materiau_pkey PRIMARY KEY (id);

ALTER TABLE ONLY type_hydrant_modele
    ADD CONSTRAINT type_hydrant_modele_pkey PRIMARY KEY (id);

ALTER TABLE ONLY type_hydrant_nature
    ADD CONSTRAINT type_hydrant_nature_pkey PRIMARY KEY (id);

ALTER TABLE ONLY type_hydrant
    ADD CONSTRAINT type_hydrant_pkey PRIMARY KEY (id);

ALTER TABLE ONLY type_hydrant_positionnement
    ADD CONSTRAINT type_hydrant_positionnement_pkey PRIMARY KEY (id);

ALTER TABLE ONLY type_hydrant_saisie
    ADD CONSTRAINT type_hydrant_saisie_pkey PRIMARY KEY (id);

ALTER TABLE ONLY type_hydrant_vol_constate
    ADD CONSTRAINT type_hydrant_vol_constate_pkey PRIMARY KEY (id);

ALTER TABLE ONLY type_hydrant_modele
    ADD CONSTRAINT fk41b5bdf8cd9e6420 FOREIGN KEY (marque) REFERENCES type_hydrant_marque(id);

ALTER TABLE ONLY type_hydrant_nature
    ADD CONSTRAINT fk42acd04386657e5d FOREIGN KEY (type_hydrant) REFERENCES type_hydrant(id);

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f028374add52 FOREIGN KEY (organisme) REFERENCES organisme(id);

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f0285d29d8a8 FOREIGN KEY (domaine) REFERENCES type_hydrant_domaine(id);

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f028d10a0428 FOREIGN KEY (nature) REFERENCES type_hydrant_nature(id);

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f028d2da796c FOREIGN KEY (commune) REFERENCES commune(id);

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f028da542518 FOREIGN KEY (tournee) REFERENCES tournee(id);

ALTER TABLE ONLY hydrant_document
    ADD CONSTRAINT fk5b90bf5236f0130a FOREIGN KEY (document) REFERENCES document(id);

ALTER TABLE ONLY hydrant_document
    ADD CONSTRAINT fk5b90bf5250004fc FOREIGN KEY (hydrant) REFERENCES hydrant(id);

ALTER TABLE ONLY hydrant_anomalies
    ADD CONSTRAINT fk5e56b38a1c51b70d FOREIGN KEY (anomalies) REFERENCES type_hydrant_anomalie(id);

ALTER TABLE ONLY hydrant_anomalies
    ADD CONSTRAINT fk5e56b38a50004fc FOREIGN KEY (hydrant) REFERENCES hydrant(id);

ALTER TABLE ONLY type_hydrant_anomalie_nature
    ADD CONSTRAINT fk95654598771bfbfe FOREIGN KEY (anomalie) REFERENCES type_hydrant_anomalie(id) ON DELETE CASCADE;

ALTER TABLE ONLY type_hydrant_anomalie_nature
    ADD CONSTRAINT fk95654598d10a0428 FOREIGN KEY (nature) REFERENCES type_hydrant_nature(id);

ALTER TABLE ONLY type_hydrant_anomalie
    ADD CONSTRAINT fkaafafc6efd3ae2e2 FOREIGN KEY (critere) REFERENCES type_hydrant_critere(id);

ALTER TABLE ONLY tournee
    ADD CONSTRAINT fkbc630036dbf82b2f FOREIGN KEY (affectation) REFERENCES organisme(id);

ALTER TABLE ONLY hydrant_pena
    ADD CONSTRAINT fkd60e141f7dc71cd6 FOREIGN KEY (positionnement) REFERENCES type_hydrant_positionnement(id);

ALTER TABLE ONLY hydrant_pena
    ADD CONSTRAINT fkd60e141f8ac9c5e3 FOREIGN KEY (vol_constate) REFERENCES type_hydrant_vol_constate(id);

ALTER TABLE ONLY hydrant_pena
    ADD CONSTRAINT fkd60e141fb34721ef FOREIGN KEY (id) REFERENCES hydrant(id);

ALTER TABLE ONLY hydrant_pena
    ADD CONSTRAINT fkd60e141fe51486ba FOREIGN KEY (materiau) REFERENCES type_hydrant_materiau(id);

ALTER TABLE ONLY hydrant_pibi
    ADD CONSTRAINT fkd60e21b7a5a0e880 FOREIGN KEY (diametre) REFERENCES type_hydrant_diametre(id);

ALTER TABLE ONLY hydrant_pibi
    ADD CONSTRAINT fkd60e21b7b34721ef FOREIGN KEY (id) REFERENCES hydrant(id);

ALTER TABLE ONLY hydrant_pibi
    ADD CONSTRAINT fkd60e21b7cd9e6420 FOREIGN KEY (marque) REFERENCES type_hydrant_marque(id);

ALTER TABLE ONLY hydrant_pibi
    ADD CONSTRAINT fkd60e21b7cf1bdf92 FOREIGN KEY (modele) REFERENCES type_hydrant_modele(id);

ALTER TABLE ONLY hydrant_pibi
    ADD CONSTRAINT fkd60e21b7dda2e3c4 FOREIGN KEY (pena) REFERENCES hydrant_pena(id);

ALTER TABLE ONLY type_hydrant_anomalie_nature_saisies
    ADD CONSTRAINT fkd67208386bceb98b FOREIGN KEY (type_hydrant_anomalie_nature) REFERENCES type_hydrant_anomalie_nature(id);

ALTER TABLE ONLY type_hydrant_anomalie_nature_saisies
    ADD CONSTRAINT fkd6720838873aedcd FOREIGN KEY (saisies) REFERENCES type_hydrant_saisie(id);

ALTER TABLE ONLY type_hydrant_diametre_natures
    ADD CONSTRAINT fkfd10131c75c4c51c FOREIGN KEY (type_hydrant_diametre) REFERENCES type_hydrant_diametre(id);

ALTER TABLE ONLY type_hydrant_diametre_natures
    ADD CONSTRAINT fkfd10131c76d4a02d FOREIGN KEY (natures) REFERENCES type_hydrant_nature(id);


COMMIT;
