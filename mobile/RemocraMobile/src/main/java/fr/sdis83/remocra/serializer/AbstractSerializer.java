package fr.sdis83.remocra.serializer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.database.NatureTable;
import fr.sdis83.remocra.database.ReferentielTable;
import fr.sdis83.remocra.parser.AbstractRemocraParser;
import fr.sdis83.remocra.util.DbUtils;

/**
 * Created by jpt on 03/10/13.
 */
public class AbstractSerializer {

    private Context context;

    public AbstractSerializer(Context context) {
        this.context = context;
    }

    protected Context getContext() {
        return this.context;
    }

    protected void addBalise(XmlSerializer serializer, String tag, Object value) throws IOException {
        if (value != null && !TextUtils.isEmpty(String.valueOf(value))) {
            serializer.startTag(AbstractRemocraParser.ns, tag);
            serializer.text(String.valueOf(value));
            serializer.endTag(AbstractRemocraParser.ns, tag);
        }
    }

    protected void addBaliseDate(XmlSerializer serializer, String tag, Long value) throws IOException {
        if (value != null && value != 0) {
            try {
                String date = DbUtils.DATE_FORMAT_ISO.format(value);
                serializer.startTag(AbstractRemocraParser.ns, tag);
                serializer.text(date);
                serializer.endTag(AbstractRemocraParser.ns, tag);
            } catch (NumberFormatException ex) {
                // on ne fait rien
                Log.d("REMOCRA", "date invalide : " + value);
            }
        }
    }

    protected void addBaliseImage(XmlSerializer serializer, String tag, byte[] blob) throws IOException {
        if (blob != null && blob.length > 0) {
            String imageEncoded = Base64.encodeToString(blob, Base64.DEFAULT);
            serializer.startTag(AbstractRemocraParser.ns, tag);
            serializer.cdsect(imageEncoded);
            serializer.endTag(AbstractRemocraParser.ns, tag);
        }
    }


    protected String getCodeFromIdReferentiel(String id, Uri uri) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        Cursor c = this.context.getContentResolver().query(uri, new String[]{ReferentielTable.COLUMN_CODE}, ReferentielTable._ID + "=?", new String[]{id}, null);
        try {
            if (c.getCount() == 1) {
                c.moveToPosition(0);
                String result = c.getString(c.getColumnIndex(ReferentielTable.COLUMN_CODE));
                if (!RemocraProvider.EMPTY_FIELD.equals(result)) {
                    return result;
                }
            }
            return null;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    protected String getCodeFromLibelle(String id, Uri uri) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        Cursor c = this.context.getContentResolver().query(uri, new String[]{ReferentielTable.COLUMN_CODE}, ReferentielTable.COLUMN_LIBELLE + "=?", new String[]{id}, null);
        try {
            if (c.getCount() == 1) {
                c.moveToPosition(0);
                String result = c.getString(c.getColumnIndex(ReferentielTable.COLUMN_CODE));
                if (!RemocraProvider.EMPTY_FIELD.equals(result)) {
                    return result;
                }
            }
            return null;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    protected String toValidBooleanValue(String value) {
        return value==null?null:"true".equalsIgnoreCase(value)?"true":"false";
    }
}
