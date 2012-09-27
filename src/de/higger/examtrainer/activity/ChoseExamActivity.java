package de.higger.examtrainer.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
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
import de.higger.examtrainer.webservice.ExamWebService;
import de.higger.examtrainer.webservice.QuestionWebService;

public class ChoseExamActivity extends Activity {
	public final static String TRAINING_MODE = "TRAINING_MODE";
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

		trainingMode = (TrainingMode) getIntent().getExtras()
				.get(TRAINING_MODE);
		setContentView(R.layout.chose_exam);

		List<Exam> exams = retrieveAllExams();

		if (exams.size() == 0) {
			finish();
		} else {
			refillExamSpinner(exams);
		}
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
					"Fragenb�gen konnten nicht geladen werden.",
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
			updateExamsInDatabase();
			List<Exam> exams = examDBService.getAllExams();
			refillExamSpinner(exams);

			Toast toast = Toast.makeText(getApplicationContext(),
					"Fragenb�gen aktualisiert", Toast.LENGTH_SHORT);
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

		int examId = selectedExam.getId();
		if (refreshExam.isChecked()) {
			updateQuestionsInDatabase(examId);
			Toast toast = Toast.makeText(getApplicationContext(),
					"Fragen aktualisiert.", Toast.LENGTH_SHORT);
			toast.show();
		}
		List<Question> questionsList = retrieveAllQuestions(examId);
		Log.d(LOG_TAG, "all questions: " + questionsList);

		Intent intent = new Intent(this, ChoseExamActivity.class);
		intent.putExtra(ChoseExamActivity.TRAINING_MODE, trainingMode);
		startActivity(intent);
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