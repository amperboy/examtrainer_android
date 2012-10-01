package de.higger.examtrainer.activity;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.higger.examtrainer.Constants;
import de.higger.examtrainer.R;
import de.higger.examtrainer.TrainingMode;
import de.higger.examtrainer.db.service.QuestionResultDBService;
import de.higger.examtrainer.vo.Answer;
import de.higger.examtrainer.vo.Question;
import de.higger.examtrainer.vo.QuestionList;

public class TrainingActivity extends Activity {
	private final String LOG_TAG = Constants.LOG_TAG_PRE
			+ getClass().getSimpleName();

	private TrainingMode trainingMode;

	private List<Question> allQuestions;

	private int displayedQuestionId;
	private List<Answer> displayedAnswers;

	private QuestionResultDBService questionResultDBService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initServices();

		trainingMode = (TrainingMode) getIntent().getExtras().get(
				ChoseExamActivity.EXTRA_TRAINING_MODE);
		QuestionList questionList = (QuestionList) getIntent().getExtras().get(
				ChoseExamActivity.EXTRA_TRAINING_QUESTIONS);

		setContentView(R.layout.training);

		allQuestions = questionList.getQuestions();

		showNextQuestion();
	}

	private void initServices() {
		questionResultDBService = new QuestionResultDBService(this);
	}

	@SuppressWarnings("unchecked")
	private void showNextQuestion() {
		Question question = getNextQuestion();
		displayedQuestionId = question.getId();

		TextView textView = (TextView) findViewById(R.id.trn_text_question_text);
		textView.setText(question.getQuestion());

		FrameLayout imageFrame = (FrameLayout) findViewById(R.id.trn_image_placeholder);
		if (imageFrame.getChildCount() > 0) {
			imageFrame.removeViewAt(0);
		}

		if (question.isImage()) {
			ImageView imageView = new ImageView(this);

			File filesDir = getFilesDir();
			imageView.setImageURI(Uri.parse(filesDir.getAbsolutePath()
					+ "/Geocaching_Logo.jpg"));

			View wrapper = findViewById(R.id.trn_view_wrapper);

			imageFrame.addView(imageView);
		}

		TableLayout answersLayout = (TableLayout) findViewById(R.id.trn_answers_placeholder);
		answersLayout.removeViewsInLayout(0, answersLayout.getChildCount());

		displayedAnswers = getCopy(question.getAnswers());
		Collections.shuffle(displayedAnswers);

		for (Answer answer : displayedAnswers) {
			TableRow answerRow = new TableRow(this);

			CheckBox isCorrect = new CheckBox(this);
			answerRow.addView(isCorrect);

			TextView answerText = new TextView(this);
			answerText.setText(answer.getAnswer());
			answerRow.addView(answerText);

			answersLayout.addView(answerRow);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getCopy(List src) {
		List copy = new LinkedList();

		for (Object obj : src) {
			copy.add(obj);
		}

		return copy;
	}

	private Question getNextQuestion() {
		if (trainingMode.equals(TrainingMode.RANDOM)) {
			int amountQuestions = allQuestions.size();
			Random random = new Random();
			int questionIndex = random.nextInt(amountQuestions);
			Question question = allQuestions.get(questionIndex);

			return question;
		}

		return null;
	}

	public void clickContinue(View view) {
		Button confirmButton = (Button) findViewById(R.id.trn_btn_confirm);
		Button nextButton = (Button) findViewById(R.id.trn_btn_next);
		confirmButton.setEnabled(false);
		nextButton.setEnabled(false);

		if (view.getId() == R.id.trn_btn_confirm) {
			nextButton.setEnabled(true);

			validateQuestion();

		} else if (view.getId() == R.id.trn_btn_next) {
			confirmButton.setEnabled(true);

			showNextQuestion();
		}

	}

	private void validateQuestion() {
		TableLayout answersLayout = (TableLayout) findViewById(R.id.trn_answers_placeholder);

		boolean isAllCorrect = true;
		int i = 0;
		for (Answer answer : displayedAnswers) {
			TableRow answerRow = (TableRow) answersLayout.getChildAt(i);
			CheckBox answerCheckBox = (CheckBox) answerRow.getChildAt(0);
			answerCheckBox.setEnabled(false);

			TextView answerText = (TextView) answerRow.getChildAt(1);

			if (answerCheckBox.isChecked() != answer.isCorrect()) {
				answerText.setTextColor(Color.RED);
				isAllCorrect = false;
			} else if (answerCheckBox.isChecked()) {
				answerText.setTextColor(Color.GREEN);
			}
			i++;
		}

		if (isAllCorrect) {
			questionResultDBService.addCorrect(displayedQuestionId);
		} else {
			questionResultDBService.addWrong(displayedQuestionId);
		}

		Log.d(LOG_TAG, "answered question " + displayedQuestionId
				+ " correkt? " + isAllCorrect);
	}

}
