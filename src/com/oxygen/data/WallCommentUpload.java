package com.oxygen.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.oxygen.wall.WallCurrent;

public class WallCommentUpload {
	private String mComment;
	private Context context;
	
	public static final String USER="user";
	public static final String WALL="wall";
	public static final String COMMENT="comment";
	
	public WallCommentUpload(Context c,String comment){
		context=c;
		mComment=comment;
		upload();
	}
	
	private  void upload(){
		AVObject comment=new AVObject("WallComment");
		comment.put(COMMENT, mComment);
		AVUser user=AVUser.getCurrentUser();
		comment.put(USER, user);
		comment.put(WALL, WallCurrent.wid.getWallObject());
		comment.saveInBackground(new SaveCallback(){

			@Override
			public void done(AVException arg0) {
				// TODO Auto-generated method stub
				if(arg0==null){
					AVObject object=WallCurrent.wid.getWallObject();
					object.setFetchWhenSave(true);
					object.increment(WallInfoUpload.COMMENT);
					object.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(AVException arg0) {
							// TODO Auto-generated method stub
							if(arg0==null){
								Toast.makeText(context, "发表成功", Toast.LENGTH_SHORT).show();
									//WallCurrent.wid.increaseCommentCount();
								}
							
						}
					});
					
				}	
			}
		});
	}
}
