package de.higger.examtrainer.activity;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
	private static final String SAVE_PARAM_DISPLAYED_QUESTION = "SAVE_PARAM_DISPLAYED_QUESTION";

	private final String LOG_TAG = Constants.LOG_TAG_PRE
			+ getClass().getSimpleName();

	private TrainingMode trainingMode;

	private List<Question> allQuestions;

	private Question displayedQuestion = null;

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

		loadNextQuestion();
		showQuestion();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(SAVE_PARAM_DISPLAYED_QUESTION,
				displayedQuestion);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		displayedQuestion = (Question) savedInstanceState
				.get(SAVE_PARAM_DISPLAYED_QUESTION);

		showQuestion();
	}

	private void initServices() {
		questionResultDBService = new QuestionResultDBService(this);
	}

	private void showQuestion() {
		TextView textView = (TextView) findViewById(R.id.trn_text_question_text);
		textView.setText(displayedQuestion.getQuestion());

		FrameLayout imageFrame = (FrameLayout) findViewById(R.id.trn_image_placeholder);
		if (imageFrame.getChildCount() > 0) {
			imageFrame.removeViewAt(0);
		}

		if (displayedQuestion.isImage()) {
			ImageView imageView = new ImageView(this);

			final Uri imageUri = ActivityHelper.getImagePath(this,
					displayedQuestion.getId());
			imageView.setImageURI(imageUri);

			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					File file = new File(imageUri.toString());
					intent.setDataAndType(Uri.fromFile(file), "image/*");
					startActivity(intent);
				}
			});

			imageFrame.addView(imageView);
		}

		TableLayout answersLayout = (TableLayout) findViewById(R.id.trn_answers_placeholder);
		answersLayout.removeViewsInLayout(0, answersLayout.getChildCount());

		for (Answer answer : displayedQuestion.getAnswers()) {
			final TableRow answerRow = (TableRow) getLayoutInflater().inflate(
					R.layout.training_answer_row, null);
			TextView answerText = (TextView) answerRow.getChildAt(1);
			answerText.setText(answer.getAnswer());

			answerRow.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CheckBox answerCheckBox = (CheckBox) answerRow
							.getChildAt(0);
					boolean newState = !answerCheckBox.isChecked();
					answerCheckBox.setChecked(newState);
				}
			});

			answersLayout.addView(answerRow);
		}
	}

	private void loadNextQuestion() {
		displayedQuestion = getNextQuestion();
		Collections.shuffle(displayedQuestion.getAnswers());
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

			loadNextQuestion();
			showQuestion();
		}

	}

	private void validateQuestion() {
		TableLayout answersLayout = (TableLayout) findViewById(R.id.trn_answers_placeholder);

		boolean isAllCorrect = true;
		int i = 0;
		for (Answer answer : displayedQuestion.getAnswers()) {
			TableRow answerRow = (TableRow) answersLayout.getChildAt(i);
			CheckBox answerCheckBox = (CheckBox) answerRow.getChildAt(0);
			answerCheckBox.setEnabled(false);

			TextView answerText = (TextView) answerRow.getChildAt(1);

			if (answerCheckBox.isChecked() == true
					&& answer.isCorrect() == false) {
				answerText.setTextColor(Color.RED);
				isAllCorrect = false;
			} else if (answerCheckBox.isChecked() == false
					&& answer.isCorrect() == true) {
				answerText.setTextColor(Color.BLUE);
				isAllCorrect = false;
			} else if (answerCheckBox.isChecked() == true
					&& answer.isCorrect() == true) {
				answerText.setTextColor(Color.GREEN);
			}
			i++;
		}

		if (isAllCorrect) {
			questionResultDBService.addCorrect(displayedQuestion.getId());
		} else {
			questionResultDBService.addWrong(displayedQuestion.getId());
		}

		Log.d(LOG_TAG, "answered question " + displayedQuestion.getId()
				+ " correkt? " + isAllCorrect);
	}

}
