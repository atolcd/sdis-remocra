package fr.sdis83.remocra.fragment.components;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import fr.sdis83.remocra.util.DbUtils;

import static java.util.Calendar.getInstance;

/**
 * Classe d'affichage / de gestion de la dialog pour un TimePicker
 * Created by yaz on 20/09/2019.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TextView textView;

    /**
     * Utiliser la méthode setTextView pour faire la correspondance entre l'heure et le field
     */
    public TimePickerFragment() {
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, min, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        this.textView.setText(DbUtils.TIME_FORMAT_EDIT.format(c.getTime()));
    }
}
