package org.rebecalang.ide.afra.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
//import org.rebecalang.rmc.RMC;

public class Compile extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws org.eclipse.core.commands.ExecutionException {
		//IEditorPart activeFile = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
	    IResource activeFile = (IResource)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
	    System.out.println(activeFile.getRawLocation().toString());
	    
	    if (!activeFile.getName().contains(".rebeca")){
	    	MessageDialog.openInformation(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Error", "No Rebeca file is active in editor.");
	    }else{
	    	String propertyFile = activeFile.getRawLocation().toString().substring(0, activeFile.getRawLocation().toString().indexOf(activeFile.getName())) + activeFile.getName().substring(0, activeFile.getName().indexOf(".rebeca")) + ".property";
	    	String outputFolder = activeFile.getFullPath().toString().substring(1);
	    	outputFolder = Platform.getLocation().toOSString() + "/" + outputFolder.substring(0, outputFolder.indexOf('/')) + "/out/" + activeFile.getName().substring(0, activeFile.getName().indexOf(".rebeca")) + "/" ;
	    	System.out.println("rebeca file: " + activeFile.getRawLocation().toString());
	    	System.out.println("property file: " + propertyFile);
	    	System.out.println("output foler: " + outputFolder);
	    	//MessageDialog.openInformation(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Error", propertyFile);
	    	String result = Compile.compileSec(activeFile, propertyFile, outputFolder);
	    	if(result.length() > 0){
	           	MessageDialog.openInformation(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Error", result);
	        }else{
	        	MessageDialog.openInformation(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Info", "Done :)");
	        	System.out.println("Done :)");
	        }
	    }
	    return null;
	}
	
	private static String[] generateCommand(String outputFolder , String postFix){
		File loc = new File(outputFolder);
		String files[] = loc.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".cpp");
			}
		});
		String command [] = new String[files.length + 4];
		
		command[0] = "g++";
		for (int i = 0; i < files.length ; i++)
			command[i+1] = files[i];
		command[files.length + 1] = "-w";
		command[files.length + 2] = "-o";
		command[files.length + 3] = "a" + postFix;
		return command;
	}
	
	public static String compileSec(IResource activeFile , String propertyFile , String outputFolder){
		String[] parameters = new String[] {
			"-s" , activeFile.getRawLocation().toString() , "-p" , propertyFile , "-v" , "2.1" , "-e" , "TimedRebeca" , "-o" , outputFolder 
		};
		//RMC.main(parameters);
		String error = "";
		String output = "";
		try {
			String postFix = (System.getProperty("os.name").contains("Windows")) ? ".exe" : ".out" ;
			ProcessBuilder pr = new ProcessBuilder();
			pr.directory(new File(outputFolder));
			
			String command [] = generateCommand(outputFolder, postFix);
			pr.command(command);
			
			Process p = pr.start();
			p.waitFor();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	        String line = reader.readLine();
	        while (line != null) {
	          	error += line + "\n";
	            System.out.println(line);
	            line = reader.readLine();
	        }
	        BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        line = errorReader.readLine();
	        while (line != null) {
	          	output += line + "\n";
	            System.out.println(line);
	            line = errorReader.readLine();
	        }
	        
	    }
	    catch(IOException e1) {}
	    catch(InterruptedException e2) {}
		return output + error;
	}
	
}
