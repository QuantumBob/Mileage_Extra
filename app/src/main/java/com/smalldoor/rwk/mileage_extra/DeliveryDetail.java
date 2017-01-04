package com.smalldoor.rwk.mileage_extra;

/**
 * Created by quant on 23/11/2016.
 * Holds the details for a single delivery
 */

public class DeliveryDetail {

    private int mId;
    private String mDate;
    private int mTicketNumber;
    private double mPrice;
    private double mTip;
    private double mExtra;

    DeliveryDetail() {

    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public double getTip() {
        return mTip;
    }

    void setTip(double tip) {
        mTip = tip;
    }

    public double getExtra() {
        return mExtra;
    }

    public void setExtra(double extra) {
        mExtra = extra;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        mPrice = price;
    }

    public int getTicketNumber() {
        return mTicketNumber;
    }

    void setTicketNumber(int ticketNumber) {
        mTicketNumber = ticketNumber;
    }
}
