package fr.sdis83.remocra.database;

import android.content.Context;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.util.DbUtils;

/**
 * Created by jpt on 06/08/13.
 */
public final class DiametreTable implements ReferentielTable {

    public static final String TABLE_NAME = "diametre";

    public static final String COLUMN_NATURES = "natures";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_CODE + " TEXT not null," +
            COLUMN_LIBELLE + " TEXT not null," +
            COLUMN_NATURES + " TEXT null" +
            " )";

    public static String getCodeById(Context ctx, Integer id) {
        return DbUtils.getCodeFromIdReferentiel(ctx.getContentResolver(), RemocraProvider.CONTENT_DIAMETRE_URI, id);
    }
}
