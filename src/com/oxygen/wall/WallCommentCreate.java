package com.oxygen.wall;

import com.oxygen.data.WallCommentUpload;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class WallCommentCreate extends Activity {

	private EditText mComment;
	private ImageView mPublish;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 自定义标题栏模式
		setContentView(R.layout.wall_comment_create);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.wall_create_title_bar);// 加载自定义标题栏
		
		mComment=(EditText)findViewById(R.id.comment_create);
		mPublish=(ImageView)findViewById(R.id.publish);
		mPublish.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String comment=mComment.getText().toString();
				if(!comment.equals("")){
					WallCommentUpload wcp=new WallCommentUpload(getApplicationContext(),comment);
					WallCommentCreate.this.finish();
				}
			}
			
		});
	}
	public void clickBack(View v){
		this.finish();
	}
}
