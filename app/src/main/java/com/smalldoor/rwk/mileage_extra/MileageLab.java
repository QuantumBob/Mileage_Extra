package com.smalldoor.rwk.mileage_extra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.Double.isNaN;

/**
 * Created by quant on 04/12/2016.
 * MileageLab
 */
class MileageLab {

    /* member variables */
    private static MileageLab sMileageLab;
    private DbHelper mDbHelper;
    private BarDataSetWithMileageDetails mDataSet;
    private String mUnits;
    private BarChart mileageChart;
    private int mCurrentHighlightedIndex;

    /** formats and sets the labels for the x axis of the chart
     *
     */
    private class LabelFormatter implements IAxisValueFormatter {

        LabelFormatter(ArrayList<String> labels) {

//            mDataSet.setLabels(labels);
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            return mDataSet.getLabel((int) value);
        }

    }

    /** singleton access to the MileageLab
     *
     * @param context the activity context
     * @return MileageLab either a new one or the previously created and stored static value
     */
    static MileageLab getInstance(Context context) {

        if (sMileageLab == null){
            sMileageLab = new MileageLab(context);
        }
        return sMileageLab;
    }

    /** Constructor
     *
     * @param context the activity context
     */
    private MileageLab(Context context) {

        mDbHelper = DbHelper.get(context);
        mUnits = "Litres";
        mCurrentHighlightedIndex = -1;
        /*look for mileage data in the database */
        if (!mDbHelper.doesTableExist(MileageDbContract.Mileages.TABLE_NAME)) {
            mDbHelper.createTable(MileageDbContract.Mileages.TABLE_NAME, null);
        }
        if (mDbHelper.isTableEmpty(MileageDbContract.Mileages.TABLE_NAME) == 0) {
            setTestDate();
        }
    }

    int getCurrentHighlightedIndex(){
        return mCurrentHighlightedIndex;
    }

    void setCurrentHighlightedIndex(int index){
        mCurrentHighlightedIndex = index;
    }

    /** set some test data */
    private void setTestDate () {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();

            Calendar date = Calendar.getInstance();
            date.set(2016, 9, 22);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            String dateStr;
            Double startMiles;

            for (int i = 0; i < 20; i ++) {
                date.add(Calendar.DAY_OF_MONTH, 1);
                dateStr = formatter.format(date.getTime());
                startMiles = 100d;
                values.put(MileageDbContract.Mileages.COLUMN_NAME_DATE, dateStr);
                values.put(MileageDbContract.Mileages.COLUMN_NAME_START_MILES, startMiles);
                values.put(MileageDbContract.Mileages.COLUMN_NAME_END_MILES, startMiles + 50);
                values.put(MileageDbContract.Mileages.COLUMN_NAME_FUEL_BOUGHT, 40);
                values.put(MileageDbContract.Mileages.COLUMN_NAME_PRICE_UNIT, 2);
                values.put(MileageDbContract.Mileages.COLUMN_NAME_MILEAGE, calculateMileage(values));

                db.insert(MileageDbContract.Mileages.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        }catch (SQLiteException err) {
            Log.e("Mileage setData", err.toString());
        } finally {
            db.endTransaction();
        }
    }

    /** calls the necessary methods to add a new entry into the database
     *  and update the chart
     * @param rootView the view that the form and chart are in
     */
    void CommitEntry(View rootView) {

        ContentValues mileageDetails = getFormData(rootView);

        addEntryToDatabase(mileageDetails);
        getMileageDataFromDb();
        createChart(rootView);
    }

    /** Gets the hidden _ID value from the form
     *
     * @param view the forms view objetc
     * @return int that represents the _ID from the database or -1 if an error occurs
     */
    private int getFormID(View view){

        TextView mID_TextView = (TextView) view.findViewById(R.id._ID);
        Integer mID;
        try {
            mID = Integer.valueOf(mID_TextView.getText().toString(), 10);
            return mID;
        } catch (NumberFormatException err) {
            Log.e("getFormID", err.toString());
            return -1;
        }
    }

    /** gets the input values from the form and pops them into a ContentValues
     * Does not hold the mileage value as this is always calculated
     * @param view the forms view object
     * @return ContentValues that contain the input values or null if there are not enough values to calculate the mileage
     **/
    ContentValues getFormData(View view) {

        ContentValues values = new ContentValues();

        TextView mID = (TextView) view.findViewById(R.id._ID);
        EditText mStartMiles = (EditText) view.findViewById(R.id.editStartMiles);
        EditText mEndMiles = (EditText) view.findViewById(R.id.editEndMiles);
        EditText mFuelBought = (EditText) view.findViewById(R.id.editFuelBought);
        EditText mPriceUnit = (EditText) view.findViewById(R.id.editPriceUnit);
        EditText mDate = (EditText) view.findViewById(R.id.editMileageDate);

        if (mDate.getText().toString().isEmpty() || mStartMiles.getText().toString().isEmpty() || mFuelBought.getText().toString().isEmpty() || mPriceUnit.getText().toString().isEmpty()) {
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String dateStr = formatter.format(Calendar.getInstance().getTime());
        try {
            Date date = formatter.parse(mDate.getText().toString());
            dateStr = formatter.format(date);
        } catch (ParseException err) {
            Log.e("afterTextChange", err.toString());
        }

        Integer id;
        try {
            id = Integer.valueOf(mID.getText().toString(), 10);
        } catch (NumberFormatException err) {
            Log.e("getFormData", err.toString());
            id = -1;
        }

        values.put(MileageDbContract.Mileages._ID, id);
        values.put(MileageDbContract.Mileages.COLUMN_NAME_DATE, dateStr);
        values.put(MileageDbContract.Mileages.COLUMN_NAME_START_MILES, Float.valueOf(mStartMiles.getText().toString()));
        if (mEndMiles.getText().toString().isEmpty()) {
            values.put(MileageDbContract.Mileages.COLUMN_NAME_END_MILES, 0.0f);
        } else {
            values.put(MileageDbContract.Mileages.COLUMN_NAME_END_MILES, Float.valueOf(mEndMiles.getText().toString()));
        }
        values.put(MileageDbContract.Mileages.COLUMN_NAME_FUEL_BOUGHT, Float.valueOf(mFuelBought.getText().toString()));
        values.put(MileageDbContract.Mileages.COLUMN_NAME_PRICE_UNIT, Float.valueOf(mPriceUnit.getText().toString()));
        values.put(MileageDbContract.Mileages.COLUMN_NAME_MILEAGE, calculateMileage(values));

        return values;
    }

    /** calculate the mileage from the given data, which must be in float format */
    Double calculateMileage(ContentValues inputData){

        double mileage;

        float conversion = mUnits.equals("Litres") ? 0.219f : 1.0f;
        Float mStartMiles = inputData.getAsFloat(MileageDbContract.Mileages.COLUMN_NAME_START_MILES);
        Float mEndMiles = inputData.getAsFloat(MileageDbContract.Mileages.COLUMN_NAME_END_MILES);
        Float mFuelBought = inputData.getAsFloat(MileageDbContract.Mileages.COLUMN_NAME_FUEL_BOUGHT);
        Float mPriceUnit = inputData.getAsFloat(MileageDbContract.Mileages.COLUMN_NAME_PRICE_UNIT);

        if (mEndMiles == 0) {
            mileage = (mStartMiles * mPriceUnit) / (mFuelBought * conversion);
        } else {
            mileage = (mEndMiles - mStartMiles) * (mPriceUnit / mFuelBought * conversion);
        }
        if (isNaN(mileage)) {
            return -1.0;
        }
        return mileage;
    }

    /** get the values from the database given the id in the highlighted bar
     *
     * @param highlight the bar that has been clicked
     * @return ContentValues containing the input data
     */
    ContentValues getChartEntryFromDb(Highlight highlight){

        Cursor inputData;

        BarData barData = mileageChart.getBarData();
        BarDataSetWithMileageDetails bds = (BarDataSetWithMileageDetails)barData.getDataSetByIndex(highlight.getDataSetIndex());
        /* highlight.getX() returns the zero based index of the selected bar */
        int x = (int)highlight.getX();
        Integer id = bds.getIdAtIndex(x);
        Log.d("get chart entry-x", String.valueOf(x));

        String TABLE = MileageDbContract.Mileages.TABLE_NAME;                 // The table to query
        String[] RETURN = MileageDbContract.Mileages.USE_COLUMNS_ALL;   // The columns to return
        String WHERE = MileageDbContract.Mileages._ID  + " = ?";  // The rows for the WHERE clause
        String[] selectionArgs = {id.toString()};                                        // The values for the WHERE clause
        String orderBy = MileageDbContract.Mileages.COLUMN_NAME_DATE + " ASC";// The sort order

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            inputData = db.query(TABLE, RETURN, WHERE, selectionArgs, null, null, orderBy);
            db.setTransactionSuccessful();
        } catch (SQLiteException err) {
            Log.e("getCursor", err.toString());
            return null;
        } finally {
            db.endTransaction();
        }

        if (inputData.getCount() != 1) {
            return null;
        }

        ContentValues values = new ContentValues();
        inputData.moveToFirst();

        values.put(MileageDbContract.Mileages._ID, inputData.getInt(inputData.getColumnIndexOrThrow(MileageDbContract.Mileages._ID)));
        values.put(MileageDbContract.Mileages.COLUMN_NAME_DATE, inputData.getString(inputData.getColumnIndexOrThrow(MileageDbContract.Mileages.COLUMN_NAME_DATE)));
        values.put(MileageDbContract.Mileages.COLUMN_NAME_START_MILES, inputData.getString(inputData.getColumnIndexOrThrow(MileageDbContract.Mileages.COLUMN_NAME_START_MILES)));
        values.put(MileageDbContract.Mileages.COLUMN_NAME_END_MILES, inputData.getString(inputData.getColumnIndexOrThrow(MileageDbContract.Mileages.COLUMN_NAME_END_MILES)));
        values.put(MileageDbContract.Mileages.COLUMN_NAME_FUEL_BOUGHT, inputData.getString(inputData.getColumnIndexOrThrow(MileageDbContract.Mileages.COLUMN_NAME_FUEL_BOUGHT)));
        values.put(MileageDbContract.Mileages.COLUMN_NAME_PRICE_UNIT, inputData.getString(inputData.getColumnIndexOrThrow(MileageDbContract.Mileages.COLUMN_NAME_PRICE_UNIT)));
        values.put(MileageDbContract.Mileages.COLUMN_NAME_MILEAGE, inputData.getString(inputData.getColumnIndexOrThrow(MileageDbContract.Mileages.COLUMN_NAME_MILEAGE)));

        inputData.close();
        return values;
    }

    /**
     * updates the current highlighted bar
     * @param view the root view that the chart is in
     */
    void updateEntry(View view){

        ContentValues values = getFormData(view);
        Integer id = values.getAsInteger(MileageDbContract.Mileages._ID);

        if (id == -1 || id == 0)
            return;

        String TABLE = MileageDbContract.Mileages.TABLE_NAME;                 // The table to query
        String WHERE = MileageDbContract.Mileages.DELETE_WHERE;  // The rows for the WHERE clause
        String[] selectionArgs = {id.toString()};                        // The values for the WHERE clause

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.update(TABLE, values, WHERE, selectionArgs);

        BarData data = mileageChart.getBarData();
        BarDataSetWithMileageDetails dataSet = (BarDataSetWithMileageDetails)data.getDataSetByIndex(0);

        BarEntry entry = dataSet.getEntryForIndex(getCurrentHighlightedIndex());

        entry.setY(values.getAsFloat(MileageDbContract.Mileages.COLUMN_NAME_MILEAGE));

        mileageChart.notifyDataSetChanged();
        mileageChart.invalidate();

    }

    /** deletes the current entry shown in the form.
     *  checks if the hidden id field is null (ie this is a new entry) and returns if it is.
     * @param view the view that contains the form
     */
    void deleteEntry(View view){

        Integer id = getFormID(view);

        if (id == -1 || id == 0)
            return;

        String TABLE = MileageDbContract.Mileages.TABLE_NAME;                 // The table to query
        String WHERE = MileageDbContract.Mileages.DELETE_WHERE;  // The rows for the WHERE clause
        String[] selectionArgs = {id.toString()};                        // The values for the WHERE clause

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(TABLE, WHERE, selectionArgs);

        mDataSet.removeEntry(mCurrentHighlightedIndex);
        mileageChart.notifyDataSetChanged();
        mileageChart.invalidate();
    }

    /** adds the entry given in the ContentValues into the database
     *
     * @param values ContentValues given as the input values
     */
    private void addEntryToDatabase(ContentValues values){

        if (values.getAsString(MileageDbContract.Mileages.COLUMN_NAME_DATE).equals("")) return;
        if (values.getAsString(MileageDbContract.Mileages.COLUMN_NAME_START_MILES).equals( "")) return;
        if (values.getAsString(MileageDbContract.Mileages.COLUMN_NAME_FUEL_BOUGHT).equals( "")) return;
        if (values.getAsString(MileageDbContract.Mileages.COLUMN_NAME_PRICE_UNIT).equals( "")) return;

        values.put(MileageDbContract.Mileages.COLUMN_NAME_MILEAGE, calculateMileage(values));
        values.remove(MileageDbContract.Mileages._ID);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            db.insertOrThrow(MileageDbContract.Mileages.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (SQLiteException err) {
            Log.e("addEntryToDatabase", err.toString());
        } finally {
            db.endTransaction();
        }
    }

    /** initialises the chart.
     *  Does not check for correct data
     * @param view the view that the chart is in
     */
    void createChart(View view) {

//        http://stackoverflow.com/questions/38857038/mpandroidchart-adding-labels-to-bar-chart/38860000#38860000
//        XAxis xAxis = mBarChart.getXAxis();
//        xAxis.setGranularity(1f);
//        xAxis.setGranularityEnabled(true);

        mileageChart = (BarChart) view.findViewById(R.id.mileage_chart);

        Description desc = new Description();
        desc.setText("");
        mileageChart.setDescription(desc);

        List<BarEntry> entries = new ArrayList<>();
        List<MileageDetails> mileageDetailsArrayList = getMileageDataFromDb();
        if (mileageDetailsArrayList == null)
            return;

        int i = 0;

        for(MileageDetails m : mileageDetailsArrayList) {
            entries.add(new BarEntry((float)i, m.getMileage()));
            i++;
        }

        mDataSet = new BarDataSetWithMileageDetails(entries, "Mileage Chart", mileageDetailsArrayList);
        mDataSet.setDrawValues(true);
        mDataSet.setValueTextColor(Color.LTGRAY);
        mDataSet.setValueTextSize(12f);

        ArrayList<String> labels = new ArrayList<>();

        for(MileageDetails m : mileageDetailsArrayList) {
            labels.add(m.getLabel());
        }

        XAxis xAxis = mileageChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(90f);
        xAxis.setValueFormatter(new LabelFormatter(labels));

        BarData data = new BarData(mDataSet);
        data.setBarWidth(0.9f); // set custom bar width
        mileageChart.setData(data);

        mileageChart.setVisibleXRangeMaximum(8f);
        mileageChart.setVisibleXRangeMinimum(4f);
        mileageChart.moveViewToX(mileageDetailsArrayList.size() - 8f);
        mileageChart.invalidate(); // refresh
    }

    /** returns the _id, date and mileage fields from the database to use in the chart,
     * and puts them into the mChartData global variable
     * mChartData the ChartDataHelper global class
     */
    private List<MileageDetails> getMileageDataFromDb(){

        String TABLE = MileageDbContract.Mileages.TABLE_NAME;                 // The table to query
        String[] RETURN = MileageDbContract.Mileages.USE_COLUMNS_ALL;   // The columns to return
//        String WHERE = MileageDbContract.Mileages.COLUMN_NAME_DATE  + " = ?";  // The rows for the WHERE clause
//        String[] selectionArgs = {date};                                        // The values for the WHERE clause
//        String GROUPBY = null;                                                          // don't group the rows
//        String HAVING = null;                                                           // don't filter by row groups
        String orderBy = MileageDbContract.Mileages.COLUMN_NAME_DATE + " ASC";// The sort order

        List<MileageDetails> MileageDetailsList = new ArrayList<>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        db.beginTransaction();
        try {
            Cursor mCursor = db.query(TABLE, RETURN, null, null, null, null, orderBy);
            mCursor.moveToFirst();
            while(mCursor.moveToNext()){

                MileageDetailsList.add(new MileageDetails(mCursor));
            }
            Log.d("num bars", String.valueOf(MileageDetailsList.size()));

            mCursor.close();
            db.setTransactionSuccessful();
            return MileageDetailsList;
        } catch (SQLiteException err) {
            Log.e("buildCursor", err.toString());
        } finally {
            db.endTransaction();
        }

        return null;
    }

    /** returns the global units variable
     *
     * @return String mUnits either Litres or Gallons
     */
    public String getUnits() {
        return mUnits;
    }

    /** set the global units variable
     *
     * @param units String, either Litres or Gallons
     */
    public void setUnits(String units) {
        mUnits = units;
    }

    /** changes the units to their opposite
     *
     */
    void flipUnits(){
        if (mUnits.equals("Litres")){
            mUnits = "Gallons";
        } else {
            mUnits = "Litres";
        }
    }
}

