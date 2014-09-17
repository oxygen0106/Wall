package com.oxygen.my;

import com.oxygen.wall.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class MyAbout extends Activity {
	
	TextView titleText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(R.layout.my_about);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.my_about_title_bar);
		
		titleText = (TextView)findViewById(R.id.my_about_title_tv);
		titleText.setText("关于");// 设置TitleBar的TextView
	}
}
