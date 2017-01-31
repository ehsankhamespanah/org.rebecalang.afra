package org.rebecalang.ide.afra.editors.rebecaprop;

import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.rebecalang.ide.afra.editors.ColorManager;
import org.rebecalang.ide.afra.editors.GeneralSourceViewerConfiguration;
import org.rebecalang.ide.afra.editors.GeneralTextAttribute;


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
