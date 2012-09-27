package de.higger.examtrainer.db.service;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.higger.examtrainer.db.ddl.ExamDBHelper;
import de.higger.examtrainer.db.ddl.QuestionDDL;
import de.higger.examtrainer.vo.Answer;
import de.higger.examtrainer.vo.Question;

public class QuestionDBService {
	private ExamDBHelper examDBHelper;
	private AnswerDBService answerDBService;

	public QuestionDBService(Context context) {
		examDBHelper = new ExamDBHelper(context);

		answerDBService = new AnswerDBService(context);
	}

	public List<Question> getQuestions(int examId) {
		SQLiteDatabase db = examDBHelper.getReadableDatabase();
		Cursor c = db.query(QuestionDDL.TABLE_NAME, new String[] {
				QuestionDDL.COLUMNNAME_ID, QuestionDDL.COLUMNNAME_QUESTION, QuestionDDL.COLUMNNAME_HAS_IMAGE },
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
