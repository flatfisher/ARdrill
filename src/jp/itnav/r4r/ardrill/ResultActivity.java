package jp.itnav.r4r.ardrill;

import java.math.BigDecimal;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ResultActivity extends Activity implements OnClickListener {
	private RelativeLayout ShowMapButton;
	private TextView mDistanceText;
	private TextView mAverageText;
	private TextView mTimeText;
	private TextView mNormalSpeedText;
	private MySQLite mSql;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		ShowMapButton = (RelativeLayout) findViewById(R.id.ShowMapButton);
		ShowMapButton.setOnClickListener(this);
		mDistanceText = (TextView) findViewById(R.id.distanceText);
		mAverageText = (TextView) findViewById(R.id.averageText);
		mTimeText = (TextView) findViewById(R.id.timeText);
		mNormalSpeedText = (TextView) findViewById(R.id.normalSpeedText);
		mSql = new MySQLite(this);
		setResult();
	}

	private void setResult() {
		mDistanceText.setText(setDistance() + "m");
		setTime();
//		MyCountDownTimer setTime = new MyCountDownTimer(1000, 1000, this);
//		setTime.start();
		
		mNormalSpeedText.setText("01:57");
	}

	private double setDistance() {
		double distance1;
		distance1 = mSql.DistanceSerch();
		BigDecimal bi = new BigDecimal(String.valueOf(distance1));
		double distance2 = bi.setScale(2, BigDecimal.ROUND_UP).doubleValue();
		return distance2;
	}
	
	private void setTime(){
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		mTimeText.setText(sp.getString("playtime", null));
		
		sp.getString("laptime", null);
		double ave = setDistance() / Double.valueOf(sp.getString("laptime", null))*3.6;
		BigDecimal bi = new BigDecimal(String.valueOf(ave));
		double average = bi.setScale(2, BigDecimal.ROUND_UP).doubleValue();
		mAverageText.setText(String.valueOf(average)+"km/h");
		
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ShowMapButton:
			Intent intent = new Intent(this, MapActivity.class);
			startActivity(intent);
		}

	}

	public class MyCountDownTimer extends CountDownTimer {
		Context context;

		public MyCountDownTimer(long millisInFuture, long countDownInterval,
				Context context) {
			super(millisInFuture, countDownInterval);
			this.context = context;
		}

		@Override
		public void onFinish() {
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(context);
			mTimeText.setText(sp.getString("playtime", null));
			
			sp.getString("laptime", null);
			double ave = setDistance() / Double.valueOf(sp.getString("laptime", null))*3.6;
			BigDecimal bi = new BigDecimal(String.valueOf(ave));
			double average = bi.setScale(2, BigDecimal.ROUND_UP).doubleValue();
			mAverageText.setText(String.valueOf(average)+"km/h");
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}

}
