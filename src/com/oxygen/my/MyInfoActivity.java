package com.oxygen.my;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.oxygen.data.UserInfo;
import com.oxygen.wall.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyInfoActivity extends Activity implements OnClickListener {

	private TextView titleText; 
	private ImageView backBtn;

	private MyPopWindow myPopWindow;

	MyRoundImageTool roundImageTool;
	public File file;
	public Uri imageUri;
	private final int TAKE_PHOTO = 0;
	private final int CHOOSE_PHOTO = 1;
	private final int CROP_PHOTO = 2;

	private String userName = null;
	private String mail;
	private String userNote;
	private boolean userStatus = false;

	private Bitmap bitmapImage;
	final int REFRESH_MY_IMAGE = 1;
	boolean isUpdata = false;
	
	private LinearLayout myImageLayout;
	private RelativeLayout userNameLayout;
	private RelativeLayout userMailLayout;
	private RelativeLayout userSexLayout;
	private RelativeLayout userNoteLayout;
	private ImageView myImage;
	private TextView userNameText;
	private TextView userSexText;
	private TextView userMailText;
	private TextView userNoteText;
	private TextView loginBtn;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(R.layout.my_info);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_setting_title_bar);

		titleText = (TextView)findViewById(R.id.my_setting_title_tv);
		backBtn = (ImageView)findViewById(R.id.my_setting_title_bar_back_btn);
		titleText.setText("帐号信息");
		setBackBtnListener();

		initImageFile();// 设置路径
		roundImageTool = new MyRoundImageTool(imageUri.getPath());
		

		setUserData();// 获取当前用户的数据

		loginBtn = (TextView) findViewById(R.id.my_info_login_btn);
		myImageLayout = (LinearLayout) findViewById(R.id.my_info_my_image_layout);
		userNameLayout = (RelativeLayout) findViewById(R.id.my_info_my_name_layout);
		userMailLayout = (RelativeLayout) findViewById(R.id.my_info_mail_layout);
		userSexLayout = (RelativeLayout) findViewById(R.id.my_info_sex_layout);
		userNoteLayout = (RelativeLayout) findViewById(R.id.my_info_user_note_layout);
		
		myImage = (ImageView)findViewById(R.id.my_info_my_image);
		userNameText = (TextView)findViewById(R.id.my_info_my_name_tv);
		userSexText = (TextView)findViewById(R.id.my_info_sex);
		userMailText  = (TextView)findViewById(R.id.my_info_mail_tv);
		userNoteText = (TextView)findViewById(R.id.my_info_user_note_tv);
		
		myImageLayout.setOnClickListener(this);
		userNameLayout.setOnClickListener(this);
		userMailLayout.setOnClickListener(this);
		userSexLayout.setOnClickListener(this);
		userNameLayout.setOnClickListener(this);
		
		userNameText.setText(userName);
//		userSexText = userSex;
		userMailText.setText(mail);
		userNoteText.setText(userNote);
		
		initImage();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		changeLoginButton();// 设置登录按钮样式
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		resultToDo(requestCode, resultCode);// 处理返回事件

	}

	@Override
	public void onClick(View v) {
		if(userStatus=true){
			switch (v.getId()) {
			case R.id.my_info_my_image_layout:
				setMyImage();// 设置我的头像
				break;
			case R.id.my_info_my_name_layout:
				new AlertDialog.Builder(MyInfoActivity.this).setTitle("设置用户名")
						.setView(new EditText(MyInfoActivity.this))
						.setPositiveButton("确定", null)
						.setNegativeButton("取消", null).show();
				break;
			case R.id.my_info_sex_layout:
				new AlertDialog.Builder(this).setTitle("性别")
						.setMultiChoiceItems(new String[] { "男", "女" }, null, null)
						.setPositiveButton("确定", null)
						.setNegativeButton("取消", null).show();
				break;
			case R.id.my_info_mail_layout:
				new AlertDialog.Builder(MyInfoActivity.this).setTitle("设置邮箱")
						.setView(new EditText(MyInfoActivity.this))
						.setPositiveButton("确定", null)
						.setNegativeButton("取消", null).show();
				break;
			case R.id.my_info_user_note_layout:
				new AlertDialog.Builder(MyInfoActivity.this).setTitle("个性签名")
						.setView(new EditText(MyInfoActivity.this))
						.setPositiveButton("确定", null)
						.setNegativeButton("取消", null).show();
				break;
			}
		}else{
			switch (v.getId()) {
			case R.id.my_info_my_image_layout:
				setMyImage();// 设置我的头像
				break;
			default:
				new AlertDialog.Builder(MyInfoActivity.this)
					.setTitle("请先完成用户注册").setPositiveButton("确定", null)
					.setNegativeButton("取消", null).show();
				break;
			}
		}
	}

	/**
	 * @param
	 * @return void
	 * @Description TODO
	 */
	void setUserData() {
		AVUser currentUser = AVUser.getCurrentUser();
		if (currentUser.isAnonymous()) {
			userStatus = false;
			userName = "临时访客";
			mail = currentUser.getString(UserInfo.MAIL);
			userNote = currentUser.getString(UserInfo.NOTE);
		} else {
			userStatus = true;
			userName = currentUser.getString(UserInfo.USER_NAME);
			mail = currentUser.getString(UserInfo.MAIL);
			userNote = currentUser.getString(UserInfo.NOTE);
		}

	}

	

	/**
	 * @param
	 * @return void
	 * @Description 设置路径
	 */
	private void initImageFile() {
		file = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/Android/data/" + MyInfoActivity.this.getPackageName()
				+ "/user");
		if (!file.exists()) {
			file.mkdirs();// 创建路径mnt/sdcard/Android/data/com.oxygen.wall/user/userImg
		}
		imageUri = Uri
				.fromFile(new File(file.getAbsoluteFile(), "myImage.png"));
	}

	/**
	* @param 
	* @return void
	* @Description 加载头像  
	*/
	public void initImage() {
		if (new File(imageUri.getPath()).exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath());
			if (bitmap != null) {// 如果该路径系下的文件不存在，加载默认图片背景
				 myImage.setImageBitmap(bitmap);// 将图片显示在ImageView里
			}
		}
	}

	private void setMyImage() {
		OnClickListener itemsOnClick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myPopWindow.dismiss();// 收回PopWindow
				switch (v.getId()) {
				case R.id.my_pop_btn_camera:// 打开相机

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(intent, TAKE_PHOTO);// requestCode
					break;
				case R.id.my_pop_btn_select_photo:// 打开相册
					intent = new Intent(Intent.ACTION_GET_CONTENT, null);
					intent.setType("image/*");
					intent.putExtra("crop", "true");
					intent.putExtra("aspectX", 1);
					intent.putExtra("aspectY", 1);
					intent.putExtra("outputX", 500);
					intent.putExtra("outputY", 500);
					intent.putExtra("scale", true);
					intent.putExtra("return-data", false);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					intent.putExtra("outputFormat",
							Bitmap.CompressFormat.PNG.toString());
					intent.putExtra("noFaceDetection", false); // no
																// face
																// detection
					startActivityForResult(intent, CHOOSE_PHOTO);
					break;
				case R.id.my_pop_btn_cancel:
					break;
				default:
					break;
				}
			}
		};

		myPopWindow = new MyPopWindow(MyInfoActivity.this, itemsOnClick);
		// 设置layout在PopupWindow中显示的位置
		myPopWindow.showAtLocation(
				MyInfoActivity.this.findViewById(R.id.my_info_layout),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	/**
	 * @param @param uri
	 * @param @param outputX
	 * @param @param outputY
	 * @param @param requestCode
	 * @return void
	 * @Description 图像裁剪选框
	 */
	public void corpImageUri(Uri uri, int outputX, int outputY, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, requestCode);

	}

	/**
	 * @param @param bitmap
	 * @param @param path
	 * @return void
	 * @Description 根据当前设备屏幕，设置采样率 ，进行图片压缩
	 */
	public void scaleImage(Bitmap bitmap, String path) {
		BitmapFactory.Options opts = new Options();
		opts.inJustDecodeBounds = true;// 不读取像素数组到内存中，仅读取图片的信息
		BitmapFactory.decodeFile(imageUri.getPath(), opts);
		int imageHeight = opts.outHeight;// 从Options中获取图片的分辨率
		int imageWidth = opts.outWidth;
		// 获取WindowManager
		WindowManager wm = (WindowManager) MyInfoActivity.this
				.getSystemService(MyInfoActivity.this.WINDOW_SERVICE);
		// 获取屏幕的分辨率，getHeight()、getWidth已经deprecated, 使用getSize()
		// requires API level 13
		// Point outSize = new Point();
		// wm.getDefaultDisplay().getSize(outSize);
		// int windowHeight = outSize.y;
		// int windowWidth = outSize.x;
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		int windowHeight = metrics.heightPixels;
		int windowWidth = metrics.widthPixels;

		// 计算采样率
		int scaleX = imageWidth / windowWidth;
		int scaleY = imageHeight / windowHeight;
		int scale = 1;
		// 采样率依照最大的方向为准
		if (scaleX > scaleY && scaleY >= 1) {
			scale = scaleX;
		}
		if (scaleX < scaleY && scaleX >= 1) {
			scale = scaleY;
		}

		opts.inJustDecodeBounds = false;// false表示读取图片像素数组到内存中，依照设定的采样率
		opts.inSampleSize = scale + 1;// 采样率
		bitmap = BitmapFactory.decodeFile(path, opts);

		writeBitmap(bitmap, path);
		bitmap = null;
		System.gc();
	}

	/**
	 * @param @param bitmap
	 * @param @param path
	 * @return void
	 * @Description 输出Bitmap到SD卡
	 */
	public void writeBitmap(Bitmap bitmap, String path) {
		// Bundle bundle = data.getExtras();
		// bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
		FileOutputStream b = null;
		try {
			b = new FileOutputStream(path);
			bitmap.compress(Bitmap.CompressFormat.PNG, 1, b);// 把数据写入文件
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param
	 * @return void
	 * @Description 处理相机返回的结果
	 */
	private void resultToDo(int requestCode, int resultCode) {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		Bitmap bitmap = null;
		if (resultCode == MyInfoActivity.this.RESULT_OK) {
			switch (requestCode) {
			case TAKE_PHOTO:// 成功拍照返回后执行剪裁
				corpImageUri(imageUri, 500, 500, CROP_PHOTO);// 跳转至截图
				break;
			case CROP_PHOTO:// 设置剪裁后的图片
				isUpdata = true;
				new Thread(new reFreshRunnable()).start();//更新头像UI
				break;

			case CHOOSE_PHOTO:// 从相册中选择图片
				isUpdata = true;
				new Thread(new reFreshRunnable()).start();//更新头像UI
				break;

			default:
				break;
			}
		} else {
			scaleImage(bitmap, imageUri.getPath());//根据当前屏幕压缩图像
			new Thread(new reFreshRunnable()).start();//更新头像UI
		}
	}

	Handler myHandler = new Handler() {
	    public void handleMessage(Message msg) {
	        if(msg.what== REFRESH_MY_IMAGE){
	        	if(bitmapImage!=null){
	        		myImage.setImageBitmap(bitmapImage);
	        		if(isUpdata){
	        			uploadMyImage();
	        			Toast.makeText(MyInfoActivity.this, "头像已更新", Toast.LENGTH_SHORT).show();
	        		}
	        		bitmapImage = null;
	        		System.gc();
	        	}
	        }
	    }
	};
	
	/**
	* @ClassName reFreshRunnable
	* @Description 头像更新线程
	*/
	private class reFreshRunnable implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			roundImageTool.getRoundImage();//裁剪
			bitmapImage = BitmapFactory.decodeFile(imageUri.getPath());
			 Message msg = myHandler.obtainMessage();
			 msg.what = REFRESH_MY_IMAGE;
		     myHandler.sendMessage(msg);    // 向Handler发送消息，更新UI
		}
	}
	
	
	/**
	 * @param
	 * @return void
	 * @Description 修改登录按钮内容
	 */
	private void changeLoginButton() {
		sp = MyInfoActivity.this.getSharedPreferences("user",
				Context.MODE_PRIVATE);
		if (sp.getBoolean("userStatus", false)) {// 判断是否为已登录的注册用户
			loginBtn.setText("退出");
			loginBtn.setBackgroundResource(R.drawable.my_logout_btn_selector);
			loginBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SharedPreferences.Editor editor = sp.edit();
					editor.clear();
					editor.commit();
					AVUser.logOut();
					startActivity(new Intent(MyInfoActivity.this,
							MyLoginActivity.class));
				}
			});
		} else {
			loginBtn.setText("登录/注册");
			loginBtn.setBackgroundResource(R.drawable.my_login_btn_selector);
			loginBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					startActivity(new Intent(MyInfoActivity.this,
							MyLoginActivity.class));
				}
			});
		}

	}
	
	
	/**
	* @param 
	* @return void
	* @Description 上传头像  
	*/
	private void uploadMyImage(){
		AVFile imageFile= null;
		try {
			imageFile = AVFile.withAbsoluteLocalPath("myImage.png", imageUri.getPath());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(imageFile != null){
			AVUser currentUser = AVUser.getCurrentUser();
			currentUser.put(UserInfo.USER_IMG, imageFile);
			currentUser.saveInBackground();
		}
	}
	
	
	private void setBackBtnListener(){
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyInfoActivity.this.finish();
			}
		});
	}
}
