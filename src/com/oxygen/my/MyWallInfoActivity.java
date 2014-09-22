package com.oxygen.my;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.oxygen.data.WallInfoDownload;
import com.oxygen.data.WallInfoUpload;
import com.oxygen.wall.R;
import com.oxygen.wall.WallCommentActivity;
import com.oxygen.wall.WallListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @ClassName WallFragment
 * @Description 留言板
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-8-18 下午09:35:28
 */

public class MyWallInfoActivity extends Activity {

	private ArrayList<WallInfoDownload> mData;
	private int mFirstItem;
	private int mLastItem;
	private ItemAdapter mAdapter;
	private WallListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.wall_fragment);
		mListView = (WallListView) findViewById(R.id.wall_listview);
		init();
	}

	private void init() {

		mData = new ArrayList<WallInfoDownload>();
		mAdapter = new ItemAdapter(this, R.layout.wall_list_item);
		// listview.setOnScrollListener(this);
		mListView.setAdapter(mAdapter);
		mListView.setonRefreshListener(new WallListView.OnRefreshListener() {
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							// getData();
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mListView.onRefreshComplete();
						mAdapter.notifyDataSetChanged();
						mListView.changeRefreshFlag();
					}

				}.execute();
			}

			public void onIncrease() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							getData();

						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						// mListView.onRefreshComplete();
						mAdapter.notifyDataSetChanged();
						mListView.changeIncreaseFlag();
					}

				}.execute();
			}
		});
	}

	private ArrayList<WallInfoDownload> getData() {
		final ArrayList<WallInfoDownload> list = new ArrayList<WallInfoDownload>();

		AVUser user = AVUser.getCurrentUser();
		AVQuery<AVObject> query = new AVQuery<AVObject>("WallInfo");
		query.include(WallInfoUpload.USER);
		query.whereEqualTo(WallInfoUpload.USER, user);
		query.addDescendingOrder("createdAt");

		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					if (arg0 != null && arg0.size() > 0) {
						AVObject object;
						for (int i = 0; i < arg0.size(); i++) {
							object = arg0.get(i);
							AVUser user = (AVUser) object
									.get(WallInfoUpload.USER);

							WallInfoDownload wid = new WallInfoDownload(object,
									user);
							mData.add(wid);
							new AsyncTask<Object, Void, Void>() {
								@Override
								protected Void doInBackground(Object... params) {
									// TODO Auto-generated method stub
									try {
										WallInfoDownload wid = (WallInfoDownload) params[0];
										Log.v("getImage", "beginaaa");

										wid.setUserBitmap(BitmapFactory
												.decodeStream(new URL(wid
														.getUserImageURL())
														.openStream()));

										Log.v("getImage", "done");

									} catch (MalformedURLException e) {
										// TODO Auto-generated catch block
										Log.v("getImage", "error");
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										Log.v("getImage", "error");
										e.printStackTrace();
									}
									return null;
								}

								@Override
								protected void onPostExecute(Void result) {
									// TODO Auto-generated method stub
									super.onPostExecute(result);
									mAdapter.notifyDataSetChanged();
								}
							}.execute(wid);
						}
					} else
						Log.v("query", "zero");
				} else {
					Log.v("query", "error");
					Log.v("query", arg1.getMessage());
				}
			}

		});

		/*
		 * WallInfoDownload wallInfo=new WallInfoDownload(); list.add(wallInfo);
		 * 
		 * wallInfo=new WallInfoDownload(); list.add(wallInfo);
		 * 
		 * wallInfo=new WallInfoDownload(); list.add(wallInfo);
		 */

		return list;
	}

	private class ItemAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private int mResource;
		private Context mContext;

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
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
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
			WallInfoDownload wid = mData.get(position);
			TextView username = (TextView) view.findViewById(R.id.authorName);
			username.setText(wid.getUserName());
			ImageView image = (ImageView) view.findViewById(R.id.authorImage);
			if (wid.getUserBitmap() != null)
				image.setImageBitmap(wid.getUserBitmap());
			TextView posttime = (TextView) view.findViewById(R.id.post_time);
			posttime.setText(wid.getmWallCreateTime());
			TextView distance = ((TextView) view.findViewById(R.id.distance));
			distance.setText(String.valueOf(wid.getDistance()));
			TextView content = (TextView) view.findViewById(R.id.content);
			content.setText(wid.getWallContent());

			LinearLayout support = (LinearLayout) view
					.findViewById(R.id.support_linear);

			support.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.v("Suport", "click");
				}
			});
			view.setTag(position);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					WallInfoDownload w = (WallInfoDownload) mData
							.get((Integer) v.getTag());
					Intent intent = new Intent(getApplicationContext(),
							WallCommentActivity.class);
					intent.putExtra("Info", w);
					startActivity(intent);
				}
			});
		}
	}

}
