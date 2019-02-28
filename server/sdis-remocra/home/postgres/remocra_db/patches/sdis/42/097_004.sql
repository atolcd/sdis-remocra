begin;

CREATE TABLE remocra_referentiel.nature_diametre_sig (
    id_nature_diametre_sig serial NOT NULL,
	code_nature character varying NOT NULL,
	code_diametre character varying NOT NULL,
	sous_type character varying NOT NULL,
	CONSTRAINT nature_diametre_sig_pkey PRIMARY KEY (id_nature_diametre_sig),
	CONSTRAINT nature_diametre_unq UNIQUE(code_nature,code_diametre)
);
COMMENT ON TABLE remocra_referentiel.nature_diametre_sig IS 'Table de correspondance nature/diametre liée à un sous type du SIG';
COMMENT ON COLUMN remocra_referentiel.nature_diametre_sig.code_nature IS 'Code nature de l''hydrant';
COMMENT ON COLUMN remocra_referentiel.nature_diametre_sig.code_diametre IS 'Code diametre du pibi';
COMMENT ON COLUMN remocra_referentiel.nature_diametre_sig.sous_type IS 'Libellé lié à la correspondance nature/diametre';

INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('PI','','PI 100');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('PI','DIAM100','PI 100');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('PI','DIAM150','PI 100');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('PI','DIAM2X100','PI 2x100');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('PI','DIAM70','PI 70');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('PI','DIAM80','PI 70');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('BI','','BI');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('BI','DIAM100','BI');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('BI','DIAM150','BI');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('BI','DIAM70','BI');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('BI','DIAM80','BI');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('CE','','citerne');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('CHE','','chateau_eau');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('CI_FIXE','','citerne');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('PA','','point_aspiration');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('PE','','citerne');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('PR','','Poteau relais');
INSERT INTO remocra_referentiel.nature_diametre_sig (code_nature,code_diametre,sous_type) VALUES ('PU','','puisard');

commit;