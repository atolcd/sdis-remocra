#
# Container name  : dbdocker_geoserver_1
#

cd $(dirname $0)

GEOSERVER_URL="${GEOSERVER_URL:-http://localhost:8090/geoserver}"
GEOSERVER_URL_REST="${GEOSERVER_URL}/rest"

GEOSERVER_ADMIN_USERNAME="${GEOSERVER_ADMIN_USERNAME:-remocraadmin}"
GEOSERVER_ADMIN_PASSWORD="${GEOSERVER_ADMIN_PASSWORD:-geoserver}"
USERNAME_PWD="${USERNAME_PWD:-${GEOSERVER_ADMIN_USERNAME}:${GEOSERVER_ADMIN_PASSWORD}}"





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
  workspace=$5
  workspace=${workspace:-remocra}
  echo "    Création du style ${stylename}"
  curl -u ${USERNAME_PWD} -XPOST -H "Content-type: text/xml" -d "<style><name>${stylename}</name><filename>${filename}</filename></style>" "${GEOSERVER_URL_REST}/workspaces/${workspace}/styles"
  curl -u ${USERNAME_PWD} -XPUT -H "Content-type: application/vnd.ogc.sld+xml" -d @${localfilepath} "${GEOSERVER_URL_REST}/workspaces/${workspace}/styles/${stylename}?raw=${raw}"
}

# Crée le style si besoin en le récupérant depuis le projet GitHub
function maybeCreateStyle {
  stylename=$1
  filename=$2
  localfilepath=$3
  raw=$4
  raw=${raw:-false}
  workspace=$5
  workspace=${workspace:-remocra}

  stylename="remocra_selection"
  response=$(curl --write-out %{http_code} --silent --output /dev/null -u ${USERNAME_PWD} -XGET "${GEOSERVER_URL_REST}/workspaces/${workspace}/styles/${stylename}.sld")
  if [ "${response}" -eq 404 ] ; then
    echo "    Style ${stylename} : récupération"
    curl -q "https://raw.githubusercontent.com/atolcd/sdis-remocra/master/server/sdis-remocra/var/remocra/geoserver_data/workspaces/${workspace}/styles/${stylename}.sld" -o ${stylename}.sld
    createStyle $stylename $filename $localfilepath $raw
  else
    echo "    Style ${stylename}.sld : déjà présent"
  fi
}

# createLayer layername localfilepath [defaultStylename]
function createLayer {
  layername=$1
  localfilepath=$2
  defaultStylename=$3
  workspace=$4
  workspace=${workspace:-remocra}
  datastore=$5
  datastore=${datastore:-remocra}
  echo "      Création de la couche ${layername} (style : ${defaultStylename:-aucun})"
  curl  -u ${USERNAME_PWD} -XPOST -H "Content-type: text/xml" -d @${localfilepath} "${GEOSERVER_URL_REST}/workspaces/${workspace}/datastores/${datastore}/featuretypes"
  # Style par défaut
  if [ ! -z "${defaultStylename+x}" ] ; then
    curl  -u ${USERNAME_PWD} -XPUT -H "Content-type: text/xml" -d "<layer><defaultStyle><name>${defaultStylename}</name><workspace>${workspace}</workspace></defaultStyle></layer>" "${GEOSERVER_URL_REST}/layers/${workspace}:${layername}"
  fi
}

# Crée la couche si besoin en la récupérant depuis le projet GitHub
function maybeCreateLayer {
  layername=$1
  localfilepath=$2
  defaultStylename=$3
  workspace=$4
  workspace=${workspace:-remocra}
  datastore=$5
  datastore=${datastore:-remocra}

  response=$(curl --write-out %{http_code} --silent --output /dev/null -u ${USERNAME_PWD} -XGET "${GEOSERVER_URL_REST}/layers/${layername}.json")
  if [ "${response}" -eq 404 ] ; then
    echo "    Couche ${layername} : récupération"
    curl -q "https://raw.githubusercontent.com/atolcd/sdis-remocra/master/server/sdis-remocra/var/remocra/geoserver_data/workspaces/${workspace}/${datastore}/${layername}/featuretype.xml" -o featuretype.xml
    createLayer ${layername} ${localfilepath} ${defaultStylename}
  else
    echo "    Couche ${layername} : déjà présente"
  fi
}

function clearCache {
  echo "Vidage du cache"
  curl -u ${USERNAME_PWD} -XPOST "${GEOSERVER_URL_REST}/reset"
}





dirname="GeoserverWorkingDir"
mkdir -p ${dirname}
cd ${dirname}


# HYDRANT_PIBI / HYDRANT_PENA
maybeCreateStyle remocra_hydrant remocra_hydrant.sld remocra_hydrant.sld true "remocra"
maybeCreateLayer v_hydrant_pibi featuretype.xml remocra_hydrant "remocra" "remocra"
maybeCreateLayer v_hydrant_pena featuretype.xml remocra_hydrant "remocra" "remocra"

# HYDRANT_PRESCRIT
maybeCreateStyle remocra_pibi_prescrit remocra_pibi_prescrit.sld remocra_pibi_prescrit.sld false "remocra"
maybeCreateLayer hydrant_prescrit featuretype.xml remocra_pibi_prescrit "remocra" "remocra"

# PERMIS
maybeCreateStyle remocra_permis remocra_permis.sld remocra_permis.sld false "remocra"
maybeCreateLayer v_permis featuretype.xml remocra_permis "remocra" "remocra"

# ZONE_COMPETENCE
maybeCreateLayer zone_competence featuretype.xml polygon "remocra" "remocra"

# ZONE_SPECIALE
maybeCreateLayer zone_speciale featuretype.xml polygon "remocra" "remocra"

# OLDEB
maybeCreateStyle remocra_oldeb remocra_oldeb.sld remocra_oldeb.sld false "remocra"
maybeCreateLayer oldeb_zonage featuretype.xml remocra_oldeb "remocra" "remocra"

# REQUETES
maybeCreateStyle "remocra_selection" "remocra_selection.sld" "remocra_selection.sld" false "remocra"
maybeCreateLayer "v_requete_modele_selection_detail" "featuretype.xml" "remocra_selection" "remocra" "remocra"


cd ..
rm -rf ${dirname}





clearCache

