package de.higger.examtrainer.db;

public class ExamDDL {
	public static final String CREATE_TABLE = "CREATE TABLE " + ExamDDL.TABLE_NAME + " ("
			+ ExamDDL.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, "
			+ ExamDDL.COLUMN_NAME_NAME + " TEXT);";
	
	public static final String TABLE_NAME = "exam";

	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_NAME = "name";
}
