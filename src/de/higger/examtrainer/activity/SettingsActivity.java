package de.higger.examtrainer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
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
	private abstract class BusyAsynchTask<Params, Progress, Result> extends
			AsyncTask<Params, Progress, Result> {

		private ProgressDialog mDialog;

		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(SettingsActivity.this, null,
					SettingsActivity.this
							.getText(R.string.progressdiaglog_default));
			mDialog.setCancelable(false);
		}

		@Override
		@Deprecated
		protected final void onPostExecute(Result result) {
			onExecuteComplete(result);
			mDialog.cancel();
		}

		protected void onExecuteComplete(Result result) {
		}
	}

	private static final String LOG_TAG = SettingsActivity.class
			.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);

		PrefsHelper prefsHelper = new PrefsHelper(this);
		EditText editText = (EditText) findViewById(R.id.stn_webservice_url);
		editText.setText(prefsHelper.read(Preferences.PREF_WS_URI));
	}
	
	@Override
	protected void onStop() {
		PrefsHelper prefsHelper = new PrefsHelper(this);
		if (null == prefsHelper.read(Preferences.PREF_WS_URI)) {
			System.exit(0);
		}
	}

	public void save(View view) {
		EditText editText = (EditText) findViewById(R.id.stn_webservice_url);
		final String webServiceUrl = editText.getText().toString();

		BusyAsynchTask<Void, Void, Integer> busyAsynchTask = new BusyAsynchTask<Void, Void, Integer>() {
			@Override
			protected Integer doInBackground(Void... params) {

				TestWebService testWebService = new TestWebService(
						SettingsActivity.this, webServiceUrl);
				try {
					int version = testWebService.receiveWSVersion();
					
					return version;
				} catch (WSRequestFailedException e) {
					e.printStackTrace();

					Log.w(LOG_TAG, "exams coundn't load", e);

					return null;
				}

			}

			@Override
			protected void onExecuteComplete(Integer result) {
				if (result == null) {
					Toast toast = Toast.makeText(getApplicationContext(),
							getText(R.string.stn_error_save_url),
							Toast.LENGTH_SHORT);
					toast.show();
				} else {
					PrefsHelper prefsHelper = new PrefsHelper(
							SettingsActivity.this);
					prefsHelper.store(Preferences.PREF_WS_URI, webServiceUrl);

					//finish();
					System.exit(0);
				}
			}
		};
		busyAsynchTask.execute();

	}
}
