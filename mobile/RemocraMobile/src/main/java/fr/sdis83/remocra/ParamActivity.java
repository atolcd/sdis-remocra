package fr.sdis83.remocra;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by jpt on 27/09/13.
 */
public class ParamActivity extends PreferenceActivity {

    public static final String SERVEUR_URL_DEFAULT_VALUE = "http://remocra.sapeurspompiers-var.fr/remocra";
    private static final String PARAM_NUMBER_PASSWORD = "3217";

    public static final Integer APP_XML_VERSION = 2;

    public static final String SERVEUR_URL = "serveur_url";
    public static final int PARAMETRAGE_INITIAL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new FirstPreferenceFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class FirstPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Preference pref = findPreference(SERVEUR_URL);
            pref.setSummary(getPreferenceScreen().getSharedPreferences().getString(SERVEUR_URL, ""));
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(SERVEUR_URL)) {
                Preference connectionPref = findPreference(key);
                connectionPref.setSummary(sharedPreferences.getString(key, "Renseigner le serveur distant"));
            }
        }


    }

    /**
     * Demande un mot de passe à l'utilisateur avant de lancer l'Intent "ParamActivity".
     *
     * @param activity
     */
    public static void askPasswordBeforeParams(final Activity activity) {
        // Mot de passe numérique
        final EditText input = new EditText(activity);
        input.setHint(R.string.prompt_password);
        input.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setRawInputType(Configuration.KEYBOARD_QWERTY);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        // On demande la saisie d'un mot de passe avant de passer à la suite
        new AlertDialog.Builder(activity)
                .setTitle(R.string.title_activity_param)
                .setMessage(R.string.param_password_msg)
                .setView(input)
                .setPositiveButton(R.string.valid, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        if (PARAM_NUMBER_PASSWORD.equals(value.toString())) {
                            Intent intent = new Intent(activity, ParamActivity.class);
                            // Si forResultRequestCode <0, équivalent à activity.startActivity(intent);
                            activity.startActivityForResult(intent, ParamActivity.PARAMETRAGE_INITIAL);
                        } else {
                            Toast.makeText(activity, R.string.error_incorrect_password, Toast.LENGTH_LONG).show();
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Rien
            }
        }).show();
    }

}
