package fr.sdis83.remocra.fragment.components;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fr.sdis83.remocra.util.DbUtils;

import static java.util.Calendar.*;

/**
 * Classe d'affichage / de gestion de la dialog pour une DatePicker
 * Created by vde on 06/02/2017.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private TextView textView;

    /**
     * Utiliser la méthode setTextView pour faire la correspondance entre la date et le field
     */
    public DatePickerFragment() {
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = getInstance();
        int year = c.get(YEAR);
        int month = c.get(MONTH);
        int day = c.get(DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        this.textView.setText(DbUtils.DATE_FORMAT_EDIT.format(c.getTime()));
    }
}
