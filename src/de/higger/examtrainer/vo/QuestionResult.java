package de.higger.examtrainer.vo;

import java.io.Serializable;

public class QuestionResult implements Serializable {
	private static final long serialVersionUID = -7185575036819220714L;

	private int questionId;
	private int answeredCorrect;
	private int answeredWrong;

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public int getAnsweredCorrect() {
		return answeredCorrect;
	}

	public void setAnsweredCorrect(int answeredCorrect) {
		this.answeredCorrect = answeredCorrect;
	}

	public int getAnsweredWrong() {
		return answeredWrong;
	}

	public void setAnsweredWrong(int answeredWrong) {
		this.answeredWrong = answeredWrong;
	}
}
