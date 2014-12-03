package com.oxygen.explore;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * @ClassName Shake
 * @Description 摇一摇监听
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-9-21 上午11:01:35
 * 
 * 记得退出或是返回时，取消注册传感器监听,调用stopShakeListener()
 * 
 */
public class Shake implements SensorEventListener {

	private Context context;
	private ShakeListener shakeListener;
	private SensorManager sensorManager = null;// 传感器管理
	private Vibrator vibrator = null;// 震动器
	private int MSG_SHAKE_OK = 1;
	private int MSG_SHAKE_FAIL = 0;
	private ProgressDialog dialog;
	private boolean flag;
	public Shake(Context context) {
		this.context = context;
	}

	public void setShakeListener(ShakeListener shakeListener) {
		this.shakeListener = shakeListener;
		sensorManager = (SensorManager) context
				.getSystemService(Service.SENSOR_SERVICE);
		vibrator = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		flag=true;
	}

	public void stopShakeListener(){
		if(flag)
			sensorManager.unregisterListener(this);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		// 当传感器精度改变时回调该方法，Do nothing.
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		int sensorType = event.sensor.getType();
		// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
		float[] values = event.values;
		if (sensorType == Sensor.TYPE_ACCELEROMETER) {
			if ((Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math
					.abs(values[2]) > 17)) {

				dialog = new ProgressDialog(context);
				dialog.setMessage("请稍候...");
				Window window = dialog.getWindow();
				WindowManager.LayoutParams lp = window.getAttributes();
				lp.alpha = 0.7f;
				lp.dimAmount = 0.8f;
				window.setAttributes(lp);
				dialog.show();

				Thread td = new Thread(mThread);
				td.start();

				vibrator.vibrate(500); // 摇动手机后，再伴随震动提示~~
			}

		}

	}

	Runnable mThread = new Runnable() {
		public void run() {
			synchronized (this) {// 同步，防止传感器和按钮争夺资源

				// TODO 网络线程
				shakeListener.onShakeListener();// 回调方法
				
				Message m = Message.obtain();
				m.what = MSG_SHAKE_OK;
				mHandler.sendMessage(m);
			}
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_SHAKE_OK) {//摇一摇成功
				dialog.dismiss();
			} else if (msg.what == MSG_SHAKE_FAIL) {
				dialog.dismiss();
				Toast.makeText(context, "很抱歉，摇一摇失败", Toast.LENGTH_LONG).show();
			} else {
				dialog.dismiss();
				Toast.makeText(context, "再试一次", Toast.LENGTH_LONG).show();

			}
		}
	};

	public interface ShakeListener {
		public void onShakeListener();
	}
}
