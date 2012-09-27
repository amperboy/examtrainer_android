package de.higger.examtrainer.db.ddl;

public class ExamDDL {
	public static final String CREATE_TABLE = "CREATE TABLE " + ExamDDL.TABLE_NAME + " ("
			+ ExamDDL.COLUMNNAME_ID + " INTEGER PRIMARY KEY, "
			+ ExamDDL.COLUMNNAME_NAME + " TEXT);";
	
	public static final String TABLE_NAME = "exam";

	public static final String COLUMNNAME_ID = "id";
	public static final String COLUMNNAME_NAME = "name";
}
