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

package org.jamon.codegen;

import java.io.Writer;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import org.jamon.util.StringUtils;

public class IntfGenerator
{
    public IntfGenerator(Writer p_writer,
                         TemplateResolver p_resolver,
                         TemplateDescriber p_describer,
                         TemplateUnit p_templateUnit)
        throws IOException
    {
        m_writer = new IndentingWriter(p_writer);
        m_resolver = p_resolver;
        m_describer = p_describer;
        m_templateUnit = p_templateUnit;
    }

    public void generateClassSource()
        throws IOException
    {
        generatePrologue();
        generateImports();
        generateDeclaration();
        generateConstructor();
        generateSignature();
        generateArgArrays();
        generateIntf();
        generateGetInstance();
        generateOptionalArgs();
        generateFragmentInterfaces(false);
        if (! m_templateUnit.isParent())
        {
            generateMakeRenderer();
            generateRender();
            generateSetWriter();
            generateEscaping();
        }
        generateEpilogue();
        m_writer.finish();
    }

    private final TemplateResolver m_resolver;
    private final TemplateDescriber m_describer;
    private IndentingWriter m_writer;
    private final TemplateUnit m_templateUnit;

    private void generateImports()
        throws IOException
    {
        for (Iterator i = m_templateUnit.getImports();
             i.hasNext(); )
        {
            m_writer.println("import " + i.next() + ";");
        }
        m_writer.println();
    }

    private String getClassName()
    {
        return m_resolver.getIntfClassName(m_templateUnit.getName());
    }

    private String getPackageName()
    {
        return m_resolver.getIntfPackageName(m_templateUnit.getName());
    }

    private void generatePrologue()
        throws IOException
    {
        String pkgName = getPackageName();
        if (pkgName.length() > 0)
        {
            m_writer.println("package " + pkgName + ";");
            m_writer.println();
        }
    }


    private void generateConstructor()
        throws IOException
    {
        m_writer.println();
        m_writer.println
            ("public " + getClassName()
             + "(" + ClassNames.TEMPLATE_MANAGER + " p_manager)");
        m_writer.openBlock();
        m_writer.println(" super(p_manager);");
        m_writer.closeBlock();
    }

    private void generateFragmentInterface(FragmentUnit p_fragmentUnit,
                                           boolean p_inner)
        throws IOException
    {
        String className = p_fragmentUnit.getFragmentInterfaceName();
        m_writer.println("public static abstract class " + className);
        if (!p_inner)
        {
            m_writer.println("  extends " + getClassName() + ".Intf."
                             + className);
            m_writer.openBlock();
            generateTemplateImplConstructor(className);
            m_writer.closeBlock();
        }
        else
        {
            m_writer.println(" extends " + ClassNames.BASE_TEMPLATE);
            m_writer.openBlock();
            generateTemplateImplConstructor(className);
            m_writer.println();
            m_writer.print  ("abstract public void render(");
            p_fragmentUnit.printRequiredArgsDecl(m_writer);
            m_writer.println(")");
            m_writer.println("  throws " + ClassNames.IOEXCEPTION + ";");
            m_writer.print("abstract public " + ClassNames.RENDERER
                           + " makeRenderer(");
            p_fragmentUnit.printRequiredArgsDecl(m_writer);
            m_writer.println(");");
            m_writer.closeBlock();
        }
        m_writer.println();
    }

    private void generateTemplateImplConstructor(String p_className)
    {
        m_writer.println("protected " + p_className
                         + "(" + ClassNames.TEMPLATE_MANAGER + " p_manager"
                         + ", String p_path)");
        m_writer.openBlock();
        m_writer.println("super(p_manager, p_path);");
        m_writer.closeBlock();
    }

    private void generateFragmentInterfaces(boolean p_inner)
        throws IOException
    {
        for (Iterator f = m_templateUnit.getDeclaredFragmentArgs();
             f.hasNext(); )
        {
            generateFragmentInterface(((FragmentArgument) f.next())
                                          .getFragmentUnit(),
                                      p_inner);
        }
        m_writer.println();
    }

    private void generateFargInfo()
        throws IOException
    {
        for (Iterator f = m_templateUnit.getDeclaredFragmentArgs();
             f.hasNext(); )
        {
            FragmentUnit fragmentUnit =
                ((FragmentArgument) f.next()).getFragmentUnit();
            String name = fragmentUnit.getName();
            m_writer.print("public static final String[] FARGINFO_" + name
                             + "_ARG_NAMES = new String[] ");
            m_writer.openBlock();
            for (Iterator a = fragmentUnit.getRequiredArgs(); a.hasNext(); )
            {
                m_writer.print("\""
                               + ((RequiredArgument) a.next()).getName()
                               + "\", ");
            }
            m_writer.closeBlock(";");

            m_writer.print("public static final String[] FARGINFO_" + name
                             + "_ARG_TYPES = new String[] ");
            m_writer.openBlock();
            for (Iterator a = fragmentUnit.getRequiredArgs(); a.hasNext(); )
            {
                m_writer.print("\""
                               + ((RequiredArgument) a.next()).getType()
                               + "\", ");
            }
            m_writer.closeBlock(";");
        }
        m_writer.println();
    }


    private void generateDeclaration()
        throws IOException
    {
        m_writer.print("public ");
        if(m_templateUnit.isParent())
        {
            m_writer.print("abstract ");
        }
        m_writer.println("class " + getClassName());
        m_writer.println("  extends "
                         + (m_templateUnit.hasParentPath()
                            ? m_resolver.getFullyQualifiedIntfClassName(
                                m_templateUnit.getParentPath())
                            : ClassNames.TEMPLATE));
        m_writer.openBlock();
    }

    private void generateArgArrays()
        throws IOException
    {
        List parentRequiredArgs = new LinkedList();
        printArgNames("REQUIRED", m_templateUnit.getSignatureRequiredArgs());
        printArgTypes("REQUIRED", m_templateUnit.getSignatureRequiredArgs());
        printArgNames("OPTIONAL", m_templateUnit.getSignatureOptionalArgs());
        printArgTypes("OPTIONAL", m_templateUnit.getSignatureOptionalArgs());

        m_writer.print("public static final String[] FRAGMENT_ARG_NAMES = ");
        m_writer.openBlock();
        for (Iterator i = m_templateUnit.getFragmentArgs(); i.hasNext(); )
        {
            printQuoted(((FragmentArgument) i.next()).getName());
        }
        m_writer.closeBlock(";");

        generateFargInfo();
    }

    private void printArgNames(String p_argsType, Iterator p_args)
    {
        m_writer.print("public static final String[] "
                       + p_argsType + "_ARG_NAMES = ");
        m_writer.openBlock();
        while (p_args.hasNext())
        {
            printQuoted(((AbstractArgument) p_args.next()).getName());
        }
        m_writer.closeBlock(";");
    }

    private void printArgTypes(String p_argsType, Iterator p_args)
    {
        m_writer.print("public static final String[] "
                       + p_argsType + "_ARG_TYPES = ");
        m_writer.openBlock();
        while (p_args.hasNext())
        {
            printQuoted(((AbstractArgument) p_args.next()).getType());
        }
        m_writer.closeBlock(";");
    }

    private void printQuoted(String p_string)
    {
        m_writer.print("\"" + p_string + "\", ");
    }

    private void generateRender()
        throws IOException
    {
        m_writer.print( m_templateUnit.isParent()
                        ? "protected void render("
                        : "public void render(" );
        m_templateUnit.printRequiredArgsDecl(m_writer);
        m_writer.println(")");

        m_writer.println("  throws java.io.IOException");
        m_writer.openBlock();
        m_writer.println("try");
        m_writer.openBlock();
        m_writer.print  ("getInstance().render(");
        m_templateUnit.printRequiredArgs(m_writer);
        m_writer.println(");");
        m_writer.closeBlock();
        m_writer.println("finally");
        m_writer.openBlock();
        m_writer.println("releaseInstance();");
        m_writer.closeBlock();
        m_writer.closeBlock();
        m_writer.println();
    }


    private void generateMakeRenderer()
        throws IOException
    {
        m_writer.print( m_templateUnit.isParent()
                        ? "protected "
                        : "public " );
        m_writer.print( ClassNames.RENDERER + " makeRenderer(");
        m_templateUnit.printRequiredArgsDecl(m_writer);
        m_writer.println(")");

        m_writer.openBlock();
        m_writer.print(  "return new " + ClassNames.RENDERER + "() ");
        m_writer.openBlock();
        m_writer.println("public void renderTo("
                         + ClassNames.WRITER + " p_writer)");
        m_writer.println(  "  throws " + ClassNames.IOEXCEPTION);
        m_writer.openBlock();
        m_writer.println("writeTo(p_writer);");
        m_writer.print  ("render(");
        m_templateUnit.printRequiredArgs(m_writer);
        m_writer.println(");");
        m_writer.closeBlock();
        m_writer.closeBlock(";");
        m_writer.closeBlock();
        m_writer.println();
    }



    private void generateOptionalArgs()
        throws IOException
    {
        for (Iterator i = m_templateUnit.getDeclaredOptionalArgs();
             i.hasNext(); )
        {
            OptionalArgument arg = (OptionalArgument) i.next();
            m_writer.println();
            String name = arg.getName();
            m_writer.print("public final ");
            String pkgName = getPackageName();
            if (pkgName.length() > 0)
            {
                m_writer.print(pkgName + ".");
            }
            m_writer.print(getClassName());
            m_writer.println(" set" + StringUtils.capitalize(name)
                             + "(" + arg.getType() +" p_" + name + ")");
            m_writer.print  ("  throws ");
            m_writer.println(ClassNames.IOEXCEPTION);
            m_writer.openBlock();
            m_writer.println("getInstance().set" + StringUtils.capitalize(name)
                             + "(" + "p_" + name + ");");
            m_writer.println("return this;");
            m_writer.closeBlock();
        }
    }

    private void generateSignature()
        throws IOException
    {
        m_writer.print("public static final String SIGNATURE = \"");
        m_writer.print(m_templateUnit.getSignature());
        m_writer.println("\";");
    }

    private void generateIntf()
        throws IOException
    {
        m_writer.println("public interface Intf");
        m_writer.print("  extends "
                       + (m_templateUnit.hasParentPath()
                          ? m_resolver.getFullyQualifiedIntfClassName(
                              m_templateUnit.getParentPath()) + ".Intf"
                          : ClassNames.TEMPLATE_INTF));
        m_writer.openBlock();

        generateFragmentInterfaces(true);

        if(! m_templateUnit.isParent())
        {
            m_writer.print("void render(");
            m_templateUnit.printRequiredArgsDecl(m_writer);
            m_writer.println(")");
            m_writer.println("  throws java.io.IOException;");
            m_writer.println();
        }

        for (Iterator i = m_templateUnit.getDeclaredOptionalArgs();
             i.hasNext(); )
        {
            OptionalArgument arg = (OptionalArgument) i.next();
            m_writer.println();
            m_writer.println
                ("void set" + StringUtils.capitalize(arg.getName())
                 + "(" + arg.getType() + " " + arg.getName() + ");");
        }
        m_writer.closeBlock();
    }


    private void generateGetInstance()
        throws IOException
    {
        m_writer.println();
        m_writer.println("private Intf getInstance()");
        m_writer.print("  throws " + ClassNames.IOEXCEPTION);
        m_writer.println();
        m_writer.openBlock();
        m_writer.println("return (Intf) getUntypedInstance();");
        m_writer.closeBlock();

        if(! m_templateUnit.isParent())
        {
            m_writer.println("protected " + ClassNames.TEMPLATE_INTF
                             + " getUntypedInstance()");
            m_writer.println("  throws " + ClassNames.IOEXCEPTION);
            m_writer.openBlock();
            m_writer.println("return getInstance(\""
                             + m_templateUnit.getName() + "\");");
            m_writer.closeBlock();
        }
        else if(! m_templateUnit.hasParentPath())
        {
            m_writer.println("abstract protected " + ClassNames.TEMPLATE_INTF
                             + " getUntypedInstance()");
            m_writer.println("  throws " + ClassNames.IOEXCEPTION + ";");
        }
    }

    private void generateSetWriter()
        throws IOException
    {
        m_writer.println();
        m_writer.println("public " + getClassName()
                         + " writeTo(java.io.Writer p_writer)");
        m_writer.print  ("  throws ");
        m_writer.println(ClassNames.IOEXCEPTION);
        m_writer.openBlock();
        m_writer.println("getInstance().writeTo(p_writer);");
        m_writer.println("return this;");
        m_writer.closeBlock();
    }

    private void generateEscaping()
        throws IOException
    {
        m_writer.println();
        m_writer.println("public " + getClassName()
                         + " escaping(org.jamon.escaping.Escaping p_escaping)");
        m_writer.openBlock();
        m_writer.println("escape(p_escaping);");
        m_writer.println("return this;");
        m_writer.closeBlock();
    }

    private void generateEpilogue()
        throws IOException
    {
        m_writer.println();
        m_writer.closeBlock();
    }
}
