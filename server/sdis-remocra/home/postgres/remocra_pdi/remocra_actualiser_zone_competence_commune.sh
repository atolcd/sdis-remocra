# Réactualise le contenu de la table remocra.zone_competence_commune
# Périodicité : A DETERMINER

cd /home/postgres/pdi
sh kitchen.sh -rep:"ref_pdi_remocra" -dir:"maintenance/zone_competence_commune" -job:"actualisation_zone_competence_commune" -user:admin -pass:admin -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"

