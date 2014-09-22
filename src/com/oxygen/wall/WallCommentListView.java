package com.oxygen.wall;

import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WallCommentListView extends ListView implements OnScrollListener {

	private final static int DONE = 1;
	private final static int INCREASE_RELEASE_To_INCREASE = 2;
	private final static int INCREASE_PULL_To_INCREASE = 3;
	private final static int INCREASE_INCREASING = 4;

	private final static int RATIO = 3;
	private LayoutInflater inflater;

	private LinearLayout footerView;
	private TextView footerTextView;
	private ProgressBar footerProgressBar;;

	private int startY;
	private int lastItemIndex;
	private int totleItemNumber;
	private int state;
	
	private OnRefreshListener refreshListener;
	private boolean isRefreshable;// 是否可以刷新，在设置刷新监听后为true;
	private boolean isSlide;

	public WallCommentListView(Context context) {
		super(context);
		init(context);
	}

	public WallCommentListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setCacheColorHint(context.getResources().getColor(
				R.color.wall_list_head_transparent));
		inflater = LayoutInflater.from(context);
		footerView = (LinearLayout) inflater.inflate(R.layout.wall_list_foot,
				null);
		footerProgressBar = (ProgressBar) footerView
				.findViewById(R.id.foot_progressBar);
		footerTextView = (TextView) footerView.findViewById(R.id.foot_TextView);
		footerProgressBar.setVisibility(View.GONE);
		addFooterView(footerView);

		setOnScrollListener(this);

		state = INCREASE_PULL_To_INCREASE;
		isRefreshable = false;
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
			int arg3) {
		lastItemIndex = firstVisiableItem + arg2 - 1;
		totleItemNumber = arg3 - 1;

	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}

	public boolean onTouchEvent(MotionEvent event) {
		if(state!=DONE){
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (lastItemIndex == totleItemNumber && !isSlide) {
					isSlide = true;
					startY = (int) event.getY();
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != INCREASE_INCREASING) {
					if (state == INCREASE_PULL_To_INCREASE) {
						changeViewByState();
					}
					if (state == INCREASE_RELEASE_To_INCREASE) {
						state = INCREASE_INCREASING;
						changeViewByState();						
						onIncrease();
					}
				}
				isSlide = false;
				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				if (!isSlide && lastItemIndex == totleItemNumber) {
					isSlide = true;
					startY = tempY;
				}
				if (state != INCREASE_INCREASING && isSlide) {
					if (state == INCREASE_RELEASE_To_INCREASE) 
						if ((startY - tempY) / 3 < 100) {
							state = INCREASE_PULL_To_INCREASE;
							changeViewByState();
						}
					if (state == INCREASE_PULL_To_INCREASE) {
						if ((startY - tempY) / 3 >= 100) {
							state = INCREASE_RELEASE_To_INCREASE;
							changeViewByState();
						}
					}
					footerView.setPadding(0, 0, 0, (startY - tempY) / 3);
				}
				break;
			}
			}
			return super.onTouchEvent(event);
	}

	public void changeIncreaseFlag() {
		state=INCREASE_PULL_To_INCREASE;
		changeViewByState();
	}

	private void changeViewByState() {
		switch (state) {
		case DONE:
			footerView.setPadding(0,0, 0, 0);
			footerTextView.setText("没有更多了...");
			footerProgressBar.setVisibility(View.GONE);
			footerTextView.setVisibility(View.VISIBLE);
			break;
		case INCREASE_PULL_To_INCREASE:
			footerView.setPadding(0,0, 0, 0);
			footerProgressBar.setVisibility(View.GONE);
			footerTextView.setVisibility(View.VISIBLE);
			break;
		case INCREASE_INCREASING:
			footerView.setPadding(0,0, 0, 0);
			footerProgressBar.setVisibility(View.VISIBLE);
			footerTextView.setVisibility(View.GONE);
			break;
		}
	}

	public void setonRefreshListener(OnRefreshListener onRefreshListener) {
		this.refreshListener = onRefreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onIncrease();
	}

	private void onIncrease() {
		if (refreshListener != null) {
			refreshListener.onIncrease();
		}
	}
	public void setAdapter(BaseAdapter adapter) {
		super.setAdapter(adapter);
	}
}