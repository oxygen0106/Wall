package com.oxygen.data;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;

public class WallSupport {
	public static boolean isDone;
	private static  ArrayList<String> wall=new ArrayList<String>();
	//private static boolean isGetWallSupport;
	public synchronized static void add(String id){
		if(wall==null)
			wall=new ArrayList<String>();
		if(!wall.contains(id))
			wall.add(id);
	}
	public synchronized  static void delete(String id){
		if(wall!=null)
			wall.remove(id);
	}
	public static ArrayList<String> getList(){
		return wall;
	}
	public synchronized static boolean isHave(String id){
		return wall.contains(id);
	}
	public static void getSupports(){
		AVUser currentUser=AVUser.getCurrentUser();
		if(!isDone&&currentUser!=null){
		AVRelation relation=currentUser.getRelation("Supports");
		relation.setTargetClass("WallInfo");
		
		if(relation==null){
			isDone=true;
			return;
		}
		AVQuery<AVObject> supports = relation.getQuery();
		Log.v("user","3");
		supports.findInBackground(new FindCallback<AVObject>(){
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null&&arg0!=null){
					for(AVObject b:arg0){
						WallSupport.add(b.getObjectId());
					}
					isDone=true;
				}
			}
		});
	}
	}
}
