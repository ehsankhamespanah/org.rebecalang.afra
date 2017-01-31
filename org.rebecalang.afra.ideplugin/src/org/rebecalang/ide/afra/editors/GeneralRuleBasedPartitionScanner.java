package org.rebecalang.ide.afra.editors;

import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;

public abstract class GeneralRuleBasedPartitionScanner extends RuleBasedPartitionScanner {
	public abstract String[] getContentTypes();
	public abstract GeneralTextAttribute[] getContentTypeAttributes();
}
