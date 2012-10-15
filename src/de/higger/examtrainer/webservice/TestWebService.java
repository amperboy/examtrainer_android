package de.higger.examtrainer.webservice;

import java.util.HashMap;

import android.content.Context;

import com.google.gson.Gson;

import de.higger.examtrainer.exception.WSRequestFailedException;
import de.higger.examtrainer.tool.WebService;
import de.higger.examtrainer.vo.Test;

public class TestWebService {
	private WebService webService;

	public TestWebService(Context context, String webservicePrefix) {
		String url = webservicePrefix + "test.html";
		webService = new WebService(url);
	}

	public int receiveWSVersion() throws WSRequestFailedException {
		String response = webService.webGet("", new HashMap<String, String>());

		if (null == response) {
			throw new WSRequestFailedException(
					"WebService request fehlgeschlagen.");
		}

		try {
			Test exams = new Gson().fromJson(response, Test.class);
			return exams.getVersion();
		} catch (Exception e) {
			throw new WSRequestFailedException(e);
		}
	}
}
