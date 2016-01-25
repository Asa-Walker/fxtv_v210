package emojicon;
import java.io.InputStream;
import java.net.URL;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html.ImageGetter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

public class URLImageParser implements ImageGetter {

	Context ctx;
	TextView tv_image;
	static float density=-1 ;
	public static int textMaxWidth=0;
	public URLImageParser(TextView textView,Context ctx) {
		this.tv_image = textView;
		this.ctx=ctx;
		//获取手机屏幕分辨率
		if(density==-1){
			DisplayMetrics dm  = ctx.getResources().getDisplayMetrics();
			density=dm.density;
		}
		if(textMaxWidth<=0){
			textMaxWidth=textView.getWidth();
			if(textMaxWidth<=0)
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					textMaxWidth=textView.getMaxWidth();
				}
			if(textMaxWidth<=0)
				textMaxWidth=textView.getMeasuredWidth();
			if(textMaxWidth<=0)
				textMaxWidth=textView.getLayoutParams().width;
			Log.i("BOYS", "手机密度=" + density+" TextView最大宽="+textMaxWidth);
		}
	}

	@Override
	public Drawable getDrawable(String source) {
		// TODO Auto-generated method stub
		URLDrawable urlDrawable = new URLDrawable();
		ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable);
		asyncTask.execute(source);

		return urlDrawable;
	}

	public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
		URLDrawable urlDrawable;
		public ImageGetterAsyncTask(URLDrawable d) {
			this.urlDrawable = d;
		}

		@Override
		protected void onPostExecute(Drawable result) {
			if (result != null) {
				//图片实际大小*手机密度
				int intrinsicWidth=(int)(result.getIntrinsicWidth()*density);
				int intrinsicHeight=(int)(result.getIntrinsicHeight()*density);
				String log="";
				log+="原来"+intrinsicWidth+"*"+intrinsicHeight;
				if(intrinsicWidth>textMaxWidth && textMaxWidth>0){//超过TextView最大宽，压缩一下
					double scale=((double)textMaxWidth)/intrinsicWidth;
					log+="比例="+scale;
					intrinsicHeight=(int)(scale*(double)intrinsicHeight);
					intrinsicWidth=textMaxWidth;
				}
				log+=" 压缩后"+intrinsicWidth+"*"+intrinsicHeight;
				Log.i("BOYS","webimg"+log);

				urlDrawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
				urlDrawable.drawable = result;
				urlDrawable.drawable.setBounds(urlDrawable.getBounds());
				tv_image.setText(tv_image.getText());//文字图片重叠关键代码

			}
		}

		@Override
		protected Drawable doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				if(params!=null && params.length>0){
					Drawable drawable = Drawable.createFromStream(new URL(params[0]).openStream(), "src");
					return drawable;
				}
			} catch (Exception e) {
				return null;
			}
			return null;
		}

	}

}
