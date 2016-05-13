package fr.sdis83.remocra.fragment;

import android.content.ContentValues;
import android.widget.EditText;

import fr.sdis83.remocra.GlobalRemocra;
import fr.sdis83.remocra.R;
import fr.sdis83.remocra.database.HydrantTable;

public class Hydrant5 extends AbstractHydrant {

    public Hydrant5() {
        super(R.layout.hydrant5, HydrantTable.COLUMN_STATE_H5);
        addBindableData(R.id.observation, HydrantTable.COLUMN_OBSERVATION, EditText.class);
    }

    @Override
    public CharSequence getTabTitle() {
        return GlobalRemocra.getInstance().getContext().getString(R.string.observation);
    }

    @Override
    public ContentValues getDataToSave() {
        ContentValues values = super.getDataToSave();
        values.put(HydrantTable.COLUMN_STATE_H5, true);
        return values;
    }

}
