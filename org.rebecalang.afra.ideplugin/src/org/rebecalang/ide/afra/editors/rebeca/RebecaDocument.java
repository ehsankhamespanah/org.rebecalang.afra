package org.rebecalang.ide.afra.editors.rebeca;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.rebecalang.ide.afra.editors.GeneralDocument;
import org.rebecalang.ide.afra.editors.GeneralRuleBasedPartitionScanner;

public class RebecaDocument extends GeneralDocument {
	
	public RebecaDocument(IProject project, IFile file) {
		super(project, file);
	}

	@Override
	public GeneralRuleBasedPartitionScanner createPartitionScanner() {
		return new RebecaPartitionScanner();
	}

}
