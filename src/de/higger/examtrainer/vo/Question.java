package de.higger.examtrainer.vo;

import java.io.Serializable;
import java.util.List;

import de.higger.examtrainer.tool.StringBase64;

public class Question implements Serializable {
	private static final long serialVersionUID = -5234208147461806142L;

	private int id;
	private String question;
	private boolean image;
	private List<Answer> answers;

	private String desc;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getQuestion() {
		return StringBase64.decode(question);
	}

	public void setQuestion(String question) {
		this.question = StringBase64.encode(question);
	}

	public boolean isImage() {
		return image;
	}

	public void setImage(boolean image) {
		this.image = image;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		return "Question [id=" + id + ", question=" + getQuestion()
				+ ", image=" + image + ", answers=" + answers + "]";
	}
}
