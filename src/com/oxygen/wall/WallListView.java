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

public class WallListView extends ListView implements OnScrollListener {
	private final static int REFRESH_RELEASE_To_REFRESH = 0;
	private final static int REFRESH_PULL_To_REFRESH = 1;
	private final static int REFRESH_REFRESHING = 2;
	private final static int DONE = 3;
	private final static int INCREASE_RELEASE_To_INCREASE = 4;
	private final static int INCREASE_PULL_To_INCREASE = 5;
	private final static int INCREASE_INCREASING = 6;
	// private final static int INCREASE_DONE = 7;
	private boolean refreshFlag;
	private boolean increaseFlag;

	private final static int RATIO = 3;
	private LayoutInflater inflater;
	private LinearLayout headerView;
	private TextView headerTipsTextview;
	private ImageView headerArrowImageView;
	private ProgressBar headerProgressBar;

	private LinearLayout footerView;
	private TextView footerTextView;
	private ProgressBar footerProgressBar;
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int headContentWidth;
	private int headContentHeight;
	private int startY;
	private int firstItemIndex;
	private int lastItemIndex;
	private int totleItemNumber;
	private int state;
	private boolean isBack;
	private OnRefreshListener refreshListener;
	private boolean isRefreshable;// 是否可以刷新，在设置刷新监听后为true;
	private boolean isSlide;
	int i = 1;

	public WallListView(Context context) {
		super(context);
		init(context);
	}

	public WallListView(Context context, AttributeSet attrs) {
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

		headerView = (LinearLayout) inflater.inflate(R.layout.wall_list_head,
				null);
		headerArrowImageView = (ImageView) headerView
				.findViewById(R.id.head_arrowImageView);
		headerArrowImageView.setMinimumWidth(70);
		headerArrowImageView.setMinimumHeight(50);
		headerProgressBar = (ProgressBar) headerView
				.findViewById(R.id.head_progressBar);
		headerTipsTextview = (TextView) headerView
				.findViewById(R.id.head_tipsTextView);

		measureView(headerView);

		headContentHeight = headerView.getMeasuredHeight();
		headContentWidth = headerView.getMeasuredWidth();
		headerView.setPadding(0, -1 * headContentHeight, 0, 0);
		headerView.invalidate();
		addHeaderView(headerView, null, false);
		setOnScrollListener(this);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
			int arg3) {
		firstItemIndex = firstVisiableItem;
		lastItemIndex = firstVisiableItem + arg2 - 1;
		totleItemNumber = arg3 - 1;

	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}

	public boolean onTouchEvent(MotionEvent event) {
		if (isRefreshable) {
			Log.v("record", String.valueOf(refreshFlag));
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				/*
				 * if (firstItemIndex == 0 && !isRecored) { isRecored = true;
				 * startY = (int) event.getY(); Log.v("@@@@@@",
				 * "ACTION_DOWN 这是第  "+i+++"步" +1 ); }
				 */
				break;
			case MotionEvent.ACTION_UP:
				Log.v("action", "actionup");
				if (state != REFRESH_REFRESHING && state != INCREASE_INCREASING) {
					if (state == REFRESH_PULL_To_REFRESH) {
						Log.v("action_up", "REFRESH_PULL_To_REFRESH");
						state = DONE;
						refreshFlag=false;
						changeViewByState();
					}
					if (state == REFRESH_RELEASE_To_REFRESH) {
						Log.v("action_up", "REFRESH_RELEASE_To_REFRESH");
						state = REFRESH_REFRESHING;
						changeViewByState();
						onRefresh();
						refreshFlag=false;
					}
					if (state == INCREASE_RELEASE_To_INCREASE) {
						state=INCREASE_INCREASING;
						changeViewByState();
						onIncrease();
						Log.v("action_up", "INCREASE_INCREASING");
						increaseFlag=false;
					}
					isSlide=false;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				if (!isSlide) {
					isSlide = !isSlide;
					startY = tempY;
					Log.v("zuobiao", "startY="+String.valueOf(startY));
				}
				Log.v("zuobiao", "tempY="+String.valueOf(tempY));
				if (firstItemIndex == 0 && (tempY - startY) > 0
						&& state == DONE) {
					Log.v("action_move", "refreshFlag=true");
					refreshFlag = true;
					state = REFRESH_PULL_To_REFRESH;
				}
				if (state != REFRESH_REFRESHING && refreshFlag) {
					if (state == REFRESH_PULL_To_REFRESH) {
						Log.v("action_move", "setPadding");
						Log.v("action_move", String.valueOf(-1 * headContentHeight
								+ (tempY - startY) / RATIO));
						headerView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);
					}
					if (state == REFRESH_RELEASE_To_REFRESH) {
						headerView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}
					if (state == REFRESH_PULL_To_REFRESH) {
						Log.v("action_move", "REFRESH_PULL_To_REFRESH");
						//setSelection(0);
						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = REFRESH_RELEASE_To_REFRESH;
							isBack = true;
							changeViewByState();
						} else if (tempY - startY <= 0) {
							state = DONE;
							changeViewByState();
						}
					}
					if (state == REFRESH_RELEASE_To_REFRESH) {
						Log.v("action_move", "REFRESH_RELEASE_To_REFRESH");
						setSelection(0);
						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = REFRESH_PULL_To_REFRESH;
							changeViewByState();
						} else if (tempY - startY <= 0) {
							state = DONE;
							changeViewByState();
						}
					}
				
				}
				if (lastItemIndex == totleItemNumber && (startY - tempY) > 0
						&& state == DONE) {
					Log.v("action_move", "increaseFlag=true");
					increaseFlag = true;
					state = INCREASE_PULL_To_INCREASE;
				}
				if (state != INCREASE_INCREASING && increaseFlag) {
					if (state == INCREASE_PULL_To_INCREASE) {
						Log.v("action_move", "INCREASE_PULL_To_INCREASE");
						if (startY - tempY > 50) {
							state = INCREASE_RELEASE_To_INCREASE;
							changeViewByState();
						} else if (startY - tempY < 0) {
							state = DONE;
						}
					}
				}

				/*if (!refreshFlag && !increaseFlag && firstItemIndex == 0
						&& state == DONE) {
					refreshFlag = true;
					startY = tempY;
					state = DONE;
				} else if (!increaseFlag && !refreshFlag
						&& lastItemIndex == totleItemNumber
						&& (state == DONE || state == INCREASE_INCREASING)) {
					increaseFlag = true;
					startY = tempY;
					state = INCREASE_PULL_To_INCREASE;
				}
				if (state != REFRESH_REFRESHING && refreshFlag) {
					if (state == REFRESH_RELEASE_To_REFRESH) {
						setSelection(0);
						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = REFRESH_PULL_To_REFRESH;
							changeViewByState();
						} else if (tempY - startY <= 0) {
							state = DONE;
							changeViewByState();
						}
					}
					if (state == REFRESH_PULL_To_REFRESH) {
						setSelection(0);
						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = REFRESH_RELEASE_To_REFRESH;
							isBack = true;
							changeViewByState();
						} else if (tempY - startY <= 0) {
							state = DONE;
							changeViewByState();
						}
					}
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = REFRESH_PULL_To_REFRESH;
							changeViewByState();
						}
					}
					if (state == REFRESH_PULL_To_REFRESH) {
						headerView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);
					}
					if (state == REFRESH_RELEASE_To_REFRESH) {
						headerView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}
				} else if (state != INCREASE_INCREASING && increaseFlag) {
					if (state == INCREASE_PULL_To_INCREASE) {
						setSelection(totleItemNumber);
						if ((startY - tempY) / RATIO >= 50) {
							state = INCREASE_RELEASE_To_INCREASE;
							changeViewByState();
						}
					}
					if (state == INCREASE_RELEASE_To_INCREASE) {
						setSelection(totleItemNumber);
						if ((startY - tempY) / RATIO < 50) {
							state = INCREASE_PULL_To_INCREASE;
							changeViewByState();
						}
					}
					if (state == INCREASE_RELEASE_To_INCREASE) {
						headerView
								.setPadding(0, (startY - tempY) / RATIO, 0, 0);
					}
					if (state == INCREASE_RELEASE_To_INCREASE) {
						headerView
								.setPadding(0, (startY - tempY) / RATIO, 0, 0);
					}
				}*/
				break;
			}
		}
		return super.onTouchEvent(event);
	}

	public void changeRefreshFlag() {
		isBack=false;
		state=DONE;
		changeViewByState();
	}

	public void changeIncreaseFlag() {
		state=INCREASE_PULL_To_INCREASE;
		changeViewByState();
		state=DONE;
	}

	private void changeViewByState() {
		switch (state) {
		case REFRESH_RELEASE_To_REFRESH:
			headerArrowImageView.setVisibility(View.VISIBLE);
			headerProgressBar.setVisibility(View.GONE);
			headerTipsTextview.setVisibility(View.VISIBLE);
			headerArrowImageView.clearAnimation();
			headerArrowImageView.startAnimation(animation);
			headerTipsTextview.setText("请释放 刷新");
			Log.v("@@@@@@", "RELEASE_To_REFRESH 这是第  " + i++ + "步" + 12
					+ "请释放 刷新");
			break;
		case REFRESH_PULL_To_REFRESH:
			headerProgressBar.setVisibility(View.GONE);
			headerTipsTextview.setVisibility(View.VISIBLE);
			headerArrowImageView.clearAnimation();
			headerArrowImageView.setVisibility(View.VISIBLE);
			if (isBack) {
				isBack = false;
				headerArrowImageView.clearAnimation();
				headerArrowImageView.startAnimation(reverseAnimation);
				headerTipsTextview.setText("isBack  is true ！！！");
			} else {
				headerTipsTextview.setText("isBack  is false ！！！");
			}
			Log.v("@@@@@@", "PULL_To_REFRESH 这是第  " + i++ + "步" + 13
					+ "  changeHeaderViewByState()");
			break;
		case REFRESH_REFRESHING:
			headerView.setPadding(0, 0, 0, 0);
			headerProgressBar.setVisibility(View.VISIBLE);
			headerArrowImageView.clearAnimation();
			headerArrowImageView.setVisibility(View.GONE);
			headerTipsTextview.setText("正在加载中 ...REFRESHING");
			Log.v("@@@@@@", "REFRESHING 这是第  " + i++ + "步"
					+ "正在加载中 ...REFRESHING");
			break;
		case DONE:
			headerView.setPadding(0, -1 * headContentHeight, 0, 0);
			//headerProgressBar.setVisibility(View.GONE);
			headerArrowImageView.clearAnimation();
			headerArrowImageView.setImageResource(R.drawable.arrow);
			headerTipsTextview.setText("已经加载完毕- DONE ");
			//headerTipsTextview.setVisibility(View.GONE);
			//headerArrowImageView.setVisibility(View.GONE);
			Log.v("@@@@@@", "DONE 这是第  " + i++ + "步" + "已经加载完毕- DONE ");
			break;
		case INCREASE_PULL_To_INCREASE:
			footerProgressBar.setVisibility(View.GONE);
			footerTextView.setVisibility(View.VISIBLE);
			break;
		case INCREASE_INCREASING:
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
		public void onRefresh();

		public void onIncrease();
	}

	public void onRefreshComplete() {
		state = DONE;
		changeViewByState();
		Log.v("@@@@@@", "onRefreshComplete() 被调用。。。");
	}

	private void onIncrease() {
		if (refreshListener != null) {
			refreshListener.onIncrease();
		}
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
			Log.v("@@@@@@", "onRefresh被调用，这是第  " + i++ + "步");
		}
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setAdapter(BaseAdapter adapter) {
		super.setAdapter(adapter);
	}
}