# Synchroniser les utilisateurs entre le LDAP et Remocra
cd /home/postgres/pdi
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/77/ldap/synchroniser_ldap_remocra.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"