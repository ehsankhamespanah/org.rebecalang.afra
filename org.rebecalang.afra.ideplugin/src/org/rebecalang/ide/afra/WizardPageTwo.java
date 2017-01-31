package org.rebecalang.ide.afra;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class WizardPageTwo extends WizardPage {
  private Text text1;
  private Composite container;
  private Button ty_1;
  private Button ty_2;
  private Button ty_3;
  private Button ty_4;
  private Button ver_1;
  private Button ver_2;
  private Button ver_3;
  
  private static final String TYPE_1 = "Core Rebeca";
  private static final String TYPE_2 = "Timed Rebeca";
  private static final String TYPE_3 = "Probabilistic Rebeca";
  private static final String TYPE_4 = "Probabilistic Timed Rebeca";
  
  private static final String VERSION_1 = "2.0";
  private static final String VERSION_2 = "2.1";
  private static final String VERSION_3 = "2.2";
  
  
  public WizardPageTwo() {
    super("Second Page");
    setTitle("New Project Type");
    setDescription("Set Your New Project Type And Version");
    setControl(text1);
    
  }

  @Override
  public void createControl(Composite parent) {
	  
	container = new Composite(parent,SWT.NONE);
	container.setLayout(new GridLayout());
	  
	/******************Group 1************************/
	Group group_type = new Group(container, SWT.SHADOW_IN);
	group_type.setText("Choose New Project Type");
	group_type.setLayout(new GridLayout(1 , false));
	group_type.setLayoutData(new GridData(SWT.VERTICAL));
	  
	ty_1 = new Button(group_type, SWT.RADIO);
	ty_1.setText(TYPE_1);
	ty_1.setSelection(true);
	  
	ty_2 = new Button(group_type, SWT.RADIO);
	ty_2.setText(TYPE_2);
	  
	ty_3 = new Button(group_type, SWT.RADIO);
	ty_3.setText(TYPE_3);
	ty_3.setEnabled(false);
	
	ty_4 = new Button(group_type, SWT.RADIO);
	ty_4.setText(TYPE_4);
	ty_4.setEnabled(false);
	  
	/******************Group 2************************/
	Group group_version = new Group(container, SWT.SHADOW_ETCHED_IN);
	group_version.setText("Choose New Project Version");
	group_version.setLayout(new GridLayout(1 , false));
	group_version.setLayoutData(new GridData(SWT.VERTICAL));
	ver_1 = new Button(group_version, SWT.RADIO);
	
	ver_1.setText(VERSION_1);
	ver_1.setSelection(true);
	  
	ver_2 = new Button(group_version, SWT.RADIO);
	ver_2.setText(VERSION_2);
	
	ver_3 = new Button(group_version, SWT.RADIO);
	ver_3.setText(VERSION_3);
	ver_3.setEnabled(false);
	  
	// required to avoid an error in the system
	setControl(container);
	setPageComplete(true);
  }

  public String getType() {
	String retVal = "";
	retVal = (ty_1.getSelection()) ? TYPE_1 :
			 (ty_2.getSelection()) ? TYPE_2 :
			 (ty_3.getSelection()) ? TYPE_3 : TYPE_4 ;
  	return retVal;
  }
  
  public String getVersion(){
	String retVal = "";
	retVal = (ver_1.getSelection()) ? VERSION_1 : 
			 (ver_2.getSelection()) ? VERSION_2 : VERSION_3;
	return retVal;
  }
}
 