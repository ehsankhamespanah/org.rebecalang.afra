package org.rebecalang.ide.afra.views.counterexample;

import java.util.ArrayList;
import java.util.List;

public class RebecItem extends Item {

	private String type;

	private List<StatevarItem> statevarItems = new ArrayList<StatevarItem>();

	private MsgsrvQueue msgsrvQueue;

	public MsgsrvQueue getMsgsrvQueue() {
		return msgsrvQueue;
	}

	public void setMsgsrvQueue(MsgsrvQueue msgsrvQueue) {
		this.msgsrvQueue = msgsrvQueue;
	}

	public RebecItem(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

	public List<StatevarItem> getStatevars() {
		return statevarItems;
	}

	public void setStatevars(List<StatevarItem> statevarItems) {
		this.statevarItems = statevarItems;
	}

	public boolean addStatevar(StatevarItem sv) {
		return statevarItems.add(sv);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Item getParent() {
		return null;
	}

}
