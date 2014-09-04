package com.oxygen.my;

import com.oxygen.wall.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;

public class MyListView extends ListView {
	public MyListView(Context context) {  
        super(context);  
    }  
  
    public MyListView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
    public MyListView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {  
        switch (ev.getAction()) {  
        case MotionEvent.ACTION_DOWN:  
            int x = (int) ev.getX();  
            int y = (int) ev.getY();  
            int itemNum = pointToPosition(x, y);  
            if (itemNum == AdapterView.INVALID_POSITION)  
                break;  
            else {  
                if (itemNum == 0) {  
                    if (itemNum == (getAdapter().getCount() - 1)) {  
                        //只有一项  
                        setSelector(R.drawable.my_lv_item_4_selector);  
                    } else {  
                        //第一项  
                        setSelector(R.drawable.my_lv_item_2_top_selector);  
                    }  
                } else if (itemNum == (getAdapter().getCount() - 1))  
                    //最后一项  
                    setSelector(R.drawable.my_lv_item_2_end_selector);
                else {  
                    //中间项  
                    setSelector(R.drawable.my_lv_item_mid_selector);  
                }
            }  
            break;  
        case MotionEvent.ACTION_UP: 
            break;  
        }  
        return super.onInterceptTouchEvent(ev);  
    }
    

}  