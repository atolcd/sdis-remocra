--
-- Mise à jour des numéros <code insee commune><numéro interne> avec numéro interne sur 3 caractères
--

begin;



ALTER TABLE remocra.hydrant DISABLE TRIGGER ALL;


-- Configuration
update remocra.param_conf set valeur='09' where cle='HYDRANT_NUMEROTATION_METHODE';



-- Table des états courants
-- "090940003" -> "09094003"
update remocra.hydrant h set numero=substr(h.numero, 0, 6) ||  substr(h.numero, 7, 3);


-- Table de traçabilité
update tracabilite.hydrant set numero=insee || substr(numero, 7, 3);



ALTER TABLE remocra.hydrant ENABLE TRIGGER ALL;



commit;

