package com.smalldoor.rwk.mileage_extra;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

public class MileageAppActivity extends LeftDrawerActivity {

    /* constants */
    public static final int DATE_PICKED_RESULT_CODE = 123;
    public static final String RETURN_DATE = "Return Date";
    public static final int EDIT_UPDATE_RESULT_CODE = 789;
    public static final int EDIT_DELETE_RESULT_CODE = 987;
    public static final String RETURN_NUM = "Return Num";
    public static final String RETURN_PRICE = "Return Price";
    public static final String RETURN_TIP = "Return Tip";
    public static final String RETURN_EXTRA = "Return Extra";
    public static final String RETURN_ID = "Return Id";

    /* member variables */
    private DbHelper mDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mDbHelper = DbHelper.get(this);

        Fragment fragment = createFragment(2);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } else {
            Log.e("Null Value", "MileageAppActivity onCreate fragment is null");
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        mDbHelper.close();
    }
    @Override
    protected Fragment createFragment(int position) {

        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new MileageFragment();
                break;

            case 1:
                fragment = new DeliveriesFragment();
                break;

            case 2:
                android.support.v4.app.Fragment support_fragment = new MapsFragment();
                break;

            default:
                break;

        }
        return fragment;
    }
    @Override
    protected Bundle putArguments(Fragment fragment) {
        return null;
    }
    @Override
    protected void onDrawerItemClick(AdapterView<?> parent, View view, int position, long id){

        switch (position) {
            case 0:case 1:case 2:
                Bundle args;// = new Bundle();
                Fragment fragment = createFragment(position);
                args = putArguments(fragment);
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            case 5:
                mDbHelper.getTableCount(null);
                break;
            case 6:
                if (mDbHelper.clearTables(null)){
                    try {
                        DeliveriesFragment deliveriesFragment = (DeliveriesFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        deliveriesFragment.buildDateSpinner(null);
                    } catch (ClassCastException err) {
                        Log.e("onDrawerItemClick", "clearTables:" + err.toString());
                    }
                    mDbHelper.getTableCount(null);
                } else {
                    Log.e("Tables", "NOT CLEARED");
                }
                break;
            case 7:
                if(mDbHelper.createTables(null)){
                    mDbHelper.getTableCount(null);
                } else {
                    Log.e("Tables", "NOT CREATED");
                }
                break;
            case 8:
                if(mDbHelper.deleteTables(null)){
                    DeliveryDepot.get(this).clearDeliveriesList();
                    DeliveryDepot.get(this).clearDatesList();
                    try {
                        DeliveriesFragment deliveriesFragment = (DeliveriesFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        deliveriesFragment.buildDateSpinner(null);
                    } catch (ClassCastException err) {
                        Log.e("onDrawerItemClick", "deleteTables:" + err.toString());
                    }

                } else {
                    Log.e("Tables", "NOT DELETED");
                }
                break;
            case 9:
                if(deleteDatabase("MileageApp.db")){
                    Log.d("onDrawerItemClick", "Database Deleted");
                } else {
                    Log.e("Database", "NOT DELETED");
                }
                break;
            case 10:
                if (mDbHelper.addTestData(null)){
                    DeliveryDepot.get(this).buildDeliveriesListFromDb("Today", null);
                    DeliveryDepot.get(this).buildDatesListFromDb(null);
                    DeliveryDepot.get(this).updateUI(this);
                    Log.d("onDrawerItemClick", "Test data added");
                } else {
                    Log.e("onDrawerItemClick", "NO DATA ADDED");
                }
                break;
            default:
                //args;// = new Bundle();
                fragment = createFragment(0);
                //args = putArguments(fragment);
                //fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
        }
    }
}



