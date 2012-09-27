package de.higger.examtrainer.db.ddl;

public class AnswerDDL {
	public static final String CREATE_TABLE = "CREATE TABLE "
			+ AnswerDDL.TABLE_NAME + " (" + AnswerDDL.COLUMNNAME_ID
			+ " INTEGER PRIMARY KEY, " + AnswerDDL.COLUMNNAME_QUESTION_ID
			+ " INTEGER REFERENCES " + QuestionDDL.TABLE_NAME
			+ "("+QuestionDDL.COLUMNNAME_ID+") ON DELETE CASCADE, " + AnswerDDL.COLUMNNAME_ANSWER + " TEXT, "
			+ AnswerDDL.COLUMNNAME_IS_CORRECT + " NUMERIC);";

	public static final String TABLE_NAME = "answer";

	public static final String COLUMNNAME_ID = "id";
	public static final String COLUMNNAME_QUESTION_ID = "id_question";
	public static final String COLUMNNAME_ANSWER = "answer";
	public static final String COLUMNNAME_IS_CORRECT = "is_correct";
}
