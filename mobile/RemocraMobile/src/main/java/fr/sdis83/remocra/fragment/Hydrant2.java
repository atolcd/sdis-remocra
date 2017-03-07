package fr.sdis83.remocra.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.sdis83.remocra.GlobalRemocra;
import fr.sdis83.remocra.R;
import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.AnomalieTable;
import fr.sdis83.remocra.database.DiametreTable;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.database.NatureTable;
import fr.sdis83.remocra.util.DbUtils;
public class Hydrant2 extends AbstractHydrant{

    private String currentDiametre;
    private boolean pibi;
    private enum TypeControle {
        debit,
        pression,
        pressionDyn
    }



    private Map<TypeControle, ContentValues> anomalieApp = new HashMap<TypeControle, ContentValues>();

    public Hydrant2() {
        super(R.layout.hydrant2, HydrantTable.COLUMN_STATE_H2);
        addBindableData(R.id.verif_debit, HydrantTable.COLUMN_DEBIT, EditText.class);
        addBindableData(R.id.verif_pression, HydrantTable.COLUMN_PRESSION, EditText.class);
        addBindableData(R.id.verif_debit_max, HydrantTable.COLUMN_DEBIT_MAX, EditText.class);
        addBindableData(R.id.verif_pression_dyn, HydrantTable.COLUMN_PRESSION_DYN, EditText.class);
        addBindableData(R.id.verif_pression_dyn_deb, HydrantTable.COLUMN_PRESSION_DYN_DEB, EditText.class);
        addBindableData(R.id.citerne_positionnement, HydrantTable.COLUMN_POSITIONNEMENT, Spinner.class);
        addBindableData(R.id.citerne_materiau, HydrantTable.COLUMN_MATERIAU, Spinner.class);
        addBindableData(R.id.citerne_qappoint, HydrantTable.COLUMN_QAPPOINT, EditText.class);
        addBindableData(R.id.verif_pena_capa, HydrantTable.COLUMN_CAPACITE, EditText.class);
    }

    @Override
    public CharSequence getTabTitle() {
        return GlobalRemocra.getInstance().getContext().getString(R.string.verification);
    }

    @Override
    protected void onBeforeBind(Cursor cursor) {
        pibi = isPibi(cursor);
        String codeNature = NatureTable.getCodeById(getActivity(), cursor.getInt(cursor.getColumnIndex(HydrantTable.COLUMN_NATURE)));

        if (pibi) {
            currentDiametre = DiametreTable.getCodeById(getActivity(), cursor.getInt(cursor.getColumnIndex(HydrantTable.COLUMN_DIAMETRE)));
        } else {
            currentDiametre = null;
        }

        getView().findViewById(R.id.verif_pibi).setVisibility(pibi ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.verif_pena).setVisibility(pibi ? View.GONE : View.VISIBLE);
        getView().findViewById(R.id.verif_citerne).setVisibility(codeNature.startsWith(NatureTable.CITERNE) ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.lbl_citerne_positionnement).setVisibility(codeNature.equals(NatureTable.CITERNE_FIXE) ? View.VISIBLE : View.GONE);
        getView().findViewById(R.id.citerne_positionnement).setVisibility(codeNature.equals(NatureTable.CITERNE_FIXE) ? View.VISIBLE : View.GONE);
    }

    @Override
    public ContentValues getDataToSave() {
        ContentValues values = super.getDataToSave();
        values.put(HydrantTable.COLUMN_STATE_H2, true);
        if (pibi) {
            List<Integer> lstIds = new ArrayList<Integer>();
            for (ContentValues anomalie : anomalieApp.values()) {
                if (anomalie != null) {
                    lstIds.add(anomalie.getAsInteger(BaseColumns._ID));
                }
            }
            values.put(HydrantTable.COLUMN_ANOMALIES_APP, TextUtils.join(",", lstIds));
        }
        return values;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        loadSpinner(R.id.citerne_positionnement, RemocraProvider.CONTENT_POSITIONNEMENT_URI);
        loadSpinner(R.id.citerne_materiau, RemocraProvider.CONTENT_MATERIAU_URI);

        /*

        ((Spinner) view.findViewById(R.id.citerne_positionnement)).setAdapter(DbUtils.getReferentielAdapter(getActivity(), RemocraProvider.CONTENT_POSITIONNEMENT_URI));
        ((Spinner) view.findViewById(R.id.citerne_materiau)).setAdapter(DbUtils.getReferentielAdapter(getActivity(), RemocraProvider.CONTENT_MATERIAU_URI));
        */

        EditText verif_debit = (EditText) view.findViewById(R.id.verif_debit);
        TextView verif_debit_msg = (TextView) view.findViewById(R.id.verif_debit_msg);
        verif_debit.addTextChangedListener(new VerifTextWatcher(this, verif_debit_msg, TypeControle.debit));

        EditText verif_debit_max = (EditText) view.findViewById(R.id.verif_debit_max);
        TextView verif_debit_max_msg = (TextView) view.findViewById(R.id.verif_debit_max_msg);
        verif_debit_max.addTextChangedListener(new VerifTextWatcher(this, verif_debit_max_msg, TypeControle.debit));

        EditText verif_pression = (EditText) view.findViewById(R.id.verif_pression);
        TextView verif_pression_msg = (TextView) view.findViewById(R.id.verif_pression_msg);
        verif_pression.addTextChangedListener(new VerifTextWatcher(this, verif_pression_msg, TypeControle.pression));

        EditText verif_pression_dyn = (EditText) view.findViewById(R.id.verif_pression_dyn);
        TextView verif_pression_dyn_msg = (TextView) view.findViewById(R.id.verif_pression_dyn_msg);
        verif_pression_dyn.addTextChangedListener(new VerifTextWatcher(this, verif_pression_dyn_msg, TypeControle.pressionDyn));

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


    private class VerifTextWatcher implements TextWatcher {
        private static final String DEBIT_INSUFF = "DEBIT_INSUFF";
        private static final String DEBIT_INSUFF_NC = "DEBIT_INSUFF_NC";
        private static final String DEBIT_TROP_ELEVE = "DEBIT_TROP_ELEVE";
        private static final String PRESSION_INSUFF = "PRESSION_INSUFF";
        private static final String PRESSION_TROP_ELEVEE = "PRESSION_TROP_ELEVEE";
        private static final String PRESSION_DYN_INSUFF = "PRESSION_DYN_INSUFF";
        private static final String PRESSION_DYN_TROP_ELEVEE = "PRESSION_DYN_TROP_ELEVEE";


        private TextView messageTextView;
        private TypeControle typeControle;
        private Hydrant2 hydrant2;
        private VerifTextWatcher(Hydrant2 hydrant2, TextView messageTextView, TypeControle typeControle) {
            this.messageTextView = messageTextView;
            this.typeControle = typeControle;
            this.hydrant2 = hydrant2;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {

            EditText debit_Max = (EditText) hydrant2.getView().findViewById(R.id.verif_debit_max);
            TextView message_debit_Max = (TextView) hydrant2.getView().findViewById(R.id.verif_debit_max_msg);

            EditText debit = (EditText) hydrant2.getView().findViewById(R.id.verif_debit);
            TextView message_debit = (TextView) hydrant2.getView().findViewById(R.id.verif_debit_msg);

            EditText pression_Dyn = (EditText) hydrant2.getView().findViewById(R.id.verif_pression_dyn);
            TextView message_pression_Dyn = (TextView) hydrant2.getView().findViewById(R.id.verif_pression_dyn_msg);
            TextView message_pression_Dyn_Erreur = (TextView) hydrant2.getView().findViewById(R.id.verif_pression_dyn_msgerreur);

            String error = null;
            ContentValues anomalie = null;

            if (pibi) {

                String val = editable.toString();
                Double valeur;
                if (!TextUtils.isEmpty(val)) {
                    if (messageTextView.getId() == message_debit.getId()){
                        if(!debit_Max.getText().toString().isEmpty()){
                            val = debit_Max.getText().toString();
                        }
                    }
                    valeur = Double.valueOf("0"+val);
                    String codeAnomalie = getCodeAnomalie(valeur);
                    if (!TextUtils.isEmpty(codeAnomalie)) {
                        anomalie = DbUtils.getContentFromCodeReferentiel(getActivity().getContentResolver(), RemocraProvider.CONTENT_ANOMALIE_URI, codeAnomalie);
                        if (anomalie != null) {
                            error = anomalie.getAsString(AnomalieTable.COLUMN_LIBELLE);
                        }
                    }

                }
                messageTextView.setVisibility(error == null ? View.GONE : View.VISIBLE);

                if (pression_Dyn.getText().toString().equals(".")){

                    pression_Dyn.getText().clear();
                }
                if (!pression_Dyn.getText().toString().isEmpty() && Double.valueOf("0"+pression_Dyn.getText().toString()) < 1) {
                    if (!debit.getText().toString().isEmpty()) {
                        pression_Dyn.getText().clear();
                        message_pression_Dyn_Erreur.setVisibility(View.VISIBLE);
                        message_pression_Dyn.setVisibility(View.GONE);

                    }
                }else message_pression_Dyn_Erreur.setVisibility(View.GONE);


                if(!debit_Max.getText().toString().isEmpty() || debit.getText().toString().isEmpty()){
                    message_debit.setVisibility(View.GONE);

                }else if (debit_Max.getText().toString().isEmpty() && !message_debit.getText().toString().isEmpty()){

                    message_debit.setVisibility(View.VISIBLE);
                }

            }
            anomalieApp.put(typeControle, anomalie);
            messageTextView.setText(error);


        }


        private String getCodeAnomalie(Double valeur) {

            switch (typeControle) {


                case debit:
                    if ("DIAM80".equals(currentDiametre)) {
                        return (valeur < 30 ? DEBIT_INSUFF : valeur > 90 ? DEBIT_TROP_ELEVE : null);
                    } else if ("DIAM100".equals(currentDiametre)) {
                        return (valeur < 30 ? DEBIT_INSUFF : valeur < 60 ? DEBIT_INSUFF_NC : valeur > 130 ? DEBIT_TROP_ELEVE : null);
                    } else if ("DIAM150".equals(currentDiametre)) {
                        return (valeur < 60 ? DEBIT_INSUFF : valeur < 120 ? DEBIT_INSUFF_NC : valeur > 150 ? DEBIT_TROP_ELEVE : null);
                    }
                    break;

                case pression:
                    return valeur < 1 ? PRESSION_INSUFF : valeur > 16 ? PRESSION_TROP_ELEVEE : null;
                case pressionDyn:
                    return valeur < 0 ? null : valeur < 1 ? PRESSION_DYN_INSUFF : valeur > 16 ? PRESSION_DYN_TROP_ELEVEE : null;
            }
            return null;
        }
    }
}
