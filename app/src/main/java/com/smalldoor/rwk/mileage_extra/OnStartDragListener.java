package com.smalldoor.rwk.mileage_extra;

import android.support.v7.widget.RecyclerView;

/**
 * Created by quant on 15/12/2016.
 * Drag listener for RecyclerView
 */

public interface OnStartDragListener {
    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);
}
