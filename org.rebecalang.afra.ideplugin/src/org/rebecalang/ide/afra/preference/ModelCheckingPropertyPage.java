package org.rebecalang.ide.afra.preference;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;


public class ModelCheckingPropertyPage extends PropertyPage {

	private static final String MAX_DEPTH_TITLE = "Max Depth: ";

	public static final String DEFAULT_MAX_DEPTH = "100000";

	private static final String HASHMAP_SIZE_TITLE = "Hash Map Size: ";

	public static final String DEFAULT_HASHMAP_SIZE = "20";

	public static final String DEFAULT_OUT_DIR = "";

	public static final String DEFAULT_SIMULATOR_PORT = "2000";

	public static final int TEXT_GROUP_D = 30;
	public static final int TEXT_GROUP_L = 30;

	private static final int TEXT_FIELD_WIDTH = 20;

	public String CompileAlgorithm = "DFS";
	public String ReductionMethod = "";
	public String SystemCType = "SYSFIER";

	private Text maxDepthText;
	private Text hashMapSizeText;
	private Text outDir;
	private Text sPort;

	private Group general;
	private Group reduction;
	private Group compileAlgorithm;
	private Group visualization;
	private Group systemc;

	private Button dfs;
	private Button bfs;
	private Button po;
	private Button interRS;
	private Button intraRS;
	private Button graphViz;
	private Button cadp;
	private Button ceGraphViz;
	private Button sysfier;
	private Button shalifier;

	private Button browse;
	private Button reductionEnable;
	private Button visualizationEnable;

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;

		// *********** General Section ***********//
		general = new Group(container, SWT.NONE);
		general.setText("General");
		general.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout glayout = new GridLayout();
		general.setLayout(glayout);
		glayout.numColumns = 2;

		Label labelHashMapSize = new Label(general, SWT.NONE);
		labelHashMapSize.setText(HASHMAP_SIZE_TITLE);
		labelHashMapSize.pack();
		hashMapSizeText = new Text(general, SWT.BORDER | SWT.SINGLE);
		GridData gd2 = new GridData();
		gd2.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		hashMapSizeText.setLayoutData(gd2);
		hashMapSizeText.pack();

		// *********** Compile Algorithm Section ***********//
		compileAlgorithm = new Group(container, SWT.SHADOW_IN);
		compileAlgorithm.setText("State Space Exploration Algorithm");
		compileAlgorithm.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout clayout = new GridLayout();
		compileAlgorithm.setLayout(clayout);
		clayout.numColumns = 3;
		clayout.marginLeft = 10;

		dfs = new Button(compileAlgorithm, SWT.RADIO);
		dfs.setText("DFS");
		dfs.setSelection(true);
		dfs.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				compileAlgorithmChanged();
			}
		});
		dfs.pack();

		Label label = new Label(compileAlgorithm, SWT.NONE);
		label.setText(MAX_DEPTH_TITLE);
		label.pack();
		maxDepthText = new Text(compileAlgorithm, SWT.BORDER | SWT.SINGLE);
		GridData gd1 = new GridData();
		gd1.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		maxDepthText.setLayoutData(gd1);
		maxDepthText.pack();

		bfs = new Button(compileAlgorithm, SWT.RADIO);
		bfs.setText("BFS");
		bfs.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				compileAlgorithmChanged();
			}
		});
		bfs.pack();

		// *********** Reduction Section ***********//
		reduction = new Group(container, SWT.SHADOW_IN);
		reduction.setText("Reduction");
		reduction.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		reductionEnable = new Button(reduction, SWT.CHECK);
		reductionEnable.setText("Add Reduction");
		reductionEnable.setSelection(false);
		reductionEnable.setLocation(10, 25);
		reductionEnable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				reductionEnable();
			}
		});
		reductionEnable.pack();

		po = new Button(reduction, SWT.RADIO);
		po.setText("Partial Order");
		po.setSelection(true);
		po.setEnabled(false);
		po.setLocation(TEXT_GROUP_L, TEXT_GROUP_D + 20);
		po.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				reductionMethodChanged();
			}
		});
		po.pack();

		interRS = new Button(reduction, SWT.RADIO);
		interRS.setText("Inter Rebec Symmetry");
		interRS.setEnabled(false);
		interRS.setLocation(TEXT_GROUP_L, TEXT_GROUP_D + 40);
		interRS.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				reductionMethodChanged();
			}
		});
		interRS.pack();

		intraRS = new Button(reduction, SWT.RADIO);
		intraRS.setText("Intra Rebec Symmetry");
		intraRS.setEnabled(false);
		intraRS.setLocation(TEXT_GROUP_L, TEXT_GROUP_D + 60);
		intraRS.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				reductionMethodChanged();
			}
		});
		intraRS.pack();

		// *********** Visualization Section ***********//
		visualization = new Group(container, SWT.SHADOW_IN);
		visualization.setText("State Space Visualization");
		visualization.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout vlayout = new GridLayout();
		visualization.setLayout(vlayout);
		vlayout.numColumns = 3;

		visualizationEnable = new Button(visualization, SWT.CHECK);
		visualizationEnable.setText("Add Visualization");
		visualizationEnable.setSelection(false);
		visualizationEnable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				visualizationEnable();
			}
		});
		visualizationEnable.pack();

		Label l = new Label(visualization, SWT.NONE);
		l.pack();
		Label l2 = new Label(visualization, SWT.NONE);
		l2.pack();
		Label l3 = new Label(visualization, SWT.NONE);
		l3.pack();

		graphViz = new Button(visualization, SWT.CHECK);
		graphViz.setText("GraphViz");
//		graphViz.setSelection(false);
		graphViz.setEnabled(false);
		graphViz.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				visualizeMethodChanged();
			}
		});
		graphViz.pack();

		Label l4 = new Label(visualization, SWT.NONE);
		l4.pack();
		Label l5 = new Label(visualization, SWT.NONE);
		l5.pack();

		cadp = new Button(visualization, SWT.CHECK);
		cadp.setText("CADP");
//		cadp.setSelection(true);
		cadp.setEnabled(false);
		cadp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				visualizeMethodChanged();
			}
		});
		cadp.pack();

		Label l6 = new Label(visualization, SWT.NONE);
		l6.pack();
		Label l7 = new Label(visualization, SWT.NONE);
		l7.pack();

		ceGraphViz = new Button(visualization, SWT.CHECK);
		ceGraphViz.setText("CounterExample GraphViz");
		ceGraphViz.setEnabled(false);
		ceGraphViz.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				visualizeMethodChanged();
			}
		});
		ceGraphViz.pack();

		Label l8 = new Label(visualization, SWT.NONE);
		l8.pack();

		Label outputLabel = new Label(visualization, SWT.NONE);
		outputLabel.setText("Output Location: ");
		outputLabel.pack();

		outDir = new Text(visualization, SWT.BORDER | SWT.SINGLE);
		IProject project = getProject();
		outDir.setText(project.getLocation().toOSString());
		outDir.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		outDir.setEnabled(false);
		outDir.pack();

		browse = new Button(visualization, SWT.PUSH);
		browse.setText("Browse...");
		browse.setEnabled(false);
		browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		browse.pack();

		// *********** Dynamic Sections ***********//
		try {
			String val = getProject().getPersistentProperty(
					new QualifiedName("rmc.modelcheck", "projectType"));
			if (val != null && val.equals("SYSTEM_C")) {
				// *********** SystemC Section ***********//
				systemc = new Group(container, SWT.SHADOW_IN);
				systemc.setText("SystemC Settings");
				systemc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				shalifier = new Button(systemc, SWT.RADIO);
				shalifier.setText("Shalifier");
				shalifier.setLocation(20, TEXT_GROUP_D + 20);
				shalifier.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						systemCTypeChanged();
					}
				});
				shalifier.pack();

				sysfier = new Button(systemc, SWT.RADIO);
				sysfier.setText("Sysfier");
				sysfier.setSelection(true);
				sysfier.setLocation(20, TEXT_GROUP_D);
				sysfier.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						systemCTypeChanged();
					}
				});
				sysfier.pack();
			} else {
				Label simulatorPort = new Label(general, SWT.NONE);
				simulatorPort.setText("Simulator Port");
				simulatorPort.pack();
				sPort = new Text(general, SWT.BORDER | SWT.SINGLE);
				GridData sgd = new GridData();
				sgd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
				sPort.setLayoutData(sgd);
				sPort.pack();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		// Added by ehsan for the first release.
		// begin
		bfs.setEnabled(false);
		reductionEnable.setEnabled(false);
		// end

		initContents();
		return container;
	}

	protected void visualizeMethodChanged() {
//		Visualization = "";
//		Extension = ".dot";
//		if (graphViz.getSelection())
//			Visualization = "GRAPH_VIZ";
//		else if (cadp.getSelection()) {
//			Extension = ".svc";
//			Visualization = "CADP";
//		} else
//			Visualization = "COUNTEREXAMPLE_GRAPH_VIZ";
	}

	protected void reductionMethodChanged() {
		ReductionMethod = "";
		if (po.getSelection())
			ReductionMethod = "PARTIAL_ORDER";
		else if (interRS.getSelection())
			ReductionMethod = "INTER_REBEC_SYMMETRY";
		else
			ReductionMethod = "INTRA_REBEC_SYMMETRY";
	}

	protected void compileAlgorithmChanged() {
		CompileAlgorithm = "";
		if (dfs.getSelection()) {
			CompileAlgorithm = "DFS";
			maxDepthText.setEnabled(true);
		} else {
			CompileAlgorithm = "BFS";
			maxDepthText.setEnabled(false);
		}
	}

	protected void visualizationEnable() {
		if (visualizationEnable.getSelection()) {
			graphViz.setEnabled(true);
			cadp.setEnabled(true);
//			ceGraphViz.setEnabled(true);
			outDir.setEnabled(true);
			browse.setEnabled(true);
			visualizeMethodChanged();
		} else {
			graphViz.setEnabled(false);
			graphViz.setSelection(false);
			cadp.setEnabled(false);
			cadp.setSelection(false);
//			ceGraphViz.setEnabled(false);
			outDir.setEnabled(false);
			browse.setEnabled(false);
		}
	}

	protected void reductionEnable() {
		if (reductionEnable.getSelection()) {
			po.setEnabled(true);
			interRS.setEnabled(true);
			intraRS.setEnabled(true);
			reductionMethodChanged();
		} else {
			ReductionMethod = "";
			po.setEnabled(false);
			interRS.setEnabled(false);
			intraRS.setEnabled(false);
		}
	}

	protected void systemCTypeChanged() {
		if (sysfier.getSelection())
			SystemCType = "SYSFIER";
		else
			SystemCType = "SHALIFIER";
	}

	private void handleBrowse() {
		IProject project = getProject();
		DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
//		String[] extensions = new String[1];
//		extensions[0] = "*" + Extension;
//		dialog.setFilterExtensions(extensions);

		dialog.setFilterPath(project.getLocation().toOSString());
		String path = dialog.open();
		if (path == null) {
			path = project.getLocation().toOSString();
		}
//		if (!path.endsWith(Extension))
//			path += Extension;
		outDir.setText(path);

	}

	private String checkFilePathName() {
		try {
			String filePathName = outDir.getText();
//			String ext = filePathName.substring(filePathName.lastIndexOf("."));
//			if (!ext.equals(Extension))
//				return "Invalid state space visualization output file extension!";
//
//			String fileName = filePathName.substring(
//					filePathName.lastIndexOf(File.separator) + 1,
//					filePathName.lastIndexOf("."));
//			if (!fileName.matches("[a-zA-Z0-9_]+"))
//				return "State space visualization output file name should only contains letters, numbers and underline charachter!";
//
//			if (!filePathName.endsWith(Extension)) {
//				filePathName += Extension;
//				outDir.setText(filePathName);
//			}
//			File testFileName = new File(filePathName);
//			if (testFileName.exists())
//				return "";
//			if (!testFileName.createNewFile())
//				return "Invalid state space visualization output file path!";
//			testFileName.delete();
			File target = new File (filePathName);
			if (!target.exists())
				return "The selected location does not exist!";
			if (!target.isDirectory())
				return "The selected location should be directory!";
		} catch (Exception e) {
			return "Invalid state space visualization output file path!";
		}
		return "";
	}

	private void showInvalidNumber() {
		MessageDialog.openError(getShell(), "Error", "Invalid number");
	}

	private void showInvalidFilePathName(String error) {
		MessageDialog.openError(getShell(), "Error", error);
	}

	protected void performDefaults() {
		IProject project = getProject();

		// Populate the owner text field with the default value
		maxDepthText.setText(DEFAULT_MAX_DEPTH);
		maxDepthText.setEnabled(true);
		hashMapSizeText.setText(DEFAULT_HASHMAP_SIZE);
		dfs.setSelection(true);
		bfs.setSelection(false);
		reductionEnable.setSelection(false);
		po.setEnabled(false);
		interRS.setEnabled(false);
		intraRS.setEnabled(false);
		visualizationEnable.setSelection(false);
		graphViz.setEnabled(false);
		cadp.setEnabled(false);
		ceGraphViz.setEnabled(false);
		outDir.setEnabled(false);
		outDir.setText(project.getLocation().toOSString());
		browse.setEnabled(false);

		// Added by ehsan for the first release.
		// begin
		bfs.setEnabled(false);
		reductionEnable.setEnabled(false);
		// end

//		try {
//			project.setPersistentProperty(new QualifiedName("rmc.modelcheck",
//					"maxdepth"), DEFAULT_MAX_DEPTH);
//			project.setPersistentProperty(new QualifiedName("rmc.modelcheck",
//					"hashmapSize"), DEFAULT_HASHMAP_SIZE);
//			project.setPersistentProperty(new QualifiedName("rmc.modelcheck",
//					"compileAlgorithm"), "DFS");
//			project.setPersistentProperty(new QualifiedName("rmc.modelcheck",
//					"reductionMethod"), "");
//			project.setPersistentProperty(new QualifiedName("rmc.modelcheck",
//					"visualization"), "");
//			String val = getProject().getPersistentProperty(
//					new QualifiedName("rmc.modelcheck", "projectType"));
//			if (val != null && val.equals("SYSTEM_C")) {
//				sysfier.setSelection(true);
//				shalifier.setSelection(false);
//				project.setPersistentProperty(new QualifiedName(
//						"rmc.modelcheck", "systemCType"), "SYSFIER");
//			} else {
//				sPort.setText(DEFAULT_SIMULATOR_PORT);
//				project.setPersistentProperty(new QualifiedName("rmc.simulate",
//						"port"), DEFAULT_SIMULATOR_PORT);
//			}
//		} catch (CoreException e) {
//		}
	}

	private IProject getProject() {
//		IAdaptable element = getElement();
//		if (element != null && element instanceof IProject)
//			return (IProject) element;
//		else
//			return null;
		return (IProject) getElement().getAdapter(IProject.class);

	}

	private void initContents() {
		IProject project = getProject();
		try {
			String val = project.getPersistentProperty(new QualifiedName(
					"rmc.modelcheck", "compileAlgorithm"));
			if (val != null) {
				CompileAlgorithm = val;
				if (CompileAlgorithm.equals("BFS")) {
					bfs.setSelection(true);
					dfs.setSelection(false);
					maxDepthText.setEnabled(false);
				}
			}

			val = project.getPersistentProperty(new QualifiedName(
					"rmc.modelcheck", "maxdepth"));
			if (val != null) {
				maxDepthText.setText(val);
			} else {
				maxDepthText.setText(DEFAULT_MAX_DEPTH);
			}

			val = project.getPersistentProperty(new QualifiedName(
					"rmc.modelcheck", "hashmapSize"));
			if (val != null) {
				hashMapSizeText.setText(val);
			} else {
				hashMapSizeText.setText(DEFAULT_HASHMAP_SIZE);
			}

			val = project.getPersistentProperty(new QualifiedName(
					"rmc.modelcheck", "reductionMethod"));
			if (val == null || val.equals("")) {
				ReductionMethod = "";
				reductionEnable.setSelection(false);
				reductionEnable();
			} else {
				reductionEnable.setSelection(true);
				reductionEnable();
				po.setSelection(false);
				interRS.setSelection(false);
				intraRS.setSelection(false);
				ReductionMethod = val;
				if (ReductionMethod.equals("PARTIAL_ORDER"))
					po.setSelection(true);
				else if (ReductionMethod.equals("INTER_REBEC_SYMMETRY"))
					interRS.setSelection(true);
				else if (ReductionMethod.equals("INTRA_REBEC_SYMMETRY"))
					intraRS.setSelection(true);
			}

			val = project.getPersistentProperty(new QualifiedName(
					"rmc.modelcheck.visualization", "visualization"));
			if (val == null || val.equals("false")) {
				visualizationEnable.setSelection(false);
				visualizationEnable();
			} else {
				visualizationEnable.setSelection(true);
				visualizationEnable();
				graphViz.setSelection(false);
				cadp.setSelection(false);
				ceGraphViz.setSelection(false);
				val = project.getPersistentProperty(new QualifiedName(
						"rmc.modelcheck.visualization", "GraphViz"));
				if (val != null && val.equals("true"))
					graphViz.setSelection(true);
				val = project.getPersistentProperty(new QualifiedName(
						"rmc.modelcheck.visualization", "CADP"));
				if (val != null && val.equals("true"))
					cadp.setSelection(true);
				val = project.getPersistentProperty(new QualifiedName(
						"rmc.modelcheck.visualization", "CEGraphViz"));
				if (val != null && val.equals("true"))
					ceGraphViz.setSelection(true);
				val = project.getPersistentProperty(new QualifiedName(
						"rmc.modelcheck.visualization", "visualizationTarget"));
				if (val != null && !val.equals("")) {
					outDir.setText(val);
				} else {
					outDir.setText(project.getLocation().toOSString());
				}
				
			}

			val = getProject().getPersistentProperty(
					new QualifiedName("rmc.modelcheck", "projectType"));
			if (val != null && val.equals("SYSTEM_C")) {
				val = project.getPersistentProperty(new QualifiedName(
						"rmc.modelcheck", "systemCType"));
				if (val != null && val.equals("SHALIFIER")) {
					SystemCType = val;
					shalifier.setSelection(true);
					sysfier.setSelection(false);
				} else {
					SystemCType = "SYSFIER";
					sysfier.setSelection(true);
					shalifier.setSelection(false);
				}
			} else {
				val = project.getPersistentProperty(new QualifiedName(
						"rmc.simulate", "port"));
				if (val != null) {
					sPort.setText(val);
				} else {
					sPort.setText(DEFAULT_SIMULATOR_PORT);
				}
			}
		} catch (CoreException e) {
			performDefaults();
		}
	}

/*	public boolean performOk() {
		IProject project = getProject();
		try {
			if (CompileAlgorithm.equals("DFS")) {
				String mdstr = maxDepthText.getText();
				Integer.parseInt(mdstr);
				project.setPersistentProperty(new QualifiedName(
						"rmc.modelcheck", "maxdepth"), mdstr);
			} else
				project.setPersistentProperty(new QualifiedName(
						"rmc.modelcheck", "maxdepth"), DEFAULT_MAX_DEPTH);

			String hsstr = hashMapSizeText.getText();
			Integer.parseInt(hsstr);
			project.setPersistentProperty(new QualifiedName("rmc.modelcheck",
					"hashmapSize"), hsstr);

			project.setPersistentProperty(new QualifiedName("rmc.modelcheck",
					"compileAlgorithm"), CompileAlgorithm);
			project.setPersistentProperty(new QualifiedName("rmc.modelcheck",
					"reductionMethod"), ReductionMethod);

			project.setPersistentProperty(new QualifiedName("rmc.modelcheck.visualization",
					"visualization"), String.valueOf(visualizationEnable.getSelection()));
			project.setPersistentProperty(new QualifiedName("rmc.modelcheck.visualization",
					"GraphViz"), String.valueOf(graphViz.getSelection()));
//			System.out.println("Ehsan:" + cadp.getSelection());
			project.setPersistentProperty(new QualifiedName("rmc.modelcheck.visualization",
					"CADP"), String.valueOf(cadp.getSelection()));
			project.setPersistentProperty(new QualifiedName("rmc.modelcheck.visualization",
					"CEGraphViz"), String.valueOf(ceGraphViz.getSelection()));
			project.setPersistentProperty(new QualifiedName("rmc.modelcheck.visualization",
					"visualizationTarget"), outDir.getText());
			
			if (cadp.getSelection() || graphViz.getSelection() || ceGraphViz.getSelection()) {
				String message = checkFilePathName();
				if (!message.equals(""))
					throw new Exception(message);
			}
//			if (!Visualization.equals("")) {
//				Exception e;
//				if (outDir.getText().equals("")) {
//					e = new Exception();
//					throw e;
//				}
//				String error = checkFilePathName();
//				if (!error.equals("")) {
//					e = new Exception(error);
//					throw e;
//				}
//
//			}

			String val = getProject().getPersistentProperty(
					new QualifiedName("rmc.modelcheck", "projectType"));
			if (val != null && val.equals("SYSTEM_C"))
				project.setPersistentProperty(new QualifiedName(
						"rmc.modelcheck", "systemCType"), SystemCType);
			else {
				String pstr = sPort.getText();
				int p = Integer.parseInt(pstr);
				project.setPersistentProperty(new QualifiedName("rmc.simulate",
						"port"), pstr);
				SimulatorSocket.setSimulatorPort(p);
			}

//			IPath pPath = project.getLocation();
//			File out = new File(pPath.toOSString(), "out");
//			if (!out.exists()) {
//				out.mkdirs();
//			}
//			System.out.println("out path: " + out.getPath());
//			setOptions(project, options);
//			ModelCheckingFilesCreationFacade.updateConfigHeaderFile(out,
//					options);
			SysRebModelCheckerUtil.clearHistory();
			return true;
		} catch (NumberFormatException e) {
			showInvalidNumber();
		} catch (CoreException e) {
			performDefaults();
		} catch (Exception e) {
			showInvalidFilePathName(e.getMessage());
		}
		SysRebModelCheckerUtil.clearHistory();
		return false;
	}

	@Override
	protected void performApply() {
		performOk();
//		IProject project = getProject();
//		try {
//			if (CompileAlgorithm.equals("DFS")) {
//				String mdstr = maxDepthText.getText();
//				Integer.parseInt(mdstr);
//				project.setPersistentProperty(new QualifiedName(
//						"rmc.modelcheck", "maxdepth"), mdstr);
//			} else
//				project.setPersistentProperty(new QualifiedName(
//						"rmc.modelcheck", "maxdepth"), DEFAULT_MAX_DEPTH);
//
//			String hsstr = hashMapSizeText.getText();
//			Integer.parseInt(hsstr);
//			project.setPersistentProperty(new QualifiedName("rmc.modelcheck",
//					"hashmapSize"), hsstr);
//
//			project.setPersistentProperty(new QualifiedName("rmc.modelcheck",
//					"compileAlgorithm"), CompileAlgorithm);
//			project.setPersistentProperty(new QualifiedName("rmc.modelcheck",
//					"reductionMethod"), ReductionMethod);
//
//			if (!Visualization.equals("")) {
//				Exception e;
//				if (outDir.getText().equals("")) {
//					e = new Exception();
//					throw e;
//				}
//				String error = checkFilePathName();
//				if (!error.equals("")) {
//					e = new Exception(error);
//					throw e;
//				}
//			}
//			project.setPersistentProperty(new QualifiedName("rmc.modelcheck",
//					"visualization"), Visualization);
//			project.setPersistentProperty(new QualifiedName("rmc.modelcheck",
//					"v_path"), outDir.getText());
//
//			String val = getProject().getPersistentProperty(
//					new QualifiedName("rmc.modelcheck", "projectType"));
//			if (val != null && val.equals("SYSTEM_C"))
//				project.setPersistentProperty(new QualifiedName(
//						"rmc.modelcheck", "systemCType"), SystemCType);
//			else {
//				String pstr = sPort.getText();
//				int p = Integer.parseInt(pstr);
//				project.setPersistentProperty(new QualifiedName("rmc.simulate",
//						"port"), pstr);
//				SimulatorSocket.setSimulatorPort(p);
//			}
//
//			IPath pPath = project.getLocation();
//			System.out.println("path: " + pPath);
//			File out = new File(pPath.toOSString(), "out");
//			if (!out.exists()) {
//				out.mkdirs();
//			}
//			System.out.println("out path: " + out.getPath());
//			setOptions(project, options);
//			ModelCheckingFilesCreationFacade.updateConfigHeaderFile(out,
//					options);
//
//		} catch (NumberFormatException e) {
//			showInvalidNumber();
//		} catch (CoreException e) {
//			performDefaults();
//		} catch (Exception e) {
//			showInvalidFilePathName(e.getMessage());
//		}
	}
	
	public static void setOptions(IProject project,
			Set<SupportingFeature> options) {
		try {
			String val;
			val = project.getPersistentProperty(new QualifiedName(
					"rmc.modelcheck", "projectCore"));
			options.add(SupportingFeature.valueOf(val));

			val = project.getPersistentProperty(new QualifiedName(
					"rmc.modelcheck", "projectType"));
			if (val != null && !val.equals("")) {
				options.add(SupportingFeature.valueOf(val));
				if (val.equals("SYSTEM_C"))
					options.add(SupportingFeature.valueOf(project
							.getPersistentProperty(new QualifiedName(
									"rmc.modelcheck", "systemCType"))));
			}

			val = project.getPersistentProperty(new QualifiedName(
					"rmc.modelcheck", "compileAlgorithm"));
			if (val != null && !val.equals(""))
				options.add(SupportingFeature.valueOf(val));
			else
				options.add(SupportingFeature.valueOf("DFS"));

			val = project.getPersistentProperty(new QualifiedName(
					"rmc.modelcheck", "reductionMethod"));
			if (val != null && !val.equals(""))
				options.add(SupportingFeature.valueOf(val));

			val = project.getPersistentProperty(new QualifiedName(
					"rmc.simulate", "action"));
			if (val != null && val.equals("SIMULATE"))
				options.add(SupportingFeature.valueOf(val));

			val = project.getPersistentProperty(new QualifiedName(
					"rmc.modelcheck.visualization", "CADP"));
			if (val != null && val.equals("true"))
				options.add(SupportingFeature.CADP);
			
			val = project.getPersistentProperty(new QualifiedName(
					"rmc.modelcheck.visualization", "GraphViz"));
			if (val != null && val.equals("true"))
				options.add(SupportingFeature.GRAPH_VIZ);
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
*/
}
