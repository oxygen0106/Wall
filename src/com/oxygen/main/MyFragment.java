package com.oxygen.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.oxygen.data.UserInfo;
import com.oxygen.data.WallInfoUpload;
import com.oxygen.map.GetLocation;
import com.oxygen.my.MyMessageActivity;
import com.oxygen.my.MyRoundImageTool;
import com.oxygen.my.MyWallInfoActivity;
import com.oxygen.my.MyZanActivity;
import com.oxygen.my.MyUserInfoActivity;
import com.oxygen.my.MyListView;
import com.oxygen.my.MyPopWindow;
import com.oxygen.my.MySettingActivity;
import com.oxygen.my.MyTimeLineActivity;
import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @ClassName MyFragment
 * @Description 用户界面包括三部分：头像（上），按钮（中），ListView（下）
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-8-18 下午09:47:52
 * 
 */
public class MyFragment extends Fragment {

	private boolean userStatus;
	private String userName;

	private ImageView myImage;
	private MyListView myListView;
	private LinearLayout midLayoutMsg;
	private LinearLayout midLayoutRoad;
	private LinearLayout midLayoutZan;
	private TextView userNameText;

	private List<Map<String, Object>> listData = null;
	private SimpleAdapter adapter = null;

	private Activity activity;

	public String userImagePath;
	public File file;
	public Uri imageUri;
	private Bitmap bitmapImage;
	final int REFRESH_MY_IMAGE = 1;
	boolean isUpdata = false;
	private MyRoundImageTool roundImageTool;
	private final int TAKE_PHOTO = 0;
	private final int CHOOSE_PHOTO = 1;
	private final int CROP_PHOTO = 2;
	private MyPopWindow myPopWindow;
	
	private ImageView notifyPoint;
	private final static String publicMsgId = "541ea7ade4b0eabcaeb2e1f8";

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		this.activity = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.my_fragment, container, false);

		myImage = (ImageView) view.findViewById(R.id.my_image);
		midLayoutMsg = (LinearLayout) view.findViewById(R.id.my_mid_layout_msg);
		midLayoutRoad = (LinearLayout) view
				.findViewById(R.id.my_mid_layout_road);
		midLayoutZan = (LinearLayout) view.findViewById(R.id.my_mid_layout_zan);
		myListView = (MyListView) view.findViewById(R.id.my_lv);
		userNameText = (TextView) view.findViewById(R.id.my_name_tv);
		notifyPoint = (ImageView)view.findViewById(R.id.notify_point);
		

		currentUserStatus();// 检测当前用户身份

		initImageFile();
		initImage();
		
		setListView();// 初始化ListView内容

		addSetListener();// 添加监听事件
		
		checkNotifyPoint();//redPoint
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		initImage();
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * @param
	 * @return void
	 * @Description 添加My界面各个按钮的监听事件
	 */
	private void addSetListener() {

		// 1. 头像添加监听事件
		myImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setMyImage();
			}
		});

		// 2. 添加中间栏监听
		midLayoutMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						MyMessageActivity.class);
				startActivity(intent);
			}
		});
		midLayoutRoad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						MyTimeLineActivity.class);
				startActivity(intent);
			}
		});
		midLayoutZan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						MyZanActivity.class);
				startActivity(intent);
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
				+ "/Android/data/" + activity.getPackageName()
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
				 bitmap = null;
				 System.gc();
			}
		}
	}
	
	/**
	 * @param
	 * @return void
	 * @Description 设置下半部分ListView选项内容
	 */
	private void setListView() {
		listData = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("text", "留言板");
		listData.add(map);

		map = new HashMap<String, Object>();
		map.put("text", "评论");
		listData.add(map);

		map = new HashMap<String, Object>();
		map.put("text", "收藏");
		listData.add(map);

		map = new HashMap<String, Object>();
		map.put("text", "好友");
		listData.add(map);

		adapter = new SimpleAdapter(activity, listData, R.layout.my_lv_item,
				new String[] { "text" }, new int[] { R.id.my_lv_item_tv });
		myListView.setAdapter(adapter);

		myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				if (arg3 == 0) {
					Intent intent = new Intent(getActivity(),
							MyWallInfoActivity.class);
					startActivity(intent);
				}
				if (arg3 == 1) {
					Toast.makeText(activity, "评论", Toast.LENGTH_SHORT).show();
				}
				if (arg3 == 2) {
					Toast.makeText(activity, "收藏", Toast.LENGTH_SHORT).show();
				}
				if (arg3 == 3) {
					Intent intent = new Intent(activity,
							MySettingActivity.class);
					startActivity(intent);
				}

			}
		});
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

		myPopWindow = new MyPopWindow(activity, itemsOnClick);
		// 设置layout在PopupWindow中显示的位置
		myPopWindow.showAtLocation(
				activity.findViewById(R.id.my_fragment_layout),
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
		WindowManager wm = (WindowManager) activity
				.getSystemService(activity.WINDOW_SERVICE);
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
		if (resultCode == activity.RESULT_OK) {
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
	        			Toast.makeText(activity, "头像已更新", Toast.LENGTH_SHORT).show();
	        		}bitmapImage = null;
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
	 * @Description 判断当前用户身份,设置用户名，建立路径
	 */
	private void currentUserStatus() {
		AVUser currentUser = AVUser.getCurrentUser();
		if (currentUser.isAnonymous()) {
			userStatus = false;
			userNameText.setText("游客");
			
		} else {
			userStatus = true;
			userNameText.setText(currentUser.getString(UserInfo.USER_NAME));
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
	
	private void checkNotifyPoint(){

		
		AVQuery<AVUser> adminQuery = AVUser.getQuery();
		adminQuery.getInBackground(publicMsgId, new GetCallback<AVUser>() {
			
			@Override
			public void done(AVUser arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null){
					AVQuery<AVObject> query = new AVQuery<AVObject>("Message");
					query.include("User");
					query.whereEqualTo("user", arg0);

					query.findInBackground(new FindCallback<AVObject>() {
						public void done(List<AVObject> arg0, AVException arg1) {
						
							if(arg1==null){
								AVUser currentUser = AVUser.getCurrentUser();
								final String userMsgUpdateTime = currentUser.getDate("msgUpdate").toString();
								final String adminPushCreatedTime = arg0.get(0).getCreatedAt().toString();
								
								Log.v("msg","管理员最近更新消息时间~~~~~~~~~"+adminPushCreatedTime);
								Log.v("msg","用户最近更新消息时间~~~~~~~~~"+"用户名"+currentUser.getString("username")+"消息更新时间"+userMsgUpdateTime);
								
								if(!userMsgUpdateTime.equals(adminPushCreatedTime)){
									Log.v("msg","用户最近更新消息时间~~~不相等~~~亮灯~~~");
									notifyPoint.setVisibility(View.VISIBLE);//点亮 红灯
								}
							}
						};
					});
				}
			}
		});
		
		AVUser currentUser = AVUser.getCurrentUser();
		AVQuery<AVObject> queryMy = new AVQuery<AVObject>("Message");
		queryMy.whereEqualTo("user", currentUser);
		queryMy.addDescendingOrder("createdAt");
		queryMy.findInBackground(new FindCallback<AVObject>() {
			
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null){
					//如果user的msgUpdate和Message中关联用户的createdAt不相等，更新消息
					AVUser currentUser = AVUser.getCurrentUser();
					if(!arg0.get(0).getString("createdAt").equals(currentUser.getString("msgUpdate"))){
						notifyPoint.setVisibility(View.VISIBLE);//点亮 红灯
					}
				}
			}
		});
		
	}
}
