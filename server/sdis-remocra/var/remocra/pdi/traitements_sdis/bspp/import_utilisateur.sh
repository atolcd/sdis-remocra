# Intégration des utilisateurs via un export csv du LDAP
cd /home/postgres/pdi7.1
sh kitchen.sh -file:"/var/remocra/pdi/traitements_sdis/bspp/utilisateur/synchroniser_ldap_remocra.kjb" -level:Error -param:"PDI_FICHIER_PARAMETRE=/home/postgres/remocra_pdi/remocra.properties"
