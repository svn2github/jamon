/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.eclipse.editor;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.Token;

public class JamonPartitionScannerTest extends TestCase
{

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    JamonColorProvider.setColorFactory(new MockColorFactory());
    m_scanner = new JamonPartitionScanner();
  }

  private JamonPartitionScanner m_scanner;

  public void testNoMarkup()
  {
    IDocument doc = new Document("Just some plain old <b>text</b> but not Jamon.\nAdd a second lline though.\n");
    m_scanner.setRange(doc, 0, doc.getLength());
    assertEquals(JamonPartitionScanner.JAMON_TOKEN, m_scanner.nextToken());
    assertEquals(0, m_scanner.getTokenOffset());
    assertEquals(doc.getLength(), m_scanner.getTokenLength());
    assertEquals(Token.EOF, m_scanner.nextToken());
  }
}
