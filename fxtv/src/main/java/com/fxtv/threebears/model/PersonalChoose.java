package com.fxtv.threebears.model;

public class PersonalChoose {

	public String getName() {
		return name;
	}

	public int getSource() {
		return source;
	}

	private String name;

	private int source;

	public PersonalChoose(String name, int source) {
		this.name = name;
		this.source = source;
	}

}
