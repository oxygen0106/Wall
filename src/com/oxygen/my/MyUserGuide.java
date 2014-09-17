package com.oxygen.my;

import com.oxygen.wall.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class MyUserGuide extends Activity {
	
	TextView titleText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(R.layout.my_user_guide);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.my_user_guide_title_bar);
		
		titleText = (TextView)findViewById(R.id.my_user_guide_title_tv);
		titleText.setText("使用说明");// 设置TitleBar的TextView
	}
}
