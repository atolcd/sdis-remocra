package fr.sdis83.remocra.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import fr.sdis83.remocra.R;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.util.DbUtils;

public class HydrantAdapter extends CursorAdapter {

    private int mColNumero;
    private int mColPhotoMini;
    private int mColDateRecep;
    private int mColDateReco;
    private int mColDateCtrl;
    private int mColStates;

    private static final String[] projection = new String[]{
            HydrantTable._ID,
            HydrantTable.COLUMN_NUMERO,
            HydrantTable.COLUMN_DATE_RECEP,
            HydrantTable.COLUMN_DATE_RECO,
            HydrantTable.COLUMN_DATE_CTRL,
            HydrantTable.COLUMN_DISPO,
            HydrantTable.COLUMN_DISPO_HBE,
            HydrantTable.COLUMN_HBE,
            HydrantTable.COLUMN_PHOTO_MINI,
            "(" + HydrantTable.COLUMN_STATE_H1 + " + "
                    + HydrantTable.COLUMN_STATE_H2 + " + "
                    + HydrantTable.COLUMN_STATE_H3 + " + "
                    + HydrantTable.COLUMN_STATE_H4 + ") " + HydrantTable.COLUMN_STATES,
            HydrantTable.COLUMN_NATURE_DECI

    };

    static class ViewHolder {
        public ImageView imageHydrant;
        public TextView numero;
        public TextView diametre;
        public TextView dateRecep;
        public TextView dateReco;
        public TextView dateCtrl;
        public ImageView lineState;
        public ImageView imageState;
    }

    public HydrantAdapter(Context context, Cursor cursor) {
        super(context, cursor, true);
    }

    public HydrantAdapter(Context context) {
        this(context, null);
    }

    public String[] getProjection() {
        return projection;
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        if (cursor != null) {
            mColNumero = cursor.getColumnIndex(HydrantTable.COLUMN_NUMERO);
            mColPhotoMini = cursor.getColumnIndex(HydrantTable.COLUMN_PHOTO_MINI);
            mColDateRecep = cursor.getColumnIndex(HydrantTable.COLUMN_DATE_RECEP);
            mColDateReco = cursor.getColumnIndex(HydrantTable.COLUMN_DATE_RECO);
            mColDateCtrl = cursor.getColumnIndex(HydrantTable.COLUMN_DATE_CTRL);
            mColStates = cursor.getColumnIndex(HydrantTable.COLUMN_STATES);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.hydrant_list_item, viewGroup, false);
        ViewHolder holder = new ViewHolder();
        holder.imageHydrant = (ImageView) view.findViewById(R.id.imageView);
        holder.numero = (TextView) view.findViewById(R.id.numero);
        holder.diametre = (TextView) view.findViewById(R.id.diametre);
        holder.dateRecep = (TextView) view.findViewById(R.id.dateRecep);
        holder.dateReco = (TextView) view.findViewById(R.id.dateReco);
        holder.dateCtrl = (TextView) view.findViewById(R.id.dateCtrl);
        holder.lineState = (ImageView) view.findViewById(R.id.state);
        holder.imageState = (ImageView) view.findViewById(R.id.stateImage);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.numero.setText(cursor.getString(mColNumero));
        holder.diametre.setText("");
        holder.dateRecep.setText(formatDate(cursor.getLong(mColDateRecep)));
        holder.dateReco.setText(formatDate(cursor.getLong(mColDateReco)));
        holder.dateCtrl.setText(formatDate(cursor.getLong(mColDateCtrl)));

        holder.imageHydrant.setImageBitmap(null);
        byte[] dataPhoto = cursor.getBlob(mColPhotoMini);
        if (dataPhoto != null && dataPhoto.length > 0) {
            holder.imageHydrant.setImageBitmap(BitmapFactory.decodeByteArray(dataPhoto, 0, dataPhoto.length));
        } else {
            holder.imageHydrant.setImageBitmap(null);
        }

        // Etat "tout neuf", non uploadable (tous onglets non saisis => perte potentielle), uploadable (tous onglets saisis)
        long state = cursor.getLong(mColStates);
        if (state == 0) {
            holder.imageState.setImageDrawable(context.getResources().getDrawable(R.drawable.point_off));
        } else if (state == 4) {
            holder.imageState.setImageDrawable(context.getResources().getDrawable(R.drawable.point_on));
        } else {
            holder.imageState.setImageDrawable(context.getResources().getDrawable(R.drawable.point_half));
        }
    }

    private int getDrawableIdFromDispo(String dispo) {
        return "DISPO".equals(dispo) ? R.drawable.hydrant_etat_dispo
                : "NON_CONFORME".equals(dispo) ? R.drawable.hydrant_etat_nc
                : R.drawable.hydrant_etat_indispo;
    }

    private String formatDate(Long date) {
        if (date != null && date > 0) {
            return DbUtils.DATE_FORMAT_FR.format(date);
        }
        return "";
    }

}
