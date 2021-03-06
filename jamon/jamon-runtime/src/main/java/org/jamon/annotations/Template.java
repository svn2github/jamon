/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.jamon.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Template {
  Argument[] requiredArguments() default {};
  Argument[] optionalArguments() default {};
  Fragment[] fragmentArguments() default {};
  Method[] methods() default {};
  String[] abstractMethodNames() default {};
  String signature();
  int genericsCount() default 0;
  int inheritanceDepth() default 0;
  String jamonContextType() default "";
  boolean replaceable() default false;
}
