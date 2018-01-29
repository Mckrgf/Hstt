package com.example.yb.hstt.Base;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.example.yb.hstt.R;
import com.example.yb.hstt.View.MyImageView;

import java.util.HashMap;

/**
 * Created by TFHR02 on 2017/12/14.
 */
public class MyVideoThumbLoader {
    // 创建cache
    private LruCache<String, Bitmap> lruCache;

    public MyVideoThumbLoader() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();// 获取最大的运行内存
        int maxSize = maxMemory / 4;
        // 拿到缓存的内存大小
        lruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // 这个方法会在每次存入缓存的时候调用
                return value.getByteCount();
            }
        };
    }

    public void addVideoThumbToCache(String path, Bitmap bitmap) {
        if (getVideoThumbToCache(path) == null) {
            // 当前地址没有缓存时，就添加

            lruCache.put(path, bitmap);
        }
    }

    public Bitmap getVideoThumbToCache(String path) {

        return lruCache.get(path);

    }

    public void showThumbByAsynctack(Context context,String path, MyImageView imgview) {

        if (getVideoThumbToCache(path) == null) {
            // 异步加载
            new MyBobAsynctack(context,imgview, path).execute(path);
        } else {
            imgview.setImageBitmap(getVideoThumbToCache(path));
        }
    }

    class MyBobAsynctack extends AsyncTask<String, Void, Bitmap> {
        private static final String TAG = "MyBobAsynctack";
        private MyImageView imgView;
        private String path;
        private Context context;

        public MyBobAsynctack(Context context,MyImageView imageView, String path) {
            this.context=context;
            this.imgView = imageView;
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                ThumbnailUtils tu = new ThumbnailUtils();
                bitmap = createVideoThumbnail(params[0],200,200);
                Log.i(TAG, "111111path: " + path + "  bitmap: " + bitmap);
                if (bitmap == null) {
                    bitmap = android.graphics.BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_photo_error);
                    Log.i(TAG, "5555555path: " + path + "  bitmap: " + bitmap);
                }
                //直接对Bitmap进行缩略操作，最后一个参数定义为OPTIONS_RECYCLE_INPUT ，来回收资源
//                Bitmap bitmap2 = tu.extractThumbnail(bitmap, 50, 50,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
//                Log.i(TAG, "path: " + path + "bitmap2: " + bitmap2);
                // 加入缓存中
                if (getVideoThumbToCache(params[0]) == null) {
                    addVideoThumbToCache(path, bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        private Bitmap createVideoThumbnail(String url, int width, int height) {
            Bitmap bitmap = null;
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            int kind = MediaStore.Video.Thumbnails.MINI_KIND;
            try {
                if (Build.VERSION.SDK_INT >= 14) {
                    retriever.setDataSource(url, new HashMap<String, String>());
                } else {
                    retriever.setDataSource(url);
                }
                bitmap = retriever.getFrameAtTime();
            } catch (IllegalArgumentException ex) {
            } catch (RuntimeException ex) {
            } finally {
                try {
                    retriever.release();
                } catch (RuntimeException ex) {
                }
            }
            if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                        ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.i(TAG, "onPostExecute: "+bitmap);
            if (imgView.getTag().equals(path)) {// 通过 Tag可以绑定 图片地址和
                Log.i(TAG, "equals: true");
                // imageView，这是解决Listview加载图片错位的解决办法之一
                imgView.setImageBitmap(bitmap);
            }
        }
    }
}
