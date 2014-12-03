/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.oxygen.image;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oxygen.wall.R;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */

public class ImagePagerActivity extends Activity {
	private static final String TAG="ImagePager";
	public static final int INDEX = 2;

	//String[] imageUrls = Constants.IMAGES;
	private static String imageURL;
	private DisplayImageOptions options;
	private ImageView imageView;
	private ProgressBar progressBar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 自定义标题栏模式
		Intent intent=getIntent();
		imageURL=intent.getStringExtra("imageURL");
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.resetViewBeforeLoading(true)
				.cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300))
				.build();
		setContentView(R.layout.item_pager_image);
		imageView = (ImageView) findViewById(R.id.image);
		progressBar = (ProgressBar)findViewById(R.id.loading);
		Log.v(TAG, "OnCreateDone");
		loadImage();
	}
	private void loadImage(){
		Log.v(TAG, "loadeImage");
		ImageLoader.getInstance().displayImage(imageURL, imageView,options, LoaderListener.loaderListener);
//		ImageLoader.getInstance().displayImage(imageURL, imageView,options, new SimpleImageLoadingListener() {
//			@Override
//			public void onLoadingStarted(String imageUri, View view) {
//				Log.v(TAG, "onLoadingStarted");
//				progressBar.setVisibility(View.VISIBLE);
//			}
//
//			@Override
//			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//				String message = null;
//				switch (failReason.getType()) {
//					case IO_ERROR:
//						message = "Input/Output error";
//						break;
//					case DECODING_ERROR:
//						message = "Image can't be decoded";
//						break;
//					case NETWORK_DENIED:
//						message = "Downloads are denied";
//						break;
//					case OUT_OF_MEMORY:
//						message = "Out Of Memory error";
//						break;
//					case UNKNOWN:
//						message = "Unknown error";
//						break;
//				}
//				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//
//				progressBar.setVisibility(View.GONE);
//			}
//
//			@Override
//			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//				Log.v(TAG, "onLoadingComplete");
//				progressBar.setVisibility(View.GONE);
//			}
//		});
	}
}