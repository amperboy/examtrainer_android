package de.higger.examtrainer.db.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.higger.examtrainer.Constants;
import de.higger.examtrainer.db.ddl.ExamDBHelper;
import de.higger.examtrainer.db.ddl.QuestionResultDDL;
import de.higger.examtrainer.vo.QuestionResult;

public class QuestionResultDBService {
	private final String LOG_TAG = Constants.LOG_TAG_PRE
			+ getClass().getSimpleName();

	private ExamDBHelper examDBHelper;

	public QuestionResultDBService(Context context) {
		examDBHelper = new ExamDBHelper(context);
	}

	public void addCorrect(int questionId) {
		addResult(questionId, 1, 0);
	}

	public void addWrong(int questionId) {
		addResult(questionId, 0, 1);
	}

	private void addResult(int questionId, int addCorrect, int addWrong) {
		QuestionResult questionResult = getQuestionResult(questionId);
		if (null == questionResult) {
			questionResult = new QuestionResult();
			questionResult.setQuestionId(questionId);
			questionResult.setAnsweredCorrect(addCorrect);
			questionResult.setAnsweredWrong(addWrong);

			createResult(questionResult);
		} else {
			int newCorrect = questionResult.getAnsweredCorrect() + addCorrect;
			int newWrong = questionResult.getAnsweredWrong() + addWrong;

			questionResult.setAnsweredCorrect(newCorrect);
			questionResult.setAnsweredWrong(newWrong);

			updateResult(questionResult);
		}

		Log.v(LOG_TAG, "questionId: " + questionId + ", correct: "
				+ questionResult.getAnsweredCorrect() + ", wrong: "
				+ questionResult.getAnsweredWrong());

	}

	public QuestionResult getQuestionResult(int questionId) {
		SQLiteDatabase db = examDBHelper.getReadableDatabase();
		Cursor ca = db
				.query(QuestionResultDDL.TABLE_NAME, new String[] {
						QuestionResultDDL.COLUMNNAME_ANSWERED_CORRECT,
						QuestionResultDDL.COLUMNNAME_ANSWERED_WRONG },
						QuestionResultDDL.COLUMNNAME_QUESTION_ID + " = ?",
						new String[] { Integer.toString(questionId) }, null,
						null, null);

		QuestionResult questionResult = null;

		if (ca.moveToNext()) {
			questionResult = new QuestionResult();

			questionResult.setQuestionId(questionId);
			questionResult.setAnsweredCorrect(ca.getInt(0));
			questionResult.setAnsweredWrong(ca.getInt(1));

		}
		db.close();

		return questionResult;
	}

	private void createResult(QuestionResult questionResult) {
		SQLiteDatabase db = examDBHelper.getWritableDatabase();

		ContentValues avalues = new ContentValues();
		avalues.put(QuestionResultDDL.COLUMNNAME_QUESTION_ID,
				questionResult.getQuestionId());
		avalues.put(QuestionResultDDL.COLUMNNAME_ANSWERED_CORRECT,
				questionResult.getAnsweredCorrect());
		avalues.put(QuestionResultDDL.COLUMNNAME_ANSWERED_WRONG,
				questionResult.getAnsweredWrong());

		db.insert(QuestionResultDDL.TABLE_NAME, null, avalues);
		db.close();
	}

	private void updateResult(QuestionResult questionResult) {
		SQLiteDatabase db = examDBHelper.getWritableDatabase();

		ContentValues avalues = new ContentValues();
		avalues.put(QuestionResultDDL.COLUMNNAME_ANSWERED_CORRECT,
				questionResult.getAnsweredCorrect());
		avalues.put(QuestionResultDDL.COLUMNNAME_ANSWERED_WRONG,
				questionResult.getAnsweredWrong());

		db.update(
				QuestionResultDDL.TABLE_NAME,
				avalues,
				QuestionResultDDL.COLUMNNAME_QUESTION_ID + " = ?",
				new String[] { Integer.toString(questionResult.getQuestionId()) });
		db.close();
	}
}
