package de.higger.examtrainer.db.service;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.higger.examtrainer.Constants;
import de.higger.examtrainer.db.ddl.ExamDBHelper;
import de.higger.examtrainer.db.ddl.ImageReferenceDDL;
import de.higger.examtrainer.db.ddl.QuestionDDL;
import de.higger.examtrainer.vo.ImageReference;

public class ImageReferenceDBService {
	private final String LOG_TAG = Constants.LOG_TAG_PRE
			+ getClass().getSimpleName();

	private ExamDBHelper examDBHelper;

	public ImageReferenceDBService(Context context) {
		examDBHelper = new ExamDBHelper(context);
	}

	public List<ImageReference> getUnassignedImageReferences() {
		String query = "SELECT " + ImageReferenceDDL.COLUMNNAME_QUESTION_ID
				+ " FROM " + ImageReferenceDDL.TABLE_NAME + " WHERE "
				+ ImageReferenceDDL.COLUMNNAME_QUESTION_ID + " NOT IN (SELECT "
				+ QuestionDDL.COLUMNNAME_ID + " FROM " + QuestionDDL.TABLE_NAME
				+ ");";

		SQLiteDatabase db = examDBHelper.getReadableDatabase();
		Cursor c = db.rawQuery(query, null);
		List<ImageReference> unassigneImageReferences = new LinkedList<ImageReference>();
		while (c.moveToNext()) {
			ImageReference imageReference = new ImageReference();
			imageReference.setQuestionId(c.getInt(0));

			unassigneImageReferences.add(imageReference);
		}
		c.close();
		db.close();

		return unassigneImageReferences;
	}

	public void removeUnassignedImageReferences(
			List<ImageReference> unassignedImageReferences) {

		Log.v(LOG_TAG, "remove image " + unassignedImageReferences.size()
				+ " references");

		StringBuilder imageIds = new StringBuilder();
		int i = 0;
		for (ImageReference imageReference : unassignedImageReferences) {
			if (i != 0) {
				imageIds.append(',');
			}

			imageIds.append(imageReference.getQuestionId());

			i++;
		}

		SQLiteDatabase db = examDBHelper.getWritableDatabase();
		db.rawQuery("DELETE FROM " + ImageReferenceDDL.TABLE_NAME + " WHERE "
				+ ImageReferenceDDL.COLUMNNAME_QUESTION_ID + " IN(?)",
				new String[] { imageIds.toString() });
		db.close();

	}

	public void remove(int questionId) {
		SQLiteDatabase db = examDBHelper.getWritableDatabase();
		db.delete(ImageReferenceDDL.TABLE_NAME,
				ImageReferenceDDL.COLUMNNAME_QUESTION_ID + " = ? ",
				new String[] { Integer.toString(questionId) });
		db.close();
	}

	public void create(int questionId) {
		SQLiteDatabase db = examDBHelper.getWritableDatabase();

		ContentValues avalues = new ContentValues();
		avalues.put(ImageReferenceDDL.COLUMNNAME_QUESTION_ID, questionId);

		db.insert(ImageReferenceDDL.TABLE_NAME, null, avalues);
		db.close();
	}
}
