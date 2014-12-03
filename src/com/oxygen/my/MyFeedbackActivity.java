package com.oxygen.my;

import com.avos.avoscloud.AVObject;
import com.oxygen.wall.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyFeedbackActivity extends Activity {
	private TextView titleText; 
	private ImageView backBtn;
	
	private EditText contentText;
	private EditText mailText;
	private Button submitBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(R.layout.my_feedback_activity);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.my_setting_title_bar);
		
		titleText = (TextView)findViewById(R.id.my_setting_title_tv);
		backBtn = (ImageView)findViewById(R.id.my_setting_title_bar_back_btn);
		titleText.setText("意见反馈");
		setBackBtnListener();
		
		contentText = (EditText)findViewById(R.id.my_feedback_content_tv);
		mailText = (EditText)findViewById(R.id.my_feedback_mail_tv);
		submitBtn = (Button)findViewById(R.id.my_feedback_content_ok_btn);
		
		
		submitBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(contentText.getText().toString()==null||contentText.getText().toString().equals("")){
					Toast.makeText(MyFeedbackActivity.this, "请填写完整内容", Toast.LENGTH_LONG).show();
				}else if(mailText.getText().toString()==null||mailText.getText().toString().equals("")){
					Toast.makeText(MyFeedbackActivity.this, "请填写联系方式", Toast.LENGTH_LONG).show();
				}else{
					AVObject avo = new AVObject("Feedback");
					avo.put("content", contentText.getText().toString());
					avo.put("mail", mailText.getText().toString());
					avo.saveInBackground();
					contentText.setText("");
					mailText.setText("");
					Toast.makeText(MyFeedbackActivity.this, "提交成功，感谢您的意见和建议！", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	
	
	private void setBackBtnListener(){
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyFeedbackActivity.this.finish();
			}
		});
	}
}
