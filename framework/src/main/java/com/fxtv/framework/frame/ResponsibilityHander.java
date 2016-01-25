package com.fxtv.framework.frame;

/**
 * 
 * 责任链处理者
 * 
 * @author FXTV-Android
 * 
 */
public abstract class ResponsibilityHander {

	protected ResponsibilityHander mSuccessor;

	/**
	 * 示意处理请求的方法，虽然这个示意方法是没有传入参数的 但实际是可以传入参数的，根据具体需要来选择是否传递参数
	 */
	public abstract void handleRequest(boolean handle);

	/**
	 * 取值方法
	 */
	public ResponsibilityHander getSuccessor() {
		return mSuccessor;
	}

	/**
	 * 赋值方法，设置后继的责任对象
	 */
	public void setSuccessor(ResponsibilityHander successor) {
		this.mSuccessor = successor;
	}

}
