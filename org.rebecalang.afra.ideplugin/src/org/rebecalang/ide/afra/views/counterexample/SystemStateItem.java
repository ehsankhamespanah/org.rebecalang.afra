package org.rebecalang.ide.afra.views.counterexample;

import java.util.ArrayList;
import java.util.List;

public class SystemStateItem extends Item {
	private List<RebecItem> rebecItems = new ArrayList<RebecItem>();

	private List<PropertyItem> propertyItems = new ArrayList<PropertyItem>();

	private List<InfoItem> infoItems = new ArrayList<InfoItem>();

	private GlobalVariablesItem globalVariablesItem;

	private Integer id;

	public GlobalVariablesItem getGlobalVariablesItem() {
		return globalVariablesItem;
	}

	public void setGlobalVariablesItem(GlobalVariablesItem globalVariablesItem) {
		this.globalVariablesItem = globalVariablesItem;
	}

	public boolean addRebecItem(RebecItem rebecItem) {
		return rebecItems.add(rebecItem);
	}

	public List<RebecItem> getRebecItems() {
		return rebecItems;
	}

	@Override
	public Item getParent() {
		return null;
	}

	public boolean addPropertyItem(PropertyItem pItem) {
		return propertyItems.add(pItem);
	}

	public List<PropertyItem> getPropertyItems() {
		return propertyItems;
	}

	public boolean addInfoItem(InfoItem iItem) {
		return infoItems.add(iItem);
	}

	public List<InfoItem> getInfoItems() {
		return infoItems;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
