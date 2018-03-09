package fr.sdis83.remocra.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import fr.sdis83.remocra.R;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.database.TourneeTable;
import fr.sdis83.remocra.util.DbUtils;

public class TourneeAdapter extends CursorAdapter {

    private static final String[] projection = new String[]{
            TourneeTable._ID,
            TourneeTable.COLUMN_NAME_DEB_SYNC,
            TourneeTable.COLUMN_NAME_LAST_SYNC,
            TourneeTable.COLUMN_NB_HYDRANT,
            "(select sum(100 / " + TourneeTable.TABLE_NAME + "." + TourneeTable.COLUMN_NB_HYDRANT + ")" +
                    " from " + HydrantTable.TABLE_NAME +
                    " where " + HydrantTable.COLUMN_TOURNEE + " = " + TourneeTable.TABLE_NAME + "." + TourneeTable._ID +
                    " and((" + HydrantTable.COLUMN_STATE_H1 + ") + " +
                    "(" + HydrantTable.COLUMN_STATE_H2 + ") + " +
                    "(" + HydrantTable.COLUMN_STATE_H3 + ") + " +
                    "(" + HydrantTable.COLUMN_STATE_H4 + ") + " +
                    "(" + HydrantTable.COLUMN_STATE_H5 + ") + " +
                    "(" + HydrantTable.COLUMN_STATE_H6 + ")) = 6 )" + TourneeTable.FIELD_TOTAL_HYDRANT
    };

    public TourneeAdapter(Context context, Cursor cursor) {
        super(context, cursor, true);
    }

    public TourneeAdapter(Context context) {
        this(context, null);
    }

    public String[] getProjection() {
        return projection;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.tournee_list_item, viewGroup, false);
        bindView(v, context, cursor);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Resources res = context.getResources();
        TextView textViewId = (TextView) view.findViewById(R.id.textViewId);
        TextView textViewPECount = (TextView) view.findViewById(R.id.textViewPECount);
        TextView textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        TextView textPourcentage = (TextView) view.findViewById(R.id.textPourcentage);

        textViewId.setText(res.getString(R.string.tournee_id, cursor.getInt(cursor.getColumnIndex(TourneeTable._ID))));
        Integer nbHydrant = cursor.getInt(cursor.getColumnIndex(TourneeTable.COLUMN_NB_HYDRANT));
        if (nbHydrant == null || nbHydrant <= 0) {
            textViewPECount.setText(res.getString(R.string.nombreHydrantEmpty));
        } else {
            textViewPECount.setText(res.getQuantityString(R.plurals.nombreHydrant, nbHydrant, nbHydrant));
        }

        Long date = cursor.getLong(cursor.getColumnIndex(TourneeTable.COLUMN_NAME_DEB_SYNC));
        if (date != null && date > 0) {
            textViewDate.setText(DbUtils.DATE_FORMAT_FR.format(date));
        } else {
            textViewDate.setText("");
        }

        Double valeurPrct = (Math.ceil(cursor.getInt(cursor.getColumnIndex(TourneeTable.FIELD_TOTAL_HYDRANT))));
        Integer prct = valeurPrct.intValue();
        textPourcentage.setText(prct.toString() + "%");
        progressBar.setProgress(prct);

    }
}
