package com.fxtv.framework.system.components;

/**
 * 登录组件
 * 
 * @author Android2
 * 
 */
public class LoginComponent extends BaseComponent {

	/**
	 * 第三方登录
	 * 
	 * @param context
	 *            --当前activity
	 * @param loginType
	 *            --登录方式
	 * @param actionListener
	 *            --监听用于回调
	 */
	/*public void thridLogin(Context context, Platform loginType,
			PlatformActionListener actionListener) {
		if (FrameworkUtils.isNetworkConnected(context)) {
			if (loginType.isValid()) {
				loginType.removeAccount(false);
			}
			loginType.setPlatformActionListener(actionListener);
			loginType.SSOSetting(false);
			loginType.showUser(null);
		} else {
			FrameworkUtils.showToast(context, "网络未连接");
		}
	}*/

}
