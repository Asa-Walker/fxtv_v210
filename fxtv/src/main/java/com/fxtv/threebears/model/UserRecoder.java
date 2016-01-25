package com.fxtv.threebears.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tb_user_recoder")
public class UserRecoder {

	@DatabaseField(generatedId = true)
	public int id;

	/**
	 * 用户名
	 */
	@DatabaseField(columnName = "userName")
	public String userName;
	/**
	 * 密码
	 */
	@DatabaseField(columnName = "passWord")
	public String passWord;
	/**
	 * 登录返回的内容
	 */
	@DatabaseField(columnName = "content")
	public String content;
	/**
	 * 是否是默认账户
	 */
	@DatabaseField(columnName = "defaultUser")
	public boolean defaultUser;
	/**
	 * 是否登出
	 */
	@DatabaseField(columnName = "logout")
	public boolean logout;
	/**
	 * 0:正常登录
	 * 
	 * 1：短信登录
	 * 
	 * 2：qq登录
	 * 
	 * 3：新浪登录
	 * 
	 * 4：微信登录
	 */
	@DatabaseField(columnName = "loginType")
	public int loginType;
	/**
	 * 第三方登录的id
	 */
	@DatabaseField(columnName = "thirdLoginId")
	public String thirdLoginId;

}
