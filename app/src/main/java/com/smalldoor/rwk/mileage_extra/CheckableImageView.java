package com.smalldoor.rwk.mileage_extra;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

/**
 * Created by quant on 24/12/2016.
 */

public class CheckableImageView extends ImageView implements Checkable {

    private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };
    private boolean mChecked;

    public CheckableImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int[] onCreateDrawableState(final int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        return drawableState;
    }

    @Override
    public boolean performClick(){
        toggle();
        return true;
    }
    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(final boolean checked) {
        if (mChecked == checked)
            return;
        mChecked = checked;
        refreshDrawableState();
    }
}
