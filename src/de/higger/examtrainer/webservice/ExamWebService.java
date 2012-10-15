package de.higger.examtrainer.webservice;

import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;

import de.higger.examtrainer.exception.WSRequestFailedException;
import de.higger.examtrainer.tool.PrefsHelper;
import de.higger.examtrainer.tool.PrefsHelper.Preferences;
import de.higger.examtrainer.tool.WebService;
import de.higger.examtrainer.vo.Exam;

public class ExamWebService {
	private class ExamsList {
		private List<Exam> exams;

		public List<Exam> getExams() {
			return exams;
		}
	}

	private WebService webService;

	public ExamWebService(Context context) {
		PrefsHelper prefsHelper = new PrefsHelper(context);
		String url = prefsHelper.read(Preferences.PREF_WS_URI)
				+ "get_exams.php";
		webService = new WebService(url);
	}

	public List<Exam> getExams() throws WSRequestFailedException {
		String response = webService.webGet("", new HashMap<String, String>());

		if (null == response) {
			throw new WSRequestFailedException(
					"WebService request fehlgeschlagen.");
		}

		try {
			ExamsList exams = new Gson().fromJson(response, ExamsList.class);

			return exams.getExams();
		} catch (Exception e) {
			throw new WSRequestFailedException(e);
		}
	}
}
