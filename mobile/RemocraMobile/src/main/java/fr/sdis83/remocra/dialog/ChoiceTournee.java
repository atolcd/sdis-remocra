package fr.sdis83.remocra.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jpt on 29/10/13.
 */
public class ChoiceTournee extends DialogFragment {

    public interface ChoiceTourneeListener {
        public void onSelectTournee(List<Integer> idTournees);
    }

    int[] lstTourneeId;
    boolean[] lstTourneeChk;
    String[] lstTourneeLib;
    ChoiceTourneeListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Check that the container activity has implemented the callback interface
        try {
            mListener = (ChoiceTourneeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ChoiceTourneeListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray("ChoiceTournee.Boolean", lstTourneeChk);
        outState.putIntArray("ChoiceTournee.Identifiant", lstTourneeId);
        outState.putStringArray("ChoiceTournee.Libelle", lstTourneeLib);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            initDataFromBundle(savedInstanceState);
        } else {
            initDataFromBundle(this.getArguments());
        }
    }

    private void initDataFromBundle(Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey("ChoiceTournee.String")) {
                lstTourneeId = bundle.getIntArray("ChoiceTournee.Identifiant");
                lstTourneeChk = bundle.getBooleanArray("ChoiceTournee.Boolean");
                lstTourneeLib = bundle.getStringArray("ChoiceTournee.Libelle");
            } else {
                SparseBooleanArray data = new SparseBooleanArray();
                HashMap<Integer, Map<String, Boolean>> l = new HashMap<Integer, Map<String, Boolean>>();
                if (bundle.containsKey("idNewTournee")) {
                    HashMap<Integer, String> newTournee = (HashMap<Integer, String>) bundle.getSerializable("idNewTournee");
                    for (Integer key : newTournee.keySet()) {
                        String value = newTournee.get(key);
                        Map<String, Boolean> li = new HashMap<String, Boolean>();
                        li.put(value, false);
                        l.put(key, li);
                    }
                }
                if (bundle.containsKey("idLocalTournee")) {
                    HashMap<Integer, String> newTournee = (HashMap<Integer, String>) bundle.getSerializable("idLocalTournee");
                    for (Integer key : newTournee.keySet()) {
                        String value = newTournee.get(key);
                        Map<String, Boolean> li = new HashMap<String, Boolean>();
                        li.put(value, true);
                        l.put(key, li);
                    /*int[] newTournee = bundle.getIntArray("idLocalTournee");
                    for (int id : newTournee) {
                        data.put(id, true);
                    }*/
                    }
                }

                lstTourneeChk = new boolean[l.size()];
                lstTourneeId = new int[l.size()];
                lstTourneeLib = new String[l.size()];
                int idx = 0;
                for (Integer key : l.keySet()) {
                    Map<String, Boolean> value = l.get(key);
                    for(String nom : value.keySet()){
                        lstTourneeLib[idx] = nom;
                        lstTourneeChk[idx] = value.get(nom);
                    }
                    int id = key;
                    lstTourneeId[idx] = id;
                    idx++;
                }

               /* lstTourneeChk = new boolean[data.size()];
                lstTourneeId = new int[data.size()];
                lstTourneeLib = new String[data.size()];
                for (int idx = 0; idx < data.size(); idx++) {
                    int id = data.keyAt(idx);
                    lstTourneeId[idx] = id;
                    lstTourneeChk[idx] = data.get(id);
                    lstTourneeLib[idx] = "Tournée " + id;
                }*/
            }
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Tournées à embarquer")
                .setPositiveButton("Valider",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                List<Integer> ids = new ArrayList<Integer>();
                                for (int idx = 0; idx < lstTourneeChk.length; ++idx) {
                                    if (lstTourneeChk[idx]) {
                                        ids.add(lstTourneeId[idx]);
                                    }
                                }
                                mListener.onSelectTournee(ids);
                            }
                        }
                )
                .setNegativeButton("Annuler",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }
                )
                .setMultiChoiceItems(lstTourneeLib, lstTourneeChk, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        lstTourneeChk[which] = isChecked;
                    }
                })
                .setMessage(lstTourneeChk.length < 1 ? "Aucune tournée n'est disponible. Veuillez valider pour envoyer les nouveaux points d'eau éventuels ainsi que pour récupérer les données de référence." : null)
                .create();

        return dialog;
    }


}
