package de.higger.examtrainer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import de.higger.examtrainer.R;
import de.higger.examtrainer.exception.WSRequestFailedException;
import de.higger.examtrainer.tool.PrefsHelper;
import de.higger.examtrainer.tool.PrefsHelper.Preferences;
import de.higger.examtrainer.webservice.TestWebService;

public class SettingsActivity extends Activity {

	private static final String LOG_TAG = SettingsActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);

		PrefsHelper prefsHelper = new PrefsHelper(this);
		EditText editText = (EditText) findViewById(R.id.stn_webservice_url);
		editText.setText(prefsHelper.read(Preferences.PREF_WS_URI));
	}

	public void save(View view) {
		
		EditText editText = (EditText) findViewById(R.id.stn_webservice_url);
		
		String webServiceUrl = editText.getText().toString();

		TestWebService testWebService = new TestWebService(this, webServiceUrl);
		try {
			int version = testWebService.receiveWSVersion();
			if (version == 1) {
				PrefsHelper prefsHelper = new PrefsHelper(this);
				prefsHelper.store(Preferences.PREF_WS_URI, webServiceUrl);
				
				finish();
			}
			else {
				Toast toast = Toast.makeText(getApplicationContext(),
						getText(R.string.stn_error_incompatible),
						Toast.LENGTH_SHORT);
				toast.show();
			}
		} catch (WSRequestFailedException e) {
			e.printStackTrace();
			
			Toast toast = Toast.makeText(getApplicationContext(),
					getText(R.string.stn_error_save_url),
					Toast.LENGTH_SHORT);
			toast.show();
			Log.w(LOG_TAG, "exams coundn't load", e);
		}
	}
}
