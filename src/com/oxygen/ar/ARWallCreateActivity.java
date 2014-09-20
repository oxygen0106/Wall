package com.oxygen.ar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.oxygen.ar.target.PostNewTarget;
import com.oxygen.data.WallInfoUpload;
import com.oxygen.my.MyInfoActivity;
import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ARWallCreateActivity extends Activity {
	
	private TextView mPublish;
	private TextView mBack;
	private EditText mContent;
	private ImageView mPicture;
	private ImageView mCamera;
	private Uri mImageUri;
	private File mFile;
	private ClickListener mClickListener;
	private String mUserID;
	private static final int CAMERA=1;
	private boolean hasPicture;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 自定义标题栏模式
		setContentView(R.layout.wall_create_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.wall_create_title_bar);// 加载自定义标题栏
		Intent intent=getIntent();
		createDir();
		mClickListener=new ClickListener();
		
		mPublish=(TextView)findViewById(R.id.publish);
		mPicture=(ImageView)findViewById(R.id.picture);
		mContent=(EditText)findViewById(R.id.wall_content);
		mCamera=(ImageView)findViewById(R.id.camera);
		mCamera.setOnClickListener(mClickListener);
		mPublish.setOnClickListener(mClickListener);
		mBack=(TextView)findViewById(R.id.back);
		mBack.setOnClickListener(mClickListener);
		hasPicture=false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK){
		switch(requestCode){
			case CAMERA:
				if(data!=null&&data.hasExtra("data")){
					Bitmap thumbnail = data.getParcelableExtra("data");
					mPicture.setImageBitmap(thumbnail);
				}else{
					int width=mPicture.getWidth();
					int height=mPicture.getHeight();
					Log.v("Width", String.valueOf(width));
					Log.v("Height",String.valueOf(height));
					BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
					factoryOptions.inJustDecodeBounds=true;
					BitmapFactory.decodeFile(mImageUri.getPath(),factoryOptions);
					int imageWidth = factoryOptions.outWidth;
					int imageHeight= factoryOptions.outHeight;
					
					int scaleFactor= Math.min(imageWidth/width,imageHeight/height);
					factoryOptions.inJustDecodeBounds=false;
					factoryOptions.inSampleSize=scaleFactor;
					factoryOptions.inPurgeable=true;
					Bitmap bitmap= BitmapFactory.decodeFile(mImageUri.getPath(),factoryOptions);
					
					int rotation=RotateImage.readPictureDegree(mImageUri.getPath());
					bitmap=RotateImage.rotaingImageView(rotation, bitmap);
					mPicture.setImageBitmap(bitmap);
					mPicture.setVisibility(View.VISIBLE);
					
					factoryOptions.inSampleSize=(int)imageHeight/480;
					Bitmap bm=BitmapFactory.decodeFile(mImageUri.getPath(),factoryOptions);
					bm=RotateImage.rotaingImageView(rotation, bm);
					compressImage(bm);
					hasPicture=true;
				}
				break;
			}
		}
	}
	
	
	private void compressImage(Bitmap image) {  
		  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        try {
			FileOutputStream fos=new FileOutputStream(mImageUri.getPath());
			fos.write(baos.toByteArray());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    } 
	
	private void createDir(){
		mFile = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/Android/data/" + this.getPackageName()
				+ "/wallImage/");
		if(!mFile.exists())
			mFile.mkdirs();
		mImageUri = Uri
				.fromFile(new File(mFile.getAbsoluteFile(), "wallImage.png"));
	}
	private class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
				case R.id.camera:
					//InputMethodManager imm = (InputMethodManager) (ARWallCreateActivity.this).getSystemService(Context.INPUT_METHOD_SERVICE);
					//imm.showSoftInputFromInputMethod(token, flags)
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 打开相机
					intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
					startActivityForResult(intent, CAMERA);
					break;
				case R.id.publish:
					if(mContent.getText().toString().equals("")&&!hasPicture){
						
					}else{
						Log.v("publish", "content不为空");
						SharedPreferences sp=getSharedPreferences("user", MODE_PRIVATE);
						String userID=sp.getString("userID", null);
						userID="1111111111111";
						//if(userID!=null){
							Log.v("publish", "userID不为空");
							WallInfoUpload wallInfoUpload=new WallInfoUpload(mContent.getText().toString(),mImageUri.getPath(),userID,getApplicationContext());
						
						//}
					ARWallCreateActivity.this.finish();
					}		
					break;
				case R.id.back:
					ARWallCreateActivity.this.finish();
					break;
			}
		}
	}
}
