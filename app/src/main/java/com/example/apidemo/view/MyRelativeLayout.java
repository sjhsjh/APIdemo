package com.example.apidemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;

/**
 *
 * Created by Administrator on 2018/8/4 0004.
 */

public class MyRelativeLayout extends RelativeLayout implements Checkable{
    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private boolean isChecked = false;
    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
        // NLog.v("sjh1", "checked = " + checked + "   " + ((TextView)findViewById(R.id.itemview)).getText());
        if (checked) {
            setBackgroundResource(R.color.colorPrimary);
           // setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            setBackgroundResource(0);
        }
    }

    @Override
    public boolean isChecked() {
        NLog.i("sjh1", "isChecked" );   // mListView.setChoiceMode没触发这
        return isChecked;
        // return false;
    }

    @Override
    public void toggle() {
        NLog.i("sjh1", "toggle" );      // mListView.setChoiceMode没触发这
        setChecked(!isChecked);
    }


}
