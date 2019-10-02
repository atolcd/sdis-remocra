package fr.sdis83.remocra.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import fr.sdis83.remocra.GlobalRemocra;
import fr.sdis83.remocra.R;
import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.database.MarqueTable;
import fr.sdis83.remocra.database.NatureTable;
import fr.sdis83.remocra.fragment.components.EditDate;

    public  class Hydrant3 extends AbstractHydrant {

        public Hydrant3() {
            super(R.layout.hydrant3, HydrantTable.COLUMN_STATE_H3);
            addBindableData(R.id.observation, HydrantTable.COLUMN_OBSERVATION, EditText.class);
        }

        @Override
        public CharSequence getTabTitle() {
            return GlobalRemocra.getInstance().getContext().getString(R.string.observation);
        }

        @Override
        public ContentValues getDataToSave() {
            ContentValues values = super.getDataToSave();
            values.put(HydrantTable.COLUMN_STATE_H3, true);
            return values;
        }

    }

