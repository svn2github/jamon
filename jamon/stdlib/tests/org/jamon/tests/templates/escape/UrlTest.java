package org.jamon.tests.templates.escape;

import org.jamon.escape.Url;

public class UrlTest
    extends TestBase
{
    public void testEmpty()
        throws Exception
    {
        new Url(getTemplateManager())
            .render(getWriter(), new Fragment(""));
        checkOutput("");
    }

    public void testSimple()
        throws Exception
    {
        new Url(getTemplateManager())
            .render(getWriter(), new Fragment("hello"));
        checkOutput("hello");
    }

    public void testEscaping()
        throws Exception
    {
        new Url(getTemplateManager())
            .render(getWriter(), new Fragment("a b&c"));
        checkOutput("a+b%26c");
    }

}
