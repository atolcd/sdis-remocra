package fr.sdis83.remocra.parser;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;

import fr.sdis83.remocra.GlobalRemocra;
import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.UserTable;

/**
 * Created by jpt on 19/09/13.
 */
public class UserParser extends AbstractRemocraParser {

    private static final String TAG_USER = "user";
    private static final String TAG_RIGHT = "right";
    private static final String TAG_LOGGED_AGENT = "loggedAgent";

    public UserParser(RemocraParser remocraParser) {
        super(remocraParser);
    }

    @Override
    public Iterable<? extends String> getNames() {
        return Arrays.asList(TAG_USER, TAG_RIGHT, TAG_LOGGED_AGENT);
    }

    @Override
    protected void processNode(XmlPullParser xmlParser, String name) throws IOException, XmlPullParserException {
        if (TAG_USER.equals(name)) {
            readUser(xmlParser);
        }
    }

    private void readUser(XmlPullParser xmlParser) throws IOException, XmlPullParserException {
        ContentValues values = new ContentValues();
        GlobalRemocra global = GlobalRemocra.getInstance(remocraParser.context);
        values.put(UserTable.COLUMN_USERNAME, global.getLogin());
        values.put(UserTable.COLUMN_PASSWORD, global.getPassword());
        values.put(UserTable.COLUMN_HYDRANT, "");
        values.put(UserTable.COLUMN_CONTROLE, "");
        values.put(UserTable.COLUMN_RECONNAISSANCE, "");
        values.put(UserTable.COLUMN_MCO, "");
        values.put(UserTable.COLUMN_RECEPTION, "");
        values.put(UserTable.COLUMN_VISIT_RECEP, "");
        int eventType = xmlParser.getEventType();
        //On parcourt le xml jusqu'a la fin du document
        while (eventType != xmlParser.END_DOCUMENT) {
            //a chaque ouverture de balise on teste si elle est pas une balise user
            if (eventType == xmlParser.START_TAG && !xmlParser.getName().equals("user")) {
                String name = xmlParser.getName();
                if (TAG_RIGHT.equals(name)&& name != null) {
                    readBaliseRight(xmlParser, values);
                } else if (TAG_LOGGED_AGENT.equals(name) && name != null) {
                    values.put(UserTable.COLUMN_LOGGED_AGENT, this.readBaliseText(xmlParser, name));
                }else {
                    skip(xmlParser);
                }
            }
            //On passe à la deuxieme balise
            eventType = xmlParser.next();

        }
        addContent(RemocraProvider.CONTENT_USER_URI, values);

    }

    private void readBaliseRight(XmlPullParser xmlParser, ContentValues values) throws IOException, XmlPullParserException {
       // xmlParser.require(XmlPullParser.START_TAG, ns, XmlParser.EN);
        String code = xmlParser.getAttributeValue(ns, TAG_CODE);
        if (code != null) {
            if (code.equals("HYDRANTS_C")) {
                values.put(UserTable.COLUMN_HYDRANT, "C");
            } else if (code.equals("HYDRANTS_RECEPTION_C")) {
                values.put(UserTable.COLUMN_RECEPTION, "C");
            } else if (code.equals("HYDRANTS_CONTROLE_C")) {
                values.put(UserTable.COLUMN_CONTROLE, "C");
            } else if (code.equals("HYDRANTS_RECONNAISSANCE_C")) {
                values.put(UserTable.COLUMN_RECONNAISSANCE, "C");
            } else if (code.equals("HYDRANTS_MCO_C")) {
                values.put(UserTable.COLUMN_MCO, "C");
            } else if (code.equals("HYDRANTS_CREATION_C")) {
                values.put(UserTable.COLUMN_VISIT_RECEP, "C");
            } else {
                Log.d("REMOCRA", "Code ignoré : " + code);
            }
        }
      //  xmlParser.require(XmlPullParser.END_TAG,"", TAG_RIGHT);
    }
}
