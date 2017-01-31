package org.rebecalang.ide.afra.editors.rebecaprop;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.rebecalang.ide.afra.editors.GeneralDocument;
import org.rebecalang.ide.afra.editors.GeneralFileDocumentProvider;

public class RebecaPropDocumentProvider extends GeneralFileDocumentProvider {

	@Override
	protected GeneralDocument getDocument(IProject project, IFile file) {
		return new RebecaPropDocument(project, file);
	}
	
}
