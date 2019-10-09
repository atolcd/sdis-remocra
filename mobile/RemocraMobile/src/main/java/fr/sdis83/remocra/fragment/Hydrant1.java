package fr.sdis83.remocra.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import fr.sdis83.remocra.GlobalRemocra;
import fr.sdis83.remocra.R;
import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.database.TypeSaisiesTable;
import fr.sdis83.remocra.fragment.components.EditDate;
import fr.sdis83.remocra.fragment.components.EditTime;
import fr.sdis83.remocra.holder.TypeVisiteHolder;
import fr.sdis83.remocra.util.DbUtils;

public class Hydrant1 extends AbstractHydrant {

    private EditDate datePicker;
    private EditTime timePicker;
    private Spinner types;
    private Switch controle;
    private EditText agent1;

    public Hydrant1() {
        super(R.layout.hydrant1, HydrantTable.COLUMN_STATE_H1);
        addBindableData(R.id.agent2, HydrantTable.COLUMN_AGENT2, EditText.class);
        addBindableData(R.id.controle, HydrantTable.COLUMN_CONTROLE, Switch.class);
        addBindableData(R.id.date_visite, HydrantTable.COLUMN_DATE_VISITE, EditDate.class);
        addBindableData(R.id.date_visite, HydrantTable.COLUMN_DATE_VISITE, EditTime.class);
    }

    @Override
    public CharSequence getTabTitle() {
        return GlobalRemocra.getInstance().getContext().getString(R.string.visite);
    }

    @Override
    protected void onBeforeBind(Cursor cursor) {
        getView().findViewById(R.id.verif_pibi).setVisibility(View.GONE);

        try {
            if(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_DATE_VISITE)) != null){
                Long d= Long.valueOf(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_DATE_VISITE)));
                datePicker.setDate(d);
            }else {
                datePicker.setNow();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            if(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_DATE_VISITE)) != null){
                Long t = Long.valueOf(cursor.getString(cursor.getColumnIndex(HydrantTable.COLUMN_DATE_VISITE)));
                timePicker.setTime(t);
            }else {
                timePicker.setNow();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        ContentResolver resolver = getView().getContext().getContentResolver();

        /**** On filtre le curseur selon le type de saisie
         *  RECEP ==> RECEP
         *  CTRL  ==> RECO ,CTRL
         *  RECO ==> RECO
         */
        String selection = TypeSaisiesTable.COLUMN_CODE + "=?";
        String[] arg;
        //Si le type de saisie est reception et qu'on a aussi le droit de controle on propose les deux
        // cas ou le pompier a le droit reconnaissance et controle
        if (("CTRL".equals(typeSaisie.toString())) || ("RECO".equals(typeSaisie.toString()) && GlobalRemocra.getInstance().getCanControl())) {
            selection = TypeSaisiesTable.COLUMN_CODE + "=?" + " OR " + TypeSaisiesTable.COLUMN_CODE + "=?" ;
            arg = new String[]{String.valueOf("CTRL") , String.valueOf("RECO")  };
        } else {
            arg = new String[]{String.valueOf(typeSaisie)};
        }

        Cursor c= resolver.query(RemocraProvider.CONTENT_TYPE_SAISIES_URI, null,
                selection,
                arg,
                null);

        ArrayList<TypeVisiteHolder> mArrayList = new ArrayList<TypeVisiteHolder>();
        while(c.moveToNext()) {
            TypeVisiteHolder v = new TypeVisiteHolder(c.getLong(c.getColumnIndex(TypeSaisiesTable._ID)) ,
                    c.getString(c.getColumnIndex(TypeSaisiesTable.COLUMN_CODE)), c.getString(c.getColumnIndex(TypeSaisiesTable.COLUMN_LIBELLE)));
            mArrayList.add(v); //add the item
        }
        ArrayAdapter<TypeVisiteHolder> a = new ArrayAdapter<TypeVisiteHolder>(getView().getContext(), android.R.layout.simple_spinner_item, mArrayList);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        types = (Spinner) getView().findViewById(R.id.type_visite);
        types.setAdapter(a);
        types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(types.getSelectedItem() != null) {
                    TypeVisiteHolder tv = (TypeVisiteHolder) types.getSelectedItem();
                    controle.setChecked(false);
                    controle.setEnabled(true);
                    if (!("CTRL".equals(tv.getCode()))){
                        getView().findViewById(R.id.verif_pibi).setVisibility(View.GONE);
                        controle.setEnabled(false);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //On sélectionne le type de saisie actuel
        if(a.getCount() > 1){
            for(int i = 0; i< a.getCount(); i++){
                if (typeSaisie.toString().equals(a.getItem(i).getCode())){
                   types.setSelection(i);
                }
            }
        } else {
            types.setSelection(0);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        // Initialisation du fragment manager obligatoire pour l'affichage de la dialog de date.
        datePicker = ((EditDate) view.findViewById(R.id.date_visite));
        datePicker.setFragmentManager(getFragmentManager());

        // Initialisation du fragment manager obligatoire pour l'affichage de la dialog de l'heure.
        timePicker  = ((EditTime) view.findViewById(R.id.heure_visite));
        timePicker.setFragmentManager(getFragmentManager());

        controle = ((Switch) view.findViewById(R.id.controle));
        controle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //La saisie des mesures est activé seulement en momde CTRL
                if(types.getSelectedItem() != null ) {
                    if (isChecked) {
                        if("CTRL".equals(((TypeVisiteHolder) types.getSelectedItem()).getCode())){
                            getView().findViewById(R.id.verif_pibi).setVisibility(View.VISIBLE);
                        }

                    } else {
                        getView().findViewById(R.id.verif_pibi).setVisibility(View.GONE);
                    }
                }

            }
        });

        agent1 = ((EditText) view.findViewById(R.id.agent1));
        String ag = GlobalRemocra.getInstance().getLoggedAgent();
        agent1.setText(ag);

        return view;
    }

    @Override
    public ContentValues getDataToSave() {
        ContentValues values = super.getDataToSave();
        values.put(HydrantTable.COLUMN_STATE_H1, true);
        values.put(HydrantTable.COLUMN_AGENT1, agent1.getText().toString());
        TypeVisiteHolder v = (TypeVisiteHolder) types.getSelectedItem();
        values.put(HydrantTable.COLUMN_TYPE_SAISIE, v.getCode());
        //On concatène la date et l'heure pour avoir la date du visite
        try {
            Date dateVisite =  DbUtils.DATE_FORMAT_EDIT.parse(datePicker.getText().toString());
            dateVisite.setHours(DbUtils.TIME_FORMAT_EDIT.parse(timePicker.getText().toString()).getHours());
            dateVisite.setMinutes(DbUtils.TIME_FORMAT_EDIT.parse(timePicker.getText().toString()).getMinutes());
            values.put(HydrantTable.COLUMN_DATE_VISITE, String.valueOf(dateVisite.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return values;
    }

}
