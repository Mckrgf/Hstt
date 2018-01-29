package com.example.yb.hstt.Utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Video;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.ArrayList;

public class FileUtils {
    /**
     * KB
     */
    public static final long ONE_KB = 1024;

    /**
     * MB
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;

    /**
     * GB
     */
    public static final long ONE_GB = ONE_KB * ONE_MB;

    private static final int BUF_SIZE = 1024;
    private static final String TAG = "FileUtils";

    /**
     * 将输入流写入文件，原文件存在会被清空
     *
     * @param context
     * @param is
     * @param filename
     * @return
     */
    public static String writeToFile(Context context, InputStream is, String filename) {
        if (is == null || context == null || TextUtils.isEmpty(filename))
            return null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        clearFile(context, filename);
        Log.d(TAG, "writeToFile : " + filename);
        try {
            in = new BufferedInputStream(is);
            out = new BufferedOutputStream(new FileOutputStream(getFile(context, filename)));
            byte[] buffer = new byte[BUF_SIZE];
            int l;
            while ((l = in.read(buffer)) != -1) {
                // LogUtil.d("writeToFile", "length : " + l);
                out.write(buffer, 0, l);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                is.close();
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return context.getFilesDir() + File.separator + filename;
    }

    public static String writeToFile(Context context, byte[] bytes, String filename) {
        return writeToFile(context, bytes, filename, false);
    }

    public static String writeToFile(byte[] bytes, String dirPath, String filename) {
        return writeToFile(bytes, dirPath, filename, false);
    }
    /**
     * 把mOriginalBitmaps转Byte
     *
     * @Author HEH
     * @EditTime 2010-07-19 上午11:45:56
     */
    public static byte[] picPath2Bytes(String picPath) {
        //先根据路径获得bitmap图片
        Bitmap mOriginalBitmap = BitmapFactory.decodeFile(picPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mOriginalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
    public static String writeToFile(Context context, byte[] bytes, String filename, boolean isAppend) {
        if (bytes == null || context == null || TextUtils.isEmpty(filename))
            return null;
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(getFile(context, filename), isAppend));
            out.write(bytes);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.e(TAG, "IOException : " + ioe.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return context.getFilesDir() + File.separator + filename;
    }

    public static String writeToFile(byte[] bytes, String dirPath, String filename, boolean isAppend) {
        if (bytes == null || TextUtils.isEmpty(filename) || TextUtils.isEmpty(filename)) {
            return null;
        }

        File file = getFile(dirPath, filename);

        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file, isAppend));
            out.write(bytes);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.e(TAG, "IOException : " + ioe.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return (null == file) ? "" : file.getAbsolutePath();
    }

    /**
     * write a string To a File
     *
     * @param file
     * @param string
     * @param isAppend
     * @return
     */
    public static boolean writeStringToFile(File file, String string, boolean isAppend) {
        boolean isWriteOk = false;

        if (null == file || null == string) {
            return isWriteOk;
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(file, isAppend);

            fw.write(string, 0, string.length());
            fw.flush();
            isWriteOk = true;
        } catch (Exception e) {
            isWriteOk = false;
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    isWriteOk = false;
                    e.printStackTrace();
                }
            }
        }
        return isWriteOk;
    }

    /**
     * 根据文件URI判断是否为媒体文件
     *
     * @param uri
     * @return
     */
    public static boolean isMediaUri(String uri) {
        if (uri.startsWith(Audio.Media.INTERNAL_CONTENT_URI.toString())
                || uri.startsWith(Audio.Media.EXTERNAL_CONTENT_URI.toString())
                || uri.startsWith(Video.Media.INTERNAL_CONTENT_URI.toString())
                || uri.startsWith(Video.Media.EXTERNAL_CONTENT_URI.toString())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 复制文件
     *
     * @param src
     * @param dec
     */
    public static void copyFile(File src, File dec) {
        try {
            if (src == null || dec == null) {
                return;
            }

            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dec);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建空文件或清空文件内容
     *
     * @param context
     * @param filename
     */
    public static void clearFile(Context context, String filename) {
        if (context == null || TextUtils.isEmpty(filename))
            return;
        File file = getFile(context, filename);
        if (file == null)
            return;

        Log.d(TAG, "clearFile path : " + file.getAbsolutePath());
        File dir = file.getParentFile();

        if (dir != null && !dir.exists()) {
            Log.d(TAG, "dir not exists");
            dir.mkdirs();
        }
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getFile(Context context, String filename) {
        if (context == null || TextUtils.isEmpty(filename))
            return null;
        return new File(context.getFilesDir().getAbsoluteFile() + filename);
    }

    public static File getFile(String dirPath, String filename) {
        if (TextUtils.isEmpty(dirPath) || TextUtils.isEmpty(filename))
            return null;
        return new File(dirPath + File.separator + filename);

    }

    public static String getFileName(String path) {
        if (path == null) {
            return null;
        }
        String retStr = "";
        if (path.indexOf(File.separator) > 0) {
            retStr = path.substring(path.lastIndexOf(File.separator) + 1);
        } else {
            retStr = path;
        }
        return retStr;
    }

    public static String getFileNameNoPostfix(String path) {
        if (path == null) {
            return null;
        }
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    /**
     * 根据文件URI得到文件扩展名
     *
     * @param uri
     * @return
     */
    public static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }

    /**
     * 判断是否为本地文件
     *
     * @param uri
     * @return
     */
    public static boolean isLocal(String uri) {
        if (uri != null && !uri.startsWith("http://")) {
            return true;
        }
        return false;
    }

    /**
     * 判断文件是否为视频文件
     *
     * @param filename
     * @return
     */
    public static boolean isVideo(String filename) {
        String mimeType = getMimeType(filename);
        if (mimeType != null && mimeType.startsWith("video/")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断文件是否为音频文件
     *
     * @param filename
     * @return
     */
    public static boolean isAudio(String filename) {
        String mimeType = getMimeType(filename);
        if (mimeType != null && mimeType.startsWith("audio/")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据文件名得到文件的mimetype 简单判断,考虑改为xml文件配置关联
     *
     * @param filename
     * @return
     */
    public static String getMimeType(String filename) {
        String mimeType = null;

        if (filename == null) {
            return mimeType;
        }
        if (filename.endsWith(".3gp")) {
            mimeType = "video/3gpp";
        } else if (filename.endsWith(".mid")) {
            mimeType = "audio/mid";
        } else if (filename.endsWith(".mp3")) {
            mimeType = "audio/mpeg";
        } else if (filename.endsWith(".xml")) {
            mimeType = "text/xml";
        } else {
            mimeType = "";
        }
        return mimeType;
    }

    /**
     * 将文件大小的long值转换为可读的文字
     *
     * @param size
     * @return 10KB或10MB或1GB
     */
    public static String byteCountToDisplaySize(long size) {
        String displaySize;

        if (size / ONE_GB > 0) {
            displaySize = String.valueOf(size / ONE_GB) + " GB";
        } else if (size / ONE_MB > 0) {
            displaySize = String.valueOf(size / ONE_MB) + " MB";
        } else if (size / ONE_KB > 0) {
            displaySize = String.valueOf(size / ONE_KB) + " KB";
        } else {
            displaySize = String.valueOf(size) + " bytes";
        }
        return displaySize;
    }

    /**
     * 将文件大小的long值转换为可读的文字
     *
     * @param size
     * @param scale 保留几位小数
     * @return 10KB或10MB或1GB
     */
    public static String byteCountToDisplaySize(long size, int scale) {
        String displaySize;
        if (size / ONE_GB > 0) {
            float d = (float) size / ONE_GB;
            displaySize = getOffsetDecimal(d, scale) + " GB";
        } else if (size / ONE_MB > 0) {
            float d = (float) size / ONE_MB;
            displaySize = getOffsetDecimal(d, scale) + " MB";
        } else if (size / ONE_KB > 0) {
            float d = (float) size / ONE_KB;
            displaySize = getOffsetDecimal(d, scale) + " KB";
        } else {
            displaySize = String.valueOf(size) + " bytes";
        }
        return displaySize;
    }

    public static String getOffsetDecimal(float ft, int scale) {
        int roundingMode = 4;// 表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
        BigDecimal bd = new BigDecimal((double) ft);
        bd = bd.setScale(scale, roundingMode);
        ft = bd.floatValue();
        return "" + ft;
    }

    public static boolean isDirectory(File file) {
        return file.exists() && file.isDirectory();
    }

    public static boolean isFile(File file) {
        return file.exists() && file.isFile();
    }

    // 检测目录，没有重建
    public static void checkDir(String dirPath) {
        File file = new File(dirPath);
        createNewDirectory(file);
    }

    public static boolean createNewDirectory(File file) {
        if (file.exists() && file.isDirectory()) {
            return false;
        }
        return file.mkdirs();
    }

    public static boolean deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return true;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return true;
        }
        boolean flag = false;
        if (file.isFile()) {
            flag = file.delete();
        }
        return flag;
    }

    /**
     * 删除目录和其中所有文件
     *
     * @param directoryPath
     */
    public static void delDirectory(String directoryPath) {
        try {
            delAllFile(directoryPath); // 删除完里面所有内容
            String filePath = directoryPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delDirectory(path + "/" + tempList[i]);// 再删除空文件夹
                // flag = true;
            }
        }
        flag = isEmpty(file);
        return flag;
    }

    /**
     * 删除目录中的文件，保留目录结构
     *
     * @param path
     * @return
     */
    public static boolean delAllFileWithoutDir(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFileWithoutDir(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                // flag = true;
            }
        }
        flag = isEmpty(file);
        return flag;
    }

    /**
     * 判断目录是否为空
     *
     * @param dir
     * @return
     */
    private static boolean isEmpty(File dir) {
        if (null == dir) {
            return false;
        }
        if (!dir.isDirectory()) {
            return false;
        }
        File[] files = dir.listFiles();
        return files == null || files.length == 0;
    }

    // -------------------- 获得文件的md5等hash值---------------------//
    public final static String HASH_TYPE_MD5 = "MD5";
    public final static String HASH_TYPE_SHA1 = "SHA1";
    public final static String HASH_TYPE_SHA1_256 = "SHA-256";
    public final static String HASH_TYPE_SHA1_384 = "SHA-384";
    public final static String HASH_TYPE_SHA1_512 = "SHA-512";
    public static char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getHash(String fileName, String hashType) throws Exception {
        InputStream fis;
        fis = new FileInputStream(fileName);
        byte[] buffer = new byte[1024];
        MessageDigest md5 = MessageDigest.getInstance(hashType);
        int numRead = 0;
        while ((numRead = fis.read(buffer)) > 0) {
            md5.update(buffer, 0, numRead);
        }
        fis.close();
        return toHexString(md5.digest());
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 打开apk
     *
     * @param mContext
     * @param filepath
     */
    public static void OpenAPK(Context mContext, String filepath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(filepath ));
        intent.setDataAndType(uri,"application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    /**
     * 清除目录dirPath下后缀名为suffix的文件
     *
     * @param dirPath
     * @param suffix
     */
    public static void clearFiles(String dirPath, String suffix) {
        if (TextUtils.isEmpty(dirPath) || TextUtils.isEmpty(suffix))
            return;
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory())
            return;
        String filename = null;
        for (File file : dir.listFiles()) {
            filename = file.getName();
            if (filename.endsWith(suffix))
                file.delete();
        }
    }

    /**
     * 将输入流is指定的数据写入filepath指定的文件中
     *
     * @param is
     * @param filepath
     * @return
     * @throws IOException
     */
    public static String writeToFile(InputStream is, String filepath) throws IOException {
        if (is == null || TextUtils.isEmpty(filepath))
            return null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            Log.d(TAG, "write to file : " + filepath);

            File file = new File(filepath);
            checkDir(file.getParent());
            in = new BufferedInputStream(is);
            out = new BufferedOutputStream(new FileOutputStream(filepath));
            byte[] buffer = new byte[BUF_SIZE];
            int l;
            while ((l = in.read(buffer)) != -1)
                out.write(buffer, 0, l);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (in != null)
                    in.close();
                is.close();
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return filepath;
    }

    /**
     * 将输入流is指定数据同步写入filepath指定位置
     *
     * @param is
     * @param filepath
     * @return
     * @throws IOException
     */
    synchronized public static String writeToFileSync(InputStream is, String filepath) throws IOException {
        return FileUtils.writeToFile(is, filepath);
    }

    /**
     * read file to a string
     *
     * @param file
     * @return
     */
    public static String loadString(File file) {
        if (null == file || !file.exists()) {
            return "";
        }
        FileInputStream fis = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            fis = new FileInputStream(file);
            int restSize = fis.available();
            int bufSize = restSize > BUF_SIZE ? BUF_SIZE : restSize;
            byte[] buf = new byte[bufSize];
            while (fis.read(buf) != -1) {
                baos.write(buf);
                restSize -= bufSize;

                if (restSize <= 0)
                    break;
                if (restSize < bufSize)
                    bufSize = restSize;
                buf = new byte[bufSize];
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return baos.toString();
    }

    public static long getFolderSize(File folder) throws IllegalArgumentException {
        // Validate
        if (folder == null || !folder.isDirectory())
            throw new IllegalArgumentException("Invalid   folder ");
        String list[] = folder.list();
        if (list == null || list.length < 1)
            return 0;

        // Get size
        File object = null;
        long folderSize = 0;
        for (int i = 0; i < list.length; i++) {
            object = new File(folder, list[i]);
            if (object.isDirectory())
                folderSize += getFolderSize(object);
            else if (object.isFile())
                folderSize += object.length();
        }
        return folderSize;
    }

    /**
     * 检查文件是否存在于某目录下, 3.3.0
     *
     * @return
     */
    public static String checkFile(String filePathName) {
        // 添加入口参数检查
        if (filePathName == null) {
            return null;
        }
        // 在图片存储目录里检查
        File file = new File(filePathName);
        if (file.exists() || file.isFile()) {
            try {
                return file.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取文件编码
     *
     * @param sourceFile
     * @return
     */
    public static String getFileCharset(String sourceFile) {
        Log.d(TAG, "getFileCharset, sourceFile=" + sourceFile);
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
            bis.mark(3);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return charset; // 文件编码为 ANSI
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE"; // 文件编码为 Unicode
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE"; // 文件编码为 Unicode big endian
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8"; // 文件编码为 UTF-8
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                            // (0x80
                            // - 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getFileCharset, charset=" + charset);
        return charset;
    }

    /**
     * 清除目录下的所以文件
     *
     * @param dirPath 目录路径
     * @return 是否清除成功
     */
    public static boolean clearDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory())
            return false;
        for (File file : dir.listFiles()) {
            if (!file.exists())
                continue;
            if (file.isFile())
                file.delete();
            if (file.isDirectory()) {
                clearDir(file.getAbsolutePath());
            }
        }
        File[] files = dir.listFiles();
        return files == null || files.length == 0;
    }

    /**
     * 过滤文件名，保证过滤后的文件名为合法文件名<br/>
     * 非法字符将被替换成下划线_
     *
     * @param filename 需要过滤的文件名(不包括父目录路径)
     * @return 过滤后合法的文件名
     */
    public static String filterFileName(String filename) {
        if (TextUtils.isEmpty(filename))
            return filename;
        filename = filename.replace(' ', '_');
        filename = filename.replace('"', '_');
        filename = filename.replace('\'', '_');
        filename = filename.replace('\\', '_');
        filename = filename.replace('/', '_');
        filename = filename.replace('<', '_');
        filename = filename.replace('>', '_');
        filename = filename.replace('|', '_');
        filename = filename.replace('?', '_');
        filename = filename.replace(':', '_');
        filename = filename.replace(',', '_');
        filename = filename.replace('*', '_');
        return filename;
    }

    public static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canRead() == false) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }

    public static void writeStringToFile(File file, String data, String encoding) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file);
            if (data != null) {
                if (encoding == null) {
                    out.write(data.getBytes());
                } else {
                    out.write(data.getBytes(encoding));
                }
            }
        } catch (IOException e) {
            // ignore
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) {
                // ignore
            }
        }
    }

    public static FileOutputStream openOutputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && parent.exists() == false) {
                if (parent.mkdirs() == false) {
                    throw new IOException("File '" + file + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file);
    }

    /**
     * 重命名文件。源文件和目标文件必须在用同一个mount点下
     *
     * @param srcFullName
     * @param dstFullName
     * @return
     */
    public static boolean renameFile(String srcFullName, String dstFullName) {
        if (!TextUtils.isEmpty(srcFullName) && !TextUtils.isEmpty(dstFullName)) {
            File f = new File(srcFullName);
            return f.renameTo(new File(dstFullName));
        }

        return false;
    }

    /**
     * ObjectOutputStream 直接写入vector map
     */
    public static boolean writeFileOOS(String pathName, Object o, boolean append) {
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        File file = new File(pathName);
        if (!file.exists())
            try {
                if (!file.createNewFile())
                    return false;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        if (o == null)
            return false;
        else {
            try {
                fos = new FileOutputStream(file, append);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(o);
                oos.flush();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    if (oos != null)
                        oos.close();
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return true;
        }
    }

    /**
     * ObjectInputStream return Set/Map/Vector
     */
    public static Object readFileOIS(String pathName) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        Object obj = null;
        File file = new File(pathName);
        if (!file.exists())
            return null;
        else {
            try {
                fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);
                obj = ois.readObject();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ois != null)
                        ois.close();
                    if (fis != null)
                        fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return obj;
        }
    }

    private static final ArrayList<String> mWildCardChars = new ArrayList<String>();

    static {
        mWildCardChars.add("\\");
        mWildCardChars.add("/");
        mWildCardChars.add(":");
        mWildCardChars.add("*");
        mWildCardChars.add("?");
        mWildCardChars.add("\"");
        mWildCardChars.add("<");
        mWildCardChars.add(">");
        mWildCardChars.add("|");
    }

    public static String clearWildCardChars(String name) {
        try {
            for (String wildCardChar : mWildCardChars) {
                if (name.contains(wildCardChar)) {
                    name = name.replace(wildCardChar, "");
                }
            }
        } catch (Exception e) {
            name = "";
        }

        return name;
    }

    /**
     * 获取系统图片浏览器 选择的图片的路径
     *
     * @param context 上下文
     * @param data    系统图片浏览器界面 返回的数据
     * @return
     */
    public static String chooseGalleryImageHandler(
            Context context, Intent data) {
        if (data == null) {
            return null;
        }
        Uri uri = data.getData();
        String filePath;
        if (uri.toString().substring(0, 4).equals("file")) {
            filePath = uri.getPath();
        } else {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            filePath = cursor.getString(columnIndex);
        }
        return filePath;
    }

}
