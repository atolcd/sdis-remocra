package fr.sdis83.remocra.parser;

import android.content.ContentValues;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.NatureTable;

/**
 * Created by jpt on 19/09/13.
 */
public class NatureParser extends AbstractRemocraParser {

    public NatureParser(RemocraParser remocraParser) {
        super(remocraParser);
    }

    @Override
    public Iterable<? extends String> getNames() {
        return Arrays.asList("natures", "nature");
    }

    @Override
    public void processNode(XmlPullParser xmlParser, String name) throws IOException, XmlPullParserException {
        if ("nature".equals(name)) {
            readNature(xmlParser);
        }
    }

    public void readNature(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        ContentValues values = new ContentValues();
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String name = xmlParser.getName();
            if ("type".equals(name)) {
                values.put(NatureTable.COLUMN_TYPE_NATURE, this.readBaliseText(xmlParser, "type"));
            } else if ("libelle".equals(name)) {
                values.put(NatureTable.COLUMN_LIBELLE, this.readBaliseText(xmlParser, "libelle"));
            } else if ("code".equals(name)) {
                values.put(NatureTable.COLUMN_CODE, this.readBaliseText(xmlParser, "code"));
            } else {
                skip(xmlParser);
            }
        }
        this.addContent(RemocraProvider.CONTENT_NATURE_URI, values);
    }
}
