package de.higger.examtrainer.db.ddl;

public class QuestionDDL {
	public static final String CREATE_TABLE = "CREATE TABLE "
			+ QuestionDDL.TABLE_NAME + " (" + QuestionDDL.COLUMNNAME_ID
			+ " INTEGER PRIMARY KEY, " + QuestionDDL.COLUMNNAME_EXAM_ID
			+ " INTEGER REFERENCES " + ExamDDL.TABLE_NAME
			+ "("+ExamDDL.COLUMNNAME_ID+") ON DELETE CASCADE, " + QuestionDDL.COLUMNNAME_QUESTION
			+ " TEXT);";

	public static final String TABLE_NAME = "question";

	public static final String COLUMNNAME_ID = "id";
	public static final String COLUMNNAME_EXAM_ID = "id_exam";
	public static final String COLUMNNAME_QUESTION = "question";
}
