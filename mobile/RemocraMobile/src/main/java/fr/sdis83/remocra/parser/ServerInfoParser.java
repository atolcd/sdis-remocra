package fr.sdis83.remocra.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jpt on 30/10/13.
 */
public class ServerInfoParser extends AbstractParser {

    private static final String TAG_TOURNEES_DISPO = "tourneesDispo";
    private static final String TAG_TOURNEE = "tournee";

    private HashMap<Integer,String> lstIdentifiant;

    public HashMap<Integer,String> parse(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        lstIdentifiant = new HashMap<Integer,String>();

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
            String attribute = xmlParser.getAttributeValue(0);
            if (TAG_TOURNEE.equals(name)) {
                Integer id = null;
                String nom = null;
                Integer value = readBaliseInteger(xmlParser, name);
                if (value != null) {
                    id =value;
                }
                if (attribute != null) {
                    nom=attribute;
                }
               lstIdentifiant.put(id,nom);
            } else {
                skip(xmlParser);
            }
        }
    }
}
