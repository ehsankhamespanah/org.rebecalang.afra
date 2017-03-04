package org.rebecalang.afra.ideplugin.editors.rebecaprop;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextEditor;
import org.rebecalang.afra.ideplugin.editors.ColorManager;

public class RebecaPropEditor extends TextEditor {
	
	private static RebecaPropEditor current;

	private ColorManager colorManager;

	public static RebecaPropEditor current() {
		return current;
	}

	public RebecaPropEditor() {
		super();
		current = this;
		colorManager = new ColorManager();
		RebecaPropTextAttribute.init();
		setDocumentProvider(new RebecaPropDocumentProvider());
		setSourceViewerConfiguration(new RebecaPropSourceViewerConfiguration(colorManager));
	}

	public IDocument getDocument() {
		return getDocumentProvider().getDocument(getEditorInput());
	}

	protected void initializeEditor() {
		super.initializeEditor();
	}

	/**
	 * @return Returns the colorManager.
	 */
	public ColorManager getColorManager() {
		return colorManager;
	}
}
