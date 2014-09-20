package com.oxygen.ar;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class RotateImage {
	 public static int readPictureDegree(String path) {
	        int degree  = 0;
	        try {
	                ExifInterface exifInterface = new ExifInterface(path);
	                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
	                switch (orientation) {
	                case ExifInterface.ORIENTATION_ROTATE_90:
	                        degree = 90;
	                        break;
	                case ExifInterface.ORIENTATION_ROTATE_180:
	                        degree = 180;
	                        break;
	                case ExifInterface.ORIENTATION_ROTATE_270:
	                        degree = 270;
	                        break;
	                }
	        } catch (IOException e) {
	                e.printStackTrace();
	        }
	        return degree;
	    }
	   public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
	       //旋转图片 动作   
	       Matrix matrix = new Matrix();;  
	       matrix.postRotate(angle); 
	       // 创建新的图片   
	       Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
	               bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
	       return resizedBitmap;  
	   }
}
