package com.oxygen.ar;

import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;

import android.widget.Button;
import android.widget.PopupWindow;

public class ARPopWindow extends PopupWindow {
	private Button btnCamera, btnCancel;
	private View arMenuView;
	private Button btnWall;
	public ARPopWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		arMenuView = inflater.inflate(R.layout.ar_popwindow, null);
		btnCamera = (Button) arMenuView.findViewById(R.id.ar_pop_btn_camera);
		btnWall=(Button)arMenuView.findViewById(R.id.ar_pop_btn_wall);
		btnCancel = (Button) arMenuView.findViewById(R.id.ar_pop_btn_cancel);

		btnCancel.setOnClickListener(itemsOnClick);
		btnWall.setOnClickListener(itemsOnClick);
		btnCamera.setOnClickListener(itemsOnClick);// 设置按钮监听

		this.setContentView(arMenuView);// 设置PopupWindow的View
		this.setWidth(LayoutParams.MATCH_PARENT);// 设置PopupWindow窗体的宽
		this.setHeight(LayoutParams.MATCH_PARENT);// 设置PopupWindow窗体的高
		this.setFocusable(true);// 设置可点击
		this.setAnimationStyle(R.style.my_pop_anim);// 设置PopupWindow弹出动画
		ColorDrawable dw = new ColorDrawable(0xb0000000);// 实例化一个ColorDrawable颜色为半透明
		this.setBackgroundDrawable(dw);// 设置SelectPicPopupWindow弹出窗体的背景

		// popWindow添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		arMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = arMenuView.findViewById(R.id.pop_layout).getTop();
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
