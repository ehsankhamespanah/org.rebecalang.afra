package org.rebecalang.afra.ideplugin.wizard.newproject;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.rebecalang.compiler.utils.CompilerFeature;

public class NewProjectWizardPageTwo extends WizardPage {
	private Text text1;
	private Composite container;

	private static final String TYPE_1 = "Core Rebeca";
	private static final String TYPE_2 = "Timed Rebeca";
	private static final String TYPE_3 = "Probabilistic Rebeca";
	private static final String TYPE_4 = "Probabilistic Timed Rebeca";

	private static final String VERSION_1 = "2.0";
	private static final String VERSION_2 = "2.1\t\t\t\t\t";
	private static final String VERSION_3 = "2.2                    ";

	CompilerFeature languageVersion;
	String projectExtention;
	protected boolean runInSafeMode;
	protected boolean exportStateSpace;
	
	public boolean isRunInSafeMode() {
		return runInSafeMode;
	}
	
	public boolean isExportStateSpace() {
		return exportStateSpace;
	}
	
	public NewProjectWizardPageTwo() {
		super("Second Page");
		setTitle("New Project Type");
		setDescription("Set Your New Project Type And Version");
		setControl(text1);
		runInSafeMode = true;
		languageVersion = CompilerFeature.CORE_2_1;
		projectExtention = "CoreRebeca";
	}

	@Override
	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		/****************** Group 1 ************************/
		Group type = new Group(container, SWT.SHADOW_IN);
		type.setText("Project Extension Type");
		type.setLayout(new GridLayout(1, false));
		type.setLayoutData(new GridData(SWT.VERTICAL));

		Button button;
		button = new Button(type, SWT.RADIO);
		button.setText(TYPE_1);
		button.setSelection(true);
		button.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				projectExtention = "CoreRebeca";
			}
		});

		button = new Button(type, SWT.RADIO);
		button.setText(TYPE_2);
		button.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				projectExtention = "TimedRebeca";
			}
		});

		button = new Button(type, SWT.RADIO);
		button.setText(TYPE_3);
		button.setEnabled(false);
		button.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				projectExtention = "ProbabilisticRebeca";
			}
		});

		button = new Button(type, SWT.RADIO);
		button.setText(TYPE_4);
		button.setEnabled(false);
		button.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				projectExtention = "ProbabilisticTimedRebeca";
			}
		});

		/****************** Group 2 ************************/
		Group version = new Group(container, SWT.SHADOW_ETCHED_IN);
		version.setText("Language Version");
		version.setLayout(new GridLayout(1, false));
		version.setLayoutData(new GridData(SWT.VERTICAL));

		button = new Button(version, SWT.RADIO);
		button.setText(VERSION_1);
		button.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				languageVersion = CompilerFeature.CORE_2_0;
			}
		});

		button = new Button(version, SWT.RADIO);
		button.setText(VERSION_2);
		button.setSelection(true);
		button.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				languageVersion = CompilerFeature.CORE_2_1;
			}
		});

		button = new Button(version, SWT.RADIO);
		button.setText(VERSION_3);
		button.setEnabled(false);
		button.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				languageVersion = CompilerFeature.CORE_2_2;
			}
		});


		Group runtimeconfig = new Group(container, SWT.SHADOW_ETCHED_IN);
		runtimeconfig.setText("Runtime Configuration");
		runtimeconfig.setLayout(new GridLayout(1, false));
		runtimeconfig.setLayoutData(new GridData(SWT.VERTICAL));
		
		button = new Button(runtimeconfig, SWT.CHECK);
		button.setText("Run in safe mode");
		button.setSelection(true);
		button.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				runInSafeMode = ((Button)e.getSource()).getSelection();
			}
		});
		
		button = new Button(runtimeconfig, SWT.CHECK);
		button.setText("Export state space");
		button.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				exportStateSpace = ((Button)e.getSource()).getSelection();
			}
		});
		
		// required to avoid an error in the system
		setControl(container);
		setPageComplete(true);
	}

	public String getType() {
		return projectExtention;
	}

	public CompilerFeature getVersion() {
		return languageVersion;
	}
	
}
