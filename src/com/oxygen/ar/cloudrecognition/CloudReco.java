/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.oxygen.ar.cloudrecognition;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.ImageTracker;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.TargetFinder;
import com.qualcomm.vuforia.TargetSearchResult;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.Vuforia;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.oxygen.ar.utils.ApplicationControl;
import com.oxygen.ar.utils.ApplicationException;
import com.oxygen.ar.utils.ApplicationSession;
import com.oxygen.ar.utils.LoadingDialogHandler;
import com.oxygen.ar.utils.ApplicationGLView;
import com.oxygen.ar.utils.Texture;
import com.oxygen.data.WallInfoDownload;
import com.oxygen.data.WallInfoUpload;
import com.oxygen.explore.Shake;
import com.oxygen.explore.Shake.ShakeListener;
import com.oxygen.wall.R;
import com.oxygen.wall.WallCommentActivity;
import com.oxygen.wall.WallCommentCreate;
import com.oxygen.wall.WallCurrent;

// The main activity for the CloudReco sample. 
@SuppressLint("NewApi")
public class CloudReco extends Activity implements ApplicationControl {
	private static final String LOGTAG = "CloudReco";

	ApplicationSession vuforiaAppSession;

	// These codes match the ones defined in TargetFinder in Vuforia.jar
	static final int INIT_SUCCESS = 2;
	static final int INIT_ERROR_NO_NETWORK_CONNECTION = -1;
	static final int INIT_ERROR_SERVICE_NOT_AVAILABLE = -2;
	static final int UPDATE_ERROR_AUTHORIZATION_FAILED = -1;
	static final int UPDATE_ERROR_PROJECT_SUSPENDED = -2;
	static final int UPDATE_ERROR_NO_NETWORK_CONNECTION = -3;
	static final int UPDATE_ERROR_SERVICE_NOT_AVAILABLE = -4;
	static final int UPDATE_ERROR_BAD_FRAME_QUALITY = -5;
	static final int UPDATE_ERROR_UPDATE_SDK = -6;
	static final int UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE = -7;
	static final int UPDATE_ERROR_REQUEST_TIMEOUT = -8;

	static final int HIDE_LOADING_DIALOG = 0;
	static final int SHOW_LOADING_DIALOG = 1;

	// Our OpenGL view:
	private ApplicationGLView mGlView;

	// Our renderer:
	private CloudRecoRenderer mRenderer;

	private boolean mContAutofocus = true;
	private boolean mExtendedTracking = false;
	boolean mFinderStarted = false;
	boolean mStopFinderIfStarted = false;

	// The textures we will use for rendering:
	private Vector<Texture> mTextures;

	private static final String kAccessKey = "a1168f4bacf84d8891477bd084af6dc85474a96a";
	private static final String kSecretKey = "1cf55cabcc8305c4324ee52b74af42b64b16fc48";

	// View overlays to be displayed in the Augmented View
	private RelativeLayout mUILayout;

	// Error message handling:
	private int mlastErrorCode = 0;
	private int mInitErrorCode = 0;
	private boolean mFinishActivityOnError;

	// Alert Dialog used to display SDK errors
	private AlertDialog mErrorDialog;

	private GestureDetector mGestureDetector;

	private LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(
			this);

	private double mLastErrorTime;

	boolean mIsDroidDevice = false;

	private Shake shake;

	private CloudShakeListener mShakeListener;
	private Toast toast;
	private boolean hasWallInfo;
	private String wallID;
	  
	// Called when the activity first starts or needs to be recreated after
	// resuming the application or a configuration change.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(LOGTAG, "onCreate");
		super.onCreate(savedInstanceState);

		vuforiaAppSession = new ApplicationSession(this);
		Intent intent = getIntent();
		hasWallInfo = intent.getBooleanExtra("hasWallInfo", false);
		startLoadingAnimation();

		vuforiaAppSession
				.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Creates the GestureDetector listener for processing double tap
		mGestureDetector = new GestureDetector(this, new GestureListener());

		mTextures = new Vector<Texture>();
		loadTextures();

		mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith(
				"droid");

		shake = new Shake(CloudReco.this);
		mShakeListener = new CloudShakeListener();
		toast = Toast.makeText(this, "晃动手机，捕捉该墙进行评论！", Toast.LENGTH_LONG);
	}

	private double[] readVerts(){
		AssetManager am=getAssets();
		
		//double [] TEAPOT = new double[27846];
		
		//double [] TEAPOT =  new double[30000];
		//double [] TEAPOT=new double[2472];
		double [] TEAPOT = new double[16056];
   		 try {
				InputStreamReader read = new InputStreamReader((am.open("Verts4.txt")));
				BufferedReader bufferedReader = new BufferedReader(read);
               String lineTxt = null;
               int i=0;
               int num=0;
               while((lineTxt = bufferedReader.readLine()) != null){
            	Log.v("Teapot", "Read verts"+i);
               	if(i%4!=0){
               		//System.out.println(lineTxt);
               		String [] a= lineTxt.split(",");
               		for(int j=0;j<a.length;j++){
               			TEAPOT[num]=Double.valueOf(a[j]);
               			num++;
               		}
               	}
               	i++;
               }
               read.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return TEAPOT;
	}
	
	private double[] readNorms(){
		AssetManager am=getAssets();
		//double [] TEAPOT = new double[27846];
		//double [] TEAPOT =  new double[30000];
		//double [] TEAPOT=new double[2472];
		double [] TEAPOT = new double[16056];
   		 try {
				InputStreamReader read = new InputStreamReader((am.open("Norms4.txt")));
				BufferedReader bufferedReader = new BufferedReader(read);
               String lineTxt = null;
               int i=0;
               int num=0;
               while((lineTxt = bufferedReader.readLine()) != null){
            	   Log.v("Teapot", "Read normss"+i);
               	if(i%4!=0){
               		//System.out.println(lineTxt);
               		String [] a= lineTxt.split(",");
               		for(int j=0;j<a.length;j++){
               			TEAPOT[num]=Double.valueOf(a[j]);
               			num++;
               		}
               	}
               	i++;
               }
               read.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return TEAPOT;
	}
	private double[] readCoords(){
		AssetManager am=getAssets();
		//double [] TEAPOT =  new double[18564];
	//	double [] TEAPOT =  new double[20000];
		//double [] TEAPOT=new double[1648];
		double [] TEAPOT =  new double[10704];
   		 try {
   			InputStreamReader read = new InputStreamReader((am.open("Coords4.txt")));
			BufferedReader bufferedReader = new BufferedReader(read);
               String lineTxt = null;
               int i=0;
               int num=0;
               while((lineTxt = bufferedReader.readLine()) != null){
            	   Log.v("Teapot", "Read coords"+i);
               	if(i%4!=0){
               		//System.out.println(lineTxt);
               		String [] a= lineTxt.split(",");
               		for(int j=0;j<a.length;j++){
               			TEAPOT[num]=Double.valueOf(a[j]);
               			num++;
               		}
               	}
               	i++;
               }
               read.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return TEAPOT;
	}
	// Process Single Tap event to trigger autofocus
	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {
		// Used to set autofocus one second after a manual focus is triggered
		private final Handler autofocusHandler = new Handler();

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// Generates a Handler to trigger autofocus
			// after 1 second
			autofocusHandler.postDelayed(new Runnable() {
				public void run() {
					boolean result = CameraDevice.getInstance().setFocusMode(
							CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);

					if (!result)
						Log.e("SingleTapUp", "Unable to trigger focus");
				}
			}, 1000L);

			return true;
		}
	}

	// We want to load specific textures from the APK, which we will later use
	// for rendering.
	private void loadTextures() {
		Log.d(LOGTAG, "loadTextures");
		mTextures.add(Texture.loadTextureFromApk("Wall.png",
				getAssets()));
		Log.v(LOGTAG, mTextures.toString());
	}

	// Called when the activity will start interacting with the user.
	@Override
	protected void onResume() {
		Log.d(LOGTAG, "onResume");
		super.onResume();

		// This is needed for some Droid devices to force portrait
		if (mIsDroidDevice) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		try {
			vuforiaAppSession.resumeAR();
		} catch (ApplicationException e) {
			Log.e(LOGTAG, e.getString());
		}

		// Resume the GL view:
		if (mGlView != null) {
			mGlView.setVisibility(View.VISIBLE);
			mGlView.onResume();
		}

	}

	// Callback for configuration changes the activity handles itself
	@Override
	public void onConfigurationChanged(Configuration config) {
		Log.d(LOGTAG, "onConfigurationChanged");
		super.onConfigurationChanged(config);

		vuforiaAppSession.onConfigurationChanged();
	}

	// Called when the system is about to start resuming a previous activity.
	@Override
	protected void onPause() {
		Log.d(LOGTAG, "onPause");
		super.onPause();

		try {
			vuforiaAppSession.pauseAR();
		} catch (ApplicationException e) {
			Log.e(LOGTAG, e.getString());
		}

		// Pauses the OpenGLView
		if (mGlView != null) {
			mGlView.setVisibility(View.INVISIBLE);
			mGlView.onPause();
		}
	}

	// The final call you receive before your activity is destroyed.
	@Override
	protected void onDestroy() {
		Log.d(LOGTAG, "onDestroy");
		super.onDestroy();

		try {
			vuforiaAppSession.stopAR();
		} catch (ApplicationException e) {
			Log.e(LOGTAG, e.getString());
		}

		System.gc();
	}

	public void deinitCloudReco() {
		Log.d(LOGTAG, "deinitCloudReco");
		// Get the image tracker:
		TrackerManager trackerManager = TrackerManager.getInstance();
		ImageTracker imageTracker = (ImageTracker) trackerManager
				.getTracker(ImageTracker.getClassType());
		if (imageTracker == null) {
			Log.e(LOGTAG,
					"Failed to destroy the tracking data set because the ImageTracker has not"
							+ " been initialized.");
			return;
		}

		// Deinitialize target finder:
		TargetFinder finder = imageTracker.getTargetFinder();
		finder.deinit();
	}

	private void startLoadingAnimation() {
		Log.d(LOGTAG, "startLoadingAnimation");
		// Inflates the Overlay Layout to be displayed above the Camera View
		LayoutInflater inflater = LayoutInflater.from(this);
		mUILayout = (RelativeLayout) inflater.inflate(R.layout.camera_overlay,
				null, false);

		mUILayout.setVisibility(View.VISIBLE);
		mUILayout.setBackgroundColor(Color.BLACK);

		// By default
		loadingDialogHandler.mLoadingDialogContainer = mUILayout
				.findViewById(R.id.loading_indicator);
		loadingDialogHandler.mLoadingDialogContainer
				.setVisibility(View.VISIBLE);

		addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

	}

	// Initializes AR application components.
	private void initApplicationAR() {
		Log.d(LOGTAG, "initApplicationAR");
		// Create OpenGL ES view:
		int depthSize = 16;
		int stencilSize = 0;
		boolean translucent = Vuforia.requiresAlpha();

		// Initialize the GLView with proper flags
		mGlView = new ApplicationGLView(this);
		mGlView.init(translucent, depthSize, stencilSize);

		// Setups the Renderer of the GLView
		mRenderer = new CloudRecoRenderer(vuforiaAppSession, this,readVerts(),readNorms(),readCoords());
		Log.v("Teapot", "renderDone");
		mRenderer.setTextures(mTextures);
		mGlView.setRenderer(mRenderer);

	}

	// Returns the error message for each error code
	private String getStatusDescString(int code) {
		Log.d(LOGTAG, "getStatusDescString");
		if (code == UPDATE_ERROR_AUTHORIZATION_FAILED)
			return getString(R.string.UPDATE_ERROR_AUTHORIZATION_FAILED_DESC);
		if (code == UPDATE_ERROR_PROJECT_SUSPENDED)
			return getString(R.string.UPDATE_ERROR_PROJECT_SUSPENDED_DESC);
		if (code == UPDATE_ERROR_NO_NETWORK_CONNECTION)
			return getString(R.string.UPDATE_ERROR_NO_NETWORK_CONNECTION_DESC);
		if (code == UPDATE_ERROR_SERVICE_NOT_AVAILABLE)
			return getString(R.string.UPDATE_ERROR_SERVICE_NOT_AVAILABLE_DESC);
		if (code == UPDATE_ERROR_UPDATE_SDK)
			return getString(R.string.UPDATE_ERROR_UPDATE_SDK_DESC);
		if (code == UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE)
			return getString(R.string.UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE_DESC);
		if (code == UPDATE_ERROR_REQUEST_TIMEOUT)
			return getString(R.string.UPDATE_ERROR_REQUEST_TIMEOUT_DESC);
		if (code == UPDATE_ERROR_BAD_FRAME_QUALITY)
			return getString(R.string.UPDATE_ERROR_BAD_FRAME_QUALITY_DESC);
		else {
			return getString(R.string.UPDATE_ERROR_UNKNOWN_DESC);
		}
	}

	// Returns the error message for each error code
	private String getStatusTitleString(int code) {
		Log.d(LOGTAG, "getStatusTitleString");
		if (code == UPDATE_ERROR_AUTHORIZATION_FAILED)
			return getString(R.string.UPDATE_ERROR_AUTHORIZATION_FAILED_TITLE);
		if (code == UPDATE_ERROR_PROJECT_SUSPENDED)
			return getString(R.string.UPDATE_ERROR_PROJECT_SUSPENDED_TITLE);
		if (code == UPDATE_ERROR_NO_NETWORK_CONNECTION)
			return getString(R.string.UPDATE_ERROR_NO_NETWORK_CONNECTION_TITLE);
		if (code == UPDATE_ERROR_SERVICE_NOT_AVAILABLE)
			return getString(R.string.UPDATE_ERROR_SERVICE_NOT_AVAILABLE_TITLE);
		if (code == UPDATE_ERROR_UPDATE_SDK)
			return getString(R.string.UPDATE_ERROR_UPDATE_SDK_TITLE);
		if (code == UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE)
			return getString(R.string.UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE_TITLE);
		if (code == UPDATE_ERROR_REQUEST_TIMEOUT)
			return getString(R.string.UPDATE_ERROR_REQUEST_TIMEOUT_TITLE);
		if (code == UPDATE_ERROR_BAD_FRAME_QUALITY)
			return getString(R.string.UPDATE_ERROR_BAD_FRAME_QUALITY_TITLE);
		else {
			return getString(R.string.UPDATE_ERROR_UNKNOWN_TITLE);
		}
	}

	// Shows error messages as System dialogs
	public void showErrorMessage(int errorCode, double errorTime,
			boolean finishActivityOnError) {
		// Log.d(LOGTAG, "showErrorMessage");
		if (errorTime < (mLastErrorTime + 5.0) || errorCode == mlastErrorCode)
			return;

		mlastErrorCode = errorCode;
		mFinishActivityOnError = finishActivityOnError;

		runOnUiThread(new Runnable() {
			public void run() {
				if (mErrorDialog != null) {
					mErrorDialog.dismiss();
				}

				// Generates an Alert Dialog to show the error message
				AlertDialog.Builder builder = new AlertDialog.Builder(
						CloudReco.this);
				builder.setMessage(
						getStatusDescString(CloudReco.this.mlastErrorCode))
						.setTitle(
								getStatusTitleString(CloudReco.this.mlastErrorCode))
						.setCancelable(false)
						.setIcon(0)
						.setPositiveButton(getString(R.string.button_OK),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										if (mFinishActivityOnError) {
											finish();
										} else {
											dialog.dismiss();
										}
									}
								});

				mErrorDialog = builder.create();
				mErrorDialog.show();
			}
		});
	}

	public void startFinderIfStopped() {
		// Log.d(LOGTAG, "startFinderIfStopped");
		if (!mFinderStarted) {
			Log.d(LOGTAG, "startFinderIfStopped_in");
			mFinderStarted = true;

			// Get the image tracker:
			TrackerManager trackerManager = TrackerManager.getInstance();
			ImageTracker imageTracker = (ImageTracker) trackerManager
					.getTracker(ImageTracker.getClassType());

			// Initialize target finder:
			TargetFinder targetFinder = imageTracker.getTargetFinder();

			targetFinder.clearTrackables();
			targetFinder.startRecognition();
			toast.cancel();
			shake.stopShakeListener();
		}
	}

	public void stopFinderIfStarted() {
		// Log.d(LOGTAG, "stopFinderIfStarted");
		if (mFinderStarted) {
			mFinderStarted = false;
			Log.d(LOGTAG, "stopFinderIfStarted_in");
			// Get the image tracker:
			TrackerManager trackerManager = TrackerManager.getInstance();
			ImageTracker imageTracker = (ImageTracker) trackerManager
					.getTracker(ImageTracker.getClassType());

			// Initialize target finder:
			TargetFinder targetFinder = imageTracker.getTargetFinder();

			targetFinder.stop();
			toast.show();
			shake.setShakeListener(mShakeListener);

		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean doLoadTrackersData() {

		Log.d(LOGTAG, "initCloudReco");

		// Get the image tracker:
		TrackerManager trackerManager = TrackerManager.getInstance();
		ImageTracker imageTracker = (ImageTracker) trackerManager
				.getTracker(ImageTracker.getClassType());

		// Initialize target finder:
		TargetFinder targetFinder = imageTracker.getTargetFinder();

		// Start initialization:
		if (targetFinder.startInit(kAccessKey, kSecretKey)) {
			targetFinder.waitUntilInitFinished();
		}

		int resultCode = targetFinder.getInitState();
		if (resultCode != TargetFinder.INIT_SUCCESS) {
			if (resultCode == TargetFinder.INIT_ERROR_NO_NETWORK_CONNECTION) {
				mInitErrorCode = UPDATE_ERROR_NO_NETWORK_CONNECTION;
			} else {
				mInitErrorCode = UPDATE_ERROR_SERVICE_NOT_AVAILABLE;
			}

			Log.e(LOGTAG, "Failed to initialize target finder.");
			return false;
		}

		// Use the following calls if you would like to customize the color of
		// the UI
		// targetFinder->setUIScanlineColor(1.0, 0.0, 0.0);
		// targetFinder->setUIPointColor(0.0, 0.0, 1.0);

		return true;
	}

	@Override
	public boolean doUnloadTrackersData() {
		Log.d(LOGTAG, "doUnloadTrackersData");
		return true;
	}

	@Override
	public void onInitARDone(ApplicationException exception) {
		Log.d(LOGTAG, "onInitARDone");
		if (exception == null) {
			initApplicationAR();

			// Now add the GL surface view. It is important
			// that the OpenGL ES surface view gets added
			// BEFORE the camera is started and video
			// background is configured.
			addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));

			// Start the camera:
			try {
				vuforiaAppSession.startAR(CameraDevice.CAMERA.CAMERA_DEFAULT);
				//CameraDevice.getInstance.Init(CameraDevice.CameraDirection.CAMERA_FRONT);
				//CameraDevice.getInstance().init(CameraDevice.CAMERA.CAMERA_FRONT);
			} catch (ApplicationException e) {
				Log.e(LOGTAG, e.getString());
			}

			boolean result = CameraDevice.getInstance().setFocusMode(
					CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

			if (result)
				mContAutofocus = true;
			else
				Log.e(LOGTAG, "Unable to enable continuous autofocus");

			mUILayout.bringToFront();

			// Hides the Loading Dialog
			loadingDialogHandler.sendEmptyMessage(HIDE_LOADING_DIALOG);

			mUILayout.setBackgroundColor(Color.TRANSPARENT);

		} else {
			Log.e(LOGTAG, exception.getString());
			if (mInitErrorCode != 0) {
				showErrorMessage(mInitErrorCode, 10, true);
			} else {
				finish();
			}
		}
	}

	@Override
	public void onQCARUpdate(State state) {
		// Log.d(LOGTAG, "onQCARUpdate");
		// Get the tracker manager:
		TrackerManager trackerManager = TrackerManager.getInstance();

		// Get the image tracker:
		ImageTracker imageTracker = (ImageTracker) trackerManager
				.getTracker(ImageTracker.getClassType());

		// Get the target finder:
		TargetFinder finder = imageTracker.getTargetFinder();

		// Check if there are new results available:
		final int statusCode = finder.updateSearchResults();

		// Show a message if we encountered an error:
		if (statusCode < 0) {

			boolean closeAppAfterError = (statusCode == UPDATE_ERROR_NO_NETWORK_CONNECTION || statusCode == UPDATE_ERROR_SERVICE_NOT_AVAILABLE);

			showErrorMessage(statusCode, state.getFrame().getTimeStamp(),
					closeAppAfterError);

		} else if (statusCode == TargetFinder.UPDATE_RESULTS_AVAILABLE) {
			// Process new search results
			if (finder.getResultCount() > 0) {
				TargetSearchResult result = finder.getResult(0);

				if (hasWallInfo) {
					String wallID = WallCurrent.wid.getWallID();
					for (int i = 0; i < finder.getResultCount(); i++) {
						if (wallID == finder.getResult(i).getTargetName()) {
							result = finder.getResult(i);
							// Toast.makeText(getApplicationContext(),
							// "晃动手机，捕捉该墙进行评论！", Toast.LENGTH_LONG).show();
						}
					}
				} else {
					wallID = result.getTargetName();
				}
				// Check if this target is suitable for tracking:
				if (result.getTrackingRating() > 0) {
					Trackable trackable = finder.enableTracking(result);
					Log.v(LOGTAG, "Toast");

					// toast.show();
					// shake.setShakeListener(mShakeListener);
					if (mExtendedTracking)
						trackable.startExtendedTracking();
				}
			}
		}
	}

	private class CloudShakeListener implements ShakeListener {
		private boolean first=true;
		public void onShakeListener() {
			if (hasWallInfo&&first) {
				first=false;
				toast.cancel();
				Intent intent = new Intent(getApplicationContext(),
						WallCommentCreate.class);
				startActivity(intent);
				close();
			} else if(first){
				first=false;
				AVQuery<AVObject> query = new AVQuery<AVObject>("WallInfo");
				query.include(WallInfoUpload.USER);
				query.whereEqualTo("objectId", wallID);
				query.findInBackground(new FindCallback<AVObject>() {

					@Override
					public void done(List<AVObject> arg0, AVException arg1) {
						// TODO Auto-generated method stub
						if (arg1 == null && arg0 != null) {

							WallCurrent.wid = new WallInfoDownload(arg0.get(0));
							toast.cancel();
							Intent intent = new Intent(getApplicationContext(),
									WallCommentActivity.class);
							intent.putExtra("commentFlag", true);
							startActivity(intent);
							close();
						}
					}

				});
			}
		}
	}

	private void close() {
		this.finish();
	}

	@Override
	public boolean doInitTrackers() {
		Log.d(LOGTAG, "doInitTrackers");
		TrackerManager tManager = TrackerManager.getInstance();
		Tracker tracker;

		// Indicate if the trackers were initialized correctly
		boolean result = true;

		tracker = tManager.initTracker(ImageTracker.getClassType());
		if (tracker == null) {
			Log.e(LOGTAG,
					"Tracker not initialized. Tracker already initialized or the camera is already started");
			result = false;
		} else {
			Log.i(LOGTAG, "Tracker successfully initialized");
		}

		return result;
	}

	@Override
	public boolean doStartTrackers() {
		Log.d(LOGTAG, "doStartTrackers");
		// Indicate if the trackers were started correctly
		boolean result = true;

		// Start the tracker:
		TrackerManager trackerManager = TrackerManager.getInstance();
		ImageTracker imageTracker = (ImageTracker) trackerManager
				.getTracker(ImageTracker.getClassType());
		imageTracker.start();

		// Start cloud based recognition if we are in scanning mode:
		TargetFinder targetFinder = imageTracker.getTargetFinder();
		targetFinder.startRecognition();
		mFinderStarted = true;

		return result;
	}

	@Override
	public boolean doStopTrackers() {
		Log.d(LOGTAG, "doStopTrackers");
		// Indicate if the trackers were stopped correctly
		boolean result = true;

		TrackerManager trackerManager = TrackerManager.getInstance();
		ImageTracker imageTracker = (ImageTracker) trackerManager
				.getTracker(ImageTracker.getClassType());

		if (imageTracker != null) {
			imageTracker.stop();

			// Stop cloud based recognition:
			TargetFinder targetFinder = imageTracker.getTargetFinder();
			targetFinder.stop();
			mFinderStarted = false;

			shake.stopShakeListener();
			// Clears the trackables
			targetFinder.clearTrackables();
		} else {
			result = false;
		}

		return result;
	}

	@Override
	public boolean doDeinitTrackers() {
		Log.d(LOGTAG, "doDeinitTrackers");
		// Indicate if the trackers were deinitialized correctly
		boolean result = true;

		TrackerManager tManager = TrackerManager.getInstance();
		tManager.deinitTracker(ImageTracker.getClassType());

		return result;
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

}
