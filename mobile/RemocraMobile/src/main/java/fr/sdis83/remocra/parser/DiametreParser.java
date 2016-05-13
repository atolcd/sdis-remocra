package fr.sdis83.remocra.parser;

import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.DiametreTable;
import fr.sdis83.remocra.database.ReferentielTable;

/**
 * Created by jpt on 19/09/13.
 */
public class DiametreParser extends AbstractRemocraParser {

    private static final String TAG_NATURE = "nature";
    private static final String TAG_CODE = "code";

    public DiametreParser(RemocraParser remocraParser) {
        super(remocraParser);
    }

    @Override
    public Iterable<? extends String> getNames() {
        return Arrays.asList("diametres", "diametre");
    }

    @Override
    protected void processNode(XmlPullParser xmlParser, String name) throws IOException, XmlPullParserException {
        if ("diametre".equals(name)) {
            readDiametre(xmlParser);
        }
    }

    @Override
    protected void onAddUri(Uri uri) {
        if (uri.equals(RemocraProvider.CONTENT_DIAMETRE_URI)) {
            ContentValues values = new ContentValues();
            values.put(ReferentielTable.COLUMN_CODE, RemocraProvider.EMPTY_FIELD);
            values.put(ReferentielTable.COLUMN_LIBELLE, RemocraProvider.EMPTY_FIELD);
            addContent(uri, values);
        }
    }

    private void readDiametre(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        ContentValues values = new ContentValues();
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String name = xmlParser.getName();
            if ("code".equals(name)) {
                values.put(DiametreTable.COLUMN_CODE, this.readBaliseText(xmlParser, name));
            } else if ("libelle".equals(name)) {
                values.put(DiametreTable.COLUMN_LIBELLE, this.readBaliseText(xmlParser, name));
            } else if ("natures".equals(name)) {
                values.put(DiametreTable.COLUMN_NATURES, this.readBaliseNatures(xmlParser, name));
            } else {
                skip(xmlParser);
            }
        }
        this.addContent(RemocraProvider.CONTENT_DIAMETRE_URI, values);
    }

    private String readBaliseNatures(XmlPullParser parser, String balise) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, balise);
        ArrayList<Integer> lstIds = new ArrayList<Integer>();
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (TAG_NATURE.equals(name)) {
                Integer id = readNature(parser);
                if (id != null) {
                    lstIds.add(id);
                }
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, balise);
        return TextUtils.join(",", lstIds);
    }

    private Integer readNature(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_NATURE);
        Integer result = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (TAG_CODE.equals(name)) {
                result = this.readBaliseIdReferentiel(parser, name, RemocraProvider.CONTENT_NATURE_URI);
            } else {
                skip(parser);
            }
        }
        return result;
    }
}
