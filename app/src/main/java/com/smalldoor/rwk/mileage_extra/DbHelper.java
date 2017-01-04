package com.smalldoor.rwk.mileage_extra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.database.DatabaseUtils.queryNumEntries;

/**
 * Created by quant on 17/11/2016.
 * the sqlite helper class
 */
class DbHelper extends SQLiteOpenHelper {

    /* member variables */
    private static DbHelper sDbHelper;
    /* constructor */
    private DbHelper(Context context) {

        super(context, MileageDbContract.DATABASE_NAME, null, MileageDbContract.DATABASE_VERSION);
    }
    /* getter for the singleton **/
    public static DbHelper get(Context context) {

        if (sDbHelper == null) {
            sDbHelper = new DbHelper(context);
        }
        return sDbHelper;
    }
    /* called by system when database created */
    @Override
    public void onCreate(SQLiteDatabase db) {

        createTables(db);
        Log.d("Location", "DbHelper:onCreate");
//        createTables(db);
//        addTestData(db);
    }
    /* called by system when database upgraded */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("Location", "onUpgrade");
        /** if the database needs upgrading we need to backup the data first **/
        deleteTables(db);
        onCreate(db);
    }
    /* called by system when database downgraded */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("Location", "onDowngrade");
        onUpgrade(db, oldVersion, newVersion);
    }
    /* creates all tables necessary for the database */
    boolean createTables(@Nullable SQLiteDatabase db){

        if (db == null) {
            db = getWritableDatabase();
        }
        db.beginTransaction();
        try {
            db.execSQL(MileageDbContract.Mileages.CREATE_TABLE);
            db.execSQL(MileageDbContract.Deliveries.CREATE_TABLE);
            db.execSQL(MileageDbContract.Dates.CREATE_TABLE);
            db.setTransactionSuccessful();
        } catch (SQLiteException err) {
            Log.e("createTables", err.toString());
        } finally {
            db.endTransaction();
        }
        Log.d("createTables", "table created");
        return true;
    }
    /* creates given table */
    boolean createTable(String tableName, @Nullable SQLiteDatabase db){

        if (db == null) {
            db = getWritableDatabase();
        }
        String query = "";
        switch (tableName.toLowerCase()){
            case "mileages" :
                query = MileageDbContract.Mileages.CREATE_TABLE;
                break;
            case "dates" :
                query = MileageDbContract.Deliveries.CREATE_TABLE;
                break;
            case "deliveries" :
                query = MileageDbContract.Dates.CREATE_TABLE;
                break;
        }
        db.beginTransaction();
        try {
            db.execSQL(query);
            Log.d("createTables", "table created");
            db.setTransactionSuccessful();
            return true;
        } catch (SQLiteException err) {
            Log.e("createTables", err.toString());
            return false;
        } finally {
            db.endTransaction();
        }

    }
    /* clears all table data */
    boolean clearTables(@Nullable SQLiteDatabase db) {

        if (db == null) {
            db = getReadableDatabase();
        }
        db.beginTransaction();
        try {
            db.delete(MileageDbContract.Mileages.TABLE_NAME, null, null);
            db.delete(MileageDbContract.Deliveries.TABLE_NAME, null, null);
            db.delete(MileageDbContract.Dates.TABLE_NAME, null, null);
            db.setTransactionSuccessful();
        } catch (SQLiteException err) {
            Log.e("clearTables", err.toString());
        } finally {
            db.endTransaction();
        }
        Log.d("in clearTables", "tables cleared");
        return true;
    }
    /* deletes all tables in database */
    boolean deleteTables(@Nullable SQLiteDatabase db){

        if (db == null) {
            db = getReadableDatabase();
        }

        db.beginTransaction();
        try {
            db.execSQL(MileageDbContract.Mileages.DELETE_TABLE);
            db.execSQL(MileageDbContract.Deliveries.DELETE_TABLE);
            db.execSQL(MileageDbContract.Dates.DELETE_TABLE);
            db.setTransactionSuccessful();
        } catch (SQLiteException err) {
            Log.e("deleteTables", err.toString());
        } finally {
            db.endTransaction();
        }
        Log.d("in deleteTables", "tables deleted");
        return true;
    }
    /* delete given table */
    boolean deleteTable(String tableName, @Nullable SQLiteDatabase db){

        if (db == null) {
            db = getReadableDatabase();
        }
        String query = "";
        switch (tableName.toLowerCase()){
            case "mileages" :
                query = MileageDbContract.Mileages.DELETE_TABLE;
                break;
            case "dates" :
                query = MileageDbContract.Deliveries.DELETE_TABLE;
                break;
            case "deliveries" :
                query = MileageDbContract.Dates.DELETE_TABLE;
                break;
        }
        db.beginTransaction();
        try {
            db.execSQL(query);
            db.setTransactionSuccessful();
            return true;
        } catch (SQLiteException err) {
            Log.e("deleteTable", err.toString());
            return false;
        } finally {
            db.endTransaction();
        }
    }
    /* checks if the table exists in the database */
    boolean doesTableExist(String tableName){

        SQLiteDatabase db = getReadableDatabase();
        String sqlStr = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = '" + tableName + "'";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sqlStr, null);
            int count = cursor.getCount();
            if (count == 1){
                cursor.close();
                return true;
            } else {
                cursor.close();
                return false;
            }
        } catch (SQLiteException err) {
            Log.e("doesTableExist", err.toString());
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }
    /* gets the number of tables in database */
    int getTableCount(@Nullable SQLiteDatabase db){

        if (db == null) {
            db = getReadableDatabase();
        }
        String sqlStr = "SELECT name FROM sqlite_master WHERE type = 'table' AND name != 'android_metadata' AND name != 'sqlite_sequence'";
        Cursor cursor = db.rawQuery(sqlStr, null);
        int count = 0;
        long entries;

        while(cursor.moveToNext()){
            count++;
            entries = queryNumEntries(db, cursor.getString(0));
            Log.d("table name", cursor.getString(0) + ":" + Long.toString(entries));
        }
        cursor.close();
        Log.d("getTableCount", "tables = " + Integer.toString(count));
        return count;
    }

    public int getRowCount(String tableName){

        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.query(tableName, null, null, null, null, null, null);
        int count = mCursor.getCount();
        mCursor.close();
        return count;
    }
    /** returns the number of entries in the given table or 0 if there is an error or the table is empty */
    int isTableEmpty(String tableName){

        long count;
        SQLiteDatabase db = getReadableDatabase();
        try {
            count = queryNumEntries(db, tableName);
            return (int) count;
        } catch (SQLiteException err){
            Log.e("isTableEmpty", err.toString());
            return 0;
        }
    }
    /* string value exists in given table */
    boolean stringExists(String tableName, String field, String value) {

        SQLiteDatabase db = getReadableDatabase();
        String Query = "Select * FROM " + tableName + " WHERE " + field + " = " + value;

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(Query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            } else {
                cursor.close();
                return true;
            }
        } catch(SQLiteException err){
            Log.e("selectAll", err.toString());
            if(cursor != null) {
                cursor.close();
            }
            return false;
        }
    }
    /* checks if the tables exist then adds test data to them */
    boolean addTestData(@Nullable SQLiteDatabase db){

        if (db == null || db.isReadOnly()){
            db = getWritableDatabase();
        }
        if (getTableCount(db) <= 0){
            createTables(db);
        } else {
            deleteTables(db);
            createTables(db);
        }

        if (db != null) {
            ContentValues values = new ContentValues();

            for (int i = 0; i < 20; i ++) {
                values.put(MileageDbContract.Deliveries.COLUMN_NAME_DATE, "2016-10-23");
                values.put(MileageDbContract.Deliveries.COLUMN_NAME_TICKET_NUM, i+1);
                values.put(MileageDbContract.Deliveries.COLUMN_NAME_PRICE, Math.random() * 20);
                values.put(MileageDbContract.Deliveries.COLUMN_NAME_EXTRA, Math.random() * 2);
                values.put(MileageDbContract.Deliveries.COLUMN_NAME_TIPS, Math.random() * 2);

                db.insert(MileageDbContract.Deliveries.TABLE_NAME, null, values);
            }

            Calendar date = Calendar.getInstance();
            date.set(2016, 9, 22);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            String dateStr;

            values.clear();

            for (int i = 0; i < 6; i ++) {
                date.add(Calendar.DAY_OF_MONTH, 1);
                dateStr = formatter.format(date.getTime());
                values.put(MileageDbContract.Dates.COLUMN_NAME_DATE, dateStr);
                db.insert(MileageDbContract.Dates.TABLE_NAME, null, values);
            }
        }
        return true;
    }
}
