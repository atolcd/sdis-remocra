package fr.sdis83.remocra.parser;

import android.text.TextUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import fr.sdis83.remocra.util.DbUtils;

/**
 * Created by jpt on 23/09/13.
 */
public abstract class AbstractParser {

    public final static String ns = null;

    protected abstract void processNode(XmlPullParser xmlParser, String name) throws IOException, XmlPullParserException;

    protected String readBaliseText(XmlPullParser parser, String balise) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, balise);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, balise);
        return text;
    }

    protected Boolean readBaliseBoolean(XmlPullParser parser, String balise) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, balise);
        Boolean result = readBoolean(parser);
        parser.require(XmlPullParser.END_TAG, ns, balise);
        return result;
    }

    protected Long readBaliseDate(XmlPullParser parser, String balise) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, balise);
        Long result = readDate(parser);
        parser.require(XmlPullParser.END_TAG, ns, balise);
        return result;
    }

    protected Long readBaliseLong(XmlPullParser parser, String balise) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, balise);
        Long result = readLong(parser);
        parser.require(XmlPullParser.END_TAG, ns, balise);
        return result;
    }

    protected Integer readBaliseInteger(XmlPullParser parser, String balise) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, balise);
        Integer result = readInteger(parser);
        parser.require(XmlPullParser.END_TAG, ns, balise);
        return result;
    }

    protected Double readBaliseDouble(XmlPullParser parser, String balise) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, balise);
        Double result = readDouble(parser);
        parser.require(XmlPullParser.END_TAG, ns, balise);
        return result;
    }

    protected String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    protected Boolean readBoolean(XmlPullParser parser) throws IOException, XmlPullParserException {
        Boolean result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            String text = parser.getText();
            if (!TextUtils.isEmpty(text)) {
                result = Boolean.parseBoolean(text);
            }
            parser.nextTag();
        }
        return result;
    }

    protected Long readDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        Long result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            String text = parser.getText();
            if (!TextUtils.isEmpty(text)) {
                try {
                    Date date = DbUtils.DATE_FORMAT_ISO.parse(text);
                    result = date.getTime();
                } catch (ParseException e) {
                    Log.d("PARSE", "erreur parsing date : " + text);
                }
            }
            parser.nextTag();
        }
        return result;
    }

    private Long readLong(XmlPullParser parser) throws IOException, XmlPullParserException {
        Long result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            String text = parser.getText();
            if (!TextUtils.isEmpty(text)) {
                try {
                    result = Long.valueOf(text);
                } catch (NumberFormatException e) {
                    Log.d("PARSE", "erreur conversion long : " + text);
                }
            }
            parser.nextTag();
        }
        return result;
    }

    private Integer readInteger(XmlPullParser parser) throws IOException, XmlPullParserException {
        Integer result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            String text = parser.getText();
            if (!TextUtils.isEmpty(text)) {
                try {
                    result = Integer.valueOf(text);
                } catch (NumberFormatException e) {
                    Log.d("PARSE", "erreur conversion long : " + text);
                }
            }
            parser.nextTag();
        }
        return result;
    }

    private Double readDouble(XmlPullParser parser) throws IOException, XmlPullParserException {
        Double result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            String text = parser.getText();
            if (!TextUtils.isEmpty(text)) {
                try {
                    result = Double.valueOf(text);
                } catch (NumberFormatException e) {
                    Log.d("PARSE", "erreur conversion double : " + text);
                }
            }
            parser.nextTag();
        }
        return result;
    }

    protected void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        Log.d("REMOCRA", "ignoring tag " + parser.getName() + " in " + this.getClass().getSimpleName());
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
