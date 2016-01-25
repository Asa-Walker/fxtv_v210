package com.fxtv.threebears.activity.other;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.threebears.R;

/**
 * @author FXTV-Android
 * 
 *         关于我们
 */
public class ActivityAboutUs extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_use_agreement);
		initView();
	}

	private void initView() {
		initActionbar();

		TextView aboutUs = (TextView) findViewById(R.id.activity_use_agreement_content);
		aboutUs.setText("飞熊视频是一款为游戏玩家和游戏主播服务的APP，我们将最新鲜最热门的游戏资讯推送给游戏玩家，也把最优秀最努力的游戏主播带给大家。目前我们的产品处于测试阶段，欢迎提供意见建议，您可以加入QQ群457786473提供意见建议，如果被采纳，您可以得到一份精美的礼物噢！");

	}

	private void initActionbar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		title.setText("关于我们");

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

}
