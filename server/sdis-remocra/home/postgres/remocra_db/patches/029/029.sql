SET search_path = remocra, pdi, public, pg_catalog; 

BEGIN;  


-- Communes PPRIF
update remocra.commune set pprif = false;  
update remocra.commune set pprif = true where insee in(
    '83035', '83133', '83001', '83027', '83118', '83094', '83115', '83127', '83148', '83053', '83043', '83019', '83016', '83061', '83063', '83107'
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
83107  ->  ROQUEBRUNE SUR ARGENS
83127  ->  SIGNES
83115  ->  STE MAXIME
83118  ->  ST RAPHAEL
83133  ->  TANNERON
83148  ->  VIDAUBAN
*/


COMMIT;

