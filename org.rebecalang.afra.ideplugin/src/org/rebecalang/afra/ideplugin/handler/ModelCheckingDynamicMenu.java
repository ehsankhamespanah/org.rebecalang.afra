package org.rebecalang.afra.ideplugin.handler;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

public class ModelCheckingDynamicMenu extends CompoundContributionItem implements IWorkbenchContribution {
	  private IServiceLocator _serviceLocator;  
	  private long _mLastTimeStamp = 0;  
	  private IContributionItem[] _contributionItems;
	  public ModelCheckingDynamicMenu () {  
	     _contributionItems =  new IContributionItem[0];
	  }  
	  public ModelCheckingDynamicMenu (final String id_p) {  
	   super(id_p);  
	   _contributionItems =  new IContributionItem[0];
	  }  
	  @Override
	  protected IContributionItem[] getContributionItems() {
	    _contributionItems = new IContributionItem[2];
	      addPopupMenu("net.osgiliath.pulldown.blue", "Blue", 0);
	      addPopupMenu("net.osgiliath.pulldown.red", "Red", 0);
	    return _contributionItems;
	  }
	  public void addPopupMenu(String id_p, String name_p, int index_p) {
	    Map parameters= new HashMap();
	    parameters.put("color.name", name_p);
	    CommandContributionItemParameter params = new CommandContributionItemParameter(_serviceLocator, id_p,"net.osgiliath.commands.menus.showColorsCommand", parameters, null, null, null, name_p, "Select color " +name_p, "Select color " +name_p + " on menu", CommandContributionItem.STYLE_PULLDOWN, null, true);
	    _contributionItems[index_p] = new CommandContributionItem(params);;   
	  }
	  @Override
	  public void initialize(IServiceLocator serviceLocator_p) {
	    _serviceLocator = serviceLocator_p;
	  }
	  @Override  
	  public boolean isDirty() {  
	   return _mLastTimeStamp + 5000 < System.currentTimeMillis();  
	  }  
	
}
