package com.oxygen.explore;

import java.util.List;
import java.util.Random;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.oxygen.explore.Shake.ShakeListener;
import com.oxygen.map.GetLocation;
import com.oxygen.wall.R;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

public class ExploreLaunchPopWindow extends PopupWindow {
	
	private View menuView;
	


	public ExploreLaunchPopWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		menuView = inflater.inflate(R.layout.explore_launch_popwindow, null);
		



		this.setContentView(menuView);// 设置PopupWindow的View
		this.setWidth(LayoutParams.MATCH_PARENT);// 设置PopupWindow窗体的宽
		this.setHeight(LayoutParams.WRAP_CONTENT);// 设置PopupWindow窗体的高
		this.setFocusable(true);// 设置可点击
		this.setAnimationStyle(R.style.my_pop_anim);// 设置PopupWindow弹出动画
		ColorDrawable dw = new ColorDrawable(0xb0000000);// 实例化一个ColorDrawable颜色为半透明
		this.setBackgroundDrawable(dw);// 设置SelectPicPopupWindow弹出窗体的背景

		// popWindow添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		menuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = menuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {//触摸popWindow以外区域退出
						dismiss();
					}
				}
				return true;
			}
		});

	}
	

	
	
}
