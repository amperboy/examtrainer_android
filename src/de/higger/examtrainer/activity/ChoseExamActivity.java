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
import de.higger.examtrainer.db.AnswerDDL;
import de.higger.examtrainer.db.ExamDBHelper;
import de.higger.examtrainer.db.ExamDDL;
import de.higger.examtrainer.db.QuestionDDL;
import de.higger.examtrainer.exception.WSRequestFailedException;
import de.higger.examtrainer.vo.Answer;
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

		List<Exam> exams = retrieveAllExams();

		if (exams.size() == 0) {
			finish();
		} else {
			refillExamSpinner(exams);
		}
	}

	private List<Exam> retrieveAllExams() {
		List<Exam> exams = getExamsFromDB();
		if (exams.size() == 0) {
			updateExamsInDatabase();
			exams = getExamsFromDB();
		}
		return exams;
	}

	private List<Exam> getExamsFromDB() {
		SQLiteDatabase db = examDBHelper.getReadableDatabase();
		Cursor c = db.query(ExamDDL.TABLE_NAME, new String[] {
				ExamDDL.COLUMNNAME_ID, ExamDDL.COLUMNNAME_NAME }, null, null,
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
					values.put(ExamDDL.COLUMNNAME_ID, exam.getId());
					values.put(ExamDDL.COLUMNNAME_NAME, exam.getName());

					db.insert(ExamDDL.TABLE_NAME, null, values);
					i++;
				}
				db.close();
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
			updateExamsInDatabase();
			List<Exam> exams = getExamsFromDB();
			refillExamSpinner(exams);

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
		List<Question> questions = getQuestionsFromDB(examId);
		if (questions.size() == 0) {
			updateQuestionsInDatabase(examId);
			questions = getQuestionsFromDB(examId);
		}
		return questions;
	}

	private List<Question> getQuestionsFromDB(int examId) {
		SQLiteDatabase db = examDBHelper.getReadableDatabase();
		Cursor c = db.query(QuestionDDL.TABLE_NAME, new String[] {
				QuestionDDL.COLUMNNAME_ID, QuestionDDL.COLUMNNAME_QUESTION },
				QuestionDDL.COLUMNNAME_EXAM_ID + " = ?",
				new String[] { Integer.toString(examId) }, null, null, null);

		List<Question> questions = new LinkedList<Question>();
		while (c.moveToNext()) {

			Question question = new Question();

			int questionId = c.getInt(0);
			question.setId(questionId);
			question.setQuestion(c.getString(1));

			Cursor ca = db.query(AnswerDDL.TABLE_NAME, new String[] {
					AnswerDDL.COLUMNNAME_ID, AnswerDDL.COLUMNNAME_ANSWER,
					AnswerDDL.COLUMNNAME_IS_CORRECT },
					AnswerDDL.COLUMNNAME_QUESTION_ID + " = ?",
					new String[] { Integer.toString(questionId) }, null, null,
					null);

			List<Answer> answers = new LinkedList<Answer>();
			while (ca.moveToNext()) {
				Answer answer = new Answer();
				answer.setId(ca.getInt(0));
				answer.setAnswer(ca.getString(1));
				//answer.setCorrect(ca.getShort(2) == 1 ? true : false);

				answers.add(answer);
			}
			ca.close();
			question.setAnswers(answers);

			questions.add(question);
		}
		db.close();

		return questions;
	}

	private void updateQuestionsInDatabase(int examId) {
		try {
			QuestionService questionService = new QuestionService(this);
			List<Question> questions = questionService.getQuestions(examId);

			if (questions.size() > 0) {
				SQLiteDatabase db = examDBHelper.getWritableDatabase();
				db.delete(QuestionDDL.TABLE_NAME,
						QuestionDDL.COLUMNNAME_EXAM_ID + " = ?",
						new String[] { Integer.toString(examId) });

				int i = 0;
				int k = 0;
				for (Question question : questions) {
					ContentValues values = new ContentValues();
					values.put(QuestionDDL.COLUMNNAME_ID, question.getId());
					values.put(QuestionDDL.COLUMNNAME_EXAM_ID, examId);
					values.put(QuestionDDL.COLUMNNAME_QUESTION,
							question.getQuestion());

					db.insert(QuestionDDL.TABLE_NAME, null, values);

					db.delete(AnswerDDL.TABLE_NAME,
							AnswerDDL.COLUMNNAME_QUESTION_ID + " = ?",
							new String[] { Integer.toString(question
									.getId()) });
					
					for (Answer answer : question.getAnswers()) {

						ContentValues avalues = new ContentValues();
						avalues.put(AnswerDDL.COLUMNNAME_ID, answer.getId());
						avalues.put(AnswerDDL.COLUMNNAME_QUESTION_ID,
								question.getId());
						avalues.put(AnswerDDL.COLUMNNAME_ANSWER,
								answer.getAnswer());
						avalues.put(AnswerDDL.COLUMNNAME_IS_CORRECT,
								answer.isCorrect());

						db.insert(AnswerDDL.TABLE_NAME, null, avalues);

						k++;
					}

					i++;
				}
				db.close();
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
