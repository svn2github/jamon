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

package org.jamon;

import java.util.Map;
import java.util.HashMap;

public class EscapingDirective
{

    public boolean isDefault()
    {
        return m_java == null;
    }

    public String toJava()
    {
        return m_java;
    }


    private static final String PREFIX = org.jamon.Escaping.class.getName() + ".";

    public static final EscapingDirective NONE = new EscapingDirective(PREFIX + "NONE");
    public static final EscapingDirective DEFAULT = new EscapingDirective(null);

    public static EscapingDirective get(String p_abbreviation)
    {
        EscapingDirective result = (EscapingDirective) s_standardDirectives.get(p_abbreviation);
        if (result == null)
        {
            throw new RuntimeException("No escaping directive found with abbreviation '"
                                       + p_abbreviation
                                       + "'");
        }
        return result;
    }


    private final String m_java;

    private static final Map s_standardDirectives = new HashMap();

    static
    {
        s_standardDirectives.put("H",
                                 new EscapingDirective(PREFIX + "STRICT_HTML"));
        s_standardDirectives.put("h",
                                 new EscapingDirective(PREFIX + "HTML"));
        s_standardDirectives.put("n",
                                 NONE);
        s_standardDirectives.put("u",
                                 new EscapingDirective(PREFIX + "URL"));
        s_standardDirectives.put("x",
                                 new EscapingDirective(PREFIX + "XML"));
    }

    private EscapingDirective(String p_java)
    {
        m_java = p_java;
    }

}