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

import java.io.OutputStream;
import java.util.Iterator;

import org.jamon.ParserError;
import org.jamon.ParserErrors;
import org.jamon.emit.EmitMode;

public class ImplGenerator
{
    public ImplGenerator(OutputStream p_out,
                         TemplateDescriber p_describer,
                         TemplateUnit p_templateUnit,
                         EmitMode p_emitMode)
    {
        m_writer = new CodeWriter(p_out);
        m_describer = p_describer;
        m_templateUnit = p_templateUnit;
        m_emitMode = p_emitMode;
    }

    public void generateSource()
        throws java.io.IOException
    {
        try
        {
            generateHeader();
            generatePrologue();
            generateImports();
            generateDeclaration();
            generateSetOptionalArguments();
            generateConstructor();
            generateRender();
            generateDefs();
            generateMethods();
            generateEpilogue();
            m_writer.finish();
        }
        catch (ParserError e)
        {
            throw new ParserErrors(e);
        }
    }

    private final CodeWriter m_writer;
    private final TemplateDescriber m_describer;
    private final TemplateUnit m_templateUnit;
    private final EmitMode m_emitMode;

    private final String getPath()
    {
        return m_templateUnit.getName();
    }

    private String getClassName()
    {
        return PathUtils.getImplClassName(getPath());
    }

    private void generateHeader()
    {
        m_writer.println("// Autogenerated Jamon implementation");
        m_writer.println("// "
                         + m_describer.getExternalIdentifier(
                             getPath()).replace('\\','/'));
        m_writer.println();
    }

    private void generateDeclaration()
    {
        m_writer.print("public");
        if(m_templateUnit.isParent())
        {
            m_writer.print(" abstract");
        }
        m_writer.println(
            " class " + getClassName()
            + m_templateUnit.getGenericParams().generateGenericsDeclaration());
        m_writer.println("  extends "
                         + (m_templateUnit.hasParentPath()
                            ? PathUtils.getFullyQualifiedImplClassName(
                                m_templateUnit.getParentPath())
                            : ClassNames.BASE_TEMPLATE));
        m_writer.println(
            "  implements " + getProxyClassName() + ".Intf"
            + m_templateUnit.getGenericParams().generateGenericParamsList());
        m_writer.println();
        m_writer.openBlock();
        for (Iterator<AbstractArgument> i = m_templateUnit.getVisibleArgs();
             i.hasNext(); )
        {
            AbstractArgument arg = i.next();
            m_writer.println(
                "private final " + arg.getType() + " " + arg.getName() + ";");
        }
        m_templateUnit.printClassContent(m_writer);
    }

    private void generateSetOptionalArguments()
    {
        m_writer.println(
            "protected static "
            + m_templateUnit.getGenericParams().generateGenericsDeclaration()
            + getImplDataClassName()
            + " " + SET_OPTS + "("
            + getImplDataClassName() + " p_implData)");
        m_writer.openBlock();
        for (Iterator<OptionalArgument> i =
                m_templateUnit.getSignatureOptionalArgs();
             i.hasNext(); )
        {
            OptionalArgument arg = i.next();
            String value = m_templateUnit.getOptionalArgDefault(arg);
            if (value != null)
            {
                m_writer.println(
                    "if(! p_implData." + arg.getIsNotDefaultName() + "())");
                m_writer.openBlock();
                m_writer.println("p_implData." + arg.getSetterName() + "("
                                 + value + ");");
                m_writer.closeBlock();
            }
        }
        if (m_templateUnit.hasParentPath())
        {
            m_writer.println(getParentImplClassName() + "."
                             + SET_OPTS + "(p_implData);");
        }
        m_writer.println("return p_implData;");
        m_writer.closeBlock();
    }

    private void generateConstructor()
    {
        m_writer.println("public " +  getClassName()
                         + "(" + ClassNames.TEMPLATE_MANAGER
                         + " p_templateManager, "
                         + getImplDataClassName() + " p_implData)");
        m_writer.openBlock();
        m_writer.println(
            "super(p_templateManager, " + SET_OPTS + "(p_implData));");
        for (Iterator<AbstractArgument> i = m_templateUnit.getVisibleArgs();
             i.hasNext(); )
        {
            AbstractArgument arg = i.next();
            m_writer.println(arg.getName()
                             + " = p_implData." + arg.getGetterName() + "();");
        }
        m_writer.closeBlock();
        m_writer.println();
    }

    private void generatePrologue()
    {
        String pkgName = PathUtils.getImplPackageName(getPath());
        if (pkgName.length() > 0)
        {
            m_writer.println("package " + pkgName + ";");
            m_writer.println();
        }
    }

    private void generateInnerUnitFargInterface(
        FragmentUnit p_fragmentUnit, boolean p_private)
    {
        p_fragmentUnit.printInterface(m_writer,
                                      p_private ? "private" : "protected",
                                      false);
    }


    private void generateDefs() throws ParserError
    {
        for (Iterator<DefUnit> i = m_templateUnit.getDefUnits(); i.hasNext(); )
        {
            DefUnit defUnit = i.next();
            m_writer.println();
            for (Iterator<FragmentArgument> f = defUnit.getFragmentArgs();
                 f.hasNext(); )
            {
                generateInnerUnitFargInterface(f.next().getFragmentUnit(), true);
            }

            m_writer.print("private void __jamon_innerUnit__");
            m_writer.print(defUnit.getName());
            m_writer.openList();
            m_writer.printArg(ArgNames.WRITER_DECL);
            defUnit.printRenderArgsDecl(m_writer);
            m_writer.closeList();
            m_writer.println();
            m_writer.println("  throws " + ClassNames.IOEXCEPTION);
            defUnit.generateRenderBody(m_writer, m_describer, m_emitMode);
            m_writer.println();
        }
    }

    private void generateMethods() throws ParserError
    {
        for (Iterator<MethodUnit> i = m_templateUnit.getDeclaredMethodUnits();
             i.hasNext(); )
        {
            generateMethodIntf(i.next());
        }
        for (Iterator<MethodUnit> i = m_templateUnit.getImplementedMethodUnits();
             i.hasNext(); )
        {
            generateMethodImpl(i.next());
        }
    }

    private void generateMethodIntf(MethodUnit p_methodUnit)
    {
        m_writer.println();
        for (Iterator<FragmentArgument> f = p_methodUnit.getFragmentArgs();
             f.hasNext(); )
        {
            generateInnerUnitFargInterface(f.next().getFragmentUnit(), false);
        }

    }

    private void generateMethodImpl(MethodUnit p_methodUnit) throws ParserError
    {
        //FIXME - cut'n'pasted from generateDefs
        m_writer.println();
        if (p_methodUnit.isOverride())
        {
           m_writer.print("@Override ");
        }
        m_writer.print("protected "
                       + (p_methodUnit.isAbstract() ? "abstract " : "")
                       + "void __jamon_innerUnit__");
        m_writer.print(p_methodUnit.getName());
        m_writer.openList();
        m_writer.printArg(ArgNames.WRITER_DECL);
        p_methodUnit.printRenderArgsDecl(m_writer);
        m_writer.closeList();
        m_writer.println();
        m_writer.println("  throws " + ClassNames.IOEXCEPTION);
        if (p_methodUnit.isAbstract())
        {
            m_writer.println("  ;");
        }
        else
        {
            p_methodUnit.generateRenderBody(m_writer, m_describer, m_emitMode);
        }
        m_writer.println();

        for (Iterator<OptionalArgument> i =
                p_methodUnit.getOptionalArgsWithDefaults();
             i.hasNext(); )
        {
            OptionalArgument arg = i.next();
            m_writer.println("protected " + arg.getType() + " "
                             + p_methodUnit.getOptionalArgDefaultMethod(arg)
                             + "()");
            m_writer.openBlock();
            m_writer.println(
                "return " + p_methodUnit.getDefaultForArg(arg) + ";");
            m_writer.closeBlock();
        }
    }

    private void generateRender() throws ParserError
    {
        if (m_templateUnit.hasParentPath())
        {
            m_writer.println("@Override protected void child_render_"
                             + m_templateUnit.getInheritanceDepth()
                             + "("  + ArgNames.WRITER_DECL + ") throws "
                             + ClassNames.IOEXCEPTION);
        }
        else
        {
            m_writer.println("public void renderNoFlush("
                             + ArgNames.WRITER_DECL + ") throws "
                             + ClassNames.IOEXCEPTION);
        }
        m_templateUnit.generateRenderBody(m_writer, m_describer, m_emitMode);

        m_writer.println();
        if (m_templateUnit.isParent())
        {
            m_writer.println("protected abstract void child_render_"
                             + (m_templateUnit.getInheritanceDepth() + 1)
                             + "("  + ArgNames.WRITER_DECL + ") throws "
                             + ClassNames.IOEXCEPTION
                             + ";");
            m_writer.println();
        }
    }

    private void generateEpilogue()
    {
        m_writer.println();
        m_writer.closeBlock();
    }

    private void generateImports()
    {
        m_templateUnit.printImports(m_writer);
    }

    private String getProxyClassName()
    {
        return PathUtils.getFullyQualifiedIntfClassName(getPath());
    }

    private String getImplDataClassName()
    {
        return getProxyClassName() + ".ImplData"
            + m_templateUnit.getGenericParams().generateGenericParamsList();
    }

    private String getParentImplClassName()
    {
        return PathUtils.getFullyQualifiedImplClassName(
            m_templateUnit.getParentPath());
    }


    private final static String SET_OPTS = "__jamon_setOptionalArguments";
}
