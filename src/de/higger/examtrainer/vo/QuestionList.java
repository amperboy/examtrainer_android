package de.higger.examtrainer.vo;

import java.io.Serializable;
import java.util.List;

public class QuestionList implements Serializable {
	private static final long serialVersionUID = -2667665170168242194L;

	private List<Question> questions;

	public List<Question> getQuestions() {
		return questions;
	}
	
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
}
