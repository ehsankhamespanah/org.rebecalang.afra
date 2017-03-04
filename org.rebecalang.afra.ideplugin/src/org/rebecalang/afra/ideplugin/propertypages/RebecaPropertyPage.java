package org.rebecalang.afra.ideplugin.propertypages;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;

public class RebecaPropertyPage  extends PropertyPage implements IWorkbenchPropertyPage {
	@Override
	protected Control createContents(Composite parent) {
    
		noDefaultButton();
		
		String version = this.getVersion();
		String type = this.getType();
		
		Composite composite = new Composite(parent, SWT.NULL);
	    GridLayout gridLayout = new GridLayout(2 ,false);
	    composite.setLayout(gridLayout);
		Label labelForType = new Label(composite, SWT.NONE);	
		labelForType.setText("Project Type: ");
		labelForType.setFont(JFaceResources.getFontRegistry().getBold(""));
		
		Label typeT = new Label(composite, SWT.NONE);
		typeT.setText(type);

		
	    Label labelForVersion = new Label(composite, SWT.NONE);
	    labelForVersion.setText("Project Version: ");
		labelForVersion.setFont(JFaceResources.getFontRegistry().getBold(""));

		Label versionT = new Label(composite, SWT.NONE);
		versionT.setText(version);
		
		return composite;
	}
	
	private String getVersion(){
		String version = "";
		IProject project;
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	    ISelection selection = selectionService.getSelection("org.eclipse.ui.navigator.ProjectExplorer");
	    if(selection instanceof IStructuredSelection) {    
	        Object element = ((IStructuredSelection)selection).getFirstElement();    

	        if (element instanceof IResource) {    
	            project = ((IResource)element).getProject();
	            try {
					version = project.getPersistentProperty(new QualifiedName("rmc.modelcheck", "projectVersion"));
				} catch (CoreException e) {
					e.printStackTrace();
				}
	        }
	    }
		return version;
	}
	
	private String getType(){
		String type = "";
		IProject project;
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	    ISelection selection = selectionService.getSelection("org.eclipse.ui.navigator.ProjectExplorer");
	    if(selection instanceof IStructuredSelection) {    
	        Object element = ((IStructuredSelection)selection).getFirstElement();    

	        if (element instanceof IResource) {    
	            project = ((IResource)element).getProject();
	            try {
	            	type = project.getPersistentProperty(new QualifiedName("rmc.modelcheck", "projectType"));
				} catch (CoreException e) {
					e.printStackTrace();
				}
	        }
	    }
		return type;
	}
	/*try {
	String type = project.getPersistentProperty(new QualifiedName("rmc.modelcheck", "projectType"));
	String version = project.getPersistentProperty(new QualifiedName("rmc.modelcheck", "projectVersion"));
	label.setText("Project Type: " + type);
	label1.setText("Project Version: " + version);
} catch (CoreException e) {

	e.printStackTrace();
}*/

}
