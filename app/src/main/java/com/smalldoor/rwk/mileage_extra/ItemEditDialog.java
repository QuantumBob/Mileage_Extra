package com.smalldoor.rwk.mileage_extra;


import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.text.DecimalFormat;

import static com.smalldoor.rwk.mileage_extra.MileageAppActivity.EDIT_DELETE_RESULT_CODE;
import static com.smalldoor.rwk.mileage_extra.MileageAppActivity.EDIT_UPDATE_RESULT_CODE;
import static com.smalldoor.rwk.mileage_extra.MileageAppActivity.RETURN_ID;
import static com.smalldoor.rwk.mileage_extra.MileageAppActivity.RETURN_EXTRA;
import static com.smalldoor.rwk.mileage_extra.MileageAppActivity.RETURN_NUM;
import static com.smalldoor.rwk.mileage_extra.MileageAppActivity.RETURN_PRICE;
import static com.smalldoor.rwk.mileage_extra.MileageAppActivity.RETURN_TIP;

public class ItemEditDialog extends DialogFragment {

    /* member variables */
    public Button mCancel;
    Button mUpdate;
    Button mDelete;
    EditText mId;
    EditText mTicketNum;
    EditText mPrice;
    EditText mTip;
    EditText mExtra;

    public ItemEditDialog() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_item_edit_dialog, container, false);
        mUpdate = (Button) view.findViewById(R.id.dialog_update);
        mCancel = (Button) view.findViewById(R.id.dialog_cancel);
        mDelete = (Button) view.findViewById(R.id.dialog_delete);

        int width = (view.getWidth()-10) /4;

        Bundle args = getArguments();

        Double price = args.getDouble("Price");
        Double tip = args.getDouble("Tip");
        String id = args.getString("id");
        String ticketNum = args.getString("ticketNum");
        Double extra = args.getDouble("extra");

        mId = (EditText) view.findViewById(R.id.delivery_list_new_id);
        mTicketNum = (EditText) view.findViewById(R.id.delivery_list_new_num);
        mPrice = (EditText) view.findViewById(R.id.delivery_list_new_price);
        mTip = (EditText) view.findViewById(R.id.delivery_list_new_tip);
        mExtra = (EditText) view.findViewById(R.id.delivery_list_new_extra);

        DecimalFormat df = new DecimalFormat("#.00");
        mId.setText(String.valueOf(id));
        mTicketNum.setText(String.valueOf(ticketNum));
        mPrice.setText(df.format(price));
        mTip.setText(df.format(tip));
        mExtra.setText(df.format(extra));

        mDelete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Intent intent = new Intent();
                intent.putExtra(RETURN_ID, mId.getText().toString());
                intent.putExtra(RETURN_NUM, mTicketNum.getText().toString());
                intent.putExtra(RETURN_PRICE, mPrice.getText().toString());
                intent.putExtra(RETURN_TIP, mTip.getText().toString());
                intent.putExtra(RETURN_EXTRA, mExtra.getText().toString());
                getTargetFragment().onActivityResult(getTargetRequestCode(), EDIT_DELETE_RESULT_CODE, intent);

                ItemEditDialog.this.dismiss();
                return false;
            }
        });

        mUpdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Log.d("Update", "clicked");

                /* return the data to DeliveriesFragment */
                Intent intent = new Intent();
                intent.putExtra(RETURN_ID, mId.getText().toString());
                intent.putExtra(RETURN_NUM, mTicketNum.getText().toString());
                intent.putExtra(RETURN_PRICE, mPrice.getText().toString());
                intent.putExtra(RETURN_TIP, mTip.getText().toString());
                intent.putExtra(RETURN_EXTRA, mExtra.getText().toString());
                getTargetFragment().onActivityResult(getTargetRequestCode(), EDIT_UPDATE_RESULT_CODE, intent);

                ItemEditDialog.this.dismiss();
                return false;
            }
        });

        mCancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ItemEditDialog.this.dismiss();
                return false;
            }
        });

        return view;
    }
}
