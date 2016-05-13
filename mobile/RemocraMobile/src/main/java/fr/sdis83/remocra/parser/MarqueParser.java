package fr.sdis83.remocra.parser;

import android.content.ContentValues;
import android.net.Uri;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.MarqueTable;
import fr.sdis83.remocra.database.ModeleTable;
import fr.sdis83.remocra.database.ReferentielTable;

/**
 * Created by jpt on 19/09/13.
 */
public class MarqueParser extends AbstractRemocraParser {

    ArrayList<ContentValues> lstModeles;

    public MarqueParser(RemocraParser remocraParser) {
        super(remocraParser);
    }

    @Override
    public Iterable<? extends String> getNames() {
        return Arrays.asList("marques", "marque", "modeles", "modele");
    }

    @Override
    protected void processNode(XmlPullParser xmlParser, String name) throws IOException, XmlPullParserException {
        if ("marque".equals(name)) {
            readMarque(xmlParser);
        }
    }

    @Override
    protected void onAddUri(Uri uri) {
        if (uri.equals(RemocraProvider.CONTENT_MARQUE_URI)) {
            ContentValues values = new ContentValues();
            values.put(ReferentielTable.COLUMN_CODE, RemocraProvider.EMPTY_FIELD);
            values.put(ReferentielTable.COLUMN_LIBELLE, RemocraProvider.EMPTY_FIELD);
            addContent(uri, values);
        }
        if (uri.equals(RemocraProvider.CONTENT_MODELE_URI)) {
            ContentValues values = new ContentValues();
            values.put(ReferentielTable.COLUMN_CODE, RemocraProvider.EMPTY_FIELD);
            values.put(ReferentielTable.COLUMN_LIBELLE, RemocraProvider.EMPTY_FIELD);
            values.put(ModeleTable.COLUMN_MARQUE, RemocraProvider.EMPTY_FIELD);
            addContent(uri, values);
        }
    }

    private void readMarque(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        ContentValues values = new ContentValues();
        lstModeles = new ArrayList<ContentValues>();
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String name = xmlParser.getName();
            if ("code".equals(name)) {
                values.put(MarqueTable.COLUMN_CODE, this.readBaliseText(xmlParser, "code"));
            } else if ("libelle".equals(name)) {
                values.put(MarqueTable.COLUMN_LIBELLE, this.readBaliseText(xmlParser, "libelle"));
            } else if ("modeles".equals(name)) {
                readModeles(xmlParser);
            } else {
                skip(xmlParser);
            }
        }
        this.addContent(RemocraProvider.CONTENT_MARQUE_URI, values);
        for (ContentValues valueModele : lstModeles) {
            valueModele.put(ModeleTable.COLUMN_MARQUE, values.getAsString(MarqueTable.COLUMN_CODE));
            this.addContent(RemocraProvider.CONTENT_MODELE_URI, valueModele);
        }
    }

    private void readModeles(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String name = xmlParser.getName();
            if ("modele".equals(name)) {
                readModele(xmlParser);
            }
        }
    }

    private void readModele(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        ContentValues values = new ContentValues();
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String name = xmlParser.getName();
            if ("code".equals(name)) {
                values.put(ModeleTable.COLUMN_CODE, this.readBaliseText(xmlParser, "code"));
            } else if ("libelle".equals(name)) {
                values.put(ModeleTable.COLUMN_LIBELLE, this.readBaliseText(xmlParser, "libelle"));
            } else {
                skip(xmlParser);
            }
        }
        this.lstModeles.add(values);
    }
}
