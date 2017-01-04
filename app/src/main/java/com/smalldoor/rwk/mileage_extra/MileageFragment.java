package com.smalldoor.rwk.mileage_extra;


import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.smalldoor.rwk.mileage_extra.MileageAppActivity.DATE_PICKED_RESULT_CODE;
import static com.smalldoor.rwk.mileage_extra.MileageAppActivity.RETURN_DATE;

/**
 * Android version of the JavaScript Mileage screen
 */
public class MileageFragment extends Fragment {

    /* member variables */
    MileageFragment mMileageFragment;
    MileageLab mMileageLab;
    TextView mID;
    TextView mStartMiles;
    TextView mEndMiles;
    TextView mFuelBought;
    TextView mPriceUnit;
    TextView mMileage;
    TextView mDateView;
    TextView mUnitsText;
    LinearLayout rowFour;
    LinearLayout rowFive;
    BarChart mileageChart;
    ContentValues values;

    /**
     * the listeners for the ticketNumber EditText
     */
    private final TextWatcher mileageDataWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {


            View view = getView();
            if (view != null) {
                values = mMileageLab.getFormData(view.getRootView());
                if (values != null) {
                    Double mileage = mMileageLab.calculateMileage(values);
                    DecimalFormat df = new DecimalFormat("#.##");
                    mMileage.setText(df.format(mileage));
                }
            }
        }
    };

    public MileageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_mileage, container, false);

        mMileageFragment = this;
        mMileageLab = MileageLab.getInstance(view.getContext());

        values = new ContentValues();

        rowFour = (LinearLayout) view.findViewById(R.id.row_four);
        rowFive = (LinearLayout) view.findViewById(R.id.row_five);

        mID = (TextView) view.findViewById(R.id._ID);
        mStartMiles = (TextView) view.findViewById(R.id.editStartMiles);
        mEndMiles = (TextView) view.findViewById(R.id.editEndMiles);
        mFuelBought = (TextView) view.findViewById(R.id.editFuelBought);
        mPriceUnit = (TextView) view.findViewById(R.id.editPriceUnit);
        mMileage = (TextView) view.findViewById(R.id.textMileageValue);
        mDateView = (TextView) view.findViewById(R.id.editMileageDate);
        mUnitsText = (TextView) view.findViewById(R.id.textUnits);

        mDateView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    DialogFragment dateFragment = new DatePickerMileageFragment();
                    dateFragment.setTargetFragment(mMileageFragment, DATE_PICKED_RESULT_CODE);
                    dateFragment.show(getFragmentManager(), "datePicker");
                }
                return false;
            }
        });

        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        mDateView.setText(formatter.format(rightNow.getTime()));

        final ToggleButton toggleUnit = (ToggleButton) view.findViewById(R.id.toggleUnit);
        toggleUnit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {

                    mUnitsText.setText(mMileageLab.getUnits().equals("Litres") ? " mpg" : " mpl");
                    mMileageLab.flipUnits();

                    ContentValues values = mMileageLab.getFormData(view.getRootView());
                    if (values != null) {
                        Double mileage = mMileageLab.calculateMileage(values);
                        DecimalFormat df = new DecimalFormat("#.##");
                        mMileage.setText(df.format(mileage));
                    }

                }
                return false;
            }
        });
        view.findViewById(R.id.button_cancel).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                rowFour.setVisibility(View.VISIBLE);
                rowFive.setVisibility(View.GONE);
                mileageChart.highlightValue(0, -1);
                clearInputs();
                return false;
            }
        });
        view.findViewById(R.id.button_clear).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    clearInputs();
                }
                return false;
            }
        });
        view.findViewById(R.id.button_update).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {

                    mMileageLab.updateEntry(view.getRootView());
                    rowFour.setVisibility(View.VISIBLE);
                    rowFive.setVisibility(View.GONE);
                    mileageChart.highlightValue(0, -1);
                    clearInputs();
                }
                return false;
            }
        });
        view.findViewById(R.id.button_delete).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                mMileageLab.deleteEntry(view.getRootView());
                rowFour.setVisibility(View.VISIBLE);
                rowFive.setVisibility(View.GONE);
                mileageChart.highlightValue(0, -1);
                clearInputs();
                return false;
            }
        });
        view.findViewById(R.id.button_commit).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {

                    mMileageLab.CommitEntry(view.getRootView());
                }
                return false;
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        /* add watcher to data fields */
        mStartMiles.addTextChangedListener(mileageDataWatcher);
        mEndMiles.addTextChangedListener(mileageDataWatcher);
        mFuelBought.addTextChangedListener(mileageDataWatcher);
        mPriceUnit.addTextChangedListener(mileageDataWatcher);

        mileageChart = (BarChart) view.findViewById(R.id.mileage_chart);
        mileageChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                LinearLayout rowFour = (LinearLayout) view.findViewById(R.id.row_four);
                LinearLayout rowFive = (LinearLayout) view.findViewById(R.id.row_five);
                rowFour.setVisibility(View.GONE);
                rowFive.setVisibility(View.VISIBLE);

                ContentValues inputData = mMileageLab.getChartEntryFromDb(h);
                if (inputData != null) {
                    fillInputs(inputData);
                    mMileageLab.setCurrentHighlightedIndex((int)h.getX());
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

        mMileageLab.createChart(view);

        view.requestFocus();
        return view;
    }

    /**
     * closes the keyboard
     */
    private void closeSoftKeyboard(@Nullable View view) {

        view = view == null ? getView() : view;
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /** clear all the inputs, keeping the date the same */
    private void clearInputs(){

        mStartMiles.setText("");
        mEndMiles.setText("");
        mFuelBought.setText("");
        mPriceUnit.setText("");
        mMileage.setText("");
    }

    /** fills the EditText fields with the given data
     *
     * @param values the valies to put in to the form. No checking on null values has been done so far
     */
    private void fillInputs(ContentValues values){

        mID.setText(values.getAsString(MileageDbContract.Mileages._ID));
        mDateView.setText(values.getAsString(MileageDbContract.Mileages.COLUMN_NAME_DATE));
        mStartMiles.setText(values.getAsString(MileageDbContract.Mileages.COLUMN_NAME_START_MILES));
        mEndMiles.setText(values.getAsString(MileageDbContract.Mileages.COLUMN_NAME_END_MILES));
        mFuelBought.setText(values.getAsString(MileageDbContract.Mileages.COLUMN_NAME_FUEL_BOUGHT));
        mPriceUnit.setText(values.getAsString(MileageDbContract.Mileages.COLUMN_NAME_PRICE_UNIT));
    }

    /**
     * the callback from the date picker
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case DATE_PICKED_RESULT_CODE:
                String date = data.getStringExtra(RETURN_DATE);
//                mDateSpinnerAdapter.add(date);
//                mDateSpinnerAdapter.sort(new Comparator<String>() {
//                    @Override
//                    public int compare(String s, String t1) {
//                        if (t1.equalsIgnoreCase("Today") || t1.equalsIgnoreCase("Pick") ) {
//                            return 0;
//                        }
//                        return s.compareToIgnoreCase(t1);
//                    }
//                });
//                mDateSpinnerAdapter.notifyDataSetChanged();
//                mDateSpinner.setSelection(mDateSpinnerAdapter.getPosition(date));
                closeSoftKeyboard(null);
                break;
//            case EDIT_UPDATE_RESULT_CODE:
//                data.putExtra(RETURN_DATE, mCurrentDate);
//                mDeliveryDepot.updateDeliveryInDb(data, null);
//                mDeliveryDepot.buildDeliveriesListFromDb(data.getStringExtra(RETURN_DATE), null);
//                closeSoftKeyboard(null);
//                updateUI();
//                break;
//            case EDIT_DELETE_RESULT_CODE:
//                data.putExtra(RETURN_DATE, mCurrentDate);
//                mDeliveryDepot.deleteDeliveryFromDb(data, null);
//                mDeliveryDepot.buildDeliveriesListFromDb(data.getStringExtra(RETURN_DATE), null);
//                closeSoftKeyboard(null);
//                updateUI();
//                break;

            default:
                break;
        }
    }
}
