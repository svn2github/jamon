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
package org.jamon.codegen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jamon.ParserError;
import org.jamon.ParserErrors;
import org.jamon.TemplateFileLocation;
import org.jamon.TemplateLocation;
import org.jamon.node.AbsolutePathNode;
import org.jamon.node.Location;
import org.jamon.node.NamedAliasPathNode;
import org.jamon.node.PathElementNode;
import org.jamon.node.RelativePathNode;
import org.jamon.node.RootAliasPathNode;
import org.jamon.node.UpdirNode;

import junit.framework.TestCase;

public class PathAdapterTest extends TestCase
{
    private static final String s_templateDir = "/templateDir";
    private static final TemplateLocation s_templateLocation =
        new TemplateFileLocation("/foobar");
    private static final Location s_location =
        new Location(s_templateLocation, 1,1);
    private PathAdapter m_adapter;
    private ParserErrors m_errors;

    @Override protected void setUp() throws Exception
    {
        Map<String, String> aliases = new HashMap<String, String>();
        aliases.put("/", "/root/dir");
        aliases.put("foo", "/foo/dir");
        m_errors = new ParserErrors();
        m_adapter = new PathAdapter(s_templateDir + "/", aliases, m_errors);
    }

    public void testAbsolutePath() throws Exception
    {
        new AbsolutePathNode(s_location)
            .addPathElement(new PathElementNode(s_location, "baz"))
            .apply(m_adapter);
        assertEquals("/baz", m_adapter.getPath());
    }

    public void testRelativePath() throws Exception
    {
        new RelativePathNode(s_location)
            .addPathElement(new PathElementNode(s_location, "baz"))
            .apply(m_adapter);
        assertEquals("baz", m_adapter.getPath());
    }

    public void testNamedAliasedPath() throws Exception
    {
        new NamedAliasPathNode(s_location, "foo")
            .addPathElement(new PathElementNode(s_location, "baz"))
            .apply(m_adapter);
        assertEquals("/foo/dir/baz", m_adapter.getPath());
    }

    public void testRootAliasPath() throws Exception
    {
        new RootAliasPathNode(s_location)
            .addPathElement(new PathElementNode(s_location, "baz"))
            .apply(m_adapter);
        assertEquals("/root/dir/baz", m_adapter.getPath());
    }

    public void testUpdirAtFrontPath() throws Exception
    {
        new RelativePathNode(s_location)
            .addPathElement(new UpdirNode(s_location))
            .addPathElement(new PathElementNode(s_location, "baz"))
            .apply(m_adapter);
        assertEquals("/baz", m_adapter.getPath());
    }

    public void testUpdirOutOfRoot() throws Exception
    {
        Location location2 = new Location(s_templateLocation, 2,2);
        new RelativePathNode(s_location)
            .addPathElement(new UpdirNode(s_location))
            .addPathElement(new UpdirNode(location2))
            .apply(m_adapter);
        assertEquals(
            Arrays.asList(new ParserError(
                location2, "Cannot reference templates above the root")),
            m_errors.getErrors());
    }

    public void testUpdirInMiddleOfPath() throws Exception
    {
        new RelativePathNode(s_location)
            .addPathElement(new PathElementNode(s_location, "baz"))
            .addPathElement(new UpdirNode(s_location))
            .addPathElement(new PathElementNode(s_location, "bar"))
            .apply(m_adapter);
        assertEquals(s_templateDir + "/bar", m_adapter.getPath());
    }

    public PathAdapterTest(String p_name)
    {
        super(p_name);
    }

}
