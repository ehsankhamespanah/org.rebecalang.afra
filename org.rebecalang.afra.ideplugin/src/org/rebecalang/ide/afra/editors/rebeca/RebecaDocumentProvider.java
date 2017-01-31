package org.rebecalang.ide.afra.editors.rebeca;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.rebecalang.ide.afra.editors.GeneralDocument;
import org.rebecalang.ide.afra.editors.GeneralFileDocumentProvider;


public class RebecaDocumentProvider extends GeneralFileDocumentProvider {

	@Override
	protected GeneralDocument getDocument(IProject project, IFile file) {
		return new RebecaDocument(project, file);
	}
	
}
