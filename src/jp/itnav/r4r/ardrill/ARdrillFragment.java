/**
 * 
 */
package jp.itnav.r4r.ardrill;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

import org.json.JSONException;
import org.json.JSONObject;

import jp.itnav.r4r.ardrill.MyLocation.GetResult;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import rajawali.RajawaliFragment;
import rajawali.animation.mesh.VertexAnimationObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.math.vector.Vector3;
import rajawali.parser.LoaderMD2;
import rajawali.parser.ParsingException;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;

/**
 * @author ktaka
 * 
 */
public class ARdrillFragment extends RajawaliFragment implements
		SensorEventListener {

	private final float ALPHA = 0.8f;
	private final int SENSITIVITY = 5;
	public static final String BUNDLE_EXAMPLE_URL = "BUNDLE_EXAMPLE_URL";
	protected ARRenderer mRenderer;
	private SensorManager mSensorManager;
	private Camera myCamera;
	private float mGravity[];
	private TextView msgText1;
	private TextView msgText2;
	private TextView msgText3;
	private TextView msgText4;
	private TextView msgText5;
	private TextView msgText6;
	private TextView artText;
	private TextView artText2;
	private ImageView stpImage;
	private RelativeLayout artLayout;
	private ImageView artImage;
	private boolean actionBarShowing = true;
	private MyLocation mGetLocation;
	private GetResult mGetResult;
	private Timer mTimer;
	Handler mHandler;
	MySQLite mSql;

	private SurfaceHolder.Callback mSurfaceListener = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			myCamera = Camera.open();
			try {
				myCamera.setPreviewDisplay(holder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			myCamera.release();
			myCamera = null;
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			Camera.Parameters parameters = myCamera.getParameters();
			parameters.setPreviewSize(width, height);
			// myCamera.setParameters(parameters);
			myCamera.startPreview();
		}
	};

	public ARdrillFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setGLBackgroundTransparent(true);

		mRenderer = createRenderer();

		mRenderer.setSurfaceView(mSurfaceView);
		setRenderer(mRenderer);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLayout = (FrameLayout) inflater.inflate(R.layout.fragment_ardrill,
				container, false);

		mLayout.addView(mSurfaceView);

		SurfaceView mySurfaceView = (SurfaceView) mLayout
				.findViewById(R.id.surface_view);
		SurfaceHolder holder = mySurfaceView.getHolder();
		holder.addCallback(mSurfaceListener);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// setLayout();
		// View topPanelLayout = mLayout.findViewById(R.id.top_panel_layout);
		// topPanelLayout.bringToFront();

		mGravity = new float[3];
		mSensorManager = (SensorManager) getActivity().getSystemService(
				Context.SENSOR_SERVICE);
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);
		//
		mLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getActivity().getActionBar().isShowing()) {
					getActivity().getActionBar().hide();
				} else {
					getActivity().getActionBar().show();
				}
			}
		});

		mHandler = new Handler();
		mTimer = new Timer();
		mGetLocation = new MyLocation(getActivity());
		mSql = new MySQLite(getActivity());
		mSql.Serch();

		return mLayout;
	}

	@Override
	public void onResume() {
		super.onResume();
		startGetLocation(1);
	}

	@Override
	public void onPause() {
		stop();
		super.onPause();
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private void stop() {
		mTimer.cancel();
		mGetLocation.stop();
		SharedPeferebces();
		// mGetActivityRecognition.stop();
	}

	@Override
	public void onDestroyView() {
//		stop();
		super.onDestroyView();
	}

	private void SharedPeferebces() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		sp.edit().putString("playtime", setPlayTime()).commit();
		sp.edit().putString("laptime", String.valueOf(lapTime)).commit();
	}

	private String setPlayTime() {
		String hour;
		String minute;

		if (hourTime < 10) {
			hour = "0" + String.valueOf(hourTime);
		} else {
			hour = String.valueOf(hourTime);
		}

		if (minuteTime < 10) {
			minute = "0" + String.valueOf(minuteTime);
		} else {
			minute = String.valueOf(minuteTime);
		}
		return hour + ":" + minute;
	}

	private void startGetLocation(int interval) {
		mGetLocation.start(interval);
		// mGetActivityRecognition.start();
		lapTime = 0;
		minuteTime = 0;
		hourTime = 0;
		Get(interval);
	}

	int lapTime = 0;
	int minuteTime = 0;
	int hourTime = 0;
	double distance = 0.0;
	double onceLatitude = 999;
	double onceLongitude = 999;

	private void Get(final int interval) {
		mTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				lapTime += 1 * interval;
				if (minuteTime > 59) {
					minuteTime = 0;
					hourTime += 1 * interval;
				} else {
					minuteTime += 1 * interval;
				}
				mHandler.post(new Runnable() {
					public void run() {

						mGetResult = mGetLocation.getResult();
						SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");

						// mTimeText.setText(sdf.format(mGetResult.time));
						// mLatitudeText.setText(String.valueOf(mGetResult.latitude));
						// mLongitudeText.setText(String
						// .valueOf(mGetResult.longitude));
						// mSpeedText.setText(String.valueOf(mGetResult.speed));
						// mAccuracyText.setText(String
						// .valueOf(mGetResult.accuracy));
						// mAltitudeText.setText(String
						// .valueOf(mGetResult.altitude));

						if (mGetResult.latitude != 0
								&& mGetResult.longitude != 0) {
							if (onceLatitude == 999 && onceLongitude == 999) {
								mSql.Insert(mGetResult.latitude,
										mGetResult.longitude, 0);
								onceLatitude = mGetResult.latitude;
								onceLongitude = mGetResult.longitude;
							} else {
								
								distance += setDistance();
								
								mSql.Insert(mGetResult.latitude,
										mGetResult.longitude, setDistance());
								onceLatitude = mGetResult.latitude;
								onceLongitude = mGetResult.longitude;
							}
						}

					}
				});
			}
		}, 0, interval * 1000);

	}

	private double setDistance() {
		return getDistance(onceLatitude, onceLongitude, mGetResult.latitude,
				mGetResult.longitude, 7) * 1000;
	}
	
	private double getSpeed(double distance, int interval){
		return distance / interval;
	}

	private double getDistance(double lat1, double lng1, double lat2,
			double lng2, int precision) {
		// kmで返す
		int R = 6378;
		double lat = Math.toRadians(lat2 - lat1);
		double lng = Math.toRadians(lng2 - lng1);
		double A = Math.sin(lat / 2) * Math.sin(lat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lng / 2)
				* Math.sin(lng / 2);
		double C = 2 * Math.atan2(Math.sqrt(A), Math.sqrt(1 - A));
		double decimalNo = Math.pow(10, precision);
		double distance = R * C;
		distance = Math.round(decimalNo * distance / 1) / decimalNo;
		return distance;
	}

	protected ARRenderer createRenderer() {
		return new ARRenderer(getActivity());
	}

	class ARRenderer extends RajawaliRenderer {
		private DirectionalLight mLight;
		private VertexAnimationObject3D mOgre;
		private Vector3 mAccValues;
		private Plane mPlane;

		public ARRenderer(Context context) {
			super(context);
			mAccValues = new Vector3();
			setFrameRate(60);
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// showLoader();
			super.onSurfaceCreated(gl, config);
			// hideLoader();
		}

		public void playAnimation(String name) {
			if (name.equals("loop all")) {
				mOgre.play();
			} else {
				mOgre.play(name, true);
			}
		}

		protected void initScene() {
			mLight = new DirectionalLight(0, 0, 1);
			mLight.setPower(1);
			getCurrentScene().addLight(mLight);
			getCurrentCamera().setPosition(0, 0, -8);
			getCurrentCamera().setLookAt(0, 0, 0);

			Material material = new Material();
			material.setColor(0x880000ff);
			mPlane = new Plane(30.0f, 13.0f, 1, 1);
			mPlane.setMaterial(material);
			mPlane.setY(-1.5);

			LoaderMD2 parser = new LoaderMD2(mContext.getResources(),
					mTextureManager, R.raw.ogro);
			try {
				parser.parse();

				mOgre = (VertexAnimationObject3D) parser
						.getParsedAnimationObject();
				mOgre.setScale(.07f);
				// mOgre.setRotY(90);
				mOgre.setY(-1);
				mOgre.setFps(4);
				// addChild(mOgre);
				getCurrentScene().addChild(mOgre);
				getCurrentScene().addChild(mPlane);
				// addChild(mPlane);

				mOgre.play();
				// mOgre.play("loop all", true);
				mOgre.play("run", true);
				// mOgre.play("crwalk", true);
				// mRenderer.playAnimation("loop all");
			} catch (ParsingException e) {
				e.printStackTrace();
			}
			getCurrentScene().setBackgroundColor(0);
		}

		public void onDrawFrame(GL10 glUnused) {
			mOgre.setRotation(mAccValues.x, mAccValues.y + 90.0, mAccValues.z);
			mPlane.setRotation(mAccValues.x - 90, mAccValues.y, mAccValues.z);
			super.onDrawFrame(glUnused);
		}

		public void setAccelerometerValues(float x, float y, float z) {
			mAccValues.setAll(-y, -x, -z);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			mGravity[0] = ALPHA * mGravity[0] + (1 - ALPHA) * event.values[0];
			mGravity[1] = ALPHA * mGravity[1] + (1 - ALPHA) * event.values[1];
			mGravity[2] = ALPHA * mGravity[2] + (1 - ALPHA) * event.values[2];

			mRenderer.setAccelerometerValues(event.values[1] - mGravity[1]
					* SENSITIVITY, event.values[0] - mGravity[0] * SENSITIVITY,
					0);
		}

	}

}
