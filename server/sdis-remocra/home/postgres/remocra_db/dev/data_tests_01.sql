begin;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = remocra, pdi, public, pg_catalog;


-- Jeu de données

-- Zone de compétence "FR"
insert into remocra.zone_competence (code, nom, geometrie) values ('FR', 'France', geomfromtext('MULTIPOLYGON(((1157055 6049646.5,1157055 6158599,1159342 6158599,1159342 6233671,1242375.125 6233671,1242375.125 6102227.5,1233711.125 6102227.5,1233711.125 6049646.5,1157055 6049646.5)),((253465.140625 6997399.5,253465.140625 7016562,216607.21875 7016562,216607.21875 7097114,254268.3125 7097114,254268.3125 7114420.5,328282.71875 7114420.5,328282.71875 7039838,327053.5 7039838,327053.5 6997399.5,253465.140625 6997399.5)),((595004 6137163.5,595004 6164369.5,522390.96875 6164369.5,522390.96875 6179671.5,508067 6179671.5,508067 6178845.5,428171.96875 6178845.5,428171.96875 6193171.5,311995 6193171.5,311995 6283552,334283 6283552,334283 6389853,361762.96875 6389853,361762.96875 6448283.5,348630.96875 6448283.5,348630.96875 6582755.5,287967.96875 6582755.5,287967.96875 6649967.5,280972.96875 6649967.5,280972.96875 6704492.5,199376.984375 6704492.5,199376.984375 6760584.5,99225.9921875 6760584.5,99225.9921875 6873212,209750.984375 6873212,209750.984375 6881976,337516 6881976,337516 6856659,343081.96875 6856659,343081.96875 6969694,425027 6969694,425027 6929242,487572.96875 6929242,487572.96875 6998137,583879.9375 6998137,583879.9375 7030667,597189.9375 7030667,597189.9375 7101577,634421.9375 7101577,634421.9375 7110524,788153.0625 7110524,788153.0625 7009226,873032.0625 7009226,873032.0625 6949057,908589.0625 6949057,908589.0625 6943889,1004385.0625 6943889,1004385.0625 6939584,1038956.0625 6939584,1038956.0625 6895581,1082671.125 6895581,1082671.125 6789935.5,1044932.0625 6789935.5,1044932.0625 6710918.5,1006440.0625 6710918.5,1006440.0625 6610704.5,944643 6610704.5,944643 6596484,1013117.0625 6596484,1013117.0625 6541572,1027185 6541572,1027185 6445178.5,1022851.0625 6445178.5,1022851.0625 6370072,1077507 6370072,1077507 6272481.5,1018256.0625 6272481.5,1018256.0625 6214472.5,914649 6214472.5,914649 6232732.5,799606.9375 6232732.5,799606.9375 6263118.5,796333.0625 6263118.5,796333.0625 6234873.5,719561 6234873.5,719561 6172490.5,714448 6172490.5,714448 6137163.5,595004 6137163.5),(643206 6867927.5,643206 6853168,654970.9375 6853168,654970.9375 6867927.5,643206 6867927.5)))', 2154));


-- Organisme SDIS, utilisateur sdis-adm-app et message entête
insert into remocra.organisme (actif, code, email_contact, nom, type_organisme, profil_organisme, zone_competence, version) values (true, 'SDIS', null, 'SDIS', (select id from remocra.type_organisme where code = 'SDIS'), (select id from remocra.profil_organisme where code = 'SDIS'), (select id from remocra.zone_competence order by st_area(geometrie) desc limit 1), 1);

update remocra.param_conf set valeur=(select substring(md5(to_char(now(), 'HH24:MI:SS-MS')) from 1 for 7)) where cle='MESSAGE_ENTETE';

insert into remocra.utilisateur(actif, email, identifiant, message_remocra, nom, password, prenom, salt, telephone, version, organisme, profil_utilisateur) values (true, 'no-reply@sdis.fr', 'sdis-adm-app', true, 'Min', (md5((select valeur from remocra.param_conf where cle='MESSAGE_ENTETE') || '{' || md5(to_char(now(),'us-ss-mm-hh-dd-mm-yyyy') || 'sdis-adm-app') || '}')), 'Ad', (md5(to_char(now(),'us-ss-mm-hh-dd-mm-yyyy') || 'sdis-adm-app')), null, 1, (select id from remocra.organisme where code = 'SDIS'), (select id from remocra.profil_utilisateur where code = 'SDIS-ADM-APP'));

update remocra.param_conf set valeur='DEV : sdis-adm-app / ' || (select valeur from remocra.param_conf where cle='MESSAGE_ENTETE') || ' (à modifier)' where cle='MESSAGE_ENTETE';

update remocra.param_conf set valeur=(select (select id from remocra.utilisateur where identifiant='sdis-adm-app')::text) where cle='PDI_NOTIFICATION_GENERAL_UTILISATEUR_ID' or cle='PDI_NOTIFICATION_KML_UTILISATEUR_ID';

commit;

