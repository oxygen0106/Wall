package com.oxygen.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.oxygen.my.MyCheckOtherUserInfo;
import com.oxygen.my.MyListView;
import com.oxygen.my.MyPopWindow;
import com.oxygen.my.MyRoundView;
import com.oxygen.my.MyTimeLine;
import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
 */
public class MyFragment extends Fragment {

	private MyRoundView myImage;
	private MyListView myListView;
	private MyPopWindow myPopWindow;
	private LinearLayout midLayoutMsg;
	private LinearLayout midLayoutRoad;
	private LinearLayout midLayoutZan;

	private List<Map<String, Object>> listData = null;
	private SimpleAdapter adapter = null;

	private Activity activity;
	
	public String myImageFileName;
	public File file;
	public Uri imageUri;
	
	private final int TAKE_PHOTO = 0;
	private final int CHOOSE_PHOTO = 1;
	private final int CROP_PHOTO = 2;
	
	
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

		myImage = (MyRoundView) view.findViewById(R.id.my_image);
		midLayoutMsg = (LinearLayout) view.findViewById(R.id.my_mid_layout_msg);
		midLayoutRoad = (LinearLayout) view
				.findViewById(R.id.my_mid_layout_road);
		midLayoutZan = (LinearLayout) view.findViewById(R.id.my_mid_layout_zan);
		myListView = (MyListView) view.findViewById(R.id.my_lv);

		SharedPreferences mSP = activity.getPreferences(Activity.MODE_PRIVATE);
		if (mSP.getString("MyImgURL", null) != null) {
			String imgFilename = mSP.getString("MyImgURL", null);
			Bitmap bitmap = BitmapFactory.decodeFile(imgFilename);
			if (bitmap != null) {// 如果该路径系下的文件不存在，加载默认图片背景
				myImage.setImageBitmap(bitmap);// 将图片显示在ImageView里
			}
		}

		setListView();// 初始化ListView内容

		addSetListener();// 添加监听事件

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		file = new File(Environment.getExternalStorageDirectory()
				.getPath()
				+ "/Android/data/"
				+ activity.getPackageName()
				+ "/user/userImg");
		if (!file.exists()) {
			file.mkdirs();//创建路径mnt/sdcard/Android/data/com.oxygen.wall/user/userImg
		}
		imageUri = Uri.fromFile(new File(file.getAbsoluteFile(), "myImage.jpg"));
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case TAKE_PHOTO:
//			FileOutputStream fos = null;
//			try {
//				fos = new FileOutputStream(new File(file.getAbsoluteFile(),"rawImage.jpg"));
//			} catch (FileNotFoundException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			Bitmap rawBitmap = BitmapFactory.decodeFile(file.getAbsolutePath()+"/myImage.jpg");
//			if(fos != null){
//				rawBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//			}
			corpImageUri(imageUri, 500, 500, CROP_PHOTO);//跳转至截图
			break;
		case CROP_PHOTO:
			if(imageUri!=null){
				Bitmap bitmap;
				try {
					bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imageUri));
					myImage.setImageBitmap(bitmap);
					myImageFileName = file.getAbsolutePath() + "/myImage.jpg";
					SharedPreferences mSP = activity
							.getPreferences(Activity.MODE_PRIVATE);
					SharedPreferences.Editor mSpEd = mSP.edit();// 用来加载自定义的个人头像
					mSpEd.putString("MyImgURL", myImageFileName);
					mSpEd.commit();
					Toast.makeText(activity, "头像已更新", Toast.LENGTH_SHORT).show();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case CHOOSE_PHOTO:
			if(imageUri!= null){
				Bitmap bitmap;
				try {
					bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imageUri));
					myImage.setImageBitmap(bitmap);
					myImageFileName = file.getAbsolutePath() + "/myImage.jpg";
					SharedPreferences mSP = activity
							.getPreferences(Activity.MODE_PRIVATE);
					SharedPreferences.Editor mSpEd = mSP.edit();// 用来加载自定义的个人头像
					mSpEd.putString("MyImgURL", myImageFileName);
					mSpEd.commit();
					Toast.makeText(activity, "头像已更新", Toast.LENGTH_SHORT).show();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
		
		
/*		
		if (resultCode == Activity.RESULT_OK && requestCode == 1) {
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
				Toast.makeText(activity, "SD卡加载失败！", Toast.LENGTH_SHORT).show();
				Log.i("SDcard", "未加载");
				return;
			}
			

			
			// String name = new DateFormat().format("yyyyMMdd_hhmmss",
			// Calendar.getInstance(Locale.CHINA)) + ".jpg";

//			Bundle bundle = data.getExtras();
//			Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
			
//			if (!file.exists()) {
//				file.mkdirs();//创建路径mnt/sdcard/Android/data/com.oxygen.wall/user/userImg
//			}
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath()+"/rawImage.jpg");
			myImageFileName = file.getAbsolutePath() + "/myImage.jpg";
			FileOutputStream b = null;
			try {
				b = new FileOutputStream(myImageFileName);
				
				bitmap.compress(Bitmap.CompressFormat.JPEG, 20, b);// 把压缩的图像输出
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					b.flush();
					b.close();
					bitmap.recycle();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
Bitmap myBitmap = BitmapFactory.decodeFile(myImageFileName); 
			if (myBitmap != null) {
				myImage.setImageBitmap(myBitmap);// 将图片显示在ImageView里
				SharedPreferences mSP = activity
						.getPreferences(Activity.MODE_PRIVATE);
				SharedPreferences.Editor mSpEd = mSP.edit();// 用来加载自定义的个人头像
				mSpEd.putString("MyImgURL", myImageFileName);
				mSpEd.commit();
				Toast.makeText(activity, "头像已更新", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(activity, "头像更新失败", Toast.LENGTH_SHORT).show();
			}
		}
*/
	}

	/**
	* @param @param uri
	* @param @param outputX
	* @param @param outputY
	* @param @param requestCode
	* @return void
	* @Description 截图选框  
	*/
	public void corpImageUri(Uri uri, int outputX, int outputY, int requestCode){
		Intent intent  = new Intent("com.android.camera.action.CROP");
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
	 * @param
	 * @return void
	 * @Description 添加My界面各个按钮的监听事件
	 */
	private void addSetListener() {

		// 1. 头像添加监听事件
		myImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 创建itemOnClick对象，初始化popWindow
				OnClickListener itemsOnClick = new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						myPopWindow.dismiss();//收回PopWindow
						switch (v.getId()) {
						case R.id.my_pop_btn_camera:
							
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE); // 打开相机
							intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
							startActivityForResult(intent, TAKE_PHOTO);// requestCode TAKE_PHOTO
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
							intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
							intent.putExtra("noFaceDetection", false); // no face detection
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
						activity.findViewById(R.id.main_layout), Gravity.BOTTOM
								| Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});

		// 2. 添加中间栏监听
		midLayoutMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						MyCheckOtherUserInfo.class);
				startActivity(intent);
			}
		});
		midLayoutRoad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), MyTimeLine.class);
				startActivity(intent);
			}
		});
		midLayoutZan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(activity, "Test", Toast.LENGTH_SHORT).show();
			}
		});

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
					Toast.makeText(activity, "留言", Toast.LENGTH_SHORT).show();
				}
				if (arg3 == 1) {
					Toast.makeText(activity, "留言板", Toast.LENGTH_SHORT).show();
				}
				if (arg3 == 2) {
					Toast.makeText(activity, "收藏", Toast.LENGTH_SHORT).show();
				}
				if (arg3 == 3) {
					Toast.makeText(activity, "好友", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

}
