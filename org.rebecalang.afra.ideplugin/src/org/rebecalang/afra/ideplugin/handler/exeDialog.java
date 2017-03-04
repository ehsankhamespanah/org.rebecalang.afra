package org.rebecalang.afra.ideplugin.handler;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class exeDialog extends TitleAreaDialog {

        private Text txtExeName;
        private Text txtXmlName;
        
        private String exeName;
        private String xmlName;

        public exeDialog(Shell parentShell) {
                super(parentShell);
        }

        @Override
        public void create() {
                super.create();
                setTitle("Generate Exe File");
                setMessage("Now choose your favorite names for compilation output file & result file.", IMessageProvider.INFORMATION);
        }

        @Override
        protected Control createDialogArea(Composite parent) {
                Composite area = (Composite) super.createDialogArea(parent);
                Composite container = new Composite(area, SWT.NONE);
                container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                GridLayout layout = new GridLayout(2, false);
                container.setLayout(layout);

                createExeName(container);
                createXmlName(container);

                return area;
        }

        private void createExeName(Composite container) {
                Label lbtExeName = new Label(container, SWT.NONE);
                lbtExeName.setText("Execution File Name");

                GridData dataExeName = new GridData();
                dataExeName.grabExcessHorizontalSpace = true;
                dataExeName.horizontalAlignment = GridData.FILL;

                txtExeName = new Text(container, SWT.BORDER);
                txtExeName.setLayoutData(dataExeName);
        }


        private void createXmlName(Composite container) {
            Label lbtXmlName = new Label(container, SWT.NONE);
            lbtXmlName.setText("Result File Name");

            GridData dataXmlName = new GridData();
            dataXmlName.grabExcessHorizontalSpace = true;
            dataXmlName.horizontalAlignment = GridData.FILL;

            txtXmlName = new Text(container, SWT.BORDER);
            txtXmlName.setLayoutData(dataXmlName);
        }


        @Override
        protected boolean isResizable() {
                return true;
        }

        // save content of the Text fields because they get disposed
        // as soon as the Dialog closes
        private void saveInput() {
        	exeName = txtExeName.getText();
        	xmlName = txtXmlName.getText();
        }

        @Override
        protected void okPressed() {
                saveInput();
                super.okPressed();
        }

        public String getExeName() {
                return exeName;
        }

        public String getXmlName() {
            return xmlName;
        }
}