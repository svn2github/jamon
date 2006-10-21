package org.jamon.eclipse.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;

public class JamonArgsScanner extends BufferedRuleBasedScanner 
{
    public JamonArgsScanner(JamonColorProvider provider) 
    {
        super();
        final IToken defaultToken= new Token(new TextAttribute(provider.getColor(JamonColorProvider.TYPE), provider.getColor(JamonColorProvider.ARGS_BG), SWT.NORMAL));
        setDefaultReturnToken(defaultToken);
    }

}
