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
 * The Initial Developer of the Original Code is Jay Sachs.  Portions
 * created by Jay Sachs are Copyright (C) 2003 Jay Sachs.  All Rights
 * Reserved.
 *
 * Contributor(s): Ian Robertson
 */

package org.jamon.integration;

import java.io.File;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;

import org.jamon.StandardTemplateManager;
import org.jamon.TemplateManager;
import org.jamon.TemplateProcessor;

import junit.framework.TestCase;

public abstract class TestBase
    extends TestCase
{
    public void setUp()
    {
        m_templateManager = null;
        m_recompilingTemplateManager = null;
        resetWriter();
    }

    protected final boolean doDynamicRecompilation()
    {
        return false;
    }


    private static final String BASEDIR =
        System.getProperty("org.jamon.integration.basedir");
    protected static final String SOURCE_DIR =
        BASEDIR + File.separator + "templates";
    protected static final String WORK_DIR =
        BASEDIR + File.separator + "build/work";

    protected void resetWriter()
    {
        m_writer = new StringWriter();
    }

    protected Writer getWriter()
        throws IOException
    {
        return m_writer;
    }

    protected void checkOutputContains(String p_expected)
        throws IOException
    {
        assertTrue(getOutput().indexOf(p_expected) >= 0);
    }

    protected void checkOutput(String p_expected)
        throws IOException
    {
        assertEquals(p_expected, getOutput());
    }

    protected void checkOutput(String p_message, String p_expected)
        throws IOException
    {
        assertEquals(p_message, p_expected, getOutput());
    }

    protected TemplateManager getTemplateManager()
        throws IOException
    {
        if(m_templateManager == null)
        {
            m_templateManager = constructTemplateManager(false);
        }
        return m_templateManager;
    }

    protected TemplateManager getRecompilingTemplateManager()
        throws IOException
    {
        if(m_recompilingTemplateManager == null)
        {
            m_recompilingTemplateManager = constructTemplateManager(true);
        }
        return m_recompilingTemplateManager;
    }

    private TemplateManager constructTemplateManager(boolean p_recompiling)
        throws IOException
    {
        return new StandardTemplateManager
            (new StandardTemplateManager.Data()
                 .setDynamicRecompilation(p_recompiling)
                 .setSourceDir(SOURCE_DIR)
                 .setClasspath(System.getProperty
                               ("org.jamon.integration.classpath"))
                 .setWorkDir(WORK_DIR));
    }

    private String removeCrs(StringBuffer p_string)
    {
        StringBuffer buffer = new StringBuffer(p_string.length());
        for (int i = 0; i < p_string.length(); ++i)
        {
            char c = p_string.charAt(i);
            if (c != '\r')
            {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    protected String getOutput()
    {
        m_writer.flush();
        return removeCrs(m_writer.getBuffer());
    }

    protected void generateSource(String p_path)
        throws Exception
    {
        String integrationDir =
            System.getProperty("org.jamon.integration.basedir");
        new TemplateProcessor(new File(integrationDir + "/build/src"),
                              new File(integrationDir + "/templates"),
                              getClass().getClassLoader())
            .generateSource(p_path);
    }

    public static void assertEquals(String p_first, String p_second)
    {
        if( showFullContextWhenStringEqualityFails() )
        {
            assertEquals((Object) p_first, (Object) p_second);
        }
        else
        {
            TestCase.assertEquals(p_first, p_second);
        }
    }

    private static boolean showFullContextWhenStringEqualityFails()
    {
        return Boolean.valueOf
            (System.getProperty
             ("org.jamon.integration.verbose","false")).booleanValue();
    }

    private TemplateManager m_templateManager;
    private TemplateManager m_recompilingTemplateManager;
    private StringWriter m_writer;
}
