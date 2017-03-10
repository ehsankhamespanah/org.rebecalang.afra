package org.rebecalang.afra.ideplugin.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rebecalang.afra.ideplugin.propertypages.PropertySelectionDialog;
import org.rebecalang.afra.ideplugin.view.AnalysisResultView;
import org.rebecalang.afra.ideplugin.view.CounterExampleGraphView;
import org.rebecalang.afra.ideplugin.view.ViewUtils;
import org.rebecalang.afra.ideplugin.view.modelcheckreport.resultobjectmodel.ModelCheckingReport;

public class ModelCheckingHandler extends AbstractHandler {
	public ModelCheckingHandler() {
	}

	private boolean delagateToCompileCommand(ExecutionEvent event) throws ExecutionException {
		CompileHandler compile = new CompileHandler();
		try {
			return (boolean) compile.execute(event, false);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		TextEditor codeEditor = (TextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
		String outputFolder;
		if (codeEditor == null) {
			MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Error",
					"No Rebeca file is active in the editor.");
		} else {
			IResource activeFile = codeEditor.getEditorInput().getAdapter(IFile.class);
			if (!activeFile.getName().contains(".rebeca")) {
				MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Error",
						"No Rebeca file is active in the editor.");
			} else {
				outputFolder = activeFile.getFullPath().toString().substring(1);
				outputFolder = Platform.getLocation().toOSString() + "/"
						+ outputFolder.substring(0, outputFolder.indexOf('/')) + "/out/"
						+ activeFile.getName().substring(0, activeFile.getName().indexOf(".rebeca")) + "/";
				try {

					String executableFileName = "execute" + ((System.getProperty("os.name").contains("Windows")) ? ".exe" : "");

					Path execFile = Paths.get(outputFolder + executableFileName);
					Path rebecaFile = Paths.get(activeFile.getRawLocation().toString());
					Path propertyFile = Paths.get(activeFile.getRawLocation().toString().substring(0,
							activeFile.getRawLocation().toString().indexOf(activeFile.getName()))
							+ activeFile.getName().substring(0, activeFile.getName().indexOf(".rebeca")) + ".property");
					boolean compilationResult = true;
					try {
						BasicFileAttributes execAttr = Files.readAttributes(execFile, BasicFileAttributes.class);
						BasicFileAttributes rebecaAttr = Files.readAttributes(rebecaFile, BasicFileAttributes.class);
						BasicFileAttributes propAttr = Files.readAttributes(propertyFile, BasicFileAttributes.class);
						if (rebecaAttr.lastModifiedTime().toMillis() > execAttr.lastModifiedTime().toMillis() || 
								propAttr.lastModifiedTime().toMillis() > execAttr.lastModifiedTime().toMillis() ) {
							compilationResult = delagateToCompileCommand(event);
						}
					} catch (IOException e) {
						compilationResult = delagateToCompileCommand(event);
					}
					if (compilationResult) {
						String definedProperties = activeFile.getProject().getPersistentProperty(new QualifiedName("rebeca", "definedProperties"));
						
						String[] properiesNameList = definedProperties.isEmpty() ? new String[0] : definedProperties.split(";");

						PropertySelectionDialog dialog = new PropertySelectionDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
								properiesNameList);
						dialog.create();
						if (dialog.open() == TitleAreaDialog.OK) {
							String selectedPropertyName = dialog.getSelectedPropertyName();
							String[] params;
							if (selectedPropertyName != null) {
								params = new String[5];
								params[3] = "-p";
								params[4] = selectedPropertyName;
							} else {
								params = new String[3];
							}
							params[0] = outputFolder + executableFileName;
							params[1] = "-o";
							params[2] = "output.xml";

							Process p = Runtime.getRuntime().exec(params, null,
									new File(outputFolder));
							p.waitFor();
							BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
							String line;
							while ((line = reader.readLine()) != null) {
								System.out.println(line);
							}
							
							AnalysisResultView view = (AnalysisResultView) ViewUtils.getViewPart(AnalysisResultView.class.getName());

							File modelCheckingResultFile = new File(outputFolder + "output.xml");
							if (modelCheckingResultFile.exists() && modelCheckingResultFile.length() > 0) {
								try {
									JAXBContext jaxbContext;
									jaxbContext = JAXBContext.newInstance(ModelCheckingReport.class.getPackage().getName());
									Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
									ModelCheckingReport modelCheckingReport = (ModelCheckingReport) unmarshaller.unmarshal(modelCheckingResultFile);
									view.setReport(modelCheckingReport);
									if (modelCheckingReport != null)
										if (!modelCheckingReport.getCheckedProperty().getResult().equals("satisfied")) {
											ViewUtils.counterExampleVisible(true);
											CounterExampleGraphView ceView = 
													(CounterExampleGraphView) ViewUtils.getViewPart(CounterExampleGraphView.class.getName());
											ceView.update(outputFolder + "output.xml");
											
										}
										else {
											IWorkbenchPage page = 
											PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 
											IWorkbenchPartReference myView = page.findViewReference(CounterExampleGraphView.class.getName());
											if (myView != null)
												page.setPartState(myView, IWorkbenchPage.STATE_MINIMIZED); 
											
										}
											
								} catch (JAXBException e) {
									e.printStackTrace();
								}
							}

							view.update();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}


		return null;
	}

}
