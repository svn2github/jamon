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
package org.jamon.parser;

import org.jamon.ParserErrors;

public class ImportParserTest extends AbstractParserTest
{
    public void testParseSimpleImport() throws Exception
    {
        assertEquals("foo", parseImport("foo "));
    }
    
    public void testParseCompoundImport() throws Exception
    {
        assertEquals("foo.bar", parseImport("foo . bar "));
    }
    
    public void testParseStarImport() throws Exception
    {
        assertEquals("foo.bar.*", parseImport("foo.bar . *"));
    }
    
    private String parseImport(String p_content) throws Exception
    {
        ParserErrors errors = new ParserErrors();
        String result = 
            new ImportParser(START_LOC, makeReader(p_content), errors).getType();
        if (errors.hasErrors())
        {
            throw errors;
        }
        return result;
    }
}