package de.higger.examtrainer.vo;

import java.io.Serializable;

public class ImageReference implements Serializable {
	private static final long serialVersionUID = -6595155949609790389L;

	private int questionId;

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
}
