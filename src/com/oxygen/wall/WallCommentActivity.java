package com.oxygen.wall;

import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.oxygen.data.WallCommentDownload;
import com.oxygen.data.WallInfoDownload;
import com.oxygen.main.MainActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

public class WallCommentActivity extends Activity{
	
	
	private WallCommentListView mListView;
	private ItemAdapter mAdapter;
	private WallInfoDownload mWallData;
	private ArrayList<WallCommentDownload> mCommentData;
	private ClickListener mClickListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.v(null, "开始");
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 自定义标题栏模式
		setContentView(R.layout.wall_comment_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.wall_comment_title_bar);// 加载自定义标题栏
		Log.v(null, "获取Bundle");
		
		
		Bundle b=getIntent().getExtras();
		mWallData=(WallInfoDownload)b.get("Info");
		Log.v(null, "绑定数据");
	
		mClickListener=new ClickListener();
		mCommentData=new ArrayList<WallCommentDownload>();
		addComment();
		mAdapter = new ItemAdapter(this,R.layout.wall_comment_item);
		mListView = (WallCommentListView)findViewById(R.id.comment_list);
		mListView.setAdapter(mAdapter);
		
		mListView.setonRefreshListener(new WallCommentListView.OnRefreshListener() {
			public void onIncrease(){
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(10000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						addComment();
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						//mListView.onRefreshComplete();
						mAdapter.notifyDataSetChanged();
						mListView.changeIncreaseFlag();
					}

				}.execute();
			}
		});
	}
	
	public void addComment(){
		/*String wallID=mWallData.getWallID();
		AVQuery<AVObject> query= new AVQuery<AVObject>("WallCommentInfo");
		query.whereEqualTo("wallID", wallID);
		query.orderByAscending("createdAt");
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null){
					
				}else{
					
				}
			}
		});
		*/
		
		WallCommentDownload w=new WallCommentDownload();
		mCommentData.add(w);
		w=new WallCommentDownload();
		mCommentData.add(w);
		w=new WallCommentDownload();
		mCommentData.add(w);
		w=new WallCommentDownload();
		mCommentData.add(w);
		w=new WallCommentDownload();
		mCommentData.add(w);
		w=new WallCommentDownload();
		mCommentData.add(w);
		w=new WallCommentDownload();
		mCommentData.add(w);
		w=new WallCommentDownload();
		mCommentData.add(w);
		w=new WallCommentDownload();
		mCommentData.add(w);
		w=new WallCommentDownload();
		mCommentData.add(w);
		w=new WallCommentDownload();
		mCommentData.add(w);
		w=new WallCommentDownload();
		mCommentData.add(w);
		w=new WallCommentDownload();
		mCommentData.add(w);
	}
	public void clickBack(View v){
		this.finish();
	}
	
	private class ItemAdapter extends BaseAdapter{
		
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
			return mCommentData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
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
			View v;
			if(position==0){
				v=mInflater.inflate(R.layout.wall_list_item, null);
				TextView t=(TextView)v.findViewById(R.id.distance);
				LinearLayout comment=(LinearLayout)v.findViewById(R.id.comment_linear);
				comment.setOnClickListener(mClickListener);
				LinearLayout share=(LinearLayout)v.findViewById(R.id.share_linear);
				share.setOnClickListener(mClickListener);
				LinearLayout support=(LinearLayout)v.findViewById(R.id.support_linear);
				support.setOnClickListener(mClickListener);
			}else {
				Log.v(null, "position!=0");
				Log.v(null, convertView.toString());
				if (convertView==null||convertView.getTag()==null) {
					Log.v(null, "getView convertView==null");
					v = mInflater.inflate(mResource, parent, false);
				} else {
					v = convertView;
				}
				Log.v("position", String.valueOf(position));
				Log.v("datasize", String.valueOf(mCommentData.size()));
				bindView(position, v);
			}
			return v;
		}
		private void bindView(int position, View view) {
			TextView commenterName = ((TextView) view.findViewById(R.id.commenter_name));
			commenterName.setText(mCommentData.get(position).getCommenterName());
			TextView commentRank = (TextView) view.findViewById(R.id.comment_rank);
			commentRank.setText(String.valueOf(position));
			//添加头像
			//ImageView commenterImage = ((ImageView) view.findViewById(R.id.commenterImage));
			TextView commentContent = (TextView) view.findViewById(R.id.comment_content);
			commentContent.setText(mCommentData.get(position).getCommentContent());
			TextView commentCreateTime = (TextView) view.findViewById(R.id.comment_time);
			commentCreateTime.setText(mCommentData.get(position).getCommentCreateTime());
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
				break;
			case R.id.comment_linear:
				Toast.makeText(getApplicationContext(), "Comment", 1000).show();
				break;
			}
		}
		
	}
}
