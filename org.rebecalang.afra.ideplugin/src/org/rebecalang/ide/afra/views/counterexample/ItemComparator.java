package org.rebecalang.ide.afra.views.counterexample;

import java.util.Comparator;

public class ItemComparator implements Comparator<Item> {

	public int compare(Item o1, Item o2) {
		return o1.compareTo(o2);
	}

}
