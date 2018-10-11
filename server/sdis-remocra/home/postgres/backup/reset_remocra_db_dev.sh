# ----------
# Script de réinitialisation de la base Remocra en développement
#
# Prérequis :
#  * Conteneur Docker démarré
#  * Script remocra_all.sql généré à partir de /home/postgres/backup/backup_remocra.sh
#
# ⚠ Définir PATCHES_NUM si nécessaire (cf utilisation plus bas)
# ----------

cd $(dirname $0)

# Au moment de la mise en place de ce script, le container Docker a une locale de_DE
DB_TARGETLOCALE=${DB_TARGETLOCALE:-de_DE}
echo "-- Remplacement de la base --" \
  && dropdb -h localhost -U postgres remocra \
  && sed -i "s/fr_FR\.UTF-8/${DB_TARGETLOCALE}\.UTF-8/g" "remocra_all.sql" \
  && psql -h localhost -U postgres -d postgres -f remocra_all.sql

if [ $? = 1 ]; then
  echo "-- ERREUR Remplacement de la base"
  exit $?
fi



echo "-- Nettoyage de la base --"
export MAIL_USERNAME=${MAIL_USERNAME:-${USERNAME}}
export MAIL_DOMAINNAME=${MAIL_DOMAINNAME:-atolcd.com}
echo "   Mail : ${MAIL_USERNAME}@${MAIL_DOMAINNAME}"
envsubst << "EOF" | psql -h localhost -U postgres remocra
-- Début script nettoyage

begin;

-- Nettoyage des données de remocra (copie de serveur, etc.)

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pdi, public, pg_catalog;



-- Utilisateurs et organismes
update remocra.utilisateur set email = '${MAIL_USERNAME}+dev'||identifiant||'@${MAIL_DOMAINNAME}';
update remocra.utilisateur set
  password=(md5('remocra' || '{' || md5(to_char(now(),'us-ss-mm-hh-dd-mm-yyyy') || identifiant) || '}')),
  salt=(md5(to_char(now(),'us-ss-mm-hh-dd-mm-yyyy') || identifiant));
update remocra.organisme set email_contact = '${MAIL_USERNAME}+devorg.'||lower(code)||'@${MAIL_DOMAINNAME}' where email_contact is not null and email_contact !='';

-- emails
update remocra.email set destinataire_email = '${MAIL_USERNAME}+dev@${MAIL_DOMAINNAME}', destinataire= '${MAIL_USERNAME}+dev@${MAIL_DOMAINNAME}', expediteur='${MAIL_USERNAME}+dev@${MAIL_DOMAINNAME}', expediteur_email='${MAIL_USERNAME}+dev@${MAIL_DOMAINNAME}';


-- param_conf ftp
update remocra.param_conf set valeur='VOIR' where cle in ('PDI_FTP_URL', 'PDI_FTP_USER_PASSWORD');

-- param_conf smtp
update remocra.param_conf set valeur='${MAIL_USERNAME}+dev@${MAIL_DOMAINNAME}' where cle in('PDI_SMTP_EME_MAIL', 'PDI_SMTP_REP_MAIL', 'PDI_SMTP_AR_MAIL', 'PDI_SMTP_ERR_MAIL');
update remocra.param_conf set valeur='' where cle in('PDI_SMTP_PASSWORD', 'PDI_SMTP_USER');

-- param_conf imap
update remocra.param_conf set valeur='993' where cle ='PDI_IMAP_PORT';
update remocra.param_conf set valeur='' where cle ='PDI_IMAP_URL';
update remocra.param_conf set valeur='' where cle ='PDI_IMAP_PASSWORD';
update remocra.param_conf set valeur='${MAIL_USERNAME}+dev@${MAIL_DOMAINNAME}' where cle ='PDI_IMAP_USER';

-- param_conf ldap
update remocra.param_conf set valeur='' where cle ='PDI_LDAP_ADMIN_PASSWORD';

-- param_conf sign
update remocra.param_conf set valeur='' where cle ='PDI_PDF_SIGN_KEY_PASSWORD';
update remocra.param_conf set valeur='' where cle ='PDI_PDF_SIGN_PFX_PASSWORD';

-- param_conf email (autres)
update remocra.param_conf set valeur='${MAIL_USERNAME}+devtravaux@${MAIL_DOMAINNAME}' where cle ='EMAIL_DEST_DEPOT_RECEPTRAVAUX';
update remocra.param_conf set valeur='${MAIL_USERNAME}+devrci@${MAIL_DOMAINNAME}' where cle ='EMAIL_DEST_CREATION_RCI';
update remocra.param_conf set valeur='${MAIL_USERNAME}+devdelib@${MAIL_DOMAINNAME}' where cle ='EMAIL_DEST_DEPOT_DELIB';
update remocra.param_conf set valeur='${MAIL_USERNAME}+devdecla@${MAIL_DOMAINNAME}' where cle ='EMAIL_DEST_DEPOT_DECLAHYDRANT';

-- param_conf ign et url
update remocra.param_conf set valeur='fjwf53vbh2ikn9q009g6mi7f' where cle='CLES_IGN';
update remocra.param_conf set valeur='http://localhost:8080/remocra/' where cle='PDI_URL_SITE';

commit;

-- Fin script nettoyage
EOF

if [ $? = 1 ]; then
  echo "-- ERREUR Nettoyage de la base"
  exit $?
fi



PATCHES_BASEDIR=../remocra_db/patches
echo "-- Application des patches --"
PATCHES_NUM=${PATCHES_NUM:-"089 090 091 092 093"}
IFS=' ' read -r -a PATCHES_NUM_ARRAY <<< "${PATCHES_NUM}"
for i in "${PATCHES_NUM_ARRAY[@]}"
do
  psql -h localhost -U postgres -f ${PATCHES_BASEDIR}/${i}/${i}.sql remocra
  if [ $? = 1 ]; then
    echo "-- ERREUR Application des patches"
    exit $?
  fi
done

