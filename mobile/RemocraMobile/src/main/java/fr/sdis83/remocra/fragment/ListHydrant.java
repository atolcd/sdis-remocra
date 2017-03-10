package fr.sdis83.remocra.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.text.MessageFormat;

import fr.sdis83.remocra.R;
import fr.sdis83.remocra.RemocraApp;
import fr.sdis83.remocra.adapter.HydrantAdapter;
import fr.sdis83.remocra.contentprovider.RemocraProvider;
import fr.sdis83.remocra.database.HydrantTable;

/**
 * Activité qui affiche la liste des hydrants d'une tournée.
 */
public class ListHydrant extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String PARAM_SORT = "ListHydrant.param_sort";
    public static String PARAM_TOURNEE = "ListHydrant.param_tournee";
    private static final int URL_LOADER = 0;

    public interface ListHydrantListener {
        void openHydrant(Long idHydrant);
    }

    private ListHydrantListener mListener;

    private BroadcastReceiver broadcastReceiver = null;
    private HydrantAdapter adapter;

    private long idTournee = 0;
    private int itemMenuSort = R.id.action_sort_position;

    public static ListHydrant newInstance(Long idTournee) {
        ListHydrant listHydrant = new ListHydrant();
        Bundle bundle = new Bundle();
        if (idTournee != null) {
            bundle.putLong(PARAM_TOURNEE, idTournee);
        }
        bundle.putInt(PARAM_SORT, R.id.action_sort_position);
        listHydrant.setArguments(bundle);
        return listHydrant;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getArguments() != null) {
            idTournee = this.getArguments().getLong(PARAM_TOURNEE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.hydrants, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(itemMenuSort).setChecked(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PARAM_TOURNEE, idTournee);
        outState.putInt(PARAM_SORT, itemMenuSort);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            itemMenuSort = savedInstanceState.getInt(PARAM_SORT);
            idTournee = savedInstanceState.getLong(PARAM_TOURNEE);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_SORT, this.itemMenuSort);
        bundle.putLong(PARAM_TOURNEE, this.idTournee);
        getLoaderManager().initLoader(URL_LOADER, bundle, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = new Bundle();
        bundle.putLong(PARAM_TOURNEE, idTournee);
        switch (item.getItemId()) {
            case R.id.action_sort_name:
            case R.id.action_sort_position:
                item.setChecked(!item.isChecked());
                itemMenuSort = item.getItemId();
                bundle.putInt(PARAM_SORT, item.getItemId());
                startBroadcast();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        getLoaderManager().restartLoader(URL_LOADER, bundle, this);
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Check that the container activity has implemented the callback interface
        try {
            mListener = (ListHydrantListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ListHydrantListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hydrant_list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        startBroadcast();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopBroadcast();
    }

    private void startBroadcast() {
        if (itemMenuSort == R.id.action_sort_position) {
            if (broadcastReceiver == null) {
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        getLoaderManager().restartLoader(URL_LOADER, ListHydrant.this.getArguments(), ListHydrant.this);
                    }
                };
                LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(RemocraApp.LOCATION_CHANGE));
            }
        } else {
            stopBroadcast();
        }
    }

    private void stopBroadcast() {
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new HydrantAdapter(getActivity().getBaseContext());
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mListener.openHydrant(id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID) {
            case URL_LOADER:
                String select = null;
                String[] selectParam = null;

                long paramIdTournee = bundle.getLong(PARAM_TOURNEE);
                int paramSort = bundle.getInt(PARAM_SORT);

                if (paramIdTournee > 0) {
                    select = HydrantTable.COLUMN_TOURNEE + "=" + paramIdTournee;
                }

                String orderBy = null;
                switch (paramSort) {
                    case R.id.action_sort_position:
                        Location currentLocation = ((RemocraApp) getActivity().getApplicationContext()).getCurrentLocation();
                        if (currentLocation != null && currentLocation.getLatitude() != 0 && currentLocation.getLongitude() != 0) {
                            String lon = "(" + HydrantTable.COLUMN_COORD_LON + " - " + String.valueOf(currentLocation.getLongitude()) + ")";
                            String lat = "(" + HydrantTable.COLUMN_COORD_LAT + " - " + String.valueOf(currentLocation.getLatitude()) + ")";
                            orderBy = MessageFormat.format("(({0} * {0}) + ({1} * {1}))", lon.replace(",", "."), lat.replace(",", "."));
                        }
                        break;
                    case R.id.action_sort_name:
                        orderBy = HydrantTable.COLUMN_NUMERO;
                        break;
                    default:
                        break;
                }

                // Returns a new CursorLoader
                return new CursorLoader(
                        getActivity(),
                        RemocraProvider.CONTENT_HYDRANT_URI,
                        adapter.getProjection(),
                        select,
                        selectParam,
                        orderBy
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.changeCursor(null);
    }

}
