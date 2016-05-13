package fr.sdis83.remocra.parser;

import android.content.ContentValues;
import android.net.Uri;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.PositionnementTable;
import fr.sdis83.remocra.database.ReferentielTable;

/**
 * Created by jpt on 19/09/13.
 */
public class PositionnementParser extends AbstractRemocraParser {

    public PositionnementParser(RemocraParser remocraParser) {
        super(remocraParser);
    }

    @Override
    public Iterable<? extends String> getNames() {
        return Arrays.asList("positionnements", "positionnement");
    }

    @Override
    public void processNode(XmlPullParser xmlParser, String name) throws IOException, XmlPullParserException {
        if ("positionnement".equals(name)) {
            readPositionnement(xmlParser);
        }
    }

    @Override
    protected void onAddUri(Uri uri) {
        if (uri.equals(RemocraProvider.CONTENT_POSITIONNEMENT_URI)) {
            ContentValues values = new ContentValues();
            values.put(ReferentielTable.COLUMN_CODE, RemocraProvider.EMPTY_FIELD);
            values.put(ReferentielTable.COLUMN_LIBELLE, RemocraProvider.EMPTY_FIELD);
            addContent(uri, values);
        }
    }

    public void readPositionnement(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        ContentValues values = new ContentValues();
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String nameTag = xmlParser.getName();
            if ("libelle".equals(nameTag)) {
                values.put(PositionnementTable.COLUMN_LIBELLE, this.readBaliseText(xmlParser, "libelle"));
            } else if ("code".equals(nameTag)) {
                values.put(PositionnementTable.COLUMN_CODE, this.readBaliseText(xmlParser, "code"));
            } else {
                skip(xmlParser);
            }
        }
        this.addContent(RemocraProvider.CONTENT_POSITIONNEMENT_URI, values);
    }
}
