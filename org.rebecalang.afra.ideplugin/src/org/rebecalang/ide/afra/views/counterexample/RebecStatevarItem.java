package org.rebecalang.ide.afra.views.counterexample;

public class RebecStatevarItem extends StatevarItem {
	private RebecItem rebecItem;

	public RebecStatevarItem(String name, String value, RebecItem rebecItem) {
		super(name, value);
		this.rebecItem = rebecItem;
	}

	public RebecItem getRebec() {
		return rebecItem;
	}

	public void setRebec(RebecItem rebecItem) {
		this.rebecItem = rebecItem;
	}

	@Override
	public Item getParent() {
		return rebecItem;
	}

	public boolean equal(RebecStatevarItem r) {
		if(this.getName().equals(r.getName()) && this.getValue().equals(r.getValue())) 
			return true;
		return false;
	}

}
