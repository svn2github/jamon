package org.jamon;

import java.io.Writer;
import java.io.IOException;
import java.net.URLEncoder;

public abstract class AbstractTemplate
    implements Template
{
    protected AbstractTemplate(TemplateManager p_templateManager)
    {
        m_templateManager = p_templateManager;
    }

    public final void initialize(Writer p_writer)
        throws JamonException
    {
        m_writer = p_writer;
        initializeDefaultArguments();
    }

    protected abstract void initializeDefaultArguments()
        throws JamonException;

    protected void writeEscaped(String p_string)
        throws IOException
    {
        // FIXME: need to set default escaping
        writeHtmlEscaped(p_string);
    }


    protected TemplateManager getTemplateManager()
    {
        return m_templateManager;
    }

    protected Writer getWriter()
    {
        return m_writer;
    }

    protected void writeHtmlEscaped(String p_string)
        throws IOException
    {
        for (int i = 0;i < p_string.length(); ++i)
        {
            char c = p_string.charAt(i);
            switch (c)
            {
              case '<': m_writer.write("&lt;"); break;
              case '>': m_writer.write("&gt;"); break;
              case '&': m_writer.write("&amp;"); break;
                // FIXME: numerically escape other chars
              default: m_writer.write(c);
            }
        }
    }

    protected void writeXmlEscaped(String p_string)
        throws IOException
    {
        for (int i = 0;i < p_string.length(); ++i)
        {
            char c = p_string.charAt(i);
            switch (c)
            {
              case '<': m_writer.write("&lt;"); break;
              case '>': m_writer.write("&gt;"); break;
              case '&': m_writer.write("&amp;"); break;
              case '"': m_writer.write("&quot;"); break;
              case '\'': m_writer.write("&apos;"); break;
                // FIXME: numerically escape other chars
              default: m_writer.write(c);
            }
        }
    }

    protected void writeUnEscaped(String p_string)
        throws IOException
    {
        m_writer.write(p_string);
    }

    protected void writeUrlEscaped(String p_string)
        throws IOException
    {
        m_writer.write(URLEncoder.encode(p_string));
    }

    protected String valueOf(Object p_obj)
    {
        return p_obj != null ? p_obj.toString() : "";
    }

    protected String valueOf(int p_int)
    {
        return String.valueOf(p_int);
    }

    protected String valueOf(double p_double)
    {
        return String.valueOf(p_double);
    }

    protected String valueOf(char p_char)
    {
        return String.valueOf(p_char);
    }

    protected String valueOf(boolean p_bool)
    {
        return String.valueOf(p_bool);
    }

    private Writer m_writer;
    private final TemplateManager m_templateManager;
}
