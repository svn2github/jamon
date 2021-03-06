/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jamon.test;

import java.io.File;
import java.util.EnumMap;

public class TemplateBundle {
  public static enum Type {
    TEMPLATE, PROXY, IMPL
  }

  private static final String GENERATED_SOURCES_PATH = "target/generated-sources/jamon/";

  private final EnumMap<Type, File> generatedFiles = new EnumMap<Type, File>(Type.class);

  public TemplateBundle(File basedir, String path) {
    generatedFiles.put(Type.TEMPLATE, new File(basedir, "src/main/java/" + path + ".jamon"));
    generatedFiles.put(Type.PROXY, new File(basedir, GENERATED_SOURCES_PATH + path + ".java"));
    generatedFiles.put(Type.IMPL, new File(basedir, GENERATED_SOURCES_PATH + path + "Impl.java"));
  }

  public File get(Type type) {
    return generatedFiles.get(type);
  }

  public long templateTimestamp() {
    return get(Type.TEMPLATE).lastModified();
  }
}
