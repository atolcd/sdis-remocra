CREATE TABLE remocra.commune_cis_referent (
                                              id_commune int8 NOT NULL,
                                              id_cis int8 NOT NULL,
                                              CONSTRAINT commune_cis_referent_pkey PRIMARY KEY (id_commune, id_cis)
);

ALTER TABLE remocra.commune_cis_referent ADD CONSTRAINT commune_cis_referent_fk_commune FOREIGN KEY (id_commune) REFERENCES remocra.commune(id);
ALTER TABLE remocra.commune_cis_referent ADD CONSTRAINT commune_cis_referent_fk_organisme FOREIGN KEY (id_cis) REFERENCES remocra.organisme(id);