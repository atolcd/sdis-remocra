package fr.sdis83.remocra.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.BaseColumns;
import android.widget.Adapter;

import java.text.SimpleDateFormat;
import java.util.Locale;

import fr.sdis83.remocra.database.ReferentielTable;

/**
 * Created by jpt on 05/09/13.
 */
public final class DbUtils {

    public static final SimpleDateFormat DATE_FORMAT_ISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_FR = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat DATE_HEURE_FORMAT_FR = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_LIGHT = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final SimpleDateFormat DATE_FORMAT_EDIT = new SimpleDateFormat("dd MMMM yyyy");
    public static final SimpleDateFormat TIME_FORMAT_EDIT = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static <T> T nvl(T a, T b) {
        return (a == null) ? b : a;
    }

    public static int findPosition(Adapter cursor, int id) {
        if (cursor != null) {
            for (int i = 0; i < cursor.getCount(); i++) {
                if (cursor.getItemId(i) == id) {
                    return i;
                }
            }
        }
        return 0;
    }

    public static String getCodeFromIdReferentiel(ContentResolver contentResolver, Uri uri, int id) {
        Cursor curReferentiel = contentResolver.query(uri,
                new String[]{ReferentielTable.COLUMN_CODE},
                BaseColumns._ID + "=?",
                new String[]{String.valueOf(id)},
                null);
        try {
            if (curReferentiel != null && curReferentiel.moveToFirst()) {
                return curReferentiel.getString(curReferentiel.getColumnIndex(ReferentielTable.COLUMN_CODE));
            }
        } finally {
            if (curReferentiel != null) {
                curReferentiel.close();
            }
        }
        return "";
    }

    public static ContentValues getContentFromCodeReferentiel(ContentResolver contentResolver, Uri uri, String code) {
        Cursor c = contentResolver.query(uri,
                null,
                ReferentielTable.COLUMN_CODE + "=?",
                new String[]{code},
                null);
        try {
            if (c.moveToFirst()) {
                ContentValues value = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(c, value);
                return value;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    public static String makePlaceHolders(String[] lstAnomalie) {
        if (lstAnomalie == null || lstAnomalie.length < 1) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" (?");
        for (int index = 1; index < lstAnomalie.length; index++) {
            sb.append(",?");
        }
        sb.append(")");
        return sb.toString();
    }
}
