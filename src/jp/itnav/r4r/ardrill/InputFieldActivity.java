package jp.itnav.r4r.ardrill;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InputFieldActivity extends Activity implements OnClickListener {
	private EditText editAge;
	private EditText editHeight;
	private RadioGroup radioGroup;
	private RadioButton radioButton;
	private String mInputGender;
	private String mInputAge;
	private String mInputHeight;
	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_field);

		RelativeLayout startButton = (RelativeLayout) findViewById(R.id.startButton);
		startButton.setOnClickListener(this);

		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(this);

		editAge = (EditText) findViewById(R.id.ageEditText);

		editHeight = (EditText) findViewById(R.id.heightEditText);
		editHeight
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						// TODO Auto-generated method stub
						if (event != null
								&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
							if (event.getAction() == KeyEvent.ACTION_UP) {
								// ソフトキーボードを隠す
								((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
										.hideSoftInputFromWindow(
												v.getWindowToken(), 0);

							}
							return true;
						}
						return false;
					}
				});

		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		radioButton = (RadioButton) findViewById(radioGroup
				.getCheckedRadioButtonId());
		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						RadioButton radioButton = (RadioButton) findViewById(checkedId);
						mInputGender = radioButton.getText().toString();

					}
				});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != -1) {
			Intent intent = new Intent(this, ResultActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.startButton:
			Log.v("ddd", "ddd");
			Intent intent = new Intent(this, ARdrillActivity.class);
			intent.putExtra("Gender", mInputGender);

			editAge.selectAll();
			mInputAge = editAge.getText().toString();
			intent.putExtra("Age", mInputAge);

			editHeight.selectAll();
			mInputHeight = editHeight.getText().toString();
			intent.putExtra("Height", mInputHeight);

			try {
				MySQLite sql = new MySQLite(this);
				sql.Delete(this);
				// LogがNullにならなかったらインテント
				Log.i("Gender", mInputGender);
				Log.i("Age", mInputAge);
				Log.i("mInputHeight", mInputHeight);
				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(this);
				sp.edit().remove("playtime").commit();
				sp.edit().remove("normaltime").commit();
				Log.i("GoalPotision", sp.getString("goalposition", null));
				startActivityForResult(intent, 0);
			} catch (NullPointerException e) {
				Toast.makeText(this, "Error There are missing fields. Back",
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.button1:
			Intent intent2 = new Intent(this, MapActivity.class);
			intent2.putExtra("type", "destination");
			startActivityForResult(intent2, 0);
			break;
		}
	}
}
