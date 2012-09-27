package de.higger.examtrainer.db.service;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.higger.examtrainer.db.ddl.ExamDBHelper;
import de.higger.examtrainer.db.ddl.ExamDDL;
import de.higger.examtrainer.vo.Exam;

public class ExamDBService {
	private ExamDBHelper examDBHelper;

	public ExamDBService(Context context) {
		examDBHelper = new ExamDBHelper(context);
	}

	public List<Exam> getAllExams() {
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

	public void removeAllExams() {
		SQLiteDatabase db = examDBHelper.getWritableDatabase();
		db.delete(ExamDDL.TABLE_NAME, null, null);
		db.close();
	}

	public void addExam(Exam exam) {
		SQLiteDatabase db = examDBHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ExamDDL.COLUMNNAME_ID, exam.getId());
		values.put(ExamDDL.COLUMNNAME_NAME, exam.getName());

		db.insert(ExamDDL.TABLE_NAME, null, values);
		db.close();
	}
}
