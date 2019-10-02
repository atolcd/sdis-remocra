package fr.sdis83.remocra.parser;

import android.content.ContentValues;
import android.net.Uri;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.ReferentielTable;
import fr.sdis83.remocra.database.TypeSaisiesTable;

/**
 * Created by yaz on 26/09/19.
 */
public class TypeSaisiesParser extends AbstractRemocraParser {


    public TypeSaisiesParser(RemocraParser remocraParser) {
        super(remocraParser);
    }

    @Override
    public Iterable<? extends String> getNames() {
        return Arrays.asList("typeSaisies", "saisie");
    }

    @Override
    protected void processNode(XmlPullParser xmlParser, String name) throws IOException, XmlPullParserException {
        if ("saisie".equals(name)) {
            readTypeSaisies(xmlParser);
        }
    }

    @Override
    protected void onAddUri(Uri uri) {

        if (uri.equals(RemocraProvider.CONTENT_TYPE_SAISIES_URI)) {
            ContentValues values = new ContentValues();
            values.put(ReferentielTable.COLUMN_CODE, RemocraProvider.EMPTY_FIELD);
            values.put(ReferentielTable.COLUMN_LIBELLE, RemocraProvider.EMPTY_FIELD);
            addContent(uri, values);
        }
    }

    private void readTypeSaisies(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        ContentValues values = new ContentValues();
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String name = xmlParser.getName();
            if ("code".equals(name)) {
                values.put(TypeSaisiesTable.COLUMN_CODE, this.readBaliseText(xmlParser, "code"));
            } else if ("libelle".equals(name)) {
                values.put(TypeSaisiesTable.COLUMN_LIBELLE, this.readBaliseText(xmlParser, "libelle"));
            } else {
                skip(xmlParser);
            }
        }
        this.addContent(RemocraProvider.CONTENT_TYPE_SAISIES_URI, values);
    }
}
