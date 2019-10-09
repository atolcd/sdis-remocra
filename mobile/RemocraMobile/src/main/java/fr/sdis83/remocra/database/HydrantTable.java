package fr.sdis83.remocra.database;

import android.content.Context;
import android.provider.BaseColumns;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.util.DbUtils;

/**
 * Created by jpt on 06/08/13.
 */
public final class HydrantTable implements BaseColumns {

    public static enum TYPE_SAISIE {
        // TODO : Gérer correctement le mode "lecture seule" partout dans l'application
        LECT,
        CREA,
        RECEP,
        RECO,
        CTRL,
        VERIF
    }

    public static final String TYPE_PIBI = "PIBI";
    public static final String TYPE_PENA = "PENA";

    public static final String TABLE_NAME = "hydrant";

    public static final String COLUMN_TOURNEE = "tournee";

    public static final String COLUMN_TYPE_HYDRANT = "typeHydrant";

    public static final String COLUMN_DATE_RECEP = "dateRecep";
    public static final String COLUMN_DATE_RECO = "dateReco";
    public static final String COLUMN_DATE_CTRL = "dateCtrl";
    public static final String COLUMN_DATE_MODIF = "dateModif";
    public static final String COLUMN_DATE_VERIF = "dateVerif";

    public static final String COLUMN_DATE_GPS = "dateGps";
    public static final String COLUMN_NUMERO = "numero";
    public static final String COLUMN_NUMERO_INTERNE = "numeroInterne";
    public static final String COLUMN_NUMERO_SCP = "numeroSCP";

    public static final String COLUMN_DISPO = "dispo";
    public static final String COLUMN_DISPO_HBE = "dispoHbe";
    public static final String COLUMN_AGENT1 = "agent1";
    public static final String COLUMN_AGENT2 = "agent2";
    public static final String COLUMN_NATURE = "nature";
    public static final String COLUMN_NATURE_DECI = "natureDeci";

    // localisation
    public static final String COLUMN_COMMUNE = "commune";
    public static final String COLUMN_LIEUDIT = "lieudit";
    public static final String COLUMN_VOIE = "voie";
    public static final String COLUMN_VOIE2 = "voie2";
    public static final String COLUMN_COMPLEMENT = "complement";

    //
    public static final String COLUMN_DIAMETRE = "diametre";

    // Vérification pibi
    public static final String COLUMN_DEBIT = "debit";
    public static final String COLUMN_PRESSION = "pression";
    public static final String COLUMN_DEBIT_MAX = "debitMax";
    public static final String COLUMN_PRESSION_DYN = "pressionDyn";
    public static final String COLUMN_PRESSION_DYN_DEB = "pressionDynDeb";

    // PENA
    public static final String COLUMN_HBE = "hbe";

    // Citerne
    public static final String COLUMN_POSITIONNEMENT = "positionnement";
    public static final String COLUMN_MATERIAU = "materiau";
    public static final String COLUMN_QAPPOINT = "qappoint";

    // - Vérification pena
    public static final String COLUMN_CAPACITE = "capacite";
    public static final String COLUMN_VOL_CONSTATE = "volConstate";

    // Element de MCO
    public static final String COLUMN_MARQUE = "marque";
    public static final String COLUMN_MODELE = "modele";
    public static final String COLUMN_ANNEE_FAB = "annee";
    public static final String COLUMN_CHOC = "choc";

    // Gestionnaire
    public static final String COLUMN_DOMAINE = "domaine";
    public static final String COLUMN_GEST_PTEAU = "gestPtEau";
    public static final String COLUMN_GEST_RESEAU = "gestReseau";

    // Divers
    public static final String COLUMN_COURRIER = "courrier";
    public static final String COLUMN_DATE_ATTESTATION = "dateAttestation";

    // Anomalie
    public static final String COLUMN_ANOMALIES = "anomalies";
    public static final String COLUMN_ANOMALIES_APP = "anomalies_app";

    // Observation
    public static final String COLUMN_OBSERVATION = "observation";

    // Photo (stockée en binaire)
    public static final String COLUMN_PHOTO = "photo";
    public static final String COLUMN_PHOTO_MINI = "photoMini";

    // flag pour savoir si c'est une nouvelle photo ou non
    public static final String COLUMN_IS_NEW_PHOTO = "isNewPhoto";

    // localisation
    public static final String COLUMN_COORD_DFCI = "coordDFCI";
    public static final String COLUMN_COORD_LON = "coordLon";
    public static final String COLUMN_COORD_LAT = "coordLat";

    // état interne
    public static final String COLUMN_TYPE_SAISIE = "typeSaisie";
    public static final String COLUMN_STATE_H0 = "hydrant0";
    public static final String COLUMN_STATE_H1 = "hydrant1";
    public static final String COLUMN_STATE_H2 = "hydrant2";
    public static final String COLUMN_STATE_H3 = "hydrant3";
    public static final String COLUMN_STATE_H4 = "hydrant4";
    public static final String COLUMN_STATES = "sum_states";
    public static final String COLUMN_CODE_NATURE = "code_nature";
    public static final String COLUMN_CONTROLE = "controle";
    public static final String COLUMN_DATE_VISITE = "dateVisite";
    public static final String COLUMN_DEBIT_RENFORCE = "debitRenforce";
    public static final String COLUMN_GROS_DEBIT = "grosDebit";
    public static final String COLUMN_JUMELE = "jumele";
    public static final String COLUMN_ILLIMITEE = "illimitee";
    public static final String COLUMN_ASPIRATIONS = "aspirations";
    public static final String COLUMN_ADRESSE = "adresse";
    public static final String COLUMN_NB_VISITE = "nbVisite";


    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_TYPE_HYDRANT + " TEXT not null," +
            COLUMN_TOURNEE + " INTEGER null," +
            COLUMN_NATURE + " INTEGER not null," +
            COLUMN_DATE_RECEP + " INTEGER null," +
            COLUMN_DATE_RECO + " INTEGER null," +
            COLUMN_DATE_CTRL + " INTEGER null," +
            COLUMN_DATE_VERIF + " INTEGER null," +
            COLUMN_DATE_MODIF + " INTEGER null," +
            COLUMN_DATE_GPS + " INTEGER null," +
            COLUMN_NUMERO + " TEXT not null," +
            COLUMN_NUMERO_INTERNE + " INTEGER null," +
            COLUMN_NUMERO_SCP + " TEXT null," +

            COLUMN_DISPO + " BOOLEAN null," +
            COLUMN_DISPO_HBE + " BOOLEAN null," +
            COLUMN_AGENT1 + " TEXT null," +
            COLUMN_AGENT2 + " TEXT null," +

            COLUMN_COMMUNE + " TEXT null," +
            COLUMN_LIEUDIT + " TEXT null," +
            COLUMN_VOIE + " TEXT null," +
            COLUMN_VOIE2 + " TEXT null," +
            COLUMN_COMPLEMENT + " TEXT null," +

            COLUMN_DIAMETRE + " INTEGER null," +

            COLUMN_DEBIT + " REAL null," +
            COLUMN_PRESSION + " REAL null," +
            COLUMN_DEBIT_MAX + " REAL null," +
            COLUMN_PRESSION_DYN + " REAL null," +
            COLUMN_PRESSION_DYN_DEB + " REAL null," +

            COLUMN_HBE + " BOOLEAN DEFAULT FALSE," +
            COLUMN_POSITIONNEMENT + " INTEGER null," +
            COLUMN_MATERIAU + " INTEGER null," +
            COLUMN_QAPPOINT + " REAL null," +

            COLUMN_CAPACITE + " REAL null," +
            COLUMN_VOL_CONSTATE + " INTEGER null," +

            COLUMN_MARQUE + " INTEGER null," +
            COLUMN_MODELE + " INTEGER null," +
            COLUMN_ANNEE_FAB + " INTEGER null," +
            COLUMN_CHOC + " BOOLEAN DEFAULT FALSE," +

            COLUMN_DOMAINE + " TEXT null," +
            COLUMN_GEST_PTEAU + " TEXT null," +
            COLUMN_GEST_RESEAU + " TEXT null," +
            COLUMN_COURRIER + " TEXT null," +
            COLUMN_DATE_ATTESTATION + " INTEGER null," +

            COLUMN_ANOMALIES + " TEXT null," +
            COLUMN_ANOMALIES_APP + " TEXT null," +

            COLUMN_OBSERVATION + " TEXT null," +

            COLUMN_PHOTO + " BLOB null," +
            COLUMN_PHOTO_MINI + " BLOB null," +
            COLUMN_IS_NEW_PHOTO + "  BOOLEAN DEFAULT FALSE," +

            COLUMN_COORD_DFCI + " TEXT null," +
            COLUMN_COORD_LON + " REAL null," +
            COLUMN_COORD_LAT + " REAL null," +
            COLUMN_STATE_H0 + " BOOLEAN DEFAULT TRUE," +

            COLUMN_STATE_H1 + " BOOLEAN DEFAULT FALSE," +
            COLUMN_STATE_H2 + " BOOLEAN DEFAULT FALSE," +
            COLUMN_STATE_H3 + " BOOLEAN DEFAULT FALSE," +
            COLUMN_STATE_H4 + " BOOLEAN DEFAULT FALSE," +
            COLUMN_CONTROLE + " BOOLEAN DEFAULT FALSE," +
            COLUMN_TYPE_SAISIE + " TEXT null," +
            COLUMN_CODE_NATURE + " TEXT not null," +
            COLUMN_DATE_VISITE + " INTEGER null," +
            COLUMN_NATURE_DECI + " TEXT null, "+
            COLUMN_JUMELE + " TEXT null, "+
            COLUMN_ILLIMITEE + " BOOLEAN DEFAULT FALSE, "+
            COLUMN_DEBIT_RENFORCE + " BOOLEAN DEFAULT FALSE, "+
            COLUMN_GROS_DEBIT + " BOOLEAN DEFAULT FALSE, "+
            COLUMN_ADRESSE + " STRING null, "+
            COLUMN_ASPIRATIONS + " INTEGER null, "+
            COLUMN_NB_VISITE + " INTEGER null "+

            " )";

}
