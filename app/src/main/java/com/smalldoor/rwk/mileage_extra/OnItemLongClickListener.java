package com.smalldoor.rwk.mileage_extra;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by quant on 16/12/2016.
 * long click on local checkbox
 */

public interface OnItemLongClickListener {

    void onLongClick(View view, RecyclerView.ViewHolder viewHolder);

}

