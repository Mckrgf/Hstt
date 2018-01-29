package com.example.yb.hstt.Utils;

/**
 * Created by cloudtp on 2016/5/default_errors.
 */

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片压缩工具类
 *
 * @author 丶Life_
 */
public class ImageCompressUtil {
    public static int compressBitmap(String srcBitmap){
        return compressBitmap(srcBitmap,1080,1920,80,srcBitmap);
    }

    /**
     *
     * @param srcBitmap
     * @param wantWidth
     * @param wantHeight
     * @param quality
     * @param destBitmap
     * @throws FileNotFoundException
     */
    public static int compressBitmap(String srcBitmap, int wantWidth,
                                     int wantHeight, int quality, String destBitmap) {
        int result = -1;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(srcBitmap);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scaleWidth,scaleHeight;
            if (height>width){
                 scaleWidth = ((float) wantWidth) / width;
                 scaleHeight = ((float) wantHeight) / height;
            }else{
                 scaleWidth = ((float) wantWidth) / height;
                 scaleHeight = ((float) wantHeight) / width;
            }

            float scaleFactor = (scaleWidth > scaleHeight) ? scaleWidth
                    : scaleHeight;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleFactor, scaleFactor);
//		matrix.postRotate(90);

            // 生成缩放的新图片
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                    matrix, true);

            // 保存图片到文件
            File destFile = new File(destBitmap);
            FileOutputStream ostream = new FileOutputStream(destFile);
            resizedBitmap.compress(CompressFormat.JPEG, quality, ostream);

            bitmap.recycle();
            resizedBitmap.recycle();
            result = 1;
        } catch (FileNotFoundException e) {
            result = -2;
        }
        return result;
    }

    /**
     * 读取资源图片
     *
     * @return
     */
    public static Bitmap readBitMap(String picpath, int maxsize) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        /*
         * 设置让解码器以最佳方式解码
         */
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        //下面两个字段需要组合使用
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        /*
         * 获取资源图片
         */
        return compressByQuality(BitmapFactory.decodeFile(picpath, opt), maxsize);
    }


    /**
     * 通过降低图片的质量来压缩图片
     *
     * @param bitmap  要压缩的图片位图对象
     * @param maxSize 压缩后图片大小的最大值,单位KB
     * @return 压缩后的图片位图对象
     */
    public static Bitmap compressByQuality(Bitmap bitmap, int maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(CompressFormat.JPEG, quality, baos);
        System.out.println("图片压缩前大小：" + baos.toByteArray().length + "byte");
        boolean isCompressed = false;
        while (baos.toByteArray().length / 1024 > maxSize) {
            quality -= 5;
            if (quality < 1) {
                quality = 30;
            }
            baos.reset();
            bitmap.compress(CompressFormat.JPEG, quality, baos);
            System.out.println("质量压缩到原来的" + quality + "%时大小为：" + baos.toByteArray().length + "byte");
            isCompressed = true;
            if (quality == 30) {
                break;
            }
        }
        System.out.println("图片压缩后大小：" + baos.toByteArray().length + "byte");
        if (isCompressed) {
            Bitmap compressedBitmap = BitmapFactory.decodeByteArray(
                    baos.toByteArray(), 0, baos.toByteArray().length);
            recycleBitmap(bitmap);
            return compressedBitmap;
        } else {
            return bitmap;
        }
    }
    /**
     * 加载本地图片
     * http://bbs.3gstdy.com
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过压缩图片的尺寸来压缩图片大小，仅仅做了缩小，如果图片本身小于目标大小，不做放大操作
     *
     * @param pathName     图片的完整路径
     * @param targetWidth  缩放的目标宽度
     * @param targetHeight 缩放的目标高度
     * @return 缩放后的图片
     */
    public static Bitmap compressBySize(String pathName, int targetWidth,
                                        int targetHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);
        // 得到图片的宽度、高度；
        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;
        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于等于该比例的最小整数；
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        if (widthRatio > 1 || heightRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }
        // 设置好缩放比例后，加载图片进内容；
        opts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(pathName, opts);
        return bitmap;
    }

    /**
     * 通过压缩图片的尺寸来压缩图片大小
     *
     * @param bitmap       要压缩图片
     * @param targetWidth  缩放的目标宽度
     * @param targetHeight 缩放的目标高度
     * @return 缩放后的图片
     */
    public static Bitmap compressBySize(Bitmap bitmap, int targetWidth,
                                        int targetHeight) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0,
                baos.toByteArray().length, opts);
        // 得到图片的宽度、高度；
        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;
        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        if (widthRatio > 1 || heightRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }
        // 设置好缩放比例后，加载图片进内存；
        opts.inJustDecodeBounds = false;
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(
                baos.toByteArray(), 0, baos.toByteArray().length, opts);
        recycleBitmap(bitmap);
        return compressedBitmap;
    }

    /**
     * 通过压缩图片的尺寸来压缩图片大小，通过读入流的方式，可以有效防止网络图片数据流形成位图对象时内存过大的问题；
     *
     * @param is           要压缩图片，以流的形式传入
     * @param targetWidth  缩放的目标宽度
     * @param targetHeight 缩放的目标高度
     * @return 缩放后的图片
     * @throws IOException 读输入流的时候发生异常
     */
    public static Bitmap compressBySize(InputStream is, int targetWidth,
                                        int targetHeight) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = is.read(buff)) != -1) {
            baos.write(buff, 0, len);
        }

        byte[] data = baos.toByteArray();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                opts);
        // 得到图片的宽度、高度；
        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;
        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        if (widthRatio > 1 || heightRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }
        // 设置好缩放比例后，加载图片进内存；
        opts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        return bitmap;
    }

    /**
     * 旋转图片摆正显示
     *
     * @param srcPath
     * @param bitmap
     * @return
     */
    public static Bitmap rotateBitmapByExif(String srcPath, Bitmap bitmap) {
        ExifInterface exif;
        Bitmap newBitmap = null;
        try {
            exif = new ExifInterface(srcPath);
            if (exif != null) { // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                int digree = 0;
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                }
                if (digree != 0) {
                    Matrix m = new Matrix();
                    m.postRotate(digree);
                    newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            bitmap.getWidth(), bitmap.getHeight(), m, true);
                    recycleBitmap(bitmap);
                    return newBitmap;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 回收位图对象
     *
     * @param bitmap
     */
    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            System.gc();
            bitmap = null;
        }
    }

    /**
     * 把图片变成圆角
     *
     * @param bitmap 需要修改的图片
     * @param pixels 圆角的弧度
     * @return 圆角图片
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


}