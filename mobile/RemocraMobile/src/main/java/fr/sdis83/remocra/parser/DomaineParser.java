package fr.sdis83.remocra.parser;

import android.content.ContentValues;
import android.net.Uri;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.DomaineTable;
import fr.sdis83.remocra.database.ReferentielTable;

/**
 * Created by jpt on 19/09/13.
 */
public class DomaineParser extends AbstractRemocraParser {


    public DomaineParser(RemocraParser remocraParser) {
        super(remocraParser);
    }

    @Override
    public Iterable<? extends String> getNames() {
        return Arrays.asList("domaines", "domaine");
    }

    @Override
    protected void processNode(XmlPullParser xmlParser, String name) throws IOException, XmlPullParserException {
        if ("domaine".equals(name)) {
            readDomaine(xmlParser);
        }
    }

    @Override
    protected void onAddUri(Uri uri) {
        if (uri.equals(RemocraProvider.CONTENT_DOMAINE_URI)) {
            ContentValues values = new ContentValues();
            values.put(ReferentielTable.COLUMN_CODE, RemocraProvider.EMPTY_FIELD);
            values.put(ReferentielTable.COLUMN_LIBELLE, RemocraProvider.EMPTY_FIELD);
            addContent(uri, values);
        }
    }

    private void readDomaine(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        ContentValues values = new ContentValues();
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String name = xmlParser.getName();
            if ("code".equals(name)) {
                values.put(DomaineTable.COLUMN_CODE, this.readBaliseText(xmlParser, "code"));
            } else if ("libelle".equals(name)) {
                values.put(DomaineTable.COLUMN_LIBELLE, this.readBaliseText(xmlParser, "libelle"));
            } else {
                skip(xmlParser);
            }
        }
        this.addContent(RemocraProvider.CONTENT_DOMAINE_URI, values);
    }
}
