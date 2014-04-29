package jp.itnav.r4r.ardrill;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_field);

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

		RelativeLayout startButton = (RelativeLayout) findViewById(R.id.startButton);
		startButton.setOnClickListener(this);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Intent intent = new Intent(this, ResultActivity.class);
		startActivity(intent);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.startButton:
			Intent intent = new Intent(this, ARdrillActivity.class);
			// intent.putExtra("Gender", mInputGender);
			// intent.putExtra("Age", mInputAge);
			editHeight.selectAll();
			mInputHeight = editHeight.getText().toString();
			intent.putExtra("Height", mInputHeight);
			Log.v("mInputHeight",mInputHeight);
			try {
				MySQLite sql = new MySQLite(this);
				sql.Delete(this);
			} finally {

			}
			startActivityForResult(intent, 0);
			break;
		}
	}
}
