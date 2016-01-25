package master.flame.danmaku.danmaku.loader.android;

import android.content.Context;
import android.net.Uri;

import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.SystemHttp;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import master.flame.danmaku.danmaku.loader.ILoadCallBack;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.parser.android.JSONSource;

/**
 * Ac danmaku loader
 *
 * @author yrom
 */
public class SelfDanmakuLoader implements ILoader {
    private static final String TAG = "SelfDanmakuLoader";

    private SelfDanmakuLoader() {
    }

    private static volatile SelfDanmakuLoader instance;
    private JSONSource dataSource;

    public static ILoader instance() {
        if (instance == null) {
            synchronized (SelfDanmakuLoader.class) {
                if (instance == null)
                    instance = new SelfDanmakuLoader();
            }
        }
        return instance;
    }

    @Override
    public JSONSource getDataSource() {
        return dataSource;
    }

    @Override
    public void load(String uri) throws IllegalDataException {
        try {
            dataSource = new JSONSource(Uri.parse(uri));
        } catch (Exception e) {
            throw new IllegalDataException(e);
        }
    }

    @Override
    public void load(InputStream in) throws IllegalDataException {
        try {
            dataSource = new JSONSource(in);
        } catch (Exception e) {
            throw new IllegalDataException(e);
        }
    }

    public void load(Context context, String url, final ILoadCallBack callBack) {

        SystemManager.getInstance().getSystem(SystemHttp.class).get(context, url, "getDanmaku", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                try {
                    Logger.d(TAG, "load successful");
                    InputStream io = byteTOInputStream(bytes);
                    dataSource = new JSONSource(io);
                    callBack.onLoadCallBack(true);
                } catch (Exception e) {
                    Logger.d(TAG, "load error,code=0");
                    e.printStackTrace();
                    callBack.onLoadCallBack(false);
                }
            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                throwable.printStackTrace();
                Logger.d(TAG, "load error,code=1");
                callBack.onLoadCallBack(false);
            }
        });

    }

    /**
     * ��byte����ת����InputStream
     *
     * @param in
     * @return
     * @throws Exception
     */
    private InputStream byteTOInputStream(byte[] in) throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(in);
        return is;
    }

}
