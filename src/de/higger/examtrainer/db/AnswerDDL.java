package de.higger.examtrainer.db;

public class AnswerDDL {
	public static final String CREATE_TABLE = "CREATE TABLE "
			+ AnswerDDL.TABLE_NAME + " (" + AnswerDDL.COLUMN_NAME_ID
			+ " INTEGER PRIMARY KEY, " + AnswerDDL.COLUMN_NAME_QUESTION_ID
			+ " INTEGER REFERENCES " + ExamDDL.TABLE_NAME
			+ " ON DELETE CASCADE, " + AnswerDDL.COLUMN_NAME_ANSWER + " TEXT, "
			+ AnswerDDL.COLUMN_NAME_IS_CORRECT + " NUMERIC);";

	public static final String TABLE_NAME = "answer";

	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_QUESTION_ID = "id_question";
	public static final String COLUMN_NAME_ANSWER = "answer";
	public static final String COLUMN_NAME_IS_CORRECT = "is_correct";
}
