package org.rebecalang.afra.ideplugin.editors.rebecaprop;

import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.rebecalang.afra.ideplugin.editors.ColorManager;
import org.rebecalang.afra.ideplugin.editors.GeneralSourceViewerConfiguration;
import org.rebecalang.afra.ideplugin.editors.GeneralTextAttribute;


public class RebecaPropSourceViewerConfiguration extends GeneralSourceViewerConfiguration {

	public RebecaPropSourceViewerConfiguration(ColorManager colorManager) {
		super(colorManager);
	}

	@Override
	public RuleBasedScanner createScanner() {
		return new RebecaPropScanner(colorManager);
	}

	@Override
	protected GeneralTextAttribute[] getContentTypeAttributes() {
		return new RebecaPropPartitionScanner().getContentTypeAttributes();
	}

	@Override
	public String[] getContentTypes() {
		return new RebecaPropPartitionScanner().getContentTypes();
	}

	@Override
	protected RuleBasedScanner getScanner() {
		if (scanner == null)
		{
			scanner = createScanner();
			scanner.setDefaultReturnToken(new Token(
					RebecaPropTextAttribute.DEFAULT.getTextAttribute(colorManager)));
		}
		return scanner;
	}
	
}
