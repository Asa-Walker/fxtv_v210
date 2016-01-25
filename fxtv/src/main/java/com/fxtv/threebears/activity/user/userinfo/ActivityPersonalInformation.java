package com.fxtv.threebears.activity.user.userinfo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemFrameworkConfig;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.SystemUpload;
import com.fxtv.framework.system.SystemUpload.IUploadCallBack;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.user.login.ActivityLogin;
import com.fxtv.threebears.model.CityModel;
import com.fxtv.threebears.model.ProvinceModel;
import com.fxtv.threebears.model.User;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemConfig;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.util.XmlParserHandler;
import com.fxtv.threebears.view.MyDialog;
import com.fxtv.threebears.view.wheel.TosGallery;
import com.fxtv.threebears.view.wheel.TosGallery.OnEndFlingListener;
import com.fxtv.threebears.view.wheel.WheelView;
import com.fxtv.threebears.view.wheelview.MyWheelView;
import com.fxtv.threebears.view.wheelview.OnWheelChangedListener;
import com.fxtv.threebears.view.wheelview.adapters.ArrayWheelAdapter;
import com.google.gson.JsonObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 个人信息界面
 * 
 * @author 薛建浩
 * 
 */
public class ActivityPersonalInformation extends BaseActivity implements OnWheelChangedListener {
	private ImageView mUserImage, temp;
	private TextView mUserName, mPhoneNumber, mBirthday, mSex, mLocation, mQQ, mIntrestingGames;
	private PopupWindow mPopupWindow, mDatePopupWindow, mLocationPopupWindow, mImagePopupWindow;
	private TosGallery.OnEndFlingListener mDateLinstener;
	private int CROP_IMAGE = 1991;
	private int TAKE_FROM_PHOTO = 1994;
	private int GET_IMAGE_FROME_PHOTO = 1995;
	private ArrayList<DateInfo> mDayList = null;
	private ArrayList<DateInfo> mMotnthList = null;
	private ArrayList<DateInfo> mYearList = null;
	private WheelView mDayWheel = null;
	private WheelView mMonthWheel = null;
	private WheelView mYearWheel = null;
	private MyWheelView mProvinceWheel = null;
	private MyWheelView mCityWheel = null;
	public static final String path = Environment.getExternalStorageDirectory().toString() + "/fxtv/userImage/";
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1993) {
				getSystem(SystemUser.class).mUser.image = msg.obj.toString();
				getAdavatarBitmap(msg.obj.toString(), mUserImage);
			}
		};
	};
	/**
	 * 当前的省的名字
	 */
	private String mCurrentProviceName;
	/**
	 * 当前城市的名字
	 */
	private String mCurrentCityName;
	/**
	 * 所有省
	 */
	private String[] mProvinceDatas;
	/**
	 * key - 省 value - 市
	 */
	private Map<String, String[]> mCitisDatasMap = null;
	int mCurDate = 0;
	int mCurMonth = 0;
	int mCurYear = 0;
	private String[] MONTH_NAME = null;
	private int[] DAYS_PER_MONTH = null;
	private String mPath;
	private EditText mRecondCode;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (!getSystem(SystemUser.class).isLogin()) {
			FrameworkUtils.skipActivity(ActivityPersonalInformation.this, ActivityLogin.class);
			return;
		}
		super.onCreate(savedInstanceState);
		// 测试用地址
		JsonObject params = new JsonObject();
		mPath=Utils.processUrlForPhoto(ModuleType.USER,ApiType.USER_uploadImage,params);
		setContentView(R.layout.activity_personal_information);
		initView();
	}

	// 友盟统计
	@Override
	protected void onPause() {
		super.onPause();
	}

	private void initView() {
		initActionbar();
		// 初始化个人信息
		initPersonalInfo();
	}

	/**
	 * 初始化用户信息
	 */
	private void initPersonalInfo() {
		user = getSystem(SystemUser.class).mUser;
		mUserName = (TextView) findViewById(R.id.activity_personal_information_name);
		mUserImage = (ImageView) findViewById(R.id.activity_personal_information_user_pic);
		mPhoneNumber = (TextView) findViewById(R.id.activity_personal_information_phone_number);
		mBirthday = (TextView) findViewById(R.id.activity_personal_information_birthday);
		mLocation = (TextView) findViewById(R.id.activity_personal_information_location);
		mQQ = (TextView) findViewById(R.id.activity_personal_information_qq);
		mSex = (TextView) findViewById(R.id.activity_personal_information_sex);
		mIntrestingGames = (TextView) findViewById(R.id.activity_personal_information_intresting);
		mRecondCode = (EditText) findViewById(R.id.activity_personal_information_code);
		mRecondCode.setText(judgeValue(user.recommend_code));
		mUserName.setText(user.nickname);
		mBirthday.setText(judgeValue(user.birthday));
		mLocation.setText(judgeValue(user.address));
		mQQ.setText(judgeValue(user.qq));
		mSex.setText(judgeValue(user.sex));
		mIntrestingGames.setText(judgeValue(user.intro));
		if (getSystem(SystemUser.class).mUser.phone.equals("")) {
			mPhoneNumber.setText("绑定手机");
		} else {
			mPhoneNumber.setText(getSystem(SystemUser.class).mUser.phone);
		}
		getAdavatarBitmap(getSystem(SystemUser.class).mUser.image, mUserImage);
		// 选择头像
		findViewById(R.id.activity_personal_information).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// FrameworkUtils.skipActivity(ActivityPersonalInformation.this,
				// ActivityImageDepot.class);
				showImagePop(v);
			}
		});
		mUserName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ("0".equals(user.modified_nickname)) {
					initDialog("请修改昵称");
				}
			}
		});
		mPhoneNumber.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FrameworkUtils.skipActivity(ActivityPersonalInformation.this, ActivityBindPhone.class);
			}
		});
		// 生日
		findViewById(R.id.activity_personal_information_birthday_layout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePopWindow(v);
			}
		});
		// 性别
		findViewById(R.id.activity_personal_information_sex_layout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopWindow(v);
			}
		});
		// 所在地
		findViewById(R.id.activity_personal_information_location_layout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// initDialog("请输入所在地");
				showLocationPop(v);
			}
		});
		// qq
		findViewById(R.id.activity_personal_information_qq_layout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initDialog("请输入QQ");
			}
		});
		// 感兴趣的游戏
		findViewById(R.id.activity_personal_information_intresting_layout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityPersonalInformation.this, ActivityIntrestingGame.class);
				intent.putExtra("intro",user.intro);
				startActivityForResult(intent, 0);
			}
		});
		((ScrollView)findViewById(R.id.scroll_view)).smoothScrollTo(0,0);
	}

	public boolean ExistSDCard() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	// 选择头像的方式
	private void showImagePop(View parent) {
		if (mImagePopupWindow != null && !mImagePopupWindow.isShowing()) {
			mImagePopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		} else {
			ViewGroup layout = (ViewGroup) View.inflate(this, R.layout.pop_image, null);
			mImagePopupWindow = new PopupWindow(layout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
			mImagePopupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.touming_black_color));
			temp = (ImageView) layout.findViewById(R.id.img);
			// 拍照
			layout.findViewById(R.id.take_photo).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, TAKE_FROM_PHOTO);
				}
			});
			// 相册照片
			layout.findViewById(R.id.take_from_photo).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intentFromGallery = new Intent();
					intentFromGallery.setType("image/*"); // 设置文件类型
					intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intentFromGallery, GET_IMAGE_FROME_PHOTO);

					/*if (Build.VERSION.SDK_INT < 19) {
						Intent intent = new Intent();
						intent.setType("image*//*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(Intent.createChooser(intent, "Select Picture"), GET_IMAGE_FROME_PHOTO);
					}
					else {
						Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
						intent.addCategory(Intent.CATEGORY_OPENABLE);
						intent.setType("image/jpeg");
						startActivityForResult(intent, GET_IMAGE_FROME_PHOTO);
					}*/
				}
			});
			// 系统照片
			layout.findViewById(R.id.take_sys_photo).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					FrameworkUtils.skipActivity(ActivityPersonalInformation.this, ActivityImageDepot.class);
				}
			});
			// 取消
			layout.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mImagePopupWindow.dismiss();
				}
			});

			mImagePopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		}
	}

	// 地址
	private void showLocationPop(View parent) {
		if (mLocationPopupWindow != null && !mLocationPopupWindow.isShowing()) {
			mLocationPopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		} else {
			createLoactionPopWindow(parent);
		}
	}

	private void createLoactionPopWindow(View parent) {
		ViewGroup layout = (ViewGroup) View.inflate(this, R.layout.pop_location, null);
		mLocationPopupWindow = new PopupWindow(layout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		mLocationPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.touming_black_color));
		// 取消
		layout.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mLocationPopupWindow.dismiss();
			}
		});
		// 确定
		layout.findViewById(R.id.confirm).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 所在地
				int pCurrent = mProvinceWheel.getCurrentItem();
				mCurrentProviceName = mProvinceDatas[pCurrent];
				int cityCurrent = mCityWheel.getCurrentItem();
				mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[cityCurrent];//重新获取一遍数据

				Logger.d("TAG","选择所在地："+mCurrentProviceName+"-"+mCurrentCityName);
				submitUserInfo("3", mCurrentProviceName + mCurrentCityName);
				mLocationPopupWindow.dismiss();
			}
		});
		mProvinceWheel = (MyWheelView) layout.findViewById(R.id.province);
		mCityWheel = (MyWheelView) layout.findViewById(R.id.city);
		// 设置是否加阴影
		mProvinceWheel.setDrawShadows(false);
		mCityWheel.setDrawShadows(false);
		// 添加change事件
		mProvinceWheel.addChangingListener(this);
		// 添加change事件
		mCityWheel.addChangingListener(this);
		initProvinceDatas();
		// 设置可见条目数量
		mProvinceWheel.setVisibleItems(7);
		mCityWheel.setVisibleItems(7);
		mProvinceWheel.setViewAdapter(new ArrayWheelAdapter<String>(ActivityPersonalInformation.this, mProvinceDatas));
		// 更新数据
		updateCities();
		mLocationPopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

	}

	/**
	 * 从本地文件读取城市数据,用sax解析，速度快，性能高
	 */
	private void initProvinceDatas() {
		List<ProvinceModel> provinceList = null;
		AssetManager asset = getAssets();
		try {
			InputStream input = asset.open("province_data.xml");
			// 创建一个解析xml的工厂对象
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// 解析xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			// 获取解析出来的数据
			provinceList = handler.getDataList();
			// */ 初始化默认选中的省、市、区
			if (provinceList != null && !provinceList.isEmpty()) {
				mCurrentProviceName = provinceList.get(0).getName();
				List<CityModel> cityList = provinceList.get(0).getCityList();
				if (cityList != null && !cityList.isEmpty()) {
					mCurrentCityName = cityList.get(0).getName();
					// List<DistrictModel> districtList =
					// cityList.get(0).getDistrictList();
					// mCurrentDistrictName = districtList.get(0).getName();
				}
			}
			int len=provinceList==null?0:provinceList.size();
			mProvinceDatas = new String[len];
			for (int i = 0; i < len; i++) {
				// 遍历所有省的数据
				mProvinceDatas[i] = provinceList.get(i).getName();
				List<CityModel> cityList = provinceList.get(i).getCityList();
				String[] cityNames = new String[cityList.size()];
				for (int j = 0; j < cityList.size(); j++) {
					// // 遍历省下面的所有市的数据
					cityNames[j] = cityList.get(j).getName();
					// // List<DistrictModel> districtList =
					// // cityList.get(j).getDistrictList();
					// // String[] distrinctNameArray = new
					// // String[districtList.size()];
					// // DistrictModel[] distrinctArray = new
					// // DistrictModel[districtList.size()];
					// for (int k=0; k<districtList.size(); k++) {
					// // 遍历市下面所有区/县的数据
					// DistrictModel districtModel = new
					// DistrictModel(districtList.get(k).getName(),
					// districtList.get(k).getZipcode());
					// // 区/县对于的邮编，保存到mZipcodeDatasMap
					// mZipcodeDatasMap.put(districtList.get(k).getName(),
					// districtList.get(k).getZipcode());
					// distrinctArray[k] = districtModel;
					// distrinctNameArray[k] = districtModel.getName();
					// }
					// // 市-区/县的数据，保存到mDistrictDatasMap
					// mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
				}
				// 省-市的数据，保存到mCitisDatasMap
				if (mCitisDatasMap == null) {
					mCitisDatasMap = new HashMap<String, String[]>();
				}
				mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
		}
	}

	// 判断用户信息里的内容是否为空
	private String judgeValue(String value) {
		return (value == null) ? "" : value;
	}

	private void showDatePopWindow(View parent) {
		if (mDatePopupWindow != null && !mDatePopupWindow.isShowing()) {
			mDatePopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		} else {
			createDatePopWindow(parent);
		}
	}

	/**
	 * 格式化日期
	 * 
	 * @return
	 */
	private String formatDate() {
		return String.format("Date: %d/%02d/%02d", mCurYear, mCurMonth + 1, mCurDate);
	}

	private void setDate(int date) {
		if (date != mCurDate) {
			mCurDate = date;
		}
	}

	private void setYear(int year) {
		if (year != mCurYear) {
			mCurYear = year;
			Calendar calendar = Calendar.getInstance();
			int date = calendar.get(Calendar.DATE);
			prepareDayData(mCurYear, mCurMonth, date);
		}
	}

	private void setMonth(int month) {
		if (month != mCurMonth) {
			mCurMonth = month;
			Calendar calendar = Calendar.getInstance();
			int date = calendar.get(Calendar.DATE);
			prepareDayData(mCurYear, month, date);
		}
	}

	private void prepareDayData(int year, int month, int date) {
		mDayList.clear();
		int days = DAYS_PER_MONTH[month];
		// The February.
		if (1 == month) {
			days = isLeapYear(year) ? 29 : 28;
		}
		for (int i = 1; i <= days; ++i) {
			// mDates.add(new TextInfo(i, String.valueOf(i), (i == curDate)));
			mDayList.add(new DateInfo(i, i + "日", (i == date)));
		}
		((WheelAdapter) mDayWheel.getAdapter()).setListData(mDayList);
	}

	private boolean isLeapYear(int year) {
		return ((0 == year % 4) && (0 != year % 100) || (0 == year % 400));
	}

	private void showPopWindow(View parent) {
		if (mPopupWindow != null && !mPopupWindow.isShowing()) {
			mPopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		} else {
			createPopWindow(parent);
		}
	}

	private void createDatePopWindow(View parent) {
		ViewGroup layout = (ViewGroup) View.inflate(this, R.layout.pop_date_choose, null);
		mDatePopupWindow = new PopupWindow(layout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		mDatePopupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.touming_black_color));
		mYearWheel = (WheelView) layout.findViewById(R.id.year);
		mMonthWheel = (WheelView) layout.findViewById(R.id.month);
		mDayWheel = (WheelView) layout.findViewById(R.id.day);
		if (mDateLinstener == null) {
			mDateLinstener = new OnEndFlingListener() {
				public void onEndFling(TosGallery v) {
					getDate(v);
				}
			};
		}
		mDayWheel.setOnEndFlingListener(mDateLinstener);
		mMonthWheel.setOnEndFlingListener(mDateLinstener);
		mYearWheel.setOnEndFlingListener(mDateLinstener);
		mDayWheel.setSoundEffectsEnabled(true);
		mMonthWheel.setSoundEffectsEnabled(true);
		mYearWheel.setSoundEffectsEnabled(true);
		mDayWheel.setAdapter(new WheelAdapter(this,mDayList));
		mMonthWheel.setAdapter(new WheelAdapter(this,mMotnthList));
		mYearWheel.setAdapter(new WheelAdapter(this,mYearList));
		prepareDate();
		// 取消
		layout.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDatePopupWindow.dismiss();
			}
		});
		// 确定
		layout.findViewById(R.id.confirm).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getDate(mDayWheel);
				getDate(mMonthWheel);
				getDate(mYearWheel);

				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");

				String date=mCurYear+"-"+String.format("%02d",mCurMonth)+"-"+String.format("%02d",mCurDate);//补0

				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DATE);
				try {
					long getTime=format.parse(date).getTime();
					long nowTime=format.parse(String.format("%04d",year)+"-"+String.format("%02d",month)+"-"+String.format("%02d",day)).getTime();

					//Logger.d("TAG","getTime="+getTime +" == "+nowTime);
					if(getTime>nowTime){
						showToast("生日不能选择未来时间!");
						return;
					}
				} catch (ParseException e) {
					e.printStackTrace();
					return;
				}
				String date1=mCurYear+"-"+String.format("%02d",((mCurMonth + 1)))+"-"+String.format("%02d",mCurDate);//补0
				//Logger.d("TAG","选择日期为="+date1);
				submitUserInfo("1",date1);
				mDatePopupWindow.dismiss();
			}
		});
		mDatePopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

	}
	private void getDate(TosGallery v){
		int pos = v.getSelectedItemPosition();
		if (v == mDayWheel) {
			DateInfo info = mDayList.get(pos);
			setDate(info.mIndex);
		} else if (v == mMonthWheel) {
			DateInfo info = mMotnthList.get(pos);
			setMonth(info.mIndex);
		} else if (v == mYearWheel) {
			DateInfo info = mYearList.get(pos);
			setYear(info.mIndex);
		}
	}
	private void updateCities() {
		int pCurrent = mProvinceWheel.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[]{""};
		}
		mCityWheel.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		mCityWheel.setCurrentItem(0);
	}

	private void updateLocation() {
		int pCurrent = mCityWheel.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
	}

	private void prepareDate() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		int startYear = 1905;
		int endYear = year;
		mCurDate = day;
		mCurMonth = month;
		mCurYear = year;
		MONTH_NAME = new String[]{"01月", "02月", "03月", "04月", "05月", "06月", "07月", "08月", "09月", "10月", "11月", "12月",};
		DAYS_PER_MONTH = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		mMotnthList = new ArrayList<DateInfo>();
		mYearList = new ArrayList<DateInfo>();
		mDayList = new ArrayList<DateInfo>();
		for (int i = 0; i < MONTH_NAME.length; ++i) {
			mMotnthList.add(new DateInfo(i, MONTH_NAME[i], (i == month)));
		}
		for (int i = startYear; i <= endYear; ++i) {
			mYearList.add(new DateInfo(i, i + "年", (i == year)));
		}
		((WheelAdapter) mMonthWheel.getAdapter()).setListData(mMotnthList);
		((WheelAdapter) mYearWheel.getAdapter()).setListData(mYearList);
		prepareDayData(year, month, day);
		mMonthWheel.setSelection(month);
		mYearWheel.setSelection(year - startYear);
		mDayWheel.setSelection(day - 1);
	}

	private void createPopWindow(View parent) {
		ViewGroup layout = (ViewGroup) View.inflate(this, R.layout.pop_sex_choose, null);
		mPopupWindow = new PopupWindow(layout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.touming_black_color));
		// 男
		layout.findViewById(R.id.male).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitUserInfo("2", "男");
				mPopupWindow.dismiss();
			}
		});
		// 女
		layout.findViewById(R.id.female).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitUserInfo("2", "女");
				mPopupWindow.dismiss();
			}
		});
		// 取消
		layout.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
			}
		});
		mPopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
	}

	/**
	 * 请求接口
	 * 
	 * @param infoType
	 *            信息的种类(1：生日；2：性别；3：所在地；4：QQ；5：感兴趣的游戏)
	 * @param userInfo
	 *            用户信息
	 */
	private void submitUserInfo(final String infoType, final String userInfo) {
		JsonObject params = new JsonObject();
		params.addProperty("type", infoType);
		params.addProperty("value", userInfo);
		Utils.showProgressDialog(this);

		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_setUserInfo, params), "modifyUserInfo", false, false, new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				showToast(resp.msg);
				changeUserInfo(infoType, userInfo);
			}

			@Override
			public void onFailure(Response resp) {
				showToast(resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
			}
		});

	}

	/**
	 * 修改用户信息
	 * 
	 * @param infoType
	 *            信息的种类(1：生日；2：性别；3：所在地；4：QQ；5：感兴趣的游戏)
	 * @param userInfo
	 */
	private void changeUserInfo(String infoType, String userInfo) {
		int type = Integer.parseInt(infoType);
		switch (type) {
			case 1 :
				mBirthday.setText(userInfo);
				getSystem(SystemUser.class).mUser.birthday = userInfo;
				break;
			case 2 :
				mSex.setText(userInfo);
				getSystem(SystemUser.class).mUser.sex = userInfo;
				break;
			case 3 :
				mLocation.setText(userInfo);
				getSystem(SystemUser.class).mUser.address = userInfo;
				break;
			case 4 :
				mQQ.setText(userInfo);
				getSystem(SystemUser.class).mUser.qq = userInfo;
				break;
			case 5 :
				mIntrestingGames.setText(userInfo);
				getSystem(SystemUser.class).mUser.intro = userInfo;
				break;
			default :
				break;
		}
	}

	protected void initDialog(final String title) {
		getSystem(SystemCommon.class)
				.showDialog(ActivityPersonalInformation.this, title, new MyDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog, View view, String value) {
						if (!value.trim().equals("")) {
							if ("请修改昵称".equals(title)) {
								modifyNickName(value);
							}
							if ("请输入所在地".equals(title)) {
								submitUserInfo("3", value);
							}
							if ("请输入QQ".equals(title)) {
								submitUserInfo("4", value);
							}
						}
						dialog.dismiss();
					}
				}, new MyDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog, View view, String value) {
						dialog.dismiss();
					}
				}, true);
	}

	/**
	 * 修改昵称
	 */
	protected void modifyNickName(final String nickName) {
		JsonObject params = new JsonObject();
		params.addProperty("nickname", nickName);
		Utils.showProgressDialog(this);
		/*String url = processUrl("User", "modifyNickname", params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, urlcallBack);*/

		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_modifyNickname, params), "modifyNickName", false, false, new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				mUserName.setText(nickName);
				// getSystem(SystemUser.class).updateUserNick(nickName);
				getSystem(SystemUser.class).mUser.nickname = nickName;
				getSystem(SystemUser.class).mUser.modified_nickname = "1";
				user.modified_nickname = "1";
				FrameworkUtils.showToast(ActivityPersonalInformation.this, resp.msg);
			}

			@Override
			public void onFailure(Response resp) {
				showToast(resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
			}
		});

	}

	private void initActionbar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		title.setText("个人信息");
		ImageView btnBack = (ImageView) findViewById(R.id.ab_left_img);
		btnBack.setImageResource(R.drawable.icon_arrow_left1);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 获取登录用户的头像
	 */
	private void getAdavatarBitmap(String url, ImageView view) {
		//SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageSquare(url, view);
		getSystem(SystemCommon.class).displayDefaultImage(ActivityPersonalInformation.this,view,url);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getAdavatarBitmap(getSystem(SystemUser.class).mUser.image, mUserImage);
		if (getSystem(SystemUser.class).mUser.phone.equals("")) {
			mPhoneNumber.setText("绑定手机");
		} else {
			mPhoneNumber.setText(getSystem(SystemUser.class).mUser.phone);
		}
	}

	/**
	 * 日期对象
	 * 
	 * @author Android2
	 * 
	 */
	protected class DateInfo {
		public DateInfo(int index, String text, boolean isSelected) {
			mIndex = index;
			mText = text;
			mIsSelected = isSelected;
		}

		public int mIndex;
		public String mText;
		public boolean mIsSelected = false;
		public int mColor = Color.BLACK;
	}

	protected class WheelAdapter extends BaseListGridAdapter<DateInfo> {
		int mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
		int mHeight = 50;
		Context mContext = null;

		public WheelAdapter(Context context,List<DateInfo> list) {
			super(list);
			mContext = context;
			float density = context.getResources().getDisplayMetrics().density;
			mHeight = (int) (mHeight * density);
		}

		public void setItemSize(int width, int height) {
			mWidth = width;
			float density = mContext.getResources().getDisplayMetrics().density;
			mHeight = (int) (mHeight * density);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = null;
			if (null == convertView) {
				convertView = new TextView(mContext);
				convertView.setLayoutParams(new TosGallery.LayoutParams(mWidth, mHeight));
				textView = (TextView) convertView;
				textView.setGravity(Gravity.CENTER);
				textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
				textView.setTextColor(Color.BLACK);
			}
			if (null == textView) {
				textView = (TextView) convertView;
			}
			if(position<=getCount()){
				DateInfo info = getItem(position);
				textView.setText(info.mText);
				textView.setTextColor(info.mColor);
			}
			return convertView;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1990) {
			mIntrestingGames.setText(data.getStringExtra("userInfo"));
			getSystem(SystemUser.class).mUser.intro = data.getStringExtra("userInfo");
		}
		if (resultCode == RESULT_OK && data != null) {
			if (requestCode == TAKE_FROM_PHOTO) {
				Uri uri = getBitmapUri((Bitmap) data.getExtras().get("data"));
				if (uri != null) {
					cropPhoto(uri);
				} else {
					showToast("无法获取图片地址");
				}
			} else if (requestCode == CROP_IMAGE) {
				if(mImagePopupWindow!=null)
					mImagePopupWindow.dismiss();
				Bitmap bitmap = data.getParcelableExtra("data");
				goTo(bitmap);
			} else if (requestCode == GET_IMAGE_FROME_PHOTO) {
				Uri uri=data.getData();//content://com.android.providers.media.documents/document/image%3A327871
				if(uri.toString().contains("content://")){
					Bitmap bitmap=Utils.getBitmapToUrl(this,uri,SystemConfig.POST_IMG_MAX_W, SystemConfig.POST_IMG_MAX_H);
					if(bitmap!=null){
						uri=getBitmapUri(bitmap);
					}
				}
				cropPhoto(uri);
				Logger.d("photo", "bitmap=" + uri.toString());
			} else {
				showToast("操作失败");
			}
		}
	}

	public Uri getBitmapUri(Bitmap bitmap) {
		String filePath = SystemManager.getInstance().getSystem(SystemFrameworkConfig.class).mCacheDir + "/photos/";
		File file = new File(filePath);
		file.mkdirs();
		String fileName = file.getPath() + "/" + "head.jpg";
		FileOutputStream b = null;
		try {
			b = new FileOutputStream(fileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(b!=null){
					b.flush();
					b.close();
				}
				return Uri.fromFile(new File(fileName));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	// 剪切拍的照片
	private void cropPhoto(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, CROP_IMAGE);
	}

	private void goTo(final Bitmap bitmap) {
		saveBitmap(bitmap);
		Utils.showProgressDialog(this);
		getSystem(SystemUpload.class).uploadFile(mPath, path + "user.png", new IUploadCallBack() {
			@Override
			public void onSuccess(String imageUrl,String message) {
				Message msg = Message.obtain();
				msg.what = 1993;
				msg.obj = imageUrl;
				mHandler.sendMessage(msg);
				Utils.dismissProgressDialog();
			}

			@Override
			public void onFailure(Exception e,String msg) {
				Utils.dismissProgressDialog();
			}
		});
	}

	/**
	 * 保存bitmap
	 * 
	 * @param bitmap
	 */
	private void saveBitmap(Bitmap bitmap) {
		File dirFile = new File(path);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File myCaptureFile = new File(path + "user.png");
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onChanged(MyWheelView wheel, int oldValue, int newValue) {
		if (wheel == mProvinceWheel) {
			updateCities();
		} else if (wheel == mCityWheel) {
			updateLocation();
		}
	}
}
