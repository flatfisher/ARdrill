package jp.itnav.r4r.ardrill;

import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import jp.itnav.r4r.ardrill.MyLocation.GetResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.Toast;

public class MapActivity extends FragmentActivity implements OnClickListener {
	private GoogleMap mMap = null;
	private MySQLite mSql;
	private Button registerBtn;
	private MarkerOptions mDestination;
	private MyLocation mGetLocation;
	private GetResult mGetResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		setToast("Retrieving Current Location");
		registerBtn = (Button) findViewById(R.id.registerBtn);
		registerBtn.setOnClickListener(this);
		mGetLocation = new MyLocation(this);
		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		mSql = new MySQLite(this);
		mMap.setMyLocationEnabled(true);

		try {
			Intent get = getIntent();
			String type = get.getStringExtra("type");
			if (type.equals("destination")) {
				setDestination();
			} else if (type.equals("result")) {
				setResultMap();
			}

		} catch (NullPointerException e) {

		}

	}

	private void setDestination() {
		Get(1);
	}

	private void setResultMap() {
		registerBtn.setVisibility(View.GONE);
		ArrayList<LatLng> latlng = new ArrayList<LatLng>();
		latlng.addAll(mSql.getLatLng());
		for (int i = 0; i < latlng.size(); i++) {
			if (i != 0) {
				addPolyLine(latlng.get(i), latlng.get(i - 1));
			}
		}
		try {
			addMarker(latlng.get(0), 0);
			addMarker(latlng.get(latlng.size() - 1), 1);
			moveCamera(latlng.get(latlng.size() - 1), 15);
		} catch (IndexOutOfBoundsException e) {
			Toast.makeText(this, "NoData", Toast.LENGTH_SHORT).show();
		}
	}

	private void moveCamera(LatLng latlng, float zoom) {
		LatLng location = new LatLng(latlng.latitude, latlng.longitude);
		CameraPosition cameraPos = new CameraPosition.Builder()
				.target(location).zoom(zoom).build();
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
	}

	private void addMarker(LatLng latlng) {
		mDestination = new MarkerOptions();
		LatLng location = new LatLng(latlng.latitude, latlng.longitude);
		mDestination.position(location);
		mDestination.draggable(true);
		mMap.addMarker(mDestination);
	}

	private void addMarker(LatLng latlng, int i) {
		MarkerOptions options = new MarkerOptions();
		LatLng location = new LatLng(latlng.latitude, latlng.longitude);
		options.position(location);
		options.draggable(false);

		switch (i) {
		case 0:
			BitmapDescriptor icon0 = BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
			options.icon(icon0);
			options.title("Start");

			break;
		case 1:
			BitmapDescriptor icon1 = BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
			options.icon(icon1);
			options.title("Goal");
			break;
		}

		mMap.addMarker(options);
	}

	private void addPolyLine(LatLng point1, LatLng point2) {
		PolylineOptions options = new PolylineOptions();
		options.add(point1);
		options.add(point2);
		options.color(0xcc00ffff);
		options.width(10);
		options.geodesic(true);
		mMap.addPolyline(options);
	}

	@Override
	public void onClick(View v) {
		try {
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(this);
			sp.edit()
					.putString("goalposition",
							String.valueOf(mDestination.getPosition()))
					.commit();
			setResult(RESULT_OK);
			setToast("Goal Position Set");
			finish();
		} catch (NullPointerException e) {
			setToast("Retrieving Current Location");
		}
	}

	private Handler mHandler = new Handler();
	private Timer mTimer = new Timer();

	private void Get(final int interval) {
		mGetLocation.start(interval);
		mTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				mGetResult = mGetLocation.getResult();
				mHandler.post(new Runnable() {
					public void run() {
						if (mGetResult.latitude != 0
								&& mGetResult.longitude != 0) {
							LatLng latlng = new LatLng(mGetResult.latitude,
									mGetResult.longitude);
							moveCamera(latlng, 17.0f);
							addMarker(latlng);
							setToast("Drop pin on Goal Position");
							mGetLocation.stop();
							mTimer.cancel();
						}
					}
				});
			}
		}, 0, interval * 1000);

	}
	private void setToast(String message){
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	@Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode==KeyEvent.KEYCODE_BACK){
	    	setResult(RESULT_OK);
			finish();
	      return true;
	    }
	    return false;
	  }
	
}
