package com.oxygen.my;

import com.oxygen.wall.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MySlipButton extends View implements OnTouchListener {

	private boolean NowChoose = false;// 记录当前按钮是否打开,true为打开,false为关闭
	private boolean OnSlip = false;// 记录用户是否在滑动的变量
	private float DownX, NowX;// 按下时的x,当前的x,

	private boolean isChangListenerOn = false;
	private OnChangedListener changedListener;

	private Bitmap bg_on, bg_off;
	
	public MySlipButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public MySlipButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {// 初始化
		// 载入图片资源
		bg_on = BitmapFactory.decodeResource(getResources(),
				R.drawable.my_btn_slip_on);
		bg_off = BitmapFactory.decodeResource(getResources(),
				R.drawable.my_btn_slip_off);
		
		setOnTouchListener(this);// 设置触摸监听
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		float x;

		if (NowX < (bg_on.getWidth() / 2)) {// 滑动到前半段与后半段的背景不同,在此做判断
			canvas.drawBitmap(bg_off, matrix, paint);// 画出关闭时的背景
		} else {
			canvas.drawBitmap(bg_on, matrix, paint);// 画出打开时的背景
		}
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction())// 根据动作来执行代码
		{
		case MotionEvent.ACTION_MOVE:// 滑动
			NowX = event.getX();
			break;
		case MotionEvent.ACTION_DOWN:// 按下
			if (event.getX() > bg_on.getWidth()
					|| event.getY() > bg_on.getHeight())
				return false;
			OnSlip = true;
			DownX = event.getX();
			NowX = DownX;
			break;
		case MotionEvent.ACTION_UP:// 松开
			OnSlip = false;
			boolean LastChoose = NowChoose;
//			if (event.getX() >= (bg_on.getWidth() / 2)){
			if (NowChoose != true){
				NowChoose = true;
				NowX = bg_on.getWidth();
			}else{
				NowChoose = false;
				NowX = 0;
			}
			if (isChangListenerOn && (LastChoose != NowChoose))// 如果设置了监听器,就调用其方法..
				changedListener.OnChanged(NowChoose);//当slipButton处于ture，执行回调接口方法。
			break;
		default:

		}
		invalidate();// 重画控件
		return true;
	}

	
	public void SetOnChangedListener(OnChangedListener l) {// 设置监听器,当状态修改的时候
		isChangListenerOn = true;
		changedListener = l;
	}

	public interface OnChangedListener {
		public abstract void OnChanged(boolean CheckState);
	}
}