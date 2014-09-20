package com.oxygen.ar;

import com.oxygen.wall.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;

public class ARArcMenu extends ViewGroup implements OnClickListener {

	private static final String TAG = "ArcMenu";

	int screenWidth = getMeasuredWidth();
	int screenHeight = getMeasuredHeight();
	private Position mPosition = Position.LEFT_TOP;// 菜单位置
	private int mRadius = 100;// 菜单半径
	private View mButton;// 主按钮
	private Status mCurrentStatus = Status.CLOSE;// 菜单状态
	private OnMenuItemClickListener onMenuItemClickListener;// Callback

	public ARArcMenu(Context context) {
		this(context, null);
	}

	public ARArcMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ARArcMenu(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		// dp convert to px
		mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				mRadius, getResources().getDisplayMetrics());
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.ARArcMenu, defStyle, 0);

		int n = a.getIndexCount();// 根据属性值，确定菜单显示类型
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.ARArcMenu_position:
				int val = a.getInt(attr, 0);
				switch (val) {
				case 0:
					mPosition = Position.LEFT_TOP;
					break;
				case 1:
					mPosition = Position.RIGHT_TOP;
					break;
				case 2:
					mPosition = Position.RIGHT_BOTTOM;
					break;
				case 3:
					mPosition = Position.LEFT_BOTTOM;
					break;
				case 4:
					mPosition = Position.BOTTOM;
					break;
				}
				break;
			case R.styleable.ARArcMenu_radius:
				// dp convert to px
				mRadius = a.getDimensionPixelSize(attr, (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f,
								getResources().getDisplayMetrics()));
				break;

			}
		}
		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			// mesure child
			getChildAt(i).measure(MeasureSpec.UNSPECIFIED,
					MeasureSpec.UNSPECIFIED);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {

			layoutButton();
			int count = getChildCount();
			if (mPosition == Position.BOTTOM) {
				for (int i = 0; i < count - 1; i++) {
					View child = getChildAt(i + 1);
					child.setVisibility(View.GONE);
					int sin = 0;
					int cos = 0;

					int cl = 0;
					int ct = 0;

					int cWidth = child.getMeasuredWidth();
					// childview height
					int cHeight = child.getMeasuredHeight();

					switch (i) {
					case 1:
						sin = (int) (mRadius * Math.sin(Math.PI / 4));
						cos = (int) (mRadius * Math.cos(Math.PI / 4));

						cl = screenWidth / 2 - cos - cWidth / 2;
						ct = screenHeight / 2 - sin - cHeight / 2;
						break;
					case 2:
						sin = mRadius;
						cos = 0;

						cl = screenWidth / 2 - cos - cWidth / 2;
						ct = screenHeight / 2 - sin - cHeight / 2;

						break;
					case 3:
						sin = (int) (mRadius * Math.sin(Math.PI / 4));
						// child top
						cos = -((int) (mRadius * Math.cos(Math.PI / 4)));

						cl = screenWidth / 2 - cos - cWidth / 2;
						ct = screenHeight / 2 - sin - cHeight / 2;

						break;
					}
					Log.e(TAG, cl + " , " + ct);
					child.layout(cl, ct, cl + cWidth, ct + cHeight);
				}

			} else {
				/**
				 * 设置所有孩子的位置 例如(第一个为按钮)： 左上时，从左到右 ] 第2个：mRadius(sin0 , cos0)
				 * 第3个：mRadius(sina ,cosa) 注：[a = Math.PI / 2 * (cCount - 1)]
				 * 第4个：mRadius(sin2a ,cos2a) 第5个：mRadius(sin3a , cos3a) ...
				 */
				for (int i = 0; i < count - 1; i++) {
					View child = getChildAt(i + 1);
					child.setVisibility(View.GONE);

					int cl = (int) (mRadius * Math.sin(Math.PI / 2
							/ (count - 2) * i));
					int ct = (int) (mRadius * Math.cos(Math.PI / 2
							/ (count - 2) * i));
					// childview width
					int cWidth = child.getMeasuredWidth();
					// childview height
					int cHeight = child.getMeasuredHeight();

					// 右上，右下
					if (mPosition == Position.LEFT_BOTTOM
							|| mPosition == Position.RIGHT_BOTTOM) {
						ct = getMeasuredHeight() - cHeight - ct;
					}
					// 右上，右下
					if (mPosition == Position.RIGHT_TOP
							|| mPosition == Position.RIGHT_BOTTOM) {
						cl = getMeasuredWidth() - cWidth - cl;
					}

					Log.e(TAG, cl + " , " + ct);
					child.layout(cl, ct, cl + cWidth, ct + cHeight);

				}
			}
		}
	}

	/**
	 * @param
	 * @return void
	 * @Description 第一个子元素为按钮，为按钮布局且初始化点击事件
	 */
	private void layoutButton() {
		View cButton = getChildAt(0);

		cButton.setOnClickListener(this);

		int l = 0;
		int t = 0;
		int width = cButton.getMeasuredWidth();
		int height = cButton.getMeasuredHeight();
		switch (mPosition) {
		case LEFT_TOP:
			l = 0;
			t = 0;
			break;
		case LEFT_BOTTOM:
			l = 0;
			t = getMeasuredHeight() - height;
			break;
		case RIGHT_TOP:
			l = getMeasuredWidth() - width;
			t = 0;
			break;
		case RIGHT_BOTTOM:
			l = getMeasuredWidth() - width;
			t = getMeasuredHeight() - height;
			break;
		case BOTTOM:
			l = (getMeasuredWidth() - width) / 2;
			t = l = (getMeasuredHeight() - height) / 2;
			break;
		}
		Log.e(TAG, l + " , " + t + " , " + (l + width) + " , " + (t + height));
		cButton.layout(l, t, l + width, t + height);

	}

	/**
	 * @Description 主按钮监听事件
	 */
	@Override
	public void onClick(View v) {
//		mButton = findViewById(R.id.id_button);
		if (mButton == null) {
			mButton = getChildAt(0);
		}
		rotateView(mButton, 0f, 270f, 300);
		toggleMenu(300);
	}

	/**
	 * @param @param view
	 * @param @param fromDegrees
	 * @param @param toDegrees
	 * @param @param durationMillis
	 * @return void
	 * @Description 按钮旋转动画
	 */
	public static void rotateView(View view, float fromDegrees,
			float toDegrees, int durationMillis) {
		RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotate.setDuration(durationMillis);
		rotate.setFillAfter(true);
		view.startAnimation(rotate);
	}

	/**
	 * @param @param durationMillis
	 * @return void
	 * @Description 四角子按钮布局
	 */
	public void toggleMenu(int durationMillis) {
		int count = getChildCount();
		for (int i = 0; i < count - 1; i++) {
			final View childView = getChildAt(i + 1);
			childView.setVisibility(View.VISIBLE);

			int xflag = 1;
			int yflag = 1;
			boolean bottom = false;

			if (mPosition == Position.LEFT_TOP
					|| mPosition == Position.LEFT_BOTTOM)
				xflag = -1;
			if (mPosition == Position.LEFT_TOP
					|| mPosition == Position.RIGHT_TOP)
				yflag = -1;
			if (bottom == false) {
				bottom = true;
			}

			// child left
			int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
			// child top
			int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));

			AnimationSet animset = new AnimationSet(true);
			Animation animation = null;
			if (mCurrentStatus == Status.CLOSE) {// to open

				animset.setInterpolator(new OvershootInterpolator(2F));
				animation = new TranslateAnimation(xflag * cl, 0, yflag * ct, 0);
				childView.setClickable(true);
				childView.setFocusable(true);
			} else {// to close
				animation = new TranslateAnimation(0f, xflag * cl, 0f, yflag
						* ct);
				childView.setClickable(false);
				childView.setFocusable(false);
			}
			animation.setAnimationListener(new AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					if (mCurrentStatus == Status.CLOSE)
						childView.setVisibility(View.GONE);

				}
			});

			animation.setFillAfter(true);
			animation.setDuration(durationMillis);
			// 为动画设置一个开始延迟时间，纯属好看，可以不设
			animation.setStartOffset((i * 100) / (count - 1));
			RotateAnimation rotate = new RotateAnimation(0, 720,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			rotate.setDuration(durationMillis);
			rotate.setFillAfter(true);
			animset.addAnimation(rotate);
			animset.addAnimation(animation);
			childView.startAnimation(animset);
			final int index = i + 1;
			childView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onMenuItemClickListener != null)
						onMenuItemClickListener.onClick(childView, index - 1);
					menuItemAnin(index - 1);
					changeStatus();

				}
			});

		}
		changeStatus();
		Log.e(TAG, mCurrentStatus.name() + "");
	}

	/**
	 * @param @param durationMillis
	 * @return void
	 * @Description 底部中间子按钮布局
	 */
	public void bottomMenu(int durationMillis) {
		int count = getChildCount();
		for (int i = 0; i < count - 1; i++) {
			final View childView = getChildAt(i + 1);
			childView.setVisibility(View.VISIBLE);

			int sin = 0;
			int cos = 0;

			switch (i) {
			case 1:
				sin = (int) (mRadius * Math.sin(Math.PI / 4));
				// child top
				cos = (int) (mRadius * Math.cos(Math.PI / 4));
				break;
			case 2:
				sin = mRadius;
				// child top
				cos = 0;
				break;
			case 3:
				sin = (int) (mRadius * Math.sin(Math.PI / 4));
				// child top
				cos = -((int) (mRadius * Math.cos(Math.PI / 4)));
				break;
			}

			AnimationSet animset = new AnimationSet(true);
			Animation animation = null;
			if (mCurrentStatus == Status.CLOSE) {// to open

				animset.setInterpolator(new OvershootInterpolator(2F));
				animation = new TranslateAnimation(screenWidth / 2, screenWidth
						/ 2 - cos, screenHeight, screenHeight - sin);
				childView.setClickable(true);
				childView.setFocusable(true);
			} else {// to close
				animation = new TranslateAnimation(screenWidth / 2 - cos,
						screenWidth / 2, screenHeight - sin, screenHeight);
				childView.setClickable(false);
				childView.setFocusable(false);
			}

			animation.setAnimationListener(new AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					if (mCurrentStatus == Status.CLOSE)
						childView.setVisibility(View.GONE);

				}
			});

			animation.setFillAfter(true);
			animation.setDuration(durationMillis);
			// 为动画设置一个开始延迟时间，纯属好看，可以不设
			animation.setStartOffset((i * 100) / (count - 1));
			RotateAnimation rotate = new RotateAnimation(0, 720,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			rotate.setDuration(durationMillis);
			rotate.setFillAfter(true);
			animset.addAnimation(rotate);
			animset.addAnimation(animation);
			childView.startAnimation(animset);
			final int index = i + 1;
			childView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onMenuItemClickListener != null)
						onMenuItemClickListener.onClick(childView, index - 1);
					menuItemAnin(index - 1);
					changeStatus();

				}
			});

		}
		changeStatus();
		Log.e(TAG, mCurrentStatus.name() + "");
	}

	/**
	 * @param
	 * @return void
	 * @Description 改变状态
	 */
	private void changeStatus() {
		mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN
				: Status.CLOSE);
	}

	/**
	 * @param @param item
	 * @return void
	 * @Description 开始菜单动画，点击的MenuItem放大消失，其他的缩小消失
	 */
	private void menuItemAnin(int item) {
		for (int i = 0; i < getChildCount() - 1; i++) {
			View childView = getChildAt(i + 1);
			if (i == item) {
				childView.startAnimation(scaleBigAnim(300));
			} else {
				childView.startAnimation(scaleSmallAnim(300));
			}
			childView.setClickable(false);
			childView.setFocusable(false);

		}

	}

	/**
	 * @param @param durationMillis
	 * @param @return
	 * @return Animation
	 * @Description 缩小消失动画
	 */
	private Animation scaleSmallAnim(int durationMillis) {
		Animation anim = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setDuration(durationMillis);
		anim.setFillAfter(true);
		return anim;
	}

	/**
	 * @param @param durationMillis
	 * @param @return
	 * @return Animation
	 * @Description 放大，透明度降低
	 */
	private Animation scaleBigAnim(int durationMillis) {
		AnimationSet animationset = new AnimationSet(true);

		Animation anim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		Animation alphaAnimation = new AlphaAnimation(1, 0);
		animationset.addAnimation(anim);
		animationset.addAnimation(alphaAnimation);
		animationset.setDuration(durationMillis);
		animationset.setFillAfter(true);
		return animationset;
	}

	/**
	 * @param @return
	 * @return Position
	 * @Description 获取位置
	 */
	public Position getmPosition() {
		return mPosition;
	}

	/**
	 * @param @param mPosition
	 * @return void
	 * @Description 设置位置
	 */
	public void setmPosition(Position mPosition) {
		this.mPosition = mPosition;
	}

	/**
	 * @param @return
	 * @return int
	 * @Description 获取半径
	 */
	public int getmRadius() {
		return mRadius;
	}

	/**
	 * @param @param 
	 * @return void
	 * @Description 设置半径
	 */
	public void setmRadius(int mRadius) {
		this.mRadius = mRadius;
	}

	/**
	 * @param @return
	 * @return Status
	 * @Description 得到当前状态
	 */
	public Status getmCurrentStatus() {
		return mCurrentStatus;
	}

	/**
	 * @param @param mCurrentStatus
	 * @return void
	 * @Description 设置当前状态
	 */
	public void setmCurrentStatus(Status mCurrentStatus) {
		this.mCurrentStatus = mCurrentStatus;
	}

	public OnMenuItemClickListener getOnMenuItemClickListener() {
		return onMenuItemClickListener;
	}

	public void setOnMenuItemClickListener(
			OnMenuItemClickListener onMenuItemClickListener) {
		this.onMenuItemClickListener = onMenuItemClickListener;
	}

	public enum Status {// 状态枚举
		OPEN, CLOSE
	}

	public enum Position {// 位置枚举
		LEFT_TOP, RIGHT_TOP, RIGHT_BOTTOM, LEFT_BOTTOM, BOTTOM;
	}

	public interface OnMenuItemClickListener {// 回调方法
		void onClick(View view, int pos);
	}

}
