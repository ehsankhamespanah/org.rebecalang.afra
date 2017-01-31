package org.rebecalang.ide.afra.views.counterexample;

public abstract class Item {
	protected String name = "";

	public final int compareTo(Item item) {
		return this.name.compareTo(item.name);
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public abstract Item getParent();
}
