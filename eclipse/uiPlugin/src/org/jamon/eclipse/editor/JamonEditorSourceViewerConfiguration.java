/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Jamon code, released February, 2003.
 *
 * The Initial Developer of the Original Code is Ian Robertson.  Portions
 * created by Ian Robertson are Copyright (C) 2005 Ian Robertson.  All Rights
 * Reserved.
 *
 * Contributor(s):
 */
package org.jamon.eclipse.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.jamon.eclipse.JamonAnnotationHover;
import org.jamon.eclipse.JamonEditor;

public class JamonEditorSourceViewerConfiguration extends
        SourceViewerConfiguration
{
    private static final IAnnotationHover s_annotationHover = 
        new JamonAnnotationHover(); 

    @Override
    public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) 
    {
        return s_annotationHover;
    }

    @Override
    public IAnnotationHover getOverviewRulerAnnotationHover(
        ISourceViewer sourceViewer)
    {
        return s_annotationHover;
    }
    
    @Override
    public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) 
    {
        return JamonEditor.JAMON_PARTITIONING;
    }
    
    @Override
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) 
    {
    	String[] result = new String[JamonPartitionScanner.JAMON_PARTITION_TYPES.length + 1];
    	System.arraycopy(JamonPartitionScanner.JAMON_PARTITION_TYPES, 0, result, 1, JamonPartitionScanner.JAMON_PARTITION_TYPES.length);
    	result[0] = IDocument.DEFAULT_CONTENT_TYPE;
    	return result;
    }
    
    @Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
    {
        PresentationReconciler reconciler= new PresentationReconciler();
        reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
        final JamonColorProvider colorProvider = new JamonColorProvider();
        DefaultDamagerRepairer dr;
        RuleBasedScanner scanner = new RuleBasedScanner();
        // scanner.setDefaultReturnToken(Token.UNDEFINED);
        dr= new DefaultDamagerRepairer(scanner);
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
        
        dr = new DefaultDamagerRepairer(new JamonScanner(colorProvider));
        reconciler.setDamager(dr, JamonPartitionScanner.JAMON);
        reconciler.setRepairer(dr, JamonPartitionScanner.JAMON);
        
        dr = new DefaultDamagerRepairer(new JamonArgsScanner(colorProvider));
        reconciler.setDamager(dr, JamonPartitionScanner.ARGS);
        reconciler.setRepairer(dr, JamonPartitionScanner.ARGS);

        dr = new DefaultDamagerRepairer(new JamonJavaScanner(colorProvider));
        reconciler.setDamager(dr, JamonPartitionScanner.JAVA);
        reconciler.setRepairer(dr, JamonPartitionScanner.JAVA);

        dr = new DefaultDamagerRepairer(new JamonEmitScanner(colorProvider));
        reconciler.setDamager(dr, JamonPartitionScanner.EMIT);
        reconciler.setRepairer(dr, JamonPartitionScanner.EMIT);
        /*
        dr= new DefaultDamagerRepairer(new JamonDocScanner(colorProvider));
        reconciler.setDamager(dr, JamonPartitionScanner.JAMON_DOC);
        reconciler.setRepairer(dr, JamonPartitionScanner.JAMON_DOC);
        
        dr= new DefaultDamagerRepairer(new JamonJavaCodeScanner(colorProvider, "java"));
        reconciler.setDamager(dr, JamonPartitionScanner.JAMON_JAVA);
        reconciler.setRepairer(dr, JamonPartitionScanner.JAMON_JAVA);

        dr= new DefaultDamagerRepairer(new JamonArgsScanner(colorProvider));
        reconciler.setDamager(dr, JamonPartitionScanner.JAMON_ARGS);
        reconciler.setRepairer(dr, JamonPartitionScanner.JAMON_ARGS);
        
        dr= new DefaultDamagerRepairer(new DefaultJamonScanner(colorProvider, "xargs"));
        reconciler.setDamager(dr, JamonPartitionScanner.JAMON_XARGS);
        reconciler.setRepairer(dr, JamonPartitionScanner.JAMON_XARGS);
        
        dr= new DefaultDamagerRepairer(new JamonAliasScanner(colorProvider));
        reconciler.setDamager(dr, JamonPartitionScanner.JAMON_ALIAS);
        reconciler.setRepairer(dr, JamonPartitionScanner.JAMON_ALIAS);

        dr= new DefaultDamagerRepairer(new JamonImportScanner(colorProvider));
        reconciler.setDamager(dr, JamonPartitionScanner.JAMON_IMPORT);
        reconciler.setRepairer(dr, JamonPartitionScanner.JAMON_IMPORT);

        dr= new DefaultDamagerRepairer(new JamonJavaCodeScanner(colorProvider, "class"));
        reconciler.setDamager(dr, JamonPartitionScanner.JAMON_CLASS);
        reconciler.setRepairer(dr, JamonPartitionScanner.JAMON_CLASS);
	*/
        return reconciler;
    }
}
