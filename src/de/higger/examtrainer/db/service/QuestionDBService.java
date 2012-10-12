package de.higger.examtrainer.db.service;

import java.util.LinkedList;
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

	public int count(int examId) {
		//FIXME implement!
		
		return 0;
	}
	
	public Question getPreferedQuestion(int examId) {
		StringBuilder queryString = new StringBuilder("SELECT ");
		queryString.append(QuestionDDL.COLUMNNAME_ID);
		queryString.append(" id_question, 2 wtg from ");
		queryString.append(QuestionDDL.TABLE_NAME);
		queryString.append(" where ");
		queryString.append(QuestionDDL.COLUMNNAME_EXAM_ID);
		queryString.append(" = ");
		queryString.append(examId);
		queryString.append(" and ");
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
		queryString.append(" = ");
		queryString.append(examId);
		queryString.append(" ) union select ");
		queryString.append(QuestionResultDDL.COLUMNNAME_QUESTION_ID);
		queryString.append(" , (");
		queryString.append(QuestionResultDDL.COLUMNNAME_ANSWERED_WRONG);
		queryString.append(" / (");
		queryString.append(QuestionResultDDL.COLUMNNAME_ANSWERED_CORRECT);
		queryString.append(" + ");
		queryString.append(QuestionResultDDL.COLUMNNAME_ANSWERED_WRONG);
		queryString.append(")) + 1 - ((");
		queryString.append(QuestionResultDDL.COLUMNNAME_ANSWERED_CORRECT);
		queryString.append(" + ");
		queryString.append(QuestionResultDDL.COLUMNNAME_ANSWERED_WRONG);
		queryString.append(") / ( select sum(");
		queryString.append(QuestionResultDDL.COLUMNNAME_ANSWERED_CORRECT);
		queryString.append(") + sum(");
		queryString.append(QuestionResultDDL.COLUMNNAME_ANSWERED_WRONG);
		queryString.append(") gesamt from ");
		queryString.append(QuestionResultDDL.TABLE_NAME);
		queryString.append(" qr, ");
		queryString.append(QuestionDDL.TABLE_NAME);
		queryString.append(" q WHERE q.");
		queryString.append(QuestionDDL.COLUMNNAME_ID);
		queryString.append(" = qr.");
		queryString.append(QuestionResultDDL.COLUMNNAME_QUESTION_ID);
		queryString.append(" and q.");
		queryString.append(QuestionDDL.COLUMNNAME_EXAM_ID);
		queryString.append(" = ");
		queryString.append(examId);
		queryString.append(")) wtg from ");
		queryString.append(QuestionResultDDL.TABLE_NAME);
		queryString.append(" qr, ");
		queryString.append(QuestionDDL.TABLE_NAME);
		queryString.append(" q WHERE q.");
		queryString.append(QuestionDDL.COLUMNNAME_ID);
		queryString.append(" = qr.");
		queryString.append(QuestionResultDDL.COLUMNNAME_QUESTION_ID);
		queryString.append(" and q.");
		queryString.append(QuestionDDL.COLUMNNAME_EXAM_ID);
		queryString.append(" = ");
		queryString.append(examId);
		queryString.append(" order by 2 desc");

		System.out.println(queryString.toString());
		//FIXME implement!

		return null;
	}

	public List<Question> getQuestions(int examId) {
		SQLiteDatabase db = examDBHelper.getReadableDatabase();
		Cursor c = db.query(QuestionDDL.TABLE_NAME, new String[] {
				QuestionDDL.COLUMNNAME_ID, QuestionDDL.COLUMNNAME_QUESTION,
				QuestionDDL.COLUMNNAME_HAS_IMAGE },
				QuestionDDL.COLUMNNAME_EXAM_ID + " = ?",
				new String[] { Integer.toString(examId) }, null, null, null);

		List<Question> questions = new LinkedList<Question>();
		while (c.moveToNext()) {
			Question question = new Question();

			int questionId = c.getInt(0);
			question.setId(questionId);
			question.setQuestion(c.getString(1));
			question.setImage(c.getShort(2) == 1 ? true : false);

			List<Answer> answers = answerDBService.getAnswers(questionId);
			question.setAnswers(answers);

			questions.add(question);
		}
		db.close();

		return questions;
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
