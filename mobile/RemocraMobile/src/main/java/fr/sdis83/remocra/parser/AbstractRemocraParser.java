package fr.sdis83.remocra.parser;

import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by jpt on 23/09/13.
 */
public abstract class AbstractRemocraParser extends AbstractParser {

    protected static final String TAG_CODE = "code";

    final RemocraParser remocraParser;

    protected AbstractRemocraParser(RemocraParser remocraParser) {
        this.remocraParser = remocraParser;
    }

    public abstract Iterable<? extends String> getNames();

    protected abstract void processNode(XmlPullParser xmlParser, String name) throws IOException, XmlPullParserException;

    protected String readBaliseLibelleReferentiel(XmlPullParser parser, String balise, Uri uri) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, balise);
        String code = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, balise);
        if (!TextUtils.isEmpty(code)) {
            return this.remocraParser.getLibelleFromCodeReferentiel(code, uri);
        }
        return null;
    }

    protected Integer readBaliseIdReferentiel(XmlPullParser parser, String balise, Uri uri) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, balise);
        String code = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, balise);
        if (!TextUtils.isEmpty(code)) {
            return this.remocraParser.getIdFromCodeReferentiel(code, uri);
        }
        return null;
    }

    protected void onAddUri(Uri uri) {
    }

    public void addContent(Uri uri, ContentValues values) {
        this.remocraParser.addContent(this, uri, values);
    }

    protected void onAttach(RemocraParser remocraParser) {

    }
}
