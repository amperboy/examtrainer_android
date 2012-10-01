package de.higger.examtrainer.db.service;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.higger.examtrainer.Constants;
import de.higger.examtrainer.db.ddl.AnswerDDL;
import de.higger.examtrainer.db.ddl.ExamDBHelper;
import de.higger.examtrainer.db.ddl.ExamDDL;
import de.higger.examtrainer.db.ddl.QuestionDDL;
import de.higger.examtrainer.vo.Answer;

public class AnswerDBService {
	private final String LOG_TAG = Constants.LOG_TAG_PRE
			+ getClass().getSimpleName();

	private ExamDBHelper examDBHelper;

	public AnswerDBService(Context context) {
		examDBHelper = new ExamDBHelper(context);
	}

	public List<Answer> getAnswers(int questionId) {

		SQLiteDatabase db = examDBHelper.getReadableDatabase();
		Cursor ca = db
				.query(AnswerDDL.TABLE_NAME, new String[] {
						AnswerDDL.COLUMNNAME_ID, AnswerDDL.COLUMNNAME_ANSWER,
						AnswerDDL.COLUMNNAME_IS_CORRECT },
						AnswerDDL.COLUMNNAME_QUESTION_ID + " = ?",
						new String[] { Integer.toString(questionId) }, null,
						null, null);

		List<Answer> answers = new LinkedList<Answer>();
		while (ca.moveToNext()) {
			Answer answer = new Answer();
			answer.setId(ca.getInt(0));
			answer.setAnswer(ca.getString(1));
			answer.setCorrect(ca.getShort(2) == 1 ? true : false);

			answers.add(answer);
		}
		db.close();

		return answers;
	}

	public void createAnswer(int questionId, Answer answer) {
		SQLiteDatabase db = examDBHelper.getWritableDatabase();

		ContentValues avalues = new ContentValues();
		avalues.put(AnswerDDL.COLUMNNAME_ID, answer.getId());
		avalues.put(AnswerDDL.COLUMNNAME_QUESTION_ID, questionId);
		avalues.put(AnswerDDL.COLUMNNAME_ANSWER, answer.getAnswer());
		avalues.put(AnswerDDL.COLUMNNAME_IS_CORRECT, answer.isCorrect());

		db.insert(AnswerDDL.TABLE_NAME, null, avalues);
		db.close();
	}

}
