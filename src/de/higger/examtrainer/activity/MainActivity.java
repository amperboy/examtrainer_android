package de.higger.examtrainer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import de.higger.examtrainer.R;
import de.higger.examtrainer.TrainingMode;
import de.higger.examtrainer.db.service.QuestionResultDBService;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void runRandomTrainer(View view) {
		Intent intent = new Intent(this, ChoseExamActivity.class);
		intent.putExtra(ChoseExamActivity.EXTRA_TRAINING_MODE,
				TrainingMode.RANDOM);
		startActivity(intent);
	}

	public void runOptTrainer(View view) {
		Intent intent = new Intent(this, ChoseExamActivity.class);
		intent.putExtra(ChoseExamActivity.EXTRA_TRAINING_MODE,
				TrainingMode.OPTIMIZED);
		startActivity(intent);
	}

	private void clearStatistic() {
		QuestionResultDBService questionResultDBService = new QuestionResultDBService(
				this);
		questionResultDBService.clearStatistic();
		
		Toast toast = Toast.makeText(getApplicationContext(),
				getText(R.string.main_statistic_cleared),
				Toast.LENGTH_SHORT);
		toast.show();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_menu_clear_stat:
			clearStatistic();
			break;
		case R.id.main_menu_exit:
			System.exit(0);
			break;
		}

		return true;
	}
}