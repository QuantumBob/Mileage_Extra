package com.smalldoor.rwk.mileage_extra;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quant on 31/12/2016.
 * Helper class to hold chart info
 */

public class ChartDataHelper {

    private List<BarEntry> mChartData;
    private ArrayList<String> mLabels;
    private ArrayList<Integer> mIds;

    ChartDataHelper() {
        mChartData = new ArrayList<>();
        mLabels = new ArrayList<>();
        mIds = new ArrayList<>();
    }

    ChartDataHelper(List<BarEntry> chartData, ArrayList<String> labels, ArrayList<Integer> ids){

        mChartData = chartData;
        mLabels = labels;
        mIds = ids;
    }

    public void add (BarEntry entry, String label, Integer id){

        mChartData.add(entry);
        mLabels.add(label);
        mIds.add(id);
    }

    public void clear(){

        mChartData.clear();
        mLabels.clear();
        mIds.clear();
    }

    public void setLabels(ArrayList<String> labels){

        mLabels = labels;
    }

    public String getLabel(int value){
        return mLabels.get(value);
    }

    public List<BarEntry> getChartData(){
        return mChartData;
    }

    public ArrayList<String> getLabels(){
        return mLabels;
    }

    public ArrayList<Integer> getIds(){
        return mIds;
    }

    public int getCount(){

        return mChartData.size();
    }
}
