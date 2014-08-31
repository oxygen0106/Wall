package com.oxygen.my;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class MyRoundView extends ImageView {

	Paint circlePaint;
	int i;

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public MyRoundView(Context context) {
		super(context);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param att
	 */
	public MyRoundView(Context context, AttributeSet att) {
		this(context, att, 0);

		initMyView();
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param att
	 * @param defStyle
	 */
	public MyRoundView(Context context, AttributeSet att, int defStyle) {
		super(context, att, defStyle);
		initMyView();
	}

	/**
	 * @param
	 * @return void
	 * @Description //初始化view,将此方法加入构造函数中，减少内存频繁分配
	 */
	private void initMyView() {
		Resources r = this.getResources();

		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 圆形画笔，设置Paint为抗锯齿
		circlePaint.setColor(Color.WHITE);// 设置Paint颜色
		circlePaint.setStrokeWidth(4);// 轮廓宽度
		circlePaint.setStyle(Paint.Style.STROKE);
		// circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
	}

	@Override
	protected void onMeasure(int wMeasureSpec, int hMeasureSpec) {
		int width = measure(wMeasureSpec);
		int height = measure(hMeasureSpec);
		int d = (width >= height) ? height : width;// 获取最短的边作为直径
		setMeasuredDimension(d, d);// onMeasure必须调用该方法来确定绘制控件面积
	}

	/**
	 * @param @param measureSpec
	 * @param @return
	 * @return int
	 * @Description 测量可获得的长度，满足自定义长度或不满足自定义时按照测得返回长度
	 */
	private int measure(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.UNSPECIFIED) {// parent对绘制面积无限制
			result = 200;
		} else if (specMode == MeasureSpec.AT_MOST) {// 给出了可绘制的最大面积界限
			if (specSize >= 200) {// 与界限比较
				result = 200;
			} else {
				result = specSize;
			}
		} else {// MeasureSpec.EXACTLY 给出了明确的限定范围
			result = specSize;
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		int width = getMeasuredWidth();// //计算控件的中心位置。Measured不是Measure，该方法属于View类
		int height = getMeasuredHeight();
		int pointX = width / 2;// 获得圆心坐标
		int pointY = height / 2;
		int radius = (pointX >= pointY) ? pointY : pointX;// 设置半径

		// radius -= 5;
		// canvas.translate(radius + 2, radius + 2);
		// canvas.drawCircle(0, 0, radius, circlePaint);// 绘制扫描区域

		Drawable drawable = getDrawable();
		if (drawable == null) {
			canvas.drawLine(0, height / 2, width, height / 2, circlePaint);
			return;
		}
		if (width == 0 || height == 0) {
			return;
		}
		Bitmap b = ((BitmapDrawable) drawable).getBitmap();// 将背景图像转换成Bitmap
		if (null == b) {
			canvas.drawLine(0, height / 2, width, height / 2, circlePaint);
			return;
		}
		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);// 按32位图复制

		Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);

		canvas.drawBitmap(roundBitmap, 0, 0, null);

		canvas.translate(radius, radius);
		canvas.drawCircle(0, 0, radius - 2, circlePaint);// 绘制扫描区域

	}

	public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
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
		float scaleWidth = ((float) diameter) / squareBitmap.getWidth();//此时Width == Height
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
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); // Mode.SRC_IN 取两层绘制交集。显示上层。
		canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
		// bitmap回收(recycle导致在布局文件XML看不到效果)
		// bmp.recycle();
		// squareBitmap.recycle();
		// scaledSrcBmp.recycle();
		bmp = null;
		squareBitmap = null;
		scaledSrcBmp = null;
		return output;
	}

}
