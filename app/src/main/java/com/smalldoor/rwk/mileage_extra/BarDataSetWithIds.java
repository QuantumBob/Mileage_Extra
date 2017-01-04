package com.smalldoor.rwk.mileage_extra;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quant on 29/12/2016.
 * Extend the data set to include data ids
 */

class BarDataSetWithIds extends BarDataSet  {

    private List<Integer> mIds = null;

    BarDataSetWithIds(List<BarEntry> yVals, String label, List<Integer> ids){
        super(yVals, label);

        this.mIds = ids;

        if (mIds == null)
            mIds = new ArrayList<>();

    }

    public int findID(Integer id){

        return mIds.indexOf(id);
    }

    public List<Integer> getIds() {
        return mIds;
    }

    public void setIds(List<Integer> ids) {
        mIds = ids;
    }

    public int getIdAtIndex(int index) {
        return mIds.get(index);
    }

    public void setIdAtIndex(int index, Integer e){
        mIds.set(index, e);
    }


}
