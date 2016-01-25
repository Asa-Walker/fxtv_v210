package com.fxtv.threebears.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.RequestHead;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.threebears.R;
import com.fxtv.threebears.romlite.DatabaseHelper;
import com.fxtv.threebears.system.SystemConfig;
import com.fxtv.threebears.system.SystemPreference;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
	private final static String TAG = "Utils";
	private static Dialog mProgressDialog;

	public static void showProgressDialog(Activity activity) {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			return;
		}

		LayoutInflater inflater = LayoutInflater.from(activity);
		LinearLayout v = (LinearLayout) inflater.inflate(R.layout.progressbar,
				null);// 得到加载view
		// main.xml中的ImageView
		ImageView spaceshipImage = (ImageView) v
				.findViewById(R.id.progress_bar_img);
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				activity, R.anim.progress_bar);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);

		mProgressDialog = new Dialog(activity, R.style.loading_dialog);// 创建自定义样式dialog

		mProgressDialog.setCancelable(true);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		try {
			if (!activity.isFinishing()) {
				mProgressDialog.show();
			}
		} catch (Exception e) {
		}
	}

	public static void dismissProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	public static boolean checkPassWord(String passWord) {
		String passRule = "[a-zA-Z0-9]{5,16}";
		return passWord.matches(passRule);
	}

	public static void copy(String content, Context context) {
		// TODO Auto-generated method stub
		ClipboardManager copy = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		if (content != null) {
			copy.setText(content);
			FrameworkUtils.showToast(context, "复制成功");
		} else {
			FrameworkUtils.showToast(context, "复制出现错误");
		}
	}

	public static String getPasterString(Context context) {
		ClipboardManager plaster = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);

		return plaster.getText().toString().trim();
	}

	/**
	 * 获得view的测量宽高  0 width 1 height
	 * @param view
	 * @return
	 */
	public static int[] getViewWidthHeight(View view){
		String log="";
		int[] wh=new int[]{view.getWidth(),view.getHeight()};
		log+="getWidth="+wh[0]+"*"+wh[1];
		if(wh[0]<=0 && wh[1]<=0){
			int width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
			int height =View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			view.measure(width, height);
			wh[0]=view.getMeasuredWidth();
			wh[1]=view.getMeasuredHeight();
			log+=" measureWidth="+wh[0]+"*"+wh[1];
		}
		if(wh[0]<=0 && wh[1]<=0){
			wh[0]=view.getLayoutParams().width;
			wh[1]=view.getLayoutParams().height;
			log+=" ParamsWidth="+wh[0]+"*"+wh[1];
		}
		Logger.d(TAG, log);
		return wh;
	}
	/**
	 * 显示输入键盘
	 * @param ed_content 键盘焦点输入框
	 */
	public static void setInputVisible(Activity act,EditText ed_content){
		((InputMethodManager)act.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(ed_content, 0);
	}
	/**
	 * 隐藏输入键盘
	 * @param act
	 */
	public static void setInputGone(Activity act){
		//隐藏键盘
		InputMethodManager input=(InputMethodManager)act.getSystemService(Service.INPUT_METHOD_SERVICE);
		View view=act.getCurrentFocus();
		if(input!=null && view!=null)
			input.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * @return 通过相册url得到图片
	 */
	public static Bitmap getBitmapToUrl(Context ctx,Uri url,int maxWidth,int maxHeight){
		if(ctx ==null || url==null)return null;
		try {
			if(maxWidth==0 && maxHeight==0){//不裁剪图片大小
				return MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), url);
			}else{
				return getBitmapToInputStream(ctx.getContentResolver().openInputStream(url),maxWidth,maxHeight);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * @return 通过图片路径得到图片
	 */
	public static Bitmap getBitmapToPath(String imagePath,int maxWidth,int maxHeight){
		// Check if file exists with a FileInputStream
		FileInputStream fis=null;
		try {
			fis=new FileInputStream(imagePath);
			return getBitmapToInputStream(fis,maxWidth,maxHeight);
		} catch (Exception e){
			Logger.d("getBitmapToPath",e.getMessage());
			return null;
		}finally {
			closeStream(fis);
		}
	}
	/**
	 * @return 通过InputStream得到图片
	 */
	public static Bitmap getBitmapToInputStream(InputStream inputStream,int maxWidth,int maxHeight) {
		if (inputStream != null) {
			//inputStream是有序流，只能BitmapFactory.decodeStream()一次，所以要先转byte[]
			if(inputStream instanceof FileInputStream){
				try {
					FileDescriptor fd=((FileInputStream) inputStream).getFD();
					Logger.d("TAG","getBitmapToInputStream fd==null？ "+(fd==null));
					return fileDescriptorToBitmap(fd,maxWidth,maxHeight);
				} catch (IOException e) {
					e.printStackTrace();
					Logger.e("TAG","getBitmapToFileInputStream Exception= "+e.getLocalizedMessage());
				}
			}
			byte[] data = input2ByteArray(inputStream);
			return byteArrayToBitmap(data, maxWidth, maxHeight);
		}
		return null;
	}

	/**
	 * fileDescriptor转Bitmap
	 * @param fd
	 * @param maxWidth
	 * @param maxHight
	 * @return
	 */
	public static Bitmap fileDescriptorToBitmap(FileDescriptor fd,int maxWidth,int maxHight){
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			options.inSampleSize = getSamPleSize(options.outWidth, options.outHeight, maxWidth, maxHight);
			options.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
			if (bitmap == null) {
				Logger.d("TAG","byteArrayToBitmap bitmap==null? 返回原图");
				bitmap = BitmapFactory.decodeFileDescriptor(fd, null, null);
			}
			return bitmap;
		}catch (Exception e ){
			Logger.e(TAG, "byteArrayToBitmap 异常=" + e.getMessage());
			return null;
		}
	}
	/**
	 * byteArray转Bitmap
	 * @param dataArray
	 * @param maxWidth
	 * @param maxHight
	 * @return
	 */
	public static Bitmap byteArrayToBitmap(byte[] dataArray,int maxWidth,int maxHight){
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(dataArray, 0, dataArray.length, options);
			options.inSampleSize = getSamPleSize(options.outWidth, options.outHeight, maxWidth, maxHight);
			options.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeByteArray(dataArray, 0, dataArray.length, options);
			if (bitmap == null) {
				Logger.d("TAG","byteArrayToBitmap bitmap==null? 返回原图");
				bitmap = BitmapFactory.decodeByteArray(dataArray, 0, dataArray.length);
			}
			return bitmap;
		}catch (Exception e ){
			Logger.e(TAG, "byteArrayToBitmap 异常=" + e.getMessage());
			return null;
		}
	}
	/**
	 * bitmap转byte[]
	 * @param bitmap
	 * @return
	 */
	public static byte[] bitmapToByteArray(Bitmap bitmap){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 获得insampleSize，压缩比例
	 * @param outWidth
	 * @param outHeight
	 * @param maxWidth
	 * @param maxHight
	 * @return
	 */
	public static int getSamPleSize(int outWidth, int outHeight, int maxWidth, int maxHight) {
		int inSampleSize=1;
		if(maxWidth!=0 && maxHight!=0){
			while(outWidth/inSampleSize>maxWidth && outHeight/inSampleSize>maxHight){
				inSampleSize*=2;
			}
		}
		Logger.d(TAG, "getSamPleSize=" + inSampleSize + " width=" + outWidth + " height=" + outHeight + " maxW=" + maxWidth + " maxH=" + maxHight);
		return inSampleSize;
	}

	/**
	 * inputTobyte[]
	 * @param inputStream
	 * @return
	 */
	public static byte[] input2ByteArray(InputStream inputStream){
		if(inputStream==null) return null;
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		BufferedOutputStream bos=new BufferedOutputStream(baos);
		byte[] buffer=new byte[1024];
		int len;
		try {
			while((len=inputStream.read(buffer))!=-1){
                bos.write(buffer,0,len);
            }
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			closeStream(bos);
			closeStream(baos);
		}
		return null;
	}

	public static void closeStream(InputStream inputStream){
		if(inputStream!=null){
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void closeStream(OutputStream outputStream){
		if(outputStream!=null){
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 适合 赞数+1，阅读数+1……
	 * @param num num为数字类型，返回num+increament,否则返回num
	 * @param increament
	 * @return
	 */
	public static String getNumberString(String num,long increament){
		try{
			long numL=Long.parseLong(num);
			return ""+(numL+increament);
		}catch(Exception e){
			return num;
		}
	}

	public static void exitApp(Context context) {
		DatabaseHelper.getHelper(context).close();
		FrameworkUtils.exitApp(context);
	}

	/**
	 * 重启app
	 *
	 * @param context
	 */
	public static void restartApplication(Context context) {
		SystemManager.getInstance().destoryAllSystem();
		FrameworkUtils.restartApplication(context, context.getPackageName());
	}

	/**
	 * 常用，填入 module，apiType，JsonObject 参数，
	 * processUrl(ModuleType.INDEX, ApiType.INDEX_menu,params)
	 * @param module
	 * @param apiType
	 * @param paramsJson
	 * @return
	 */
	public static String processUrl(String module,String apiType,com.google.gson.JsonObject paramsJson){//
		String uri;
		if (SystemManager.getInstance().getSystem(SystemConfig.class).DEBUG_ENV) {
			uri = SystemManager.getInstance().getSystem(SystemConfig.class).HTTP_BASE_URL_TEST_2_0;
		} else {
			uri = SystemManager.getInstance().getSystem(SystemConfig.class).HTTP_BASE_URL_2_0;
		}
		return processUrl(uri,module,apiType,paramsJson);
	}

	/**
	 * 上传图片接口
	 * @param module
	 * @param apiType
	 * @param paramsJson
	 * @return
	 */
	public static String processUrlForPhoto(String module,String apiType,com.google.gson.JsonObject paramsJson) {
		SystemConfig config=SystemManager.getInstance().getSystem(SystemConfig.class);
		String url=config.HTTP_BASE_URL_2_0_FOR_PHOTO;
		if(config.DEBUG_ENV){
			url=config.HTTP_BASE_URL_2_0_FOR_PHOTO_TEST;
		}
		return processUrl(url, module, apiType, paramsJson);
	}
	/**
	 * 拼接到指定uri上
	 * @param uri
	 * @param module
	 * @param apiType
	 * @param paramsJson
	 * @return
	 */
	public static String processUrl(String uri,String module,String apiType,com.google.gson.JsonObject paramsJson){
		RequestHead head=new RequestHead(module,apiType);
		head.params=paramsJson;
		head.uri=uri;
		head.uc = SystemManager.getInstance().getSystem(SystemPreference.class).getUC(2);
		if (TextUtils.isEmpty(head.uc)) {
			head.uc = SystemManager.getInstance().getSystem(SystemPreference.class).getUC(0);
		}
		return SystemManager.getInstance().getSystem(SystemHttp.class).processUrl(head);

	}
	public static void setVideoLogo(ImageView imPrize,ImageView imLogo,String status){
		if ("1".equals(status)) {
			imPrize.setVisibility(View.VISIBLE);
			imLogo.setVisibility(View.GONE);
			imPrize.setImageResource(R.drawable.icon_prize);
		}else if("2".equals(status)){
			imPrize.setVisibility(View.VISIBLE);
			imLogo.setVisibility(View.GONE);
			imPrize.setImageResource(R.drawable.icon_prize_open);
		} else {
			imPrize.setVisibility(View.GONE);
			imLogo.setVisibility(View.VISIBLE);
		}
	}

}
