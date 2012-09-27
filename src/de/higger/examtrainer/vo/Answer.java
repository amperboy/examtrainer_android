package de.higger.examtrainer.vo;

import java.io.Serializable;

import de.higger.examtrainer.tool.StringBase64;

public class Answer implements Serializable {
	private static final long serialVersionUID = 2825355352108787937L;

	private int id;
	private String answer;
	private boolean isCorrect;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAnswer() {
		return StringBase64.decode(answer);
	}

	public void setAnswer(String answer) {
		this.answer = StringBase64.encode(answer);
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	@Override
	public String toString() {
		return "Answer [id=" + id + ", answer=" + getAnswer() + ", isCorrect="
				+ isCorrect + "]";
	}

}
