package fr.sdis83.remocra.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jpt on 30/10/13.
 */
public class ServerInfoParser extends AbstractParser {

    private static final String TAG_TOURNEES_DISPO = "tourneesDispo";
    private static final String TAG_TOURNEE = "tournee";
    private List<Integer> lstIdentifiant;

    public List<Integer> parse(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        lstIdentifiant = new ArrayList<Integer>();
        if (xmlParser != null) {
            while (xmlParser.next() != XmlPullParser.END_DOCUMENT) {
                String name = xmlParser.getName();
                this.processNode(xmlParser, name);
            }
        }
        return lstIdentifiant;
    }

    @Override
    protected void processNode(XmlPullParser xmlParser, String name) throws IOException, XmlPullParserException {
        if (TAG_TOURNEES_DISPO.equals(name)) {
            readTournee(xmlParser);
        }
    }

    private void readTournee(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String name = xmlParser.getName();
            if (TAG_TOURNEE.equals(name)) {
                Integer value = readBaliseInteger(xmlParser, name);
                if (value != null) {
                    lstIdentifiant.add(value);
                }
            } else {
                skip(xmlParser);
            }
        }
    }
}
