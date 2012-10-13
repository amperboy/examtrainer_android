package de.higger.examtrainer.db.service;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.higger.examtrainer.Constants;
import de.higger.examtrainer.db.ddl.ExamDBHelper;
import de.higger.examtrainer.db.ddl.ExamDDL;
import de.higger.examtrainer.db.ddl.QuestionDDL;
import de.higger.examtrainer.db.ddl.QuestionResultDDL;
import de.higger.examtrainer.vo.Answer;
import de.higger.examtrainer.vo.Question;

public class QuestionDBService {
	private final String LOG_TAG = Constants.LOG_TAG_PRE
			+ getClass().getSimpleName();

	private ExamDBHelper examDBHelper;
	private AnswerDBService answerDBService;

	public QuestionDBService(Context context) {
		examDBHelper = new ExamDBHelper(context);
		answerDBService = new AnswerDBService(context);
	}

	public int getCount(int examId) {
		StringBuilder queryString = new StringBuilder("SELECT count(*) FROM ");
		queryString.append(QuestionDDL.TABLE_NAME);
		queryString.append(" WHERE ").append(QuestionDDL.COLUMNNAME_EXAM_ID);
		queryString.append(" = ?");

		String sExamId = Integer.toString(examId);
		SQLiteDatabase db = examDBHelper.getReadableDatabase();
		Cursor mCount = db.rawQuery(queryString.toString(),
				new String[] { sExamId });

		mCount.moveToFirst();
		int count = mCount.getInt(0);
		db.close();
		mCount.close();

		return count;
	}

	public Question getRandomQuestion(int examId) {
		StringBuilder queryString = new StringBuilder("SELECT ")
				.append(QuestionDDL.COLUMNNAME_ID).append(" FROM ")
				.append(QuestionDDL.TABLE_NAME).append(" WHERE ")
				.append(QuestionDDL.COLUMNNAME_EXAM_ID)
				.append(" = ? ORDER BY RANDOM() LIMIT 1;");

		String sExamId = Integer.toString(examId);
		SQLiteDatabase db = examDBHelper.getReadableDatabase();
		Cursor mCount = db.rawQuery(queryString.toString(),
				new String[] { sExamId });
		mCount.moveToFirst();
		int questionId = mCount.getInt(0);
		db.close();
		mCount.close();

		Log.v(LOG_TAG, "random question id: " + questionId);

		Question question = getQuestion(questionId);
		question.setDesc("(#" + questionId + ")");
		return question;
	}

	public Question getPreferedQuestion(int examId) {
		StringBuilder queryString = new StringBuilder("SELECT ");
		queryString.append(QuestionDDL.COLUMNNAME_ID);
		queryString.append(" id_question, 2.0 wtg from ");
		queryString.append(QuestionDDL.TABLE_NAME);
		queryString.append(" where ");
		queryString.append(QuestionDDL.COLUMNNAME_EXAM_ID);
		queryString.append(" = ? and ");
		queryString.append(QuestionDDL.COLUMNNAME_ID);
		queryString.append(" not in (SELECT distinct ");
		queryString.append(QuestionResultDDL.COLUMNNAME_QUESTION_ID);
		queryString.append(" from ");
		queryString.append(QuestionResultDDL.TABLE_NAME);
		queryString.append(" qr, ");
		queryString.append(QuestionDDL.TABLE_NAME);
		queryString.append(" q WHERE q.");
		queryString.append(QuestionDDL.COLUMNNAME_ID);
		queryString.append(" = qr.");
		queryString.append(QuestionResultDDL.COLUMNNAME_QUESTION_ID);
		queryString.append(" and q.");
		queryString.append(QuestionDDL.COLUMNNAME_EXAM_ID);
		queryString.append(" = ?) union select ");
		queryString.append(QuestionResultDDL.COLUMNNAME_QUESTION_ID);
		queryString.append(" , (cast(");
		queryString.append(QuestionResultDDL.COLUMNNAME_ANSWERED_WRONG);
		queryString.append(" as REAL) / (cast(");
		queryString.append(QuestionResultDDL.COLUMNNAME_ANSWERED_CORRECT);
		queryString.append(" as REAL) + cast(");
		queryString.append(QuestionResultDDL.COLUMNNAME_ANSWERED_WRONG);
		queryString.append(" as REAL))) + 1 - ((cast(");
		queryString.append(QuestionResultDDL.COLUMNNAME_ANSWERED_CORRECT);
		queryString.append(" as REAL) + cast(");
		queryString.append(QuestionResultDDL.COLUMNNAME_ANSWERED_WRONG);
		queryString.append(" as REAL)) / ( select sum(cast(");
		queryString.append(QuestionResultDDL.COLUMNNAME_ANSWERED_CORRECT);
		queryString.append(" as REAL)) + sum(cast(");
		queryString.append(QuestionResultDDL.COLUMNNAME_ANSWERED_WRONG);
		queryString.append(" as REAL)) gesamt from ");
		queryString.append(QuestionResultDDL.TABLE_NAME);
		queryString.append(" qr, ");
		queryString.append(QuestionDDL.TABLE_NAME);
		queryString.append(" q WHERE q.");
		queryString.append(QuestionDDL.COLUMNNAME_ID);
		queryString.append(" = qr.");
		queryString.append(QuestionResultDDL.COLUMNNAME_QUESTION_ID);
		queryString.append(" and q.");
		queryString.append(QuestionDDL.COLUMNNAME_EXAM_ID);
		queryString.append(" = ?)) wtg from ");
		queryString.append(QuestionResultDDL.TABLE_NAME);
		queryString.append(" qr, ");
		queryString.append(QuestionDDL.TABLE_NAME);
		queryString.append(" q WHERE q.");
		queryString.append(QuestionDDL.COLUMNNAME_ID);
		queryString.append(" = qr.");
		queryString.append(QuestionResultDDL.COLUMNNAME_QUESTION_ID);
		queryString.append(" and q.");
		queryString.append(QuestionDDL.COLUMNNAME_EXAM_ID);
		queryString.append(" = ? order by 2 desc");

		String sExamId = Integer.toString(examId);
		SQLiteDatabase db = examDBHelper.getReadableDatabase();
		Cursor mCount = db.rawQuery(queryString.toString(), new String[] {
				sExamId, sExamId, sExamId, sExamId });
		mCount.moveToFirst();
		int questionId = mCount.getInt(0);
		double wtg = mCount.getDouble(1);
		db.close();
		mCount.close();

		Log.v(LOG_TAG, "optimized question id: " + questionId);

		Question question = getQuestion(questionId);
		question.setDesc("(#" + questionId + ",wtg:" + wtg + ")");
		return question;
	}

	public Question getQuestion(int questionId) {
		SQLiteDatabase db = examDBHelper.getReadableDatabase();
		Cursor c = db.query(QuestionDDL.TABLE_NAME, new String[] {
				QuestionDDL.COLUMNNAME_ID, QuestionDDL.COLUMNNAME_QUESTION,
				QuestionDDL.COLUMNNAME_HAS_IMAGE }, QuestionDDL.COLUMNNAME_ID
				+ " = ?", new String[] { Integer.toString(questionId) }, null,
				null, null);

		c.moveToFirst();
		Question question = new Question();

		question.setId(questionId);
		question.setQuestion(c.getString(1));
		question.setImage(c.getShort(2) == 1 ? true : false);
		db.close();

		List<Answer> answers = answerDBService.getAnswers(questionId);
		question.setAnswers(answers);

		return question;
	}

	public void removeQuestions(int examId) {
		SQLiteDatabase db = examDBHelper.getWritableDatabase();
		db.delete(QuestionDDL.TABLE_NAME, QuestionDDL.COLUMNNAME_EXAM_ID
				+ " = ?", new String[] { Integer.toString(examId) });
		db.close();
	}

	public void removeUnassignedQuestions() {
		SQLiteDatabase db = examDBHelper.getWritableDatabase();
		String query = "DELETE FROM " + QuestionDDL.TABLE_NAME + " WHERE "
				+ QuestionDDL.COLUMNNAME_EXAM_ID + " NOT IN (SELECT "
				+ ExamDDL.COLUMNNAME_ID + " FROM " + ExamDDL.TABLE_NAME + ");";
		db.execSQL(query);
		db.close();

		Log.v(LOG_TAG, "all unassigned questions removed");
	}

	public void createQuestion(int examId, Question question) {
		SQLiteDatabase db = examDBHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(QuestionDDL.COLUMNNAME_ID, question.getId());
		values.put(QuestionDDL.COLUMNNAME_EXAM_ID, examId);
		values.put(QuestionDDL.COLUMNNAME_QUESTION, question.getQuestion());
		values.put(QuestionDDL.COLUMNNAME_HAS_IMAGE, question.isImage());

		db.insert(QuestionDDL.TABLE_NAME, null, values);
		db.close();

	}
}
