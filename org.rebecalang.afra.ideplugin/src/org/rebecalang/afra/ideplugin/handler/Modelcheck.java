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
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;
import org.rebecalang.afra.ideplugin.general.RebecaUIPlugin;
import org.rebecalang.afra.ideplugin.modelcheckreport.ModelCheckingReport;
import org.rebecalang.afra.ideplugin.view.counterexample.rebeca.CounterExampleR;
import org.rebecalang.afra.ideplugin.view.counterexample.rebeca.CounterExampleView;

public class Modelcheck extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws org.eclipse.core.commands.ExecutionException {

		IResource activeFile = (IResource) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor().getEditorInput().getAdapter(IFile.class);

		if (!activeFile.getName().contains(".rebeca")) {
			CompileHandler.showNoActiveRebecaFileErrorDialog(event);
		} else {
			String outputFolder = activeFile.getFullPath().toString().substring(1);
			outputFolder = Platform.getLocation().toOSString() + "/"
					+ outputFolder.substring(0, outputFolder.indexOf('/')) + "/out/"
					+ activeFile.getName().substring(0, activeFile.getName().indexOf(".rebeca")) + "/";

			String postFix = (System.getProperty("os.name").contains("Windows")) ? ".exe" : ".out";

			Path execFile = Paths.get(outputFolder + "a" + postFix);
			Path rebecaFile = Paths.get(activeFile.getRawLocation().toString());
			Path propertyFile = Paths.get(activeFile.getRawLocation().toString().substring(0,
					activeFile.getRawLocation().toString().indexOf(activeFile.getName()))
					+ activeFile.getName().substring(0, activeFile.getName().indexOf(".rebeca")) + ".property");
			try {
				BasicFileAttributes execAttr = Files.readAttributes(execFile, BasicFileAttributes.class);
				BasicFileAttributes rebecaAttr = Files.readAttributes(rebecaFile, BasicFileAttributes.class);
				BasicFileAttributes propAttr = Files.readAttributes(propertyFile, BasicFileAttributes.class);
				if (rebecaAttr.lastModifiedTime().toMillis() > execAttr.lastModifiedTime().toMillis() || 
						propAttr.lastModifiedTime().toMillis() > execAttr.lastModifiedTime().toMillis() ) {
					delagateToCompileCommand(event);
				}
			} catch (IOException e) {
				delagateToCompileCommand(event);
			}
			String consoleError = Modelcheck.exec(outputFolder, postFix);
			assert(consoleError.length() > 0);
			this.showResult(outputFolder + "modelCheck.xml");
		}
		return null;
	}

	private void delagateToCompileCommand(ExecutionEvent event) throws ExecutionException {
		CompileHandler compile = new CompileHandler();
		try {
			compile.execute(event, false);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public static String exec(String outputFolder, String postFix) {
		String result = "";
		try {
			String runPath = outputFolder + "a" + postFix + " -o " + outputFolder + "modelCheck.xml";
			Process p = Runtime.getRuntime().exec(runPath);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				result += line + "\n";
				System.out.println(line);
				line = reader.readLine();
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		return result;
	}

	private void showResult(String xmlPathFile) {
		CounterExampleR counterExampleR = null;
		JAXBContext jaxbContext;
		try {
			File xmlResultFile = new File(xmlPathFile);
			jaxbContext = JAXBContext.newInstance(ModelCheckingReport.class.getPackage().getName());
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			ModelCheckingReport report = (ModelCheckingReport) unmarshaller.unmarshal(xmlResultFile);

			counterExampleR = new CounterExampleR("", report);
			counterExampleR.parse();
			CounterExampleView counterExampleView = RebecaUIPlugin.showCounterExampleView();
			counterExampleView.setCounterExample(null);
			counterExampleView.checkRefreshAll();
			counterExampleView.setCounterExample(counterExampleR);
			counterExampleView.checkRefreshAll();
			RebecaUIPlugin.activateView(counterExampleView);

		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
}
