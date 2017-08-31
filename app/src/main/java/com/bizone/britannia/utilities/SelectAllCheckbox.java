package com.bizone.britannia.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Created by Sonam on 16/11/16.
 */
public class SelectAllCheckbox extends CheckBox{
    public SelectAllCheckbox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
    }

    public SelectAllCheckbox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SelectAllCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectAllCheckbox(Context context) {
        super(context);
    }

    public boolean isSelfUncheckFlag() {
        return selfUncheckFlag;
    }

    public void setSelfUncheckFlag(boolean selfUncheckFlag) {
        this.selfUncheckFlag = selfUncheckFlag;
    }

    private boolean selfUncheckFlag=false;

}
