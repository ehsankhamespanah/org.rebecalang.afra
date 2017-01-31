package org.rebecalang.ide.afra.views.counterexample;

public class PropertyItem extends Item {

	private String value;

	private Item parent;

	public PropertyItem(String name, String value, Item parent) {
		this.name = name;
		this.value = value;
		this.parent = parent;
	}

	public void setParent(Item parent) {
		this.parent = parent;
	}

	@Override
	public Item getParent() {
		return parent;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
