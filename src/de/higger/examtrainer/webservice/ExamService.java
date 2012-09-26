package de.higger.examtrainer.webservice;

import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;

import de.higger.examtrainer.R;
import de.higger.examtrainer.exception.WSRequestFailedException;
import de.higger.examtrainer.tool.WebService;
import de.higger.examtrainer.vo.Exam;

public class ExamService {
	private class ExamsList {
		private List<Exam> exams;

		public List<Exam> getExams() {
			return exams;
		}
	}
	
	private WebService webService;

	public ExamService(Context context) {
		String url = context.getString(R.string.constants_uri_ws_prefix) + "get_exams.php";
		webService = new WebService(url);
	}

	public List<Exam> getExams() throws WSRequestFailedException {
		String response = webService.webGet("", new HashMap<String, String>());

		if (null == response) {
			throw new WSRequestFailedException("WebService request fehlgeschlagen.");
		}

		ExamsList exams = new Gson().fromJson(response, ExamsList.class);
		
		return exams.getExams();
	}
}
