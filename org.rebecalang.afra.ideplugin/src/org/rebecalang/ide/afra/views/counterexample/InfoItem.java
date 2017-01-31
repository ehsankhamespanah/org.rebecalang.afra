package org.rebecalang.ide.afra.views.counterexample;

import java.util.ArrayList;
import java.util.List;

public class InfoItem extends Item {

	private List<PropertyItem> proList = new ArrayList<PropertyItem>();

	public boolean addInfoItem(PropertyItem p) {
		return proList.add(p);
	}

	public List<PropertyItem> getInfoItems() {
		return proList;
	}

	public Item getParent() {
		return null;
	}

	public InfoItem(String name) {
		super();
		this.name = name;
	}

}
