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
 * The Initial Developer of the Original Code is Jay Sachs.  Portions
 * created by Jay Sachs are Copyright (C) 2002 Jay Sachs.  All Rights
 * Reserved.
 *
 * Contributor(s): Ian Robertson
 */

package org.jamon.codegen;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import org.jamon.JamonException;
import org.jamon.util.StringUtils;

public class DefInfo extends UnitInfo
{
    public DefInfo(String p_name)
    {
        super(p_name);
    }

    public void printAllArgsDecl(IndentingWriter p_writer)
    {
        printRequiredArgsDecl(p_writer);
        if(hasRequiredArgs() && hasOptionalArgs())
        {
            p_writer.println(", ");
        }
        printArgsDecl(p_writer, getOptionalArgs());
    }
}