package org.rebecalang.afra.ideplugin.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rebecalang.compiler.propertycompiler.PropertyCodeCompilationException;
import org.rebecalang.compiler.propertycompiler.corerebeca.objectmodel.LTLDefinition;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.rmc.AnalysisFeature;
import org.rebecalang.rmc.GenerateFiles;

public class CompileHandler extends AbstractHandler {

	public final static String ID = CompileHandler.class.getName(); 
	
	private Object propertyModel;
	
	static String createErrorMessage(Exception e) {
		return "Cannot create temp file.\n" + e.getMessage();
	}

	private static IMarker createMarker(IResource file, CodeCompilationException cce, boolean isWarning) {
		try {
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.SEVERITY, isWarning ? IMarker.SEVERITY_WARNING : IMarker.SEVERITY_ERROR);
			marker.setAttribute(IMarker.MESSAGE, cce.getMessage());
			marker.setAttribute(IMarker.LINE_NUMBER, cce.getLine());

			return marker;
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean result;

	public Object execute(ExecutionEvent event, boolean showDialog) throws ExecutionException {
		boolean compilationResult = false;
		TextEditor codeEditor = (TextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();

		if (codeEditor == null) {
			showNoActiveRebecaFileErrorDialog(event);
		} else {
			IResource activeFile = codeEditor.getEditorInput().getAdapter(IFile.class);
			if (!activeFile.getFileExtension().equals("rebeca")) {
				showNoActiveRebecaFileErrorDialog(event);
			} else {
				try {
					new ProgressMonitorDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell()).run(true, true,
							new IRunnableWithProgress() {

								@Override
								public void run(IProgressMonitor monitor)
										throws InvocationTargetException, InterruptedException {
									boolean indeterminate = false;
									monitor.beginTask("Compiling the Rebeca model",
											indeterminate ? IProgressMonitor.UNKNOWN : 100);

									File propertyFile = new File(activeFile.getRawLocation().toString().substring(0,
											activeFile.getRawLocation().toString().indexOf(activeFile.getName()))
											+ activeFile.getName().substring(0, activeFile.getName().indexOf(".rebeca"))
											+ ".property");
									if (!propertyFile.exists())
										propertyFile = null;
									String projectPath = activeFile.getFullPath().toString().substring(1);
									File outputFolder = new File(Platform.getLocation().toOSString() + "/"
											+ projectPath.substring(0, projectPath.indexOf('/')) + "/out/"
											+ activeFile.getName().substring(0, activeFile.getName().indexOf(".rebeca"))
											+ "/");

									try {
										activeFile.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
										IFile propertyFileResource = activeFile.getProject().getWorkspace().getRoot().
												getFileForLocation(new Path(propertyFile.getAbsolutePath()));
										propertyFileResource.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
										result = compileSec(activeFile, propertyFile, outputFolder);
										monitor.worked(10);
										if (!result) {
											monitor.done();
										} else {
											// execute generated files

											final File path = outputFolder;
											try {
												String[][] compilerCommand = generateCompilationCommands(path);
												int step = 80 / compilerCommand.length;
												monitor.subTask("Compiling auto generated C++ files");
												for (String[] command : compilerCommand) {
													Process p = Runtime.getRuntime().exec(command, null, path);
													p.waitFor();
													BufferedReader reader = new BufferedReader(
															new InputStreamReader(p.getErrorStream()));
													String line;
													while ((line = reader.readLine()) != null) {
														System.out.println(line);
													}
													reader = new BufferedReader(
															new InputStreamReader(p.getInputStream()));
													while ((line = reader.readLine()) != null) {
														System.out.println(line);
													}
													monitor.worked(step);
												}

												monitor.subTask("Linking auto generated C++ files");
												String[] linkerCommand = generateLinkerCommands(path);
												Process p = Runtime.getRuntime().exec(linkerCommand, null, path);
												p.waitFor();
												BufferedReader reader = new BufferedReader(
														new InputStreamReader(p.getErrorStream()));
												String line;
												while ((line = reader.readLine()) != null) {
													System.out.println(line);
												}
												reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
												while ((line = reader.readLine()) != null) {
													System.out.println(line);
												}

												monitor.done();

											} catch (IOException e) {
												result = false;
												e.printStackTrace();
											} catch (Exception e) {
												e.printStackTrace();
												result = false;
											}
										}
									} catch (IOException e1) {
										result = false;
										e1.printStackTrace();
									} catch (PartInitException e1) {
										result = false;
										e1.printStackTrace();
									} catch (CoreException e) {
										result = false;
										e.printStackTrace();
									}
								}
							});
					if (result) {
						storeDefinedPropertiesNames(codeEditor.getEditorInput().getAdapter(IFile.class).getProject());
						compilationResult = true;
						if (showDialog)
							MessageDialog.openInformation(
									HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
									"Compilation Report",
									activeFile.getName() + " is compiled successfully.");
					} else {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView("org.eclipse.ui.views.ProblemView");
						
					}

				} catch (InvocationTargetException | InterruptedException e2) {
					MessageDialog.openError(
							HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
							"Internal Error", e2.getMessage());
					e2.printStackTrace();
				} catch (PartInitException e) {
					MessageDialog.openError(
							HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
							"Internal Error", e.getMessage());
					e.printStackTrace();
				} catch (CoreException e) {
					MessageDialog.openError(
							HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
							"Internal Error", e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return compilationResult;
	}

	private void storeDefinedPropertiesNames(IProject project) throws CoreException {
		String definedProperties = "";
		if (propertyModel instanceof org.rebecalang.compiler.propertycompiler.corerebeca.objectmodel.PropertyModel) {
			org.rebecalang.compiler.propertycompiler.corerebeca.objectmodel.PropertyModel model = 
					(org.rebecalang.compiler.propertycompiler.corerebeca.objectmodel.PropertyModel) propertyModel;
			for (LTLDefinition definition : model.getLTLDefinitions())
				definedProperties += definition.getName() + ";";
		} else if (propertyModel instanceof org.rebecalang.compiler.propertycompiler.timedrebeca.objectmodel.PropertyModel) {
//			org.rebecalang.compiler.propertycompiler.timedrebeca.objectmodel.PropertyModel model = 
//					(org.rebecalang.compiler.propertycompiler.timedrebeca.objectmodel.PropertyModel) propertyModel;
//			for (TCTLDefinition definition : model.getTCTLDefinitions())
//				definedProperties += definition.getName() + ";";
		}
		project.setPersistentProperty(new QualifiedName("rebeca", "definedProperties"), definedProperties);
	}

	public static void showNoActiveRebecaFileErrorDialog(ExecutionEvent event) {
		MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Error",
				"No Rebeca file is active in the editor.");
	}

	private static String[] generateLinkerCommands(File outputFolder) {
		String files[] = outputFolder.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".o");
			}
		});
		String command[] = new String[files.length + 4];

		command[0] = "g++";
		for (int i = 0; i < files.length; i++)
			command[i + 1] = files[i];
		command[files.length + 1] = "-w";
		command[files.length + 2] = "-o";
		command[files.length + 3] = "execute";
		return command;
	}

	private static String[][] generateCompilationCommands(File outputFolder) {
		String files[] = outputFolder.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".cpp");
			}
		});
		String command[][] = new String[files.length][4];
		for (int cnt = 0; cnt < files.length; cnt++) {
			command[cnt][0] = "g++";
			command[cnt][1] = "-c";
			command[cnt][2] = files[cnt];
			command[cnt][3] = "-w";
		}
		return command;
	}

	public boolean compileSec(IResource rebecaFile, File propertyFile, File outputFolder)
			throws IOException, CoreException {
		boolean result = true;
		IProject project = rebecaFile.getProject();

		Set<CompilerFeature> compilerFeatures = new HashSet<CompilerFeature>();
		Set<AnalysisFeature> analysisFeatures = new HashSet<AnalysisFeature>();
		Properties properties = new Properties();

		CompilerFeature version = CompilerFeature
				.valueOf(project.getPersistentProperty(new QualifiedName("rebeca", "languageVersion")));
		compilerFeatures.add(version);
		String type = project.getPersistentProperty(new QualifiedName("rebeca", "projectType"));
		if (type.equals("TimedRebeca")) {
			compilerFeatures.add(CompilerFeature.TIMED_REBECA);
		} else if (type.equals("ProbabilisitcTimedRebeca")) {
			compilerFeatures.add(CompilerFeature.TIMED_REBECA);
			compilerFeatures.add(CompilerFeature.PROBABILISTIC_REBECA);
		}
		if (Boolean.parseBoolean(project.getPersistentProperty(new QualifiedName("rebeca", "runInSafeMode"))))
			analysisFeatures.add(AnalysisFeature.SAFE_MODE);
		if (Boolean.parseBoolean(project.getPersistentProperty(new QualifiedName("rebeca", "exportStateSpace")))) {
			analysisFeatures.add(AnalysisFeature.EXPORT_STATE_SPACE);
			properties.setProperty("statespace",
					outputFolder.getAbsolutePath() + File.separatorChar + rebecaFile.getName() + ".statespace");
		}

		GenerateFiles.getInstance().generateFiles(rebecaFile.getRawLocation().toFile(), propertyFile, outputFolder,
				compilerFeatures, analysisFeatures, properties);
		
		propertyModel = GenerateFiles.getInstance().getPropertyModel();
		IFile propertyFileResource = project.getWorkspace().getRoot().getFileForLocation(new Path(propertyFile.getAbsolutePath()));
		for (Exception e : GenerateFiles.getInstance().getExceptionContainer().getWarnings()) {
			if (e instanceof CodeCompilationException) {
				CodeCompilationException cce = (CodeCompilationException) e;
				if (cce instanceof PropertyCodeCompilationException)
					createMarker(propertyFileResource, cce, true);
				else
					createMarker(rebecaFile, cce, true);
			} else {
				e.printStackTrace();
			}
		}
		List<Exception> exceptions = new ArrayList<Exception>();
		exceptions.addAll(GenerateFiles.getInstance().getExceptionContainer().getExceptions());
		Collections.sort(exceptions, new Comparator<Exception>() {
			public int compare(Exception o1, Exception o2) {
				if (!(o1 instanceof CodeCompilationException))
					return 1;
				if (!(o2 instanceof CodeCompilationException))
					return -1;
				CodeCompilationException cce1 = (CodeCompilationException) o1, cce2 = (CodeCompilationException) o2;
				return cce1.getLine() < cce2.getLine() ? -1
						: cce1.getLine() > cce2.getLine() ? 1
								: cce1.getColumn() < cce2.getColumn() ? -1
										: cce1.getColumn() > cce2.getColumn() ? 1 : 0;
			}
		});
		for (Exception e : exceptions) {
			result = false;
			if (e instanceof CodeCompilationException) {
				CodeCompilationException cce = (CodeCompilationException) e;
				if (cce instanceof PropertyCodeCompilationException)
					createMarker(propertyFileResource, cce, false);
				else
					createMarker(rebecaFile, cce, false);
			} else {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public Object execute(ExecutionEvent event) throws org.eclipse.core.commands.ExecutionException {
		return execute(event, true);
	}

}
