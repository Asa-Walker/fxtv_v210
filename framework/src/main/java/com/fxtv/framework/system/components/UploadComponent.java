package com.fxtv.framework.system.components;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fxtv.framework.Logger;
import com.fxtv.framework.system.SystemUpload.IUploadCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

/**
 * 上传组件
 * 
 * @author FXTV-Android
 * 
 */
public class UploadComponent extends BaseComponent {
	private String TAG = "UploadComponent";
	private final int SUCCESS=0;
	private final int ERROR=1;
	private IUploadCallBack callBack;
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(callBack==null) return;
			switch (msg.what){
				case SUCCESS:
					JSONObject jsonresult= (JSONObject) msg.obj;
					if(jsonresult!=null){
						try {
							callBack.onSuccess(jsonresult.getString("data"),jsonresult.getString("message"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					break;
				case ERROR:
					Bundle data=msg.getData();
					Serializable e=data.getSerializable("exception");
					callBack.onFailure(e==null?null:(Exception)e,data.getString("message"));
					break;
			}
		}
	};
	// 组件中 编码具体的文件上传代码
	public void upload(final String netUrl, final String filePath, final IUploadCallBack callBack) {
		new Thread() {
			public void run() {
				String boundary = "******";
				String end = "\r\n";
				String twoHyphens = "--";
				DataOutputStream dos = null;
				InputStream is = null;
				try {
					URL url = new URL(netUrl);
					HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
					httpURLConnection.setDoInput(true);
					httpURLConnection.setDoOutput(true);
					httpURLConnection.setUseCaches(false);
					httpURLConnection.setRequestMethod("POST");
					httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
					httpURLConnection.setRequestProperty("Charset", "UTF-8");
					httpURLConnection.setRequestProperty("Content-Type",
							"multipart/form-data;boundary=" + boundary);
					OutputStream outputStream = httpURLConnection.getOutputStream();
					dos = new DataOutputStream(outputStream);
					dos.writeBytes(twoHyphens + boundary + end);
					dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
							+ "user.png" + "\"" + end);
					dos.writeBytes(end);
					FileInputStream fis = new FileInputStream(filePath);
					byte[] buffer = new byte[8192]; // 8k
					int count = 0;
					while ((count = fis.read(buffer)) != -1) {
						dos.write(buffer, 0, count);
					}
					fis.close();
					dos.writeBytes(end);
					dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
					dos.flush();
					// 读取服务器返回结果
					is = httpURLConnection.getInputStream();
					InputStreamReader isr = new InputStreamReader(is, "utf-8");
					BufferedReader br = new BufferedReader(isr);
					String result = br.readLine();
					JSONObject jObject = new JSONObject(result);
					JSONObject data = jObject.getJSONObject("data");
					String icon = data.getString("image");
					callBack.onSuccess(icon,"");
				} catch (Exception e) {
					callBack.onFailure(e,"");
					Logger.d(TAG, e.toString());
				}
			}
		}.start();
	}

	public void upload(final String netUrl,final List<byte[]> imgDatas, final IUploadCallBack callBack) {
		this.callBack=callBack;
		final Message msg=handler.obtainMessage();
		new Thread() {
			public void run() {
				String boundary = "******";
				String end = "\r\n";
				String twoHyphens = "--";
				InputStream is = null;
				try {
					String thisneturl=netUrl;
					//解决中文乱码，表情乱码关键代码
					try {
						String decodedURL = URLDecoder.decode(thisneturl, "UTF-8");
						URL _url = new URL(decodedURL);
						URI _uri = new URI(_url.getProtocol(), _url.getUserInfo(), _url.getHost(), _url.getPort(), _url.getPath(), _url.getQuery(), _url.getRef());
						thisneturl = _uri.toASCIIString();
					} catch (Exception ex) {
						Log.e(TAG, "getUrlWithQueryString encoding URL", ex);
					}
					URL url = new URL(thisneturl);
					Logger.d(TAG, netUrl+" == " + thisneturl);
					HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
					httpURLConnection.setDoInput(true);
					httpURLConnection.setDoOutput(true);
					httpURLConnection.setUseCaches(false);
					httpURLConnection.setRequestMethod("POST");
					httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
					httpURLConnection.setRequestProperty("Charset", "UTF-8");
					httpURLConnection.setRequestProperty("Content-Type",
							"multipart/form-data;boundary=" + boundary);
					DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
					//多图上传关键代码
					if(imgDatas!=null && imgDatas.size()>0) {

						for (int i = 0; i < imgDatas.size(); i++) {
							dos.writeBytes(twoHyphens + boundary + end);
							dos.writeBytes("Content-Disposition: form-data; name=\"file" + i + "\"; filename=\""
									+ "user" + i + ".png" + "\"" + end);
							dos.writeBytes(end);
							dos.write(imgDatas.get(i));
							dos.writeBytes(end);
							dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
						}

					}
					dos.flush();
					dos.close();
					// 读取服务器返回结果
					is = httpURLConnection.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
					String result ="";
					String line;
					while((line=br.readLine())!=null){
						result+=line;
					}
					Logger.d(TAG, "result=" + result);
					JSONObject jsonresult=new JSONObject(result);
					if("2000".equals(jsonresult.getString("code"))){
						msg.what=SUCCESS;
						msg.obj=jsonresult;
					}else{
						msg.what=ERROR;
						Bundle bundle=new Bundle();
						bundle.putString("message", jsonresult.getString("message"));
						msg.setData(bundle);
					}

				} catch (Exception e) {
					msg.what=ERROR;
					Bundle bundle=new Bundle();
					bundle.putString("message","发送失败");
					bundle.putSerializable("exception", e);
					msg.setData(bundle);

					Logger.d(TAG, e.toString());
				}finally{
					handler.sendMessage(msg);
				}
			}
		}.start();
	}

}
