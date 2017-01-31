package org.rebecalang.ide.afra.handlers;

import java.io.BufferedReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import org.eclipse.core.resources.IProject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;


import org.rebecalang.ide.afra.views.counterexample.rebeca.CounterExampleR;
import org.rebecalang.ide.afra.views.counterexample.systemc.CounterExample;
import org.rebecalang.ide.afra.modelcheckreport.ModelCheckingReport;
import org.rebecalang.ide.afra.general.RebecaUIPlugin;
import org.rebecalang.ide.afra.handlers.Compile;
import org.rebecalang.ide.afra.views.counterexample.rebeca.CounterExampleView;

public class Modelcheck extends AbstractHandler{
	private static IProject project;
	@Override
	public Object execute(ExecutionEvent event) throws org.eclipse.core.commands.ExecutionException {
		
		project = ((IFileEditorInput)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput()).getFile().getProject();
		IResource activeFile = (IResource)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
	    
	    if (!activeFile.getName().contains(".rebeca")){
	    	MessageDialog.openInformation(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Error", "No Rebeca file is active in editor.");
	    }else{
	    	String propertyFile = activeFile.getRawLocation().toString().substring(0, activeFile.getRawLocation().toString().indexOf(activeFile.getName())) + activeFile.getName().substring(0, activeFile.getName().indexOf(".rebeca")) + ".property";
	    	String outputFolder = activeFile.getFullPath().toString().substring(1);
	    	outputFolder = Platform.getLocation().toOSString() + "/" + outputFolder.substring(0, outputFolder.indexOf('/')) + "/out/" + activeFile.getName().substring(0, activeFile.getName().indexOf(".rebeca")) + "/" ;
	    	
	    	String postFix = (System.getProperty("os.name").contains("Windows")) ? ".exe" : ".out" ;
	    	
	    	Path execFile = Paths.get(outputFolder + "a" + postFix);
	    	Path rebecaFile = Paths.get(activeFile.getRawLocation().toString());
	    	try {
				BasicFileAttributes execAttr = Files.readAttributes(execFile, BasicFileAttributes.class);
				BasicFileAttributes rebecaAttr = Files.readAttributes(rebecaFile, BasicFileAttributes.class);
				if (rebecaAttr.lastModifiedTime().toMillis() > execAttr.lastModifiedTime().toMillis()){
					this.doCompile(event , activeFile, propertyFile, outputFolder);
				}
			} catch (IOException e) {
				this.doCompile(event , activeFile, propertyFile, outputFolder);
			}
	    	String xmlOutput = Modelcheck.exec(outputFolder , postFix);
			if (xmlOutput.length() > 0){
				//not gonna happen
			}else{
				this.showResult(outputFolder + "modelCheck.xml");
			}
	    }
	    return null;
	}
	
	
	public static String exec(String outputFolder , String postFix){
		String result = "";
		try {
			String runPath =  outputFolder + "a" + postFix + " -o " + outputFolder + "modelCheck.xml";
			System.out.println(runPath);
	        Process p = Runtime.getRuntime().exec(runPath);
	        p.waitFor();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String line=reader.readLine();
	        while (line != null) {
	          	result += line + "\n";
	            System.out.println(line);
	            line = reader.readLine();
	        }
	            
	    }
	    catch(IOException e1) {}
	    catch(InterruptedException e2) {}
		System.out.println(result);
		return result;
	}
	
	
	private void doCompile(ExecutionEvent event , IResource activeFile , String propertyFile , String outputFolder){
		MessageDialog.openInformation(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Info", "Compile process begins.\n Please be patient, It could take a while.\n When the compile process finishes, we will notify you.");
		String result = Compile.compileSec(activeFile, propertyFile, outputFolder);
    	if(result.length() > 0){
           	MessageDialog.openInformation(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Error", result);
        }else{
        	MessageDialog.openInformation(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Info", "Compile was successfull.");
        	System.out.println("Compile was successfull.");
        }
	}
	
	private void showResult(String xmlPathFile) {
		CounterExample counterExample = null;
		CounterExampleR counterExampleR = null;
		JAXBContext jaxbContext;
		try {
			File xmlResultFile = new File(xmlPathFile);
			jaxbContext = JAXBContext.newInstance(ModelCheckingReport.class.getPackage().getName());
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			ModelCheckingReport report = (ModelCheckingReport) unmarshaller
					.unmarshal(xmlResultFile);
			
			String prj_type = project.getPersistentProperty(new QualifiedName("rmc.modelcheck", "projectType"));
			
			if(prj_type==null || !prj_type.equals("SYSTEM_C"))
			{
				counterExampleR = new CounterExampleR("", report);
				counterExampleR.parse();
				CounterExampleView counterExampleView = RebecaUIPlugin.showCounterExampleView();
				counterExampleView.setCounterExample(null);
				counterExampleView.checkRefreshAll();
				counterExampleView.setCounterExample(counterExampleR);
				counterExampleView.checkRefreshAll();
				RebecaUIPlugin.activateView(counterExampleView);
			}

		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
