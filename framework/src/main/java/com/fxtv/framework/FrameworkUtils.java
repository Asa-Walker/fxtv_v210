package com.fxtv.framework;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.fxtv.framework.frame.DatabaseHelperFramework;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.SystemPage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrameworkUtils {
    private static final String TAG = "FrameworkUtils";
    private static Toast result;
    private static Toast old_toast;

    public static void showToast(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String msg, int duration) {
        if (context == null) {
            return;
        }
        /*if(result==null){  //自定义布局
            result = new Toast(context);
            LayoutInflater inflate = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflate.inflate(R.layout.transient_notification, null);
            result.setView(v);
            result.setDuration(Toast.LENGTH_SHORT);
        }
        count++;
        result.setText(count+" "+msg);
        result.show();
        */
        //  Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT);

        try {
            View mNextView;
            if (old_toast != null && (mNextView = old_toast.getView()) != null && mNextView instanceof ViewGroup) {
                TextView msgTextView = ((ViewGroup) mNextView).getChildCount() > 0 ? (TextView) ((ViewGroup) mNextView).getChildAt(0) : null;
                if (msgTextView != null && msgTextView.getText().length() != msg.length()) {
                    old_toast.cancel();
                    result = null;
                }
            }
            if (result == null) {
                // 用getApplicationContext ，Toast生命周期不会随某Activity Destroy而消失
                result = Toast.makeText(context.getApplicationContext(), null, duration);
            }
            result.setText("" + msg);
            //result.setMargin(100, 100); 调整Toast显示位置
            result.show();
            old_toast = result;
        }catch (Exception e){
            Toast.makeText(context.getApplicationContext(), "" + msg, duration).show();
        }
    }

    /**
     * Activity 跳转
     *
     * @param context
     * @param goal
     */
    public static void skipActivity(Context context, Class<?> goal) {
        Intent intent = new Intent(context, goal);
        context.startActivity(intent);
    }

    /**
     * Activity 跳转
     *
     * @param context
     * @param goal
     */
    public static void skipActivity(Context context, Class<?> goal, Bundle bundle) {
        Intent intent = new Intent(context, goal);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * Activity 跳转
     *
     * @param context
     * @param goal
     */
    public static void skipActivityAndFinish(Context context, Class<?> goal, Bundle bundle) {
        Intent intent = new Intent(context, goal);
        intent.putExtras(bundle);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得状�?�栏的高�?
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状�?�栏
     *
     * @param activity
     * @return
     */
    @SuppressLint("NewApi")
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return bp;

    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率�? px(像素) 的单�? 转成�? dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static float pixelToDp(Context context, float val) {
        float density = context.getResources().getDisplayMetrics().density;
        return val * density;
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static String getTime(String timestamp, String Format) {
        int timeOld = Integer.parseInt(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat(Format);
        String time = null;
        try {
            java.util.Date currentdate = new java.util.Date();// 当前时间

            long i = (currentdate.getTime() / 1000 - timeOld) / (60);
            Timestamp now = new Timestamp(System.currentTimeMillis());// 获取系

            String str = sdf.format(new Timestamp(IntToLong(timeOld)));
            time = str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    private static long IntToLong(int i) {
        long result = (long) i;
        result *= 1000;
        return result;
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context == null)
            return false;

        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        boolean wifi = false, internet = false;
        try {
            wifi = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        } catch (Exception e) {
            Logger.e(TAG, "isWifiNetworkConnected_e=" + e);
        }
        try {
            internet = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        } catch (Exception e) {
            Logger.e(TAG, "isInternetNetworkConnected_e=" + e);
        }
//        if (mNetworkInfo != null) {
//            return mNetworkInfo.isAvailable();
//        }
        return wifi || internet;
    }

    /**
     * 判断网络连接是否是wifi
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null) {
            return activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        } else {
            return false;
        }
    }

    /**
     * 判断网络连接是否是移动数据
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mMobileNetworkInfo != null) {
            return mMobileNetworkInfo.isAvailable();
        }
        return false;
    }

    public static void setEmptyList(List<?> tmp) {
        if (tmp != null) {
            tmp.clear();
            tmp = null;
        }
    }

    public static class Files {
        /**
         * 下载文件
         *
         * @param url  网络url
         * @param path 本地文件存储路径
         * @return 文件
         * @throws Exception
         */
        public static File downLoadFile(final String url, final String path) throws Exception {
            URLConnection conn = (new URL(url)).openConnection();
            InputStream inputStream = conn.getInputStream();
            File file = writeFileFromInput(path, inputStream);
            inputStream.close();
            return file;
        }

        /**
         * 写文件
         *
         * @param filePath
         * @param content
         */
        public static void writeFile(String filePath, String content) {
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(content);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static String readFile(String filePath) {
            try {
                FileInputStream in = new FileInputStream(filePath);
                byte bs[] = new byte[in.available()];
                in.read(bs);
                in.close();
                return new String(bs);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static String readFile(File file) {
            try {
                FileInputStream in = new FileInputStream(file);
                byte bs[] = new byte[in.available()];
                in.read(bs);
                in.close();
                return new String(bs);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static boolean isFileExist(String path, String fileName) {
            File file = new File(path + fileName);
            return file.exists();
        }

        public static boolean isFileExist(String path) {
            File file = new File(path);
            return file.exists();
        }

        public static File write2SDFromInput(String filePath, InputStream inputstream) {
            File file = null;
            OutputStream output = null;
            try {
                file = new File(filePath);
                output = new FileOutputStream(file);
                byte buffer[] = new byte[4 * 1024];
                int temp = 0;
                while ((temp = inputstream.read(buffer)) != -1) {
                    output.write(buffer, 0, temp);
                }
                output.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return file;
        }

        /**
         * 输入流转文件
         *
         * @param filePath
         * @param inputstream
         * @return
         */
        public static File writeFileFromInput(String filePath, InputStream inputstream) throws Exception {
            File file = null;
            OutputStream output = null;

            file = new File(filePath);
            if (file.exists()) {
                FileWriter fw = new FileWriter(file);
                fw.write("");
                fw.close();
            }
            output = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            int temp = 0;
            while ((temp = inputstream.read(buffer)) != -1) {
                output.write(buffer, 0, temp);
            }
            output.flush();
            output.close();

            return file;
        }

        /**
         * @param folderPath
         */
        public static void delFolder(String folderPath) {
            try {
                delAllFile(folderPath); // 删除完里面所有内容
                String filePath = folderPath;
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
                    delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                    flag = true;
                }
            }
            return flag;
        }

        /**
         * 获取指定文件的指定单位的大小
         *
         * @param filePath 文件路径
         * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
         * @return double值的大小
         */
        public static double getFileOrFilesSize(String filePath, int sizeType) {
            File file = new File(filePath);
            long blockSize = 0;
            try {
                if (file.isDirectory()) {
                    blockSize = getFileSizes(file);
                } else {
                    blockSize = getFileSize(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return FormetFileSize(blockSize, sizeType);
        }

        /**
         * 获取指定文件大小
         *
         * @param file
         * @return
         * @throws Exception
         */
        private static long getFileSize(File file) {
            long size = 0;
            if (file.exists()) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    size = fis.available();
                } catch (Exception e) {
                    e.printStackTrace();
                    size = 0;
                } finally {
                    closeStream(fis);
                }
            } else {
                return 0;
            }
            return size;
        }

        /**
         * 获取指定文件夹
         *
         * @param f
         * @return
         * @throws Exception
         */
        public static long getFileSizes(File f) throws Exception {
            long size = 0;
            File flist[] = f.listFiles();
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFileSizes(flist[i]);
                } else {
                    size = size + getFileSize(flist[i]);
                }
            }
            return size;
        }

        /**
         * 转换文件大小,指定转换的类型
         *
         * @param fileS
         * @param sizeType
         * @return
         */
        private static double FormetFileSize(long fileS, int sizeType) {
            DecimalFormat df = new DecimalFormat("#.00");
            double fileSizeLong = 0;
            switch (sizeType) {
                case 1:
                    fileSizeLong = Double.valueOf(df.format((double) fileS));
                    break;
                case 2:
                    fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                    break;
                case 3:
                    fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                    break;
                case 4:
                    fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                    break;
                default:
                    break;
            }
            return fileSizeLong;
        }
    }

    public static String string2Unicode(String string) {
        if (TextUtils.isEmpty(string))
            return "";

        StringBuffer unicode = new StringBuffer();
        boolean isNext = false;
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            if (Integer.toHexString(c).equals("d83d")) {// 转换为unicode
                isNext = true;
                unicode.append("\\u" + Integer.toHexString(c));
            } else if (isNext) {
                isNext = false;
                unicode.append("\\u" + Integer.toHexString(c));
            } else {
                isNext = false;
                unicode.append(c);
            }
        }

        return unicode.toString();
    }

    public static String unicode2String(String unicode) {
        if (TextUtils.isEmpty(unicode))
            return "";

        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        string.append(hex[0]);
        for (int i = 1; i < hex.length; i++) {
            // 转换出每一个代码点
            if (hex[i].length() < 4) {
                string.append(hex[i]);
            } else {
                try {
                    int data = Integer.parseInt(hex[i].substring(0, 4), 16);
                    // 追加成string
                    string.append((char) data);
                    if (hex[i].length() > 4) {
                        string.append(hex[i].substring(4));
                    }
                } catch (Exception e) {
                }
            }
        }

        return string.toString();
    }

    public static String getExtSDCardPath(Context context) {
        String sdCardPath = "";
        String insideStorage = Environment.getExternalStorageDirectory().getPath();
        StorageManager storageManager = (StorageManager) context.getSystemService(Activity.STORAGE_SERVICE);
        try {
            Method methodGetPaths = storageManager.getClass().getMethod("getVolumePaths");
            String[] paths = (String[]) methodGetPaths.invoke(storageManager);
            for (String path : paths) {
                if (!path.equals(insideStorage)) {
                    File file = new File(path);
                    if (file.exists() && file.isDirectory() && file.canWrite() && file.length() > 0) {
                        sdCardPath = path;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sdCardPath;
    }

    public static boolean isMoblePhone(String phone) {
        String nameRule = "[0-9]{11}";
        return phone.matches(nameRule);
    }

    public static String getVersion(Context context) {
        String version;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return version;
    }

    public static boolean isListEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    /**
     * 获取设备号
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static String getSystemVersion(Context context) {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getDeviceName(Context context) {
        return android.os.Build.MODEL;
    }

    public static String getMetaData(Context context, String key) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA).metaData.getString(key);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getNetType(Context context) {
        if (isMobileConnected(context)) {
            if (isWifiConnected(context)) {
                return "wifi";
            } else {
                return "mobile";
            }
        }

        return "mobile";
    }

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private static final int TRANSPARENT = 0x00000000;

    private static String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    public static Bitmap createQrCodeBitmap(String contents, int pixelResolution) throws WriterException {
        if (contents == null || contents.equals("")) {
            return null;
        }
        Bitmap bitmap = null;

        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>(2);
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        // hints.put(EncodeHintType.MARGIN, 1);
        // hints.put(EncodeHintType.CHARACTER_SET, 1);

        BitMatrix result = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, pixelResolution,
                pixelResolution, hints);
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static InputStream getImageByte(String imagePath) throws MalformedURLException {
        URL url = new URL(imagePath);
        HttpURLConnection conn = null;
        InputStream inStream = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            inStream = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inStream;
    }

    /**
     * 输入流转字符串
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static String inputStream2String(InputStream in) throws Exception {
        if (in == null) {
            return "";
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int count = -1;
        while ((count = in.read(data, 0, 4096)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray());
    }

    public static void exitApp(Context context) {
        SystemManager.getInstance().getSystem(SystemPage.class).finishAllActivity();
        DatabaseHelperFramework.getHelper(context).close();
//        SystemManager.getInstance().destoryAllSystem();
        // 如果开发者调用Process.kill或者System.exit之类的方法杀死进程，请务必在此之前调用MobclickAgent.onKillProcess(Context
        // context)方法，用来保存统计数据。
//        MobclickAgent.onKillProcess(context.getApplicationContext());
//        System.exit(0);
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 重启app
     *
     * @param context
     */
    public static void restartApplication(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        exitApp(context);
    }

    public static void closeStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeStream(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasNavigationBar;

    }
    private static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && checkDeviceHasNavigationBar(context)) {
            navigationBarHeight = rs.getDimensionPixelSize(id);
        }
        return navigationBarHeight;
    }
}
