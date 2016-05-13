package fr.sdis83.remocra.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import fr.sdis83.remocra.GlobalRemocra;
import fr.sdis83.remocra.R;
import fr.sdis83.remocra.adapter.AnomalieAdapter;
import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.AnomalieNatureTable;
import fr.sdis83.remocra.database.AnomalieTable;
import fr.sdis83.remocra.database.HydrantTable;

public class Hydrant4 extends AbstractHydrant {

    private AnomalieAdapter adapter;

    public Hydrant4() {
        super(R.layout.hydrant4, HydrantTable.COLUMN_STATE_H4);
        addBindableData(android.R.id.list, HydrantTable.COLUMN_ANOMALIES, ListView.class);
    }

    @Override
    public CharSequence getTabTitle() {
        return GlobalRemocra.getInstance().getContext().getString(R.string.point_attention);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        final ListView lstAnomalie = (ListView) view.findViewById(android.R.id.list);
        adapter = new AnomalieAdapter(this.getActivity(), getCursor(null));
        lstAnomalie.setAdapter(adapter);
        adapter.setListView(lstAnomalie);
        return view;
    }

    @Override
    protected void onBeforeBind(Cursor cursor) {
        Integer idNature = cursor.getInt(cursor.getColumnIndex(HydrantTable.COLUMN_NATURE));
        if (idNature != null) {
            Cursor newCursor = getCursor(idNature);
            adapter.changeCursor(newCursor);
        }
    }

    @Override
    public void onDestroyView() {
        adapter.changeCursor(null);
        super.onDestroyView();
    }

    private Cursor getCursor(Integer idNature) {
        if (idNature == null) {
            idNature = -1;
        }
        String mSelection = " nullif(" + AnomalieTable.TABLE_NAME + "." + AnomalieTable.COLUMN_CRITERE + ",'') IS NOT NULL " +
                " AND " + AnomalieNatureTable.TABLE_NAME + "." + AnomalieNatureTable.COLUMN_NATURE + "=?" +
                " AND " + AnomalieNatureTable.TABLE_NAME + "." + AnomalieNatureTable.COLUMN_TYPE_SAISIE + "=?";
        String[] mSelectionArgs = new String[]{idNature.toString(), typeSaisie.toString()};

        String[] proj = new String[]{
                AnomalieTable.TABLE_NAME + "." + AnomalieTable._ID,
                AnomalieTable.TABLE_NAME + "." + AnomalieTable.COLUMN_LIBELLE,
                AnomalieTable.TABLE_NAME + "." + AnomalieTable.COLUMN_CODE,
                AnomalieTable.TABLE_NAME + "." + AnomalieTable.COLUMN_CRITERE,
                AnomalieNatureTable.TABLE_NAME + "." + AnomalieNatureTable.COLUMN_NATURE};


        return this.getActivity().getContentResolver().query(RemocraProvider.CONTENT_ANOMALIE_NATURE_URI,
                proj,
                mSelection,
                mSelectionArgs,
                AnomalieTable.COLUMN_CRITERE + ", " + AnomalieTable.COLUMN_LIBELLE);
    }

    @Override
    public ContentValues getDataToSave() {
        ContentValues values = super.getDataToSave();
        values.put(HydrantTable.COLUMN_STATE_H4, true);
        return values;
    }
}
