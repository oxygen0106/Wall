package com.oxygen.my;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.Environment;

public class MyRoundImageTool {

	String path;
	Paint circlePaint;
	int radius;
	int i;

	MyRoundImageTool(String path) {
		this.path = path;
		initMyView();

	}

	public void getRoundImage() {
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		radius = getRadius(bitmap);
		getCroppedRoundBitmap(bitmap, 100);

	}

	/**
	 * @param
	 * @return void
	 * @Description //初始化view,将此方法加入构造函数中，减少内存频繁分配
	 */
	private void initMyView() {

		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 圆形画笔，设置Paint为抗锯齿
		circlePaint.setColor(Color.WHITE);// 设置Paint颜色
		circlePaint.setStrokeWidth(4);// 轮廓宽度
		circlePaint.setStyle(Paint.Style.STROKE);
		// circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
	}

	public int getRadius(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (width >= height) {
			return height;
		} else {
			return width;
		}

	}

	/**
	 * @param @param bmp
	 * @param @param radius
	 * @param @return
	 * @return Bitmap
	 * @Description 将图像进行剪裁和压缩，设置圆形
	 */
	public void getCroppedRoundBitmap(Bitmap bmp, int radius) {
		Bitmap scaledSrcBmp;
		int diameter = radius * 2;
		int startX, startY;
		Bitmap squareBitmap;
		int squareWidth, squareHeight;

		// 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();

		if (bmpHeight > bmpWidth) {// 高大于宽，取宽
			// if(bmpWidth>diameter)
			squareWidth = squareHeight = bmpWidth;
			startX = 0;
			startY = (bmpHeight - bmpWidth) / 2;

			squareBitmap = Bitmap.createBitmap(bmp, startX, startY,
					squareWidth, squareHeight);// 截取正方形图片

		} else if (bmpHeight < bmpWidth) {// 宽大于高，取高
			squareWidth = squareHeight = bmpHeight;
			startX = (bmpWidth - bmpHeight) / 2;
			startY = 0;
			squareBitmap = Bitmap.createBitmap(bmp, startX, startY,
					squareWidth, squareHeight);
		} else {
			squareBitmap = bmp;
		}

		Matrix matrix = new Matrix();// 缩放比例矩阵
		float scaleWidth = ((float) diameter) / squareBitmap.getWidth();// 此时Width
																		// ==
																		// Height
		matrix.postScale(scaleWidth, scaleWidth);

		if (squareBitmap.getWidth() != diameter
				|| squareBitmap.getHeight() != diameter) {// 正方形图片边长不等于直径，进行缩放
			// scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap,
			// diameter,diameter, true);//两种缩放方式
			scaledSrcBmp = Bitmap.createBitmap(squareBitmap, 0, 0,
					squareBitmap.getWidth(), squareBitmap.getWidth(), matrix,
					true);
		} else {
			scaledSrcBmp = squareBitmap;
		}
		Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
				scaledSrcBmp.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
				scaledSrcBmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
				scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
				paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); // Mode.SRC_IN
																// 取两层绘制交集。显示上层。
		canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
		// bitmap回收(recycle导致在布局文件XML看不到效果)
		// bmp.recycle();
		// squareBitmap.recycle();
		// scaledSrcBmp.recycle();

		squareBitmap = null;
		scaledSrcBmp = null;
		
		canvas.drawBitmap(output, 0, 0, null);

		canvas.translate(radius, radius);
		canvas.drawCircle(0, 0, radius - 2, circlePaint);// 绘制白圈区域
		
		try {//保存到sdcard
			output.compress(Bitmap.CompressFormat.PNG, 1, new FileOutputStream(new File(path)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		output.recycle();
		System.gc();
		
		
	}
}
