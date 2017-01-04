package com.smalldoor.rwk.mileage_extra;

import android.database.Cursor;

/**
 * Created by quant on 02/01/2017.
 * Contains the details for each mileage entry in the database
 */

public class MileageDetails {

    private Integer mID;
    private String mDate;
    private Float mStartMiles;
    private Float mEndMiles;
    private Float mFuelBought;
    private Float mPriceUnit;
    private Float mMileage;


    MileageDetails(Cursor cursor){

        mID = cursor.getInt(cursor.getColumnIndexOrThrow(MileageDbContract.Mileages._ID));
        mDate = cursor.getString(cursor.getColumnIndexOrThrow(MileageDbContract.Mileages.COLUMN_NAME_DATE));
        mStartMiles = cursor.getFloat(cursor.getColumnIndexOrThrow(MileageDbContract.Mileages.COLUMN_NAME_START_MILES));
        mEndMiles = cursor.getFloat(cursor.getColumnIndexOrThrow(MileageDbContract.Mileages.COLUMN_NAME_END_MILES));
        mFuelBought = cursor.getFloat(cursor.getColumnIndexOrThrow(MileageDbContract.Mileages.COLUMN_NAME_FUEL_BOUGHT));
        mPriceUnit = cursor.getFloat(cursor.getColumnIndexOrThrow(MileageDbContract.Mileages.COLUMN_NAME_PRICE_UNIT));
        mMileage = cursor.getFloat(cursor.getColumnIndexOrThrow(MileageDbContract.Mileages.COLUMN_NAME_MILEAGE));

    }

    MileageDetails(Integer id, String date, Float startMiles, Float endMiles, Float fuelBought, Float priceUnit){

        mID = id;
        mDate = date;
        mStartMiles = startMiles;
        mEndMiles = endMiles;
        mFuelBought = fuelBought;
        mPriceUnit = priceUnit;
    }

    MileageDetails(Integer id, String date, Float startMiles, Float endMiles, Float fuelBought, Float priceUnit, Float mileage){

        mID = id;
        mDate = date;
        mStartMiles = startMiles;
        mEndMiles = endMiles;
        mFuelBought = fuelBought;
        mPriceUnit = priceUnit;
        mMileage = mileage;
    }

    public String getLabel() {
        return mDate;
    }

    public Integer getID() {
        return mID;
    }

    public void setID(Integer ID) {
        mID = ID;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public Float getStartMiles() {
        return mStartMiles;
    }

    public void setStartMiles(Float startMiles) {
        mStartMiles = startMiles;
    }

    public Float getEndMiles() {
        return mEndMiles;
    }

    public void setEndMiles(Float endMiles) {
        mEndMiles = endMiles;
    }

    public Float getFuelBought() {
        return mFuelBought;
    }

    public void setFuelBought(Float fuelBought) {
        mFuelBought = fuelBought;
    }

    public Float getPriceUnit() {
        return mPriceUnit;
    }

    public void setPriceUnit(Float priceUnit) {
        mPriceUnit = priceUnit;
    }

    public Float getMileage() {
        return mMileage;
    }

    public void setMileage(Float mileage) {
        mMileage = mileage;
    }
}
