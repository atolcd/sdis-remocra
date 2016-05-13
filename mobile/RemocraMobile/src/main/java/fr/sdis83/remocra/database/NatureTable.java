package fr.sdis83.remocra.database;

import android.content.Context;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.util.DbUtils;

/**
 * Created by jpt on 06/08/13.
 */
public final class NatureTable implements ReferentielTable {

    public static final String TABLE_NAME = "nature";
    public static final String CITERNE = "CI_";
    public static final String CITERNE_FIXE = "CI_FIXE";

    public static final java.lang.String COLUMN_TYPE_NATURE = "type_nature";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_CODE + " TEXT not null," +
            COLUMN_LIBELLE + " TEXT not null," +
            COLUMN_TYPE_NATURE + " TEXT not null" +
            " )";


    public static String getCodeById(Context context, Integer id) {
        return DbUtils.getCodeFromIdReferentiel(context.getContentResolver(), RemocraProvider.CONTENT_NATURE_URI, id);
    }
}
