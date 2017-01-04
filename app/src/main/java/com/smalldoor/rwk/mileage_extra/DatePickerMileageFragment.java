package com.smalldoor.rwk.mileage_extra;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import static com.smalldoor.rwk.mileage_extra.MileageAppActivity.DATE_PICKED_RESULT_CODE;
import static com.smalldoor.rwk.mileage_extra.MileageAppActivity.RETURN_DATE;

/**
 * pops up the date picker
 */
public class DatePickerMileageFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        TextView dateView = (TextView) getActivity().findViewById(R.id.editMileageDate);
        dateView.setText(Integer.toString(day) +  "-" + Integer.toString(month + 1) +  "-" + Integer.toString(year));

        /* return the date to MileagesFragment */
        Intent intent = new Intent();
        intent.putExtra(RETURN_DATE, "");
        getTargetFragment().onActivityResult(getTargetRequestCode(), DATE_PICKED_RESULT_CODE, intent);

    }
}
