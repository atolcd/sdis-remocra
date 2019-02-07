package fr.sdis83.remocra.serializer;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.database.NatureTable;
import fr.sdis83.remocra.parser.AbstractRemocraParser;
import fr.sdis83.remocra.parser.TourneeParser;

/**
 * Created by jpt on 03/10/13.
 */
public class HydrantSerializer extends AbstractSerializer {

    static final String HYDRANTS_XSI = "xsi:hydrants";
    static final String HYDRANTS = "hydrants";
    private static final String TAG_HYDRANT_PENA = "hydrantPena";
    private static final String TAG_HYDRANT_PIBI = "hydrantPibi";

    private static final String TAG_VERIF = "verif";

    private int colTypeHydrant;
    private int colNature;

    private boolean fullExportMode = false;

    public HydrantSerializer(Context context) {
        super(context);

    }

    public String serialize(Cursor cursor) throws IOException {
        this.fullExportMode = false;
        this.colTypeHydrant = cursor.getColumnIndex(HydrantTable.COLUMN_TYPE_HYDRANT);
        this.colNature = cursor.getColumnIndex(HydrantTable.COLUMN_NATURE);
        StringWriter writer = new StringWriter();
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(writer);
        serializer.startDocument(AbstractRemocraParser.ns, false);
        addHydrants(serializer, cursor);
        serializer.endDocument();
        return writer.toString();
    }


    public void serialize(XmlSerializer serializer, Cursor cursor) throws IOException {
        this.fullExportMode = true;
        this.colTypeHydrant = cursor.getColumnIndex(HydrantTable.COLUMN_TYPE_HYDRANT);
        this.colNature = cursor.getColumnIndex(HydrantTable.COLUMN_NATURE);
        addHydrants(serializer, cursor);
    }

    private void addHydrants(XmlSerializer serializer, Cursor cursor) throws IOException {
        if (!this.fullExportMode) {
            serializer.startTag(AbstractRemocraParser.ns, HYDRANTS_XSI);
            serializer.attribute(AbstractRemocraParser.ns, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        } else {
            serializer.startTag(AbstractRemocraParser.ns, HYDRANTS);
        }
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                addHydrant(serializer, cursor);
                cursor.moveToNext();
            }
        }
        if (!this.fullExportMode) {
            serializer.endTag(AbstractRemocraParser.ns, HYDRANTS_XSI);
        } else {
            serializer.endTag(AbstractRemocraParser.ns, HYDRANTS);
        }
    }

    private void addHydrant(XmlSerializer serializer, Cursor cursor) throws IOException {
        boolean isPibi = HydrantTable.TYPE_PIBI.equals(cursor.getString(colTypeHydrant));
        String tag = isPibi ? TAG_HYDRANT_PIBI : TAG_HYDRANT_PENA;
        String natureCode = getCodeFromIdReferentiel(cursor.getString(colNature), RemocraProvider.CONTENT_NATURE_URI);
        String typeSaisie = cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_TYPE_SAISIE));
        if (natureCode != null && (this.fullExportMode || !TextUtils.isEmpty(typeSaisie))) {
            serializer.startTag(AbstractRemocraParser.ns, tag);
            serializer.attribute(AbstractRemocraParser.ns, "xsi:type", natureCode);
            serializer.attribute(AbstractRemocraParser.ns, TAG_VERIF, (typeSaisie != null && typeSaisie.equals(HydrantTable.TYPE_SAISIE.VERIF.toString()) ? "true" : "false"));
            fillHydrant(serializer, cursor, tag, natureCode);
            serializer.endTag(AbstractRemocraParser.ns, tag);
        } else {
            Log.e("REMOCRA", "Hydrant without nature (" + cursor.getString(colNature) + "/" + natureCode + "/" + typeSaisie + ")");
        }
    }

    private void fillHydrant(XmlSerializer serializer, Cursor cursor, String tag, String natureCode) throws IOException {
        addBalise(serializer, TourneeParser.TAG_AGENT1, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_AGENT1)));
        addBalise(serializer, TourneeParser.TAG_AGENT2, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_AGENT2)));
        addBalise(serializer, TourneeParser.TAG_ANNEE_FABRICATION, cursor.getLong(cursor.getColumnIndex(HydrantTable.COLUMN_ANNEE_FAB)));
        addBaliseAnomalies(serializer, TourneeParser.TAG_ANOMALIES, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_ANOMALIES)));
        addBalise(serializer, TourneeParser.TAG_CODE_COMMUNE, getCodeFromLibelle(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_COMMUNE)), RemocraProvider.CONTENT_COMMUNE_URI));
        addBalise(serializer, TourneeParser.TAG_DOMAINE, getCodeFromIdReferentiel(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_DOMAINE)), RemocraProvider.CONTENT_DOMAINE_URI));
        addBalise(serializer, TourneeParser.TAG_NATURE_DECI, getCodeFromIdReferentiel(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_NATURE_DECI)), RemocraProvider.CONTENT_NATURE_DECI_URI));
        // Nature
        addBalise(serializer, TourneeParser.TAG_COMPLEMENT, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_COMPLEMENT)));
        addBaliseCoordonnee(serializer, cursor);
        addBalise(serializer, TourneeParser.TAG_COURRIER, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_COURRIER)));

        addBaliseDate(serializer, TourneeParser.TAG_DATE_ATTESTATION, cursor.getLong(cursor.getColumnIndex(HydrantTable.COLUMN_DATE_ATTESTATION)));
        addBaliseDate(serializer, TourneeParser.TAG_DATE_CTRL, cursor.getLong(cursor.getColumnIndex(HydrantTable.COLUMN_DATE_CTRL)));
        // Date GPS
        addBaliseDate(serializer, TourneeParser.TAG_DATE_MODIF, cursor.getLong(cursor.getColumnIndex(HydrantTable.COLUMN_DATE_MODIF)));
        addBaliseDate(serializer, TourneeParser.TAG_DATE_RECEP, cursor.getLong(cursor.getColumnIndex(HydrantTable.COLUMN_DATE_RECEP)));
        addBaliseDate(serializer, TourneeParser.TAG_DATE_RECO, cursor.getLong(cursor.getColumnIndex(HydrantTable.COLUMN_DATE_RECO)));
        addBaliseDate(serializer, TourneeParser.TAG_DATE_VERIF, cursor.getLong(cursor.getColumnIndex(HydrantTable.COLUMN_DATE_VERIF)));
        addBalise(serializer, TourneeParser.TAG_DISPO, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_DISPO)));
        addBalise(serializer, TourneeParser.TAG_GEST_PT_EAU, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_GEST_PTEAU)));
        addBalise(serializer, TourneeParser.TAG_LIEU_DIT, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_LIEUDIT)));
        String typeSaisie = cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_TYPE_SAISIE));
        if (this.fullExportMode || !typeSaisie.equals(HydrantTable.TYPE_SAISIE.CREA.toString())) {
            if (this.fullExportMode) {
                addBalise(serializer, "typeSaisie", typeSaisie);
            }
            addBalise(serializer, TourneeParser.TAG_NUMERO, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_NUMERO)));
            addBalise(serializer, TourneeParser.TAG_NUM_INTERNE, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_NUMERO_INTERNE)));
        }
        addBalise(serializer, TourneeParser.TAG_OBSERVATION, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_OBSERVATION)));
        if (cursor.getInt(cursor.getColumnIndex(HydrantTable.COLUMN_IS_NEW_PHOTO)) == 1) {
            addBaliseImage(serializer, TourneeParser.TAG_PHOTO, cursor.getBlob(cursor.getColumnIndex(HydrantTable.COLUMN_PHOTO)));
        }
        // Version
        addBalise(serializer, TourneeParser.TAG_VOIE, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_VOIE)));
        addBalise(serializer, TourneeParser.TAG_VOIE2, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_VOIE2)));
        if (TAG_HYDRANT_PIBI.equals(tag)) {
            addBalise(serializer, TourneeParser.TAG_CHOC, toValidBooleanValue(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_CHOC))));
            addBalise(serializer, TourneeParser.TAG_CODE_DIAMETRE, getCodeFromIdReferentiel(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_DIAMETRE)), RemocraProvider.CONTENT_DIAMETRE_URI));
            addBalise(serializer, TourneeParser.TAG_MARQUE, getCodeFromIdReferentiel(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_MARQUE)), RemocraProvider.CONTENT_MARQUE_URI));
            addBalise(serializer, TourneeParser.TAG_MODELE, getCodeFromIdReferentiel(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_MODELE)), RemocraProvider.CONTENT_MODELE_URI));
            // CodePena
            addBalise(serializer, TourneeParser.TAG_DEBIT, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_DEBIT)));
            addBalise(serializer, TourneeParser.TAG_DEBIT_MAX, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_DEBIT_MAX)));
            addBalise(serializer, TourneeParser.TAG_GEST_RESEAU, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_GEST_RESEAU)));
            addBalise(serializer, TourneeParser.TAG_NUM_SCP, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_NUMERO_SCP)));
            addBalise(serializer, TourneeParser.TAG_PRESSION, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_PRESSION)));
            addBalise(serializer, TourneeParser.TAG_PRESSION_DYN, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_PRESSION_DYN)));
            addBalise(serializer, TourneeParser.TAG_PRESSION_DYN_DEB, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_PRESSION_DYN_DEB)));
        } else {
            addBalise(serializer, TourneeParser.TAG_CAPACITE, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_CAPACITE)));
            addBalise(serializer, TourneeParser.TAG_DISPO_HBE, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_DISPO_HBE)));
            addBalise(serializer, TourneeParser.TAG_HBE, toValidBooleanValue(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_HBE))));
            if (natureCode.startsWith(NatureTable.CITERNE)) {
                addBalise(serializer, TourneeParser.TAG_CODE_MATERIAU, getCodeFromIdReferentiel(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_MATERIAU)), RemocraProvider.CONTENT_MATERIAU_URI));
                addBalise(serializer, TourneeParser.TAG_VOL_CONSTATE, getCodeFromIdReferentiel(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_VOL_CONSTATE)), RemocraProvider.CONTENT_VOL_CONSTATE_URI));
                addBalise(serializer, TourneeParser.TAG_QAPPOINT, cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_QAPPOINT)));
                if (NatureTable.CITERNE_FIXE.equals(natureCode)) {
                    addBalise(serializer, TourneeParser.TAG_POSITIONNEMENT, getCodeFromIdReferentiel(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_POSITIONNEMENT)), RemocraProvider.CONTENT_POSITIONNEMENT_URI));
                }
            }
        }
    }

    private void addBaliseCoordonnee(XmlSerializer serializer, Cursor cursor) throws IOException {
        double lon = cursor.getDouble(cursor.getColumnIndex(HydrantTable.COLUMN_COORD_LON));
        double lat = cursor.getDouble(cursor.getColumnIndex(HydrantTable.COLUMN_COORD_LAT));
        serializer.startTag(AbstractRemocraParser.ns, TourneeParser.TAG_COORDONNEE);

        serializer.startTag(AbstractRemocraParser.ns, TourneeParser.TAG_COORD_LATITUDE);
        serializer.text(String.valueOf(lat));
        serializer.endTag(AbstractRemocraParser.ns, TourneeParser.TAG_COORD_LATITUDE);

        serializer.startTag(AbstractRemocraParser.ns, TourneeParser.TAG_COORD_LONGITUDE);
        serializer.text(String.valueOf(lon));
        serializer.endTag(AbstractRemocraParser.ns, TourneeParser.TAG_COORD_LONGITUDE);

        serializer.endTag(AbstractRemocraParser.ns, TourneeParser.TAG_COORDONNEE);
    }

    private void addBaliseAnomalies(XmlSerializer serializer, String tag, String anomalies) throws IOException {

        if (anomalies != null) {
            String[] ids = anomalies.split(",");
            if (ids.length > 0) {
                serializer.startTag(AbstractRemocraParser.ns, tag);
                for (String id : ids) {
                    if (!TextUtils.isEmpty(id) && TextUtils.isDigitsOnly(id)) {
                        serializer.startTag(AbstractRemocraParser.ns, TourneeParser.TAG_ANOMALIE);
                        addBalise(serializer, TourneeParser.TAG_CODE, getCodeFromIdReferentiel(id, RemocraProvider.CONTENT_ANOMALIE_URI));
                        serializer.endTag(AbstractRemocraParser.ns, TourneeParser.TAG_ANOMALIE);
                    }
                }
                serializer.endTag(AbstractRemocraParser.ns, tag);
            }
        }

    }

}
