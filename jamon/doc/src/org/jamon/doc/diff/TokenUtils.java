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
 * The Original Code is Jamon code, released October, 2002.
 *
 * The Initial Developer of the Original Code is Luis O'Shea.  Portions
 * created by Luis O'Shea are Copyright (C) 2002 Luis O'Shea.  All Rights
 * Reserved.
 *
 * Contributor(s):
 */

package org.jamon.doc.diff;

import java.util.List;
import java.util.LinkedList;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;

public class TokenUtils
{

    public static LineToken[] coarseTokenize(String p_text)
    {
        BufferedReader reader = new BufferedReader(new StringReader(p_text));
        List tokenList = new LinkedList();
        String line;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                tokenList.add(new LineToken(line));
            }
        }
        catch (IOException e)
        {
            // This can't actually happen
            throw new RuntimeException("readLine threw an exception on a buffered StringReader");
        }
        return (LineToken[]) tokenList.toArray(new LineToken[0]);
    }

}
