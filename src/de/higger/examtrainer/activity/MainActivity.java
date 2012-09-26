package de.higger.examtrainer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import de.higger.examtrainer.R;
import de.higger.examtrainer.TrainingMode;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void runRandomTrainer(View view) {
		Intent intent = new Intent(this, ChoseExamActivity.class);
		intent.putExtra(ChoseExamActivity.TRAINING_MODE, TrainingMode.RANDOM);
		startActivity(intent);
	}
}