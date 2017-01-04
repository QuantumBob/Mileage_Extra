package com.smalldoor.rwk.mileage_extra;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * pop out left drawer helper
 * need to override DrawerItemCLickListener, createFragment and putArguments
 */

public abstract class LeftDrawerActivity extends AppCompatActivity{//Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_drawer);

        String[] mDrawerListTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mDrawerListTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    protected abstract Fragment createFragment(int position);
    protected abstract Bundle putArguments(Fragment fragment);
    protected abstract void onDrawerItemClick(AdapterView<?> parent, View view, int position, long id);

    /** The click listener for ListView in the navigation drawer */
    protected class DrawerItemClickListener implements ListView.OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onDrawerItemClick(parent, view, position, id);
            /** update selected item and title, then close the drawer */
            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
}


