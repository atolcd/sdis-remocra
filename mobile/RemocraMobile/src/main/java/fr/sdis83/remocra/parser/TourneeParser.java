package fr.sdis83.remocra.parser;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import fr.sdis83.remocra.GlobalRemocra;
import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.AnomalieTable;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.database.TourneeTable;
import fr.sdis83.remocra.util.DbUtils;
import fr.sdis83.remocra.util.ImageUtils;

/**
 * Created by jpt on 19/09/13.
 */
public class TourneeParser extends AbstractRemocraParser {

    public static final String TAG_AGENT1 = "agent1";
    public static final String TAG_AGENT2 = "agent2";
    public static final String TAG_DATE_RECEP = "dateRecep";
    public static final String TAG_DATE_RECO = "dateReco";
    public static final String TAG_DATE_CTRL = "dateContr";
    public static final String TAG_DATE_MODIF = "dateModification";
    public static final String TAG_DATE_VERIF = "dateVerif";
    public static final String TAG_DATE_GPS = "dateGps";
    public static final String TAG_NUMERO = "numero";
    public static final String TAG_NUM_INTERNE = "numeroInterne";
    public static final String TAG_NUM_SCP = "numeroSCP";
    public static final String TAG_DISPO = "dispo";
    public static final String TAG_DISPO_HBE = "dispoHbe";
    public static final String TAG_CODE_COMMUNE = "codeCommune";
    public static final String TAG_LIEU_DIT = "lieuDit";
    public static final String TAG_VOIE = "voie";
    public static final String TAG_VOIE2 = "voie2";
    public static final String TAG_COMPLEMENT = "complement";
    public static final String TAG_CODE_DIAMETRE = "codeDiametre";
    public static final String TAG_DEBIT = "debit";
    public static final String TAG_PRESSION = "pression";
    public static final String TAG_DEBIT_MAX = "debitMax";
    public static final String TAG_PRESSION_DYN = "pressionDyn";
    public static final String TAG_PRESSION_DYN_DEB = "pressionDynDeb";
    public static final String TAG_HBE = "hbe";
    public static final String TAG_POSITIONNEMENT = "codePositionnement";
    public static final String TAG_CODE_MATERIAU = "codeMateriau";
    public static final String TAG_QAPPOINT = "qAppoint";
    public static final String TAG_CAPACITE = "capacite";
    public static final String TAG_ANOMALIES = "anomalies";
    public static final String TAG_MARQUE = "codeMarque";
    public static final String TAG_MODELE = "codeModele";
    public static final String TAG_ANNEE_FABRICATION = "anneeFabrication";
    public static final String TAG_CHOC = "choc";
    public static final String TAG_DOMAINE = "codeDomaine";
    public static final String TAG_GEST_PT_EAU = "gestPointEau";
    public static final String TAG_GEST_RESEAU = "gestReseau";
    public static final String TAG_COURRIER = "courrier";
    public static final String TAG_DATE_ATTESTATION = "dateAttestation";
    public static final String TAG_ANOMALIE = "anomalie";
    public static final String TAG_CODE = "code";
    public static final String TAG_VOL_CONSTATE = "codeVolConstate";
    public static final String TAG_OBSERVATION = "observation";
    public static final String TAG_PHOTO = "photo";
    public static final String TAG_COORD_DFCI = "coordDFCI";
    public static final String TAG_COORDONNEE = "coordonnees";
    public static final String TAG_COORD_LATITUDE = "latitude";
    public static final String TAG_COORD_LONGITUDE = "longitude";
    public static final String TAG_CODE_NATURE = "codeNature";
    public static final String TAG_TYPE_SAISIE = "typeSaisie";
    public static final String TAG_NATURE_DECI = "codeNatureDeci";
    public static final String TAG_JUMELE = "jumele";
    public static final String TAG_GROS_DEBIT = "grosDebit";
    public static final String TAG_DEBIT_RENFORCE = "debitRenforce";
    public static final String TAG_ADRESSE = "adresse";
    public static final String TAG_ASPIRATIONS = "aspirations";
    public static final String TAG_ILLIMITEE = "illimitee";
    public static final String TAG_NB_VISITE = "nbVisite";
    public static final String TAG_DATE_VISITE = "dateVisite";
    public static final String TAG_HEURE_VISITE = "heureVisite";


    private ArrayList<ContentValues> lstHydrant;
    private ArrayList<Integer> lstAnomalieStandard;
    private ArrayList<Integer> lstAnomalieApplicative;

    public TourneeParser(RemocraParser remocraParser) {
        super(remocraParser);
    }

    @Override
    public Iterable<? extends String> getNames() {
        return Arrays.asList("tournees", "tournee");
    }

    @Override
    public void processNode(XmlPullParser xmlParser, String name) throws IOException, XmlPullParserException {
        if ("tournee".equals(name)) {
            readTournee(xmlParser);
        }
    }

    public void readTournee(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        ContentValues values = new ContentValues();
        lstHydrant = new ArrayList<ContentValues>();
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String nameTag = xmlParser.getName();
            if ("debSync".equals(nameTag)) {
                values.put(TourneeTable.COLUMN_NAME_DEB_SYNC, this.readBaliseDate(xmlParser, nameTag));
            } else if ("lastSync".equals(nameTag)) {
                values.put(TourneeTable.COLUMN_NAME_LAST_SYNC, this.readBaliseDate(xmlParser, nameTag));
            } else if ("id".equals(nameTag)) {
                values.put(TourneeTable._ID, this.readBaliseText(xmlParser, nameTag));
            }else if ("nom".equals(nameTag)) {
                values.put(TourneeTable.COLUMN_NOM, this.readBaliseText(xmlParser, nameTag));
            }else if ("pourcent".equals(nameTag)) {
                values.put(TourneeTable.COLUMN_POURCENT, this.readBaliseText(xmlParser, nameTag));
            }
            else if ("hydrants".equals(nameTag)) {
                readHydrants(xmlParser);
            } else {
                skip(xmlParser);
            }
        }
        values.put(TourneeTable.COLUMN_NB_HYDRANT, lstHydrant.size());
        if (lstHydrant.size() > 0) {
            for (ContentValues hydrant : lstHydrant) {
                hydrant.put(HydrantTable.COLUMN_TOURNEE, values.getAsInteger(TourneeTable._ID));
                Long maxDate = Collections.max(Arrays.asList(
                        DbUtils.nvl(hydrant.getAsLong(HydrantTable.COLUMN_DATE_CTRL), -1L),
                        DbUtils.nvl(hydrant.getAsLong(HydrantTable.COLUMN_DATE_RECEP), -1L),
                        DbUtils.nvl(hydrant.getAsLong(HydrantTable.COLUMN_DATE_RECO), -1L),
                        DbUtils.nvl(hydrant.getAsLong(HydrantTable.COLUMN_DATE_VERIF), -1L)));
                Long dateDebSync = DbUtils.nvl(values.getAsLong(TourneeTable.COLUMN_NAME_DEB_SYNC), 0L);
                if (maxDate > dateDebSync || DbUtils.nvl(values.getAsInteger(TourneeTable.COLUMN_POURCENT), 0) == 100) {
                    hydrant.put(HydrantTable.COLUMN_STATE_H1, true);
                    hydrant.put(HydrantTable.COLUMN_STATE_H3, true);
                    hydrant.put(HydrantTable.COLUMN_STATE_H2, true);
                    hydrant.put(HydrantTable.COLUMN_STATE_H4, true);
                }
                this.addContent(RemocraProvider.CONTENT_HYDRANT_URI, hydrant);
            }
            this.addContent(RemocraProvider.CONTENT_TOURNEE_URI, values);
        }
    }

    private void readHydrants(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String name = xmlParser.getName();
            if ("hydrantPibi".equals(name) || "hydrantPena".equals(name)) {
                readHydrant(xmlParser);
            }
        }
    }

    private void readHydrant(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        String nature = xmlParser.getAttributeValue(ns, "xsi:type");
        if (!TextUtils.isEmpty(nature)) {
            ContentValues values = new ContentValues();
            values.put(HydrantTable.COLUMN_CODE_NATURE, nature);
            values.put(HydrantTable.COLUMN_NATURE, this.remocraParser.getIdFromCodeReferentiel(nature, RemocraProvider.CONTENT_NATURE_URI));
            values.put(HydrantTable.COLUMN_TYPE_HYDRANT, "hydrantPibi".equals(xmlParser.getName()) ? HydrantTable.TYPE_PIBI : HydrantTable.TYPE_PENA);

            while (xmlParser.next() != XmlPullParser.END_TAG) {
                String name = xmlParser.getName();
                if (TAG_CODE_NATURE.equals(name)) {
                    if (nature != this.readBaliseText(xmlParser, name)) {
                        Log.e("REMOCRA", "Problème de cohérence du fichier");
                    }
                } else if (TAG_AGENT1.equals(name)) {
                    values.put(HydrantTable.COLUMN_AGENT1, this.readBaliseText(xmlParser, name));
                } else if (TAG_AGENT2.equals(name)) {
                    values.put(HydrantTable.COLUMN_AGENT2, this.readBaliseText(xmlParser, name));
                } else if (TAG_DATE_RECEP.equals(name)) {
                    values.put(HydrantTable.COLUMN_DATE_RECEP, this.readBaliseDate(xmlParser, name));
                } else if (TAG_DATE_RECO.equals(name)) {
                    values.put(HydrantTable.COLUMN_DATE_RECO, this.readBaliseDate(xmlParser, name));
                } else if (TAG_DATE_VERIF.equals(name)) {
                    values.put(HydrantTable.COLUMN_DATE_VERIF, this.readBaliseDate(xmlParser, name));
                } else if (TAG_DATE_CTRL.equals(name)) {
                    values.put(HydrantTable.COLUMN_DATE_CTRL, this.readBaliseDate(xmlParser, name));
                } else if (TAG_DATE_MODIF.equals(name)) {
                    values.put(HydrantTable.COLUMN_DATE_MODIF, this.readBaliseDate(xmlParser, name));
                } else if (TAG_DATE_GPS.equals(name)) {
                    values.put(HydrantTable.COLUMN_DATE_GPS, this.readBaliseDate(xmlParser, name));
                } else if (TAG_NUMERO.equals(name)) {
                    values.put(HydrantTable.COLUMN_NUMERO, this.readBaliseText(xmlParser, name));
                } else if (TAG_NUM_INTERNE.equals(name)) {
                    values.put(HydrantTable.COLUMN_NUMERO_INTERNE, this.readBaliseText(xmlParser, name));
                } else if (TAG_NUM_SCP.equals(name)) {
                    values.put(HydrantTable.COLUMN_NUMERO_SCP, this.readBaliseText(xmlParser, name));
                } else if (TAG_DISPO.equals(name)) {
                    values.put(HydrantTable.COLUMN_DISPO, this.readBaliseText(xmlParser, name));
                } else if (TAG_DISPO_HBE.equals(name)) {
                    values.put(HydrantTable.COLUMN_DISPO_HBE, this.readBaliseText(xmlParser, name));
                } else if (TAG_CODE_COMMUNE.equals(name)) {
                    values.put(HydrantTable.COLUMN_COMMUNE, this.readBaliseLibelleReferentiel(xmlParser, name, RemocraProvider.CONTENT_COMMUNE_URI));
                } else if (TAG_LIEU_DIT.equals(name)) {
                    values.put(HydrantTable.COLUMN_LIEUDIT, this.readBaliseText(xmlParser, name));
                } else if (TAG_VOIE.equals(name)) {
                    values.put(HydrantTable.COLUMN_VOIE, this.readBaliseText(xmlParser, name));
                } else if (TAG_VOIE2.equals(name)) {
                    values.put(HydrantTable.COLUMN_VOIE2, this.readBaliseText(xmlParser, name));
                } else if (TAG_COMPLEMENT.equals(name)) {
                    values.put(HydrantTable.COLUMN_COMPLEMENT, this.readBaliseText(xmlParser, name));
                } else if (TAG_CODE_DIAMETRE.equals(name)) {
                    values.put(HydrantTable.COLUMN_DIAMETRE, this.readBaliseIdReferentiel(xmlParser, name, RemocraProvider.CONTENT_DIAMETRE_URI));
                } else if (TAG_DEBIT.equals(name)) {
                    values.put(HydrantTable.COLUMN_DEBIT, this.readBaliseText(xmlParser, name));
                } else if (TAG_PRESSION.equals(name)) {
                    values.put(HydrantTable.COLUMN_PRESSION, this.readBaliseText(xmlParser, name));
                } else if (TAG_DEBIT_MAX.equals(name)) {
                    values.put(HydrantTable.COLUMN_DEBIT_MAX, this.readBaliseText(xmlParser, name));
                } else if (TAG_PRESSION_DYN.equals(name)) {
                    values.put(HydrantTable.COLUMN_PRESSION_DYN, this.readBaliseText(xmlParser, name));
                } else if (TAG_PRESSION_DYN_DEB.equals(name)) {
                    values.put(HydrantTable.COLUMN_PRESSION_DYN_DEB, this.readBaliseText(xmlParser, name));
                } else if (TAG_HBE.equals(name)) {
                    values.put(HydrantTable.COLUMN_HBE, this.readBaliseBoolean(xmlParser, name));
                } else if (TAG_POSITIONNEMENT.equals(name)) {
                    values.put(HydrantTable.COLUMN_POSITIONNEMENT, this.readBaliseText(xmlParser, name));
                } else if (TAG_CODE_MATERIAU.equals(name)) {
                    values.put(HydrantTable.COLUMN_MATERIAU, this.readBaliseIdReferentiel(xmlParser, name, RemocraProvider.CONTENT_MATERIAU_URI));
                } else if (TAG_QAPPOINT.equals(name)) {
                    values.put(HydrantTable.COLUMN_QAPPOINT, this.readBaliseText(xmlParser, name));
                } else if (TAG_CAPACITE.equals(name)) {
                    values.put(HydrantTable.COLUMN_CAPACITE, this.readBaliseText(xmlParser, name));
                } else if (TAG_MARQUE.equals(name)) {
                    values.put(HydrantTable.COLUMN_MARQUE, this.readBaliseIdReferentiel(xmlParser, name, RemocraProvider.CONTENT_MARQUE_URI));
                } else if (TAG_MODELE.equals(name)) {
                    values.put(HydrantTable.COLUMN_MODELE, this.readBaliseIdReferentiel(xmlParser, name, RemocraProvider.CONTENT_MODELE_URI));
                } else if (TAG_ANNEE_FABRICATION.equals(name)) {
                    values.put(HydrantTable.COLUMN_ANNEE_FAB, this.readBaliseText(xmlParser, name));
                } else if (TAG_CHOC.equals(name)) {
                    values.put(HydrantTable.COLUMN_CHOC, this.readBaliseBoolean(xmlParser, name));
                } else if (TAG_DOMAINE.equals(name)) {
                    values.put(HydrantTable.COLUMN_DOMAINE, this.readBaliseIdReferentiel(xmlParser, name, RemocraProvider.CONTENT_DOMAINE_URI));
                } else if (TAG_GEST_PT_EAU.equals(name)) {
                    values.put(HydrantTable.COLUMN_GEST_PTEAU, this.readBaliseText(xmlParser, name));
                } else if (TAG_GEST_RESEAU.equals(name)) {
                    values.put(HydrantTable.COLUMN_GEST_RESEAU, this.readBaliseText(xmlParser, name));
                } else if (TAG_COURRIER.equals(name)) {
                    values.put(HydrantTable.COLUMN_COURRIER, this.readBaliseText(xmlParser, name));
                } else if (TAG_DATE_ATTESTATION.equals(name)) {
                    values.put(HydrantTable.COLUMN_DATE_ATTESTATION, this.readBaliseDate(xmlParser, name));
                } else if (TAG_VOL_CONSTATE.equals(name)) {
                    values.put(HydrantTable.COLUMN_VOL_CONSTATE, this.readBaliseIdReferentiel(xmlParser, name, RemocraProvider.CONTENT_VOL_CONSTATE_URI));
                } else if (TAG_OBSERVATION.equals(name)) {
                    values.put(HydrantTable.COLUMN_OBSERVATION, this.readBaliseText(xmlParser, name));
                } else if (TAG_COORD_DFCI.equals(name)) {
                    values.put(HydrantTable.COLUMN_COORD_DFCI, this.readBaliseText(xmlParser, name));
                } else if (TAG_COORDONNEE.equals(name)) {
                    this.readCoordonnee(xmlParser, values);
                } else if (TAG_PHOTO.equals(name)) {
                    this.readPhoto(xmlParser, values);
                } else if (TAG_ANOMALIES.equals(name)) {
                    this.readBaliseAnomalies(xmlParser, name, values);
                }else if (TAG_TYPE_SAISIE.equals(name)) {
                    values.put(HydrantTable.COLUMN_TYPE_SAISIE, this.readBaliseText(xmlParser, name));
                } else if(TAG_NATURE_DECI.equals(name)) {
                    values.put(HydrantTable.COLUMN_NATURE_DECI, this.readBaliseIdReferentiel(xmlParser, name, RemocraProvider.CONTENT_NATURE_DECI_URI));
                } else if(TAG_ILLIMITEE.equals(name)) {
                    values.put(HydrantTable.COLUMN_ILLIMITEE, this.readBaliseBoolean(xmlParser, name));
                } else if(TAG_ASPIRATIONS.equals(name)) {
                    values.put(HydrantTable.COLUMN_ASPIRATIONS, this.readBaliseText(xmlParser, name));
                } else if(TAG_GROS_DEBIT.equals(name)) {
                    values.put(HydrantTable.COLUMN_GROS_DEBIT, this.readBaliseBoolean(xmlParser, name));
                } else if(TAG_JUMELE.equals(name)) {
                    values.put(HydrantTable.COLUMN_JUMELE, this.readBaliseText(xmlParser, name));
                } else if(TAG_DEBIT_RENFORCE.equals(name)) {
                    values.put(HydrantTable.COLUMN_DEBIT_RENFORCE, this.readBaliseBoolean(xmlParser, name));
                } else if(TAG_ADRESSE.equals(name)) {
                    values.put(HydrantTable.COLUMN_ADRESSE, this.readBaliseText(xmlParser, name));
                } else if(TAG_NB_VISITE.equals(name)) {
                    values.put(HydrantTable.COLUMN_NB_VISITE, this.readBaliseText(xmlParser, name));
                } else {
                    skip(xmlParser);
                }
            }

            if (values.size() > 0) {
                this.lstHydrant.add(values);
            }
        } else {
            Log.e("REMOCRA", "Hydrant sans xsi:type défini");
            skip(xmlParser);
        }
    }

    private void readCoordonnee(XmlPullParser parser, ContentValues values) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_COORDONNEE);
        Double lat = null;
        Double lon = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (TAG_COORD_LATITUDE.equals(name)) {
                lat = readBaliseDouble(parser, name);
            } else if (TAG_COORD_LONGITUDE.equals(name)) {
                lon = readBaliseDouble(parser, name);
            }
        }
        if (lat != null && lon != null) {
            values.put(HydrantTable.COLUMN_COORD_LAT, lat);
            values.put(HydrantTable.COLUMN_COORD_LON, lon);
        }
        parser.require(XmlPullParser.END_TAG, ns, TAG_COORDONNEE);
    }

    private void readPhoto(XmlPullParser parser, ContentValues values) throws IOException, XmlPullParserException {
        String text64 = readBaliseText(parser, TAG_PHOTO);
        if (!TextUtils.isEmpty(text64)) {
            byte[] imgData = Base64.decode(text64, Base64.DEFAULT);
            values.put(HydrantTable.COLUMN_PHOTO, ImageUtils.getMediumBytes(imgData));
            values.put(HydrantTable.COLUMN_PHOTO_MINI, ImageUtils.getMiniatureBytes(imgData));
        }
    }

    private ContentValues readBaliseAnomalies(XmlPullParser parser, String balise, ContentValues values) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, balise);
        lstAnomalieStandard = new ArrayList<Integer>();
        lstAnomalieApplicative = new ArrayList<Integer>();
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (TAG_ANOMALIE.equals(name)) {
                readAnomalie(parser);
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, balise);

        values.put(HydrantTable.COLUMN_ANOMALIES, TextUtils.join(",", lstAnomalieStandard));
        values.put(HydrantTable.COLUMN_ANOMALIES_APP, TextUtils.join(",", lstAnomalieApplicative));

        return values;
    }

    private void readAnomalie(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_ANOMALIE);
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (TAG_CODE.equals(name)) {
                String codeAnomalie = readBaliseText(parser, name);
                ContentValues anomalie = this.remocraParser.getContentFromCodeReferentiel(codeAnomalie, RemocraProvider.CONTENT_ANOMALIE_URI);
                if (anomalie != null) {
                    Integer idAnomalie = anomalie.getAsInteger(AnomalieTable._ID);
                    if (TextUtils.isEmpty(anomalie.getAsString(AnomalieTable.COLUMN_CRITERE))) {
                        // anomalie "applicative"
                        lstAnomalieApplicative.add(idAnomalie);
                    } else {
                        // anomalie "standard"
                        lstAnomalieStandard.add(idAnomalie);
                    }
                }
            } else {
                skip(parser);
            }
        }
    }

    @Override
    protected void onAttach(RemocraParser remocraParser) {
        super.onAttach(remocraParser);
        this.addContent(RemocraProvider.CONTENT_TOURNEE_URI, null);
        this.addContent(RemocraProvider.CONTENT_HYDRANT_URI, null);
    }
}
