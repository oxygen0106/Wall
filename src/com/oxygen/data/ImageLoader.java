package com.oxygen.data;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
* @ClassName ImageLoader
* @Description 图片异步加载
* @author oxygen
* @email oxygen0106@163.com
* @date 2014-9-22 上午12:58:47
*/
public class ImageLoader {  
	  
    private HashMap<String, SoftReference<Drawable>> imageCache;  
       
        public ImageLoader() {  
            imageCache = new HashMap<String, SoftReference<Drawable>>();  
        }  
       
        public Drawable loadDrawable(final String imageUrl, final ImageView imageView,final ImageCallback imageCallback) {  
            if (imageCache.containsKey(imageUrl)) {  
                SoftReference<Drawable> softReference = imageCache.get(imageUrl);  
                Drawable drawable = softReference.get();  
                if (drawable != null) {  
                    return drawable;  
                }  
            }  
            final Handler handler = new Handler() {  
                public void handleMessage(Message message) {  
                    imageCallback.imageLoaded((Drawable) message.obj, imageView);  
                }  
            };  
            new Thread() {  
                @Override  
                public void run() {  
                    Drawable drawable = loadImageFromUrl(imageUrl);  
                    imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));  
                    Message message = handler.obtainMessage(0, drawable);  
                    handler.sendMessage(message);  
                }  
            }.start();  
            return null;  
        }  
       
       public static Drawable loadImageFromUrl(String url) {  
           URL m;  
           InputStream i = null;  
           try {  
               m = new URL(url);  
               i = (InputStream) m.getContent();  
           } catch (MalformedURLException e1) {  
               e1.printStackTrace();  
           } catch (IOException e) {  
               e.printStackTrace();  
           }  
           Drawable d = Drawable.createFromStream(i, "src");  
           return d;  
       }  
       
        public interface ImageCallback {  
            public void imageLoaded(Drawable imageDrawable, ImageView imageView);  
        }  
 
}  
