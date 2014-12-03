package com.oxygen.explore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.oxygen.data.UserInfo;
import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ExploreSignUpResultActivity extends Activity {
	
	private TextView titleText;
	private ImageView backBtn;
	
	private String avobjectId;
	private List<String> userList = new ArrayList<String>();
	private List<String> timeList = new ArrayList<String>();
	private List<String> usernameList = new ArrayList<String>();
	private List<String> userImageUrlList = new ArrayList<String>();
	
	private ImageView okImage;
	private ImageView failImage;
	private TextView resultText;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.explore_sign_up_result);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_setting_title_bar);

		titleText = (TextView) findViewById(R.id.my_setting_title_tv);
		backBtn = (ImageView) findViewById(R.id.my_setting_title_bar_back_btn);
		titleText.setText("");
		
		failImage = (ImageView)findViewById(R.id.explore_sign_up_result_fail_image);
		okImage = (ImageView)findViewById(R.id.explore_sign_up_result_ok_image);
		resultText = (TextView)findViewById(R.id.explore_sign_up_result_tv);
		
		getResult();
	}
	
	private void getAVObjectId(){
		Intent intent = getIntent();
		avobjectId = intent.getStringExtra("result");
	}
	
	private void getResult(){
		getAVObjectId();
		AVQuery<AVObject> query = new AVQuery<AVObject>("Attendance");
		query.getInBackground(avobjectId, new GetCallback<AVObject>() {
			
			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null){
					userList = (ArrayList<String>)arg0.getList("userList");
					timeList = (ArrayList<String>)arg0.getList("timeList");
					usernameList = (ArrayList<String>)arg0.getList("usernameList");
					userImageUrlList = (ArrayList<String>)arg0.getList("userImageUrlList");
					AVUser currentUser = AVUser.getCurrentUser();
					String userID = currentUser.getObjectId();
					AVFile userImage = currentUser.getAVFile(UserInfo.USER_IMG);
					userList.add(userID);
					timeList.add(dateFormat.format(new Date()));
					arg0.put("userList", userList);
					arg0.put("timeList", timeList);
					arg0.put("usernameList", currentUser.getString("username"));
					arg0.put("userImageUrlList", userImage.getUrl());
					arg0.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(AVException arg0) {
							// TODO Auto-generated method stub
							okImage.setVisibility(View.VISIBLE);
							failImage.setVisibility(View.GONE);
							resultText.setText("成功签到");
						}
					});
				}else{
					okImage.setVisibility(View.GONE);
					failImage.setVisibility(View.VISIBLE);
					resultText.setText("签到失败");
				}
			}
		});
	}
}
