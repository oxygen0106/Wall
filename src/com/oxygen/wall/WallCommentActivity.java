package com.oxygen.wall;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.oxygen.ar.cloudrecognition.CloudReco;
import com.oxygen.data.WallCommentDownload;
import com.oxygen.data.WallCommentUpload;
import com.oxygen.data.WallInfoDownload;
import com.oxygen.data.WallInfoUpload;
import com.oxygen.data.WallSupport;
import com.oxygen.image.ImagePagerActivity;
import com.oxygen.image.LoaderListener;
import com.oxygen.main.MainActivity;
import com.oxygen.my.MyUserInfoActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

public class WallCommentActivity extends Activity{
	
	private static final String TAG="WallCommentActivity";
	private WallCommentListView mListView;
	private ItemAdapter mAdapter;
	private WallInfoDownload mWallData;
	private ArrayList<WallCommentDownload> mCommentData;
	private ClickListener mClickListener;
	private boolean mSupportFlag;
	private boolean mCommentFlag;
	private static final int  RESULT=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 自定义标题栏模式
		setContentView(R.layout.wall_comment_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.wall_comment_title_bar);// 加载自定义标题栏
	
		Intent intent = getIntent();
		mCommentFlag=intent.getBooleanExtra("commentFlag", false);
		mWallData=WallCurrent.wid;

		ImageView ar=(ImageView)findViewById(R.id.ar);
		if(!mCommentFlag){
		if(!mWallData.isNear)
			ar.setVisibility(View.GONE);
		else{
			ar.setVisibility(View.VISIBLE);
			ar.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.v("AR", "start");
					Intent intent=new Intent(getApplicationContext(),CloudReco.class);
					intent.putExtra("hasWallInfo",true);
				//	startActivity(intent);
					startActivityForResult(intent, RESULT);
				}
			});
		}
		}else{
			ar.setVisibility(View.VISIBLE);
			ar.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//Log.v("AR", "start");
					Intent intent=new Intent(getApplicationContext(),WallCommentCreate.class);
					//intent.putExtra("hasWallInfo",true);
					//startActivity(intent);
					startActivityForResult(intent, RESULT);
				}
			});
		}
		mClickListener=new ClickListener();
		mCommentData=new ArrayList<WallCommentDownload>();
		addComment();
		mAdapter = new ItemAdapter(this,R.layout.wall_comment_item);
		mListView = (WallCommentListView)findViewById(R.id.comment_list);
		mListView.setAdapter(mAdapter);
		Log.v(TAG, "setAdapter");
		
		mListView.setonRefreshListener(new WallCommentListView.OnRefreshListener() {
			public void onIncrease(){
				addComment();
			}
		});
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		switch(requestCode){
			case RESULT:
				addComment();
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	public void addComment(){
		Log.v(TAG, "addComment");
		AVQuery<AVObject> query= new AVQuery<AVObject>("WallComment");
		query.orderByAscending("createdAt");
		query.include(WallCommentUpload.USER);
		query.include(WallCommentUpload.WALL);
		query.setLimit(10);
		query.setSkip(mCommentData.size());
		query.whereEqualTo(WallCommentUpload.WALL, WallCurrent.wid.getWallObject());
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				Log.v(TAG, "done");
				if(arg1==null){
					Log.v(TAG, "!=null");
					int i=0;
					for(;i<arg0.size();i++){
						Log.v(TAG, "!=0");
						WallCommentDownload wcd=new WallCommentDownload(arg0.get(i));
						mCommentData.add(wcd);
					}
					mWallData.setCommentCount(mCommentData.size());
					mAdapter.notifyDataSetChanged();
					//mAdapter.notifyDataSetChanged();
					if(i<10){
						mListView.removeFootView();
					}else if(!mListView.hasFootView){
						mListView.addFootView();
					}
						
				}else{
					Log.v(TAG, "else error");
				}
			}
		});
	}
	public void clickBack(View v){
		this.finish();
	}
	public void onResume() {
		super.onResume();
		mAdapter.notifyDataSetChanged();
	};
	private class ItemAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater;
		private int mResource;
		private Context mContext;
		private ImageLoadingListener animateFirstListener = LoaderListener.loaderListener;
		private DisplayImageOptions options = LoaderListener.getThumbNailOptions();
		public ItemAdapter(Context context, int resource) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mResource = resource;
			mContext = context;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mCommentData.size()+1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if(position==0)
				return mWallData;
			return mCommentData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Log.v(TAG, "getView");
			View view;
			if(position==0){
				view=mInflater.inflate(R.layout.wall_list_item, null);
				View authorContent=view.findViewById(R.id.author_content);
				authorContent.setTag(position);
				authorContent.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
						Intent intent=new Intent(mContext,MyUserInfoActivity.class);
						intent.putExtra("userID",mWallData.getUserID());
						startActivity(intent);
					}
				});
				ImageView image=(ImageView)view.findViewById(R.id.authorImage);
				if(mWallData.getUserImageURL()!=null){
					ImageLoader.getInstance().displayImage(mWallData.getUserImageURL(), image, options, animateFirstListener);
				}
				TextView username = (TextView) view.findViewById(R.id.authorName);
				username.setText(mWallData.getUserName());
				TextView posttime = (TextView) view.findViewById(R.id.post_time);
				posttime.setText(mWallData.getWallCreateTime());
				TextView distance = ((TextView) view.findViewById(R.id.distance));
				
				distance.setText("距离："+String.valueOf((double)(mWallData.getDistance())/1000.0)+"千米");
				TextView content = (TextView) view.findViewById(R.id.content);
				content.setText(mWallData.getWallContent());
				ImageView picture = (ImageView)view.findViewById(R.id.contentImage);
				ImageLoader.getInstance().displayImage(mWallData.getImageURL(), picture, options, animateFirstListener);
				picture.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getApplicationContext(),ImagePagerActivity.class);
						intent.putExtra("imageURL",mWallData.getImageURL());
						startActivity(intent);
					}
				});
				TextView commentNum = (TextView) view.findViewById(R.id.comment_num);
				commentNum.setText(String.valueOf(mWallData.getCommentCount()));
				TextView supportNum = (TextView) view.findViewById(R.id.support_num);
				supportNum.setText(String.valueOf(mWallData.getSupportCount()));
				LinearLayout comment=(LinearLayout)view.findViewById(R.id.comment_linear);
				comment.setOnClickListener(mClickListener);
				LinearLayout share=(LinearLayout)view.findViewById(R.id.share_linear);
				share.setOnClickListener(mClickListener);
				LinearLayout support=(LinearLayout)view.findViewById(R.id.support_linear);
				support.setOnClickListener(mClickListener);
			}else {
				Log.v(TAG, "position!=0");
				Log.v(TAG, convertView.toString());
				if (convertView==null||convertView.getTag()==null) {
					Log.v(TAG, "getView convertView==null");
					view = mInflater.inflate(mResource, parent, false);
				} else {
					view = convertView;
				}
				Log.v("position", String.valueOf(position));
				Log.v("datasize", String.valueOf(mCommentData.size()));
				bindView(position, view);
			}
			return view;
		}
		private void bindView(int position, View view) {
			//position--;
			WallCommentDownload wcd=mCommentData.get(position-1);
			View authorContent=view.findViewById(R.id.commenter_content);
			authorContent.setTag(position-1);
			authorContent.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WallCommentDownload wcd=mCommentData.get((Integer) v.getTag());
					Intent intent=new Intent(mContext,MyUserInfoActivity.class);
					intent.putExtra("userID",wcd.getCommenterID());
					startActivity(intent);
				}
			});
			TextView commenterName = ((TextView) view.findViewById(R.id.commenter_name));
			commenterName.setText(wcd.getCommenterName());
			TextView commentRank = (TextView) view.findViewById(R.id.comment_rank);
			commentRank.setText(String.valueOf(position));
			//添加头像
			ImageView commenterImage = ((ImageView) view.findViewById(R.id.commenter_image));
			if(wcd.getCommenterImage()!=null){
				ImageLoader.getInstance().displayImage(wcd.getCommenterImage(), commenterImage, options, animateFirstListener);
			}
			TextView commentContent = (TextView) view.findViewById(R.id.comment_content);
			commentContent.setText(wcd.getCommentContent());
			TextView commentCreateTime = (TextView) view.findViewById(R.id.comment_time);
			commentCreateTime.setText(wcd.getCommentCreateTime());
		//	Log.v(null, (String) commenterName.getText());
			/*commenterImage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.v("Suport", "click");
				}
			});*/
		}
	}
	private class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.share_linear:
				break;
			case R.id.support_linear:
					// TODO Auto-generated method stub
					Log.v(TAG, "click");
			
					if(!mSupportFlag&&WallSupport.isHave(mWallData.getWallID())){
						mSupportFlag=true;
						WallSupport.delete(mWallData.getWallObject().getObjectId());
						mWallData.decreaseSupportCount();
						mAdapter.notifyDataSetChanged();
						AVUser user = AVUser.getCurrentUser();
						AVRelation<AVObject> relation = user.getRelation("Supports");
						relation.remove(mWallData.getWallObject());
						user.saveInBackground(new SaveCallback(){
							@Override
							public void done(AVException arg0) {
								// TODO Auto-generated method stub
								if(arg0==null){
									mWallData.getWallObject().increment(WallInfoUpload.SUPPORT,-1);
									mWallData.getWallObject().setFetchWhenSave(true);
									mWallData.getWallObject().saveInBackground(new SaveCallback(){
										@Override
										public void done(AVException arg0) {
											// TODO Auto-generated method stub
											if(arg0==null){
												WallSupport.delete(mWallData.getWallObject().getObjectId());
												mWallData.setSupportCount(mWallData.getWallObject().getInt(WallInfoUpload.SUPPORT));
												//Log.v("supportnum",mWallInfoDownload.getWallObject().get)
												Log.v("numdecrease", "decrease");
												mAdapter.notifyDataSetChanged();
												mSupportFlag=false;
											}else{
												mWallData.increaseSupportCount();
												mAdapter.notifyDataSetChanged();
												Log.v("numdecrease", "increase2");
												mSupportFlag=false;
											}
										}
										
									});
								}else{
									mWallData.increaseSupportCount();
									mAdapter.notifyDataSetChanged();
									Log.v("numdecrease", "increase1");
									mSupportFlag=false;
								}
							}
						});
						//relation.getQuery().
						//relation.get
					}else if(!mSupportFlag){
						mSupportFlag=true;
						WallSupport.add(mWallData.getWallObject().getObjectId());
						mWallData.increaseSupportCount();
						mAdapter.notifyDataSetChanged();
						
						AVUser user = AVUser.getCurrentUser();
						AVRelation<AVObject> relation = user.getRelation("Supports");
						relation.add(mWallData.getWallObject());
						user.saveInBackground(new SaveCallback(){
							@Override
							public void done(AVException arg0) {
								// TODO Auto-generated method stub
								if(arg0==null){
									mWallData.getWallObject().increment(WallInfoUpload.SUPPORT);
									mWallData.getWallObject().setFetchWhenSave(true);
									mWallData.getWallObject().saveInBackground(new SaveCallback(){
										@Override
										public void done(AVException arg0) {
											// TODO Auto-generated method stub
											if(arg0==null){
												WallSupport.add(mWallData.getWallObject().getObjectId());
												mWallData.setSupportCount(mWallData.getWallObject().getInt(WallInfoUpload.SUPPORT));
												//Log.v("supportnum",mWallInfoDownload.getWallObject().get)
												Log.v("numIncrease", "increase");
												mAdapter.notifyDataSetChanged();
												mSupportFlag=false;
											}else{
												Log.v("numIncrease", "decrease2");
												mWallData.decreaseSupportCount();
												mAdapter.notifyDataSetChanged();
												mSupportFlag=false;
											}
										}
										
									});
								}else{
									Log.v("numIncrease", "decrease1");
									mWallData.decreaseSupportCount();
									mAdapter.notifyDataSetChanged();
									mSupportFlag=false;
								}
							}
						});
				}
				break;
			case R.id.comment_linear:
				Toast.makeText(getApplicationContext(), "Comment", 1000).show();
				break;
			}
		}
		
	}
}
