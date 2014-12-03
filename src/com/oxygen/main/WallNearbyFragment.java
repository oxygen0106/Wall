package com.oxygen.main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.oxygen.data.WallInfoDownload;
import com.oxygen.data.WallInfoUpload;
import com.oxygen.data.WallSupport;
import com.oxygen.image.ImagePagerActivity;
import com.oxygen.image.LoaderListener;
import com.oxygen.map.GetLocation;
import com.oxygen.my.MyUserInfoActivity;
import com.oxygen.wall.R;
import com.oxygen.wall.WallCommentActivity;
import com.oxygen.wall.WallCurrent;
import com.oxygen.wall.WallListView;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @ClassName WallFragment
 * @Description 留言板
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-8-18 下午09:35:28
 */

public class WallNearbyFragment extends Fragment {

	private static final String TAG="WallNearbyFragment";
	private ArrayList<WallInfoDownload> mData;
	private ItemAdapter mAdapter;
	private WallListView mListView;
	private boolean mSupportFlag;
	private ArrayList<String> mDataID;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.wall_fragment, container);
		mListView=(WallListView)v.findViewById(R.id.wall_listview);
		return v;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.v(null, "WallFragment.onActivityCreatedBegin");
		super.onActivityCreated(savedInstanceState);
		init();
	}
	
	public void onResume() {
		super.onResume();
		mAdapter.notifyDataSetChanged();
	};
	private void init(){
		
		mData = new ArrayList<WallInfoDownload>();
		mDataID=new ArrayList<String>();
		mAdapter = new ItemAdapter(this.getActivity(), R.layout.wall_list_item);
		mListView.setAdapter(mAdapter);
		mListView.setonRefreshListener(new WallListView.OnRefreshListener() {
			public void onRefresh() {
				updateData();
			}
			public void onIncrease(){
				increaseData();
			}
		});
		
	}
	public void getData(){
		WallSupport.getSupports();
		updateData();
	}
	private void updateData() {

		AVQuery<AVObject> query = new AVQuery<AVObject>("WallInfo");
		double lantitude=GetLocation.mCurrentLantitude;
		double longitude=GetLocation.mCurrentLongitude;
		query.include(WallInfoUpload.USER);
		query.whereGreaterThan(WallInfoUpload.LATITUDE, lantitude-0.1);
		query.whereLessThan(WallInfoUpload.LATITUDE, lantitude+0.1);
		query.whereGreaterThan(WallInfoUpload.LONGITUDE, longitude-0.1);
		query.whereLessThan(WallInfoUpload.LONGITUDE, longitude+0.1);
		query.addDescendingOrder("createdAt");
		query.addDescendingOrder("supportCount");
		query.setLimit(10);
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		query.findInBackground(new FindCallback<AVObject>(){

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null){
					if(arg0!=null&&arg0.size()>0){
						mData=new ArrayList<WallInfoDownload>();
						AVObject object;
						for(int i=0;i<arg0.size();i++){
							object=arg0.get(i);
							AVUser user=(AVUser)object.get(WallInfoUpload.USER);
							String name=user.getUsername();
							Log.v("query name", name);
							WallInfoDownload wid=new WallInfoDownload(object,user);
							mData.add(wid);
							mDataID.add(wid.getWallID());
						}
					
						mAdapter.notifyDataSetChanged();
						if(mData.size()>=9&&!mListView.hasFootView)
							mListView.addFootView();
						else
							mListView.removeFootView();
					}else{
						Toast.makeText(getActivity(), "附近没有留言板", Toast.LENGTH_SHORT).show();
						Log.v("query", "zero~1");
					}
				}else{
					Toast.makeText(getActivity(), "请检查网络,然后下拉刷新", Toast.LENGTH_SHORT).show();
					Log.v("query", "error~2");
					Log.v("query", arg1.getMessage()+"~3");
				}
				mListView.onRefreshComplete();
			}
			
		});
	}

	private void increaseData(){
		AVQuery<AVObject> query = new AVQuery<AVObject>("WallInfo");
		double lantitude=GetLocation.mCurrentLantitude;
		double longitude=GetLocation.mCurrentLongitude;
		query.include(WallInfoUpload.USER);
		query.whereGreaterThan(WallInfoUpload.LATITUDE, lantitude-0.1);
		query.whereLessThan(WallInfoUpload.LATITUDE, lantitude+0.1);
		query.whereGreaterThan(WallInfoUpload.LONGITUDE, longitude-0.1);
		query.whereLessThan(WallInfoUpload.LONGITUDE, longitude+0.1);
		query.addDescendingOrder("createdAt");
		query.addDescendingOrder("supportCount");
		query.setLimit(10);
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		query.findInBackground(new FindCallback<AVObject>(){

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null){
					if(arg0!=null&&arg0.size()>0){
						AVObject object;
						int i=0;
						for(;i<arg0.size();i++){
							object=arg0.get(i);
							AVUser user=(AVUser)object.get(WallInfoUpload.USER);
							String name=user.getUsername();
							Log.v("query name", name);
							WallInfoDownload wid=new WallInfoDownload(object,user);
							mData.add(wid);
							mDataID.add(wid.getWallID());
						}
						mAdapter.notifyDataSetChanged();
						if(i<9)
							mListView.removeFootView();
					}else
						Log.v("query", "zero~4");
				}else{
					Toast.makeText(getActivity(), "请检查网络,然后下拉刷新", Toast.LENGTH_SHORT).show();
					Log.v("query", "error~5");
					Log.v("query", arg1.getMessage()+"~6");
					
				}
				mListView.onIncreaseComplete();
			}
			
		});
	}
	
	private class ItemAdapter extends BaseAdapter {
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
		}
		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			if (convertView == null) {
				v = mInflater.inflate(mResource, parent, false);
			} else {
				v = convertView;
			}
			
			bindView(position, v);
			return v;
		}

		private void bindView(int position, View view) {
			Log.v("bindView", String.valueOf(position));
			WallInfoDownload wid=mData.get(position);
			View authorContent=view.findViewById(R.id.author_content);
			authorContent.setTag(position);
			authorContent.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					WallInfoDownload w=mData.get((Integer) v.getTag());
					Intent intent=new Intent(mContext,MyUserInfoActivity.class);
					intent.putExtra("userID",w.getUserID());
					intent.putExtra("userName",w.getUserName());
					intent.putExtra("userImageUrl",w.getUserImageURL());
					startActivity(intent);
				}
			});
			TextView username = (TextView) view.findViewById(R.id.authorName);
			username.setText(wid.getUserName());
			ImageView image = (ImageView)view.findViewById(R.id.authorImage);
			if(wid.getUserImageURL()!=null){
				ImageLoader.getInstance().displayImage(wid.getUserImageURL(), image, options, animateFirstListener);
			}
			TextView posttime = (TextView) view.findViewById(R.id.post_time);
			posttime.setText(wid.getWallCreateTime());
			TextView distance = ((TextView) view.findViewById(R.id.distance));
			distance.setText("距离："+String.valueOf((double)(wid.getDistance())/1000.0)+"千米");
			TextView content = (TextView) view.findViewById(R.id.content);
			content.setText(wid.getWallContent());
			ImageView picture = (ImageView)view.findViewById(R.id.contentImage);
			ImageLoader.getInstance().displayImage(wid.getImageURL(), picture, options, animateFirstListener);
			TextView commentNum = (TextView) view.findViewById(R.id.comment_num);
			commentNum.setText(String.valueOf(wid.getCommentCount()));
			TextView supportNum = (TextView) view.findViewById(R.id.support_num);
			supportNum.setText(String.valueOf(wid.getSupportCount()));
			LinearLayout  support = (LinearLayout) view.findViewById(R.id.support_linear);
			if(WallSupport.isHave(wid.getWallID())){
				//support.setBackgroundColor(0x00ff00);
			}
			picture.setTag(position);
			picture.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					WallInfoDownload w=(WallInfoDownload)mData.get((Integer)v.getTag());
					
					Intent intent = new Intent(getActivity(),ImagePagerActivity.class);
					
					intent.putExtra("imageURL",w.getImageURL());
					
					startActivity(intent);
				}
			});
			support.setTag(position);
			support.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.v("Suport", "click");
					WallInfoDownload w=(WallInfoDownload)mData.get((Integer)v.getTag());
					if(!mSupportFlag&&WallSupport.isHave(w.getWallID())){
						mSupportFlag=true;
						WallSupport.delete(w.getWallObject().getObjectId());
						w.decreaseSupportCount();
						mAdapter.notifyDataSetChanged();
						AVUser user = AVUser.getCurrentUser();
						AVRelation<AVObject> relation = user.getRelation("Supports");
						relation.remove(w.getWallObject());
						user.saveInBackground(new WallSaveCallback(w){
							@Override
							public void done(AVException arg0) {
								// TODO Auto-generated method stub
								if(arg0==null){
									mWallInfoDownload.getWallObject().increment(WallInfoUpload.SUPPORT,-1);
									mWallInfoDownload.getWallObject().setFetchWhenSave(true);
									mWallInfoDownload.getWallObject().saveInBackground(new WallSaveCallback(mWallInfoDownload){
										@Override
										public void done(AVException arg0) {
											// TODO Auto-generated method stub
											if(arg0==null){
												WallSupport.delete(mWallInfoDownload.getWallObject().getObjectId());
												mWallInfoDownload.setSupportCount(mWallInfoDownload.getWallObject().getInt(WallInfoUpload.SUPPORT));
												Log.v("numdecrease", "decrease");
												mAdapter.notifyDataSetChanged();
												mSupportFlag=false;
											}else{
												mWallInfoDownload.increaseSupportCount();
												mAdapter.notifyDataSetChanged();
												Log.v("numdecrease", "increase2");
												mSupportFlag=false;
											}
										}
										
									});
								}else{
									mWallInfoDownload.increaseSupportCount();
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
						WallSupport.add(w.getWallObject().getObjectId());
						w.increaseSupportCount();
						mAdapter.notifyDataSetChanged();
						
						AVUser user = AVUser.getCurrentUser();
						AVRelation<AVObject> relation = user.getRelation("Supports");
						relation.add(w.getWallObject());
						user.saveInBackground(new WallSaveCallback(w){
							@Override
							public void done(AVException arg0) {
								// TODO Auto-generated method stub
								if(arg0==null){
									mWallInfoDownload.getWallObject().increment(WallInfoUpload.SUPPORT);
									mWallInfoDownload.getWallObject().setFetchWhenSave(true);
									mWallInfoDownload.getWallObject().saveInBackground(new WallSaveCallback(mWallInfoDownload){
										@Override
										public void done(AVException arg0) {
											// TODO Auto-generated method stub
											if(arg0==null){
												WallSupport.add(mWallInfoDownload.getWallObject().getObjectId());
												mWallInfoDownload.setSupportCount(mWallInfoDownload.getWallObject().getInt(WallInfoUpload.SUPPORT));
												//Log.v("supportnum",mWallInfoDownload.getWallObject().get)
												Log.v("numIncrease", "increase");
												mAdapter.notifyDataSetChanged();
												mSupportFlag=false;
											}else{
												Log.v("numIncrease", "decrease2");
												mWallInfoDownload.decreaseSupportCount();
												mAdapter.notifyDataSetChanged();
												mSupportFlag=false;
											}
										}
										
									});
								}else{
									Log.v("numIncrease", "decrease1");
									mWallInfoDownload.decreaseSupportCount();
									mAdapter.notifyDataSetChanged();
									mSupportFlag=false;
								}
							}
						});
					}
				}
			});
			LinearLayout  comment = (LinearLayout) view.findViewById(R.id.comment_linear);
			comment.setTag(position);
			comment.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					WallInfoDownload w=(WallInfoDownload)mData.get((Integer)v.getTag());
					Intent intent=new Intent(getActivity(),WallCommentActivity.class);
					WallCurrent.wid=w;
					startActivity(intent);
				}
			});
			view.setTag(position);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.v(null, "getTag");
					WallInfoDownload w=(WallInfoDownload)mData.get((Integer)v.getTag());
					Log.v(null, "AfterTag");
					Log.v("测试获取", String.valueOf(w.getDistance()));
					Intent intent=new Intent(getActivity(),WallCommentActivity.class);
					WallCurrent.wid=w;
					Log.v(null, "startActivity");
					startActivity(intent);
					Log.v("View", "click");
				}
			});
		}
	}
	
	private abstract class WallSaveCallback extends SaveCallback{
		protected WallInfoDownload mWallInfoDownload;
		public WallSaveCallback(WallInfoDownload w){
			mWallInfoDownload=w;
		}
	}
}
