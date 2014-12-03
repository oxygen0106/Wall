package com.oxygen.main;

import java.util.ArrayList;
import java.util.List;

import com.oxygen.my.MyLoginActivity;
import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class WelcomeActivity extends Activity {

	private ViewPager viewpager = null;
	private List<View> list = null;
	private ImageView[] img = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.welcome_activity);
		viewpager = (ViewPager) findViewById(R.id.welcome_viewpage);
		list = new ArrayList<View>();
		list.add(getLayoutInflater().inflate(R.layout.welcome_page_1, null));
		list.add(getLayoutInflater().inflate(R.layout.welcome_page_2, null));
		View pager3 = getLayoutInflater()
				.inflate(R.layout.welcome_page_3, null);
		list.add(pager3);

		setPager3ButtonListener(pager3);

		img = new ImageView[list.size()];
		LinearLayout layout = (LinearLayout) findViewById(R.id.welcome_viewgroup);

		for (int i = 0; i < list.size(); i++) {
			img[i] = new ImageView(WelcomeActivity.this);
			if (0 == i) {
				img[i].setBackgroundResource(R.drawable.welcome_page_indicator_focused);
			} else {
				img[i].setBackgroundResource(R.drawable.welcome_page_indicator);
			}
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 0, 10, 0);
			img[i].setLayoutParams(lp);
			layout.addView(img[i]);
		}

		viewpager.setAdapter(new ViewPagerAdapter(list));
		viewpager.setOnPageChangeListener(new ViewPagerPageChangeListener());

	}

	/**
	 * @param @param pager
	 * @return void
	 * @Description ViewPager 按钮监听
	 */
	private void setPager3ButtonListener(View pager) {
		Button startBtn = (Button) pager
				.findViewById(R.id.welcome_page3_start_btn);
		startBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(WelcomeActivity.this,
						LoadingActivity.class));
				WelcomeActivity.this.finish();
			}
		});

		Button registerBtn = (Button) pager
				.findViewById(R.id.welcome_page3_register_btn);
		registerBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WelcomeActivity.this,
						MyLoginActivity.class);
				intent.putExtra("isExit", true);
				startActivity(intent);
				WelcomeActivity.this.finish();
			}
		});
	}

	class ViewPagerAdapter extends PagerAdapter {

		private List<View> list = null;

		public ViewPagerAdapter(List<View> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(list.get(position));
			return list.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(list.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	class ViewPagerPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		/*
		 * page：看名称就看得出，当前页； positionOffset：位置偏移量，范围[0,1]；
		 * positionoffsetPixels：位置像素，范围[0,屏幕宽度)； 个人认为一般情况下是不会重写这个方法的
		 */
		@Override
		public void onPageScrolled(int page, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int page) {
			// 更新图标
			for (int i = 0; i < list.size(); i++) {
				if (page == i) {
					img[i].setBackgroundResource(R.drawable.welcome_page_indicator_focused);
				} else {
					img[i].setBackgroundResource(R.drawable.welcome_page_indicator);
				}
			}
		}
	}

}
