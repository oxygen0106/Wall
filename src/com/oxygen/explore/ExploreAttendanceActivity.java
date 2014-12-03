package com.oxygen.explore;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ExploreAttendanceActivity extends Activity implements OnClickListener{
	private TextView titleText;
	private ImageView backBtn;
	
	private Button createBtn;
	private Button infoBtn;
	
	private String currentUserID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.explore_attendance);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_setting_title_bar);

		titleText = (TextView) findViewById(R.id.my_setting_title_tv);
		backBtn = (ImageView) findViewById(R.id.my_setting_title_bar_back_btn);
		titleText.setText("新建考勤");
		setBackBtnListener();
		
		createBtn = (Button)findViewById(R.id.explore_attendance_create_btn);
		infoBtn = (Button)findViewById(R.id.explore_attendance_info_btn);
		
		createBtn.setOnClickListener(this);
		infoBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.explore_attendance_create_btn:
			createAVObject();
			break;
		case R.id.explore_attendance_info_btn:
			startActivity(new Intent(ExploreAttendanceActivity.this,ExploreSignUPDataActivity.class));
			break;
		}
	}
	
	
	/**
	* @param 
	* @return void
	* @Description 返回按钮  
	*/
	private void setBackBtnListener() {
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ExploreAttendanceActivity.this.finish();
			}
		});
	}
	
	private void createAVObject(){
		AVUser currentUser = AVUser.getCurrentUser();
		AVObject avo = new AVObject("Attendance");
		avo.put("user", currentUser);
		avo.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(AVException arg0) {
				// TODO Auto-generated method stub
				if(arg0==null){
					
					startActivity(new Intent(ExploreAttendanceActivity.this, ExploreAttendanceQRActivity.class));
				}else{
					Toast.makeText(ExploreAttendanceActivity.this, "服务器异常，请稍后重试！", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
