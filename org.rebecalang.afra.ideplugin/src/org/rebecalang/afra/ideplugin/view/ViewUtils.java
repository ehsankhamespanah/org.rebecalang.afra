package org.rebecalang.afra.ideplugin.view;

import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.E4PartWrapper;
import org.eclipse.ui.part.ViewPart;

@SuppressWarnings("restriction")
public class ViewUtils {
	public static ViewPart getViewPart(String id) {
		try {
			E4PartWrapper newPart = (E4PartWrapper) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView(id);
			EPartService partService = 
					(EPartService) newPart.getViewSite().getService(EPartService.class); 
			return (ViewPart) partService.findPart(id).getObject();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}
}
