package fr.sdis83.remocra.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by jpt on 30/10/13.
 */
public class XmlErrorParser extends AbstractParser{

    private static final String TAG_ERROR = "error";
    private static final String TAG_MESSAGE = "message";
    private String message;

    public String parse(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        message = null;
        if (xmlParser != null) {
            while (xmlParser.next() != XmlPullParser.END_DOCUMENT) {
                String name = xmlParser.getName();
                this.processNode(xmlParser, name);
            }
        }
        return message;
    }

    @Override
    protected void processNode(XmlPullParser xmlParser, String name) throws IOException, XmlPullParserException {
        if (TAG_ERROR.equals(name)) {
            readError(xmlParser);
        }
    }

    private void readError(XmlPullParser xmlParser) throws IOException, XmlPullParserException {

        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String name = xmlParser.getName();
            if (TAG_MESSAGE.equals(name)) {
                message = readBaliseText(xmlParser, name);
            } else {
                skip(xmlParser);
            }
        }
    }

}
