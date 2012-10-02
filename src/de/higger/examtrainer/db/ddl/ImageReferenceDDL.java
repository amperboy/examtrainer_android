package de.higger.examtrainer.db.ddl;

public class ImageReferenceDDL {
	public static final String CREATE_TABLE = "CREATE TABLE "
			+ ImageReferenceDDL.TABLE_NAME + " ("
			+ ImageReferenceDDL.COLUMNNAME_QUESTION_ID + " INTEGER);";

	public static final String TABLE_NAME = "image_reference";

	public static final String COLUMNNAME_QUESTION_ID = "id_question";
}
