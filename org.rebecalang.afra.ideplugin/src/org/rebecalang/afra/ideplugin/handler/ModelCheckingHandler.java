package org.rebecalang.afra.ideplugin.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
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
		if(!CompileHandler.validateActiveFile(codeEditor)) {
			CompileHandler.showNoActiveRebecaFileErrorDialog(event);
			return null;
		}
		IResource activeFile = codeEditor.getEditorInput().getAdapter(IFile.class);
		if (activeFile.getFileExtension().equals("property")) {
			File rebecaFile = new File(activeFile.getRawLocation().toString().substring(0,
					activeFile.getRawLocation().toString().indexOf(activeFile.getName()))
					+ activeFile.getName().substring(0, activeFile.getName().indexOf(".property"))
					+ ".rebeca");
			activeFile = activeFile.getProject().getWorkspace().getRoot().getFileForLocation(new org.eclipse.core.runtime.Path(rebecaFile.getAbsolutePath()));
		}
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
						params = new String[7];
						params[5] = "-p";
						params[6] = selectedPropertyName;
					} else {
						params = new String[5];
					}
					params[0] = outputFolder + executableFileName;
					params[1] = "-o";
					params[2] = "output.xml";
					params[3] = "-g";
					params[4] = "progress";
					final String finalOutputFolder = outputFolder;
					try {
						final Process p = Runtime.getRuntime().exec(params, null,
								new File(finalOutputFolder));
						ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell()) {

							@Override
							protected void cancelPressed() {
								super.cancelPressed();
								p.destroyForcibly();
							}
							
						};
						progressMonitorDialog.run(true, true,
								new IRunnableWithProgress() {
									@Override
									public void run(IProgressMonitor monitor)
											throws InvocationTargetException, InterruptedException {
										monitor.beginTask("Performing Model Checking",
												IProgressMonitor.UNKNOWN);
										try {
											RepeatingJob job = new RepeatingJob() {			
												protected IStatus run(IProgressMonitor localMonitor){ 
													try {
														BufferedReader reader = 
																new BufferedReader(new InputStreamReader(new FileInputStream(finalOutputFolder + "progress")));
														String line, backup = "";
														while ((line = reader.readLine())!= null) {
															backup = line;
														};
														monitor.subTask(backup + " are generated.");
														reader.close();
												  		schedule(2000);
													} catch (IOException e) {
														e.printStackTrace();
													}
											  		return org.eclipse.core.runtime.Status.OK_STATUS;
												}
											};
											job.schedule();  
											
											p.waitFor();
											BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
											if (reader.ready()) {
												String line;
												while ((line = reader.readLine()) != null) {
													System.out.println(line);
												}
											}
											showResult(finalOutputFolder);

										} catch (IOException e1) {
											e1.printStackTrace();
										}
									}
								});
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (InterruptedException | CoreException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void showResult(final String finalOutputFolder) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				AnalysisResultView view = (AnalysisResultView) ViewUtils.getViewPart(AnalysisResultView.class.getName());
				File modelCheckingResultFile = new File(finalOutputFolder + "output.xml");
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
								ceView.update(finalOutputFolder + "output.xml");
								
							}
							else {
								ViewUtils.counterExampleVisible(false);
							}
					} catch (JAXBException | IOException e) {
						e.printStackTrace();
					}
				}														
				view.update();
			}
		});
	}
	public class RepeatingJob extends Job {
		private boolean running = true;
		public RepeatingJob() {
			super("Repeating Job");
		}
		protected IStatus run(IProgressMonitor monitor) {
			schedule(60000);
			return Status.OK_STATUS;
		}
		public boolean shouldSchedule() {
			return running;
		}
		public void stop() {
			running = false;
		}
	}
}
