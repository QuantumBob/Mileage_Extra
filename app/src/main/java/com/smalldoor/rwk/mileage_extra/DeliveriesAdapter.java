package com.smalldoor.rwk.mileage_extra;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by quant on 15/12/2016.
 * Separate class for the RecyclerView adapter
 * the adapter that passes the item holder to the view
 */

class DeliveriesAdapter extends RecyclerView.Adapter<DeliveriesAdapter.DeliveryHolder> {

    /* member variables */
    private List<DeliveryDetail> mDeliveries = new ArrayList<>();
    private final OnItemLongClickListener mLongClickListener;

    /* Provide a suitable constructor (depends on the kind of data in the list) */
    DeliveriesAdapter(Context context, OnItemLongClickListener longClickListener) {

        mDeliveries.addAll(DeliveryDepot.get(context).getDeliveries());
        mLongClickListener = longClickListener;
    }

    /* Create new views (invoked by the layout manager) */
    @Override
    public DeliveryHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.delivery_list_item_2, parent, false);

        return new DeliveryHolder(view);
    }

    /* Return the size of your list (invoked by the layout manager) */
    @Override
    public int getItemCount() {

        return mDeliveries.size();
    }

    /* Replace the contents of a view (invoked by the layout manager) */
    @Override
    public void onBindViewHolder(final DeliveryHolder holder, int position) {

        /** get element from your list at this position, replace the contents of the view with that element **/
        DeliveryDetail delivery = mDeliveries.get(position);

        holder.mId.setText(String.valueOf(delivery.getId()));
        holder.mTicketNumView.setText(String.valueOf(delivery.getTicketNumber()));
        holder.mPriceTextView.setText(String.format(Locale.ENGLISH, "%.2f", delivery.getPrice()));
        holder.mTipTextView.setText(String.format(Locale.ENGLISH, "%.2f", delivery.getTip()));
        holder.mExtraTextView.setText(String.format(Locale.ENGLISH, "%.2f", delivery.getExtra()));

        /* Start an edit whenever the edit view is touched */
        holder.mTicketNumView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mLongClickListener.onLongClick(view, holder);
                return true;
            }
        });
    }

    /* the holder for one item in the list **/
    class DeliveryHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        private TextView mId;
        private TextView mTicketNumView;
        private TextView mPriceTextView;
        private TextView mTipTextView;
        private TextView mExtraTextView;

        DeliveryHolder(View itemView) {
            super(itemView);

            mId = (TextView) itemView.findViewById(R.id.delivery_list_item_id_text_view);
            mTicketNumView = (TextView) itemView.findViewById(R.id.delivery_list_item_num_text_view);
            mPriceTextView = (TextView) itemView.findViewById(R.id.delivery_list_item_price_text_view);
            mTipTextView = (TextView) itemView.findViewById(R.id.delivery_list_item_tip_text_view);
            mExtraTextView = (TextView) itemView.findViewById(R.id.delivery_list_item_extra_text_view);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}

