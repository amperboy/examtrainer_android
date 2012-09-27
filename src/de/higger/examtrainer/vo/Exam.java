package de.higger.examtrainer.vo;

import java.io.Serializable;

import de.higger.examtrainer.tool.StringBase64;

public class Exam implements Serializable {
	private static final long serialVersionUID = 4976704795787994222L;
	
	private int id;
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return StringBase64.decode(name);
	}

	public void setName(String name) {
		this.name = StringBase64.encode(name);
	}

	@Override
	public String toString() {
		return getName();
	}
}
