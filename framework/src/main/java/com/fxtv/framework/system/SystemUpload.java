package com.fxtv.framework.system;

import android.content.Context;

import com.fxtv.framework.frame.SystemBase;
import com.fxtv.framework.system.components.UploadComponent;

import java.io.InputStream;

/**
 * 上传文件系统
 * 
 * @author FXTV-Android
 * 
 */
public class SystemUpload extends SystemBase {

	@Override
	public void createSystem(Context context) {
		super.createSystem(context);
	}

	@Override
	public void destroySystem() {
		super.destroySystem();
	}

	/**
	 * 上传文件
	 * 
	 * @param url
	 *            上传服务器地址
	 * @param filePath
	 *            本地文件地址
	 * @param callBack
	 *            回调
	 */
	public void uploadFile(String url, String filePath, IUploadCallBack callBack) {
		UploadComponent uploadComponent = new UploadComponent();
		uploadComponent.upload(url, filePath, callBack);

	}

	/**
	 * 上传文件
	 * 
	 * @param url
	 *            上传服务器地址
	 * @param inputStream
	 *            文件输入流
	 * @param callBack
	 *            回调
	 */
	public void uploadFile(String url, InputStream inputStream, IUploadCallBack callBack) {

	}

	public interface IUploadCallBack {
		public void onSuccess(String imageUrl,String msg);

		public void onFailure(Exception e,String msg);
	}
}
