package com.oxygen.my;

import java.io.File;
import java.text.SimpleDateFormat;
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
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.oxygen.data.UserInfo;
import com.oxygen.data.WallInfoUpload;
import com.oxygen.image.LoaderListener;
import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @ClassName MyTimeLine
 * @Description 时间轴：显示我创建和我参与的留言板动态
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-9-2 上午10:02:42
 */
public class MyTimeLineActivity extends Activity {

	private AVUser currentUser;

	private int wallInfoCount = 0;

	private LinearLayout infoCountLayout;
	private ListView lv;
	private TextView dateText;
	private ImageView myImage;
	public Uri imageUri;
	
	private List<HashMap<String, String>> listData = null;
	private MyTimeLineAdapter adapter = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	private ImageLoadingListener animateFirstListener = LoaderListener.loaderListener;
	private DisplayImageOptions options = LoaderListener.getThumbNailOptions();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 自定义标题栏模式
		this.setContentView(R.layout.my_timeline);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.my_timeline_title_bar);// 加载自定义标题栏
		
		infoCountLayout = (LinearLayout)findViewById(R.id.my_timeline_info_num_layout);
		dateText = (TextView)findViewById(R.id.my_timeline_info_date);
		dateText.setText(dateFormat.format(new Date()));
		myImage = (ImageView)findViewById(R.id.my_timeline_user_img);
		
		lv = (ListView) findViewById(R.id.my_timeline_lv);
		
		
		getCurrentUser();//判断当前用户并加载数据
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * @param
	 * @return void
	 * @Description 设置下半部分ListView选项内容
	 */
	private void setListView(List<AVObject> list) {
		listData = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;
		for (int i = 0; i < wallInfoCount; i++) {
			AVObject wallAVObject = list.get(i);

			map = new HashMap<String, String>();
			map.put("type", Integer.toString(i % 2));
			map.put("itemText", wallAVObject.getString(WallInfoUpload.CONTENT));
			map.put("date", dateFormat.format(wallAVObject.getCreatedAt()));
			map.put("location", wallAVObject.getString("address"));
			listData.add(map);
		}

		adapter = new MyTimeLineAdapter(this, listData);
		lv.setAdapter(adapter);
	}

	/**
	 * @param
	 * @return void
	 * @Description 获取用户所发布wall数量
	 */
	private void getMyWallInfo(AVUser currentUser) {

		AVQuery<AVObject> query = new AVQuery<AVObject>("WallInfo");
		query.whereEqualTo(WallInfoUpload.USER, currentUser);
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					wallInfoCount = arg0.size();
					setListView(arg0);//载入ListView数据
					initNumImageView(wallInfoCount);
					
				} else {
					Toast.makeText(MyTimeLineActivity.this, arg1.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	/**
	* @param @param count
	* @return void
	* @Description 加载数字ImageView  
	*/
	private void initNumImageView(int count) {
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		Resources res = this.getResources();
		String str = String.valueOf(count);
		for (int i = 0; i < str.length(); i++) {
			int num = Integer.valueOf(str.charAt(i)+"");
			int imageId = res.getIdentifier("my_timeline_info_num_" + num,
					"drawable", this.getPackageName());
			TextView imageView = new TextView(this);
			imageView.setText(" ");
			imageView.setBackgroundResource(imageId);
			infoCountLayout.addView(imageView);
		}
	}
	
	/**
	* @param 
	* @return void
	* @Description 加载头像  
	*/
	public void initImage() {
		initImageFile();
		if (new File(imageUri.getPath()).exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath());
			if (bitmap != null) {// 如果该路径系下的文件不存在，加载默认图片背景
				 myImage.setImageBitmap(bitmap);// 将图片显示在ImageView里
			}
		}
	}
	
	
	/**
	 * @param
	 * @return void
	 * @Description 设置路径
	 */
	private void initImageFile() {
//		AVUser user = AVUser.getCurrentUser();
//		if(user!=null){
//			AVFile userImage = user.getAVFile(UserInfo.USER_IMG);
//			if(userImage !=null){
//			ImageLoader.getInstance().displayImage(userImage.getUrl(), myImage, options, animateFirstListener);
//			}
//		}
		
		File file = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/Android/data/" + this.getPackageName()
				+ "/user");
		if (!file.exists()) {
			file.mkdirs();// 创建路径mnt/sdcard/Android/data/com.oxygen.wall/user/userImg
		}
		imageUri = Uri
				.fromFile(new File(file.getAbsoluteFile(), "myImage.png"));
	}
	
	private String getUserID(){
		Intent intent = getIntent();
		String userID= intent.getStringExtra("userID");
		return userID;
	}
	
	private void getCurrentUser(){
		
		setUserImage();
		if(getUserID()==null||getUserID().equals("")){//当前用户
			currentUser = AVUser.getCurrentUser();
			getMyWallInfo(currentUser);
		}else{
			AVQuery<AVUser> query = new  AVQuery<AVUser>("_User");
			query.whereEqualTo("objectId", getUserID());
			query.findInBackground(new FindCallback<AVUser>() {
				@Override
				public void done(List<AVUser> arg0, AVException arg1) {
					// TODO Auto-generated method stub
					if(arg1==null){
						currentUser = arg0.get(0);
						getMyWallInfo(currentUser);
					}else{
						Toast.makeText(MyTimeLineActivity.this, "网络异常", Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	}
	
	
	private void setUserImage(){
		AVUser user = AVUser.getCurrentUser();
		AVQuery<AVUser> query = new AVQuery<AVUser>("_User");
		query.whereEqualTo("objectId", user.getObjectId());
		query.findInBackground(new FindCallback<AVUser>() {
			@Override
			public void done(List<AVUser> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null){
					if(arg0.size()>0){
						AVUser user = arg0.get(0);
						if(user!=null){
							Log.v("10.1", "user != NULL");
							AVFile userImage = user.getAVFile(UserInfo.USER_IMG);
							if(userImage !=null){
								Log.v("10.1", "image != NULL");
							ImageLoader.getInstance().displayImage(userImage.getUrl(), myImage, options, animateFirstListener);
							}else{
								Log.v("10.1", "image == NULL");
							}
						}
					}
				}
			}
		});
	}

}
