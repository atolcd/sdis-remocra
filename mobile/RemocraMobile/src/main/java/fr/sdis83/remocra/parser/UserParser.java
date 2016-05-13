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
    private static final String TAG_PERMISSIONS = "permissions";

    public UserParser(RemocraParser remocraParser) {
        super(remocraParser);
    }

    @Override
    public Iterable<? extends String> getNames() {
        return Arrays.asList(TAG_USER, TAG_RIGHT, TAG_PERMISSIONS);
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

        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String name = xmlParser.getName();
            if (TAG_RIGHT.equals(name)) {
                readBaliseRight(xmlParser, values);
            } else {
                skip(xmlParser);
            }
        }
        addContent(RemocraProvider.CONTENT_USER_URI, values);
    }

    private void readBaliseRight(XmlPullParser xmlParser, ContentValues values) throws IOException, XmlPullParserException {
        xmlParser.require(XmlPullParser.START_TAG, ns, TAG_RIGHT);
        String code = xmlParser.getAttributeValue(ns, TAG_CODE);
        String permissions = "";
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            String name = xmlParser.getName();
            if (TAG_PERMISSIONS.equals(name)) {
                String text = this.readBaliseText(xmlParser, name);
                if (!TextUtils.isEmpty(text)) {
                    permissions += text.substring(0, 1);
                }
            } else {
                skip(xmlParser);
            }
        }
        if (code != null) {
            if (code.equals("HYDRANTS")) {
                // Création et Réception, c'est le même droit.
                values.put(UserTable.COLUMN_HYDRANT, permissions);
                values.put(UserTable.COLUMN_RECEPTION, permissions);
            } else if (code.equals("HYDRANTS_CONTROLE")) {
                values.put(UserTable.COLUMN_CONTROLE, permissions);
            } else if (code.equals("HYDRANTS_RECONNAISSANCE")) {
                values.put(UserTable.COLUMN_RECONNAISSANCE, permissions);
            } else if (code.equals("HYDRANTS_MCO")) {
                values.put(UserTable.COLUMN_MCO, permissions);
            } else {
                Log.d("REMOCRA", "Code ignoré : " + code);
            }
        }
        xmlParser.require(XmlPullParser.END_TAG, ns, TAG_RIGHT);
    }
}
