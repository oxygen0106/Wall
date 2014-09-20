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
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.oxygen.data.UserInfo;
import com.oxygen.wall.R;

import android.app.Activity;
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
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyInfoActivity extends Activity {

	private TextView titleText;

	private MyPopWindow myPopWindow;

	MyRoundImageTool roundImageTool;
	public String myImageFileName;
	public File file;
	public Uri imageUri;

	private final int TAKE_PHOTO = 0;
	private final int CHOOSE_PHOTO = 1;
	private final int CROP_PHOTO = 2;

	private List<Map<String, Object>> listData;
	private String userName = null;
	private ListView listView;
	private MyInfoAdapter adapter = null;
	
	private TextView loginBtn;
	
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(R.layout.my_info);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_info_title_bar);

		titleText = (TextView) findViewById(R.id.my_info_title_tv);
		titleText.setText("我的帐号");// 设置TitleBar的TextView
		
		
		initImageFile();//设置路径
		roundImageTool = new MyRoundImageTool(imageUri.getPath());
		
		listView = (ListView) findViewById(R.id.my_info_lv);
		setData();
		setListView();
		
		loginBtn = (TextView) findViewById(R.id.my_info_login_btn);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		changeLoginButton();//设置登录按钮样式
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		resultToDo(requestCode, resultCode);

	}

	void setData(){
		
	}
	
	private void setListView() {
		listData = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", 0);
		map.put("text", "头像");
		listData.add(map);

		map = new HashMap<String, Object>();
		map.put("type", 1);
		map.put("text", "用户名");
		map.put("content", userName);// TODO
		listData.add(map);

		map = new HashMap<String, Object>();
		map.put("type", 1);
		map.put("text", "邮箱");
		map.put("content", "Mail");// TODO
		listData.add(map);

		map = new HashMap<String, Object>();
		map.put("type", 1);
		map.put("text", "个性签名");
		map.put("content", "......");// TODO
		listData.add(map);

		adapter = new MyInfoAdapter(this, listData);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				if (arg3 == 0) {
					setMyImage();// 设置我的头像
				}
				if (arg3 == 1) {
					Toast.makeText(MyInfoActivity.this, "评论",
							Toast.LENGTH_SHORT).show();
				}
				if (arg3 == 2) {
					Toast.makeText(MyInfoActivity.this, "收藏",
							Toast.LENGTH_SHORT).show();
				}
				if (arg3 == 3) {
					Intent intent = new Intent(MyInfoActivity.this,
							MySettingActivity.class);
					startActivity(intent);
				}

			}
		});
	}

	/**
	 * @param
	 * @return void
	 * @Description 设置路径
	 */
	private void initImageFile() {
		file = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/Android/data/" + MyInfoActivity.this.getPackageName()
				+ "/user/userImg");
		if (!file.exists()) {
			file.mkdirs();// 创建路径mnt/sdcard/Android/data/com.oxygen.wall/user/userImg
		}
		imageUri = Uri
				.fromFile(new File(file.getAbsoluteFile(), "myImage.png"));
		myImageFileName = imageUri.getPath();
	}
	
	public void initImage(){
		SharedPreferences mSP = MyInfoActivity.this.getPreferences(Activity.MODE_PRIVATE);
		if (mSP.getString("MyImgURL", null) != null) {
			String imgFilename = mSP.getString("MyImgURL", null);
			Bitmap bitmap = BitmapFactory.decodeFile(imgFilename);
			if (bitmap != null) {// 如果该路径系下的文件不存在，加载默认图片背景
				
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
				case R.id.my_pop_btn_camera:

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 打开相机
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(intent, TAKE_PHOTO);// requestCode
																// TAKE_PHOTO
					break;
				case R.id.my_pop_btn_select_photo:
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
							Bitmap.CompressFormat.JPEG.toString());
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
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
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
			bitmap.compress(Bitmap.CompressFormat.JPEG, 20, b);// 把数据写入文件
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
	 * @Description 更新MyImageView背景，和SP
	 */
	public void updateMyImage(Bitmap bitmap) {
		if (imageUri != null) {
			try {
				bitmap = BitmapFactory.decodeStream(MyInfoActivity.this
						.getContentResolver().openInputStream(imageUri));
				if (bitmap != null) {
					roundImageTool.getRoundImage();
					SharedPreferences mSP = PreferenceManager
							.getDefaultSharedPreferences(this);
					SharedPreferences.Editor mSpEd = mSP.edit();// 用来加载自定义的个人头像
					mSpEd.putString("MyImgURL", myImageFileName);
					mSpEd.commit();
					// bitmap.recycle();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
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

				scaleImage(bitmap, imageUri.getPath());
				break;

			case CROP_PHOTO:// 设置剪裁后的图片

				if (imageUri != null) {
					try {
						bitmap = BitmapFactory
								.decodeStream(MyInfoActivity.this
										.getContentResolver().openInputStream(
												imageUri));
						if (bitmap != null) {
							roundImageTool.getRoundImage();
							SharedPreferences mSP = PreferenceManager
									.getDefaultSharedPreferences(this);
							SharedPreferences.Editor mSpEd = mSP.edit();// 用来加载自定义的个人头像
							mSpEd.putString("MyImgURL", myImageFileName);
							mSpEd.commit();
						}
					} catch (FileNotFoundException e) {
						System.out.println("FileNotFoundException");
						e.printStackTrace();
					}
				}

				Toast.makeText(MyInfoActivity.this, "头像已更新", Toast.LENGTH_SHORT)
						.show();
				break;

			case CHOOSE_PHOTO:// 从相册中选择图片
				updateMyImage(bitmap);
				Toast.makeText(MyInfoActivity.this, "头像已更新, from CHOOSE_PHOTO",
						Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		} else {
			updateMyImage(bitmap);
		}
	}
		
	/**
	* @param 
	* @return void
	* @Description 修改登录按钮内容  
	*/
	private void changeLoginButton(){
		sp = MyInfoActivity.this.getSharedPreferences("user", Context.MODE_PRIVATE);
		if(sp.getBoolean("userStatus", false)){//判断是否为已登录的注册用户
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
					startActivity(new Intent(MyInfoActivity.this, MyLoginActivity.class));
				}
			});
		}else{
			loginBtn.setText("登录/注册");
			loginBtn.setBackgroundResource(R.drawable.my_login_btn_selector);
			loginBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					startActivity(new Intent(MyInfoActivity.this, MyLoginActivity.class));
				}
			});
		}
	}
}
