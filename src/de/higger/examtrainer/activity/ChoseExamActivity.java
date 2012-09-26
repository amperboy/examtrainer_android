package de.higger.examtrainer.activity;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;
import de.higger.examtrainer.Constants;
import de.higger.examtrainer.R;
import de.higger.examtrainer.TrainingMode;
import de.higger.examtrainer.db.ExamDBHelper;
import de.higger.examtrainer.db.ExamDDL;
import de.higger.examtrainer.exception.WSRequestFailedException;
import de.higger.examtrainer.vo.Exam;
import de.higger.examtrainer.vo.Question;
import de.higger.examtrainer.webservice.ExamService;
import de.higger.examtrainer.webservice.QuestionService;

public class ChoseExamActivity extends Activity {
	public final static String TRAINING_MODE = "TRAINING_MODE";
	private final String LOG_TAG = Constants.LOG_TAG_PRE
			+ getClass().getSimpleName();

	private TrainingMode trainingMode = null;
	private ExamDBHelper examDBHelper = new ExamDBHelper(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		trainingMode = (TrainingMode) getIntent().getExtras()
				.get(TRAINING_MODE);
		setContentView(R.layout.chose_exam);

		List<Exam> exams = getExamsFromDB();
		if (exams.size() == 0) {
			updateExamsInDatabase();
			exams = getExamsFromDB();
		}

		if (exams.size() == 0) {
			finish();
		} else {
			refillSpinner(exams);
		}
	}

	private List<Exam> getExamsFromDB() {
		SQLiteDatabase db = examDBHelper.getReadableDatabase();
		Cursor c = db.query(ExamDDL.TABLE_NAME, new String[] {
				ExamDDL.COLUMN_NAME_ID, ExamDDL.COLUMN_NAME_NAME }, null, null,
				null, null, null);

		List<Exam> exams = new LinkedList<Exam>();
		while (c.moveToNext()) {
			int id = c.getInt(0);
			String name = c.getString(1);

			Exam exam = new Exam();
			exam.setId(id);
			exam.setName(name);

			exams.add(exam);
		}
		db.close();

		return exams;
	}

	private void updateExamsInDatabase() {
		try {
			ExamService examService = new ExamService(this);
			List<Exam> exams = examService.getExams();

			if (exams.size() > 0) {
				SQLiteDatabase db = examDBHelper.getWritableDatabase();
				db.delete(ExamDDL.TABLE_NAME, null, null);

				int i = 0;
				for (Exam exam : exams) {
					ContentValues values = new ContentValues();
					values.put("id", exam.getId());
					values.put("name", exam.getName());

					db.insert(ExamDDL.TABLE_NAME, null, values);
					i++;
				}
				db.close();
				Log.v(LOG_TAG, i + " rows inserted");
			}
		} catch (WSRequestFailedException e) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Fragenbšgen konnten nicht geladen werden.",
					Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	private void refillSpinner(List<Exam> exams) {
		Spinner spinner = (Spinner) findViewById(R.id.choseexam_spn_exam);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, exams.toArray());
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerArrayAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.exam_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.exam_menu_refresh:
			updateExamsInDatabase();
			List<Exam> exams = getExamsFromDB();
			refillSpinner(exams);

			Toast toast = Toast.makeText(getApplicationContext(),
					"Fragenbšgen aktualisiert", Toast.LENGTH_SHORT);
			toast.show();
			break;
		case R.id.exam_menu_back:
			finish();
			break;
		}

		return true;
	}

	public void startTrainer(View view) {
		Spinner spinner = (Spinner) findViewById(R.id.choseexam_spn_exam);

		ToggleButton refreshExam = (ToggleButton) findViewById(R.id.choseexam_btn_refresh_exam);
		Exam selectedExam = (Exam) spinner.getSelectedItem();

		Log.d(LOG_TAG, "run training in exam " + selectedExam);
		Log.d(LOG_TAG, "refreshExam.isChecked = " + refreshExam.isChecked());

		QuestionService questionService = new QuestionService(this);
		try {
			List<Question> questionsList = questionService
					.getQuestions(selectedExam.getId());
			Log.d(LOG_TAG, "all questions: " + questionsList);

		} catch (WSRequestFailedException e) {
		}

		Intent intent = new Intent(this, ChoseExamActivity.class);
		intent.putExtra(ChoseExamActivity.TRAINING_MODE, trainingMode);
		startActivity(intent);
	}

}
