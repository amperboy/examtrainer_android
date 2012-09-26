package de.higger.examtrainer.db;

public class QuestionDDL {
	public static final String CREATE_TABLE = "CREATE TABLE "
			+ QuestionDDL.TABLE_NAME + " (" + QuestionDDL.COLUMN_NAME_ID
			+ " INTEGER PRIMARY KEY, " + QuestionDDL.COLUMN_NAME_EXAM_ID
			+ " INTEGER REFERENCES " + ExamDDL.TABLE_NAME
			+ " ON DELETE CASCADE, " + QuestionDDL.COLUMN_NAME_QUESTION
			+ " TEXT);";

	public static final String TABLE_NAME = "question";

	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_EXAM_ID = "id_exam";
	public static final String COLUMN_NAME_QUESTION = "question";
}
