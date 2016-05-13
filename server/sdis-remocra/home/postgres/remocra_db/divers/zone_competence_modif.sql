
--
-- Modification des zones de compétences 27/09/2013
--

/*
FA Pasquini :

Dans un premier temps, j'ai effectué les manipulations sur la table zone_competence_REMOCRA :
 - j'ai modifié géométriquement la zone de compétence LE MUY ROQUEBRUNE qui est devenue la zone LE MUY ( organisme CIS LMY)
 - j'ai créé la zone de compétence ROQUEBRUNE SUR ARGENS - organisme CIS RAG)

Puis j'ai activé la synchronisation de la table zone_competence_remocra d'Oracle vers PostgreSQL.

*/


BEGIN;


-- Création de la zone de compétence "SP RAG" (ROQUEBRUNE SUR ARGENS)
insert into remocra.zone_competence(code, nom, geometrie)
  select 'SPRAG', 'SP RAG', geometrie from remocra_referentiel.zone_competence_remocra where nom = 'SP RAG'
;

-- Création de l'organisme "CIS RAG" (ROQUEBRUNE SUR ARGENS)
insert into remocra.organisme(code, email_contact, nom, profil_organisme, type_organisme, zone_competence, actif, version) values (
  'CISRAG', null, 'CIS RAG',
  (select id from remocra.profil_organisme where code = 'CIS-ETAPE-1'),
  (select id from remocra.type_organisme where code = 'CIS'),
  (select id from remocra.zone_competence where code = 'SPRAG' limit 1),
  true, 1
);


-- Reprise du geom de la zone de compétence "SP LMY" (LE MUY ROQUEBRUNE)
update remocra.zone_competence set geometrie = (select geometrie from remocra_referentiel.zone_competence_remocra where nom = 'SP LMY')
  where code = 'SPLMY';


COMMIT;
