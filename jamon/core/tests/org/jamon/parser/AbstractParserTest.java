package org.jamon.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.jamon.ParserError;
import org.jamon.ParserErrors;
import org.jamon.TemplateFileLocation;
import org.jamon.TemplateLocation;
import org.jamon.node.AbstractNode;
import org.jamon.node.AbstractPathNode;
import org.jamon.node.Location;
import org.jamon.node.PathElementNode;
import org.jamon.node.TopNode;
import org.jamon.node.UpdirNode;

import static org.junit.Assert.*;
/**
 * @author ian
 **/
public abstract class AbstractParserTest
{
    protected final static TemplateLocation TEMPLATE_LOC =
        new TemplateFileLocation("x");
    protected final static Location START_LOC =
        new Location(TEMPLATE_LOC, 1, 1);

    public AbstractParserTest() {}

    protected static PositionalPushbackReader makeReader(String p_text)
    {
        return new PositionalPushbackReader(
            TEMPLATE_LOC, new StringReader(p_text));
    }

    protected static TopNode topNode()
    {
        return new TopNode(new Location(TEMPLATE_LOC, 1, 1));
    }

    protected static Location location(int p_line, int p_column)
    {
        return new Location(TEMPLATE_LOC, p_line, p_column);
    }

    protected AbstractNode parse(String p_text) throws IOException
    {
        return new TopLevelParser(TEMPLATE_LOC, new StringReader(p_text))
            .parse()
            .getRootNode();
    }

    protected static class PartialError
    {
        private final String m_message;
        private final int m_line, m_column;
        public PartialError(final int p_line, final int p_column, final String p_message)
        {
            m_message = p_message;
            m_line = p_line;
            m_column = p_column;
        }

        public ParserError makeError()
        {
            return new ParserError(new Location(TEMPLATE_LOC, m_line, m_column), m_message);
        }
    }

    private ParserError makeParserError(int p_line, int p_column, String p_message)
    {
        return new ParserError(new Location(TEMPLATE_LOC, p_line, p_column),
                        p_message);
    }

    protected void assertErrors(String p_body, PartialError... p_partialErrors) throws Exception
    {
        try
        {
            parse(p_body);
            fail("No failure registered for '" + p_body + "'");
        }
        catch (ParserErrors e)
        {
            List<ParserError> errors = new LinkedList<ParserError>(e.getErrors());
            List<ParserError> expected = new LinkedList<ParserError>();
            for (PartialError partialError: p_partialErrors)
            {
                expected.add(partialError.makeError());
            }
            assertEquals(expected, errors);
        }
    }

    protected void assertError(
        String p_body,
        int p_line, int p_column, String p_message)
        throws Exception
    {
        assertErrors(p_body, new PartialError(p_line, p_column, p_message));
        try
        {
            parse(p_body);
            fail("No failure registered for '" + p_body + "'");
        }
        catch (ParserErrors e)
        {
            assertEquals(
                Arrays.asList(makeParserError(p_line, p_column, p_message)), e.getErrors());
        }
    }

    protected void assertErrorPair(
            String p_body,
            int p_line1, int p_column1, String p_message1,
            int p_line2, int p_column2, String p_message2)
            throws Exception
    {
        try
        {
           parse(p_body);
           fail("No failure registered for '" + p_body + "'");
        }
        catch (ParserErrors e)
        {
            assertEquals(
                Arrays.asList(
                    makeParserError(p_line1, p_column1, p_message1),
                    makeParserError(p_line2, p_column2, p_message2)),
                e.getErrors());
        }
    }

    protected void assertErrorTripple(
            String p_body,
            int p_line1, int p_column1, String p_message1,
            int p_line2, int p_column2, String p_message2,
            int p_line3, int p_column3, String p_message3)
            throws Exception
    {
        try
        {
           parse(p_body);
                fail("No failure registered for '" + p_body + "'");
        }
        catch (ParserErrors e)
        {
            assertEquals(
                Arrays.asList(
                    makeParserError(p_line1, p_column1, p_message1),
                    makeParserError(p_line2, p_column2, p_message2),
                    makeParserError(p_line3, p_column3, p_message3)),
                e.getErrors());
        }
    }

    protected static AbstractPathNode buildPath(Location p_start, AbstractPathNode p_path, String p_elements)
    {
        Location loc = p_start;
        StringTokenizer tokenizer = new StringTokenizer(p_elements, "/");
        while (tokenizer.hasMoreTokens())
        {
            String elt = tokenizer.nextToken();
            if ("..".equals(elt))
            {
                p_path.addPathElement(new UpdirNode(loc));
            }
            else
            {
                p_path.addPathElement(new PathElementNode(loc, elt));
            }
            loc =
                new Location(loc.getTemplateLocation(),
                             loc.getLine(),
                             loc.getColumn() + 1 + elt.length());
        }
        return p_path;
    }
}
