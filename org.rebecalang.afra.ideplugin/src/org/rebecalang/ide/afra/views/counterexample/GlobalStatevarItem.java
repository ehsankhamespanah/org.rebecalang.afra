package org.rebecalang.ide.afra.views.counterexample;

public class GlobalStatevarItem extends StatevarItem {

	private GlobalVariablesItem globalVariablesItem;

	public GlobalStatevarItem(String name, String value, GlobalVariablesItem globalVariablesItem) {
		super(name, value);
		this.globalVariablesItem = globalVariablesItem;
	}

	public GlobalStatevarItem(String name, String value) {
		super(name, value);
	}

	public GlobalVariablesItem getGlobalVariables() {
		return globalVariablesItem;
	}

	public void setGlobalVariables(GlobalVariablesItem globalVariablesItem) {
		this.globalVariablesItem = globalVariablesItem;
	}

	@Override
	public Item getParent() {
		return globalVariablesItem;
	}

}
