/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.jamon.integration;

public class ParametersTest extends TestBase {
  public void testUnusedAnonDefFragment() throws Exception {
    expectParserError(
      "UnusedAnonDefFragment", "Call provides a fragment, but none are expected", 2, 1);
  }

  public void testUnusedAnonTemplateFragment() throws Exception {
    expectParserError("UnusedAnonTemplateFragment",
      "Call provides a fragment, but none are expected", 1, 1);
  }

  public void testUnusedNamedTemplateFragment() throws Exception {
    expectParserError(
      "UnusedNamedTemplateFragment", "Call provides unused fragments content", 1, 1);
  }

  public void testUnusedNamedDefFragment() throws Exception {
    expectParserError("UnusedNamedDefFragment", "Call provides unused fragments content", 2, 1);
  }

  public void testSingleFragmentCallToMultiFragmentUnit() throws Exception {
    expectParserError("MultiFarg", "Call must provide multiple fragments", 5, 1);
  }

  public void testUnusedDefArgument() throws Exception {
    expectParserError("UnusedDefArgument", "Call provides unused arguments x", 2, 1);
  }

  public void testUnusedTemplateArgument() throws Exception {
    expectParserError("UnusedTemplateArgument", "Call provides unused arguments x", 1, 1);
  }

  public void testMissingDefFragment() throws Exception {
    expectParserError("MissingDefFragment", "Call is missing fragment content", 4, 1);
  }

  public void testMissingTemplateFragment() throws Exception {
    expectParserError("MissingTemplateFragment", "Call is missing fragment f", 1, 1);
  }

  public void testMissingRequiredArgumentForDef() throws Exception {
    expectParserError("MissingRequiredArgument", "No value supplied for required argument x", 6, 8);
  }

  public void testMissingRequiredArgumentForTemplate() throws Exception {
    expectParserError(
      "MissingTemplateRequiredArgument", "No value supplied for required argument i", 1, 25);
  }

  public void testMissingRequiredArgumentForFragment() throws Exception {
    expectParserError(
      "MissingFragmentRequiredArgument", "No value supplied for required argument x", 5, 6);
  }

  public void testFictionalParentArgument() throws Exception {
    expectParserError(
      "FictionalParentArgument", "/test/jamon/Parent does not have an arg named nosucharg", 3, 3);
  }

  public void testSettingDefaultForInheritedRequiredArg() throws Exception {
    expectParserError(
      "DefaultForInheritedRequiredArg",
      "i is an inherited required argument, and may not be given a default value", 3, 7);
  }

  public void testSettingDefaultForInheritedFragmentArg() throws Exception {
    expectParserError(
      "DefaultForInheritedFragmentArg",
      "f is an inherited fragment argument, and may not be given a default value", 3, 7);
  }

  public void testDuplicateArgument() throws Exception {
    expectParserError("DuplicateArgument", "multiple arguments named opt1", 3, 7);
  }

  public void testDuplicateFragmentArgument() throws Exception {
    expectParserError("DuplicateFragmentArgument", "multiple arguments named f", 3, 7);
  }

  public void testXargsWithoutExtends() throws Exception {
    expectParserError(
      "XargsWithoutExtends", "xargs may not be declared without extending another template", 1, 1);
  }
}
