package fr.sdis83.remocra.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Button;
import android.widget.Toast;

import fr.sdis83.remocra.R;
import fr.sdis83.remocra.RemocraApp;
import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.NatureTable;
import fr.sdis83.remocra.holder.NatureHolder;

/**
 * Created by jpt on 29/10/13.
 */
public class NewHydrant extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface NewHydrantListener {
        public void onNewHydrant(NatureHolder nature, Location location);
    }

    private NewHydrantListener mListener;
    private CursorAdapter adapter;
    private NatureHolder selectedNature;
    private Button buttonCreate;

    BroadcastReceiver broadcastReceiver;
    Location lastKnownLocation;

    @Override
    public void onStart() {
        super.onStart();

        // On enregistre la dernière position connue (peut-être null)
        this.lastKnownLocation = ((RemocraApp) this.getActivity().getApplicationContext()).getCurrentLocation();
        if (this.lastKnownLocation == null) {
            Toast.makeText(getActivity(), R.string.gps_pos_unknown, Toast.LENGTH_LONG).show();
        }

        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (RemocraApp.LOCATION_CHANGE.equals(intent.getAction())) {
                        boolean hadALocation = (lastKnownLocation != null);
                        // On enregistre la dernière position connue et gère l'activation du bouton Valider
                        NewHydrant.this.lastKnownLocation = ((RemocraApp) NewHydrant.this.getActivity().getApplicationContext()).getCurrentLocation();

                        // On prévient l'utilisateur si la localisation était inconnue et est maintenant connue ou l'inverse
                        if (!hadALocation && NewHydrant.this.lastKnownLocation != null) {
                            Toast.makeText(getActivity(),
                                    R.string.gps_pos_became_known,
                                    Toast.LENGTH_SHORT).show();
                        } else if (hadALocation && NewHydrant.this.lastKnownLocation == null) {
                            Toast.makeText(getActivity(),
                                    R.string.gps_pos_lost,
                                    Toast.LENGTH_SHORT).show();
                        }
                        manageValidButton();
                    }
                }
            };
        }

        // On écoute le changement de position
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(RemocraApp.LOCATION_CHANGE));
    }

    @Override
    public void onStop() {
        super.onStop();
        // On Arrête l'écoute du changement de position
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        }
        this.lastKnownLocation = null;
    }

    /**
     * Le bouton qui permet de passer à la suite est activé si une nature a été sélectionnée ET si la position est connue
     */
    protected void manageValidButton() {
        buttonCreate.setEnabled(lastKnownLocation != null && selectedNature != null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.selectedNature = null;
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Check that the container activity has implemented the callback interface
        try {
            mListener = (NewHydrantListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NewHydrantListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        adapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_single_choice,
                null,
                new String[]{NatureTable.COLUMN_LIBELLE},
                new int[]{android.R.id.text1},
                0
        );
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                // .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(R.string.hydrant_nature)
                .setPositiveButton(R.string.create,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (selectedNature != null && lastKnownLocation != null) {
                                    mListener.onNewHydrant(selectedNature, lastKnownLocation);
                                }
                            }
                        }
                )
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Rien
                            }
                        }
                )
                .setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cursor nature = (Cursor) adapter.getItem(which);
                        if (nature != null && buttonCreate != null) {
                            selectedNature = new NatureHolder(nature.getLong(nature.getColumnIndex(NatureTable._ID)),
                                    nature.getString(nature.getColumnIndex(NatureTable.COLUMN_CODE)),
                                    nature.getString(nature.getColumnIndex(NatureTable.COLUMN_LIBELLE)), nature.getString(nature.getColumnIndex(NatureTable.COLUMN_TYPE_NATURE)));
                            manageValidButton();
                        }
                    }
                })
                .create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                buttonCreate = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                manageValidButton();
            }
        });
        return dialog;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), RemocraProvider.CONTENT_NATURE_URI,
                new String[]{NatureTable._ID,
                        NatureTable.COLUMN_LIBELLE,
                        NatureTable.COLUMN_CODE,
                        NatureTable.COLUMN_TYPE_NATURE}, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

}
