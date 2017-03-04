 

import java.util.Date;
import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

public class DataContributionTest {
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) {
	    MDirectMenuItem dynamicItem = MMenuFactory.INSTANCE
	            .createDirectMenuItem();
	    dynamicItem.setLabel("Dynamic Menu Item (" + new Date() + ")");
	    dynamicItem.setContributorURI("platform:/plugin/at.descher.test");
	    dynamicItem
	            .setContributionURI("bundleclass://at.descher.test/at.descher.test.DirectMenuItem");    
	        items.add(dynamicItem);
	}
}