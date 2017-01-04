package com.smalldoor.rwk.mileage_extra;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by quant on 15/12/2016.
 * Touch listener for delivery items in the RecyclerView
 */

public interface OnItemTouchListener {
    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onTouch(View view, RecyclerView.ViewHolder viewHolder);
}
