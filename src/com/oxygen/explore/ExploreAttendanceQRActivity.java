package com.oxygen.explore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.google.zxing.WriterException;
import com.oxygen.wall.R;
import com.oxygen.zxing.EncodingHandler;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ExploreAttendanceQRActivity extends Activity implements
		OnClickListener {

	private TextView titleText;
	private ImageView backBtn;
	private TextView timeSetting;

	private ImageView imageQR;
	private TextView contentText;

	TimePickerDialog dialog;
	private int mHour;
	private int mMinute;

	private int waitTime = 2000;// 守护进程等待时间
	private String avobjectId;
	private String currentUserID;
	private String QRstring;
	private AVObject avo;
	private int signUpCount = 0;
	private boolean isSettingTime = false;
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd-hhmm");
	private String time;
	private String code;
	private List signerList = new ArrayList<String>();

	private final int UPDATE_QR = 1;
	private final int UPDATE_FAIL = 0;
	private final int TIME_OVER = -1;
	
	private TextView timeOverText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.explore_attendance_create);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.explore_attendance_create_title_bar);

		titleText = (TextView) findViewById(R.id.explore_attendance_create_tv);
		backBtn = (ImageView) findViewById(R.id.explore_attendance_create_back_btn);
		timeSetting = (TextView) findViewById(R.id.explore_attendance_create_time_setting);
		titleText.setText("签到处");

		imageQR = (ImageView) findViewById(R.id.explore_attendance_create_QR);
		contentText = (TextView) findViewById(R.id.explore_attendance_create_content_tv);
		timeOverText = (TextView)findViewById(R.id.explore_attendance_create_time_over_tv);

		imageQR.setOnClickListener(this);
		contentText.setOnClickListener(this);
		timeSetting.setOnClickListener(this);

		setTime();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.explore_attendance_create_content_tv:
			break;
		case R.id.explore_attendance_create_QR:
			break;
		case R.id.explore_attendance_create_time_setting:
			setTime();
			break;
		default:
			break;
		}
	}

	/**
	 * @param
	 * @return void
	 * @Description 设置结束时间
	 */
	private void setTime() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		TimePickerDialog dialog = new TimePickerDialog(this,
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker arg0, int hour, int minute) {
						mHour = hour;
						mMinute = minute;
						isSettingTime = true;
						startQR();// 设置结束时间后开始生成第一个QR
					}
				}, hour, minute, true);
		dialog.setTitle("结束时间");
		dialog.show();
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UPDATE_QR) {
				generateQRCode();
				new Thread(new holdThread()).start();// 生成下一个QR
			} else if (msg.what == TIME_OVER) {
				Toast.makeText(ExploreAttendanceQRActivity.this, "签到结束",
						Toast.LENGTH_LONG).show();
				timeOverText.setVisibility(View.VISIBLE);
			} else {

			}
		};
	};

	/**
	 * @param
	 * @return void
	 * @Description 生成二维码
	 */
	private void generateQRCode() {

		if (isTimeOver() == false) {
			long date = System.currentTimeMillis();
			code = avobjectId + date;
			if (!code.equals("") && !avobjectId.equals("")) {
				Bitmap qrCodeBitmap;
				try {
					qrCodeBitmap = EncodingHandler.createQRCode(code, 350);
					imageQR.setImageBitmap(qrCodeBitmap);

				} catch (WriterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Toast.makeText(ExploreAttendanceQRActivity.this,
						"服务器异常，请稍后重试！", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * @ClassName RefreshQR
	 * @Description 通知主线程更新UI
	 * @author oxygen
	 * @email oxygen0106@163.com
	 * @date 2014-9-26 下午3:52:16
	 */
	private class RefreshQR implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (isTimeOver()) {
				Message m = Message.obtain();
				m.what = TIME_OVER;
				mHandler.sendMessage(m);
			} else {
				Message m = Message.obtain();
				m.what = UPDATE_QR;
				mHandler.sendMessage(m);
			}
		}
	}

	/**
	 * @param
	 * @return void
	 * @Description 更新当前item
	 */
	private void getAVObject() {
		AVQuery<AVObject> query = new AVQuery<AVObject>("Attendance");
		query.getInBackground(avobjectId, new GetCallback<AVObject>() {

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					avo = arg0;
					
				}
			}
		});
	}

	/**
	 * @param @return
	 * @return boolean
	 * @Description 是否到达结束时间
	 */
	private boolean isTimeOver() {
		if (isSettingTime) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(System.currentTimeMillis());
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			if (hour >= mHour && minute >= mMinute) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	* @param 
	* @return void
	* @Description 首次启动QR并开启更新线程  
	*/
	private void startQR() {
		AVUser currentUser = AVUser.getCurrentUser();
		AVQuery<AVObject> query = new AVQuery<AVObject>("Attendance");
		query.whereEqualTo("user", currentUser);
		query.addDescendingOrder("createdAt");
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					avo = arg0.get(0);
					avobjectId = avo.getObjectId();
					new Thread(new RefreshQR()).start();
				}
			}
		});
	}

	private class holdThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			getAVObject();
			while (avo.getList("userList") == null
					|| avo.getList("userList").size() == signUpCount) {// 当签到名单人数与本地端相等时
				// 查询数据库
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getAVObject();
			}
			signUpCount = avo.getList("userList").size();
			new Thread(new RefreshQR()).start();
		}
	}
}
