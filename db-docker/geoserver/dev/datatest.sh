#
# Container name  : dbdocker_geoserver_1
#

cd $(dirname $0)

GEOSERVER_URL="${GEOSERVER_URL:-http://localhost:8090/geoserver}"
GEOSERVER_URL_REST="${GEOSERVER_URL}/rest"

USERNAME_PWD="${USERNAME_PWD:-admin:geoserver}"

LOCAL_REMOCRA_WS_PATH="${LOCAL_REMOCRA_WS_PATH:-../../../server/sdis-remocra/var/remocra/geoserver_data/workspaces/remocra}"



# createWorkspace workspacename
function createWorkspace {
  workspacename=$1
  echo "Création du workspace ${workspacename}"
  curl -u ${USERNAME_PWD} -XPOST -H "Content-type: text/xml" -d "<workspace><name>${workspacename}</name></workspace>" "${GEOSERVER_URL_REST}/workspaces"
}

# createStyle stylename filenames localfilepath [raw : false]
function createStyle {
  stylename=$1
  filename=$2
  localfilepath=$3
  raw=$4
  raw=${raw:-false}
  echo "    Création du style ${stylename}"
  curl  -u ${USERNAME_PWD} -XPOST -H "Content-type: text/xml" -d "<style><name>${stylename}</name><filename>${filename}</filename></style>" "${GEOSERVER_URL_REST}/workspaces/remocra/styles"
  curl  -u ${USERNAME_PWD} -XPUT -H "Content-type: application/vnd.ogc.sld+xml" -d @${localfilepath} "${GEOSERVER_URL_REST}/workspaces/remocra/styles/${stylename}?raw=${raw}"
}

# createLayer layername localfilepath [defaultStylename]
function createLayer {
  layername=$1
  localfilepath=$2
  defaultStylename=$3
  echo "      Création de la couche ${layername} (style : ${defaultStylename:-aucun})"
  curl  -u ${USERNAME_PWD} -XPOST -H "Content-type: text/xml" -d @${localfilepath} "${GEOSERVER_URL_REST}/workspaces/remocra/datastores/remocra/featuretypes"
  # Style par défaut
  if [ ! -z "${defaultStylename+x}" ] ; then
    curl  -u ${USERNAME_PWD} -XPUT -H "Content-type: text/xml" -d "<layer><defaultStyle><name>${defaultStylename}</name><workspace>remocra</workspace></defaultStyle></layer>" "${GEOSERVER_URL_REST}/layers/remocra:${layername}"
  fi
}



# Workspace
createWorkspace remocra


# Datastore
echo "  Création du datastore remocra"
curl -u ${USERNAME_PWD} -XPOST -H "Content-type: text/xml" -d "<dataStore><name>remocra</name><connectionParameters><host>postgres</host><port>5432</port><database>remocra</database><schema>remocra</schema><user>postgres</user><passwd>postgres</passwd><dbtype>postgis</dbtype><entry key='Expose primary keys'>true</entry></connectionParameters></dataStore>" "${GEOSERVER_URL_REST}/workspaces/remocra/datastores"


# HYDRANT_PIBI / HYDRANT_PENA
createStyle remocra_hydrant remocra_hydrant.sld ${LOCAL_REMOCRA_WS_PATH}/styles/remocra_hydrant.sld true
createLayer v_hydrant_pibi ${LOCAL_REMOCRA_WS_PATH}/remocra/v_hydrant_pibi/featuretype.xml remocra_hydrant
createLayer v_hydrant_pena ${LOCAL_REMOCRA_WS_PATH}/remocra/v_hydrant_pena/featuretype.xml remocra_hydrant

# HYDRANT_PRESCRIT
createStyle remocra_pibi_prescrit remocra_pibi_prescrit.sld ${LOCAL_REMOCRA_WS_PATH}/styles/remocra_pibi_prescrit.sld
createLayer hydrant_prescrit ${LOCAL_REMOCRA_WS_PATH}/remocra/hydrant_prescrit/featuretype.xml remocra_pibi_prescrit

# PERMIS
createStyle remocra_permis remocra_permis.sld ${LOCAL_REMOCRA_WS_PATH}/styles/remocra_permis.sld
createLayer v_permis ${LOCAL_REMOCRA_WS_PATH}/remocra/v_permis/featuretype.xml remocra_permis

# ZONE_COMPETENCE
createLayer zone_competence ${LOCAL_REMOCRA_WS_PATH}/remocra/zone_competence/featuretype.xml polygon

# ZONE_SPECIALE
createLayer zone_speciale ${LOCAL_REMOCRA_WS_PATH}/remocra/zone_speciale/featuretype.xml polygon

# OLDEB
createStyle remocra_oldeb remocra_oldeb.sld ${LOCAL_REMOCRA_WS_PATH}/styles/remocra_oldeb.sld
createLayer oldeb_zonage ${LOCAL_REMOCRA_WS_PATH}/remocra/oldeb_zonage/featuretype.xml remocra_oldeb


# Reset cache
echo "Vidage du cache"
curl -u ${USERNAME_PWD} -XPOST "${GEOSERVER_URL_REST}/reset"
