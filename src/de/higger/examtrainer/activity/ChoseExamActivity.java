package de.higger.examtrainer.activity;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import de.higger.examtrainer.db.service.AnswerDBService;
import de.higger.examtrainer.db.service.ExamDBService;
import de.higger.examtrainer.db.service.QuestionDBService;
import de.higger.examtrainer.exception.WSRequestFailedException;
import de.higger.examtrainer.vo.Answer;
import de.higger.examtrainer.vo.Exam;
import de.higger.examtrainer.vo.Question;
import de.higger.examtrainer.vo.QuestionList;
import de.higger.examtrainer.webservice.ExamWebService;
import de.higger.examtrainer.webservice.QuestionWebService;

public class ChoseExamActivity extends Activity {
	private abstract class BusyAsynchTask<Params, Progress, Result> extends
			AsyncTask<Params, Progress, Result> {

		private ProgressDialog mDialog;

		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog
					.show(ChoseExamActivity.this, "", "wait...");
		}

		@Override
		@Deprecated
		protected final void onPostExecute(Result result) {
			onPostExecuteComplete(result);
			mDialog.cancel();
		}

		protected void onPostExecuteComplete(Result result) {
		}
	}

	public static final String EXTRA_TRAINING_MODE = "TRAINING_MODE";
	public static final String EXTRA_TRAINING_QUESTIONS = "TRAINING_QUESTIONS";

	private final String LOG_TAG = Constants.LOG_TAG_PRE
			+ getClass().getSimpleName();

	private TrainingMode trainingMode;

	private ExamDBService examDBService;
	private QuestionDBService questionDBService;
	private AnswerDBService answerDBService;

	private ExamWebService examWebService;
	private QuestionWebService questionWebService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initServices();

		trainingMode = (TrainingMode) getIntent().getExtras().get(
				EXTRA_TRAINING_MODE);
		setContentView(R.layout.chose_exam);

		AsyncTask<Void, Void, List<Exam>> asyncTask = new BusyAsynchTask<Void, Void, List<Exam>>() {

			@Override
			protected List<Exam> doInBackground(Void... arg0) {

				return retrieveAllExams();
			}

			@Override
			protected void onPostExecuteComplete(List<Exam> exams) {
				if (exams.size() == 0) {
					finish();
				} else {
					refillExamSpinner(exams);
				}
			}
		};

		asyncTask.execute();

	}

	private void initServices() {
		examDBService = new ExamDBService(this);
		questionDBService = new QuestionDBService(this);
		answerDBService = new AnswerDBService(this);

		examWebService = new ExamWebService(this);
		questionWebService = new QuestionWebService(this);
	}

	private List<Exam> retrieveAllExams() {
		List<Exam> exams = examDBService.getAllExams();
		if (exams.size() == 0) {
			updateExamsInDatabase();
			exams = examDBService.getAllExams();
		}
		return exams;
	}

	private void updateExamsInDatabase() {
		try {
			List<Exam> exams = examWebService.getExams();

			if (exams.size() > 0) {
				examDBService.removeAllExams();

				int i = 0;
				for (Exam exam : exams) {
					examDBService.addExam(exam);
					i++;
				}
				Log.v(LOG_TAG, i + " exam rows inserted");
			}
		} catch (WSRequestFailedException e) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Fragenbšgen konnten nicht geladen werden.",
					Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	private void refillExamSpinner(List<Exam> exams) {
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
			updateExamsMenu();
			break;
		case R.id.exam_menu_back:
			finish();
			break;
		}

		return true;
	}

	private void updateExamsMenu() {
		AsyncTask<Void, Void, List<Exam>> asyncTask = new BusyAsynchTask<Void, Void, List<Exam>>() {

			@Override
			protected List<Exam> doInBackground(Void... arg0) {
				updateExamsInDatabase();
				List<Exam> exams = examDBService.getAllExams();

				return exams;
			}

			@Override
			protected void onPostExecuteComplete(List<Exam> exams) {
				if (exams.size() > 0) {
					refillExamSpinner(exams);

					Toast toast = Toast.makeText(getApplicationContext(),
							"Fragenbšgen aktualisiert", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		};

		asyncTask.execute();

	}

	public void startTrainer(View view) {
		Spinner spinner = (Spinner) findViewById(R.id.choseexam_spn_exam);

		final ToggleButton refreshExam = (ToggleButton) findViewById(R.id.choseexam_btn_refresh_exam);
		final Exam selectedExam = (Exam) spinner.getSelectedItem();

		AsyncTask<Void, Void, List<Question>> asyncTask = new BusyAsynchTask<Void, Void, List<Question>>() {

			@Override
			protected List<Question> doInBackground(Void... voids) {
				int examId = selectedExam.getId();

				if (refreshExam.isChecked()) {
					updateQuestionsInDatabase(examId);

					return questionDBService.getQuestions(examId);
				} else {
					return retrieveAllQuestions(examId);
				}
			}

			@Override
			protected void onPostExecuteComplete(List<Question> questions) {
				if (questions.size() > 0) {
					QuestionList questionList = new QuestionList();
					questionList.setQuestions(questions);

					Intent intent = new Intent(ChoseExamActivity.this,
							TrainingActivity.class);
					intent.putExtra(ChoseExamActivity.EXTRA_TRAINING_MODE,
							trainingMode);
					intent.putExtra(ChoseExamActivity.EXTRA_TRAINING_QUESTIONS,
							questionList);
					startActivity(intent);
				} else {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Keine Fragen in dem Katalog vorhanden",
							Toast.LENGTH_SHORT);
					toast.show();
				}

			}
		};

		asyncTask.execute();

	}

	private List<Question> retrieveAllQuestions(int examId) {
		List<Question> questions = questionDBService.getQuestions(examId);

		if (questions.size() == 0) {
			updateQuestionsInDatabase(examId);
			questions = questionDBService.getQuestions(examId);
		}
		return questions;
	}

	private void updateQuestionsInDatabase(int examId) {
		try {
			List<Question> questions = questionWebService.getQuestions(examId);

			if (questions.size() > 0) {
				questionDBService.removeQuestions(examId);

				int i = 0;
				int k = 0;
				for (Question question : questions) {
					questionDBService.createQuestion(examId, question);

					for (Answer answer : question.getAnswers()) {
						answerDBService.createAnswer(question.getId(), answer);
						k++;
					}

					i++;
				}
				Log.v(LOG_TAG, i + " question rows inserted");
				Log.v(LOG_TAG, k + " answer rows inserted");
			}
		} catch (WSRequestFailedException e) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Fragen konnten nicht geladen werden.", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

}
