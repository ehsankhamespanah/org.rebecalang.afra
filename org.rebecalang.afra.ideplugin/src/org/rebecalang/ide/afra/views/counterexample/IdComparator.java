package org.rebecalang.ide.afra.views.counterexample;

import java.util.Comparator;

public class IdComparator implements Comparator<SystemStateItem> {

	public int compare(SystemStateItem o1, SystemStateItem o2) {
		if (o1.getId() > o2.getId())
			return 1;
		else if (o1.getId() < o2.getId())
			return -1;
		return 0;
	}
}
