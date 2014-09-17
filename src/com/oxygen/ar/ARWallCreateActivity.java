package com.oxygen.ar;

import java.io.File;

import com.oxygen.my.MyInfoActivity;
import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ARWallCreateActivity extends Activity {
	
	private TextView mPublish;
	private EditText mContent;
	private ImageView mPicture;
	
	private Uri imageUri;
	private File file;
	private ClickListener mClickListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 自定义标题栏模式
		setContentView(R.layout.wall_create_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.wall_create_title_bar);// 加载自定义标题栏
		file = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/Android/data/" + this.getPackageName()
				+ "/user/wallImage");
		imageUri = Uri
				.fromFile(new File(file.getAbsoluteFile(), "wallImage.png"));
		mClickListener=new ClickListener();
		mPublish=(TextView)findViewById(R.id.publish);
		mContent=(EditText)findViewById(R.id.wall_content);
		mPicture=(ImageView)findViewById(R.id.picture);
		mPicture.setOnClickListener(mClickListener);
		mPublish.setOnClickListener(mClickListener);
	}
	private class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
				case R.id.picture:
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 打开相机
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivity(intent);
					break;
				case R.id.publish:
					
					break;
			}
		}
	}
}
