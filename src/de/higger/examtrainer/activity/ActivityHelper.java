package de.higger.examtrainer.activity;

import java.io.File;

import android.app.Activity;
import android.net.Uri;

public class ActivityHelper {
	public static Uri getImagePath(Activity activity, int questionId) {
		File filesDir = activity.getFilesDir();
		final Uri imageUri = Uri.parse(filesDir.getAbsolutePath()
				+ "/question_image_" + questionId + ".jpg");

		return imageUri;
	}
}
