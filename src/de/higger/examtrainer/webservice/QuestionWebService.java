package de.higger.examtrainer.webservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.google.gson.Gson;

import de.higger.examtrainer.R;
import de.higger.examtrainer.exception.WSRequestFailedException;
import de.higger.examtrainer.tool.WebService;
import de.higger.examtrainer.vo.Question;
import de.higger.examtrainer.vo.QuestionList;

public class QuestionWebService {
	private WebService webService;

	public QuestionWebService(Context context) {
		String url = context.getString(R.string.constants_uri_ws_prefix) + "get_all_questions.php";
		webService = new WebService(url);
	}
	
	public List<Question> getQuestions(int examId) throws WSRequestFailedException {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("id_exam", Integer.toString(examId));
		
		String response = webService.webGet("", parameters);

		if (null == response) {
			throw new WSRequestFailedException("WebService request fehlgeschlagen.");
		}

		QuestionList questionsList = new Gson().fromJson(response, QuestionList.class);
		
		return questionsList.getQuestions();
	}
}
