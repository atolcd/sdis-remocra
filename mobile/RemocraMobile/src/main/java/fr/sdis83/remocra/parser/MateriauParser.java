package fr.sdis83.remocra.parser;

import android.content.ContentValues;
import android.net.Uri;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.MateriauTable;
import fr.sdis83.remocra.database.ReferentielTable;

/**
 * Created by jpt on 19/09/13.
 */
public class MateriauParser extends AbstractRemocraParser {


    public MateriauParser(RemocraParser remocraParser) {
        super(remocraParser);
    }

    @Override
    public Iterable<? extends String> getNames() {
        return Arrays.asList("materiaux", "materiau");
    }

    @Override
    protected void processNode(XmlPullParser xmlParser, String name) throws IOException, XmlPullParserException {
        if ("materiau".equals(name)) {
            readMateriau(xmlParser);
        }
    }

    @Override
    protected void onAddUri(Uri uri) {
        if (uri.equals(RemocraProvider.CONTENT_MATERIAU_URI)) {
            ContentValues values = new ContentValues();
            values.put(ReferentielTable.COLUMN_CODE, RemocraProvider.EMPTY_FIELD);
            values.put(ReferentielTable.COLUMN_LIBELLE, RemocraProvider.EMPTY_FIELD);
            addContent(uri, values);
        }
    }

    private void readMateriau(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        ContentValues values = new ContentValues();
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String name = xmlParser.getName();
            if ("code".equals(name)) {
                values.put(MateriauTable.COLUMN_CODE, this.readBaliseText(xmlParser, "code"));
            } else if ("libelle".equals(name)) {
                values.put(MateriauTable.COLUMN_LIBELLE, this.readBaliseText(xmlParser, "libelle"));
            } else {
                skip(xmlParser);
            }
        }
        this.addContent(RemocraProvider.CONTENT_MATERIAU_URI, values);
    }

}
