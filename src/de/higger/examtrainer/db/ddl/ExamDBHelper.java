package de.higger.examtrainer.db.ddl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.higger.examtrainer.Constants;

public class ExamDBHelper extends SQLiteOpenHelper {
	private final String TAG = Constants.LOG_TAG_PRE
			+ getClass().getSimpleName();

	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "mgiebel_examdb";

	public ExamDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// version 1
		db.execSQL(ExamDDL.CREATE_TABLE);
		db.execSQL(QuestionDDL.CREATE_TABLE);
		db.execSQL(AnswerDDL.CREATE_TABLE);
		db.execSQL(QuestionResultDDL.CREATE_TABLE);

		Log.v(TAG, "Database created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int versionOld, int versionNew) {
		Log.v(TAG, "Database updated from version " + versionOld + " to "
				+ versionNew);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}
}
