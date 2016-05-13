SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = remocra, pdi, public, pg_catalog;

SET default_with_oids = false;

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



-- Création des tables


--
-- Name: hydrant; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE hydrant (
    id bigint NOT NULL,
    agent1 character varying,
    agent2 character varying,
    annee_fabrication integer,
    code character varying,
    complement character varying,
    date_contr timestamp without time zone,
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
    marque bigint,
    modele bigint,
    nature bigint,
    tournee bigint,
    date_modification timestamp without time zone,
    courrier character varying,
    gest_point_eau character varying,
    utilisateur bigint
);


--
-- Name: hydrant_anomalies; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE hydrant_anomalies (
    hydrant bigint NOT NULL,
    anomalies bigint NOT NULL
);


--
-- Name: hydrant_document; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE hydrant_document (
    id bigint NOT NULL,
    document bigint NOT NULL,
    hydrant bigint NOT NULL
);


--
-- Name: hydrant_document_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE hydrant_document_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: hydrant_document_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE hydrant_document_id_seq OWNED BY hydrant_document.id;


--
-- Name: hydrant_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE hydrant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: hydrant_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE hydrant_id_seq OWNED BY hydrant.id;


--
-- Name: hydrant_pena; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE hydrant_pena (
    capacite character varying,
    coorddfci character varying,
    hbe boolean DEFAULT false,
    piste character varying,
    id bigint NOT NULL,
    materiau bigint,
    positionnement bigint,
    vol_constate bigint
);


--
-- Name: hydrant_pibi; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE hydrant_pibi (
    debit integer,
    debit_max integer,
    pression double precision,
    pression_dyn double precision,
    id bigint NOT NULL,
    diametre bigint,
    gest_reseau character varying,
    numeroscp character varying,
    choc boolean
);


--
-- Name: tournee; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE tournee (
    id bigint NOT NULL,
    deb_sync timestamp without time zone,
    last_sync timestamp without time zone,
    version integer DEFAULT 1,
    affectation bigint
);


--
-- Name: tournee_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE tournee_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: tournee_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE tournee_id_seq OWNED BY tournee.id;


--
-- Name: type_hydrant; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE type_hydrant (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


--
-- Name: type_hydrant_anomalie; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE type_hydrant_anomalie (
    id bigint NOT NULL,
    actif boolean DEFAULT true,
    code character varying NOT NULL,
    commentaire character varying,
    nom character varying NOT NULL,
    version integer DEFAULT 1,
    critere bigint
);


--
-- Name: type_hydrant_anomalie_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE type_hydrant_anomalie_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: type_hydrant_anomalie_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE type_hydrant_anomalie_id_seq OWNED BY type_hydrant_anomalie.id;


--
-- Name: type_hydrant_anomalie_nature; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE type_hydrant_anomalie_nature (
    id bigint NOT NULL,
    val_indispo_admin integer,
    val_indispo_hbe integer,
    val_indispo_terrestre integer,
    version integer,
    anomalie bigint,
    nature bigint
);


--
-- Name: type_hydrant_anomalie_nature_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE type_hydrant_anomalie_nature_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: type_hydrant_anomalie_nature_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE type_hydrant_anomalie_nature_id_seq OWNED BY type_hydrant_anomalie_nature.id;


--
-- Name: type_hydrant_anomalie_nature_saisies; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE type_hydrant_anomalie_nature_saisies (
    type_hydrant_anomalie_nature bigint NOT NULL,
    saisies bigint NOT NULL
);


--
-- Name: type_hydrant_critere; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE type_hydrant_critere (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


--
-- Name: type_hydrant_critere_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE type_hydrant_critere_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: type_hydrant_critere_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE type_hydrant_critere_id_seq OWNED BY type_hydrant_critere.id;


--
-- Name: type_hydrant_diametre; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE type_hydrant_diametre (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


--
-- Name: type_hydrant_diametre_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE type_hydrant_diametre_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: type_hydrant_diametre_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE type_hydrant_diametre_id_seq OWNED BY type_hydrant_diametre.id;


--
-- Name: type_hydrant_diametre_natures; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE type_hydrant_diametre_natures (
    type_hydrant_diametre bigint NOT NULL,
    natures bigint NOT NULL
);


--
-- Name: type_hydrant_domaine; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE type_hydrant_domaine (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


--
-- Name: type_hydrant_domaine_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE type_hydrant_domaine_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: type_hydrant_domaine_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE type_hydrant_domaine_id_seq OWNED BY type_hydrant_domaine.id;


--
-- Name: type_hydrant_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE type_hydrant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: type_hydrant_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE type_hydrant_id_seq OWNED BY type_hydrant.id;


--
-- Name: type_hydrant_marque; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE type_hydrant_marque (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


--
-- Name: type_hydrant_marque_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE type_hydrant_marque_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: type_hydrant_marque_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE type_hydrant_marque_id_seq OWNED BY type_hydrant_marque.id;


--
-- Name: type_hydrant_materiau; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE type_hydrant_materiau (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


--
-- Name: type_hydrant_materiau_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE type_hydrant_materiau_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: type_hydrant_materiau_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE type_hydrant_materiau_id_seq OWNED BY type_hydrant_materiau.id;


--
-- Name: type_hydrant_modele; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE type_hydrant_modele (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1,
    marque bigint
);


--
-- Name: type_hydrant_modele_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE type_hydrant_modele_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: type_hydrant_modele_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE type_hydrant_modele_id_seq OWNED BY type_hydrant_modele.id;


--
-- Name: type_hydrant_nature; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE type_hydrant_nature (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer,
    type_hydrant bigint
);


--
-- Name: type_hydrant_nature_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE type_hydrant_nature_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: type_hydrant_nature_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE type_hydrant_nature_id_seq OWNED BY type_hydrant_nature.id;


--
-- Name: type_hydrant_positionnement; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE type_hydrant_positionnement (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


--
-- Name: type_hydrant_positionnement_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE type_hydrant_positionnement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: type_hydrant_positionnement_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE type_hydrant_positionnement_id_seq OWNED BY type_hydrant_positionnement.id;


--
-- Name: type_hydrant_saisie; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE type_hydrant_saisie (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer
);


--
-- Name: type_hydrant_saisie_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE type_hydrant_saisie_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: type_hydrant_saisie_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE type_hydrant_saisie_id_seq OWNED BY type_hydrant_saisie.id;


--
-- Name: type_hydrant_vol_constate; Type: TABLE; Schema: remocra; Owner: -
--

CREATE TABLE type_hydrant_vol_constate (
    id bigint NOT NULL,
    actif boolean DEFAULT true NOT NULL,
    code character varying NOT NULL,
    nom character varying NOT NULL,
    version integer DEFAULT 1
);


--
-- Name: type_hydrant_vol_constate_id_seq; Type: SEQUENCE; Schema: remocra; Owner: -
--

CREATE SEQUENCE type_hydrant_vol_constate_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: type_hydrant_vol_constate_id_seq; Type: SEQUENCE OWNED BY; Schema: remocra; Owner: -
--

ALTER SEQUENCE type_hydrant_vol_constate_id_seq OWNED BY type_hydrant_vol_constate.id;


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant ALTER COLUMN id SET DEFAULT nextval('hydrant_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant_document ALTER COLUMN id SET DEFAULT nextval('hydrant_document_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY tournee ALTER COLUMN id SET DEFAULT nextval('tournee_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant ALTER COLUMN id SET DEFAULT nextval('type_hydrant_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_anomalie ALTER COLUMN id SET DEFAULT nextval('type_hydrant_anomalie_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_anomalie_nature ALTER COLUMN id SET DEFAULT nextval('type_hydrant_anomalie_nature_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_critere ALTER COLUMN id SET DEFAULT nextval('type_hydrant_critere_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_diametre ALTER COLUMN id SET DEFAULT nextval('type_hydrant_diametre_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_domaine ALTER COLUMN id SET DEFAULT nextval('type_hydrant_domaine_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_marque ALTER COLUMN id SET DEFAULT nextval('type_hydrant_marque_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_materiau ALTER COLUMN id SET DEFAULT nextval('type_hydrant_materiau_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_modele ALTER COLUMN id SET DEFAULT nextval('type_hydrant_modele_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_nature ALTER COLUMN id SET DEFAULT nextval('type_hydrant_nature_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_positionnement ALTER COLUMN id SET DEFAULT nextval('type_hydrant_positionnement_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_saisie ALTER COLUMN id SET DEFAULT nextval('type_hydrant_saisie_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_vol_constate ALTER COLUMN id SET DEFAULT nextval('type_hydrant_vol_constate_id_seq'::regclass);


--
-- Name: hydrant_anomalies_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant_anomalies
    ADD CONSTRAINT hydrant_anomalies_pkey PRIMARY KEY (hydrant, anomalies);


--
-- Name: hydrant_code_numero_commune_key; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT hydrant_code_numero_commune_key UNIQUE (code, numero, commune);


--
-- Name: hydrant_document_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant_document
    ADD CONSTRAINT hydrant_document_pkey PRIMARY KEY (id);


--
-- Name: hydrant_pena_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant_pena
    ADD CONSTRAINT hydrant_pena_pkey PRIMARY KEY (id);


--
-- Name: hydrant_pibi_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant_pibi
    ADD CONSTRAINT hydrant_pibi_pkey PRIMARY KEY (id);


--
-- Name: hydrant_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT hydrant_pkey PRIMARY KEY (id);


--
-- Name: tournee_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY tournee
    ADD CONSTRAINT tournee_pkey PRIMARY KEY (id);


--
-- Name: type_hydrant_anomalie_nature_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_anomalie_nature
    ADD CONSTRAINT type_hydrant_anomalie_nature_pkey PRIMARY KEY (id);


--
-- Name: type_hydrant_anomalie_nature_saisies_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_anomalie_nature_saisies
    ADD CONSTRAINT type_hydrant_anomalie_nature_saisies_pkey PRIMARY KEY (type_hydrant_anomalie_nature, saisies);


--
-- Name: type_hydrant_anomalie_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_anomalie
    ADD CONSTRAINT type_hydrant_anomalie_pkey PRIMARY KEY (id);


--
-- Name: type_hydrant_critere_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_critere
    ADD CONSTRAINT type_hydrant_critere_pkey PRIMARY KEY (id);


--
-- Name: type_hydrant_diametre_natures_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_diametre_natures
    ADD CONSTRAINT type_hydrant_diametre_natures_pkey PRIMARY KEY (type_hydrant_diametre, natures);


--
-- Name: type_hydrant_diametre_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_diametre
    ADD CONSTRAINT type_hydrant_diametre_pkey PRIMARY KEY (id);


--
-- Name: type_hydrant_domaine_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_domaine
    ADD CONSTRAINT type_hydrant_domaine_pkey PRIMARY KEY (id);


--
-- Name: type_hydrant_marque_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_marque
    ADD CONSTRAINT type_hydrant_marque_pkey PRIMARY KEY (id);


--
-- Name: type_hydrant_materiau_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_materiau
    ADD CONSTRAINT type_hydrant_materiau_pkey PRIMARY KEY (id);


--
-- Name: type_hydrant_modele_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_modele
    ADD CONSTRAINT type_hydrant_modele_pkey PRIMARY KEY (id);


--
-- Name: type_hydrant_nature_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_nature
    ADD CONSTRAINT type_hydrant_nature_pkey PRIMARY KEY (id);


--
-- Name: type_hydrant_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant
    ADD CONSTRAINT type_hydrant_pkey PRIMARY KEY (id);


--
-- Name: type_hydrant_positionnement_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_positionnement
    ADD CONSTRAINT type_hydrant_positionnement_pkey PRIMARY KEY (id);


--
-- Name: type_hydrant_saisie_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_saisie
    ADD CONSTRAINT type_hydrant_saisie_pkey PRIMARY KEY (id);


--
-- Name: type_hydrant_vol_constate_pkey; Type: CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_vol_constate
    ADD CONSTRAINT type_hydrant_vol_constate_pkey PRIMARY KEY (id);


--
-- Name: fk41b5bdf8cd9e6420; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_modele
    ADD CONSTRAINT fk41b5bdf8cd9e6420 FOREIGN KEY (marque) REFERENCES type_hydrant_marque(id);


--
-- Name: fk42acd04386657e5d; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_nature
    ADD CONSTRAINT fk42acd04386657e5d FOREIGN KEY (type_hydrant) REFERENCES type_hydrant(id);


--
-- Name: fk51b8f0285d29d8a8; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f0285d29d8a8 FOREIGN KEY (domaine) REFERENCES type_hydrant_domaine(id);


--
-- Name: fk51b8f028cd9e6420; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f028cd9e6420 FOREIGN KEY (marque) REFERENCES type_hydrant_marque(id);

    
    
ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f028a98055b2 FOREIGN KEY (utilisateur) REFERENCES utilisateur(id);    
    

--
-- Name: fk51b8f028cf1bdf92; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f028cf1bdf92 FOREIGN KEY (modele) REFERENCES type_hydrant_modele(id);


--
-- Name: fk51b8f028d10a0428; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f028d10a0428 FOREIGN KEY (nature) REFERENCES type_hydrant_nature(id);


--
-- Name: fk51b8f028d2da796c; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f028d2da796c FOREIGN KEY (commune) REFERENCES commune(id);


--
-- Name: fk51b8f028da542518; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant
    ADD CONSTRAINT fk51b8f028da542518 FOREIGN KEY (tournee) REFERENCES tournee(id);


--
-- Name: fk5b90bf5236f0130a; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant_document
    ADD CONSTRAINT fk5b90bf5236f0130a FOREIGN KEY (document) REFERENCES document(id);


--
-- Name: fk5b90bf5250004fc; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant_document
    ADD CONSTRAINT fk5b90bf5250004fc FOREIGN KEY (hydrant) REFERENCES hydrant(id);


--
-- Name: fk5e56b38a1c51b70d; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant_anomalies
    ADD CONSTRAINT fk5e56b38a1c51b70d FOREIGN KEY (anomalies) REFERENCES type_hydrant_anomalie(id);


--
-- Name: fk5e56b38a50004fc; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant_anomalies
    ADD CONSTRAINT fk5e56b38a50004fc FOREIGN KEY (hydrant) REFERENCES hydrant(id);


--
-- Name: fk95654598771bfbfe; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_anomalie_nature
    ADD CONSTRAINT fk95654598771bfbfe FOREIGN KEY (anomalie) REFERENCES type_hydrant_anomalie(id);


--
-- Name: fk95654598d10a0428; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_anomalie_nature
    ADD CONSTRAINT fk95654598d10a0428 FOREIGN KEY (nature) REFERENCES type_hydrant_nature(id);


--
-- Name: fkaafafc6efd3ae2e2; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_anomalie
    ADD CONSTRAINT fkaafafc6efd3ae2e2 FOREIGN KEY (critere) REFERENCES type_hydrant_critere(id);


--
-- Name: fkbc630036dbf82b2f; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY tournee
    ADD CONSTRAINT fkbc630036dbf82b2f FOREIGN KEY (affectation) REFERENCES utilisateur(id);


--
-- Name: fkd60e141f7dc71cd6; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant_pena
    ADD CONSTRAINT fkd60e141f7dc71cd6 FOREIGN KEY (positionnement) REFERENCES type_hydrant_positionnement(id);


--
-- Name: fkd60e141f8ac9c5e3; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant_pena
    ADD CONSTRAINT fkd60e141f8ac9c5e3 FOREIGN KEY (vol_constate) REFERENCES type_hydrant_vol_constate(id);


--
-- Name: fkd60e141fb34721ef; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant_pena
    ADD CONSTRAINT fkd60e141fb34721ef FOREIGN KEY (id) REFERENCES hydrant(id);


--
-- Name: fkd60e141fe51486ba; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant_pena
    ADD CONSTRAINT fkd60e141fe51486ba FOREIGN KEY (materiau) REFERENCES type_hydrant_materiau(id);


--
-- Name: fkd60e21b7a5a0e880; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant_pibi
    ADD CONSTRAINT fkd60e21b7a5a0e880 FOREIGN KEY (diametre) REFERENCES type_hydrant_diametre(id);


--
-- Name: fkd60e21b7b34721ef; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY hydrant_pibi
    ADD CONSTRAINT fkd60e21b7b34721ef FOREIGN KEY (id) REFERENCES hydrant(id);


--
-- Name: fkd67208386bceb98b; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_anomalie_nature_saisies
    ADD CONSTRAINT fkd67208386bceb98b FOREIGN KEY (type_hydrant_anomalie_nature) REFERENCES type_hydrant_anomalie_nature(id);


--
-- Name: fkd6720838873aedcd; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_anomalie_nature_saisies
    ADD CONSTRAINT fkd6720838873aedcd FOREIGN KEY (saisies) REFERENCES type_hydrant_saisie(id);


--
-- Name: fkfd10131c75c4c51c; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_diametre_natures
    ADD CONSTRAINT fkfd10131c75c4c51c FOREIGN KEY (type_hydrant_diametre) REFERENCES type_hydrant_diametre(id);


--
-- Name: fkfd10131c76d4a02d; Type: FK CONSTRAINT; Schema: remocra; Owner: -
--

ALTER TABLE ONLY type_hydrant_diametre_natures
    ADD CONSTRAINT fkfd10131c76d4a02d FOREIGN KEY (natures) REFERENCES type_hydrant_nature(id);


--
-- PostgreSQL database dump complete
--



-- Données Hydrant
INSERT INTO remocra.type_hydrant (id, actif, code, nom, version) VALUES (1, true, 'PIBI', 'PIBI', 1);
INSERT INTO remocra.type_hydrant (id, actif, code, nom, version) VALUES (2, true, 'PENA', 'PENA', 1);

SELECT pg_catalog.setval('remocra.type_hydrant_id_seq', 2, true);

delete from remocra.type_hydrant_diametre_natures;

INSERT INTO remocra.type_hydrant_nature(id, actif, code, nom, version, type_hydrant) VALUES (1, true, 'PI', 'PI', 1 , 1);
INSERT INTO remocra.type_hydrant_nature(id, actif, code, nom, version, type_hydrant) VALUES (2, true, 'BI', 'BI', 1 , 1);
INSERT INTO remocra.type_hydrant_nature(id, actif, code, nom, version, type_hydrant) VALUES (3, true, 'CI_FIXE', 'Citerne fixe', 1 , 2);
INSERT INTO remocra.type_hydrant_nature(id, actif, code, nom, version, type_hydrant) VALUES (4, true, 'CI_ENTERRE', 'Citerne enterrée', 1 , 2);
INSERT INTO remocra.type_hydrant_nature(id, actif, code, nom, version, type_hydrant) VALUES (5, true, 'CE', 'Cours d''eau', 1 , 2);
INSERT INTO remocra.type_hydrant_nature(id, actif, code, nom, version, type_hydrant) VALUES (6, true, 'PE', 'Plan d''eau', 1 , 2);
INSERT INTO remocra.type_hydrant_nature(id, actif, code, nom, version, type_hydrant) VALUES (7, true, 'PU', 'Puisard', 1 , 2);
INSERT INTO remocra.type_hydrant_nature(id, actif, code, nom, version, type_hydrant) VALUES (8, true, 'RE', 'Retenue', 1 , 2);

SELECT pg_catalog.setval('remocra.type_hydrant_nature_id_seq', 8, true);


delete from remocra.type_hydrant_diametre;
INSERT INTO remocra.type_hydrant_diametre(id, actif, code, nom, version) VALUES (1, true, 'DIAM80', '80', 1);
INSERT INTO remocra.type_hydrant_diametre(id, actif, code, nom, version) VALUES (2, true, 'DIAM100', '100', 1);
INSERT INTO remocra.type_hydrant_diametre(id, actif, code, nom, version) VALUES (3, true, 'DIAM150', '150', 1);

SELECT pg_catalog.setval('remocra.type_hydrant_diametre_id_seq', 3, true);

delete from remocra.type_hydrant_diametre_natures;
INSERT INTO remocra.type_hydrant_diametre_natures(type_hydrant_diametre, natures) VALUES (1, 1);
INSERT INTO remocra.type_hydrant_diametre_natures(type_hydrant_diametre, natures) VALUES (2, 1);
INSERT INTO remocra.type_hydrant_diametre_natures(type_hydrant_diametre, natures) VALUES (3, 1);
INSERT INTO remocra.type_hydrant_diametre_natures(type_hydrant_diametre, natures) VALUES (2, 2);

INSERT INTO remocra.type_hydrant_domaine(id, actif, code, nom, version) VALUES (1, true, 'DEPARTEMENT', 'Départemental', 1);
INSERT INTO remocra.type_hydrant_domaine(id, actif, code, nom, version) VALUES (2, true, 'DOMAINE', 'Domanial', 1);
INSERT INTO remocra.type_hydrant_domaine(id, actif, code, nom, version) VALUES (3, true, 'COMMUNAL', 'Communal', 1);
INSERT INTO remocra.type_hydrant_domaine(id, actif, code, nom, version) VALUES (4, true, 'MILITAIRE', 'Militaire', 1);
INSERT INTO remocra.type_hydrant_domaine(id, actif, code, nom, version) VALUES (5, true, 'PRIVE', 'Privé', 1);

SELECT pg_catalog.setval('remocra.type_hydrant_domaine_id_seq', 5, true);

INSERT INTO remocra.type_hydrant_vol_constate(id, actif, code, nom, version) VALUES (1, true, 'VOL4', '4/4', 1);
INSERT INTO remocra.type_hydrant_vol_constate(id, actif, code, nom, version) VALUES (2, true, 'VOL3', '3/4', 1);
INSERT INTO remocra.type_hydrant_vol_constate(id, actif, code, nom, version) VALUES (3, true, 'VOL2', '2/4', 1);
INSERT INTO remocra.type_hydrant_vol_constate(id, actif, code, nom, version) VALUES (4, true, 'VOL1', '1/4', 1);
INSERT INTO remocra.type_hydrant_vol_constate(id, actif, code, nom, version) VALUES (5, true, 'VOL0', '0', 1);

SELECT pg_catalog.setval('remocra.type_hydrant_vol_constate_id_seq', 5, true);

INSERT INTO remocra.type_hydrant_materiau(id, actif, code, nom, version) VALUES (1, true, 'METAL', 'Métal', 1);
INSERT INTO remocra.type_hydrant_materiau(id, actif, code, nom, version) VALUES (2, true, 'METAL CALO', 'Métal calorifugé', 1);
INSERT INTO remocra.type_hydrant_materiau(id, actif, code, nom, version) VALUES (3, true, 'BETON', 'Béton', 1);
INSERT INTO remocra.type_hydrant_materiau(id, actif, code, nom, version) VALUES (4, true, 'METAL MEMBRANE', 'Métal avec membrane interne', 1);
INSERT INTO remocra.type_hydrant_materiau(id, actif, code, nom, version) VALUES (5, true, 'AUTRE', 'Autre', 1);

SELECT pg_catalog.setval('remocra.type_hydrant_materiau_id_seq', 5, true);

INSERT INTO remocra.type_hydrant_positionnement(id, actif, code, nom, version) VALUES (1, true, 'POSEE', 'Posée', 1);
INSERT INTO remocra.type_hydrant_positionnement(id, actif, code, nom, version) VALUES (2, true, 'SEMI', 'Semi-enterrée', 1);
-- INSERT INTO remocra.type_hydrant_positionnement(id, actif, code, nom, version) VALUES (3, true, 'ENTERRE', 'Enterrée', 1);

SELECT pg_catalog.setval('remocra.type_hydrant_positionnement_id_seq', 3, true);

INSERT INTO remocra.type_hydrant_saisie(id, actif, code, nom, version) VALUES (1, true, 'LECT', 'Lecture', 1);
INSERT INTO remocra.type_hydrant_saisie(id, actif, code, nom, version) VALUES (2, true, 'RECEP', 'Réception', 1);
INSERT INTO remocra.type_hydrant_saisie(id, actif, code, nom, version) VALUES (3, true, 'CREA', 'Création', 1);
INSERT INTO remocra.type_hydrant_saisie(id, actif, code, nom, version) VALUES (4, true, 'RECO', 'Reconnaisance', 1);
INSERT INTO remocra.type_hydrant_saisie(id, actif, code, nom, version) VALUES (5, true, 'CTRL', 'Contrôle', 1);
INSERT INTO remocra.type_hydrant_saisie(id, actif, code, nom, version) VALUES (6, true, 'VERIF', 'Vérification', 1);

SELECT pg_catalog.setval('remocra.type_hydrant_saisie_id_seq', 6, true);

-- anomalies 

INSERT INTO remocra.type_hydrant_critere (id, actif, code, nom, version) VALUES (1, true, 'ABORD', 'Abords', 1);
INSERT INTO remocra.type_hydrant_critere (id, actif, code, nom, version) VALUES (2, true, 'ACCESS', 'Accessibilité', 1);
INSERT INTO remocra.type_hydrant_critere (id, actif, code, nom, version) VALUES (3, true, 'ASPECT', 'Aspect', 1);
INSERT INTO remocra.type_hydrant_critere (id, actif, code, nom, version) VALUES (4, true, 'HYDRAU', 'Capacité hydraulique', 1);
INSERT INTO remocra.type_hydrant_critere (id, actif, code, nom, version) VALUES (5, true, 'DIVER', 'Divers', 1);
INSERT INTO remocra.type_hydrant_critere (id, actif, code, nom, version) VALUES (6, true, 'ETANCH', 'Etanchéité', 1);
INSERT INTO remocra.type_hydrant_critere (id, actif, code, nom, version) VALUES (7, true, 'OEUVRE', 'Mise en œuvre', 1);
INSERT INTO remocra.type_hydrant_critere (id, actif, code, nom, version) VALUES (8, true, 'SECU', 'Sécurité', 1);
INSERT INTO remocra.type_hydrant_critere (id, actif, code, nom, version) VALUES (9, true, 'SIGN', 'Signalisation', 1);

SELECT pg_catalog.setval('remocra.type_hydrant_critere_id_seq', 9, true);

delete from remocra.type_hydrant_anomalie;
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 1 , true, '','½ raccord d’aspiration Ø100 mm ôté ou inopérant (*)',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 2 , true, '','A débroussailler',1,2);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 3 , true, '','A déplacer',1,2);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 4 , true, '','A numéroter',1,9);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 5 , true, '','A peindre',1,3);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 6 , true, '','A protéger',1,3);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 7 , true, '','A tourner',1,2);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 8 , true, '','Abords à dégager',1,1);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 9 , true, '','Absente',1,2);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 10 , true, '','Aire de manœuvre à (re)faire devant l’orifice d’aspiration (ne doit pas arrêter la circulation)',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 11 , true, '','Aire de pompage Ø 30 m non conforme',1,2);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 12 , true, '','Aire de poser lisse Ø 10 m non conforme (au centre de l’aire de sécurité)',1,2);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 13 , true, '','Aire de sécurité Ø 30 m non conforme (inclut l’aire de poser)',1,2);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 14 , true, '','Ancrage',1,1);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 15 , true, '','Approche(s) > 50 m non conforme(s)',1,2);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 16 , true, '','Bandeau jaune absent au 1/3 supérieur',1,9);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 17 , true, '','Bouchon obturateur Ø100 mm à l’orifice d’aspiration absent (*)',1,5);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 18 , true, '','Bouchon obturateur Ø65 ou 40 mm (remplissage) absent (*)',1,5);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 19 , true, '','Bouchon stop pression',1,8);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 20 , true, '','Capot casse - manquant',1,3);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 21 , true, '','Carre de manœuvre hors service',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 22 , true, '','Citerne métallique : Peinture verte à refaire (*)',1,3);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 23 , true, '','Corrosion',1,3);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 24 , true, '','Couleur non normalisée (A repeindre)',1,3);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 25 , true, '','Crépine d’aspiration à < 0,30 m de la surface du cours ou plan d’eau',1,4);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 26 , true, '','Crépine d’aspiration à < 0,50 m du fond du cours ou plan d’eau',1,4);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 27 , true, '','Débroussaillement et/ou éclaircie à faire (couronne de 25 m autour de l’aire de manœuvre)',1,8);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 28 , true, '','Distance > 5m',1,2);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 29 , true, '','Echec à l’aspiration (autre cause non visible liée au P.E.N.A.)',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 30 , true, '','Echelle hors service (rendant l’usage impossible) (*)',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 31 , true, '','En travaux',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 32 , true, '','Fermeture hors service (Fuite)',1,6);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 33 , true, '','Fuite',1,4);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 34 , true, '','Fuite ½ raccord',1,6);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 35 , true, '','Fuite à la base',1,6);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 36 , true, '','Fuite au purgeur',1,6);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 37 , true, '','Fuite au volant',1,6);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 38 , true, '','Grippé',1,4);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 39 , true, '','Hauteur géométrique',1,4);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 40 , true, '','Hauteur géométrique d’aspiration > 5,50 m (*)',1,4);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 41 , true, '','Hydrant détruit – Cassé',1,4);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 42 , true, '','Hydrant enterré',1,2);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 43 , true, '','Immatriculation au-dessus absente ou erronée',1,9);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 44 , true, '','Immatriculation sur (flanc ou panneau) absente ou erronée (pour tout type)',1,9);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 45 , true, '','Impossible d’ouvrir le trou d’homme (0,80 m) d’un point d’eau pour engins terrestres) (*)',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 46 , true, '','Impossible de fermer/verrouiller le capot trou d’homme (0,80 m) ou le regard (0,80 m) de point d’eau pour engins terrestres (*)',1,5);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 47 , true, '','Inaccessible – cause cheminement impraticable',1,2);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 48 , true, '','Inaccessible aux engins',1,2);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 49 , true, '','Incontrôlable (décrire en observation la cause)',1,4);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 50 , true, '','Introuvable',1,2);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 51 , true, '','Isolation (Bouche à Clef) Hors service',1,5);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 52 , true, '','Isolation (Bouche à Clef) Introuvable',1,5);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 53 , true, '','Joint bouchon hors service',1,6);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 54 , true, '','Ligne d’aspiration > 8 m (*)',1,4);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 55 , true, '','Manque bouchon 100',1,6);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 56 , true, '','Manque bouchon 70',1,6);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 57 , true, '','Manque deux bouchons',1,6);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 58 , true, '','Manque plaque signalétique (BI uniquement)',1,9);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 59 , true, '','Obstacle à l’ouverture',1,1);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 60 , true, '','Orifice ou puisard d’aspiration rendu inaccessible (*)',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 61 , true, '','Passerelle hors service (rendant l’usage impossible)',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 62 , true, '','Pi sous coffre',1,1);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 63 , true, '','Pi sous scellé',1,1);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 64 , true, '','Proche d’une scellé',1,1);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 65 , true, '','Proche d’une installation électrique',1,8);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 66 , true, '','Profondeur',1,1);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 67 , true, '','Profondeur du cours ou plan d’eau < 0,80 m',1,4);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 68 , true, '','Sans eau',1,4);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 69 , true, '','Socle de propreté',1,1);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 70 , true, '','Stabilité / verticalité',1,1);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 71 , true, '','Tenons du ½ raccord d’aspiration mal orientés (doivent être à ‘12H-6H’) (*)',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 72 , true, '','Trappe d’ouverture hors service (interdisant le pompage)',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 73 , true, '','Vanne d’ouverture-fermeture d’aspiration inopérante (*)',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 74 , true, '','Vanne de mise à l’air : vanne ou manette inopérante (*)',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 75 , true, '','Vanne de mise à l’air : vanne ou manette ôtée (*)',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 76 , true, '','Vidange colonne',1,6);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 77 , true, '','Volant de manœuvre absent, cassé Carré non normalisé',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 78 , true, '','Volant hors service',1,7);
INSERT INTO remocra.type_hydrant_anomalie (id, actif, code, nom, version, critere) VALUES ( 79 , true, '','Volume de dégagement insuffisant (50 cm autour hydrant)',1,1);

SELECT pg_catalog.setval('remocra.type_hydrant_anomalie_id_seq', 79, true);



INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (1, 1, NULL, 1, 1, 9, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (2, 1, NULL, 1, 1, 47, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (3, 1, NULL, 1, 1, 47, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (4, 1, NULL, 1, 1, 47, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (5, 1, NULL, 1, 1, 10, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (6, 1, NULL, 1, 1, 10, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (7, 1, NULL, 1, 1, 10, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (8, 1, NULL, 1, 1, 27, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (9, 1, NULL, 1, 1, 27, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (10, 1, NULL, 1, 1, 27, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (11, 1, NULL, 1, 1, 33, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (12, 1, NULL, 1, 1, 60, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (13, 1, NULL, 1, 1, 60, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (14, 1, NULL, 1, 1, 60, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (15, 1, NULL, 1, 1, 73, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (16, 1, NULL, 1, 1, 1, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (17, 1, NULL, 1, 1, 1, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (18, 1, NULL, 1, 1, 1, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (19, 1, NULL, 1, 1, 71, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (20, 1, NULL, 1, 1, 71, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (21, 1, NULL, 1, 1, 71, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (22, 1, NULL, 1, 1, 74, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (23, 1, NULL, 1, 1, 75, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (24, 1, NULL, 1, 1, 30, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (25, 1, NULL, 1, 1, 45, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (26, 1, NULL, 1, 1, 46, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (27, 1, NULL, 1, 1, 17, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (28, 1, NULL, 1, 1, 18, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (29, 1, NULL, 1, 1, 44, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (30, 1, NULL, 1, 1, 22, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (31, 1, NULL, 1, 1, 23, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (32, 1, NULL, 1, 1, 40, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (33, 1, NULL, 1, 1, 40, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (34, 1, NULL, 1, 1, 54, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (35, 1, NULL, 1, 1, 54, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (36, 1, NULL, 1, 1, 29, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (37, 1, NULL, 1, 1, 29, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (38, 1, NULL, 1, 1, 67, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (39, 1, NULL, 1, 1, 67, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (42, 1, NULL, 1, 1, 25, 4);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (43, 1, NULL, 1, 1, 25, 5);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (44, NULL, 1, NULL, 1, 16, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (45, NULL, 1, NULL, 1, 43, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (46, NULL, 1, NULL, 1, 61, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (47, NULL, 1, NULL, 1, 72, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (48, NULL, 1, NULL, 1, 15, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (49, NULL, 1, NULL, 1, 11, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (50, NULL, 1, NULL, 1, 13, 3);
INSERT INTO type_hydrant_anomalie_nature (id, val_indispo_admin, val_indispo_hbe, val_indispo_terrestre, version, anomalie, nature) VALUES (51, NULL, 1, NULL, 1, 12, 3);

SELECT pg_catalog.setval('type_hydrant_anomalie_nature_id_seq', 51, true);




delete from type_hydrant_anomalie_nature_saisies;
-- 4 = reconnaissance
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (1, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (2, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (3, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (4, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (5, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (6, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (7, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (8, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (9, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (10, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (11, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (12, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (13, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (14, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (16, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (17, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (18, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (19, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (20, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (21, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (23, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (22, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (24, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (25, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (26, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (27, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (28, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (29, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (30, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (31, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (32, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (33, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (34, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (35, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (38, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (39, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (42, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (43, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (44, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (45, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (46, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (47, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (48, 4);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (49, 4);

-- 5 = reconnaissance
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (15, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (36, 5);
INSERT INTO type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies) VALUES (37, 5);

insert into type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies)
 select type_hydrant_anomalie_nature, 5 
  from type_hydrant_anomalie_nature_saisies
  where saisies = 4;

-- 6 = vérification
insert into type_hydrant_anomalie_nature_saisies (type_hydrant_anomalie_nature, saisies)
 select type_hydrant_anomalie_nature, 6
  from type_hydrant_anomalie_nature_saisies
  where saisies = 5;

-- Ajout champs "code" à la commune
CREATE FUNCTION addCodeToCommune20130603() RETURNS void AS $$
BEGIN

if not exists (SELECT 1 FROM pg_attribute WHERE attrelid = 'remocra.commune'::regclass AND   attname = 'code' AND NOT attisdropped) then
  ALTER TABLE remocra.commune ADD COLUMN code character varying;
  update remocra.commune set code = (upper(regexp_replace(nom, '[^a-zA-Z0-9]', '', 'g')));
end if;

END;
$$ LANGUAGE plpgsql;

SELECT addCodeToCommune20130603();
DROP FUNCTION addCodeToCommune20130603();

-- 

DELETE from param_conf where cle = 'DOSSIER_DOC_HYDRANT';
INSERT INTO param_conf (cle, description, valeur, version) VALUES ('DOSSIER_DOC_HYDRANT', 'Emplacement du dossier de stockage des photos des hydrants', '/var/remocra/hydrants', 1);
  
COMMIT;
