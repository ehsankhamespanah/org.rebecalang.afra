package org.rebecalang.afra.ideplugin.view.counterexample;

public abstract class StatevarItem extends Item {
	private String value;

	public StatevarItem(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
