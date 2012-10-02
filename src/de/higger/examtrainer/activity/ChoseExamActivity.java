package de.higger.examtrainer.activity;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import de.higger.examtrainer.db.service.ImageReferenceDBService;
import de.higger.examtrainer.db.service.QuestionDBService;
import de.higger.examtrainer.exception.WSRequestFailedException;
import de.higger.examtrainer.vo.Answer;
import de.higger.examtrainer.vo.Exam;
import de.higger.examtrainer.vo.ImageReference;
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
			mDialog = ProgressDialog.show(ChoseExamActivity.this, null,
					ChoseExamActivity.this
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

	public static final String EXTRA_TRAINING_MODE = "TRAINING_MODE";
	public static final String EXTRA_TRAINING_QUESTIONS = "TRAINING_QUESTIONS";

	private final String LOG_TAG = Constants.LOG_TAG_PRE
			+ getClass().getSimpleName();

	private TrainingMode trainingMode;

	private ExamDBService examDBService;
	private QuestionDBService questionDBService;
	private AnswerDBService answerDBService;
	private ImageReferenceDBService imageReferenceDBService;

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
			protected void onExecuteComplete(List<Exam> exams) {
				if (exams.size() == 0) {
					Log.d(LOG_TAG, "no exams available");
					finish();
				} else {
					Log.d(LOG_TAG, "put exams into spinner");
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
		imageReferenceDBService = new ImageReferenceDBService(this);

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
		Log.d(LOG_TAG, "update exams in database");
		try {
			List<Exam> exams = examWebService.getExams();

			if (exams.size() > 0) {
				examDBService.removeAllExams();

				int i = 0;
				for (Exam exam : exams) {
					examDBService.addExam(exam);
					i++;
				}

				Log.v(LOG_TAG, "added " + i + " exams");
				questionDBService.removeUnassignedQuestions();

			}
		} catch (WSRequestFailedException e) {
			Toast toast = Toast.makeText(getApplicationContext(),
					getText(R.string.choseexam_exams_load_error),
					Toast.LENGTH_SHORT);
			toast.show();
			Log.w(LOG_TAG, "exams coundn't load", e);
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
		Log.d(LOG_TAG, "exam spinner refreshed");
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
		Log.d(LOG_TAG, "update exams spinner");

		AsyncTask<Void, Void, List<Exam>> asyncTask = new BusyAsynchTask<Void, Void, List<Exam>>() {
			@Override
			protected List<Exam> doInBackground(Void... arg0) {
				updateExamsInDatabase();
				List<Exam> exams = examDBService.getAllExams();

				return exams;
			}

			@Override
			protected void onExecuteComplete(List<Exam> exams) {
				if (exams.size() > 0) {
					refillExamSpinner(exams);

					Toast toast = Toast.makeText(getApplicationContext(),
							getText(R.string.choseexam_exams_load_success),
							Toast.LENGTH_SHORT);
					toast.show();
					Log.d(LOG_TAG, "exams refreshed");
				}
			}
		};

		asyncTask.execute();

	}

	public void startTrainer(View view) {
		Log.d(LOG_TAG, "start training activity");
		Spinner spinner = (Spinner) findViewById(R.id.choseexam_spn_exam);

		final ToggleButton refreshExam = (ToggleButton) findViewById(R.id.choseexam_btn_refresh_exam);
		final Exam selectedExam = (Exam) spinner.getSelectedItem();

		AsyncTask<Void, Void, List<Question>> asyncTask = new BusyAsynchTask<Void, Void, List<Question>>() {
			@Override
			protected List<Question> doInBackground(Void... voids) {
				int examId = selectedExam.getId();

				if (refreshExam.isChecked()) {
					Log.d(LOG_TAG, "trigger questions update");
					updateQuestionsInDatabase(examId);

					return questionDBService.getQuestions(examId);
				} else {
					return retrieveAllQuestions(examId);
				}
			}

			@Override
			protected void onExecuteComplete(List<Question> questions) {
				if (questions.size() > 0) {
					Log.d(LOG_TAG, "send intent to start training");
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
					Log.d(LOG_TAG, "no questions available to start training");
					Toast toast = Toast.makeText(getApplicationContext(),
							getText(R.string.choseexam_start_no_questions),
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
			Log.d(LOG_TAG, "trigger question update");
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

				Log.v(LOG_TAG, "added " + i + " questions and " + k
						+ " answers");

				removeUnassignedImages();

			}
		} catch (WSRequestFailedException e) {
			Toast toast = Toast.makeText(getApplicationContext(),
					getText(R.string.choseexam_questions_load_error),
					Toast.LENGTH_SHORT);
			toast.show();
			Log.w(LOG_TAG, "question coundn't received", e);
		}
	}

	private void removeUnassignedImages() {
		List<ImageReference> imageReferences = imageReferenceDBService
				.getUnassignedImageReferences();

		int k = 0;
		for (ImageReference imageReference : imageReferences) {
			final Uri imageUri = ActivityHelper.getImagePath(this,
					imageReference.getImageId());
			File f = new File(imageUri.toString());
			if (f.exists()) {
				f.delete();
				k++;
			}
		}

		Log.d(LOG_TAG, k + " image files removed");

		imageReferenceDBService
				.removeUnassignedImageReferences(imageReferences);

	}

}
