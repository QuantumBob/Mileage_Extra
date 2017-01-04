package com.smalldoor.rwk.mileage_extra;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;

import static com.smalldoor.rwk.mileage_extra.MileageAppActivity.DATE_PICKED_RESULT_CODE;
import static com.smalldoor.rwk.mileage_extra.MileageAppActivity.EDIT_DELETE_RESULT_CODE;
import static com.smalldoor.rwk.mileage_extra.MileageAppActivity.EDIT_UPDATE_RESULT_CODE;
import static com.smalldoor.rwk.mileage_extra.MileageAppActivity.RETURN_DATE;


/**
 * Fragment to show the deliveries screen
 */
public class DeliveriesFragment extends Fragment implements OnItemLongClickListener {

    /* member variables **/
    private RecyclerView mRecyclerView;
    private Spinner mDateSpinner;
    private EditText mNum;
    private EditText mPrice;
    private EditText mTip;
    private EditText mExtra;
    ImageButton mImageButton;
    private TextView mPriceTotals;
    private TextView mTipsTotal;
    private TextView mWage;
    private TextView mShop;
    private String mCurrentDate;
    private DeliveryDepot mDeliveryDepot;
    private DeliveriesFragment mDeliveriesFragment;
    private ArrayAdapter<String> mDateSpinnerAdapter;

    /**
     * the listeners for the date spinner
     */
    private final AdapterView.OnItemSelectedListener dateSpinnerSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            if (adapterView.getItemAtPosition(i).toString().toLowerCase().equals("pick")) {
                DialogFragment dateFragment = new DatePickerDeliveriesFragment();
                dateFragment.setTargetFragment(mDeliveriesFragment, DATE_PICKED_RESULT_CODE);
                dateFragment.show(getFragmentManager(), "datePicker");
            } else {
                mDeliveryDepot.buildDeliveriesListFromDb(adapterView.getItemAtPosition(i).toString(), null);
            }
            mCurrentDate = adapterView.getItemAtPosition(i).toString();
            updateUI();

            String item = adapterView.getItemAtPosition(i).toString();
            if (!item.equals("Pick") && !item.equals("Today") && !mCurrentDate.equals(item)) {
                Toast.makeText(adapterView.getContext(), "Date selected : " + adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
                mCurrentDate = item;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    /**
     * the listener for the add delivery button
     */
    private final ImageButton.OnTouchListener addButtonTouchListener = new ImageButton.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (MotionEvent.ACTION_UP == motionEvent.getAction()) {

                DeliveryDetail delivery = new DeliveryDetail();

                String ticketNumber = mNum.getText().toString();
                String price = mPrice.getText().toString();
                String tip = mTip.getText().toString();
                String date = getDate();

                if (ticketNumber.isEmpty() || price.isEmpty() || date.isEmpty()) return false;
                if (tip.isEmpty()) {
                    tip = "0";
                }

                /* need to test for existing ticket number or auto add a number */
                if (mDeliveryDepot.ticketNumExistsInList(Integer.valueOf(ticketNumber, 10))) {
                    Toast.makeText(getActivity(), "Ticket number : " + ticketNumber + " already exists.", Toast.LENGTH_SHORT).show();
                    return false;
                }

                delivery.setDate(date);
                delivery.setTicketNumber(Integer.valueOf(ticketNumber, 10));
                delivery.setPrice(Integer.valueOf(price, 10));
                delivery.setTip(Integer.valueOf(tip, 10));


                mDeliveryDepot.addDateToDb(date);
                mDeliveryDepot.incrementTotalPrice(delivery.getPrice());
                mDeliveryDepot.incrementTotalTips(delivery.getTip());
                mDeliveryDepot.incrementTotalExtras(delivery.getExtra());
                mDeliveryDepot.addNewDeliveryToDb(delivery, null);
                mDeliveryDepot.buildDeliveriesListFromDb(date, null);

                clearNewDeliveryEditTexts();

                /* need to close soft keyboard after delivery added */
                closeSoftKeyboard(view);

                updateUI();
            }
            return false;
        }
    };
    /**
     * the listener for the add delivery button
     */
    private final ImageButton.OnTouchListener addButton2TouchListener = new ImageButton.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (MotionEvent.ACTION_UP == motionEvent.getAction()) {

                DeliveryDetail delivery = new DeliveryDetail();

                String ticketNumber = mNum.getText().toString();
                String price = mPrice.getText().toString();
                String tip = mTip.getText().toString();
                String extra = mExtra.getText().toString();
                String date = getDate();

                if (ticketNumber.isEmpty() || price.isEmpty() || date.isEmpty()) return false;
                if (tip.isEmpty()) {
                    tip = "0";
                }

                /* need to test for existing ticket number or auto add a number */
                if (mDeliveryDepot.ticketNumExistsInList(Integer.valueOf(ticketNumber, 10))) {
                    Toast.makeText(getActivity(), "Ticket number : " + ticketNumber + " already exists.", Toast.LENGTH_SHORT).show();
                    return false;
                }

                delivery.setDate(date);
                delivery.setTicketNumber(Integer.valueOf(ticketNumber, 10));
                delivery.setPrice(Double.valueOf(price));
                delivery.setTip(Double.valueOf(tip));
                delivery.setExtra(Double.valueOf(extra));

                mDeliveryDepot.addDateToDb(date);
                mDeliveryDepot.incrementTotalPrice(delivery.getPrice());
                mDeliveryDepot.incrementTotalTips(delivery.getTip());
                mDeliveryDepot.incrementTotalExtras(delivery.getExtra());
                mDeliveryDepot.addNewDeliveryToDb(delivery, null);
                mDeliveryDepot.buildDeliveriesListFromDb(date, null);

                clearNewDeliveryEditTexts();

                /* need to close soft keyboard after delivery added */
                closeSoftKeyboard(view);

                updateUI();
            }
            return false;
        }
    };
    /**
     * listener for RecyclerView to hide soft keyboard
     */
    private final RecyclerView.OnTouchListener recyclerTouchListener = new RecyclerView.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                Log.d("recycler touched", "yeah");
            }
            closeSoftKeyboard(v);
            return false;
        }
    };

    /**
     * Required empty public constructor
     **/
    public DeliveriesFragment() {
    }

    /**
     * create the view for the whole list
     **/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        /* get the DeliveryDepot instance. SHOULD be first call to it! */
        mDeliveryDepot = DeliveryDepot.get(getActivity());
        mDeliveriesFragment = this;

        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_deliveries_2, container, false);
        /* Cache some variables */
        mPriceTotals = (TextView) view.findViewById(R.id.delivery_list_item_price_total);
        mTipsTotal = (TextView) view.findViewById(R.id.delivery_list_item_tip_total);
        mWage = (TextView) view.findViewById(R.id.delivery_list_item_wage_total);
        mShop = (TextView) view.findViewById(R.id.delivery_list_item_shop_total);
        mNum = (EditText) view.findViewById(R.id.delivery_list_new_num);
        mPrice = (EditText) view.findViewById(R.id.delivery_list_new_price);
        mTip = (EditText) view.findViewById(R.id.delivery_list_new_tip);
        mExtra = (EditText) view.findViewById(R.id.delivery_list_new_extra);
        mDateSpinner = (Spinner) view.findViewById(R.id.spinDeliveryDate);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;
            }
        });

        buildAddButton(view);
        buildDateSpinner(view);
        mCurrentDate = mDeliveryDepot.getToday();
        buildDeliveriesRecycler(view);

        mDeliveryDepot.setTotalPrice();
        mDeliveryDepot.setTotalTips();

        updateUI();
        view.requestFocus();
        return view;
    }

    /**
     * edit the item that is touched
     */
    @Override
    public void onLongClick(View view, RecyclerView.ViewHolder viewHolder) {

        Log.d("item long clicked", String.valueOf(viewHolder.getLayoutPosition()));
        DialogFragment itemEditDialog = new ItemEditDialog();

        Bundle args = new Bundle();
        args.putDouble("Price", mDeliveryDepot.getDeliveryByPosition(viewHolder.getLayoutPosition()).getPrice());
        args.putDouble("Tip", mDeliveryDepot.getDeliveryByPosition(viewHolder.getLayoutPosition()).getTip());
        args.putString("id", String.valueOf(mDeliveryDepot.getDeliveryByPosition(viewHolder.getLayoutPosition()).getId()));
        args.putString("ticketNum", String.valueOf(mDeliveryDepot.getDeliveryByPosition(viewHolder.getLayoutPosition()).getTicketNumber()));
        args.putDouble("extra", mDeliveryDepot.getDeliveryByPosition(viewHolder.getLayoutPosition()).getExtra());
        itemEditDialog.setArguments(args);

        itemEditDialog.setTargetFragment(mDeliveriesFragment, EDIT_UPDATE_RESULT_CODE);
        itemEditDialog.show(getFragmentManager(), "ItemEdit");

    }

    /**
     * sets the image button up
     */
    private void buildAddButton(View view) {

        mImageButton = (ImageButton) view.findViewById(R.id.delivery_list_item_add);
        mImageButton.setOnTouchListener(addButton2TouchListener);
    }

    /**
     * sets up the spinner and populates it with the date info from the database
     **/
    public void buildDateSpinner(@Nullable View view) {

        view = view == null ? getView() : view;

        try {
            if (view != null) {
                mDateSpinner = (Spinner) view.findViewById(R.id.spinDeliveryDate);
            } else {
                return;
            }
            /* get the list of dates from the depot and add them to the spinner **/
            mDeliveryDepot.buildDatesListFromDb(null);
            mDateSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.date_spinner_item, mDeliveryDepot.getDates());

            /* Specify the layout to use when the list of choices appears **/
            mDateSpinnerAdapter.setDropDownViewResource(R.layout.date_spinner_item);

            /* Apply the adapter to the spinner **/
            mDateSpinner.setAdapter(mDateSpinnerAdapter);
            /* set the listener for the spinner (see top of class) */
            mDateSpinner.setOnItemSelectedListener(dateSpinnerSelectedListener);

        } catch (NullPointerException err) {
            Log.e("buildDateSpinner", err.toString());
        }
    }

    /**
     * sets up the recycler view for the deliveries list
     **/
    private void buildDeliveriesRecycler(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        /* improves performance if changes in content do not change layout size of RecyclerView */
        mRecyclerView.setHasFixedSize(true);
        /* use a linear layout manager */
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        /* set the touch listener. See top of class */
        mRecyclerView.setOnTouchListener(recyclerTouchListener);

    }

    /**
     * updates the list on every onCreateView call
     **/
    public void updateUI() {

        DeliveriesAdapter mAdapter = new DeliveriesAdapter(getActivity().getApplicationContext(), this);
        mRecyclerView.setAdapter(mAdapter);

        mPriceTotals.setText(getString(R.string.totalPrice, mDeliveryDepot.getTotalPrice()));
        mTipsTotal.setText(getString(R.string.totalTip, mDeliveryDepot.getTotalTips()));

//        double totalWage = 40 + (mDeliveryDepot.getDistanceDeliveries() * 1.5) + mDeliveryDepot.getLocalDeliveries() + mDeliveryDepot.getTotalTips();
//        mWage.setText(getString(totalWage, totalWage));
        double totalWage = 40 + mDeliveryDepot.getTotalExtras();
        mWage.setText(getString(R.string.totalWage, totalWage));

        double totalShop = mDeliveryDepot.getTotalPrice() - totalWage;
        mShop.setText(getString(R.string.totalShop, totalShop));
    }

    /** closes the keyboard */
    private void closeSoftKeyboard(@Nullable View view){

        view = view == null ? getView() : view;
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * clear new delivery EditTexts
     */
    private void clearNewDeliveryEditTexts() {
        mNum.setText("");
        mPrice.setText("");
        mTip.setText("");
//        mLocal.setChecked(false);
        mExtra.setText("");
    }

    /**
     * gets the current visible date from the date spinner
     */
    private String getDate() {

        String date = mDateSpinner.getSelectedItem().toString();

        if (date.isEmpty() || date.equalsIgnoreCase("today") || date.equalsIgnoreCase("pick")) {
            Calendar cDate = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            date = formatter.format(cDate.getTime());
        }
        return date;
    }

    /**
     * the callback from the date picker
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode){
            case DATE_PICKED_RESULT_CODE:
                String date = data.getStringExtra(RETURN_DATE);
                mDateSpinnerAdapter.add(date);
                mDateSpinnerAdapter.sort(new Comparator<String>() {
                    @Override
                    public int compare(String s, String t1) {
                        if (t1.equalsIgnoreCase("Today") || t1.equalsIgnoreCase("Pick") ) {
                            return 0;
                        }
                        return s.compareToIgnoreCase(t1);
                    }
                });
                mDateSpinnerAdapter.notifyDataSetChanged();
                mDateSpinner.setSelection(mDateSpinnerAdapter.getPosition(date));
                closeSoftKeyboard(null);
                break;
            case EDIT_UPDATE_RESULT_CODE:
                data.putExtra(RETURN_DATE, mCurrentDate);
                mDeliveryDepot.updateDeliveryInDb(data, null);
                mDeliveryDepot.buildDeliveriesListFromDb(data.getStringExtra(RETURN_DATE), null);
                closeSoftKeyboard(null);
                updateUI();
                break;
            case EDIT_DELETE_RESULT_CODE:
                data.putExtra(RETURN_DATE, mCurrentDate);
                mDeliveryDepot.deleteDeliveryFromDb(data, null);
                mDeliveryDepot.buildDeliveriesListFromDb(data.getStringExtra(RETURN_DATE), null);
                closeSoftKeyboard(null);
                updateUI();
                break;

            default:
                break;
        }
    }
}
