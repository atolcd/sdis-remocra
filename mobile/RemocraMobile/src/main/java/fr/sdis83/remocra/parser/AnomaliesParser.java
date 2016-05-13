package fr.sdis83.remocra.parser;

import android.content.ContentValues;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.AnomalieNatureTable;
import fr.sdis83.remocra.database.AnomalieTable;

/**
 * Created by jpt on 19/09/13.
 */
public class AnomaliesParser extends AbstractRemocraParser {

    private static final String TAG_NATURES = "natures";
    private static final String TAG_NATURE = "nature";
    private static final String TAG_CODE_NATURE = "codeNature";
    private static final String TAG_VALEUR = "valeur";
    private static final String TAG_SAISIES = "saisies";
    private static final String TAG_SAISIE = "saisie";
    private static final String TAG_VALEUR_HBE = "valeurAdmin";

    public AnomaliesParser(RemocraParser remocraParser) {
        super(remocraParser);
    }

    @Override
    public Iterable<? extends String> getNames() {
        return Arrays.asList("anomalies", "anomalie");
    }

    @Override
    protected void processNode(XmlPullParser xmlParser, String name) throws IOException, XmlPullParserException {
        if ("anomalie".equals(name)) {
            readAnomalie(xmlParser);
        }
    }

    private void readAnomalie(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        ContentValues values = new ContentValues();
        ArrayList<ContentValues> lstNature = null;
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String name = xmlParser.getName();
            if ("critere".equals(name)) {
                values.put(AnomalieTable.COLUMN_CRITERE, this.readBaliseText(xmlParser, name));
            } else if ("libelle".equals(name)) {
                values.put(AnomalieTable.COLUMN_LIBELLE, this.readBaliseText(xmlParser, name));
            } else if ("code".equals(name)) {
                values.put(AnomalieTable.COLUMN_CODE, this.readBaliseText(xmlParser, name));
            } else if ("natures".equals(name)) {
                lstNature = this.readBaliseNatures(xmlParser);
            } else {
                skip(xmlParser);
            }
        }
        this.addContent(RemocraProvider.CONTENT_ANOMALIE_URI, values);
        Integer idAnomalie = values.getAsInteger(AnomalieTable._ID);

        if (lstNature != null && lstNature.size() > 0) {
            for (ContentValues anomalieNature : lstNature) {
                anomalieNature.put(AnomalieNatureTable.COLUMN_ANOMALIE, idAnomalie);
                this.addContent(RemocraProvider.CONTENT_ANOMALIE_NATURE_URI, anomalieNature);
            }
        }
    }

    private ArrayList<ContentValues> readBaliseNatures(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_NATURES);
        ArrayList<ContentValues> lstNature = new ArrayList<ContentValues>();
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (TAG_NATURE.equals(name)) {
                lstNature.addAll(readNature(parser));
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, TAG_NATURES);
        return lstNature;
    }

    private List<ContentValues> readNature(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_NATURE);
        List<ContentValues> result = new ArrayList<ContentValues>();
        Integer idNature = null;
        Long valeur = null;
        Long valeurAdmin = null;
        ArrayList<String> saisies = new ArrayList<String>();
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (TAG_CODE_NATURE.equals(name)) {
                idNature = this.readBaliseIdReferentiel(parser, name, RemocraProvider.CONTENT_NATURE_URI);
            } else if (TAG_VALEUR.equals(name)) {
                valeur = this.readBaliseLong(parser, name);
            } else if (TAG_VALEUR_HBE.equals(name)) {
                valeurAdmin = this.readBaliseLong(parser, name);
            } else if (TAG_SAISIES.equals(name)) {
                saisies = readBaliseSaisies(parser);
            } else {
                skip(parser);
            }
        }

        if (saisies.size() == 0) {
            saisies.add("");
        }
        for (String saisie : saisies) {
            ContentValues value = new ContentValues();
            value.put(AnomalieNatureTable.COLUMN_NATURE, idNature);
            value.put(AnomalieNatureTable.COLUMN_VALEUR, valeur);
            value.put(AnomalieNatureTable.COLUMN_VALEUR_HBE, valeurAdmin);
            if (!TextUtils.isEmpty(saisie)) {
                value.put(AnomalieNatureTable.COLUMN_TYPE_SAISIE, saisie);
            }
            result.add(value);
        }
        return result;
    }

    private ArrayList<String> readBaliseSaisies(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_SAISIES);
        ArrayList<String> lstSaisie = new ArrayList<String>();
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (TAG_SAISIE.equals(name)) {
                String saisie = readSaisie(parser);
                if (!TextUtils.isEmpty(saisie)) {
                    lstSaisie.add(saisie);
                }
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, TAG_SAISIES);
        return lstSaisie;
    }

    private String readSaisie(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_SAISIE);
        String saisie = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (TAG_CODE.equals(name)) {
                saisie = this.readBaliseText(parser, name);
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, TAG_SAISIE);
        return saisie;
    }
}
