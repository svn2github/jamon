package org.modusponens.jtt;

import java.io.File;
import java.io.Writer;
import java.io.PushbackReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.modusponens.jtt.parser.Parser;
import org.modusponens.jtt.parser.ParserException;
import org.modusponens.jtt.lexer.Lexer;
import org.modusponens.jtt.lexer.LexerException;

public class StandardTemplateManager
    implements TemplateManager
{

    private String m_templateSourceDir = "testdata";
    private String m_workDir = "work";
    private String m_javac =
        System.getProperty("java.home") + "/../bin/javac";
    private boolean m_includeRtJar = false;

    public StandardTemplateManager()
    {
    }

    public void setWorkDir(String p_workDir)
    {
        m_workDir = p_workDir;
    }

    public void setTemplateSourceDir(String p_dir)
    {
        m_templateSourceDir = p_dir;
    }

    public void setJavaCompiler(String p_javac)
    {
        m_javac = p_javac;
    }

    public void setJavaCompilerNeedsRtJar(boolean p_includeRtJar)
    {
        m_includeRtJar = p_includeRtJar;
    }

    private long createJavaFile(String p_path)
        throws IOException,
               ParserException,
               LexerException
    {
        Parser parser =
            new Parser(new Lexer(new PushbackReader
                                 (new FileReader(getTemplateFileName(p_path)),
                                  1024)));
        int i = p_path.lastIndexOf('/');
        String dir, name;
        if (i >= 0)
        {
            dir = p_path.substring(1,i).replace('/','.');
            new File(m_workDir + p_path).mkdirs();
            name = p_path.substring(i+1);
        }
        else
        {
            dir = "";
            name = p_path;
        }
        FileWriter w = new FileWriter(getJavaFileName(p_path));
        Phase2Generator g2 = new Phase2Generator(w,dir,name);
        parser.parse().apply(g2);
        g2.generateClassSource();
        w.close();
        return System.currentTimeMillis();
    }

    private String getJavaFileName(String p_path)
    {
        return m_workDir + p_path + "Impl.java";
    }

    private String getClassFileName(String p_path)
    {
        return m_workDir + p_path + "Impl.class";
    }

    private String getTemplateFileName(String p_path)
    {
        return m_templateSourceDir + p_path;
    }

    private long getLastModifiedJava(String p_path)
        throws IOException,
               ParserException,
               LexerException
    {
        File f = new File(getJavaFileName(p_path));
        if (! f.exists())
        {
            return createJavaFile(p_path);
        }
        else
        {
            return f.lastModified();
        }
    }

    private class Loader
        extends ClassLoader
    {
        private byte [] readBytesForClass(String p_name)
            throws IOException
        {
            FileInputStream s =
                new FileInputStream(getClassFileNameForClass(p_name));
            final byte [] buf = new byte[1024];
            byte [] bytes = new byte[0];
            while (true)
            {
                int i = s.read(buf);
                if (i <= 0)
                {
                    break;
                }
                byte [] newbytes = new byte[bytes.length + i];
                System.arraycopy(bytes,0,newbytes,0,bytes.length);
                System.arraycopy(buf,0,newbytes,bytes.length,i);
                bytes = newbytes;
            }
            return bytes;
        }

        private String getClassFileNameForClass(String p_name)
        {
            return m_workDir + "/" + p_name.replace('.','/') + "Impl.class";
        }

        protected Class findClass(String p_name)
            throws ClassNotFoundException
        {
            try
            {
                byte [] bytecode = readBytesForClass(p_name);
                return defineClass(p_name+"Impl",bytecode,0,bytecode.length);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new ClassNotFoundException();
            }
        }

        public Class load(String p_name, boolean p_resolve)
            throws ClassNotFoundException
        {
            Class c = findClass(p_name);
            resolveClass(c);
            return c;

        }
    }

    private final Loader m_loader = new Loader();

    private String getClassName(String p_path)
    {
        // FIXME
        return p_path.replace('/','.').substring(1);
    }

    private Class loadAndResolveClass(String p_path)
        throws ClassNotFoundException
    {
        // FIXME: need to check that it still implements the interface
        return m_loader.load(getClassName(p_path),true);
    }

    private String getClassPath()
    {
        String cp =
            m_workDir + ":" + System.getProperty("java.class.path");
        if (m_includeRtJar)
        {
            cp += System.getProperty("java.home") + "/lib/rt.jar";
        }
        return cp;
    }

    private void compile(String p_path)
        throws IOException
    {
        // FIXME
        String [] cmdline = new String [] { m_javac,
                                            "-classpath",
                                            getClassPath(),
                                            getJavaFileName(p_path) };

        Process p = Runtime.getRuntime().exec(cmdline);
        int rv = -1;
        while (true)
        {
            try
            {
                rv = p.waitFor();
                break;
            }
            catch (InterruptedException e)
            {
            }
        }
        System.err.println("compile done " + rv);
    }

    private Class getClass(String p_path)
        throws IOException,
               ParserException,
               LexerException,
               ClassNotFoundException
    {
        File cf = new File(getClassFileName(p_path));
        if (cf.lastModified() < getLastModifiedJava(p_path))
        {
            compile(p_path);
        }
        return loadAndResolveClass(p_path);
    }


    public Template getInstance(String p_path, Writer p_writer)
    {
        try
        {
            Class c = getClass(p_path);
            Constructor con =
                c.getConstructor(new Class [] { Writer.class,
                                                TemplateManager.class });
            return (Template) con.newInstance(new Object [] { p_writer, this });
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
