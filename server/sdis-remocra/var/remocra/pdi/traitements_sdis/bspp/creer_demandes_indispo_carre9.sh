# Créer automatiquement les demandes de génération de listing des PEI indispo
# pour les CS concernés

cd /home/postgres/pdi7.1
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/bspp/creer_demandes_indispo_carre9/creer_demandes_indispo_carre9.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"
