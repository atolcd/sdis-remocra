# déposer le vieux dump postgis1.5 ici 
export DUMP_REPOSITORY=/livraison/db ;
export REMOCRA_DB_PASSWORD=${REMOCRA_DB_PASSWORD:-xxxxxx};
docker run --name remocra-upgrade-db -d -v ${DUMP_REPOSITORY}:/var/lib/postgresql/ --rm -d mdillon/postgis:11-alpine;
sleep 20;
# Migration
docker exec -it -e PGUSER=postgres remocra-upgrade-db dropdb remocra;
docker exec -it -e PGUSER=postgres remocra-upgrade-db psql -c "CREATE user remocra WITH SUPERUSER ENCRYPTED PASSWORD '${REMOCRA_DB_PASSWORD}'";
docker exec -it -e PGUSER=postgres remocra-upgrade-db createdb remocra -E UTF8 remocra;
docker exec -it -e PGUSER=postgres remocra-upgrade-db psql remocra -c "CREATE EXTENSION postgis;";
docker exec -it -e PGUSER=postgres remocra-upgrade-db psql remocra -f /usr/local/share/postgresql/contrib/postgis-2.5/legacy.sql;
# si besoin :
docker exec -it -e PGUSER=postgres remocra-upgrade-db psql remocra -c "drop function ndims(geometry) cascade;";
docker exec -it -e PGUSER=postgres remocra-upgrade-db psql remocra -c "drop function srid(geometry) cascade;";
docker exec -it remocra-upgrade-db apk add perl;
docker exec -it -e PGUSER=postgres remocra-upgrade-db bash -c "perl /usr/local/share/postgresql/contrib/postgis-2.5/postgis_restore.pl /var/lib/postgresql/remocra-1.5.backup | psql remocra 2> /var/lib/postgresql/errors.txt";
docker exec -it remocra-upgrade-db cat /var/lib/postgresql/errors.txt;
docker exec -it remocra-upgrade-db apk del perl;
docker exec -it -e PGUSER=postgres remocra-upgrade-db psql remocra -f /usr/local/share/postgresql/contrib/postgis-2.5/uninstall_legacy.sql;

# Récupération du dump de la nouvelle base postgis 2.5
docker exec -it -e PGUSER=postgres remocra-upgrade-db pg_dump -Fc -b -v -f "/home/remocra-2.5.backup" remocra;
docker cp remocra-upgrade-db:/home/remocra-2.5.backup /livraison/db;
docker stop remocra-upgrade-db;
docker rmi mdillon/postgis:11-alpine;
