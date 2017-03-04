package org.rebecalang.afra.ideplugin.view.counterexample;

import java.util.ArrayList;
import java.util.List;

public class GlobalVariablesItem extends Item {
	private List<GlobalStatevarItem> globalStatevarItems = new ArrayList<GlobalStatevarItem>();

	public boolean addVariable(GlobalStatevarItem gStatevar) {
		return globalStatevarItems.add(gStatevar);
	}

	public List<GlobalStatevarItem> getGlobalStatevarItems() {
		return globalStatevarItems;
	}

	@Override
	public Item getParent() {
		return null;
	}
}
