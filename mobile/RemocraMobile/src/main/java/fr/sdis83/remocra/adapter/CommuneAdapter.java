package fr.sdis83.remocra.adapter;

import android.content.Context;
import android.database.Cursor;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;

import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.CommuneTable;

/**
 * Created by jpt on 16/09/13.
 */
public class CommuneAdapter extends SimpleCursorAdapter {

    public CommuneAdapter(Context context) {
        super(context, android.R.layout.simple_dropdown_item_1line, null, new String[]{CommuneTable.COLUMN_LIBELLE}, new int[]{android.R.id.text1}, 0);

        final Context mContext = context;

        // This will provide the labels for the choices to be displayed in the AutoCompleteTextView
        this.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                final int colIndex = cursor.getColumnIndexOrThrow(CommuneTable.COLUMN_LIBELLE);
                return cursor.getString(colIndex);
            }
        });

        this.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence description) {
                if (CommuneAdapter.this.getCursor() != null && !CommuneAdapter.this.getCursor().isClosed()) {
                    CommuneAdapter.this.getCursor().close();
                }
                String[] desc = null;
                if (description != null) {
                    desc = new String[]{description.toString() + '%'};
                }
                return mContext.getContentResolver().query(RemocraProvider.CONTENT_COMMUNE_URI,
                        new String[]{CommuneTable._ID, CommuneTable.COLUMN_LIBELLE},
                        CommuneTable.COLUMN_LIBELLE + " LIKE ?",
                        desc,
                        CommuneTable.COLUMN_LIBELLE);
            }
        });

    }


}
