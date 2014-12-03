package com.oxygen.ar;

import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import android.widget.LinearLayout;


public class ARPopWindow extends PopupWindow {
	private LinearLayout sacnBtn, createBtn, locationBtn;
	private View arMenuView;
	private ImageView cancelBtn;
	private Context context;
	private LinearLayout layout;

	public ARPopWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);

		arMenuView = inflater.inflate(R.layout.ar_popwindow, null);

		sacnBtn = (LinearLayout) arMenuView.findViewById(R.id.ar_pop_scan_btn);
		createBtn = (LinearLayout) arMenuView.findViewById(R.id.ar_pop_create_btn);
		locationBtn = (LinearLayout) arMenuView
				.findViewById(R.id.ar_pop_location_btn);
		cancelBtn = (ImageView) arMenuView.findViewById(R.id.ar_pop_cancel_btn);
		layout = (LinearLayout)arMenuView.findViewById(R.id.ar_pop_layout);
		
		
//		sacnBtn.startAnimation(scaleBigAnim(300));

		sacnBtn.setOnClickListener(itemsOnClick);
		createBtn.setOnClickListener(itemsOnClick);
		locationBtn.setOnClickListener(itemsOnClick);// 设置按钮监听
		
		setPopAnimation(layout);

		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dismiss();
			}
		});

		this.setContentView(arMenuView);// 设置PopupWindow的View
		this.setWidth(LayoutParams.MATCH_PARENT);// 设置PopupWindow窗体的宽
		this.setHeight(LayoutParams.MATCH_PARENT);// 设置PopupWindow窗体的高
		this.setFocusable(true);// 设置可点击
		// this.setAnimationStyle(R.style.my_pop_anim);// 设置PopupWindow弹出动画
		ColorDrawable dw = new ColorDrawable(0xc0000000);// 实例化一个ColorDrawable颜色为半透明
		this.setBackgroundDrawable(dw);// 设置SelectPicPopupWindow弹出窗体的背景

		// popWindow添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		arMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = arMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {// 触摸popWindow以外区域退出
						dismiss();
					}
				}
				return true;
			}
		});

	}

	private void setPopAnimation(View view) {
		int[] location = new int[2];
		view.getLocationInWindow(location);

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		

		AnimationSet animset = new AnimationSet(true);
		Animation animation = null;
		animset.setInterpolator(new OvershootInterpolator(2F));
		animation = new TranslateAnimation(0, 0, 500, 0);
//		animation.setFillAfter(true);
		animation.setDuration(1000);
		RotateAnimation rotate = new RotateAnimation(0, 270,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(1000);
//		rotate.setFillAfter(true);
//		animset.addAnimation(rotate);
		animset.addAnimation(animation);
		view.startAnimation(animset);
	}
	
	private void cancelPopAnimation(View view) {
		int[] location = new int[2];
		view.getLocationInWindow(location);

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();

		AnimationSet animset = new AnimationSet(true);
		Animation animation = null;
		animset.setInterpolator(new OvershootInterpolator(2F));
		animation = new TranslateAnimation(0, 0, 0, 500);
//		animation.setFillAfter(true);
		animation.setDuration(1000);
		RotateAnimation rotate = new RotateAnimation(0, 270,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(1000);
//		rotate.setFillAfter(true);
//		animset.addAnimation(rotate);
		animset.addAnimation(animation);
		view.startAnimation(animset);
		
	}
	
Handler handler = new Handler(){
	public void handleMessage(Message msg) {
		if(msg.what==1){
			
			cancelPopAnimation(layout);
//			dismiss();
		}
	};
};
	
Thread thread = new Thread(){
	public void run() {
		Message cancel = Message.obtain();
		cancel.what = 1;
		handler.sendMessage(cancel);
	};
};


//private Animation scaleBigAnim(int durationMillis) {
//	AnimationSet animationset = new AnimationSet(true);
//
//	Animation anim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
//			Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//			0.5f);
//	Animation alphaAnimation = new AlphaAnimation(1, 0);
//	animationset.addAnimation(anim);
//	animationset.addAnimation(alphaAnimation);
//	animationset.setDuration(durationMillis);
//	animationset.setFillAfter(true);
//	return animationset;
//}



}
