SET search_path = remocra, pdi, public, pg_catalog; 

BEGIN;  


-- Communes PPRIF
update remocra.commune set pprif = false;  
update remocra.commune set pprif = true where insee in(
    '83035', '83133', '83001', '83027', '83118', '83094', '83115', '83127', '83148', '83053', '83043', '83019', '83016', '83061', '83063'
); 

/*
83019  ->  BORMES LES MIMOSAS
83043  ->  COLLOBRIERES
83053  ->  EVENOS
83061  ->  FREJUS
83027  ->  LA CADIERE D'AZUR
83063  ->  LA GARDE FREINET
83016  ->  LE BEAUSSET
83035  ->  LE CASTELLET
83001  ->  LES ADRETS DE L'ESTEREL
83094  ->  PLAN DE LA TOUR
83127  ->  SIGNES
83115  ->  STE MAXIME
83118  ->  ST RAPHAEL
83133  ->  TANNERON
83148  ->  VIDAUBAN
*/


-- Mise à jour des codes des organismes de type 'COMMUNE' qui deviennent les codes insee. Ces derniers
-- sont trouvés dans la table commune (jointure sur les noms en traitant les cas particuliers)
-- ATTENTION : pour le démo, on ne réalise pas la concaténation "'COM '||"
update remocra.organisme o set code = (
  select c.insee from remocra.commune c where 
    c.insee like '83%' and
    replace(replace(replace(o.nom, '-', ' '), 'SAINTE ', 'STE '), 'SAINT ', 'ST ')
    =
    ('COM '||replace(c.nom, '-', ' '))
)
where type_organisme = (select id from remocra.type_organisme where code = 'COMMUNE');


COMMIT;

