package de.higger.examtrainer.db.ddl;

public class QuestionResultDDL {

	public static final String CREATE_TABLE = "CREATE TABLE "
			+ QuestionResultDDL.TABLE_NAME + " ("
			+ QuestionResultDDL.COLUMNNAME_QUESTION_ID + " INTEGER REFERENCES "
			+ QuestionDDL.TABLE_NAME + "(" + QuestionDDL.COLUMNNAME_ID
			+ ") ON DELETE CASCADE, "
			+ QuestionResultDDL.COLUMNNAME_ANSWERED_CORRECT + " INTEGER, "
			+ QuestionResultDDL.COLUMNNAME_ANSWERED_WRONG + " INTEGER);";

	public static final String TABLE_NAME = "questionresult";

	public static final String COLUMNNAME_QUESTION_ID = "id_question";
	public static final String COLUMNNAME_ANSWERED_CORRECT = "answered_correct";
	public static final String COLUMNNAME_ANSWERED_WRONG = "answered_wrong";
}
