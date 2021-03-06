/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.jamon.codegen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.jamon.api.Location;
import org.jamon.compiler.TemplateFileLocation;
import org.jamon.node.ArgNameNode;
import org.jamon.node.ArgValueNode;
import org.jamon.node.GenericsBoundNode;
import org.jamon.node.GenericsParamNode;
import org.jamon.node.LocationImpl;
import org.jamon.node.ParentArgNode;
import org.jamon.node.ParentArgWithDefaultNode;

public class TemplateUnitTest extends TestCase {
  private static final Location LOCATION = new LocationImpl(null, 1, 1);

  public void testInheritanceDepth() throws Exception {
    TemplateUnit parent = new TemplateUnit("/parent", null);
    TemplateUnit child = new TemplateUnit("/child", null);
    TemplateUnit grandchild = new TemplateUnit("/grandchild", null);
    child.setParentDescription(new TemplateDescription(parent));
    grandchild.setParentDescription(new TemplateDescription(child));

    assertEquals(0, parent.getInheritanceDepth());
    assertEquals(1, child.getInheritanceDepth());
    assertEquals(2, grandchild.getInheritanceDepth());
  }

  public void testParentArgs() throws Exception {
    TemplateUnit parent = new TemplateUnit("/parent", null);
    TemplateUnit child = new TemplateUnit("/child", null);
    TemplateUnit grandchild = new TemplateUnit("/grandchild", null);

    RequiredArgument pr1 = new RequiredArgument("pr1", "int", null);
    RequiredArgument pr2 = new RequiredArgument("pr2", "int", null);
    RequiredArgument cr3 = new RequiredArgument("cr2", "int", null);
    OptionalArgument po1 = new OptionalArgument("po1", "int", "op1");
    OptionalArgument po2 = new OptionalArgument("po2", "int", "op2");
    OptionalArgument co2 = new OptionalArgument("co2", "int", "oc3");

    parent.addRequiredArg(pr1);
    parent.addRequiredArg(pr2);
    parent.addOptionalArg(po1);
    parent.addOptionalArg(po2);
    child.setParentPath(parent.getName());
    child.setParentDescription(new TemplateDescription(parent));

    Location loc = new LocationImpl(new TemplateFileLocation("x"), 1, 1);
    child.addParentArg(new ParentArgNode(loc, new ArgNameNode(loc, "pr2")));
    child.addParentArg(
      new ParentArgWithDefaultNode(loc, new ArgNameNode(loc, "po2"), new ArgValueNode(loc, "oc2")));
    child.addRequiredArg(cr3);
    child.addOptionalArg(co2);

    checkArgList(new RequiredArgument[] { pr1, pr2, cr3 }, child.getSignatureRequiredArgs());
    checkArgList(new RequiredArgument[] { cr3 }, child.getDeclaredRenderArgs());
    checkArgList(new AbstractArgument[] { po2, pr2, cr3, co2 }, child.getVisibleArgs());
    checkArgList(new OptionalArgument[] { po1, po2, co2 }, child.getSignatureOptionalArgs());
    checkArgList(new OptionalArgument[] { co2 }, child.getDeclaredOptionalArgs());

    FragmentArgument f = new FragmentArgument(
      new FragmentUnit("f", child, new GenericParams(), null, null), null);
    child.addFragmentArg(f);
    checkArgList(new AbstractArgument[] { po2, pr2, cr3, f, co2 }, child.getVisibleArgs());

    grandchild.setParentDescription(new TemplateDescription(child));
    checkArgList(new RequiredArgument[] { pr1, pr2, cr3 }, grandchild.getSignatureRequiredArgs());
    checkArgList(new RequiredArgument[0], grandchild.getDeclaredRenderArgs());
    checkArgList(new AbstractArgument[] {}, grandchild.getVisibleArgs());
    checkArgList(new OptionalArgument[] { co2, po1, po2}, grandchild.getSignatureOptionalArgs());
    checkArgList(new OptionalArgument[0], grandchild.getDeclaredOptionalArgs());
  }

  public void testSignature() throws Exception {
    TemplateUnit unit = new TemplateUnit("/foo", null);
    TemplateUnit parent = new TemplateUnit("/bar", null);

    Set<String> sigs = new HashSet<String>();
    checkSigIsUnique(unit, sigs);

    RequiredArgument i = new RequiredArgument("i", "int", null);
    RequiredArgument j = new RequiredArgument("j", "Integer", null);
    OptionalArgument a = new OptionalArgument("a", "boolean", "true");
    OptionalArgument b = new OptionalArgument("b", "Boolean", "null");
    FragmentUnit f = new FragmentUnit("f", unit, new GenericParams(), null, null);
    FragmentUnit g = new FragmentUnit("g", unit, new GenericParams(), null, null);

    unit.addRequiredArg(i);
    checkSigIsUnique(unit, sigs);

    unit.addRequiredArg(j);
    checkSigIsUnique(unit, sigs);

    unit.addOptionalArg(a);
    checkSigIsUnique(unit, sigs);

    unit.addOptionalArg(b);
    checkSigIsUnique(unit, sigs);

    unit = new TemplateUnit("/foo", null);
    unit.setParentDescription(new TemplateDescription(parent));
    checkSigIsUnique(unit, sigs);

    unit = new TemplateUnit("/foo", null);
    parent.addRequiredArg(i);
    unit.setParentDescription(new TemplateDescription(parent));
    // suboptimal - if the parent's sig changes, so does the child's
    checkSigIsUnique(unit, sigs);

    unit.addFragmentArg(new FragmentArgument(f, null));
    checkSigIsUnique(unit, sigs);
    f.addRequiredArg(new RequiredArgument("x", "float", null));
    checkSigIsUnique(unit, sigs);
    unit.addFragmentArg(new FragmentArgument(g, null));

    GenericsParamNode genericsParamNode = new GenericsParamNode(LOCATION, "d");
    unit.addGenericsParamNode(genericsParamNode);
    checkSigIsUnique(unit, sigs);
    genericsParamNode.addBound(new GenericsBoundNode(LOCATION, "String"));
    checkSigIsUnique(unit, sigs);

    unit.setReplaceable(true);
    checkSigIsUnique(unit, sigs);
  }

  public void testDependencies() throws Exception {
    TemplateUnit unit = new TemplateUnit("/foo/bar", null);
    unit.addCallPath("/baz");
    unit.addCallPath("/foo/wazza");
    unit.setParentPath("/foo/balla");
    Collection<String> dependencies = unit.getTemplateDependencies();
    assertEquals(3, dependencies.size());
    assertTrue(dependencies.contains("/baz"));
    assertTrue(dependencies.contains("/foo/balla"));
    assertTrue(dependencies.contains("/foo/wazza"));
  }

  private TemplateUnit makeUnitWithContext(String p_path, String p_context, TemplateUnit p_parent) {
    TemplateUnit unit = new TemplateUnit(p_path, null);
    unit.setJamonContextType(p_context);
    if (p_parent != null) {
      unit.setParentPath(p_parent.getName());
      unit.setParentDescription(new TemplateDescription(p_parent));
    }
    return unit;
  }

  public void testSimpleUnitIsOriginatingJamonContext() throws Exception {
    assertFalse(new TemplateUnit("/foo/bar", null).isOriginatingJamonContext());
    assertTrue(makeUnitWithContext("/foo/bar", "someContext", null).isOriginatingJamonContext());
  }

  public void testChildOfContextTemplateIsOriginatingJamonContext() {
    assertFalse(
      makeUnitWithContext(
        "/foo/baz",
        "someContext",
        makeUnitWithContext("/foo/bar", "someContext", null))
      .isOriginatingJamonContext());
  }

  public void testChildOfContextlessTemplateIsOriginatingJamonContext() {
    assertTrue(
      makeUnitWithContext("/foo/bar", "jamonContext", new TemplateUnit("/foo/baz", null))
      .isOriginatingJamonContext());
  }

  public void testGetProxyParentClassOfSimpleTemplate() {
    assertEquals(ClassNames.TEMPLATE, new TemplateUnit("/foo", null).getProxyParentClass());
  }

  public void testGetProxyParentClassOfChildTemlpate() {
    TemplateUnit child = new TemplateUnit("/child", null);
    child.setParentPath("/Parent");
    assertEquals("Parent", child.getProxyParentClass());
  }

  private void checkSigIsUnique(TemplateUnit unit, Set<String> set) throws Exception {
    String sig = unit.getSignature();
    assertTrue(!set.contains(sig));
    set.add(sig);
  }

  private void checkArgList(
    AbstractArgument[] expected, Collection<? extends AbstractArgument> actual) {
    assertEquals(Arrays.asList(expected), new ArrayList<AbstractArgument>(actual));
  }

}